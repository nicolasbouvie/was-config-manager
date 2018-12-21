package me.bouvie.wasconfigmanager.setup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListenerPortInfo extends AbstractSetupInfo {
    private String name;
    private String initialState;
    private String description;
    private JMSConnectionFactoryInfo connectionFactory;
    private JMSQueueInfo destination;
    private Integer maxSessions;
    private Integer maxRetries;
    private Integer maxMessages;
}
