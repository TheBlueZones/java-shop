package com.example.shop.cotroller;

import com.example.shop.commom.ApiRestResponse;
import com.example.shop.commom.Constant;
import com.example.shop.expection.emmoeceException;
import com.example.shop.expection.expectionEnum;
import com.example.shop.model.dao.ProductMapper;
import com.example.shop.model.pojo.Product;
import com.example.shop.model.request.AddProductReq;
import com.example.shop.model.request.UpdateProductReq;
import com.example.shop.server.ProductService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
public class AdminProductController {

    @Autowired
    ProductService productService;

    @ApiOperation("后台添加商品")
    @PostMapping("/admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq) {
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/upload/file")
    /*Save the image uploaded from the front end in the specified path of the server*/
    public ApiRestResponse upload(HttpServletRequest httpServletRequest,
                                  @RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        /*generate file name UUID*/
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;
        /*creat a file*/
        /*to judge  fileDirectory existenxe*/
        File fileDirectory = new File(Constant.FILE_UOLOAD_DIR);
        File destFile = new File(Constant.FILE_UOLOAD_DIR + newFileName);
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {
                throw new emmoeceException(expectionEnum.MKDIR_FAILED);
            }
        }
        try {
            /*write in folder*/
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*add " ",turn Stringbuff yo String*/
        try {
            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURI() + ""))
                    + "/images/" + newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(expectionEnum.UPDATE_FAILED);
        }
    }

    /*Break down ip addresses into useful information*/
    private URI getHost(URI uri) {
        URI effectiveUri;
        try {
            effectiveUri = new URI(uri.getScheme(),
                    uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (URISyntaxException e) {
            effectiveUri = null;
        }
        return effectiveUri;
    }
    @ApiOperation("后台更新商品")
    @PostMapping("/admin/product/update")
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除商品")
    @PostMapping("/admin/product/delete")
    public ApiRestResponse deleteProduct(@RequestParam Integer id) {
      productService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台批量上下架")
    @PostMapping("/admin/product/batchUpdateSellStatus")
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids,
                                                 @RequestParam Integer sellstatus) {
        productService.batchUpdateSellStatus(ids, sellstatus);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台商品列表")
    @PostMapping("/admin/product/BackstageProductList")
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer pageNum,
                                                 @RequestParam Integer pageSize) {
        PageInfo pageInfo =productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }


}
