package com.vsms.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vsms.user.dto.CustomerResponse;
import com.vsms.user.entity.User;
import com.vsms.user.enums.UserStatus;
import com.vsms.user.exception.BusinessException;
import com.vsms.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository repo;

    public List<CustomerResponse> pendingUsers() {
        return repo.findByStatus(UserStatus.PENDING)
                .stream()
                .map(this::map)
                .toList();
    }

    public void approveUsers(List<String> ids) {
        for (String id : ids) {
            User user = repo.findById(id)
                    .orElseThrow(() -> new BusinessException("User not found"));

            user.setStatus(UserStatus.APPROVED);
            repo.save(user);
        }
    }


    private CustomerResponse map(User u) {
        return CustomerResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole())
                .build();
    }

}

