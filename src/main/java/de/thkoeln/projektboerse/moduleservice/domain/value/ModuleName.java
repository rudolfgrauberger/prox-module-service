package de.thkoeln.projektboerse.moduleservice.domain.value;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
@Embeddable
public class ModuleName {

  private static final int MAX_LENGTH = 255;

  private String name;

  protected ModuleName() {
  }

  public ModuleName(String name) {
    if (!isValid(name)) {
      throw new IllegalArgumentException(String
          .format("Name %s exceeded maximum number of %d allowed characters", name, MAX_LENGTH));
    }
    this.name = name;
  }

  public static boolean isValid(String name) {
    return name == null ? false : name.length() <= MAX_LENGTH;
  }

}
