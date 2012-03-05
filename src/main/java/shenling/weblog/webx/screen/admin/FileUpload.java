package shenling.weblog.webx.screen.admin;

import com.alibaba.citrus.turbine.Context;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-19 обнГ4:44
 */
public class FileUpload {
    @Autowired
    HttpServletRequest request;
    public void execute(Context context){
        context.put("menuId", "fileUpload");
        context.put("id", request.getParameter("id"));
        context.put("msg", request.getParameter("msg"));
    }
}
