package org.k3yake;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by katsuki-miyake on 18/03/07.
 */
@RestController
public class Controller {

    @Autowired
    private Service service;

    @GetMapping("/service")
    public String get(){
        return service.execService();
    }
}
