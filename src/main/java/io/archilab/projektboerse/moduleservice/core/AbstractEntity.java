package io.archilab.projektboerse.moduleservice.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@MappedSuperclass
@Data
@Setter(AccessLevel.NONE)
public abstract class AbstractEntity {

  @Id
  @JsonIgnore
  private UUID id;

  protected AbstractEntity() {
    this.id = UUID.randomUUID();
  }

}
