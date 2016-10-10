package se.tain;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public class ScopedEnumerablePropertySourceWrapper<T> extends EnumerablePropertySource<T> {
    private EnumerablePropertySource<T> delegate;

    public ScopedEnumerablePropertySourceWrapper(EnumerablePropertySource<T> delegate) {
        super("scoped." + delegate.getName());
        this.delegate = delegate;
    }

    @Override
    public String[] getPropertyNames() {
        String op = OperatorContext.getCurrentOperator();
        String[] propertyNames = delegate.getPropertyNames();
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
        return opScopedName != null && delegate.containsProperty( opScopedName ) ? delegate.getProperty(opScopedName) : delegate.getProperty(name);

    }}
