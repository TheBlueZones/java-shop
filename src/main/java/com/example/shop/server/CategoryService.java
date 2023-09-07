package com.example.shop.server;

import com.example.shop.model.pojo.Category;
import com.example.shop.model.request.AddCategoryRequest;
import com.example.shop.vo.CategoryVo;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CategoryService {

    void add(AddCategoryRequest addCategoryRequest);

    void update(Category category);

    void delete(Integer id);

    PageInfo listForadmin(Integer pageNum, Integer pageSize);

    List<CategoryVo> listCategoryForCustomer(Integer parentId);
}
