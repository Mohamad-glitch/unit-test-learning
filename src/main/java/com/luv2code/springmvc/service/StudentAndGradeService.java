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

    private final ScienceGradeDAO scienceGradeDAO;

    private final HistoryGradeDAO historyDAO;


    @Qualifier("mathGrades")
    private MathGrade mathGrade;


    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;


    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;




    @Autowired
    public StudentAndGradeService(StudentDAO studentDAO, MathGradeDAO mathGradeDAO,
                                  MathGrade mathGrade, HistoryGrade historyGrade, ScienceGrade scienceGrade, ScienceGradeDAO scienceGradeDAO, HistoryGradeDAO historyDAO) {
        this.studentDAO = studentDAO;
        this.mathGradeDAO = mathGradeDAO;
        this.mathGrade = mathGrade;
        this.historyGrade = historyGrade;
        this.scienceGrade = scienceGrade;
        this.scienceGradeDAO = scienceGradeDAO;
        this.historyDAO = historyDAO;
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

}
