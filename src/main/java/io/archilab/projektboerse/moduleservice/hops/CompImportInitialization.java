package io.archilab.projektboerse.moduleservice.hops;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;
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
	    	  // test mehrfach import update
	    	  getDatta();
//	    	  getDatta();
//	    	  getDatta();
	      };
	  }
	  
	  private static int loop=1;

	  @Transactional()
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
					  
					e.printStackTrace();
				}
				  
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
		  
		  //  doppelungen entfernen ModStuMappingHOPS aber nur vlt. weil dort nur ide kürzel von intersse sind, nciht die weiteren daten. und der primary key unklar ist.
		  // wenn sich die kürzel nicht ändern können, macht es keinen sinn.
		  
		  
		  {

			  for (StudyCourse sc : studyCourseRepository.findAll()) 
			  {
				  CompImportInitialization.log.info(sc.getName()+" mod co "+sc.getId());
//				  int counter = 0;
//				  for (Module module : sc.getModules()) 
//				  {
//					  counter+=1;
//					  if(module.getName().getName().equals("WPF Mobile IT-Security"))
//					  {
//						  CompImportInitialization.log.info("liked real "+sc.getName().getName());
//					  }
//				  }
//				  CompImportInitialization.log.info(sc.getName()+" mod co "+String.valueOf(counter));
//				  
			  }
			  
		  }
		  
		  
		  // study courses vorbereiten
		  for (StudiengängeHOPS studyCourse : studiengängeHopsGET) 
		  {
			  String kürzel = studyCourse.getSG_KZ();
			  Optional<HopsStudyCourseMapping> scMapping = hopsStudyCourseMappingRepository.findByHopsId(new HopsStudyCourseId(kürzel));
			  AcademicDegree academicDegree = null;
			  StudyCourse newSC = null;
			  
//			  CompImportInitialization.log.info(kürzel);
			  
			  
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
				 // prepare to add new study Course  and save the mapping
				  newSC = new StudyCourse(new StudyCourseName(studyCourse.getSTUDIENGANG()),academicDegree);
				  
				  HopsStudyCourseMapping newStudyCourseMapper = new HopsStudyCourseMapping(new HopsStudyCourseId(kürzel),newSC.getId());
				  hopsStudyCourseMappingRepository.save(newStudyCourseMapper);
				 
			  
			  }
			  else
			  {
				  // get study Course from database
				  Optional<StudyCourse> optSC= studyCourseRepository.findById(scMapping.get().getStudyCourseId());	  
				  newSC=optSC.get();
				  
			  }
			  
			  // fill study course or update
			  newSC.setAcademicDegree(academicDegree);
			  newSC.setName(new StudyCourseName(studyCourse.getSTUDIENGANG()));
//			  newSC.removeAllModules();
			 
			  newSC = studyCourseRepository.save(newSC);	  
		  }

		  // module vorbereiten
		  // leider ist ein praxisprojekt gemapped auf mehrere studiengänge, aber bachelorarbeiten sind es nicht.
		  // um konsistenz zu erreichen wird hier nun das praxixprojekt modul geklont, wenn es mherere studiengänge hat.
		  
	  
		// module einarbeiten
		  CompImportInitialization.log.info("module einarbeiten");
		  
		  {
			  long size= StreamSupport.stream(moduleRepository.findAll().spliterator(), false).count();
			  CompImportInitialization.log.info("Anzahl Module "+String.valueOf(size));
			   
		  }
		  // TODO  output studiengänge anzahl deren module anzahl
		  
		 

//		  for (Module module : moduleRepository.findAll()) 
//		  {
//			  
//			  CompImportInitialization.log.info(module.getName().getName());
//		  }
		  
		  for (ModuleHOPS module : moduleHopsGET) 
		  {
//			  String bezeichnung = module.getMODULBEZEICHNUNG();
//			  
//			  if( !( bezeichnung.equals("Master Thesis (English)") || bezeichnung.equals("Master Thesis and Colloquium (English)") || 
//					  bezeichnung.equals("Masterarbeit") ||   bezeichnung.equals("Masterarbeit und Kolloquium (German)") 
//					  ||  bezeichnung.equals("Bachelorarbeit")  ||  bezeichnung.equals("Kolloquium zur Bachelorarbeit")    
//					  ||  bezeichnung.equals("Bachelor Kolloquium")   ||  bezeichnung.equals("Bachelor Arbeit ")
//					  ||  bezeichnung.equals("Bachelor Arbeit ") Praxisprojekt  Masterarbeit und Kolloquium  ) )
//			  {
//				  continue;
//			  }
			  
			  
			  ArrayList<ModStuMappingHOPS> doppelt = new ArrayList<>();
			  
			  String kürzel = module.getMODULKUERZEL();
			  for (ModStuMappingHOPS mapping : mappingHopsGET) 
			  {
				  // finde das modul
				  if(mapping.getMODULKUERZEL().equals(kürzel) )
				  {
					  doppelt.add(mapping);
				  }
			  }
//			  CompImportInitialization.log.info("anzahl "+String.valueOf(doppelungen));
	  
			  Iterable<StudyCourse> iterable = studyCourseRepository.findAll();

			  List<StudyCourse> studyCourses = new ArrayList<>();
			  iterable.forEach(studyCourses::add);

			  Module newModule = null;
			  
			  List<HopsModuleMapping> moMapping = hopsModuleMappingRepository.findByHopsId(new HopsModuleId(kürzel, module.getDATEVERSION()));
			 
			  // erstmal update existierende module
	
			  for (HopsModuleMapping moMapped : moMapping) 
			  {
				  Optional<Module> optMO= moduleRepository.findById(moMapped.getModuleId());
				  newModule=optMO.get();  
				  fillModule(newModule,module);

				  moduleRepository.save(newModule);
			  }  
			  
			  for (ModStuMappingHOPS doppelEle : doppelt) 
			  {
				  
				  // teste, ob das element bereits existiert
				 Optional<HopsStudyCourseMapping> hopsScMap = hopsStudyCourseMappingRepository.findByHopsId(new HopsStudyCourseId(doppelEle.getSG_KZ()));
				 if(hopsScMap.isPresent())
				 {

					 Optional<StudyCourse> optSc = studyCourseRepository.findById(hopsScMap.get().getStudyCourseId());
					 if(optSc.isPresent())
					 {
		
						 // wenn nein, kreire ein neues modul und speichere es ab
						 boolean moduleMissing = true;
						
						 StudyCourse tempSc = optSc.get();

						 for (Module tempModule : tempSc.getModules()) 
						 {
							 
							 Optional<HopsModuleMapping> tempModuleMapping = hopsModuleMappingRepository.findByModuleId(tempModule.getId());
							 if(tempModuleMapping.isPresent())
							 {

								 
								 if( tempModuleMapping.get().getHopsId().getKuerzel().equals(module.getMODULKUERZEL())
									|| tempModule.getName().getName().equals(module.getMODULBEZEICHNUNG()) )
								 {
									  // es kann sein, dass es mehrere module gibt, mit gleichem namen und gleicher verlinkung zu studiengängen.
									  // dann ist es unmöglich, eines der beiden rauszufiltern sinnvoll. nicht sinnvol unterscheidbar
									  // daher regel: wenn es per name bereits existiert, wird es rausgefiltert alle weiteren.
									  
									  // als beispiel modul: Praxisprojekt
									 
//									CompImportInitialization.log.info("found  "+module.getMODULBEZEICHNUNG());
								 	moduleMissing=false;
								 	break;
								 }
							 }
							 else
							 {
								 CompImportInitialization.log.info("missing map");
							 }
						 }
						 
						 
						 if(moduleMissing==true)
						 {  

							 newModule = createAndFillModule(module);
							 newModule = moduleRepository.save(newModule);
		
							 tempSc.addModule(newModule);
							 hopsModuleMappingRepository.save(new HopsModuleMapping(new HopsModuleId(module.getMODULKUERZEL(), module.getDATEVERSION()), newModule.getId()));
							 // verlinke es mit studiengang
							 tempSc = studyCourseRepository.save(tempSc);
 
						 }
					 }
					 else
					 {
						 CompImportInitialization.log.info("partly Error  study Course should not be missing ");
					 } 
				 }
				 else
				 {
					 CompImportInitialization.log.info("partly Error  study Course mapping should not be missing" +doppelEle.getSG_KZ()+" "+doppelEle.getID());
				 
				 } 
				  
			  }
		  }
		  
		  for (StudyCourse sc : studyCourseRepository.findAll()) 
		  {
			  CompImportInitialization.log.info(String.valueOf(sc.getModules().size()));
				 
		  }
		  int css = 0;
		  for (Module mo : moduleRepository.findAll()) 
		  {
			  css++;
			  
				 
		  }
		  loop+=1;
		  CompImportInitialization.log.info(String.valueOf(css));
		  		  
		  CompImportInitialization.log.info("All Done. Start normal operation.");
		  
		   
			  
			  
			//						  StudyCourse sc = studyCourse.get();
//			  
//			  // es kann sein, dass es mehrere module gibt, mit gleichem namen und gleicher verlinkung zu studiengängen.
//			  // dann ist es unmöglich, eines der beiden rauszufiltern sinnvoll. nicht sinnvol unterscheidbar
//			  // daher regel: wenn es per name bereits existiert, wird es rausgefiltert alle weiteren.
//			  
//			  // als beispiel moddul: Praxisprojekt
//			  boolean istEinfachVorhanden=true;
//			  for(Module modEle : sc.getModules())
//			  {
//				  if(modEle.getName().equals(newModule.getName())) 
//				  {
////					  CompImportInitialization.log.info("doppelung name");
//					  istEinfachVorhanden=false;
//					  break;
//					  
//				  }
//			  }
//			  if(istEinfachVorhanden)
//			  {
//				  CompImportInitialization.log.info("doppelung erkannt");
////				  CompImportInitialization.log.info(module.getMODULKUERZEL()+" "+newModule.getName());
//				  sc.addModule(newModule);
//				  hopsModuleMappingRepository.save(newModuleMapping);
//				  moduleRepository.save(newModule);
//				  studyCourseRepository.save(sc);
//			  }
			  
	

		
		  
		  // praxis projekt einmla veraltlet einmal ohne verbidnug zu studiengang daher nciht importeiren

		  // link studyCourses and modules
		  
		  // here error no session  workaround fetch eager    
		 
//		  Iterable<StudyCourse> iterable = studyCourseRepository.findAll();
//
//		  List<StudyCourse> studyCourses = new ArrayList<>();
//		  iterable.forEach(studyCourses::add);
////		  while (iterable.iterator().hasNext() ) {
////			  StudyCourse studyCourse = (StudyCourse) iterable.iterator().next();
////		  }
//		  
//		  for (StudyCourse studyCourse : studyCourses) 
//		  {
//			  studyCourse = studyCourseRepository.findById(studyCourse.getId()).get();
//			 
//			  studyCourse.getModules().size();
//			  CompImportInitialization.log.info("fill "+studyCourse.getId().toString());
//			  Optional<HopsStudyCourseMapping> mappero = hopsStudyCourseMappingRepository.findByStudyCourseId(studyCourse.getId());
//			  String studyCourseKürzel = mappero.get().getHopsId().getStudy_course_kürzel();
//			  List<UUID> moduelID= new ArrayList<UUID>();
//			  for (ModStuMappingHOPS mapping : mappingHopsGET) 
//			  {
//				  // get alle modulkürzel
//				  String stgaK=mapping.getSG_KZ();
//				  String moK=mapping.getMODULKUERZEL();
//				  if(studyCourseKürzel.equals(stgaK))
//				  {
//					  Iterable<HopsModuleMapping> it2 = hopsModuleMappingRepository.findAll();
//					  List<HopsModuleMapping> modules = new ArrayList<>();
//					  it2.forEach(modules::add);
//					  for (HopsModuleMapping hopsModuleMapping : modules) 
//					  {
//						  if(hopsModuleMapping.getHopsId().getKuerzel().equals(moK))
//						  {
//							  if(moduleRepository.findById(hopsModuleMapping.getModuleId()).get().)
//							  moduelID.add( );
//						  }
//						 
//					  }
//					  
//				  }
//			  }
//			  // fill modules
//			  for (UUID uuid : moduelID) 
//			  {
//				  Optional<Module> moduleO = moduleRepository.findById(uuid);
//				  if(moduleO.isPresent())
//				  {
////					  // to initialsie the collection so it gets loaded  otherwise transaction error
//					  moduleO.get().getId();
//					  studyCourse.getModules().size();
//					  studyCourse.addModule(moduleO.get());
//				  }
//			  }
//			  studyCourseRepository.save(studyCourse);
//			  
//		  }
////		  studyCourseRepository.saveAll(studyCourses);
//	
//		  
		  
	  }
	  
	  private Module createAndFillModule(ModuleHOPS module) 
	  {
		  Module newM = new Module(new ModuleName(""),new ModuleDescription(""));
		  fillModule(newM,module);
		  return newM;
	  }

	private void fillModule(Module newModule, ModuleHOPS module) 
	{
		newModule.setName(new ModuleName (module.getMODULBEZEICHNUNG()));
		newModule.setDescription(new ModuleDescription ( (module.getINHALT()!=null ? module.getINHALT() : "") ));	
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

