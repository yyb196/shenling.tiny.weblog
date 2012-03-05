package shenling.weblog.dao;

import org.springframework.stereotype.Component;
import shenling.weblog.beans.ArticleData;
import shenling.weblog.dao.pmf.PMF;
import shenling.weblog.utils.Constants;
import shenling.weblog.utils.DataCache;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-8 ÏÂÎç7:55
 */
@Component("ArticleDataDao")
public class ArticleDataDao {
    private Set<String> keyList = new TreeSet<String>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    public ArticleData getArticleData(final String link) {
        String key = Constants.DataArticlePrefix + link;
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            keyList.add(key);
        } finally {
            writeLock.unlock();
        }
        return DataCache.get(key, new DataCache.Run<ArticleData>() {
            public ArticleData get() {
                PersistenceManager pm = PMF.get().getPersistenceManager();
                try {
                    Query query = pm.newQuery(ArticleData.class);
                    query.setRange(0, 1);
                    query.setFilter("link==linkParam");
                    query.declareParameters("String linkParam");
                    List<ArticleData> execute = (List<ArticleData>) query.execute(link);
                    if (execute.isEmpty()) {
                        ArticleData articleData = new ArticleData();
                        articleData.setLink(link);
                        articleData.setUpdate(true);
                        return articleData;
                    }
                    ArticleData articleData = pm.detachCopy(execute.get(0));
                    articleData.setUpdate(false);
                    return articleData;
                } finally {
                    pm.close();
                }
            }
        });
    }

    private void removeKey(String link) {
        String key = Constants.DataArticlePrefix + link;
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            if (keyList.contains(key)) {
                keyList.remove(key);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public ArticleData putArticleData(ArticleData one) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            ArticleData articleData = pm.makePersistent(one);
            one.setKey(articleData.getKey());
            return one;
        } finally {
            pm.close();
        }
    }

    public void removeWithLink(String link) {
        removeKey(link);
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Query query = pm.newQuery(ArticleData.class);
            query.setFilter("link==linkParam");
            query.declareParameters("String linkParam");
            query.deletePersistentAll(link);
        } finally {
            pm.close();
        }
    }

    public Set<String> getKeyList() {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        try {
            return new TreeSet(keyList);
        } finally {
            readLock.unlock();
        }
    }
}
