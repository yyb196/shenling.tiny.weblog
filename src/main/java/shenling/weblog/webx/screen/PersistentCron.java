package shenling.weblog.webx.screen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import shenling.weblog.beans.ArticleData;
import shenling.weblog.beans.SiteData;
import shenling.weblog.dao.ArticleDataDao;
import shenling.weblog.dao.SiteDataDao;
import shenling.weblog.utils.Constants;
import shenling.weblog.utils.DataCache;
import shenling.weblog.utils.PersistenceCronUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-8 ÏÂÎç8:31
 */
public class PersistentCron {
    @Resource(name = "PersistenceCronUtils")
    private PersistenceCronUtils persistenceCronUtils;

    public void execute() {
        persistenceCronUtils.persistenceCacheData();
    }
}
