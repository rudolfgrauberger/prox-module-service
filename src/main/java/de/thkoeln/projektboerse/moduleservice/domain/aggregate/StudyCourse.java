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
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Table(name = "STUDYCOURSES")
@Data
@Setter(AccessLevel.NONE)
public class StudyCourse {

  @Id
  @JsonIgnore
  private UUID id;

  @JsonUnwrapped
  private StudyCourseName name;

  private AcademicDegree academicDegree;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private Set<Module> modules = new HashSet<>();

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

  public Set<Module> getModules() {
    return Collections.unmodifiableSet(modules);
  }

  public Boolean addModule(Module module) {
    return modules.add(module);
  }

  public Boolean removeModule(Module module) {
    return modules.remove(module);
  }

}
