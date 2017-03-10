package com.alan.springmvc.dao;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alan.springmvc.entity.User;

public interface UserDao extends JpaRepository<User, Serializable>{
	    User findById(Integer id);
}
