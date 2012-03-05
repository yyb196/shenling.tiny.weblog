package shenling.weblog.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shenling.weblog.beans.Comment;
import shenling.weblog.dao.pmf.PMF;
import shenling.weblog.utils.Constants;
import shenling.weblog.utils.DataCache;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-3 ÏÂÎç3:50
 */
@Component("CommentDao")
public class CommentDao {
    @Autowired
    SiteMetaDao siteMetaDao;

    public List<Comment> getComments(final String articleLink, Date from) {
        final Date initD = from == null ? new Date() : from;
        return DataCache.get(Constants.CommnetListCachePrefix + articleLink +
                (from == null ? "null" : from.getTime()), new DataCache.Run<List<Comment>>() {
            public List<Comment> get() {
                PersistenceManager pm = PMF.get().getPersistenceManager();
                try {
                    Query query = pm.newQuery(Comment.class);
                    query.setFilter("commentDate < fromParam && articleLink==articleLinkParam");
                    query.declareParameters("java.util.Date fromParam, String articleLinkParam");
                    query.setOrdering("commentDate desc");
                    query.setRange(0, 200);
                    return (List<Comment>) pm.detachCopyAll((Collection<?>) query.execute(initD, articleLink));
                } finally {
                    pm.close();
                }
            }
        });

    }

    public List<Comment> getRecentComments() {
        return DataCache.get(Constants.CommnetRecentCachePrefix, new DataCache.Run<List<Comment>>() {
            public List<Comment> get() {
                PersistenceManager pm = PMF.get().getPersistenceManager();
                try {
                    Query query = pm.newQuery(Comment.class);
                    query.setOrdering("commentDate desc");
                    query.setRange(0, siteMetaDao.getSiteMeta().getPerPageCount());
                    List<Comment> rs = (List<Comment>) query.execute();
                    return (List<Comment>) pm.detachCopyAll(rs);
                } finally {
                    pm.close();
                }
            }
        });

    }

    public void addOneComment(Comment one) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(one);
        } finally {
            pm.close();
        }
        DataCache.freeWithPrefix(Constants.TotalCommentPrefix);
    }

    public void deleteComment(String oid) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Query query = pm.newQuery(Comment.class);
            query.setFilter("oId==oidParam");
            query.declareParameters("String oidParam");
            query.deletePersistentAll(oid);
        } finally {
            pm.close();
        }
        DataCache.freeWithPrefix(Constants.TotalCommentPrefix);
    }

    public long deleteCommentWithArticleLink(String link) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        long count = 0;
        try {
            Query query = pm.newQuery(Comment.class);
            query.setFilter("articleLink==linkParam");
            query.declareParameters("String linkParam");
            count =  query.deletePersistentAll(link);
        } finally {
            pm.close();
        }
        DataCache.freeWithPrefix(Constants.TotalCommentPrefix);
        return count;
    }
}
