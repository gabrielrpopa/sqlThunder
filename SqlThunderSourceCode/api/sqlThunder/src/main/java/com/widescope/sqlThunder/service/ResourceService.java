package com.widescope.sqlThunder.service;


import com.widescope.chat.db.ChatDb;
import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.logging.AppLogger;
import com.widescope.logging.repo.H2LogRepoDb;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticClusterDb;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticExecutedQueriesRepoDb;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.EmbeddedRepoGenericSql;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.EmbeddedDbRepo;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotDbRepo;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotElasticDbRepo;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.SnapshotMongoDbRepo;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterDb;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoExecutedQueriesRepoDb;
import com.widescope.rdbmsRepo.database.mongodb.sql.toH2.MongoToH2SqlRepo;
import com.widescope.rdbmsRepo.database.mongodb.sql.toH2.MongoToRdbmsSqlRepo;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoStorageDb;
import com.widescope.rdbmsRepo.database.warehouse.repo.WarehouseRepoDb;
import com.widescope.restApi.repo.RestApiDb;
import com.widescope.scripting.db.ScriptingInternalDb;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDbRecord;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.user.DataWhalesJson;
import com.widescope.sqlThunder.utils.user.InternalUserDb;
import com.widescope.storage.dataExchangeRepo.ExchangeDb;
import com.widescope.storage.internalRepo.InternalStorageRepoDb;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


@Service
public class ResourceService {

    public static String sqlThunderRestClient;



    @Autowired
    private ResourceLoader resourceLoader;

    public String readFile(String relativePath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + relativePath);
        InputStream inputStream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        return stringBuilder.toString();
    }

    public void loadResourceFiles() throws IOException {
        String sqlThunderRestClientFile = "libs/python/python3/sqlThunderRestClient.py";
        ResourceService.sqlThunderRestClient = readFile(sqlThunderRestClientFile);
    }

    public void generateEmbeddedSchemas(final AppConstants appConstants) throws IOException {
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

        fileName = "./elasticRepo.mv.db";
        if(!FileUtilWrapper.isFilePresent(fileName)) {
            try {
                ElasticClusterDb.generateSchema();
                AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "ElasticClusterDb created");
            } catch(Exception e) {
                AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            }
        }

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


        AppLogger.logInfo(Thread.currentThread().getStackTrace()[1], AppLogger.main, "Embedded DB creation complete");

    }

}
