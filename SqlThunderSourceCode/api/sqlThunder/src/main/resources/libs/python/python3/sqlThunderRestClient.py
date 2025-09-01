

'''
Install the following packages:
pip install requests
pip install pymongo

'''

from ctypes.wintypes import BYTE
import requests
import urllib.request
import shutil
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
from bson.codec_options import CodecOptions
from typing import Union
import uuid

class RestInterface:pass
       



class ClassEncoder(JSONEncoder):
	def default(self, o):
		return o.__dict__

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
	def __init__(self, row:List[CellValue], timeStamp:int):
		self.row = row
		self.t = timeStamp


class TableAffected:
	def __init__(self, recAffected:int, tableName:str):
		self.recAffected = recAffected
		self.tableName = tableName


class DataTransfer:
	def __init__(self, countRecord:str, embeddedInMemDbName:str, lstTables:List[TableAffected], isSuccess_:bool):
		self.countRecord = countRecord
		self.isSuccess = isSuccess_
		self.embeddedInMemDbName = embeddedInMemDbName
		self.lstTables = lstTables

class RdbmsCompoundQuery:
	def __init__(self, schemaUniqueName:str, sqlContent:str, uuid:str):
		self.schemaUniqueName = schemaUniqueName
		self.sqlContent = sqlContent
		self.uuid = uuid

class ListRdbmsCompoundQuery:
	def __init__(self, tableName:str, sqlAggregator:str, lst:List[RdbmsCompoundQuery]):
		self.tableName = tableName
		self.sqlAggregator = sqlAggregator
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




class IpToEndpointDbRecord:
	def __init__(self, id:int, idEndpoint:str, ipAddress:str):
		self.id = id
		self.idEndpoint = idEndpoint
		self.ipAddress = ipAddress


class ListOfStrings:
	def __init__(self, lst_:List[str]):
		self.lst=lst_
		

class ListOfInts:
	def __init__(self, lst_:List[int]):
		self.lst=lst_

class ListOfLongs:
	def __init__(self, lst_:List[int]):
		self.lst=lst_

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

	
	
'''START Runtime variables'''
api_url_stem:str = "@api_url_stem@"
def set_api_url_stem(api_url_stem_:str):
        global api_url_stem 
        api_url_stem = api_url_stem_

authorization:str = "@authorization@"
def set_authorization(authorization_:str):
        global authorization 
        authorization = authorization_

internalUserName:str = "@internalUserName@"
def set_internalUserName(internalUserName_:str):
        global internalUserName 
        internalUserName = internalUserName_

internalUserPassword:str = "@internalUserPassword@"
def set_internalUserPassworde(internalUserPassword_:str):
        global internalUserPassword 
        internalUserPassword = internalUserPassword_

internalAdmin:str = "@internalAdmin@"
def set_internalAdmin(internalAdmin_:str):
        global internalAdmin 
        internalAdmin = internalAdmin_

internalAdminPassword:str = "@internalAdminPassword@"
def set_internalAdminPassword(internalAdminPassword_:str):
        global internalAdminPassword 
        internalAdminPassword = internalAdminPassword_

user:str = "@user@"
def set_user(user_:str):
        global user 
        user = user_

session:str = "@session@"
def set_session(session_:str):
        global session 
        session = session_

'''END Runtime variables'''


''' 
 ############################ Cache Controller BEGIN #####################################
'''




class CacheResponse:
	def __init__(self, key:str, value:str, user:str, message:str):
		self.key = key
		self.value = value
		self.user = user
		self.message = message


def isJson(response:requests.Response)-> bool:
	try:
		json.loads(response.json())
		return True
	except ValueError as e:
		print (e)
		return False


def retObject(response:requests.Response) -> RestObject | None:
	if(response.status_code == 400 or response.status_code == 405):
		err = 'Http Error Code:  ' +  str(response.status_code) + ' . Server denied access to this request'
		print(err) 
		return None
		
	r:RestObject | None = response.json()
	return r
	

def printBeautifulObject(r:object):
	json_object = json.loads( ClassEncoder().encode(r) )
	json_formatted_str = json.dumps(json_object, indent=2)
	print(json_formatted_str)
	 
def printBeautiful(r:RestObject):
	json_object = json.loads( ClassEncoder().encode(r["payload"]) )
	json_formatted_str = json.dumps(json_object, indent=2)
	print(json_formatted_str)
	
def printBeautifulOutput(r:RestObject | None):
    if(r==None):print(r)
    else:printBeautiful(r)

def printUglyOutput(r:RestObject | None):
    if(r==None):print(r)
    else:print(r['payload'])
	

'''
#Clear the cache
returns RestObject with CacheResponse in payload with something like {"",JSONObject,internalAdmin, ""}#
'''
def clearCacheStore(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cache/store:clear"
	response = requests.delete(api_url, headers=headers)
	return retObject(response) 
  

'''
#Get all keys in the cache#
#payload in RestObject is CacheResponse#
'''
def getAllCacheKeys(requestId:str, keyList: str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['keyList'] = keyList
	api_url = api_url_stem + "/cache/keys:query"
	response = requests.get(api_url, headers=headers)
	return retObject(response) 



'''
#Set a new key with its value to cache#
#payload in RestObject is CacheResponse#
'''
def setCacheKey(requestId:str, key:str, validFor:int, jsonObj:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['key'] = key
	headers['validFor'] = validFor
	api_url = api_url_stem + "/cache/store:set"
	response = requests.put(api_url, data=jsonObj, headers=headers)
	return retObject(response) 

 
'''
#Set a new key with its value to cache#
#payload in RestObject is CacheResponse#
'''
def updateCacheKeyValueAndValidity(requestId:str, key:str, validFor:int, jsonObj:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['key'] = key
	headers['validFor'] = validFor
	api_url = api_url_stem + "/cache/store:update"
	response = requests.post(api_url, data=jsonObj, headers=headers)
	return retObject(response) 
	
	
'''
#Delete a key and its value#
#payload has CacheResponse with something like {"","", internalAdmin, "CLEARED"}#
'''
def deleteCacheKey(requestId:str, key:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['key'] = key
	api_url = api_url_stem + "/cache/store:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  

'''
#Update Validity#
#jsonResponse['payload'] is CacheResponse #
'''
def updateCacheKeyValidFor(requestId:str, key:str, validFor:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['key'] = key
	headers['validFor'] = validFor
	api_url = api_url_stem + "/cache/store:updateValidFor"
	response =requests.post(api_url, headers=headers)
	return retObject(response)



	
'''
#Update value of a key
jsonResponse['payload'] is CacheResponse #
'''
def updateCacheKeyValue(requestId:str, key:str, jsonObj:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['key'] = key
	api_url = api_url_stem + "/cache/store:updateValue"
	response =requests.post(api_url, data=jsonObj, headers=headers)
	return retObject(response) 
	

'''
Update entire object of a key
jsonResponse['payload'] is CacheResponse
'''
def updateCacheKeyValueAndValidity(requestId:str, key:str, validFor:str, jsonObj:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['key'] = key
	headers['validFor'] = validFor
	api_url = api_url_stem + "/cache/store:update"
	response =requests.post(api_url, data=jsonObj, headers=headers)
	return retObject(response)  
	
	
	
'''
#Get an object from the cache
jsonResponse['payload'] is CacheResponse #
'''
def getCacheKey(requestId:str, key:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['key'] = key
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cache/store:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)



'''
#Check if a key exists in the cache#
#jsonResponse['payload'] is CacheResponse #
'''
def isCacheKey(requestId:str, key:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['key'] = key
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cache/store:isKey"
	response =requests.get(api_url, headers=headers)
	return retObject(response) 
 
 


''' 
 ############################ Cache Controller END #####################################
'''



''' 
 ############################ Cluster Controller BEGIN #####################################
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
#Get All Registered Nodes
payload in RestObject represented by MachineNodeList #
'''
def getAllNodes(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  
	

'''
#Get node by id
returns payload in RestObject represented by MachineNode#
'''
def getNodeById(requestId:str, id:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['id'] = id
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) 

'''
#Delete Node (User action)
returns payload in RestObject represented by MachineNode#
'''
def deleteNode(requestId:str, id:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['id'] = id
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response) 



'''
#Register node to a particular cluster node (User action)#
'''
def addNode(requestId:str, baseUrl:str, type:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['baseUrl'] = baseUrl
	headers['type'] = type
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Register multiple nodes#
'''
def addNodes(requestId:str, nodesList:MachineNodeList):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node/multiple:add"
	response =requests.put(api_url, data=ClassEncoder().encode(nodesList), headers=headers)
	return retObject(response)
	
class PingResult:
	def __init__(self, baseUrl_:str, isReachable_:str):
		self.baseUrl = baseUrl_;
		self.isReachable = isReachable_
		
class ListOfPingResult:
	def __init__(self, listResult_:List):
		self.listResult = listResult_;
		

'''
#Scan all free nodes in the subnet
payload is ListOfPingResult#
'''
def scanClusterNodes(requestId:str, ipStart:str, ipEnd:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['ipStart'] = ipStart
	headers['ipEnd'] = ipEnd
	api_url = api_url_stem + "/cluster/scan:network"
	response =requests.post(api_url, headers=headers)
	return retObject(response) 
	
'''
#Register node to a particular cluster node (User action)#
#payload is ListOfPingResult#
'''	
def registerNode(requestId:str, baseUrl:str, type:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['baseUrl'] = baseUrl
	headers['type'] = type
	api_url = api_url_stem + "/cluster/node:register"
	response =requests.put(api_url, headers=headers)
	return retObject(response) 
	
'''
#responds back to ping#
'''
def pongInfo(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node:info"
	response =requests.get(api_url, headers=headers)
	jsonResponse = response.json()
	return retObject(response)


'''
#Test Admin account
returns as String which is "ERROR" or "OK"#
'''	
def testAdminAccount(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['internalAdmin'] = internalAdmin
	headers['internalAdminPassword'] = internalAdminPassword
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node/test/account:admin"
	response =requests.get(api_url, headers=headers)
	return retObject(response) 


'''
#Test User account#
#returns as String which is "ERROR" or "OK"#
'''	
def testUserAccount(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['internalUserName'] = internalUserName
	headers['internalUserPassword'] = internalUserPassword
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node/test/account:user"
	response =requests.get(api_url, headers=headers)
	return retObject(response) 


'''
#Broadcast replace mode
returns as RestObject with payload as MachineNodeList #
'''	
def broadcastReplace(requestId:str, nodesList: MachineNodeList):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node/broadcast:replace"
	response = requests.put(api_url, data=ClassEncoder().encode(nodesList), headers=headers)
	return retObject(response) 
	
'''
#Broadcast replace mode#
#returns as RestObject with payload as MachineNodeList #
'''	
def broadcastUpdate(requestId:str, nodesList: MachineNodeList):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node/broadcast:update"
	response =requests.put(api_url, data=ClassEncoder().encode(nodesList), headers=headers)
	return retObject(response) 	
	
'''
#Ping a node#
'''	
def ping(requestId:str, baseUrl: str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['baseUrl'] = baseUrl
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node:ping"
	response = requests.get(api_url, headers=headers)
	return retObject(response)
	

'''
#Pong back to ping#
'''	
def pong(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/cluster/node:pong"
	response =requests.get(api_url, headers=headers)
	return retObject(response) 
	
	


''' 
 ############################ Cluster Controller END #####################################
'''

''' 
 ############################ Config Controller BEGIN #####################################
'''

class ConfigRepoDbRecord:
	def __init__(self, id:int, configName:str, configValue:str, configDescription:str, configType:str):
		self.id = id
		self.configName = configName
		self.configValue = configValue
		self.configDescription = configDescription
		self.configType = configType
		
	def __iter__(self):
		return self
	
	def __bytes__(self):
		return self
	
		
class ConfigRepoDbRecordList:
	def __init__(self, configRepoDbRecordLst_:List):
		self.configRepoDbRecordLst = configRepoDbRecordLst_;

	

'''
#Get all current config settings
return RestObject with payload as ConfigRepoDbRecordList#
'''
def getConfig(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) 
	
'''
#Get owner's name#
'''
def getOwner(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config:owner"
	response =requests.get(api_url, headers=headers)
	return retObject(response)



'''
#Change config setting#
'''
def changeConfig(requestId:str, configRepoDbRecord: ConfigRepoDbRecord):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config:update"
	response =requests.post(api_url, data=ClassEncoder().encode(configRepoDbRecord), headers=headers)
	return retObject(response)

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
#Get all endpoint allowed IPs#
#return RestObject with payload as EndpointDbRecordList#
'''
def getAllEndpointAllowedIPs(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config/endpoint:get"
	response = requests.get(api_url, headers=headers)
	return retObject(response)  
	
	
'''
#Add allowed IP to endpoint#
'''
def addEndpointAllowedIp(requestId:str, ip: IpToEndpointDbRecord):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config/endpoint/ip:add"
	response =requests.post(api_url, data=ClassEncoder().encode(ip), headers=headers)
	return retObject(response) # return RestObject with payload as EndPointDbRecord


'''
#Add allowed IP to all endpoint#
'''
def addAllowedIpToAllEndpoints(requestId:str, ipAddress: str):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['ipAddress'] = ipAddress
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config/endpoint/ip:addall"
	response =requests.post(api_url, headers=headers)
	return retObject(response)

'''
#Delete IP associated to endpoint#
'''
def deleteAllowedIpToEndpoint(requestId:str, id:int, idEndpoint:int, ipAddress:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = id
	headers['idEndpoint'] = idEndpoint
	headers['ipAddress'] = ipAddress
	api_url = api_url_stem + "/config/endpoint/ip:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)


'''
#Delete IP associated to endpoint#
'''
def deleteAllowedIpToEndpointRec(requestId:str, ip: IpToEndpointDbRecord):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['ipToEndpointDbRecord'] = ip
	api_url = api_url_stem + "/config/endpointRec/ip:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)


'''
#Add allowed IP to all endpoint#
'''
def deleteAllowedIpToAllEndpoints(requestId:str, ipAddress:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['ipAddress'] = ipAddress
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config/endpoint/ip:deleteAll"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)



'''
#Reload mapping ip to endpoints#
'''
def reloadIpToEndpoints(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/config/endpoint:reload"
	response =requests.post(api_url, headers=headers)
	return retObject(response)

'''
#Test Remote Admin account#
#returns as String which is "ERROR" or "OK"#
'''	
def testNodeAdminAccount(requestId:str, nodeAddress:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['admin'] = internalAdmin
	headers['passcode'] = internalAdminPassword
	headers['requestId'] = requestId
	headers['nodeAddress'] = nodeAddress
	api_url = api_url_stem + "/config/node/test/account:admin"
	response =requests.get(api_url, headers=headers)
	return retObject(response) 


'''
#Test Remote User account#
#returns as String which is "ERROR" or "OK"#
'''	
def testNodeUserAccount(requestId:str, nodeAddress:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = internalUserName
	headers['passcode'] = internalUserPassword
	headers['requestId'] = requestId
	headers['nodeAddress'] = nodeAddress
	api_url = api_url_stem + "/config/node/test/account:user"
	response =requests.get(api_url, headers=headers)
	return retObject(response) 

''' 
 ############################ Config Controller END #####################################
'''



''' 
 ############################ Elastic Controller BEGIN #####################################
'''

class ElasticCompoundQuery:
	def __init__(self, clusterUniqueName_:str, indexName_:str, sqlType_:str, httpVerb_:str, elasticApi_:str, endPoint_:str, sqlContent_:str, uuid_:str):
		self.clusterUniqueName = clusterUniqueName_
		self.indexName = indexName_
		self.sqlType = sqlType_
		self.httpVerb = httpVerb_
		self.elasticApi = elasticApi_
		self.endPoint = endPoint_
		self.sqlContent = sqlContent_
		self.uuid = uuid_

class ListElasticCompoundQuery:
	def __init__(self, elasticCompoundQuery_:List[ElasticCompoundQuery], tableName_:str, sqlAggregator_:str):
		self.lst = elasticCompoundQuery_
		self.tableName = tableName_
		self.sqlAggregator = sqlAggregator_

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

class ElasticHostList:
	def __init__(self, elasticHostLst_:List[ElasticHost]):
		self.elasticHostLst=elasticHostLst_
	
'''
#Query the Elastic Db Repository#
'''
def elasticRepo(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response)
	
'''
#Add a new Elastic cluster with all node connections#
'''
def addElasticCluster(requestId:str, clusterUniqueName:str, clusterDescription:str, hostList:ElasticHostList):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['clusterDescription'] = clusterDescription

	api_url = api_url_stem + "/elastic-repo/cluster:add"
	response =requests.put(api_url, data=ClassEncoder().encode(hostList), headers=headers)
	return retObject(response)
	
'''
#Update current elastic cluster info (cluster name and description)#
'''
def updateElasticCluster(requestId:str, clusterId:int, clusterUniqueName:str, clusterDescription:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['clusterId'] = clusterId
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['clusterDescription'] = clusterDescription


	api_url = api_url_stem + "/elastic-repo/cluster:update"
	response =requests.post(api_url, headers=headers)
	return retObject(response)

'''
#Remove Elasticsearch server/cluster connection#
'''
def removeElasticCluster(requestId:str, clusterUniqueName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName

	api_url = api_url_stem + "/elastic-repo/cluster:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)
	
	
'''
#Add a new host to an existing Elastic cluster#
'''
def addElasticHost(requestId:str, clusterUniqueName:str, elasticHost: ElasticHost):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	api_url = api_url_stem + "/elastic-repo/cluster/host:add"
	response =requests.put(api_url, data=ClassEncoder().encode(elasticHost), headers=headers)
	return retObject(response) 


'''
#Add a new host to an existing Elastic cluster#
'''
def updateElasticHost(requestId:str, clusterUniqueName:str, elasticHost: ElasticHost):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	api_url = api_url_stem + "/elastic-repo/cluster/host:update"
	response = requests.post(api_url, data=ClassEncoder().encode(elasticHost), headers=headers)
	return retObject(response)
	
	
'''
#Add a new host to an existing Elastic cluster#
'''
def deleteElasticHost(requestId:str, clusterUniqueName:str, hostId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['hostId'] = hostId

	api_url = api_url_stem + "/elastic-repo/cluster/host:delete"
	response = requests.delete(api_url, headers=headers)
	return retObject(response)


	
	
'''
#Add elastic repo association#
'''
def addElasticRepoAssociationTable(requestId:str, associationName: str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationName'] = associationName

	api_url = api_url_stem + "/elastic-repo/association:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Delete association#
'''
def deleteElasticRepoAssociation(requestId:str, associationId: int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationId'] = associationId

	api_url = api_url_stem + "/elastic-repo/association:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)


'''
#Delete association#
'''
def deleteElasticRepoAssociation(requestId:str, associationName: str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationName'] = associationName

	api_url = api_url_stem + "/elastic-repo/associationByName:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)


'''
#Get Elastic repo associations#
'''
def getElasticRepoAssociationTable(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo/association:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
#Update association#
'''
def updateElasticRepoAssociationTable(requestId:str, associationId:int, associationName: str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationId'] = associationId
	headers['associationName'] = associationName

	api_url = api_url_stem + "/elastic-repo/association:update"
	response =requests.put(api_url, headers=headers)
	return retObject(response)

class ElasticQueryParam:
	def __init__(self, queryParamId_:int, 
						queryId_:int, 
						queryParamName_:str,
						queryParamDefault_:str,
						queryParamType_:str,
						queryParamPosition_:str,
						queryParamOrder_:str
						):
		self.queryParamId = queryParamId_
		self.queryId = queryId_
		self.queryParamName=queryParamName_
		self.queryParamDefault=queryParamDefault_
		self.queryParamType=queryParamType_
		self.queryParamPosition=queryParamPosition_
		self.queryParamOrder=queryParamOrder_

		

class ElasticQuery:

	def __init__(self, queryId_:int, 
						verb_:str, 
						queryReturnType_:str, 
						queryType_:str, 
						elasticApi_:str, 
						indexName_:str, 
						clusterName_:str, 
						queryCategory_:str, 
						queryName_:str,
						queryDescription_:str,
						endPoint_:str,
						queryContent_:str,
						active_:int,
						elasticQueryParamList_:List[ElasticQueryParam]):
		self.queryId = queryId_
		self.verb = verb_
		self.queryReturnType = queryReturnType_
		self.queryType = queryType_
		self.elasticApi = elasticApi_
		self.indexName = indexName_
		self.clusterName =clusterName_
		self.queryCategory = queryCategory_
		self.queryName=queryName_
		self.queryDescription=queryDescription_
		self.endPoint=endPoint_
		self.queryContent=queryContent_
		self.active=active_
		self.elasticQueryParamList=elasticQueryParamList_


from enum import Enum
class ElasticVerbApi:
	GET = ""
	POST = ""
	PUT = ""
	DELETE = ""
	def __init__(self):
		self.GET = "GET"
		self.POST = "POST"
		self.PUT = "PUT"
		self.DELETE = "DELETE"
    

class ElasticQueryReturnType:
	JSON = ""
	NUMBER = ""
	STRING = ""
	BINARY = ""
	def __init__(self):
		self.JSON = "JSON"
		self.NUMBER = "NUMBER"
		self.STRING = "STRING"
		self.BINARY = "BINARY"


class ElasticQueryType:
	DSL = ""
	SQL = ""
	def __init__(self):
		self.DSL = "DSL"
		self.SQL = "SQL"

class ElasticApi:
	search = ""
	mapping = ""
	sql = ""
	cat = ""
	create = ""
	doc = ""
	source = ""
	delete_by_query = ""
	update = ""
	update_by_query = ""
	mget = ""
	bulk = ""
	reindex = ""
	termvectors = ""
	mtermvectors = ""
	enrich = ""
	enrich_stats = ""
	eql = ""
	features = ""
	features_reset = ""
	fleet = ""
	fleet_fleet_search = ""
	fleet_fleet_msearch = ""
	text_structure_find_structure = ""
	graph_explore = ""
	alias = ""
	aliases = ""
	analyze = ""
	disk_usage = ""
	cache_clear = ""
	clone = ""
	close = ""
	def __init__(self):
		self.search = "_search"
		self.mapping = "_mapping"
		self.sql = "_sql"
		self.cat = "_cat"
		self.create = "_create"
		self.doc = "_doc"
		self.source = "_source"
		self.delete_by_query = "_delete_by_query"
		self.update = "_update"
		self.update_by_query = "_update_by_query"
		self.mget = "_mget"
		self.bulk = "_bulk"
		self.reindex = "_reindex"
		self.termvectors = "_termvectors"
		self.mtermvectors = "_mtermvectors"
		self.enrich = "_enrich"
		self.enrich_stats = "_enrich/_stats"
		self.eql = "_eql"
		self.features = "_features"
		self.features_reset = "_features/_reset"
		self.fleet = "_fleet"
		self.fleet_fleet_search = "_fleet/_fleet_search"
		self.fleet_fleet_msearch = "_fleet/_fleet_msearch"
		self.text_structure_find_structure = "_text_structure/find_structure"
		self.graph_explore = "_graph/explore"
		self.alias = "_alias"
		self.aliases = "_aliases"
		self.analyze = "_analyze"
		self.disk_usage = "_disk_usage"
		self.cache_clear = "_cache/clear"
		self.clone = "_clone"
		self.close = "_close"


'''
#Add a new SQL/DSL statement to the repo#
'''
def addElasticQuery(requestId:str, elasticQuery:ElasticQuery):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo/management/query:add"
	response =requests.put(api_url, data=ClassEncoder().encode(elasticQuery), headers=headers)
	return retObject(response)


'''
#Delete query statement against a Elasticsearch cluster/server #
'''
def deleteElasticQuery(requestId:str, queryId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	api_url = api_url_stem + "/elastic-repo/management/query:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)

'''
#Search Dsl/Sql statement by searching a keyword #
'''
def searchElasticQuery(requestId:str, stringToSearch:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['stringToSearch'] = stringToSearch
	api_url = api_url_stem + "/elastic-repo/management/query:search"
	response =requests.get(api_url, headers=headers)
	return retObject(response)
	

'''
#Get the list of queries associated with a cluster #
'''
def getQueriesForCluster(requestId:str, clusterName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	api_url = api_url_stem + "/elastic-repo/management/query:cluster"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

	
'''
#Get the Dsl/Sql statement by searching a keyword#
'''
def getSpecificQuery(requestId:str, clusterUniqueName:str, queryId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['queryId'] = queryId
	
	api_url = api_url_stem + "/elastic-repo/management/query:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

	
class ElasticExecutedQuery:
	def __init__(self, id_:int,
						requestId_:str, 
						statementId_:int, 
						statementName_:str, 
						statementType_:str, 
						statement_:str, 
						jsonParam_:str, 
						group_:str,
						source_:int, 
						userId_:str, 
						clusterId_:int, 
						httpVerb_:str,
						elasticApi_:str,
						indexName_:str,
						endPoint_:str,
						isOriginalFormat_:str,
						comment_:str,
						repPath_:str, 
						timestamp_:int,
						flag_:str):
		self.id=id_
		self.requestId = requestId_
		self.statementId=statementId_
		self.statementName=statementName_
		self.statementType=statementType_
		self.statement=statement_
		self.jsonParam=jsonParam_
		self.group=group_
		self.source=source_
		self.userId=userId_
		self.clusterId=clusterId_
		self.httpVerb= httpVerb_ 
		self.elasticApi= elasticApi_ 
		self.indexName= indexName_
		self.endPoint= endPoint_
		self.isOriginalFormat= isOriginalFormat_
		self.repPath=repPath_
		self.comment=comment_
		self.timestamp=timestamp_
		self.flag = flag_
		
class ElasticExecutedQueryList:
	def __init__(self, elasticExecutedQueryLst_:List[ElasticExecutedQuery]):
		self.elasticExecutedQueryLst=elasticExecutedQueryLst_
		
class ElasticQueryParam:
	def __iter__(self):
		return self
	def __init__(self, queryParamId_:int, queryId_:int, queryParamName_:str, queryParamDefault_:str, queryParamType_:str, queryParamPosition_:str, queryParamOrder_:int):
		self.queryParamId=queryParamId_
		self.queryId=queryId_
		self.queryParamName=queryParamName_
		self.queryParamDefault=queryParamDefault_
		self.queryParamType=queryParamType_
		self.queryParamPosition=queryParamPosition_
		self.queryParamOrder=queryParamOrder_
		
'''
#Get all params of the query#
'''
def getElasticQueryParams(requestId:str, queryId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	api_url = api_url_stem + "/elastic-repo/management/query/params:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)
	
	
'''
#Get all params of the query#
'''
def getElasticQueryParam(requestId:str, queryId:int, paramName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	headers['paramName'] = paramName
	api_url = api_url_stem + "/elastic-repo/management/query/param:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

	
'''
#Get query params #
'''
def addElasticQueryParam(requestId:str, elasticQueryParam:ElasticQueryParam):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo/management/query/param:add"
	response =requests.put(api_url, data=ClassEncoder().encode(elasticQueryParam), headers=headers)
	return retObject(response)


'''
#Delete query params #
'''
def deleteElasticQueryParam(requestId:str, queryId:int, paramId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	headers['paramId'] = paramId
	api_url = api_url_stem + "/elastic-repo/management/query/param:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)


'''
#Get Input Object for Query execution#
'''
def getElasticQueryInputObject(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo/management/query/signature"
	response =requests.get(api_url, headers=headers)
	return retObject(response)



'''
#Set Query Bridge To Cluster#
'''
def addQueryBridgeToCluster(requestId:str,  queryId:str, clusterId:str, active:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	headers['clusterId'] = clusterId
	headers['active'] = active
	api_url = api_url_stem + "/elastic-repo/management/query/bridge:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Delete Query Bridge To Cluster#
'''
def deleteElasticQueryBridgeToCluster(requestId:str, queryId:str, clusterId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	headers['clusterId'] = clusterId
	api_url = api_url_stem + "/elastic-repo/management/query/bridge:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)


'''
#Set Query Bridge To Cluster#
'''
def getElasticQueryBridgeToCluster(requestId:str, queryId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['queryId'] = queryId
	
	api_url = api_url_stem + "/elastic-repo/management/query/bridge:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
Get the List of executed sql statements
'''
def getElasticHistStm(requestId:str, type:str, stext:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['type'] = type
	headers['sText'] = stext
	api_url = api_url_stem + "/elastic-repo/history/stm:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
Copy sql statements to another user
'''
def copyEsHistStm(requestId:str, 
				  toUserId:int, 
				  statementId:str, 
				  repoAdhoc:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['toUserId'] = toUserId
	headers['statementId'] = statementId
	headers['repoAdhoc'] = repoAdhoc

	
	api_url = api_url_stem + "/elastic-repo/history/stm:copy"
	response =requests.post(api_url, headers=headers)
	return retObject(response)
	

'''
Delete an executed sql statement from your profile
'''
def deleteEsHistStmt(requestId:str, statementId:str, repoAdhoc:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['statementId'] = statementId
	headers['repoAdhoc'] = repoAdhoc

	
	api_url = api_url_stem + "/elastic-repo/history/stm:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)

	
'''
#Create Elasticsearch Index#
'''
def createIndex(requestId:str, 
				clusterUniqueName:str, 
				indexName:str, 
				numberOfShards:int, 
				numberOfReplicas:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['clusterUniqueName'] = clusterUniqueName
	headers['indexName'] = indexName
	headers['numberOfShards'] = str(numberOfShards)
	headers['numberOfReplicas'] = str(numberOfReplicas)
	
	api_url = api_url_stem + "/elastic-repo/index:create"
	response =requests.put(api_url, headers=headers)
	return retObject(response)
	


'''
#Remove elasticsearch index#
'''
def deleteElasticIndex(requestId:str, clusterUniqueName:str, indexName: str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['indexName'] = indexName

	api_url = api_url_stem + "/elastic-repo/cluster/index:remove"
	response = requests.delete(api_url, headers=headers)
	return retObject(response)


'''
#Copy to index from an Embedded Db query#
'''
def copyEmbeddedQueryToElastic(	requestId:str, 
								fromClusterId:str, 
								fromEmbeddedDatabaseName:str, 
								fromEmbeddedSchemaName:str, 
								toElasticClusterName:str, 
								toElasticIndexName:str, 
								sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromClusterId'] = fromClusterId
	headers['fromEmbeddedDatabaseName'] = fromEmbeddedDatabaseName
	headers['fromEmbeddedSchemaName'] = fromEmbeddedSchemaName
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toElasticIndexName'] = toElasticIndexName

	api_url = api_url_stem + "/elastic-repo/index/copy/embedded:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response)

'''
#Copy Csv to Elastic Index#
'''
def copyCsvToElastic(requestId:str, toElasticClusterName:str, toIndexName:str, path:str):
	fileName, fileContent, fileMime = getFileContent(path)

	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fileType'] = str(fileMime)
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toIndexName'] = toIndexName
	
	theFiles = {'attachment': fileContent}
	api_url = api_url_stem + "/elastic-repo/index/copy/csv:load"
	response =requests.put(api_url, files=theFiles, headers=headers)
	return retObject(response)


'''
#Copy to Elastic Index from another Elastic Dsl query#
'''
def copyElasticToElasticViaDsl(requestId:str, 
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
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, data=fromHttpPayload, headers=headers)
	return retObject(response)


'''
#Copy to Elastic Index from another Elastic Sql query#
'''
def copyElasticToElasticViaSql(requestId:str, fromElasticClusterName:str, fromIndexName:str, toElasticClusterName:str, toIndexName:str, fetchSize:int,  batchValue:int, sqlPayload:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)





'''
#Copy to index from Mongo simple search#
'''
def copyMongoAdhocToElastic(requestId:str,  fromMongoClusterName:str, fromMongoDatabaseName:str, fromMongoCollectionName:str, toElasticClusterName:str, toElasticIndexName:str, batchValue:int, bsonQuery:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toElasticIndexName'] = toElasticIndexName
	headers['batchValue'] = batchValue

	api_url = api_url_stem + "/elastic-repo/index/copy/mongo/adhoc:mql"
	response =requests.put(api_url, data=bsonQuery, headers=headers)
	return retObject(response)


'''
#Copy from Mongo collection to Elastic#
'''
def copyMongoFullCollectionToElastic(	requestId:str,
										fromMongoClusterName:str, 
										fromMongoDatabaseName:str, 
										fromMongoCollectionName:str, 
										toElasticClusterName:str, 
										toElasticIndexName:str, 
										batchCount:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['toElasticClusterName'] = toElasticClusterName
	headers['toElasticIndexName'] = toElasticIndexName
	headers['batchCount'] = batchCount
	
	
	
	api_url = api_url_stem + "/elastic-repo/index/copy/mongo:collection"
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Copy to Elastic Index from a Mongo range search#
'''
def copyMongoRangeToElastic(requestId:str,
							fromMongoClusterName:str, 
							fromMongoDatabaseName:str, 
							fromMongoCollectionName:str, 
							toElasticClusterName:str, 
							toElasticIndexName:str, 
							itemToSearch:str, 
							fromValue:str, 
							toValue:str, 
							valueSearchType:str, 
							batchValue:str):
	headers = {}
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Copy to index from Mongo simple search#
'''
def copyMongoSimpleQueryToElastic(	requestId:str,
									fromMongoClusterName:str, 
									fromMongoDatabaseName:str, 
									fromMongoCollectionName:str, 
									toElasticClusterName:str, 
									toElasticIndexName:str, 
									itemToSearch:str, 
									valueToSearch:str, 
									valueToSearchType:str, 
									operator:str, 
									batchValue:str):
	headers = {}
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Copy to index from an RDBMS query#
'''
def copyRDBMSQueryToElastic(requestId:str, fromRdbmsSchemaName:str, toElasticClusterName:str, toElasticIndexName:str, batchValue:str, sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)


'''
#Execute a generic adhoc Dsl query#
'''
def runAdhocDsl(requestId:str, 
				clusterUniqueName:str, 
				httpVerb:str, 
				elasticApi:str, 
				indexName:str, 
				endPoint:str, 
				isOriginalFormat:str, 
				persist:str, 
				comment:str, 
				dslName:str, 
				httpPayload:str):
	headers = {}
	headers['Authorization'] = authorization
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
	headers['dslName'] = dslName
	
	api_url = api_url_stem + "/elastic-repo/index/dsl/adhoc:run"
	response =requests.post(api_url, data=httpPayload, headers=headers)
	return retObject(response)


class QueryType:
	def __init__(self, values_:List[str], typeName_:str, queryType_:str, fieldName_:str):
		self.values=values_
		self.typeName=typeName_
		self.queryType=queryType_
		self.fieldName=fieldName_
		


class ElasticDslType:
	terms = ""
	term = ""
	prefix = ""
	range = ""
	fuzzy = ""
	def __init__(self):
		self.terms = "terms"
		self.term = "term"
		self.prefix = "prefix"
		self.range = "range"
		self.fuzzy = "fuzzy"


'''
#Query index via native DSL#
'''
def searchFuzzyIndex(requestId:str, 
					 clusterUniqueName:str, 
					 indexName:str, 
					 fromRecno:int, 
					 size:int, 
					 queryType:QueryType):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['requestId'] = requestId
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['clusterUniqueName'] = clusterUniqueName
	headers['indexName'] = indexName
	headers['fromRecno'] = fromRecno
	headers['size'] = size
	
	api_url = api_url_stem + "/elastic-repo/index/dsl:fuzzy"
	response =requests.post(api_url, data=ClassEncoder().encode(queryType), headers=headers)
	return retObject(response)


'''
List indeces
'''
def listIndexes(requestId:str, clusterUniqueName:str, indexName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['indexName'] = indexName
	api_url = api_url_stem + "/elastic-repo/index/management:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
#Update index mapping/properties#
'''
def updateIndexMapping(requestId:str, clusterUniqueName:str, indexName:str, properties:dict):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['indexName'] = indexName
	api_url = api_url_stem + "/elastic-repo/index/mapping:update"
	response =requests.post(api_url, data=ClassEncoder().encode(properties), headers=headers)
	return retObject(response)



class ElasticQueryExecParam:
	def __init__(self, queryParamId_:int, value_:str):
		self.queryParamId=queryParamId_
		self.value=value_



class ElasticQueryExec:
	def __init__(self, queryId_:int, elasticQueryExecParamList_:ElasticQueryExecParam):
		self.queryId=queryId_
		self.elasticQueryExecParamList=elasticQueryExecParamList_


'''
#Execute a repo Elasticquery #
'''
def runElasticQueryFromRepo(requestId:str, 
							clusterUniqueName:str,
						    queryId:str, 
							queryType:str, 
							persist:str, 
							comment:str, 
							paramObj:ElasticQueryExec):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['queryId'] = queryId
	headers['queryType'] = queryType
	headers['persist'] = persist
	headers['comment'] = comment
	
	api_url = api_url_stem + "/elastic-repo/index/query:run"
	response =requests.post(api_url, data=ClassEncoder().encode(paramObj), headers=headers)
	return retObject(response)


'''
#Execute an adhoc SQL statement against an index#
'''
def runAdhocSql(requestId:str, 
				clusterUniqueName:str, 
				fetchSize:int, 
				persist:str, 
				comment:str, 
				sqlName:str, 
				sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['fetchSize'] = fetchSize
	headers['persist'] = persist
	headers['comment'] = comment
	headers['sqlName'] = sqlName

	api_url = api_url_stem + "/elastic-repo/index/sql/adhoc:run"
	response =requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response)


'''
#Translate Sql to Dsl#
'''
def translateSqlToDsl(requestId:str, clusterUniqueName:str, sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	api_url = api_url_stem + "/elastic-repo/index/sql:translate"
	response =requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response)


'''
#Delete snapshot#
'''
def deleteElasticSnapshot(requestId:str, snapshotId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['snapshotId'] = str(snapshotId)
	api_url = api_url_stem + "/elastic-repo/snapshot:delete"
	response =requests.post(api_url, headers=headers)
	return retObject(response)


'''
#Get snapshot to visualize#
'''
def getElasticSnapshot(requestId:str, snapshotId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['snapshotId'] = str(snapshotId)
	api_url = api_url_stem + "/elastic-repo/snapshot:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
#Get a list of snapshots to visualize#
'''
def getElasticSnapshotHistory(requestId:str, ownerId:int, startTime:int, endTime:int, sqlStatement:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['ownerId'] = str(ownerId)
	headers['startTime'] = str(startTime)
	headers['endTime'] = str(endTime)
	headers['sqlStatement'] = sqlStatement
	
	
	api_url = api_url_stem + "/elastic-repo/snapshot:history"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

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
	def __init__(self, hList_:List[str] ,hListNameToIndex_:List[str]):
		self.hList=hList_
		self.hListNameToIndex=hListNameToIndex_
	
	

class TableRow:
	def __init__(self, tRow_:List[str]):
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
#Execute Sql or Dsl on multiple clusters / indexes and aggregate results with Sql#
'''
def executeAdhocMultipleIndex(requestId:str, listElasticCompoundQuery:ListElasticCompoundQuery):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/elastic-repo/execute/adhoc/multiple:aggregate"
	response =requests.put(api_url, data=ClassEncoder().encode(listElasticCompoundQuery), headers=headers)
	return retObject(response) 



	

''' 
 ############################ Elastic Controller END #####################################
'''




''' 
 ############################ Sql Repo Environment Controller BEGIN #####################################
'''


class DatabaseTypeList:
	def __init__(self, databaseTypeList_:List[str]):
		self.databaseTypeList=databaseTypeList_
		

'''
#Get Database Types List#
'''
def GetDatabaseTypes(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	api_url = api_url_stem + "/db-types"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


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
#Get Repo List#
'''
def GetRepoDbList(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	api_url = api_url_stem + "/repos"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns DatabaseList in RestObject.payload
''' 
 ############################ Sql Repo Environment Controller END #####################################
'''



''' 
 ############################ Embedded Controller BEGIN #####################################
'''


'''
#Validate Adhoc Sql on a multiple dbs or an entire cluster#
'''
def validateAdhocSqlOnCluster(requestId:str, fullBody:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/adhoc/cluster/validate"
	response =requests.post(api_url, data=fullBody, headers=headers)
	return retObject(response)

'''
#Add user permission to cluster#
'''
def addEmbeddedDbToCluster(requestId:str, userId: int, clusterId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userId'] = userId
	headers['clusterId'] = clusterId
	api_url = api_url_stem + "/embedded/cluster/access:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Delete User access to cluster#
'''
def deleteUserAccessToCluster(requestId:str, userId:int, clusterId: int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userId'] = userId
	headers['clusterId'] = clusterId
	api_url = api_url_stem + "/embedded/cluster/access:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)



'''
#Get list of Embedded Databases for a cluster#
'''
def getUserAccessToCluster(requestId:str, clusterId: int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	api_url = api_url_stem + "/embedded/cluster/access:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

'''
Get Schemas of an embedded db belonging to a cluster
'''
def getSchemas(requestId:str, clusterId: int, dbId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = clusterId
	headers['dbId'] = dbId

	response = getSchemas(headers)
	return retObject(response)

'''
##
'''
def getSchemas(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/cluster/database/schemas"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
#Get Embedded Db Tables#
'''
def getTables(requestId:str, clusterId: int, dbId:int, schema:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	headers['dbId'] = dbId
	headers['schema'] = schema
	api_url = api_url_stem + "/embedded/cluster/database/tables"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
#Add Embedded Databases to a cluster#
'''
def addEmbeddedDbToCluster(requestId:str, er: EmbeddedDbRecord):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/cluster/db:add"
	response =requests.put(api_url, data=ClassEncoder().encode(er), headers=headers)
	return retObject(response)


'''
#Delete Embedded Databases of a cluster#
'''
def deleteEmbeddedDbToCluster(requestId:str, dbId:int, clusterId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['dbId'] = str(dbId)
	headers['clusterId'] = str(clusterId)
	api_url = api_url_stem + "/embedded/cluster/db:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)


'''
#Get list of Embedded Databases for a cluster#
'''
def getEmbeddedDbToCluster(requestId:str, clusterId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	api_url = api_url_stem + "/embedded/cluster/db:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


''' #Create an empty database as is part of a cluster# '''
def newEmbeddedDbToCluster(requestId:str, clusterId:int, dbName:str, dbType: str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	headers['dbName'] = dbName
	headers['dbType'] = dbType
	api_url = api_url_stem + "/embedded/cluster/db:new"
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Add Embedded Cluster#
'''
def addEmbeddedCluster(requestId:str, clusterName:str, description:str, ec: EmbeddedClusterInfo):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['description'] = description
	api_url = api_url_stem + "/embedded/clusters:add"
	response =requests.put(api_url, data=ClassEncoder().encode(ec), headers=headers)
	return retObject(response)

'''
#Delete Embedded Cluster#
'''
def deleteEmbeddedCluster(requestId:str, clusterId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	api_url = api_url_stem + "/embedded/clusters:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)


'''
#Get list of Embedded Clusters#
'''
def getEmbeddedClusters(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/clusters:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
#Copy Elastic DSL query result to Embedded table#
'''
def copyElasticDslResultToEmbedded(	requestId:str,
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
	headers['Authorization'] = authorization
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
	return retObject(response)



'''
#Copy Elastic SQL query result to Embedded table#
'''
def copyElasticSqlResultToEmbedded(	requestId:str,
								    fromElasticClusterName:str, 
									fromElasticFetchSize:str, 
									toEmbeddedType:str, 
									toEmbeddedDatabaseName:str,
									toClusterId:str,
									toEmbeddedSchemaName:str,
									toEmbeddedTableName:str,
									sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)


'''
#Copy Csv to embedded table#
'''
def copyCsvToEmbeddedTable(	requestId:str,
						    tableScript:str,
							toEmbeddedType:str, 
							toClusterId:int, 
							toEmbeddedDatabaseName:str, 
							toEmbeddedSchemaName:str,
							toEmbeddedTableName:str, 
							filePath:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)


'''
#Copy Rdbms Sql result to Embedded table#
'''
def copyEmbeddedSqlResultToEmbedded(requestId:str, 
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
	headers['Authorization'] = authorization
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
	return retObject(response)


'''
#Copy Mongodb collection(s) range search result to Embedded table#
'''
def copyMongoRangeSearchResultToEmbedded(requestId:str,
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
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Copy records from Mongodb simple search to Embedded table#
'''
def copyMongoSimpleSearchResultToEmbedded(	requestId:str,
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
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Copy Mongodb ad-hoc search result to Embedded table#
'''
def copyMongoAdhocResultToEmbedded( requestId:str,
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
	headers['Authorization'] = authorization
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
	return retObject(response)



'''
#Copy full Mongodb collection to Embedded table#
'''
def copyMongoFullCollectionToEmbedded(	requestId:str,
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
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers)
	return retObject(response)

'''
#Copy Rdbms Sql result to Embedded table#
'''
def copyRdbmsSqlResultToEmbedded(	requestId:str,
								    fromRdbmsSchemaUniqueName:str, 
									toEmbeddedType:str,
									toClusterId:str,
									toEmbeddedDatabaseName:str,
									toEmbeddedSchemaName:str,
									toEmbeddedTableName:str,
									sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)


'''
#Get list of supported database types#
'''
def getEmbeddedDbTypes(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/dbtypes:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
#Execute Adhoc DDL#
'''
def getCreateTableStmFromSql(requestId:str, sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/execute/adhoc/statement/table:create"
	response =requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response)



'''
#Execute Adhoc Sql on a multiple dbs or an entire cluster#
'''
def executeAdhocSqlOnCluster(requestId:str,
							 sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/execute/adhoc:cluster"
	response =requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response)

'''
#Execute Adhoc Ddl#
'''
def executeDdl(requestId:str, clusterId:int, fileName:str, t:str, schema:str, sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	headers['fileName'] = fileName
	headers['type'] = t
	headers['schema'] = schema
	api_url = api_url_stem + "/embedded/execute/adhoc:ddl"
	response =requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response)

'''
#Execute Adhoc Sql#
'''
def executeInMemAdhocSql(requestId:str, sqlType:str, clusterId:int, dbId:int, sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlType'] = sqlType
	headers['clusterId'] = str(clusterId)
	headers['dbId'] = str(dbId)
	api_url = api_url_stem + "/embedded/execute/adhoc:single"
	response =requests.post(api_url, data=sqlContent, headers=headers) 
	return retObject(response)


'''
#Execute Adhoc Sql#
'''
def executeInMemAdhocSql(requestId:str, sqlType:str, clusterId:int, dbId:int, sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlType'] = sqlType
	headers['clusterId'] = str(clusterId)
	headers['dbId'] = str(dbId)
	api_url = api_url_stem + "/embedded/execute/inmem/adhoc:single"
	response = requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response)



'''
#Copy Csv to in mem table#
'''
def copyCsvToInMemDb(requestId:str, tableScript:str, filePath:str, comment:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['tableScript'] = tableScript
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/csv:load"
	file = open(filePath, "rb")
	files = {'attachment': file }
	response =requests.put(api_url, files=files, headers=headers)
	return retObject(response)



'''
#Create Empty tables to in mem db#
'''
def createEmptyTablesInMemDb(requestId:str, comment:str, tableDefinition:List[TableDefinition] ):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/table:empty"
	response =requests.put(api_url, data=ClassEncoder().encode(tableDefinition), headers=headers) 
	return retObject(response)


'''
#Append in-mem db#
'''
def appendInMemDbTables(requestId:str, toClusterId:int, toEmbeddedDatabaseName:str, toEmbeddedSchemaName:str,toEmbeddedTableName:str ,tableDefinition:List[TableDefinition] ):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toClusterId'] = toClusterId
	headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
	headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
	headers['toEmbeddedTableName'] = toEmbeddedTableName
	api_url = api_url_stem + "/embedded/inmem/table:append"
	response =requests.put(api_url, data=ClassEncoder().encode(tableDefinition), headers=headers) 
	return retObject(response)

'''
#Load Elastic Index Dsl query result in memory#
'''
def loadElasticIndexInMemViaDsl(requestId:str, 
								fromElasticClusterName:str, 
								fromIndexName:str,  
								fromHttpVerb:str, 
								fromElasticApi:str, 
								fromEndPoint:str, 
								batchValue:int, 
								comment:str, 
								dslStatement:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)



'''
#Load Elastic Index Sql query result in memory# 
'''
def loadElasticIndexInMemViaSql(requestId:str, fromElasticClusterName:str, fromIndexName:str, fromHttpVerb:str, fromElasticApi:str, fromEndPoint:str, batchValue:str, comment:str, sqlStatement:str): 
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)
	
'''
#Copy records to RDBMS table from another Mongodb collection(s) range search#
'''
def loadMongoRangeSearchResultInMem(requestId:str, 
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
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers) 
	return retObject(response)


'''
#Load in memory result from Mongodb simple search#
'''
def loadMongoSimpleSearchResultInMem(requestId:str,
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
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers) 
	return retObject(response)


'''
#Copy records to RDBMS table from Mongodb ad-hoc search#
'''
def loadMongoFullCollectionInMem(requestId:str,
								 fromMongoClusterName:str, 
								fromMongoDatabaseName:str, 
								fromMongoCollectionName:str, 
								batchCount:int, 
								comment:str, 
								bsonQuery:str): 
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)



'''
#Load results in memory full Mongodb collection#
'''
def loadMongoFullCollectionInMem(requestId:str,
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
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers) 
	return retObject(response)





''' #Load RDBMS query result in memory# '''
def loadRdbmsQueriesInMem(requestId:str, schemaUniqueName:str, comment:str, listRdbmsCompoundQuery:List[ListRdbmsCompoundQuery]): 
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['schemaUniqueName'] = schemaUniqueName
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/rdbms:queries"
	response =requests.put(api_url, body=listRdbmsCompoundQuery, headers=headers) 
	return retObject(response)




'''
#Load RDBMS query result in memory#
'''
def loadRdbmsTablesInMem(requestId:str, schemaUniqueName:str, comment:str, listRdbmsTables:List[str]): 
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['schemaUniqueName'] = schemaUniqueName
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/rdbms:tables"
	response =requests.put(api_url, data=ClassEncoder().encode(listRdbmsTables), headers=headers) 
	return retObject(response)



'''
#remove all in-mem storage for request#
'''
def removeRequestInMemoryDbs(requestId:str): 
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/inmem/stores/remove:request"
	response =requests.post(api_url, headers=headers) 
	return retObject(response)

'''
#Load cluster in memory #
'''
def loadEmbeddedClusterInMem(requestId:str, clusterId:int, comment:str): 
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = clusterId
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem:cluster"
	response =requests.put(api_url, headers=headers) 
	return retObject(response)


'''
#Load database in memory.Database is part of a cluster #
'''
def loadEmbeddedDbInMem(requestId:str, clusterId:int, dbId:int, comment:str): 
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['dbId'] = dbId
	headers['clusterId'] = clusterId
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem:database"
	response =requests.put(api_url, headers=headers) 
	return retObject(response)


'''
#Load RDBMS query result in memory #
'''
def loadRdbmsQueryInMem(requestId:str, schemaUniqueName:str, sqlContent:str, comment:str): 
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['schemaUniqueName'] = schemaUniqueName
	headers['sqlContent'] = sqlContent
	headers['comment'] = comment
	api_url = api_url_stem + "/embedded/inmem/rdbms:query"
	response =requests.put(api_url, headers=headers) 
	return retObject(response)


'''
#Load query result in memory#
'''
def loadEmbeddedQueryInMem(requestId:str, fromEmbeddedType:str, fromClusterId:int, fromEmbeddedDatabaseName:str, fromEmbeddedSchemaName:str, comment:str, sqlContent:str): 
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)


'''
#Get a list of in-mem db#
'''
def getInMemoryDbs(requestId:str): 
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/inmem:stores"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

'''
#Get list of Sql Commands#
'''
def getEmbeddedSqlCommands(requestId:str): 
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/embedded/staticinfo:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


''' 
 ############################ Embedded Controller END #####################################
'''


''' 
 ############################ Environment Controller BEGIN #####################################
'''


'''
#Get the log#
'''
def log( requestId:str, stringToSearch:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['stringToSearch'] = stringToSearch
	api_url = api_url_stem + "/environment/log:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
#About this API#
'''
def about(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	api_url = api_url_stem + "/environment:about"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

''' 
 ############################ Environment Controller END #####################################
'''


''' 
 ############################ Exchange Storage Controller BEGIN #####################################
'''



'''
#Receive a file form an external exchange#
'''
def receiveFilesFromRemoteExchange( requestId:str, externalUserEmail:str, externalExchangeUid:str, externalUserPassword:str, toUserEmail:str, fileAttachments:List[str]):
								   
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
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/file/receive:remote"
	response =requests.post(api_url, data=files, headers=headers)
	return retObject(response)



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
#Send a file from this exchange/instance to a remote exchange/instance#
'''
def sendFileToRemoteExchange(requestId:str, 
							 toExchangeId:str, 
							 externalUserPassword:str, 
							 toUserEmail:str, 
							 fileDescriptorList:FileDescriptorList):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	headers['toExchangeId'] = toExchangeId
	headers['externalUserPassword'] = externalUserPassword
	headers['toUserEmail'] = toUserEmail
	api_url = api_url_stem + "/exchange/file/send:remote"
	response =requests.post(api_url, data=ClassEncoder().encode(fileDescriptorList), headers=headers)
	return retObject(response)
	
	
class SnapshotDbRecord:
	def __init__(self, snapshotId_:int, fileName_:str, statementName_:str, outputType_:str, statementType_:str, userId_:int, timestamp_:int, statementContent_:str):
		self.snapshotId=snapshotId_
		self.fileName=fileName_
		self.statementName=statementName_
		self.outputType=outputType_
		self.statementType=statementType_
		self.userId=str(userId_)
		self.timestamp=str(timestamp_)
		self.statementContent=statementContent_


class SnapshotDbRecordList:
	def __init__(self, snapshotDbRecordList_:List[SnapshotDbRecord]):
		self.snapshotDbRecordList=snapshotDbRecordList_


'''
#Get List of Files from a saved Files Repo#
'''
def getSnapshot( requestId:str, startTime:int, endTime:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['startTime'] = str(startTime)
	headers['endTime'] = str(endTime)
	
	api_url = api_url_stem + "/exchange/file/hist:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # RestObject with payload as SnapshotDbRecordList



'''
#Delete a file on this Data Exchange Server or own file on remote#
'''
def deleteFileFromLocalRequest( requestId:str, fileId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fileId'] = fileId
	api_url = api_url_stem + "/exchange/file/delete:local"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  #
		
		
		

class EmbeddedDbRecordList:
	def __init__(self, embeddedDbRecordList_:List[EmbeddedDbRecord]):
		self.embeddedDbRecordList=embeddedDbRecordList_
'''
Get List of Files from a saved Files Repo
'''
def moveAndAttachH2FromExchange(requestId:str, fileId:int, clusterId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fileId'] = str(fileId)
	headers['clusterId'] = str(clusterId)
	
	api_url = api_url_stem + "/exchange/file/hist:get"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  # RestObject with payload as EmbeddedDbRecordList	
	

'''
#Add new remote exchange, local exchange can interact with#
'''
def addNewRemoteExchange(requestId:str, exchangeAddress:str, exchangeName:str, exchangeUid:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	headers['exchangeAddress'] = exchangeAddress
	headers['exchangeName'] = exchangeName
	headers['exchangeUid'] = exchangeUid
	api_url = api_url_stem + "/exchange/exchange:new"
	response =requests.put(api_url, headers=headers)

	return retObject(response)  # returns EmbeddedDbRecordList in  RestObjec.payload 
	
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
#Update remote exchange, local exchange can interact with. This end point will not update own exchange#
'''
def updateRemoteExchange(requestId:str, id:int, exchangeAddress:str, exchangeName:str, exchangeUid:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	headers['id'] = str(id)
	headers['exchangeAddress'] = exchangeAddress
	headers['exchangeName'] = exchangeName
	headers['exchangeUid'] = exchangeUid
	api_url = api_url_stem + "/exchange/exchange:update"
	response =requests.post(api_url, headers=headers)
	return retObject(response)	# returns ExchangeRecord in the RestObject.payload


'''
#Delete Exchange#
'''
def deleteRemoteExchange(requestId:str, exchangeUid:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	headers['exchangeUid'] = exchangeUid
	api_url = api_url_stem + "/exchange/exchange:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response) # returns ExchangeRecord in the RestObject.payload


		
		
'''
#Get exchange info#
'''
def getAllExchanges(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/exchange:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) #returns ExchangeRecordList in the RestObject.payload


'''
#Search exchanges#
'''
def searchExchanges(requestId:str, exchange:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['exchange'] = exchange
	api_url = api_url_stem + "/exchange/exchange/search:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) #returns ExchangeRecordList in the RestObject.payload
	

		
		
		
'''
#Get exchange info#
'''
def getAssociatedExchanges(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/exchange/associated:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	# returns ExchangeList in the RestObject.payload

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
#Get exchange info#
'''
def getExchangeUsers(requestId:str, email:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['email'] = email
	api_url = api_url_stem + "/exchange/users/email:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	# returns UserDbList in the RestObject.payload

'''
Get exchange info
'''
def getUsersByExchange(requestId:str, exchangeId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['exchangeId'] = str(exchangeId)
	api_url = api_url_stem + "/exchange/users/email:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	# returns UserDbList in the RestObject.payload
	
'''
#Get associated exchanges to a user#
'''
def getAssociatedUsersToExchange(requestId:str, userId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userId'] = str(userId)
	api_url = api_url_stem + "/exchange/user/exchanges:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	# returns ExchangeList in the RestObject.payload

'''
#Get own user info related to exchanges#
'''
def getCurrentUserInfo(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/user/own:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	# returns UserDbRecord in the RestObject.payload


'''
#Add new user to this exchange#
'''
def addNewExchangeUser(requestId:str, email:str, exchangeId:int, isAdmin:str, userPassword:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	headers['email'] = email
	headers['exchangeId'] = str(exchangeId)
	headers['isAdmin'] = isAdmin
	headers['userPassword'] = userPassword
	api_url = api_url_stem + "/exchange/user:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)	# returns UserDbRecord in the RestObject.payload

'''
#Delete user from current exchange#
'''
def deleteExchangeUser(requestId:str, id:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)
	api_url = api_url_stem + "/exchange/user:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response) # returns ExchangeRecord in the RestObject.payload


'''
#Update external users password#
'''
def updateExternalUserPasswordByAdmin(requestId:str, id:int, password:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)
	headers['password'] = password
	
	api_url = api_url_stem + "/exchange/user:update"
	response =requests.post(api_url, headers=headers)
	return retObject(response)	# returns requestId in the RestObject.payload, RestObject.error depicts the success or failure
	
'''
#Update user from current exchange#
'''
def updateExternalUserPasswordByAdmin(requestId:str, externalUserEmail:str, externalExchangeUid:str, externalUserPassword:str, newExternalUserPassword:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['externalUserEmail'] = externalUserEmail
	headers['externalExchangeUid'] = externalExchangeUid
	headers['externalUserPassword'] = externalUserPassword
	headers['newExternalUserPassword'] = newExternalUserPassword
	
	api_url = api_url_stem + "/exchange/user/self:update"
	response =requests.post(api_url, headers=headers)
	return retObject(response)	# returns requestId in the RestObject.payload, RestObject.error depicts the success or failure


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
#Get user info for a certain exchange#
'''
def getUserToExchanges(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/user/exchange:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	# returns UserToExchangeDbList in the RestObject.payload

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
#Get user to exchange extended info#
'''
def getUserToExchangesExtended(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/user/exchange:getExtended"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	# returns UserToExchangeDbRecordExtended in the RestObject.payload
	
'''
#Add new user to current exchange#
'''
def addUserToExchange(requestId:str, userId:int, exchangeId:int, isAdmin:str ):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userId'] = userId
	headers['exchangeId'] = exchangeId
	headers['isAdmin'] = isAdmin
	api_url = api_url_stem + "/exchange/user/exchange:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)	# returns requestId in the RestObject.payload, RestObject.error depicts the success or failure
	
'''
#Delete user from current exchange#
'''
def deleteUserToExchange(requestId:str, userId:int, exchangeId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userId'] = userId
	headers['exchangeId'] = exchangeId
	api_url = api_url_stem + "/exchange/user/exchange:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)	# returns requestId in the RestObject.payload, RestObject.error depicts the success or failure


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
#Query Files, sent or received#
'''
def queryExchange(requestId:str, externalUserPassword:str, fromUserEmail:str, toUserEmail:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['externalUserPassword'] = externalUserPassword
	headers['fromUserEmail'] = fromUserEmail
	headers['toUserEmail'] = toUserEmail
	
	api_url = api_url_stem + "/exchange/local:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	# returns ExchangeFileDbList in the RestObject.payload

'''
#Query Files, sent or received#
'''
def queryExchange(requestId:str, externalUserEmail:str, externalUserPassword:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['externalUserPassword'] = externalUserPassword
	headers['externalUserEmail'] = externalUserEmail

	
	api_url = api_url_stem + "/exchange/remote:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	# returns ExchangeFileDbList in the RestObject.payload


'''
#Generate uid#
'''
def generateUid(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/generate:uid"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	# returns GenericResponse in the RestObject.payload


'''
#Generate strong password#
'''
def generateStrongPassword(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/exchange/generate:password"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	# returns GenericResponse in the RestObject.payload

	
''' 
 ############################ Exchange Storage Controller END #####################################
'''


''' 
 ############################ Firebase Controller BEGIN #####################################
'''

'''
Send message to firebase topic
'''
def postToTopic(requestId:str, topic:str, message:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/firebase/topics/" + topic
	response =requests.post(api_url, data=message, headers=headers)
	return retObject(response)	# returns String


class ConditionMessageRepresentation:
	def __init__(self, condition_:str, data_:str):
		self.condition=condition_
		self.data=data_
		
		
'''
#Send message to firebase queues enforced by condition#
'''
def postToTopic(requestId:str, condition:ConditionMessageRepresentation):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/firebase/condition" 
	response =requests.post(api_url, data=ClassEncoder().encode(condition), headers=headers)
	return retObject(response)	# returns String
	
	
	
class MulticastMessageRepresentation:
	def __init__(self, registrationTokens_:List[str], data_:str):
		self.registrationTokens=registrationTokens_
		self.data=data_

	
'''
#Send message to a subscribing list of device#
'''
def postToClient(requestId:str, r:MulticastMessageRepresentation):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	api_url = api_url_stem + "/firebase/clients"
	response =requests.post(api_url, data=ClassEncoder().encode(r), headers=headers)
	return retObject(response)	# returns String


'''
Create subscription for a device
'''
def createSubscription(requestId:str, topic:str, registrationTokens:List[str]):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	api_url = api_url_stem + "/firebase/subscriptions/" + topic
	response =requests.post(api_url, data=ClassEncoder().encode(registrationTokens), headers=headers)
	return retObject(response)	# returns String


'''
Delete subscription for a device
'''
def deleteSubscription(requestId:str, topic:str, registrationToken:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	api_url = api_url_stem + "/firebase/subscriptions/" + topic + "/" + registrationToken
	response =requests.post(api_url, data=registrationToken, headers=headers)
	return retObject(response)	# returns String




	
	
''' 
 ############################ Firebase Controller END #####################################
'''


	
''' 
 ############################ Internal Backup Storage Controller BEGIN #####################################
'''

'''
#Create backup before checking in any file to it#
'''
def createBackup(requestId:str, machineName:str, storageType:str, group:str, source:str, fullPath:str, comment:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['machineName'] = machineName
	headers['storageType'] = storageType
	headers['fullPath'] = str(fullPath)
	headers['group'] = str(group)
	headers['source'] = source
	api_url = api_url_stem + "/internalStorage/backup:create"
	response =requests.put(api_url, data=comment, headers=headers)
	return retObject(response)


'''
#Create backup before checking in any file to it#
'''
def endBackup(requestId:str, backupId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['machineName'] = str(backupId)
	api_url = api_url_stem + "/internalStorage/backup:end"
	response =requests.post(api_url, data={}, headers=headers)
	return retObject(response)



'''
#Delete backup with all associate files and privileges to users#
'''
def deleteBackup(requestId:str, backupId:str, force:bool):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['backupId'] = backupId
	headers['force'] = str(force)
	api_url = api_url_stem + "/internalStorage/backup:delete"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns GenericResponse with OK in RestObject.payload


class BackupStorage:
	def __init__(self, backupId_:int, userId_:int, machineName_:str, storageType_:str, comment_:str, group_:str, fullPath_:str, timeStart_:int, timeEnd_:int, flag_:int, accessRec_:int):
		self.backupId=backupId_
		self.userId=userId_
		self.machineName=machineName_
		self.storageType=storageType_
		self.comment=comment_
		self.group=group_
		self.fullPath=fullPath_
		self.timeStart=timeStart_
		self.timeEnd=timeEnd_
		self.flag=flag_;
		self.accessRec=accessRec_


class BackupStorageList:
	def __init__(self, backupStorageLst_:List[BackupStorage]):
		self.backupStorageLst=backupStorageLst_


'''
#Get a list of executed backups for a certain machine in a time range#
'''
def getBackups(requestId:str, machineName:str, timeRangeStart:int, timeRangeEnd:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['machineName'] = machineName
	headers['timeRangeStart'] = str(timeRangeStart)
	headers['timeRangeEnd'] = str(timeRangeEnd)
	api_url = api_url_stem + "/internalStorage/backups:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns BackupStorageList in RestObject.payload




'''
Get a list of user defined groups for a certain machine
'''
def getGroups(requestId:str, machineName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['machineName'] = machineName
	api_url = api_url_stem + "/internalStorage/groups:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns ListOfStrings in RestObject.payload


'''
Get a list of user defined sources for a certain machine
'''
def getSources(requestId:str, machineName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['machineName'] = machineName
	api_url = api_url_stem + "/internalStorage/sources:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns ListOfStrings in RestObject.payload


'''
#Get a list of machines for which backups have been performed#
'''
def getMachines(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/internalStorage/machines:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns ListOfStrings in RestObject.payload




'''
#Upload a generic file, on internal storage, that belong to a backup. A backup has to be created prior to this operation#
'''
def uploadFile(requestId:str, backupId:int, function:str, fullPath:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['backupId'] = backupId
	headers['function'] = function
	api_url = api_url_stem + "/internalStorage/file:upload"
	f = open(fullPath, "r")
	form_data = {'attacment': f}
	response =requests.post(api_url, data=form_data, headers=headers)
	f.close()
	return retObject(response)

'''
#Download file from backup id, where storageId is file id#
'''
def downloadFile(requestId:str, storageId:int, backupId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['storageId'] = str(storageId)
	headers['backupId'] = str(backupId)
	api_url = api_url_stem + "/internalStorage/file:download"

	with requests.g(api_url, headers=headers) as f:
		return f.read().decode('utf-8')
	

	
	
'''
#Delete backed up file belonging to a backup, when not needed anymore.#
'''
def deleteBackedUpFile(requestId:str, storageId:int, backupId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['storageId'] = str(storageId)
	headers['backupId'] = str(backupId)
	
	api_url = api_url_stem + "/internalStorage/file:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response) # returns InternalFileStorageRecord in RestObject.payload


class FileList:
	def __init__(self, listOfFiles_:List[str]):
		self.listOfFiles=listOfFiles_

'''
#List all files in internal storage for certain backup#
'''
def listBackupFiles(requestId:str, backupId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['backupId'] = str(backupId)
	api_url = api_url_stem + "/internalStorage/backup:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns InternalFileStorageList in RestObject.payload
	
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
Add internal user privilege to backup 
'''
def addUserToBackup(requestId:str, backupId:int, userId:int, privilegeType:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['backupId'] = str(backupId)
	headers['userId'] = str(userId)
	headers['privilegeType'] = privilegeType
	api_url = api_url_stem + "/internalStorage/backup/user:add"
	response =requests.put(api_url, data={}, headers=headers)
	return retObject(response) # returns GenericResponse OK


'''
Delete user privilege to backup
'''
def deleteUserToBackup(requestId:str,  backupId:int, userId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['backupId'] = str(backupId)
	headers['userId'] = str(userId)
	api_url = api_url_stem + "/internalStorage/backup/user:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response) # returns GenericResponse OK



class InternalStoragePrivilege:
	def __init__(self, privId_:int, storageId_:int, userId_:int, privType_:str):
		self.privId=privId_
		self.storageId=storageId_
		self.userId=userId_
		self.privType=privType_


class InternalStoragePrivilegeList:
	def __init__(self, storagePrivRepoDbRecordbLst_:List[InternalStoragePrivilege]):
		self.storagePrivRepoDbRecordbLst=storagePrivRepoDbRecordbLst_

	
'''
Get all privileges associated to a backup
'''
def getPrivilegesToBackup(requestId:str, backupId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['backupId'] = str(backupId)
	
	api_url = api_url_stem + "/internalStorage/backup:privileges"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns InternalStoragePrivilegeList in RestObject.payload



'''
Get all user privileges associated to all backups
'''
def getUserPrivilegesToBackups(requestId:str, userId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userId'] = str(userId)
	
	api_url = api_url_stem + "/internalStorage/user:privileges"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns InternalStoragePrivilegeList in RestObject.payload

''' 
 ############################ Internal Backup Storage Controller END #####################################
'''


''' 
 ############################ Logging Controller BEGIN #####################################
'''


'''
#Query internal log#
'''
def queryInternalLog(requestId:str, startTime:int, endTime:int, stringToSearch:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['startTime'] = startTime
	headers['endTime'] = endTime
	headers['stringToSearch'] = stringToSearch
	
	api_url = api_url_stem + "/logging/internal/logs:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)
	
	
'''
#Set application#
'''
def setApplication(requestId:str, application:str, partitionType:str, repositoryType:str, repositoryId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['requestId'] = requestId
	
	headers['application'] = application
	headers['partitionType'] = partitionType
	headers['repositoryType'] = repositoryType
	headers['repositoryId'] = repositoryId
	
	api_url = api_url_stem + "/logging/application:set"
	response =requests.put(api_url, headers=headers)
	return retObject(response) # returns ApplicationRecord in RestObject.payload
	
'''
#Remove Application Logs#
'''
def deleteApplication(requestId:str, applicationId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	api_url = api_url_stem + "/logging/application:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)
	
	
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
#Get All Applications#
'''
def getAllApplications(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/logging/application:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns ApplicationRecordList in RestObject.payload
	
'''
#Get All Partitions of an application#
'''
def getApplicationPartitions(requestId:str, applicationId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	api_url = api_url_stem + "/logging/application/partition:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response)



'''
#Remove Application Logs#
'''
def deleteApplicationLogs(requestId:str, applicationId:int, fromEpochMilliseconds:int, toEpochMilliseconds:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	headers['fromEpochMilliseconds'] = fromEpochMilliseconds
	headers['toEpochMilliseconds'] = toEpochMilliseconds
	
	api_url = api_url_stem + "/logging/application/logs:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)



	
	

'''
#Add new log entry#
'''
def addLogEntry(requestId:str, applicationId:int, message:str, messageType:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	headers['message'] = message
	headers['messageType'] = messageType
	
	api_url = api_url_stem + "/logging/application/logs/entry:add"
	response =requests.post(api_url, headers=headers)
	return retObject(response)

'''
#Add new log entry with artifact/attached file#
'''
def addLogEntryWithArtifact(requestId:str, applicationId:int, message:str, messageType:str, artifactName:str, artifactType:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	headers['message'] = message
	headers['messageType'] = messageType
	headers['artifactName'] = artifactName
	headers['artifactType'] = artifactType
	
	api_url = api_url_stem + "/logging/application/logs/artifact:add"
	response =requests.post(api_url, headers=headers)
	return retObject(response)


'''
#Query logs#
'''
def queryLogs(requestId:str, applicationId:int, fromEpochMilliseconds:int, toEpochMilliseconds:int, textToSearch:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	headers['fromEpochMilliseconds'] = fromEpochMilliseconds
	headers['toEpochMilliseconds'] = toEpochMilliseconds
	headers['textToSearch'] = textToSearch
	
	api_url = api_url_stem + "/logging/application/logs:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns LogRecordList in RestObject.payload


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
#Query logs#
'''
def getLogEntry(requestId:str, applicationId:int, timestampMilliseconds:int, entryId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	headers['timestampMilliseconds'] = timestampMilliseconds
	headers['entryId'] = entryId
	api_url = api_url_stem + "/logging/application/logs:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	# returns LogRecordList in RestObject.payload
	
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
#Query logs#
'''
def getLogArtifact(requestId:str, applicationId:int, timestampMilliseconds:int, entryId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['applicationId'] = applicationId
	headers['timestampMilliseconds'] = timestampMilliseconds
	headers['entryId'] = entryId
	api_url = api_url_stem + "/logging/application/artifact:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) #returns ApplicationRecord in RestObject.payload


''' 
 ############################ Logging Controller END #####################################
'''



''' 
 ############################ ML Controller BEGIN #####################################
'''


'''
#Add Interpreter and its metadata information#
'''
def runModel(requestId:str, mlApiUniqueName:str, interpreterVersion:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mlApiUniqueName'] = mlApiUniqueName
	headers['interpreterVersion'] = interpreterVersion
	api_url = api_url_stem + "/ml/model:run"
	response =requests.put(api_url, headers=headers)
	return retObject(response)

'''
#Get information about ML APi installed#
'''
def getAllMlApiServer(requestId:str, mlApiUniqueName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mlApiUniqueName'] = mlApiUniqueName
	api_url = api_url_stem + "/ml/mlApi:get"
	response =requests.put(api_url, headers=headers)
	return retObject(response)
	
'''
#Start ML API Server#
'''
def startMlApiServer(requestId:str, interpreterName:str, interpreterVersion:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterName'] = interpreterName
	headers['interpreterVersion'] = interpreterVersion
	
	api_url = api_url_stem + "/ml/mlApi:start"
	response =requests.put(api_url, headers=headers)
	return retObject(response)

'''
#Add Interpreter and its metadata information#
'''
def createNewMlApiServer(requestId:str, interpreterName:str, interpreterVersion:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterName'] = interpreterName
	headers['interpreterVersion'] = interpreterVersion
	api_url = api_url_stem + "/ml/mlApi:new"
	response =requests.put(api_url, headers=headers)
	return retObject(response)
	
	
'''
#Add  Api Stub #
'''
def addMlApiStub(requestId:str, interpreterName:str, interpreterVersion:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterName'] = interpreterName
	headers['interpreterVersion'] = interpreterVersion
	api_url = api_url_stem + "/ml/mlApi/deployment:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)

'''
#Add ML Api#
'''
def deleteMlApiStub(requestId:str, interpreterName:str, interpreterVersion:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterName'] = interpreterName
	headers['interpreterVersion'] = interpreterVersion
	api_url = api_url_stem + "/ml/mlApi/deployment:delete"
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Add Interpreter and its metadata information#
'''
def stopMlApiServer(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/ml/mlApi:stop"
	response =requests.put(api_url, headers=headers)
	return retObject(response)




''' 
 ############################ ML Controller END #####################################
'''


''' 
 ############################ MongoDb Controller BEGIN #####################################
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
def reloadMongoRepo(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/mongo-repo:reload"
	response =requests.post(api_url, headers=headers)
	return retObject(response)  # returns MongoClusterList in RestObject.payload


'''
#Get the Mongo Db Repository#
'''
def getMongoRepo(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/mongo-repo:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #  # returns MongoClusterList in RestObject.payload
	
'''
#Add a new Mongo database/cluster connection to the list of available databases/cluster connections#
'''
def addMongoRepo(requestId:str,
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
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers)
	return retObject(response)

'''
#Update a Mongo database/cluster connection#
'''
def updateMongoRepo(requestId:str, 
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
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterId'] = str(clusterId)
	headers['uniqueName'] = clusterUniqueName
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
	response =requests.post(api_url, headers=headers)
	return retObject(response)

	
'''
#Remove a Mongo database/cluster connection#
'''
def removeMongoRepo(requestId:str, clusterUniqueName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	
	api_url = api_url_stem + "/mongo-repo/cluster:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)

'''
#Get the list of databases in a cluster/MongoDB Server#
'''
def mongoDatabaseList(requestId:str, clusterUniqueName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	api_url = api_url_stem + "/mongo-repo/cluster/databases:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response)
	
'''
#Get the list of collections of a single database of a cluster/Mongo Server#
'''
def mongoDatabaseCollectionList(requestId:str, clusterUniqueName:str, databaseName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	
	api_url = api_url_stem + "/mongo-repo/cluster/collections:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	


'''
#Get the last Mql statement by MQL group and source#
'''
def getLastMongoStmt(requestId:str, group:str, src:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['group'] = group
	headers['source'] = src


	api_url = api_url_stem + "/mongo-repo/management/query:last"
	response =requests.get(api_url, headers=headers)
	return retObject(response)	



'''
#Add new collection to a Mongo Database#
'''
def addMongoDatabaseCollection(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	api_url = api_url_stem + "/mongo-repo/cluster/collection:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Drop Records from a list of ids#
'''
def addManyDocumentsToCollection(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str,  jsonDocument:List[str]):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toMongoClusterName'] = clusterUniqueName
	headers['toMongoDatabaseName'] = databaseName
	headers['toMongoCollectionName'] = collectionName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/add:many"
	response =requests.put(api_url, data=ClassEncoder().encode(jsonDocument), headers=headers)
	return retObject(response)


'''
#Add new bucket to a Mongo Database#
'''
def addMongoDatabaseBucket(requestId:str, clusterUniqueName:str, databaseName:str, bucketName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	api_url = api_url_stem + "/mongo-repo/cluster/bucket:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)
	

'''
#Delete bucket from Mongo Database#
'''
def deleteMongoDatabaseBucket(requestId:str, clusterUniqueName:str, databaseName:str, bucketName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	
	api_url = api_url_stem + "/mongo-repo/cluster/bucket:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)

'''
#Drop collection from Mongo Database#
'''
def deleteMongoDatabaseCollection(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	api_url = api_url_stem + "/mongo-repo/cluster/collections:drop"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)

'''
#Create an index for collection#
'''
def createMongoCollectionIndex(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, fieldName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['fieldName'] = fieldName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/index:create"
	response =requests.put(api_url, headers=headers)
	return retObject(response)
	
'''
#Create an index for collection#
'''
def deleteMongoCollectionIndex(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, fieldName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['fieldName'] = fieldName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/index:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)

'''
#replace/update object by id#
'''
def replaceDocumentById(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, idObject:str, newObject:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['idObject'] = idObject

	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/replace-update:single"
	response =requests.post(api_url, data = newObject, headers=headers)
	return retObject(response)
	
'''
#Drop object by id#
'''
def deleteDocumentById(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, idObject:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['idObject'] = idObject
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/delete:single"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)

'''
#Drop Records from a list of ids#
'''
def deleteMultipleDocuments(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str,  jsonDocument:List[str]):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/delete:multiple"
	response =requests.post(api_url, data=ClassEncoder().encode(jsonDocument), headers=headers)
	return retObject(response)


'''
#Delete Mongodb records#
'''
def deleteMongoRecords(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, command:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['command'] = command

	api_url = api_url_stem + "/mongo-repo/cluster/collection/delete:command"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)



'''
#Drop Mongodb Records based on condition#
'''
def deleteMongoRecordsByCondition(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, itemToSearch:str, valueToSearch:str, operator:str, valueToSearchType:str):
	headers = {}
	headers['Authorization'] = authorization
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
	
	api_url = api_url_stem + "/mongo-repo/cluster/collection/delete:condition"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)

'''
#Drop Records in a range#
'''
def deleteMongoRecordsByRange(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, itemToSearch:str, frm:str, to:str, valueToSearchType:str):
	headers = {}
	headers['Authorization'] = authorization
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
	api_url = api_url_stem + "/mongo-repo/cluster/collection/delete:range"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)






class MongoResultSet:
	def __init__(self, metadata_:List[str], resultSet_:List[bson.BSON], countQuery_:int, countCollection_:int):
		self.metadata=metadata_
		self.resultSet=resultSet_
		self.countQuery_=countQuery_
		self.countCollection=countCollection_

'''
#Search collection with a raw mongodb query#
'''
def runRawQuery(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, query:str, sqlName:str, comment:str, persist:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['query'] = query
	headers['sqlName'] = sqlName
	headers['comment'] = comment
	headers['persist'] = persist

	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/search:raw"
	response =requests.get(api_url, headers=headers)
	return retObject(response) #returns MongoResultSet in RestObject.payload


class Range:
	def __init__(self, from__:object, to__:object):
		self.from_ = from__
		self.to_=to__
		


class ComplexAndSearch:
	def __init__(self, range_:dict[str, Range], 
						equal_:dict[str, object], 
						lessThan_:dict[str, object],
						greaterThan_:dict[str, object], 
						like_:dict[str, object], 
						in_:dict[str, object], 
						notIn_:dict[str, object], 
						sort_:dict[str, object],
						fromRow_:int,
						noRow_:int):
		self.range=range_
		self.equal=equal_
		self.lessThan=lessThan_
		self.greaterThan=greaterThan_
		self.like=like_
		self._in=in_
		self.notIn=notIn_
		self.sort=sort_
		self.fromRow=fromRow_
		self.noRow=noRow_




'''
Search for a range by Complex And Statements
'''
def searchDocumentComplexAnd(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, persist:str, comment:str, sqlName:str, groupId:int, command:ComplexAndSearch):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['persist'] = persist
	headers['comment'] = comment
	headers['sqlName'] = sqlName
	headers['groupId'] = str(groupId)
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/search:complex-and"
	response =requests.post(api_url, {command}, headers=headers)
	return retObject(response) #returns MongoResultSet in RestObject.payload


'''
Search collection for text
'''
def searchSimpleText(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, language:str, itemToSearch:str, isHighestScore:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response) #returns MongoResultSet in RestObject.payload

'''
#Get count of all documents in collection#
'''
def getCollectionDocsCount(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, isEstimate:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['isEstimate'] = isEstimate
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/count"
	response =requests.put(api_url, headers=headers)
	return retObject(response)

'''
#Add single document to collection#
'''
def addDocumentToCollection(requestId:str, clusterUniqueName:str, databaseName:str, collectionName:str, jsonDocument:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/add:single"
	response =requests.put(api_url, data=jsonDocument, headers=headers)
	return retObject(response)
	
'''
#Copy records to collection from Embedded adhoc query#
'''
def copyEmbeddedQueryToCollection(	requestId:str, 
									toMongoClusterName:str, 
									toMongoDbName:str, 
									toMongoCollectionName:str, 
									fromEmbeddedType:str,
									fromClusterId:int,
									fromEmbeddedDatabaseName:str,
									fromEmbeddedSchemaName:str,
									sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response) # returns GenericResponse in RestObject.payload

'''
#Copy records to collection from RDBMS query#
'''
def copyRDBMSQueryToCollection(requestId:str, toMongoClusterName:str, toMongoDbName:str, toMongoCollectionName:str, fromRdbmsSchemaUniqueName:str, batchCount:int, sqlContent):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response) #returns GenericResponse in RestObject.payload
	
'''
#Copy records to Mongo collection from Elastic DSL query#
'''
def copyElasticDslToCollection(requestId:str, fromElasticClusterName:str, fromElasticHttpVerb:str, fromElasticEndPoint:str, toMongoClusterName:str, toMongoDbName:str, toMongoCollectionName:str,  batchCount:int, httpPayload:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['fromElasticHttpVerb'] = fromElasticHttpVerb
	headers['fromElasticEndPoint'] = fromElasticEndPoint
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDatabaseName'] = toMongoDbName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['batchCount'] = str(batchCount)
	api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/elastic:dsl"
	response =requests.put(api_url, data=httpPayload, headers=headers)
	return retObject(response) 
	
'''
#Create/add records to collection from Elastic SQL query#
'''
def copyElasticSqlToCollection(requestId:str, fromElasticClusterName:str, toMongoClusterName:str, toMongoDatabaseName:str, toMongoCollectionName:str, fetchSize:int, batchCount:int, sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDatabaseName'] = toMongoDatabaseName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['fetchSize'] = str(fetchSize)
	headers['batchCount'] = str(batchCount)
	
	api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/elastic:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response)	#returns GenericResponse in RestObject.payload 
	
'''
#Copy records to collection from another Mongodb collection(s) simple search#
'''
def copySimpleSearchToCollection(requestId:str, 
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
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers)
	return retObject(response) #returns GenericResponse in RestObject.payload 
	

'''
#Copy records to collection from another Mongodb collection(s) range search#
'''
def copyRangeSearchToCollection(requestId:str,
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
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers)
	return retObject(response) #returns GenericResponse in RestObject.payload 


'''
#Copy records to collection from full Mongodb collection#
'''
def copyFullCollectionToCollection(requestId:str, fromMongoClusterName:str, fromMongoDatabaseName:str, fromMongoCollectionName:str, toMongoClusterName:str, toMongoDatabaseName:str, toMongoCollectionName:str, batchCount:int):
	headers = {}
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers)
	return retObject(response) #returns GenericResponse in RestObject.payload 

'''
#Copy records to collection from full Mongodb collection#
'''
def copyMongoAdhocMqlToCollection(requestId:str, fromMongoClusterName:str, fromMongoDatabaseName:str, fromMongoCollectionName:str, toMongoClusterName:str, toMongoDatabaseName:str,  toMongoCollectionName:str, batchCount:int, bsonQuery:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response) #returns GenericResponse in RestObject.payload 
	

'''
#Copy Csv file to collection#
'''
def copyCsvToCollection(requestId:str, toMongoClusterName:str, toMongoDatabaseName:str, toMongoCollectionName:str, batchCount:int, fileType:str, filePath:str):
	fileName, fileContent, fileMime = getFileContent(filePath)

	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDatabaseName'] = toMongoDatabaseName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['batchCount'] = str(batchCount)
	headers['origFileName'] = fileName
	headers['fileType'] = fileType
	api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/csv:load"
	theFiles = {'attachment': fileContent}
	response =requests.put(api_url, files=theFiles, headers=headers)
	return retObject(response) #returns GenericResponse in RestObject.payload 
	
'''
#add multiple records to a collection delivered in a zip file #
'''
def addBatchDocumentToCollection(requestId:str, toMongoClusterName:str, toMongoDatabaseName:str, toMongoCollectionName:str, batchCount:int, filePath:str):
	fileName, fileContent, fileMime = getFileContent(filePath)
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toMongoClusterName'] = toMongoClusterName
	headers['toMongoDatabaseName'] = toMongoDatabaseName
	headers['toMongoCollectionName'] = toMongoCollectionName
	headers['origFileName'] = fileName
	headers['batchCount'] = str(batchCount)
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/add:batch"
	theFiles = {'attachment': fileContent}
	response =requests.post(api_url, files=theFiles, headers=headers)
	return retObject(response)


'''
#Get previously saved document/resultset#
'''
def getDocument(requestId:str, clusterName:str, dbName:str, cName:str, docId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['clusterName'] = clusterName
	headers['dbName'] = dbName
	headers['cName'] = cName
	headers['docId'] = docId
	
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) #return ResultQuery in RestObject.payload
	
	
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

class LargeMongoBinaryFileSummary:
	def __init__(self, id_:str, filename_:int, timeStamp_:int, fileSize_:int, originalFolder_:str, originalUserId_:int, originalType_:str, originalLastModified_:int):
		self.id=id_
		self.filename=filename_
		self.timeStamp=timeStamp_
		self.fileSize=fileSize_
		self.originalFolder = originalFolder_
		self.originalUserId = originalUserId_
		self.originalType = originalType_
		self.originalLastModified = originalLastModified_


class LargeMongoBinaryFileSummaryList:
	def __init__(self,  largeMongoBinaryFileSummaryLst_:List[LargeMongoBinaryFileSummary]):
		self.largeMongoBinaryFileSummaryLst=largeMongoBinaryFileSummaryLst_

class LargeObjectAssociatedMetadata:
	def __init__(self,  originalFolder_:str, originalUserId_:int, originalType_:str, originalLastModified_:int):
		self.originalFolder = originalFolder_
		self.originalUserId = originalUserId_
		self.originalType = originalType_
		self.originalLastModified = originalLastModified_



class LargeMongoBinaryFile:
	def __init__(self, fileId_:str, filename_:int, largeObjectAssociatedMetadata_:LargeObjectAssociatedMetadata, fileSize_:int, fileStr_:str):
		self.fileId=fileId_
		self.filename=filename_
		self.largeObjectAssociatedMetadata=largeObjectAssociatedMetadata_
		self.fileSize=fileSize_
		self.fileStr = fileStr_


'''
#Get first N documents in the collection#
'''
def getFirstNDocuments(requestId:str, clusterName:str, databaseName:str, collectionName:str, limit:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['collectionName'] = collectionName
	headers['limit'] = str(limit)
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document:firstN"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #return ResultQuery in RestObject.payload

'''
#Get first N documents in the bucket#
'''
def getFirstNBucketFiles(requestId:str, clusterName:str, databaseName:str, bucketName:str, limit:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	headers['limit'] = str(limit)
	api_url = api_url_stem + "/mongo-repo/cluster/bucket/document:firstN"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #return LargeMongoBinaryFileMetaList in RestObject.payload

'''
#Get all documents in the bucket#
'''
def getAllBucketFilesMetadata(requestId:str, clusterName:str, databaseName:str, bucketName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	api_url = api_url_stem + "/mongo-repo/cluster/bucket/document:all"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #return LargeMongoBinaryFileMetaList in RestObject.payload

'''
#Get filtered documents metadata in the bucket#
'''
def getFilteredBucketFilessMetadata(requestId:str, clusterName:str, databaseName:str, bucketName:str, filter:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	headers['filter'] = filter
	api_url = api_url_stem + "/mongo-repo/cluster/bucket/document:filter"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #return LargeMongoBinaryFileSummaryList in RestObject.payload

		
'''
#Simple Search, providing item to search, value to search and value type #
'''
def searchDocumentSimple(requestId:str, clusterName:str, databaseName:str, collectionName:str, itemToSearch:str, valueToSearch:str, valueToSearchType:str, operator:str, persist:str, comment:str, sqlName:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)  #return MongoResultSet in RestObject.payload


'''
#Search for a range of Documents#
'''
def searchDocumentRange(requestId:str, clusterName:str, databaseName:str, collectionName:str, itemToSearch:str, fromValue:str, toValue:str, valueSearchType:str, persist:str, comment:str, sqlName:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)  #return MongoResultSet in RestObject.payload


'''
#Move document/resultset from one collection to another accross clusters and databases#
'''
def moveMongoDocument(requestId:str, clusterNameSource:str, dbNameSource:str, cNameSource:str, clusterNameDest:str, dbNameDest:str, cNameDest:str, docId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['cNameSource'] = cNameSource
	headers['dbNameSource'] = dbNameSource
	headers['clusterNameSource'] = clusterNameSource
	headers['cNameDest'] = cNameDest
	headers['dbNameDest'] = dbNameDest
	headers['clusterNameDest'] = clusterNameDest
	headers['docId'] = docId
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document:move"
	response =requests.post(api_url, headers=headers)
	return retObject(response)

'''
#Create RDBMS table from ResultQuery document#
'''
def createRdbmsTableFromDocument(requestId:str, fromMongoClusterName:str, fromMongoDatabaseName:str, fromMongoCollectionName:str, rdbmsConnectionName:str, rdbmsSchema:str, rdbmsTable:str):
	headers = {}
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers)
	return retObject(response)

class MongoObjectRef:
	def __init__(self, clusterName_:str, databaseName_:str, collectionName_:str, documentId_:str):
		self.clusterName=clusterName_
		self.databaseName=databaseName_
		self.collectionName=collectionName_
		self.documentId=documentId_
	
'''
#Create RDBMS table from multiple ResultQuery documents#
'''
def compoundMongoDocuments(requestId:str, rdbmsConnectionName:str, rdbmsSchema:str, rdbmsTable:str, listOfMongoObjects:List[MongoObjectRef]):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['rdbmsConnectionName'] = rdbmsConnectionName
	headers['rdbmsSchema'] = rdbmsSchema
	headers['rdbmsTable'] = rdbmsTable
	api_url = api_url_stem + "/mongo-repo/cluster/collection/document/rdbms/table:compound"
	response =requests.put(api_url, data=ClassEncoder().encode(listOfMongoObjects), headers=headers)
	return retObject(response)


class MongoRepoMqlParamExecution:
	def __init__(self, id_:int, value_:any):
		self.id=id_
		self.value=value_
		
		
class MongoRepoDynamicMqlExecution:
	def __init__(self, mqlId_:int, mongoRepoMqlParamListList_:List[MongoRepoMqlParamExecution]):
		self.mqlId=mqlId_
		self.mongoRepoMqlParamListList=mongoRepoMqlParamListList_
		
		
		
'''
#Execute Repo Mql#
'''
def runRepoMql(requestId:str, 
				clusterUniqueName:str, 
				mongoDbName:str, 
				mqlId:str, 
				persist:str, 
				comment:str, 
				sqlName:str, 
				parameters:MongoRepoDynamicMqlExecution) -> any:
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
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
	response =requests.post(api_url, data=ClassEncoder().encode(parameters), headers=headers)
	return retObject(response) 


'''
#Execute a generic Mql command providing it in Bson/Json format#
'''
def runAdhocBson(requestId:str, clusterUniqueName:str, mongoDbName:str, collectionName:str, persist:str, comment:str, sqlName:str, command:str):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['mongoDbName'] = mongoDbName
	headers['collectionName'] = collectionName
	headers['persist'] = persist
	headers['comment'] = comment
	headers['requestId'] = requestId
	headers['sqlName'] = sqlName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/query:bson"
	response =requests.post(api_url, data=command, headers=headers)
	return retObject(response)  # returns MongoResultSet in RestObject.payload






'''
#Execute a generic Mql command providing it in Bson/Json format#
'''
def runAdhocMql(requestId:str, clusterUniqueName:str, mongoDbName:str, collectionName:str, persist:str, comment:str, sqlName:str, command:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterUniqueName'] = clusterUniqueName
	headers['mongoDbName'] = mongoDbName
	headers['collectionName'] = collectionName
	headers['persist'] = persist
	headers['comment'] = comment
	headers['sqlName'] = sqlName
	api_url = api_url_stem + "/mongo-repo/cluster/collection/query:adhoc"
	response =requests.post(api_url, data=command, headers=headers)
	return retObject(response)	



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
#Get the Mql statement by searching a keyword #
'''
def searchMongoQuery(requestId:str, stringToSearch:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['stringToSearch'] = stringToSearch
	api_url = api_url_stem + "/mongo-repo/management/query:search"
	response =requests.get(api_url, headers=headers)
	return retObject(response) #returns MongoRepoDynamicMqlList in RestObject.payload

'''
#Add a new MQL statement or update an existing one#
'''
def addMongoQuery(requestId:str, mQuery:MongoRepoDynamicMql):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/mongo-repo/management/query:add"
	response =requests.put(api_url, data=ClassEncoder().encode(mQuery), headers=headers)
	return retObject(response)
	
'''
#Get the Mql statement by id#
'''
def getMongoQueryById(requestId:str, id:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)
	api_url = api_url_stem + "/mongo-repo/management/query:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

'''
#Delete Mql statement #
'''
def deleteMongoQuery(requestId:str, mqlId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mqlId'] = str(mqlId)
	api_url = api_url_stem + "/mongo-repo/management/query:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)


class MongoRepoMqlParamInput:
	def __init__(self, dynamic_mql_param_name_:str, 
						dynamic_mql_param_default_:str, 
						dynamic_mql_param_type_:str, 
						dynamic_mql_param_position_:str, 
						dynamic_mql_param_order_:int, 
						value_:str):
		self.dynamicMqlParamName=dynamic_mql_param_name_
		self.dynamicMqlParamDefault=dynamic_mql_param_default_
		self.dynamicMqlParamType=dynamic_mql_param_type_
		self.dynamicMqlParamPosition=dynamic_mql_param_position_
		self.dynamicMqlParamOrder=dynamic_mql_param_order_
		self.value=value_


'''
#Add params to an existing MQL statement#
'''
def addMongoQueryParam(requestId:str, mqlId:int, param:MongoRepoMqlParamInput):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mqlId'] = str(mqlId)
	api_url = api_url_stem + "/mongo-repo/management/query/param:add"
	response =requests.put(api_url, data=ClassEncoder().encode(param), headers=headers)
	return retObject(response)

'''
#Delete MongoDb Query Parameter#
'''
def deleteMongoQueryParam(requestId:str, mqlId:int, paramId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mqlId'] = str(mqlId)
	headers['paramId'] = str(paramId)
	api_url = api_url_stem + "/mongo-repo/management/query/param:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)
	
	

'''
#Generate Query Param#
'''
def generateMongoQueryParam(requestId:str, paramNumber:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['paramNumber'] = str(paramNumber)
	api_url = api_url_stem + "/mongo-repo/management/query/param:generate"
	response =requests.put(api_url, headers=headers)
	return retObject(response)


'''
#Get All Query Bridges#
'''
def getAllMongoQueryBridges(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/mongo-repo/management/query/bridge/all:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

'''
#Get Query Bridges for a certain statement id#
'''
def getMongoQueryBridges(requestId:str, mqlId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mqlId'] = str(mqlId)
	api_url = api_url_stem + "/mongo-repo/management/query/bridge:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

'''
#Add existing MQL statement to a cluster#
'''
def addMqlToClusterBridge(requestId:str, mqlId:int, clusterId:int, active:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mqlId'] = str(mqlId)
	headers['clusterId'] = str(clusterId)
	headers['active'] = str(active)
	api_url = api_url_stem + "/mongo-repo/management/query/bridge:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)
	
'''
#Delete Mql to Cluster bridge#
'''
def deleteMqlToClusterBridge(requestId:str, bridgeId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['bridgeId'] = str(bridgeId)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
	api_url = api_url_stem + "/mongo-repo/management/query/bridge:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)
	



class RepoAssociationTable:
	def __init__(self, associationId_:int, associationName_:str):
		self.associationId=associationId_
		self.associationName=associationName_
		
		
class RepoAssociationTableList:
	def __init__(self, rList_:List[RepoAssociationTable]):
		self.rList=rList_


'''
#Get associations#
'''
def getAllMongoRepoAssociationTable(requestId:str) :
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/mongo-repo/management/association/all:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns RepoAssociationTableList in RestObject.payload

'''
#Get associations#
'''
def getMongoRepoAssociationTable(requestId:str, associationName:str) :
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationName'] = associationName
	api_url = api_url_stem + "/mongo-repo/management/association:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns RepoAssociationTableList in RestObject.payload

	
'''
#Add association#
'''
def addMongoRepoAssociationTable(requestId:str, associationName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationName'] = associationName
	api_url = api_url_stem + "/mongo-repo/management/association:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)

'''
#Update association#
'''
def updateRepoAssociationTable(requestId:str, associationId:int, associationName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationId'] = str(associationId)
	headers['associationName'] = associationName
	api_url = api_url_stem + "/mongo-repo/management/association:update"
	response =requests.put(api_url, headers=headers)
	return retObject(response)
	
'''
#Delete association#
'''
def deleteMongoRepoAssociationTable(requestId:str, associationId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['associationId'] = str(associationId)
	api_url = api_url_stem + "/mongo-repo/management/association:delete"
	response =requests.put(api_url, headers=headers)
	return retObject(response)




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
#Add large binary file to mongo database#
'''
def addMongoBucketFile(requestId:str, clusterName:str, databaseName:str, bucketName:str, filePath:str):
	fileName, fileContent, fileMime = getFileContent(filePath)
	print(fileName)
	print(fileContent)
	print(fileMime)

	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	headers['fileName'] = fileName
	headers['metadata'] = ''
	api_url = api_url_stem + "/mongo-repo/cluster/large:add"
	theFiles = {'attachment': fileContent}
	response =requests.post(api_url, files=theFiles, headers=headers)
	return retObject(response)


'''
#Get large binary file from mongo database#
'''
def getMongoBucketFile(requestId:str, clusterName:str, databaseName:str, bucketName:str, fileId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	headers['fileId'] = fileId
	api_url = api_url_stem + "/mongo-repo/cluster/large:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns LargeMongoBinaryFile in RestObject.payload
	
	
'''
#Get large binary file from mongo database, browser based#
'''
def downloadMongoBucketFile(requestId:str, clusterName:str, databaseName:str, bucketName:str, fileId:str, downloadFileName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	headers['fileId'] = fileId
	api_url = api_url_stem + "/mongo-repo/cluster/large:download"
	response = requests.get(api_url, headers=headers, stream=True)

	with open(downloadFileName, 'wb') as out_file:
		shutil.copyfileobj(response.raw, out_file)

	return response.headers




'''
#Delete Mongo Bucket file from mongo database#
'''
def deleteMongoBucketFile(requestId:str, clusterName:str, databaseName:str, bucketName:str, fileId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	headers['fileId'] = fileId
	api_url = api_url_stem + "/mongo-repo/cluster/large:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)
	
'''
#Delete Mongo Bucket file from mongo database#
'''
def deleteManyFilesFromBucket(requestId:str, clusterName:str, databaseName:str, bucketName:str, filter:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['clusterName'] = clusterName
	headers['databaseName'] = databaseName
	headers['bucketName'] = bucketName
	headers['filter'] = filter
	api_url = api_url_stem + "/mongo-repo/cluster/large/many:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)


class MongoCompoundQuery:
	def __init__(self, clusterUniqueName_:str, mongoDbName_:str, collectionName_:str, queryContent_:str, uuid_:str):
		self.clusterUniqueName=clusterUniqueName_
		self.mongoDbName=mongoDbName_
		self.collectionName=collectionName_
		self.queryContent=queryContent_
		self.uuid = uuid_
		
		
class ListMongoCompoundQuery:
	def __init__(self, tableName_:str, sqlAggregator_:str, lst_:List[MongoCompoundQuery]):
		self.tableName=tableName_
		self.sqlAggregator=sqlAggregator_
		self.lst=lst_


'''
#Execute Mql on multiple clusters / collections and aggregate results with Sql#
'''
def executeAdhocMultipleCollection(requestId:str, strObj:ListMongoCompoundQuery, sqlName:str, comment:str, persist:str):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlName'] = sqlName
	headers['comment'] = comment
	headers['persist'] = persist
	api_url = api_url_stem + "/mongo-repo/execute/adhoc/multiple:aggregate"
	response =requests.put(api_url, data=ClassEncoder().encode(strObj), headers=headers)
	return retObject(response)  # returns ResultQuery in RestObject.payload


'''
Query previously saved record set
'''
def queryMongoHistoryResults(requestId:str, fromDateTime:int, toDateTime:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromDateTime'] = fromDateTime
	headers['toDateTime'] = toDateTime
	api_url = api_url_stem + "/mongo-repo/cluster/result:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

'''
Query previously saved record set
'''
def queryMongoHistoryResult(requestId:str, objectType:str, mqlId:int, fromDateTime:int, toDateTime:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['mqlId'] = mqlId
	headers['fromDateTime'] = fromDateTime
	headers['toDateTime'] = toDateTime
	api_url = api_url_stem + "/mongo-repo/cluster/result:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


class MongoExecutedQuery:
	def __init__(self, id_:int,requestId_:str,statementId_:int,statementName_:str,statementType_:str,statement_:str,isValid_:str,jsonParam_:str,clusterId_:int,database_:str,collection_:str,groupId_:int,source_:str, userId_:int,repPath_:str,comment_:str,timestamp_:int,flag_:int,cntAccess_:int):
		self.id = id_
		self.requestId = requestId_
		self.statementId = statementId_
		self.statementName = statementName_
		self.statementType = statementType_
		self.statement = statement_
		self.statement = statement_
		self.isValid = isValid_
		self.jsonParam = jsonParam_
		self.clusterId = clusterId_
		self.database = database_
		self.collection = collection_
		self.groupId = groupId_
		self.source = source_
		self.userId = userId_
		self.repPath = repPath_
		self.comment = comment_
		self.timestamp = timestamp_
		self.flag = flag_
		self.cntAccess = cntAccess_


		
class MongoExecutedQueryList:
	def __init__(self, mongoExecutedQueryLst_:List[MongoExecutedQuery]):
		self.mongoExecutedQueryLst=mongoExecutedQueryLst_
	


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
 ############################ MongoDb Controller END #####################################
'''


''' 
 ############################ RestApi Controller BEGIN #####################################
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
#Get all user saved requests#
'''
def getAllRequests(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/restapi/requests:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns UserRestApiRequestDetailList in RestObject.payload
	

'''
#Get saved request#
'''
def getRequest(requestId:str, restApiId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['restApiId'] = str(restApiId)
	api_url = api_url_stem + "/restapi/request:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns UserRestApiRequestDetail in RestObject.payload


'''
#Save request#
'''
def saveRequest(requestId:str, name:str, description:str, verbId:int, userRestApiRequest:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['name'] = name
	headers['description'] = description
	headers['verbId'] = str(verbId)
	headers['userRestApiRequest'] = userRestApiRequest
	api_url = api_url_stem + "/restapi/request:save"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns UserRestApiRequest in RestObject.payload

''' 
 ############################ RestApi Controller END #####################################
'''

''' 
 ############################ Scripting Controller BEGIN #####################################
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
def interpreterAdd(requestId:str, interpreterName:str, interpreterVersion:str, interpreterPath:str, command:str, fileExtensions:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterName'] = interpreterName
	headers['interpreterVersion'] = interpreterVersion
	headers['interpreterPath'] = interpreterPath
	headers['command'] = command
	headers['fileExtensions'] = fileExtensions
	api_url = api_url_stem + "/scripting/interpreter:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  # returns InterpreterList in RestObject.payload	


	
'''
Update Interpreter
'''
def interpreterUpdate(requestId:str, interpreterId:int, interpreterName:str, interpreterVersion:str, interpreterPath:str, command:str, fileExtensions:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['interpreterId'] = str(interpreterId)
	headers['interpreterName'] = interpreterName
	headers['interpreterVersion'] = interpreterVersion
	headers['interpreterPath'] = interpreterPath
	headers['command'] = command
	headers['fileExtensions'] = fileExtensions
	api_url = api_url_stem + "/scripting/interpreter:update"
	response =requests.post(api_url, headers=headers)
	return retObject(response)  # returns InterpreterList in RestObject.payload	

'''
Delete Interpreter
'''
def interpreterDelete(requestId:str, interpreterId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterId'] = interpreterId
	api_url = api_url_stem + "/scripting/interpreter:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  #
	
'''
Get A List Of Interpreter Versions providing a filter for the name
'''
def searchInterpreter(requestId:str, interpreterName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['interpreterName'] = interpreterName
	api_url = api_url_stem + "/scripting/interpreter:search"
	response =requests.get(api_url, headers=headers)
	return retObject(response)   # returns InterpreterList in RestObject.payload	

'''
Get List of Interpreters
'''
def listAllInterpreters(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/scripting/interpreter:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns InterpreterList in RestObject.payload	

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
def addScript(requestId:str, scriptName:str, paramString:str, mainFile:str, interpreterId:int, fileType:str, filePath:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)  # returns ScriptDetail in RestObject.payload	
	
'''
Add Script to Repository
'''
def addScriptByContent(requestId:str, scriptName:str, comment:str, paramString:str, interpreterId:int, scriptContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptName'] = scriptName
	headers['comment'] = comment
	headers['paramString'] = paramString
	headers['interpreterId'] = str(interpreterId)
	api_url = api_url_stem + "/scripting/script:content"
	response =requests.put(api_url, data=scriptContent, headers=headers)
	return retObject(response)  # returns ScriptDetail in RestObject.payload		



'''
Get Script and versions, scriptName can also be only part of name to wider range for searching, used by browser
'''
def getScriptContent(requestId:str, scriptId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptId'] = str(scriptId)
	api_url = api_url_stem + "/scripting/script:content"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # download script content, used by browser


class ScriptDetailObject:
	def __init__(self, listOfScripts_:List[ScriptDetail]):
		self.listOfScripts=listOfScripts_
		

'''
Get Script and versions, scriptName can also be only part of name to wider range for searching
'''
def scriptSearch(requestId:str, scriptName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptName'] = scriptName
	api_url = api_url_stem + "/scripting/script:search"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns ScriptDetailObject  in RestObject.payload	

'''
Get Scripts, specific to a user
'''
def userScripts(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/scripting/script:user"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns ScriptDetailObject  in RestObject.payload

'''
Get Specific Script Information
'''
def scriptSearch(requestId:str, scriptId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptId'] = str(scriptId)
	api_url = api_url_stem + "/scripting/script:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns ScriptDetail  in RestObject.payload	

'''
Remove Script and All its verions from Repository
'''
def scriptRemove(requestId:str, scriptId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptId'] = str(scriptId)
	api_url = api_url_stem + "/scripting/script:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  # returns GenericResponse in RestObject.payload	


'''
Remove Script Version Of A Script from Repository
'''
def scriptVersionRemove(requestId:str, scriptName:str, scriptVersion:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptName'] = scriptName
	headers['scriptVersion'] = scriptVersion
	api_url = api_url_stem + "/scripting/script/version:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  # returns GenericResponse in RestObject.payload	


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
def getScriptParam(requestId:str, scriptName:str, scriptVersion:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptName'] = scriptName
	headers['scriptVersion'] = scriptVersion
	api_url = api_url_stem + "/scripting/script/param:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns ScriptParamCompoundObject in RestObject.payload


'''
Add Script Param for a corresponding Script
'''
def scriptParamAdd(requestId:str, 
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
	response =requests.put(api_url, headers=headers)
	return retObject(response)  # returns ScriptParamDetail in RestObject.payload

'''
Delete Script Param
'''
def scriptParamDelete(requestId:str, scriptId:int, scriptParamId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptId'] = str(scriptId)
	headers['scriptParamId'] = str(scriptParamId)
	
	api_url = api_url_stem + "/scripting/script/param:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  #

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
def machineNodeBridgeToScriptAdd(requestId:str, nodeId:int, scriptId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['nodeId'] = str(nodeId)
	headers['scriptId'] = str(scriptId)
	api_url = api_url_stem + "/scripting/bridge:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  # returns MachineNodeToScriptBridgeList in RestObject.payload


'''
Delete association of a script to machine node
'''
def machineNodeBridgeToScriptDelete(requestId:str, id:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)
	api_url = api_url_stem + "/scripting/bridge:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  # returns GenericResponse in RestObject.payload



'''
Get associations to scripts for a node
'''
def nodeBridgeToScriptForNode(requestId:str, nodeId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['nodeId'] = str(nodeId)
	api_url = api_url_stem + "/scripting/bridge/node:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns MachineNodeToScriptBridgeList in RestObject.payload

'''
Get associations of script for nodes
'''
def nodeBridgeToScriptForScript(requestId:str, scriptId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptId'] = str(scriptId)
	api_url = api_url_stem + "/scripting/bridge/script:get"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  #


class ScriptingReturnObject:
	def __init__(self, machineNodeToScriptBridgeList_:List[MachineNodeToScriptBridge]):
		self.machineNodeToScriptBridgeList=machineNodeToScriptBridgeList_
		



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
def runRepoScript(requestId:str, scriptParameters:ScriptParamRepoList):
	headers = {}
	headers['Content-Type']="application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/scripting/script/repo:run"
	response =requests.post(api_url, data = ClassEncoder().encode(scriptParameters), headers=headers)
	return retObject(response)  # returns ScriptingReturnObject in RestObject.payload

	
'''
Run/Execute Script Version via client
'''
def runClientAdhocScript(requestId:str, scriptName:str,  interpreterId:int, machineList:str, scriptContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptName'] = scriptName
	headers['interpreterId'] = str(interpreterId)
	headers['machineList'] = machineList
	api_url = api_url_stem + "/scripting/script/adhoc:run"
	response =requests.post(api_url, data=scriptContent, headers=headers)
	return retObject(response)  # returns ScriptingReturnObject in RestObject.payload

'''
Run/Execute Script Version via another node, background execution with sending notifications
'''
def runAdhocScriptViaNode(requestId:str, internalUser:str, internalPassword:str, scriptName:str,  interpreterId:int, baseUrl:str, scriptContent:str):
	headers = {}
	headers['Authorization'] = authorization
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
	return retObject(response)  # returns printable message such as 'OK' in RestObject.payload

	

'''
Execute Repo Script Version via cluster gate node, background execution with sending notifications
'''
def runRepoScriptViaNodeMultipart(requestId:str, 
								  internalUser:str, 
								  internalPassword:str, 
								  mainFileName:str,
								  interpreterId:int, 
								  baseUrl:str, 
								  baseFolder:str,
								  attachmentPath:str):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
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
	return retObject(response)  # returns printable message such as 'OK' in RestObject.payload

'''
#Scripting push notification to web or thick client via web socket of the data header#
'''
def loopbackScriptDataHeader(requestId, tableDefinition: TableDefinition):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['internalUser'] = internalUserName
	headers['internalPassword'] = internalUserPassword
	api_url = api_url_stem + "/scripting/loopback/data:header"
	response =requests.post(api_url, data=ClassEncoder().encode(tableDefinition), headers=headers)
	return retObject(response)  # returns printable message such as 'OK' in RestObject.payload

'''
#Scripting push notification to web or thick client via web socket of the data footer#
'''
def loopbackScriptDataFooter(requestId:str, rowValue:RowValue):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['internalUser'] = internalUserName
	headers['internalPassword'] = internalUserPassword
	api_url = api_url_stem + "/scripting/loopback/data:footer"
	response =requests.post(api_url, data=ClassEncoder().encode(rowValue), headers=headers)
	return retObject(response)   # returns printable message such as 'OK' in RestObject.payload

'''
#Scripting push notification to web or thick client via web socket of the data row#
'''
def loopbackScriptDataDetail(requestId:str, rowValue:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['internalUser'] = internalUserName
	headers['internalPassword'] = internalUserPassword
	api_url = api_url_stem + "/scripting/loopback/data:detail"
	response =requests.post(api_url, data=rowValue, headers=headers)
	return retObject(response)  # returns printable message such as 'OK' in RestObject.payload


'''
#Scripting push notification to web or thick client via web socket of the data row#
'''
def loopbackScriptDataDetails(requestId:str, rowValue:List[RowValue]):
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['internalUser'] = internalUserName
	headers['internalPassword'] = internalUserPassword
	api_url = api_url_stem + "/scripting/loopback/data:details"
	response =requests.post(api_url, data=ClassEncoder().encode(rowValue), headers=headers)
	return retObject(response)  # returns printable message such as 'OK' in RestObject.payload


'''
#Loopback output from executing scripts in String or json format#
'''
def loopbackScriptStdin(requestId:str, 
						internalUser:str, 
						internalPassword:str, 
						baseUrl:str, 
						websocketMessageType:str,
						line:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['internalUser'] = internalUser
	headers['internalPassword'] = internalPassword
	headers['baseUrl'] = baseUrl
	headers['websocketMessageType'] = websocketMessageType
	api_url = api_url_stem + "/scripting/loopback/log:stdin"
	response =requests.post(api_url, data=line, headers=headers)
	return retObject(response)  # returns printable message such as 'OK' in RestObject.payload


'''
Accept streams of table rows
'''
def acceptStream(requestId:str, type:str, rowOrHeader):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['type'] = type # /*type = "H" - header, "M" - middle, "F" = finished/done*/
	api_url = api_url_stem + "/scripting/script/streaming/adhoc:accept"
	response =requests.post(api_url, body=rowOrHeader, headers=headers)
	return retObject(response)  #

'''
Run/Execute Script Version with Streaming
'''
def streamAdhocScript(requestId:str, scriptName:str, interpreterId:int, scriptContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['scriptName'] = scriptName
	headers['interpreterId'] = str(interpreterId)
	api_url = api_url_stem + "/scripting/script/streaming/adhoc:run"
	response =requests.post(api_url, data=scriptContent, headers=headers)
	return retObject(response) 



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
Get the List of executed scripts by user filtered by scriptName and source (adhoc/repo)
'''
def getScriptExecutionHistList(requestId:str, source:str, scriptName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['src'] = source
	headers['scriptName'] = scriptName
	api_url = api_url_stem + "/scripting/history/script/execution/filter:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns HistScriptList in RestObject.payload


'''
Get the List of ALL executed scripts by a user
'''
def getAllScriptExecutionHistList(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/scripting/history/script/execution/all:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns HistScriptList in RestObject.payload


'''
Copy historical adhoc scripts
'''
def copyAdhocScriptHist(requestId:str, toUserId:int, type:str, interpreterName:str, scriptName:str, shaHash:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toUserId'] = str(toUserId)
	headers['type'] = type
	headers['interpreterName'] = interpreterName
	headers['scriptName'] = scriptName
	headers['shaHash'] = shaHash
	api_url = api_url_stem + "/scripting/history/adhoc/script:copy"
	response =requests.post(api_url, headers=headers)
	return retObject(response) # returns GenericResponse with "OK" on success in RestObject.payload

'''
Copy Repo Scripts to another user
'''
def copyRepoScriptHist(requestId:str, toUserId:int, type:str, interpreterId:int, scriptId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toUserId'] = str(toUserId)
	headers['type'] = type
	headers['interpreterId'] = str(interpreterId)
	headers['scriptId'] = str(scriptId)

	api_url = api_url_stem + "/scripting/history/repo/script:copy"
	response =requests.post(api_url, headers=headers)
	return retObject(response) # returns GenericResponse with message to print or display on success in RestObject.payload

'''
Delete script from your profile"
'''
def deleteScriptHist(requestId:str, toUserId:int, type:str, interpreterId:int, scriptId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['toUserId'] = str(toUserId)
	headers['type'] = type
	headers['interpreterId'] = str(interpreterId)
	headers['scriptId'] = str(scriptId)

	api_url = api_url_stem + "/scripting/history/script:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response) # returns GenericResponse with true or false on success/failure in RestObject.payload

'''
Download script in the browser
'''
def downloadScript(requestId:str, scriptId:str):
	headers = {}
	headers['Authorization'] = authorization
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
 ############################ SQLRepo Controller BEGIN #####################################
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
		

			
class RdbmsExecutedQuery(RestInterface):
	def __init__(self, id_:int, 
						requestId_:str,
						databaseId_:int,
						statementId_:int,
					   statementName_:str, 
					   statementType_:str, 
					   statement_:str, 
					   jsonParam_:str, 
					   clusterId_:int, 
					   dbType_:str,
					   group_:str, 
					   source_:int, 
					   userId_:str, 
					   repPath_:str, 
					   comment_:str,
					   timestamp_:int,
					   flag_:int,
					   cntAccess_:int
					   ):
		self.id=id_
		self.requestId=requestId_
		self.databaseId=databaseId_
		self.statementId=statementId_
		self.statementName=statementName_
		self.statementType=statementType_
		self.statement=statement_
		self.jsonParam=jsonParam_
		self.clusterId=clusterId_
		self.dbType = dbType_
		self.group=group_
		self.source=source_
		self.userId=userId_
		self.repPath=repPath_
		self.comment=comment_
		self.timestamp=timestamp_
		self.flag=flag_
		self.cntAccess = cntAccess_

'''
#Get the List of available Databases#
'''
def getDatabase(requestId:str, databaseName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['databaseName'] = databaseName
	api_url = api_url_stem + "/sqlRepo/databases"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns RdbmsRepoDatabaseList in RestObject.payload

'''
#Add a new database connection to the list of available Databases/schema connections#
'''
def addDatabase(requestId:str,
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
	headers['Authorization'] = authorization
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
	
	api_url = api_url_stem + "/sqlRepo/database/add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  # returns GenericResponse with a printable message in RestObject.payload


'''
#Update database connection#
'''
def updateDatabase(	requestId:str,
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
	headers['Authorization'] = authorization
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
	
	api_url = api_url_stem + "/sqlRepo/database/update"
	response =requests.put(api_url, headers=headers)
	return retObject(response)    # returns GenericResponse with a printable message in RestObject.payload

'''
#Delete database/schema connection#
'''
def databaseDelete(requestId:str, databaseId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['databaseId'] = str(databaseId)
	api_url = api_url_stem + "/sqlRepo/database/delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  # returns GenericResponse with a printable message in RestObject.payload

'''
#Validate a new Database/schema connection#
'''
def validateDatabase(	requestId:str, 
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
	headers['Authorization'] = authorization
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
	

	api_url = api_url_stem + "/sqlRepo/database/connection/validate:new-connection"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns GenericResponse with a boolean message in RestObject.payload. For lack of connectivity the error message RestObject.erroMessage is populated

'''
#Validate an existing Database/schema connection#
'''
def validateSqlRepoDatabase(requestId:str, databaseName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['databaseName'] = databaseName
	api_url = api_url_stem + "/sqlRepo/database/connection/validate:connection"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #  returns GenericResponse with a boolean message in RestObject.payload. For lack of connectivity the error message RestObject.erroMessage is populated

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
#Reload Repo List#
'''
def reloadSqlRepo(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/sqlRepo/reload"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #  returns SqlRepoList in RestObject.payload


'''
#Add a new Sql statement to the repo#
'''
def addSql(requestId:str, sqlType:str, sqlReturnType:str, sqlCategory:str, sqlName:str, sqlDescription:str, sqlContent:str, execution:str, active:str ):
	headers = {}
	headers['Authorization'] = authorization
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

	api_url = api_url_stem + "/sqlRepo/sql:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  #  returns SqlRepoList in RestObject.payload


'''
#Add a new Sql statement to the repo#
'''
def deleteSql(requestId:str, 
			  sqlId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	
	
	api_url = api_url_stem + "/sqlRepo/sql:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  #  returns GenericResponse with message in RestObject.payload

'''
#Update Sql statement to the repo#
'''
def updateSql(requestId:str, 
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
	api_url = api_url_stem + "/sqlRepo/sql:update"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  #  returns GenericResponse with message in RestObject.payload

class SqlRepoParamListDetail:
	def __init__(self,listOfSqlRepoParam_:List[SqlRepoParam]):
		self.listOfSqlRepoParam=listOfSqlRepoParam_
		

'''
#Add Sql Param to Sql Statement#
'''
def addSqlParam(requestId:str, 
				sqlId:int, 
				sqlParamName:str, 
				sqlParamType:str, 
				sqlParamDefaultValue:str, 
				sqlParamPosition:str, 
				sqlParamOrder:str, 
				sqlParamOriginTbl:str, 
				sqlParamOriginCol):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['sqlParamName'] = sqlParamName
	headers['sqlParamType'] = sqlParamType
	headers['sqlParamDefaultValue'] = sqlParamDefaultValue
	headers['sqlParamPosition'] = sqlParamPosition
	headers['sqlParamOrder'] = str(sqlParamOrder)
	headers['sqlParamOriginTbl'] = sqlParamOriginTbl
	headers['sqlParamOriginCol'] = sqlParamOriginCol
	
	api_url = api_url_stem + "/sqlRepo/sqlparam:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  #  returns SqlRepoParamListDetail in RestObject.payload

'''
#Delete Sql Param#
'''
def deleteSqlParam(requestId:str, sqlId:int, sqlParamId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['sqlParamId'] = str(sqlParamId)
	
	api_url = api_url_stem + "/sqlRepo/sqlparam:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  

'''
#Get Sql Repo List#
'''
def getSqlRepoList(requestId:str, filter:str, databaseId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['filter'] = filter
	headers['databaseId'] = str(databaseId)
	
	api_url = api_url_stem + "/sqlrepo"
	response =requests.get(api_url, headers=headers)
	return retObject(response)   #  returns SqlRepoList in RestObject.payload


'''
#Get Sql Repo List Without Params#
'''
def getSqlRepoListWithNoParams(requestId:str, filter:str, databaseId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['filter'] = filter
	headers['databaseId'] = str(databaseId)
	
	api_url = api_url_stem + "/sqlRepo/sql"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #  returns SqlRepoList in RestObject.payload


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
#Get Sql Repo List Summary Format#
'''
def getSqlRepoListSummary(requestId:str, filter:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['filter'] = filter
	api_url = api_url_stem + "/sqlRepo/sql/summary"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #  returns SqlRepoListShortFormat in RestObject.payload


'''
#Get Sql Detail#
'''
def getSqlDetail(requestId:str, sqlId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	api_url = api_url_stem + "/sqlRepo/sql/detail"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #  returns SqlRepoList in RestObject.payload


class SqlParameter:
	def __init__(self,pid_:int, pname_:str, value_:str):
		self.pid=pid_
		self.pname=pname_
		self.value=value_

class ParamObj:
	def __init__(self,plist_:List[SqlParameter]):
		self.plist=plist_
		
'''
#Get Sql Param List#
'''
def getSqlParamList(requestId:str, sqlId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	api_url = api_url_stem + "/sqlRepo/params"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #  returns ParamObj in RestObject.payload


class ParamListObj:
	def __init__(self,plistlist_:List[ParamObj]):
		self.plistlist=plistlist_
		

'''
#Get Sql Param List for Bulk DML. DQLs and DDLs are excluded#
'''
def getSqlParamListBulk(requestId:str, sqlId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	api_url = api_url_stem + "/sqlRepo/param/bulk"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #  returns ParamListObj in RestObject.payload

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
#Assign sql statement to a certain database#
'''
def addSqlToDbMapping(requestId:str, sqlId:int, dbId:int, active:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['dbId'] = str(dbId)
	headers['active'] = str(active)
	api_url = api_url_stem + "/sqlRepo/sqlToDb/mapping:update"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  #  returns SqlStmToDbBridgeList in RestObject.payload

'''
#Delete association of sql statement to database#
'''
def deleteSqlToDbMapping(requestId:str, sqlId:int, dbId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['dbId'] = str(dbId)
	api_url = api_url_stem + "/sqlRepo/sqlToDb/mapping:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)   #  returns GenericResponse with text 'OK' on success in RestObject.payload

'''
#Get mapping of sql statements to databases#
'''
def listSqlToDbMapping(requestId:str, sqlId:int, dbId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['dbId'] = str(dbId)
	api_url = api_url_stem + "/sqlRepo/sqlToDb/mapping:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #  returns SqlStmToDbBridgeList in RestObject.payload

'''
#Execute Adhoc Sql#
'''
def executeAdhocSql(requestId:str, 
					schemaUniqueName:str,
					outputCompression:str,
					persist:str,
					forceNoPush:str,
					sqlType:str,
					comment:str,
					sqlName:str,
					sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
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
	
	api_url = api_url_stem + "/sqlRepo/execute/adhoc"
	response =requests.post(api_url, data=sqlContent, headers=headers)
	return retObject(response)  # returns ResultQuery in RestObject.payload
 
'''
#Get the List of all user tables in database schema#
'''
def generateCreateScriptForTable(requestId:str, fromRdbmsSchemaUniqueName:str, tableName:str, sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromRdbmsSchemaUniqueName'] = fromRdbmsSchemaUniqueName
	headers['tableName'] = tableName
	api_url = api_url_stem + "/sqlRepo/database/generate:script"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response)  # returns GenericResponse with script content in RestObject.payload

class TableList:
	def __init__(self,lst_:List[str]):
		self.lst=lst_

'''
#Get the List of all user tables in database schema#
'''
def getDatabaseTables(requestId:str, connectionUniqueName:str, schema:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['connectionUniqueName'] = connectionUniqueName
	headers['schema'] = schema
	
	api_url = api_url_stem + "/sqlRepo/database/tables"
	response =requests.get(api_url, headers=headers)
	return retObject(response) # returns TableList in RestObject.payload


'''
#Get the List of database schemas#
'''
def getDatabaseSchemas(requestId:str, connectionUniqueName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['connectionUniqueName'] = connectionUniqueName
	api_url = api_url_stem + "/sqlRepo/database/schemas"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns TableList in RestObject.payload


'''
#Execute Sql Repo#
'''
def executeSqlRepo(requestId:str, 
				   sqlId:int, 
				   schemaUniqueName:str,
				   outputCompression:str,
				   outputType:str,
				   batchCount:int,
				   persist:str,
				   comment:str,
				   jsonObjSqlParam:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['schemaUniqueName'] = schemaUniqueName
	headers['outputCompression'] = outputCompression
	headers['outputType'] = outputType
	headers['batchCount'] = str(batchCount)
	headers['persist'] = persist
	headers['comment'] = comment
	
	api_url = api_url_stem + "/sqlRepo/execute:singleDb"
	response =requests.post(api_url, data=jsonObjSqlParam, headers=headers)
	return retObject(response)  # returns ResultQuery in RestObject.payload

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
#Execute Sql On multiple DBs and aggregate results#
'''
def executeSqlRepoMultiple(requestId:str, 
						   sqlId:int, 
						   outputCompression:str,
						   outputType:str, 
						   batchCount:int,
						   comment:str,
						   dbIdList:str,
						   persist:str,
						   jsonObjSqlParam:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	headers['outputCompression'] = outputCompression
	headers['outputType'] = outputType
	headers['batchCount'] = batchCount
	headers['comment'] = comment
	headers['dbIdList'] = dbIdList  # comma separated id array
	headers['persist'] = persist
	api_url = api_url_stem + "/sqlRepo/execute:multipleDb"
	response =requests.post(api_url, data=jsonObjSqlParam, headers=headers)
	return retObject(response)  # returns ResultQuery in RestObject.payload

'''
#Execute Sql On multiple DBs and aggregate results#
'''
def executeSqlAdhocMultiple(requestId:str, strObj:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/sqlRepo/execute/adhoc/multipledb:aggregate"
	response =requests.put(api_url, data=strObj, headers=headers)
	return retObject(response)  # returns ResultQuery in RestObject.payload


'''
#Create and Insert table from Sql Repo execution#
'''
def executeSqlRepoToMigrateData(requestId:str, 
								sqlId:int, 
								sourceConnectionName:str, 
								inputCompression:str, 
								destinationConnectionName:str, 
								destinationSchema:str, 
								destinationTable:str,
								jsonObjSqlParam):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	
	headers['sqlId'] = str(sqlId)
	headers['sourceConnectionName'] = sourceConnectionName
	headers['inputCompression'] = inputCompression
	headers['destinationConnectionName'] = destinationConnectionName
	headers['destinationSchema'] = destinationSchema
	headers['destinationTable'] = destinationTable
	
	api_url = api_url_stem + "/sqlRepo/migrate"
	response =requests.post(api_url, data=jsonObjSqlParam, headers=headers)
	return retObject(response)  # returns GenericResponse with printable message in RestObject.payload





class RecordsAffected:
	def __init__(self,operation_:str, recAffected_:int, recFailed_:int, message_:str):
		self.operation=operation_
		self.recAffected=recAffected_
		self.recFailed=recFailed_
		self.message=message_
		

'''
#Copy records from Embedded Sql to RDBMS table#
'''
def copyEmbeddedSqlResultToRdbmsTable(requestId:str, 
									  fromEmbeddedType:str, 
									  fromClusterId:int,
									  fromEmbeddedDatabaseName:str,
									  fromEmbeddedSchemaName:str,
									  toRdbmsConnectionName:str,
									  toRdbmsSchemaName:str,
									  toRdbmsTableName:str,
									  sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
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
	api_url = api_url_stem + "/sqlRepo/copy/embedded/adhoc:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response)   # returns RecordsAffected in RestObject.payload

'''
#Copy records from Mongodb simple search to RDBMS table#
'''
def copyMongoSimpleSearchResultToRdbmsTable(requestId:str,
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
	headers['Authorization'] = authorization
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
	api_url = api_url_stem + "/sqlRepo/copy/mongodb/search:simple"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  # returns RecordsAffected in RestObject.payload



'''
#Copy records to RDBMS table from another Mongodb collection(s) range search#
'''
def copyMongoRangeSearchResultToRdbmsTable(requestId:str,
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
	headers['Authorization'] = authorization
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
	api_url = api_url_stem + "/sqlRepo/copy/mongodb/search:range"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  # returns RecordsAffected in RestObject.payload



'''
#Copy records to RDBMS table from full Mongodb collection#
'''
def copyMongoFullCollectionToRdbmsTable(requestId:str, 
										fromMongoClusterName:str, 
										fromMongoDatabaseName:str, 
										fromMongoCollectionName:str,
										toRdbmsConnectionName:str, 
										toRdbmsSchemaName:str, 
										toRdbmsTableName:str ):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromMongoClusterName'] = fromMongoClusterName
	headers['fromMongoDatabaseName'] = fromMongoDatabaseName
	headers['fromMongoCollectionName'] = fromMongoCollectionName
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlRepo/copy/mongodb:collection"
	response =requests.put(api_url, headers=headers)
	return retObject(response)   # returns RecordsAffected in RestObject.payload



'''
#Copy records to RDBMS table from Mongodb ad-hoc search#
'''
def copyMongoAdhocResultToRdbmsTable(requestId:str, 
									 fromClusterUniqueName:str, 
									 fromMongoDbName:str, 
									 fromCollectionName:str, 
									 toRdbmsConnectionName:str, 
									 toRdbmsSchemaName:str, 
									 toRdbmsTableName:str,
									 bsonQuery:str ):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	headers['fromClusterUniqueName'] = fromClusterUniqueName
	headers['fromMongoDbName'] = fromMongoDbName
	headers['fromCollectionName'] = fromCollectionName
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlRepo/copy/mongodb:adhoc"
	response =requests.put(api_url, data=bsonQuery, headers=headers)
	return retObject(response)  # returns RecordsAffected in RestObject.payload


'''
#Copy records to Rdbms table from Elastic DSL query#
'''
def copyElasticDslResultToRdbmsTable(requestId:str, 
									 fromElasticClusterName:str,
									 fromElasticHttpVerb:str, 
									 fromElasticEndPoint:str, 
									 toRdbmsConnectionName:str, 
									 toRdbmsSchemaName:str,
									 toRdbmsTableName:str,
									 sqlContent:str
									 ):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['fromElasticHttpVerb'] = fromElasticHttpVerb
	headers['fromElasticEndPoint'] = fromElasticEndPoint
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlRepo/copy/elastic:dsl"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response)  # returns RecordsAffected in RestObject.payload

'''
#Create/add records to collection from Elastic SQL query#
'''
def copyElasticSqlResultToRdbmsTable(requestId:str, 
									 fromElasticClusterName:str, 
									 fromElasticFetchSize:str, 
									 toRdbmsConnectionName:str, 
									 toRdbmsSchemaName:str, 
									 toRdbmsTableName:str, 
									 sqlContent:str):
									
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromElasticClusterName'] = fromElasticClusterName
	headers['fromElasticFetchSize'] = fromElasticFetchSize
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlRepo/copy/elastic:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response)  # returns RecordsAffected in RestObject.payload

'''
#Copy Rdbms Sql result records to another Rdbms System Table#
'''
def copyRdbmsSqlResultToRdbmsTable(requestId:str, 
								   fromRdbmsSchemaUniqueName:str, 
								   toRdbmsConnectionName:str, 
								   toRdbmsSchemaName:str, 
								   toRdbmsTableName:str,
								   sqlContent:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fromRdbmsSchemaUniqueName'] = fromRdbmsSchemaUniqueName
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlRepo/copy/sqlrepo:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response)  # returns RecordsAffected in RestObject.payload


'''
Copy Csv to table
'''
def copyCsvToRdbmsTable(requestId:str, 
						tableScript:str,
						toRdbmsConnectionName:str,
						toRdbmsSchemaName:str,
						toRdbmsTableName:str,
						filePath:str):
	
	fileName, fileContent, fileType = getFileContent(filePath)
	headers = {}
	headers['Content-Type'] = "application/json; charset=utf-8"
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['fileType'] = fileType
	headers['tableScript'] = tableScript
	headers['toRdbmsConnectionName'] = toRdbmsConnectionName
	headers['toRdbmsSchemaName'] = toRdbmsSchemaName
	headers['toRdbmsTableName'] = toRdbmsTableName
	api_url = api_url_stem + "/sqlRepo/copy/sqlRepo/csv:load"
	form_data = {'file': fileContent}
	response =requests.put(api_url, data=form_data, headers=headers)
	return retObject(response)  # returns RecordsAffected in RestObject.payload

'''
#Validate Sql#
'''
def validateSql(requestId:str, sqlContent):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/sqlRepo/validate:sql"
	response =requests.put(api_url, data=sqlContent, headers=headers)
	return retObject(response)  # returns GenericResponse with DQL,DML,DDL or NONE in RestObject.payload


class HistSqlList:
	def __init__(self,tempSqlList_:str):
		self.tempSqlList=tempSqlList_







'''
#Get Sql Param List Detail#
'''
def getSqlParamListDetail(requestId:str, sqlId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['sqlId'] = str(sqlId)
	api_url = api_url_stem + "/sqlRepo/param/detail"
	response =requests.get(api_url, headers=headers)
	return retObject(response)   # returns SqlRepoParamListDetail in RestObject.payload






''' 
 ############################ SQLRepo Controller END #####################################
'''


''' 
 ############################ User Controller BEGIN #####################################
'''





'''
#Authenticate user and generate session on success#
'''
def login(user:str, password:str, requestId:str, pns:str, deviceToken:str, authBody:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['password'] = password
	headers['requestId'] = requestId
	headers['pns'] = pns
	headers['deviceToken'] = deviceToken
	api_url = api_url_stem + "/users/user:login"
	response =requests.post(api_url, data=authBody, headers=headers)
	return retObject(response)  # returns User in RestObject.payload


'''
#Authenticate mobile user and generate session on success#
'''
def loginMobil(user:str, password:str, requestId:str, mobileKey:str, pns:str, deviceToken:str, authBody:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['password'] = password
	headers['requestId'] = requestId
	headers['mobileKey'] = mobileKey
	headers['pns'] = pns
	headers['deviceToken'] = deviceToken
	api_url = api_url_stem + "/users/mobile/user:login"
	response =requests.post(api_url, data=authBody, headers=headers)
	return retObject(response)  # returns User in RestObject.payload


'''
#Logout#
'''
def logout(requestId:str, deviceToken:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['deviceToken'] = deviceToken
	api_url = api_url_stem + "/users/user:logout"
	response =requests.post(api_url, headers=headers)
	return retObject(response)  #

class UserStatus:
	def __init__(self, userName_:int, isSession_:bool, isSocket_:bool):
		self.userName=userName_
		self.isSession=isSession_
		self.isSocket=isSocket_

'''
#Check user connectivity#
'''
def checkUser(requestId:str, checkedUser:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['checkedUser'] = checkedUser
	api_url = api_url_stem + "/users/user:check"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns UserStatus in RestObject.payload

'''
#Check user connectivity#
'''
def cleanupIdleSessions(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/users:cleanup"
	response =requests.post(api_url, headers=headers)
	return response  # returns simply 'OK' in the HTTP body


class Department:
	def __init__(self, id_:int, department_:str, description_:str):
		self.id=id_
		self.department=department_
		self.description=description_
		

'''
#Add a new department#
'''
def addDepartment(requestId:str, newDepartment:str, newDepartmentDescription:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['newDepartment'] = newDepartment
	headers['newDepartmentDescription'] = newDepartmentDescription
	api_url = api_url_stem + "/users/department:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  # returns Department in RestObject.payload



'''
#Update department#
'''
def updateDepartment(requestId:str, id:int, newDepartment:str, newDepartmentDescription:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)
	headers['newDepartment'] = newDepartment
	headers['newDepartmentDescription'] = newDepartmentDescription
	
	api_url = api_url_stem + "/users/department:update"
	response =requests.post(api_url, headers=headers)
	return retObject(response)   # returns Department in RestObject.payload


class Title:
	def __init__(self, id_:int, title_:str, description_:str):
		self.id=id_
		self.title=title_
		self.description=description_
		

'''
Add a new title
'''
def addTitle(requestId:str, newTitle:str, newTitleDescription:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['newTitle'] = newTitle
	headers['newTitleDescription'] = newTitleDescription
	
	api_url = api_url_stem + "/users/title:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  # returns Title in RestObject.payload


'''
#Update title#
'''
def updateTitle(requestId:str, titleId:int, newTitle:str, newTitleDescription:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(titleId)
	headers['newTitle'] = newTitle
	headers['newTitleDescription'] = newTitleDescription

	api_url = api_url_stem + "/users/title:update"
	response =requests.post(api_url, headers=headers)
	return retObject(response)  #

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
#Add a new user#
'''
def addUser(requestId:str,
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
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers)
	return retObject(response)   # returns User in RestObject.payload



'''
#Register user#
'''
def registerUser(requestId:str, newUser:str, newUserPassword:str, newUserType:str, newUserFirstName:str, newUserLastName:str, newUserEmail:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['newUser'] = newUser
	headers['newUserPassword'] = newUserPassword
	headers['newUserType'] = newUserType
	headers['newUserFirstName'] = newUserFirstName
	headers['newUserLastName'] = newUserLastName
	headers['newUserEmail'] = newUserEmail

	api_url = api_url_stem + "/users/user:register"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  # 


'''
#Approve a registering used#
'''
def approveRegisteringUser(requestId:str, newUser:str, departmentId:int, titleId:int, managerId:int, characteristic:str, descrption:str):
	headers = {}
	headers['Authorization'] = authorization
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
	response =requests.put(api_url, headers=headers)
	return retObject(response)  #

'''
#Reject a registering used#
'''
def rejectRegisteringUser(requestId:str, newUser:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['newUser'] = newUser

	api_url = api_url_stem + "/users/user:reject"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  #

'''
#Update user#
'''
def updateUser(requestId:str,
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
	headers['Authorization'] = authorization
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
	response =requests.post(api_url, headers=headers)
	return retObject(response)  #

'''
#Update user#
'''
def quickUserUpdate(requestId:str,
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
	headers['Authorization'] = authorization
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
	response =requests.post(api_url, headers=headers)
	return retObject(response)  #


'''
#Update my first and last name#
'''
def updateMyNames(requestId:str, userId:int, newUserFirstName:str, newUserLastName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(userId)
	headers['newUserFirstName'] = newUserFirstName
	headers['newUserLastName'] = newUserLastName
	api_url = api_url_stem + "/users/user:update-my-names"
	response =requests.post(api_url, headers=headers)
	return retObject(response)  #

'''
#Update my password#
'''
def updateMyPassword(requestId:str, userId:int, password:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(userId)
	headers['password'] = password
	api_url = api_url_stem + "/users/user:update-my-password"
	response =requests.post(api_url, headers=headers)
	return retObject(response)  #



'''
#Update my first name and last name#
'''
def updateMyEmailAndUserName(requestId:str, userId:int, userName, email):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(userId)
	headers['userName'] = userName
	headers['email'] = email
	api_url = api_url_stem + "/users/user:update-my-email"
	response =requests.post(api_url, headers=headers)
	return retObject(response)  #

'''
#Delete User#
'''
def deleteUser(requestId:str, userId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(userId)
	api_url = api_url_stem + "/users/user:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  #


'''
#Delete Department#
'''
def deleteDepartment(requestId:str, departmentId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(departmentId)
	api_url = api_url_stem + "/users/department:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  #

'''
#Delete Title#
'''
def deleteTitle(requestId:str, titleId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(titleId)
	api_url = api_url_stem + "/users/title:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  #


class UserList:
	def __init__(self, listOfUsers_:List[User]):
		self.listOfUsers=listOfUsers_

'''
#Get Users#
'''
def getUsers(requestId:str, paternToSearch:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['paternToSearch'] = paternToSearch
	api_url = api_url_stem + "/users:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # return UserList in RestObject.payload

'''
#Get Users minus current#
'''
def getUsersMinusCurrent(requestId:str, patternToSearch:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['patternToSearch'] = patternToSearch
	api_url = api_url_stem + "/users/minus:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # return UserList in RestObject.payload

'''
#Get registering users#
'''
def getRegisteringUsers(requestId:str, patternToSearch:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['patternToSearch'] = patternToSearch
	api_url = api_url_stem + "/users:registering"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # return UserList in RestObject.payload



class ManagerShort:
	def __init__(self, listOfUsers_:List[User]):
		self.listOfUsers=listOfUsers_
		

class ManagerShortList:
	def __init__(self, listOfUsers_:List[ManagerShort]):
		self.listOfUsers=listOfUsers_
		

'''
#Get Managers#
'''
def getManagers(requestId:str, patternToSearch:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['paternToSearch'] = patternToSearch
	api_url = api_url_stem + "/managers:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # return ManagerShortList in RestObject.payload



'''
#Get specific User based on id#
'''
def getUser(requestId:str, id:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)

	api_url = api_url_stem + "/users/user:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # return UserList in RestObject.payload

class Department:
	def __init__(self, id_:int, department_:str, description_:str):
		self.id=id_
		self.department=department_
		self.description=description_

class DepartmentList:
	def __init__(self, listOfDepartments_:List[Department]):
		self.listOfDepartments=listOfDepartments_

'''
#Get Departments#
'''
def getDepartments(requestId:str, patternToSearch:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['patternToSearch'] = patternToSearch
	api_url = api_url_stem + "/users/departments:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # return DepartmentList in RestObject.payload

'''
#Get a specific Department, from an id#
'''
def getDepartment(requestId:str, id:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = id
	api_url = api_url_stem + "/users/department:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)   # return DepartmentList in RestObject.payload


'''
#Get a specific department by name#
'''
def getDepartmentByName(requestId:str, department:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['department'] = department
	api_url = api_url_stem + "/users/department:search"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #


class Title:
	def __init__(self, id_:int, title_:str, description_:str):
		self.id=id_
		self.title=title_
		self.description=description_
		

class TitleList:
	def __init__(self, listOfTitles_:List[Title]):
		self.listOfTitles=listOfTitles_
		

'''
#Get Titles#
'''
def getTitles(requestId:str, patternToSearch:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['patternToSearch'] = patternToSearch
	api_url = api_url_stem + "/users/titles:query"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # return TitleList in RestObject.payload

'''
#Get a specific title, based on an id#
'''
def getTitleById(requestId:str, id:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['id'] = str(id)
	api_url = api_url_stem + "/users/title:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # return TitleList in RestObject.payload

'''
#Get a specific title, based on title#
'''
def getTitleByName(requestId:str, title:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['title'] = title
	api_url = api_url_stem + "/users/title:search"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # return TitleList in RestObject.payload

'''
#Generates a synthetic session for debug only in DEV or QA environment.#
'''
def generateSyntheticDevSession(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['admin'] = internalAdmin
	headers['password'] = internalAdminPassword
	headers['requestId'] = requestId
	api_url = api_url_stem + "/users/generateSyntheticSession"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #

'''
#50 milliseconds timer subscription#
'''
def subscribeToTimer(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/users/timer:subscribe"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns GenericResponse with 'OK' on success in RestObject.payload


'''
#Unsubscribe from 50 milliseconds timer#
'''
def unsubscribeToTimer(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	api_url = api_url_stem + "/users/timer:unsubscribe"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  # returns GenericResponse with 'OK' on success in RestObject.payload

''' 
 ############################ User Controller END #####################################
'''

'''
 ############################## Chat API BEGIN ########################################
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
#Send a message to a particular user#
'''
def sendTextMessageToUser(requestId:str, userId:int, toUser:str, toUserId:str, message:str, isEncrypt:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['userId'] = str(userId)
	headers['toUser'] = toUser
	headers['toUserId'] = toUserId
	headers['message'] = message
	headers['isEncrypt'] = isEncrypt

	api_url = api_url_stem + "/chat/fromUser/toUser/text:send"
	response =requests.put(api_url, headers=headers)

	return retObject(response)  # returns RestObject, which contains payload ChatConfirmation


class FileAttachments:
	def __init__(self, filePath_:str, text_:str):
		self.filePath = filePath_
		self.text = text_
		
'''
Send a message to a particular user with attachments
'''
def sendMultipartMessageToUser(	requestId:str, 
								userId:int, 
								toUser:str, 
								toUserId:str, 
								message:str, 
								isEncrypt:str, 
								fileAttachments:List[str]):
	headers = {}
	headers['Authorization'] = authorization
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

	return retObject(response)  # returns RestObject, which contains payload ChatConfirmation

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
	def __init__(self, chatMessage_:List[str]):
		self.chatMessage = chatMessage_ # list of ChatMessage




'''
#Get a list of messages from a specific user if any#
'''
def getMessageToUser(requestId:str, toUser:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['toUser'] = toUser
 
	api_url = api_url_stem + "/chat/fromUser/toUser/message:get"
	response = requests.post(api_url, headers=headers)

	return retObject(response)  # returns RestObject, which contains payload ChatMessageList

'''
#Get a list of messages from a specific user if any#
'''
def getNewMessagesFromUser(requestId:str, toUser:str, fromDate:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['toUser'] = toUser
	headers['fromDate'] = fromDate
 
	api_url = api_url_stem + "/chat/fromUser/toUser/messages/new:get"
	response = requests.post(api_url, headers=headers)

	return retObject(response)  # returns RestObject, which contains payload ChatMessageList
	
'''
#Get server time in milliseconds since EPOCH#
'''
def getServerTime(requestId: str):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	api_url = api_url_stem + "/chat/server/time:get"
	response = requests.get(api_url, headers=headers)

	return retObject(response)  #returns RestObject, which contains payload GenericResponse with payload the timestamp as string
	
class UserPairValue:
	def __init__(self, id_:int, user_:str):
		self.id = id_ 
		self.user = user_
		
		
class UserPairValueList:
	def __init__(self, userPairValue_:List[str]):
		self.userPairValue = userPairValue_ # list of UserPairValue
		
		
'''
#Retrieve the list of users sending messages to user#
'''
def getUsersWithOutstandingMessages(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	
	api_url = api_url_stem + "/chat/toUser/messages/new:get"
	response = requests.post(api_url, headers=headers)

	return retObject(response)  # returns RestObject, which contains payload UserPairValueList



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
#Set transfer status for a message(received or read)#
'''
def setTransferFlag(requestId:str, chatRecord: ChatRecord):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	
	api_url = api_url_stem + "/chat/fromUser/toUser/message:set"
	response = requests.post(api_url, data=chatRecord, headers=headers)

	return retObject(response)  # returns RestObject, which contains payload ChatRecord



'''
#Delete message permanently#
'''
def deleteMessage(requestId:str, chatRecord: ChatRecord):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	
	api_url = api_url_stem + "/chat/fromUser/toUser/message:delete"
	response = requests.post(api_url, data=chatRecord, headers=headers)

	return retObject(response)  # returns RestObject, which contains payload ChatRecord
	
	
'''
#Get unread message list for a particular user#
'''
def getUnreadMessageList(requestId:str, toUser:str, fromDate:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['toUser'] = toUser
	headers['fromDate'] = fromDate
	
	api_url = api_url_stem + "/chat/fromUser/toUser:get"
	response = requests.post(api_url, headers=headers)

	return retObject(response)  # returns RestObject, which contains payload ChatMessage

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

'''
#Get a multipart message after receiving notification#
'''
def getHistMessageList(requestId:str, toUser:str, fromDate:str,  toDate:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['toUser'] = toUser
	headers['fromDate'] = fromDate
	headers['toDate'] = toDate
	
	api_url = api_url_stem + "/chat/fromUser/toUser/history:get"
	response = requests.get(api_url, headers=headers)

	return retObject(response)  # returns RestObject, which contains payload ChatMessage




'''
#Get the count of unread messages#
'''
def getCountUnreadMessages(requestId:str, toUser:str, fromDate:str,  toDate:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	
	headers['toUser'] = toUser
	headers['fromDate'] = fromDate
	headers['toDate'] = toDate

		
	api_url = api_url_stem + "/chat/fromUser/toUser/count:get"
	response = requests.get(api_url, headers=headers)

	return retObject(response)  # returns RestObject, which contains payload with number as string


	




'''
#Get all available users that match a pattern#
'''
def searchChatUsers(requestId:str, patternToSearch:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['patternToSearch'] = patternToSearch
	
	api_url = api_url_stem + "/chat/users:query"
	response = requests.get(api_url, headers=headers)

	return retObject(response)  # returns RestObject, which contains payload UserToChatList

'''
#Get a specific Chat User#
'''
def getChatUser(requestId:str, userName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userName'] = userName

	api_url = api_url_stem + "/chat/user:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)  #['payload']['listOfUsers']

'''
#Add new chat user#
'''
def addChatUser(requestId:str, toUser:str, toUserId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['toUser'] = toUser
	headers['toUserId'] = toUserId
	headers['requestId'] = requestId

	api_url = api_url_stem + "/chat/user:add"
	response =requests.post(api_url, headers=headers)
	return retObject(response)  #['payload']['listOfUsers']

'''
#Get Users in the chat#
'''
def getChatUsers(requestId:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId

	api_url = api_url_stem + "/chat/users:chat"
	response =requests.post(api_url, headers=headers)
	return retObject(response)  #['payload']['listOfUsers']  
	
'''
Send a text message to a particular group
'''
def sendTextMessageToGroup(requestId:str,  userId:int, toGroup:str, toGroupId:str, message:str, isEncrypt:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['requestId'] = requestId
	headers['user'] = user
	headers['session'] = session
	headers['userId'] = userId
	headers['toGroup'] = toGroup
	headers['toGroupId'] = toGroupId
	headers['message'] = message
	headers['isEncrypt'] = isEncrypt

	api_url = api_url_stem + "/chat/fromUser/toGroup:send"
	response =requests.put(api_url, headers=headers)

	return retObject(response)  # returns RestObject, which contains payload ChatConfirmation


		
'''
Send a message to a particular user with attachments
'''
def sendMultipartMessageToGroup( requestId:str, 
								 userId:int, 
								 toGroup:str, 
								 toGroupId:str, 
								 message:str, 
								 isEncrypt:str, 
								 fileAttachments:List[str]):
	headers = {}
	headers['Authorization'] = authorization
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

	return retObject(response)  # returns RestObject, which contains payload ChatConfirmation
	

class UserToChatList:
	def __init__(self, userToChat_:List[str]):
		self.userToChat = userToChat_
			


	
'''
 ############################## Chat API END ########################################
'''






'''
 ############################## Execution History Start ########################################
'''
class RepoStaticDesc:
	generic = ""
	sqlRepo = "sqlRepo"
	mongoRepo = "mongoRepo"
	elasticRepo = "elasticRepo"
	scriptRepo = "scriptRepo"
	fileRepo = "fileRepo"
	exchangeRepo = "exchangeRepo"


	
		

'''
#Create Persistence Group for a certain repository#
'''
def createArtifactGroup(requestId:str, groupName:str, repoName:str, comment:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['groupName'] = groupName
	headers['repoName'] = repoName
	headers['comment'] = comment
	api_url = api_url_stem + "/history/execution/group:add"
	response =requests.put(api_url, {}, headers=headers)
	return retObject(response)  # returns DB record corresponding to Mongo/Elastic/Rdbms/Script repo in RestObject.payload


'''
#Get Persistence Group for a certain repository#
'''
def getArtifactGroup(requestId:str, groupName:str, repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['groupName'] = groupName
	headers['repoName'] = repoName

	api_url = api_url_stem + "/history/execution/group:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
#Delete Persistence Group for a certain repository#
'''
def deleteArtifactGroup(requestId:str, groupId:int, repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['groupId'] = str(groupId)
	headers['repoName'] = repoName
	api_url = api_url_stem + "/history/execution/group:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)  # return GenericResponse("OK") on success in RestObject.payload



'''
#Delete access to object to all users on a certain repository#
'''
def deleteAccessByArtifactId(requestId:str, objectId:int, repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['repoName'] = repoName
	headers['objectId'] = str(objectId)
	api_url = api_url_stem + "/history/execution/access/object:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)	
	


'''
#Test save execution data#
'''
def testSaveExecution(requestId:str, repoName:str, record:RestInterface, persist:str, o:object):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['record'] = json.dumps(record.__dict__)
	headers['repoName'] = repoName
	headers['persist'] = persist
	api_url = api_url_stem + "/history/execution/test:set"
	response =requests.put(api_url, data=ClassEncoder().encode(0), headers=headers)
	return retObject(response)  # returns DB record corresponding to Mongo/Elastic/Rdbms/Script repo in RestObject.payload

'''
#Add Object Access to a certain repository#
'''
def addArtifactAccess(requestId:str, repoName:str, objectId:int, userId:int):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['repoName'] = repoName
	headers['objectId'] = str(objectId)
	headers['userId'] = str(userId)
	headers['privilegeType'] = str(privilegeType)
	
	api_url = api_url_stem + "/history/execution/access:add"
	response =requests.put(api_url, headers=headers)
	return retObject(response)  # returns record in RestObject.payload


'''
#Get a list of Object Access by user for a certain repository#
'''
def getArtifactAccessByUserId(requestId:str, userId:int, repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['userId'] = str(userId)
	headers['repoName'] = repoName

	api_url = api_url_stem + "/history/execution/access/user:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
#Get Object Access by user on a certain repository#
'''
def getArtifactAccess(requestId:str, objectId:int, userId:int, repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['objectId'] = str(objectId)
	headers['userId'] = str(userId)
	headers['repoName'] = repoName

	api_url = api_url_stem + "/history/execution/access/object/user:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
#Get Object Access by user on a certain repository#
'''
def getArtifactAccessById(requestId:str, objectId:int, repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['objectId'] = str(objectId)
	headers['repoName'] = repoName

	api_url = api_url_stem + "/history/execution/access:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)




'''
#Delete access to object to a user on a certain repository#
'''
def deleteArtifactAccess(requestId:str, objectId:int, userId:int, repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['repoName'] = repoName
	headers['objectId'] = str(objectId)
	headers['userId'] = str(userId)
	api_url = api_url_stem + "/history/execution/access/object/user:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)	


'''
#Get Object Access by user on a certain repository#
'''
def countArtifactAccess(requestId:str, objectId:int, repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['objectId'] = str(objectId)
	headers['repoName'] = repoName

	api_url = api_url_stem + "/history/execution/access/object:count"
	response =requests.get(api_url, headers=headers)
	return retObject(response)



'''
#Check if artifact execution name exists already#
'''
def isExecutedName(requestId:str, repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['repoName'] = repoName

	api_url = api_url_stem + "/history/execution:name"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

'''
#Get the list of executed artifacts#
'''
def getUserExecutedArtifactList(requestId:str, repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['repoName'] = repoName

	api_url = api_url_stem + "/history/execution/filter:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

'''
#Provide executed artifact access to another user#
'''
def giveExecutedArtifactAccessToUser(requestId:str, repoName:str, artifactId:int, toUser:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['repoName'] = repoName
	headers['artifactId'] = str(artifactId)
	headers['toUser'] = str(toUser)
	
	api_url = api_url_stem + "/history/execution/access:set"
	response =requests.put(api_url, {}, headers=headers)
	return retObject(response)  # returns record in RestObject.payload

'''
#Delete access to executed script#
'''
def deleteExecutedArtifactAccess(requestId:str, artifactId:int,  repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['repoName'] = repoName
	headers['artifactId'] = str(artifactId)
	api_url = api_url_stem + "/history/execution/access:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)

'''
#Get the list of executed artifacts#
'''
def getAllUserArtifactExecutionList(requestId:str, repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['repoName'] = repoName

	api_url = api_url_stem + "/history/execution/all:list"
	response =requests.get(api_url, headers=headers)
	return retObject(response)


'''
#Delete some executed scripts#
'''
def deleteExecutedArtifacts(requestId:str, ids:List[int],  repoName:str, force:bool):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['repoName'] = repoName
	headers['ids'] = str(ids)
	headers['force'] = str(force)
	api_url = api_url_stem + "/history/execution/some:delete"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)


'''
#Get the list of executed artifacts#
'''
def getExecutionOutput(requestId:str, artifactId:int, repoName:str):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['artifactId'] = str(artifactId)
	headers['repoName'] = repoName

	api_url = api_url_stem + "/history/execution/output:get"
	response =requests.get(api_url, headers=headers)
	return retObject(response)

'''
#Delete artifact from user profile#
'''
def deleteExecutedOutput(requestId:str, ids:List[int],  repoName:str, force:bool):
	headers = {}
	headers['Authorization'] = authorization
	headers['user'] = user
	headers['session'] = session
	headers['requestId'] = requestId
	headers['repoName'] = repoName
	headers['ids'] = str(ids)
	headers['force'] = str(force)
	api_url = api_url_stem + "/history/execution/output:remove"
	response =requests.delete(api_url, headers=headers)
	return retObject(response)


'''
 ############################## Execution History End ########################################
'''



'''
To be considered



/scripting/node/sink:data  
/scripting/repo/script/param:get 
/scripting/adhoc/script:run 
/scripting/repo/script:get  
/scripting/repo/script:add 
/scripting/repo/script/param:add   
/scripting/repo/script/param:delete 
/scripting/repo/script:user 
/scripting/repo/script:run 
/scripting/repo/interpreter:search  
/scripting/adhoc/node/script:run 
/scripting/repo/interpreter:update  
/scripting/repo/script/version:remove  
/scripting/repo/script/content:add 
/scripting/repo/script:copy  
/scripting/repo/script:remove 
/scripting/repo/bridge/node:list
/scripting/repo/bridge/script:list 
/scripting/repo/interpreter:list
/scripting/repo/script:content 
/scripting/repo/script:search
/scripting/repo/bridge:delete
/scripting/repo/interpreter:add
/scripting/repo/interpreter:delete
/scripting/repo/bridge:add
/scripting/repo/script:download 

/exchange/users:get
/exchange/file/attach-h2 
/exchange/file/upload:remote

/environment/be:version 
/environment:mEpoch

/chat/fromUser/toGroup/multipart:send 
/chat/fromUser/toGroup/text:send
/chat/fromUser/toUser/multipart:send

/user/queue/control
/user/queue/audio
/user/queue/typing

/push/user/queue/multipart 

/heartbeat
/client:ip

/google/authenticate-new 
/google/token 
/google/authenticate

/okta/login
/okta/users:query 
/okta/polle
/okta/users:get
/okta/user:create 
/okta/register-phone 
/okta/register
/okta/logout
/okta/select-authenticator  -
/okta/verify-webauthn 
/okta/register-password
/okta/enroll-webauthn 
/okta/poll















/okta/forgot-password

/push/user/queue/update 

/exchange/users/exchange:get


/timer
/okta/verify-channel-data


'''



