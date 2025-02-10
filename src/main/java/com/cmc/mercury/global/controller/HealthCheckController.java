package com.cmc.mercury.global.controller;

import com.cmc.mercury.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/api")
public class HealthCheckController {

    @GetMapping("/health")
    public SuccessResponse<String> healthCheck() {
        return SuccessResponse.ok("Yeah! Healthy");
    }
}

