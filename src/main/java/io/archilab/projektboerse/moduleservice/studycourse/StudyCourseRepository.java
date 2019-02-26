package io.archilab.projektboerse.moduleservice.studycourse;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StudyCourseRepository extends PagingAndSortingRepository<StudyCourse, UUID> {

  Set<StudyCourse> findByAcademicDegree(AcademicDegree academicDegree);

}
