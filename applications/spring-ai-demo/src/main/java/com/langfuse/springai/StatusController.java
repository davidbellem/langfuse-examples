package com.langfuse.springai;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/status")
public class StatusController {

    public StatusController() {

    }

    @GetMapping
    public String get() {
        return "OK";
    }
}
