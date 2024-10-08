package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.HistoryGrade;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.ScienceGrade;
import com.luv2code.springmvc.repository.HistoryGradeDAO;
import com.luv2code.springmvc.repository.MathGradeDAO;
import com.luv2code.springmvc.repository.ScienceGradeDAO;
import com.luv2code.springmvc.repository.StudentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    private final StudentDAO studentDAO;

    private final MathGradeDAO mathGradeDAO;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;

    @Autowired
    private ScienceGradeDAO scienceGradeDAO;

    @Autowired
    private HistoryGradeDAO historyDAO;


    @Autowired
    public StudentAndGradeService(StudentDAO studentDAO, MathGradeDAO mathGradeDAO) {
        this.studentDAO = studentDAO;
        this.mathGradeDAO = mathGradeDAO;
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

    public void deleteStudent(int i) {
        studentDAO.deleteById(i);
    }

    public Iterable<CollegeStudent> getGradeBook() {
        return studentDAO.findAll();
    }

    public boolean createGrade(double grade, int id, String type){

        if(checkIfStudentIsNull(id)){

            return false;
        }

        if(grade <= 100.00 && grade >= 0.00){
            if(type.equals("math")){
                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(id);
                mathGradeDAO.save(mathGrade);
                return true;
            }
        }

        if(grade <= 100.00 && grade >= 0.00){
            if(type.equals("science")){
                scienceGrade.setId(0);
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(id);
                scienceGradeDAO.save(scienceGrade);
                return true;
            }
        }


        if(grade <= 100.00 && grade >= 0.00){
            if(type.equals("history")){
                historyGrade.setId(0);
                historyGrade.setGrade(grade);
                historyGrade.setStudentId(id);
                historyDAO.save(historyGrade);
                return true;
            }
        }


        return false;
    }

}
