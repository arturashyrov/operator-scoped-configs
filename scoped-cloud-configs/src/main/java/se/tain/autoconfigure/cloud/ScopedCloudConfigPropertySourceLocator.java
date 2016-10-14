package se.tain.autoconfigure.cloud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import se.tain.autoconfigure.ScopeContext;
import se.tain.autoconfigure.RuntimeConfigScope;
import se.tain.autoconfigure.ScopedEnumerablePropertySourceWrapper;

import java.util.Collection;
import java.util.LinkedList;

@Configuration
@ConditionalOnExpression(value = "!${spring.cloud.config.enabled} && ${spring.cloud.config.scoped}")
@ConditionalOnClass(value = ConfigServicePropertySourceLocator.class)
public class ScopedCloudConfigPropertySourceLocator implements EnvironmentAware {
    private Environment environment;

    @Bean
    public RuntimeConfigScope configScope() {
        return new RuntimeConfigScope() {
            @Override
            public String lookup() {
                return ScopeContext.get();
            }
        };
    }

    @Bean
    public ConfigClientProperties configClientProperties() {
        ConfigClientProperties client = new ConfigClientProperties(this.environment);
        client.setEnabled(false);
        return client;
    }

    @Bean
    public ConfigServicePropertySourceLocator configServicePropertySourceLocator() {
        ConfigClientProperties clientProperties = configClientProperties();

        ConfigServicePropertySourceLocator configServicePropertySourceLocator = new ConfigServicePropertySourceLocator(clientProperties) {
            @Override
            public PropertySource<?> locate(Environment environment) {
                CompositePropertySource cloudPropertySource = (CompositePropertySource) super.locate(environment);
                if(cloudPropertySource == null) return null;

                // get a copy of cloud config property sources first
                Collection<PropertySource<?>> propertySourcesCopy = new LinkedList<>();
                propertySourcesCopy.addAll(cloudPropertySource.getPropertySources());

                // now wrap them with some context
                cloudPropertySource.getPropertySources().clear();

                propertySourcesCopy.stream()
                        .forEach(
                                ps -> cloudPropertySource.addPropertySource(
                                        new ScopedEnumerablePropertySourceWrapper(configScope(), (EnumerablePropertySource) ps)
                                )
                        );

                return cloudPropertySource;
            }
        };
        return configServicePropertySourceLocator;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
