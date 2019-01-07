package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import me.bouvie.wasconfigmanager.setup.SharedLibInfo;
import org.springframework.stereotype.Service;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;

@Service
public class SharedLibSetupService implements ComponentSetupService<SharedLibInfo> {

    private ReferenceService refService;

    public SharedLibSetupService(ReferenceService refService) {
        this.refService = refService;
    }

    @Override
    public ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, SharedLibInfo libInfo) throws Exception {
        ObjectName scope = refService.getDefaultCellRef(config, session);
        ObjectName[] workManager = config.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "Library", libInfo.getName()), null);
        if (workManager != null && workManager.length >= 1) {
            return workManager[0];
        }

        AttributeList attrs = new AttributeList();
        attrs.add(new Attribute("name", libInfo.getName()));
        attrs.add(new Attribute("description", libInfo.getDescription()));
        attrs.add(new Attribute("nativePath", libInfo.getNativePath()));
        attrs.add(new Attribute("classPath", libInfo.getClasspath()));
        attrs.add(new Attribute("isolatedClassLoader", libInfo.isIsolatedClassLoader()));

        return config.createConfigData(session, scope, "Library", "Library", attrs);
    }
}
