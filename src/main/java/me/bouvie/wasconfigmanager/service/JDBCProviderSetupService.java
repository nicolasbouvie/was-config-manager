package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import me.bouvie.wasconfigmanager.setup.JDBCProviderInfo;
import org.springframework.stereotype.Service;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;

@Service
public class JDBCProviderSetupService implements ComponentSetupService<JDBCProviderInfo> {
    private ReferenceService refService;

    public JDBCProviderSetupService(ReferenceService refService) {
        this.refService = refService;
    }

    @Override
    public ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, JDBCProviderInfo providerInfo) throws Exception {
        ObjectName scope = refService.getDefaultCellRef(config, session);
        ObjectName[] jdbcProvider = config.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "JDBCProvider", providerInfo.getName()), null);
        if (jdbcProvider != null && jdbcProvider.length >= 1) {
            return jdbcProvider[0];
        }

        AttributeList authAliasAttrs = new AttributeList();
        authAliasAttrs.add(new Attribute("name", providerInfo.getName()));
        authAliasAttrs.add(new Attribute("implementationClassName", providerInfo.getImplementationClassName()));
        authAliasAttrs.add(new Attribute("classpath", providerInfo.getClasspath()));
        authAliasAttrs.add(new Attribute("providerType", providerInfo.getType()));

        return config.createConfigData(session, scope, "JDBCProvider", "JDBCProvider", authAliasAttrs);
    }
}
