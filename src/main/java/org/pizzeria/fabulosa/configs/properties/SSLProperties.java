package org.pizzeria.fabulosa.configs.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ssl")
@Setter
@Getter
public class SSLProperties {

	private String keyStoreType;

	private String keyStorePath;

	private String keyStorePassword;

	private String keyAlias;
}