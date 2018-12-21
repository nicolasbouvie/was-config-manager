package me.bouvie.wasconfigmanager.controller;

import me.bouvie.wasconfigmanager.service.SetupService;
import me.bouvie.wasconfigmanager.setup.AbstractSetupInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
public class ConfigurationController {

    private SetupService setupService;

    public ConfigurationController(SetupService setupService) {
        this.setupService = setupService;
    }

    @RequestMapping(value="/configure", method = RequestMethod.PUT)
    @ResponseBody
    public String configure(@RequestBody Collection<AbstractSetupInfo> configuration,
                            @RequestParam(defaultValue="false") boolean dry) throws Exception {
        setupService.configure(configuration, dry);
        return "success";
    }
}
