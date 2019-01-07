package me.bouvie.wasconfigmanager.setup;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, of = {"jndi"})
public class DatasourceInfo extends AbstractSetupInfo {
    private String jndi;
    private String description;
    private JDBCProviderInfo jdbcProviderInfo;
    private AuthInfo authInfo;
    private String datasourceHelperClassname;
    private Integer statementCacheSize;
    private String url;
}
