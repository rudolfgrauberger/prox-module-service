package de.thkoeln.projektboerse.moduleservice.hops;

import de.thkoeln.projektboerse.moduleservice.studycourse.AcademicDegree;
import de.thkoeln.projektboerse.moduleservice.studycourse.StudyCourse;
import de.thkoeln.projektboerse.moduleservice.studycourse.StudyCourseName;
import de.thkoeln.projektboerse.moduleservice.studycourse.StudyCourseRepository;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class HopsService {

  private final HopsClient hopsClient;

  private final StudyCourseRepository studyCourseRepository;

  private final StudyCourseMappingRepository studyCourseMappingRepository;

  public HopsService(HopsClient hopsClient, StudyCourseRepository studyCourseRepository,
      StudyCourseMappingRepository studyCourseMappingRepository) {
    this.hopsClient = hopsClient;
    this.studyCourseRepository = studyCourseRepository;
    this.studyCourseMappingRepository = studyCourseMappingRepository;
  }

  public void importStudyCourses() {

    // Load study courses from HoPS API
    List<HopsStudyCourse> hopsStudyCourses = hopsClient.getHopsStudyCourses();

    // Unfortunately the HoPS API just returns "null" in the response body if its backend is down
    // and does not reply with a corresponding error code or message and not even valid JSON so the
    // HopsClient just returns null instead of a list.
    if (hopsStudyCourses == null) {
      log.debug("HoPS-API ist down!");
      return;
    }

    for (HopsStudyCourse hopsStudyCourse : hopsStudyCourses) {

      // Parse relevant data and convert to domain model
      HopsStudyCourseId hopsId = new HopsStudyCourseId(hopsStudyCourse.getKuerzel());
      String[] tokens = hopsStudyCourse.getBezeichnung().split(": ");
      AcademicDegree academicDegree =
          tokens[0].equals("Master") ? AcademicDegree.MASTER : AcademicDegree.BACHELOR;
      StudyCourseName studyCourseName = new StudyCourseName(tokens[1]);

      // Update existing study course with new data or create a new one from scratch
      StudyCourse studyCourse;
      Optional<StudyCourseMapping> studyCourseMapping = studyCourseMappingRepository
          .findByHopsId(hopsId);
      if (studyCourseMapping.isPresent()) {
        log.debug("Study course with HoPS ID " + hopsId + " already exists.");
        studyCourse = studyCourseRepository.findById(studyCourseMapping.get().getStudyCourseId())
            .get();
        studyCourse.setName(studyCourseName);
        studyCourse.setAcademicDegree(academicDegree);
        studyCourse = studyCourseRepository.save(studyCourse);
      } else {
        log.debug("Study course with HoPS ID " + hopsId + " does not exist yet.");
        studyCourse = new StudyCourse(studyCourseName, academicDegree);
        studyCourse = studyCourseRepository.save(studyCourse);
        studyCourseMappingRepository.save(new StudyCourseMapping(hopsId, studyCourse.getId()));
      }

      log.debug("Imported " + hopsStudyCourse + "into " + studyCourse);
    }
  }

}
