package se.tain;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;

import static java.lang.String.format;

/**
 * This post processor should be used for local configs
 * @see ScopedConfigServiceBootstrapConfiguration
 * @see ScopedEnumerablePropertySourceWrapper
 */
public class ScopedEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        wrapApplicationConfigurationProperties( propertySources );
    }

    private void wrapApplicationConfigurationProperties( MutablePropertySources propertySources ) {
        if ( !propertySources.contains( "applicationConfigurationProperties" ) ) {
            return;
        }

        EnumerablePropertySource applicationConfigurationProperties = (EnumerablePropertySource) propertySources.get("applicationConfigurationProperties");
        propertySources.replace("applicationConfigurationProperties", new ScopedEnumerablePropertySourceWrapper<>(applicationConfigurationProperties));
    }
}
