package me.bouvie.wasconfigmanager.setup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthInfo extends AbstractSetupInfo {
    private String alias;
    private String username;
    private String password;
    private String description;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
