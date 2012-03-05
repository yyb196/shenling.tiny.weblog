package shenling.weblog.webx.screen;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import shenling.weblog.beans.Article;
import shenling.weblog.beans.ArticleData;
import shenling.weblog.beans.SiteMetaInfo;
import shenling.weblog.dao.ArticleDao;
import shenling.weblog.dao.ArticleDataDao;
import shenling.weblog.dao.SiteDataDao;
import shenling.weblog.dao.SiteMetaDao;
import shenling.weblog.utils.DataCache;
import shenling.weblog.utils.SpringUtils;
import shenling.weblog.utils.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-2 ÏÂÎç8:05
 */
public class Index {

    @Resource(name = "SiteMetaDao")
    SiteMetaDao siteMetaDao;

    @Resource(name = "ArticleDao")
    ArticleDao articleDao;

    @Resource(name = "StringUtils")
    StringUtils stringUtils;

    @Resource(name = "SiteDataDao")
    SiteDataDao siteDataDao;

    @Resource(name = "ArticleDataDao")
    ArticleDataDao articleDataDao;

    //init spring utils
    @Resource(name="SpringUtils")
    SpringUtils springUtils;

    public void execute(@Param(name = "hasPre", defaultValue = "false") boolean hasPre,
                        @Param(name = "fromDate") String fromDate, Context context) {
        DataCache.addSiteData(siteDataDao, 0, 0, 1);

        SiteMetaInfo siteMeta = siteMetaDao.getSiteMeta();
        context.put("siteMeta", siteMeta);
        context.put("title", siteMeta.getTitle());
        context.put("keyWords", siteMeta.getKeyWords());
        context.put("description", siteMeta.getDescription());
        context.put("hasPre", hasPre);
        context.put("fromDate", fromDate);
        context.put("sectionName", "index");

        List<Article> articles = articleDao.getArticles(stringUtils.str2Date(fromDate));
        int size = articles.size();

        if (size > siteMeta.getPerPageCount()) {
            context.put("articles", articles.subList(0, siteMeta.getPerPageCount()));
            context.put("hasNext", Boolean.TRUE);
            context.put("nextDate", stringUtils.date2Str(articles.get(size - 2).getCreateDate()));
        } else {
            context.put("articles", articles);
        }
        for (Article one : (List<Article>) context.get("articles")) {
            ArticleData articleData = articleDataDao.getArticleData(one.getLink());
            one.setViewCount(articleData.getViewCount());
            one.setCommentCount(articleData.getCommentCount());
        }
    }
}
