package io.archilab.projektboerse.moduleservice.studycourse;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.archilab.projektboerse.moduleservice.core.AbstractEntity;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Module extends AbstractEntity {

  @Setter
  @JsonUnwrapped
  private ModuleName name;

  @Setter
  @JsonUnwrapped
  private ModuleDescription description;

  public Module(ModuleName name, ModuleDescription description) {
    this.name = name;
    this.description = description;
  }

}
