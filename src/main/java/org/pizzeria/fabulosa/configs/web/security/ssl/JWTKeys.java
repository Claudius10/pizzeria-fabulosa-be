package org.pizzeria.fabulosa.configs.web.security.ssl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.configs.properties.SSLProperties;
import org.pizzeria.fabulosa.utils.loggers.ExceptionLogger;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPublicKey;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTKeys {

	private final SSLProperties sslProperties;

	private KeyPair keyPair;

	@PostConstruct
	public void init() {
		KeyStore keyStore = getKeyStore();
		if (keyStore != null) {
			ssl(keyStore);
		} else {
			backup();
		}
	}

	private KeyStore getKeyStore() {
		try {
			return KeyStore.getInstance(sslProperties.getKeyStoreType());
		} catch (KeyStoreException ex) {
			log.error("Error getting KeyStore type {}", sslProperties.getKeyStoreType());
			ExceptionLogger.log(ex, log, null);
			return null;
		}
	}

	private void ssl(KeyStore keyStore) {
		try (FileInputStream fileInputStream = new FileInputStream(sslProperties.getKeyStorePath())) {

			keyStore.load(fileInputStream, sslProperties.getKeyStorePassword().toCharArray());

			PrivateKey privateKey = (PrivateKey) keyStore.getKey(sslProperties.getKeyAlias(), sslProperties.getKeyStorePassword().toCharArray());
			Certificate certificate = keyStore.getCertificate(sslProperties.getKeyAlias());
			PublicKey publicKey = certificate.getPublicKey();

			keyPair = new KeyPair(publicKey, privateKey);
			log.info("KeyPair loaded from {}", sslProperties.getKeyStorePath());

		} catch (Exception ex) {
			log.error("Error loading KeyPair from {}", sslProperties.getKeyStorePath());
			ExceptionLogger.log(ex, log, null);
			backup();
		}
	}

	private void backup() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(3072);
			keyPair = keyPairGenerator.generateKeyPair();
			log.info("Loaded backup KeyPair");
		} catch (Exception e) {
			ExceptionLogger.log(e, log, null);
			throw new IllegalStateException();
		}
	}

	public PrivateKey getPrivateKey() {
		return keyPair.getPrivate();
	}

	public RSAPublicKey getPublicKey() {
		return (RSAPublicKey) keyPair.getPublic();
	}
}