package me.bouvie.wasconfigmanager.setup;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JMSProviderInfo extends AbstractSetupInfo {
    private String name;
    private List<String> classpath;
    private String externalInitialContextFactory;
    private String externalProviderURL;
    private String username;
    private String password;
}
