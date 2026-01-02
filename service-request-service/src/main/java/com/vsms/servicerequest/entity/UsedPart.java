package com.vsms.servicerequest.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsedPart {

    @NotBlank
    private String partId;

    @NotBlank
    private String partName;

    @Min(1)
    private int quantity;

    @Positive
    private double unitPrice;

    @Positive
    private double totalPrice;
}

