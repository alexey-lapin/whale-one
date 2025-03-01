package com.github.alexeylapin.whaleone.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{spring:(?!api|assets|index\\.html).*}")
                .setViewName("forward:/index.html");
//        registry.addViewController("/static/").setViewName("forward:/new/index.html");
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**")
//                .addResourceLocations("classpath:/static/new/")
//                .resourceChain(true)
//                .addResolver(new VueHistoryModeResourceResolver());
//    }

    // Custom resolver to correctly serve assets and Vue history mode
//    private static class VueHistoryModeResourceResolver implements ResourceResolver {
//        private final Resource indexHtml = new ClassPathResource("/static/new/index.html");
//
//        @Override
//        public Resource resolveResource(HttpServletRequest request,
//                                        String requestPath,
//                                        List<? extends Resource> locations,
//                                        ResourceResolverChain chain) {
//            // First, try to find an actual static file
//            Resource resource = chain.resolveResource(request, requestPath, locations);
//            if (resource != null) {
//                return resource; // Serve the requested static file
//            }
//
//            // If the request is for an asset (CSS, JS, images, etc.), return null to trigger a 404
//            if (requestPath.matches(".+\\.(js|css|png|jpg|jpeg|gif|svg|woff2?|ttf|otf|eot|ico|json|map|txt)$")) {
//                return null; // This will result in a 404 Not Found
//            }
//
//            // Otherwise, return index.html for Vue history mode routing
//            return indexHtml;
//        }
//
//        @Override
//        public String resolveUrlPath(String resourcePath, List<? extends Resource> locations,
//                                     ResourceResolverChain chain) {
//            String url = chain.resolveUrlPath(resourcePath, locations);
//            return (url != null) ? url : "/new/index.html";
//        }
//    }

}
