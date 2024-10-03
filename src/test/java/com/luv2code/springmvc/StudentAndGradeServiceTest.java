package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.repository.StudentDAO;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

// this project uses TDD(Test Driven Development)
@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDAO studentDAO;

    @Autowired
    private JdbcTemplate jdbc;


    @BeforeEach
    public void setUp() {
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address)"+
                "values (1, 'Mohamad', 'Altalib', 'whatever@gamil.com')");

    }

    @AfterEach
    public void tearDown() {
        jdbc.execute("DELETE FROM student");
    }


    @Test
    public void createStudentService(){
        studentService.createStudent("Mohamad", "Shlool", "whatever@gamil.com");

        CollegeStudent student = studentDAO.findByEmailAddress("whatever@gamil.com");

        assertEquals("whatever@gamil.com",
                student.getEmailAddress(), "find by email address");//"This Should be EQUALS"

    }

    @DisplayName("gitting the student by email address")
    @Test
    public void findByEmailAddress(){

        CollegeStudent student = studentDAO.findByEmailAddress("whatever@gamil.com");

        assertEquals("whatever@gamil.com",
                student.getEmailAddress(), "find by email address");//"This Should be EQUALS"

    }

    @DisplayName("check if it is null")
    @Test
    public void checkIfItIsNull() {
        assertNotNull(studentDAO.findById(1));

        // or in other way

        assertTrue(studentService.checkIfStudentIsNull(0));
        assertFalse(studentService.checkIfStudentIsNull(1));

    }


}
