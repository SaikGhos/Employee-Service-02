package com.EmployeeService.empServWebFlux.Repo;

import com.EmployeeService.empServWebFlux.Entity.EmpSkillSet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EmpSkillSetRepository extends ReactiveMongoRepository<EmpSkillSet, Integer> {


    Mono<EmpSkillSet> findByemployeeId(Integer emp_id);

    Flux<EmpSkillSet> findByJavaExperienceGreaterThan(double java_exp);

    Flux<EmpSkillSet> findBySpringExperienceGreaterThan(double java_exp);

}
