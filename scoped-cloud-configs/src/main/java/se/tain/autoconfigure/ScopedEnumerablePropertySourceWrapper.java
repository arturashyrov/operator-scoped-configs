package se.tain.autoconfigure;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public class ScopedEnumerablePropertySourceWrapper<T> extends EnumerablePropertySource<T> {
    private final RuntimeConfigScope configScope;
    private final EnumerablePropertySource<T> delegate;

    public ScopedEnumerablePropertySourceWrapper(RuntimeConfigScope configScope, EnumerablePropertySource<T> delegate) {
        super("scoped." + delegate.getName());
        this.configScope = configScope;
        this.delegate = delegate;
    }

    @Override
    public String[] getPropertyNames() {
        String scope = configScope.lookup();
        String[] propertyNames = delegate.getPropertyNames();
        if(StringUtils.hasText(scope)){
            Stream<String> stream = Arrays.stream(propertyNames);
            List<String> propNamesFiltered = stream.map(s -> s.replace(format("%s.", scope), "")).collect(Collectors.toList());
            propertyNames = propNamesFiltered.toArray(new String[propNamesFiltered.size()]);
        }
        return propertyNames;
    }

    @Override
    public Object getProperty(String name) {
        String scope = configScope.lookup();
        String opScopedName = null;
        if (StringUtils.hasText(scope)) {
            opScopedName = format("%s.%s", scope, name);
        }
        return opScopedName != null && delegate.containsProperty( opScopedName ) ? delegate.getProperty(opScopedName) : delegate.getProperty(name);

    }}
