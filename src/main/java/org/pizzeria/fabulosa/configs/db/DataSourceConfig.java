package org.pizzeria.fabulosa.configs.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.pizzeria.fabulosa.configs.properties.DBProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

	private final DBProperties dbProperties;

	@Bean
	DataSource dataSource() {
		HikariConfig config = new HikariConfig();
		config.setPoolName("pizzeria");

		DataSource actualDataSource = DataSourceBuilder
				.create()
				.url(dbProperties.getUrl())
				.username(dbProperties.getUsername())
				.password(dbProperties.getPassword())
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
