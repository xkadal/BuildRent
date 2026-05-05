package com.vlad.buildrent.web.admin;

import com.vlad.buildrent.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final ReportingService reportingService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("summary", reportingService.summary());
        return "admin/dashboard";
    }
}
