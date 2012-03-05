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

package com.alibaba.citrus.turbine.pipeline.valve;

import com.alibaba.citrus.service.mappingrule.MappingRuleService;
import com.alibaba.citrus.service.pipeline.PipelineContext;
import com.alibaba.citrus.service.pipeline.support.AbstractValve;
import com.alibaba.citrus.service.pipeline.support.AbstractValveDefinitionParser;
import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.service.requestcontext.parser.ParserRequestContext;
import com.alibaba.citrus.service.requestcontext.parser.impl.ParameterParserImpl;
import com.alibaba.citrus.turbine.TurbineRunDataInternal;
import com.alibaba.citrus.util.ServletUtil;
import com.alibaba.citrus.util.StringUtil;
import com.alibaba.citrus.util.internal.ActionEventUtil;
import com.alibaba.citrus.webx.WebxComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alibaba.citrus.springext.util.SpringExtUtil.attributesToProperties;
import static com.alibaba.citrus.turbine.TurbineConstant.ACTION_MODULE;
import static com.alibaba.citrus.turbine.TurbineConstant.EXTENSION_INPUT;
import static com.alibaba.citrus.turbine.util.TurbineUtil.getTurbineRunData;
import static com.alibaba.citrus.util.FileUtil.normalizeAbsolutePath;
import static com.alibaba.citrus.util.StringUtil.trimToNull;

/**
 * ����URL������������rundata���������¹���
 * <ol>
 * <li>ȡ��servletPath + pathInfo - componentPath��Ϊtarget��</li>
 * <li>ʹ��MappingRuleService����target�ĺ�׺ת����ͳһ���ڲ���׺�����磺��jhtmlת����jsp��</li>
 * </ol>
 *
 * @author Michael Zhou
 */
public class AnalyzeURLValve extends AbstractValve {
    private static final String DEFAULT_ACTION_PARAM_NAME = "action";

    private static final Pattern viewPat = Pattern.compile("/view/(\\w+)\\.htm");

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ParserRequestContext parserRequestContext;

    @Autowired
    private MappingRuleService mappingRuleService;

    @Autowired
    private WebxComponent component;

    private String homepage;
    private String actionParam;


    /**
     * ������URL query�д���action�Ĳ�������
     */
    public void setActionParam(String actionParam) {
        this.actionParam = trimToNull(actionParam);
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = normalizeAbsolutePath(homepage);
    }

    @Override
    protected void init() throws Exception {
        if (actionParam == null) {
            actionParam = DEFAULT_ACTION_PARAM_NAME;
        }

        if (homepage == null) {
            setHomepage("/index");
        }
        System.err.println("yangyubing:my analyzeURLValve inited.");
    }

    public void invoke(PipelineContext pipelineContext) throws Exception {
        TurbineRunDataInternal rundata = (TurbineRunDataInternal) getTurbineRunData(request);
        String target = null;

        // ȡ��target����ת����ͳһ���ڲ���׺����
        String pathInfo = ServletUtil.getResourcePath(rundata.getRequest()).substring(
                component.getComponentPath().length());

        if ("/".equals(pathInfo)) {
            pathInfo = getHomepage();
        }
        
        String initPathInfo = pathInfo;

        // ע�⣬���뽫pathInfoת����camelCase��
        int lastSlashIndex = pathInfo.lastIndexOf("/");

        if (lastSlashIndex >= 0) {
            pathInfo = pathInfo.substring(0, lastSlashIndex) + "/"
                    + StringUtil.toCamelCase(pathInfo.substring(lastSlashIndex + 1));
        } else {
            pathInfo = StringUtil.toCamelCase(pathInfo);
        }
        if (parserRequestContext == null) {
            System.err.println("ParserRequestContext is null.");
        } else {
            Matcher matcher = viewPat.matcher(initPathInfo);
            if (matcher.find()) {
                String id = matcher.group(1);
                ParameterParser parameters = parserRequestContext.getParameters();
                if(parameters instanceof ParameterParserImpl){
                    ((ParameterParserImpl)parameters).add("id", id);
                }
                pathInfo = "/view.htm";
            }
        }

        //TODO /view.htm?id=xxx���������Ҫ����ɲ���������
        target = mappingRuleService.getMappedName(EXTENSION_INPUT, pathInfo);

        rundata.setTarget(target);

        // ȡ��action
        String action = StringUtil.toCamelCase(trimToNull(rundata.getParameters().getString(actionParam)));

        action = mappingRuleService.getMappedName(ACTION_MODULE, action);
        rundata.setAction(action);

        // ȡ��actionEvent
        String actionEvent = ActionEventUtil.getEventName(rundata.getRequest());
        rundata.setActionEvent(actionEvent);

        pipelineContext.invokeNext();
    }

    public static class DefinitionParser extends AbstractValveDefinitionParser<AnalyzeURLValve> {
        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            attributesToProperties(element, builder, "homepage", "actionParam");
        }
    }
}
