package com.example.springbatchdemo.repository;

import com.example.springbatchdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
