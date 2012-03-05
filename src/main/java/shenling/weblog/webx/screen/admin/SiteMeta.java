package shenling.weblog.webx.screen.admin;

import com.alibaba.citrus.turbine.Context;
import org.springframework.beans.factory.annotation.Autowired;
import shenling.weblog.dao.SiteMetaDao;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-17 обнГ12:38
 */
public class SiteMeta {
    @Autowired
    private SiteMetaDao siteMetaDao;
    public void execute(Context context) {
        context.put("menuId", "siteMeta");
        context.put("siteMeta", siteMetaDao.getSiteMeta());
    }
}
