package org.pizzeria.fabulosa.configs.db;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import ch.vorburger.mariadb4j.MariaDB4jService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@TestConfiguration
@Slf4j
public class DataSourceConfig {

	@Value("${db.url}")
	private String url;

	@Value("${db.username}")
	private String username;

	@Value("${db.password}")
	private String password;

	private MariaDB4jService embeddedDB;

	@PostConstruct
	void embeddedDb() {
		MariaDB4jService mariaDB4jService = new MariaDB4jService();
		DBConfigurationBuilder config = mariaDB4jService.getConfiguration();
		config.setPort(3307);
		this.embeddedDB = mariaDB4jService;
	}

	@Bean
	DataSource dataSource() throws ManagedProcessException {
		if (!"jdbc:mariadb://localhost:3307/test".equals(url)) {
			throw new RuntimeException("Error initializing embedded db: using wrong application properties file");
		}

		embeddedDB.start();

		return DataSourceBuilder.create()
				.username(username)
				.password(password)
				.url(url)
				.build();
	}

	@PreDestroy
	void shutdown() throws ManagedProcessException {
		if (embeddedDB != null && embeddedDB.getDB() != null) {
			embeddedDB.stop();
		}
	}
}
