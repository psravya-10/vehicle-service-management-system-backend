package com.vsms.servicerequest.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "service_bays")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBay {

    @Id
    private String id;

    private String bayCode;

    private BayStatus status;
}
