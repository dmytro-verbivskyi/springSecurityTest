package com.dverbivskyi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping
    public String get() {
        return "root GET: " + System.currentTimeMillis();
    }

    @GetMapping("css/backdoor")
    public String cssBackdoor() {
        //  "/css/**", "/js/**", "/images/**", "/webjars/**", "/**/favicon.ico", "/error"
        return "If url matches any of these patterns, it's not secured with basic configuration";
    }
}
