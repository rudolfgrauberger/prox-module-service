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

  public HopsService(HopsClient hopsClient, StudyCourseRepository studyCourseRepository) {
    this.hopsClient = hopsClient;
    this.studyCourseRepository = studyCourseRepository;
  }

  public void importStudyCourses() {
    List<HopsStudyCourse> hopsStudyCourses = hopsClient.getHopsStudyCourses();

    for (HopsStudyCourse hopsStudyCourse : hopsStudyCourses) {
      HopsStudyCourseId hopsId = new HopsStudyCourseId(hopsStudyCourse.getKuerzel());
      String[] tokens = hopsStudyCourse.getBezeichnung().split(": ");
      AcademicDegree academicDegree =
          tokens[0].equals("Master") ? AcademicDegree.MASTER : AcademicDegree.BACHELOR;
      StudyCourseName studyCourseName = new StudyCourseName(tokens[1]);

      StudyCourse studyCourse;
      Optional<StudyCourse> studyCourseOptional = studyCourseRepository.findByHopsId(hopsId);
      if (studyCourseOptional.isPresent()) {
        log.debug("Study course with HoPS ID " + hopsId + " already exists.");
        studyCourse = studyCourseOptional.get();
        studyCourse.setName(studyCourseName);
        studyCourse.setAcademicDegree(academicDegree);
      } else {
        log.debug("Study course with HoPS ID " + hopsId + " does not exist.");
        studyCourse = new StudyCourse(studyCourseName, academicDegree);
        studyCourse.setHopsId(hopsId);
      }
      studyCourse = studyCourseRepository.save(studyCourse);

      log.debug("Imported " + hopsStudyCourse + "into " + studyCourse);
    }
  }

}
