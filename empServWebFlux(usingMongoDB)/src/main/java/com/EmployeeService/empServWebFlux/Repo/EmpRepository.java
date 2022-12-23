package com.EmployeeService.empServWebFlux.Repo;

import com.EmployeeService.empServWebFlux.Entity.EmployeeMain;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EmpRepository extends ReactiveMongoRepository<EmployeeMain, Integer> {

    @Query(value ="emp_name" )
    Flux<EmployeeMain> findByEmpName(String emp_name);

    Flux<EmployeeMain> findByEmployeeId(Integer emp_id);

    Mono<Boolean> existsByEmployeeId(Integer emp_id);
}
