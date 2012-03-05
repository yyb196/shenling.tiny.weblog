package shenling.weblog.webx.control;

import com.alibaba.citrus.turbine.Context;
import shenling.weblog.beans.SiteData;
import shenling.weblog.dao.SiteDataDao;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-8 обнГ8:18
 */
public class Head {
    @Resource(name = "SiteDataDao")
    SiteDataDao siteDataDao;

    public void execute(Context context) {
        SiteData siteData = siteDataDao.getSiteData();
        context.put("siteData", siteData);
    }
}
