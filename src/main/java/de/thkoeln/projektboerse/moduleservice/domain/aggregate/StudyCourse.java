package de.thkoeln.projektboerse.moduleservice.domain.aggregate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.thkoeln.projektboerse.moduleservice.domain.entity.Module;
import de.thkoeln.projektboerse.moduleservice.domain.value.AcademicDegree;
import de.thkoeln.projektboerse.moduleservice.domain.value.StudyCourseName;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.ToString;

@Entity
@ToString
@Getter
public class StudyCourse {

  @Id
  @GeneratedValue
  @JsonIgnore
  private UUID id;

  @JsonUnwrapped
  private StudyCourseName name;

  private AcademicDegree academicDegree;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private Set<Module> modules = new HashSet<>();

  @OneToMany(mappedBy = "parentStudyCourse", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<StudyCourse> studyDirections = new HashSet<>();

  @ManyToOne
  private StudyCourse parentStudyCourse;

  protected StudyCourse() {
  }

  public StudyCourse(StudyCourseName name, AcademicDegree academicDegree) {
    this.name = name;
    this.academicDegree = academicDegree;
  }

  public Set<StudyCourse> getStudyDirections() {
    return Collections.unmodifiableSet(studyDirections);
  }

  public Set<Module> getModules() {
    return Collections.unmodifiableSet(modules);
  }

  public void addModule(Module module) {
    modules.add(module);
  }

  public void removeModule(Module module) {
    modules.remove(module);
  }

  public void addStudyDirection(StudyCourse studyDirection) {
    if (studyDirection.getParentStudyCourse() != null) {
      throw new RuntimeException("A study direction must only have one parent!");
    }
    if (this.academicDegree != studyDirection.academicDegree) {
      throw new RuntimeException(
          "A study direction must have the same academic degree as the corresponding study course!");
    }
    studyDirections.add(studyDirection);
    studyDirection.setParentStudyCourse(this);
  }

  public void removeStudyDirection(StudyCourse studyDirection) {
    studyDirections.remove(studyDirection);
    studyDirection.setParentStudyCourse(null);
  }

  public void setParentStudyCourse(StudyCourse parentStudyCourse) {
    if (!this.studyDirections.isEmpty()) {
      throw new RuntimeException("A study direction must not have study directions!");
    }
    if (this.academicDegree != parentStudyCourse.academicDegree) {
      throw new RuntimeException(
          "A study direction must have the same academic degree as the corresponding study course!");
    }
    this.parentStudyCourse = parentStudyCourse;
  }

}
