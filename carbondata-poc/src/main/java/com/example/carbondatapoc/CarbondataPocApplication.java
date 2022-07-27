package com.example.carbondatapoc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;

@SpringBootApplication
@Slf4j
@Import({ CarbondataPocRunner.class })
public class CarbondataPocApplication {

	public static void main(String[] args) {
		log.info("Starting CarbondataPocApplication ...");
		SpringApplication.run(CarbondataPocApplication.class, args);
	}

	@Bean("conversionService")
	public ConversionService getConversionService() {
		ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
		// bean.setConverters(...); //add converters
		bean.afterPropertiesSet();
		return bean.getObject();
	}
}
