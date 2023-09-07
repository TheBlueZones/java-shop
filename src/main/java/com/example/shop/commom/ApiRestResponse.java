package com.example.shop.commom;

import com.example.shop.expection.expectionEnum;
/**
 * 接口返回给前端的数据
 */

/**
 * description：common return object
 */
public class ApiRestResponse<T> {/*genericity*/
    private Integer status;

    private String msg;

    private T data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiRestResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }


    private static final int Ok_CODE = 10000;

    private static final String OK_MESSAGE = "SUCCESS";

    public ApiRestResponse() {
        this(Ok_CODE, OK_MESSAGE);
    }

    public ApiRestResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ApiRestResponse(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiRestResponse<T> success() {
        return new ApiRestResponse<>();
    }

    public static <T> ApiRestResponse<T> success(T result) {
        ApiRestResponse<T> response = new ApiRestResponse<>();
        /*会有默认构造函数给其他两个值赋值*/
        response.setData(result);
        return response;
    }
    public static <T> ApiRestResponse<T> success(Integer code, String msg) {
        return new ApiRestResponse<>(code, msg);
    }


    public static <T> ApiRestResponse<T> error(expectionEnum ex) {
        return new ApiRestResponse<>(ex.getCode(), ex.getMsg());
    }

    public static <T> ApiRestResponse<T> error(Integer code, String msg) {
        return new ApiRestResponse<>(code, msg);
    }


}
