package me.bouvie.wasconfigmanager.setup;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, of = {"jndiName"})
public class WorkManagerInfo extends AbstractSetupInfo {
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

