package me.bouvie.wasconfigmanager.service;

import com.google.common.collect.Lists;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementProxy;
import me.bouvie.wasconfigmanager.dto.ApplicationInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Properties;

@Service
public class WebSphereService {

    public Collection<ApplicationInfo> listApps() throws Exception {
        Collection<ApplicationInfo> apps = Lists.newArrayList();
        AppManagement appManagement = AppManagementProxy.getJMXProxyForClient(getClient());
        for (Object name : appManagement.listApplications(null, null)) {
            ApplicationInfo app = new ApplicationInfo();
            app.setName((String) name);
            apps.add(app);
        }
        return apps;
    }

    private AdminClient getClient() throws Exception {
        Properties props = new Properties();
        props.setProperty(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
        props.setProperty(AdminClient.CONNECTOR_HOST, "localhost");
        props.setProperty(AdminClient.CONNECTOR_PORT, "8880");
        props.setProperty(AdminClient.CONNECTOR_SECURITY_ENABLED, "true");
        props.setProperty(AdminClient.USERNAME, "wsadmin");
        props.setProperty(AdminClient.PASSWORD, "0XZaJvDj");

        return AdminClientFactory.createAdminClient(props);
    }
}
