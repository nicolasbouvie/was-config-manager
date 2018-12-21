package me.bouvie.wasconfigmanager.setup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JMSConnectionFactoryInfo extends AbstractSetupInfo {
    private String name;
    private boolean xa;
    private AuthInfo authInfo;
    private String externalJndi;
    private String jndi;
    private String type;
    private JMSProviderInfo providerInfo;
    private JMSPoolInfo poolInfo;
}
