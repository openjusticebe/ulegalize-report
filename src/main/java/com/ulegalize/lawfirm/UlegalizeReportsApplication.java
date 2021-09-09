package com.ulegalize.lawfirm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@EntityScan(value = "com.ulegalize.lawfirm", basePackageClasses = {Jsr310JpaConverters.class})
@SpringBootApplication
public class UlegalizeReportsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UlegalizeReportsApplication.class, args);
    }

}
