package de.thkoeln.projektboerse.moduleservice.studycourse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString
public class Module {

  @Id
  @GeneratedValue
  @JsonIgnore
  private UUID id;

  @JsonUnwrapped
  private ModuleName name;

  @JsonUnwrapped
  private ModuleDescription description;

  protected Module() {
  }

  public Module(ModuleName name, ModuleDescription description) {
    this.name = name;
    this.description = description;
  }

}