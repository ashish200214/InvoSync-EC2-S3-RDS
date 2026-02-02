package com.asent.invoSync.mapper;

import com.asent.invoSync.entity.Quotation;
import com.asent.invoSync.dto.QuotationDTO;
import java.util.stream.Collectors;

public class QuotationMapper {
    public static QuotationDTO toDTO(Quotation q) {
        if(q==null) return null;
        QuotationDTO dto = new QuotationDTO();
        dto.setId(q.getId());
        if(q.getCustomer()!=null){
            var c = q.getCustomer();
            com.asent.invoSync.dto.CustomerDTO cd = new com.asent.invoSync.dto.CustomerDTO();
            cd.setId(c.getId());
            cd.setName(c.getName());
            cd.setEmail(c.getEmail());
            cd.setWhatsAppNo(c.getWhatsAppNo());
            dto.setCustomer(cd);
        }
        dto.setDrawingId(q.getDrawing()!=null?q.getDrawing().getId():null);
        dto.setDate(q.getDate());
        dto.setTotal(q.getTotal());
        dto.setStatus(q.getStatus());
        dto.setInitialRequirement(q.getInitialRequirement());
        if(q.getItems()!=null){
            dto.setItems(q.getItems().stream().map(ItemMapper::toDTO).collect(Collectors.toList()));
        }
        return dto;
    }
}
