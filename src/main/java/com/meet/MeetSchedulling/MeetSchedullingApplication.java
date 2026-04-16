package com.meet.MeetSchedulling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MeetSchedullingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeetSchedullingApplication.class, args);
	}

}
