package com.example.workmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WorkMateApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkMateApplication.class, args);
	}

}
