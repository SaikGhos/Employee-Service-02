package com.EmployeeService.empServWebFlux.Repo;

import com.EmployeeService.empServWebFlux.Entity.EmployeeMain;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface EmpDtoRepository extends ReactiveMongoRepository<EmployeeMain,Integer> {

    Mono<EmployeeMain> findByEmployeeId(int jobId);

}
