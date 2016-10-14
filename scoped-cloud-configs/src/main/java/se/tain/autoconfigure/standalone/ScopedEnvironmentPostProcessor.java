package se.tain.autoconfigure.standalone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import se.tain.autoconfigure.RuntimeConfigScope;
import se.tain.autoconfigure.ScopedEnumerablePropertySourceWrapper;

/**
 * This post processor should be used for local configs
 * @see ScopedEnumerablePropertySourceWrapper
 */
public class ScopedEnvironmentPostProcessor implements EnvironmentPostProcessor {

    // TODO: this doesn't work. fix it
    @Autowired
    private RuntimeConfigScope configScope;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        if ("true".equals(environment.getProperty("spring.config.standalone.scoped"))) {
            wrapApplicationConfigurationProperties(propertySources);
        }
    }

    private void wrapApplicationConfigurationProperties( MutablePropertySources propertySources ) {
        if ( !propertySources.contains( "applicationConfigurationProperties" ) ) {
            return;
        }

        EnumerablePropertySource applicationConfigurationProperties = (EnumerablePropertySource) propertySources.get("applicationConfigurationProperties");
        propertySources.replace("applicationConfigurationProperties", new ScopedEnumerablePropertySourceWrapper<>(configScope, applicationConfigurationProperties));
    }
}
