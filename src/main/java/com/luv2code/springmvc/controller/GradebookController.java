package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

    private final Gradebook gradebook;

    private final StudentAndGradeService studentService;

    @Autowired
    public GradebookController(Gradebook gradebook, StudentAndGradeService studentService) {
        this.gradebook = gradebook;
        this.studentService = studentService;
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getStudents(Model m) {
        Iterable<CollegeStudent> collegeStudents = studentService.getGradeBook();// get the students
        m.addAttribute("collegeStudents", collegeStudents);// add the students to model

        return "index";
    }

    @PostMapping("/")
    public String addStudent(@ModelAttribute("student") CollegeStudent student, Model m) {
        // this will save model student to database
        studentService.createStudent(student.getFirstname(),
                student.getLastname(),student.getEmailAddress());

        Iterable<CollegeStudent> students = studentService.getGradeBook();
        m.addAttribute("students", students);


        return "index";
    }


    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {
        return "studentInformation";
    }

    @GetMapping("/delete/student/{id}")
    public String deleteStudent(@PathVariable int id, Model m) {

        studentService.deleteStudent(id);
        Iterable<CollegeStudent> students = studentService.getGradeBook();
        m.addAttribute("students", students);

        return "index";
    }

}
