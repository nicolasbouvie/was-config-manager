package me.bouvie.wasconfigmanager.setup;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SharedLibInfo extends AbstractSetupInfo {
    private String description;
    private List<String> nativePath;
    private List<String> classpath;
    private boolean isolatedClassLoader;
}
