package shenling.weblog.utils;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import shenling.weblog.beans.ArticleData;
import shenling.weblog.beans.SiteData;
import shenling.weblog.dao.SiteDataDao;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-8 ÏÂÎç6:49
 */
public class DataCache {
    public static interface Run<T> {
        T get();
    }

    private static Cache cacheInst;

    private static int cacheInterval = 60 * 60 * 24;

    public static <T> T get(String key, Run<T> run) {
        Cache cache = getCache();
        T obj;
        if (cache != null) {
            obj = (T) cache.get(key);
            if (obj != null) {
                return obj;
            }
        }
        obj = run.get();
        if (cache != null && obj != null) {
            cache.put(key, obj);
        }
        return obj;
    }

    public static Object get(String key) {
        Cache cache = getCache();
        if (cache == null) return null;
        return cache.get(key);
    }

    public static void free(String key) {
        Cache cache = getCache();
        if (cache != null) {
            Constants.free(key);
        }
    }

    /*public static Map<String, ArticleData> getArticleDataWithPrefix(String prefix) {
        System.err.println("free with prefix:" + prefix);
        Map<String, ArticleData> retMap = new HashMap<String, ArticleData>();
        Cache cache = getCache();
        if (cache == null) return retMap;
        for (String k : new ArrayList<String>(keyList)) {
            if (k.startsWith(prefix)) {
                retMap.put(k, (ArticleData) cache.get(k));
            }
        }
        return retMap;
    }*/

    private static Cache getCache() {
        if (cacheInst == null) {
            try {
                Map props = new HashMap(1);
                props.put(GCacheFactory.EXPIRATION_DELTA, cacheInterval);
                cacheInst = CacheManager.getInstance().getCacheFactory().createCache(props);
            } catch (CacheException e) {
                e.printStackTrace();
            }
        }
        return cacheInst;
    }

    private static void persistence() {
        PersistenceCronUtils persistenceCronUtils = (PersistenceCronUtils) SpringUtils.getBean("PersistenceCronUtils");
        persistenceCronUtils.persistenceCacheData();
    }

    public static void putCache(String key, Object data) {
        Cache cache = getCache();
        if (cache == null) return;
        cache.put(key, data);
    }

    public static void freeWithPrefix(String prefix) {
        System.out.println("free with prefix:" + prefix);
        Cache cache = getCache();
        if (cache == null) {
            return;
        }
        Constants.freeWithPrefix(prefix);
    }

    public static SiteData addSiteData(SiteDataDao siteDataDao, int articleCount, int commentCount, long viewCount) {
        SiteData siteData = siteDataDao.getSiteData();
        siteData.setCommentCount(siteData.getCommentCount() + commentCount);
        siteData.setArticleCount(siteData.getArticleCount() + articleCount);
        siteData.setViewCount(siteData.getViewCount() + viewCount);
        siteData.setUpdate(true);
        putCache(Constants.SiteData, siteData);
        return siteData;
    }

    public static ArticleData addArticleData(ArticleData data, int commentCount, long viewCount) {
        data.setCommentCount(data.getCommentCount() + commentCount);
        data.setViewCount(data.getViewCount() + viewCount);
        data.setUpdate(true);
        putCache(Constants.DataArticlePrefix + data.getLink(), data);
        return data;
    }

    public static void invalidate() {
        persistence();
        Constants.freeAll();
    }
}
