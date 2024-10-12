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
import org.springframework.beans.factory.annotation.Value;
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

    // added a sql scripts in properties and inject them in variables (cleaner way to do sql in test)
    @Value("${sql.scripts.create.student}")
    private String sqlScriptCreateStudent;

    @Value("${sql.scripts.create.math.grade}")
    private String sqlScriptCreateMathGrade;

    @Value("${sql.scripts.create.history.grade}")
    private String sqlScriptCreateHistoryGrade;

    @Value("${sql.scripts.create.science.grade}")
    private String sqlScriptCreateScienceGrade;

    @Value("${sql.script.delete.student}")
    private String sqlScriptDeleteStudent;


    @Value("${sql.script.delete.math}")
    private String sqlScriptDeleteMath;

    @Value("${sql.script.delete.science}")
    private String sqlScriptDeleteScienceGrade;

    @Value("${sql.script.delete.history}")
    private String sqlScriptDeleteHistory;



    @BeforeEach
    public void beforeEach() {
        jdbc.execute(sqlScriptCreateStudent);


        jdbc.execute(sqlScriptCreateMathGrade);
        jdbc.execute(sqlScriptCreateHistoryGrade);
        jdbc.execute(sqlScriptCreateScienceGrade);


    }

    @AfterEach
    public void afterEach() {
        jdbc.execute(sqlScriptDeleteStudent);// this to free the RAM from data
        jdbc.execute(sqlScriptDeleteMath);
        jdbc.execute(sqlScriptDeleteScienceGrade);
        jdbc.execute(sqlScriptDeleteHistory);

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
