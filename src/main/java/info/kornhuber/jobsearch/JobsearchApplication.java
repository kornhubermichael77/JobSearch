package info.kornhuber.jobsearch;

import info.kornhuber.jobsearch.config.RememberMeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RememberMeProperties.class)
@SpringBootApplication
public class JobsearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobsearchApplication.class, args);
	}

}
