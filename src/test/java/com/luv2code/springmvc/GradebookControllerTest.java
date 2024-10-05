package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc// this is needed when we are testing MVC
@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class GradebookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Mock
    private StudentAndGradeService studentService;




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



}
