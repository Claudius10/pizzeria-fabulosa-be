package org.pizzeria.fabulosa.common.util;

import org.junit.jupiter.api.Test;
import org.pizzeria.fabulosa.common.util.logger.PropertiesLogger;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.mock.env.MockEnvironment;

import java.util.Properties;

import static org.mockito.Mockito.*;

public class LoggerTests {
	
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