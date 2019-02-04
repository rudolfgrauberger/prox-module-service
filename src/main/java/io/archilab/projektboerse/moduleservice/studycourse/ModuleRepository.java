package io.archilab.projektboerse.moduleservice.studycourse;

import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
public interface ModuleRepository extends PagingAndSortingRepository<Module, UUID> {

}
