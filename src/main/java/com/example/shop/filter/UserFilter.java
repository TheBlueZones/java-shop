package com.example.shop.filter;

import com.example.shop.commom.Constant;
import com.example.shop.model.pojo.User;
import com.example.shop.server.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/*Administrator validation filter*/
public class UserFilter implements Filter {

    public  static User currentUser;/*hope to save it*/
    @Autowired
    UserService userService;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if ("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            /*obtain session*/
            request = (HttpServletRequest) servletRequest;
            HttpSession session = request.getSession();

            currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
            if (currentUser == null) {
                PrintWriter out = new HttpServletResponseWrapper(
                        (HttpServletResponse) servletResponse).getWriter();
                out.write("{\n" +
                        "    \"status\": 10008,\n" +
                        "    \"msg\": \"NEED_LOGIN \",\n" +
                        "    \"data\": null\n" +
                        "}");
                out.flush();
                out.close();
                return;
            }

            filterChain.doFilter(servletRequest, servletResponse);/**/
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
