package se.tain;

import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

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
    public ConfigServicePropertySourceLocator configServicePropertySourceLocator() {
        ConfigClientProperties clientProperties = configClientProperties();

        ConfigServicePropertySourceLocator configServicePropertySourceLocator =  new ConfigServicePropertySourceLocator(clientProperties) {
            @Override
            public PropertySource<?> locate( Environment environment ) {
                PropertySource<?> propertySource = super.locate( environment );
                /*
                CompositePropertySource compositePropertySource = new CompositePropertySource( "scopedCloudConfigsPropertySource" );
                compositePropertySource.addPropertySource( new EnumerablePropertySource<>() {
                } );
*/
                return propertySource;
            }
        };
        return configServicePropertySourceLocator;
    }

    @Override
    public void setEnvironment( Environment environment ) {
        this.environment = environment;
    }
}
