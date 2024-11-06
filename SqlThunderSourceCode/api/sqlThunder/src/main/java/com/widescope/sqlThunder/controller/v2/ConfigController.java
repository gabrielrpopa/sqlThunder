package com.widescope.sqlThunder.controller.v2;




import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import javax.validation.Valid;

import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.widescope.rest.RestObject;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDbRecord;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDbRecordList;
import com.widescope.sqlThunder.config.configRepo.EndPointDbRecord;
import com.widescope.sqlThunder.config.configRepo.EndpointDbRecordList;
import com.widescope.sqlThunder.config.configRepo.IpToEndpointDbRecord;
import com.widescope.sqlThunder.config.configRepo.IpToEndpointDbRecordList;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.storage.dataExchangeRepo.ExchangeDb;


@CrossOrigin
@RestController
@Schema(title = "Data Exchange Server Controller")
public class ConfigController {


	@Autowired
	private ConfigRepoDb configRepoDb;
	
	@Autowired
	private ExchangeDb exchangeDb;
	

	@Autowired
	private AuthUtil authUtil;


	@PostConstruct
	public void initialize(){
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/config:get", method = RequestMethod.GET)
	@Operation(summary = "Get all current config settings") 
	public ResponseEntity<RestObject> 
	getConfig(	@RequestHeader(value="requestId", defaultValue = "") String requestId)  {
		requestId = StringUtils.generateRequestId(requestId);
		try {
			ConfigRepoDbRecordList ret = configRepoDb.getConfig();
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/config:owner", method = RequestMethod.GET)
	@Operation(summary = "Get owner's name") /*response = ResponseEntity.class*/
	public ResponseEntity<RestObject> 
	getOwner(@RequestHeader(value="requestId", defaultValue = "") String requestId)  {
		requestId = StringUtils.generateRequestId(requestId);
		try {
			ConfigRepoDbRecord ret = configRepoDb.getConfigRec("owner");
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/config:update", method = RequestMethod.POST)
	@Operation(summary = "Change config setting")
	public ResponseEntity<RestObject> 
	changeConfig(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestBody final ConfigRepoDbRecord configRepoDbRecord)  {
		requestId = StringUtils.generateRequestId(requestId);
		try {
			configRepoDb.updateConfig(configRepoDbRecord);
			ConfigRepoDb.updateInMemConfig(configRepoDbRecord);
			//Change the exchange name if the change of owner is done
			if(configRepoDbRecord.getConfigName().equals("owner")) {
				exchangeDb.changeOwnExchange(configRepoDbRecord.getConfigValue());
			}
			ConfigRepoDbRecordList ret = configRepoDb.getConfig(configRepoDbRecord.getConfigName());
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	/******************************End-Points and IP associated**********************************************************************/
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/config/endpoint:get", method = RequestMethod.GET)
	@Operation(summary = "Get all endpoint allowed IPs")
	public ResponseEntity<RestObject> 
	getAllEndpointAllowedIPs(@RequestHeader(value="requestId", defaultValue = "") String requestId)  {
		requestId = StringUtils.generateRequestId(requestId);
		try {
			EndpointDbRecordList ret = configRepoDb.getEndpoints();
			IpToEndpointDbRecordList ipList = configRepoDb.getAllIp();
			for(EndPointDbRecord r : ret.getEndpointDbRecordLst()) {
				List<IpToEndpointDbRecord> l = ipList.getIpToEndpointDbRecordLst().stream().filter(x->x.getIdEndpoint() == r.getId()).collect(Collectors.toList());
				r.setIpToEndpointDbRecordLst(l);
			}
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/config/endpoint/ip:add",method = RequestMethod.POST)
	@Operation(summary = "Add allowed IP to endpoint") /*response = ResponseEntity.class*/
	public ResponseEntity<RestObject> 
	addEndpointAllowedIp(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
							@Valid @RequestBody final IpToEndpointDbRecord ipToEndpointDbRecord)  {
		requestId = StringUtils.generateRequestId(requestId);
		try {
			List<IpToEndpointDbRecord> p = new ArrayList<IpToEndpointDbRecord>();
			p.add(ipToEndpointDbRecord);
			configRepoDb.addIpAddressToEndpoint(p); /* Add the IP in the database*/
			EndPointDbRecord r = configRepoDb.getEndpoint(ipToEndpointDbRecord.getIdEndpoint());
			ConfigRepoDb.endpointsWithIp.get(r.getEndpoint()).add(ipToEndpointDbRecord.getIpAddress());
			IpToEndpointDbRecordList ret = configRepoDb.getAllIpForEndpoint(ipToEndpointDbRecord.getIdEndpoint());
			r.setIpToEndpointDbRecordLst(ret.getIpToEndpointDbRecordLst());
			return RestObject.retOKWithPayload(r, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} 
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/config/endpoint/ip:addAll", method = RequestMethod.POST)
	@Operation(summary = "Add allowed IP to all endpoint") /*response = ResponseEntity.class*/
	public ResponseEntity<RestObject> 
	addAllowedIpToAllEndpoints( @RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="ipAddress") final String ipAddress)  {
		requestId = StringUtils.generateRequestId(requestId);
		try {
			List<Integer> allIds = configRepoDb.getEndpointIds();
			configRepoDb.addIpAddressToEndpoints(allIds, ipAddress);
			ConfigRepoDb.reloadEndpointInMem();
			return RestObject.retOKWithPayload("OK", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/config/endpoint/ip:deleteAll",method = RequestMethod.DELETE)
	@Operation(summary = "Delete allowed IP to all endpoint")
	public ResponseEntity<RestObject> 
	deleteAllowedIpToAllEndpoints(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
									@RequestHeader(value="ipAddress") final String ipAddress)  {
		requestId = StringUtils.generateRequestId(requestId);
		try {
			configRepoDb.deleteIpAddressToAllEndpoints(ipAddress);
			ConfigRepoDb.reloadEndpointInMem();
			return RestObject.retOKWithPayload("OK", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/config/endpoint/ip:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete IP associated to endpoint") /*response = ResponseEntity.class*/
	public ResponseEntity<RestObject> 
	deleteAllowedIpToEndpoint(	@RequestHeader(value="requestId", defaultValue = "") String requestId,
								@RequestHeader(value="id") final String id,
								@RequestHeader(value="idEndpoint") final String idEndpoint,
								@RequestHeader(value="ipAddress") final String ipAddress)  {
		requestId = StringUtils.generateRequestId(requestId);
		try {
			EndPointDbRecord p = configRepoDb.getEndpoint(Integer.parseInt(idEndpoint));
			configRepoDb.deleteIpAddressToEndpoint(Integer.parseInt(id));
			ConfigRepoDb.endpointsWithIp.get(p.getEndpoint()).remove(ipAddress);
			EndPointDbRecord ret = configRepoDb.getEndpoint(Integer.parseInt(idEndpoint));
			IpToEndpointDbRecordList ipList = configRepoDb.getAllIpForEndpoint(Integer.parseInt(idEndpoint));
			ret.setIpToEndpointDbRecordLst(ipList.getIpToEndpointDbRecordLst());
			return RestObject.retOKWithPayload(ret, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/config/endpoint:deleteAll", method = RequestMethod.POST)
	@Operation(summary = "Reload mapping ip to endpoints") /*response = ResponseEntity.class*/
	public ResponseEntity<RestObject> 
	reloadIpToEndpoints(@RequestHeader(value="requestId", defaultValue = "") String requestId)  {
		requestId = StringUtils.generateRequestId(requestId);
		try {
			ConfigRepoDb.reloadEndpointInMem();
			return RestObject.retOKWithPayload("OK", requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex) {
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
}
