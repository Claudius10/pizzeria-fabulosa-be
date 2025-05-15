package org.pizzeria.fabulosa.utils;

import org.junit.jupiter.api.Test;
import org.pizzeria.fabulosa.common.util.logger.PropertiesLogger;
import org.pizzeria.fabulosa.web.controllers.open.AnonController;
import org.pizzeria.fabulosa.web.util.logger.EndpointsLogger;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.mockito.Mockito.*;

public class LoggerTests {

	@Test
	void givenEndpoints_thenLogEndpoints() {

		// Arrange

		ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		RequestMappingHandlerMapping requestMappingHandlerMapping = mock(RequestMappingHandlerMapping.class);
		RequestMappingInfo requestMappingInfo = mock(RequestMappingInfo.class);
		HandlerMethod handlerMethod = mock(HandlerMethod.class);
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = Map.of(requestMappingInfo, handlerMethod);
		PathPatternsRequestCondition pathPatternsRequestCondition = mock(PathPatternsRequestCondition.class);

		doReturn(applicationContext).when(event).getApplicationContext();
		doReturn(requestMappingHandlerMapping).when(applicationContext).getBean(RequestMappingHandlerMapping.class);
		doReturn(handlerMethods).when(requestMappingHandlerMapping).getHandlerMethods();
		doReturn(pathPatternsRequestCondition).when(requestMappingInfo).getPathPatternsCondition();
		doReturn(Set.of("/test")).when(pathPatternsRequestCondition).getPatternValues();


		RequestMethodsRequestCondition methodsRequestCondition = new RequestMethodsRequestCondition(RequestMethod.GET);
		doReturn(methodsRequestCondition).when(requestMappingInfo).getMethodsCondition();
		doReturn(mock(AnonController.class)).when(handlerMethod).getBean();

		EndpointsLogger endpointsLogger = spy(new EndpointsLogger());

		// Act

		endpointsLogger.onApplicationEvent(event);

		// Assert

		verify(endpointsLogger, times(1)).onApplicationEvent(event);
	}

	@Test
	void givenProperties_thenLogProperties() {

		// Arrange

		ApplicationPreparedEvent event = mock(ApplicationPreparedEvent.class);
		ConfigurableApplicationContext applicationContext = mock(ConfigurableApplicationContext.class);
		ConfigurableEnvironment mockEnvironment = new MockEnvironment();

		Properties prop = new Properties();
		prop.put("test1", "test2");

		mockEnvironment.addActiveProfile("test");
		mockEnvironment.getPropertySources().addLast(new PropertiesPropertySource("test", prop));

		PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource("systemProperties", prop);
		MutablePropertySources mutablePropertySources = new MutablePropertySources();
		mutablePropertySources.addFirst(propertiesPropertySource);

		doReturn(applicationContext).when(event).getApplicationContext();
		doReturn(mockEnvironment).when(applicationContext).getEnvironment();


		PropertiesLogger propertiesLogger = spy(new PropertiesLogger());

		// Act

		propertiesLogger.onApplicationEvent(event);

		// Assert

		verify(propertiesLogger, times(1)).onApplicationEvent(event);
	}
}