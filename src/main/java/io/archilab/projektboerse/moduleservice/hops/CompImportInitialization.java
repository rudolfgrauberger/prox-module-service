package io.archilab.projektboerse.moduleservice.hops;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
			//	    	  getDatta();

		};
	}

	@Transactional()
	private void getDatta()
	{
		// TODO  error no session transaction   workaround fetch eager instead of lazy
		CompImportInitialization.log.info("Start Data Import HOPS");

		ArrayList<ModuleHOPS> moduleHopsGET = (ArrayList<ModuleHOPS>)importData("MODULE",hopsApiGet::getModules);
		ArrayList<StudiengängeHOPS> studiengängeHopsGET = (ArrayList<StudiengängeHOPS>)importData("MSTUDIENGANGRICHTUNG",hopsApiGet::getStudiengänge);
		ArrayList<ModStuMappingHOPS> mappingHopsGET = (ArrayList<ModStuMappingHOPS>)importData("MODULECURRICULUM",hopsApiGet::getModuleCuriculum);

		CompImportInitialization.log.info("Retrieved all Data from HOPS");

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


		// study courses vorbereiten
		for (StudiengängeHOPS studyCourse : studiengängeHopsGET) 
		{
			String kürzel = studyCourse.getSG_KZ();
			Optional<HopsStudyCourseMapping> scMapping = hopsStudyCourseMappingRepository.findByHopsId(new HopsStudyCourseId(kürzel));
			AcademicDegree academicDegree = null;
			StudyCourse newSC = null;

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

			newSC = studyCourseRepository.save(newSC);	  
		}

		// module einarbeiten
		CompImportInitialization.log.info("module einarbeiten");


		for (ModuleHOPS module : moduleHopsGET) 
		{

			// Potentieller Filter
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
									// Leider ist ein Praxisprojekt gemapped auf mehrere studiengänge, aber bachelorarbeiten sind es nicht.
									// um konsistenz zu erreichen, wird hier nun das Praxixprojekt modul geklont, wenn es mehrere Studiengänge hat.

									// es kann sein, dass es mehrere module gibt, mit gleichem namen und gleicher verlinkung zu studiengängen.
									// dann ist es unmöglich, eines der beiden rauszufiltern sinnvoll. nicht sinnvol unterscheidbar
									// daher regel: wenn es per name bereits existiert, wird es rausgefiltert alle weiteren.

									// als beispiel modul: Praxisprojekt

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
					CompImportInitialization.log.info("partly Error  study Course mapping should not be missing, maybe Module not in use " +doppelEle.getSG_KZ()+" "+doppelEle.getID());
				} 

			}
		}

		// Status Info über den Import
		CompImportInitialization.log.info("Status Info");
		{
			long size= StreamSupport.stream(moduleRepository.findAll().spliterator(), false).count();
			CompImportInitialization.log.info("Anzahl Module: "+String.valueOf(size));  
		}

		{
			long size= StreamSupport.stream(studyCourseRepository.findAll().spliterator(), false).count();
			CompImportInitialization.log.info("Anzahl Studiengänge: "+String.valueOf(size));  
		}
		CompImportInitialization.log.info("Alle Studiengänge und Anzahl verlinkter Module");
		{
			for (StudyCourse sc : studyCourseRepository.findAll()) 
			{
				CompImportInitialization.log.info("Name:   "+sc.getName());
				CompImportInitialization.log.info("Module: "+String.valueOf(sc.getModules().size()));
			}
		}

		CompImportInitialization.log.info("All Done. Start normal operation.");
	}

	// Helper Funktion: erstellt neues Modul und füllt es mit daten aus dem Hops Modul
	private Module createAndFillModule(ModuleHOPS module) 
	{
		Module newM = new Module(new ModuleName(""),new ModuleDescription(""));
		fillModule(newM,module);
		return newM;
	}

	// Helper Funktion: füllt ein Modul mit daten aus dem Hops Modul
	private void fillModule(Module newModule, ModuleHOPS module) 
	{
		newModule.setName(new ModuleName (module.getMODULBEZEICHNUNG()));
		newModule.setDescription(new ModuleDescription ( (module.getINHALT()!=null ? module.getINHALT() : "") ));	
	}

	// Hier findet der Import statt, erst wird versucht, aus dem Hops zu importieren, danach wird versucht, aus den json dateien zu importieren
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

