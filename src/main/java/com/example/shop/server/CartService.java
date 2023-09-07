package com.example.shop.server;

import com.example.shop.vo.CartVo;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CartService {
    List<CartVo> list(Integer usedId);

    List<CartVo> add(Integer userId, Integer productId, Integer count);

    List<CartVo> update(Integer userId, Integer productId, Integer count);

    List<CartVo> delete(Integer userId, Integer productId);

    List<CartVo> selectOrNot(Integer userId, Integer productId,
                             Integer selected);

    List<CartVo> selectAllOrNot(Integer userId,
                                Integer selected);
}
