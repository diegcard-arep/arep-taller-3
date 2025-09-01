package com.escuelaing.arep.controllers;

import com.escuelaing.arep.annotations.GetMapping;
import com.escuelaing.arep.annotations.RestController;

@RestController
public class HelloController {
    @GetMapping("/hola")
    public String index() {
        return "Greetings from MicroSpringBoot!";
    }
}
