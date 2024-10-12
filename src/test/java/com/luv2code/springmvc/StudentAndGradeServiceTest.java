package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.HistoryGrade;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.ScienceGrade;
import com.luv2code.springmvc.repository.HistoryGradeDAO;
import com.luv2code.springmvc.repository.MathGradeDAO;
import com.luv2code.springmvc.repository.ScienceGradeDAO;
import com.luv2code.springmvc.repository.StudentDAO;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
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
    @Autowired
    private MathGradeDAO mathGradeDAO;
    @Autowired
    private HistoryGradeDAO historyGradeDAO;
    @Autowired
    private ScienceGradeDAO scienceGradeDAO;
    // because the temporary data stored in RAM


    @BeforeEach
    public void setUp() {
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address)"+
                "values (1, 'Mohamad', 'Altalib', 'whatever@gamil.com')");

        // added some data to play with
        jdbc.execute("insert into math_grade(id, student_id, grade) values (1, 1, 100.00)");
        jdbc.execute("insert into science_grade(id, student_id, grade) values (1, 1, 100.00)");
        jdbc.execute("insert into history_grade(id, student_id, grade) values (1, 1, 100.00)");

    }

    @AfterEach
    public void tearDown() {
        jdbc.execute("DELETE FROM student");// this to free the RAM from data
        jdbc.execute("DELETE FROM math_grade");
        jdbc.execute("DELETE FROM science_grade");
        jdbc.execute("DELETE FROM history_grade");
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
    public void deleteStudentFromDatabase(){// academic code
        // add code to delete associated grades
        Optional<CollegeStudent> student = studentDAO.findById(1);
        Optional<MathGrade> mathGrade = mathGradeDAO.findById(1);
        Optional<HistoryGrade> historyGrade = historyGradeDAO.findById(1);
        Optional<ScienceGrade> scienceGrade = scienceGradeDAO.findById(1);


        assertTrue(student.isPresent(), "return true");
        assertTrue(mathGrade.isPresent(), "return true");
        assertTrue(historyGrade.isPresent(), "return true");
        assertTrue(scienceGrade.isPresent(), "return true");

        studentService.deleteStudent(1);

        mathGrade = mathGradeDAO.findById(1);
        historyGrade = historyGradeDAO.findById(1);
        scienceGrade = scienceGradeDAO.findById(1);

        student = studentDAO.findById(1);

        assertFalse(student.isPresent(), "return false");
        assertFalse(historyGrade.isPresent(), "return false");
        assertFalse(mathGrade.isPresent(), "return false");
        assertFalse(scienceGrade.isPresent(), "return false");


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
