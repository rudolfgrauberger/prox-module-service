package de.thkoeln.projektboerse.moduleservice.domain.entity;

import de.thkoeln.projektboerse.moduleservice.domain.value.ExaminationRegulationsVersion;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "EXAMINATIONREGULATIONS")
@Data
public class ExaminationRegulations {

  private ExaminationRegulationsVersion version;
  private Date effectiveDate;

  protected ExaminationRegulations() {
  }

  public ExaminationRegulations(ExaminationRegulationsVersion version, Date effectiveDate) {
    this.version = version;
    this.effectiveDate = effectiveDate;
  }
}
