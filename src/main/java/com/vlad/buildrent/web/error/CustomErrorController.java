package com.vlad.buildrent.web.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int code = status != null ? Integer.parseInt(status.toString()) : 500;
        model.addAttribute("status", code);
        if (code == HttpStatus.NOT_FOUND.value()) return "error/404";
        if (code == HttpStatus.FORBIDDEN.value()) return "error/access-denied";
        return "error/500";
    }

    @GetMapping("/error/access-denied")
    public String accessDenied() { return "error/access-denied"; }
}
