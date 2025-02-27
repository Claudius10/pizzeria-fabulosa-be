package org.pizzeria.fabulosa;

import org.pizzeria.fabulosa.utils.PropertiesLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(App.class);
		springApplication.addListeners(new PropertiesLogger());
		springApplication.run(args);
	}
}