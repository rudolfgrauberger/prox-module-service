package de.thkoeln.projektboerse.moduleservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.thkoeln.projektboerse.moduleservice.domain.value.HoPSModuleID;
import de.thkoeln.projektboerse.moduleservice.domain.value.ModuleName;
import java.util.UUID;
import javax.persistence.Entity;
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
  @JsonIgnore
  private UUID id;

  @JsonIgnore
  private HoPSModuleID hoPSModuleID;

  @JsonUnwrapped
  private ModuleName name;

  protected Module() {
  }

  public Module(ModuleName name, HoPSModuleID hoPSModuleID) {
    this(UUID.randomUUID(), name, hoPSModuleID);
  }

  public Module(UUID id, ModuleName name, HoPSModuleID hoPSModuleID) {
    this.id = id;
    this.name = name;
    this.hoPSModuleID = hoPSModuleID;
  }
}
