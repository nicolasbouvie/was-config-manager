package me.bouvie.wasconfigmanager.service;

import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.configservice.ConfigServiceProxy;
import me.bouvie.wasconfigmanager.setup.AbstractSetupInfo;

import javax.management.ObjectName;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface ComponentSetupService<T extends AbstractSetupInfo> {

    default Class<T> getComponentType() {
        Type[] genericInterfaces = getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType
                    && ((ParameterizedType)genericInterface).getRawType().equals(ComponentSetupService.class)) {
                Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                return (Class<T>) genericTypes[0];
            }
        }
        throw new IllegalStateException("Impossible state");
    }

    ObjectName getOrCreateConfig(ConfigServiceProxy configServiceProxy, Session session, T configuration) throws Exception;
}
