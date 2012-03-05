package shenling.weblog.webx.action;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.google.appengine.api.datastore.Text;
import org.springframework.beans.factory.annotation.Autowired;
import shenling.weblog.beans.Article;
import shenling.weblog.beans.Draft;
import shenling.weblog.beans.SiteMetaInfo;
import shenling.weblog.dao.*;
import shenling.weblog.utils.DataCache;
import shenling.weblog.utils.PersistenceCronUtils;
import shenling.weblog.utils.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-16 下午7:03
 */
public class ArticleAction {
    @Autowired
    ArticleDao articleDao;
    @Autowired
    ArticleDataDao articleDataDao;
    @Autowired
    CommentDao commentDao;
    @Autowired
    SiteMetaDao siteMetaDao;
    @Autowired
    StringUtils stringUtils;
    @Autowired
    DraftDao draftDao;
    @Autowired
    SiteDataDao siteDataDao;
    @Resource(name = "PersistenceCronUtils")
    private PersistenceCronUtils persistenceCronUtils;
    @Autowired
    HttpServletResponse response;
    /*@Resource
    private URIBrokerService uriBrokerService;*/
    /*@Resource(name = "SiteMetaDao")
    SiteMetaDao siteMetaDao;*/

    public void doDeleteArticle(@Param(name = "id") String link, Context context, Navigator navigator) throws IOException {
        articleDao.removeArticle(link);
        articleDataDao.removeWithLink(link);
        int count = (int) commentDao.deleteCommentWithArticleLink(link);
        count = 0 - count;
        DataCache.addSiteData(siteDataDao, -1, count, 0);
        persistenceCronUtils.persistenceCacheData();
        response.sendRedirect("/admin/postList.htm");
    }

    public void doDeleteDraft(@Param(name = "key") String key, Context context, Navigator navigator) throws IOException {
        draftDao.deleteDraft(key);
        response.sendRedirect("/admin/draftList.htm");
    }

    public void doPost(@Param(name = "link") String link, @Param(name = "title") String title,
                       @Param(name = "tags") String tags, @Param(name = "content") String content,
                       @Param(name = "isModify", defaultValue = "false") boolean isModify,
                       @Param(name = "draftKey") String draftKey,
                       Context context,
                       Navigator navigator) throws IOException {
        Article article = validateParams(link, title, tags, content, context, navigator);
        if (article == null) {
            return;
        }
        boolean isModifyPostedFile = false;
        Article oldArticle = articleDao.getArticle(link);
        if (isModify) {
            if (oldArticle != null) {
                //修改已发布的文章
                article.setKey(oldArticle.getKey());
                article.setCreateDate(oldArticle.getCreateDate());
                article.setUpdateDate(new Date());
                article.setCommentCount(oldArticle.getCommentCount());
                article.setViewCount(oldArticle.getViewCount());
                isModifyPostedFile = true;
            }
        } else {
            //新发布文章需要校验链接是否已经存在
            if (oldArticle != null) {
                context.put("errMessage", "链接已经存在！");
                context.put("article", article);
                navigator.forwardTo("admin/post");
                return;
            }
        }
        articleDao.putArticle(article);
        if (!isModifyPostedFile) {
            DataCache.addSiteData(siteDataDao, 1, 0, 0);
        }
        if (!stringUtils.isBlank(draftKey)) {
            //发布文章后删除草稿
            draftDao.deleteDraft(draftKey);
        }
        persistenceCronUtils.persistenceCacheData();
        response.sendRedirect("/index.htm");
    }

    public void doPreview(@Param(name = "link") String link, @Param(name = "title") String title,
                          @Param(name = "tags") String tags, @Param(name = "content") String content, Context context,
                          Navigator navigator) {
        Article article = validateParams(link, title, tags, content, context, navigator);
        if (article == null) {
            return;
        }

        context.put("article", article);
        context.put("title", article.getTitle());
        context.put("keyWords", article.getTags());
        context.put("commentList", Collections.emptyList());
        navigator.forwardTo("view");
    }

    public void doDraft(@Param(name = "link") String link, @Param(name = "title") String title,
                        @Param(name = "tags") String tags, @Param(name = "content") String content,
                        @Param(name = "draftKey") String draftKey, Context context,
                        Navigator navigator) {
        Article article = validateParams(link, title, tags, content, context, navigator);
        if (article == null) {
            return;
        }
        Draft d = new Draft();
        if (!stringUtils.isBlank(draftKey)) {
            Draft draft = draftDao.getDraft(draftKey);
            if (draft != null) {
                d = draft;
            }
        }
        d.set(article);
        d = draftDao.putDraft(d);
        context.put("article", d);
        context.put("key", d.getStrKey());
        context.put("errMessage", "草稿保存成功!");
        navigator.forwardTo("admin/post");
    }

    /**
     * 非空性判断
     *
     * @param link
     * @param title
     * @param tags
     * @param content
     * @param context
     * @param navigator
     * @return 校验失败返回null
     */
    private Article validateParams(String link, String title, String tags, String content, Context context, Navigator navigator) {
        Article article = new Article();
        article.setContent(new Text(content));
        article.setLink(link);
        article.setTitle(title);
        article.setTags(tags);
        if (!validate(link, title, tags, content, context)) {
            context.put("article", article);
            navigator.forwardTo("admin/post");
            return null;
        }
        return article;
    }

    private boolean validate(String link, String title,
                             String tags, String content, Context context) {
        if (!validOne(link, "链接", context)) {
            return false;
        }
        if (!validOne(title, "标题", context)) {
            return false;
        }
        if (!validOne(link, "标签", context)) {
            return false;
        }
        if (!validOne(link, "正文", context)) {
            return false;
        }
        return true;
    }

    private boolean validOne(String value, String title, Context context) {
        if (stringUtils.isBlank(value)) {
            context.put("errMessage", title + "不能为空！");
            return false;
        }
        return true;
    }

    public void doModifySiteMeta(@Param(name = "title") String title,
                                 @Param(name = "host") String host,
                                 @Param(name = "author") String author,
                                 @Param(name = "keyWords") String keyWords,
                                 @Param(name = "perPageCount", defaultValue = "0") int perPageCount,
                                 @Param(name = "description") String description,
                                 @Param(name = "subTitle") String subTitle,
                                 @Param(name = "noticeBoard") String noticeBoard,
                                 @Param(name = "year") String year,
                                 @Param(name = "version") String version,
                                 @Param(name = "ads") String ads,Context context,
                                 Navigator navigator) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("title", "标题");
        map.put("host", "Host");
        map.put("author", "Author");
        map.put("keyWords", "KeyWords");
//        map.put("perPageCount", "每页条数");
        map.put("description", "描述");
        map.put("subTitle", "副标题");
        map.put("noticeBoard", "通知");
        map.put("year", "Year");
        map.put("version", "Version");
        Map<String, String> value = new HashMap<String, String>();
        value.put("title", title);
        value.put("host", host);
        value.put("author", author);
        value.put("keyWords", keyWords);
        value.put("description", description);
        value.put("subTitle", subTitle);
        value.put("noticeBoard", noticeBoard);
        value.put("year", year);
        value.put("version", version);
        SiteMetaInfo temp = new SiteMetaInfo();
        setParams(title, host, author, keyWords, perPageCount, description, subTitle, noticeBoard, year, version, temp, ads);
        context.put("siteMeta", temp);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (stringUtils.isBlank(value.get(entry.getKey()))) {
                context.put("errMessage", entry.getValue() + "不能为空！");
                navigator.forwardTo("/admin/siteMeta");
                return;
            }
        }

        if (perPageCount < 2 || perPageCount > 50) {
            context.put("errMessage", "每页条数范围错误！");
            navigator.forwardTo("/admin/siteMeta");
            return;
        }
        SiteMetaInfo siteMeta = siteMetaDao.getSiteMeta();
        setParams(title, host, author, keyWords, perPageCount, description, subTitle, noticeBoard, year, version, siteMeta, ads);
        siteMetaDao.putSiteMeta(siteMeta);
        context.put("errMessage", "保存成功！");
        navigator.forwardTo("/admin/siteMeta");
    }

    private void setParams(String title, String host, String author, String keyWords,
                           int perPageCount, String description, String subTitle, String noticeBoard,
                           String year, String version, SiteMetaInfo temp, String ads) {
        temp.setAuthor(author);
        temp.setDescription(description);
        temp.setHost(host);
        temp.setKeyWords(keyWords);
        temp.setNoticeBoard(noticeBoard);
        temp.setPerPageCount(perPageCount);
        temp.setSubTitle(subTitle);
        temp.setTitle(title);
        temp.setVersion(version);
        temp.setYear(year);
        temp.setAdsStr(ads);
    }
}
