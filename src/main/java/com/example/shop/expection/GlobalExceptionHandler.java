package com.example.shop.expection;

import com.example.shop.commom.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice/*拦截这些异常*/
public class GlobalExceptionHandler {
    /*打上日志*/
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)/*规定处理那些异常*/
    @ResponseBody
    public Object handleException(Exception e) {
        log.error("Default Exception:", e);
        return ApiRestResponse.error(expectionEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(emmoeceException.class)/*规定处理那些异常*/
    @ResponseBody
    public ApiRestResponse handleemmoeceException(emmoeceException e) {
        log.error("emmoeceException Exception: ", e);
        return ApiRestResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiRestResponse handleMethodArgumentNotValidException
            (MethodArgumentNotValidException e) {
        log.error("MethondArgumentNotValidException:", e);
        return handleBindingResult(e.getBindingResult());
    }

    private ApiRestResponse handleBindingResult(BindingResult result) {
        /*treat exception as exposed hints*/
        List<String> list = new ArrayList<>();
        if(result.hasErrors()){
            List<ObjectError> allErrors = result.getAllErrors();
        /*    for (int i = 0; i < allErrors.size(); i++){
                ObjectError objectError=allErrors.get(i);
            }*/
            for (ObjectError objectError:allErrors){/*allErrors是一个有内容的数组*/
                String message=objectError.getDefaultMessage();
                list.add(message);
            }
        }
        if (list.size()==0) {
            return ApiRestResponse.error(expectionEnum.REQYEST_PARAM_ERROR);
        }
        return ApiRestResponse.error(expectionEnum.REQYEST_PARAM_ERROR.getCode(),list.toString());
    }

}
