#pip install requests
#pip install bson

from ctypes.wintypes import BYTE
import requests
from typing import List
import json
from json import JSONEncoder
import mimetypes
import ntpath
import pathlib
import datetime
import time
import uuid
import bson
from typing import Union


class RestObject:
	def __init__(self):
		self.payload = None
		self.errorMessage = ""
		self.debugMessage = ""
		self.errorCode = ErrorCode.OK
		self.errorSource = Sources.NONE
		self.timestamp = datetime.datetime.fromtimestamp(round(time.time() * 1000))
		self.errorSeverity = ErrorSeverity.NONE
		self.objectType = ""
		self.requestId = uuid.uuid4()

class ErrorSeverity: 
	def __init__(self):
		self.NONE = "NONE"
		self.WARNING = "WARNING"
		self.LOW = "LOW"
		self.MEDIUM = "MEDIUM"
		self.HIGH = "HIGH"
  

class ErrorCode: 
	def __init__(self):
		self.OK = 0
		self.WARNING = -1
		self.ERROR = -2
		self.FATALERROR = -3
		
class Sources: 
	def __init__(self):
		self.UNKNOWN = "UNKNOWN"
		self.NONE = "NONE"
		self.AUTH = "AUTH"
		self.SQLTHUNDER = "SQLTHUNDER API"
		self.ELASTICSEARCH = "ELASTICSEARCH"
		self.DATABASE = "DATABASE"
		self.CACHE = "CACHE API"
		self.SCRIPTINGAPI = "SCRIPTING API"
		self.FRONTEND = "FRONT END"
		self.LOGGINGAPI = "LOGGINGAPI"
		self.IP = "IP"

		



class JsonEncoder(JSONEncoder):
 def default(self, o):
  return o.__dict__

class GenericResponse:
	def __init__(self, payload_:str):
		self.payload = payload_


class ColumnDefinition:
	def __init__(self, columnName, columnType, columnPrecision, columnScale):
		self.columnName = columnName
		self.columnType = columnType
		self.columnPrecision = columnPrecision
		self.columnScale = columnScale


class TableDefinition:
	def __init__(self, tableName:str, tableScript:str, tableInsert:str, metadata:List[ColumnDefinition]):
		self.tableName = tableName
		self.tableScript = tableScript
		self.tableInsert = tableInsert
		self.metadata = metadata


class EmbeddedDbRecord:
	def __init__(self, dbId:int, fileName:str, type:str, userId:int, clusterId:int, path:str, info:str, tableDefinitions:List[TableDefinition]):
		self.dbId = dbId
		self.fileName = fileName
		self.type = type
		self.userId = userId
		self:clusterId = clusterId
		self:path = path
		self:info = info
		self:tableDefinitions = tableDefinitions

class ClusterTransfer:
	def __init__(self, clusterId:int, embeddedDbRecordList:List[TableDefinition]):
		self.clusterId = clusterId
		self.embeddedDbRecordList = embeddedDbRecordList


class CellValue:
	def __init__(self, columnName:str, columnValue):
		self.columnName = columnName
		self.columnValue = columnValue

class RowValue:
	def __init__(self, row:List[CellValue]):
		self.row = row


class TableAffected:
	def __init__(self, recAffected:int, tableName:str):
		self.recAffected = recAffected
		self.tableName = tableName


class DataTransfer:
	def __init__(self, countRecord:str, embeddedInMemDbName:str, lstTables:List[TableAffected]):
		self.countRecord = countRecord
		self.embeddedInMemDbName = embeddedInMemDbName
		self.lstTables = lstTables

class RdbmsCompoundQuery:
	def __init__(self, tableName:str, sqlContent:str, uuid:str):
		self.tableName = tableName
		self.sqlContent = sqlContent
		self.uuid = uuid

class ListRdbmsCompoundQuery:
	def __init__(self, countRecord:str, embeddedInMemDbName:str, lst:List[RdbmsCompoundQuery]):
		self.countRecord = countRecord
		self.embeddedInMemDbName = embeddedInMemDbName
		self.lst = lst


class ClusterRule:
	def __init__(self, rule:str):
		self.rule = rule


class SqlRule:
	def __init__(self, rule:str):
		self.rule = rule


class EmbeddedClusterInfo:
	def __init__(self, clusterRule:ClusterRule, sqlRule:SqlRule):
		self.clusterRule = clusterRule
		self.sqlRule = sqlRule


class ListRdbmsCompoundQuery:
	def __init__(self, countRecord:str, embeddedInMemDbName:str, lst:List[RdbmsCompoundQuery]):
		self.countRecord = countRecord
		self.embeddedInMemDbName = embeddedInMemDbName
		self.lst = lst


class ConfigRepoDbRecord:
	def __init__(self, id:int, configName:str, configValue:str, configDescription:str, configType:str):
		self.id = id
		self.configName = configName
		self.configValue = configValue
		self.configDescription = configDescription
		self.configType = configType



class IpToEndpointDbRecord:
	def __init__(self, id:int, idEndpoint:str, ipAddress:str):
		self.id = id
		self.idEndpoint = idEndpoint
		self.ipAddress = ipAddress



'''
get metadata and content for a list of file paths
'''
def getFileContent(filePath:str) -> Union[str, str, str] :
	f = open(filePath, "rb")
	mime_type = mimetypes.guess_type(filePath)
	fileName = ntpath.basename(filePath);
	file_bytes = f.read()
	f.close() 
	return fileName, file_bytes, mime_type

	
	


api_url_stem = "@api_url_stem@"



''' 
 ############################ Cache Controller START #####################################
'''




class CacheResponse:
	def __init__(self, key:str, value:str, user:str, message:str):
		self.key = key
		self.value = value
		self.user = user
		self.message = message

def retObject(response:json) -> str | None:
	if response["errorCode"] == ErrorCode().OK:
		print( "Error Message: " + response["errorMessage"])
		return response["payload"]
	else:
		return None


'''
Clear the cache
'''
def clearCacheStore(internalAdmin:str, internalAdminPasscode:str):
	headers = {}
	headers['internalAdmin'] = internalAdmin
	headers['internalAdminPasscode'] = internalAdminPasscode
	api_url = api_url_stem + "/cache/store:clear"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json()) # returns CacheResponse with something like {"",JSONObject,internalAdmin, ""}
  

'''
Get all keys in the cache
'''
def getAllCacheKeys(internalAdmin:str, internalAdminPasscode:str, keyList: str):
	headers = {}
	headers['internalAdmin'] = internalAdmin
	headers['internalAdminPasscode'] = internalAdminPasscode
	headers['keyList'] = keyList
	api_url = api_url_stem + "/cache/keys:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # payload in RestObject is CacheResponse



'''
Set a new key with its value to cache
'''
def setCacheKey(user:str, session:str, requestId:str, validFor:int, notificationProxy:str, jsonObj:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['validFor'] = validFor
	headers['notificationProxy'] = notificationProxy
	api_url = api_url_stem + "/cache/store:set"
	response =requests.put(api_url, data=jsonObj, headers=headers)
	return retObject(response.json()) # payload in RestObject is CacheResponse
 

	
	
'''
Delete a key and its value
'''
def deleteCacheKey(user:str, session:str, requestId:str, key:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['key'] = key
	api_url = api_url_stem + "/cache/store:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  #  jsonResponse['payload'] is CacheResponse with something like {"","", internalAdmin, "CLEARED"}

'''
Update Validity
'''
def updateCacheKeyValidFor(user:str, session:str, requestId:str, key:str, validFor:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['key'] = key
	headers['validFor'] = validFor
	api_url = api_url_stem + "/cache/store:updateValidFor"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json()) #jsonResponse['payload'] is CacheResponse 

	
'''
Update value of a key
'''
def updateCacheKeyValue(user:str, session:str, requestId:str, key:str, jsonObj:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['key'] = key
	api_url = api_url_stem + "/cache/store:updateValue"
	response =requests.post(api_url, data=jsonObj, headers=headers)
	return retObject(response.json()) #jsonResponse['payload'] is CacheResponse 
	

'''
Update entire object of a key
'''
def updateCacheKey(user:str, session:str, requestId:str, key:str, validFor:str, jsonObj:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['key'] = key
	headers['validFor'] = validFor
	api_url = api_url_stem + "/cache/store:update"
	response =requests.post(api_url, data=jsonObj, headers=headers)
	return retObject(response.json()) #jsonResponse['payload'] is CacheResponse 
	
	
	
'''
Get an object from the cache
'''
def getCacheKey(user:str, session:str, requestId:str, key:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['key'] = key
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cache/store:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) #jsonResponse['payload'] is CacheResponse 



'''
Check if a key exists in the cache
'''
def isCacheKey(user:str, session:str, requestId:str, key:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['key'] = key
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cache/store:isKey"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) #jsonResponse['payload'] is CacheResponse 
 
 


''' 
 ############################ Cache Controller END #####################################
'''



''' 
 ############################ Cluster Controller START #####################################
'''

'''
Stream back to the caller node, streaming response
'''

class MachineNode:
	def __init__(self, id_:int, 
						baseUrl_:str, 
						type_:str, 
						isReachable_:str, 
						isAccepted_:str, 
						isRegistered_:str, 
						totalMemory_:int, 
						freeMemory_:int, 
						cpuUsed_:int, 
						noScripts_:int, 
						lastTimeUpdated_:str, 
						ipList_:List):
		self.id = id_;
		self.baseUrl = baseUrl_;
		self.type = type_;
		self.isReachable = isReachable_;  
		self.isAccepted = isAccepted_;
		self.isRegistered = isRegistered_;
		self.totalMemory=totalMemory_;  
		self.freeMemory = freeMemory_;  
		self.cpuUsed = cpuUsed_;
		self.noScripts = noScripts_;
		self.lastTimeUpdated = lastTimeUpdated_;
		self.ipList = ipList_;
		
class MachineNodeList:
	def __init__(self, machineNode:List):
		self.machineNode = machineNode


'''
Get All Registered Nodes
'''
def getAllNodes():
	headers = {}
	api_url = api_url_stem + "/cluster/node:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #payload in RestObject represented by MachineNodeList 
	

'''
Get node by id
'''
def getNodeById(user:str, session:str, id:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['id'] = id
	api_url = api_url_stem + "/cluster/node:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # payload in RestObject represented by MachineNode

'''
Delete Node (User action)
'''
def deleteNode(user:str, session:str, id:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['id'] = id
	api_url = api_url_stem + "/cluster/node:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json()) # payload in RestObject represented by MachineNode



'''
Register node to a particular cluster node (User action)
'''
def addNode(user:str, session:str, baseUrl:str, type:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['baseUrl'] = baseUrl
	headers['type'] = type

	api_url = api_url_stem + "/cluster/node:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Register multiple nodes
'''
def addNodes(user:str, session:str, requestId:str, nodesList:MachineNodeList):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node/multiple:add"
	response =requests.put(api_url, data=nodesList, headers=headers)
	return retObject(response.json())
	
class PingResult:
	def __init__(self, baseUrl_:str, isReachable_:str):
		self.baseUrl = baseUrl_;
		self.isReachable = isReachable_
		
class ListOfPingResult:
	def __init__(self, listResult_:List):
		self.listResult = listResult_;
		

'''
Scan all free nodes in the subnet
'''
def scanClusterNodes(user:str, session:str, requestId:str, ipStart:str, ipEnd:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['ipStart'] = ipStart
	headers['ipEnd'] = ipEnd
	api_url = api_url_stem + "/cluster/scan:network"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json()) # payload is ListOfPingResult
	
'''
Register node to a particular cluster node (User action)
'''	
def registerNode(user:str, session:str, requestId:str, baseUrl:str, type:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['baseUrl'] = baseUrl
	headers['type'] = type
	api_url = api_url_stem + "/cluster/node:register"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json()) # payload is ListOfPingResult
	
'''
info
'''
def info():
	headers = {}
	api_url = api_url_stem + "/cluster/node:info"
	response =requests.get(api_url, headers=headers)
	jsonResponse = response.json()
	return retObject(response.json())


'''
Test Admin account
'''	
def testAdminAccount(user:str, passcode:str):
	headers = {}
	headers['user'] = user
	headers['passcode'] = passcode
	api_url = api_url_stem + "/cluster/node/test/account:admin"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns as String which is "ERROR" or "OK"


'''
Test User account
'''	
def testUserAccount(user:str, passcode:str):
	headers = {}
	headers['user'] = user
	headers['passcode'] = passcode
	api_url = api_url_stem + "/cluster/node/test/account:user"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns as String which is "ERROR" or "OK"


'''
Broadcast replace mode
'''	
def broadcastReplace(user:str, session:str, requestId:str, nodesList: MachineNodeList):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node/broadcast:replace"
	response =requests.put(api_url, data=nodesList, headers=headers)
	return retObject(response.json()) # returns as RestObject with payload as MachineNodeList 
	
'''
Broadcast replace mode
'''	
def broadcastUpdate(user:str, session:str, requestId:str, nodesList: MachineNodeList):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node/broadcast:update"
	response =requests.put(api_url, data=nodesList, headers=headers)
	return retObject(response.json()) # returns as RestObject with payload as MachineNodeList 	
	
'''
Ping a node
'''	
def ping(user:str, session:str, baseUrl: str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['baseUrl'] = baseUrl
	api_url = api_url_stem + "/cluster/node:ping"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json()) # returns as RestObject with payload as GenericResponse 
	

'''
Pong back to ping
'''	
def pong(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	api_url = api_url_stem + "/cluster/node:pong"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json()) # returns as STRING with "PONG"
	
	


''' 
 ############################ Cluster Controller END #####################################
'''

''' 
 ############################ Config Controller START #####################################
'''

class ConfigRepoDbRecord:
	def __init__(self, baseUrl_:str, isReachable_:str):
		self.baseUrl = baseUrl_;
		self.isReachable = isReachable_
		
class ConfigRepoDbRecordList:
	def __init__(self, configRepoDbRecordLst_:List):
		self.configRepoDbRecordLst = configRepoDbRecordLst_;

	

'''
Get all current config settings
'''
def getConfig(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # return RestObject with payload as ConfigRepoDbRecordList
	
'''
Get owner's name
'''
def getOwner(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config:owner"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())



'''
Change config setting
'''
def changeConfig(user:str, session:str, requestId:str, configRepoDbRecord: ConfigRepoDbRecord):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config:update"
	response =requests.post(api_url, data=configRepoDbRecord, headers=headers)
	return retObject(response.json())

class IpToEndpointDbRecord:
	def __init__(self, id_:int, idEndpoint_:int, ipAddress_:str):
		self.id = id_;
		self.idEndpoint = idEndpoint_
		self.ipAddress = ipAddress_
		
		
class EndPointDbRecord:
	def __init__(self, id_:int, endpoint_:str, ipToEndpointDbRecordLst_:List[IpToEndpointDbRecord]):
		self.id = id_;
		self.endpoint = endpoint_
		self.ipToEndpointDbRecordLst = ipToEndpointDbRecordLst_
		
class EndpointDbRecordList:
	def __init__(self, endpointDbRecordLst_:List[EndPointDbRecord]):
		self.endpointDbRecordLst = endpointDbRecordLst_
		
		
'''
Get all endpoint allowed IPs
'''
def getAllEndpointAllowedIPs(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config/enpoint:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # return RestObject with payload as EndpointDbRecordList
	
	
'''
Add allowed IP to endpoint
'''
def addEndpointAllowedIp(user:str, session:str, requestId:str, ip: IpToEndpointDbRecord):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config/enpoint/ip:add"
	response =requests.post(api_url, data=ip, headers=headers)
	return retObject(response.json()) # return RestObject with payload as EndPointDbRecord


'''
Add allowed IP to all endpoint
'''
def addAllowedIpToAllEndpoints(user:str, session:str, requestId:str, ipAddress: str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['ipAddress'] = ipAddress
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config/enpoint/ip:addall"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Delete IP associated to endpoint
'''
def deleteAllowedIpToEndpoint(user:str, session:str, id:int, requestId:str, idEndpoint:int, ipAddress:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['id'] = id
	headers['idEndpoint'] = idEndpoint
	headers['ipAddress'] = ipAddress
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config/enpoint/ip:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())

'''
Add allowed IP to all endpoint
'''
def deleteAllowedIpToAllEndpoints(user:str, session:str, requestId:str, ipAddress:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['ipAddress'] = ipAddress
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config/enpoint/ip:deleteall"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())



'''
Reload mapping ip to endpoints
'''
def reloadIpToEndpoints(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config/enpoint:deleteall"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())





''' 
 ############################ Config Controller END #####################################
'''



''' 
 ############################ Elastic Controller START #####################################
'''

class ElasticCompoundQuery:
	def __init__(self, clusterUniqueName:str, indexName:str, sqlType:str, httpVerb:str, elasticApi:str, endPoint:str, sqlContent:str, uuid:str):
		self.clusterUniqueName = clusterUniqueName
		self.indexName = indexName
		self.sqlType = sqlType
		self.httpVerb = httpVerb
		self.elasticApi = elasticApi
		self.endPoint = endPoint
		self.sqlContent =sqlContent
		self.uuid = uuid

class ListElasticCompoundQuery:
	def __init__(self, elasticCompoundQuery:List[ElasticCompoundQuery], tableName:str, sqlAggregator:str):
		self.elasticCompoundQuery = elasticCompoundQuery
		self.tableName = tableName
		self.sqlAggregator = sqlAggregator

class ElasticHost:
	def __init__(self, hostId:int, clusterId:int, server:str, port:int, protocol:str, description:str, tunnelLocalPort:int, tunnelRemoteHostAddress:str, tunnelRemoteHostPort:int, tunnelRemoteUser:str, tunnelRemoteUserPassword:str, tunnelRemoteRsaKey:str):
		self.hostId = hostId
		self.clusterId = clusterId
		self.server = server
		self.port = port
		self.protocol = protocol
		self.description = description
		self.tunnelLocalPort = tunnelLocalPort
		self.tunnelRemoteHostAddress = tunnelRemoteHostAddress
		self.tunnelRemoteHostPort = tunnelRemoteHostPort
		self.tunnelRemoteUser = tunnelRemoteUser
		self.tunnelRemoteUserPassword = tunnelRemoteUserPassword
		self.tunnelRemoteRsaKey = tunnelRemoteRsaKey
		
		
class ElasticCluster:
	def __init__(self, clusterId_:int, uniqueName_:str, description_:str, listElasticHosts_:List[ElasticHost]):
		self.clusterId=clusterId_
		self.uniqueName=uniqueName_
		self.description=description_
		self.listElasticHosts=listElasticHosts_
		
		
class ElasticClusterList:
	def __init__(self, elasticClusterDbLst_:List[ElasticCluster]):
		self.elasticClusterDbLst=elasticClusterDbLst_
	
'''
Query the Elastic Db Repository
'''
def elasticRepo(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())
	
'''
Add a new Elastic cluster with all node connections
'''
def addElasticCluster(user:str, session:str, clusterUniqueName:str, requestId:str, clusterDescription:str, hostListStr:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['clusterDescription'] = clusterDescription

	api_url = api_url_stem + "/elastic-repo/cluster:add"
	response =requests.put(api_url, data=hostListStr, headers=headers)
	return retObject(response.json())
	
'''
Update current elastic cluster info (cluster name and description)
'''
def updateElasticCluster(user:str, session:str, clusterId:int, requestId:str, clusterUniqueName:str, clusterDescription:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['clusterId'] = clusterId
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['clusterDescription'] = clusterDescription


	api_url = api_url_stem + "/elastic-repo/cluster:update"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Remove Elasticsearch server/cluster connection
'''
def removeElasticCluster(user:str, session:str, requestId:str, clusterUniqueName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName

	api_url = api_url_stem + "/elastic-repo/cluster:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())
	
	
'''
Add a new host to an existing Elastic cluster
'''
def addElasticHost(user:str, session:str, clusterUniqueName:str, requestId:str, elasticHost: ElasticHost):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName

	api_url = api_url_stem + "/elastic-repo/cluster/host:add"
	response =requests.put(api_url, data=elasticHost, headers=headers)
	return retObject(response.json()) # ElasticClusterList


'''
Add a new host to an existing Elastic cluster
'''
def updateElasticHost(user:str, session:str, clusterUniqueName:str, requestId:str, elasticHost: ElasticHost):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName

	api_url = api_url_stem + "/elastic-repo/cluster/host:update"
	response = requests.post(api_url, data=elasticHost, headers=headers)
	return retObject(response.json())
	
	
'''
Add a new host to an existing Elastic cluster
'''
def deleteElasticHost(user:str, session:str, clusterUniqueName:str, requestId:str, hostId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['hostId'] = hostId

	api_url = api_url_stem + "/elastic-repo/cluster/host:delete"
	response = requests.delete(api_url, headers=headers)
	return retObject(response.json())


	
	
'''
Add elastic repo association
'''
def addElasticRepoAssociationTable(user:str, session:str, requestId:str, associationName: str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationName'] = associationName

	api_url = api_url_stem + "/elastic-repo/association:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Delete association
'''
def deleteElasticRepoAssociation(user:str, session:str, requestId:str, associationId: int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationId'] = associationId

	api_url = api_url_stem + "/elastic-repo/association:delete"
	response =requests.delete(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Get Elastic repo associations
'''
def getElasticRepoAssociationTable(user:str, session:str, requestId:str, associationName: str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationName'] = associationName

	api_url = api_url_stem + "/elastic-repo/association:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
Update association
'''
def updateElasticRepoAssociationTable(user:str, session:str, requestId:str, associationId:int, associationName: str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationId'] = associationId
	headers['associationName'] = associationName

	api_url = api_url_stem + "/elastic-repo/association:update"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Add a new SQL/DSL statement to the repo
'''
def addQuery(user:str, session:str, requestId:str, queryObj):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo/management/query:add"
	response =requests.put(api_url, data=queryObj, headers=headers)
	return retObject(response.json())


'''
Delete query statement against a Elasticsearch cluster/server 
'''
def deleteElasticQuery(user:str, session:str, requestId:str, queryId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	api_url = api_url_stem + "/elastic-repo/management/query:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())

'''
Search Dsl/Sql statement by searching a keyword 
'''
def searchElasticQuery(user:str, session:str, requestId:str, stringToSearch:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['stringToSearch'] = stringToSearch
	api_url = api_url_stem + "/elastic-repo/management/query:search"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())
	

'''
Get the list of queries associated with a cluster 
'''
def getQueriesForCluster(user:str, session:str, requestId:str, clusterName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	api_url = api_url_stem + "/elastic-repo/management/query:cluster"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())

	
'''
Get the Dsl/Sql statement by searching a keyword 
'''
def getSpecificQuery(user:str, session:str, requestId:str, clusterUniqueName:str, queryId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['queryId'] = queryId
	
	api_url = api_url_stem + "/elastic-repo/management/query:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())
	
class ElasticQueryParam:
	def __init__(self, queryParamId_:int, queryId_:int, queryParamName_:str, queryParamDefault_:str, queryParamType_:str, queryParamPosition_:str, queryParamOrder_:str):
		self.queryParamId=queryParamId_
		self.queryId=queryId_
		self.queryParamName=queryParamName_
		self.queryParamDefault=queryParamDefault_
		self.queryParamType=queryParamType_
		self.queryParamPosition=queryParamPosition_
		self.queryParamOrder=queryParamOrder_
		
'''
Get all params of the query
'''
def getElasticQueryParams(user:str, session:str, requestId:str, queryId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	api_url = api_url_stem + "/elastic-repo/management/query/params:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())
	
	
'''
Get all params of the query
'''
def getElasticQueryParam(user:str, session:str, requestId:str, queryId:str, paramName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	headers['paramName'] = paramName
	api_url = api_url_stem + "/elastic-repo/management/query/param:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())

	
'''
Get query params 
'''
def addElasticQueryParam(user:str, session:str, fullBody, requestId:str, elasticQueryParam:ElasticQueryParam):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo/management/query/param:add"
	response =requests.put(api_url, data=elasticQueryParam, headers=headers)
	return retObject(response.json())


'''
Delete query params 
'''
def deleteElasticQueryParam(user:str, session:str, requestId:str, queryId:str, paramId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	headers['paramId'] = paramId
	api_url = api_url_stem + "/elastic-repo/management/query/param:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())


'''
Get Input Object for Query execution
'''
def getElasticQueryInputObject(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo/management/query/signature"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())



'''
Set Query Bridge To Cluster
'''
def addQueryBridgeToCluster(user:str, session:str, requestId:str, queryId:str, clusterId:str, active:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	headers['clusterId'] = clusterId
	headers['active'] = active
	api_url = api_url_stem + "/elastic-repo/management/query/bridge:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Delete Query Bridge To Cluster
'''
def deleteElasticQueryBridgeToCluster(user:str, session:str, requestId:str, queryId:str, clusterId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	headers['clusterId'] = clusterId
	api_url = api_url_stem + "/elastic-repo/management/query/bridge:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())


'''
Set Query Bridge To Cluster
'''
def getElasticQueryBridgeToCluster(user:str, session:str, requestId:str, queryId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	
	api_url = api_url_stem + "/elastic-repo/management/query/bridge:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
Get the List of executed sql statements
'''
def getElasticHistStm(user:str, session:str, requestId:str, type:str, stext:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['type'] = type
	headers['stextm,'] = stext
	api_url = api_url_stem + "/elastic-repo/history/stm:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
Copy sql statements to another user
'''
def copyEsHistStm(user:str, session:str, requestId:str, toUserId:int, shaHash:str, type:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['clusterUniqueName'] = str(toUserId)
	headers['shaHash'] = shaHash
	headers['type'] = type

	
	api_url = api_url_stem + "/elastic-repo/history/stm:copy"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())
	

'''
Delete an executed sql statement from your profile
'''
def deleteEsHistStmt(user:str, session:str, requestId:str, shaHash:str, type:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['shaHash'] = shaHash
	headers['type'] = type

	
	api_url = api_url_stem + "/elastic-repo/history/stm:remove"
	response =requests.delete(api_url, data={}, headers=headers)
	return retObject(response.json())

	
'''
Create Elasticsearch Index
'''
def createIndex(user:str, session:str, requestId:str, clusterUniqueName:str, indexName:str, numberOfShards:int, numberOfReplicas:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['clusterUniqueName'] = clusterUniqueName
	headers['indexName'] = indexName
	headers['numberOfShards'] = str(numberOfShards)
	headers['numberOfReplicas'] = str(numberOfReplicas)
	
	api_url = api_url_stem + "/elastic-repo/index:create"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())
	


'''
Remove elasticsearch index
'''
def deleteElasticIndex(user:str, session:str, clusterUniqueName:str, requestId:str, indexName: str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['indexName'] = indexName

	api_url = api_url_stem + "/elastic-repo/cluster/index:remove"
	response = requests.delete(api_url, headers=headers)
	return retObject(response.json())


'''
Copy to index from an Embedded Db query
'''
def copyFromEmbeddedQueryToElastic(user:str, session:str, requestId:str, toElasticClusterName:str, toElasticIndexName:str, fromClusterId:str, fromEmbeddedDatabaseName:str, fromEmbeddedSchemaName:str, sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toElasticIndexName'] = toElasticIndexName
	headers['fromClusterId'] = fromClusterId
	headers['fromEmbeddedDatabaseName'] = fromEmbeddedDatabaseName
	headers['fromEmbeddedSchemaName'] = fromEmbeddedSchemaName
	
	api_url = api_url_stem + "/elastic-repo/index/copy/embedded:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())

'''
Copy Csv to Elastic Index
'''
def copyCsvToElastic(user:str, session:str, requestId:str, toElasticClusterName:str, toIndexName:str, path:str):

	fileType = mimetypes.guess_type(path)
	
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fileType'] = fileType
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toIndexName'] = toIndexName
	
	
	f = open(path, "r")
	api_url = api_url_stem + "/elastic-repo/index/copy/csv:load"
	form_data = {'file': f}
	response =requests.put(api_url, data=form_data, headers=headers)
	f.close()
	return retObject(response.json())


'''
Copy to Elastic Index from another Elastic Dsl query
'''
def copyElasticToElasticViaDsl(user:str, 
							   session:str, 
							   requestId:str, 
							   fromElasticClusterName:str, 
							   fromIndexName:str, 
							   fromHttpVerb:str, 
							   fromElasticApi:str, 
							   fromEndPoint:str, 
							   toElasticClusterName:str, 
							   toIndexName:str, 
							   batchValue:int, 
							   fromHttpPayload:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['fromIndexName'] = fromIndexName
	headers['fromHttpVerb'] = fromHttpVerb
	headers['fromElasticApi'] = fromElasticApi
	headers['fromEndPoint'] = fromEndPoint
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toIndexName'] = toIndexName
	headers['batchValue'] = str(batchValue)
	
	
	api_url = api_url_stem + "/elastic-repo/index/copy/elastic:dsl"
	response =requests.put(api_url, body=fromHttpPayload, headers=headers)
	return retObject(response.json())


'''
Copy to Elastic Index from another Elastic Sql query
'''
def copyElasticToElasticViaSql(user:str, session:str, requestId:str, fromElasticClusterName:str, fromIndexName:str, toElasticClusterName:str, toIndexName:str, fetchSize:int,  batchValue:int, sqlPayload:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['fromIndexName'] = fromIndexName
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toIndexName'] = toIndexName
	headers['fetchSize'] = fetchSize
	headers['batchValue'] = batchValue
	
	api_url = api_url_stem + "/elastic-repo/index/copy/elastic:sql"
	response =requests.put(api_url, data=sqlPayload, headers=headers)
	return retObject(response.json())





'''
Copy to index from Mongo simple search
'''
def copyFromMongoAdhocToElastic(user:str, session:str, requestId:str, toElasticClusterName:str, toElasticIndexName:str, fromMongoClusterName:str, fromMongoDatabaseName:str, fromMongoCollectionName:str, batchValue:str, sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toElasticIndexName'] = toElasticIndexName
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['batchValue'] = batchValue

	
	api_url = api_url_stem + "/elastic-repo/index/copy/mongo/adhoc:mql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())


'''
Copy from Mongo collection to Elastic
'''
def copyFromMongoFullCollectionToElastic(	user:str, 
											session:str, 
											requestId:str, 
											toElasticClusterName:str, 
											toElasticIndexName:str, 
											fromMongoClusterName:str, 
											fromMongoDatabaseName:str, 
											fromMongoCollectionName:str, 
											batchCount:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toElasticIndexName'] = toElasticIndexName
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['batchCount'] = str(batchCount)
	
	
	
	api_url = api_url_stem + "/elastic-repo/index/copy/mongo:collection"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Copy to Elastic Index from a Mongo range search
'''
def copyFromMongoRangeToElastic(user:str, 
								session:str, 
								requestId:str, 
								toElasticClusterName:str, 
								toElasticIndexName:str, 
								fromMongoClusterName:str, 
								fromMongoDatabaseName:str, 
								fromMongoCollectionName:str, 
								itemToSearch:str, 
								fromValue:str, 
								toValue:str, 
								valueSearchType:str, 
								batchValue:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toElasticIndexName'] = toElasticIndexName
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['itemToSearch'] = itemToSearch
	headers['fromValue'] = fromValue
	headers['toValue'] = toValue
	headers['valueSearchType'] = valueSearchType
	headers['batchValue'] = batchValue
	
	
	api_url = api_url_stem + "/elastic-repo/index/copy/mongo:range"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Copy to index from Mongo simple search
'''
def copyFromMongoSimpleQueryToElastic(user:str, 
										session:str, 
										requestId:str, 
										toElasticClusterName:str, 
										toElasticIndexName:str, 
										fromMongoClusterName:str, 
										fromMongoDatabaseName:str, 
										fromMongoCollectionName:str, 
										itemToSearch:str, 
										valueToSearch:str, 
										valueToSearchType:str, 
										operator:str, 
										batchValue:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toElasticIndexName'] = toElasticIndexName
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['itemToSearch'] = itemToSearch
	headers['valueToSearch'] = valueToSearch
	headers['valueToSearchType'] = valueToSearchType
	headers['operator'] = operator
	headers['batchValue'] = batchValue
	
	api_url = api_url_stem + "/elastic-repo/index/copy/mongo:simple"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Copy to index from an RDBMS query
'''
def copyFromRDBMSQueryToElastic(user:str, session:str, requestId:str, toElasticClusterName:str, toElasticIndexName:str, fromRdbmsSchemaName:str, batchValue:str, sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toElasticIndexName'] = toElasticIndexName
	headers['fromRdbmsSchemaName'] = fromRdbmsSchemaName
	headers['batchValue'] = batchValue
	headers['sqlContent'] = sqlContent
	
	api_url = api_url_stem + "/elastic-repo/index/copy/rdbms:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())


'''
Execute a generic adhoc Dsl query
'''
def runAdhocDsl(user:str, session:str, requestId:str, clusterUniqueName:str, httpVerb:str, elasticApi:str, indexName:str, endPoint:str, isOriginalFormat:str, persist:str, comment:str, sqlName:str, httpPayload:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['httpVerb'] = httpVerb
	headers['elasticApi'] = elasticApi
	headers['indexName'] = indexName
	headers['endPoint'] = endPoint
	headers['isOriginalFormat'] = isOriginalFormat
	headers['persist'] = persist
	headers['comment'] = comment
	headers['sqlName'] = sqlName
	
	api_url = api_url_stem + "/elastic-repo/index/dsl/adhoc:run"
	response =requests.post(api_url, data=httpPayload, headers=headers)
	return retObject(response.json())


class QueryType:
	def __init__(self, values_:List[str], queryType_:str, fieldName_:str):
		self.values=values_
		self.queryType=queryType_
		self.fieldName=fieldName_


'''
Query index via native DSL
'''
def searchFuzzyIndex(user:str, session:str, requestId:str, clusterUniqueName:str, indexName:str, fromRecno:int, size:int, queryType:QueryType):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['clusterUniqueName'] = clusterUniqueName
	headers['indexName'] = indexName
	headers['fromRecno'] = fromRecno
	headers['size'] = size
	
	api_url = api_url_stem + "/elastic-repo/index/dsl:fuzzy"
	response =requests.post(api_url, data=queryType, headers=headers)
	return retObject(response.json())


'''
List indeces
'''
def listIndeces(user:str, session:str, requestId:str, clusterUniqueName:str, indexName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['indexName'] = indexName
	api_url = api_url_stem + "/elastic-repo/index/management:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
Update index mapping/properties
'''
def updateIndexMapping(user:str, session:str, requestId:str, clusterUniqueName:str, indexName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['indexName'] = indexName
	api_url = api_url_stem + "/elastic-repo/index/mapping:update"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Execute a repo Elasticquery 
'''
def runElasticQueryFromRepo(user:str, session:str, requestId:str, clusterUniqueName:str, queryId:str, queryType:str, persist:str, comment:str, paramObj:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['queryId'] = queryId
	headers['queryType'] = queryType
	headers['persist'] = persist
	headers['comment'] = comment
	
	api_url = api_url_stem + "/elastic-repo/index/query:run"
	response =requests.post(api_url, data=paramObj, headers=headers)
	return retObject(response.json())


'''
Execute an adhoc SQL statement against an index
'''
def runAdhocSql(user:str, session:str, requestId:str, fullBody):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo/index/sql/adhoc:run"
	response =requests.post(api_url, data=fullBody, headers=headers)
	return retObject(response.json())


'''
Translate Sql to Dsl
'''
def translateSqlToDsl(user:str, session:str, requestId:str, clusterUniqueName:str, sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	api_url = api_url_stem + "/elastic-repo/index/sql:translate"
	response =requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())


'''
Delete snapshot
'''
def deleteElasticSnapshot(user:str, session:str, requestId:str, snapshotId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['snapshotId'] = str(snapshotId)
	api_url = api_url_stem + "/elastic-repo/snapshot:delete"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Get snapshot to visualize
'''
def getElasticSnapshot(user:str, session:str, requestId:str, snapshotId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['snapshotId'] = str(snapshotId)
	api_url = api_url_stem + "/elastic-repo/snapshot:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
Get a list of snapshots to visualize
'''
def getElasticSnapshotHistory(user:str, session:str, requestId:str, ownerId:int, startTime:int, endTime:int, sqlStatement:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['ownerId'] = str(ownerId)
	headers['startTime'] = str(startTime)
	headers['endTime'] = str(endTime)
	headers['sqlStatement'] = sqlStatement
	
	
	api_url = api_url_stem + "/elastic-repo/snapshot:history"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())

class MongoDbId:
	def __init__(self, docId_:int, cName_:int, dbName_:str, clusterName_:str):
		self.docId=docId_
		self.cName=cName_
		self.dbName=dbName_
		self.clusterName=clusterName_
		
class TableColumn:
	def __init__(self, index_:int, columnName_:str, columnTypeId_:str, columnTypeName_:str, length_:str, scale_:str):
		self.index=index_;
		self.columnName=columnName_;
		self.columnTypeId=columnTypeId_;
		self.columnTypeName=columnTypeName_;
		self.length=length_
		self.scale=scale_
	
	
class TableHeader:
	def __init__(self, hList_:{},hListNameToIndex_:{}):
		self.hList=hList_
		self.hListNameToIndex=hListNameToIndex_
	
	

class TableRow:
	def __init__(self, tRow_:{}):
		self.tRow=tRow_
		

class Table:
	def __init__(self, tableHeader_:TableHeader, searchCountSqlAll_:str, searchCountSqlVarchar_:str, searchResultSql_:str, body_:List[TableRow]):
		self.tableHeader=tableHeader_
		self.searchCountSqlAll=searchCountSqlAll_
		self.searchCountSqlVarchar=searchCountSqlVarchar_
		self.searchResultSql=searchResultSql_	
		self.body=body_
		
		
class ResultMetadata:
	def __init__(self, columnName_:str, columnTypeId_:int, columnTypeName_:str, length_:int, scale_:int):
		self.columnName=columnName_
		self.columnTypeId=columnTypeId_
		self.columnTypeName=columnTypeName_
		self.length=length_	
		self.scale=scale_
		
		
class ResultQuery:
	def __init__(self, mongoId_:MongoDbId, 
						streaming_:int, 
						sqlType_:str, 
						user_:str, 
						recordsAffected_:int, 
						columnsAffected_:int, 
						sqlHash_:int, 
						timestamp_:int, 
						sqlId_:int, 
						sqlName_:str, 
						sqlStm_:str, 
						outputFormat_:str,
						outputPackaging_:str,
						metadata_:List[ResultMetadata],
						resultQuery_:str,
						resultQueryJson_:json,
						resultQueryTable_:Table,
						resultQueryByteArray_:List[BYTE],
						comment_:str
						):
		self.mongoId=mongoId_
		self.streaming=streaming_
		self.sqlType=sqlType_
		self.user=user_
		self.recordsAffected=recordsAffected_
		self.columnsAffected=columnsAffected_
		self.sqlHash=sqlHash_
		self.timestamp=timestamp_
		self.sqlId=sqlId_
		self.sqlName=sqlName_
		self.sqlStm=sqlStm_
		self.outputFormat=outputFormat_
		self.outputPackaging=outputPackaging_
		self.metadata=metadata_
		self.resultQuery=resultQuery_
		self.resultQueryJson=resultQueryJson_
		self.resultQueryTable=resultQueryTable_
		self.resultQueryByteArray=resultQueryByteArray_
		self.comment=comment_
		
		
		
		
		
		

'''
Execute Sql or Dsl on multiple clusters / indexes and aggregate results with Sql
'''
def executeAdhocMultipleIndex(user:str, session:str, requestId:str, strObj:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo/execute/adhoc/multiple:aggregate"
	response =requests.put(api_url, data=strObj, headers=headers)
	return retObject(response.json()) # RestObject contains in the payload ResultQuery


'''
Get Execution History for Current User
'''
def getElasticHistory(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo/history/user:personal"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())
	

''' 
 ############################ Elastic Controller END #####################################
'''




''' 
 ############################ Sql Repo Environment Controller START #####################################
'''


class DatabaseTypeList:
	def __init__(self, databaseTypeList_:List[str]):
		self.databaseTypeList=databaseTypeList_
		

'''
Get Database Types List
'''
def GetDatabaseTypes():
	headers = {}
	api_url = api_url_stem + "/db-types"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


class DbConnectionInfo:
	def __init__(self, dbType_:str,dbName_:str,
						server_:str,
						port_:str,
						userName_:str,
						password_:str,
						jdbcDriver_:str,
						dbUrl_:str,
						description_:str,
						warehouse_:str,
						account_:str,
						other_:str,
						connType_:str,
						tunnelLocalPort_:str,
						tunnelRemoteHostAddress_:str,
						tunnelRemoteHostPort_:str,
						tunnelRemoteUser_:str,
						tunnelRemoteUserPassword_:str,
						tunnelRemoteRsaKey_:str
						):
		self.dbType=dbType_
		self.dbType=dbType_
		self.dbName=dbName_
		self.server=server_
		self.port=port_
		self.userName=userName_
		self.password=password_
		self.jdbcDriver=jdbcDriver_
		self.dbUrl=dbUrl_
		self.description=description_
		self.warehouse=warehouse_
		self.account=account_
		self.other=other_
		self.connType=connType_
		self.tunnelLocalPort=tunnelLocalPort_
		self.tunnelRemoteHostAddress=tunnelRemoteHostAddress_
		self.tunnelRemoteHostPort=tunnelRemoteHostPort_
		self.tunnelRemoteUser=tunnelRemoteUser_
		self.tunnelRemoteUserPassword+tunnelRemoteUserPassword_
		self.tunnelRemoteRsaKey=tunnelRemoteRsaKey_

class DatabaseList:
	def __init__(self, listOfDbs_:List[DbConnectionInfo]):
		self.listOfDbs=listOfDbs_

'''
Get Repo List
'''
def GetRepoDbList():
	headers = {}
	api_url = api_url_stem + "/repos"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns DatabaseList in RestObject.payload
''' 
 ############################ Sql Repo Environment Controller END #####################################
'''



''' 
 ############################ Embedded Controller START #####################################
'''


'''
Validate Adhoc Sql on a multiple dbs or an entire cluster
'''
def validateAdhocSqlOnCluster(user:str, session:str, requestId:str, fullBody):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/adhoc/cluster/validate"
	response =requests.post(api_url, data=fullBody, headers=headers)
	return retObject(response.json())

'''
Add user permission to cluster
'''
def addEmbeddedDbToCluster(user:str, session:str, requestId:str, userId: int, clusterId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userId'] = userId
	headers['clusterId'] = clusterId
	api_url = api_url_stem + "/embedded/cluster/access:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Delete User access to cluster
'''
def deleteUserAccessToCluster(user:str, session:str, requestId:str, userId:int, clusterId: int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userId'] = userId
	headers['clusterId'] = clusterId
	api_url = api_url_stem + "/embedded/cluster/access:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())



'''
Get list of Embedded Databases for a cluster
'''
def getUserAccessToCluster(user:str, session:str, requestId:str, clusterId: int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	api_url = api_url_stem + "/embedded/cluster/access:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())

'''
Get Schemas of an embedded db belonging to a cluster
'''
def getSchemas(user:str, session:str, requestId:str, clusterId: int, dbId:int):
	data = {}
	data['user'] = user
	data['session'] = session
	data['requestId'] = requestId
	data['clusterId'] = clusterId
	data['dbId'] = dbId

	response = getSchemas(data)
	return retObject(response.json())


def getSchemas(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/cluster/database/schemas"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
Get Db Tables
'''
def getTables(user:str, session:str, clusterId: int, requestId:str, dbId:int, schema:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	headers['dbId'] = dbId
	headers['schema'] = schema
	api_url = api_url_stem + "/embedded/cluster/database/tables"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
Add Embedded Databases to a cluster
'''
def addEmbeddedDbToCluster(user:str, session:str, requestId:str, er: EmbeddedDbRecord):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/cluster/db:add"
	response =requests.put(api_url, data=er, headers=headers)
	return retObject(response.json())


'''
Delete Embedded Databases of a cluster
'''
def deleteEmbeddedDbToCluster(user:str, session:str, requestId:str, dbId:int, clusterId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['dbId'] = str(dbId)
	headers['clusterId'] = str(clusterId)
	api_url = api_url_stem + "/embedded/cluster/db:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())


'''
Get list of Embedded Databases for a cluster
'''
def getEmbeddedDbToCluster(user:str, session:str, requestId:str, clusterId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	api_url = api_url_stem + "/embedded/cluster/db:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


''' Create an empty database as is part of a cluster '''
def newEmbeddedDbToCluster(user:str, session:str, requestId:str, clusterId:int, dbName:str, dbType: str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	headers['dbName'] = dbName
	headers['dbType'] = dbType
	api_url = api_url_stem + "/embedded/cluster/db:new"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Add Embedded Cluster
'''
def addEmbeddedCluster(user:str, session:str, requestId:str, clusterName:str, description:str, ec: EmbeddedClusterInfo):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['description'] = description
	api_url = api_url_stem + "/embedded/clusters:add"
	response =requests.put(api_url, data=ec, headers=headers)
	return retObject(response.json())

'''
Delete Embedded Cluster
'''
def deleteEmbeddedCluster(user:str, session:str, requestId:str, clusterId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	api_url = api_url_stem + "/embedded/clusters:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())


'''
Get list of Embedded Clusters
'''
def getEmbeddedClusters(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	api_url = api_url_stem + "/embedded/clusters:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
Copy Elastic DSL query result to Embedded table
'''
def copyElasticDslResultToEmbedded(	user:str, 
									session:str, 
									requestId:str,
									fromClusterUniqueName:str, 
									fromMongoDbName:str, 
									fromCollectionName:str, 
									toEmbeddedType:str,
									toEmbeddedDatabaseName:str,
									toClusterId:int,
									toEmbeddedSchemaName:str,
									toEmbeddedTableName:str,
									dsl:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromClusterUniqueName'] = fromClusterUniqueName
	headers['fromMongoDbName'] = fromMongoDbName
	headers['fromCollectionName'] = fromCollectionName
	headers['toEmbeddedType'] = toEmbeddedType
	headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
	headers['toCluster'] = str(toClusterId)
	headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
	headers['toEmbeddedTableName'] = toEmbeddedTableName
	api_url = api_url_stem + "/embedded/copy/elastic:dsl"
	response =requests.put(api_url, data=dsl, headers=headers)
	return retObject(response.json())



'''
Copy Elastic SQL query result to Embedded table
'''
def copyElasticSqlResultToEmbedded(	user:str, 
									session:str, 
									requestId:str, 
									fromElasticClusterName:str, 
									fromElasticFetchSize:str, 
									toEmbeddedType:str, 
									toEmbeddedDatabaseName:str,
									toClusterId:str,
									toEmbeddedSchemaName:str,
									toEmbeddedTableName:str,
									sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['fromElasticFetchSize'] = fromElasticFetchSize
	headers['toEmbeddedType'] = toEmbeddedType
	headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
	headers['toCluster'] = str(toClusterId)
	headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
	headers['toEmbeddedTableName'] = toEmbeddedTableName
	api_url = api_url_stem + "/embedded/copy/elastic:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())


'''
Copy Csv to embedded table
'''
def copyCsvToEmbeddedTable(user:str, 
							session:str, 
							requestId:str, 
							tableScript:str,
							toEmbeddedType:str, 
							toClusterId:int, 
							toEmbeddedDatabaseName:str, 
							toEmbeddedSchemaName:str,
							toEmbeddedTableName:str, 
							filePath:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['tableScript'] = tableScript
	headers['toEmbeddedType'] = toEmbeddedType
	headers['toClusterId'] = str(toClusterId)
	headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
	headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
	headers['toEmbeddedTableName'] = toEmbeddedTableName
	api_url = api_url_stem + "/embedded/copy/embedded/csv:load"
	file = open(filePath, "rb")
	files = {'attachment': file }
	response =requests.put(api_url, files=files, headers=headers)
	file.close()
	return retObject(response.json())


'''
Copy Rdbms Sql result to Embedded table
'''
def copyEmbeddedSqlResultToEmbedded(user:str, 
									session:str, 
									requestId:str, 
									fromEmbeddedType:str, 
									fromClusterId:str, 
									fromEmbeddedDatabaseName:str, 
									fromEmbeddedSchemaName:str, 
									toEmbeddedType:str, 
									toClusterId:int, 
									toEmbeddedDatabaseName:str, 
									toEmbeddedSchemaName:str, 
									toEmbeddedTableName:str, 
									sqlContent:str):

	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromEmbeddedType'] = fromEmbeddedType
	headers['fromClusterId'] = fromClusterId
	headers['fromEmbeddedDatabaseName'] = fromEmbeddedDatabaseName
	headers['fromEmbeddedSchemaName'] = fromEmbeddedSchemaName
	headers['toEmbeddedType'] = toEmbeddedType
	headers['toClusterId'] = str(toClusterId)
	headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
	headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
	headers['toEmbeddedTableName'] = toEmbeddedTableName
	api_url = api_url_stem + "/embedded/copy/embedded:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())


'''
Copy Mongodb collection(s) range search result to Embedded table
'''
def copyMongoRangeSearchResultToEmbedded(user:str, 
										session:str, 
										requestId:str, 
										fromClusterUniqueName:str, 
										fromMongoDbName:str, 
										fromCollectionName:str, 
										itemToSearch:str, 
										fromValue:str,
										toValue:str,
										valueSearchType:str,
										toEmbeddedType:str,
										toEmbeddedDatabaseName:str,
										toCluster:str,
										toEmbeddedSchemaName:str,
										toEmbeddedTableName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromClusterUniqueName'] = fromClusterUniqueName
	headers['fromMongoDbName'] = fromMongoDbName
	headers['fromCollectionName'] = fromCollectionName
	headers['itemToSearch'] = itemToSearch
	headers['fromValue'] = fromValue
	headers['toValue'] = toValue
	headers['valueSearchType'] = valueSearchType
	headers['toEmbeddedType'] = toEmbeddedType
	headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
	headers['toCluster'] = toCluster
	headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
	headers['toEmbeddedTableName'] = toEmbeddedTableName
	api_url = api_url_stem + "/embedded/copy/mongodb/search:range"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Copy records from Mongodb simple search to Embedded table
'''
def copyMongoSimpleSearchResultToEmbedded(	user:str, 
											session:str, 
											requestId:str, 
											fromClusterUniqueName:str, 
											fromMongoDbName:str, 
											fromCollectionName:str, 
											itemToSearch:str, 
											valueToSearch:str,
											valueToSearchType:str,
											operator:str,
											toEmbeddedType:str,
											toEmbeddedDatabaseName:str,
											toCluster:str,
											toEmbeddedSchemaName:str,
											toEmbeddedTableName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromClusterUniqueName'] = fromClusterUniqueName
	headers['fromMongoDbName'] = fromMongoDbName
	headers['fromCollectionName'] = fromCollectionName
	headers['itemToSearch'] = itemToSearch
	headers['valueToSearch'] = valueToSearch
	headers['valueToSearchType'] = valueToSearchType
	headers['operator'] = operator
	headers['toEmbeddedType'] = toEmbeddedType
	headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
	headers['toCluster'] = toCluster
	headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
	headers['toEmbeddedTableName'] = toEmbeddedTableName
	api_url = api_url_stem + "/embedded/copy/mongodb/search:simple"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Copy Mongodb ad-hoc search result to Embedded table
'''
def copyMongoAdhocResultToEmbedded( user:str, 
									session:str, 
									requestId:str, 
									fromClusterUniqueName:str, 
									fromMongoDbName:str, 
									fromCollectionName:str, 
									toEmbeddedType:str,
									toEmbeddedDatabaseName:str,
									toCluster:str,
									toEmbeddedSchemaName:str,
									toEmbeddedTableName:str,
									bsonQuery:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromClusterUniqueName'] = fromClusterUniqueName
	headers['fromMongoDbName'] = fromMongoDbName
	headers['fromCollectionName'] = fromCollectionName
	headers['toEmbeddedType'] = toEmbeddedType
	headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
	headers['toCluster'] = toCluster
	headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
	headers['toEmbeddedTableName'] = toEmbeddedTableName
	api_url = api_url_stem + "/embedded/copy/mongodb:adhoc"
	response =requests.put(api_url, data=bsonQuery, headers=headers)
	return retObject(response.json())



'''
Copy full Mongodb collection to Embedded table
'''
def copyMongoFullCollectionToEmbedded(	user:str, 
										session:str, 
										requestId:str, 
										fromClusterUniqueName:str, 
										fromMongoDbName:str, 
										fromCollectionName:str, 
										itemToSearch:str, 
										valueToSearch:str,
										valueToSearchType:str,
										operator:str,
										toEmbeddedType:str,
										toEmbeddedDatabaseName:str,
										toCluster:str,
										toEmbeddedSchemaName:str,
										toEmbeddedTableName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromClusterUniqueName'] = fromClusterUniqueName
	headers['fromMongoDbName'] = fromMongoDbName
	headers['fromCollectionName'] = fromCollectionName
	headers['itemToSearch'] = itemToSearch
	headers['valueToSearch'] = valueToSearch
	headers['valueToSearchType'] = valueToSearchType
	headers['operator'] = operator
	headers['toEmbeddedType'] = toEmbeddedType
	headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
	headers['toCluster'] = toCluster
	headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
	headers['toEmbeddedTableName'] = toEmbeddedTableName
	api_url = api_url_stem + "/embedded/copy/mongodb:collection"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Copy Rdbms Sql result to Embedded table
'''
def copyRdbmsSqlResultToEmbedded(	user:str, 
									session:str, 
									requestId:str, 
									fromRdbmsSchemaUniqueName:str, 
									toEmbeddedType:str,
									toClusterId:str,
									toEmbeddedDatabaseName:str,
									toEmbeddedSchemaName:str,
									toEmbeddedTableName:str,
									sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromRdbmsSchemaUniqueName'] = fromRdbmsSchemaUniqueName
	headers['toEmbeddedType'] = toEmbeddedType
	headers['toClusterId'] = toClusterId
	headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName 
	headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
	headers['toEmbeddedTableName'] = toEmbeddedTableName
	api_url = api_url_stem + "/embedded/copy/sqlrepo:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())


'''
Get list of supported database types
'''
def getEmbeddedDbTypes(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/dbtypes:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
Execute Adhoc DDL
'''
def getCreateTableStmFromSql(user:str, session:str, requestId:str, sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	api_url = api_url_stem + "/embedded/execute/adhoc/statement/table:create"
	response =requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())



'''
Execute Adhoc Sql on a multiple dbs or an entire cluster
'''
def executeAdhocSqlOnCluster(user:str, session:str, requestId:str, sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/execute/adhoc:cluster"
	response =requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())

'''
Execute Adhoc Ddl
'''
def executeDdl(user:str, session:str, requestId:str, clusterId:int, fileName:str, t:str, schema:str, sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	headers['fileName'] = fileName
	headers['type'] = t
	headers['schema'] = schema
	api_url = api_url_stem + "/embedded/execute/adhoc:ddl"
	response =requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())

'''
Execute Adhoc Sql
'''
def executeInMemAdhocSql(user:str, session:str, requestId:str, sqlType:str, clusterId:int, dbId:int, sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlType'] = sqlType
	headers['clusterId'] = str(clusterId)
	headers['dbId'] = str(dbId)
	api_url = api_url_stem + "/embedded/execute/adhoc:single"
	response =requests.post(api_url, data=sqlContent, headers=headers) 
	return retObject(response.json())


'''
Execute Adhoc Sql
'''
def executeInMemAdhocSql(user:str, session:str, requestId:str, sqlType:str, clusterId:int, dbId:int, sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlType'] = sqlType
	headers['clusterId'] = str(clusterId)
	headers['dbId'] = str(dbId)
	api_url = api_url_stem + "/embedded/execute/inmem/adhoc:single"
	response = requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())



'''
Copy Csv to in mem table
'''
def copyCsvToInMemDb(user:str, session:str, requestId:str, tableScript:str, filePath:str, comment:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['tableScript'] = tableScript
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/csv:load"
	file = open(filePath, "rb")
	files = {'attachment': file }
	response =requests.put(api_url, files=files, headers=headers)
	return retObject(response.json())



'''
Create Empty tables to in mem db
'''
def createEmptyTablesInMemDb(user:str, session:str, requestId:str, comment:str, tableDefinition:List[TableDefinition] ):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/table:empty"
	response =requests.put(api_url, data=tableDefinition, headers=headers) 
	return retObject(response.json())


'''
Append in-mem db
'''
def appendInMemDbTables(user:str, session:str, comment:str, requestId:str, tableDefinition:List[TableDefinition] ):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/table:append"
	response =requests.put(api_url, data=tableDefinition, headers=headers) 
	return retObject(response.json())

'''
Load Elastic Index Dsl query result in memory
'''
def loadElasticIndexInMemViaDsl(user:str, 
								session:str, 
								requestId:str, 
								fromElasticClusterName:str, 
								fromIndexName:str,  
								fromHttpVerb:str, 
								fromElasticApi:str, 
								fromEndPoint:str, 
								batchValue:str, 
								comment:str, 
								dslStatement:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['fromIndexName'] = fromIndexName
	headers['fromHttpVerb'] = fromHttpVerb
	headers['fromElasticApi'] = fromElasticApi
	headers['fromEndPoint'] = fromEndPoint
	headers['batchValue'] = batchValue
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/elastic:dsl"
	response =requests.put(api_url, data=dslStatement, headers=headers) 
	return retObject(response.json())



''' Load Elastic Index Sql query result in memory '''
def loadElasticIndexInMemViaSql(user:str, session:str, requestId:str, fromElasticClusterName:str, fromIndexName:str, fromHttpVerb:str, fromElasticApi:str, fromEndPoint:str, batchValue:str, comment:str, sqlStatement:str): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['fromIndexName'] = fromIndexName
	headers['fromHttpVerb'] = fromHttpVerb
	headers['fromElasticApi'] = fromElasticApi
	headers['fromEndPoint'] = fromEndPoint
	headers['batchValue'] = batchValue
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/elastic:sql"
	response =requests.put(api_url, data=sqlStatement, headers=headers) 
	return retObject(response.json())
	
'''
Copy records to RDBMS table from another Mongodb collection(s) range search
'''
def loadMongoRangeSearchResultInMem(user:str, 
									session:str,
									requestId:str,
									fromMongoClusterName:str, 
									fromMongoDatabaseName:str, 
									fromMongoCollectionName:str, 
									itemToSearch:str, 
									fromValue:str, 
									toValue:str, 
									valueSearchType:str, 
									batchCount:int, 
									comment:str): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['itemToSearch'] = itemToSearch
	headers['fromValue'] = fromValue
	headers['toValue'] = toValue
	headers['valueSearchType'] = valueSearchType
	headers['batchCount'] = str(batchCount)
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/mongodb/search:range"
	response =requests.put(api_url, data={}, headers=headers) 
	return retObject(response.json())


'''
Load in memory result from Mongodb simple search
'''
def loadMongoSimpleSearchResultInMem(user:str, 
									session:str, 
									requestId:str,
									fromMongoClusterName:str, 
									fromMongoDatabaseName:str, 
									fromMongoCollectionName:str, 
									itemToSearch:str, 
									valueToSearch:str, 
									valueToSearchType:str, 
									operator:str, 
									batchCount:int, 
									comment:str): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['itemToSearch'] = itemToSearch
	headers['valueToSearch'] = valueToSearch
	headers['valueToSearchType'] = valueToSearchType
	headers['operator'] = operator
	headers['batchCount'] = batchCount
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/mongodb/search:simple"
	response =requests.put(api_url, data={}, headers=headers) 
	return retObject(response.json())


'''
Copy records to RDBMS table from Mongodb ad-hoc search
'''
def loadMongoFullCollectionInMem(user:str, 
								session:str, 
								requestId:str,
								fromMongoClusterName:str, 
								fromMongoDatabaseName:str, 
								fromMongoCollectionName:str, 
								batchCount:int, 
								comment:str, 
								bsonQuery:str): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['batchCount'] = batchCount
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/mongodb:adhoc"
	response =requests.put(api_url, data=bsonQuery, headers=headers) 
	return retObject(response.json())



'''
Load results in memory full Mongodb collection
'''
def loadMongoFullCollectionInMem(user:str, 
								session:str, 
								requestId:str,
								fromMongoClusterName:str, 
								fromMongoDatabaseName:str, 
								fromMongoCollectionName:str, 
								itemToSearch:str, 
								valueToSearch:str, 
								valueToSearchType:str, 
								operator:str, 
								batchCount:int, 
								comment:str): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['itemToSearch'] = itemToSearch
	headers['valueToSearch'] = valueToSearch
	headers['valueToSearchType'] = valueToSearchType
	headers['operator'] = operator
	headers['batchCount'] = batchCount
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/mongodb:collection"
	response =requests.put(api_url, data={}, headers=headers) 
	return retObject(response.json())





''' Load RDBMS query result in memory '''
def loadRdbmsQueriesInMem(user:str, session:str, requestId:str, schemaUniqueName:str, comment:str, listRdbmsCompoundQuery:List[ListRdbmsCompoundQuery]): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['schemaUniqueName'] = schemaUniqueName
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/rdbms:queries"
	response =requests.put(api_url, body=listRdbmsCompoundQuery, headers=headers) 
	return retObject(response.json())




'''
Load RDBMS query result in memory
'''
def loadRdbmsTablesInMem(user:str, session:str, requestId:str, schemaUniqueName:str, comment:str, listRdbmsTables:List[str]): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['schemaUniqueName'] = schemaUniqueName
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/rdbms:tables"
	response =requests.put(api_url, data=listRdbmsTables, headers=headers) 
	return retObject(response.json())



'''
remove all in-mem storage for request
'''
def removeRequestInMemoryDbs(user:str, session:str, requestId): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/inmem/stores/remove:request"
	response =requests.post(api_url, data={}, headers=headers) 
	return retObject(response.json())

'''
Load cluster in memory 
'''
def loadEmbeddedClusterInMem(user:str, session:str, requestId:str, clusterId:int, comment:str): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = clusterId
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem:cluster"
	response =requests.put(api_url, data={}, headers=headers) 
	return retObject(response.json())


'''
Load database in memory.Database is part of a cluster 
'''
def loadEmbeddedDbInMem(user:str, session:str, requestId:str, clusterId:str, dbId:str, comment:str): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['dbId'] = dbId
	headers['clusterId'] = clusterId
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem:database"
	response =requests.put(api_url, data={}, headers=headers) 
	return retObject(response.json())




'''
Load query result in memory
'''
def loadEmbeddedQueryInMem(user:str, session:str, requestId:str, fromEmbeddedType:str, fromClusterId:int, fromEmbeddedDatabaseName:str, fromEmbeddedSchemaName:str, comment:str, sqlContent:str): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromEmbeddedType'] = fromEmbeddedType
	headers['fromClusterId'] = str(fromClusterId)
	headers['fromEmbeddedDatabaseName'] = fromEmbeddedDatabaseName
	headers['fromEmbeddedSchemaName'] = fromEmbeddedSchemaName
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem:query"
	response =requests.put(api_url, data=sqlContent, headers=headers) 
	return retObject(response.json())


'''
Get a list of in-mem db
'''
def getInMemoryDbs(user:str, session:str, requestId:str): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/inmem:stores"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())

'''
Get list of Sql Commands
'''
def getEmbeddedSqlCommands(user:str, session:str, requestId:str): 
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/staticinfo:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


''' 
 ############################ Embedded Controller END #####################################
'''


''' 
 ############################ Environment Controller START #####################################
'''


'''
Get the log
'''
def log(user:str, session:str, requestId:str, stringToSearch:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['stringToSearch'] = stringToSearch
	api_url = api_url_stem + "/environment/log:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
About this API
'''
def about():
	headers = {}
	api_url = api_url_stem + "/environment:about"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())

''' 
 ############################ Environment Controller END #####################################
'''


''' 
 ############################ Exchange Storage Controller START #####################################
'''



'''
Receive a file form an external exchange
'''
def receiveFilesFromRemoteExchange(user:str, session:str, requestId:str, externalUserEmail:str, externalExchangeUid:str, externalUserPassword:str, toUserEmail:str, fileAttachments:List[str]):
								   
	messageMetadataList  = MessageMetadataList()
	files = {}
	for fileAttachment in fileAttachments:
		fname = pathlib.Path(fileAttachment.filePath)
		f = open(fileAttachment.filePath, "rb")
		mime_type = mimetypes.guess_type(fileAttachment.filePath)
		fileName = ntpath.basename(fileAttachment.filePath);
		file_bytes = f.read()
		f.close()
		files[fileName] = (fileName, file_bytes, mime_type)
		messageMetadata = MessageMetadata(mime_type, fileName, fname.stat().st_ctime, fileAttachment.text)
		messageMetadataList.append(messageMetadata)


	files["filesMetadata"] = ("filesMetadata", json.dumps(messageMetadataList), "text/plain")
	
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/file/receive:remote"
	response =requests.post(api_url, data=files, headers=headers)
	return retObject(response.json())



class FileDescriptor:
	def __init__(self, description_:int, path_:str, fileName_:str, type_:str,  sqls_:List[str]):
		self.description=description_
		self.path=path_
		self.fileName=fileName_
		self.type=type_
		self.sqls=sqls_
		

class FileDescriptorList:
	def __init__(self, fileDescriptorList_:List[FileDescriptor]):
		self.fileDescriptorList=fileDescriptorList_

'''
Send a file from this exchange/instance to a remote exchange/instance
'''
def sendFileToRemoteExchange(user:str, 
							 session:str, 
							 requestId:str, 
							 toExchangeId:str, 
							 externalUserPassword:str, 
							 toUserEmail:str, 
							 fileDescriptorList:FileDescriptorList):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	headers['toExchangeId'] = toExchangeId
	headers['externalUserPassword'] = externalUserPassword
	headers['toUserEmail'] = toUserEmail
	api_url = api_url_stem + "/exchange/file/send:remote"
	response =requests.post(api_url, data=fileDescriptorList, headers=headers)
	return retObject(response.json())
	
	
class SnapshotDbRecord:
	def __init__(self, snapshotId_:int, fileName_:str, sqlName_:str, type_:str, userId_:int, timestamp_:int, sqlStatement_:str):
		self.snapshotId=snapshotId_
		self.fileName=fileName_
		self.sqlName=sqlName_
		self.type=type_
		self.userId=str(userId_)
		self.timestamp=str(timestamp_)
		self.sqlStatement=sqlStatement_


class SnapshotDbRecordList:
	def __init__(self, snapshotDbRecordList_:List[SnapshotDbRecord]):
		self.snapshotDbRecordList=snapshotDbRecordList_


'''
Get List of Files from a saved Files Repo
'''
def getSnapshot(user:str, session:str, requestId:str, startTime:int, endTime:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['startTime'] = str(startTime)
	headers['endTime'] = str(endTime)
	
	api_url = api_url_stem + "/exchange/file/hist:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # RestObject with payload as SnapshotDbRecordList



'''
Delete a file on this Data Exchange Server or own file on remote
'''
def deleteFileFromLocalRequest(user:str, session:str, requestId:str, fileId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fileId'] = fileId
	api_url = api_url_stem + "/exchange/file/delete:local"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  #
		
		
		

class EmbeddedDbRecordList:
	def __init__(self, embeddedDbRecordList_:List[EmbeddedDbRecord]):
		self.embeddedDbRecordList=embeddedDbRecordList_
'''
Get List of Files from a saved Files Repo
'''
def moveAndAttachH2FromExchange(user:str, session:str, requestId:str, fileId:int, clusterId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fileId'] = str(fileId)
	headers['clusterId'] = str(clusterId)
	
	api_url = api_url_stem + "/exchange/file/hist:get"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  # RestObject with payload as EmbeddedDbRecordList	
	

'''
Add new remote exchange, local exchange can interact with
'''
def addNewRemoteExchange(user:str, session:str, requestId:str, exchangeAddress:str, exchangeName:str, exchangeUid:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	headers['exchangeAddress'] = exchangeAddress
	headers['exchangeName'] = exchangeName
	headers['exchangeUid'] = exchangeUid
	api_url = api_url_stem + "/exchange/exchange:new"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())	

	return retObject(response.json())  # returns EmbeddedDbRecordList in  RestObjec.payload 
	
class ExchangeRecord:
	def __init__(self, id_:int, exchangeAddress_:int, exchangeName_:str, exchangeUid_:str):
		self.id=id_
		self.exchangeAddress=exchangeAddress_
		self.exchangeName=exchangeName_
		self.exchangeUid=exchangeUid_
		
		
class ExchangeList:
	def __init__(self, exchangeList_:List[ExchangeRecord]):
		self.exchangeList=exchangeList_
		
		
'''
Update remote exchange, local exchange can interact with. This end point will not update own exchange
'''
def updateRemoteExchange(user:str, session:str, requestId:str, id:int, exchangeAddress:str, exchangeName:str, exchangeUid:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	headers['id'] = str(id)
	headers['exchangeAddress'] = exchangeAddress
	headers['exchangeName'] = exchangeName
	headers['exchangeUid'] = exchangeUid
	api_url = api_url_stem + "/exchange/exchange:update"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())	# returns ExchangeRecord in the RestObject.payload


'''
Delete Exchange
'''
def deleteRemoteExchange(user:str, session:str, requestId:str, exchangeUid:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	headers['exchangeUid'] = exchangeUid
	api_url = api_url_stem + "/exchange/exchange:delete"
	response =requests.delete(api_url, data={}, headers=headers)
	return retObject(response.json()) # returns ExchangeRecord in the RestObject.payload


		
		
'''
Get exchange info
'''
def getAllExchanges(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/exchange:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) #returns ExchangeRecordList in the RestObject.payload


'''
Search exchanges
'''
def searchExchanges(user:str, session:str, requestId:str, exchange:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['exchange'] = exchange
	api_url = api_url_stem + "/exchange/exchange/search:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) #returns ExchangeRecordList in the RestObject.payload
	

		
		
		
'''
Get exchange info
'''
def getAssociatedExchanges(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/exchange/associated:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	# returns ExchangeList in the RestObject.payload

class UserDbRecord:
	def __init__(self, id_:int, internalUserId_:int, email_:str, exchangeId_:int, isAdmin_:str, userPassword_:str):
		self.id=id_
		self.internalUserId=internalUserId_
		self.email=email_
		self.exchangeId=exchangeId_
		self.isAdmin=isAdmin_
		self.userPassword=userPassword_
		
		
class UserDbList:
	def __init__(self, userDbLst_:List[UserDbRecord]):
		self.userDbLst=userDbLst_
		
		
		
'''
Get exchange info
'''
def getExchangeUsers(user:str, session:str, requestId:str, email:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['email'] = email
	api_url = api_url_stem + "/exchange/users/email:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	# returns UserDbList in the RestObject.payload

'''
Get exchange info
'''
def getUsersByExchange(user:str, session:str, requestId:str, exchangeId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['exchangeId'] = str(exchangeId)
	api_url = api_url_stem + "/exchange/users/email:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	# returns UserDbList in the RestObject.payload
	
'''
Get associated exchanges to a user
'''
def getAssociatedUsersToExchange(user:str, session:str, requestId:str, userId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userId'] = str(userId)
	api_url = api_url_stem + "/exchange/user/exchanges:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	# returns ExchangeList in the RestObject.payload

'''
Get own user info related to exchanges
'''
def getCurrentUserInfo(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/user/own:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	# returns UserDbRecord in the RestObject.payload


'''
Add new user to this exchange
'''
def addNewExchangeUser(user:str, session:str, requestId:str, email:str, exchangeId:int, isAdmin:str, userPassword:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	headers['email'] = email
	headers['exchangeId'] = str(exchangeId)
	headers['isAdmin'] = isAdmin
	headers['userPassword'] = userPassword
	api_url = api_url_stem + "/exchange/user:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())	# returns UserDbRecord in the RestObject.payload

'''
Delete user from current exchange
'''
def deleteExchangeUser(user:str, session:str, requestId:str, id:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)
	api_url = api_url_stem + "/exchange/user:delete"
	response =requests.delete(api_url, data={}, headers=headers)
	return retObject(response.json()) # returns ExchangeRecord in the RestObject.payload


'''
Update external users password
'''
def updateExternalUserPasswordByAdmin(user:str, session:str, requestId:str, id:int, password:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)
	headers['password'] = password
	
	api_url = api_url_stem + "/exchange/user:update"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())	# returns requestId in the RestObject.payload, RestObject.error depicts the success or failure
	
'''
Update user from current exchange
'''
def updateExternalUserPasswordByAdmin(user:str, session:str, requestId:str, externalUserEmail:str, externalExchangeUid:str, externalUserPassword:str, newExternalUserPassword:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['externalUserEmail'] = externalUserEmail
	headers['externalExchangeUid'] = externalExchangeUid
	headers['externalUserPassword'] = externalUserPassword
	headers['newExternalUserPassword'] = newExternalUserPassword
	
	api_url = api_url_stem + "/exchange/user/self:update"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())	# returns requestId in the RestObject.payload, RestObject.error depicts the success or failure


class UserToExchangeDbRecord:
	def __init__(self, id_:int, userId_:int, exchangeId_:int, isAdmin_:str):
		self.id=id_
		self.userId=userId_
		self.exchangeId=exchangeId_
		self.isAdmin=isAdmin_

		

class UserToExchangeDbList:
	def __init__(self, userToExchangeDbRecordLst_:List[UserToExchangeDbRecord]):
		self.userToExchangeDbRecordLst=userToExchangeDbRecordLst_


'''
Get user info for a certain exchange
'''
def getUserToExchanges(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/user/exchange:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	# returns UserToExchangeDbList in the RestObject.payload

class UserToExchangeDbRecordExtended:
	def __init__(self, id_:int, userId_:int, internalUserId_:int, userEmail_:str, exchangeId_:int, exchangeUid_:str, exchangeName_:str):
		self.id=id_
		self.userId=userId_
		self.internalUserId=internalUserId_
		self.userEmail=userEmail_
		self.exchangeId=exchangeId_
		self.exchangeUid=exchangeUid_
		self.exchangeName=exchangeName_
		

		
class UserToExchangeDbRecordExtendedList:
	def __init__(self, userToExchangeDbExtendedRecordLst_:List[UserToExchangeDbRecordExtended]):
		self.userToExchangeDbExtendedRecordLst=userToExchangeDbExtendedRecordLst_
'''
Get user to exchange extended info
'''
def getUserToExchangesExtended(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/user/exchange:getExtended"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	# returns UserToExchangeDbRecordExtended in the RestObject.payload
	
'''
Add new user to current exchange
'''
def addUserToExchange(user:str, session:str, requestId:str, userId:int, exchangeId:int, isAdmin:str ):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userId'] = userId
	headers['exchangeId'] = exchangeId
	headers['isAdmin'] = isAdmin
	api_url = api_url_stem + "/exchange/user/exchange:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())	# returns requestId in the RestObject.payload, RestObject.error depicts the success or failure
	
'''
Delete user from current exchange
'''
def deleteUserToExchange(user:str, session:str, requestId:str, userId:int, exchangeId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userId'] = userId
	headers['exchangeId'] = exchangeId
	api_url = api_url_stem + "/exchange/user/exchange:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())	# returns requestId in the RestObject.payload, RestObject.error depicts the success or failure


class ExchangeFileDbRecord:
	def __init__(self, id_:int, exchangeId_:int, exchangeUid_:str, fromUserId_:int, toUserId_:int, fileName_:str, fileType_:str):
		self.id=id_
		self.exchangeId=exchangeId_
		self.exchangeUid=exchangeUid_
		self.fromUserId=fromUserId_
		self.toUserId=toUserId_
		self.fileName=fileName_
		self.fileType=fileType_
		
		
	
class ExchangeFileDbList:
	def __init__(self, exchangeCompanyDbRecordLst_:List[ExchangeFileDbRecord]):
		self.exchangeCompanyDbRecordLst=exchangeCompanyDbRecordLst_
		
		
'''
Query Files, sent or received
'''
def queryExchange(user:str, session:str, requestId:str, externalUserPassword:str, fromUserEmail:str, toUserEmail:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['externalUserPassword'] = externalUserPassword
	headers['fromUserEmail'] = fromUserEmail
	headers['toUserEmail'] = toUserEmail
	
	api_url = api_url_stem + "/exchange/local:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	# returns ExchangeFileDbList in the RestObject.payload

'''
Query Files, sent or received
'''
def queryExchange(user:str, session:str, requestId:str, externalUserEmail:str, externalUserPassword:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['externalUserPassword'] = externalUserPassword
	headers['externalUserEmail'] = externalUserEmail

	
	api_url = api_url_stem + "/exchange/remote:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	# returns ExchangeFileDbList in the RestObject.payload


'''
Generate uid
'''
def generateUid(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/generate:uid"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	# returns GenericResponse in the RestObject.payload


'''
Generate strong password
'''
def generateStrongPassword(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/generate:password"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	# returns GenericResponse in the RestObject.payload

	
''' 
 ############################ Exchange Storage Controller END #####################################
'''


''' 
 ############################ Firebase Controller Start #####################################
'''

'''
Send message to firebase topic
'''
def postToTopic(user:str, session:str, requestId:str, topic:str, message:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/firebase/topics/" + topic
	response =requests.post(api_url, data=message, headers=headers)
	return retObject(response.json())	# returns String


class ConditionMessageRepresentation:
	def __init__(self, condition_:str, data_:str):
		self.condition=condition_
		self.data=data_
		
		
'''
Send message to firebase queues enforced by condition
'''
def postToTopic(user:str, session:str, requestId:str, condition:ConditionMessageRepresentation):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/firebase/condition" 
	response =requests.post(api_url, data=condition, headers=headers)
	return retObject(response.json())	# returns String
	
	
	
class MulticastMessageRepresentation:
	def __init__(self, registrationTokens_:List[str], data_:str):
		self.registrationTokens=registrationTokens_
		self.data=data_

	
'''
Send message to a subscribing list of device
'''
def postToClient(user:str, session:str, requestId:str, r:MulticastMessageRepresentation):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	api_url = api_url_stem + "/firebase/clients"
	response =requests.post(api_url, data=r, headers=headers)
	return retObject(response.json())	# returns String


'''
Create subscription for a device
'''
def createSubscription(user:str, session:str, requestId:str, topic:str, registrationTokens:List[str]):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	api_url = api_url_stem + "/firebase/subscriptions/" + topic
	response =requests.post(api_url, data=registrationTokens, headers=headers)
	return retObject(response.json())	# returns String


'''
Delete subscription for a device
'''
def deleteSubscription(user:str, session:str, requestId:str, topic:str, registrationToken:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	api_url = api_url_stem + "/firebase/subscriptions/" + topic + "/" + registrationToken
	response =requests.post(api_url, data=registrationToken, headers=headers)
	return retObject(response.json())	# returns String




	
	
''' 
 ############################ Firebase Controller END #####################################
'''



''' 
 ############################ Health Controller START #####################################
'''




'''
healthCheckSelf
'''
def healthCheckSelf(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/health/healthCheck"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
healthCheckOther
'''
def healthCheckOther(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/health/healthCheck/baseAddress"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
healthCheckThat
'''
def healthCheckThat(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/health/healthCheck/baseAddress/browser/{baseAddress}"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
healthCheckRegisteredNodes
'''
def healthCheckRegisteredNodes(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/health/healthCheck/registeredNodes"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
ping
'''
def ping(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/health/ping"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
pingAnotherNode
'''
def pingAnotherNode(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/health/ping/node/{baseAddress}"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
pingRegisteredNodes
'''
def pingRegisteredNodes(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/health/ping/registeredNodes"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
pingUrl
'''
def pingUrl(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/health/ping:url"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
testAdminAccount
'''
def testAdminAccount(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/health/test/adminAccount"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
testAdminAccount_
'''
def testAdminAccount_(user:str, session:str, requestId:str, admin, adminPasscode):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/health/test/adminAccount/browser/" + admin + "/" + adminPasscode
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
testUserAccount
'''
def testUserAccount(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/health/test/userAccount"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
testUserAccount_
'''
def testUserAccount_(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/health/test/userAccount/browser/{user}/{userPasscode}"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())

''' 
 ############################ Health Controller END #####################################
'''


	
''' 
 ############################ Internal Storage Controller START #####################################
'''


'''
Upload a generic file, on internal storage
'''
def uploadFile(user:str, session:str, requestId:str, machineName:str, fullPath:str, lastModified:int, storageType:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['machineName'] = machineName
	headers['fullPath'] = fullPath
	headers['lastModified'] = str(lastModified)
	headers['storageType'] = storageType
	api_url = api_url_stem + "/internalStorage/uploadFile"
	f = open(fullPath, "r")
	form_data = {'attacment': f}
	response =requests.post(api_url, data=form_data, headers=headers)
	f.close()
	return retObject(response.json())

'''
Download file from internal storage 
'''
def downloadFile(user:str, session:str, requestId:str, machineName:str, filename:str, lastModified:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['machineName'] = machineName
	headers['machineName'] = machineName
	headers['filename'] = filename
	headers['lastModified'] = str(lastModified)
	api_url = api_url_stem + "/internalStorage/downloadFile"

	with requests.g(api_url, headers=headers) as f:
		return f.read().decode('utf-8')
	

	
	
'''
Delete file from internal storage
'''
def deleteFile(user:str, session:str, requestId:str, storageId:int, fileName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['storageId'] = str(storageId)
	headers['fileName'] = fileName
	
	api_url = api_url_stem + "/internalStorage/deleteFile"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())


class FileList:
	def __init__(self, listOfFiles_:List[str]):
		self.listOfFiles=listOfFiles_

'''
List all files in internal storage
'''
def listFilesInFolder(user:str, session:str, requestId:str, appName:str, folder:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['appName'] = appName
	headers['folder'] = folder
	api_url = api_url_stem + "/internalStorage/list"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns FileList in RestObject.payload
	
class InternalFileStorageRecord:
	def __init__(self, storageId_:int, userId_:int, machineName_:str, fileName_:str, fullFilePath_:str, storageType_:str, lastModified_:int, timeStamp_:int):
		self.storageId=storageId_
		self.userId=userId_
		self.machineName=machineName_;
		self.fileName=fileName_
		self.fullFilePath=fullFilePath_
		self.storageType=storageType_
		self.lastModified=lastModified_
		self.timeStamp=timeStamp_
		
class InternalFileStorageList:
	def __init__(self, fileStorageTableDbRecordLst_:List[InternalFileStorageRecord]):
		self.fileStorageTableDbRecordLst=fileStorageTableDbRecordLst_

'''
List all files in internal storage for current user
'''
def getFilesByUser(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/internalStorage/user/list"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns InternalFileStorageList in RestObject.payload


class InternalStoragePriv:
	def __init__(self, privId_:int, storageId_:int, userId_:int, privType_:str):
		self.privId=privId_
		self.storageId=storageId_
		self.userId=userId_
		self.privType=privType_


class InternalStoragePrivList:
	def __init__(self, storagePrivRepoDbRecordbLst_:List[InternalStoragePriv]):
		self.storagePrivRepoDbRecordbLst=storagePrivRepoDbRecordbLst_

	
'''
Get user priviledges for a stored file
'''
def getFilePriv(user:str, session:str, requestId:str, storageId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['requestId'] = str(storageId)
	
	api_url = api_url_stem + "/internalStorage/storage/priv"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns InternalStoragePrivList in RestObject.payload


'''
Add internal user to a file uploaded by another internal user
'''
def addUserToFile(user:str, session:str, requestId:str, storageId:int, userId:int, privType:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['storageId'] = str(storageId)
	headers['userId'] = str(userId)
	headers['privType'] = privType
	
	api_url = api_url_stem + "/internalStorage/user:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())
	

'''
Add internal user to a file uploaded by another internal user
'''
def deleteUserToFile(user:str, session:str, requestId:str, storageId:int, userId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['storageId'] = str(storageId)
	headers['userId'] = str(userId)
	
	api_url = api_url_stem + "/internalStorage/user:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())

'''
Get file type
'''
def getFileType(user:str, session:str, requestId:str, storageId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['storageId'] = str(storageId)
	api_url = api_url_stem + "/internalStorage/storage/type"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())

	
	
'''
Move and attach embedded db from storage
'''
def moveAndAttachH2FromStorage(user:str, session:str, requestId:str, storageId:int, clusterId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['storageId'] = str(storageId)
	headers['clusterId'] = str(clusterId)
	api_url = api_url_stem + "/internalStorage/storage/attach-h2"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())

class DetectFileType:
	def __init__(self, type_:str):
		self.type=type_
		
'''
Import files such as JSON tables or RestResponse into attached systems
'''
def importFile(user:str, session:str, requestId:str, storageId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['storageId'] = str(storageId)
	api_url = api_url_stem + "/internalStorage/storage/import"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json()) # returns DetectFileType in RestObject.payload


''' 
 ############################ Internal Storage Controller END #####################################
'''


''' 
 ############################ Logging Controller START #####################################
'''


'''
Query internal log
'''
def queryInternalLog(user:str, session:str, requestId:str, startTime:int, endTime:int, stringToSearch:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['startTime'] = startTime
	headers['endTime'] = endTime
	headers['stringToSearch'] = stringToSearch
	
	api_url = api_url_stem + "/logging/internal/logs:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())
	
	
'''
Set application
'''
def setApplication(user:str, session:str, requestId:str, application:str, partitionType:str, repositoryType:str, repositoryId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['requestId'] = requestId
	
	headers['application'] = application
	headers['partitionType'] = partitionType
	headers['repositoryType'] = repositoryType
	headers['repositoryId'] = repositoryId
	
	api_url = api_url_stem + "/logging/application:set"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json()) # returns ApplicationRecord in RestObject.payload
	
'''
Remove Application Logs
'''
def deleteApplication(user:str, session:str, requestId:str, applicationId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	api_url = api_url_stem + "/logging/application:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())
	
	
class ApplicationRecord:
	def __init__(self, applicationId_:int, application_:str, partitionType_:str, repositoryType_:str, repositoryId_:int):
		self.applicationId=applicationId_
		self.application=application_
		self.partitionType=partitionType_
		self.repositoryType=repositoryType_
		self.repositoryId=repositoryId_
		
		
		
class ApplicationRecordList:
	def __init__(self, applicationRecordList_:List[ApplicationRecord]):
		self.applicationRecordList=applicationRecordList_
		
'''
Get All Applications
'''
def getAllApplications(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/logging/application:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns ApplicationRecordList in RestObject.payload
	
'''
Get All Partitions of an application
'''
def getApplicationPartitions(user:str, session:str, requestId:str, applicationId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	api_url = api_url_stem + "/logging/application/partition:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())



'''
Remove Application Logs
'''
def deleteApplicationLogs(user:str, session:str, requestId:str, applicationId:int, fromEpochMilliseconds:int, toEpochMilliseconds:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	headers['fromEpochMilliseconds'] = fromEpochMilliseconds
	headers['toEpochMilliseconds'] = toEpochMilliseconds
	
	api_url = api_url_stem + "/logging/application/logs:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())



	
	

'''
Add new log entry
'''
def addLogEntry(user:str, session:str, requestId:str, applicationId:int, message:str, messageType:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	headers['message'] = message
	headers['messageType'] = messageType
	
	api_url = api_url_stem + "/logging/application/logs/entry:add"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Add new log entry with artifact/attached file
'''
def addLogEntryWithArtifact(user:str, session:str, requestId:str, applicationId:int, message:str, messageType:str, artifactName:str, artifactType:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	headers['message'] = message
	headers['messageType'] = messageType
	headers['artifactName'] = artifactName
	headers['artifactType'] = artifactType
	
	api_url = api_url_stem + "/logging/application/logs/artifact:add"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Query logs
'''
def queryLogs(user:str, session:str, requestId:str, applicationId:int, fromEpochMilliseconds:int, toEpochMilliseconds:int, textToSearch:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	headers['fromEpochMilliseconds'] = fromEpochMilliseconds
	headers['toEpochMilliseconds'] = toEpochMilliseconds
	headers['textToSearch'] = textToSearch
	
	api_url = api_url_stem + "/logging/application/logs:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns LogRecordList in RestObject.payload


class LogRecord:
	def __init__(self, id_:int, hostId_:int, userId_:int, timestamp_:int, message_:str, messageType_:str,artifactName_:str, artifactType_:str):
		self.id=id_;
		self.hostId=hostId_;
		self.userId=userId_;
		self.timestamp=timestamp_;
		self.message=message_;
		self.messageType=messageType_;
		self.artifactName=artifactName_;
		self.artifactType=artifactType_;
			
		
class LogRecordList:
	def __init__(self, logRecordFromDbList_:List[LogRecord]):
		self.logRecordFromDbList=logRecordFromDbList_
		
		
'''
Query logs
'''
def getLogEntry(user:str, session:str, requestId:str, applicationId:int, timestampMilliseconds:int, entryId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	headers['timestampMilliseconds'] = timestampMilliseconds
	headers['entryId'] = entryId
	api_url = api_url_stem + "/logging/application/logs:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	# returns LogRecordList in RestObject.payload
	
class ApplicationPartitionRecord:
	def __init__(self, partitionId_:int, applicationId_:int, file_:str, fromTime_:int, toTime_:int):
		self.partitionId=partitionId_
		self.applicationId=applicationId_;
		self.file=file_;
		self.fromTime=fromTime_;
		self.toTime=toTime_;
		
		
class ApplicationRecord:
	def __init__(self, applicationId_:int, partitionType_:str, repositoryType_:str, repositoryId_:int, applicationPartitionRecordList_:List[ApplicationPartitionRecord]):
		self.application=applicationId_
		self.partitionType=partitionType_
		self.repositoryType=repositoryType_
		self.repositoryId=repositoryId_
		self.applicationRecordList=applicationPartitionRecordList_
		
	
'''
Query logs
'''
def getLogArtifact(user:str, session:str, requestId:str, applicationId:int, timestampMilliseconds:int, entryId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	headers['timestampMilliseconds'] = timestampMilliseconds
	headers['entryId'] = entryId
	api_url = api_url_stem + "/logging/application/artifact:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) #returns ApplicationRecord in RestObject.payload


''' 
 ############################ Logging Controller END #####################################
'''



''' 
 ############################ ML Controller START #####################################
'''


'''
Add Interpreter and its metadata information
'''
def runModel(user:str, session:str, requestId:str, mlApiUniqueName:str, interpreterVersion:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mlApiUniqueName'] = mlApiUniqueName
	headers['interpreterVersion'] = interpreterVersion
	api_url = api_url_stem + "/ml/model:run"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Get information about ML APi installed
'''
def getAllMlApiServer(user:str, session:str, requestId:str, mlApiUniqueName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mlApiUniqueName'] = mlApiUniqueName
	api_url = api_url_stem + "/ml/mlApi:get"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())
	
'''
Start ML API Server
'''
def startMlApiServer(user:str, session:str, requestId:str, interpreterName:str, interpreterVersion:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterName'] = interpreterName
	headers['interpreterVersion'] = interpreterVersion
	
	api_url = api_url_stem + "/ml/mlApi:start"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Add Interpreter and its metadata information
'''
def createNewMlApiServer(user:str, session:str, requestId:str, interpreterName:str, interpreterVersion:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterName'] = interpreterName
	headers['interpreterVersion'] = interpreterVersion
	api_url = api_url_stem + "/ml/mlApi:new"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())
	
	
'''
Add 
'''
def addMlApiStub(user:str, session:str, requestId:str, interpreterName:str, interpreterVersion:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterName'] = interpreterName
	headers['interpreterVersion'] = interpreterVersion
	api_url = api_url_stem + "/ml/mlApi/deployment:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Add 
'''
def deleteMlApiStub(user:str, session:str, requestId:str, interpreterName:str, interpreterVersion:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterName'] = interpreterName
	headers['interpreterVersion'] = interpreterVersion
	api_url = api_url_stem + "/ml/mlApi/deployment:delete"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Add Interpreter and its metadata information
'''
def stopMlApiServer(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/ml/mlApi:stop"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())




''' 
 ############################ ML Controller END #####################################
'''


''' 
 ############################ MongoDb Controller START #####################################
'''
class MongoClusterMaxRecord:
	def __init__(self, uniqueName_:str, maxCount_:int):
		self.uniqueName=uniqueName_
		self.maxCount=maxCount_
	
class MongoClusterRecord:
	def __init__(self, clusterId_:int, 
						uniqueName_:str, 
						connString_:str, 
						storageType_:str, 
						controllerId_:int, 
						startPeriod_:int, 
						endPeriod_:int, 
						sizeMb_:int, 
						countObjects_:int, 
						tunnelRemoteHostAddress_:str,
						tunnelLocalPort_:int, 
						tunnelRemoteHostPort_:int,
						tunnelRemoteUser_:str, 
						tunnelRemoteUserPassword_:str,
						tunnelRemoteRsaKey_:str,
						maxDbTemp_:MongoClusterMaxRecord,
						maxDbUser_:int,
						maxCountUser_:int):
		self.clusterId=clusterId_;
		self.uniqueName=uniqueName_;
		self.connString=connString_;
		self.storageType=storageType_;
		self.controllerId=controllerId_;
		self.startPeriod=startPeriod_;  
		self.endPeriod=endPeriod_;
		self.sizeMb=sizeMb_;
		self.countObjects=countObjects_;
		self.tunnelLocalPort=tunnelLocalPort_;
		self.tunnelRemoteHostAddress=tunnelRemoteHostAddress_;
		self.tunnelRemoteHostPort=tunnelRemoteHostPort_;
		self.tunnelRemoteUser=tunnelRemoteUser_;
		self.tunnelRemoteUserPassword=tunnelRemoteUserPassword_;
		self.tunnelRemoteRsaKey=tunnelRemoteRsaKey_;
		self.maxDbTemp=maxDbTemp_;
		self.maxDbUser=maxDbUser_;
		self.maxCountUser=maxCountUser_;
		
		
class MongoClusterList:
	def __init__(self, mongoClusterLst_:List[MongoClusterRecord]):
		self.mongoClusterLst=mongoClusterLst_
		
'''
Reload Repo List
'''
def reloadMongoRepo(user, session, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/mongo-repo:reload"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())  # returns MongoClusterList in RestObject.payload


'''
Get the Mongo Db Repository
'''
def getMongoRepo(user, session, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/mongo-repo:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #  # returns MongoClusterList in RestObject.payload
	
'''
Add a new Mongo database/cluster connection to the list of available databases/cluster connections
'''
def mongoRepoAdd(user:str, 
				session:str, 
				requestId:str,
				clusterUniqueName:str,
				connString:str,
				storageType:str,
				startPeriod:int,
				endPeriod:int,
				tunnelLocalPort:int,
				tunnelRemoteHostAddress:str,
				tunnelRemoteHostPort:int,
				tunnelRemoteHostUser:str,
				tunnelRemoteHostUserPassword:str,
				tunnelRemoteHostRsaKey:str
				):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['clusterUniqueName'] = clusterUniqueName
	headers['connString'] = connString
	headers['storageType'] = storageType
	headers['startPeriod'] = str(startPeriod)
	headers['endPeriod'] = str(endPeriod)
	headers['tunnelLocalPort'] = str(tunnelLocalPort)
	headers['tunnelRemoteHostAddress'] = tunnelRemoteHostAddress
	headers['tunnelRemoteHostPort'] = str(tunnelRemoteHostPort)
	headers['tunnelRemoteHostUser'] = tunnelRemoteHostUser
	headers['tunnelRemoteHostUserPassword'] = tunnelRemoteHostUserPassword
	headers['tunnelRemoteHostRsaKey'] = tunnelRemoteHostRsaKey
	
	api_url = api_url_stem + "/mongo-repo/cluster:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Update a Mongo database/cluster connection
'''
def mongoRepoUpdate(user:str, 
					session:str, 
					requestId:str, 
					clusterId:int,
					clusterUniqueName:str,
					connString:str,
					storageType:str,
					startPeriod:int,
					endPeriod:int,
					tunnelLocalPort:int,
					tunnelRemoteHostAddress:str,
					tunnelRemoteHostPort:int,
					tunnelRemoteHostUser:str,
					tunnelRemoteHostUserPassword:str,
					tunnelRemoteHostRsaKey:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	headers['clusterUniqueName'] = clusterUniqueName
	headers['connString'] = connString
	headers['storageType'] = storageType
	headers['startPeriod'] = str(startPeriod)
	headers['endPeriod'] = str(endPeriod)
	headers['tunnelLocalPort'] = str(tunnelLocalPort)
	headers['tunnelRemoteHostAddress'] = tunnelRemoteHostAddress
	headers['tunnelRemoteHostPort'] = str(tunnelRemoteHostPort)
	headers['tunnelRemoteHostUser'] = tunnelRemoteHostUser
	headers['tunnelRemoteHostUserPassword'] = tunnelRemoteHostUserPassword
	headers['tunnelRemoteHostRsaKey'] = tunnelRemoteHostRsaKey
	
	api_url = api_url_stem + "/mongo-repo/cluster:update"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())

	
'''
Remove a Mongo database/cluster connection
'''
def mongoRepoRemove(user:str, session:str, requestId:str, clusterUniqueName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	
	api_url = api_url_stem + "/mongo-repo/cluster:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())

'''
Get the list of databases in a cluster/MongoDB Server"
'''
def mongoDatabaseList(user:str, session:str, requestId:str, clusterUniqueName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	api_url = api_url_stem + "/mongo-repo/cluster/databases:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())
	
'''
Get the list of collections of a single database of a cluster/Mongo Server
'''
def mongoDatabaseCollectionList(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	
	api_url = api_url_stem + "/mongo-repo/cluster/collections:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())	

'''
Add new collection to a Mongo Database
'''
def mongoDatabaseCollectionAdd(user, session, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	api_url = api_url_stem + "/mongo-repo/cluster/collection:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Add new bucket to a Mongo Database
'''
def mongoDatabaseBucketAdd(user, session, requestId:str, clusterUniqueName:str, databaseName:str, bucketName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	api_url = api_url_stem + "/mongo-repo/cluster/bucket:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())
	

'''
Delete bucket from Mongo Database
'''
def mongoDatabaseBucketDelete(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, bucketName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	
	api_url = api_url_stem + "/mongo-repo/cluster/bucket:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())

'''
Drop collection from Mongo Database
'''
def mongoDatabaseCollectionDrop(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	api_url = api_url_stem + "/mongo-repo/cluster/collections:drop"
	response =requests.delete(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Create an index for collection
'''
def mongoCollectionIndexCreate(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, fieldName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['fieldName'] = fieldName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/index:create"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())
	
'''
Create an index for collection
'''
def mongoCollectionIndexDelete(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, fieldName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['fieldName'] = fieldName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/index:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())

'''
replace/update object by id
'''
def replaceDocumentById(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, idObject:str, operation:str, object:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['idObject'] = idObject
	headers['operation'] = operation
	
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/replace-update:single"
	response =requests.post(api_url, data=object, headers=headers)
	return retObject(response.json())
	
'''
Drop object by id
'''
def deleteDocumentById(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, idObject:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['idObject'] = idObject
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/delete:single"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())

'''
Drop Records from a list of ids
'''
def deleteMultipleDocuments(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str,  jsonDocument:[str]):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/delete:multiple"
	response =requests.post(api_url, data=jsonDocument, headers=headers)
	return retObject(response.json())

'''
Drop Records
'''
def deleteManyRecordsSimpleTextSearch(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, fieldName:str, language:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['fieldName'] = fieldName
	headers['language'] = language
	
	api_url = api_url_stem + "/mongo-repo/cluster/collection/drop:simple-text"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())

'''
Drop Records
'''
def deleteManyRecords(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, itemToSearch:str, valueToSearch:str, operator:str, valueToSearchType:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['itemToSearch'] = itemToSearch
	headers['valueToSearch'] = valueToSearch
	headers['operator'] = operator
	headers['valueToSearchType'] = valueToSearchType
	
	api_url = api_url_stem + "/mongo-repo/cluster/collection/drop:many"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())

'''
Drop Records in a range
'''
def deleteManyRecordsRange(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, itemToSearch:str, frm:str, to:str, valueToSearchType:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['itemToSearch'] = itemToSearch
	headers['from'] = frm
	headers['to'] = to
	headers['valueToSearchType'] = valueToSearchType
	api_url = api_url_stem + "/mongo-repo/cluster/collection/drop:range"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())






class MongoResultSet:
	def __init__(self, metadata_:{}, resultSet_:List[bson.Document], countQuery_:int, countCollection_:int):
		self.metadata=metadata_
		self.resultSet=resultSet_
		self.countQuery_=countQuery_
		self.countCollection=countCollection_
'''
Search collection for text
'''
def searchSimpleText(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, language:str, itemToSearch:str, isHighestScore:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['language'] = language
	headers['itemToSearch'] = itemToSearch
	headers['isHighestScore'] = isHighestScore
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/search:text"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) #returns MongoResultSet in RestObject.payload

'''
Get count of all documents in collection
'''
def getCollectionDocsCount(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, isEstimate:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['isEstimate'] = isEstimate
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/count"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Add single document to collection
'''
def addDocumentToCollection(user:str, session:str, requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, jsonDocument:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/add:single"
	response =requests.put(api_url, data=jsonDocument, headers=headers)
	return retObject(response.json())
	
'''
Copy records to collection from Embedded adhoc query
'''
def copyEmbeddedQueryToCollection(	user:str, 
									session:str, 
									requestId:str, 
									toMongoClusterName:str, 
									toMongoDbName:str, 
									toMongoCollectionName:str, 
									fromEmbeddedType:str,
									fromClusterId:int,
									fromEmbeddedDatabaseName:str,
									fromEmbeddedSchemaName:str,
									sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDbName'] = toMongoDbName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['toMongoCollectionName'] = str(toMongoCollectionName)
	headers['fromEmbeddedType'] = fromEmbeddedType
	headers['fromClusterId'] =str(fromClusterId)
	headers['fromEmbeddedDatabaseName'] = fromEmbeddedDatabaseName
	headers['fromEmbeddedSchemaName'] = fromEmbeddedSchemaName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/embedded/adhoc:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json()) # returns GenericResponse in RestObject.payload

'''
Copy records to collection from RDBMS query
'''
def copyRDBMSQueryToCollection(user:str, session:str, requestId:str, toMongoClusterName:str, toMongoDbName:str, toMongoCollectionName:str, fromRdbmsSchemaUniqueName:str, batchCount:int, sqlContent):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDbName'] = toMongoDbName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['fromRdbmsSchemaUniqueName'] = fromRdbmsSchemaUniqueName
	headers['batchCount'] = str(batchCount)
	api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/rdbms:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json()) #returns GenericResponse in RestObject.payload
	
'''
Copy records to collection from Elastic DSL query
'''
def copyElasticDslToCollection(user:str, session:str, requestId:str, toMongoClusterName:str, toMongoDbName:str, toMongoCollectionName:str, fromElasticClusterName:str, fromElasticHttpVerb:str, fromElasticEndPoint:str, batchCount:int, httpPayload:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDbName'] = toMongoDbName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['fromElasticHttpVerb'] = fromElasticHttpVerb
	headers['fromElasticEndPoint'] = fromElasticEndPoint
	headers['batchCount'] = str(batchCount)
	api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/elastic:dsl"
	response =requests.put(api_url, data=httpPayload, headers=headers)
	return retObject(response.json()) #returns GenericResponse in RestObject.payload
	
'''
Create/add records to collection from Elastic SQL query
'''
def copyElasticSqlToCollection(user:str, session:str, requestId:str, toMongoClusterName:str, toMongoDbName:str, toMongoCollectionName:str, fromElasticClusterName:str, fromElasticFetchSize:str, fetchSize:int, batchCount:int, sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDbName'] = toMongoDbName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['fromElasticFetchSize'] = fromElasticFetchSize
	headers['fetchSize'] = str(fetchSize)
	headers['batchCount'] = str(batchCount)
	
	api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/elastic:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())	#returns GenericResponse in RestObject.payload 
	
'''
Copy records to collection from another Mongodb collection(s) simple search
'''
def copySimpleSearchToCollection(user:str, 
								session:str, 
								requestId:str, 
								fromClusterUniqueName:str, 
								fromMongoDbName:str, 
								fromCollectionName:str, 
								itemToSearch:str, 
								valueToSearch:str, 
								valueToSearchType:str, 
								operator:str,
								toClusterUniqueName:str,
								toMongoDbName:str,
								toCollectionName:str,
								batchCount:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromClusterUniqueName'] = fromClusterUniqueName
	headers['fromMongoDbName'] = fromMongoDbName
	headers['fromCollectionName'] = fromCollectionName
	headers['itemToSearch'] = itemToSearch
	headers['valueToSearch'] = valueToSearch
	headers['valueToSearchType'] = valueToSearchType
	headers['operator'] = operator
	headers['toClusterUniqueName'] = toClusterUniqueName
	headers['toMongoDbName'] = toMongoDbName
	headers['toCollectionName'] = toCollectionName
	headers['batchCount'] = str(batchCount)
	
	api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/mongodb/search:simple"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json()) #returns GenericResponse in RestObject.payload 
	

'''
Copy records to collection from another Mongodb collection(s) range search
'''
def copyRangeSearchToCollection(user:str, 
								session:str, 
								requestId:str,
								fromClusterUniqueName:str, 
								fromMongoDbName:str, 
								fromCollectionName:str, 
								itemToSearch:str, 
								fromValue:str, 
								toValue:str, 
								valueSearchType:str,
								toMongoClusterName:str,
								toMongoDatabaseName:str,
								toMongoCollectionName:str,
								batchCount:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromClusterUniqueName'] = fromClusterUniqueName
	headers['fromMongoDbName'] = fromMongoDbName
	headers['fromMongoDbName'] = fromMongoDbName
	headers['fromCollectionName'] = fromCollectionName
	headers['itemToSearch'] = itemToSearch
	headers['fromValue'] = fromValue
	headers['toValue'] = toValue
	headers['valueSearchType'] = valueSearchType
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDatabaseName'] = toMongoDatabaseName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['batchCount'] = str(batchCount)
	api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/mongodb/search:range"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json()) #returns GenericResponse in RestObject.payload 


'''
Copy records to collection from full Mongodb collection
'''
def copyFullCollectionToCollection(user:str, session:str, requestId:str, fromMongoClusterName:str, fromMongoDatabaseName:str, fromMongoCollectionName:str, toMongoClusterName:str, toMongoDatabaseName:str, toMongoCollectionName:str, batchCount:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDatabaseName'] = toMongoDatabaseName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['batchCount'] = str(batchCount)
	api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/mongodb:collection"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json()) #returns GenericResponse in RestObject.payload 

'''
Copy records to collection from full Mongodb collection
'''
def copyMongoAdhocMqlToCollection(user:str, session:str, requestId:str,fromMongoClusterName:str, fromMongoDatabaseName:str, fromMongoCollectionName:str, toMongoClusterName:str, toMongoDatabaseName:str,  toMongoCollectionName:str, batchCount:int, bsonQuery:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDatabaseName'] = toMongoDatabaseName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['batchCount'] = str(batchCount)
	api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/mongodb:adhoc"
	response =requests.put(api_url, data=bsonQuery, headers=headers)
	return retObject(response.json()) #returns GenericResponse in RestObject.payload 
	

'''
Copy Csv file to collection
'''
def copyCsvToCollection(user:str, session:str, requestId:str, toMongoClusterName:str, toMongoDatabaseName:str, toMongoCollectionName:str, batchCount:int, path:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDatabaseName'] = toMongoDatabaseName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['batchCount'] = str(batchCount)
	api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/csv:load"
	f = open(path, "r")
	form_data = {'attacment': f}
	response =requests.put(api_url, data=form_data, headers=headers)
	f.close()
	return retObject(response.json()) #returns GenericResponse in RestObject.payload 
	
'''
add multiple records to a collection delivered in a zip file 
'''
def addBatchDocumentToCollection(user:str, session:str, requestId:str, toMongoClusterName:str, toMongoDatabaseName:str, toMongoCollectionName:str, batchCount:int, path:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDatabaseName'] = toMongoDatabaseName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['batchCount'] = str(batchCount)
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/add:batch"
	f = open(path, "r") 
	form_data = {'attacment': f}
	response =requests.post(api_url, data=form_data, headers=headers)
	f.close()
	return retObject(response.json())


'''
Get previously saved document/resultset
'''
def getDocument(user:str, session:str, requestId:str, clusterName:str, dbName:str, cName:str, docId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['clusterName'] = clusterName
	headers['dbName'] = dbName
	headers['cName'] = cName
	headers['docId'] = docId
	
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) #return ResultQuery in RestObject.payload
	
	
class LargeMongoBinaryFileMetaRev:
	def __init__(self, id_:str, revision_:int, timeStamp_:int, size_:int):
		self.id=id_
		self.revision=revision_
		self.timeStamp=timeStamp_
		self.size=size_

		
		
class LargeMongoBinaryFileMeta:
	def __init__(self, name_:str, revList_:List[LargeMongoBinaryFileMetaRev]):
		self.name=name_
		self.revList=revList_
		
class LargeMongoBinaryFileMetaList:
	def __init__(self,  largeMongoBinaryFileMetaLst_:List[LargeMongoBinaryFileMeta]):
		self.largeMongoBinaryFileMetaLst=largeMongoBinaryFileMetaLst_

		
		
'''
Get first N documents in the collection
'''
def getFirstNDocuments(user:str, session:str, requestId:str, clusterName:str, databaseName:str, collectionName:str, limit:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['limit'] = str(limit)
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document:firstn"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #return ResultQuery in RestObject.payload

'''
Get first N documents in the bucket
'''
def getFirstNBucketDocuments(user:str, session:str, requestId:str, clusterName:str, databaseName:str, collectionName:str, limit:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['limit'] = str(limit)
	api_url = api_url_stem + "/mongo-repo/cluster/bucket/document:firstn"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #return LargeMongoBinaryFileMetaList in RestObject.payload

		
'''
Simple Search, providing item to search, value to search and value type 
'''
def searchDocumentSimple(user:str, session:str, requestId:str, clusterName:str, databaseName:str, collectionName:str, itemToSearch:str, valueToSearch:str, valueToSearchType:str, operator:str, persist:str, comment:str, sqlName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['itemToSearch'] = itemToSearch
	headers['valueToSearch'] = valueToSearch
	headers['valueToSearchType'] = valueToSearchType
	headers['operator'] = operator
	headers['persist'] = persist
	headers['comment'] = comment
	headers['sqlName'] = sqlName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/search:simple"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #return MongoResultSet in RestObject.payload


'''
Search for a range of Documents
'''
def searchDocumentRange(user:str, session:str, requestId:str, clusterName:str, databaseName:str, collectionName:str, itemToSearch:str, fromValue:str, toValue:str, valueSearchType:str, persist:str, comment:str, sqlName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['itemToSearch'] = itemToSearch
	headers['fromValue'] = fromValue
	headers['toValue'] = toValue
	headers['valueSearchType'] = valueSearchType
	headers['persist'] = persist
	headers['comment'] = comment
	headers['sqlName'] = sqlName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/search:range"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #return MongoResultSet in RestObject.payload

'''
Range based Search, providing item to search, from and to value and value type
'''
def searchDocumentComplexAnd(user:str, session:str, requestId:str, clusterName:str, databaseName:str, collectionName:str, persist:str, comment:str, sqlName:str, complexAndQuery:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['persist'] = persist
	headers['comment'] = comment
	headers['sqlName'] = sqlName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/search:complex-and"
	response =requests.post(api_url, data=complexAndQuery, headers=headers)
	return retObject(response.json())  #return MongoResultSet in RestObject.payload

'''
Move document/resultset from one collection to another accross clusters and databases
'''
def moveDocument(user:str, session:str, requestId:str, cNameSource:str, dbNameSource:str, clusterNameSource:str, docId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['cNameSource'] = cNameSource
	headers['dbNameSource'] = dbNameSource
	headers['clusterNameSource'] = clusterNameSource
	headers['docId'] = docId
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document:move"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Create RDBMS table from ResultQuery document
'''
def createRdbmsTableFromDocument(user:str, session:str, requestId:str, fromMongoClusterName:str, fromMongoDatabaseName:str, fromMongoCollectionName:str, rdbmsConnectionName:str, rdbmsSchema:str, rdbmsTable:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['rdbmsConnectionName'] = rdbmsConnectionName
	headers['rdbmsSchema'] = rdbmsSchema
	headers['rdbmsTable'] = rdbmsTable
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/rdbms/table:create"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())

class MongoObjectRef:
	def __init__(self, clusterName_:str, databaseName_:str, collectionName_:str, documentId_:str):
		self.clusterName=clusterName_
		self.databaseName=databaseName_
		self.collectionName=collectionName_
		self.documentId=documentId_
	
'''
Create RDBMS table from multiple ResultQuery documents
'''
def compoundDocument(user:str, session:str, requestId:str, rdbmsConnectionName:str, rdbmsSchema:str, rdbmsTable:str, listOfMongoObjects:List[MongoObjectRef]):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['rdbmsConnectionName'] = rdbmsConnectionName
	headers['rdbmsSchema'] = rdbmsSchema
	headers['rdbmsTable'] = rdbmsTable
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/rdbms/table:compound"
	response =requests.put(api_url, data=listOfMongoObjects, headers=headers)
	return retObject(response.json())


class MongoRepoMqlParamExecution:
	def __init__(self, id_:int, value_:any):
		self.id=id_
		self.value=value_
		
		
class MongoRepoDynamicMqlExecution:
	def __init__(self, mqlId_:int, mongoRepoMqlParamListList_:List[MongoRepoMqlParamExecution]):
		self.mqlId=mqlId_
		self.mongoRepoMqlParamListList=mongoRepoMqlParamListList_
		
		
		
'''
Execute Repo Mql
'''
def repoMql(user:str, 
			session:str,
			requestId:str, 
			clusterUniqueName:str, 
			mongoDbName:str, 
			mqlId:str, 
			persist:str, 
			comment:str, 
			sqlName:str, 
			parameters:MongoRepoDynamicMqlExecution) -> any:
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['mongoDbName'] = mongoDbName
	headers['mqlId'] = mqlId
	headers['persist'] = persist
	headers['comment'] = comment
	headers['sqlName'] = sqlName
	
	api_url = api_url_stem + "/mongo-repo/cluster/collection/query:repo"
	response =requests.post(api_url, data=parameters, headers=headers)
	return retObject(response.json()) 


class MongoRepoMqlParam:
	def __init__(self, dynamicMqlParamId_:int, dynamicMqlId_:int, dynamicMqlParamName_:str, dynamicMqlParamDefault_:str, dynamicMqlParamType_:str, dynamicMqlParamPosition_:str, dynamicMqlParamOrder_:str, value_:str):
		self.dynamicMqlParamId=dynamicMqlParamId_
		self.dynamicMqlId=dynamicMqlId_
		self.dynamicMqlParamName=dynamicMqlParamName_
		self.dynamicMqlParamDefault=dynamicMqlParamDefault_
		self.dynamicMqlParamType=dynamicMqlParamType_
		self.dynamicMqlParamPosition=dynamicMqlParamPosition_
		self.dynamicMqlParamOrder=dynamicMqlParamOrder_
		self.value=value_
		
		
class MongoRepoDynamicMql:
	def __init__(self, mqlId_:int, mqlReturnType_:str, mqlCategory_:str, mqlClass_:str, type_:str, mqlName_:str, mqlDescription_:str, mqlContent_:str, active_:int, mongoRepoMqlParamList_:List[MongoRepoMqlParam] ):
		self.mqlId=mqlId_
		self.mqlReturnType=mqlReturnType_
		self.mqlCategory=mqlCategory_
		self.mqlClass=mqlClass_
		self.type=type_
		self.mqlName=mqlName_
		self.mqlDescription=mqlDescription_
		self.mqlContent=mqlContent_
		self.active=active_
		self.mongoRepoMqlParamList=mongoRepoMqlParamList_	
		
		
		
class MongoRepoDynamicMqlList:
	def __init__(self, mqlId_:int, mongoRepoMqlParamListList_:List[MongoRepoMqlParamExecution]):
		self.mqlId=mqlId_
		self.mongoRepoMqlParamListList=mongoRepoMqlParamListList_
	
'''
Get the Mql statement by searching a keyword 
'''
def searchQuery(user:str, session:str, requestId:str, stringToSearch:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['stringToSearch'] = stringToSearch
	api_url = api_url_stem + "/mongo-repo/management/query:search"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) #returns MongoRepoDynamicMqlList in RestObject.payload

'''
Add a new MQL statement or update an existing one
'''
def addMongoQuery(user:str, session:str, requestId:str, mQuery:MongoRepoDynamicMql):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/mongo-repo/management/query:add"
	response =requests.put(api_url, data=mQuery, headers=headers)
	return retObject(response.json())
	
'''
Get the Mql statement by id
'''
def getQueryById(user:str, session:str, requestId:str, id:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)
	api_url = api_url_stem + "/mongo-repo//management/query:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())

'''
Delete Mql statement 
'''
def deleteMongoQuery(user:str, session:str, requestId:str, mqlId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mqlId'] = str(mqlId)
	api_url = api_url_stem + "/mongo-repo/management/query:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())


class MongoRepoMqlParamInput:
	def __init__(self, dynamic_mql_param_name_:str, 
						dynamic_mql_param_default_:str, 
						dynamic_mql_param_type_:str, 
						dynamic_mql_param_position_:str, 
						dynamic_mql_param_order_:str, 
						value_:str):
		self.dynamic_mql_param_name=dynamic_mql_param_name_
		self.mqlReturnType=dynamic_mql_param_default_
		self.dynamic_mql_param_type=dynamic_mql_param_type_
		self.dynamic_mql_param_position=dynamic_mql_param_position_
		self.dynamic_mql_param_order=dynamic_mql_param_order_
		self.value=value_


'''
Add params to an existing MQL statement
'''
def addMongoQueryParam(user:str, session:str, requestId:str, mqlId:int, param:MongoRepoMqlParamInput):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mqlId'] = str(mqlId)
	api_url = api_url_stem + "/mongo-repo/management/query/param:add"
	response =requests.put(api_url, data=param, headers=headers)
	return retObject(response.json())

'''
Delete MongoDb Query Parameter
'''
def deleteMongoQueryParam(user:str, session:str, requestId:str, mqlId:int, paramId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mqlId'] = str(mqlId)
	headers['paramId'] = str(paramId)
	api_url = api_url_stem + "/mongo-repo/management/query/param:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())
	
	

'''
Add params to an existing MQL statement
'''
def generateQueryParam(user:str, session:str, requestId:str, noParams:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['noParams'] = str(noParams)
	api_url = api_url_stem + "/mongo-repo/management/query/param:generate"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())


'''
Get Query Bridges for a certain statement id
'''
def getQueryBridges(user:str, session:str, requestId:str, mqlId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mqlId'] = str(mqlId)
	api_url = api_url_stem + "/mongo-repo/management/query/bridge:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())

'''
Add existing MQL statement to a cluster
'''
def addMqlToClusterBridge(user:str, session:str, requestId:str, mqlId:int, clusterId:int, active:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mqlId'] = str(mqlId)
	headers['clusterId'] = str(clusterId)
	headers['active'] = active
	api_url = api_url_stem + "/mongo-repo/management/query/bridge:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())
	
'''
Delete Mql to Cluster bridge
'''
def deleteMqlToClusterBridge(user:str, session:str, requestId:str, bridgeId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['bridgeId'] = str(bridgeId)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
	api_url = api_url_stem + "/mongo-repo/management/query/bridge:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())

'''
Execute a generic Mql command providing it in Bson/Json format
'''
def runAdhocBson(user:str, session:str, requestId:str, clusterUniqueName:str, mongoDbName:str, collectionName:str, persist:str, comment:str, sqlName:str, command:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['mongoDbName'] = mongoDbName
	headers['collectionName'] = collectionName
	headers['comment'] = comment
	headers['requestId'] = requestId
	headers['sqlName'] = sqlName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/query:bson"
	response =requests.post(api_url, data=command, headers=headers)
	return retObject(response.json())  # returns MongoResultSet in RestObject.payload

'''
Execute a generic Mql command providing it in Bson/Json format
'''
def runAdhocMql(user:str, session:str, requestId:str, clusterUniqueName:str, mongoDbName:str, collectionName:str, persist:str, comment:str, sqlName:str, command:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['mongoDbName'] = mongoDbName
	headers['collectionName'] = collectionName
	headers['comment'] = comment
	headers['requestId'] = requestId
	headers['sqlName'] = sqlName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/query:adhoc"
	response =requests.post(api_url, data=command, headers=headers)
	return retObject(response.json())	
	
	
'''
Get a list of snapshots to visualize
'''
def getMongoSnapshotHistory(user:str, session:str, requestId:str, ownerId:int, startIime:int, endTime:int, sqlStatement:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['ownerId'] = str(ownerId)
	headers['startIime'] = str(startIime)
	headers['endTime'] = str(endTime)
	headers['sqlStatement'] = sqlStatement
	api_url = api_url_stem + "/mongo-repo/snapshot:history"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())


'''
Get snapshot to visualize
'''
def getSnapshot(user:str, session:str, requestId:str, snapshotId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['snapshotId'] = snapshotId
	api_url = api_url_stem + "/mongo-repo/snapshot:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns ResultQuery in RestObject.payload

'''
Delete snapshot
'''
def deleteMangoSnapshot(user:str, session:str, requestId:str, snapshotId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['snapshotId'] = snapshotId
	api_url = api_url_stem + "/mongo-repo/snapshot:delete"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())

class RepoAssociationTable:
	def __init__(self, associationId_:int, associationName_:str):
		self.associationId=associationId_
		self.associationName=associationName_
		
		
class RepoAssociationTableList:
	def __init__(self, rList_:List[RepoAssociationTable]):
		self.rList=rList_

'''
Get associations
'''
def getMongoRepoAssociationTable(user:str, session:str, requestId:str, associationName:str) :
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationName'] = associationName
	api_url = api_url_stem + "/mongo-repo/management/association:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns RepoAssociationTableList in RestObject.payload

	
'''
Add association
'''
def addMongoRepoAssociationTable(user:str, session:str, requestId:str, associationName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationName'] = associationName
	api_url = api_url_stem + "/mongo-repo/management/association:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Update association
'''
def updateRepoAssociationTable(user:str, session:str, requestId:str, associationId:int, associationName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationId'] = str(associationId)
	headers['associationName'] = associationName
	api_url = api_url_stem + "/mongo-repo/management/association:update"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())
	
'''
Delete association
'''
def deleteMongoRepoAssociationTable(user:str, session:str, requestId:str, associationId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationId'] = str(associationId)
	api_url = api_url_stem + "/mongo-repo/management/association:delete"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())

'''
Add large binary file to mongo database
'''
def addLargeAttachment(user:str, session:str, requestId:str, clusterName:str, databaseName:str, bucketName:str, filePath:str):
	fileName, fileContent, fileMime = getFileContent(filePath)
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	headers['fileName'] = fileName
	headers['metadata'] = None
	api_url = api_url_stem + "/mongo-repo/cluster/large:add"
	form_data = {'attacment': fileContent}
	response =requests.post(api_url, data=form_data, headers=headers)
	return retObject(response.json())


class LargeObjectAssociatedMetadata:
	def __init__(self, originalFolder_:str, originalUserId_:int, originalType_:str, originalLastModified_:int):
		self.originalFolder=originalFolder_
		self.originalUserId=originalUserId_
		self.originalType=originalType_
		self.originalLastModified=originalLastModified_

class LargeMongoBinaryFile:
	def __init__(self, filename_:str, largeObjectAssociatedMetadata_:LargeObjectAssociatedMetadata, fileSize_:int, file_:str):
		self.filename=filename_
		self.largeObjectAssociatedMetadata=largeObjectAssociatedMetadata_
		self.fileSize=fileSize_
		self.file=file_
'''
Get large binary file from mongo database
'''
def getLargeAttachment(user:str, session:str, requestId:str, clusterName:str, databaseName:str, bucketName:str, fileId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	headers['fileId'] = fileId
	api_url = api_url_stem + "/mongo-repo/cluster/large:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns LargeMongoBinaryFile in RestObject.payload
	
	
'''
Get large binary file from mongo database, browser based
'''
def downloadLargeAttachment(user:str, session:str, requestId:str, clusterName:str, databaseName:str, bucketName:str, fileId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	headers['fileId'] = fileId
	api_url = api_url_stem + "/mongo-repo/cluster/large:download"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())

'''
Delete large binary file from mongo database
'''
def deleteLargeAttachment(user:str, session:str, requestId:str, clusterName:str, databaseName:str, bucketName:str, fileId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	headers['fileId'] = fileId
	api_url = api_url_stem + "/mongo-repo/cluster/large:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())
	
'''
Execute Mql on multiple clusters / collections and aggregate results with Sql
'''
def executeAdhocMultipleCollection(user:str, session:str, requestId:str, strObj:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['strObj'] = strObj
	api_url = api_url_stem + "/mongo-repo/execute/adhoc/multiple:aggregate"
	response =requests.put(api_url, data=strObj, headers=headers)
	return retObject(response.json())  # returns ResultQuery in RestObject.payload


	
class MongoExecutedQuery:
	def __init__(self, id_:int, mqlClass_:str, source_:int, usr_:str, mqlContent_:str, jsonParam_:str, timestamp_:int):
		self.id=id_
		self.mqlClass=mqlClass_
		self.source=source_
		self.usr=usr_
		self.mqlContent=mqlContent_
		self.jsonParam=jsonParam_
		self.timestamp=timestamp_
		
		
class MongoExecutedQueryList:
	def __init__(self, mongoExecutedQueryLst_:List[MongoExecutedQuery]):
		self.mongoExecutedQueryLst=mongoExecutedQueryLst_
	
	
'''
Get Execution History for Current User
'''
def getMongoHistory(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/mongo-repo/history/user:personal"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns MongoExecutedQueryList in RestObject.payload

class HistoryStatement:
	def __init__(self, userId_:int, shaHash_:str, content_:int, comment_:str, timeStamp_:int, type_:str, source_:int):
		self.userId=userId_
		self.shaHash=shaHash_
		self.content=content_
		self.comment=comment_
		self.timeStamp=timeStamp_
		self.type=type_
		self.source=source_
		
class HistSqlList:
	def __init__(self, tempSqlList_:List[HistoryStatement]):
		self.tempSqlList=tempSqlList_
		
		
'''
Get the List of executed sql statements by current user
'''
def getMongoHistory(user:str, session:str, requestId:str, type:str, stext:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['type'] = type
	headers['stext'] = stext
	
	api_url = api_url_stem + "/mongo-repo/history/stm:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns HistSqlList in RestObject.payload	
	
'''
Copy sql statements to another user
'''
def copyMongoHistStm(user:str, session:str, requestId:str, toUserId:str, shaHash:str, type:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toUserId'] = toUserId
	headers['shaHash'] = shaHash
	headers['type'] = type
	
	api_url = api_url_stem + "/mongo-repo/history/stm:copy"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  # returns GenericResponse with "OK" in RestObject.payload

	
'''
Delete large binary file from mongo database
'''
def deleteMongoHistStmt(user:str, session:str, requestId:str, shaHash:str, type:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['shaHash'] = shaHash
	headers['type'] = type
	api_url = api_url_stem + "/mongo-repo/history/stm:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())	
	

''' 
 ############################ MongoDb Controller END #####################################
'''


''' 
 ############################ RestApi Controller START #####################################
'''


class UserBodyApplication:
	def __init__(self, id_:int, bodyName_:str, bodyRawInput_:str, bodyValue_:str):
		self.id=id_;
		self.bodyName=bodyName_
		self.bodyValue=bodyValue_
		
class UserBodyRawInput:
	def __init__(self, id_:int, rawInputValue_:str):
		self.id=id_;
		self.rawInputValue=rawInputValue_
		
		
class UserBodyMultipart:
	def __init__(self, id_:int, isFile_:str, filePath_:str, bodyName_:str, bodyValue_:str ):
		self.id=id_;
		self.isFile=isFile_
		self.filePath=filePath_
		self.bodyName=bodyName_
		self.bodyValue=bodyValue_
		
class UserBodyFile:
	def __init__(self, id_:int, filePath_:str, rawInputValue_:str ):
		self.id=id_;
		self.filePath=filePath_
		self.rawInputValue=rawInputValue_
		
class UserHeader:
	def __init__(self, id_:int, headerName_:str, value_:str ):
		self.id=id_;
		self.headerName=headerName_
		self.value=value_
		
		
class AuthorizationValue:
	def __init__(self, id_:int, value_:str ):
		self.id=id_;
		self.value=value_
			
class UserAuthorization:
	def __init__(self, id_:int, authorizationName_:str,authorizationValue_:AuthorizationValue ):
		self.id=id_
		self.authorizationName=authorizationName_
		self.authorizationValue=authorizationValue_
		
		
	
class UserRestApiRequest:
	def __init__(self, id_:int, bodiesApplication_:UserBodyApplication, bodyRawInput_:UserBodyRawInput, bodyMultiparts_:List[UserBodyMultipart], userHeaders_:List[UserHeader], userAuthorization_: UserAuthorization):
		self.id=id_
		self.bodiesApplication=bodiesApplication_
		self.bodyRawInput=bodyRawInput_
		self.bodyMultiparts=bodyMultiparts_
		self.userHeaders=userHeaders_
		self.userAuthorization=userAuthorization_

class UserRestApiRequestDetail:
	def __init__(self, id_:int, name_:str, description_:str, verbId_:int, userId_:int, userRequest_:UserRestApiRequest):
		self.id=id_
		self.name=name_
		self.description=description_
		self.verbId=verbId_
		self.userId=userId_
		self.userRequest=userRequest_

		
class UserRestApiRequestDetailList:
	def __init__(self, listOfUserRequestDetail_:List[UserRestApiRequestDetail]):
		self.listOfUserRequestDetail=listOfUserRequestDetail_
'''
Get all user saved requests
'''
def getAllRequests(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/restapi/requests:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns UserRestApiRequestDetailList in RestObject.payload
	

'''
Get saved request
'''
def getRequest(user:str, session:str, requestId:str, restApiId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['restApiId'] = str(restApiId)
	api_url = api_url_stem + "/restapi/request:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns UserRestApiRequestDetail in RestObject.payload


'''
Save request
'''
def saveRequest(user:str, session:str, requestId:str, name:str, description:str, verbId:int, userRestApiRequest:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['name'] = name
	headers['description'] = description
	headers['verbId'] = str(verbId)
	headers['userRestApiRequest'] = userRestApiRequest
	api_url = api_url_stem + "/restapi/request:save"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns UserRestApiRequest in RestObject.payload

''' 
 ############################ RestApi Controller END #####################################
'''

''' 
 ############################ Scripting Controller START #####################################
'''
class InterpreterType:
	def __init__(self, interpreterId_:int, interpreterName_:str, interpreterVersion_:str, interpreterPath_:str, command_:str, fileExtensions_:str):
		self.interpreterId=interpreterId_
		self.interpreterName=interpreterName_
		self.interpreterVersion=interpreterVersion_
		self.interpreterPath=interpreterPath_
		self.command=command_
		self.fileExtensions=fileExtensions_
		
		
		
class InterpreterList:
	def __init__(self, interpreterList_:List[InterpreterType]):
		self.interpreterList=interpreterList_
		
		
'''
Add Interpreter with associated information
'''
def interpreterAdd(user:str, session:str, requestId:str, interpreterName:str, interpreterVersion:str, interpreterPath:str, command:str, fileExtensions:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterName'] = interpreterName
	headers['interpreterVersion'] = interpreterVersion
	headers['interpreterPath'] = interpreterPath
	headers['command'] = command
	headers['fileExtensions'] = fileExtensions
	api_url = api_url_stem + "/scripting/interpreter:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  # returns InterpreterList in RestObject.payload	


	
'''
Update Interpreter
'''
def interpreterUpdate(user:str, session:str, requestId:str, interpreterId:int, interpreterName:str, interpreterVersion:str, interpreterPath:str, command:str, fileExtensions:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['interpreterId'] = str(interpreterId)
	headers['interpreterName'] = interpreterName
	headers['interpreterVersion'] = interpreterVersion
	headers['interpreterPath'] = interpreterPath
	headers['command'] = command
	headers['fileExtensions'] = fileExtensions
	api_url = api_url_stem + "/scripting/interpreter:update"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())  # returns InterpreterList in RestObject.payload	

'''
Delete Interpreter
'''
def interpreterDelete(user:str, session:str, requestId:str, interpreterId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterId'] = interpreterId
	api_url = api_url_stem + "/scripting/interpreter:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  #
	
'''
Get A List Of Interpreter Versions providing a filter for the name
'''
def searchInterpreter(user:str, session:str, requestId:str, interpreterName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterName'] = interpreterName
	api_url = api_url_stem + "/scripting/interpreter:search"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())   # returns InterpreterList in RestObject.payload	

'''
Get List of Interpreters
'''
def listAllInterpreters(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/scripting/interpreter:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns InterpreterList in RestObject.payload	

class MachineNodeToScriptBridge:
	def __init__(self, id_:int, nodeId_:int, nodeName_:str, scriptId_:int, scriptName_:str):
		self.id=id_
		self.nodeId=nodeId_
		self.nodeName=nodeName_
		self.scriptId=scriptId_
		self.scriptName=scriptName_
		

class ScriptParamDetail:
	def __init__(self, scriptParamId_:int, 
						scriptId_:int, 
						paramName_:str, 
						paramType_:str, 
						paramDimension_:str,
						paramDefaultValue_:str,
						paramPosition_:str,
						paramOrder_:int):
		self.scriptParamId=scriptParamId_
		self.scriptId=scriptId_
		self.paramName=paramName_
		self.paramType=paramType_
		self.paramDimension=paramDimension_
		self.paramDefaultValue=paramDefaultValue_
		self.paramPosition=paramPosition_
		self.paramOrder=paramOrder_
		

class ScriptDetail:
	def __init__(self, interpreterId_:int,
						interpreterName_:str,
						interpreterVersion_:str,
						interpreterPath_:str,
						command_:str,
						scriptId_:str,
						scriptName_:str,
						mainFile_:str,
						userCreatorId_:str,
						userEmail_:str,
						paramString_:str,
						predictFile_:str,
						predictFunc_:str,
						scriptVersion_:str,
						compliance_:str,
						scriptParamDetailList_:List[ScriptParamDetail],
						machineNodeToScriptBridgeList_:List[MachineNodeToScriptBridge]):
		self.interpreterId=interpreterId_
		self.interpreterName=interpreterName_
		self.interpreterVersion=interpreterVersion_
		self.interpreterPath=interpreterPath_
		self.command=command_
		self.scriptId=scriptId_
		self.scriptName=scriptName_
		self.mainFile=mainFile_
		self.userCreatorId=userCreatorId_
		self.userEmail=userEmail_
		self.paramString=paramString_
		self.predictFile=predictFile_
		self.predictFunc=predictFunc_
		self.scriptVersion=scriptVersion_
		self.compliance=compliance_
		self.scriptParamDetailList=scriptParamDetailList_
		self.machineNodeToScriptBridgeList=machineNodeToScriptBridgeList_
		

'''
Add Script to Repository
'''
def addScript(user:str, session:str, requestId:str, scriptName:str, paramString:str, mainFile:str, interpreterId:int, fileType:str, filePath:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['scriptName'] = scriptName
	headers['paramString'] = paramString
	headers['mainFile'] = mainFile
	headers['interpreterId'] = str(interpreterId)
	headers['fileType'] = fileType
	
	api_url = api_url_stem + "/scripting/script:add"
	f = open(filePath, "r")
	form_data = {'attacment': f}
	response =requests.put(api_url, data=form_data, headers=headers)
	f.close()
	return retObject(response.json())  # returns ScriptDetail in RestObject.payload	
	
'''
Add Script to Repository
'''
def addScriptByContent(user:str, session:str, requestId:str, scriptName:str, comment:str, paramString:str, interpreterId:int, scriptContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptName'] = scriptName
	headers['comment'] = comment
	headers['paramString'] = paramString
	headers['interpreterId'] = str(interpreterId)
	api_url = api_url_stem + "/scripting/script:content"
	response =requests.put(api_url, data=scriptContent, headers=headers)
	return retObject(response.json())  # returns ScriptDetail in RestObject.payload		



'''
Get Script and versions, scriptName can also be only part of name to wider range for searching, used by browser
'''
def getScriptContent(user:str, session:str, requestId:str, scriptId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptId'] = str(scriptId)
	api_url = api_url_stem + "/scripting/script:content"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # download script content, used by browser


class ScriptDetailObject:
	def __init__(self, listOfScripts_:List[ScriptDetail]):
		self.listOfScripts=listOfScripts_
		

'''
Get Script and versions, scriptName can also be only part of name to wider range for searching
'''
def scriptSearch(user:str, session:str, requestId:str, scriptName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptName'] = scriptName
	api_url = api_url_stem + "/scripting/script:search"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns ScriptDetailObject  in RestObject.payload	

'''
Get Scripts, specific to a user
'''
def userScripts(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/scripting/script:user"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns ScriptDetailObject  in RestObject.payload

'''
Get Specific Script Information
'''
def scriptSearch(user:str, session:str, requestId:str, scriptId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptId'] = str(scriptId)
	api_url = api_url_stem + "/scripting/script:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns ScriptDetail  in RestObject.payload	

'''
Remove Script and All its verions from Repository
'''
def scriptRemove(user:str, session:str, requestId:str, scriptId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptId'] = str(scriptId)
	api_url = api_url_stem + "/scripting/script:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  # returns GenericResponse in RestObject.payload	


'''
Remove Script Version Of A Script from Repository
'''
def scriptVersionRemove(user:str, session:str, requestId:str, scriptName:str, scriptVersion:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptName'] = scriptName
	headers['scriptVersion'] = scriptVersion
	api_url = api_url_stem + "/scripting/script/version:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  # returns GenericResponse in RestObject.payload	


class ScriptParam:
	def __init__(self, scriptParamId_:int, paramName_:str, value_:str):
		self.scriptParamId=scriptParamId_
		self.paramName=paramName_
		self.value=value_
		

class ScriptParamDetailObject:
	def __init__(self, plist_:List[ScriptParamDetail]):
		self.plist=plist_
		
class ScriptParamObject:
	def __init__(self, plist_:List[ScriptParam]):
		self.plist=plist_
		
class ScriptParamObject:
	def __init__(self, scriptParamObject_:ScriptParamObject):
		self.scriptParamObject=scriptParamObject_
		

class ScriptParamDetailObject:
	def __init__(self, scriptParamDetailObject_:ScriptParamDetailObject):
		self.scriptParamDetailObject=scriptParamDetailObject_
		

class ScriptParamCompoundObject:
	def __init__(self, scriptParamDetailObject_:ScriptParamDetailObject):
		self.scriptParamDetailObject=scriptParamDetailObject_
'''
Get Script Parameters in either form, detailed or for execution, or both
'''
def getScriptParam(user:str, session:str, requestId:str, scriptName:str, scriptVersion:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptName'] = scriptName
	headers['scriptVersion'] = scriptVersion
	api_url = api_url_stem + "/scripting/script/param:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns ScriptParamCompoundObject in RestObject.payload


'''
Add Script Param for a corresponding Script
'''
def scriptParamAdd(user:str, 
				   session:str, 
				   requestId:str, 
				   scriptId:int, 
				   paramName:str, 
				   paramType:str, 
				   paramDimension:str, 
				   paramDefaultValue:str, 
				   paramPosition:str, 
				   paramOrder:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptId'] = str(scriptId)
	headers['paramName'] = paramName
	headers['paramType'] = paramType
	headers['paramDimension'] = paramDimension
	headers['paramDefaultValue'] = paramDefaultValue
	headers['paramPosition'] = paramPosition
	headers['paramOrder'] = str(paramOrder)
	
	api_url = api_url_stem + "/scripting/script/param:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  # returns ScriptParamDetail in RestObject.payload

'''
Delete Script Param
'''
def scriptParamDelete(user:str, session:str, requestId:str, scriptId:int, scriptParamId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptId'] = str(scriptId)
	headers['scriptParamId'] = str(scriptParamId)
	
	api_url = api_url_stem + "/scripting/script/param:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  #

class MachineNodeToScriptBridge:
	def __init__(self, id_:int, nodeId_:int, nodeName_:str, scriptId_:int, scriptName_:str):
		self.id=id_
		self.nodeId=nodeId_
		self.nodeName=nodeName_
		self.scriptId=scriptId_
		self.scriptName=scriptName_

class MachineNodeToScriptBridgeList:
	def __init__(self, machineNodeToScriptBridgeList_:List[MachineNodeToScriptBridge]):
		self.machineNodeToScriptBridgeList=machineNodeToScriptBridgeList_
'''
Add association of a Script to machine node
'''
def machineNodeBridgeToScriptAdd(user:str, session:str, requestId:str, nodeId:int, scriptId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['nodeId'] = str(nodeId)
	headers['scriptId'] = str(scriptId)
	api_url = api_url_stem + "/scripting/bridge:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  # returns MachineNodeToScriptBridgeList in RestObject.payload


'''
Delete association of a script to machine node
'''
def machineNodeBridgeToScriptDelete(user:str, session:str, requestId:str, id:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)
	api_url = api_url_stem + "/scripting/bridge:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  # returns GenericResponse in RestObject.payload



'''
Get associations to scripts for a node
'''
def nodeBridgeToScriptForNode(user:str, session:str, requestId:str, nodeId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['nodeId'] = str(nodeId)
	api_url = api_url_stem + "/scripting/bridge/node:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns MachineNodeToScriptBridgeList in RestObject.payload

'''
Get associations of script for nodes
'''
def nodeBridgeToScriptForScript(user:str, session:str, requestId:str, scriptId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptId'] = str(scriptId)
	api_url = api_url_stem + "/scripting/bridge/script:get"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  #


class ScriptingReturnObject:
	def __init__(self, machineNodeToScriptBridgeList_:List[MachineNodeToScriptBridge]):
		self.machineNodeToScriptBridgeList=machineNodeToScriptBridgeList_
		

'''
Run/Execute Repo Script Version with a set of parameters
'''
def runScript(user:str, session:str, requestId:str, scriptId:int, machineList:str, scriptParameters:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptId'] = str(scriptId)
	headers['machineList'] = machineList
	headers['scriptParameters'] = scriptParameters
	api_url = api_url_stem + "/scripting/script/repo:run"
	response =requests.post(api_url, data=scriptParameters, headers=headers)
	return retObject(response.json())  # returns ScriptingReturnObject in RestObject.payload


class ScriptParamList2:
	def __init__(self, nodeUrl_:str, scriptParamList_:List[ScriptParam]):
		self.nodeUrl=nodeUrl_
		self.scriptParamList=scriptParamList_
		

class ScriptParamRepoList:
	def __init__(self, scriptId_:int, interpreterId_:int, requestId_:str, scriptParamRepoList_:List[ScriptParamList2]):
		self.scriptId=scriptId_
		self.interpreterId=interpreterId_
		self.requestId=requestId_
		self.scriptParamRepoList=scriptParamRepoList_
		

'''
"Run/Execute Repo Script Version with individual set of parameters
'''
def runRepoScriptMultiple(user:str, session:str, requestId:str, scriptParameters:ScriptParamRepoList):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/scripting/script/repo/multiple:run"
	response =requests.post(api_url, data=JsonEncoder().encode(scriptParameters), headers=headers)
	return retObject(response.json())  # returns ScriptingReturnObject in RestObject.payload

	
'''
Run/Execute Script Version via client
'''
def runAdhocScriptViaClient(user:str, session:str, requestId:str, scriptName:str,  interpreterId:int, machineList:str, scriptContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptName'] = scriptName
	headers['interpreterId'] = str(interpreterId)
	headers['machineList'] = machineList
	api_url = api_url_stem + "/scripting/script/adhoc:run"
	response =requests.post(api_url, data=scriptContent, headers=headers)
	return retObject(response.json())  # returns ScriptingReturnObject in RestObject.payload

'''
Run/Execute Script Version via another node, background execution with sending notifications
'''
def runAdhocScriptViaNode(user:str, session:str, requestId:str, internalUser:str, internalPassword:str, scriptName:str,  interpreterId:int, baseUrl:str, scriptContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['internalUser'] = internalUser
	headers['internalPassword'] = internalPassword
	headers['scriptName'] = scriptName
	headers['interpreterId'] = str(interpreterId)
	headers['baseUrl'] = baseUrl
	api_url = api_url_stem + "/scripting/script/adhoc/node:run"
	response =requests.post(api_url, data=scriptContent, headers=headers)
	return retObject(response.json())  # returns printable message such as 'OK' in RestObject.payload

	

'''
Execute Repo Script Version via cluster gate node, background execution with sending notifications
'''
def runRepoScriptViaNodeMultipart(user:str, 
								  session:str, 
								  requestId:str, 
								  internalUser:str, 
								  internalPassword:str, 
								  mainFileName:str,
								  interpreterId:int, 
								  baseUrl:str, 
								  baseFolder:str,
								  attachmentPath:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['internalUser'] = internalUser
	headers['internalPassword'] = internalPassword
	headers['mainFileName'] = mainFileName
	headers['interpreterId'] = str(interpreterId)
	headers['baseUrl'] = baseUrl
	headers['baseFolder'] = baseFolder
	f = open(attachmentPath, "r")
	form_data = {'file': f}
	api_url = api_url_stem + "/scripting/script/repo/multipart/node:run"
	response =requests.post(api_url, data=form_data, headers=headers)
	f.close()
	return retObject(response.json())  # returns printable message such as 'OK' in RestObject.payload

'''
Loopback output from executing scripts in String or json format"
'''
def loopbackScriptDataHeader(user:str, session:str, requestId, tableDefinition: TableDefinition):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/scripting/loopback/data:header"
	response =requests.post(api_url, data=JsonEncoder().encode(tableDefinition), headers=headers)
	return retObject(response.json())  # returns printable message such as 'OK' in RestObject.payload

'''
Loopback output from executing scripts in String or json format
'''
def loopbackScriptDataFooter(user:str, session:str, requestId, rowValue:RowValue):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	api_url = api_url_stem + "/scripting/loopback/data:footer"
	response =requests.post(api_url, data=JsonEncoder().encode(rowValue), headers=headers)
	return retObject(response.json())   # returns printable message such as 'OK' in RestObject.payload

'''
Loopback output from executing scripts in String or json format
'''
def loopbackScriptDataDetail(user:str, session:str, requestId, rowValue:List[RowValue]):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/scripting/loopback/data:detail"
	response =requests.post(api_url, data=JsonEncoder().encode(rowValue), headers=headers)
	return retObject(response.json())  # returns printable message such as 'OK' in RestObject.payload


'''
Loopback output from executing scripts in String or json format
'''
def loopbackScriptStdin(user:str, 
						session:str, 
						requestId:str, 
						internalUser:str, 
						internalPassword:str, 
						baseUrl:str, 
						websocketMessageType:str,
						line:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['internalUser'] = internalUser
	headers['internalPassword'] = internalPassword
	headers['baseUrl'] = baseUrl
	headers['websocketMessageType'] = websocketMessageType
	api_url = api_url_stem + "/scripting/loopback/log:stdin"
	response =requests.post(api_url, data=line, headers=headers)
	return retObject(response.json())  # returns printable message such as 'OK' in RestObject.payload


'''
Accept streams of table rows
'''
def acceptStream(user:str, session:str, requestId:str, type:str, rowOrHeader):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['type'] = type # /*type = "H" - header, "M" - middle, "F" = finished/done*/
	api_url = api_url_stem + "/scripting/script/streaming/adhoc:accept"
	response =requests.post(api_url, body=rowOrHeader, headers=headers)
	return retObject(response.json())  #

'''
Run/Execute Script Version with Streaming
'''
def streamAdhocScript(user:str, session:str, requestId:str, scriptName:str, interpreterId:int, scriptContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptName'] = scriptName
	headers['interpreterId'] = str(interpreterId)
	api_url = api_url_stem + "/scripting/script/streaming/adhoc:run"
	response =requests.post(api_url, data=scriptContent, headers=headers)
	return retObject(response.json()) 



class HistoryScriptVersion:
	def __init__(self, shaHash_:str, content_:str, comment_:str, timeStamp_:int):
		self.shaHash=shaHash_
		self.content=content_
		self.comment=comment_
		self.timeStamp=timeStamp_
		

class HistoryScript:
	def __init__(self, userId_:int, scriptName_:str, type_:str, interpreter_:str, histScriptList_:List[HistoryScriptVersion]):
		self.userId=userId_
		self.scriptName=scriptName_
		self.type=type_
		self.interpreter=interpreter_
		self.histScriptList=histScriptList_
		
class HistScriptList:
	def __init__(self, historyScriptList_:List[HistoryScript]):
		self.historyScriptList=historyScriptList_
		
		

'''
Get the List of executed scripts
'''
def getScriptHist(user:str, session:str, requestId:str, type:str, interpreterName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['type'] = type
	headers['interpreterName'] = interpreterName
	api_url = api_url_stem + "/scripting/history/script:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns HistScriptList in RestObject.payload


'''
Copy historical adhoc scripts
'''
def copyAdhocScriptHist(user:str, session:str, requestId:str, toUserId:int, type:str, interpreterName:str, scriptName:str, shaHash:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toUserId'] = str(toUserId)
	headers['type'] = type
	headers['interpreterName'] = interpreterName
	headers['scriptName'] = scriptName
	headers['shaHash'] = shaHash
	api_url = api_url_stem + "/scripting/history/adhoc/script:copy"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json()) # returns GenericResponse with "OK" on success in RestObject.payload

'''
Copy Repo Scripts to another user
'''
def copyRepoScriptHist(user:str, session:str, requestId:str, toUserId:int, type:str, interpreterId:int, scriptId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toUserId'] = str(toUserId)
	headers['type'] = type
	headers['interpreterId'] = str(interpreterId)
	headers['scriptId'] = str(scriptId)

	api_url = api_url_stem + "/scripting/history/repo/script:copy"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json()) # returns GenericResponse with message to print or display on success in RestObject.payload

'''
Delete script from your profile"
'''
def deleteScriptHist(user:str, session:str, requestId:str, toUserId:int, type:str, interpreterId:int, scriptId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toUserId'] = str(toUserId)
	headers['type'] = type
	headers['interpreterId'] = str(interpreterId)
	headers['scriptId'] = str(scriptId)

	api_url = api_url_stem + "/scripting/history/script:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json()) # returns GenericResponse with true or false on success/failure in RestObject.payload

'''
Download script in the browser
'''
def downloadScript(user:str, session:str, requestId:str, scriptId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['machineName'] = str(scriptId)
	api_url = api_url_stem + "/scripting/script:download"

	with requests.g(api_url, headers=headers) as f:
		return f.read().decode('utf-8')
	

''' 
 ############################ Scripting Controller END #####################################
'''

''' 
 ############################ SQLRepo Controller START #####################################
'''
class SqlRepoDatabase:
	def __init__(self, database_id_:int, 
						database_type_:str, 
						database_name_:str, 
						database_warehouse_name_:str,
						database_account_:str,
						database_other_:str,
						database_server_:str,
						database_port_:str,
						database_description_:str,
						schema_name_:str,
						schema_service_:str,
						schema_password_:str,
						schema_unique_user_name_:str,
						database_active_:str,
						tunnel_local_port_:str,
						tunnel_remote_host_address_:str,
						tunnel_remote_host_port_:str,
						tunnel_remote_host_user_:str,
						tunnel_remote_host_user_password_:str,
						tunnel_remote_host_rsa_key_:str,
						totalRecords_:str
						):
		self.database_id=database_id_
		self.database_type=database_type_
		self.database_name=database_name_
		self.database_warehouse_name=database_warehouse_name_
		self.database_account=database_account_
		self.database_other=database_other_
		self.database_server=database_server_
		self.database_port=database_port_
		self.database_description=database_description_
		self.schema_name=schema_name_
		self.schema_service=schema_service_
		self.schema_password=schema_password_
		self.schema_unique_user_name=schema_unique_user_name_
		self.database_active=database_active_
		self.tunnel_local_port=tunnel_local_port_
		self.tunnel_remote_host_address=tunnel_remote_host_address_
		self.tunnel_remote_host_port=tunnel_remote_host_port_
		self.tunnel_remote_host_user=tunnel_remote_host_user_
		self.tunnel_remote_host_user_password=tunnel_remote_host_user_password_
		self.tunnel_remote_host_rsa_key=tunnel_remote_host_rsa_key_
		self.totalRecords=totalRecords_
		
		

class RdbmsRepoDatabaseList:
	def __init__(self, sqlRepoDatabaseList_:List[SqlRepoDatabase]):
		self.sqlRepoDatabaseList=sqlRepoDatabaseList_
		

'''
Get the List of available Databases
'''
def getDatabase(user:str, session:str, requestId:str, databaseName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['databaseName'] = databaseName
	api_url = api_url_stem + "/sqlrepo/databases"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns RdbmsRepoDatabaseList in RestObject.payload

'''
Add a new database connection to the list of available Databases/schema connections
'''
def addDatabase(user:str, 
				session:str, 
				requestId:str,
				databaseType:str,
				databaseName:str,
				databaseServer:str,
				databasePort:str,
				databaseDescription:str,
				databaseWarehouseName:str,
				schemaName:str,
				schemaService:str,
				schemaPassword:str,
				schemaUniqueUserName:str,
				tunnelLocalPort:str,
				tunnelRemoteHostAddress:str,
				tunnelRemoteHostPort:str,
				tunnelRemoteHostUser:str,
				tunnelRemoteHostUserPassword:str,
				tunnelRemoteHostRsaKey:str
				):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['databaseType'] = databaseType
	headers['databaseName'] = databaseName
	headers['databaseServer'] = databaseServer
	headers['databasePort'] = databasePort
	headers['databaseDescription'] = databaseDescription
	headers['databaseWarehouseName'] = databaseWarehouseName
	headers['schemaName'] = schemaName
	headers['schemaService'] = schemaService
	headers['schemaPassword'] = schemaPassword
	headers['schemaUniqueUserName'] = schemaUniqueUserName
	headers['tunnelLocalPort'] = tunnelLocalPort
	headers['tunnelRemoteHostAddress'] = tunnelRemoteHostAddress
	headers['tunnelRemoteHostPort'] = tunnelRemoteHostPort
	headers['tunnelRemoteHostUser'] = tunnelRemoteHostUser
	headers['tunnelRemoteHostUserPassword'] = tunnelRemoteHostUserPassword
	headers['tunnelRemoteHostRsaKey'] = tunnelRemoteHostRsaKey
	
	api_url = api_url_stem + "/sqlrepo/database/add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  # returns GenericResponse with a printable message in RestObject.payload


'''
Update database connection
'''
def updateDatabase(	user:str, 
					session:str, 
					requestId:str,
					databaseId:int,
					databaseType:str,
					databaseName:str,
					databaseServer:str,
					databasePort:int,
					databaseDescription:str,
					databaseWarehouseName:str,
					schemaName:str,
					schemaService:str,
					schemaPassword:str,
					schemaUniqueUserName:str,
					tunnelLocalPort:int,
					tunnelRemoteHostAddress:str,
					tunnelRemoteHostPort:str,
					tunnelRemoteHostUser:str,
					tunnelRemoteHostUserPassword:str,
					tunnelRemoteHostRsaKey:str,
					isActive:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['databaseId'] = str(databaseId)
	headers['databaseType'] = databaseType
	headers['databaseName'] = databaseName
	headers['databaseServer'] = databaseServer
	headers['databasePort'] = str(databasePort)
	headers['databaseDescription'] = databaseDescription
	headers['databaseWarehouseName'] = databaseWarehouseName
	headers['schemaName'] = schemaName
	headers['schemaService'] = schemaService
	headers['schemaPassword'] = schemaPassword
	headers['schemaUniqueUserName'] = schemaUniqueUserName
	headers['tunnelLocalPort'] = str(tunnelLocalPort)
	headers['tunnelRemoteHostAddress'] = tunnelRemoteHostAddress
	headers['tunnelRemoteHostPort'] = tunnelRemoteHostPort
	headers['tunnelRemoteHostUser'] = tunnelRemoteHostUser
	headers['tunnelRemoteHostUserPassword'] = tunnelRemoteHostUserPassword
	headers['tunnelRemoteHostRsaKey'] = tunnelRemoteHostRsaKey
	headers['isActive'] = isActive
	
	api_url = api_url_stem + "/sqlrepo/database/update"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())    # returns GenericResponse with a printable message in RestObject.payload

'''
Delete database/schema connection
'''
def databaseDelete(user:str, session:str, requestId:str, databaseId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['databaseId'] = str(databaseId)
	api_url = api_url_stem + "/sqlrepo/database/delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  # returns GenericResponse with a printable message in RestObject.payload

'''
Validate a new Database/schema connection
'''
def validateDatabase(	user:str, 
						session:str, 
						requestId:str, 
						databaseType:str,
						databaseName:str,
						databaseServer:str,
						databasePort:str,
						databaseDescription:str,
						databaseWarehouseName:str,
						schemaName:str,
						schemaService:str,
						schemaPassword:str,
						schemaUniqueUserName:str,
						tunnelLocalPort:str,
						tunnelRemoteHostAddress:str,
						tunnelRemoteHostPort:str,
						tunnelRemoteHostUser:str,
						tunnelRemoteHostUserPassword:str,
						tunnelRemoteHostRsaKey:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['databaseType'] = databaseType
	headers['databaseName'] = databaseName
	headers['databaseServer'] = databaseServer
	headers['databasePort'] = databasePort
	headers['databaseDescription'] = databaseDescription
	headers['databaseWarehouseName'] = databaseWarehouseName
	headers['schemaName'] = schemaName
	headers['schemaService'] = schemaService
	headers['schemaPassword'] = schemaPassword
	headers['schemaUniqueUserName'] = schemaUniqueUserName
	headers['tunnelLocalPort'] = tunnelLocalPort
	headers['tunnelRemoteHostAddress'] = tunnelRemoteHostAddress
	headers['tunnelRemoteHostPort'] = tunnelRemoteHostPort
	headers['tunnelRemoteHostUser'] = tunnelRemoteHostUser
	headers['tunnelRemoteHostUserPassword'] = tunnelRemoteHostUserPassword
	headers['tunnelRemoteHostRsaKey'] = tunnelRemoteHostRsaKey
	

	api_url = api_url_stem + "/sqlrepo/database/connection/validate:new-connection"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns GenericResponse with a boolean message in RestObject.payload. For lack of connectivity the error message RestObject.erroMessage is populated

'''
Validate an existing Database/schema connection
'''
def validateSqlRepoDatabase(user:str, session:str, requestId:str, databaseName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['databaseName'] = databaseName
	api_url = api_url_stem + "/sqlrepo/database/connection/validate:connection"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #  returns GenericResponse with a boolean message in RestObject.payload. For lack of connectivity the error message RestObject.erroMessage is populated

class SqlRepoDatabaseSchemaBridge:
	def __init__(self,	database_schema_bridge_id_:int,
						dynamic_sql_id_:int,
						database_id_:int,
						schemaUniqueName_:str,
						database_schema_bridge_active_:int):
		
		self.database_schema_bridge_id=database_schema_bridge_id_
		self.dynamic_sql_id=dynamic_sql_id_
		self.database_id=database_id_
		self.schemaUniqueName=schemaUniqueName_
		self.database_schema_bridge_active=database_schema_bridge_active_

class SqlRepoParam:
	def __init__(self,	dynamic_sql_param_id_:int, 
						dynamic_sql_id_:int, 
						dynamic_sql_param_name_:str, 
						dynamic_sql_param_default_:str, 
						dynamic_sql_param_type_:str, 
						dynamic_sql_param_position_:str, 
						dynamic_sql_param_order_:int, 
						dynamic_sql_param_origin_tbl_:str, 
						dynamic_sql_param_origin_col_:str):
		self.dynamic_sql_param_id=dynamic_sql_param_id_
		self.dynamic_sql_id=dynamic_sql_id_
		self.dynamic_sql_param_name=dynamic_sql_param_name_
		self.dynamic_sql_param_default=dynamic_sql_param_default_
		self.dynamic_sql_param_type=dynamic_sql_param_type_
		self.dynamic_sql_param_position=dynamic_sql_param_position_
		self.dynamic_sql_param_order=dynamic_sql_param_order_
		self.dynamic_sql_param_origin_tbl=dynamic_sql_param_origin_tbl_
		self.dynamic_sql_param_origin_col=dynamic_sql_param_origin_col_
		

class SqlRepoDynamicSql:
	def __init__(self, sql_id_:int, 
						sql_type_:str, 
						sql_category_:str, 
						sql_name_:str, 
						sql_description_:str, 
						sql_content_:str, 
						execution_:str, 
						active_:int, 
						sqlRepoParamList_:List[SqlRepoParam],
						sqlParamList_:List[SqlRepoParam],
						sqlRepoDatabaseSchemaBridgeList_:List[SqlRepoDatabaseSchemaBridge]):
		
		self.sql_id=sql_id_
		self.sql_type=sql_type_
		self.sql_category=sql_category_
		self.sql_name=sql_name_
		self.sql_description=sql_description_
		self.sql_content=sql_content_
		self.execution=execution_
		self.active=active_
		self.sqlRepoParamList=sqlRepoParamList_
		self.sqlParamList=sqlParamList_
		self.sqlRepoDatabaseSchemaBridgeList=sqlRepoDatabaseSchemaBridgeList_
		

class SqlRepoList:
	def __init__(self,listOfRepoDynamicSql_:List[SqlRepoDynamicSql]):
		self.listOfRepoDynamicSql=listOfRepoDynamicSql_
'''
Reload Repo List
'''
def reloadSqlRepo(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/sqlrepo/reload"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #  returns SqlRepoList in RestObject.payload


'''
Add a new Sql statement to the repo
'''
def addSql(user:str, session:str, requestId:str, sqlType:str, sqlReturnType:str, sqlCategory:str, sqlName:str, sqlDescription:str, sqlContent:str, execution:str, active:str ):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['sqlType'] = sqlType
	headers['sqlReturnType'] = sqlReturnType
	headers['sqlReturnType'] = sqlReturnType
	headers['sqlName'] = sqlName
	headers['sqlDescription'] = sqlDescription
	headers['sqlContent'] = sqlContent
	headers['execution'] = execution
	headers['active'] = active

	api_url = api_url_stem + "/sqlrepo/sql:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  #  returns SqlRepoList in RestObject.payload


'''
Add a new Sql statement to the repo
'''
def deleteSql(user:str, 
			  session:str, 
			  requestId:str, 
			  sqlId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	
	
	api_url = api_url_stem + "/sqlrepo/sql:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  #  returns GenericResponse with message in RestObject.payload

'''
Update Sql statement to the repo
'''
def updateSql(user:str, 
			  session:str, 
			  requestId:str, 
			  sqlId:int, 
			  databaseId:int, 
			  sqlType:str, 
			  sqlReturnType:str, 
			  sqlCategory:str, 
			  sqlName:str, 
			  sqlDescription:str, 
			  sqlContent:str, 
			  execution:str, 
			  active:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['databaseId'] = str(databaseId)
	headers['sqlType'] = sqlType
	headers['sqlReturnType'] = sqlReturnType
	headers['sqlCategory'] = sqlCategory
	headers['sqlName'] = sqlName
	headers['sqlDescription'] = sqlDescription
	headers['sqlContent'] = sqlContent
	headers['execution'] = execution
	headers['active'] = str(active)
	api_url = api_url_stem + "/sqlrepo/sql:update"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  #  returns GenericResponse with message in RestObject.payload

class SqlRepoParamListDetail:
	def __init__(self,listOfSqlRepoParam_:List[SqlRepoParam]):
		self.listOfSqlRepoParam=listOfSqlRepoParam_
		

'''
Add Sql Param to Sql Statement
'''
def addSqlParam(user:str, 
				session:str, 
				requestId:str, 
				sqlId:int, 
				sqlParamName:str, 
				sqlParamType:str, 
				sqlParamDefaultValue:str, 
				sqlParamPosition:str, 
				sqlParamOrder:str, 
				sqlParamOriginTbl:str, 
				sqlParamOriginCol):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['sqlParamName'] = sqlParamName
	headers['sqlParamType'] = sqlParamType
	headers['sqlParamDefaultValue'] = sqlParamDefaultValue
	headers['sqlParamPosition'] = sqlParamPosition
	headers['sqlParamOrder'] = sqlParamOrder
	headers['sqlParamOriginTbl'] = sqlParamOriginTbl
	headers['sqlParamOriginCol'] = sqlParamOriginCol
	
	api_url = api_url_stem + "/sqlrepo/sqlparam:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  #  returns SqlRepoParamListDetail in RestObject.payload

'''
Delete Sql Param
'''
def deleteSqlParam(user:str, session:str, requestId:str, sqlId:int, sqlParamId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['sqlParamId'] = str(sqlParamId)
	
	api_url = api_url_stem + "/sqlrepo/sqlparam:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  

'''
Get Sql Repo List
'''
def getSqlRepoList(user:str, session:str, requestId:str, filter:str, databaseId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['filter'] = filter
	headers['databaseId'] = str(databaseId)
	
	api_url = api_url_stem + "/sqlrepo"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())   #  returns SqlRepoList in RestObject.payload


'''
Get Sql Repo List Without Params
'''
def getSqlRepoListWithNoParams(user:str, session:str, requestId:str, filter:str, databaseId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['filter'] = filter
	headers['databaseId'] = str(databaseId)
	
	api_url = api_url_stem + "/sqlRepo/sql"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #  returns SqlRepoList in RestObject.payload


class SqlRepoDynamicSqlShort:
	def __init__(self,dynamic_sql_id_:int, dynamic_sql_type_:str, dynamic_sql_name_:str, dynamic_sql_description_:str):
		self.dynamic_sql_id=dynamic_sql_id_
		self.dynamic_sql_type=dynamic_sql_type_
		self.dynamic_sql_name=dynamic_sql_name_
		self.dynamic_sql_description=dynamic_sql_description_

class SqlRepoListShortFormat:
	def __init__(self,listOfRepoDynamicSqlShort_:List[SqlRepoDynamicSqlShort]):
		self.listOfRepoDynamicSqlShort=listOfRepoDynamicSqlShort_
		
'''
Get Sql Repo List Summary Format
'''
def getSqlRepoListSummary(user:str, session:str, requestId:str, filter:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['filter'] = filter
	api_url = api_url_stem + "/sqlrepo/sql/summary"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #  returns SqlRepoListShortFormat in RestObject.payload


'''
Get Sql Detail
'''
def getSqlDetail(user:str, session:str, requestId:str, sqlID:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlID'] = str(sqlID)
	api_url = api_url_stem + "/sqlrepo/sql/detail"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #  returns SqlRepoList in RestObject.payload


class SqlParameter:
	def __init__(self,pid_:int, pname_:str, value_:str):
		self.pid=pid_
		self.pname=pname_
		self.value=value_

class ParamObj:
	def __init__(self,plist_:List[SqlParameter]):
		self.plist=plist_
		
'''
Get Sql Param List
'''
def getSqlParamList(user:str, session:str, requestId:str, sqlID:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlID'] = str(sqlID)
	api_url = api_url_stem + "/sqlrepo/params"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #  returns ParamObj in RestObject.payload


class ParamListObj:
	def __init__(self,plistlist_:List[ParamObj]):
		self.plistlist=plistlist_
		

'''
Get Sql Param List for Bulk DML. DQLs and DDLs are excluded
'''
def getSqlParamListBulk(user:str, session:str, requestId:str, sqlID:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlID'] = str(sqlID)
	api_url = api_url_stem + "/sqlrepo/param/bulk"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #  returns ParamListObj in RestObject.payload

class SqlStmToDbBridge:
	def __init__(self,id_:int, sql_id_:int, database_id_:int, database_name_:str, active_:int):
		self.id=id_
		self.sql_id=sql_id_
		self.database_id=database_id_
		self.database_name=database_name_
		self.active=active_

class SqlStmToDbBridgeList:
	def __init__(self,sqlToDbBridgeList_:List[SqlStmToDbBridge]):
		self.sqlToDbBridgeList=sqlToDbBridgeList_

'''
Assign sql statement to a certain database
'''
def addSqlToDbMapping(user:str, session:str, requestId:str, sqlId:int, dbId:int, active:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['dbId'] = str(dbId)
	headers['active'] = str(active)
	api_url = api_url_stem + "/sqlrepo/sqlToDb/mapping:update"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  #  returns SqlStmToDbBridgeList in RestObject.payload

'''
Delete association of sql statement to database
'''
def deleteSqlToDbMapping(user:str, session:str, requestId:str, sqlId:int, dbId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['dbId'] = str(dbId)
	api_url = api_url_stem + "/sqlrepo/sqlToDb/mapping:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())   #  returns GenericResponse with text 'OK' on success in RestObject.payload

'''
Get mapping of sql statements to databases
'''
def listSqlToDbMapping(user:str, session:str, requestId:str, sqlId:int, dbId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['dbId'] = str(dbId)
	api_url = api_url_stem + "/sqlrepo/sqlToDb/mapping:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #  returns SqlStmToDbBridgeList in RestObject.payload

'''
Execute Adhoc Sql
'''
def executeAdhocSql(user:str, 
					session:str, 
					requestId:str, 
					schemaUniqueName:str,
					outputCompression:str,
					persist:str,
					forceNoPush:str,
					sqlType:str,
					comment:str,
					sqlName:str,
					sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['schemaUniqueName'] = schemaUniqueName
	headers['outputCompression'] = outputCompression
	headers['persist'] = persist
	headers['forceNoPush'] = forceNoPush
	headers['sqlType'] = sqlType
	headers['comment'] = comment
	headers['sqlName'] = sqlName
	
	api_url = api_url_stem + "/sqlrepo/execute/adhoc"
	response =requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())  # returns ResultQuery in RestObject.payload
 
'''
Get the List of all user tables in database schema
'''
def generateCreateScriptForTable(user:str, session:str, requestId:str, fromRdbmsSchemaUniqueName:str, tableName:str, sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromRdbmsSchemaUniqueName'] = fromRdbmsSchemaUniqueName
	headers['tableName'] = tableName
	api_url = api_url_stem + "/sqlrepo/database/generate:script"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())  # returns GenericResponse with script content in RestObject.payload

class TableList:
	def __init__(self,lst_:List[str]):
		self.lst=lst_

'''
Get the List of all user tables in database schema
'''
def getDatabaseTables(user:str, session:str, requestId:str, connectionUniqueName:str, schema:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['connectionUniqueName'] = connectionUniqueName
	headers['schema'] = schema
	
	api_url = api_url_stem + "/sqlrepo/database/tables"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json()) # returns TableList in RestObject.payload


'''
Get the List of database schemas
'''
def getDatabaseSchemas(user:str, session:str, requestId:str, connectionUniqueName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['connectionUniqueName'] = connectionUniqueName
	api_url = api_url_stem + "/sqlrepo/database/schemas"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns TableList in RestObject.payload


'''
Execute Sql Repo
'''
def executeSqlRepo(user:str, 
				   session:str, 
				   requestId:str, 
				   sqlID:int, 
				   schemaUniqueName:str,
				   outputCompression:str,
				   outputType:str,
				   batchCount:int,
				   persist:str,
				   comment:str,
				   jsonObjSqlParam:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlID'] = str(sqlID)
	headers['schemaUniqueName'] = schemaUniqueName
	headers['outputCompression'] = outputCompression
	headers['outputType'] = outputType
	headers['batchCount'] = str(batchCount)
	headers['persist'] = persist
	headers['comment'] = comment
	
	api_url = api_url_stem + "/sqlrepo/execute:singleDb"
	response =requests.post(api_url, data=jsonObjSqlParam, headers=headers)
	return retObject(response.json())  # returns ResultQuery in RestObject.payload

class ResultQueryAsList:
	def __init__(self,	streaming_:str, 
						sqlType_:str, 
						isMixedMetadata_:str, 
						recordsAffected_:str, 
						columnsAffected_:str, 
						metadata_:List[ResultMetadata],
						resultQuery_:List[{str, str}]):
		self.streaming_=streaming_
		self.sqlType=sqlType_
		self.isMixedMetadata=isMixedMetadata_
		self.recordsAffected=recordsAffected_
		self.columnsAffected=columnsAffected_
		self.metadata=metadata_
		self.resultQuery=resultQuery_
		

class SqlRepoExecReturn:
	def __init__(self,errorCode_:int, errorMessage_:str, exceptionMessage_:str, results_:ResultQuery, resultsAsList_:ResultQueryAsList):
		self.errorCode=errorCode_
		self.errorMessage=errorMessage_
		self.exceptionMessage=exceptionMessage_
		self.results=results_
		self.resultsAsList_=resultsAsList_
		
'''
Execute Sql On multiple DBs and aggregate results
'''
def executeSqlRepoMultiple(user:str, 
						   session:str, 
						   requestId:str, 
						   sqlID:int, 
						   outputCompression:str,
						   outputType:str, 
						   batchCount:int,
						   comment:str,
						   dbIdList:str,
						   persist:str,
						   jsonObjSqlParam:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlID'] = str(sqlID)
	headers['outputCompression'] = outputCompression
	headers['outputType'] = outputType
	headers['batchCount'] = batchCount
	headers['comment'] = comment
	headers['dbIdList'] = dbIdList  # comma separated id array
	headers['persist'] = persist
	api_url = api_url_stem + "/sqlrepo/execute:multipleDb"
	response =requests.post(api_url, data=jsonObjSqlParam, headers=headers)
	return retObject(response.json())  # returns ResultQuery in RestObject.payload

'''
Execute Sql On multiple DBs and aggregate results
'''
def executeSqlAdhocMultiple(user:str, session:str, requestId:str, strObj:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/sqlrepo/execute/adhoc/multipledb:aggregate"
	response =requests.put(api_url, data=strObj, headers=headers)
	return retObject(response.json())  # returns ResultQuery in RestObject.payload


'''
Create and Insert table from Sql Repo execution
'''
def executeSqlRepoToMigrateData(user:str, 
								session:str, 
								requestId:str, 
								sqlID:int, 
								sourceConnectionName:str, 
								inputCompression:str, 
								destinationConnectionName:str, 
								destinationSchema:str, 
								destinationTable:str,
								jsonObjSqlParam):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['sqlID'] = sqlID
	headers['sourceConnectionName'] = sourceConnectionName
	headers['inputCompression'] = inputCompression
	headers['destinationConnectionName'] = destinationConnectionName
	headers['destinationSchema'] = destinationSchema
	headers['destinationTable'] = destinationTable
	
	api_url = api_url_stem + "/sqlrepo/migrate"
	response =requests.post(api_url, data=jsonObjSqlParam, headers=headers)
	return retObject(response.json())  # returns GenericResponse with printable message in RestObject.payload

'''
Get a list of snapshots to visualize
'''
def getRdbmsSnapshotHistory(user:str, session:str, requestId:str, ownerId:int, startIime:int, endTime:int, sqlStatement:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['ownerId'] = str(ownerId)
	headers['startIime'] = str(startIime)
	headers['endTime'] = str(endTime)
	headers['sqlStatement'] = sqlStatement

	api_url = api_url_stem + "/sqlrepo/snapshot:history"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns SnapshotDbRecordList in RestObject.payload

'''
Get snapshot to visualize
'''
def getRdbmsSnapshot(user:str, session:str, requestId:str, snapshotId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['snapshotId'] = str(snapshotId)
	api_url = api_url_stem + "/sqlrepo/snapshot:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns ResultQuery in RestObject.payload


'''
Delete snapshots
'''
def deleteSnapshot(user:str, session:str, requestId:str, snapshotId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['snapshotId'] = str(snapshotId)
	api_url = api_url_stem + "/sqlrepo/snapshot:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  # returns GenericResponse with 'OK' on success in RestObject.payload

class RecordsAffected:
	def __init__(self,operation_:str, recAffected_:int, recFailed_:int, message_:str):
		self.operation=operation_
		self.recAffected=recAffected_
		self.recFailed=recFailed_
		self.message=message_
		

'''
Copy records from Embedded Sql to RDBMS table
'''
def copyEmbeddedSqlResultToRdbmsTable(user:str, 
									  session:str, 
									  requestId:str, 
									  fromEmbeddedType:str, 
									  fromClusterId:int,
									  fromEmbeddedDatabaseName:str,
									  fromEmbeddedSchemaName:str,
									  toRdbmsConnectionName:str,
									  toRdbmsSchemaName:str,
									  toRdbmsTableName:str,
									  sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromEmbeddedType'] = fromEmbeddedType
	headers['fromClusterId'] = str(fromClusterId)
	headers['fromEmbeddedDatabaseName'] = fromEmbeddedDatabaseName
	headers['fromEmbeddedSchemaName'] = fromEmbeddedSchemaName
	headers['requtoRdbmsConnectionNameestId'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlrepo/copy/embedded/adhoc:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())   # returns RecordsAffected in RestObject.payload

'''
Copy records from Mongodb simple search to RDBMS table
'''
def copyMongoSimpleSearchResultToRdbmsTable(user:str, 
											session:str, 
											requestId:str,
											fromClusterUniqueName:str,
											fromMongoDbName:str,
											fromCollectionName:str,
											itemToSearch:str,
											valueToSearch:str,
											valueToSearchType:str,
											operator:str,
											toRdbmsConnectionName:str,
											toRdbmsSchemaName:str,
											toRdbmsTableName:str
											):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromClusterUniqueName'] = fromClusterUniqueName
	headers['fromMongoDbName'] = fromMongoDbName
	headers['fromCollectionName'] = fromCollectionName
	headers['itemToSearch'] = itemToSearch
	headers['valueToSearch'] = valueToSearch
	headers['valueToSearchType'] = valueToSearchType
	headers['operator'] = operator
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlrepo/copy/mongodb/search:simple"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  # returns RecordsAffected in RestObject.payload



'''
Copy records to RDBMS table from another Mongodb collection(s) range search
'''
def copyMongoRangeSearchResultToRdbmsTable(user:str, 
										   session:str, 
										   requestId:str,
										   fromClusterUniqueName:str,
										   fromMongoDbName:str,
										   fromCollectionName:str,
										   itemToSearch:str,
										   fromValue:str,
										   toValue:str,
										   valueSearchType:str,
										   toRdbmsConnectionName:str,
										   toRdbmsSchemaName:str,
										   toRdbmsTableName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromClusterUniqueName'] = fromClusterUniqueName
	headers['fromMongoDbName'] = fromMongoDbName
	headers['fromCollectionName'] = fromCollectionName
	headers['itemToSearch'] = itemToSearch
	headers['fromValue'] = fromValue
	headers['toValue'] = toValue
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['valueSearchType'] = valueSearchType
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlrepo/copy/mongodb/search:range"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  # returns RecordsAffected in RestObject.payload



'''
Copy records to RDBMS table from full Mongodb collection
'''
def copyMongoFullCollectionToRdbmsTable(user:str, 
										session:str, 
										requestId:str, 
										fromMongoClusterName:str, 
										fromMongoDatabaseName:str, 
										fromMongoCollectionName:str,
										toRdbmsConnectionName:str, 
										toRdbmsSchemaName:str, 
										toRdbmsTableName:str ):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlrepo/copy/mongodb:collection"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())   # returns RecordsAffected in RestObject.payload



'''
Copy records to RDBMS table from Mongodb ad-hoc search
'''
def copyMongoAdhocResultToRdbmsTable(user:str, 
									 session:str, 
									 requestId:str, 
									 fromClusterUniqueName:str, 
									 fromMongoDbName:str, 
									 fromCollectionName:str, 
									 toRdbmsConnectionName:str, 
									 toRdbmsSchemaName:str, 
									 toRdbmsTableName:str,
									 bsonQuery:str ):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	headers['fromClusterUniqueName'] = fromClusterUniqueName
	headers['fromMongoDbName'] = fromMongoDbName
	headers['fromCollectionName'] = fromCollectionName
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlrepo/copy/mongodb:adhoc"
	response =requests.put(api_url, data=bsonQuery, headers=headers)
	return retObject(response.json())  # returns RecordsAffected in RestObject.payload


'''
Copy records to Rdbms table from Elastic DSL query
'''
def copyElasticDslResultToRdbmsTable(user:str, 
									 session:str, 
									 requestId:str, 
									 fromElasticClusterName:str,
									 fromElasticHttpVerb:str, 
									 fromElasticEndPoint:str, 
									 toRdbmsConnectionName:str, 
									 toRdbmsSchemaName:str,
									 toRdbmsTableName:str,
									 sqlContent:str
									 ):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['fromElasticHttpVerb'] = fromElasticHttpVerb
	headers['fromElasticEndPoint'] = fromElasticEndPoint
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlrepo/copy/elastic:dsl"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())  # returns RecordsAffected in RestObject.payload

'''
Create/add records to collection from Elastic SQL query
'''
def copyElasticSqlResultToRdbmsTable(user:str, 
									 session:str, 
									 requestId:str, 
									 fromElasticClusterName:str, 
									 fromElasticFetchSize:str, 
									 toRdbmsConnectionName:str, 
									 toRdbmsSchemaName:str, 
									 toRdbmsTableName:str, 
									 sqlContent:str):
									
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['fromElasticFetchSize'] = fromElasticFetchSize
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlrepo/copy/elastic:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())  # returns RecordsAffected in RestObject.payload

'''
Copy Rdbms Sql result records to another Rdbms System Table
'''
def copyRdbmsSqlResultToRdbmsTable(user:str, 
								   session:str, 
								   requestId:str, 
								   fromRdbmsSchemaUniqueName:str, 
								   toRdbmsConnectionName:str, 
								   toRdbmsSchemaName:str, 
								   toRdbmsTableName:str,
								   sqlContent:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromRdbmsSchemaUniqueName'] = fromRdbmsSchemaUniqueName
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlrepo/copy/sqlrepo:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())  # returns RecordsAffected in RestObject.payload


'''
Copy Csv to table
'''
def copyCsvToRdbmsTable(user:str, 
						session:str, 
						requestId:str, 
						tableScript:str,
						toRdbmsConnectionName:str,
						toRdbmsSchemaName:str,
						toRdbmsTableName:str,
						filePath:str):
	
	fileName, fileContent, fileType = getFileContent(filePath)
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fileType'] = fileType
	headers['tableScript'] = tableScript
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlrepo/copy/sqlrepo/csv:load"
	form_data = {'file': fileContent}
	response =requests.put(api_url, data=form_data, headers=headers)
	return retObject(response.json())  # returns RecordsAffected in RestObject.payload

'''
Validate Sql
'''
def validateSql(user:str, session:str, requestId:str, sqlContent):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/sqlrepo/validate:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response.json())  # returns GenericResponse with DQL,DML,DDL or NONE in RestObject.payload


class HistSqlList:
	def __init__(self,tempSqlList_:str):
		self.tempSqlList=tempSqlList_


'''
Get the List of executed sql statements
'''
def getSqlHistStm(user:str, session:str, requestId:str, type:str, stext:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['type'] = type
	headers['stext'] = stext
	api_url = api_url_stem + "/sqlrepo/history/stm:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns HistSqlList in RestObject.payload


'''
Copy sql statements to another user
'''
def copySqlHistStm(	user:str, 
					session:str, 
					requestId:str, 
					toUserId:int, 
					shaHash:str, 
					type:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toUserId'] = str(toUserId)
	headers['shaHash'] = shaHash
	headers['type'] = type
	api_url = api_url_stem + "/sqlrepo/history/stm:copy"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())  # returns GenericResponse with 'OK' on success in RestObject.payload

'''
Get Sql Param List Detail
'''
def getSqlParamListDetail(user:str, session:str, requestId:str, sqlID:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlID'] = sqlID
	api_url = api_url_stem + "/sqlrepo/param/detail"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())   # returns SqlRepoParamListDetail in RestObject.payload



'''
"Delete an executed sql statement from your profile
'''
def deleteSnapshot(user:str, session:str, requestId:str, shaHash:str, type:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['shaHash'] = shaHash
	headers['type'] = type
	api_url = api_url_stem + "/sqlrepo/history/stm:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  #


''' 
 ############################ SQLRepo Controller END #####################################
'''


''' 
 ############################ User Controller START #####################################
'''





'''
Authenticate user and generate session on success
'''
def login(user:str, password:str, requestId:str, pns:str, deviceToken:str, authBody:str):
	headers = {}
	headers['user'] = user
	headers['password'] = password
	headers['requestId'] = requestId
	headers['pns'] = pns
	headers['deviceToken'] = deviceToken
	api_url = api_url_stem + "/users/user:login"
	response =requests.post(api_url, data=authBody, headers=headers)
	return retObject(response.json())  # returns User in RestObject.payload


'''
Authenticate mobile user and generate session on success
'''
def loginMobil(user:str, password:str, requestId:str, mobileKey:str, pns:str, deviceToken:str, authBody:str):
	headers = {}
	headers['user'] = user
	headers['password'] = password
	headers['requestId'] = requestId
	headers['mobileKey'] = mobileKey
	headers['pns'] = pns
	headers['deviceToken'] = deviceToken
	api_url = api_url_stem + "/users/mobile/user:login"
	response =requests.post(api_url, data=authBody, headers=headers)
	return retObject(response.json())  # returns User in RestObject.payload


'''
Logout
'''
def logout(user:str, session:str, requestId:str, deviceToken:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['deviceToken'] = deviceToken
	api_url = api_url_stem + "/users/user:logout"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())  #

class UserStatus:
	def __init__(self, userName_:int, isSession_:bool, isSocket_:bool):
		self.userName=userName_
		self.isSession=isSession_
		self.isSocket=isSocket_

'''
Check user connectivity
'''
def checkUser(user:str, session:str, requestId:str, checkedUser:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['checkedUser'] = checkedUser
	api_url = api_url_stem + "/users/user:check"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns UserStatus in RestObject.payload

'''
Check user connectivity
'''
def cleanupIdleSessions(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/users:cleanup"
	response =requests.post(api_url, data={}, headers=headers)
	return response  # returns simply 'OK' in the HTTP body


class Department:
	def __init__(self, id_:int, department_:str, description_:str):
		self.id=id_
		self.department=department_
		self.description=description_
		

'''
Add a new department
'''
def addDepartment(user:str, session:str, requestId:str, newDepartment:str, newDepartmentDescription:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['newDepartment'] = newDepartment
	headers['newDepartmentDescription'] = newDepartmentDescription
	api_url = api_url_stem + "/users/department:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  # returns Department in RestObject.payload



'''
Update department
'''
def updateDepartment(user:str, session:str, requestId:str, id:int, newDepartment:str, newDepartmentDescription:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)
	headers['newDepartment'] = newDepartment
	headers['newDepartmentDescription'] = newDepartmentDescription
	
	api_url = api_url_stem + "/users/department:update"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())   # returns Department in RestObject.payload


class Title:
	def __init__(self, id_:int, title_:str, description_:str):
		self.id=id_
		self.title=title_
		self.description=description_
		

'''
Add a new title
'''
def addTitle(user:str, session:str, requestId:str, newTitle:str, newTitleDescription:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['newTitle'] = newTitle
	headers['newTitleDescription'] = newTitleDescription
	
	api_url = api_url_stem + "/users/title:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  # returns Title in RestObject.payload


'''
Update title
'''
def updateTitle(user:str, session:str, requestId:str, titleId:int, newTitle:str, newTitleDescription:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(titleId)
	headers['newTitle'] = newTitle
	headers['newTitleDescription'] = newTitleDescription

	api_url = api_url_stem + "/users/title:update"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())  #

class User:
	def __init__(self, id_:int, 
						userType_:str, 
						user_:str, 
						password_:str, 
						firstName_:str, 
						lastName_:str, 
						userDescription_:str, 
						email_:str, 
						department_:int, 
						title_:int, 
						manager_:int, 
						characteristic_:str, 
						active_:str,
						authenticated_:str,
						session_:str,
						avatarUrl_:str
						):
		self.id=id_
		self.userType=userType_
		self.user=user_
		self.password=password_
		self.firstName=firstName_
		self.lastName=lastName_
		self.userDescription=userDescription_
		self.email=email_
		self.department=department_
		self.title=title_
		self.manager=manager_
		self.characteristic=characteristic_
		self.active=active_
		self.authenticated=authenticated_
		self.session=session_
		self.avatarUrl=avatarUrl_




'''
Add a new user
'''
def addUser(user:str, session:str, requestId:str,
			newUser:str, 
			newUserPassword:str, 
			newUserType:str, 
			newUserFirstName:str, 
			newUserLastName:str, 
			newUserEmail:str, 
			newUserDepartment:int, 
			newUserTitle:int, 
			newUserManager:int, 
			newUserCharacteristic:str, 
			newUserDescription:str, 
			newUserActive:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['newUser'] = newUser
	headers['newUserPassword'] = newUserPassword
	headers['newUserType'] = newUserType
	headers['newUserFirstName'] = newUserFirstName
	headers['newUserLastName'] = newUserLastName
	headers['newUserEmail'] = newUserEmail
	headers['newUserDepartment'] = newUserDepartment
	headers['newUserTitle'] = newUserTitle
	headers['newUserManager'] = newUserManager
	headers['newUserCharacteristic'] = newUserCharacteristic
	headers['newUserDescription'] = newUserDescription
	headers['newUserActive'] = newUserActive
	api_url = api_url_stem + "/users/user:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())   # returns User in RestObject.payload



'''
Register user
'''
def registerUser(newUser:str, newUserPassword:str, newUserType:str, newUserFirstName:str, newUserLastName:str, newUserEmail:str):
	headers = {}
	headers['newUser'] = newUser
	headers['newUserPassword'] = newUserPassword
	headers['newUserType'] = newUserType
	headers['newUserFirstName'] = newUserFirstName
	headers['newUserLastName'] = newUserLastName
	headers['newUserEmail'] = newUserEmail

	api_url = api_url_stem + "/users/user:register"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  # 


'''
Approve a registering used
'''
def approveRegisteringUser(user:str, session:str, requestId:str, newUser:str, departmentId:int, titleId:int, managerId:int, characteristic:str, descrption:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['newUser'] = newUser
	headers['departmentId'] = str(departmentId)
	headers['titleId'] = str(titleId)
	headers['managerId'] = managerId
	headers['characteristic'] = characteristic
	headers['descrption'] = descrption

	api_url = api_url_stem + "/users/user:approve"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  #

'''
Reject a registering used
'''
def rejectRegisteringUser(user:str, session:str, requestId:str, newUser:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['newUser'] = newUser

	api_url = api_url_stem + "/users/user:reject"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response.json())  #

'''
Update user
'''
def updateUser(user:str, 
				session:str, 
				requestId:str,
				userId:int, 
				newUser:str, 
				newUserPassword:str, 
				newUserType:str, 
				newUserFirstName:str, 
				newUserLastName:str, 
				newUserEmail:str, 
				newUserDepartment:int, 
				newUserTitle:int, 
				newUserManager:int, 
				newUserCharacteristic:str, 
				newUserDescription:str, 
				newUserActive:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(userId)
	headers['newUser'] = newUser
	headers['newUserPassword'] = newUserPassword
	headers['newUserType'] = newUserType
	headers['newUserFirstName'] = newUserFirstName
	headers['newUserLastName'] = newUserLastName
	headers['newUserEmail'] = newUserEmail
	headers['newUserDepartment'] = newUserDepartment
	headers['newUserTitle'] = newUserTitle
	headers['newUserManager'] = newUserManager
	headers['newUserCharacteristic'] = newUserCharacteristic
	headers['newUserDescription'] = newUserDescription
	headers['newUserActive'] = newUserActive
	api_url = api_url_stem + "/users/user:update"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())  #

'''
Update user
'''
def quickUserUpdate(user:str, 
				session:str, 
				requestId:str,
				userId:int, 
				newUser:str, 
				newUserType:str, 
				newUserDepartment:int, 
				newUserTitle:int, 
				newUserManager:int, 
				newUserCharacteristic:str, 
				newUserDescription:str, 
				newUserActive:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(userId)
	headers['newUser'] = newUser
	headers['newUserType'] = newUserType
	headers['newUserDepartment'] = newUserDepartment
	headers['newUserTitle'] = newUserTitle
	headers['newUserManager'] = newUserManager
	headers['newUserCharacteristic'] = newUserCharacteristic
	headers['newUserDescription'] = newUserDescription
	headers['newUserActive'] = newUserActive
	api_url = api_url_stem + "/users/user/quick:update"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())  #


'''
Update my first and last name
'''
def updateMyNames(user:str, session:str, requestId:str, userId:int, newUserFirstName:str, newUserLastName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(userId)
	headers['newUserFirstName'] = newUserFirstName
	headers['newUserLastName'] = newUserLastName
	api_url = api_url_stem + "/users/user:update-my-names"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())  #

'''
Update my password
'''
def updateMyPassword(user:str, session:str, requestId:str, userId:int, password:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(userId)
	headers['password'] = password
	api_url = api_url_stem + "/users/user:update-my-password"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())  #



'''
Update my first name and last name
'''
def updateMyEmailAndUserName(user:str, session:str, requestId:str, userId:int, userName, email):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(userId)
	headers['userName'] = userName
	headers['email'] = email
	api_url = api_url_stem + "/users/user:update-my-email"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())  #

'''
Delete User
'''
def deleteUser(user:str, session:str, requestId:str, userId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(userId)
	api_url = api_url_stem + "/users/user:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  #


'''
Delete Department
'''
def deleteDepartment(user:str, session:str, requestId:str, departmentId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(departmentId)
	api_url = api_url_stem + "/users/department:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  #

'''
Delete Title
'''
def deleteTitle(user:str, session:str, requestId:str, titleId:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(titleId)
	api_url = api_url_stem + "/users/title:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response.json())  #


class UserList:
	def __init__(self, listOfUsers_:List[User]):
		self.listOfUsers=listOfUsers_

'''
Get Users
'''
def getUsers(user:str, session:str, requestId:str, paternToSearch:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['paternToSearch'] = paternToSearch
	api_url = api_url_stem + "/users:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # return UserList in RestObject.payload

'''
Get Users minus current
'''
def getUsersMinusCurrent(user:str, session:str, requestId:str, paternToSearch:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['paternToSearch'] = paternToSearch
	api_url = api_url_stem + "/users/minus:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # return UserList in RestObject.payload

'''
Get registering users
'''
def getRegisteringUsers(user:str, session:str, requestId:str, patternToSearch:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['patternToSearch'] = patternToSearch
	api_url = api_url_stem + "/users:registering"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # return UserList in RestObject.payload



class ManagerShort:
	def __init__(self, listOfUsers_:List[User]):
		self.listOfUsers=listOfUsers_
		

class ManagerShortList:
	def __init__(self, listOfUsers_:List[ManagerShort]):
		self.listOfUsers=listOfUsers_
		

'''
Get Managers
'''
def getManagers(user:str, session:str, requestId:str, patternToSearch:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['paternToSearch'] = patternToSearch
	api_url = api_url_stem + "/managers:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # return ManagerShortList in RestObject.payload



'''
Get specific User based on id
'''
def getUser(user:str, session:str, requestId:str, id:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)

	api_url = api_url_stem + "/users/user:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # return UserList in RestObject.payload

class Department:
	def __init__(self, id_:int, department_:str, description_:str):
		self.id=id_
		self.department=department_
		self.description=description_

class DepartmentList:
	def __init__(self, listOfDepartments_:List[Department]):
		self.listOfDepartments=listOfDepartments_

'''
Get Departments
'''
def getDepartments(user:str, session:str, requestId:str, patternToSearch:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['patternToSearch'] = patternToSearch
	api_url = api_url_stem + "/users/departments:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # return DepartmentList in RestObject.payload

'''
Get a specific Department, from an id
'''
def getDepartment(user:str, session:str, requestId:str, id:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = id
	api_url = api_url_stem + "/users/department:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())   # return DepartmentList in RestObject.payload


'''
Get a specific department by name
'''
def getDepartmentByName(user:str, session:str, requestId:str, department:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['department'] = department
	api_url = api_url_stem + "/users/department:search"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #


class Title:
	def __init__(self, id_:int, title_:str, description_:str):
		self.id=id_
		self.title=title_
		self.description=description_
		

class TitleList:
	def __init__(self, listOfTitles_:List[Title]):
		self.listOfTitles=listOfTitles_
		

'''
Get Titles
'''
def getTitles(user:str, session:str, requestId:str, patternToSearch:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['patternToSearch'] = patternToSearch
	api_url = api_url_stem + "/users/titles:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # return TitleList in RestObject.payload

'''
Get a specific title, based on an id
'''
def getTitleById(user:str, session:str, requestId:str, id:int):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)
	api_url = api_url_stem + "/users/title:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # return TitleList in RestObject.payload

'''
Get a specific title, based on title
'''
def getTitleByName(user:str, session:str, requestId:str, title:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['title'] = title
	api_url = api_url_stem + "/users/title:search"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # return TitleList in RestObject.payload

'''
Generates a synthetic session for debug only in DEV or QA environment.
'''
def generateSyntheticDevSession(admin:str, password:str, requestId:str):
	headers = {}
	headers['admin'] = admin
	headers['password'] = password
	headers['requestId'] = requestId
	api_url = api_url_stem + "/users/generateSyntheticSession"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #

'''
50 milliseconds timer subscription
'''
def subscribeToTimer(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/users/timer:subscribe"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns GenericResponse with 'OK' on success in RestObject.payload


'''
unsubscribe from 50 milliseconds timer
'''
def unsubscribeToTimer(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/users/timer:unsubscribe"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  # returns GenericResponse with 'OK' on success in RestObject.payload

''' 
 ############################ User Controller END #####################################
'''

'''
 ############################## Chat API START ########################################
'''



class ChatConfirmation:
	def __init__(self, timeNano_:int, isDelivered_:str, isSaved_:str):
		self.timeNano = timeNano_
		self.isDelivered = isDelivered_
		self.isSaved = isSaved_
  

class MessageMetadata:
	def __init__(self, type_:str, name_:str, lastModified_:str, msg_:str):
		self.type = type_
		self.name = name_
		self.lastModified = lastModified_
		self.msg = msg_
  
class MessageMetadataList: 
	def __init__(self, messageMetadata_:List[str]):
		self.messageMetadata = messageMetadata_ # list of MessageMetadata

		
		
'''
Send a message to a particular user
'''
def sendTextMessageToUser(user:str, session:str, requestId:str, userId:int, toUser:str, toUserId:str, message:str, isEncrypt:str):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['userId'] = str(userId)
	headers['toUser'] = toUser
	headers['toUserId'] = toUserId
	headers['message'] = message
	headers['isEncrypt'] = isEncrypt

	api_url = api_url_stem + "/chat/fromUser/toUser/text:send"
	response =requests.put(api_url, data={}, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload ChatConfirmation


class FileAttachments:
	def __init__(self, filePath_:str, text_:str):
		self.filePath = filePath_
		self.text = text_
		
'''
Send a message to a particular user with attachments
'''
def sendMultipartMessageToUser(	user:str, 
								session:str, 
								requestId:str, 
								userId:int, 
								toUser:str, 
								toUserId:str, 
								message:str, 
								isEncrypt:str, 
								fileAttachments:[]):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['userId'] = str(userId)
	headers['toUser'] = toUser
	headers['toUserId'] = toUserId
	headers['message'] = message
	headers['isEncrypt'] = isEncrypt
	
	messageMetadataList  = MessageMetadataList()

	files = { }
 
 
 
	for fileAttachment in fileAttachments:
		fname = pathlib.Path(fileAttachment.filePath)
		f = open(fileAttachment.filePath, "rb")
		mime_type = mimetypes.guess_type(fileAttachment.filePath)
		fileName = ntpath.basename(fileAttachment.filePath);
		file_bytes = f.read()
		f.close()
		files[fileName] = (fileName, file_bytes, mime_type)
		messageMetadata = MessageMetadata(mime_type, fileName, fname.stat().st_ctime, fileAttachment.text)
		messageMetadataList.append(messageMetadata)


	files["filesMetadata"] = ("filesMetadata", json.dumps(messageMetadataList), "text/plain")
	
	api_url = api_url_stem + "/chat/fromUser/toUser/attachments:send"
	response = requests.put(api_url, files=files, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload ChatConfirmation

class ChatMessage:
	def __init__(self,	text_:str, 
						millisecondsEpoch_:int, 
						fromId_:int, 
						fromUser_:str, 
						toId_:int, 
						toUser_:str, 
						direction_:str, 
						isGroup_:bool, 
						messageId_: str, 
						isRead_:bool, 
						isEncrypt_:str, 
						readCounter_:int, 
						flags_:str):
		self.text = text_
		self.millisecondsEpoch = millisecondsEpoch_
		self.fromId = fromId_
		self.fromUser = fromUser_
		self.toId = toId_
		self.toUser = toUser_
		self.direction = direction_
		self.isGroup = isGroup_
		self.messageId = messageId_
		self.isRead = isRead_
		self.isEncrypt = isEncrypt_
		self.readCounter = readCounter_
		self.flags = flags_
		
		
		
		

class ChatMessageList:
	def __init__(self, chatMessage_:[]):
		self.chatMessage = chatMessage_ # list of ChatMessage




'''
Get a list of messages from a specific user if any
'''
def getMessageToUser(user:str, session:str, requestId:str, toUser:str):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['toUser'] = toUser
 
	api_url = api_url_stem + "/chat/fromUser/toUser/message:get"
	response = requests.post(api_url, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload ChatMessageList

'''
Get a list of messages from a specific user if any
'''
def getNewMessagesFromUser(user:str, session:str, requestId:str, toUser:str, fromDate:int):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['toUser'] = toUser
	headers['fromDate'] = fromDate
 
	api_url = api_url_stem + "/chat/fromUser/toUser/messages/new:get"
	response = requests.post(api_url, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload ChatMessageList
	
'''
Get server time in milliseconds since EPOCH
'''
def getServerTime(requestId: str):
	headers = {}
	headers['requestId'] = requestId
	api_url = api_url_stem + "/chat/server/time:get"
	response = requests.get(api_url, headers=headers)

	return retObject(response.json())  #returns RestObject, which contains payload GenericResponse with payload the timestamp as string
	
class UserPairValue:
	def __init__(self, id_:int, user_:str):
		self.id = id_ 
		self.user = user_
		
		
class UserPairValueList:
	def __init__(self, userPairValue_:[]):
		self.userPairValue = userPairValue_ # list of UserPairValue
		
		
'''
Retrieve the list of users sending messages to user
'''
def getUsersWithOutstandingMessages(user:str, session:str, requestId:str):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	
	api_url = api_url_stem + "/chat/toUser/messages/new:get"
	response = requests.post(api_url, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload UserPairValueList



class ChatRecord:
	def __init__(self, id_:int, 
						fromUser_:str, 
						toUser_:str,
						timestamp_:int, 
						requestId_:str,
						isWithAttachment_:str, 
						isDelivered_:str, 
						mongoUniqueName_: str, 
						mongoDatabase_:bool, 
						mongoCollection_:str
						):
						
		self.id = id_
		self.fromUser = fromUser_
		self.toUser = toUser_
		self.timestamp = timestamp_
		self.requestId = requestId_
		self.isWithAttachment = isWithAttachment_
		self.isDelivered = isDelivered_
		self.mongoUniqueName = mongoUniqueName_
		self.mongoDatabase = mongoDatabase_
		self.mongoCollection = mongoCollection_
		
		

'''
Set transfer status for a message(received or read)
'''
def setTransferFlag(user:str, session:str, requestId:str, chatRecord: ChatRecord):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	
	api_url = api_url_stem + "/chat/fromUser/toUser/message:set"
	response = requests.post(api_url, data=chatRecord, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload ChatRecord



'''
Delete message permanently
'''
def deleteMessage(user:str, session:str, requestId:str, chatRecord: ChatRecord):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	
	api_url = api_url_stem + "/chat/fromUser/toUser/message:delete"
	response = requests.post(api_url, data=chatRecord, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload ChatRecord
	
	
'''
Get unread message list for a particular user
'''
def getUnreadMessageList(user:str, session:str, requestId:str, toUser:str, fromDate:str):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['toUser'] = toUser
	headers['fromDate'] = fromDate
	
	api_url = api_url_stem + "/chat/fromUser/toUser:get"
	response = requests.post(api_url, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload ChatMessage



'''
Get a multipart message after receiving notification
'''
def getHistMessageList(user:str, session:str, requestId:str, toUser:str, fromDate:str,  toDate:str):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['toUser'] = toUser
	headers['fromDate'] = fromDate
	headers['toDate'] = toDate
	
	api_url = api_url_stem + "/chat/fromUser/toUser/history:get"
	response = requests.get(api_url, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload ChatMessage


'''
Get the count of unread messages
'''
def getCountUnreadMessages(user:str, session:str, requestId:str, toUser:str, fromDate:str,  toDate:str):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
		
	api_url = api_url_stem + "/chat/fromUser/toUser/count:get"
	response = requests.get(api_url, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload with number as string
	

class UserToChat:
	def __init__(self, id_:int, 
						fromUser_:str, 
						toUser_:str,
						timestamp_:int, 
						requestId_:str,
						isWithAttachment_:str, 
						isDelivered_:str, 
						mongoUniqueName_: str, 
						mongoDatabase_:bool, 
						mongoCollection_:str
						):
						
		self.id = id_
		self.fromUser = fromUser_
		self.toUser = toUser_
		self.timestamp = timestamp_
		self.requestId = requestId_
		self.isWithAttachment = isWithAttachment_
		self.isDelivered = isDelivered_
		self.mongoUniqueName = mongoUniqueName_
		self.mongoDatabase = mongoDatabase_
		self.mongoCollection = mongoCollection_


class UserToChatList:
		def __init__(self, userToChat_:[]):
			self.userToChat = userToChat_
			
			
'''
Get all available users that match a pattern
'''
def searchChatUsers(user:str, session:str, requestId:str, patternToSearch:str):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['patternToSearch'] = patternToSearch
	
	api_url = api_url_stem + "/chat/users:query"
	response = requests.get(api_url, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload UserToChatList

'''
Get a specific Chat User
'''
def getChatUser(user:str, session:str, requestId:str, userName:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userName'] = userName

	api_url = api_url_stem + "/chat/user:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response.json())  #['payload']['listOfUsers']

'''
Add new chat user
'''
def addChatUser(user:str, session:str, requestId:str, toUser:str, toUserId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['toUser'] = toUser
	headers['toUserId'] = toUserId
	headers['requestId'] = requestId

	api_url = api_url_stem + "/chat/user:add"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())  #['payload']['listOfUsers']

'''
Get Users in the chat
'''
def getChatUsers(user:str, session:str, requestId:str):
	headers = {}
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	api_url = api_url_stem + "/chat/users:chat"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response.json())  #['payload']['listOfUsers']  
	
'''
Send a text message to a particular group
'''
def sendTextMessageToGroup(user:str, session:str, requestId:str, userId:int, toGroup:str, toGroupId:str, message:str, isEncrypt:str):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['userId'] = userId
	headers['toGroup'] = toGroup
	headers['toGroupId'] = toGroupId
	headers['message'] = message
	headers['isEncrypt'] = isEncrypt

	api_url = api_url_stem + "/chat/fromUser/toGroup:send"
	response =requests.put(api_url, data={}, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload ChatConfirmation


		
'''
Send a message to a particular user with attachments
'''
def sendMultipartMessageToGroup(user:str, session:str, requestId:str, 
								 userId:int, 
								 toGroup:str, 
								 toGroupId:str, 
								 message:str, 
								 isEncrypt:str, 
								 fileAttachments:[]):
	headers = {}
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['userId'] = userId
	headers['toGroup'] = toGroup
	headers['toGroupId'] = toGroupId
	headers['message'] = message
	headers['isEncrypt'] = isEncrypt
	
	messageMetadataList  = MessageMetadataList()

	files = {
  
	}
 
 
 
	for fileAttachment in fileAttachments:
		fname = pathlib.Path(fileAttachment.filePath)
		f = open(fileAttachment.filePath, "rb")
		mime_type = mimetypes.guess_type(fileAttachment.filePath)
		fileName = ntpath.basename(fileAttachment.filePath);
		file_bytes = f.read()
		f.close()
		files[fileName] = (fileName, file_bytes, mime_type)
		messageMetadata = MessageMetadata(mime_type, fileName, fname.stat().st_ctime, fileAttachment.text)
		messageMetadataList.append(messageMetadata)


	files["filesMetadata"] = ("filesMetadata", json.dumps(messageMetadataList), "text/plain")
	
	api_url = api_url_stem + "/chat/fromUser/toUser/attachments:send"
	response = requests.put(api_url, files=files, headers=headers)

	return retObject(response.json())  # returns RestObject, which contains payload ChatConfirmation
	


	
'''
 ############################## Chat API END ########################################
'''





