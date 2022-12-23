package com.EmployeeService.empServWebFlux.Controllers;

import com.EmployeeService.empServWebFlux.Entity.EmpRequest;
import com.EmployeeService.empServWebFlux.Entity.EmpResponse;
import com.EmployeeService.empServWebFlux.Entity.EmployeeMain;
import com.EmployeeService.empServWebFlux.Service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class EmpMainController {


    @Autowired
    private EmpService empService;

    @PostMapping("/createEmployee")
    Mono<EmpResponse> createEmployeeMain(@RequestBody @Valid EmpRequest employeeRequest){
        return empService.createEmployeeMain(employeeRequest);
    }
    @PutMapping("/updateEmployee/{empId}")
    Mono<EmpResponse> updateEmployeeMain(@RequestBody @Valid EmpRequest employeeRequest){
        return empService.updateEmployeeMain(employeeRequest);
    }

    @GetMapping("/findByJavaExp")
    Flux<EmpRequest> findAllJavaGreater(@RequestParam double exp){
        return empService.findByJavaExp(exp);
    }

    /*@GetMapping("/SpringExpGreaterThan/{exp}")
    Flux<EmpRequest> findAllSpringGreater(@PathVariable double exp){
        return empService.findBySpringExp(exp);
    }*/

    @GetMapping("/getEmployeeFromCache/{id}")
    Mono<EmployeeMain> getEmployeeFromCache(@PathVariable int id){
        return empService.getEmployeeFromCache(id);
    }

    @GetMapping("/findEmpSkillSet")
    Flux<EmpRequest> findEmpSkillSet(@RequestParam(defaultValue = "-1") double javaExp,
                                       @RequestParam(defaultValue = "-1") double springExp){
        if(javaExp == -1){
            return empService.findBySpringExp(springExp);
        } else if (springExp == -1) {
            return empService.findByJavaExp(javaExp);
        } else {
            return empService.findByBothExp(javaExp, springExp);
        }
    }
}