package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.MathGradeDAO;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

    private final Gradebook gradebook;

    private final StudentAndGradeService studentService;
    private final MathGradeDAO mathGradeDAO;

    @Autowired
    public GradebookController(Gradebook gradebook, StudentAndGradeService studentService, MathGradeDAO mathGradeDAO) {
        this.gradebook = gradebook;
        this.studentService = studentService;
        this.mathGradeDAO = mathGradeDAO;
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
        if(studentService.checkIfStudentIsNull(id)){
            return "error";
        }

        GradebookCollegeStudent studentEntity = studentService.studentInformation(id);
        m.addAttribute("student", studentEntity);


        if(studentEntity.getStudentGrades().getMathGradeResults().size() > 0){
            m.addAttribute("mathGradeResults",
                    studentEntity.getStudentGrades().findGradePointAverage(
                            studentEntity.getStudentGrades().getMathGradeResults()
                    )
            );
        }else{
            m.addAttribute("mathAverage", "N/A");
        }


        if(studentEntity.getStudentGrades().getMathGradeResults().size() > 0){
            m.addAttribute("historyGradeResults",
                    studentEntity.getStudentGrades().findGradePointAverage(
                            studentEntity.getStudentGrades().getMathGradeResults()
                    )
            );
        }else{
            m.addAttribute("historyAverage", "N/A");
        }


        if(studentEntity.getStudentGrades().getScienceGradeResults().size() > 0){
            m.addAttribute("scienceGradeResults",
                    studentEntity.getStudentGrades().findGradePointAverage(
                            studentEntity.getStudentGrades().getScienceGradeResults()
                    )
            );
        }else{
            m.addAttribute("scienceAverage", "N/A");
        }

        return "studentInformation";
    }

    @GetMapping("/delete/student/{id}")
    public String deleteStudent(@PathVariable int id, Model m) {
        if(id <= 0 ){
            return "error";// cant use "redirect:/error" because it will return redirect status (300~399)
        }

        studentService.deleteStudent(id);
        Iterable<CollegeStudent> students = studentService.getGradeBook();
        m.addAttribute("students", students);

        return "index";
    }

    @PostMapping("/grades")
    public String createGrade(@RequestParam("grade") double grade,
                           @RequestParam("gradeType") String gradeType,
                           @RequestParam("studentId") int studentId,
                           Model m
                           ) {
        if (studentService.checkIfStudentIsNull(studentId)) {
            return "error";
        }

        boolean success = studentService.createGrade(grade, studentId, gradeType);

        if (!success) {
            return "error";// if the grade did not build correctly then return to the error page
        }

        GradebookCollegeStudent studentEntity = studentService.studentInformation(studentId);

        m.addAttribute("student", studentEntity);


        if(studentEntity.getStudentGrades().getMathGradeResults().size() > 0){
            m.addAttribute("mathGradeResults",
                    studentEntity.getStudentGrades().findGradePointAverage(
                            studentEntity.getStudentGrades().getMathGradeResults()
                    )
            );
        }else{
            m.addAttribute("mathAverage", "N/A");
        }


        if(studentEntity.getStudentGrades().getMathGradeResults().size() > 0){
            m.addAttribute("historyGradeResults",
                    studentEntity.getStudentGrades().findGradePointAverage(
                            studentEntity.getStudentGrades().getMathGradeResults()
                    )
            );
        }else{
            m.addAttribute("historyAverage", "N/A");
        }


        if(studentEntity.getStudentGrades().getScienceGradeResults().size() > 0){
            m.addAttribute("scienceGradeResults",
                    studentEntity.getStudentGrades().findGradePointAverage(
                            studentEntity.getStudentGrades().getScienceGradeResults()
                    )
            );
        }else{
            m.addAttribute("scienceAverage", "N/A");
        }

        return "studentInformation";
    }


}
