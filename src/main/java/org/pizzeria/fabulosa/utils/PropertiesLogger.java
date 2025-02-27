package org.pizzeria.fabulosa.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class PropertiesLogger {

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) {
		String userProperties = "Config resource 'class path resource [application.yaml]' via location 'optional:classpath:/'";
		String serverPorts = "server.ports";
		String applicationInfo = "applicationInfo";
		List<String> props = List.of(userProperties, serverPorts, applicationInfo);

		final Environment env = event.getApplicationContext().getEnvironment();
		final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();

		log.info("====== Environment and configuration ======");
		log.info("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));

		StreamSupport.stream(sources.spliterator(), false)
				.filter(ps -> ps instanceof MapPropertySource && props.contains(ps.getName()))
				.map(ps -> ((MapPropertySource) ps).getPropertyNames())
				.flatMap(Arrays::stream)
				.distinct()
				.filter(prop -> !(prop.contains("credentials") || prop.contains("password")))
				.forEach(prop -> log.info("{}: {}", prop, env.getProperty(prop)));

		log.info("===========================================");
	}
}