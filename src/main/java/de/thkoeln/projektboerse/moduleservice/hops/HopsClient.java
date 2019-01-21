package de.thkoeln.projektboerse.moduleservice.hops;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "hops", url = "https://fhpwww.gm.fh-koeln.de/hops/api/module/api.php")
public interface HopsClient {

  @GetMapping("?mode=courses")
  List<HopsStudyCourse> getHopsStudyCourses();

  @GetMapping("?mode=list")
  List<HopsModule> getHopsModules();

}
