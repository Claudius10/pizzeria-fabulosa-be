package org.pizzeria.fabulosa.utils.loggers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Set;

@Slf4j
public class EndpointsLogger implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		logEndpoints(applicationContext);
	}

	private void logEndpoints(ApplicationContext applicationContext) {
		log.info("====== Endpoints ======");
		RequestMappingHandlerMapping mappings = applicationContext.getBean(RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = mappings.getHandlerMethods();

		handlerMethods.forEach((k, v) -> {
			PathPatternsRequestCondition pathPatternsCondition = k.getPathPatternsCondition();

			if (pathPatternsCondition != null) {
				Set<String> endpoint = pathPatternsCondition.getPatternValues();
				log.info("{} - {} - {}", k.getMethodsCondition().getMethods(), endpoint, v.getBean());
			}
		});

		log.info("===========================================");
	}
}
