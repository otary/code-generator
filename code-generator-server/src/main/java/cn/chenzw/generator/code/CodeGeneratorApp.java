package cn.chenzw.generator.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CodeGeneratorApp {

    public static void main(String[] args) {
        SpringApplication.run(CodeGeneratorApp.class, new String[]{
                "--spring.profiles.active=mysql"
        });
    }
}
