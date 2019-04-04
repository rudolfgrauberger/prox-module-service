package io.archilab.projektboerse.moduleservice.hops;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.archilab.projektboerse.moduleservice.studycourse.AcademicDegree;
import io.archilab.projektboerse.moduleservice.studycourse.Module;
import io.archilab.projektboerse.moduleservice.studycourse.ModuleDescription;
import io.archilab.projektboerse.moduleservice.studycourse.ModuleName;
import io.archilab.projektboerse.moduleservice.studycourse.ModuleRepository;
import io.archilab.projektboerse.moduleservice.studycourse.StudyCourse;
import io.archilab.projektboerse.moduleservice.studycourse.StudyCourseName;
import io.archilab.projektboerse.moduleservice.studycourse.StudyCourseRepository;
import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j

public class CompImportInitialization {
	
	@Autowired
	private HopsApiGet hopsApiGet;
	
	@Autowired
	private HopsModuleMappingRepository hopsModuleMappingRepository;
	
	@Autowired
	private HopsStudyCourseMappingRepository hopsStudyCourseMappingRepository;
	
	@Autowired
	private StudyCourseRepository studyCourseRepository;
	
	@Autowired
	private ModuleRepository moduleRepository;
		
	  @Bean
	  public SmartInitializingSingleton importProcessor() 
	  { 
	      return () -> {
	    	  getDatta();
	      };
	  }

	  
	  private void getDatta()
	  {
		  CompImportInitialization.log.info("Start Data Import HOPS");
		  
	
		  ArrayList<ModuleHOPS> moduleHopsGET = (ArrayList<ModuleHOPS>)importData("MODULE",hopsApiGet::getModules);
		  ArrayList<StudiengängeHOPS> studiengängeHopsGET = (ArrayList<StudiengängeHOPS>)importData("MSTUDIENGANGRICHTUNG",hopsApiGet::getStudiengänge);
		  ArrayList<ModStuMappingHOPS> mappingHopsGET = (ArrayList<ModStuMappingHOPS>)importData("MODULECURRICULUM",hopsApiGet::getModuleCuriculum);
		  
		  CompImportInitialization.log.info("Retrieved all Data from HOPS");

		  CompImportInitialization.log.info("Testausgabe");
		  CompImportInitialization.log.info(moduleHopsGET.get(1).getMODULBEZEICHNUNG());
		  CompImportInitialization.log.info(studiengängeHopsGET.get(1).getABSCHLUSSART());
		  CompImportInitialization.log.info(mappingHopsGET.get(1).getDATEVERSION());

		  CompImportInitialization.log.info("Save and Update");
		  
		  
		// doppleungune entfernen
		  for (int i=0;i< moduleHopsGET.size();i++)
		  {
			  ModuleHOPS module = moduleHopsGET.get(i);
			  String inputDate = module.getDATEVERSION();
			  SimpleDateFormat parser=new SimpleDateFormat("dd.mm.yy");
			  Date date_active = null;
			try {
				date_active = parser.parse(inputDate);
			} catch (ParseException e) {
				CompImportInitialization.log.info("Failed to parse Date");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  boolean isOld=false;
			  for (int k=0;k< moduleHopsGET.size();k++)
			  {
				  ModuleHOPS tempModule = moduleHopsGET.get(k);
				  Date date_other = null;
				try {
					date_other = parser.parse(tempModule.getDATEVERSION());
				} catch (ParseException e) {
					CompImportInitialization.log.info("Failed to parse Date");
					  
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  // TODO
				  if(module.getMODULKUERZEL().equals(tempModule.getMODULKUERZEL()) && date_active.compareTo(date_other)<0)
				  {
					  isOld=true;
					  break;
				  }
			  }
			  if (isOld)
			  {
				  moduleHopsGET.remove(i);
				  i--;
			  }
			  
		  }
		  
		  // TODO  doppelungen entfernen ModStuMappingHOPS
		  
		  // study courses vorbereiten
		  for (StudiengängeHOPS studyCourse : studiengängeHopsGET) 
		  {
			  String kürzel = studyCourse.getSG_KZ();
			  Optional<HopsStudyCourseMapping> scMapping = hopsStudyCourseMappingRepository.findByHopsId(new HopsStudyCourseId(kürzel));
			  AcademicDegree academicDegree = null;
			  StudyCourse newSC = null;
			  
			  CompImportInitialization.log.info(kürzel);
			  
			  
			  if(studyCourse.getABSCHLUSSART().equals("Master"))
			  {
				  academicDegree = AcademicDegree.MASTER;
			  }
			  else if(studyCourse.getABSCHLUSSART().equals("Bachelor"))
			  {
				  academicDegree = AcademicDegree.BACHELOR;
			  }
			  else
			  {
				  CompImportInitialization.log.info(studyCourse.getABSCHLUSSART());
				  academicDegree = AcademicDegree.UNKNOWN;
			  }
			  
			  if(!scMapping.isPresent())
			  {
				 
				  newSC = new StudyCourse(new StudyCourseName(studyCourse.getSTUDIENGANG()),academicDegree);
				  HopsStudyCourseMapping newStudyCourseMapper = new HopsStudyCourseMapping(new HopsStudyCourseId(kürzel),newSC.getId());
				  hopsStudyCourseMappingRepository.save(newStudyCourseMapper);
//				  CompImportInitialization.log.info("neu "+newSC.getId().toString());
				  
				  
			  }
			  else
			  {
				
				  Optional<StudyCourse> optSC= studyCourseRepository.findById(scMapping.get().getStudyCourseId());
				  // woher kommt diese UUID   mytery  uuid  irngedow wird stuidengänge reinge added  was falshc sit.
				  
				  // am ebstne alles droppen und neu import.
				  newSC=optSC.get();
			  }
			  
			  // fill study course or update
			  newSC.setAcademicDegree(academicDegree);
			  newSC.setName(new StudyCourseName(studyCourse.getSTUDIENGANG()));
			  newSC.removeAllModules();
			  CompImportInitialization.log.info("save "+newSC.getId().toString());
			  newSC = studyCourseRepository.save(newSC);
			  
		  }
		  
		  
		  // über zwishcentabelle
		  // find moduels by kürzel
		  // nimm nueste date version
		  // parse nur backelor arbeit praxixprojekt master arbeit  ohne kolloquium
		  // speichern
		  // als echtes klass ienfügen repo
		  
		  
		  
		  
		// module einarbeiten
		  for (ModuleHOPS module : moduleHopsGET) 
		  {
			  String bezeichnung = module.getMODULBEZEICHNUNG();
			  
			  if( !( bezeichnung.equals("Master Thesis (English)") || bezeichnung.equals("Master Thesis and Colloquium (English)") || 
					  bezeichnung.equals("Masterarbeit") ||   bezeichnung.equals("Masterarbeit und Kolloquium (German)") 
					  ||  bezeichnung.equals("Bachelorarbeit")  ||  bezeichnung.equals("Kolloquium zur Bachelorarbeit")    
					  ||  bezeichnung.equals("Bachelor Kolloquium")   ||  bezeichnung.equals("Bachelor Arbeit ")
					  ||  bezeichnung.equals("Bachelor Arbeit ")   ) )
			  {
				  continue;
			  }
			  String kürzel = module.getMODULKUERZEL();
			  // TODO  kein dateversion für id
			  Optional<HopsModuleMapping> moMapping = hopsModuleMappingRepository.findByHopsId(new HopsModuleId(kürzel, module.getDATEVERSION()));
		
			  Module newModule = null;
			  
			  if(!moMapping.isPresent())
			  {
				  
				  newModule = new Module(new ModuleName (module.getMODULBEZEICHNUNG()), new ModuleDescription ( (module.getINHALT()!=null ? module.getINHALT() : "")   ));
				  HopsModuleMapping newModuleMapping = new HopsModuleMapping(new HopsModuleId(kürzel, module.getDATEVERSION()),newModule.getId());
				  hopsModuleMappingRepository.save(newModuleMapping);
			  }
			  else
			  {
				  Optional<Module> optMO= moduleRepository.findById(moMapping.get().getModuleId());
				  newModule=optMO.get();
			  }

			  // fill study course or update
			  newModule.setName(new ModuleName (module.getMODULBEZEICHNUNG()));
			  newModule.setDescription(new ModuleDescription ( (module.getINHALT()!=null ? module.getINHALT() : "") ));
			  newModule = moduleRepository.save(newModule);
			 
		  }
		  
		  
		  // link studyCourses and modules
		  Iterable<StudyCourse> iterable = studyCourseRepository.findAll();
		  List<StudyCourse> studyCourses = new ArrayList<>();
		  iterable.forEach(studyCourses::add);
		  for (StudyCourse studyCourse : studyCourses) 
		  {
			  CompImportInitialization.log.info("fill "+studyCourse.getId().toString());
			  Optional<HopsStudyCourseMapping> mappero = hopsStudyCourseMappingRepository.findByStudyCourseId(studyCourse.getId());
			  String studyCourseKürzel = mappero.get().getHopsId().getStudy_course_kürzel();
			  List<UUID> moduelID= new ArrayList<UUID>();
			  for (ModStuMappingHOPS mapping : mappingHopsGET) 
			  {
				  // get alle modulkürzel
				  String stgaK=mapping.getSG_KZ();
				  String moK=mapping.getMODULKUERZEL();
				  if(studyCourseKürzel.equals(stgaK))
				  {
					  Iterable<HopsModuleMapping> it2 = hopsModuleMappingRepository.findAll();
					  List<HopsModuleMapping> modules = new ArrayList<>();
					  it2.forEach(modules::add);
					  for (HopsModuleMapping hopsModuleMapping : modules) 
					  {
						  if(hopsModuleMapping.getHopsId().getKuerzel().equals(moK))
						  {
							  moduelID.add( hopsModuleMapping.getModuleId());
						  }
						 
					  }
					  
				  }
			  }
			  // fill modules
			  for (UUID uuid : moduelID) 
			  {
				  Optional<Module> moduleO = moduleRepository.findById(uuid);
				  if(moduleO.isPresent())
				  {
//					  // to initialsie the collection so it gets loaded  otherwise transaction error
//					  moduleO.get().getId();
//					  studyCourse.getModules().size();
					  studyCourse.addModule(moduleO.get());
				  }
			  }
			  
		  }
		  studyCourseRepository.saveAll(studyCourses);
	
		  
		  CompImportInitialization.log.info("All Done. Start normal operation.");
		  
		    
	  }
	  
	  private ArrayList<?> importData( String type, Supplier<ArrayList> getRequest)
	  {
		  ArrayList<?> dataToImport = null;
		  try 
		  {
			  CompImportInitialization.log.info("Import "+type);
			  dataToImport = getRequest.get();
			  if (dataToImport == null)
			  {
				  CompImportInitialization.log.info("Failed to import variable is null");
				  throw new Exception("Failed to import variable is null");
			  }
			  CompImportInitialization.log.info("Import from HOPS API successfully done!");
		  }
		  catch(Exception e)
		  {
			  CompImportInitialization.log.info("Failed to import "+type);
			  CompImportInitialization.log.info("Import "+type+" from local file");
			  TypeReference<List<?>> typeReference = new TypeReference<List<?>>() {};
			  InputStream inputStream = TypeReference.class.getResourceAsStream("/data/"+type+".json");
			      try {
			    	ObjectMapper objectMapper = new ObjectMapper();

			    	dataToImport = objectMapper.readValue(inputStream, typeReference);
			    	CompImportInitialization.log.info("Import from file successfully done!");
			      }
			      catch(Exception exx)
			      {
			    	  CompImportInitialization.log.info("Failed to import "+type+" from file");
			    	  exx.printStackTrace();
			      }
		  }
		  
		  return dataToImport;
	  }

}

