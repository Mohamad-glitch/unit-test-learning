package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.repository.StudentDAO;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    private final StudentDAO studentDAO;

    @Autowired
    public StudentAndGradeService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
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
}
