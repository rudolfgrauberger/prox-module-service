package de.thkoeln.projektboerse.moduleservice.hops;

import de.thkoeln.projektboerse.moduleservice.core.AbstractEntity;
import java.util.UUID;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HopsModuleMapping extends AbstractEntity {

  private HopsModuleId hopsId;

  private UUID moduleId;

  public HopsModuleMapping(HopsModuleId hopsId, UUID moduleId) {
    this.hopsId = hopsId;
    this.moduleId = moduleId;
  }

}
