package org.example.redis.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    private String id;
    private String firstName;
    private String lastName;
    private int age;
    private String grade;
    private String department;

}
