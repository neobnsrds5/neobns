package com.spider.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spider.demo.model.FormData;
import com.spider.demo.service.RecaptchaService;

@Controller
public class FormController {
    private final RecaptchaService recaptchaService;

    public FormController(RecaptchaService recaptchaService) {
        this.recaptchaService = recaptchaService;
    }

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("formData", new FormData());
        return "index";
    }

    @PostMapping("/submit")
    public String submitForm(
            @ModelAttribute("formData") FormData formData,
            @RequestParam(name = "g-recaptcha-response", required = false) String recaptchaResponse,
            Model model
    ) {
        try {
            if (recaptchaResponse == null || recaptchaResponse.isEmpty()) {
                model.addAttribute("error", "Please verify that you are not a robot");
                return "index";
            }

            if (!recaptchaService.verifyRecaptcha(recaptchaResponse)) {
                model.addAttribute("error", "reCAPTCHA verification failed");
                model.addAttribute("formData", formData);
                return "index";
            }

            // Process the form data here
            model.addAttribute("success", true);
            model.addAttribute("formData", new FormData());
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred. Please try again.");
            model.addAttribute("formData", formData);
            return "index";
        }
    }
}