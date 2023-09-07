package com.example.shop.cotroller;


import com.example.shop.commom.ApiRestResponse;
import com.example.shop.commom.Constant;
import com.example.shop.expection.expectionEnum;
import com.example.shop.model.pojo.Category;
import com.example.shop.model.pojo.User;
import com.example.shop.model.request.AddCategoryRequest;
import com.example.shop.model.request.UpdateCategoryRequest;
import com.example.shop.server.CategoryService;
import com.example.shop.server.UserService;
import com.example.shop.vo.CategoryVo;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    UserService userService;
    @Autowired
    CategoryService categoryService;

    @ApiOperation("后台添加目录")
    @PostMapping("/admin/category/add")
    @ResponseBody
    /*报错应为没有最后写全*/
    public ApiRestResponse addCategory(HttpSession session,
                                       @Valid @RequestBody AddCategoryRequest addCategoryRequest) {
  /*      if (addCategoryRequest.getName() == null || addCategoryRequest.getType() == null ||
                addCategoryRequest.getOrderNum() == null || addCategoryRequest.getParentId() == null) {
            return ApiRestResponse.error(expectionEnum.PARA_NOT_NULL);
        }*/
        /*出现系统异常才正常*/
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(expectionEnum.NEED_LOGIN);
        }
        /*校验是否是管理员*/
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            categoryService.add(addCategoryRequest);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(expectionEnum.NEED_ADMIN);
        }
    }

    @ApiOperation("后台更新目录")
    @PostMapping("/admin/category/update")
    @ResponseBody
    /*报错应为没有最后写全*/
    public ApiRestResponse updateCategory(HttpSession session,
                                          @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(expectionEnum.NEED_LOGIN);
        }
        /*校验是否是管理员*/
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            Category category = new Category();
            BeanUtils.copyProperties(updateCategoryRequest, category);
            categoryService.update(category);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(expectionEnum.NEED_ADMIN);
        }
    }

    @ApiOperation("后台删除目录")
    @PostMapping("/admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleteCategory(@RequestParam Integer id) {
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台目录列表")
    @PostMapping("/admin/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum,
                                                @RequestParam Integer pageSize) {
        PageInfo pageInfo = categoryService.listForadmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }


    @ApiOperation("前台目录列表")
    @PostMapping("/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer() {
        List<CategoryVo> categoryVos=categoryService.listCategoryForCustomer(0);
        return ApiRestResponse.success(categoryVos);
    }
}
