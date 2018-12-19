package me.bouvie.wasconfigmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication(scanBasePackages = "me.bouvie.wasconfigmanager")
public class WasConfigManagerApplication {

	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.keyStore", WasConfigManagerApplication.class.getResource("/was9/DummyClientKeyFile.jks").getFile());
        System.setProperty("javax.net.ssl.trustStore", WasConfigManagerApplication.class.getResource("/was9/DummyClientTrustFile.jks").getFile());
        System.setProperty("javax.net.ssl.keyStorePassword", "WebAS");
		System.setProperty("javax.net.ssl.trustStorePassword", "WebAS");
        Security.setProperty("ssl.SocketFactory.provider", "com.ibm.jsse2.SSLSocketFactoryImpl");
        Security.setProperty("ssl.ServerSocketFactory.provider", "com.ibm.jsse2.SSLServerSocketFactoryImpl");

		SpringApplication.run(WasConfigManagerApplication.class, args);
	}

}