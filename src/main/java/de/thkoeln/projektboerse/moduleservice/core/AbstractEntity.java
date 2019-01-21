package de.thkoeln.projektboerse.moduleservice.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@MappedSuperclass
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEntity {

  @Id
  @GeneratedValue
  @JsonIgnore
  private UUID id;

}
