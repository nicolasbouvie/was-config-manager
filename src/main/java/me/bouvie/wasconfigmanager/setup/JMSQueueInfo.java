package me.bouvie.wasconfigmanager.setup;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, of = {"jndi"})
public class JMSQueueInfo extends AbstractSetupInfo {
    private String jndi;
    private String externalJndi;
    private JMSProviderInfo providerInfo;
}
