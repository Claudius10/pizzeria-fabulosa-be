package org.pizzeria.fabulosa;

import org.pizzeria.fabulosa.common.util.logger.PropertiesLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PizzeriaFabulosa {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(PizzeriaFabulosa.class);
		springApplication.addListeners(new PropertiesLogger());
		springApplication.run(args);
	}
}