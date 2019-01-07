package me.bouvie.wasconfigmanager.setup;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonIdentityInfo(generator= ObjectIdGenerators.UUIDGenerator.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AuthInfo.class, name = "Auth"),
    @JsonSubTypes.Type(value = DatasourceInfo.class, name = "Datasource"),
    @JsonSubTypes.Type(value = JDBCProviderInfo.class, name = "JDBCProvider"),
    @JsonSubTypes.Type(value = JMSConnectionFactoryInfo.class, name = "JMSConnectionFactory"),
    @JsonSubTypes.Type(value = JMSProviderInfo.class, name = "JMSProvider"),
    @JsonSubTypes.Type(value = JMSQueueInfo.class, name = "JMSQueue"),
    @JsonSubTypes.Type(value = ListenerPortInfo.class, name = "ListenerPort"),
    @JsonSubTypes.Type(value = SharedLibInfo.class, name = "SharedLib"),
    @JsonSubTypes.Type(value = WorkManagerInfo.class, name = "WorkManager")

})
@Data
@EqualsAndHashCode(of = {"name"})
public abstract class AbstractSetupInfo implements Serializable {
    private String name;
}
