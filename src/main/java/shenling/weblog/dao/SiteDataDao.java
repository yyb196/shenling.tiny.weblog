package shenling.weblog.dao;

import org.springframework.stereotype.Component;
import shenling.weblog.beans.SiteData;
import shenling.weblog.dao.pmf.PMF;
import shenling.weblog.utils.Constants;
import shenling.weblog.utils.DataCache;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-8 ÏÂÎç7:37
 */
@Component("SiteDataDao")
public class SiteDataDao {
    public SiteData getSiteData() {
        return DataCache.get(Constants.SiteData, new DataCache.Run<SiteData>() {
            public SiteData get() {
                PersistenceManager pm = PMF.get().getPersistenceManager();
                try {
                    Query query = pm.newQuery(SiteData.class);
                    query.setRange(0, 10);
                    List<SiteData> list =(List<SiteData>) query.execute();
                    if (list.isEmpty()) {
                        return new SiteData();
                    }
                    int size = list.size();
                    SiteData one = pm.detachCopy(list.get(0));
                    for (int i = 1; i < size; i++) {
                        one.add(list.get(i));
                    }
                    one.setUpdate(false);
                    return one;
                } finally {
                    pm.close();
                }
            }
        }
        );
    }

    public SiteData putSiteData(SiteData one) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            SiteData siteData = pm.makePersistent(one);
            one.setKey(siteData.getKey());
            return one;
        } finally {
            pm.close();
        }
    }
}
