package edu.hei.school.evaluation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EvaluationFinaleProg3Application {
    public static void main(String[] args) {
        SpringApplication.run(EvaluationFinaleProg3Application.class, args);
    }
}
