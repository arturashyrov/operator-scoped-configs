package se.tain;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "someConfig")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, scopeName = "prototype")
public class SomeConfig {
    private String foo;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

}
