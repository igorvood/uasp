package ru.vtb.uasp.beg.lib.producertest;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackageClasses = Application.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        //keepThreadOn();
    }

    @SneakyThrows
    private static void keepThreadOn() {
        Thread.currentThread().join();
    }
}
