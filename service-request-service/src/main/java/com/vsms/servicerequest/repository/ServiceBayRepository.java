package com.vsms.servicerequest.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vsms.servicerequest.entity.BayStatus;
import com.vsms.servicerequest.entity.ServiceBay;

public interface ServiceBayRepository
        extends MongoRepository<ServiceBay, String> {

    List<ServiceBay> findByStatus(BayStatus status);
}
