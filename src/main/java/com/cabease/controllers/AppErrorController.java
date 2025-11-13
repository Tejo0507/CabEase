package com.cabease.controllers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
@Controller
@Slf4j
public class AppErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Object ex = request.getAttribute("javax.servlet.error.exception");
        String message = null;
        if (ex instanceof Throwable) {
            message = ((Throwable) ex).getMessage();
        } else if (request.getAttribute("javax.servlet.error.message") != null) {
            message = request.getAttribute("javax.servlet.error.message").toString();
        }
        log.warn("Handling error, status={}, message={}", status, message);
        model.addAttribute("status", status == null ? "Unknown" : status.toString());
        model.addAttribute("message", message == null ? "An unexpected error occurred." : message);
        return "custom-error";
    }
}
