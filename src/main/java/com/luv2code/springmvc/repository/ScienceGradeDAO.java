package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.ScienceGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScienceGradeDAO extends JpaRepository<ScienceGrade, Integer> {

    Iterable<ScienceGrade> findScienceGradeById(Integer id);

}
