package me.bouvie.wasconfigmanager.setup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JMSPoolInfo extends AbstractSetupInfo {
    private Long connectionTimeout;
    private Integer minConnections;
    private Integer maxConnections;
    private Long reapTime;
    private Long unusedTimeout;
    private Long agedTimeout;
    private String purgePolicy;
}
