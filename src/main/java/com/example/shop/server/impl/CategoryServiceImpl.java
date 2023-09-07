package com.example.shop.server.impl;

import com.example.shop.expection.emmoeceException;
import com.example.shop.expection.expectionEnum;
import com.example.shop.model.dao.CategoryMapper;
import com.example.shop.model.pojo.Category;
import com.example.shop.model.request.AddCategoryRequest;
import com.example.shop.server.CategoryService;
import com.example.shop.vo.CategoryVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public void add(AddCategoryRequest addCategoryRequest) {
        Category category = new Category();
        /*只要是参数名字相同，就把前面的赋值给后面的*/
        BeanUtils.copyProperties(addCategoryRequest, category);
        Category categoryOld = categoryMapper.selectByName(addCategoryRequest.getName());
        if (categoryOld != null) {
            throw new emmoeceException(expectionEnum.DUPLICATE_NAME);
        }
        int count = categoryMapper.insertSelective(category);
        if (count == 0) {
            throw new emmoeceException(expectionEnum.ADD_FAILED);
        }
    }

    @Override
    public void update(Category updateCategory) {
        if (updateCategory.getName() != null) {
            Category categoryOld = categoryMapper.selectByName(updateCategory.getName());
            if (categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())) {
                throw new emmoeceException(expectionEnum.DUPLICATE_NAME);
            }
        }
        int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            throw new emmoeceException(expectionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        if (categoryOld == null) {
            throw new emmoeceException(expectionEnum.NO_GOODS_IN_DATEBASE);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new emmoeceException(expectionEnum.DELETE_FAILED);
        }
    }

    @Override
    public PageInfo listForadmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type,order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo(categoryList);
        return pageInfo;
    }

    @Override
    @Cacheable(value = "listCategoryForCustomer")
    public List<CategoryVo> listCategoryForCustomer(Integer parentId) {
        ArrayList<CategoryVo> categoryVoArrayList = new ArrayList<>();
        recursivelyFindCategories(categoryVoArrayList, parentId);
        return categoryVoArrayList;
    }

    /*逻辑混乱了*/
    private void recursivelyFindCategories(List<CategoryVo> categoryVoList,
                                           Integer parentId) {
        /*get all directories recursively*/
        List<Category> categoryList = categoryMapper.selectCategoryByPArentID(parentId);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (Category category : categoryList) {
                CategoryVo categoryVo = new CategoryVo();
                BeanUtils.copyProperties(category, categoryVo);
                categoryVoList.add(categoryVo);
                recursivelyFindCategories(categoryVo.getChildCategory(),
                        categoryVo.getId());
            }
        }
    }
}
