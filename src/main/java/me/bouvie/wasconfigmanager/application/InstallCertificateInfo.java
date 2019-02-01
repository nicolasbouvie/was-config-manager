package me.bouvie.wasconfigmanager.application;

import lombok.Data;

import java.io.Serializable;

@Data
public class InstallCertificateInfo implements Serializable {
    private String alias;
    private String host;
    private Integer port;
}
