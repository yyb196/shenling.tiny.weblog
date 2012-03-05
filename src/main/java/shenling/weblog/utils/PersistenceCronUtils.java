package shenling.weblog.utils;

import org.springframework.stereotype.Component;
import shenling.weblog.beans.ArticleData;
import shenling.weblog.beans.SiteData;
import shenling.weblog.dao.ArticleDataDao;
import shenling.weblog.dao.SiteDataDao;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-18 ÉÏÎç11:24
 */
@Component("PersistenceCronUtils")
public class PersistenceCronUtils {
    @Resource(name = "SiteDataDao")
    private SiteDataDao siteDataDao;

    @Resource(name = "ArticleDataDao")
    private ArticleDataDao articleDataDao;

    public void persistenceCacheData() {
        SiteData siteData = siteDataDao.getSiteData();
        if (siteData.isUpdate()) {
            siteData = siteDataDao.putSiteData(siteData);
            siteData.setUpdate(false);
            DataCache.putCache(Constants.SiteData, siteData);
        }
        Set<String> keyList = articleDataDao.getKeyList();
        for (String k : keyList) {
            ArticleData aData = (ArticleData) DataCache.get(k);
            if (aData != null && aData.isUpdate()) {
                aData = articleDataDao.putArticleData(aData);
                aData.setUpdate(false);
                DataCache.putCache(k, aData);
            }
        }
    }
}
