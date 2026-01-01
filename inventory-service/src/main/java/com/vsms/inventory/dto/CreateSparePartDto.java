package com.vsms.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateSparePartDto {

    @NotBlank(message = "Part name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int availableQuantity;

    @Positive(message = "Unit price must be greater than zero")
    private double unitPrice;
}

