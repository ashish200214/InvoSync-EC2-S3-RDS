package com.asent.invoSync.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;

import com.asent.invoSync.service.QuotationService;
import com.asent.invoSync.dto.QuotationDTO;

import java.util.List;

@RestController
@RequestMapping("/api/quotations")
public class QuotationController {
    @Autowired private QuotationService service;

    @GetMapping("/")
    public List<com.asent.invoSync.dto.QuotationDTO> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public com.asent.invoSync.dto.QuotationDTO getById(@PathVariable Long id) { return service.getById(id); }

    // send endpoint - expects request parts
    @PostMapping("/{id}/send")
    public ResponseEntity<String> sendQuotation(
            @PathVariable Long id,
            @RequestPart(value="quotationPdf", required=false) MultipartFile quotationPdf,
            @RequestPart(value="drawing", required=false) MultipartFile drawing,
            @RequestPart(value="images", required=false) List<MultipartFile> images
    ) {
        try {
            service.sendQuotation(id, quotationPdf, drawing, images);
            return ResponseEntity.ok("Sent");
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/saveQuotation")
    public String saveQuotation(@RequestBody QuotationDTO dto){
        return service.createQuotationAndCustomer(dto);
    }
}
