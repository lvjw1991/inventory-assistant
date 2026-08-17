package com.example.recover.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class SpaWebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requested = location.createRelative(resourcePath);
                        // 请求的是真实存在的文件（js/css/图片等），直接返回，走原来的逻辑
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        // 接口请求 404 应该保持 404，不能也回退成 index.html，不然会掩盖真实的接口报错
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }
                        // 其他情况（前端路由路径）统一回退到 index.html，交给 Vue Router 接管
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
