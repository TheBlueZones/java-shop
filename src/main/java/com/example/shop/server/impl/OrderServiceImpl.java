package com.example.shop.server.impl;

import com.example.shop.commom.Constant;
import com.example.shop.expection.emmoeceException;
import com.example.shop.expection.expectionEnum;
import com.example.shop.filter.UserFilter;
import com.example.shop.model.dao.CartMapper;
import com.example.shop.model.dao.OrderItemMapper;
import com.example.shop.model.dao.OrderMapper;
import com.example.shop.model.dao.ProductMapper;
import com.example.shop.model.pojo.Order;
import com.example.shop.model.pojo.OrderItem;
import com.example.shop.model.pojo.Product;
import com.example.shop.model.request.CreatOrderReq;
import com.example.shop.server.CartService;
import com.example.shop.server.OrderService;
import com.example.shop.server.UserService;
import com.example.shop.untils.OrderCodeFactory;
import com.example.shop.untils.QrCodeGenerator;
import com.example.shop.vo.CartVo;
import com.example.shop.vo.OrderItemVo;
import com.example.shop.vo.OrderVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    CartService cartService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CartMapper cartMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    UserService userService;
    @Value("${file.upload.ip}")
    String ip;

    @Override
    @Transactional(rollbackFor = Exception.class)/*数据库事务*/
    public String creat(CreatOrderReq creatOrderReq) {
        //    get user ID
        Integer userId = UserFilter.currentUser.getId();
//    find checked products from shopping cart
        List<CartVo> cartVoList = cartService.list(userId);
        ArrayList<CartVo> cartVoListTemp = new ArrayList<>();
        for (CartVo cartVo : cartVoList) {
            if (cartVo.getSelected().equals(Constant.Cart.SELECTED)) {
                cartVoListTemp.add(cartVo);
            }
        }
        cartVoList = cartVoListTemp;
//    if cheecked cart is empty then erport an error
        if (CollectionUtils.isEmpty(cartVoList)) {
            throw new emmoeceException(expectionEnum.CART_EMPTY);
        }
//    judje goods is  not existence ,shelf strtus,storage
        validSaleStatusAndStock(cartVoList);
//把购物车对象转为订单item对象
        List<OrderItem> orderItemList = cartVoListToOrderItemList(cartVoList);
        /*扣库存*/
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new emmoeceException(expectionEnum.NOT_ENOUGH);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        //把购物车中的已勾选商品删除
        cleanCart(cartVoList);

        Order order = new Order();
        //生成订单号，有独立的规则
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(creatOrderReq.getReceiverName());
        order.setReceiverAddress(creatOrderReq.getReceiverAddress());
        order.setReceiverMobile(creatOrderReq.getReceiverMobile());
        order.setOrderStatus(expectionEnum.OrderStatusEnum.NOT_PIAD.getCode());

        order.setPostage(0);
        order.setPaymentType(1);
        //插入到Order表
        orderMapper.insertSelective(order);
        //循环保存每个商品到order_item表
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }
        //把结果返回
        return orderNo;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (OrderItem orderItem : orderItemList) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cleanCart(List<CartVo> cartVoList) {
        for (CartVo cartvo : cartVoList) {
            cartMapper.deleteByPrimaryKey(cartvo.getId());
        }
    }

    public void validSaleStatusAndStock(List<CartVo> cartVoList) {
        for (CartVo cartvo : cartVoList) {
            Product product = productMapper.selectByPrimaryKey(cartvo.getProductId());
            /*judge whether the product exists or is on the shelf*/
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new emmoeceException(expectionEnum.NOT_ENOUGH);
            }
            /*judje commodity stocks*/
            if (cartvo.getQuantity() > product.getStock()) {
                throw new emmoeceException(expectionEnum.NOT_ENOUGH);
            }
        }
    }

    public List<OrderItem> cartVoListToOrderItemList(List<CartVo> cartVoList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartVo cartvo : cartVoList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartvo.getProductId());
            orderItem.setProductName(cartvo.getProductName());
            orderItem.setProductImg(cartvo.getProductImage());
            orderItem.setUnitPrice(cartvo.getPrice());
            orderItem.setQuantity(cartvo.getQuantity());
            orderItem.setTotalPrice(cartvo.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    @Override
    public OrderVo detail(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new emmoeceException(expectionEnum.NO_ORDER);
        }
        /*订单不存在，判断属性*/
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)) {
            throw new emmoeceException(expectionEnum.NOT_YOUR_ORDER);
        }
        OrderVo orderVo = getOrderVo(order);/*获得可以返回的OrderVo对象*/
        return orderVo;
        /*我连代码的逻辑是什么都不清楚了*/
    }

    private OrderVo getOrderVo(Order order) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order, orderVo);
        /*获取订单对应的orderItemVOlist*/
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVo> orderItemVoList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(orderItem, orderItemVo);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setOrderStatusName(expectionEnum.OrderStatusEnum.
                codeof(orderVo.getOrderStatus()).getValue());
        return orderVo;
    }

    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize) {
        Integer userId = UserFilter.currentUser.getId();
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        List<OrderVo> orderVOList = orderListToOrderVoList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    private List<OrderVo> orderListToOrderVoList(List<Order> orderList) {
        List<OrderVo> orderVOList = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderVo orderVO = getOrderVo(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    @Override
    public void cancel(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //查不到订单，报错
        if (order == null) {
            throw new emmoeceException(expectionEnum.NO_ORDER);
        }
        //验证用户身份
        //订单存在，需要判断所属

        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)) {
            throw new emmoeceException(expectionEnum.NOT_YOUR_ORDER);
        }
        if (order.getOrderStatus().equals(expectionEnum.OrderStatusEnum.NOT_PIAD.getCode())) {
            order.setOrderStatus(expectionEnum.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new emmoeceException(expectionEnum.WRONG_ORDER_STATUS);
        }
    }
@Override
    public String qrcode(String orderNo) {
        ServletRequestAttributes attributes
                = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String address = ip +":"+ request.getLocalPort();
        String plyUrl="http://"+address+"/pay?orderNo="+orderNo;

        try {
            QrCodeGenerator.generateQRCodeImage(plyUrl,350,350,
                    Constant.FILE_UOLOAD_DIR+orderNo+".png");
        } catch (WriterException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String pngAderss ="http://"+address+"/images/"+orderNo+".png";
        return pngAderss;
    }
    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //查不到订单，报错
        if (order == null) {
            throw new   emmoeceException(expectionEnum.NO_ORDER);
        }
        if (order.getOrderStatus() == expectionEnum.OrderStatusEnum.NOT_PIAD.getCode()) {
            order.setOrderStatus(expectionEnum.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new   emmoeceException(expectionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllForAdmin();
        List<OrderVo> orderVOList = orderListToOrderVoList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    //发货
    @Override
    public void deliver(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //查不到订单，报错
        if (order == null) {
            throw new   emmoeceException(expectionEnum.NO_ORDER);
        }
        if (order.getOrderStatus() == expectionEnum.OrderStatusEnum.PAID.getCode()) {
            order.setOrderStatus(expectionEnum.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new   emmoeceException(expectionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void finish(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //查不到订单，报错
        if (order == null) {
            throw new   emmoeceException(expectionEnum.NO_ORDER);
        }
        //如果是普通用户，就要校验订单的所属
        if (!userService.checkAdminRole(UserFilter.currentUser) && !order.getUserId().equals(UserFilter.currentUser.getId())) {
            throw new   emmoeceException(expectionEnum.NOT_YOUR_ORDER);
        }
        //发货后可以完结订单
        if (order.getOrderStatus() == expectionEnum.OrderStatusEnum.DELIVERED.getCode()) {
            order.setOrderStatus(expectionEnum.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new   emmoeceException(expectionEnum.WRONG_ORDER_STATUS);
        }
    }
}
