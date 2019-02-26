package io.archilab.projektboerse.moduleservice.hops;

import io.archilab.projektboerse.moduleservice.studycourse.Module;
import io.archilab.projektboerse.moduleservice.studycourse.ModuleDescription;
import io.archilab.projektboerse.moduleservice.studycourse.ModuleName;
import io.archilab.projektboerse.moduleservice.studycourse.ModuleRepository;
import io.archilab.projektboerse.moduleservice.studycourse.StudyCourse;
import io.archilab.projektboerse.moduleservice.studycourse.StudyCourseRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class HopsModuleService {

  private final HopsClient hopsClient;

  private final ModuleRepository moduleRepository;

  private final HopsModuleMappingRepository hopsModuleMappingRepository;

  private final StudyCourseRepository studyCourseRepository;

  private final HopsStudyCourseMappingRepository hopsStudyCourseMappingRepository;

  public HopsModuleService(HopsClient hopsClient, ModuleRepository moduleRepository,
      HopsModuleMappingRepository hopsModuleMappingRepository,
      StudyCourseRepository studyCourseRepository,
      HopsStudyCourseMappingRepository hopsStudyCourseMappingRepository) {
    this.hopsClient = hopsClient;
    this.moduleRepository = moduleRepository;
    this.hopsModuleMappingRepository = hopsModuleMappingRepository;
    this.studyCourseRepository = studyCourseRepository;
    this.hopsStudyCourseMappingRepository = hopsStudyCourseMappingRepository;
  }

  public void importModules() {

    // Load study courses from HoPS API
    List<HopsModule> hopsModules = null;

    // Could catch exception if HoPS-API is completely down but since we have an in-memory database
    // it would cause the old data to get wiped so we commented it out
    hopsModules = this.hopsClient.getHopsModules();
//    try {
//      hopsModules = this.hopsClient.getHopsModules();
//    } catch (Exception e) {
//      HopsModuleService.log.info("HoPS-API is down!");
//      return;
//    }

    // Unfortunately the HoPS API just returns "null" in the response body if its backend is down
    // and does not reply with a corresponding error code or message and not even valid JSON so the
    // HopsClient just returns null instead of a list.
    if (hopsModules == null) {
      HopsModuleService.log.info("HoPS-API is down!");
      return;
    }

    for (HopsModule hopsModule : hopsModules) {

      // Find study course by searching for its ID in the mapping repository
      HopsStudyCourseId hopsStudyCourseId = new HopsStudyCourseId(hopsModule.getStudiengang());
      UUID studyCourseId = this.hopsStudyCourseMappingRepository.findByHopsId(hopsStudyCourseId)
          .get()
          .getStudyCourseId();
      StudyCourse studyCourse = this.studyCourseRepository.findById(studyCourseId).get();

      // Parse relevant data and convert to domain model
      HopsModuleId hopsId = new HopsModuleId(hopsModule.getKuerzel(), hopsModule.getVersion());
      ModuleName moduleName = new ModuleName(hopsModule.getBezeichnung());

      // Update existing module with new data or create a new one from scratch
      Module module;
      Optional<HopsModuleMapping> moduleMapping = this.hopsModuleMappingRepository
          .findByHopsId(hopsId);
      if (moduleMapping.isPresent()) {
        HopsModuleService.log.debug("Module with HoPS ID " + hopsId + " already exists.");
        module = this.moduleRepository.findById(moduleMapping.get().getModuleId()).get();
        module.setName(moduleName);
        module = this.moduleRepository.save(module);
      } else {
        HopsModuleService.log.debug("Module with HoPS ID " + hopsId + " does not exist yet.");
        module = new Module(moduleName, new ModuleDescription(""));
        module = this.moduleRepository.save(module);
        this.hopsModuleMappingRepository.save(new HopsModuleMapping(hopsId, module.getId()));
      }

      studyCourse.addModule(module);
      this.studyCourseRepository.save(studyCourse);

      HopsModuleService.log.debug("Imported " + hopsModule + "into " + module);
    }
  }

}
