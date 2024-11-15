import requests
from typing import List, Dict
import json
from json import JSONEncoder



api_url_stem = api_url_stem + ""

class JsonEncoder(JSONEncoder):
    def default(self, o):
        return o.__dict__



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


'''
 ################################ GENERIC API CALLS ###########################################################
'''

'''
	session:str, 
	userId:int, 
	verb:str - > PUT/POST/GET/PATCH/DELETE
	endpoint:str -> "/fromUser/toUser/text:send"
'''
def callGenericApi(	session:str, 
					user:str, 
					verb:str,
					endpoint:str,
					**headers, 
					**params):
   
   headers['user'] = user
   headers['session'] = session
   headers['id'] = userId
   headers['newUserFirstName'] = newUserFirstName
   headers['newUserLastName'] = newUserLastName

   api_url = api_url_stem + endpoint
   
   match verb.upper():
      case "GET":
         response = requests.get(api_url, headers=headers)
      case "PUT":
         response = requests.put(api_url, data={}, headers=headers)
      case "POST":
         response = requests.post(api_url, data={}, headers=headers)
	  case "PATCH":
         response = requests.patch(api_url, data={}, headers=headers)
	  case "DELETE":
         response = requests.delete(api_url, headers=headers)
      case _:
         return response.json()['errorCode']
		
   response = requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']
   
   
   
'''   Cache Start '''

'''
Get All Keys From The Store
'''
def getAllKeys(internalAdmin:str, internalAdminPasscode:str, keyList: str):
    headers = {}
    headers['internalAdmin'] = internalAdmin
    headers['internalAdminPasscode'] = internalAdminPasscode
    headers['keyList'] = keyList
    api_url = api_url_stem + "/cache/keys:query"
    response =   requests.post(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    return jsonResponse['payload']


'''
Set an object in the cache
'''
def clearStore(internalAdmin:str, internalAdminPasscode:str):
    headers = {}
    headers['internalAdmin'] = internalAdmin
    headers['internalAdminPasscode'] = internalAdminPasscode
    api_url = api_url_stem + "/cache/store:clear"
    response =   requests.post(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    return jsonResponse['payload']


'''
Delete a key and its value
'''
def delete(user:str, session:str, key:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['key'] = key
    api_url = api_url_stem + "/cache/store:delete"
    response =   requests.post(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''
Get an object from the cache
'''
def get(user:str, session:str, key:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['key'] = key
    api_url = api_url_stem + "/cache/store:get"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Check if an object by key exists in the cache
'''
def isKey(user:str, session:str, key:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['key'] = key
    api_url = api_url_stem + "/cache/store:isKey"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Set string to cache
'''
def set(user:str, session:str, validFor:int, notificationProxy:str, jsonObj:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['validFor'] = validFor
    headers['notificationProxy'] = notificationProxy
    api_url = api_url_stem + "/cache/store:set"
    response =   requests.post(api_url, data=jsonObj, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Update entire object
'''
def update(user:str, session:str, key:str, validFor:str, jsonObj:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['key'] = key
    headers['validFor'] = validFor
    api_url = api_url_stem + "/cache/store:update"
    response =   requests.post(api_url, data=jsonObj, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Update Validity
'''
def updateValidFor(user:str, session:str, key:str, validFor:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['key'] = key
    headers['validFor'] = validFor
    api_url = api_url_stem + "/cache/store:updateValidFor"
    response =   requests.post(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''
Update Value
'''
def updateValue(user:str, session:str, key:str, jsonObj:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['key'] = key
    api_url = api_url_stem + "/cache/store:updateValue"
    response =   requests.post(api_url, data=jsonObj, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''   Cache End '''





'''  ClusterController Start'''

'''
Stream back to the caller node, streaming response
'''

def streamingResponse(user:str, session:str, requestId:str, toSession:str, response:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['requestId'] = requestId
    headers['toSession'] = toSession
    api_url = api_url_stem + "/cluster/node/streaming:response"
    response =   requests.put(api_url, data=response, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Register node to a particular cluster node (User action)
'''
def addNode(user:str, session:str, ipAddress:str, port:str, type:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['ipAddress'] = ipAddress
    headers['port'] = port
    headers['type'] = type

    api_url = api_url_stem + "/cluster/node:add"
    response =   requests.put(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Delete Node (User action)
'''
def deleteNode(user:str, session:str, id:int):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['id'] = id
    api_url = api_url_stem + "/cluster/node:delete"
    response =   requests.delete(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Get All Registered Nodes
'''
def getNodeById(user:str, session:str, id:int):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['id'] = id
    api_url = api_url_stem + "/cluster/node:get"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
info
'''
def info():
    headers = {}
    api_url = api_url_stem + "/cluster/node:info"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Get All Registered Nodes
'''
def getAllNodes():
   headers = {}
   api_url = api_url_stem + "/cluster/node:query"
   response =   requests.get(api_url, headers=headers)
   jsonResponse = response.json()
   
   return jsonResponse['payload']


'''  ClusterController End'''


''' ConfigController Start '''

'''
Add allowed IP to endpoint
'''
def addEndpointAllowedIp(user:str, session:str, ip: IpToEndpointDbRecord):
    headers = {}
    headers['user'] = user
    headers['session'] = session

    api_url = api_url_stem + "/config/enpoint/ip:add"
    response =   requests.post(api_url, data=ip, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Add allowed IP to all endpoint
'''
def addAllowedIpToAllEndpoints(user:str, session:str, ipAddress: str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['ipAddress'] = ipAddress
    api_url = api_url_stem + "/config/enpoint/ip:addall"
    response =   requests.post(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''
Delete IP associated to endpoint
'''
def deleteAllowedIpToEndpoint(user:str, session:str, id:int, idEndpoint:int, ipAddress:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['id'] = id
    headers['idEndpoint'] = idEndpoint
    headers['ipAddress'] = ipAddress
    api_url = api_url_stem + "/config/enpoint/ip:delete"
    response =   requests.delete(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''
Add allowed IP to all endpoint
'''
def deleteAllowedIpToAllEndpoints(user:str, session:str, ipAddress:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['ipAddress'] = ipAddress
    api_url = api_url_stem + "/config/enpoint/ip:deleteall"
    response =   requests.delete(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Reload mapping ip to endpoints
'''
def reloadIpToEndpoints(user:str, session:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/config/enpoint:deleteall"
    response =   requests.post(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']




'''
Get all endpoint allowed IPs
'''
def getAllEndpointAllowedIPs(user:str, session:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/config/enpoint:get"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Get all current config settings
'''
def getConfig(user:str, session:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/config:get"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Get owner's name
'''
def getOwner(user:str, session:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/config:owner"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Change config setting
'''
def changeConfig(user:str, session:str, configRepoDbRecord: ConfigRepoDbRecord):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/config:update"
    response =   requests.post(api_url, data=configRepoDbRecord, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



''' ConfigController End '''



''' SqlRepoEnvironmentController Start'''

'''
Get Database Types List
'''
def GetDatabaseTypes():
   headers = {}
   api_url = api_url_stem + "/db-types"
   response =   requests.get(api_url, headers=headers)
   jsonResponse = response.json()
   
   return jsonResponse['payload']



'''
Get Repo List
'''
def GetRepoDbList():
   headers = {}
   api_url = api_url_stem + "/repos"
   response =   requests.get(api_url, headers=headers)
   jsonResponse = response.json()
   
   return jsonResponse['payload']


''' SqlRepoEnvironmentController End'''

''' ElasticsearchController Start '''

'''
Add association
'''
def addRepoAssociationTable(user:str, session:str, associationName: str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['associationName'] = associationName

   api_url = api_url_stem + "/elastic-repo/association:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete association
'''
def deleteRepoAssociationTable(user:str, session:str, associationId: int):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['associationId'] = associationId

   api_url = api_url_stem + "/elastic-repo/association:delete"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get associations
'''
def getRepoAssociationTable(user:str, session:str, associationName: str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['associationName'] = associationName

   api_url = api_url_stem + "/elastic-repo/association:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Update association
'''
def updateElasticRepoAssociationTable(user:str, session:str, associationId:int, associationName: str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['associationId'] = associationId
   headers['associationName'] = associationName

   api_url = api_url_stem + "/elastic-repo/association:update"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add a new host to an existing Elastic cluster
'''
def elasticHostAdd(user:str, session:str, clusterUniqueName:str, elasticHost: ElasticHost):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['clusterUniqueName'] = clusterUniqueName

   api_url = api_url_stem + "/elastic-repo/cluster/host:add"
   response =   requests.put(api_url, data=elasticHost, headers=headers)
   return response.json()['errorCode']


'''
Add a new host to an existing Elastic cluster
'''
def elasticHostDelete(user:str, session:str, clusterUniqueName:str, hostId:int):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['clusterUniqueName'] = clusterUniqueName
   headers['hostId'] = hostId

   api_url = api_url_stem + "/elastic-repo/cluster/host:delete"
   response = requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add a new host to an existing Elastic cluster
'''
def elasticHostUpdate(user:str, session:str, clusterUniqueName:str, elasticHost: ElasticHost):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['clusterUniqueName'] = clusterUniqueName

   api_url = api_url_stem + "/elastic-repo/cluster/host:update"
   response = requests.post(api_url, data=elasticHost, headers=headers)
   return response.json()['errorCode']


'''
Remove elasticsearch index
'''
def elasticIndexDelete(user:str, session:str, clusterUniqueName:str, indexName: str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['clusterUniqueName'] = clusterUniqueName
   headers['indexName'] = indexName

   api_url = api_url_stem + "/elastic-repo/cluster/index:remove"
   response = requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add a new Elastic cluster with all node connections
'''
def elasticClusterAdd(user:str, session:str, clusterUniqueName:str, clusterDescription:str, hostListStr:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['clusterUniqueName'] = clusterUniqueName
   headers['clusterDescription'] = clusterDescription

   api_url = api_url_stem + "/elastic-repo/cluster:add"
   response =   requests.put(api_url, data=hostListStr, headers=headers)
   return response.json()['errorCode']


'''
Remove Elasticsearch server/cluster connection
'''
def elasticClusterRemove(user:str, session:str, clusterUniqueName:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['clusterUniqueName'] = clusterUniqueName

   api_url = api_url_stem + "/elastic-repo/cluster:remove"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Update current elastic cluster info (cluster name and description)
'''
def elasticClusterUpdate(user:str, session:str, clusterId:int, clusterUniqueName:str, clusterDescription:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['clusterId'] = clusterId
   headers['clusterUniqueName'] = clusterUniqueName
   headers['clusterDescription'] = clusterDescription


   api_url = api_url_stem + "/elastic-repo/cluster:update"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Execute Sql or Dsl on multiple clusters / indexes and aggregate results with Sql
'''
def executeAdhocMultiple(user:str, session:str, listElasticCompoundQuery:ListElasticCompoundQuery):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/execute/adhoc/multiple:aggregate"
   response =   requests.put(api_url, data=listElasticCompoundQuery, headers=headers)
   return response.json()['errorCode']


'''
Get Execution History for Current User
'''
def getElasticHistory(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/history/user:personal"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Copy Csv to Elastic Index
'''
def copyCsvToElastic(user:str, session:str, path:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   f = open(path, "r")
   api_url = api_url_stem + "/elastic-repo/index/copy/csv:load"
   form_data = {'attacment': f}
   response =   requests.put(api_url, data=form_data, headers=headers)
   return response.json()['errorCode']


'''
Copy to Elastic Index from another Elastic Dsl query
'''
def copyElasticToElasticViaDsl(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/copy/elastic:dsl"
   response =   requests.put(api_url, body=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Copy to Elastic Index from another Elastic Sql query
'''
def copyElasticToElasticViaSql(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/copy/elastic:sql"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Copy to index from an Embedded Db query
'''
def copyFromEmbeddedQueryToElastic(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/copy/embedded:sql"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Copy to index from Mongo simple search
'''
def copyFromMongoAdhocToElastic(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/copy/mongo/adhoc:mql"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Copy from Mongo range search
'''
def copyFromMongoFullCollectionToElastic(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/copy/mongo:collection"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Copy from index Index from Mongo range search
'''
def copyFromMongoRangeToElastic(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/copy/mongo:range"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Copy to index from Mongo simple search
'''
def copyFromMongoSimpleQueryToElastic(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/copy/mongo:simple"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Copy to index from an RDBMS query
'''
def copyFromRDBMSQueryToElastic(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/copy/rdbms:sql"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Execute a generic adhoc Dsl query
'''
def runAdhocDsl(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/dsl/adhoc:run"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Query index via native DSL
'''
def searchFuzzyIndex(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/dsl:fuzzy"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
List indeces
'''
def listIndeces(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/management:list"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Update index mapping/properties
'''
def updateIndexMapping(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/mapping:update"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Execute a repo query 
'''
def runQueryFromRepo(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/query:run"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Execute an adhoc SQL statement against an index
'''
def runAdhocSql(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/sql/adhoc:run"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Translate Sql to Dsl
'''
def translateSqlToDsl(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index/sql:translate"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Create Elasticsearch Index
'''
def createIndex(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/index:create"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Set Query Bridge To Cluster
'''
def addQueryBridgeToCluster(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query/bridge:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete Query Bridge To Cluster
'''
def deleteQueryBridgeToCluster(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query/bridge:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Set Query Bridge To Cluster
'''
def getQueryBridgeToCluster(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query/bridge:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get query params 
'''
def addQueryParam(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query/param:add"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Delete query params 
'''
def deleteElasticQueryParam(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query/param:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get all params of the query
'''
def getQueryParam(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query/param:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get all params of the query
'''
def getQueryParams(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query/params:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Input Object for Query execution
'''
def getQueryInputObject(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query/signature"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add a new SQL/DSL statement to the repo
'''
def addQuery(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query:add"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Get the list of queries associated with a cluster 
'''
def getQueriesForCluster(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query:cluster"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Delete query statement against a Elasticsearch cluster/server 
'''
def deleteElasticQuery(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get the Dsl/Sql statement by searching a keyword 
'''
def getSpecificQuery(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query:get"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Search Dsl/Sql statement by searching a keyword 
'''
def searchElasticQuery(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/management/query:search"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Delete snapshot
'''
def deleteSnapshot(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/snapshot:delete"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get snapshot to visualize
'''
def getElasticSnapshot(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/snapshot:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get a list of snapshots to visualize
'''
def getElasticSnapshotHistory(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo/snapshot:history"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Query the Elastic Db Repository
'''
def elasticRepo(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/elastic-repo:list"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']




'''
Validate Adhoc Sql on a multiple dbs or an entire cluster
'''
def validateAdhocSqlOnCluster(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/embedded/adhoc/cluster/validate"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']

'''
Add user permission to cluster
'''
def addEmbeddedDbToCluster(user:str, session:str, userId: int, clusterId:int):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['userId'] = userId
    headers['clusterId'] = clusterId
    api_url = api_url_stem + "/embedded/cluster/access:add"
    response =   requests.put(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Delete User access to cluster
'''
def deleteUserAccessToCluster(user:str, session:str, userId:int, clusterId: int):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['userId'] = userId
    headers['clusterId'] = clusterId

    api_url = api_url_stem + "/embedded/cluster/access:delete"
    response =   requests.delete(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Get list of Embedded Databases for a cluster
'''
def getUserAccessToCluster(user:str, session:str, clusterId: int):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['clusterId'] = clusterId

    api_url = api_url_stem + "/embedded/cluster/access:get"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''
Get Schemas of an embedded db belonging to a cluster
'''
def getSchemas(user:str, session:str, clusterId: int, dbId:int):
    data = {}
    data['user'] = user
    data['session'] = session
    data['clusterId'] = clusterId
    data['dbId'] = dbId

    ret = getSchemas(data)
    jsonResponse = ret.json()
    
    return jsonResponse['payload']


def getSchemas(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/embedded/cluster/database/schemas"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Db Tables
'''
def getTables(user:str, session:str, clusterId: int, dbId:int, schema:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['clusterId'] = clusterId
    headers['dbId'] = dbId
    headers['schema'] = schema
    api_url = api_url_stem + "/embedded/cluster/database/tables"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Add Embedded Databases to a cluster
'''
def addEmbeddedDbToCluster(user:str, session:str, er: EmbeddedDbRecord):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/embedded/cluster/db:add"
    response =   requests.put(api_url, data=er, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Delete Embedded Databases of a cluster
'''
def deleteEmbeddedDbToCluster(user:str, session:str, dbId:int, clusterId:int):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['dbId'] = dbId
    headers['clusterId'] = clusterId
    api_url = api_url_stem + "/embedded/cluster/db:delete"
    response =   requests.delete(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Get list of Embedded Databases for a cluster
'''
def getEmbeddedDbToCluster(user:str, session:str, clusterId:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['clusterId'] = clusterId
    api_url = api_url_stem + "/embedded/cluster/db:get"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


''' Create an empty database as is part of a cluster '''
def newEmbeddedDbToCluster(user:str, session:str, clusterId:int, dbName:str, dbType: str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['clusterId'] = clusterId
    headers['dbName'] = dbName
    headers['dbType'] = dbType
    api_url = api_url_stem + "/embedded/cluster/db:new"
    response =   requests.put(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Add Embedded Cluster
'''
def addEmbeddedCluster(user:str, session:str, clusterName:str, description:str, ec: EmbeddedClusterInfo):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['clusterName'] = clusterName
    headers['description'] = description
    api_url = api_url_stem + "/embedded/clusters:add"
    response =   requests.put(api_url, data=ec, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''
Delete Embedded Cluster
'''
def deleteEmbeddedCluster(user:str, session:str, clusterId:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['clusterId'] = clusterId
    api_url = api_url_stem + "/embedded/clusters:delete"
    response =   requests.delete(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Get list of Embedded Clusters
'''
def getEmbeddedClusters(user:str, session:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/embedded/clusters:get"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Copy Elastic DSL query result to Embedded table
'''
def copyElasticDslResultToEmbedded(user:str, 
                                   session:str, 
                                   fromClusterUniqueName:str, 
                                   fromMongoDbName:str, 
                                   fromCollectionName:str, 

                                   toEmbeddedType:str,
                                   toEmbeddedDatabaseName:str,
                                   toCluster:str,
                                   toEmbeddedSchemaName:str,
                                   toEmbeddedTableName:str,
                                   dsl:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['fromClusterUniqueName'] = fromClusterUniqueName
    headers['fromMongoDbName'] = fromMongoDbName
    headers['fromCollectionName'] = fromCollectionName
    headers['toEmbeddedType'] = toEmbeddedType
    headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
    headers['toCluster'] = toCluster
    headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
    headers['toEmbeddedTableName'] = toEmbeddedTableName
    api_url = api_url_stem + "/embedded/copy/elastic:dsl"
    response =   requests.put(api_url, data=dsl, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Copy Elastic SQL query result to Embedded table
'''
def copyElasticSqlResultToEmbedded(user:str, 
                                   session:str, 
                                   fromElasticClusterName:str, 
                                   fromElasticFetchSize:str, 
                                   toEmbeddedType:str, 
                                   toEmbeddedDatabaseName:str,
                                   toCluster:str,
                                   toEmbeddedSchemaName:str,
                                   toEmbeddedTableName:str,
                                   sqlContent:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session

    headers['fromElasticClusterName'] = fromElasticClusterName
    headers['fromElasticFetchSize'] = fromElasticFetchSize
    headers['toEmbeddedType'] = toEmbeddedType
    headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
    headers['toCluster'] = toCluster
    headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
    headers['toEmbeddedTableName'] = toEmbeddedTableName
    api_url = api_url_stem + "/embedded/copy/elastic:sql"
    response =   requests.put(api_url, data=sqlContent, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Copy Csv to embedded table
'''
def copyCsvToEmbeddedTable(user, session, tableScript, toEmbeddedType, toClusterId, toEmbeddedDatabaseName, toEmbeddedSchemaName, toEmbeddedTableName, filePath):

    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['tableScript'] = tableScript
    headers['toEmbeddedType'] = toEmbeddedType
    headers['toClusterId'] = toClusterId

    headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
    headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
    headers['toEmbeddedTableName'] = toEmbeddedTableName
    api_url = api_url_stem + "/embedded/copy/embedded/csv:load"
    file = open(filePath, "rb")
    files = {'attachment': file }
    response =   requests.put(api_url, files=files, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Copy Rdbms Sql result to Embedded table
'''
def copyEmbeddedSqlResultToEmbedded(user:str, 
                                    session:str, 
                                    fromEmbeddedType:str, 
                                    fromClusterId:str, 
                                    fromEmbeddedDatabaseName:str, 
                                    fromEmbeddedSchemaName:str, 
                                    toEmbeddedType:str,
                                    toClusterId:str,
                                    toEmbeddedDatabaseName:str,
                                    toEmbeddedSchemaName:str,
                                    toEmbeddedTableName:str,
                                    sqlContent:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['fromEmbeddedType'] = fromEmbeddedType
    headers['fromClusterId'] = fromClusterId
    headers['fromEmbeddedDatabaseName'] = fromEmbeddedDatabaseName
    headers['fromEmbeddedSchemaName'] = fromEmbeddedSchemaName

    headers['toEmbeddedType'] = toEmbeddedType
    headers['toClusterId'] = toClusterId
    headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
    headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
    headers['toEmbeddedTableName'] = toEmbeddedTableName
    api_url = api_url_stem + "/embedded/copy/embedded:sql"
    response =   requests.put(api_url, data=sqlContent, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Copy Mongodb collection(s) range search result to Embedded table
'''
def copyMongoRangeSearchResultToEmbedded(user:str, 
                                          session:str, 
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
    response =   requests.put(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Copy records from Mongodb simple search to Embedded table
'''
def copyMongoSimpleSearchResultToEmbedded(user:str, 
                                          session:str, 
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
    response =   requests.put(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Copy Mongodb ad-hoc search result to Embedded table
'''
def copyMongoAdhocResultToEmbedded(user:str, 
                                          session:str, 
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
    headers['fromClusterUniqueName'] = fromClusterUniqueName
    headers['fromMongoDbName'] = fromMongoDbName
    headers['fromCollectionName'] = fromCollectionName
    headers['toEmbeddedType'] = toEmbeddedType
    headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName
    headers['toCluster'] = toCluster
    headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
    headers['toEmbeddedTableName'] = toEmbeddedTableName
    api_url = api_url_stem + "/embedded/copy/mongodb:adhoc"
    response =   requests.put(api_url, data=bsonQuery, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Copy full Mongodb collection to Embedded table
'''
def copyMongoFullCollectionToEmbedded(user:str, 
                                      session:str, 

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
    response =   requests.put(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''
Copy Rdbms Sql result to Embedded table
'''
def copyRdbmsSqlResultToEmbedded(user:str, 
                                 session:str, 
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

    headers['fromRdbmsSchemaUniqueName'] = fromRdbmsSchemaUniqueName
    headers['toEmbeddedType'] = toEmbeddedType
    headers['toClusterId'] = toClusterId
    headers['toEmbeddedDatabaseName'] = toEmbeddedDatabaseName                      
    headers['toEmbeddedSchemaName'] = toEmbeddedSchemaName
    headers['toEmbeddedTableName'] = toEmbeddedTableName
    api_url = api_url_stem + "/embedded/copy/sqlrepo:sql"
    response =   requests.put(api_url, data=sqlContent, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Get list of supported database types
'''
def getEmbeddedDbTypes(user:str, session:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/embedded/dbtypes:get"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Execute Adhoc DDL
'''
def getCreateTableStmFromSql(user, session, sqlContent):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/embedded/execute/adhoc/statement/table:create"
    response =   requests.post(api_url, data=sqlContent, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Execute Adhoc Sql on a multiple dbs or an entire cluster
'''
def executeAdhocSqlOnCluster(user, session, sqlContent):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/embedded/execute/adhoc:cluster"
   response =   requests.post(api_url, data=sqlContent, headers=headers)
   return response.json()['errorCode']

'''
Execute Adhoc Ddl
'''
def executeDdl(user, session, clusterId:int, fileName:str, t:str, schema:str, sqlContent:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    
    headers['clusterId'] = clusterId
    headers['fileName'] = fileName
    headers['type'] = t
    headers['schema'] = schema
    api_url = api_url_stem + "/embedded/execute/adhoc:ddl"
    response =   requests.post(api_url, data=sqlContent, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''
Execute Adhoc Sql
'''
def executeInMemAdhocSql(user, session, sessionId, requestId, dbName, sqlType, sqlContent:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['sessionId'] = sessionId
    headers['requestId'] = requestId
    headers['dbName'] = dbName
    headers['sqlType'] = sqlType
    api_url = api_url_stem + "/embedded/execute/adhoc:single"
    response =   requests.post(api_url, data=sqlContent, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Execute Adhoc Sql
'''
def executeInMemAdhocSql(user, session, sessionId, requestId, dbName, sqlType, sqlContent:str):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['sessionId'] = sessionId
    headers['requestId'] = requestId
    headers['dbName'] = dbName
    headers['sqlType'] = sqlType
    api_url = api_url_stem + "/embedded/execute/inmem/adhoc:single"
    response = requests.post(api_url, data=sqlContent, headers=headers)   
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Copy Csv to in mem table
'''
def copyCsvToInMemDb(user, session, tableScript, filePath, comment):

    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['tableScript'] = tableScript
    headers['comment'] = comment
    api_url = api_url_stem + "/embedded/inmem/csv:load"
    file = open(filePath, "rb")
    files = {'attachment': file }
    response =   requests.put(api_url, files=files, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Create Empty tables to in mem db
'''
def createEmptyTablesInMemDb(user, session, comment, tableDefinition:List[TableDefinition] ):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['comment'] = comment
    api_url = api_url_stem + "/embedded/inmem/table:empty"
    response =   requests.put(api_url, data=tableDefinition, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Append in-mem db
'''
def appendInMemDbTables(user, session, comment, tableDefinition:List[TableDefinition] ):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['comment'] = comment
    api_url = api_url_stem + "/embedded/inmem/table:append"
    response =   requests.put(api_url, data=tableDefinition, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''
Load Elastic Index Dsl query result in memory
'''
def loadElasticIndexInMemViaDsl(user, 
                                session, 
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
    headers['fromElasticClusterName'] = fromElasticClusterName
    headers['fromIndexName'] = fromIndexName
    headers['fromHttpVerb'] = fromHttpVerb
    headers['fromElasticApi'] = fromElasticApi
    headers['fromEndPoint'] = fromEndPoint
    headers['batchValue'] = batchValue
    headers['comment'] = comment
    api_url = api_url_stem + "/embedded/inmem/elastic:dsl"
    response =   requests.put(api_url, data=dslStatement, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']



''' Load Elastic Index Sql query result in memory '''
def loadElasticIndexInMemViaSql(user, session, fromElasticClusterName, fromIndexName, fromHttpVerb, fromElasticApi, fromEndPoint, batchValue, comment, sqlStatement:str): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['fromElasticClusterName'] = fromElasticClusterName
    headers['fromIndexName'] = fromIndexName
    headers['fromHttpVerb'] = fromHttpVerb
    headers['fromElasticApi'] = fromElasticApi
    headers['fromEndPoint'] = fromEndPoint
    headers['batchValue'] = batchValue
    headers['comment'] = comment
    api_url = api_url_stem + "/embedded/inmem/elastic:sql"
    response =   requests.put(api_url, data=sqlStatement, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''
Copy records to RDBMS table from another Mongodb collection(s) range search
'''
def loadMongoRangeSearchResultInMem(user, 
                                     session, 
                                     fromMongoClusterName, 
                                     fromMongoDatabaseName, 
                                     fromMongoCollectionName, 

                                     itemToSearch, 
                                     fromValue, 
                                     toValue, 
                                     valueSearchType, 
                                     batchCount, 
                                     comment): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['fromMongoClusterName'] = fromMongoClusterName
    headers['fromMongoDatabaseName'] = fromMongoDatabaseName
    headers['fromMongoCollectionName'] = fromMongoCollectionName
    headers['itemToSearch'] = itemToSearch
    headers['fromValue'] = fromValue
    headers['toValue'] = toValue
    headers['valueSearchType'] = valueSearchType
    headers['batchCount'] = batchCount
    headers['comment'] = comment
    api_url = api_url_stem + "/embedded/inmem/mongodb/search:range"
    response =   requests.put(api_url, data={}, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Load in memory result from Mongodb simple search
'''
def loadMongoSimpleSearchResultInMem(user, 
                                     session, 
                                     fromMongoClusterName, 
                                     fromMongoDatabaseName, 
                                     fromMongoCollectionName, 
                                     itemToSearch, 
                                     valueToSearch, 
                                     valueToSearchType, 
                                     operator, 
                                     batchCount, 
                                     comment): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
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
    response =   requests.put(api_url, data={}, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Copy records to RDBMS table from Mongodb ad-hoc search
'''
def loadMongoFullCollectionInMem(user:str, 
                                 session:str, 
                                 fromMongoClusterName:str, 
                                 fromMongoDatabaseName:str, 
                                 fromMongoCollectionName:str, 
                                 batchCount:int, 
                                 comment:str, 
                                 bsonQuery:str): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['fromMongoClusterName'] = fromMongoClusterName
    headers['fromMongoDatabaseName'] = fromMongoDatabaseName
    headers['fromMongoCollectionName'] = fromMongoCollectionName
    headers['batchCount'] = batchCount
    headers['comment'] = comment
    api_url = api_url_stem + "/embedded/inmem/mongodb:adhoc"
    response =   requests.put(api_url, data=bsonQuery, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Load results in memory full Mongodb collection
'''
def loadMongoFullCollectionInMem(user, 
                                 session, 
                                 fromMongoClusterName, 
                                 fromMongoDatabaseName, 
                                 fromMongoCollectionName, 
                                 itemToSearch, 
                                 valueToSearch, 
                                 valueToSearchType, 
                                 operator, 
                                 batchCount, 
                                 comment): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
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
    response =   requests.put(api_url, data={}, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']





''' Load RDBMS query result in memory '''
def loadRdbmsQueriesInMem(user, session, schemaUniqueName, comment, listRdbmsCompoundQuery:List[ListRdbmsCompoundQuery]): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['schemaUniqueName'] = schemaUniqueName
    headers['comment'] = comment
    api_url = api_url_stem + "/embedded/inmem/rdbms:queries"
    response =   requests.put(api_url, body=listRdbmsCompoundQuery, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']




'''
Load RDBMS query result in memory
'''
def loadRdbmsTablesInMem(user, session, schemaUniqueName, comment, listRdbmsTables:List[str]): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['schemaUniqueName'] = schemaUniqueName
    headers['comment'] = comment
    api_url = api_url_stem + "/embedded/inmem/rdbms:tables"
    response =   requests.put(api_url, data=listRdbmsTables, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
remove all in-mem storage for request
'''
def removeRequestInMemoryDbs(user, session, sessionId, requestId): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['sessionId'] = sessionId
    headers['requestId'] = requestId
    api_url = api_url_stem + "/embedded/inmem/stores/remove:request"
    response =   requests.post(api_url, data={}, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''
Load cluster in memory 
'''
def loadEmbeddedClusterInMem(user, session, clusterId, comment): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['clusterId'] = clusterId
    headers['comment'] = comment
    api_url = api_url_stem + "/embedded/inmem:cluster"
    response =   requests.put(api_url, data={}, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']



'''
Load database in memory.Database is part of a cluster 
'''
def loadEmbeddedDbInMem(user, session, clusterId, dbId, comment): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['dbId'] = dbId
    headers['clusterId'] = clusterId
    headers['comment'] = comment
    api_url = api_url_stem + "/embedded/inmem:database"
    response =   requests.put(api_url, data={}, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']




'''
Load query result in memory
'''
def loadEmbeddedQueryInMem(user, session, fromEmbeddedType, fromClusterId, fromEmbeddedDatabaseName, fromEmbeddedSchemaName, comment, sqlContent): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['fromEmbeddedType'] = fromEmbeddedType
    headers['fromClusterId'] = fromClusterId
    headers['fromEmbeddedDatabaseName'] = fromEmbeddedDatabaseName
    headers['fromEmbeddedSchemaName'] = fromEmbeddedSchemaName
    headers['comment'] = comment
    api_url = api_url_stem + "/embedded/inmem:query"
    response =   requests.put(api_url, data=sqlContent, headers=headers)    
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Get a list of in-mem db
'''
def getInMemoryDbs(user, session): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/embedded/inmem:stores"
    response =   requests.get(api_url, headers=headers)      
    jsonResponse = response.json()
    
    return jsonResponse['payload']

'''
Get list of Sql Commands
'''
def getEmbeddedSqlCommands(user, session): 
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/embedded/staticinfo:get"
    response =   requests.get(api_url, headers=headers)      
    jsonResponse = response.json()
    
    return jsonResponse['payload']


''' EmbeddedController  End '''


''' EnvironmentController Start '''

'''
Get the log
'''
def log(stringToSearch:str):
   headers = {}
   headers['stringToSearch'] = stringToSearch
   api_url = api_url_stem + "/environment/log:query"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
About this API
'''
def about():
   headers = {}
   api_url = api_url_stem + "/environment:about"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


''' EnvironmentController End '''


''' ExchangeStorageController Start '''

'''
Pull and attach a file that is an H2 database to en existing embedded cluster from either own or remote exchange
'''
def pullAndAttachH2(externalExchangeUid:str, externalUserPassword:str, fromExchangeUid:str, fromUserEmail:str, fileName:str, fileUid:str, fileKey:str):
   headers = {}
   headers['externalExchangeUid'] = externalExchangeUid
   headers['externalUserPassword'] = externalUserPassword
   headers['fromExchangeUid'] = fromExchangeUid
   headers['fromUserEmail'] = fromUserEmail
   headers['fileName'] = fileName
   headers['fileUid'] = fileUid
   headers['fileKey'] = fileKey
   api_url = api_url_stem + "/exchange/attach-h2"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get exchange info
'''
def getAssociatedExchanges(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/exchange/exchange/associated:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


''' Search exchanges '''
def searchExchanges(user:str, session:str, exchange):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['exchange'] = exchange
   api_url = api_url_stem + "/exchange/exchange/search:get"
   response = requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get list of Sql Commands
'''
def deleteRemoteExchange(user:str, session:str, exchangeUid):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['exchangeUid'] = exchangeUid
   api_url = api_url_stem + "/exchange/exchange:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get exchange info
'''
def getAllExchanges(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/exchange/exchange:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add new remote exchange, local exchange can interact with
'''
def addNewRemoteExchange(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/exchange/exchange:new"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Update remote exchange, local exchange can interact with. This end point will not update own exchange
'''
def updateRemoteExchange(user:str, session:str, id:int, exchangeAddress:str, exchangeName:str, exchangeUid:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session

   headers['id'] = id
   headers['exchangeAddress'] = exchangeAddress
   headers['exchangeName'] = exchangeName
   headers['exchangeUid'] = exchangeUid

   api_url = api_url_stem + "/exchange/exchange:update"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Move and attach embedded db from Exchange
'''
def moveAndAttachH2FromExchange(user:str, session:str, fileId:int, clusterId:int):
   headers = {}
   headers['user'] = user
   headers['session'] = session

   headers['fileId'] = fileId
   headers['clusterId'] = clusterId

   api_url = api_url_stem + "/exchange/file/attach-h2"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete a file on this Data Exchange Server or own file on remote
'''
def deleteFileFromLocalRequest(user:str, session:str, fileId):
   headers = {}
   headers['user'] = user
   headers['session'] = session

   headers['fileId'] = fileId
   api_url = api_url_stem + "/exchange/file/delete:local"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Delete a file on this Data Exchange Server coming from remote request
'''
def deleteFileFromRemoteRequest(externalExchangeUid:str, externalUserPassword:str, toUserEmail:str, fileName:str, fileUid:str, fileKey:str):
   headers = {}
   headers['externalExchangeUid'] = externalExchangeUid
   headers['externalUserPassword'] = externalUserPassword

   headers['toUserEmail'] = toUserEmail
   headers['fileName'] = fileName
   headers['fileUid'] = fileUid
   headers['fileKey'] = fileKey

   api_url = api_url_stem + "/exchange/file/delete:remote"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Send a file from this exchange/instance to a remote exchange/instance
'''
def sendFileToRemoteExchange(user:str, session:str, isPing, toExchangeId, externalUserPassword, toUserEmail, filePath, fileName, fileType, fileUid, fileKey, periodValid):
   headers = {}
   headers['user'] = user
   headers['session'] = session

   headers['isPing'] = isPing
   headers['toExchangeId'] = toExchangeId
   headers['externalUserPassword'] = externalUserPassword

   headers['toUserEmail'] = toUserEmail
   headers['filePath'] = filePath
   headers['fileName'] = fileName
   headers['fileType'] = fileType
   headers['fileUid'] = fileUid
   headers['fileKey'] = fileKey
   headers['periodValid'] = periodValid

   api_url = api_url_stem + "/exchange/file/send:remote"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Upload a file stored on this exchange on remote exchange
'''
def uploadFileFromRemoteExchange(user:str, session:str, isPing, toExchangeId, externalUserPassword, toUserEmail, filePath, fileName, fileType, fileUid, fileKey, periodValid, path):
   headers = {}
   headers['user'] = user
   headers['session'] = session

   headers['isPing'] = isPing
   headers['toExchangeId'] = toExchangeId
   headers['externalUserPassword'] = externalUserPassword

   headers['toUserEmail'] = toUserEmail
   headers['filePath'] = filePath
   headers['fileName'] = fileName
   headers['fileType'] = fileType
   headers['fileUid'] = fileUid
   headers['fileKey'] = fileKey
   headers['periodValid'] = periodValid
   f = open(path, "r")
   api_url = api_url_stem + "/exchange/file/upload:remote"
   form_data = {'attacment': f}
   response =   requests.post(api_url, data=form_data, headers=headers)
   return response.json()['errorCode']


'''
Update file info on remote exchange, to know I have a file on this exchange.This is not always possible if exchange do not have open endpoint
'''
def getRequestFromRemoteExchangeToUpdateFileUpload(exchangeId, fromUserEmail, fromUserPassword, toUserEmail, fileName, fileType, fileUid, fileKey, timeStamp, timeAvailable ):
   headers = {}
   headers['exchangeId'] = exchangeId
   headers['fromUserEmail'] = fromUserEmail
   headers['fromUserPassword'] = fromUserPassword
   headers['toUserEmail'] = toUserEmail
   headers['fileName'] = fileName
   headers['fileType'] = fileType
   headers['fileUid'] = fileUid
   headers['fileKey'] = fileKey
   headers['timeStamp'] = timeStamp
   headers['timeAvailable'] = timeAvailable

   api_url = api_url_stem + "/exchange/file:update"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Generate strong password
'''
def generateStrongPassword():
   headers = {}
   api_url = api_url_stem + "/exchange/generate:password"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Generate uid
'''
def generateUid(user:str, session:str):
   headers = {}
   api_url = api_url_stem + "/exchange/generate:uid"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Import files such as JSON tables or RestResponse into attached systems from either own or remote exchange
'''
def pullAndImportFile(user, session, externalExchangeUid, externalUserPassword, fromExchangeUid, fromUserEmail, fileName, fileUid, fileKey):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['externalExchangeUid'] = externalExchangeUid
   headers['externalUserPassword'] = externalUserPassword
   headers['fromExchangeUid'] = fromExchangeUid
   headers['fromUserEmail'] = fromUserEmail
   headers['fileName'] = fileName
   headers['fileUid'] = fileUid
   headers['fileKey'] = fileKey

   api_url = api_url_stem + "/exchange/import"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Query Files, sent or received 
'''
def queryExchange(exchangeId, externalUserPassword, fromUserEmail, toUserEmail):
   headers = {}
   headers['exchangeId'] = exchangeId
   headers['externalUserPassword'] = externalUserPassword
   headers['fromUserEmail'] = fromUserEmail
   headers['toUserEmail'] = toUserEmail
   api_url = api_url_stem + "/exchange/local:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Query Files, sent or received 
'''
def queryFromRemoteExchange(externalUserEmail, externalUserPassword):
   headers = {}
   headers['externalUserEmail'] = externalUserEmail
   headers['externalUserPassword'] = externalUserPassword
   api_url = api_url_stem + "/exchange/remote:query"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add new user to this exchange
'''
def addUserToExchange(user, session, userId, exchangeId, isAdmin):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['userId'] = userId
   headers['exchangeId'] = exchangeId
   headers['isAdmin'] = isAdmin

   api_url = api_url_stem + "/exchange/user/exchange:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete user from this exchange
'''
def deleteUserToExchange(user, session, userId, exchangeId):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['userId'] = userId
   headers['exchangeId'] = exchangeId

   api_url = api_url_stem + "/exchange/user/exchange:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


''' Get user to exchange info '''
def getUserToExchanges(user, session):
   headers = {}
   headers['user'] = user
   headers['session'] = session

   api_url = api_url_stem + "/exchange/user/exchange:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get user to exchange extended info
'''
def getUserToExchangesExtended(user, session):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/exchange/user/exchange:getExtended"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get associated exchanges to a user 
'''
def getAssociatedExchangesToUser(user, session, userId):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['userId'] = userId
   api_url = api_url_stem + "/exchange/user/exchanges:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get own user info related to exchanges 
'''
def getCurrentUserInfo(user, session):
   headers = {}
   headers['user'] = user
   headers['session'] = session
  
   api_url = api_url_stem + "/exchange/user/own:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Delete user from this exchange
'''
def updateExternalUserPasswordByUser(externalUserEmail, externalExchangeUid, externalUserPassword, newExternalUserPassword):
   headers = {}
   headers['externalUserEmail'] = externalUserEmail
   headers['externalExchangeUid'] = externalExchangeUid
   headers['externalUserPassword'] = externalUserPassword
   headers['newExternalUserPassword'] = newExternalUserPassword
   api_url = api_url_stem + "/exchange/user/self:update"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add new user to this exchange
'''
def addNewExchangeUser(user, session, email, exchangeId, isAdmin, userPassword):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['email'] = email
   headers['exchangeId'] = exchangeId
   headers['isAdmin'] = isAdmin
   headers['userPassword'] = userPassword
   api_url = api_url_stem + "/exchange/user:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete user from this exchange
'''
def deleteExchangeUser(user, session, idExchange:int):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['id'] = idExchange
   api_url = api_url_stem + "/exchange/user:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Delete user from this exchange
'''
def updateExternalUserPasswordByAdmin(user, session, idExchange:int, password:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['id'] = idExchange
   headers['password'] = password

   api_url = api_url_stem + "/exchange/user:update"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get users 
'''
def getExchangeUsers(user, session, email:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['email'] = email

   api_url = api_url_stem + "/exchange/users/email:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get users 
'''
def getUsersByExchange(user, session, exchangeId:int):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['exchangeId'] = exchangeId

   api_url = api_url_stem + "/exchange/users/exchange:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get users 
'''
def getAssociatedUsersToExchange(user, session, exchangeId:int):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['exchangeId'] = exchangeId

   api_url = api_url_stem + "/exchange/users:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']



''' ExchangeStorageController End '''


''' HealthController '''




'''
healthCheckSelf
'''
def healthCheckSelf(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/health/healthCheck"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
healthCheckOther
'''
def healthCheckOther(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/health/healthCheck/baseAddress"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
healthCheckThat
'''
def healthCheckThat(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/health/healthCheck/baseAddress/browser/{baseAddress}"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
healthCheckRegisteredNodes
'''
def healthCheckRegisteredNodes(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/health/healthCheck/registeredNodes"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
ping
'''
def ping(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/health/ping"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
pingAnotherNode
'''
def pingAnotherNode(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/health/ping/node/{baseAddress}"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
pingRegisteredNodes
'''
def pingRegisteredNodes(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/health/ping/registeredNodes"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
pingUrl
'''
def pingUrl(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/health/ping:url"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
testAdminAccount
'''
def testAdminAccount(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/health/test/adminAccount"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
testAdminAccount_
'''
def testAdminAccount_(user:str, session:str, admin, adminPasscode):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/health/test/adminAccount/browser/" + admin + "/" + adminPasscode
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
testUserAccount
'''
def testUserAccount(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/health/test/userAccount"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
testUserAccount_
'''
def testUserAccount_(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/health/test/userAccount/browser/{user}/{userPasscode}"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Delete file from internal storage
'''
def deleteFile(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/internalStorage/deleteFile"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Download file from internal storage 
'''
def downloadFile(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/internalStorage/downloadFile"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
List all files in internal storage
'''
def listFilesInFolder(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/internalStorage/list"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Move and attach embedded db from storage
'''
def moveAndAttachH2FromStorage(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/internalStorage/storage/attach-h2"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Import files such as JSON tables or RestResponse into attached systems
'''
def importFile(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/internalStorage/storage/import"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get user priviledges for a stored file
'''
def getFilePriv(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/internalStorage/storage/priv"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get file type
'''
def getFileType(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/internalStorage/storage/type"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Upload a generic file, on internal storage
'''
def uploadFile(user:str, session:str, path:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/internalStorage/uploadFile"
   f = open(path, "r")
   form_data = {'attacment': f}
   response =   requests.post(api_url, data=form_data, headers=headers)
   return response.json()['errorCode']


'''
List all files in internal storage for current user
'''
def getFilesByUser(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/internalStorage/user/list"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add internal user to a file uploaded by another internal user
'''
def addUserToFile(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/internalStorage/user:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add internal user to a file uploaded by another internal user
'''
def deleteUserToFile(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/internalStorage/user:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Query logs
'''
def getLogArtifact(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/logging/application/artifact:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add new log entry with artifact/attached file
'''
def addLogEntryWithArtifact(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/logging/application/logs/artifact:add"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add new log entry
'''
def addLogEntry(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/logging/application/logs/entry:add"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Query logs
'''
def getLogEntry(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/logging/application/logs:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Query logs
'''
def queryLogs(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/logging/application/logs:query"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Remove Application Logs
'''
def deleteApplicationLogs(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/logging/application/logs:remove"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get All Partitions of an application
'''
def getApplicationPartitions(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/logging/application/partition:query"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get All Applications
'''
def getAllApplications(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/logging/application:query"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Remove Application Logs
'''
def deleteApplication(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/logging/application:remove"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Set application
'''
def setApplication(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/logging/application:set"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']




'''
Get Users
'''
def getManagers(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/managers:query"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add 
'''
def addMlApiStub(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/ml/mlApi/deployment:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add 
'''
def deleteMlApiStub(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/ml/mlApi/deployment:delete"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get information about ML APi installed
'''
def getAllMlApiServer(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/ml/mlApi:get"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add Interpreter and its metadata information
'''
def createNewMlApiServer(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/ml/mlApi:new"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add Interpreter and its metadata information
'''
def startMlApiServer(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/ml/mlApi:start"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add Interpreter and its metadata information
'''
def stopMlApiServer(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/ml/mlApi:stop"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add Interpreter and its metadata information
'''
def runModel(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/ml/model:run"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get first N documents in the bucket
'''
def getFirstNBucketDocuments(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/bucket/document:firstn"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']



'''
Add new bucket to a Mongo Database
'''
def mongoDatabaseBucketAdd(user, session, clusterUniqueName, databaseName, bucketName):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['clusterUniqueName'] = clusterUniqueName
   headers['databaseName'] = databaseName
   headers['bucketName'] = bucketName
   api_url = api_url_stem + "/mongo-repo/cluster/bucket:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']



def mongoDatabaseBucketDelete(user, session, clusterUniqueName, databaseName, bucketName):
    data = {}
    data['user'] = user
    data['session'] = session
    data['clusterUniqueName'] = clusterUniqueName
    data['databaseName'] = databaseName
    data['bucketName'] = bucketName

    ret = mongoDatabaseBucketAdd(data)
    jsonResponse = ret.json()
    
    return jsonResponse['payload']['genericPayload']


'''
Delete bucket from Mongo Database
'''
def mongoDatabaseBucketAdd(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/bucket:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Copy Csv file to collection
'''
def copyCsvToCollection(user:str, session:str, path:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/csv:load"
   f = open(path, "r")
   form_data = {'attacment': f}
   response =   requests.put(api_url, data=form_data, headers=headers)
   return response.json()['errorCode']


'''
Copy records to collection from Elastic DSL query
'''
def copyElasticDslToCollection(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/elastic:dsl"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Create/add records to collection from Elastic SQL query
'''
def copyElasticSqlToCollection(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/elastic:sql"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Copy records to collection from Embedded adhoc query
'''
def copyEmbeddedQueryToCollection(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/embedded/adhoc:sql"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Copy records to collection from another Mongodb collection(s) range search
'''
def copyRangeSearchToCollection(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/mongodb/search:range"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Copy records to collection from another Mongodb collection(s) simple search
'''
def copySimpleSearchToCollection(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/mongodb/search:simple"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Copy records to collection from full Mongodb collection
'''
def copyMongoAdhocMqlToCollection(user:str, session:str, mql):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/mongodb:adhoc"
   response =   requests.put(api_url, data=mql, headers=headers)
   return response.json()['errorCode']


'''
Copy records to collection from full Mongodb collection
'''
def copyFullCollectionToCollection(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/mongodb:collection"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Copy records to collection from RDBMS query
'''
def copyRDBMSQueryToCollection(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/copy/rdbms:sql"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
add multiple records to a collection delivered in a zip file 
'''
def addBatchDocumentToCollection(user:str, session:str, path:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document/add:batch"
   f = open(path, "r") 
   form_data = {'attacment': f}
   response =   requests.post(api_url, data=form_data, headers=headers)
   return response.json()['errorCode']


'''
Add single document to collection
'''
def addDocumentToCollection(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document/add:single"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Get count of all documents in collection
'''
def getCollectionDocsCount(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document/count"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Drop Records froma list of ids
'''
def deleteMultipleDocuments(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document/delete:multiple"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Drop object by id
'''
def deleteDocumentById(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document/delete:single"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Create RDBMS table from multiple ResultQuery documents
'''
def compoundDocument(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document/rdbms/table:compound"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Create RDBMS table from ResultQuery document
'''
def createRdbmsTableFromDocument(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document/rdbms/table:create"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
replace/update object by id
'''
def replaceDocumentById(user:str, session:str, doc):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document/replace-update:single"
   response =   requests.post(api_url, data=doc, headers=headers)
   return response.json()['errorCode']


'''
Range based Search, providing item to search, from and to value and value type
'''
def searchDocumentComplexAnd(user:str, session:str, query):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document/search:complex-and"
   response =   requests.post(api_url, data=query, headers=headers)
   return response.json()['errorCode']


'''
Range based Search, providing item to search, from and to value and value type
'''
def searchDocumentRange(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document/search:range"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Simple Search, providing item to search, value to search and value type 
'''
def searchDocumentSimple(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document/search:simple"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Search collection for text
'''
def searchSimpleText(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document/search:text"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get first N documents in the collection
'''
def getFirstNDocuments(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document:firstn"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get previously saved document/resultset
'''
def getDocument(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Move document/resultset from one collection to another accross clusters and databases
'''
def moveDocument(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/document:move"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Drop Records
'''
def deleteManyRecords(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/drop:many"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Drop Records in a range
'''
def deleteManyRecordsRange(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/drop:range"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Drop Records
'''
def deleteManyRecordsSimpleTextSearch(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/drop:simple-text"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Create an index for collection
'''
def mongoCollectionIndexCreate(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/index:create"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Create an index for collection
'''
def mongoCollectionIndexDelete(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/index:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Execute a generic Mql command providing it in Bson/Json format
'''
def runAdhocMql(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/query:adhoc"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Execute a generic Mql command providing it in Bson/Json format
'''
def runAdhocBson(user:str, session:str, bsonString):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/query:bson"
   response =   requests.post(api_url, data=bsonString, headers=headers)
   return response.json()['errorCode']


'''
Query previously saved record set
'''
def queryDocuments(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/query:repo"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Execute a generic HTTP command
'''
def repoMql(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection/query:repo"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']



def mongoDatabaseCollectionAdd(user, session, clusterUniqueName, databaseName, collectionName):

    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['clusterUniqueName'] = clusterUniqueName
    headers['databaseName'] = databaseName
    headers['collectionName'] = collectionName
    api_url = api_url_stem + "/mongo-repo/cluster/collection:add"
    response =   requests.put(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    
    return jsonResponse['payload']


'''
Add new collection to a Mongo Database
'''
def mongoDatabaseCollectionAdd(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collection:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


def mongoDatabaseCollectionDrop(user, session, clusterUniqueName, databaseName, collectionName):

    data = {}
    data['user'] = user
    data['session'] = session
    data['clusterUniqueName'] = clusterUniqueName
    data['databaseName'] = databaseName
    data['collectionName'] = collectionName

    ret = mongoDatabaseCollectionDrop(data)
    jsonResponse = ret.json()
    
    return jsonResponse['payload']


'''
Drop collection from Mongo Database
'''
def mongoDatabaseCollectionDrop(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collections:drop"
   response =   requests.delete(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get the list of collections of a single database of a cluster/Mongo Server
'''
def mongoDatabaseCollectionList(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/collections:list"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get the list of dayabases in a cluster/Mongo Server
'''
def mongoDatabaseList(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/databases:list"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add large binary file to mongo database
'''
def addLargeAttachment(user:str, session:str, path:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/large:add"
   f = open(path, "r")
   form_data = {'attacment': f}
   response =   requests.post(api_url, data=form_data, headers=headers)
   return response.json()['errorCode']


'''
Delete large binary file from mongo database
'''
def deleteLargeAttachment(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/large:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get large binary file from mongo database
'''
def downloadLargeAttachment(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/large:download"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get large binary file from mongo database
'''
def getLargeAttachment(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster/large:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add a new Mongo database/cluster connection to the list of available databases/cluster connections
'''
def mongoRepoAdd(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Remove a Mongo database/cluster connection
'''
def mongoRepoRemove(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster:remove"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Update a Mongo database/cluster connection
'''
def mongoRepoUpdate(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/cluster:update"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Execute Mql on multiple clusters / collections and aggregate results with Sql
'''
def executeAdhocMultiple_1(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/execute/adhoc/multiple:aggregate"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Get Execution History for Current User
'''
def getMongoHistory(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/history/user:personal"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add association
'''
def addMongoRepoAssociationTable(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/association:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete association
'''
def deleteMongoRepoAssociationTable(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/association:delete"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get associations
'''
def getMongoRepoAssociationTable(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/association:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Update association
'''
def updateRepoAssociationTable(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/association:update"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add existing MQL statement to a cluster
'''
def addMqlToClusterBridge(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/query/bridge:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete Mql to Cluster bridge
'''
def deleteMqlToClusterBridge(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/query/bridge:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get the Mql statement by id 
'''
def getQueryBridges(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/query/bridge:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add params to an existing MQL statement
'''
def addMongoQueryParam(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/query/param:add"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Add params to an existing MQL statement
'''
def deleteMongoQueryParam(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/query/param:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add params to an existing MQL statement
'''
def generateQueryParam(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/query/param:generate"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add a new MQL statement or update an existing one
'''
def addMongoQuery(user:str, session:str, mQuery):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/query:add"
   response =   requests.put(api_url, data=mQuery, headers=headers)
   return response.json()['errorCode']


'''
Delete Mql statement 
'''
def deleteMongoQuery(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/query:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get the Mql statement by id 
'''
def getQueryById(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/query:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get the Mql statement by searching a keyword 
'''
def searchQuery(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/management/query:search"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Delete snapshot
'''
def deleteMangoSnapshot(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/snapshot:delete"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get snapshot to visualize
'''
def getSnapshot(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/snapshot:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get a list of snapshots to visualize
'''
def getMongoSnapshotHistory(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/mongo-repo/snapshot:history"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get the Mongo Db Repository
'''
def getMongoRepo(user, session, searchString):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    headers['uniqueName'] = searchString
    api_url = api_url_stem + "/mongo-repo:list"
    response =   requests.get(api_url, headers=headers)
    jsonResponse = response.json()
    print("List: ", jsonResponse['payload']['mongoClusterLst'] )
    return jsonResponse['payload']['mongoClusterLst']


'''
Reload Repo List
'''
def reloadMongoRepo(user, session):
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/mongo-repo:reload"
    response =   requests.post(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    print("ret: ", jsonResponse)
    return jsonResponse['payload']['mongoClusterLst']


'''
Get saved request
'''
def getRequest(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/restapi/request:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Save request
'''
def saveRequest(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/restapi/request:save"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get all user saved requests
'''
def getAllRequests(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/restapi/requests:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Bridges to scripts for node
'''
def nodeBridgeToScriptForNode(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/bridge/node:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Bridges of script for nodes
'''
def nodeBridgeToScriptForScript(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/bridge/script:get"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add Bridge To Script association to Repository
'''
def machineNodeBridgeToScriptAdd(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/bridge:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete Bridge To Script association
'''
def machineNodeBridgeToScriptDelete(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/bridge:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add Interpreter with associated information
'''
def interpreterAdd(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/interpreter:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete Interpreter
'''
def interpreterDelete(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/interpreter:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get List of Interpreters
'''
def listAllInterpreters(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/interpreter:list"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get A List Of Interpreter Versions providing a filter for the name
'''
def searchInterpreter(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/interpreter:search"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Update Interpreter
'''
def interpreterUpdate(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/interpreter:update"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Run/Execute Script Version
'''
def runAdhocScript(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/script/adhoc:run"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Add Script Param for a corresponding Script
'''
def scriptParamAdd(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/script/param:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete Script Param
'''
def scriptParamDelete(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/script/param:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Script Parameters in either form, detailed or for execution, or both
'''
def getScriptParam(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/script/param:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Run/Execute Script Version
'''
def runScript(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/script/repo:run"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


def loopbackScriptDataHeader(user:str, session:str, requestId, tableDefinition: TableDefinition):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['requestId'] = requestId
   api_url = api_url_stem + "/scripting/loopback/data:header"
   response =   requests.post(api_url, data=JsonEncoder().encode(tableDefinition), headers=headers)
   return response.json()['errorCode']



def loopbackScriptDataDetail(user:str, session:str, requestId, rowValue:RowValue):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['requestId'] = requestId
   api_url = api_url_stem + "/scripting/loopback/data:detail"
   response =   requests.post(api_url, data=JsonEncoder().encode(rowValue), headers=headers)
   return response.json()['errorCode']




def loopbackScriptDataFooter(user:str, session:str, requestId, rowValue:RowValue):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['requestId'] = requestId
   api_url = api_url_stem + "/scripting/loopback/data:footer"
   response =   requests.post(api_url, data=JsonEncoder().encode(rowValue), headers=headers)
   return response.json()['errorCode']






'''
Run/Execute Script Version with Streaming
'''
def streamAdhocScript(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/script/streaming/adhoc:run"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Add Script to Repository
'''
def addScript(user:str, session:str, path:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/script:add"
   f = open(path, "r")
   form_data = {'attacment': f}
   response =   requests.put(api_url, data=form_data, headers=headers)
   return response.json()['errorCode']


'''
Get Script and versions, scriptName can also be only part of name to wider range for searching
'''
def getScriptContent(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/script:content"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Specific Script Information
'''
def getScript(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/script:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Remove Script and All its verions from Repository
'''
def scriptRemove(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/script:remove"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Script and versions, scriptName can also be only part of name to wider range for searching
'''
def scriptSearch(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/script:search"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Scripts, specific to a user
'''
def userScripts(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/scripting/script:user"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Sql Repo List Without Params
'''
def getSqlRepoListWithNoParams(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlRepo/sql"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Sql Repo List
'''
def getSqlRepoList(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Copy records to Rdbms table from Elastic DSL query
'''
def copyElasticDslResultToRdbmsTable(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/copy/elastic:dsl"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Create/add records to collection from Elastic SQL query
'''
def copyElasticSqlResultToRdbmsTable(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/copy/elastic:sql"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Copy records from Embedded Sql to RDBMS table
'''
def copyEmbeddedSqlResultToRdbmsTable(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/copy/embedded/adhoc:sql"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Copy records to RDBMS table from another Mongodb collection(s) range search
'''
def copyMongoRangeSearchResultToRdbmsTable(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/copy/mongodb/search:range"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Copy records from Mongodb simple search to RDBMS table
'''
def copyMongoSimpleSearchResultToRdbmsTable(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/copy/mongodb/search:simple"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Copy records to RDBMS table from Mongodb ad-hoc search
'''
def copyMongoAdhocResultToRdbmsTable(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/copy/mongodb:adhoc"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Copy records to RDBMS table from full Mongodb collection
'''
def copyMongoFullCollectionToRdbmsTable(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/copy/mongodb:collection"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Copy Csv to table
'''
def copyCsvToRdbmsTable(user:str, session:str, path:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/copy/sqlrepo/csv:load"
   f = open(path, "r")
   form_data = {'attacment': f}
   response =   requests.put(api_url, data=form_data, headers=headers)
   return response.json()['errorCode']


'''
Copy Rdbms Sql result records to another Rdbms System Table
'''
def copyRdbmsSqlResultToRdbmsTable(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/copy/sqlrepo:sql"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Add a new database connection to the list of available Databases/schema connections
'''
def addDatabase(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/database/add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Validate an existing Database/schema connection
'''
def validateSqlRepoDatabase(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/database/connection/validate:connection"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Validate a new Database/schema connection
'''
def validateDatabase(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/database/connection/validate:new-connection"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Delete database/schema connection
'''
def databaseDelete(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/database/delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get the List of all user tables in database schema
'''
def generateCreateScriptForTable(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/database/generate:script"
   response =   requests.put(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Get the List of database schemas
'''
def getDatabaseSchemas(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/database/schemas"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get the List of all user tables in database schema
'''
def getDatabaseTables(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/database/tables"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add a new database connection to the list of available Databases/schema connections
'''
def updateDatabase(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/database/update"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get the List of available Databases
'''
def getDatabase(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/databases"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Execute Adhoc Sql
'''
def executeAdhocSql(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session

   api_url = api_url_stem + "/sqlrepo/execute/adhoc"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Execute Sql On multiple DBs and aggregate results
'''
def executeSqlAdhocMultiple(user:str, session:str, sqlContent):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/execute/adhoc/multipledb:aggregate"
   response =   requests.put(api_url, data=sqlContent, headers=headers)
   return response.json()['errorCode']


'''
Execute Sql On multiple DBs and aggregate results
'''
def executeSqlRepoMultiple(user:str, session:str, sqlContent):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/execute:multipleDb"
   response =   requests.post(api_url, data=sqlContent, headers=headers)
   return response.json()['errorCode']


'''
Execute Sql Repo
'''
def executeSqlRepo(user:str, session:str, fullBody):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/execute:singleDb"
   response =   requests.post(api_url, data=fullBody, headers=headers)
   return response.json()['errorCode']


'''
Get Execution History for Current User
'''
def getRdbmsHistory(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/history/user:personal"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Create and Insert table from Sql Repo execution
'''
def executeSqlRepoToMigrateData(user:str, session:str, sqlContent):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/migrate"
   response =   requests.post(api_url, data=sqlContent, headers=headers)
   return response.json()['errorCode']


'''
Get Sql Param List for Bulk DML. DQLs and DDLs are excluded
'''
def getSqlParamListBulk(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/param/bulk"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Sql Param List Detail
'''
def getSqlParamListDetail(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/param/detail"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Sql Param List
'''
def getSqlParamList(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/params"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Reload Repo List
'''
def reloadSqlRepo(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/reload"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Delete snapshots
'''
def deleteSnapshot(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/snapshot:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get snapshot to visualize
'''
def getRdbmsSnapshot(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/snapshot:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get a list of snapshots to visualize
'''
def getRdbmsSnapshotHistory(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/snapshot:history"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Sql Detail
'''
def getSqlDetail(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/sql/detail"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get Sql Repo List Summary Format
'''
def getSqlRepoListSummary(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/sql/summary"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add a new Sql statement to the repo
'''
def addSql(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/sql:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add a new Sql statement to the repo
'''
def deleteSql(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/sql:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Update Sql statement to the repo
'''
def updateSql(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/sql:update"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete mapping of sql statement to multiple databases
'''
def deleteSqlToDbMapping(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/sqlToDb/mapping:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get mapping of sql statements to databases
'''
def listSqlToDbMapping(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/sqlToDb/mapping:list"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Map a sql statement to multiple databases
'''
def addSqlToDbMapping(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/sqlToDb/mapping:update"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Add Sql Param to Sql Statement
'''
def addSqlParam(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/sqlparam:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete Sql Param
'''
def deleteSqlParam(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/sqlparam:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Validate Sql
'''
def validateSql(user:str, session:str, sqlContent):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/sqlrepo/validate:sql"
   response =   requests.put(api_url, data=sqlContent, headers=headers)
   return response.json()['errorCode']


'''
Get Users
'''
def getRegisteringUsers(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/users/:registering"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add a new department
'''
def addDepartment(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/users/department:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete Department
'''
def deleteDepartmentE(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/users/department:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get a specific Department, from an id
'''
def getDepartment(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/users/department:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get a specific title, based on title
'''
def getDepartmentByName(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/users/department:search"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add a new department
'''
def updateDepartment(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/users/department:update"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get Departments
'''
def getDepartments(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/users/departments:query"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add a new title
'''
def addTitle(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/users/title:add"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete Title
'''
def deleteTitle(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/users/title:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get a specific title, based on an id
'''
def getTitleById(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/users/title:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get a specific title, based on title
'''
def getTitleByName(user:str, session:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   api_url = api_url_stem + "/users/title:search"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Update title
'''
def updateTitle(user:str, session:str, titleId:int, newTitle:str, newTitleDescription:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['id'] = titleId
   headers['newTitle'] = newTitle
   headers['newTitleDescription'] = newTitleDescription

   api_url = api_url_stem + "/users/title:update"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get Titles
'''
def getTitles(user:str, session:str, paternToSearch:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['paternToSearch'] = paternToSearch
   api_url = api_url_stem + "/users/titles:query"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Add a new user
'''
def addUser(user:str, session:str, newUser:str, newUserPassword:str, newUserType:str, newUserFirstName:str, newUserLastName:str, newUserEmail:str, 
               newUserDepartment:int, newUserTitle:int, newUserManager:int, newUserCharacteristic:str, newUserDescription:str, newUserActive:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
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
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Approve a registering used
'''
def approveRegisteringUser(user:str, session:str, newUser:str, departmentId:int, titleId:int, managerId:int, characteristic:str, descrption:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['newUser'] = newUser
   headers['departmentId'] = departmentId
   headers['titleId'] = titleId
   headers['managerId'] = managerId
   headers['characteristic'] = characteristic
   headers['descrption'] = descrption

   api_url = api_url_stem + "/users/user:approve"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Delete User
'''
def deleteUser(user:str, session:str, userId:int):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['id'] = userId

   api_url = api_url_stem + "/users/user:delete"
   response =   requests.delete(api_url, headers=headers)
   return response.json()['errorCode']


'''
Get specific User based on id
'''
def getUser(user:str, session:str, paternToSearch:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['paternToSearch'] = paternToSearch

   api_url = api_url_stem + "/users/user:get"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']['payload']['listOfUsers']


'''
Authenticate user and generate session on success
'''
def login(user, password):
    print("Logging in...")
    headers = {}
    headers['user'] = user
    headers['password'] = password
    body = {}
    api_url = api_url_stem + "/users/user:login"
    response =   requests.post(api_url, data={}, headers=headers)
    if response.status_code == 200:
        jsonResponse = response.json()
        
        return jsonResponse['payload']['session']
    else:
        return None



'''
Logout
'''
def logout(user, session):
    print("logging out...")
    headers = {}
    headers['user'] = user
    headers['session'] = session
    api_url = api_url_stem + "/users/user:logout"
    response =   requests.post(api_url, data={}, headers=headers)
    jsonResponse = response.json()
    print("OK?: ", jsonResponse['payload']['genericPayload'] )
    return jsonResponse['payload']['genericPayload']


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
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Reject a registering used
'''
def rejectRegisteringUser(user:str, session:str, newUser):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['newUser'] = newUser

   api_url = api_url_stem + "/users/user:reject"
   response =   requests.put(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Update user
'''
def updateUser(user:str, session:str, userId:int, newUser:str, newUserPassword:str, newUserType:str, newUserFirstName:str, newUserLastName:str, newUserEmail:str, 
               newUserDepartment:int, newUserTitle:int, newUserManager:int, newUserCharacteristic:str, newUserDescription:str, newUserActive:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['id'] = userId
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
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Update my first name and last name
'''
def updateMyEmailAndUserName(user:str, session:str, userId:int, userName, email):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['id'] = userId
   headers['userName'] = userName
   headers['email'] = email
   api_url = api_url_stem + "/users/user:update-my-email"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Update my first name and last name
'''
def updateMyNames(user:str, session:str, userId:int, newUserFirstName:str, newUserLastName:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['id'] = userId
   headers['newUserFirstName'] = newUserFirstName
   headers['newUserLastName'] = newUserLastName

   api_url = api_url_stem + "/users/user:update-my-names"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Update my password
'''
def updateMyPassword(user:str, session:str, userId:str, password:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['userId'] = userId
   headers['password'] = password

   api_url = api_url_stem + "/users/user:update-my-password"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']


'''
Get Users
'''
def getUsers(user:str, session:str, paternToSearch:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['paternToSearch'] = paternToSearch

   api_url = api_url_stem + "/users:query"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']


'''
Generates a synthetic session for debug only in DEV and QA environment.
'''
def generateSyntheticSession(admin, password):
   headers = {}
   headers['admin'] = admin
   headers['password'] = password
   api_url = api_url_stem + "/users/generateSyntheticSession"
   response =   requests.get(api_url, headers=headers)
   return response.json()['errorCode']
   
   
'''
 ################################ Chat API ###########################################################
'''

'''
Update my first name and last name
'''
def updateMyNames(user:str, session:str, userId:int, newUserFirstName:str, newUserLastName:str):
   headers = {}
   headers['user'] = user
   headers['session'] = session
   headers['id'] = userId
   headers['newUserFirstName'] = newUserFirstName
   headers['newUserLastName'] = newUserLastName

   api_url = api_url_stem + "/fromUser/toUser/text:send"
   response =   requests.post(api_url, data={}, headers=headers)
   return response.json()['errorCode']





