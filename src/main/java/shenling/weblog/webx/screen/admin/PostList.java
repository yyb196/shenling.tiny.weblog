package shenling.weblog.webx.screen.admin;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import shenling.weblog.dao.ArticleDao;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-16 обнГ7:15
 */
public class PostList {
    @Resource(name = "ArticleDao")
    ArticleDao articleDao;

    public void execute(Context context) {
        context.put("articleList", articleDao.getArticles(null));
        context.put("menuId", "postList");
    }
}
