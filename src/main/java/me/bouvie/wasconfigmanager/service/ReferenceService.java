package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.ObjectNameHelper;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import org.springframework.stereotype.Service;

import javax.management.ObjectName;
import java.util.Properties;

@Service
public class ReferenceService {

    public AdminClient getClient() throws Exception {
        Properties props = new Properties();
        props.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
        props.setProperty(AdminClient.CONNECTOR_HOST, "localhost");
        props.setProperty(AdminClient.CONNECTOR_PORT, "8880");
        props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");
        props.setProperty(AdminClient.USERNAME, "wsadmin");
        props.setProperty(AdminClient.PASSWORD, "0XZaJvDj");

        return AdminClientFactory.createAdminClient(props);
    }

    public ObjectName getDefaultServerRef(ConfigServiceProxy config, Session session) throws Exception {
        return config.queryConfigObjects(session,
                null,
                ConfigServiceHelper.createObjectName(null, "Server", "server1"),
                null)[0];
    }

    public ObjectName getDefaultNodeRef(ConfigServiceProxy config, Session session) throws Exception {
        String nodeName = ObjectNameHelper.getNodeName(config.getAdminClient().getServerMBean());
        return config.queryConfigObjects(session,
                null,
                ConfigServiceHelper.createObjectName(null, "Node", nodeName),
                null)[0];
    }

    public ObjectName getDefaultCellRef(ConfigServiceProxy config, Session session) throws Exception {
        String cellName = ObjectNameHelper.getCellName(config.getAdminClient().getServerMBean());
        return config.queryConfigObjects(session,
                null,
                ConfigServiceHelper.createObjectName(null, "Cell", cellName),
                null)[0];
    }

    public ObjectName getDefaultSecurityRef(ConfigServiceProxy config, Session session) throws Exception {
        return config.queryConfigObjects(session,
                null,
                ConfigServiceHelper.createObjectName(null, "Security", null),
                null)[0];
    }

    public ObjectName getDefaultWorkManagerProviderRef(ConfigServiceProxy config, Session session) throws Exception {
        return config.queryConfigObjects(session,
                getDefaultCellRef(config, session),
                ConfigServiceHelper.createObjectName(null, "WorkManagerProvider", "WorkManagerProvider"),
                null)[0];
    }

    public ObjectName getDefaultMessageListenerServiceRef(ConfigServiceProxy config, Session session) throws Exception {
        return config.queryConfigObjects(session,
                getDefaultServerRef(config, session),
                ConfigServiceHelper.createObjectName(null, "MessageListenerService", null),
                null)[0];
    }
}
