package shenling.weblog.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.springframework.stereotype.Component;
import shenling.weblog.beans.Draft;
import shenling.weblog.dao.pmf.PMF;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-3 ÏÂÎç3:47
 */
@Component("DraftDao")
public class DraftDao {
    public List<Draft> getDrafts(Date from) {
        if (from == null) {
            from = new Date();
        }
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Query query = pm.newQuery(Draft.class);
            query.setFilter("createDate < fromParam");
            query.setOrdering("createDate desc");
            query.declareParameters("java.util.Date fromParam");
            return (List<Draft>) pm.detachCopyAll((Collection<?>) query.execute(from));
        } finally {
            pm.close();
        }
    }

    public Draft getDraft(String strKey) {
        Key key = KeyFactory.stringToKey(strKey);
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Draft d = pm.getObjectById(Draft.class, key);
            return pm.detachCopy(d);
        } finally {
            pm.close();
        }
    }

    public Draft putDraft(Draft draft) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Draft draft1 = pm.makePersistent(draft);
            draft.setKey(draft1.getKey());
            return draft;
        } finally {
            pm.close();
        }
    }

    public void deleteDraft(String strKey) {
        Key key = KeyFactory.stringToKey(strKey);
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Query query = pm.newQuery(Draft.class);
            query.setFilter("key==keyParam");
            query.declareParameters("com.google.appengine.api.datastore.Key keyParam");
            query.deletePersistentAll(key);
        } finally {
            pm.close();
        }
    }
}
