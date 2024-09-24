/*
 * Copyright 2022-present Infinite Loop Corporation Limited, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.widescope.sqlThunder.controller.v2;


import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import com.widescope.cluster.management.ListOfPingResult;
import com.widescope.cluster.management.healthCheck.PingResult;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.utils.internet.InternetProtocolUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.cluster.management.clusterManagement.ClusterDb.MachineNodeList;
import com.widescope.cluster.management.clusterManagement.ClusterDb.MachineNode;
import com.widescope.cluster.management.healthCheck.PingNodes;
import com.widescope.cluster.management.healthCheck.SystemInfo;
import com.widescope.rest.GenericResponse;
import com.widescope.rest.RestObject;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.restApiClient.RestApiCluster;
import com.widescope.sqlThunder.utils.user.AuthUtil;


@CrossOrigin
@RestController
@Schema(title = "Cluster Node Management Controller")
public class ClusterController {


	@Autowired
	private AuthUtil authUtil;

	@Autowired
	private ClusterDb clusterDb;

	@Autowired
	private AppConstants appConstants;



	@PostConstruct
	public void initialize() {

	}



	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node:query", method = RequestMethod.GET)
	@Operation(summary = "Get All Registered Nodes") 
	public ResponseEntity<RestObject> 
	getAllNodes(@RequestHeader(value="requestId") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		List<MachineNode> allServers;
		MachineNodeList retList = new MachineNodeList();
		try	{
			allServers = clusterDb.getAllNodesFromDb();
			List<MachineNode> clusterInfo = PingNodes.queryAllCounterparties(allServers);
			retList.setServerCounterpartyList(clusterInfo);

			PingResult local = PingNodes.pingLocal();
			if(local!=null) {
				retList.addServer(new MachineNode(-1,local.getBaseUrl(), appConstants.getInstanceType(), "Y", "Y","Y"));
			}

			return RestObject.retOKWithPayload(retList, requestId, methodName);
			
		} catch(Exception ex)	{
			return RestObject.retException(requestId, methodName, AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, methodName, AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
		
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node:get", method = RequestMethod.GET)
	@Operation(summary = "Get node by id") 
	public ResponseEntity<RestObject> 
	getNodeById(@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="id") String id) {
		try	{
			MachineNode c = clusterDb.getNode(Integer.parseInt(id));
			c.setIsPong(Objects.requireNonNull(RestApiCluster.ping(c.getBaseUrl())));
			return RestObject.retOKWithPayload(c, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node:delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Node (User action)") 
	public ResponseEntity<RestObject> 
	deleteNode(	@RequestHeader(value="requestId") String requestId,
				@RequestHeader(value="id") String id) {

		try {
			MachineNode node  = clusterDb.getNode(id);
			if(!node.getBaseUrl().isBlank() ) {
				ConfigRepoDb.clusterNodes.remove(node.getBaseUrl());
				clusterDb.deleteNode(Integer.parseInt(id));
				return RestObject.retOK(requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			} else {
				return RestObject.retExceptionWithPayload(new GenericResponse("Node does not exist"), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
			}
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node:add", method = RequestMethod.PUT)
	@Operation(summary = "Register node to a particular cluster node (User action)") 
	public ResponseEntity<RestObject> 
	addNode(@RequestHeader(value="requestId") String requestId,
			@RequestHeader(value="baseUrl") String baseUrl,
			@RequestHeader(value="type") String type) {
		try	{
			MachineNode c = clusterDb.getNode(baseUrl);
			if(c.getBaseUrl()!= null) {
				return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), "url already exists");
			}
				 
			clusterDb.addNode(baseUrl, type, "Y", "N");
			c = clusterDb.getNode(baseUrl);
			c.setIsPong(Objects.requireNonNull(RestApiCluster.ping(c.getBaseUrl())));
			return RestObject.retOKWithPayload(c, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node/multiple:add", method = RequestMethod.PUT)
	@Operation(summary = "Register multiple nodes") 
	public ResponseEntity<RestObject> 
	addNodes(@RequestHeader(value="requestId") String requestId,
			 @Valid @RequestBody MachineNodeList nodesList) {
		try	{
			clusterDb.addNodes(nodesList);
			List<MachineNode> allServers = clusterDb.getAllNodesFromDb();
			allServers  = PingNodes.pingAllCounterparties_(allServers);
			allServers  = PingNodes.queryAllCounterparties(allServers);
			MachineNodeList retList = new MachineNodeList();
			retList.setServerCounterpartyList(allServers);
			return RestObject.retOKWithPayload(retList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node/broadcast:replace", method = RequestMethod.PUT)
	@Operation(summary = "Broadcast replace mode") 
	public ResponseEntity<RestObject> 
	broadcastReplace(	@RequestHeader(value="requestId") String requestId,
						@Valid @RequestBody MachineNodeList nodesList) {
		try	{
			clusterDb.addNodes(nodesList);
			List<MachineNode> allServers = clusterDb.getAllNodesFromDb();
			allServers  = PingNodes.pingAllCounterparties_(allServers);
			allServers  = PingNodes.queryAllCounterparties(allServers);
			MachineNodeList retList = new MachineNodeList();
			retList.setServerCounterpartyList(allServers);
			return RestObject.retOKWithPayload(retList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node/broadcast:update", method = RequestMethod.PUT)
	@Operation(summary = "Broadcast update node") 
	public ResponseEntity<RestObject> 
	broadcastUpdate(	@RequestHeader(value="requestId") String requestId,
						@Valid @RequestBody MachineNodeList nodesList) {
		try	{
			clusterDb.addNodes(nodesList);
			List<MachineNode> allServers = clusterDb.getAllNodesFromDb();
			allServers  = PingNodes.pingAllCounterparties_(allServers);
			allServers  = PingNodes.queryAllCounterparties(allServers);
			MachineNodeList retList = new MachineNodeList();
			retList.setServerCounterpartyList(allServers);
			return RestObject.retOKWithPayload(retList, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node:register", method = RequestMethod.PUT)
	@Operation(summary = "Register node to a particular cluster node (User action)") 
	public ResponseEntity<RestObject> 
	registerNode(	@RequestHeader(value="requestId") String requestId,
					@RequestHeader(value="baseUrl") String baseUrl,
					@RequestHeader(value="type") String type) {

		try	{
			MachineNode c = clusterDb.getNode(baseUrl);
			if(c.getBaseUrl() != null) {
				return RestObject.retException(requestId,  Thread.currentThread().getStackTrace()[1].getMethodName(), "url already exists");
			}
				 
			clusterDb.addNode(baseUrl, type, "N", "Y");
			c = clusterDb.getNode(baseUrl);
			c.setIsPong(Objects.requireNonNull(RestApiCluster.ping(c.getBaseUrl())));
			return RestObject.retOKWithPayload(c, requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node:ping", method = RequestMethod.POST)
	@Operation(summary = "Ping a node") /*response = String.class*/
	public ResponseEntity<RestObject> 
	ping(	@RequestHeader(value="requestId") String requestId,
			 @RequestHeader(value="baseUrl") String baseUrl)	{
		try	{
			String response = RestApiCluster.ping(baseUrl);
			return RestObject.retOKWithPayload(new GenericResponse(response), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch(Exception ex)	{
			return RestObject.retException(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		} catch(Throwable ex)	{
			return RestObject.retFatal(requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node/ping:pong", method = RequestMethod.GET)
	@Operation(summary = "Pong back to ping")
	public ResponseEntity<String>
	pong () {
		return new ResponseEntity<>("PONG", HttpStatus.OK);
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node:info", method = RequestMethod.GET)
	@Operation(summary = "Pong back to ping") /*response = String.class*/
	public ResponseEntity<MachineNode> 
	info () throws Exception	{
	    try {
	    	return new ResponseEntity<>(SystemInfo.getHealthCheck(ClusterDb.ownBaseUrl, appConstants.getInstanceType()), HttpStatus.OK);
	    } catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return new ResponseEntity<>(new MachineNode(), HttpStatus.OK);
		} catch(Throwable ex)	{
			AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return new ResponseEntity<> (new MachineNode(), HttpStatus.OK);
		}
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node/test/account:admin", method = RequestMethod.GET)
	@Operation(summary = "Test admin account") /*response = String.class*/
	public ResponseEntity<String> 
	testAdminAccount (	@RequestHeader(value="admin") String admin,
						@RequestHeader(value="passcode") String adminPasscode) {
		if( !authUtil.isInternalUserAuthenticated(admin, adminPasscode) )
			return new ResponseEntity<> ("ERROR", HttpStatus.OK);
		else
			return new ResponseEntity<> ("OK", HttpStatus.OK);
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/node/test/account:user", method = RequestMethod.GET)
	@Operation(summary = "Test User account") /*response = String.class*/
	public ResponseEntity<String> 
	testUserAccount (	@RequestHeader(value="user") String user,
						@RequestHeader(value="passcode") String userPasscode) {
		if( authUtil.isUserAuthenticated(user, userPasscode, "", ClusterDb.ownBaseUrl, "", "").getId() > 0 )
			return new ResponseEntity<> ("ERROR", HttpStatus.OK);
		else
			return new ResponseEntity<> ("OK", HttpStatus.OK);
	}


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/cluster/scan:network", method = RequestMethod.POST)
	@Operation(summary = "Scan all free nodes in the subnet")
	public ResponseEntity<RestObject>
	getClusterNodes (	@RequestHeader(value="requestId") String requestId,
						@RequestHeader(value="ipStart") String ipStart,
						@RequestHeader(value="ipEnd") String ipEnd) {

        List<PingResult> retList = PingNodes.pingClusterHttp(	InternetProtocolUtils.ipToLong(ipStart),
                                                                InternetProtocolUtils.ipToLong(ipEnd),
                                                                Integer.parseInt(appConstants.getServerPort()));
        PingResult local = PingNodes.pingLocal();
        if(local!=null) {
            retList.add(local);
        }
        return RestObject.retOKWithPayload(new ListOfPingResult(retList), requestId, Thread.currentThread().getStackTrace()[1].getMethodName());
    }

}
