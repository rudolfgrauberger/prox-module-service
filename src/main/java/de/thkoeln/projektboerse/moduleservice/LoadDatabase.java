package de.thkoeln.projektboerse.moduleservice;

import de.thkoeln.projektboerse.moduleservice.domain.aggregate.StudyCourse;
import de.thkoeln.projektboerse.moduleservice.domain.entity.Module;
import de.thkoeln.projektboerse.moduleservice.domain.repository.StudyCourseRepository;
import de.thkoeln.projektboerse.moduleservice.domain.value.AcademicDegree;
import de.thkoeln.projektboerse.moduleservice.domain.value.HoPSModuleID;
import de.thkoeln.projektboerse.moduleservice.domain.value.ModuleName;
import de.thkoeln.projektboerse.moduleservice.domain.value.StudyCourseName;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDatabase {

  @Bean
  CommandLineRunner initDatabase(StudyCourseRepository repository) {
    return args -> {
      log.info("Preloading " + repository.save(
          new StudyCourse(new StudyCourseName("Studiengang Test Bachelor"),
              AcademicDegree.BACHELOR)));
      log.info("Preloading " + repository.save(
          new StudyCourse(new StudyCourseName("Studiengang Test Master"), AcademicDegree.MASTER)));
      UUID testId = UUID.randomUUID();
      log.info("Preloading " + repository.save(
          new StudyCourse(testId, new StudyCourseName("Studiengang Test UUID"),
              AcademicDegree.MASTER)) + " - Custom UUID " + testId);
      HoPSModuleID hoPSModuleID = new HoPSModuleID(1L, Date.from(Instant.now()));
      ModuleName moduleName = new ModuleName("Module Test");
      Module module = new Module(moduleName, hoPSModuleID);
      StudyCourse studyCourse = repository.findById(testId).get();
      studyCourse.addModule(module);
      log.info("Overwriting " + repository.save(studyCourse));
    };
  }
}
