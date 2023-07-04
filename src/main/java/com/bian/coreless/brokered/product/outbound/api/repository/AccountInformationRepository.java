package com.bian.coreless.brokered.product.outbound.api.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.bian.coreless.brokered.product.outbound.api.model.AccountInformation;

public interface AccountInformationRepository extends MongoRepository<AccountInformation, String> {
    
    List<AccountInformation> findAll();
    
    @Query("{brokeredProductId:'?0'}")
    AccountInformation findItemByBrokeredProductId(String brokeredProductId);
    
}