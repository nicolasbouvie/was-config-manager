package me.bouvie.wasconfigmanager.setup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthInfo extends AbstractSetupInfo {
    private String name;
    private String username;
    private String password;
    private String description;
}
