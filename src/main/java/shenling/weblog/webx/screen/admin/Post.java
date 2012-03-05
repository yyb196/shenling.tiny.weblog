package shenling.weblog.webx.screen.admin;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import shenling.weblog.beans.Article;
import shenling.weblog.beans.Draft;
import shenling.weblog.dao.ArticleDao;
import shenling.weblog.dao.DraftDao;
import shenling.weblog.utils.StringUtils;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-3 下午3:03
 */
public class Post {
    @Autowired
    ArticleDao articleDao;

    @Autowired
    DraftDao draftDao;

    @Autowired
    StringUtils stringUtils;

    public void execute(@Param(name = "id") String link, @Param(name = "key") String key, Context context, Navigator navigator) {
        context.put("menuId", "post");
        if (context.get("article") == null) {
            if (!stringUtils.isBlank(link)) {
                //这个是已发布文章修改的流程
                Article article = articleDao.getArticle(link);
                if (article != null) {
                    context.put("article", article);
                    context.put("modify", Boolean.TRUE);
                }
            } else if (!stringUtils.isBlank(key)) {
                //这个是草稿的修改流程
                Draft draft = draftDao.getDraft(key);
                if (draft != null) {
                    context.put("article", draft);
                    context.put("key", key);
                }
            }
        }
    }
}
