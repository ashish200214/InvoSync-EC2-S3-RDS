package com.asent.invoSync.mapper;

import com.asent.invoSync.entity.Item;
import com.asent.invoSync.dto.ItemDTO;

public class ItemMapper {
    public static ItemDTO toDTO(Item it){
        if(it==null) return null;
        ItemDTO d = new ItemDTO();
        d.setId(it.getId());
        d.setParticular(it.getParticular());
        d.setQuantity(it.getQuantity());
        d.setPrice(it.getPrice());
        d.setTotal(it.getTotal());
        d.setQuotationId(it.getQuotation()!=null?it.getQuotation().getId():null);
        d.setBillingId(it.getBilling()!=null?it.getBilling().getId():null);
        return d;
    }
}
