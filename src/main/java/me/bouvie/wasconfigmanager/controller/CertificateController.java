package me.bouvie.wasconfigmanager.controller;

import me.bouvie.wasconfigmanager.application.CertificateInfo;
import me.bouvie.wasconfigmanager.application.InstallCertificateInfo;
import me.bouvie.wasconfigmanager.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class CertificateController {

    private CertificateService service;

    @Autowired
    public CertificateController(CertificateService service) {
        this.service = service;
    }

    @RequestMapping(value="/cert", method = RequestMethod.GET)
    public Collection<CertificateInfo> listCertificates() throws Exception {
        return service.listCertificates();
    }

    @RequestMapping(value="/cert", method = RequestMethod.PUT)
    public int importCert(@RequestBody InstallCertificateInfo info) throws Exception {
        return service.importCert(info);
    }

    @RequestMapping(value="/cert", method = RequestMethod.DELETE)
    public void removeCert(@RequestBody InstallCertificateInfo info) throws Exception {
        service.removeCert(info.getAlias());
    }
}
