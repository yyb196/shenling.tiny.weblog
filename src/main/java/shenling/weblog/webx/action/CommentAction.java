package shenling.weblog.webx.action;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.google.appengine.api.datastore.Text;
import org.springframework.beans.factory.annotation.Autowired;
import shenling.weblog.beans.ArticleData;
import shenling.weblog.beans.Comment;
import shenling.weblog.beans.SiteData;
import shenling.weblog.dao.ArticleDataDao;
import shenling.weblog.dao.CommentDao;
import shenling.weblog.dao.SiteDataDao;
import shenling.weblog.utils.Constants;
import shenling.weblog.utils.DataCache;
import shenling.weblog.utils.PersistenceCronUtils;
import shenling.weblog.utils.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-8 下午4:04
 */
public class CommentAction {
    private static final Pattern emailPat = Pattern.compile("\\w+@\\w+\\.\\w+");
    @Autowired
    private HttpServletRequest request;
    @Resource(name = "CommentDao")
    CommentDao commentDao;
    @Resource(name = "StringUtils")
    StringUtils stringUtils;
    @Resource(name = "ArticleDataDao")
    ArticleDataDao articleDataDao;
    @Resource(name = "SiteDataDao")
    SiteDataDao siteDataDao;
    @Resource(name = "PersistenceCronUtils")
    private PersistenceCronUtils persistenceCronUtils;

    public void doPostComment(Context context, Navigator navigator) {
        String title = request.getParameter("title");
        String email = request.getParameter("email");
        String commenterUrl = request.getParameter("commenterUrl");
        String content = request.getParameter("content");
        String articleLink = request.getParameter("articleLink");
        Comment comment = new Comment();
        comment.setArticleLink(articleLink);
        comment.setCommenterUrl(commenterUrl);
        comment.setEmail(email);
        comment.setTitle(title);
        comment.setContent(new Text(content));
        if (!validate(title, email, content, articleLink)) {
            context.put("tempComment", comment);
        } else if (!validateEmail(email)) {
            context.put("tempComment", comment);
            context.put("errMsg", "email格式错误！");
        } else {
            //校验成功后入库
            commentDao.addOneComment(comment);
            context.put("addCommentSuccess", Boolean.TRUE);
            ArticleData articleData = articleDataDao.getArticleData(articleLink);
            DataCache.addArticleData(articleData, 1, 0);
            DataCache.addSiteData(siteDataDao, 0, 1, 0);
            DataCache.freeWithPrefix(Constants.ArticleCachePrefix);
            DataCache.freeWithPrefix(Constants.ArticleListCachePrefix);
        }
        navigator.forwardTo("view").withParameter("id", articleLink).withParameter("jsTarget", "commentForm");
    }

    public boolean validate(String... strs) {
        for (String s : strs) {
            if (stringUtils.isBlank(s)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateEmail(String email) {
        return emailPat.matcher(email).find();
    }
}
