package de.thkoeln.projektboerse.moduleservice.domain.repository;

import de.thkoeln.projektboerse.moduleservice.domain.aggregate.StudyCourse;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StudyCourseRepository extends PagingAndSortingRepository<StudyCourse, UUID> {

}
