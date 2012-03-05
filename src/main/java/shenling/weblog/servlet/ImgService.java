package shenling.weblog.servlet;

import com.google.appengine.api.datastore.Blob;
import shenling.weblog.beans.BFile;
import shenling.weblog.dao.BFileDao;
import shenling.weblog.utils.SpringUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-19 ÏÂÎç4:19
 */
public class ImgService extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String id = req.getParameter("id");
        BFileDao bFileDao = (BFileDao) SpringUtils.getBean("BFileDao");
        BFile file = bFileDao.getFile(id);
        if(file == null){
            resp.sendRedirect("/404.html");
        }
        Blob b = file.getBlob();
        //img/png  if html is text/html; charset=GBK
        resp.setContentType(file.getContentType());
        resp.getOutputStream().write(b.getBytes());
        resp.getOutputStream().close();
    }
}
