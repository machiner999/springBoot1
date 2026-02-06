package com.example.goldapi.controller;

import com.example.goldapi.dto.GoldPriceResponse;
import com.example.goldapi.service.GoldPriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gold")
public class GoldPriceController {
    private final GoldPriceService service;

    public GoldPriceController(GoldPriceService service) {
        this.service = service;
    }

    @GetMapping("/price")
    public GoldPriceResponse getPrice() {
        return service.getCurrentPrice();
    }
}
