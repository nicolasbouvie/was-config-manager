package me.bouvie.wasconfigmanager.setup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JMSQueueInfo extends AbstractSetupInfo {
    private String name;
    private String jndi;
    private String externalJndi;
    private JMSProviderInfo providerInfo;
}
