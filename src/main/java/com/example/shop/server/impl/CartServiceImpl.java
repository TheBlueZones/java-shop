package com.example.shop.server.impl;

import com.example.shop.commom.Constant;
import com.example.shop.expection.emmoeceException;
import com.example.shop.expection.expectionEnum;
import com.example.shop.model.dao.CartMapper;
import com.example.shop.model.dao.ProductMapper;
import com.example.shop.model.pojo.Cart;
import com.example.shop.model.pojo.Product;
import com.example.shop.server.CartService;
import com.example.shop.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    ProductMapper productMapper;

    @Autowired
    CartMapper cartMapper;

    @Override
    public List<CartVo> list(Integer usedId) {
        List<CartVo> cartVos = cartMapper.selectList(usedId);

        for (CartVo cartvo : cartVos) {
            cartvo.setTotalPrice(cartvo.getPrice() * cartvo.getQuantity());
        }
        return cartVos;
    }

    @Override
    public List<CartVo> add(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            /*this item is not in the shoping cart,you need to add a record*/
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.SELECTED);
            cartMapper.insertSelective(cart);
        } else {
            /*this item is not in the shoping cart, add up*/
            count = cart.getQuantity() + count;
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setSelected(Constant.Cart.SELECTED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }

    private void validProduct(Integer productId, Integer count) {
        Product product = productMapper.selectByPrimaryKey(productId);
        /*judge whether the product exists or is on the shelf*/
        if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
            throw new emmoeceException(expectionEnum.PRODUCT_ABNORMAL_ATATUS);
        }
        /*judje commodity stocks*/
        if (count > product.getStock()) {
            throw new emmoeceException(expectionEnum.NOT_ENOUGH);
        }
    }

    @Override
    public List<CartVo> update(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            /*this item is not in the shoping cart,can't update*/
            throw new emmoeceException(expectionEnum.UPDATE_FAILED);
        } else {
            /*this item is not in the shoping cart, update count*/
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setSelected(Constant.Cart.SELECTED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVo> delete(Integer userId, Integer productId) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new emmoeceException(expectionEnum.DELETE_FAILED);
        } else {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.list(userId);
    }

    @Override
    public List<CartVo> selectOrNot(Integer userId, Integer productId,
                                    Integer selected) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new emmoeceException(expectionEnum.UPDATE_FAILED);
        } else {
            cartMapper.selectOrNot(userId, productId, selected);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVo> selectAllOrNot(Integer userId,
                                       Integer selected) {
        cartMapper.selectOrNot(userId, null, selected);
        return this.list(userId);
    }
}
