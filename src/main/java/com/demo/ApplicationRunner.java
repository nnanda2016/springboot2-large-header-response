package com.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Application for this demo sample.
 * 
 * @author Niranjan Nanda
 */
@SpringBootApplication
@Import(AppConfig.class)
public class ApplicationRunner {
	public static void main(final String[] args) {
		SpringApplication.run(ApplicationRunner.class, args);
	}
	
	@Bean
    public CommandLineRunner startProcessing(final ServiceInvoker searchInvoker) {
        return args -> searchInvoker.start();
    }
}
