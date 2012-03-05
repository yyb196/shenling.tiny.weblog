package shenling.weblog.servlet;

import shenling.weblog.beans.Article;
import shenling.weblog.beans.SiteMetaInfo;
import shenling.weblog.beans.atom.Category;
import shenling.weblog.beans.atom.Entry;
import shenling.weblog.beans.atom.Feed;
import shenling.weblog.dao.ArticleDao;
import shenling.weblog.dao.SiteMetaDao;
import shenling.weblog.utils.SpringUtils;
import shenling.weblog.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-17 ÏÂÎç5:20
 */
public class BlogFeed extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        StringUtils stringUtils = (StringUtils) SpringUtils.getBean("StringUtils");
        ArticleDao articleDao = (ArticleDao) SpringUtils.getBean("ArticleDao");
        SiteMetaDao siteMetaDao = (SiteMetaDao) SpringUtils.getBean("SiteMetaDao");
        String content = null;
        if (articleDao == null || siteMetaDao == null) {
            content = "error";
        } else {
            SiteMetaInfo siteMeta = siteMetaDao.getSiteMeta();
            List<Article> articleList = articleDao.getArticles(null);
            Feed feed = new Feed();
            feed.setTitle(siteMeta.getTitle());
            feed.setSubtitle(siteMeta.getSubTitle());
            feed.setUpdated(articleList.isEmpty() || articleList.get(0).getUpdateDate() == null ?
                    new Date() : articleList.get(0).getUpdateDate());
            feed.setAuthor(siteMeta.getAuthor());
            feed.setLink((new StringBuilder()).append(siteMeta.getHost()).append(siteMeta.getSeed()).toString());
            feed.setId((new StringBuilder()).append(siteMeta.getHost()).append(siteMeta.getSeed()).toString());
            for (Article one : articleList) {
                Entry entry = new Entry();
                entry.setURI(siteMeta.getHost());
                feed.addEntry(entry);
                entry.setTitle(one.getTitle());
                entry.setSummary(one.getStrContent());
                entry.setUpdated(one.getUpdateDate() == null ? new Date() : one.getUpdateDate());
                java.lang.String link = (new StringBuilder()).append(siteMeta.getHost())
                        .append("/view/").append(one.getLink()).append(".htm").toString();
                entry.setLink(link);
                entry.setId(link);
                entry.setAuthor(siteMeta.getAuthor());
                String tagsString = one.getTags();
                String tagStrings[] = tagsString.split(",");
                for (int j = 0; j < tagStrings.length; j++) {
                    Category catetory = new Category();
                    entry.addCatetory(catetory);
                    java.lang.String tag = tagStrings[j];
                    catetory.setTerm(tag);
                }
            }
            content = feed.toString();
        }
        response.setContentType("application/atom+xml");
        response.setCharacterEncoding("UTF-8");
        java.io.PrintWriter writer = response.getWriter();
        writer.write(content);
        writer.close();
    }
}
