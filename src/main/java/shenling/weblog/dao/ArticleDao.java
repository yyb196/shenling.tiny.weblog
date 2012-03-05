package shenling.weblog.dao;

import com.google.appengine.api.datastore.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shenling.weblog.beans.Article;
import shenling.weblog.beans.ArticleData;
import shenling.weblog.beans.Comment;
import shenling.weblog.dao.pmf.PMF;
import shenling.weblog.utils.Constants;
import shenling.weblog.utils.DataCache;

import javax.annotation.Resource;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-3 ÏÂÎç3:44
 */
@Component("ArticleDao")
public class ArticleDao {
    @Resource(name = "SiteMetaDao")
    SiteMetaDao siteMetaDao;

    @Autowired
    SiteDataDao siteDataDao;

    @Autowired
    ArticleDataDao articleDataDao;

    public List<Article> getArticles(final Date from) {
        System.out.println("get TotalArticlePrefix from:" + from);
        final Date initD = from == null ? new Date() : from;
        return DataCache.get(Constants.ArticleListCachePrefix + (from == null ? "null" :
                from.getTime()), new DataCache.Run<List<Article>>() {
            public List<Article> get() {
                PersistenceManager pm = PMF.get().getPersistenceManager();
                try {
                    Query query = pm.newQuery(Article.class);
                    query.setFilter("createDate<fromParam");
                    query.setOrdering("createDate desc");
                    query.declareParameters("java.util.Date fromParam");
                    query.setRange(0, siteMetaDao.getSiteMeta().getPerPageCount() + 1);
                    List<Article> result = (List<Article>) pm.detachCopyAll((Collection<?>) query.execute(initD));
                    if ((result == null || result.isEmpty()) && from == null) {
                        return constructOne(pm);
                    }
                    System.err.println(result.get(0).getLink());
                    return result;
                } finally {
                    pm.close();
                }
            }
        });
    }

    public Article getArticle(final String permaLink) {
        return DataCache.get(Constants.ArticleCachePrefix + permaLink, new DataCache.Run<Article>() {
            public Article get() {
                PersistenceManager pm = PMF.get().getPersistenceManager();
                try {
                    Query query = pm.newQuery(Article.class);
                    query.setFilter("link==articlePermalinkParam");
                    query.declareParameters("String articlePermalinkParam");
                    query.setRange(0, 1);
                    List<Article> result = (List<Article>) pm.detachCopyAll((Collection<?>) query.execute(permaLink));
                    if (result == null || result.isEmpty()) {
                        return null;
                    }
                    return result.get(0);
                } finally {
                    pm.close();
                }
            }
        });

    }

    private List<Article> constructOne(PersistenceManager pm) {
        List<Article> retList = new ArrayList<Article>(1);
        Article one = new Article();
        one.setCommentCount(1);
        String title = "system_welcome";
        one.setLink(title);
        one.setTags("system");
        one.setTitle("welcome");
        one.setViewCount(0);
        one.setContent(new Text("<b>first post mocked by system.</b><br/><h1>welcome!</h1>"));
        pm.makePersistent(one);
        Comment oneComment = new Comment();
        oneComment.setArticleLink(title);
        oneComment.setContent(new Text("congratulations!!!"));
        oneComment.setCommentDate(new Date());
        oneComment.setTitle("auto generate comment");
        oneComment.setoId(String.valueOf(System.nanoTime()));
        pm.makePersistent(oneComment);
        retList.add(one);
        ArticleData articleData = articleDataDao.getArticleData(title);
        DataCache.addArticleData(articleData, 1, 0);
        DataCache.addSiteData(siteDataDao, 1, 1, 0);
        return retList;
    }

    public void removeArticle(String articlePermalink) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Query query = pm.newQuery(Article.class);
            query.setFilter("link==articlePermalinkParam");
            query.declareParameters("String articlePermalinkParam");
            query.deletePersistentAll(articlePermalink);
        } finally {
            pm.close();
        }
        DataCache.freeWithPrefix(Constants.TotalArticlePrefix);
    }

    public void putArticle(Article article) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(article);
        } finally {
            pm.close();
        }
        DataCache.freeWithPrefix(Constants.TotalArticlePrefix);
    }

    public Article getPreArticle(String link, final Date currDate) {
        return DataCache.get(Constants.ArticlePreviousPrefix + link, new DataCache.Run<Article>() {
            public Article get() {
                return getOneFrom(currDate, true);
            }
        });
    }

    private Article getOneFrom(Date currDate, boolean reverse) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Query query = pm.newQuery(Article.class);
            /*21 20 19 18*/
            /*<-pre   next->*/
            if (reverse) {
                //previouse article
                query.setFilter("createDate>currDateParam");
                query.setOrdering("createDate asc");
            } else {
                //next article
                query.setFilter("createDate<currDateParam");
                query.setOrdering("createDate desc");
            }
            query.setRange(0, 1);
            query.declareParameters("java.util.Date currDateParam");
            List<Article> execute = (List<Article>) query.execute(currDate);
            if (execute.isEmpty()) {
                return null;
            }
            return pm.detachCopy(execute.get(0));
        } finally {
            pm.close();
        }
    }

    public Article getNextArticle(String link, final Date currDate) {
        return DataCache.get(Constants.ArticleNextPrefix + link, new DataCache.Run<Article>() {
            public Article get() {
                return getOneFrom(currDate, false);
            }
        });
    }
}
