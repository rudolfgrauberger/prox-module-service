package de.thkoeln.projektboerse.moduleservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.thkoeln.projektboerse.moduleservice.domain.value.ModuleName;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Table(name = "MODULES")
@Data
@Setter(AccessLevel.NONE)
public class Module {

  @Id
  @GeneratedValue
  @JsonIgnore
  private UUID id;

  @JsonUnwrapped
  private ModuleName name;

  protected Module() {
  }

  public Module(ModuleName name) {
    this.name = name;
  }

}
