package io.archilab.projektboerse.moduleservice.hops;

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

  private String bezeichnung;

  public HopsStudyCourseId(String bezeichnung) {
    this.bezeichnung = bezeichnung;
  }

}
