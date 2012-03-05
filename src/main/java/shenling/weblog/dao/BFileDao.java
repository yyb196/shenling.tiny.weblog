package shenling.weblog.dao;

import org.springframework.stereotype.Component;
import shenling.weblog.beans.BFile;
import shenling.weblog.dao.pmf.PMF;
import shenling.weblog.utils.Constants;
import shenling.weblog.utils.DataCache;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-19 ÏÂÎç4:20
 */
@Component("BFileDao")
public class BFileDao {
    public BFile getFile(final String link) {
        return DataCache.get(Constants.BFilePrefix + link, new DataCache.Run<BFile>() {
            public BFile get() {
                PersistenceManager pm = PMF.get().getPersistenceManager();
                try {
                    Query query = pm.newQuery(BFile.class);
                    query.setFilter("link==linkParam");
                    query.declareParameters("String linkParam");
                    query.setRange(0, 1);
                    List<BFile> result = (List<BFile>) pm.detachCopyAll((Collection<?>) query.execute(link));
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

    public BFile putBFile(BFile f) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            BFile bFile = pm.makePersistent(f);
            f.setKey(bFile.getKey());
            return f;
        } finally {
            pm.close();
        }
    }
}
