package com.jubilee.workit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

import static java.awt.SystemColor.info;

@Slf4j
@SpringBootApplication
public class WorkitApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkitApplication.class, args);
		log.info("\n\n============= started =============\n\n");
	}

}