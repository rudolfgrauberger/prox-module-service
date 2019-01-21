package de.thkoeln.projektboerse.moduleservice.hops;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface StudyCourseMappingRepository extends
    PagingAndSortingRepository<StudyCourseMapping, UUID> {

  Optional<StudyCourseMapping> findByHopsId(HopsStudyCourseId hopsId);

}
