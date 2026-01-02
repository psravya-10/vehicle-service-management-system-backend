package com.vsms.inventory.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsedPartDto {

    private String partId;
    private String partName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
}
