package com.escuelaing.arep.framework;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.escuelaing.arep.annotations.GetMapping;
import com.escuelaing.arep.annotations.RequestParam;

class RouteInfoTest {

    static class DummyController {
        @GetMapping("/echo")
        public String echo(@RequestParam(value = "msg", defaultValue = "hi") String msg) {
            return "E:" + msg;
        }

        @GetMapping("/noparams")
        public String noParams() {
            return "OK";
        }
    }

    @Test
    @DisplayName("invoke should map @RequestParam with provided value")
    void invoke_should_injectProvidedQueryParam_when_present() throws Exception {
        // Arrange
        DummyController ctrl = new DummyController();
        Method m = DummyController.class.getDeclaredMethod("echo", String.class);
        RouteInfo ri = new RouteInfo("/echo", m, ctrl);
        Map<String, String> qp = new HashMap<>();
        qp.put("msg", "hola");

        // Act
        String result = ri.invoke(qp);

        // Assert
        assertEquals("E:hola", result);
    }

    @Test
    @DisplayName("invoke should use defaultValue when param missing")
    void invoke_should_useDefaultValue_when_paramMissing() throws Exception {
        // Arrange
        DummyController ctrl = new DummyController();
        Method m = DummyController.class.getDeclaredMethod("echo", String.class);
        RouteInfo ri = new RouteInfo("/echo", m, ctrl);

        // Act
        String result = ri.invoke(Map.of());

        // Assert
        assertEquals("E:hi", result);
    }

    @Test
    @DisplayName("invoke should call methods without params")
    void invoke_should_callMethodWithoutParams() throws Exception {
        // Arrange
        DummyController ctrl = new DummyController();
        Method m = DummyController.class.getDeclaredMethod("noParams");
        RouteInfo ri = new RouteInfo("/noparams", m, ctrl);

        // Act
        String result = ri.invoke(Map.of());

        // Assert
        assertEquals("OK", result);
    }
}