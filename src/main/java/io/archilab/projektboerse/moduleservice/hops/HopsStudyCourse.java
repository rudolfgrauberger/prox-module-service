package io.archilab.projektboerse.moduleservice.hops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HopsStudyCourse {

  @JsonProperty("SGKZ")
  private String kuerzel;

  @JsonProperty("BEZEICHNUNG")
  private String bezeichnung;

}
