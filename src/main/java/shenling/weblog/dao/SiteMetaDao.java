package shenling.weblog.dao;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import shenling.weblog.beans.SiteMetaInfo;
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
 * @since 12-1-3 ÏÂÎç3:01
 */
@Component("SiteMetaDao")
//prototype
@Scope("singleton")
public class SiteMetaDao {
    public SiteMetaInfo getSiteMeta() {
        return DataCache.get(Constants.SiteMetaInfoKey, new DataCache.Run<SiteMetaInfo>() {
            public SiteMetaInfo get() {
                PersistenceManager pm = PMF.get().getPersistenceManager();
                try {
                    Query query = pm.newQuery(SiteMetaInfo.class);
                    query.setRange(0, 1);
                    List<SiteMetaInfo> rs = (List<SiteMetaInfo>) query.execute();
                    SiteMetaInfo siteMeta = rs == null ? null : rs.isEmpty() ? null : rs.get(0);
                    if (siteMeta == null) {
                        siteMeta = new SiteMetaInfo();
                        putSiteMeta(siteMeta);
                    } else {
                        siteMeta = pm.detachCopy(siteMeta);
                    }
                    return siteMeta;
                } finally {
                    pm.close();
                }
            }
        });
    }

    public void putSiteMeta(SiteMetaInfo metaInfo) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(metaInfo);
        } finally {
            pm.close();
        }
        DataCache.free(Constants.SiteMetaInfoKey);
    }
}
