package me.bouvie.wasconfigmanager.setup;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class JMSProviderInfo extends AbstractSetupInfo {
    private List<String> classpath;
    private String externalInitialContextFactory;
    private String externalProviderURL;
    private String username;
    private String password;
}
