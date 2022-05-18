package de.hoehne.stackit.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TestStackitApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(TestStackitApplication.class, args);
	}

}
