package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import me.bouvie.wasconfigmanager.setup.JMSProviderInfo;
import org.springframework.stereotype.Service;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;

@Service
public class JMSProviderSetupService implements ComponentSetupService<JMSProviderInfo> {

    private ReferenceService refService;

    public JMSProviderSetupService(ReferenceService refService) {
        this.refService = refService;
    }

    public ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, JMSProviderInfo providerInfo) throws Exception {
        ObjectName scope = refService.getDefaultCellRef(config, session);
        ObjectName[] providers = config.queryConfigObjects(session,
                scope,
                ConfigServiceHelper.createObjectName(null, "JMSProvider", providerInfo.getName()),
                null);
        if (providers != null && providers.length >= 1) {
            return providers[0];
        }

        AttributeList attrs = new AttributeList();
        attrs.add(new Attribute("name", providerInfo.getName()));
        attrs.add(new Attribute("classpath", providerInfo.getClasspath()));
        attrs.add(new Attribute("externalInitialContextFactory", providerInfo.getExternalInitialContextFactory()));
        attrs.add(new Attribute("externalProviderURL", providerInfo.getExternalProviderURL()));

        ObjectName configData = config.createConfigData(session, scope, "JMSProvider", "JMSProvider", attrs);
        ObjectName propertySet = config.createConfigData(session, configData, "propertySet", "J2EEResourcePropertySet", new AttributeList());

        AttributeList userAttr = new AttributeList();
        userAttr.add(new Attribute("name", "java.naming.security.principal"));
        userAttr.add(new Attribute("value", providerInfo.getUsername()));
        userAttr.add(new Attribute("type", "java.lang.String"));
        config.createConfigData(session, propertySet, "resourceProperties", "J2EEResourceProperty", userAttr);

        AttributeList passAttr = new AttributeList();
        passAttr.add(new Attribute("name", "java.naming.security.credentials"));
        passAttr.add(new Attribute("value", providerInfo.getPassword()));
        passAttr.add(new Attribute("type", "java.lang.String"));
        return config.createConfigData(session, propertySet, "resourceProperties", "J2EEResourceProperty", passAttr);
    }
}
