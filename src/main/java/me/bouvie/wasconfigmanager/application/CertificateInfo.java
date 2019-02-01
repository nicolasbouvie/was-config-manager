package me.bouvie.wasconfigmanager.application;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
public class CertificateInfo implements Serializable {
    private String alias;
    private Integer version;
    private Integer keySize;
    private BigInteger serialNumber;
    private Date validFrom;
    private Date validUntil;
    private String issuedTo;
    private String issuedBy;
    private String fingerprint;
    private String algorithm;
}
