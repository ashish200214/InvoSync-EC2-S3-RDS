package com.asent.invoSync.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.asent.invoSync.dto.BillingDTO;
import com.asent.invoSync.entity.Bill;
import com.asent.invoSync.mapper.BillingMapper;
import com.asent.invoSync.service.BillingService;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @Autowired private BillingService billingService;

    // list all bills
    @GetMapping("/")
    public ResponseEntity<List<BillingDTO>> listAll(){
        return ResponseEntity.ok(billingService.listAllBills());
    }

    // return bill DTO (for billing page)
    @GetMapping("/{billId}")
    public ResponseEntity<BillingDTO> getBill(@PathVariable Long billId){
        Bill bill = billingService.getBill(billId);
        return ResponseEntity.ok(BillingMapper.toDTO(bill));
    }

    // old confirm endpoint (keeps path for backwards compat)
    @PostMapping("/confirmFromQuotation/{quotationId}")
    public ResponseEntity<Long> confirmQuotation(@PathVariable Long quotationId){
        Long billId = billingService.createBillFromQuotation(quotationId);
        return ResponseEntity.ok(billId);
    }

    // send final bill PDF (multipart) -> save to s3, email link
    @PostMapping("/sendBill/{billId}")
    public ResponseEntity<String> sendBill(
            @PathVariable Long billId,
            @RequestPart(value="billPdf", required=false) MultipartFile billPdf
    ){
        try {
            String url = billingService.sendBillAndStorePdf(billId, billPdf);
            return ResponseEntity.ok(url!=null?url:"Sent");
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
