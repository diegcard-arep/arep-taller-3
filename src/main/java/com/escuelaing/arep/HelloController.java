package com.escuelaing.arep;

import com.escuelaing.arep.annotations.RestController;
import com.escuelaing.arep.annotations.GetMapping;

@RestController
public class HelloController {
    @GetMapping("/hola")
    public String index() {
        return "Greetings from MicroSpringBoot!";
    }
}
