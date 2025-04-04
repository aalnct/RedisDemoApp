package org.example.redis.service;

import org.example.redis.bean.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class RedisService {
    private final RedisTemplate<String,Object> redisTemplate;
    private final HashOperations<String,String,Object> hashOperations;

    private final SetOperations<String,Object> setOperations;

    // Key prefixes for better organization
    private static final String STUDENT_KEY_PREFIX = "student:";
    private static final String DEPARTMENT_INDEX_PREFIX = "index:department:";
    private static final String GRADE_INDEX_PREFIX = "index:grade:";

    @Autowired
    public RedisService (RedisTemplate<String,Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
        this.setOperations = this.redisTemplate.opsForSet();
    }

    public void saveStudents(Student student) {
        String studentKey = STUDENT_KEY_PREFIX + student.getId();
        Map<String,Object> studentMap = new HashMap<>();

        studentMap.put("id", student.getId());
        studentMap.put("firstName", student.getFirstName());
        studentMap.put("lastName", student.getLastName());
        studentMap.put("age", student.getAge());
        studentMap.put("grade", student.getGrade());
        studentMap.put("department", student.getDepartment());

        this.hashOperations.putAll(studentKey,studentMap);

        // department index
        String departmentKey = DEPARTMENT_INDEX_PREFIX + student.getDepartment();
        this.setOperations.add(departmentKey,studentKey);

        // grade index
        String gradeIndexKey = GRADE_INDEX_PREFIX + student.getGrade();
        this.setOperations.add(gradeIndexKey,studentKey);
    }

    public Student getStudent (String studentId) {
        String studentKey = STUDENT_KEY_PREFIX + studentId;
        Map<String ,Object> studentMap = this.hashOperations.entries(studentKey);

        if (studentMap.isEmpty()) {
            return null;
        }
        // convert map back to student
        Student student = new Student();

        student.setId((String) studentMap.get("id"));
        student.setFirstName((String) studentMap.get("firstName"));
        student.setLastName((String) studentMap.get("lastName"));
        student.setAge(((Number) studentMap.get("age")).intValue()); // Handle potential Long conversion
        student.setGrade((String) studentMap.get("grade"));
        student.setDepartment((String) studentMap.get("department"));

        return student;
    }

    // find students by department

    public Set<Student> findStudentsByDepartment (String department) {
        String departmentIndexKey = DEPARTMENT_INDEX_PREFIX + department;
        Set<Object> studentKeys = setOperations.members(departmentIndexKey);

        Set<Student> students = new HashSet<>();
        for (Object key : studentKeys) {
            String studentKey = (String) key;
            Map<String,Object> studentMap = hashOperations.entries(studentKey);

            Student student = new Student();
            student.setId((String) studentMap.get("id"));
            student.setFirstName((String) studentMap.get("firstName"));
            student.setLastName((String) studentMap.get("lastName"));
            student.setAge(((Number) studentMap.get("age")).intValue());
            student.setGrade((String) studentMap.get("grade"));
            student.setDepartment((String) studentMap.get("department"));

            students.add(student);
        }
        return students;
    }

    public Set<Student> findStudentsByGrades (String grade) {
        String gradeIndexKey = GRADE_INDEX_PREFIX + grade;
        Set<Object> studentsKey = setOperations.members(gradeIndexKey);

        Set<Student> students = new HashSet<>();

        for (Object key : studentsKey) {
            String studentKey = (String) key;
            Map<String,Object> studentMap = this.hashOperations.entries(studentKey);

            Student student = new Student();
            student.setId((String) studentMap.get("id"));
            student.setFirstName((String) studentMap.get("firstName"));
            student.setLastName((String) studentMap.get("lastName"));
            student.setAge(((Number) studentMap.get("age")).intValue());
            student.setGrade((String) studentMap.get("grade"));
            student.setDepartment((String) studentMap.get("department"));

            students.add(student);
        }
        return students;
    }


    public void saveValue(String key, Object value) {
        if (!hasKey(key)) {
            System.out.println(" Value does not exits, saving it");
            redisTemplate.opsForValue().set(key,value);
        }

        System.out.println(" Value exists , no need to save ");
    }

    public Object getValue(String key) {

        return redisTemplate.opsForValue().get(key);
    }

    public boolean hasKey (String key) {
        return this.redisTemplate.hasKey(key);
    }

    // save a single field
    public void saveHash(String key, String field, Object value) {
        hashOperations.put(key,field,value);
    }

    public void saveMultipleHashValues(String key, Map<String,Object> entries) {
        hashOperations.putAll(key,entries);
    }

    public Object getHashValue (String key, String field) {
        return this.hashOperations.get(key,field);
    }

    // get all fields value

    public Map<String,Object> getAllValues (String key) {
        return this.hashOperations.entries(key);
    }

    // check if value exists in hash
    public boolean hasHashField (String key, String field) {
        return this.hashOperations.hasKey(key,field);
    }

}
