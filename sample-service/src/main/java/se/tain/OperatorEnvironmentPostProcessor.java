package se.tain;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * Created by ruel on 9/27/16.
 */
public class OperatorEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private Logger log = org.slf4j.LoggerFactory.getLogger(OperatorEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        // check if no additional checks needed in case if we work with ConfigServer
        EnumerablePropertySource applicationConfigurationProperties = (EnumerablePropertySource) propertySources.get("applicationConfigurationProperties");
        propertySources.replace("applicationConfigurationProperties", new EnumerablePropertySource<Collection<PropertySource<?>>> ("operatorSpecificApplicationConfigurationProperties") {
            @Override
            public Object getProperty(String name) {
                String op = OperatorContext.getCurrentOperator();
                String opScopedName = null;
                if (StringUtils.hasText(op)) {
                    opScopedName = format("%s.%s", op, name);
                }
                return opScopedName != null && applicationConfigurationProperties.containsProperty( opScopedName ) ? applicationConfigurationProperties.getProperty(opScopedName) : applicationConfigurationProperties.getProperty(name);
            }

            @Override
            public String[] getPropertyNames() {
                String op = OperatorContext.getCurrentOperator();
                String[] propertyNames = applicationConfigurationProperties.getPropertyNames();
                if(StringUtils.hasText(op)){
                    Stream<String> stream = Arrays.stream(propertyNames);
                    List<String> propNamesFiltered = stream.map(s -> s.replace(format("%s.", op), "")).collect(Collectors.toList());
                    propertyNames = propNamesFiltered.toArray(new String[propNamesFiltered.size()]);
                }
                return propertyNames;
            }
        });

        log.debug("Post processor called");
    }
}
