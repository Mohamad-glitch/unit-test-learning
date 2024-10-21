package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDAO;
import com.luv2code.springmvc.repository.MathGradeDAO;
import com.luv2code.springmvc.repository.ScienceGradeDAO;
import com.luv2code.springmvc.repository.StudentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    private final StudentDAO studentDAO;

    private final MathGradeDAO mathGradeDAO;

    private final ScienceGradeDAO scienceGradeDAO;

    private final HistoryGradeDAO historyDAO;

    private final HistoryGradeDAO historyGradeDAO;

    @Autowired
    private StudentGrades studentGrades;


    @Qualifier("mathGrades")
    private MathGrade mathGrade;


    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;


    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;




    @Autowired
    public StudentAndGradeService(StudentDAO studentDAO, MathGradeDAO mathGradeDAO,
                                  MathGrade mathGrade, HistoryGrade historyGrade, ScienceGrade scienceGrade, ScienceGradeDAO scienceGradeDAO, HistoryGradeDAO historyDAO, HistoryGradeDAO historyGradeDAO) {
        this.studentDAO = studentDAO;
        this.mathGradeDAO = mathGradeDAO;
        this.mathGrade = mathGrade;
        this.historyGrade = historyGrade;
        this.scienceGrade = scienceGrade;
        this.scienceGradeDAO = scienceGradeDAO;
        this.historyDAO = historyDAO;
        this.historyGradeDAO = historyGradeDAO;
    }

    public void createStudent(String firstName, String lastName, String email) {
        CollegeStudent student = new CollegeStudent(firstName, lastName, email);
        student.setId(0);
        studentDAO.save( student);

    }

    public boolean checkIfStudentIsNull(int i) {

        Optional<CollegeStudent> student = studentDAO.findById(i);

        return student.isEmpty();
    }

    public void deleteStudent(int id) {
        if(!(checkIfStudentIsNull(id))) {
            studentDAO.deleteById(id);
            mathGradeDAO.deleteByStudentId(id);
            historyGradeDAO.deleteByStudentId(id);
            scienceGradeDAO.deleteByStudentId(id);
    }

    }

    public Iterable<CollegeStudent> getGradeBook() {
        return studentDAO.findAll();
    }

    public boolean createGrade(double grade, int id, String type){

        if(checkIfStudentIsNull(id)){

            return false;
        }


        if(grade <= 100.00 && grade >= 0.00){
            if(type.equalsIgnoreCase("math")){
                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(id);
                mathGradeDAO.save(mathGrade);
                return true;
            }


            if(type.equalsIgnoreCase("science")){
                scienceGrade.setId(0);
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(id);
                scienceGradeDAO.save(scienceGrade);
                return true;
            }



            if(type.equalsIgnoreCase("history")){
                historyGrade.setId(0);
                historyGrade.setGrade(grade);
                historyGrade.setStudentId(id);
                historyDAO.save(historyGrade);
                return true;
            }
        }


        return false;
    }


    public int deleteGrade(int id, String type){
        int studentId = 0;

        if(type.equals("math")) {

            Optional<MathGrade> grade = mathGradeDAO.findById(id); // get the grade with id
            if (grade.isPresent()) {
                studentId = grade.get().getStudentId(); // get the student id
                mathGradeDAO.deleteById(id); // delete the grade
            } else {
                return 0; // return 0 if the grade is not present
            }
        }

        if(type.equals("history")) {

            Optional<HistoryGrade> grade = historyDAO.findById(id); // get the grade with id
            if (grade.isPresent()) {
                studentId = grade.get().getStudentId(); // get the student id
                historyDAO.deleteById(id); // delete the grade
            } else {
                return 0; // return 0 if the grade is not present
            }
        }

        if(type.equals("science")) {

            Optional<ScienceGrade> grade = scienceGradeDAO.findById(id); // get the grade with id
            if (grade.isPresent()) {
                studentId = grade.get().getStudentId(); // get the student id
                scienceGradeDAO.deleteById(id); // delete the grade
            } else {
                return 0; // return 0 if the grade is not present
            }
        }

        return studentId;
    }


    public GradebookCollegeStudent studentInformation(int id){

        if(checkIfStudentIsNull(id)){
            return null;
        }

        Optional<CollegeStudent> student = studentDAO.findById(id);
        Iterable<MathGrade> mathGrades = mathGradeDAO.findGradeByStudentId(id);
        Iterable<HistoryGrade> historyGrades = historyGradeDAO.findGradeByStudentId(id);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDAO.findGradeByStudentId(id);

        List<Grade> mathGradeList = new ArrayList<>();
        mathGrades.forEach(mathGradeList::add);

        List<Grade> historyGradeList = new ArrayList<>();
        historyGrades.forEach(historyGradeList::add);

        List<Grade> scienceGradeList = new ArrayList<>();
        scienceGrades.forEach(scienceGradeList::add);

        studentGrades.setMathGradeResults(mathGradeList);
        studentGrades.setHistoryGradeResults(historyGradeList);
        studentGrades.setScienceGradeResults(scienceGradeList);

        GradebookCollegeStudent gradebookCollegeStudent = new GradebookCollegeStudent(student.get().getId(),
        student.get().getFirstname(),student.get().getLastname(), student.get().getEmailAddress(), studentGrades);

        return gradebookCollegeStudent;
    }


    public void configureStudentInformationModel(int id, Model m){
        // fixed a bug where the model attribute name was historyGradeResults but the name in the HTML was historyAverage for the three subjects
        GradebookCollegeStudent studentEntity = studentInformation(id);
        m.addAttribute("student", studentEntity);

        // Math Average
        if (studentEntity.getStudentGrades().getMathGradeResults().size() > 0) {
            m.addAttribute("mathAverage",
                    studentEntity.getStudentGrades().findGradePointAverage(
                            studentEntity.getStudentGrades().getMathGradeResults()
                    )
            );
        } else {
            m.addAttribute("mathAverage", "N/A");
        }

        // History Average
        if (studentEntity.getStudentGrades().getHistoryGradeResults().size() > 0) {
            m.addAttribute("historyAverage",
                    studentEntity.getStudentGrades().findGradePointAverage(
                            studentEntity.getStudentGrades().getHistoryGradeResults()
                    )
            );
        } else {
            m.addAttribute("historyAverage", "N/A");
        }

        // Science Average
        if (studentEntity.getStudentGrades().getScienceGradeResults().size() > 0) {
            m.addAttribute("scienceAverage",
                    studentEntity.getStudentGrades().findGradePointAverage(
                            studentEntity.getStudentGrades().getScienceGradeResults()
                    )
            );
        } else {
            m.addAttribute("scienceAverage", "N/A");
        }

    }


}
