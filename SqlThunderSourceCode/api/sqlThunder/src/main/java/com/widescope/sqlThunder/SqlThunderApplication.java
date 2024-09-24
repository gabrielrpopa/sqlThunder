package com.widescope.sqlThunder;

import com.widescope.chat.db.ChatDb;
import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.logging.AppLogger;
import com.widescope.logging.repo.H2LogRepoDb;
import com.widescope.license.License;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticClusterDb;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticExecutedQueriesRepoDb;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.EmbeddedRepoGenericSql;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRepo;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotElasticDbRepo;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotMongoDbRepo;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterDb;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoExecutedQueriesRepoDb;
import com.widescope.rdbmsRepo.database.mongodb.sql.toH2.MongoToH2SqlRepo;
import com.widescope.rdbmsRepo.database.mongodb.sql.toH2.MongoToRdbmsSqlRepo;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoStorageDb;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.rdbmsRepo.database.warehouse.repo.WarehouseRepoDb;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRepo;
import com.widescope.restApi.repo.RestApiDb;
import com.widescope.scripting.db.ScriptingInternalDb;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDbRecord;
import com.widescope.sqlThunder.tcpServer.TCPServer;
import com.widescope.sqlThunder.utils.Ip4NetUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.InternalUserDb;
import com.widescope.sqlThunder.utils.user.DataWhalesJson;
import com.widescope.storage.dataExchangeRepo.ExchangeDb;
import com.widescope.storage.internalRepo.InternalStorageRepoDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * To change default dbs: SqlRepoStorageDb, MongoClusterDb, ElasticClusterDb
 * How to execute:
 * 1) Build: gradlew clean build
 * 2) Run: gradlew bootRun or java -jar build/libs/sqlThunder.war com.widescope.sqlThunder
*/
@SpringBootApplication(
		scanBasePackages = {
							"com.widescope.rdbmsRepo",
							"com.widescope.scripting",
							"com.widescope.sqlThunder",
							"com.widescope.restApi",
							"com.widescope.logging",
							"com.widescope.storage",
							"com.widescope.cluster",
							"com.widescope.chat"
							}
)
@EnableAutoConfiguration(exclude= {	MongoAutoConfiguration.class, ElasticsearchRestClientAutoConfiguration.class})
public class SqlThunderApplication {

	@Autowired
	private AppConstants appConstants;

	@Autowired
	private AuthUtil authUtil;

	@Autowired
	private TCPServer tcpServer;


	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SqlThunderApplication.class, args);
		context.getBean(SqlThunderApplication.class).initConfiguration();
	}


	private void populateRepos() {

		updateStaticApplicationProperties();
		try {
			String fileName = "./exchangeRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					ConfigRepoDbRecord owner =  ConfigRepoDb.getConfigVar("owner");
					ExchangeDb.generateSchema(owner.getConfigValue());
                	AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "ExchangeDb created ");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}

			fileName = "./chatDb.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					ChatDb.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "ChatDb created");
 				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}

			fileName = "./cluster.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					ClusterDb.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "ClusterDb created ");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}



			fileName = "./storageRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					InternalStorageRepoDb.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "StorageRepoDb created");
      			} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			fileName = "./warehouseRepoDb.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					WarehouseRepoDb.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "WarehouseRepoDb created");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			fileName = "./mongoRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					MongoClusterDb.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "MongoClusterDb created");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			MongoClusterDb mongoClusterDb = new MongoClusterDb();
			SqlRepoUtils.mongoDbMap = mongoClusterDb.getAllCluster();


			fileName = "./elasticRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					ElasticClusterDb.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "ElasticClusterDb created");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			ElasticClusterDb elasticClusterDb = new ElasticClusterDb();
			SqlRepoUtils.elasticDbMap = elasticClusterDb.getElasticClusters();


			fileName = "./userRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					InternalUserDb.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "InternalUserDb created");
      			} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}


			fileName = "./scriptRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					ScriptingInternalDb.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "ScriptingInternalDb created");
    			} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			fileName = "./restApiRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					RestApiDb.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "RestApiDb created");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			fileName = "./mongoToH2SqlRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					MongoToH2SqlRepo.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "MongoToH2SqlRepo created");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			fileName = "./mongoToRdbmsSqlRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					MongoToRdbmsSqlRepo.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "MongoToRdbmsSqlRepo");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			fileName = "./embeddedDbRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					EmbeddedDbRepo.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "EmbeddedDbRepo created");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			fileName = "./snapshotsDbRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					SnapshotDbRepo.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "SnapshotDbRepo created");
     			} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			fileName = "./snapshotsMongoDbRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					SnapshotMongoDbRepo.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "SnapshotMongoDbRepo created");
        		} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			fileName = "./snapshotsElasticDbRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					SnapshotElasticDbRepo.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "SnapshotElasticDbRepo created");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			fileName = "./embeddedRepoGenericSql.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					EmbeddedRepoGenericSql.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "EmbeddedRepoGenericSql created");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			fileName = "./logRepo.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					H2LogRepoDb.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "H2LogRepoDb created");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}

			fileName = "./elasticExecutedQueriesRepoDb.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					ElasticExecutedQueriesRepoDb.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "ElasticExecutedQueriesRepoDb created");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			fileName = "./mongoExecutedQueriesRepoDb.mv.db";
			if(!FileUtilWrapper.isFilePresent(fileName)) {
				try {
					MongoExecutedQueriesRepoDb.generateSchema();
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "MongoExecutedQueriesRepoDb created");
				} catch(Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
				}
			}
			AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "Embedded DB creation complete");

			try {
				SqlRepoUtils.populateRepo(appConstants.getActiveRepo());


				fileName = "./dataWhaleDb.json";
				if(FileUtilWrapper.isFilePresent(fileName)) {
					try {
						DataWhalesJson jsonUserWhales = DataWhalesJson.loadDataWhalesJson(fileName);
						InternalUserDb.dataWhales.loadWhaleRef(jsonUserWhales,
																appConstants.getSuperUser(),
																appConstants.getSuperPasscode(),
																appConstants.getTestUser(),
																appConstants.getTestPasscode() );
						AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "UserWhales created");
          			} catch(Exception e) {
						AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
					}
				} else {
					DataWhalesJson d = new DataWhalesJson();
					d.getSqlDbRefsList().add(SqlRepoStorageDb.schema_unique_user_name);
					d.getMongoClusterList().add("localMongoDb");
					String r = d.toString();
					FileUtilWrapper.writeFile(fileName, r.getBytes());
				}


			} catch (Exception e) {
				AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}
		} catch (Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
		}
	}


	private void createFolders() throws Exception {
		try {
			FileUtilWrapper.createRecursiveFolder(appConstants.getScriptStoragePath()) ;
			FileUtilWrapper.createRecursiveFolder(appConstants.getScriptExecutionPath()) ;
			FileUtilWrapper.createRecursiveFolder(appConstants.getScriptResultPath()) ;
			FileUtilWrapper.createRecursiveFolder(appConstants.getScriptLogPath()) ;
			FileUtilWrapper.createRecursiveFolder(appConstants.getScriptTempPath()) ;
			FileUtilWrapper.createRecursiveFolder(appConstants.getScriptLibPath()) ;
			FileUtilWrapper.createRecursiveFolder(appConstants.getScriptModelPath()) ;
		} catch (Exception e) {
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
		}
	}



	public void startInitLogStorage() throws Exception {
		try {
			FileUtilWrapper.createRecursiveFolder("./storage/log") ;
			FileUtilWrapper.createRecursiveFolder("./storage/ref") ;
			FileUtilWrapper.createRecursiveFolder("./storage/artifact") ;
		} catch (Exception ex) {
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl)) ;
		}
	}

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) throws Exception {
		List<String> endPoints = new ArrayList<>();
		ApplicationContext applicationContext = event.getApplicationContext();
		RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
				.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
				.getHandlerMethods();
		map.forEach((key, value) -> {
					if(key.getDirectPaths().toArray().length == 1) {
						System.out.println(key.getDirectPaths().toArray()[0].toString());
						endPoints.add(key.getDirectPaths().toArray()[0].toString());
					}
				}
		);



		String  fileName = "./configRepo.mv.db";
		if(!FileUtilWrapper.isFilePresent(fileName)) {
			try {
				ConfigRepoDb.generateSchema(endPoints);
			} catch(Exception e) {
				AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}
		}

		ConfigRepoDb.loadConfigInMem();
	}


	private void initConfiguration() {
        Ip4NetUtils.updateLocalIpAddresses();
		try	{
			createFolders();
			AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "createFolders Completed successfully");
			populateRepos();
			AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "populateRepos Completed successfully");
			AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "IPAddress: " + InetAddress.getLocalHost().getHostAddress() + ", Port: " + appConstants.getServerPort());
			String ip = InetAddress.getLocalHost().getHostAddress();
			if(appConstants.getServerSslEnabled())
				ClusterDb.ownBaseUrl = "https://" + ip + ":" + appConstants.getServerPort() + appConstants.getServerServletContextPath();
			else
				ClusterDb.ownBaseUrl = "http://" + ip + ":" + appConstants.getServerPort() + appConstants.getServerServletContextPath();

			FileUtilWrapper.readMachineNodeListFile();
			startInitLogStorage();
			AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "Init Config Completed successfully");
			new MaintenanceThread(	appConstants.getInstanceType(),
									appConstants.getLoggingThreadSleep(),
									Integer.parseInt(appConstants.getServerPort())).start();
			new LoggingThread(appConstants.getLoggingThreadSleep()).start();
			new TCPServerThread(appConstants, tcpServer).start();
			AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, License.printLicense());
			if( appConstants.getIsSwagger() ) {
				try {
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "SWAGGER API documentation Launched");
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, ClusterDb.ownBaseUrl + "/swagger-ui/index.html");
					AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, ClusterDb.ownBaseUrl + "/v3/api-docs" );
				} catch (Exception e) {
					AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.main);
				}
			} else {
				AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "This SqlThunder instance has disabled swagger UI in order to run websockets");
			}



		} catch(Exception e)	{
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.main);
		}
	}


	void updateStaticApplicationProperties () {
		StaticApplicationProperties.staticIp = appConstants.getServerIpStatic();
		StaticApplicationProperties.localHttpPort = appConstants.getServerPort();
		StaticApplicationProperties.applicationOwner = appConstants.getApplicationOwner();
		StaticApplicationProperties.userSessionCacheLocation = appConstants.getUserSessionCacheLocation();
	}
}
