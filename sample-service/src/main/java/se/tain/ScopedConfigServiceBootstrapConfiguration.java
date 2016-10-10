package se.tain;

import org.springframework.boot.actuate.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.*;

import static java.lang.String.format;

/**
 * This configuration should kick-in when cloud configs are in place
 * @see ScopedEnvironmentPostProcessor
 * @see ScopedEnumerablePropertySourceWrapper
 */
@Configuration
// https://github.com/spring-cloud/spring-cloud-config/issues/177
public class ScopedConfigServiceBootstrapConfiguration implements EnvironmentAware {
    private Environment environment;

    @Bean
    public ConfigClientProperties configClientProperties() {
        ConfigClientProperties client = new ConfigClientProperties(this.environment);
        client.setEnabled(false);
        return client;
    }

    @Bean
    @ConditionalOnBean(ConfigServicePropertySourceLocator.class)
    public ConfigServicePropertySourceLocator configServicePropertySourceLocator() {
        ConfigClientProperties clientProperties = configClientProperties();

        ConfigServicePropertySourceLocator configServicePropertySourceLocator =  new ConfigServicePropertySourceLocator(clientProperties) {
            @Override
            public PropertySource<?> locate( Environment environment ) {
                CompositePropertySource cloudPropertySource = (CompositePropertySource) super.locate(environment);

                // get a copy of cloud config property sources first
                Collection<PropertySource<?>> propertySourcesCopy = new LinkedList<>();
                propertySourcesCopy.addAll(cloudPropertySource.getPropertySources());

                // now wrap them with some context
                cloudPropertySource.getPropertySources().clear();

                propertySourcesCopy.stream()
                        .forEach(
                                ps -> cloudPropertySource.addPropertySource(
                                        new ScopedEnumerablePropertySourceWrapper((EnumerablePropertySource) ps)
                                )
                        );

                return cloudPropertySource;
            }
        };
        return configServicePropertySourceLocator;
    }

    @Override
    public void setEnvironment( Environment environment ) {
        this.environment = environment;
    }
}
