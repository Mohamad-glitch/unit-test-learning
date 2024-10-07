package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Mock
    private StudentAndGradeService studentService;

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

    }

    @AfterEach
    public void afterEach() {
        jdbc.execute("DELETE FROM student");// this to free the RAM from data
    }

    @Test
    public void getStudentsHttpRequest() throws Exception {
        CollegeStudent studentOne = new
                CollegeStudent("Mohamad","Shlool","shlool@gmail.com");

        CollegeStudent studentTwo = new
                CollegeStudent("Ali","Shlool","whatever@gmail.com");

        List<CollegeStudent> students = new ArrayList<>(Arrays.asList(studentOne, studentTwo));

        when(studentService.getGradeBook()).thenReturn(students);

        // just to check if they have the same values
        assertIterableEquals(students, studentService.getGradeBook(),"this should have the same values");


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

        // mocking studentService.getGradeBook()
        when(studentService.getGradeBook()).thenReturn(students);

        // just to check if it works
        assertIterableEquals(students, studentService.getGradeBook(),"this should have the same values");

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


}
