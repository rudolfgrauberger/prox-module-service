package de.thkoeln.projektboerse.moduleservice.hops;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HopsStudyCourseId {

  private String kuerzel;

  public HopsStudyCourseId(String kuerzel) {
    this.kuerzel = kuerzel;
  }

}
