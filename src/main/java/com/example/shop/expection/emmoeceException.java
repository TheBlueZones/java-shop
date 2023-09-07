package com.example.shop.expection;

/**
 * unified expection
 */
public class emmoeceException extends RuntimeException {/*更好的处理异常*/

    private final Integer code;
    private final String message;


    public String getMessage() {
        return message;
    }

    public emmoeceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public emmoeceException(expectionEnum expectionEnum){
        this(expectionEnum.getCode(),expectionEnum.getMsg());
    }

    public Integer getCode() {
        return code;
    }
}
