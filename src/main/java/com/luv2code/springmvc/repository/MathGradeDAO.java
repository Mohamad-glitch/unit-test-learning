package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.MathGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MathGradeDAO extends JpaRepository<MathGrade, Integer> {

    Iterable<MathGrade> findGradeByStudentId(int id);

    void deleteByStudentId(int id);

}
