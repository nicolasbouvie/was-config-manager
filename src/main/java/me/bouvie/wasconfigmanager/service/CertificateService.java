package me.bouvie.wasconfigmanager.service;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import me.bouvie.wasconfigmanager.application.CertificateInfo;
import me.bouvie.wasconfigmanager.application.InstallCertificateInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.Enumeration;

@Service
public class CertificateService {

    private String keystorePath;
    private char[] keystorePassword;

    @Autowired
    public CertificateService(
            @Value("${keystore.path}") String keystorePath,
            @Value("${keystore.password}") String keystorePassword) {
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword.toCharArray();
    }

    public Collection<CertificateInfo> listCertificates() throws Exception {
        Collection<CertificateInfo> certs = Lists.newArrayList();
        KeyStore keystore = getKeystore();

        Enumeration<String> aliases = keystore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);

            CertificateInfo cert = new CertificateInfo();
            cert.setAlias(alias);
            cert.setVersion(certificate.getVersion());
            cert.setKeySize(getKeyLength(certificate.getPublicKey()));
            cert.setSerialNumber(certificate.getSerialNumber());
            cert.setValidFrom(certificate.getNotBefore());
            cert.setValidUntil(certificate.getNotAfter());
            cert.setIssuedTo(certificate.getSubjectDN().getName());
            cert.setIssuedBy(certificate.getIssuerDN().getName());
            cert.setFingerprint(getFingerprint(certificate));
            cert.setAlgorithm(String.format("%s(%s)", certificate.getSigAlgName(), certificate.getSigAlgOID()));
            certs.add(cert);
        }

        return certs;
    }

    public int importCert(InstallCertificateInfo info) throws Exception {
        KeyStore keystore = getKeystore();

        X509Certificate[] certificateChain = getCertificateChain(info);

        return saveCertificateChain(keystore, info.getAlias(), certificateChain);
    }

    public void removeCert(String alias) throws Exception {
        KeyStore keystore = getKeystore();
        keystore.deleteEntry(alias);
        try (FileOutputStream out = new FileOutputStream(keystorePath)) {
            keystore.store(out, keystorePassword);
        }
    }

    private int saveCertificateChain(KeyStore keystore, String certAlias, X509Certificate[] certificateChain) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        int newCerts = 0;
        for (X509Certificate cert : certificateChain) {
            String alias = keystore.getCertificateAlias(cert);
            if (alias != null) {
                System.out.println("Certificate already exists, alias: " + alias);
                continue;
            }
            keystore.setCertificateEntry(certAlias+"_"+newCerts, cert);
            newCerts++;
        }

        if (newCerts > 0) {
            // Save the new keystore contents
            try (FileOutputStream out = new FileOutputStream(keystorePath)) {
                keystore.store(out, keystorePassword);
            }
        }
        return newCerts;
    }

    private KeyStore getKeystore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

        // Load the keystore contents
        try (FileInputStream in = new FileInputStream(keystorePath)) {
            keystore.load(in, keystorePassword);
            return keystore;
        }
    }

    private X509Certificate[] getCertificateChain(InstallCertificateInfo info) throws Exception {
        SSLSocket ss = (SSLSocket) SSLSocketFactory.getDefault().createSocket(info.getHost(), info.getPort());
        SSLContext context = SSLContext.getInstance(ss.getSession().getProtocol());
        ss.close();

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(getKeystore());

        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[] {tm}, null);
        SSLSocketFactory factory = context.getSocketFactory();

        SSLSocket socket = (SSLSocket) factory.createSocket(info.getHost(), info.getPort());
        socket.setSoTimeout(10000);
        try {
            socket.startHandshake();
            socket.close();
        } catch (SSLException e) {
            //ignored
        }
        return tm.chain;
    }

    private String getFingerprint(X509Certificate certificate) throws CertificateEncodingException {
        String sha1 = Hashing.sha1().hashBytes(certificate.getEncoded()).toString();
        return Joiner.on(":").join(Splitter.fixedLength(2).split(sha1));
    }

    private int getKeyLength(final PublicKey pk) {
        int len = -1;
        if (pk instanceof RSAPublicKey) {
            final RSAPublicKey rsapub = (RSAPublicKey) pk;
            len = rsapub.getModulus().bitLength();
        } else if (pk instanceof ECPublicKey) {
            final ECPublicKey ecpriv = (ECPublicKey) pk;
            final java.security.spec.ECParameterSpec spec = ecpriv.getParams();
            if (spec != null) {
                len = spec.getOrder().bitLength();
            } else {
                len = 0;
            }
        } else if (pk instanceof DSAPublicKey) {
            final DSAPublicKey dsapub = (DSAPublicKey) pk;
            if ( dsapub.getParams() != null ) {
                len = dsapub.getParams().getP().bitLength();
            } else {
                len = dsapub.getY().bitLength();
            }
        }
        return len;
    }

    private static class SavingTrustManager implements X509TrustManager {
        private final X509TrustManager tm;
        private X509Certificate[] chain;

        private SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }
}
