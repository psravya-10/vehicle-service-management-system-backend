package com.vsms.inventory.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping
    public Map<String, String> add(@Valid @RequestBody CreateSparePartDto dto) {
        String partId = service.addPart(dto);
        return Map.of("partId", partId);
    }

    @GetMapping
    public List<SparePart> all() {
        return service.getAllParts();
    }

    @GetMapping("/low-stock")
    public List<SparePart> lowStock() {
        return service.getLowStockParts();
    }

    @PutMapping("/{id}/restock")
    public Map<String, String> restock(@PathVariable String id, @RequestParam int quantity) {
        service.restockPart(id, quantity);
        return Map.of("message", "Part restocked successfully");
    }

    @PutMapping("/{id}/threshold")
    public Map<String, String> updateThreshold(@PathVariable String id, @RequestParam int threshold) {
        service.updateThreshold(id, threshold);
        return Map.of("message", "Threshold updated successfully");
    }
}

