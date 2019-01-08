package de.thkoeln.projektboerse.moduleservice.domain.repository;

import de.thkoeln.projektboerse.moduleservice.domain.aggregate.StudyCourse;
import de.thkoeln.projektboerse.moduleservice.domain.entity.Module;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StudyCourseRepository extends PagingAndSortingRepository<StudyCourse, UUID> {

  List<StudyCourse> findByModulesContaining(Module module);
}
