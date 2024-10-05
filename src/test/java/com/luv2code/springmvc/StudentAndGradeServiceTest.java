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
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private JdbcTemplate jdbc; // the jdbc is a helper for spring like u don't need to connect to database and it is faster
    // because the temporary data stored in RAM


    @BeforeEach
    public void setUp() {
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address)"+
                "values (1, 'Mohamad', 'Altalib', 'whatever@gamil.com')");

    }

    @AfterEach
    public void tearDown() {
        jdbc.execute("DELETE FROM student");// this to free the RAM from data
    }


    @Test
    public void createStudentService(){
        studentService.createStudent("Mohamad", "Shlool", "whatever3@gamil.com");

        CollegeStudent student = studentDAO.findByEmailAddress("whatever@gamil.com");// to preform this method the data must not be duplicate

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


    @DisplayName("delete student from database")
    @Test
    public void deleteStudentFromDatabase(){
        Optional<CollegeStudent> student = studentDAO.findById(1);

        assertTrue(student.isPresent(), "return true");

        studentService.deleteStudent(1);

        Optional<CollegeStudent> student2 = studentDAO.findById(1);

        assertFalse(student2.isPresent(), "return false");

    }

    // the beforeEach will execute first
    // but then the sql will be executed
    @Sql("/insertData.sql")
    @Test
    public void getGradBookService(){
        Iterable<CollegeStudent> students = studentService.getGradeBook();

        List<CollegeStudent> collegeStudentList = new ArrayList<>();

        for(CollegeStudent student : students){
            collegeStudentList.add(student);
        }
        //Note the sql insert should be in '' not ""
        assertEquals(6, collegeStudentList.size(), "the expected number of students");

    }


}
