package com.eduplan.eduplan.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "API EduPlan está corriendo correctamente 🚀";
    }
}