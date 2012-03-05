package shenling.weblog.webx.screen.admin;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import org.springframework.beans.factory.annotation.Autowired;
import shenling.weblog.beans.ArticleData;
import shenling.weblog.dao.ArticleDataDao;
import shenling.weblog.dao.SiteDataDao;
import shenling.weblog.utils.DataCache;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-20 ÉÏÎç10:22
 */
public class CacheOpt {
    @Autowired
    SiteDataDao siteDataDao;
    @Autowired
    ArticleDataDao articleDataDao;

    public void execute(Context context,
                        @Param(name = "siteCommentCount", defaultValue = "0") int siteCommentCount,
                        @Param(name = "siteArticleCount", defaultValue = "0") int siteArticleCount,
                        @Param(name = "link", defaultValue = "") String link,
                        @Param(name = "articleViewCount", defaultValue = "0") long articleViewCount,
                        @Param(name = "articleCommentCount", defaultValue = "0") int articleCommentCount,
                        @Param(name = "cacheKey", defaultValue = "") String cacheKey,
                        @Param(name = "update", defaultValue = "false") boolean update) {
        context.put("menuId", "cacheOpt");
        if (update) {
            if (cacheKey != null && !"".equals(cacheKey.trim())) {
                if (cacheKey.equals("all")) {
                    DataCache.invalidate();
                } else {
                    DataCache.free(cacheKey);
                }
            }
            DataCache.addSiteData(siteDataDao, siteArticleCount, siteCommentCount, 0);
            ArticleData articleData = articleDataDao.getArticleData(link);
            if (articleData != null) {
                DataCache.addArticleData(articleData, articleCommentCount, articleViewCount);
            }

            context.put("errMessage", "success!");
        }
    }
}
