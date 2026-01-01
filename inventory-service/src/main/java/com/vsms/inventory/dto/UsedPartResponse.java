package com.vsms.inventory.dto;

import lombok.Data;

@Data
public class UsedPartResponse {
    private String partId;
    private int quantity;
    private double unitPrice;
    private double totalCost;
}
