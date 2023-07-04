package com.bian.coreless.brokered.product.inbound.api.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.bian.coreless.brokered.product.inbound.api.model.BrokeredProductInformation;

public interface BrokeredProductInformationRepository extends MongoRepository<BrokeredProductInformation, String> {
    
    List<BrokeredProductInformation> findAll();
    
    @Query("{brokeredProductId:'?0'}")
    BrokeredProductInformation findItemByBrokeredProductId(String brokeredProductId);
    
}