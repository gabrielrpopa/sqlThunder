package com.widescope.persistence;

import com.widescope.logging.AppLogger;
import com.widescope.persistence.execution.*;
import com.widescope.rdbmsRepo.ExecutedStatement;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticExecutedQueriesRepoDb;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticExecutedQuery;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticExecutedQueryList;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticResultSet;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.RdbmsExecutedQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.repo.RdbmsExecutedQueryList;
import com.widescope.rdbmsRepo.database.mongodb.MongoResultSet;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoExecutedQueriesRepoDb;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoExecutedQuery;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoExecutedQueryList;
import com.widescope.rdbmsRepo.database.rdbmsRepository.RdbmsExecutedQueriesRepoDb;
import com.widescope.rdbmsRepo.database.tempSqlRepo.HistFileManagement;
import com.widescope.scripting.ScriptOutputDetail;
import com.widescope.scripting.db.ScriptExecutedRecord;
import com.widescope.scripting.db.ScriptExecutedRecordList;
import com.widescope.scripting.db.ScriptExecutedRepoDb;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.User;
import com.widescope.storage.internalRepo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PersistenceWrap {

    @Autowired
    private AuthUtil authUtil;

    /*Script Execution History Database*/
    @Autowired
    private ScriptExecutedRepoDb execScriptDb;

    /*RDBMS Statement Execution History Database*/
    @Autowired
    private RdbmsExecutedQueriesRepoDb execRdbmsDb;

    /*Mongo Statement Execution History Database*/
    @Autowired
    private MongoExecutedQueriesRepoDb execMongoDb;

    /*Elastic Statement Execution History Database*/
    @Autowired
    private ElasticExecutedQueriesRepoDb execElasticDb;

    @Autowired
    private InternalStorageRepoDb storageDb;

    @Autowired
    private HistFileManagement histFManagement;

    public PersistenceWrap() throws Exception {

    }


    private ScriptExecutedRecord
    saveScriptExecution(ScriptExecutedRecord rec, Object o, String persist)  {
        try {
            if( persist.compareToIgnoreCase("Y") == 0 && o != null) {
                String p = histFManagement.addNewArtifact(rec, o);
                rec.setRepPath(p);
            }
            execScriptDb.addExecutedScript(rec);
            rec.setFlag(ExecutedStatementFlag.operationSuccess);
            return execScriptDb.identifyScript(rec.getRequestId(), rec.getTimestamp());
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            rec.setFlag(ExecutedStatementFlag.errorAddStatement);
            return rec;
        }

    }


    private ElasticExecutedQuery
    saveElasticExecution(ElasticExecutedQuery rec, Object o, String persist)  {
        try {
            if( persist.compareToIgnoreCase("Y") == 0 && o != null) {
                rec.setRepPath(histFManagement.addNewArtifact(rec, o));
                execElasticDb.addFullExecutedStatement(rec);
                rec.setFlag(ExecutedStatementFlag.operationSuccess);
                return execElasticDb.identifyStatement(rec.getRequestId(), rec.getTimestamp());
            } else {
                rec.setFlag(ExecutedStatementFlag.operationIgnored);
                return rec;
            }

        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            rec.setFlag(ExecutedStatementFlag.errorAddStatement);
            return rec;
        }


    }


    private RdbmsExecutedQuery
    saveRdbmsExecution(RdbmsExecutedQuery rec, Object o, String persist)  {
        try {
            if( persist.compareToIgnoreCase("Y") == 0 && o != null) {
                rec.setRepPath(histFManagement.addNewArtifact(rec, o));
            }
            execRdbmsDb.addExecutedStatement(rec);
            rec.setFlag(ExecutedStatementFlag.operationSuccess);
            return execRdbmsDb.identifyStatement(rec.getRequestId(), rec.getTimestamp());
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            rec.setFlag(ExecutedStatementFlag.errorAddStatement);
            return rec;
        }
    }

    private MongoExecutedQuery
    saveMongoExecution(MongoExecutedQuery rec, Object o, String persist)  {
        try {
            if( persist.compareToIgnoreCase("Y") == 0 && o != null) {
                rec.setRepPath(histFManagement.addNewArtifact(rec, o));
            }
            execMongoDb.addExecutedStatement(rec);
            rec.setFlag(ExecutedStatementFlag.operationSuccess);
            return execMongoDb.identifyStatement(rec.getRequestId(), rec.getTimestamp());
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            rec.setFlag(ExecutedStatementFlag.errorAddStatement);
            return rec;
        }
    }


    public RestInterface saveExecution(RestInterface rec, Object o, String persist)  {
        if(isStatementName(rec) ) {
            rec = setNewStatementName(rec); /*rename it if exists, by adding a trailing string to original name*/
        }
        if(rec instanceof MongoExecutedQuery) {
            return saveMongoExecution((MongoExecutedQuery)rec, o, persist);
        } else if(rec instanceof RdbmsExecutedQuery) {
            return saveRdbmsExecution((RdbmsExecutedQuery) rec, o, persist);
        } else if(rec instanceof ElasticExecutedQuery) {
            return saveElasticExecution((ElasticExecutedQuery) rec, o, persist);
        } else if(rec instanceof ScriptExecutedRecord) {
            return saveScriptExecution((ScriptExecutedRecord)rec, o, persist);
        } else {
            return null;
        }
    }

    public boolean isStatementName(RestInterface rec)  {
        try {
            if(rec instanceof MongoExecutedQuery) {
                return execMongoDb.isStatementName(((MongoExecutedQuery)rec).getStatementName());
            } else if(rec instanceof RdbmsExecutedQuery) {
                return execRdbmsDb.isStatementName(((RdbmsExecutedQuery) rec).getStatementName());
            } else if(rec instanceof ElasticExecutedQuery) {
                return execElasticDb.isStatementName(((ElasticExecutedQuery) rec).getStatementName());
            } else if(rec instanceof ScriptExecutedRecord) {
                return execScriptDb.isScriptName(((ScriptExecutedRecord) rec).getScriptName());
            } else {
                return false;
            }
        } catch(Exception ex) {
            return true;
        }

    }


    public RestInterface setNewStatementName(RestInterface rec)  {
        String newName = "-" + StringUtils.generateUniqueString(6);
        if(rec instanceof MongoExecutedQuery) {
            ((MongoExecutedQuery)rec).setStatementName(((MongoExecutedQuery)rec).getStatementName() + newName);
        } else if(rec instanceof RdbmsExecutedQuery) {
            ((RdbmsExecutedQuery)rec).setStatementName(((RdbmsExecutedQuery)rec).getStatementName() + newName);
        } else if(rec instanceof ElasticExecutedQuery) {
            ((ElasticExecutedQuery)rec).setStatementName(((ElasticExecutedQuery)rec).getStatementName() + newName);
        } else if(rec instanceof ScriptExecutedRecord) {
            ((ScriptExecutedRecord)rec).setScriptName(((ScriptExecutedRecord)rec).getScriptName() + newName);
        }
        return rec;
    }


    /**
     * Only the owner can delete the past execution
     * @param id - record id
     * @param user - user/owner
     * @param force - force deletion when another user(s) is assigned/associated to this execution
     * @return - ScriptExecutedRecord
     */
    public ScriptExecutedRecord deleteExecutedScriptAndLogs(final long id, final String user, final boolean force) {
        ScriptExecutedRecord rec = new ScriptExecutedRecord(id);
        try {
            User u = authUtil.getUser(user);
            rec = execScriptDb.getScriptById(id);
            final long cnt = ExecutionUserAccess.countArtifactAccess(id, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
            if(cnt > 1 && force) {
                /*Delete access records first*/
                ExecutionUserAccess.deleteArtifactAccess(id, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
                execScriptDb.deleteScriptByIdAndUser(id, u.getId()); /*Delete execution record by enforcing user*/
                histFManagement.deleteArtifact(rec);  /*Delete output*/
                rec.setFlag(ExecutedStatementFlag.executionRecordDeleted);
            } else {
                rec.setFlag(ExecutedStatementFlag.notAllowedDeleteStatement);
            }


        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            rec.setFlag(ExecutedStatementFlag.unknownError);
        }

        return rec;
    }



    public MongoExecutedQuery
    deleteMongoExecution(final  long id,
                         final  String user,
                         final boolean force) throws Exception {
        MongoExecutedQuery rec = new MongoExecutedQuery();
        try {
            rec = execMongoDb.getStatementById(id);
            final long cnt = ExecutionUserAccess.countArtifactAccess(id, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
            if(cnt > 1 && force) {
                /*Delete access records first*/
                ExecutionUserAccess.deleteArtifactAccess(id, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
                execMongoDb.deleteStatementById(id); /*Delete execution record by owner*/
                histFManagement.deleteArtifact(rec);  /*Delete output*/
                rec.setFlag(ExecutedStatementFlag.executionRecordDeleted);
            } else {
                rec.setFlag(ExecutedStatementFlag.notAllowedDeleteStatement);
            }
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            rec.setFlag(ExecutedStatementFlag.unknownError);
        }
        return rec;
    }


    public ElasticExecutedQuery
    deleteElasticExecution(final  long id,
                           final  String user,
                           final boolean force) throws Exception {
        ElasticExecutedQuery rec = new ElasticExecutedQuery();
        try {
            User u = authUtil.getUser(user);
            rec = execElasticDb.getStatementById(id);
            final long cnt = ExecutionUserAccess.countArtifactAccess(id,execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
            if(cnt > 1 && force) {
                /*Delete access records first*/
                ExecutionUserAccess.deleteArtifactAccess(id, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
                execElasticDb.deleteStatementByIdAndUser(id, u.getId()); /*Delete execution record by enforcing user*/
                histFManagement.deleteArtifact(rec);  /*Delete output*/
                rec.setFlag(ExecutedStatementFlag.executionRecordDeleted);
            } else {
                rec.setFlag(ExecutedStatementFlag.notAllowedDeleteStatement);
            }
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            rec.setFlag(ExecutedStatementFlag.unknownError);
        }
        return rec;
    }

    public RdbmsExecutedQuery
    deleteRdbmsExecution(final  long id,
                         final  String user,
                         final boolean force) throws Exception {
        RdbmsExecutedQuery rec = new RdbmsExecutedQuery();
        try {
            User u = authUtil.getUser(user);
            rec = execRdbmsDb.getStatementById(id);
            final long cnt = ExecutionUserAccess.countArtifactAccess(id, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
            if(cnt > 1 && force) {
                /*Delete access records first*/
                ExecutionUserAccess.deleteArtifactAccess(id, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());

                execRdbmsDb.deleteStatementByIdAndUser(id, u.getId()); /*Delete execution record by owner*/
                histFManagement.deleteArtifact(rec);  /*Delete output*/
                rec.setFlag(ExecutedStatementFlag.executionRecordDeleted);
            } else {
                rec.setFlag(ExecutedStatementFlag.notAllowedDeleteStatement);
            }
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            rec.setFlag(ExecutedStatementFlag.unknownError);
        }
        return rec;
    }



    public RestInterface deleteExecution(final String repoName,
                                         final long id,
                                         final String user,
                                         final boolean force) throws Exception {
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            return deleteRdbmsExecution(id, user, force);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            return deleteMongoExecution(id, user, force);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            return deleteElasticExecution(id, user, force);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            return deleteExecutedScriptAndLogs(id, user, force);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            return null;
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }
    }

    /*Execution Groups*/

    public void
    createArtifactGroup(final String repoName,
                        final String groupName,
                        final String comment) throws Exception {
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            ExecutionGroup.addArtifactGroup(groupName, comment, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            ExecutionGroup.addArtifactGroup(groupName, comment, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            ExecutionGroup.addArtifactGroup(groupName, comment, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            ExecutionGroup.addArtifactGroup(groupName, comment, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            ExecutionGroup.addArtifactGroup(groupName, comment, storageDb.getJDBC_DRIVER(), storageDb.getDB_URL_DISK(), storageDb.getUSER(), storageDb.getPASS());
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
        } else {
            System.out.println("Not implemented");
        }
    }

    public void
    deleteArtifactGroup(final String repoName,
                        final long groupId) throws Exception {
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            ExecutionGroup.deleteArtifactGroup(groupId, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            ExecutionGroup.deleteArtifactGroup(groupId, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            ExecutionGroup.deleteArtifactGroup(groupId, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            ExecutionGroup.deleteArtifactGroup(groupId, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            ExecutionGroup.deleteArtifactGroup(groupId, storageDb.getJDBC_DRIVER(), storageDb.getDB_URL_DISK(), storageDb.getUSER(), storageDb.getPASS());
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
        } else {
            System.out.println("Not implemented");
        }
    }


    public RestInterface
    getArtifactGroups(final String repoName,
                      final String txt) throws Exception {
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            return ExecutionGroup.getArtifactGroups(txt, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            return ExecutionGroup.getArtifactGroups(txt, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            return ExecutionGroup.getArtifactGroups(txt, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            return ExecutionGroup.getArtifactGroups(txt, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            return ExecutionGroup.getArtifactGroups(txt, storageDb.getJDBC_DRIVER(), storageDb.getDB_URL_DISK(), storageDb.getUSER(), storageDb.getPASS());
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }
    }


    public RestInterface
    getArtifactGroup(final String repoName,
                     final String groupName) throws Exception {
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            return ExecutionGroup.getArtifactGroup(groupName, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            return ExecutionGroup.getArtifactGroup(groupName, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            return ExecutionGroup.getArtifactGroup(groupName, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            return ExecutionGroup.getArtifactGroup(groupName, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            return ExecutionGroup.getArtifactGroup(groupName, storageDb.getJDBC_DRIVER(), storageDb.getDB_URL_DISK(), storageDb.getUSER(), storageDb.getPASS());
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }
    }


    /*Execution User Access*/


    /**
     *
     * @param repoName
     * @param objectId
     * @param userId
     * @param privilegeType -  see PersistencePrivilege class
     * @throws Exception
     */
    public void
    addArtifactAccess(final String repoName,
                      final long objectId,
                      final long userId,
                      final String privilegeType) throws Exception {

        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            ExecutionUserAccess.addArtifactAccess(objectId, userId, privilegeType, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            ExecutionUserAccess.addArtifactAccess(objectId, userId, privilegeType, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            ExecutionUserAccess.addArtifactAccess(objectId, userId, privilegeType, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            ExecutionUserAccess.addArtifactAccess(objectId, userId, privilegeType, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            ExecutionUserAccess.addArtifactAccess(objectId, userId, privilegeType, storageDb.getJDBC_DRIVER(), storageDb.getDB_URL_DISK(), storageDb.getUSER(), storageDb.getPASS());
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
        } else {
            System.out.println("Not implemented");
        }
    }


    public AccessRefPrivilegeList
    getArtifactAccessById(final String repoName,
                          final long objectId) throws Exception {

        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessById(objectId, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessById(objectId, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessById(objectId, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessById(objectId, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessById(objectId, storageDb.getJDBC_DRIVER(), storageDb.getDB_URL_DISK(), storageDb.getUSER(), storageDb.getPASS());
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }
    }


    public AccessRefPrivilegeList
    getArtifactAccessByUserId(final String repoName,
                              final long userId) throws Exception {

        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessByUserId(userId, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessByUserId(userId, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessByUserId(userId, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessByUserId(userId, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessByUserId(userId, storageDb.getJDBC_DRIVER(), storageDb.getDB_URL_DISK(), storageDb.getUSER(), storageDb.getPASS());
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }
    }


    public AccessRefPrivilege
    getArtifactAccess(final String repoName,
                      final long objectId,
                      final long userId) throws Exception {

        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessByUser(objectId, userId, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessByUser(objectId, userId, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessByUser(objectId, userId, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessByUser(objectId, userId, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            return ExecutionUserAccess.getArtifactAccessByUser(objectId, userId, storageDb.getJDBC_DRIVER(), storageDb.getDB_URL_DISK(), storageDb.getUSER(), storageDb.getPASS());
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }
    }

    public void
    deleteAccessByArtifactId(final String repoName,
                             final long objectId) throws Exception {

        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            ExecutionUserAccess.deleteAccessByArtifactId(objectId, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            ExecutionUserAccess.deleteAccessByArtifactId(objectId, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            ExecutionUserAccess.deleteAccessByArtifactId(objectId, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            ExecutionUserAccess.deleteAccessByArtifactId(objectId, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            ExecutionUserAccess.deleteAccessByArtifactId(objectId, storageDb.getJDBC_DRIVER(), storageDb.getDB_URL_DISK(), storageDb.getUSER(), storageDb.getPASS());
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
        } else {
            System.out.println("Not implemented");
        }
    }

    /*delete access to user to a certain user in a certain repo*/
    public void
    deleteArtifactAccessToUser(final String repoName,
                               final long objectId,
                               final long userId) throws Exception {

        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            ExecutionUserAccess.deleteArtifactAccess(objectId, userId, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            ExecutionUserAccess.deleteArtifactAccess(objectId, userId, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            ExecutionUserAccess.deleteArtifactAccess(objectId, userId, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            ExecutionUserAccess.deleteArtifactAccess(objectId, userId, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            ExecutionUserAccess.deleteArtifactAccess(objectId, userId, storageDb.getJDBC_DRIVER(), storageDb.getDB_URL_DISK(), storageDb.getUSER(), storageDb.getPASS());
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
        } else {
            System.out.println("Not implemented");
        }
    }





    public long
    countAccessRefTable(final String repoName,
                        final long objectId) throws Exception {

        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            return ExecutionUserAccess.countArtifactAccess(objectId, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            return ExecutionUserAccess.countArtifactAccess(objectId, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            return ExecutionUserAccess.countArtifactAccess(objectId, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            return ExecutionUserAccess.countArtifactAccess(objectId, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            return ExecutionUserAccess.countArtifactAccess(objectId, storageDb.getJDBC_DRIVER(), storageDb.getDB_URL_DISK(), storageDb.getUSER(), storageDb.getPASS());
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return -1;
        } else {
            System.out.println("Not implemented");
            return -1;
        }
    }



    public boolean
    isExecutedName(final String repoName, final String name) throws Exception {
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            return execRdbmsDb.getStatementsByName(name).getRdbmsExecutedQueryList().isEmpty();
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            return execMongoDb.getStatementByName(name).getMongoExecutedQueryLst().isEmpty();
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            return execElasticDb.getStatementByName(name).getElasticExecutedQueryLst().isEmpty();
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            return execScriptDb.getScriptByName(name).getScriptExecutedRecordList().isEmpty();
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            System.out.println(RepoStaticDesc.fileRepo + " is not implemented");
            return true;
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return true;
        } else {
            System.out.println("Not implemented");
            return false;
        }
    }

    public RestInterface
    getUserExecutedArtifactList (final String repoName,
                                 final String user,
                                 final String scriptName,
                                 final String src) throws Exception {
        User u = authUtil.getUser(user);
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            return execRdbmsDb.getUserExecutedStatements(u.getId(), scriptName, src);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            return execMongoDb.getUserExecutedStatements(u.getId(), scriptName, src);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            return execElasticDb.getUserExecutedStatements(u.getId(), scriptName, src);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            return execScriptDb.getUserExecutedScripts(u.getId(), scriptName, src);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            return null;
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }
    }


    public void giveExecutedArtifactAccessToUser(final String repoName,
                                                 final String toUser,
                                                 final long artifactId) throws Exception {
        User u = authUtil.getUser(toUser);
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            ExecutionUserAccess.addArtifactAccess(artifactId, u.getId(), PersistencePrivilege.pTypeAdmin, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            ExecutionUserAccess.addArtifactAccess(artifactId, u.getId(), PersistencePrivilege.pTypeAdmin, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            ExecutionUserAccess.addArtifactAccess(artifactId, u.getId(), PersistencePrivilege.pTypeAdmin, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            ExecutionUserAccess.addArtifactAccess(artifactId, u.getId(), PersistencePrivilege.pTypeAdmin, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            System.out.println(RepoStaticDesc.fileRepo + " is not implemented");
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
        } else {
            System.out.println("Not implemented");
        }
    }




    public RestInterface
    deleteExecutedArtifactAccess (final String repoName,
                                  final String user,
                                  final long artifactId) throws Exception {
        User u = authUtil.getUser(user);
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            RdbmsExecutedQuery ret= execRdbmsDb.deleteExecutedStatementAccess(u.getId(), artifactId);
            if(ret.getFlag() == ExecutedStatementFlag.executionRecordDeleted) {
                histFManagement.deleteArtifact(ret);
            }
            return ret;
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            MongoExecutedQuery ret= execMongoDb.deleteExecutedStatementAccess(u.getId(), artifactId);
            if(ret.getFlag() == ExecutedStatementFlag.executionRecordDeleted) {
                histFManagement.deleteArtifact(ret);
            }
            return ret;
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            ElasticExecutedQuery ret= execElasticDb.deleteExecutedStatementAccess(u.getId(), artifactId);
            if(ret.getFlag() == ExecutedStatementFlag.executionRecordDeleted) {
                histFManagement.deleteArtifact(ret);
            }
            return ret;
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            ScriptExecutedRecord ret= execScriptDb.deleteExecutedScriptAccess(u.getId(), artifactId);
            if(ret.getFlag() == ExecutedStatementFlag.executionRecordDeleted) {
                histFManagement.deleteArtifact(ret);
            }
            return ret;
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            System.out.println(RepoStaticDesc.fileRepo + " is not implemented");
            return null;
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }


    }


    public RestInterface
    getAllArtifactExecutionList(final String repoName,
                                final String user) throws Exception {
        User u = authUtil.getUser(user);
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            return execRdbmsDb.getAllExecutedStatementsByUser(u.getId());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            return execMongoDb.getAllExecutedStatementsByUser(u.getId());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            return execElasticDb.getAllExecutedStatementsByUser(u.getId());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            return execScriptDb.getAllExecutedScriptsByUser(u.getId());
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            System.out.println(RepoStaticDesc.fileRepo + " is not implemented");
            return null;
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }
    }


    public RestInterface
    deleteAllUserExecutedArtifacts(final String repoName,
                                   final String user) throws Exception {
        User u = authUtil.getUser(user);
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            List<RdbmsExecutedQuery> lst = execRdbmsDb.deleteAllExecutedStatementByUserId(u.getId());
            for(RdbmsExecutedQuery rec : lst) {
                if(rec.getFlag() == ExecutedStatementFlag.executionRecordDeleted) {
                    histFManagement.deleteArtifact(rec);
                }
            }
            return new RdbmsExecutedQueryList(lst);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            List<MongoExecutedQuery> lst = execMongoDb.deleteAllExecutedStatementByUserId(u.getId());
            for(MongoExecutedQuery rec : lst) {
                if(rec.getFlag() == ExecutedStatementFlag.executionRecordDeleted) {
                    histFManagement.deleteArtifact(rec);
                }
            }
            return new MongoExecutedQueryList(lst);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            List<ElasticExecutedQuery> lst = execElasticDb.deleteAllExecutedStatementByUserId(u.getId());
            for(ElasticExecutedQuery rec : lst) {
                if(rec.getFlag() == ExecutedStatementFlag.executionRecordDeleted) {
                    histFManagement.deleteArtifact(rec);
                }
            }
            return new ElasticExecutedQueryList(lst);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            List<ScriptExecutedRecord> lst = execScriptDb.deleteAllExecutedScriptsByUserId(u.getId());
            for(ScriptExecutedRecord rec : lst) {
                if(rec.getFlag() == ExecutedStatementFlag.executionRecordDeleted) {
                    histFManagement.deleteArtifact(rec);
                }
            }
            return new ScriptExecutedRecordList(lst);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            System.out.println(RepoStaticDesc.fileRepo + " is not implemented");
            return null;
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }
    }



    public RestInterface
    deleteExecutedArtifacts(final String repoName,
                            final String user,
                            final boolean force,
                            final List<Long> ids) throws Exception {
        User u = authUtil.getUser(user);


        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            List<RdbmsExecutedQuery> lst = execRdbmsDb.deleteExecutedStatement(ids, u.getId(), force);
            for(RdbmsExecutedQuery rec : lst) {
                if(rec.getFlag() == ExecutedStatementFlag.executionRecordDeleted) {
                    histFManagement.deleteArtifact(rec);
                }
            }
            return new RdbmsExecutedQueryList(lst);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            List<MongoExecutedQuery> lst = execMongoDb.deleteExecutedStatement(ids, u.getId(), force);
            for(MongoExecutedQuery rec : lst) {
                if(rec.getFlag() == ExecutedStatementFlag.executionRecordDeleted) {
                    histFManagement.deleteArtifact(rec);
                }
            }
            return new MongoExecutedQueryList(lst);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            List<ElasticExecutedQuery> lst = execElasticDb.deleteExecutedStatement(ids, u.getId(), force);
            for(ElasticExecutedQuery rec : lst) {
                if(rec.getFlag() == ExecutedStatementFlag.executionRecordDeleted) {
                    histFManagement.deleteArtifact(rec);
                }
            }
            return new ElasticExecutedQueryList(lst);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            List<ScriptExecutedRecord> lst = execScriptDb.deleteExecutedScript(ids, u.getId(), force);
            for(ScriptExecutedRecord rec : lst) {
                if(rec.getFlag() == ExecutedStatementFlag.executionRecordDeleted) {
                    histFManagement.deleteArtifact(rec);
                }
            }
            return new ScriptExecutedRecordList(lst);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            System.out.println(RepoStaticDesc.fileRepo + " is not implemented");
            return null;
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }
    }


    public RestInterface
    getExecutionOutput(final String repoName,
                       final long artifactId) throws Exception {
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            RdbmsExecutedQuery rec = execRdbmsDb.getStatementById(artifactId);
            return ResultQuery.toResultQuery(  FileUtilWrapper.readFileToString(rec.getRepPath()) );
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            MongoExecutedQuery rec = execMongoDb.getStatementById(artifactId);
            return MongoResultSet.toMongoResultSet( FileUtilWrapper.readFileToString(rec.getRepPath()) );
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            ElasticExecutedQuery rec = execElasticDb.getStatementById(artifactId);
            return ElasticResultSet.toElasticResultSet( FileUtilWrapper.readFileToString(rec.getRepPath()) );
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            ScriptExecutedRecord rec = execScriptDb.getScriptById(artifactId);
            String content = FileUtilWrapper.readFileToString(rec.getRepPath());
            return ScriptOutputDetail.toScriptOutputDetail(content);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            System.out.println(RepoStaticDesc.fileRepo + " is not implemented");
            return null;
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }
    }

    public RestInterface
    deleteOutputExecution(final String repoName,
                          final long userId,
                          final long artifactId) throws Exception {
        
        ExecutedStatement rec;
        if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            rec = execRdbmsDb.getStatementById(artifactId);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            rec = execMongoDb.getStatementById(artifactId);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            rec = execElasticDb.getStatementById(artifactId);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            rec = execScriptDb.getScriptById(artifactId);
        } else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            System.out.println(RepoStaticDesc.fileRepo + " is not implemented");
            return null;
        }  else if(repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }

        if(rec.getUserId() == userId) {
            FileUtilWrapper.deleteFile(rec.getRepPath());
            rec.setFlag(ExecutedStatementFlag.executionRecordDeleted);
        } else {
            rec.setFlag(ExecutedStatementFlag.notAllowedDeleteOutput);
        }
        return rec;

    }



    public void
    deleteUserAndAllArtifacts(final long userId) throws Exception {

        RdbmsExecutedQueryList rList = execRdbmsDb.getAllExecutedStatementsByUser(userId);
        for(RdbmsExecutedQuery q: rList.getRdbmsExecutedQueryList()) {
            if( q.getCntAccess() == 1) {
                histFManagement.deleteArtifact(q);
            }
        }
        ExecutionUserAccess.deleteUserAccess(userId, execRdbmsDb.getJDBC_DRIVER(), execRdbmsDb.getDB_URL_DISK(), execRdbmsDb.getUSER(), execRdbmsDb.getPASS());

        MongoExecutedQueryList mList = execMongoDb.getAllExecutedStatementsByUser(userId);
        for(MongoExecutedQuery m: mList.getMongoExecutedQueryLst()) {
            if( m.getCntAccess() == 1) {
                histFManagement.deleteArtifact(m);
            }
        }
        ExecutionUserAccess.deleteUserAccess(userId, execMongoDb.getJDBC_DRIVER(), execMongoDb.getDB_URL_DISK(), execMongoDb.getUSER(), execMongoDb.getPASS());

        ElasticExecutedQueryList eList = execElasticDb.getAllExecutedStatementsByUser(userId);
        for(ElasticExecutedQuery m: eList.getElasticExecutedQueryLst()) {
            if( m.getCntAccess() == 1) {
                histFManagement.deleteArtifact(m);
            }
        }
        ExecutionUserAccess.deleteUserAccess(userId, execElasticDb.getJDBC_DRIVER(), execElasticDb.getDB_URL_DISK(), execElasticDb.getUSER(), execElasticDb.getPASS());

        ScriptExecutedRecordList sList = execScriptDb.getAllExecutedScriptsByUser(userId);
        for(ScriptExecutedRecord m: sList.getScriptExecutedRecordList()) {
            if( m.getCntAccess() == 1) {
                histFManagement.deleteArtifact(m);
            }
        }
        ExecutionUserAccess.deleteUserAccess(userId, execScriptDb.getJDBC_DRIVER(), execScriptDb.getDB_URL_DISK(), execScriptDb.getUSER(), execScriptDb.getPASS());

        BackupStorageList cList = storageDb.getBackupStorageListByUser(userId);
        for(BackupStorage m: cList.getBackupStorageList()) {
            if( m.getCntAccess() == 1) {
                histFManagement.deleteArtifact(m);
            }
        }
        ExecutionUserAccess.deleteUserAccess(userId, storageDb.getJDBC_DRIVER(), storageDb.getDB_URL_DISK(), storageDb.getUSER(), storageDb.getPASS());

    }



    public RestInterface convert(String rec, final String repoName) {
        if (repoName.trim().compareToIgnoreCase(RepoStaticDesc.sqlRepo) == 0) {
            return RdbmsExecutedQuery.toRdbmsExecutedQuery(rec);
        } else if (repoName.trim().compareToIgnoreCase(RepoStaticDesc.mongoRepo) == 0) {
            return MongoExecutedQuery.toMongoExecutedQuery(rec);
        } else if (repoName.trim().compareToIgnoreCase(RepoStaticDesc.elasticRepo) == 0) {
            return ElasticExecutedQuery.toElasticExecutedQuery(rec);
        } else if (repoName.trim().compareToIgnoreCase(RepoStaticDesc.scriptRepo) == 0) {
            return ScriptExecutedRecord.toScriptExecutedRecord(rec);
        } else if (repoName.trim().compareToIgnoreCase(RepoStaticDesc.fileRepo) == 0) {
            System.out.println(RepoStaticDesc.fileRepo + " is not implemented");
            return null;
        } else if (repoName.trim().compareToIgnoreCase(RepoStaticDesc.exchangeRepo) == 0) {
            System.out.println(RepoStaticDesc.exchangeRepo + " is not implemented");
            return null;
        } else {
            System.out.println("Not implemented");
            return null;
        }
    }

}
