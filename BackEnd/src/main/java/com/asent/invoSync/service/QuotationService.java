package com.asent.invoSync.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.asent.invoSync.entity.*;
import com.asent.invoSync.repository.*;
import com.asent.invoSync.mapper.QuotationMapper;
import com.asent.invoSync.dto.QuotationDTO;
import com.asent.invoSync.dto.CustomerDTO;
import com.asent.invoSync.dto.ItemDTO;
import com.asent.invoSync.mapper.ItemMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuotationService {

    @Autowired private QuotationRepository quotationRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private BillingRepository billRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private S3Service s3Service;
    @Autowired private EmailService emailService;

    public List<QuotationDTO> getAll() {
        return quotationRepository.findAll().stream().map(QuotationMapper::toDTO).collect(Collectors.toList());
    }

    public QuotationDTO getById(Long id) {
        Quotation q = quotationRepository.findByIdWithItems(id).orElseThrow(() -> new RuntimeException("Not found"));
        return QuotationMapper.toDTO(q);
    }

    // pdfFile = generated pdf (frontend), drawingFile uploaded, images uploaded
    public String sendQuotation(Long id, MultipartFile pdfFile, MultipartFile drawingFile, List<MultipartFile> images) throws IOException {
        Quotation q = quotationRepository.findByIdWithItems(id).orElseThrow(() -> new RuntimeException("No quotation"));
        Customer c = q.getCustomer();
        String whats = c!=null?c.getWhatsAppNo():"unknown";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String ts = LocalDateTime.now().format(fmt);

        String pdfUrl = null;
        if(pdfFile!=null && !pdfFile.isEmpty()){
            pdfUrl = s3Service.uploadFileWithCustomerFolder(pdfFile, whats, "pdf", "quotation_"+id+"_"+ts);
        }

        if(drawingFile!=null && !drawingFile.isEmpty()){
            s3Service.uploadFileWithCustomerFolder(drawingFile, whats, "drawings", "quotation_"+id+"_drawing_"+ts);
        }

        List<String> imageUrls = new ArrayList<>();
        if(images!=null){
            for(int i=0;i<images.size();i++){
                imageUrls.add(s3Service.uploadFileWithCustomerFolder(images.get(i), whats, "images", "quotation_"+id+"_img"+(i+1)+"_"+ts));
            }
        }

        // Create bill entry
        Bill bill = new Bill();
        bill.setDrawing(q.getDrawing());
        double sum = q.getItems()!=null? q.getItems().stream().mapToDouble(it->it.getTotal()!=null?it.getTotal():0).sum() : 0;
        bill.setTotal(sum);
        bill.setRemainingAmount(sum);
        billRepository.save(bill);

        // send email with pdf and images (but not drawing)
        String email = c!=null?c.getEmail():null;
        if(email!=null && !email.isBlank()){
            List<String> links = new ArrayList<>();
            if(pdfUrl!=null) links.add(pdfUrl);
            links.addAll(imageUrls);
            emailService.sendQuotationLinks(email, "Your Quotation", "Please find your quotation below:", links);
        }

        return "Sent";
    }

    public String createQuotationAndCustomer(QuotationDTO dto) {
        if(dto==null) throw new RuntimeException("Empty dto");
        // use flat fields if supplied
        String whats = dto.getWhatsAppNo() != null ? dto.getWhatsAppNo() : "";
        Customer customer = customerRepository.findByWhatsAppNo(whats);
        if(customer==null){
            customer = new Customer();
            customer.setName(dto.getName());
            customer.setEmail(dto.getEmail());
            customer.setWhatsAppNo(dto.getWhatsAppNo());
            customer.setDate(LocalDateTime.now());
            customerRepository.save(customer);
        }
        Quotation q = new Quotation();
        q.setCustomer(customer);
        q.setInitialRequirement(dto.getInitialRequirement());
        q.setStatus("Pending");
        q.setDate(LocalDateTime.now());
        quotationRepository.save(q);
        return "OK";
    }
}
