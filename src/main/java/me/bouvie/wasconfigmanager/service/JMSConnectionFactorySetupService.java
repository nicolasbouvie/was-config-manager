package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import me.bouvie.wasconfigmanager.setup.JMSConnectionFactoryInfo;
import org.springframework.stereotype.Service;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;

@Service
public class JMSConnectionFactorySetupService implements ComponentSetupService<JMSConnectionFactoryInfo> {

    private JMSProviderSetupService jmsProviderSetupService;
    private AuthSetupService authSetupService;

    public JMSConnectionFactorySetupService(JMSProviderSetupService jmsProviderSetupService, AuthSetupService authSetupService) {
        this.jmsProviderSetupService = jmsProviderSetupService;
        this.authSetupService = authSetupService;
    }

    @Override
    public ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, JMSConnectionFactoryInfo connectionFactoryInfo) throws Exception {
        ObjectName scope = jmsProviderSetupService.getOrCreateConfig(config, session, connectionFactoryInfo.getProviderInfo());
        ObjectName[] factory = config.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "GenericJMSConnectionFactory", connectionFactoryInfo.getName()), null);
        if (factory != null && factory.length >= 1) {
            return factory[0];
        }

        String authEntry = (String) config.getAttribute(session, authSetupService.getOrCreateConfig(config, session, connectionFactoryInfo.getAuthInfo()), "alias");

        AttributeList attrs = new AttributeList();
        attrs.add(new Attribute("name", connectionFactoryInfo.getName()));
        attrs.add(new Attribute("jndiName", connectionFactoryInfo.getJndi()));
        attrs.add(new Attribute("externalJNDIName", connectionFactoryInfo.getExternalJndi()));
        attrs.add(new Attribute("XAEnabled", connectionFactoryInfo.isXa()));
        attrs.add(new Attribute("authDataAlias", authEntry));
        attrs.add(new Attribute("authMechanismPreference", "BASIC_PASSWORD"));
        attrs.add(new Attribute("type", connectionFactoryInfo.getType()));

        ObjectName connectionFactory = config.createConfigData(session, scope, "GenericJMSConnectionFactory", "GenericJMSConnectionFactory", attrs);

        AttributeList mappingAttrs = new AttributeList();
        mappingAttrs.add(new Attribute("authDataAlias", authEntry));
        mappingAttrs.add(new Attribute("mappingConfigAlias", "DefaultPrincipalMapping"));
        config.createConfigData(session, connectionFactory, "mapping", "MappingModule", mappingAttrs);

        AttributeList poolAttrs = new AttributeList();
        poolAttrs.add(new Attribute("connectionTimeout", connectionFactoryInfo.getPoolInfo().getConnectionTimeout()));
        poolAttrs.add(new Attribute("minConnections", connectionFactoryInfo.getPoolInfo().getMinConnections()));
        poolAttrs.add(new Attribute("maxConnections", connectionFactoryInfo.getPoolInfo().getMaxConnections()));
        poolAttrs.add(new Attribute("reapTime", connectionFactoryInfo.getPoolInfo().getReapTime()));
        poolAttrs.add(new Attribute("unusedTimeout", connectionFactoryInfo.getPoolInfo().getUnusedTimeout()));
        poolAttrs.add(new Attribute("agedTimeout", connectionFactoryInfo.getPoolInfo().getAgedTimeout()));
        poolAttrs.add(new Attribute("purgePolicy", connectionFactoryInfo.getPoolInfo().getPurgePolicy()));


        config.createConfigData(session, connectionFactory, "connectionPool", "ConnectionPool", poolAttrs);
        config.createConfigData(session, connectionFactory, "sessionPool", "ConnectionPool", poolAttrs);

        return connectionFactory;
    }
}
