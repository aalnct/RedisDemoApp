package org.example.redis;

import org.example.redis.bean.Student;
import org.example.redis.service.RedisService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class RedisConnectionTest implements CommandLineRunner {


    private final RedisService redisService;

    public RedisConnectionTest (RedisService redisService) {
        this.redisService = redisService;
    }
    @Override
    public void run(String... args) throws Exception {
        try {


            redisService.saveValue("test:key", "Redis Connection Done");
            Object result = redisService.getValue("test:key");
            System.out.println(" Result Object :: " + result);

            System.out.println(" Working with hash operations ");

            String hashKey = "test:user1001";
            redisService.saveHash(hashKey, "name" , "John Doe");
            redisService.saveHash(hashKey, "email" , "johndoe@gmail.com");
            redisService.saveHash(hashKey, "age", 30);
            System.out.println("Retrieving hash values");
            System.out.println( "Hash Field Name :: " + redisService.getHashValue(hashKey, "name"));
            System.out.println(" Hash Field email :: " + redisService.getHashValue(hashKey,"email"));
            System.out.println(" Hash Field age :: " + redisService.getHashValue(hashKey,"age"));

            System.out.println(" Testing storing multiple keys at once ");
            String productKey = "product:2001";
            Map<String,Object> productData = new HashMap<>();
            productData.put("name", "premium headphones");
            productData.put("price", 130);
            productData.put("inStock", true);
            productData.put("categories", Arrays.asList("electronics", "audio"));

            redisService.saveMultipleHashValues(productKey, productData);

            System.out.println("All product date " + redisService.getAllValues(productKey));

            System.out.println(" Testing redis connection is completed ");

            System.out.println( "******* testing Set Operations ******** " );

            Student student1 = new Student("S1001", "John", "Doe", 19, "A", "Computer Science");
            Student student2 = new Student("S1002", "Jane", "Smith", 20, "A", "Computer Science");
            Student student3 = new Student("S1003", "Michael", "Johnson", 19, "B", "Mathematics");
            Student student4 = new Student("S1004", "Emily", "Brown", 21, "A", "Mathematics");

            this.redisService.saveStudents(student1);
            this.redisService.saveStudents(student2);
            this.redisService.saveStudents(student3);
            this.redisService.saveStudents(student4);

            System.out.println(" Four students saved in database ");
            Student retrieveStudents = redisService.getStudent("S1001");
            System.out.println("Student Retrieved");
            System.out.println(retrieveStudents);

            // find student by department
            System.out.println("  Students in computer science department ");
            Set<Student> csStudent = this.redisService.findStudentsByDepartment("Computer Science");

            for (Student s : csStudent) {
                System.out.println( " Student Information ::" + s);
            }


        }catch (Exception exception) {
            exception.printStackTrace();;
        }
    }
}
