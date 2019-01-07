package me.bouvie.wasconfigmanager.setup;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, of = {"jndi"})
public class JMSConnectionFactoryInfo extends AbstractSetupInfo {
    private boolean xa;
    private AuthInfo authInfo;
    private String externalJndi;
    private String jndi;
    private String type;
    private JMSProviderInfo providerInfo;
    private JMSPoolInfo poolInfo;
}
