package org.pizzeria.fabulosa.common.datasource;

import com.zaxxer.hikari.HikariConfig;
import lombok.RequiredArgsConstructor;
import net.ttddyy.dsproxy.listener.SingleQueryCountHolder;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.pizzeria.fabulosa.common.property.DBProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Profile("db-debug")
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
				.countQuery(new SingleQueryCountHolder())
				.traceMethods()
				.formatQuery(FormatStyle.BASIC.getFormatter()::format)
				.logQueryToSysOut()
				.buildProxy();

		return proxyDataSource;
	}
}
