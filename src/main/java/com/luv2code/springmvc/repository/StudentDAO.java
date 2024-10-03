package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.CollegeStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDAO extends JpaRepository<CollegeStudent, Integer> {


    CollegeStudent findByEmailAddress(String email);




}
