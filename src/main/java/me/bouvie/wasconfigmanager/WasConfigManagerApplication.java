package me.bouvie.wasconfigmanager;

import me.bouvie.wasconfigmanager.factory.IgnoreSSLSocketFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication(scanBasePackages = "me.bouvie.wasconfigmanager")
public class WasConfigManagerApplication {

	public static void main(String[] args) {
        Security.setProperty("ssl.SocketFactory.provider", IgnoreSSLSocketFactory.class.getName());

		SpringApplication.run(WasConfigManagerApplication.class, args);
	}

}