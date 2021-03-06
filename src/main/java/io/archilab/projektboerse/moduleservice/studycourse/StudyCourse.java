package io.archilab.projektboerse.moduleservice.studycourse;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.archilab.projektboerse.moduleservice.core.AbstractEntity;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyCourse extends AbstractEntity {

  @Setter
  @JsonUnwrapped
  private StudyCourseName name;

  @Setter
  private AcademicDegree academicDegree;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private Set<Module> modules = new HashSet<>();

  @OneToMany(mappedBy = "parentStudyCourse", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<StudyCourse> studyDirections = new HashSet<>();

  @ManyToOne
  private StudyCourse parentStudyCourse;

  public StudyCourse(StudyCourseName name, AcademicDegree academicDegree) {
    this.name = name;
    this.academicDegree = academicDegree;
  }

  public Set<StudyCourse> getStudyDirections() {
    return Collections.unmodifiableSet(this.studyDirections);
  }

  public Set<Module> getModules() {
    return Collections.unmodifiableSet(this.modules);
  }

  public void addModule(Module module) {
    this.modules.add(module);
  }

  public void removeModule(Module module) {
    this.modules.remove(module);
  }

  public void addStudyDirection(StudyCourse studyDirection) {
    if (studyDirection.getParentStudyCourse() != null) {
      throw new RuntimeException("A study direction must only have one parent!");
    }
    if (this.academicDegree != studyDirection.getAcademicDegree()) {
      throw new RuntimeException(
          "A study direction must have the same academic degree as the corresponding study course!");
    }
    this.studyDirections.add(studyDirection);
    studyDirection.setParentStudyCourse(this);
  }

  public void removeStudyDirection(StudyCourse studyDirection) {
    this.studyDirections.remove(studyDirection);
    studyDirection.setParentStudyCourse(null);
  }

  private void setParentStudyCourse(StudyCourse parentStudyCourse) {
    if (parentStudyCourse != null) {
      if (!this.studyDirections.isEmpty()) {
        throw new RuntimeException("A study direction must not have study directions!");
      }
      if (this.academicDegree != parentStudyCourse.academicDegree) {
        throw new RuntimeException(
            "A study direction must have the same academic degree as the corresponding study course!");
      }
    }
    this.parentStudyCourse = parentStudyCourse;
  }

}
