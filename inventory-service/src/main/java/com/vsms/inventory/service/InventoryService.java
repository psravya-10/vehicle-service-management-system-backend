package com.vsms.inventory.service;

import com.vsms.inventory.dto.*;
import com.vsms.inventory.entity.*;
import com.vsms.inventory.exception.ResourceNotFoundException;
import com.vsms.inventory.repository.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final SparePartRepository partRepo;
    private final PartRequestRepository requestRepo;

    public String addPart(CreateSparePartDto dto) {

        SparePart part = partRepo.save(
                SparePart.builder()
                        .name(dto.getName())
                        .category(dto.getCategory())
                        .availableQuantity(dto.getAvailableQuantity())
                        .unitPrice(dto.getUnitPrice())
                        .build()
        );

        return part.getId();
    }

    public String requestPart(CreatePartRequestDto dto) {

        partRepo.findById(dto.getPartId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Spare part not found"));

        PartRequest request = requestRepo.save(
                PartRequest.builder()
                        .serviceRequestId(dto.getServiceRequestId())
                        .technicianId(dto.getTechnicianId())
                        .partId(dto.getPartId())
                        .quantity(dto.getQuantity())
                        .status(RequestStatus.REQUESTED)
                        .build()
        );

        return request.getId();
    }

    public void approveRequest(String requestId, String managerId) {

        PartRequest request = requestRepo.findById(requestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Part request not found"));

        if (request.getStatus() == RequestStatus.APPROVED) {
            throw new IllegalStateException("Request already approved");
        }

        SparePart part = partRepo.findById(request.getPartId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Spare part not found"));

        if (part.getAvailableQuantity() < request.getQuantity()) {
            throw new IllegalStateException("Insufficient stock");
        }

        part.setAvailableQuantity(part.getAvailableQuantity() - request.getQuantity());
        partRepo.save(part);

        request.setStatus(RequestStatus.APPROVED);
        request.setApprovedBy(managerId);
        requestRepo.save(request);
    }
    public List<SparePart> getAllParts() {
        return partRepo.findAll();
    }

}
