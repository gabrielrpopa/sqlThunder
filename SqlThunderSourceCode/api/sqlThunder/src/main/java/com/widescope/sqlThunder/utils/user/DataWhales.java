package com.widescope.sqlThunder.utils.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.DbUtil;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rdbmsRepo.database.elasticsearch.lowApi.GeneralInfo;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticCluster;
import com.widescope.rdbmsRepo.database.mongodb.MongoDbTransaction;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterDbList;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DataWhales {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

    private ConcurrentMap<String, SqlRepoDatabase> sqlDbRefsList;
    private ConcurrentMap<String,MongoClusterRecord> mongoClusterList;
    private ConcurrentMap<String,ElasticCluster> elasticClusterList;


    public DataWhales() {
        this.mongoClusterList = new ConcurrentHashMap<>();
        this.sqlDbRefsList = new ConcurrentHashMap<>();
        this.elasticClusterList = new ConcurrentHashMap<>();
    }




    @Override
    public String toString() {
        try	{
            Gson gson = new Gson();
            return gson.toJson(this);
        }
        catch(Exception ex) {
            return null;
        }
    }

    public String toStringPretty() {
        try	{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        }
        catch(Exception ex) {
            return null;
        }
    }


    public ConcurrentMap<String, SqlRepoDatabase> getSqlDbRefsList() {
        return sqlDbRefsList;
    }
    public void addSqlDbRefs(final SqlRepoDatabase sqlDbRef) { this.sqlDbRefsList.put( sqlDbRef.getSchemaUniqueUserName(), sqlDbRef); }
    public void setSqlDbRefsList(ConcurrentMap<String, SqlRepoDatabase> sqlDbRefsList) { this.sqlDbRefsList = sqlDbRefsList; }

    public ConcurrentMap<String, MongoClusterRecord> getMongoClusterList() {
        return mongoClusterList;
    }
    public void setMongoClusterList(ConcurrentMap<String, MongoClusterRecord> mongoClusterList) {
        this.mongoClusterList = mongoClusterList;
    }
    public void addMongoCluster(final MongoClusterRecord mongoCluster) {
        this.mongoClusterList.put(mongoCluster.getUniqueName(), mongoCluster);
    }


    public ConcurrentMap<String, ElasticCluster> getElasticClusterList() {
        return elasticClusterList;
    }
    public void setElasticClusterList(ConcurrentMap<String, ElasticCluster> elasticClusterList) { this.elasticClusterList = elasticClusterList; }
    public void addElasticCluster(final ElasticCluster elasticCluster) {
        this.elasticClusterList.put(elasticCluster.getUniqueName(),  elasticCluster);
    }

    public void loadWhaleRef(final DataWhalesJson chatWalesJson, String superUser, String superUserPassword, String testUser, String testUserPassword) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        for (String name: chatWalesJson.getSqlDbRefsList()) {
            if(!name.isEmpty() && SqlRepoUtils.sqlRepoDatabaseMap.containsKey(name)) {
                try {
                    DbConnectionInfo dbConn = DbConnectionInfo.makeDbConnectionInfo(SqlRepoUtils.sqlRepoDatabaseMap.get(name));
                    if( DbUtil.checkConnection(dbConn) ) {
                       sqlDbRefsList.put(name, SqlRepoUtils.sqlRepoDatabaseMap.get(name));
                       InternalUsersPersistenceRef.generateSchema(dbConn, superUser, superUserPassword, testUser, testUserPassword);
                    }

                } catch (SQLException e)	{
                    AppLogger.logDb(e, className, methodName);
                } catch (Exception e) {
                    AppLogger.logException(e, className, methodName, AppLogger.db);
                }
            }
        }

        for (String name: chatWalesJson.getMongoClusterList()) {
            if(!name.isEmpty()) {
                if( SqlRepoUtils.mongoDbMap.containsKey(name) ) {
                    try {
                        MongoClusterDbList mongoClusterDbList = MongoDbTransaction.getMongoDatabaseList( name);
                        if(!mongoClusterDbList.getMongoClusterDbLst().isEmpty()) {
                            mongoClusterList.put(name, SqlRepoUtils.mongoDbMap.get(name));
                        }
                    } catch (SQLException e)	{
                        AppLogger.logDb(e, className, methodName);
                    } catch (Exception e) {
                        AppLogger.logException(e, className, methodName, AppLogger.db);
                    }
                }
            }
        }

        for (String name: chatWalesJson.getElasticClusterList()) {
            if(!name.isEmpty()) {
                if( SqlRepoUtils.elasticDbMap.containsKey(name) ) {
                    try {
                        if(GeneralInfo.ping(name) )
                            elasticClusterList.put(name, SqlRepoUtils.elasticDbMap.get(name));
                    } catch (Exception e) {
                        AppLogger.logException(e, className, methodName, AppLogger.db);
                    }
                }
            }
        }
    }

}
