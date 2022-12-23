package com.EmployeeService.empServWebFlux.Entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "emp")
public class EmployeeMain {

    @Id
    private int employeeId;
    private String employeeName;
    private String employeeCity;
    private String employeePhone;

}
