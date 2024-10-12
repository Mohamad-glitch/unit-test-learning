package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.HistoryGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryGradeDAO extends JpaRepository<HistoryGrade, Integer> {

    Iterable<HistoryGrade> findGradeByStudentId(Integer id);

    void deleteByStudentId(int id);

}

