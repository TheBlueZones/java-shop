package com.example.shop.filter;

import com.example.shop.commom.ApiRestResponse;
import com.example.shop.commom.Constant;
import com.example.shop.expection.expectionEnum;
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
public class AdminFilter implements Filter {

    @Autowired
    UserService userService;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        /*obtain session*/
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpSession session=request.getSession();

        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("{\n" +
                    "    \"status\": 10008,\n" +
                    "    \"msg\": \"NEED_LOGIN \",\n" +
                    "    \"data\": null\n" +
                    "}");
                out.flush();
                out.close();
                return;
        }
        /*校验是否是管理员*/
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
          filterChain.doFilter(servletRequest,servletResponse);
        } else {
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("{\n" +
                    "    \"status\": 10010,\n" +
                    "    \"msg\": \"NEED_ADMIN \",\n" +
                    "    \"data\": null\n" +
                    "}");
            out.flush();
            out.close();
            return;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
