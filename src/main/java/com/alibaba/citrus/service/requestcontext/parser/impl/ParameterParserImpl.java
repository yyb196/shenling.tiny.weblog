/*
 * Copyright 2010 Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.citrus.service.requestcontext.parser.impl;

import com.alibaba.citrus.service.requestcontext.parser.*;
import com.alibaba.citrus.service.requestcontext.util.QueryStringParser;
import com.alibaba.citrus.service.requestcontext.util.ValueList;
import com.alibaba.citrus.service.upload.UploadException;
import com.alibaba.citrus.service.upload.UploadParameters;
import com.alibaba.citrus.service.upload.UploadService;
import com.alibaba.citrus.service.upload.UploadSizeLimitExceededException;
import com.alibaba.citrus.util.StringEscapeUtil;
import com.alibaba.citrus.util.StringUtil;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.List;
import java.util.Map;

import static com.alibaba.citrus.service.requestcontext.parser.ParserRequestContext.DEFAULT_CHARSET_ENCODING;
import static com.alibaba.citrus.util.ArrayUtil.isEmptyArray;
import static com.alibaba.citrus.util.BasicConstant.EMPTY_STRING;
import static com.alibaba.citrus.util.CollectionUtil.createLinkedList;
import static com.alibaba.citrus.util.StringUtil.trimToEmpty;

/**
 * ��������HTTP������GET��POST�Ĳ����Ľӿ�<code>ParameterParser</code>��Ĭ��ʵ�֡�
 */
public class ParameterParserImpl extends AbstractValueParser implements ParameterParser {
    private final static Logger log = LoggerFactory.getLogger(ParameterParser.class);
    private final UploadService upload;
    private final boolean trimming;
    private boolean uploadProcessed;
    private final ParameterParserFilter[] filters;
    private final String htmlFieldSuffix;
//    private final Map<String, String> extParams = new HashMap<String, String>(1);
//
//    public void putExtParams(String key, String value) {
//        extParams.put(key, value);
//    }
//
//    public Map<String, String> getExtParams() {
//        return extParams;
//    }
//
//    @Override
//    public String getString(String key) {
//        String value = super.getString(key);
//        if (extParams.containsKey(key) && value == null) {
//            return extParams.get(key);
//        }
//        return value;
//    }
//
//    @Override
//    public Set<String> keySet() {
//        Set<String> keySet = super.keySet();
//        if (!extParams.isEmpty()) {
//            Set<String> tempSet = new TreeSet<String>(keySet);
//            tempSet.addAll(extParams.keySet());
//            return tempSet;
//        }
//        return keySet;
//    }

    /**
     * ��request�д����µ�parameters�������multipart-form�����Զ�����֮��
     */
    public ParameterParserImpl(ParserRequestContext requestContext, UploadService upload, boolean trimming,
                               ParameterParserFilter[] filters, String htmlFieldSuffix) {
        super(requestContext);

        this.upload = upload;
        this.trimming = trimming;
        this.filters = filters;
        this.htmlFieldSuffix = htmlFieldSuffix;

        HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper) requestContext.getRequest();
        HttpServletRequest wrappedRequest = (HttpServletRequest) wrapper.getRequest();
        boolean isMultipart = false;

        // �Զ�upload
        if (requestContext.isAutoUpload() && upload != null) {
            // �����multipart/*���������upload service��������
            isMultipart = upload.isMultipartContent(wrappedRequest);

            if (isMultipart) {
                try {
                    parseUpload();
                } catch (UploadSizeLimitExceededException e) {
                    add(ParserRequestContext.UPLOAD_FAILED, Boolean.TRUE);
                    add(ParserRequestContext.UPLOAD_SIZE_LIMIT_EXCEEDED, Boolean.TRUE);
                    log.warn("File upload exceeds the size limit", e);
                } catch (UploadException e) {
                    add(ParserRequestContext.UPLOAD_FAILED, Boolean.TRUE);
                    log.warn("Upload failed", e);
                }
            }
        }

        // ��request��ȡ����
        if (!isMultipart) {
            String method = wrappedRequest.getMethod();

            // ���ձ�׼��URL��ֻ�ܳ���US-ASCII�ַ����������������͵��ַ�������������URL���롣
            // �ܲ��ң��������Ҳû��ͳһ�ı�׼���ͻ��˺ͷ���˱�����ĳ����ʶ��
            //
            // ���ڿͻ��ˣ������¼��������
            // 1. ��������ύ�ı������Ե�ǰҳ����ַ������롣
            // ���磬һ��GBK�����ҳ�����ύ�ı�������GBK����ģ�������methodΪGET����POST��
            //
            // 2. ֱ���������������ַ�����URL����������������úͲ���ϵͳ��������ȷ�����롣
            // ���磬����Windows�У�����ie����firefox��������Ĭ�϶���GBK��
            // ����macϵͳ�У�����safari����firefox��������Ĭ�϶���UTF-8��
            //
            // ���ڷ���ˣ������¼��������
            // 1. Tomcat������server.xml�У���<Connector URIEncoding="xxx">��ָ���ı��룬������GET����Ĳ�������δָ��������8859_1��
            // 2. Jetty������UTF-8������GET����Ĳ�����
            // 3. ����POST��������request.setCharacterEncoding("xxx")�ı���Ϊ׼����δָ��������8859_1��
            // 4. ���������Tomcat5������<Connector useBodyEncodingForURI="true">����ôGET����Ҳ��request.setCharacterEncoding("xxx")�ı���Ϊ׼��
            //
            // �ɼ���������κ����ã�Tomcat/Jetty������8859_1��UTF-8������URL query�����½������
            //
            // Ϊ��ʹӦ�öԷ������������������٣������з�POST/PUT����һ����GET���󣩽����ֹ����룬����������servlet engine�Ľ�����ƣ�
            // ������������useServletEngineParser=true��
            if (requestContext.isUseServletEngineParser() || "post".equalsIgnoreCase(method)
                    || "put".equalsIgnoreCase(method)) {
                parseByServletEngine(wrappedRequest);
            } else {
                parseQueryString(requestContext, wrappedRequest);
            }

            postProcessParams();
        }
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    /**
     * ��servlet engine������������
     */
    private void parseByServletEngine(HttpServletRequest wrappedRequest) {
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameters = wrappedRequest.getParameterMap();

        if (parameters != null && parameters.size() > 0) {
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();

                for (String value : values) {
                    add(key, value);
                }
            }
        }
    }

    /**
     * �Լ�����query string��
     */
    private void parseQueryString(ParserRequestContext requestContext, HttpServletRequest wrappedRequest) {
        // ��useBodyEncodingForURI=trueʱ����request.setCharacterEncoding()��ָ����ֵ�����룬����ʹ��URIEncoding��Ĭ��ΪUTF-8��
        // useBodyEncodingForURIĬ��ֵ����true��
        // ����Ϊ��tomcat�ķ��һ�¡�������tomcatĬ����8859_1�����û��ϵ��
        String charset = requestContext.isUseBodyEncodingForURI() ? wrappedRequest.getCharacterEncoding()
                : requestContext.getURIEncoding();

        QueryStringParser parser = new QueryStringParser(charset, DEFAULT_CHARSET_ENCODING) {
            @Override
            protected void add(String key, String value) {
                ParameterParserImpl.this.add(key, value);
            }
        };

        parser.parse(wrappedRequest.getQueryString());
    }

    /**
     * �������в�����
     * <p>
     * ���������Ϊ.~html��β�ģ���HTML������������ͨ������
     * </p>
     */
    private void postProcessParams() {
        HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper) requestContext.getRequest();
        HttpServletRequest wrappedRequest = (HttpServletRequest) wrapper.getRequest();
        boolean[] filtering = null;

        if (!isEmptyArray(filters)) {
            filtering = new boolean[filters.length];

            for (int i = 0; i < filters.length; i++) {
                filtering[i] = filters[i].isFiltering(wrappedRequest);
            }
        }

        String[] keys = getKeys();
        List<String> keysToRemove = createLinkedList();

        for (String key : keys) {
            if (key.endsWith(htmlFieldSuffix)) {
                keysToRemove.add(key);
                key = key.substring(0, key.length() - htmlFieldSuffix.length());

                if (!containsKey(key)) {
                    setObjects(key, processValues(key, true, filtering));
                }

                continue;
            }

            boolean isHtml = !StringUtil.isBlank(getString(key + htmlFieldSuffix));
            setObjects(key, processValues(key, isHtml, filtering));
        }

        for (String key : keysToRemove) {
            remove(key);
        }
    }

    private Object[] processValues(String key, boolean isHtmlField, boolean[] filtering) {
        Object[] values = getObjects(key);

        for (int i = 0; i < values.length; i++) {
            Object value = values[i];

            if (value instanceof String) {
                // ����HTML�ֶε�&#12345;ת����unicode��
                if (!isHtmlField && requestContext.isUnescapeParameters()) {
                    value = StringEscapeUtil.unescapeEntities(null, (String) value);
                }

                // �����ַ���ֵ
                if (filtering != null) {
                    for (int j = 0; j < filters.length; j++) {
                        ParameterParserFilter filter = filters[j];

                        if (filter instanceof ParameterValueFilter && filtering[j]) {
                            value = ((ParameterValueFilter) filter).filter(key, (String) value, isHtmlField);
                        }
                    }
                }
            } else if (value instanceof FileItem) {
                // �����ϴ��ļ�
                if (filtering != null) {
                    for (int j = 0; j < filters.length; j++) {
                        ParameterParserFilter filter = filters[j];

                        if (filter instanceof UploadedFileFilter && filtering[j]) {
                            value = ((UploadedFileFilter) filter).filter(key, (FileItem) value);
                        }
                    }
                }
            }

            values[i] = value;
        }

        return values;
    }

    /**
     * ȡ��ָ�����Ƶ�<code>FileItem</code>������������ڣ��򷵻�<code>null</code>��
     *
     * @param key ������
     * @return <code>FileItem</code>����
     */
    public FileItem getFileItem(String key) {
        ValueList container = getValueList(key, false);

        return container == null ? null : container.getFileItem();
    }

    /**
     * ȡ��ָ�����Ƶ�<code>FileItem</code>������������ڣ��򷵻�<code>null</code>��
     *
     * @param key ������
     * @return <code>FileItem</code>���������
     */
    public FileItem[] getFileItems(String key) {
        ValueList container = getValueList(key, false);

        return container == null ? new FileItem[0] : container.getFileItems();
    }

    /**
     * ���<code>FileItem</code>��
     *
     * @param key   ������
     * @param value ����ֵ
     */
    public void add(String key, FileItem value) {
        if (value.isFormField()) {
            add(key, value.getString());
        } else {
            // ���Կյ��ϴ��
            if (!StringUtil.isEmpty(value.getName()) || value.getSize() > 0) {
                add(key, (Object) value);
            }
        }
    }

    /**
     * ��Ӳ�����/����ֵ��
     *
     * @param key   ������
     * @param value ����ֵ
     */
    @Override
    public void add(String key, Object value) {
        if (value == null) {
            value = EMPTY_STRING;
        }

        if (trimming && value instanceof String) {
            value = trimToEmpty((String) value);
        }

        getValueList(key, true).addValue(value);
    }

    /**
     * ��������<a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>��׼��
     * <code>multipart/form-data</code>���͵�HTTP����
     * <p>
     * Ҫִ�д˷������뽫<code>UploadService.automatic</code>���ò������ó�<code>false</code>��
     * �˷���������service��Ĭ�����ã��ʺ�����action��servlet���ֹ�ִ�С�
     * </p>
     *
     * @throws UploadException �������ʱ����
     */
    public void parseUpload() throws UploadException {
        parseUpload(null);
    }

    /**
     * ��������<a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>��׼��
     * <code>multipart/form-data</code>���͵�HTTP����
     * <p>
     * Ҫִ�д˷������뽫<code>UploadService.automatic</code>���ò������ó�<code>false</code>��
     * �˷���������service��Ĭ�����ã��ʺ�����action��servlet���ֹ�ִ�С�
     * </p>
     * <p/>
     * //@param sizeThreshold �ļ������ڴ��е���ֵ��С�ڴ�ֵ���ļ����������ڴ��С������ֵС��0����ʹ��Ԥ���ֵ
     * //@param sizeMax HTTP��������ߴ磬�����˳ߴ�����󽫱�������
     * //@param repositoryPath �ݴ������ļ��ľ���·��
     *
     * @throws UploadException �������ʱ����
     */
    public void parseUpload(UploadParameters params) throws UploadException {
        if (uploadProcessed || upload == null) {
            return;
        }

        FileItem[] items = upload.parseRequest(requestContext.getRequest(), params);

        for (FileItem item : items) {
            add(item.getFieldName(), item);
        }

        uploadProcessed = true;

        postProcessParams();
    }

    /**
     * ȡ�����ڽ��������ı����ַ�������ͬ��ʵ��ȡ�ñ����ַ����ķ���Ҳ��ͬ�����磬����<code>ParameterParser</code>��
     * �˱����ַ�������<code>request.getCharacterEncoding()</code>�����ġ�
     * <p>
     * ���δָ����Ĭ�Ϸ���<code>ISO-8859-1</code>��
     * </p>
     *
     * @return �����ַ���
     */
    @Override
    protected String getCharacterEncoding() {
        String charset = requestContext.getRequest().getCharacterEncoding();

        return charset == null ? ParserRequestContext.DEFAULT_CHARSET_ENCODING : charset;
    }

    /**
     * ��parameters������װ��query string��
     *
     * @return query string�����û�в������򷵻�<code>null</code>
     */
    public String toQueryString() {
        QueryStringParser parser = new QueryStringParser();

        for (Object element : keySet()) {
            String key = (String) element;
            Object[] values = getObjects(key);

            if (isEmptyArray(values)) {
                continue;
            }

            for (Object valueObject : values) {
                if (valueObject == null || valueObject instanceof String) {
                    parser.append(key, (String) valueObject);
                }
            }
        }

        return parser.toQueryString();
    }
}
