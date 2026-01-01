package com.vsms.inventory.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "part_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartRequest {

    @Id
    private String id;

    private String serviceRequestId;
    private String technicianId;
    private String partId;
    private int quantity;

    private RequestStatus status;
    private String approvedBy;
    private String remarks;
}
