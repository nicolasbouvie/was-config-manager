package me.bouvie.wasconfigmanager.setup;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JMSPoolInfo extends AbstractSetupInfo {
    private Long connectionTimeout;
    private Integer minConnections;
    private Integer maxConnections;
    private Long reapTime;
    private Long unusedTimeout;
    private Long agedTimeout;
    private String purgePolicy;
}
