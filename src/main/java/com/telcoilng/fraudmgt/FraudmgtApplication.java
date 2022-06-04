package com.telcoilng.fraudmgt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;


@SpringBootApplication
@EntityScan(basePackageClasses = {FraudmgtApplication.class, Jsr310JpaConverters.class})
public class FraudmgtApplication {

	public static void main(String[] args) {
		SpringApplication.run(FraudmgtApplication.class, args);
	}

}
