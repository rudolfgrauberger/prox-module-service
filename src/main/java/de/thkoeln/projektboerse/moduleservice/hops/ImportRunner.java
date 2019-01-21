package de.thkoeln.projektboerse.moduleservice.hops;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportRunner {

  @Bean
  CommandLineRunner runFeign(HopsService hopsService) {
    return args -> {
      hopsService.importStudyCourses();
    };
  }


}
