package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

// this project uses TDD(Test Driven Development)
@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Test
    public void createStudentService(){
        studentService.createStudent("Mohamad", "Shlool", "whatever@gamil.com");

        CollegeStudent student = studentDAO.findByEmailAddress("whatever@gamil.com");

        assertEquals("whatever@gamil.com",
                student.getEmailAddress(), "find by email address");//"This Should be EQUALS"

    }


}
