package com.EmployeeService.empServWebFlux.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "emp_skill")
public class EmpSkillSet {

    @Id
    private int employeeId;
    private double javaExperience;
    private double springExperience;

}
