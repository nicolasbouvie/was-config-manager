package me.bouvie.wasconfigmanager.application;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ApplicationInfo implements Serializable {
    private String name;
}
