package org.wildcodeschool.myblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController

public class MyblogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyblogApplication.class, args);
	}
	@GetMapping("/hello")
	public String sayHello() {
		return "Hello World!";
	}
}
