package com.EmployeeService.empServWebFlux.Service;

import com.EmployeeService.empServWebFlux.Constant.ApplicationConstant;
import com.EmployeeService.empServWebFlux.Entity.EmpRequest;
import com.EmployeeService.empServWebFlux.Entity.EmpResponse;
import com.EmployeeService.empServWebFlux.Entity.EmpSkillSet;
import com.EmployeeService.empServWebFlux.Entity.EmployeeMain;
import com.EmployeeService.empServWebFlux.Repo.EmpDtoRepository;
import com.EmployeeService.empServWebFlux.Repo.EmpRepository;

import com.EmployeeService.empServWebFlux.Repo.EmpSkillSetRepository;
import com.hazelcast.map.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class EmpService {

    @Autowired
    private EmpRepository empRepository;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private EmpSkillSetRepository empSkillSetRepository;

    @Autowired
    private EmpDtoRepository empDtoRepository;
    @Autowired
    private IMap<Integer, EmployeeMain> userCache;

    Logger log = LoggerFactory.getLogger(EmpService.class);

    public Mono<EmpResponse> createEmployeeMain(EmpRequest employeeRequest) {

        log.debug("inside create Employee" + employeeRequest.getEmp_id());

        String status = "";

        kafkaTemplate.send(ApplicationConstant.TOPIC,employeeRequest);

        EmployeeMain employee = new EmployeeMain(employeeRequest.getEmp_id(), employeeRequest.getEmp_name(),
                employeeRequest.getEmp_city(), employeeRequest.getEmp_phone());

        EmpSkillSet employeeSkillSet = new EmpSkillSet(employeeRequest.getEmp_id(), employeeRequest.getJava_exp(),
                employeeRequest.getSpring_exp());

        return empRepository.existsByEmployeeId(employeeRequest.getEmp_id())
                .flatMap(exists -> {
                    if (exists) {
                        log.debug("Employee already exists");
                        return Mono.zip(
                                Mono.just(employee), Mono.just(employeeSkillSet)
                        ).map(t -> new EmpResponse(t.getT1().getEmployeeId(),
                                t.getT1().getEmployeeName(), t.getT1().getEmployeeCity(), t.getT1().getEmployeePhone(),
                                t.getT2().getJavaExperience(), t.getT2().getSpringExperience(), "Already Exists"));
                    } else {
                        log.debug("Employee Create new");
                        return Mono.zip(Mono.just(employee)
                                                .flatMap(empRepository::save).log("Employee object")
                                        , Mono.just(employeeSkillSet).flatMap(empSkillSetRepository::save).log("Employee skill set object"))
                                .map(t -> new EmpResponse(t.getT1().getEmployeeId(),
                                        t.getT1().getEmployeeName(), t.getT1().getEmployeeCity(), t.getT1().getEmployeePhone(),
                                        t.getT2().getJavaExperience(), t.getT2().getSpringExperience(), "Created"));

                    }

                });

    }

    public Mono<EmpResponse> updateEmployeeMain(EmpRequest employeeRequest) {

        EmployeeMain employee = new EmployeeMain(employeeRequest.getEmp_id(), employeeRequest.getEmp_name(),
                employeeRequest.getEmp_city(), employeeRequest.getEmp_phone());

        EmpSkillSet employeeSkillSet = new EmpSkillSet(employeeRequest.getEmp_id(), employeeRequest.getJava_exp(),
                employeeRequest.getSpring_exp());

        return Mono.zip(Mono.just(employee).flatMap(empRepository::save).log("Employee object")
                        , Mono.just(employeeSkillSet).flatMap(empSkillSetRepository::save).log("Employee skill set object"))
                .map(t -> new EmpResponse(t.getT1().getEmployeeId(),
                t.getT1().getEmployeeName(), t.getT1().getEmployeeCity(), t.getT1().getEmployeePhone(),
                t.getT2().getJavaExperience(), t.getT2().getSpringExperience(), "Employee updated"));
    }

    public Flux<EmpRequest> findByJavaExp(double java_exp){


        Flux<EmpSkillSet> skillSetFlux = empSkillSetRepository.findByJavaExperienceGreaterThan(java_exp);
        Flux<EmployeeMain> employeeFlux = skillSetFlux.concatMap(
                emp-> empRepository.findByEmployeeId(emp.getEmployeeId())
        );
        
        Flux<EmpRequest> empRequestFlux = Flux.zip(employeeFlux,skillSetFlux).map(t-> new EmpRequest(t.getT1().getEmployeeId(),
                t.getT1().getEmployeeName(),t.getT1().getEmployeeCity(),
                t.getT1().getEmployeePhone(),t.getT2().getJavaExperience(),
                t.getT2().getSpringExperience()));

        return empRequestFlux;
    }

    public Flux<EmpRequest> findBySpringExp(double spring_exp) {
        Flux<EmpSkillSet> skillSetFlux = empSkillSetRepository.findBySpringExperienceGreaterThan(spring_exp);
        Flux<EmployeeMain> employeeFlux = skillSetFlux.concatMap(
                emp-> empRepository.findByEmployeeId(emp.getEmployeeId())
        );

        Flux<EmpRequest> empRequestFlux = Flux.zip(employeeFlux,skillSetFlux).map(t-> new EmpRequest(t.getT1().getEmployeeId(),
                t.getT1().getEmployeeName(),t.getT1().getEmployeeCity(),
                t.getT1().getEmployeePhone(),t.getT2().getJavaExperience(),
                t.getT2().getSpringExperience()));

        return empRequestFlux;

    }

    public Flux<EmpRequest> findByBothExp(double javaExp, double springExp) {

        Flux<EmpSkillSet> skillSetFlux = empSkillSetRepository.findByJavaExperienceGreaterThan(javaExp)
                .filter(s -> s.getSpringExperience()> springExp);

        Flux<EmployeeMain> employeeFlux = skillSetFlux.concatMap(
                emp-> empRepository.findByEmployeeId(emp.getEmployeeId())
        );

        Flux<EmpRequest> empRequestFlux = Flux.zip(employeeFlux,skillSetFlux).map(t-> new EmpRequest(t.getT1().getEmployeeId(),
                t.getT1().getEmployeeName(),t.getT1().getEmployeeCity(),
                t.getT1().getEmployeePhone(),t.getT2().getJavaExperience(),
                t.getT2().getSpringExperience()))
                ;

        return empRequestFlux;
    }

    public Mono<EmployeeMain> getEmployeeFromCache(int id) {

        Mono<EmployeeMain> result = getUserFromCache(id);
        return   result
                .switchIfEmpty(getUserFromDB(id))
                .flatMap(user -> saveUserToCache(user));
    }

    private Mono<EmployeeMain> getUserFromCache(int id) {
        return Mono.fromCompletionStage(userCache.getAsync(id));
    }
    private Mono<? extends EmployeeMain> saveUserToCache(EmployeeMain user) {
        userCache.setAsync(user.getEmployeeId(), user);
        return Mono.just(user);
    }
    private Mono<EmployeeMain> getUserFromDB(int id) {
        return empDtoRepository.findByEmployeeId(id);
    }



}
