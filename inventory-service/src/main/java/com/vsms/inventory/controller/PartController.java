package com.vsms.inventory.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vsms.inventory.dto.CreateSparePartDto;
import com.vsms.inventory.entity.SparePart;
import com.vsms.inventory.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory/parts")
@RequiredArgsConstructor
public class PartController {

    private final InventoryService service;

    // Create spare part 
    @PostMapping
    public Map<String, String> add(@Valid @RequestBody CreateSparePartDto dto) {
        String partId = service.addPart(dto);
        return Map.of("partId", partId);
    }

    // Get all spare parts
    @GetMapping
    public List<SparePart> all() {
        return service.getAllParts();
    }
}
