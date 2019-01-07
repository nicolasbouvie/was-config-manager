package me.bouvie.wasconfigmanager.setup;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class JDBCProviderInfo extends AbstractSetupInfo {
    private String implementationClassName;
    private List<String> classpath;
}
