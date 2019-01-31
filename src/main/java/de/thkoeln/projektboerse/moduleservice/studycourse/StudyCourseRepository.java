package de.thkoeln.projektboerse.moduleservice.studycourse;

import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
public interface StudyCourseRepository extends PagingAndSortingRepository<StudyCourse, UUID> {

}
