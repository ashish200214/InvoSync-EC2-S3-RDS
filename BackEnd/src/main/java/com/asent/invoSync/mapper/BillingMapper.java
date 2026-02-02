package com.asent.invoSync.mapper;

import com.asent.invoSync.entity.Bill;
import com.asent.invoSync.dto.BillingDTO;
import java.util.stream.Collectors;

public class BillingMapper {
    public static BillingDTO toDTO(Bill b){
        if(b==null) return null;
        BillingDTO dto = new BillingDTO();
        dto.setId(b.getId());
        dto.setDrawingId(b.getDrawing()!=null?b.getDrawing().getId():null);
        dto.setTotal(b.getTotal());
        dto.setRemainingAmount(b.getRemainingAmount());

        // map items
        if(b.getItems()!=null) dto.setItems(b.getItems().stream().map(ItemMapper::toDTO).collect(Collectors.toList()));

        // map customer via linked quotation if present
        if(b.getQuotation()!=null && b.getQuotation().getCustomer()!=null){
            var c = b.getQuotation().getCustomer();
            com.asent.invoSync.dto.CustomerDTO cd = new com.asent.invoSync.dto.CustomerDTO();
            cd.setId(c.getId());
            cd.setName(c.getName());
            cd.setEmail(c.getEmail());
            cd.setWhatsAppNo(c.getWhatsAppNo());
            dto.setCustomer(cd);
            dto.setName(c.getName());
        } else {
            // fallback: if bill has drawing or items with name fields, you can set dto.setName(...)
            dto.setName(null);
        }

        return dto;
    }
}
