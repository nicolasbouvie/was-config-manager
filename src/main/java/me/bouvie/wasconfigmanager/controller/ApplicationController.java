package me.bouvie.wasconfigmanager.controller;

import me.bouvie.wasconfigmanager.service.ApplicationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApplicationController {

    private ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @RequestMapping("/apps")
    public String listApps(Model model) throws Exception {
        model.addAttribute("apps", service.listApps());
        return "was/apps";
    }
}
