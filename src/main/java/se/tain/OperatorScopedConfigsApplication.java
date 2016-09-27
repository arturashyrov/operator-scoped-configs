package se.tain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

import javax.servlet.*;
import java.io.IOException;
import java.util.Map;

@SpringBootApplication
public class OperatorScopedConfigsApplication implements EnvironmentAware {

    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(OperatorScopedConfigsApplication.class, args);
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
                    OperatorContext.setCurrentOperator(operator);
                    filterChain.doFilter(servletRequest, servletResponse);
                } catch (Exception e) {
                    OperatorContext.clear();
                }
            }

            @Override
            public void destroy() {

            }
        };
    }

    @Override
    public void setEnvironment(Environment environment) {
        // ConfigurationPropertySources
        this.environment = environment;
    }
}
