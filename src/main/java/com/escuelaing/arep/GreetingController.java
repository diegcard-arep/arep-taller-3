package com.escuelaing.arep;

import com.escuelaing.arep.annotations.RestController;
import com.escuelaing.arep.annotations.GetMapping;
import com.escuelaing.arep.annotations.RequestParam;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
    
    @GetMapping("/count")
    public String count() {
        return "Count: " + counter.incrementAndGet();
    }
}
