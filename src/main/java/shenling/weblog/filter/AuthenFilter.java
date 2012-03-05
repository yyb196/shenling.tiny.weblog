package shenling.weblog.filter;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-5 ÏÂÎç4:44
 */
public class AuthenFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        UserService userService = UserServiceFactory.getUserService();
        if (!isAdmin(userService)) {
            if (servletResponse instanceof HttpServletResponse && servletRequest instanceof HttpServletRequest) {
                ((HttpServletResponse) servletResponse).
                        sendRedirect(userService.createLoginURL(((HttpServletRequest) servletRequest).getRequestURI()));
                return;
            }
            throw new RuntimeException("forbidden access!");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isAdmin(UserService userService) {

        try {
            return userService.isUserAdmin();
        } catch (Exception e) {
            return false;
        }
    }

    public void destroy() {

    }
}
