package se.tain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import se.tain.autoconfigure.ScopeContext;
import se.tain.autoconfigure.RuntimeConfigScope;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Map;

@SpringBootApplication
public class SampleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run( SampleServiceApplication.class, args);
    }

    @Bean
    public RuntimeConfigScope runtimeConfigScope() {
        return new RuntimeConfigScope() {
            @Override
            public String lookup() {
                return ScopeContext.get();
            }
        };
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(operatorFilter());
        filterRegistrationBean.addUrlPatterns("/api/*");
        filterRegistrationBean.setName("operatorFilter");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }

    @Bean(name = "operatorFilter")
    public Filter operatorFilter() {
        return new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
            }

            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                Map<String, String[]> parameterMap = servletRequest.getParameterMap();
                String[] operators = parameterMap.get("operator");
                if (operators == null) {
                    filterChain.doFilter(servletRequest, servletResponse);
                }

                try {
                    String operator = operators[0];
                    ScopeContext.set(operator);
                    filterChain.doFilter(servletRequest, servletResponse);
                } catch (Exception e) {
                    ScopeContext.clear();
                }
            }

            @Override
            public void destroy() {

            }
        };
    }
}
