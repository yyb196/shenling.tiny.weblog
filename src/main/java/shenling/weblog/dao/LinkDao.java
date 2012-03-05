package shenling.weblog.dao;

import org.springframework.stereotype.Component;
import shenling.weblog.beans.Link;
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
 * @since 12-1-3 ÏÂÎç7:47
 */
@Component("LinkDao")
public class LinkDao {
    public List<Link> getLinks() {
        return DataCache.get(Constants.LinkList, new DataCache.Run<List<Link>>() {
            public List<Link> get() {
                PersistenceManager pm = PMF.get().getPersistenceManager();
                        try {
                            Query query = pm.newQuery(Link.class);
                            query.setRange(0, 8);
                            return (List<Link>) pm.detachCopyAll((Collection<?>)query.execute());
                        } finally {
                            pm.close();
                        }
            }
        });

    }

    public void addLink(Link link) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(link);
        } finally {
            pm.close();
        }
        DataCache.freeWithPrefix(Constants.LinkList);
    }

    public void removeLink(String linkAddress) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Query query = pm.newQuery(Link.class);
            query.setFilter("linkAddress==linkAddressParam");
            query.declareParameters("String linkAddressParam");
            query.deletePersistentAll(linkAddress);
        } finally {
            pm.close();
        }
        DataCache.freeWithPrefix(Constants.LinkList);
    }
}
