package com.example.workmate.controller.community;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommuExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(
            IllegalStateException e,
            Model model
    ) {
        model.addAttribute("errorMessage", e.getMessage());
        return "community/commu-error-page";
    }
}