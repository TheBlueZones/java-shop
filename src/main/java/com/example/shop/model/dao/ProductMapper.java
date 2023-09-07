package com.example.shop.model.dao;

import com.example.shop.Query.ProductListQuery;
import com.example.shop.commom.ApiRestResponse;
import com.example.shop.model.pojo.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface ProductMapper {
    Product selectByName(String name);

    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    int batchUpdateSellStatus(@Param("ids") Integer[] ids,
                              @Param("sellstatus") Integer sellstatus);

    List<Product> selectListForAdmin();

    List<Product> selectList(@Param("query")ProductListQuery query);
}