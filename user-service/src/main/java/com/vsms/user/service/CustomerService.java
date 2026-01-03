package com.vsms.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.vsms.user.dto.CustomerResponse;
import com.vsms.user.entity.User;
import com.vsms.user.exception.BusinessException;
import com.vsms.user.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository repo;

    public CustomerResponse getCustomerById(String id) {
        User user = repo.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));

        return CustomerResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
    public CustomerResponse getByEmail(String email) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        return CustomerResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public String getEmailByUserId(String userId) {
        User user = repo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getEmail();
    }


}
