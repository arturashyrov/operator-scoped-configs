package se.tain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SomeController {
    private final SomeConfig someConfig;

    @Autowired
    public SomeController( SomeConfig someConfig ) {
        this.someConfig = someConfig;
    }

    @GetMapping(path = "/config")
    public ExternalData getConfig() {
        ExternalData someData = new ExternalData( "Scoped config value was:" + someConfig.getFoo() );
        return someData;
    }
}
