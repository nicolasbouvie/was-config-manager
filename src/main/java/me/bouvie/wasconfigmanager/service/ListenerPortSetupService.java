package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import me.bouvie.wasconfigmanager.setup.ListenerPortInfo;
import org.springframework.stereotype.Service;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;

@Service
public class ListenerPortSetupService implements ComponentSetupService<ListenerPortInfo> {

    private ReferenceService refService;
    private JMSConnectionFactorySetupService jmsConnectionFactorySetupService;
    private JMSQueueSetupService jmsQueueSetupService;

    public ListenerPortSetupService(ReferenceService refService, JMSConnectionFactorySetupService jmsConnectionFactorySetupService, JMSQueueSetupService jmsQueueSetupService) {
        this.refService = refService;
        this.jmsConnectionFactorySetupService = jmsConnectionFactorySetupService;
        this.jmsQueueSetupService = jmsQueueSetupService;
    }

    @Override
    public ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, ListenerPortInfo portInfo) throws Exception {
        ObjectName scope = refService.getDefaultMessageListenerServiceRef(config, session);
        ObjectName[] query = config.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "ListenerPort", portInfo.getName()), null);
        if (query != null && query.length >= 1) {
            return query[0];
        }

        String connectionFactory = (String) config.getAttribute(session, jmsConnectionFactorySetupService.getOrCreateConfig(config, session, portInfo.getConnectionFactory()), "jndiName");
        String destinationJNDIName = (String) config.getAttribute(session, jmsQueueSetupService.getOrCreateConfig(config, session, portInfo.getDestination()), "jndiName");

        AttributeList attrs = new AttributeList();
        attrs.add(new Attribute("name", portInfo.getName()));
        attrs.add(new Attribute("description", portInfo.getDescription()));
        attrs.add(new Attribute("connectionFactoryJNDIName", connectionFactory));
        attrs.add(new Attribute("destinationJNDIName", destinationJNDIName));
        attrs.add(new Attribute("maxSessions", portInfo.getMaxSessions()));
        attrs.add(new Attribute("maxRetries", portInfo.getMaxRetries()));
        attrs.add(new Attribute("maxMessages", portInfo.getMaxMessages()));

        ObjectName listenerPort = config.createConfigData(session, scope, "listenerPorts", "ListenerPort", attrs);

        AttributeList stateAttrs = new AttributeList();
        stateAttrs.add(new Attribute("initialState", portInfo.getInitialState()));
        config.createConfigData(session, listenerPort, "stateManagement", "StateManageable", stateAttrs);

        return listenerPort;
    }
}
