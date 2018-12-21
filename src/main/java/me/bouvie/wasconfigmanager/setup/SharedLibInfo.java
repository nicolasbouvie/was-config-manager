package me.bouvie.wasconfigmanager.setup;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SharedLibInfo extends AbstractSetupInfo {
    private String name;
    private String description;
    private List<String> nativePath;
    private List<String> classpath;
    private boolean isolatedClassLoader;
}
