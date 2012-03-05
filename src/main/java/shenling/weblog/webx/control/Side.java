package shenling.weblog.webx.control;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import shenling.weblog.beans.SiteMetaInfo;
import shenling.weblog.dao.CommentDao;
import shenling.weblog.dao.LinkDao;
import shenling.weblog.dao.SiteMetaDao;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-3 ÏÂÎç7:33
 */
public class Side {
    @Resource(name="SiteMetaDao")
        SiteMetaDao siteMetaDao;
    @Resource(name="CommentDao")
    CommentDao commentDao;
    @Resource(name="LinkDao")
    LinkDao linkDao;
    public void execute(Context context, Navigator navigator) throws Exception {
        SiteMetaInfo siteMeta = siteMetaDao.getSiteMeta();
        context.put("siteMeta", siteMeta);
        context.put("recentComments", commentDao.getRecentComments());
        context.put("mostCommentArticles", Collections.emptyList());
        context.put("links", linkDao.getLinks());
    }
}
