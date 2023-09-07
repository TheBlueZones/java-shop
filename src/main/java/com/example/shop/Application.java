package com.example.shop;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.Environment;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.naming.CannotProceedException;

@SpringBootApplication
@MapperScan(basePackages = "com.example.shop.model.dao")
@EnableSwagger2/*自动生成Api文档*/
@EnableCaching/* to turn on caching*/
/*make spring find the Mapper files */
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }
}
