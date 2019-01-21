package de.thkoeln.projektboerse.moduleservice.hops;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportRunner {

  @Bean
  CommandLineRunner runFeign(HopsStudyCourseService hopsStudyCourseService,
      HopsModuleService hopsModuleService) {
    return args -> {
      hopsStudyCourseService.importStudyCourses();
      hopsModuleService.importModules();
    };
  }


}
