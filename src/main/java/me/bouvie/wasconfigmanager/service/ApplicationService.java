package me.bouvie.wasconfigmanager.service;

import com.google.common.collect.Lists;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementProxy;
import me.bouvie.wasconfigmanager.application.ApplicationInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ApplicationService {

    private ReferenceService refService;

    public ApplicationService(ReferenceService refService) {
        this.refService = refService;
    }

    public Collection<ApplicationInfo> listApps() throws Exception {
        Collection<ApplicationInfo> apps = Lists.newArrayList();
        AdminClient client = refService.getClient();
        AppManagement appManagement = AppManagementProxy.getJMXProxyForClient(client);
        for (Object name : appManagement.listApplications(null, null)) {
            ApplicationInfo app = new ApplicationInfo();
            app.setName((String) name);
            apps.add(app);
        }

        return apps;
    }
}
