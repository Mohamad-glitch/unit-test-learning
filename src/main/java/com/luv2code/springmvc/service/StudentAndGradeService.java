package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.Student;
import com.luv2code.springmvc.repository.StudentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

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

}
