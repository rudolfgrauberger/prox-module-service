package de.thkoeln.projektboerse.moduleservice.domain.aggregate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.thkoeln.projektboerse.moduleservice.domain.value.AcademicDegree;
import de.thkoeln.projektboerse.moduleservice.domain.value.StudyCourseName;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "STUDYCOURSES")
@Data
public class StudyCourse {

  @Id
  @JsonIgnore
  private UUID id;

  @JsonUnwrapped
  private StudyCourseName name;
  private AcademicDegree academicDegree;
//  private ExaminationRegulations examinationRegulations;
//  private StudyDirection studyDirection;

  protected StudyCourse() {
  }

  public StudyCourse(StudyCourseName name, AcademicDegree academicDegree) {
    this(UUID.randomUUID(), name, academicDegree);
  }

  public StudyCourse(UUID id, StudyCourseName name, AcademicDegree academicDegree) {
    this.id = id;
    this.name = name;
    this.academicDegree = academicDegree;
  }

}
