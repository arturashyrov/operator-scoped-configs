package se.tain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ruel on 9/26/16.
 */
@RestController
@RequestMapping("/api")
public class SomeController {
    @Autowired
    private SomeConfig someConfig;

    @GetMapping(path = "/config")
    public ExternalData getConfig() {
        ExternalData someData = new ExternalData( "Scoped config value was:" + someConfig.getFoo() );
        return someData;
    }
}
