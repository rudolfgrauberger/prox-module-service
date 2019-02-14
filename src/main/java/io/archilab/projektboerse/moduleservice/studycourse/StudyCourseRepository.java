package io.archilab.projektboerse.moduleservice.studycourse;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

public interface StudyCourseRepository extends PagingAndSortingRepository<StudyCourse, UUID> {

    Set<StudyCourse> findByAcademicDegree(AcademicDegree academicDegree);
}
