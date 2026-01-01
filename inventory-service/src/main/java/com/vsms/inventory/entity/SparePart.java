package com.vsms.inventory.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "spare_parts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SparePart {

    @Id
    private String id;

    private String name;
    private String category;
    private int availableQuantity;
    private double unitPrice;
    private int lowStockThreshold = 3;
}
