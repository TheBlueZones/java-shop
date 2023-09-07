package com.example.shop.server;

import com.example.shop.model.pojo.Product;
import com.example.shop.model.request.AddProductReq;
import com.example.shop.model.request.ProductListReq;
import com.github.pagehelper.PageInfo;

public interface ProductService {

    void add(AddProductReq addProductReq);

    void update(Product updateProduct);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellstatus);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductListReq productListReq);
}
