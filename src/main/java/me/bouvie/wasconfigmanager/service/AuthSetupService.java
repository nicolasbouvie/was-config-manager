package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import me.bouvie.wasconfigmanager.setup.AuthInfo;
import org.springframework.stereotype.Service;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;

@Service
public class AuthSetupService implements ComponentSetupService<AuthInfo> {

    private ReferenceService refService;

    public AuthSetupService(ReferenceService refService) {
        this.refService = refService;
    }

    @Override
    public ObjectName getOrCreateConfig(ConfigServiceProxy configServiceProxy, Session session, AuthInfo configuration) throws Exception {
        ObjectName scope = refService.getDefaultSecurityRef(configServiceProxy, session);
        ObjectName[] jaasAuthData = configServiceProxy.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "JAASAuthData", null), null);
        if (jaasAuthData != null && jaasAuthData.length >= 1) {
            for (ObjectName auth : jaasAuthData) {
                if (configuration.getName().equals(configServiceProxy.getAttribute(session, auth, "alias"))) {
                    return auth;
                }
            }
        }

        AttributeList authAliasAttrs = new AttributeList();
        authAliasAttrs.add(new Attribute("alias", configuration.getName()));
        authAliasAttrs.add(new Attribute("userId", configuration.getUsername()));
        authAliasAttrs.add(new Attribute("password", configuration.getPassword()));
        authAliasAttrs.add(new Attribute("description", configuration.getDescription()));

        return configServiceProxy.createConfigData(session, scope, "authDataEntries", "JAASAuthData", authAliasAttrs);
    }
}
