package io.github.kypleefearz.bodybuilding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BodybuildingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BodybuildingApplication.class, args);
    }
}
