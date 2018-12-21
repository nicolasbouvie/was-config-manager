package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import me.bouvie.wasconfigmanager.setup.AbstractSetupInfo;
import me.bouvie.wasconfigmanager.setup.AuthInfo;
import me.bouvie.wasconfigmanager.setup.DatasourceInfo;
import me.bouvie.wasconfigmanager.setup.JDBCProviderInfo;
import me.bouvie.wasconfigmanager.setup.JMSConnectionFactoryInfo;
import me.bouvie.wasconfigmanager.setup.JMSProviderInfo;
import me.bouvie.wasconfigmanager.setup.JMSQueueInfo;
import me.bouvie.wasconfigmanager.setup.ListenerPortInfo;
import me.bouvie.wasconfigmanager.setup.SharedLibInfo;
import me.bouvie.wasconfigmanager.setup.WorkManagerInfo;
import org.springframework.stereotype.Service;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;
import java.util.Collection;

@Service
public class SetupService {

    private ReferenceService refService;

    public SetupService(ReferenceService webSphereService) {
        this.refService = webSphereService;
    }

    public void configure(Collection<AbstractSetupInfo> configuration, boolean dry) throws Exception {
        AdminClient client = refService.getClient();
        ConfigServiceProxy configService = new ConfigServiceProxy(client);
        Session session = new Session("was-config-manager", false);

        for (AbstractSetupInfo configObj : configuration) {
            getOrCreateConfig(configService, session, configObj);
        }

        if (dry) {
            configService.discard(session);
        } else {
            configService.save(session, false);
        }
    }

    private ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, JMSProviderInfo providerInfo) throws Exception {
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

    private ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, AuthInfo authInfo) throws Exception {
        ObjectName scope = refService.getDefaultSecurityRef(config, session);
        ObjectName[] jaasAuthData = config.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "JAASAuthData", null), null);
        if (jaasAuthData != null && jaasAuthData.length >= 1) {
            for (ObjectName auth : jaasAuthData) {
                if (authInfo.getAlias().equals(config.getAttribute(session, auth, "alias"))) {
                    return auth;
                }
            }
        }

        AttributeList authAliasAttrs = new AttributeList();
        authAliasAttrs.add(new Attribute("alias", authInfo.getAlias()));
        authAliasAttrs.add(new Attribute("userId", authInfo.getUsername()));
        authAliasAttrs.add(new Attribute("password", authInfo.getPassword()));
        authAliasAttrs.add(new Attribute("description", authInfo.getDescription()));

        return config.createConfigData(session, scope, "authDataEntries", "JAASAuthData", authAliasAttrs);
    }

    private ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, JDBCProviderInfo providerInfo) throws Exception {
        ObjectName scope = refService.getDefaultCellRef(config, session);
        ObjectName[] jdbcProvider = config.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "JDBCProvider", providerInfo.getName()), null);
        if (jdbcProvider != null && jdbcProvider.length >= 1) {
            return jdbcProvider[0];
        }

        AttributeList authAliasAttrs = new AttributeList();
        authAliasAttrs.add(new Attribute("name", providerInfo.getName()));
        authAliasAttrs.add(new Attribute("implementationClassName", providerInfo.getImplementationClassName()));
        authAliasAttrs.add(new Attribute("classpath", providerInfo.getClasspath()));

        return config.createConfigData(session, scope, "JDBCProvider", "JDBCProvider", authAliasAttrs);
    }

    private ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, DatasourceInfo datasourceInfo) throws Exception {
        ObjectName jdbcProvider = getOrCreateConfig(config, session, datasourceInfo.getJdbcProviderInfo());
        ObjectName[] datasources = config.queryConfigObjects(session, jdbcProvider, ConfigServiceHelper.createObjectName(null, "DataSource", datasourceInfo.getName()), null);
        if (datasources != null && datasources.length >= 1) {
            return datasources[0];
        }
        String authEntry = (String) config.getAttribute(session, getOrCreateConfig(config, session, datasourceInfo.getAuthInfo()), "alias");

        AttributeList authAliasAttrs = new AttributeList();
        authAliasAttrs.add(new Attribute("name", datasourceInfo.getName()));
        authAliasAttrs.add(new Attribute("jndiName", datasourceInfo.getJndi()));
        authAliasAttrs.add(new Attribute("authDataAlias", authEntry));
        authAliasAttrs.add(new Attribute("statementCacheSize", datasourceInfo.getStatementCacheSize()));
        authAliasAttrs.add(new Attribute("datasourceHelperClassname", datasourceInfo.getDatasourceHelperClassname()));

        ObjectName datasource = config.createConfigData(session, jdbcProvider, "DataSource", "DataSource", authAliasAttrs);

        AttributeList mappingAttrs = new AttributeList();
        mappingAttrs.add(new Attribute("authDataAlias", authEntry));
        mappingAttrs.add(new Attribute("mappingConfigAlias", "DefaultPrincipalMapping"));
        config.createConfigData(session, datasource, "mapping", "MappingModule", mappingAttrs);

        ObjectName propertySet = config.createConfigData(session, datasource, "propertySet", "J2EEResourcePropertySet", new AttributeList());

        AttributeList urlAttr = new AttributeList();
        urlAttr.add(new Attribute("name", "URL"));
        urlAttr.add(new Attribute("value", datasourceInfo.getUrl()));
        config.createConfigData(session, propertySet, "resourceProperties", "J2EEResourceProperty", urlAttr);

        return datasource;
    }

    private ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, JMSQueueInfo jmsQueueInfo) throws Exception {
        ObjectName scope = getOrCreateConfig(config, session, jmsQueueInfo.getProviderInfo());
        ObjectName[] queue = config.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "GenericJMSDestination", jmsQueueInfo.getName()), null);
        if (queue != null && queue.length >= 1) {
            return queue[0];
        }

        AttributeList attrs = new AttributeList();
        attrs.add(new Attribute("name", jmsQueueInfo.getName()));
        attrs.add(new Attribute("jndiName", jmsQueueInfo.getJndi()));
        attrs.add(new Attribute("externalJNDIName", jmsQueueInfo.getExternalJndi()));

        return config.createConfigData(session, scope, "GenericJMSDestination", "GenericJMSDestination", attrs);
    }

    private ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, JMSConnectionFactoryInfo connectionFactoryInfo) throws Exception {
        ObjectName scope = getOrCreateConfig(config, session, connectionFactoryInfo.getProviderInfo());
        ObjectName[] factory = config.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "GenericJMSConnectionFactory", connectionFactoryInfo.getName()), null);
        if (factory != null && factory.length >= 1) {
            return factory[0];
        }

        String authEntry = (String) config.getAttribute(session, getOrCreateConfig(config, session, connectionFactoryInfo.getAuthInfo()), "alias");

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

    private ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, WorkManagerInfo wmInfo) throws Exception {
        ObjectName scope = refService.getDefaultWorkManagerProviderRef(config, session);
        ObjectName[] workManager = config.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "WorkManagerInfo", wmInfo.getName()), null);
        if (workManager != null && workManager.length >= 1) {
            return workManager[0];
        }

        AttributeList attrs = new AttributeList();
        attrs.add(new Attribute("name", wmInfo.getName()));
        attrs.add(new Attribute("jndiName", wmInfo.getJndiName()));
        attrs.add(new Attribute("description", wmInfo.getDescription()));
        attrs.add(new Attribute("workTimeout", wmInfo.getWorkTimeout()));
        attrs.add(new Attribute("workReqQSize", wmInfo.getWorkReqQSize()));
        attrs.add(new Attribute("workReqQFullAction", wmInfo.getWorkReqQFullAction()));
        attrs.add(new Attribute("numAlarmThreads", wmInfo.getNumAlarmThreads()));
        attrs.add(new Attribute("minThreads", wmInfo.getMinThreads()));
        attrs.add(new Attribute("maxThreads", wmInfo.getMaxThreads()));
        attrs.add(new Attribute("threadPriority", wmInfo.getThreadPriority()));
        attrs.add(new Attribute("isGrowable", wmInfo.getGrowable()));

        return config.createConfigData(session, scope, "WorkManagerInfo", "WorkManagerInfo", attrs);
    }

    private ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, SharedLibInfo libInfo) throws Exception {
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

    private ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, ListenerPortInfo portInfo) throws Exception {
        ObjectName scope = refService.getDefaultMessageListenerServiceRef(config, session);
        ObjectName[] query = config.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "ListenerPort", portInfo.getName()), null);
        if (query != null && query.length >= 1) {
            return query[0];
        }

        String connectionFactory = (String) config.getAttribute(session, getOrCreateConfig(config, session, portInfo.getConnectionFactory()), "jndiName");
        String destinationJNDIName = (String) config.getAttribute(session, getOrCreateConfig(config, session, portInfo.getDestination()), "jndiName");

        AttributeList attrs = new AttributeList();
        attrs.add(new Attribute("name", portInfo.getName()));
        attrs.add(new Attribute("description", portInfo.getDescription()));
        attrs.add(new Attribute("connectionFactoryJNDIName", connectionFactory));
        attrs.add(new Attribute("destinationJNDIName", destinationJNDIName));
        attrs.add(new Attribute("maxSessions", portInfo.getMaxSessions()));
        attrs.add(new Attribute("maxRetries", portInfo.getMaxRetries()));
        attrs.add(new Attribute("maxMessages", portInfo.getMaxMessages()));

        ObjectName listenerPort = config.createConfigData(session, scope, "listenerPorts", "ListenerPort", attrs);

        AttributeList stateAttrs = new AttributeList();
        stateAttrs.add(new Attribute("initialState", portInfo.getInitialState()));
        config.createConfigData(session, listenerPort, "stateManagement", "StateManageable", stateAttrs);

        return listenerPort;
    }

    private ObjectName getOrCreateConfig(ConfigServiceProxy configService, Session session, AbstractSetupInfo configObject) throws Exception {
        if (configObject instanceof AuthInfo)                 return getOrCreateConfig(configService, session, (AuthInfo) configObject);
        if (configObject instanceof DatasourceInfo)           return getOrCreateConfig(configService, session, (DatasourceInfo) configObject);
        if (configObject instanceof JDBCProviderInfo)         return getOrCreateConfig(configService, session, (JDBCProviderInfo) configObject);
        if (configObject instanceof JMSConnectionFactoryInfo) return getOrCreateConfig(configService, session, (JMSConnectionFactoryInfo) configObject);
        if (configObject instanceof JMSProviderInfo)          return getOrCreateConfig(configService, session, (JMSProviderInfo) configObject);
        if (configObject instanceof JMSQueueInfo)             return getOrCreateConfig(configService, session, (JMSQueueInfo) configObject);
        if (configObject instanceof ListenerPortInfo)         return getOrCreateConfig(configService, session, (ListenerPortInfo) configObject);
        if (configObject instanceof SharedLibInfo)            return getOrCreateConfig(configService, session, (SharedLibInfo) configObject);
        if (configObject instanceof WorkManagerInfo)          return getOrCreateConfig(configService, session, (WorkManagerInfo) configObject);
        throw new IllegalArgumentException(String.format("Setup object of type %s not implemented", configObject.getClass()));
    }
}
