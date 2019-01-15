package de.thkoeln.projektboerse.moduleservice.studycourse;

import de.thkoeln.projektboerse.moduleservice.hops.HopsStudyCourseId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StudyCourseRepository extends PagingAndSortingRepository<StudyCourse, UUID> {

  Optional<StudyCourse> findByHopsId(HopsStudyCourseId hopsId);

}
