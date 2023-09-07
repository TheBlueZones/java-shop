package com.example.shop.cotroller;


import com.example.shop.commom.ApiRestResponse;
import com.example.shop.filter.UserFilter;
import com.example.shop.server.CartService;
import com.example.shop.vo.CartVo;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractPipeImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;

    @GetMapping("/list")
    @ApiOperation("购物车列表")
    public ApiRestResponse list() {
//        Obtain user ID internally to privent horizontal overreach
        List<CartVo> cartList=cartService.list(UserFilter.currentUser.getId());
        return ApiRestResponse.success(cartList);
    }

    @PostMapping("/add")
    @ApiOperation("添加商品到购物车")
    public ApiRestResponse add(@RequestParam Integer productId,
                               @RequestParam Integer count) {
        List<CartVo> cartVoList=cartService.add(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVoList);
    }

    @PostMapping("/update")
    @ApiOperation("更新购物车")
    public ApiRestResponse update(@RequestParam Integer productId,
                                  @RequestParam Integer count) {
        List<CartVo> cartVoList=cartService
                .update(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVoList);
    }

    @PostMapping("/delete")
    @ApiOperation("删除购物车")
    public ApiRestResponse delete(@RequestParam Integer productId) {
        List<CartVo> cartVoList=cartService.
                delete(UserFilter.currentUser.getId(), productId);
        return ApiRestResponse.success(cartVoList);
    }

    @PostMapping("/select")
    @ApiOperation("选择/不选择购物车的某商品")
    public ApiRestResponse select(@RequestParam Integer productId,
   @RequestParam Integer selected ) {
        List<CartVo> cartVoList = cartService.
                selectOrNot(UserFilter.currentUser.getId(), productId,selected);
        return ApiRestResponse.success(cartVoList);
    }

    @PostMapping("/selectAll")
    @ApiOperation("全选择或全不选择购物车的某商品")
    public ApiRestResponse selectAll(@RequestParam Integer selected ) {
        List<CartVo> cartVoList = cartService.
                selectAllOrNot(UserFilter.currentUser.getId(),selected);
        return ApiRestResponse.success(cartVoList);
    }
}
