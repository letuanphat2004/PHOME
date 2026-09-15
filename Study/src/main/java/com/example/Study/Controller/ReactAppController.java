package com.example.Study.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactAppController {
    @GetMapping({"/", "/login", "/register", "/forgot-password", "/account",
            "/appointments", "/favorites", "/my-rooms", "/admin", "/rooms/{id}"})
    public String reactApp() {
        return "forward:/index.html";
    }
}
