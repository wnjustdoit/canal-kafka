package com.caiya.ck.booter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 应用启动入口.
 *
 * @author wangnan
 * @since 1.0
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class
        , PersistenceExceptionTranslationAutoConfiguration.class})
@ComponentScan(value = "com.caiya")
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


}
