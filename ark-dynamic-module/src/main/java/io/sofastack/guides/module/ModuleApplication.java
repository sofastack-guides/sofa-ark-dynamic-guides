package io.sofastack.guides.module;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author: guolei.sgl (glmapper_2018@163.com) 2020/6/13 2:27 PM
 * @since:
 **/
@SpringBootApplication
public class ModuleApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ModuleApplication.class).web(WebApplicationType.NONE);
        builder.build().run(args);
    }
}
