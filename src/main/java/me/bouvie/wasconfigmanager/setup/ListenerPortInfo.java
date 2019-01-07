package me.bouvie.wasconfigmanager.setup;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListenerPortInfo extends AbstractSetupInfo {
    private String initialState;
    private String description;
    private JMSConnectionFactoryInfo connectionFactory;
    private JMSQueueInfo destination;
    private Integer maxSessions;
    private Integer maxRetries;
    private Integer maxMessages;
}
