package org.pizzeria.fabulosa.configs.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

	@Value("${db.url}")
	private String url;

	@Value("${db.username}")
	private String username;

	@Value("${db.password}")
	private String password;

	@Bean
	DataSource dataSource() {
		HikariConfig config = new HikariConfig();
		config.setPoolName("pizzeria");

		DataSource actualDataSource = DataSourceBuilder
				.create()
				.url(url)
				.username(username)
				.password(password)
				.build();

		DataSource proxyDataSource = ProxyDataSourceBuilder
				.create(actualDataSource)
				.name("pizzeriaDataSource")
				//.countQuery(new SingleQueryCountHolder())
				//.traceMethods()
				//.formatQuery(FormatStyle.BASIC.getFormatter()::format)
				//.logQueryToSysOut()
				.buildProxy();

		config.setDataSource(actualDataSource);
		return new HikariDataSource(config);
	}
}
