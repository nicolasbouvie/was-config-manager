package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceHelper;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import me.bouvie.wasconfigmanager.setup.WorkManagerInfo;
import org.springframework.stereotype.Service;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;

@Service
public class WorkManagerSetupService implements ComponentSetupService<WorkManagerInfo> {

    private ReferenceService refService;

    public WorkManagerSetupService(ReferenceService refService) {
        this.refService = refService;
    }

    @Override
    public ObjectName getOrCreateConfig(ConfigServiceProxy config, Session session, WorkManagerInfo wmInfo) throws Exception {
        ObjectName scope = refService.getDefaultWorkManagerProviderRef(config, session);
        ObjectName[] workManager = config.queryConfigObjects(session, scope, ConfigServiceHelper.createObjectName(null, "WorkManagerInfo", wmInfo.getName()), null);
        if (workManager != null && workManager.length >= 1) {
            return workManager[0];
        }

        AttributeList attrs = new AttributeList();
        attrs.add(new Attribute("name", wmInfo.getName()));
        attrs.add(new Attribute("jndiName", wmInfo.getJndiName()));
        attrs.add(new Attribute("description", wmInfo.getDescription()));
        attrs.add(new Attribute("workTimeout", wmInfo.getWorkTimeout()));
        attrs.add(new Attribute("workReqQSize", wmInfo.getWorkReqQSize()));
        attrs.add(new Attribute("workReqQFullAction", wmInfo.getWorkReqQFullAction()));
        attrs.add(new Attribute("numAlarmThreads", wmInfo.getNumAlarmThreads()));
        attrs.add(new Attribute("minThreads", wmInfo.getMinThreads()));
        attrs.add(new Attribute("maxThreads", wmInfo.getMaxThreads()));
        attrs.add(new Attribute("threadPriority", wmInfo.getThreadPriority()));
        attrs.add(new Attribute("isGrowable", wmInfo.getGrowable()));

        return config.createConfigData(session, scope, "WorkManagerInfo", "WorkManagerInfo", attrs);
    }
}
