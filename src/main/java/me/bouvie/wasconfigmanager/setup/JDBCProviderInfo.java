package me.bouvie.wasconfigmanager.setup;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JDBCProviderInfo extends AbstractSetupInfo {
    private String name;
    private String implementationClassName;
    private List<String> classpath;
}
