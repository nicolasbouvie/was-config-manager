package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import me.bouvie.wasconfigmanager.setup.JMSQueueInfo;
import org.springframework.stereotype.Service;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;

@Service
public class JMSQueueSetupService implements ComponentSetupService<JMSQueueInfo> {

    private JMSProviderSetupService jmsProviderSetupService;

    public JMSQueueSetupService(JMSProviderSetupService jmsProviderSetupService) {
        this.jmsProviderSetupService = jmsProviderSetupService;
    }

    @Override
    public ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, JMSQueueInfo jmsQueueInfo) throws Exception {
        ObjectName scope = jmsProviderSetupService.getOrCreateConfig(config, session, jmsQueueInfo.getProviderInfo());
        ObjectName[] queue = config.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "GenericJMSDestination", jmsQueueInfo.getName()), null);
        if (queue != null && queue.length >= 1) {
            return queue[0];
        }

        AttributeList attrs = new AttributeList();
        attrs.add(new Attribute("name", jmsQueueInfo.getName()));
        attrs.add(new Attribute("jndiName", jmsQueueInfo.getJndi()));
        attrs.add(new Attribute("externalJNDIName", jmsQueueInfo.getExternalJndi()));

        return config.createConfigData(session, scope, "GenericJMSDestination", "GenericJMSDestination", attrs);
    }
}
