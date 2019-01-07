package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import me.bouvie.wasconfigmanager.setup.AbstractSetupInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SetupService {

    private ReferenceService refService;
    private Map<Class<AbstractSetupInfo>, ComponentSetupService> setupServiceMap;

    public SetupService(ReferenceService webSphereService, Collection<ComponentSetupService> setupServices) {
        this.refService = webSphereService;
        this.setupServiceMap = setupServices.stream().collect(Collectors.toMap(ComponentSetupService::getComponentType, Function.identity()));
    }

    public void configure(Collection<AbstractSetupInfo> configuration, boolean dry) throws Exception {
        AdminClient client = refService.getClient();
        ConfigServiceProxy configService = new ConfigServiceProxy(client);
        Session session = new Session("was-config-manager", false);

        for (AbstractSetupInfo configObj : configuration) {
            setupServiceMap.get(configObj.getClass()).getOrCreateConfig(configService, session, configObj);
        }

        if (dry) {
            configService.discard(session);
        } else {
            configService.save(session, false);
        }
    }
}
