package io.sofastack.dynamic.provider;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 * dynamic-provider 启动类
 *
 * @author caojie.cj@antfin.com
 * @since 2020/2/11
 */
@SpringBootApplication
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ProviderApplication.class);
        ResourceLoader resourceLoader = new DefaultResourceLoader(SpringApplicationBuilder.class.getClassLoader());
        builder.resourceLoader(resourceLoader);
        builder.build().run(args);
    }
}
