package me.bouvie.wasconfigmanager.controller;

import me.bouvie.wasconfigmanager.repository.SetupRepository;
import me.bouvie.wasconfigmanager.service.SetupService;
import me.bouvie.wasconfigmanager.setup.AbstractSetupInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
public class ConfigurationController {

    private SetupService setupService;

    private SetupRepository setupRepository;

    public ConfigurationController(SetupService setupService, SetupRepository setupRepository) {
        this.setupService = setupService;
        this.setupRepository = setupRepository;
    }

    @RequestMapping(value="/configuration", method = RequestMethod.PUT)
    @ResponseBody
    public String configure(@RequestBody Collection<AbstractSetupInfo> configuration,
                            @RequestParam(defaultValue="false") boolean dry) throws Exception {
        setupService.configure(configuration, dry);
        return "success";
    }

    @RequestMapping(value="/configuration", method = RequestMethod.GET)
    public String loadConfiguration(@RequestParam String applicationName, Model model) throws Exception {
        model.addAttribute("namePattern", applicationName);
        model.addAttribute("configuration", setupRepository.jsonList());
        return "was/applicationSetup";
    }

}
