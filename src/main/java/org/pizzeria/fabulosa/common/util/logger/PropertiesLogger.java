package org.pizzeria.fabulosa.common.util.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.pizzeria.fabulosa.common.util.constant.AppConstants.APP_NAME;
import static org.pizzeria.fabulosa.common.util.constant.AppConstants.APP_VERSION;

@Slf4j
public class PropertiesLogger implements ApplicationListener<ApplicationPreparedEvent> {

	@Override
	public void onApplicationEvent(ApplicationPreparedEvent event) {
		logProps(event.getApplicationContext().getEnvironment());
	}

	private void logProps(Environment env) {
		final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();

		log.info("====== {} {} ======", APP_NAME, APP_VERSION);
		log.info("====== Configuration ======");
		log.info("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));

		StreamSupport.stream(sources.spliterator(), false)
				.filter(ps -> ps instanceof MapPropertySource && getPropsToLog().contains(ps.getName()))
				.map(ps -> ((MapPropertySource) ps).getPropertyNames())
				.flatMap(Arrays::stream)
				.distinct()
				.filter(prop -> !(prop.contains("credentials") || prop.contains("password") || prop.contains("email") || prop.contains("Password")))
				.forEach(prop -> log.info("{}: {}", prop, env.getProperty(prop)));

		log.info("===========================================");
	}

	private List<String> getPropsToLog() {
		String test = "test";
		String applicationInfo = "applicationInfo";
		String localProps = "Config resource 'class path resource [application.yaml]' via location 'optional:classpath:/'";
		String remoteProps = "Config resource 'file [application.yaml]' via location 'optional:file:./'";
		return List.of(test, localProps, remoteProps, applicationInfo);
	}
}