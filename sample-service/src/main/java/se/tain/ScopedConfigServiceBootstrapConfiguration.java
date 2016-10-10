package se.tain;

import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

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
                CompositePropertySource cloudPropertySource = ( CompositePropertySource ) super.locate( environment );

                String[] originalPropertyNames = cloudPropertySource.getPropertyNames();
                Map<String, Object> originalProperties = new HashMap<>();

                for (String originalPropertyName : originalPropertyNames) {
                    if (!originalProperties.containsKey(originalPropertyName)) {
                        originalProperties.put(originalPropertyName, cloudPropertySource.getProperty(originalPropertyName));
                    }
                }

                cloudPropertySource.addFirstPropertySource(new EnumerablePropertySource<Object>("scoped") {
                    @Override
                    public String[] getPropertyNames() {
                        String op = OperatorContext.getCurrentOperator();
                        String[] propertyNames = originalPropertyNames;
                        if(StringUtils.hasText(op)){
                            Stream<String> stream = Arrays.stream(propertyNames);
                            List<String> propNamesFiltered = stream.map(s -> s.replace(format("%s.", op), "")).collect(Collectors.toList());
                            propertyNames = propNamesFiltered.toArray(new String[propNamesFiltered.size()]);
                        }
                        return propertyNames;
                    }

                    @Override
                    public Object getProperty(String name) {
                        String op = OperatorContext.getCurrentOperator();
                        String opScopedName = null;
                        if (StringUtils.hasText(op)) {
                            opScopedName = format("%s.%s", op, name);
                        }
                        return opScopedName != null && originalProperties.containsKey( opScopedName ) ? originalProperties.get(opScopedName) : originalProperties.get(name);
                    }
                });

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
