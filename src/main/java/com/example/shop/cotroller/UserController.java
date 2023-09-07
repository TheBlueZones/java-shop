package com.example.shop.cotroller;

import com.example.shop.commom.ApiRestResponse;
import com.example.shop.commom.Constant;
import com.example.shop.expection.emmoeceException;
import com.example.shop.expection.expectionEnum;
import com.example.shop.model.pojo.User;
import com.example.shop.server.UserService;
import com.example.shop.untils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

/*
 * description users cotroller
 * */
@Controller
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/test")
    @ResponseBody
    public User personalInfo() {
        return userService.getUser();
    }

    /*@RequestParam indicate a request*/
    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("userName") String userName,
                                    @RequestParam("password") String password) throws emmoeceException {
        /*null和空字符串有区别*/
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(expectionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(expectionEnum.NEED_PASSWORD);
        }
        if (password.length() < 8) {
            return ApiRestResponse.error(expectionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(userName, password);
        return ApiRestResponse.success();
    }

    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("userName") String userName,
                                    @RequestParam("password") String password,
                                    HttpSession session) throws emmoeceException {
        /*null和空字符串有区别*/
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(expectionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(expectionEnum.NEED_PASSWORD);
        }

        User user = userService.login(userName, password);
        /*don't storage password*/
        user.setPassword(null);
        session.setAttribute(Constant.IMOOC_MALL_USER, user);//这句代码是将用户信息存入session中
        return ApiRestResponse.success(user);
    }

    /*update personal sign*/
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session, @RequestParam String signature) throws emmoeceException {
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) return ApiRestResponse.error(expectionEnum.NEED_LOGIN);
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateINfo(user);
        return ApiRestResponse.success();
    }

    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session){
        session.removeAttribute(Constant.IMOOC_MALL_USER);
        return ApiRestResponse.success();
    }

    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName,
                                    @RequestParam("password") String password,
                                    HttpSession session) throws emmoeceException {
        /*null和空字符串有区别*/
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(expectionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(expectionEnum.NEED_PASSWORD);
        }

        User user = userService.login(userName, password);
        if (userService.checkAdminRole(user)) {
            user.setPassword(null);
            session.setAttribute(Constant.IMOOC_MALL_USER, user);
            return ApiRestResponse.success(user);
        }else {
            return ApiRestResponse.error(expectionEnum.NEED_ADMIN);
        }
    }
}

