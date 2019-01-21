package de.thkoeln.projektboerse.moduleservice.hops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HopsModule {

  @JsonProperty("MODULKUERZEL")
  private String kuerzel;

  @JsonProperty("MODULBEZEICHNUNG")
  private String bezeichnung;

  @JsonProperty("NAMEN")
  private String betreuer;

  @JsonProperty("STUDIENGANG")
  private String studiengang;

  @JsonProperty("SEMESTER")
  private String semester;

  @JsonProperty("CREDITS")
  private String credits;

  @JsonProperty("DATEVERSION")
  private String version;

  @JsonProperty("ZEITSTEMPEL")
  private String zeitstempel;

}
