package org.pizzeria.fabulosa.configs.web.security.keys;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Getter
@Component
public class RSAKeyPair {

	private final RSAPublicKey publicKey;

	private final RSAPrivateKey privateKey;

	public RSAKeyPair() {
		KeyPair pair = KeyGenerator.generateRsaKeyPair();
		this.publicKey = (RSAPublicKey) pair.getPublic();
		this.privateKey = (RSAPrivateKey) pair.getPrivate();
	}
}