package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDAO;
import com.luv2code.springmvc.repository.MathGradeDAO;
import com.luv2code.springmvc.repository.ScienceGradeDAO;
import com.luv2code.springmvc.repository.StudentDAO;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc// this is needed when we are testing MVC
@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class GradebookControllerTest {

    // MockHttpServletRequest it's like simulating to http request with variable and input like "firstname", "Mohamad"
    // it used in unit testing to simulate MVC in action
    private static MockHttpServletRequest request;
    // its static because if i want to use it in @BeforeAll the method should be static
    // and to use a variable in static method the variable itself must be static
    //(there is no need to be static unless it used in a static method)

    @Autowired
    private MathGradeDAO mathGradeDAO;

    @Autowired
    private HistoryGradeDAO historyDAO;

    @Autowired
    private ScienceGradeDAO scienceDAO;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @Mock
    private StudentAndGradeService studentServiceMock;

    @Autowired
    private StudentDAO studentDAO;

    @BeforeAll
    public static void setUp() {
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Mohamad");
        request.setParameter("lastname", "Shlool");
        request.setParameter("email_address", "shlool@gmail.com");
    }




    @BeforeEach
    public void beforeEach() {
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address)"+
                "values (1, 'Mohamad', 'Altalib', 'whatever@gamil.com')");


        jdbc.execute("insert into math_grade(id, student_id, grade) values (1, 1, 100.00)");
        jdbc.execute("insert into science_grade(id, student_id, grade) values (1, 1, 100.00)");
        jdbc.execute("insert into history_grade(id, student_id, grade) values (1, 1, 100.00)");


    }

    @AfterEach
    public void afterEach() {
        jdbc.execute("DELETE FROM student");// this to free the RAM from data
        jdbc.execute("DELETE FROM math_grade");
        jdbc.execute("DELETE FROM science_grade");
        jdbc.execute("DELETE FROM history_grade");

    }

    @Test
    public void getStudentsHttpRequest() throws Exception {
        CollegeStudent studentOne = new
                CollegeStudent("Mohamad","Shlool","shlool@gmail.com");

        CollegeStudent studentTwo = new
                CollegeStudent("Ali","Shlool","whatever@gmail.com");

        List<CollegeStudent> students = new ArrayList<>(Arrays.asList(studentOne, studentTwo));

        when(studentServiceMock.getGradeBook()).thenReturn(students);

        // just to check if they have the same values
        assertIterableEquals(students, studentServiceMock.getGradeBook(),"this should have the same values");


        // now we will do some web testing (MVC)
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders// this to preform a web request
                .get("/")).andExpect(status().isOk()).andReturn();
        //(calling a method has @GetMapping) the url is ("/") and expectations for status is OK in (200~299)http code

        ModelAndView mav = mvcResult.getModelAndView();

        // now to assert the model and view we use ModelAndViewAssert
        ModelAndViewAssert.assertViewName(mav, "index");
        // the name is html name and mav is the mvcResult.getModelAndView
    }

    @Test
    public void createStudentHttpRequest() throws Exception {
        CollegeStudent collegeStudentOne = new CollegeStudent("Mohamad"
                ,"Shlool","shlool@gmail.com");

        List<CollegeStudent> students = new ArrayList<>(Arrays.asList(collegeStudentOne));

        // mocking studentServiceMock.getGradeBook()
        when(studentServiceMock.getGradeBook()).thenReturn(students);

        // just to check if it works
        assertIterableEquals(students, studentServiceMock.getGradeBook(),"this should have the same values");

        // create a student using http request
        // post data to mapping in controller (create a new mapping in controller type POST)

        MvcResult mvcResult =  mockMvc.perform(post("/")// this where the data is going to (the method in the controller)
                .contentType(MediaType.APPLICATION_JSON)// this for the type is coming from the http ex:JSON, HTML, XML ......
                .param("firstname", request.getParameter("firstname"))// these are the data i created in the
                .param("lastname",request.getParameter("lastname"))// @BeforeAll
                .param("emailAddress",request.getParameter("email_address")))// the name in param should match with the variable name in the class
                .andExpect(status().isOk()).andReturn();// this what i expect as the result from this operation


        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");

        CollegeStudent verifyStudent = studentDAO.findByEmailAddress("shlool@gmail.com");
        assertNotNull(verifyStudent, "it should has the student that cam from http request");
    }


    @DisplayName("delete student")
    @Test
    public void deleteStudentHttpRequest() throws Exception {
        // check if student exist
        assertTrue(studentDAO.findById(1).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders// this to preform a web request
                .get("/delete/student/{id}", 1))// 1 is for id as input
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");

        assertFalse(studentDAO.findById(1).isPresent());

    }

    @DisplayName("Error page")
    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception {
        // this method is about to get an error page if there is anything wrong
        // in this case i am calling for data id = 0 (that does not exist)
        // so it will create an error and in this case it should get an error page

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/delete/student/{id}", 0)).
                andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();


        ModelAndViewAssert.assertViewName(mav, "error");

    }

    @DisplayName("create a math grade ")
    @Test
    public void createGradeBookService(){

        // create the grade
        assertTrue(studentAndGradeService.createGrade(80.50, 1, "math"));// grade, Id, Type

        // get all grades with studentId
        Iterable<MathGrade> grades = mathGradeDAO.findGradeByStudentId(1);

        // verify there is grades
        assertTrue(grades.iterator().hasNext(), "student has math grades");

    }

    @DisplayName("create a science grade")
    @Test
    public void cratesScienceGrade(){
        // create science grade
        assertTrue(studentAndGradeService.createGrade(80.50, 1, "science"));

        // get all grades with student id
        Iterable<ScienceGrade> grades = scienceDAO.findGradeByStudentId(1);

        // verify
        assertTrue(grades.iterator().hasNext(), "science has grades");

    }

    @DisplayName("create a History grade")
    @Test
    public void cratesHistoryGrade(){
        // create science grade
        assertTrue(studentAndGradeService.createGrade(80.50, 1, "history"));

        // get all grades with student id
        Iterable<HistoryGrade> grades = historyDAO.findGradeByStudentId(1);

        // verify
        assertTrue(grades.iterator().hasNext(), "science has history");

    }

    @DisplayName("false input ")
    @Test
    public void falseInput(){

        assertFalse(studentServiceMock.createGrade(80.6, 10, "math"));// out of range
        assertFalse(studentServiceMock.createGrade(1000, 10, "math"));// student does't exist
        assertFalse(studentServiceMock.createGrade(-15, 10, "math"));// out of range

        assertFalse(studentServiceMock.createGrade(80.6, 10, "science"));// out of range
        assertFalse(studentServiceMock.createGrade(1000, 10, "science"));// student does't exist
        assertFalse(studentServiceMock.createGrade(-15, 10, "science"));// out of range

        assertFalse(studentServiceMock.createGrade(80.6, 10, "history"));// out of range
        assertFalse(studentServiceMock.createGrade(1000, 10, "history"));// student does't exist
        assertFalse(studentServiceMock.createGrade(-15, 10, "history"));// out of range

        assertFalse(studentServiceMock.createGrade(-15, 10, "english"));// there is no subject called english

    }

    @DisplayName("all of grades")
    @Test
    public void allGrades(){
        // create the grade
        assertTrue(studentAndGradeService.createGrade(80.50, 1, "math"));// grade, Id, Type
        assertTrue(studentAndGradeService.createGrade(80.50, 1, "history"));
        assertTrue(studentAndGradeService.createGrade(80.50, 1, "science"));


        // get all grades with studentId
        Iterable<MathGrade> grades = mathGradeDAO.findGradeByStudentId(1);
        Iterable<HistoryGrade> gradeHistory = historyDAO.findGradeByStudentId(1);
        Iterable<ScienceGrade> gradeScience = scienceDAO.findGradeByStudentId(1);


        // verify there is grades
        assertTrue(grades.iterator().hasNext(), "student has math grades");
        assertTrue(gradeHistory.iterator().hasNext(), "science has history");
        assertTrue(gradeScience.iterator().hasNext(), "science has grades");

        // check how many gardes there
        assertEquals(2, ((Collection<MathGrade>) grades).size());
        assertEquals(2, ((Collection<ScienceGrade>) gradeScience).size());
        assertEquals(2, ((Collection<HistoryGrade>) gradeHistory).size());


    }

    @DisplayName("Deleting grades from student")
    @Test
    public void deleteGradeService(){
        // add delete grade functionality to the system
        assertEquals(1, studentAndGradeService.deleteGrade(1, "math")// 1 = id, "math" = grade type or class
               , "Returns student id after the delete");


        assertEquals(1, studentAndGradeService.deleteGrade(1, "history")// 1 = id, "history" = grade type or class
                , "Returns student id after the delete");

        assertEquals(1, studentAndGradeService.deleteGrade(1, "science")// 1 = id, "science" = grade type or class
                , "Returns student id after the delete");

    }

    @DisplayName("delete with non-extent student id")
    @Test
    public void deleteGradeServiceReturnStudentIdOfZero(){
        // edge cases

        assertEquals(0, studentAndGradeService.deleteGrade(0, "math")
                ,"No student should have 0 id ");

        assertEquals(0, studentAndGradeService.deleteGrade(0, "whatever"),
                "there is no subject called whatever");

    }

    @DisplayName("assert student info")
    @Test
    public void studentInformation(){

        GradebookCollegeStudent gradebookCollegeStudent = studentAndGradeService.studentInformation(1);

        assertNotNull(gradebookCollegeStudent, "this should not be null");
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Mohamad", gradebookCollegeStudent.getFirstname());
        assertEquals("Altalib", gradebookCollegeStudent.getLastname());
        assertEquals("whatever@gamil.com", gradebookCollegeStudent.getEmailAddress());
        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() == 1);

    }

    @DisplayName("test if the student id isn't exist")
    @Test
    public void studentInformationServiceReturnNull(){
        // if the student isn't exist return null (edge case)
    GradebookCollegeStudent gradebookCollegeStudent = studentAndGradeService.studentInformation(0);// this id does not exist
        assertNull(gradebookCollegeStudent, "this should not be null");


    }


}
