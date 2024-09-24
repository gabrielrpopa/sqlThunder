package com.widescope.storage.dataExchangeRepo.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.widescope.storage.dataExchangeRepo.ExchangeFileDbRecord;

@Service
public interface ExchangeStorageService {
	
	public String exchangeIn = "../../exchange/exchangeIn";
	void init() throws Exception;

	String store(	MultipartFile file, final ExchangeFileDbRecord r, final String requestId) throws Exception;
	boolean delete(final ExchangeFileDbRecord exchangeFileDbRecord, final String requestId) throws Exception;
	String getFilePath(final ExchangeFileDbRecord exchangeFileDbRecord, final String requestId); 
}
