package com.example.shop.expection;

public enum expectionEnum {
    NEED_USER_NAME(10001, "username is not be awallowed null"),/*这里是逗号*/
    NEED_PASSWORD(10002, "password is not be awallowed null"),
    PASSWORD_TOO_SHORT(10003, "Password cannot be less than 8 characters"),
    DUPLICATE_NAME(10004, "duplicate name is not allowed "),
    INSERT_FAILED(10005, "insert failed"),
    USER_ERROR(10006, "Account has not been registered"),
    PASSWORD_WRONG(10007, "password is incorrect"),
    NEED_LOGIN(10008, "User not log in"),
    UPDATE_FAILED(10009, "update failed"),
    NEED_ADMIN(10010, "admin is not"),

    NAME_NOT_NILL(100011, "name is not be awallowed null"),

    PARA_NOT_NULL(100012, "paramter is not awalled  null"),

    ADD_FAILED(100012, "add failed"),

    REQYEST_PARAM_ERROR(100013, "paramter error"),

    DELETE_FAILED(100014, "delete failed"),

    NO_GOODS_IN_DATEBASE(100012, "datebase is not validation"),

    CREAT_FAILED(100013, "create failed"),

    MKDIR_FAILED(100014, "mkdir failed"),

    UPLOAD_FAILED(100015, "upload failed"),

    NAME_EXISTED(100016, "name exists"),

    PRODUCT_ABNORMAL_ATATUS(100017, "product is abnormal"),

    NOT_ENOUGH(100018, "not enough"),

    CART_EMPTY(100019, "goods in cart be selected is empty"),

    NO_ENUM(100020, "未找到对应的枚举类"),

    NO_ORDER(100021, "订单不存在"),

    NOT_YOUR_ORDER(100022, "不是你的订单"),
    WRONG_ORDER_STATUS(100023, "错误的订单状态"),

    SYSTEM_ERROR(20000, "system error");

    /**
     * exxpection enum
     */
    Integer code;

    String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    expectionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public enum OrderStatusEnum{

        CANCELED(0,"用户已经取消"),
        NOT_PIAD(10,"未付款"),
        PAID(20,"已付款"),
        DELIVERED(30,"已发货"),
        FINISHED(40,"交易完成");
        private String value;
        private int code;
        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public static OrderStatusEnum codeof(int code){
            for (OrderStatusEnum orderStatusEnum:values()){
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new emmoeceException(expectionEnum.NO_ENUM);
        }
    }
}
