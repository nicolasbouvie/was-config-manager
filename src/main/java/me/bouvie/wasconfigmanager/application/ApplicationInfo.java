package me.bouvie.wasconfigmanager.application;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApplicationInfo implements Serializable {
    private String name;
}
