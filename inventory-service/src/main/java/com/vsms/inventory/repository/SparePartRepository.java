package com.vsms.inventory.repository;

import com.vsms.inventory.entity.SparePart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SparePartRepository extends MongoRepository<SparePart, String> {
    
    @Query("{ $expr: { $lte: ['$availableQuantity', '$lowStockThreshold'] } }")
    List<SparePart> findLowStockParts();
}

