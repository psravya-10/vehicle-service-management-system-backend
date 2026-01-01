package com.vsms.servicerequest.startup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.vsms.servicerequest.entity.*;
import com.vsms.servicerequest.repository.ServiceBayRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BayDataInitializer implements CommandLineRunner {

    private final ServiceBayRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            for (int i = 1; i <= 50; i++) {
                repo.save(ServiceBay.builder()
                        .bayCode(String.format("BAY-%02d", i))
                        .status(BayStatus.AVAILABLE)
                        .build());
            }
        }
    }
}
