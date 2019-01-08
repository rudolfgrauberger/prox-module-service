package de.thkoeln.projektboerse.moduleservice.domain.value;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.ToString;

@Embeddable
@Getter
@ToString
public class ModuleDescription {

  private static final int MAX_LENGTH = 3000;

  @Column(length = 3000)
  private String description;

  protected ModuleDescription() {
  }

  public ModuleDescription(String description) {
    if (!isValid(description)) {
      throw new IllegalArgumentException(String
          .format("Name %s exceeded maximum number of %d allowed characters", description,
              MAX_LENGTH));
    }
    this.description = description;
  }

  public static boolean isValid(String name) {
    return name == null ? false : name.length() <= MAX_LENGTH;
  }

}
