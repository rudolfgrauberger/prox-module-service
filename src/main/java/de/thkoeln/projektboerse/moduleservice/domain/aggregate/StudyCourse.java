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
import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "STUDYCOURSES")
@Data
@Setter(AccessLevel.NONE)
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

  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "parentStudyCourse", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<StudyCourse> studyDirections = new HashSet<>();

  @ManyToOne
  private StudyCourse parentStudyCourse;

  protected StudyCourse() {}

  public Set<StudyCourse> getStudyDirections() { return Collections.unmodifiableSet(studyDirections); }

  public Set<Module> getModules() { return Collections.unmodifiableSet(modules); }

  public Boolean addModule(Module module) {
    return modules.add(module);
  }

  public Boolean removeModule(Module module) { return modules.remove(module); }

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