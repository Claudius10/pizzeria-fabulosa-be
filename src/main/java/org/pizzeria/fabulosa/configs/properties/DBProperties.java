package org.pizzeria.fabulosa.configs.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "db")
@Setter
@Getter
public class DBProperties {

	private String url;

	private String username;

	private String password;
}
