package org.pizzeria.fabulosa.configs.web.security.keys;

import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.utils.ExceptionLogger;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

@Slf4j
public class KeyGenerator {
	public static KeyPair generateRsaKeyPair() {
		KeyPair keyPair;

		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
			log.info("RSA Public Key {}", keyPair.getPublic());
		} catch (Exception e) {
			ExceptionLogger.log(e, log, null);
			throw new IllegalStateException();
		}
		return keyPair;
	}
}