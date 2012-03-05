package shenling.weblog.servlet;

import com.google.appengine.api.datastore.Blob;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import shenling.weblog.beans.BFile;
import shenling.weblog.dao.BFileDao;
import shenling.weblog.utils.SpringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-19 ÏÂÎç4:16
 */
public class FileUploadService extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iterator = null;
        try {
            iterator = upload.getItemIterator(req);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        BFile file = new BFile();
        BFileDao bFileDao = (BFileDao) SpringUtils.getBean("BFileDao");
        String key = String.valueOf(System.currentTimeMillis());
        try {
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                InputStream stream = item.openStream();
                if (item.isFormField()) {
//                    fileMemo = item.getString("GBK");
                } else {
                    Blob bImg = new Blob(IOUtils.toByteArray(stream));
                    file.setBlob(bImg);
                    file.setContentType("image/png");

                    file.setLink(key);
                }

            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        if (file.getBlob() != null) {
            bFileDao.putBFile(file);
            resp.sendRedirect("/admin/fileUpload.htm?msg=success&id=" + key);
            return;
        }
        resp.sendRedirect("/admin/fileUpload.htm?msg=fail");
    }

    
}
