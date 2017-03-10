package com.alan.springmvc.service;

import java.util.List;

import com.alan.springmvc.entity.User;

public interface UserService {
    User findById(Integer id);
    User save(String name);
    List<User> findAll();
}
