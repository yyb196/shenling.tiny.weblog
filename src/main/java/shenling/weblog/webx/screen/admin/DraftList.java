package shenling.weblog.webx.screen.admin;

import com.alibaba.citrus.turbine.Context;
import org.springframework.beans.factory.annotation.Autowired;
import shenling.weblog.dao.DraftDao;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-17 ионГ9:34
 */
public class DraftList {
    @Autowired
    DraftDao draftDao;

    public void execute(Context context) {
        context.put("menuId", "draftList");
        //TODO pagenation
        context.put("draftList", draftDao.getDrafts(null));
    }
}
