package com.example.shop.server.impl;

import com.example.shop.Query.ProductListQuery;
import com.example.shop.commom.Constant;
import com.example.shop.expection.emmoeceException;
import com.example.shop.expection.expectionEnum;
import com.example.shop.model.dao.ProductMapper;
import com.example.shop.model.pojo.Product;
import com.example.shop.model.request.AddProductReq;
import com.example.shop.model.request.ProductListReq;
import com.example.shop.server.CategoryService;
import com.example.shop.server.ProductService;
import com.example.shop.vo.CategoryVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryService categoryService;

    @Override
    public void add(AddProductReq addProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);
        Product productOld = productMapper.selectByName(addProductReq.getName());
        if (productOld != null) {
            throw new emmoeceException(expectionEnum.DUPLICATE_NAME);
        }
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new emmoeceException(expectionEnum.CREAT_FAILED);
        }
    }

    @Override
    public void update(Product updateProduct) {
        Product productOld = productMapper.selectByName(updateProduct.getName());
        if (productOld != null && !productOld.getId().equals(updateProduct.getId())) {
            throw new emmoeceException(expectionEnum.NAME_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (count == 0) {
            throw new emmoeceException(expectionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Product productOld = productMapper.selectByPrimaryKey(id);
        if (productOld == null) {
            throw new emmoeceException(expectionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new emmoeceException(expectionEnum.DELETE_FAILED);
        }
    }

    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellstatus) {
        productMapper.batchUpdateSellStatus(ids, sellstatus);
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }

    @Override
    public Product detail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }

    @Override
    public PageInfo list(ProductListReq productListReq) {

        ProductListQuery productListQuery = new ProductListQuery();

        /*Fuzzy lookup*/
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {
            String keyword = new StringBuilder().append("%")
                    .append(productListReq.getKeyword()).append("%").toString();
        }
        /*Catalog processing: If you check the products in a certain directory,
         you need to find out not only the products in the directory, but also
         all the products in all sub-directories, so you need to get a list of
          directory ids.*/
        if (productListReq.getCategoryId() != null) {
            List<CategoryVo> categoryVoList = categoryService
                    .listCategoryForCustomer(productListReq.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVoList, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }
        //sort
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(),
                    productListReq.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(productListReq.getPageNum()
                    , productListReq.getPageSize());
        }
        List<Product> productList = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }

    private void getCategoryIds(List<CategoryVo> categoryVoList,
                                ArrayList<Integer> categoryIds) {

        for (CategoryVo categoryVo : categoryVoList) {
            if (categoryVo != null) {
                categoryIds.add(categoryVo.getId());
                getCategoryIds(categoryVo.getChildCategory(), categoryIds);
            }
        }
    }
}