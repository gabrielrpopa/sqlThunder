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
import com.widescope.sqlThunder.service.ResourceService;
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

	@Autowired
	private ResourceService resourceService;

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SqlThunderApplication.class, args);
		context.getBean(SqlThunderApplication.class).initConfiguration();
	}


	private void populateRepos() {
		try {
			updateStaticApplicationProperties();
			MongoClusterDb mongoClusterDb = new MongoClusterDb();
			SqlRepoUtils.mongoDbMap = mongoClusterDb.getAllCluster();
			ElasticClusterDb elasticClusterDb = new ElasticClusterDb();
			SqlRepoUtils.elasticDbMap = elasticClusterDb.getElasticClusters();
			SqlRepoUtils.populateRepo(appConstants.getActiveRepo());
			resourceService.loadResourceFiles();
			resourceService.generateEmbeddedSchemas(appConstants);
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
