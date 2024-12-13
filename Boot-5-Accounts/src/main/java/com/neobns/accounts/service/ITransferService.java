package com.neobns.accounts.service;

import java.util.List;

import com.neobns.accounts.entity.Transfer;

public interface ITransferService {

    
    
    
    List<Transfer> fetchTransfersByInvalidQuery(Long accountNumber);
   


}
