package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import me.bouvie.wasconfigmanager.setup.DatasourceInfo;
import org.springframework.stereotype.Service;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;

@Service
public class DatasourceSetupService implements ComponentSetupService<DatasourceInfo> {

    private JDBCProviderSetupService jdbcProviderSetupService;
    private AuthSetupService authSetupService;

    public DatasourceSetupService(JDBCProviderSetupService jdbcProviderSetupService, AuthSetupService authSetupService) {
        this.jdbcProviderSetupService = jdbcProviderSetupService;
        this.authSetupService = authSetupService;
    }

    @Override
    public ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, DatasourceInfo datasourceInfo) throws Exception {
        ObjectName jdbcProvider = jdbcProviderSetupService.getOrCreateConfig(config, session, datasourceInfo.getJdbcProviderInfo());
        ObjectName[] datasources = config.queryConfigObjects(session, jdbcProvider, ConfigServiceHelper.createObjectName(null, "DataSource", datasourceInfo.getName()), null);
        if (datasources != null && datasources.length >= 1) {
            return datasources[0];
        }
        String authEntry = (String) config.getAttribute(session, authSetupService.getOrCreateConfig(config, session, datasourceInfo.getAuthInfo()), "alias");

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
}
