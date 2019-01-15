package de.thkoeln.projektboerse.moduleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ModuleService {

  public static void main(String[] args) {
    SpringApplication.run(ModuleService.class, args);
  }

}
