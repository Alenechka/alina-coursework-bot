package ru.alenechka.alinabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class AlinaBotApplication {

	public static void main(String[] args) {
		ApiContextInitializer.init();

		SpringApplication.run(AlinaBotApplication.class, args);
	}
}
