package vn.tamnguyen.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.List;

//cấu hình bằng WebMvcConfigurer, có 2 cách viết
//cách 1:

//@Configuration
//public class AppConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowCredentials(true)
//                .allowedOrigins("http://localhost:8000/")
//                .allowedMethods("*")
//                .allowedHeaders("*");
//    }
//}

//cách 2:
//@Configuration
//public class AppConfig {
//
//    @Bean
//    public WebMvcConfigurer corsFilter() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                    .allowCredentials(true)
//                    .allowedOrigins("http://localhost:8000/")
//                    .allowedMethods("*")
//                    .allowedHeaders("*");
//            }
//        };
//    }
//}

//cấu hình bằng cors filter
//@Configuration
//public class AppConfig {
//    List<String> ALLOWED_METHODS = List.of("GET", "POST", "PUT", "DELETE");
//    List<String> ALLOWED_HEADERS = List.of("*");
//    List<String> ALLOWED_ORIGINS = List.of("http://localhost:8000/", "https://google.com");
//
//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
////        config.addAllowedOrigin("http://localhost:8000/");
////        config.addAllowedMethod("*");
////        config.addAllowedHeader("*");
//        config.setAllowedOrigins(ALLOWED_ORIGINS);
//        config.setAllowedMethods(ALLOWED_METHODS);
//        config.setAllowedHeaders(ALLOWED_HEADERS);
//        source.registerCorsConfiguration("/**", config);
//        FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter(source));
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return bean;
//    }
//}

// cấu hình bằng OncePerRequestFilter
@Component
public class AppConfig extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //lỗi http://localhost:8000/
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8000/");
        // bỏ đi xược cuối
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8000");
        filterChain.doFilter(request, response);
    }
}
