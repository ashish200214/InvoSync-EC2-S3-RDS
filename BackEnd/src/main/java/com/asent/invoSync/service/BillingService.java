package com.asent.invoSync.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.asent.invoSync.dto.BillingDTO;
import com.asent.invoSync.entity.Bill;
import com.asent.invoSync.entity.Customer;
import com.asent.invoSync.entity.Item;
import com.asent.invoSync.entity.Quotation;
import com.asent.invoSync.mapper.BillingMapper;
import com.asent.invoSync.repository.CustomerRepository;
import com.asent.invoSync.repository.ItemRepository;
import com.asent.invoSync.repository.QuotationRepository;
import com.asent.invoSync.repository.BillingRepository;

@Service
public class BillingService {

    @Autowired private QuotationRepository quotationRepository;
    @Autowired private BillingRepository billingRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private S3Service s3Service;
    @Autowired private EmailService emailService;
    @Autowired private CustomerRepository customerRepository;

    /**
     * Create bill by copying items from quotation; returns created bill id
     */
  public Long createBillFromQuotation(Long quotationId) {
    Quotation q = quotationRepository.findByIdWithItems(quotationId)
            .orElseThrow(() -> new RuntimeException("Quotation not found"));
    q.setStatus("Confirmed");
    quotationRepository.save(q);

    Bill bill = new Bill();
    bill.setDrawing(q.getDrawing());
    bill.setQuotation(q); // link back to quotation

    // copy items from quotation to bill (so bill.items persisted and linked)
    List<Item> billItems = new ArrayList<>();
    if (q.getItems() != null && !q.getItems().isEmpty()) {
        for (Item qi : q.getItems()) {
            Item bi = new Item();
            bi.setParticular(qi.getParticular());
            bi.setQuantity(qi.getQuantity());
            bi.setPrice(qi.getPrice());

            // compute item total: prefer existing total, otherwise price * qty
            Double itemTotal = null;
            if (qi.getTotal() != null) {
                itemTotal = qi.getTotal();
            } else {
                double p = qi.getPrice() != null ? qi.getPrice() : 0.0;
                int qnt = qi.getQuantity() != null ? qi.getQuantity() : 1;
                itemTotal = p * qnt;
            }
            bi.setTotal(itemTotal);

            bi.setBilling(bill); // important: set owning side
            billItems.add(bi);
        }
    }
    bill.setItems(billItems);

    // calculate totals (sum of item totals)
    double sum = bill.getItems() != null
            ? bill.getItems().stream().mapToDouble(it ->
                it.getTotal() != null ? it.getTotal() :
                ((it.getPrice() != null ? it.getPrice() : 0.0) * (it.getQuantity() != null ? it.getQuantity() : 1))
              ).sum()
            : 0.0;

    bill.setTotal(sum);
    bill.setRemainingAmount(sum);

    billingRepository.save(bill);

    return bill.getId();
}

    public Bill getBill(Long id){
        return billingRepository.findByIdWithItemsAndQuotationAndCustomer(id)
                .orElseThrow(()->new RuntimeException("Bill not found"));
    }

    // Accept multipart PDF file and save to S3; update bill.pdfUrl and send email
    public String sendBillAndStorePdf(Long billId, MultipartFile billPdf) throws IOException {
        Bill bill = billingRepository.findByIdWithItemsAndQuotationAndCustomer(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        Customer c = null;
        if(bill.getQuotation()!=null) c = bill.getQuotation().getCustomer();

        String whats = c!=null? c.getWhatsAppNo() : "unknown";
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String pdfUrl = null;
        if(billPdf!=null && !billPdf.isEmpty()){
            pdfUrl = s3Service.uploadFileWithCustomerFolder(billPdf, whats, "bills", "bill_"+billId+"_"+ts);
            bill.setPdfUrl(pdfUrl);
            billingRepository.save(bill);
        }

        // send email with pdf link
        String email = c!=null? c.getEmail() : null;
        if(email!=null && !email.isBlank()){
            List<String> links = new ArrayList<>();
            if(pdfUrl!=null) links.add(pdfUrl);
            emailService.sendQuotationLinks(email, "Your Final Bill", "Please find your final bill below:", links);
        }

        return pdfUrl;
    }

    public List<BillingDTO> listAllBills(){
        return billingRepository.findAllWithItemsAndQuotationAndCustomer()
                .stream()
                .map(b -> {
                    BillingDTO dto = BillingMapper.toDTO(b);
                    if(b.getQuotation()!=null) dto.setQuotationId(b.getQuotation().getId());
                    if(b.getPdfUrl()!=null) dto.setPdfUrl(b.getPdfUrl());
                    if(b.getQuotation()!=null && b.getQuotation().getCustomer()!=null){
                        dto.setCustomerId(b.getQuotation().getCustomer().getId());
                        dto.setCustomerName(b.getQuotation().getCustomer().getName());
                        dto.setCustomerEmail(b.getQuotation().getCustomer().getEmail());
                    }
                    return dto;
                }).collect(Collectors.toList());
    }
}
