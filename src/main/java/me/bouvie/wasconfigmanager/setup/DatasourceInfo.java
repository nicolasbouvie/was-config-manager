package me.bouvie.wasconfigmanager.setup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatasourceInfo extends AbstractSetupInfo {
    private String name;
    private String jndi;
    private String description;
    private JDBCProviderInfo jdbcProviderInfo;
    private AuthInfo authInfo;
    private String datasourceHelperClassname;
    private Integer statementCacheSize;
    private String url;
}
