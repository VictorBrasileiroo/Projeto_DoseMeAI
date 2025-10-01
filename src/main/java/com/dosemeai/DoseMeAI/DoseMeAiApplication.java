package com.dosemeai.DoseMeAI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DoseMeAiApplication {
	public static void main(String[] args) {
		SpringApplication.run(DoseMeAiApplication.class, args);
	}
}
