package me.bouvie.wasconfigmanager.setup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkManagerInfo extends AbstractSetupInfo {
    private String name;
    private String jndiName;
    private String description;
    private Integer workTimeout;
    private Integer workReqQSize;
    private Integer workReqQFullAction;
    private Integer numAlarmThreads;
    private Integer minThreads;
    private Integer maxThreads;
    private Integer threadPriority;
    private Boolean growable;
}
