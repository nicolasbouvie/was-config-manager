package me.bouvie.wasconfigmanager.setup;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
public class ApplicationSetup implements Serializable {
    private String namePattern;
    private Collection<AbstractSetupInfo> configuration;
}
