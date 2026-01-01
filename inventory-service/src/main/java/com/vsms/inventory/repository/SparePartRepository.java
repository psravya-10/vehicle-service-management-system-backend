package com.vsms.inventory.repository;

import com.vsms.inventory.entity.SparePart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SparePartRepository extends MongoRepository<SparePart, String> {
}
