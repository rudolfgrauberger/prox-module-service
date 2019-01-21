package de.thkoeln.projektboerse.moduleservice.studycourse;

import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ModuleRepository extends PagingAndSortingRepository<Module, UUID> {

}
