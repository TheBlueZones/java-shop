package com.example.shop.server;

import com.example.shop.model.request.CreatOrderReq;
import com.example.shop.vo.OrderVo;
import com.github.pagehelper.PageInfo;

public interface OrderService {
    String creat(CreatOrderReq creatOrderReq);

    OrderVo detail(String orderNo);

    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);


    String qrcode(String orderNo);

    void pay(String orderNo);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    void deliver(String orderNo);

    void finish(String orderNo);
}
