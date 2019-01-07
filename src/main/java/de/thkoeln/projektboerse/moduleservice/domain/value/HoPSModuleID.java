package de.thkoeln.projektboerse.moduleservice.domain.value;

import java.util.Date;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
@Embeddable
public class HoPSModuleID {

  private Long modulkuerzel;

  private Date dateversion;

  protected HoPSModuleID() {
  }

  public HoPSModuleID(Long modulkuerzel, Date dateversion) {
    this.modulkuerzel = modulkuerzel;
    this.dateversion = dateversion;
  }

}