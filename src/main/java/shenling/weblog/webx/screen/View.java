package shenling.weblog.webx.screen;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import org.springframework.beans.factory.annotation.Autowired;
import shenling.weblog.beans.Article;
import shenling.weblog.beans.ArticleData;
import shenling.weblog.beans.SiteMetaInfo;
import shenling.weblog.dao.*;
import shenling.weblog.utils.DataCache;
import shenling.weblog.utils.StringUtils;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-8 ÏÂÎç1:56
 */
public class View {
    @Autowired
    ArticleDao articleDao;
    @Resource(name = "SiteMetaDao")
    SiteMetaDao siteMetaDao;
    @Resource(name = "CommentDao")
    CommentDao commentDao;
    @Resource(name = "StringUtils")
    StringUtils stringUtils;
    @Resource(name = "ArticleDataDao")
    ArticleDataDao articleDataDao;

    @Resource(name = "SiteDataDao")
    SiteDataDao siteDataDao;

    public void execute(@Param(name = "id", defaultValue = "system_welcome") String pageId,
                        @Param(name = "commentFrom") String from, Context context) {
        SiteMetaInfo siteMeta = siteMetaDao.getSiteMeta();
        context.put("siteMeta", siteMeta);
        context.put("sectionName", "view");
        if (context.get("article") == null) {
            Article article = articleDao.getArticle(pageId);
            if (article == null) {
                throw new RuntimeException("page not exist!pageId=" + pageId);
            }
            ArticleData articleData = articleDataDao.getArticleData(pageId);
            DataCache.addArticleData(articleData, 0, 1);
            DataCache.addSiteData(siteDataDao, 0, 0, 1);
            article.setViewCount(articleData.getViewCount());
            article.setCommentCount(articleData.getCommentCount());
            context.put("article", article);
            context.put("title", article.getTitle() + "-" + siteMeta.getTitle());
            context.put("keyWords", article.getTags());
            //TODO pagenation
            context.put("commentList", commentDao.getComments(article.getLink(), stringUtils.str2Date(from)));
        }

        Article article = (Article) context.get("article");
        Article nextArticle = articleDao.getNextArticle(article.getLink(), article.getCreateDate());
        Article preArticle = articleDao.getPreArticle(article.getLink(), article.getCreateDate());
        if (nextArticle != null) {
            context.put("nextArticleLink", "/view/" + nextArticle.getLink() + ".htm");
            context.put("nextArticleTitle", nextArticle.getTitle());
        }
        if (preArticle != null) {
            context.put("previousArticleLink", "/view/" + preArticle.getLink() + ".htm");
            context.put("previousArticleTitle", preArticle.getTitle());
        }
    }
}
