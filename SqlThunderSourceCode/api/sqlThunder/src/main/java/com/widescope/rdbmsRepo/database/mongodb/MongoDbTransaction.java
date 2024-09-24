package com.widescope.rdbmsRepo.database.mongodb;

import com.widescope.chat.db.ChatMessage;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeMongoBinaryFile;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeMongoBinaryFileMeta;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeMongoBinaryFileMetaList;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeObjectAssociatedMetadata;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterDb;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterDbCollectionList;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterDbList;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterRecord;
import com.widescope.rdbmsRepo.database.mongodb.response.StringList;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlRepoUtils;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

public class MongoDbTransaction {

    public static void addBucket(final String clusterUniqueName,
                                 final String databaseName,
                                 final String bucketName,
                                 final MongoClusterDb mongoClusterDb) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(), mongoClusterRecord.getClusterId(), mongoClusterRecord.getUniqueName());
        MongoBucket.createBucket(mongoDbConnection, databaseName, bucketName, mongoClusterDb);
        mongoDbConnection.disconnect();

    }


    public static void deleteBucket(final String clusterUniqueName,
                                    final String databaseName,
                                    final String bucketName,
                                    final MongoClusterDb mongoClusterDb) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());
        MongoBucket.deleteBucket(mongoDbConnection, databaseName, bucketName, mongoClusterDb);
        mongoDbConnection.disconnect();
    }

    public static void deleteBucket(final String clusterUniqueName,
                                    final String databaseName,
                                    final String bucketName) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(), mongoClusterRecord.getClusterId(), mongoClusterRecord.getUniqueName());
        MongoBucket.deleteBucket(mongoDbConnection, databaseName, bucketName);
        mongoDbConnection.disconnect();
    }


    public static void addCollection (final String clusterUniqueName,
                                      final String databaseName,
                                      final String collectionName) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());
        MongoPut.createCollection(mongoDbConnection, databaseName, collectionName);
        mongoDbConnection.disconnect();
    }

    public static void deleteCollectionFromDatabase(final String clusterUniqueName,
                                                    final String databaseName,
                                                    final String collectionName) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());
        MongoPut.dropCollection(mongoDbConnection, databaseName, collectionName);
        mongoDbConnection.disconnect();
    }


    public static MongoClusterDbList getMongoDatabaseList(  final String clusterUniqueName) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                mongoClusterRecord.getClusterId(),
                mongoClusterRecord.getUniqueName());
        MongoClusterDbList mongoClusterDbList = new MongoClusterDbList(MongoGet.getDatabasesInfo(mongoDbConnection));
        mongoDbConnection.disconnect();
        return mongoClusterDbList;
    }

    public static MongoClusterDbCollectionList getMongoClusterDbCollectionList(final String clusterUniqueName,
                                                                               final String databaseName) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                mongoClusterRecord.getClusterId(),
                mongoClusterRecord.getUniqueName());
        MongoClusterDbCollectionList ret = new MongoClusterDbCollectionList(MongoGet.getCollectionInfo(mongoDbConnection, databaseName));
        mongoDbConnection.disconnect();
        return ret;
    }

    public static void addIndexToCollection(final String clusterUniqueName,
                                            final String databaseName,
                                            final String collectionName,
                                            final String fieldName) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                mongoClusterRecord.getClusterId(),
                mongoClusterRecord.getUniqueName());
        MongoPut.createIndex(mongoDbConnection, databaseName, collectionName, fieldName);
        mongoDbConnection.disconnect();
    }

    public static void deleteIndexToCollection(final String clusterUniqueName,
                                               final String databaseName,
                                               final String collectionName,
                                               final String fieldName) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());
        MongoPut.deleteIndex(mongoDbConnection, databaseName, collectionName, fieldName);
        mongoDbConnection.disconnect();
    }


    public static boolean
    addDocumentToCollection(final String clusterUniqueName,
                            final String databaseName,
                            final String collectionName,
                            final String jsonDocument) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
        try {
            return  MongoPut.addDocument_(mongoDbConnection, databaseName, collectionName, jsonDocument);
        } catch(Exception x) {
            return false;
        } finally {
            mongoDbConnection.disconnect();
        }
    }


    public static String
    addChatMessageToCollection( final String clusterUniqueName,
                                final String databaseName,
                                final String collectionName,
                                final ChatMessage chatMessage,
                                final String requestId) throws Exception {
        String ret = "Y";
        try {
            MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
            MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
            ret = MongoPut.addChatMessage(mongoDbConnection, databaseName, collectionName, chatMessage, requestId);
            mongoDbConnection.disconnect();
        } catch(Exception x) {
            ret="N";
        }
        return ret;
    }


    public static long replaceDocumentById(final String mongoClusterName,
                                           final String mongoDatabaseName,
                                           final String mongoCollectionName,
                                           final String idObject,
                                           final String object) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(mongoClusterName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());
        long ret =  MongoPut.replaceDocumentById( mongoDbConnection, mongoDatabaseName, mongoCollectionName, idObject, object);
        mongoDbConnection.disconnect();
        return ret;
    }

    public static long deleteMultipleDocuments(final String mongoClusterName,
                                               final String mongoDatabaseName,
                                               final String mongoCollectionName,
                                               final List<String> jsonDocument) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(mongoClusterName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());
        long ret =  MongoPut.deleteDocumentByListId(mongoDbConnection, mongoDatabaseName, mongoCollectionName, jsonDocument);
        mongoDbConnection.disconnect();
        return ret;
    }

    public static long  deleteManyRecordsSimpleTextSearch(final String mongoClusterName,
                                                          final String mongoDatabaseName,
                                                          final String mongoCollectionName,
                                                          final String itemToSearchAndDelete,
                                                          final String language) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(mongoClusterName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());

        long ret = MongoPut.deleteManyRecordsSimpleTextSearch(	mongoDbConnection,
                                                                mongoDatabaseName,
                                                                mongoCollectionName,
                                                                itemToSearchAndDelete,
                                                                language);

        mongoDbConnection.disconnect();
        return ret;
    }

    public static long deleteManyRecords(final String mongoClusterName,
                                         final String mongoDatabaseName,
                                         final String mongoCollectionName,
                                         final String itemToSearch,
                                         final String valueToSearch,
                                         final String operator,
                                         final String valueToSearchType) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(mongoClusterName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());

        long ret = MongoPut.deleteManyRecords(	mongoDbConnection,
                                                mongoDatabaseName,
                                                mongoCollectionName,
                                                itemToSearch,  /*The item in the JSON onject such as name or price*/
                                                valueToSearch, /*single value, not a list, an INT or LONG or VARCHAR*/
                                                operator,
                                                valueToSearchType);
        mongoDbConnection.disconnect();
        return ret;
    }


    public static long deleteManyRecordsRange(final String mongoClusterName,
                                              final String mongoDatabaseName,
                                              final String mongoCollectionName,
                                              final String itemToSearch,
                                              final String from,
                                              final String to,
                                              final String valueToSearchType) throws Exception {

        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(mongoClusterName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());

        long ret =  MongoPut.deleteManyRecordsRange(mongoDbConnection, mongoDatabaseName, mongoCollectionName, itemToSearch, from, to, valueToSearchType);

        mongoDbConnection.disconnect();
        return ret;
    }


    public static ChatMessage getChatMessageById(final String clusterUniqueName,
                                            final String databaseName,
                                            final String collectionName,
                                            final String docId) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
        ChatMessage ret = MongoGet.getChatMessageById(mongoDbConnection, databaseName, collectionName, docId);

        mongoDbConnection.disconnect();
        return ret;
    }

    public static ChatMessage setReadChatMessageById(final String clusterUniqueName,
                                                     final String databaseName,
                                                     final String collectionName,
                                                     final String docId) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
        ChatMessage ret = MongoGet.getChatMessageById(mongoDbConnection, databaseName, collectionName, docId);
        mongoDbConnection.disconnect();
        return ret;

    }

    public static ChatMessage deleteChatMessageById(final String clusterUniqueName,
                                                     final String databaseName,
                                                     final String collectionName,
                                                     final String docId) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
        ChatMessage ret =  MongoGet.deleteMessageById(mongoDbConnection, databaseName, collectionName, docId);

        mongoDbConnection.disconnect();
        return ret;

    }

    public static String getDocumentById(final String clusterUniqueName,
                                                 final String databaseName,
                                                 final String collectionName,
                                                 final String docId) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
        String ret = MongoGet.getDocumentById(mongoDbConnection, databaseName, collectionName, docId);

        mongoDbConnection.disconnect();
        return ret;
    }

    public static MongoResultSet  searchSimpleText(  final String clusterUniqueName,
                                                     final String databaseName,
                                                     final String collectionName,
                                                     final String itemToSearch,
                                                     final String language,
                                                     final String isHighestScore
                                                     ) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());

        MongoResultSet mongoResultSet = MongoGet.searchDocumentSimpleText(	mongoDbConnection,
                                                                            databaseName,
                                                                            collectionName,
                                                                            itemToSearch,
                                                                            language,
                                                                            isHighestScore);
        mongoResultSet.setMetadata(MongoResultSet.analyseSchemaFirst(mongoResultSet.getResultSet()));

        mongoDbConnection.disconnect();
        return mongoResultSet;
    }


    public static StringList getFirstNDocuments(  final String clusterName,
                                                  final String databaseName,
                                                  final String collectionName,
                                                  final String limit) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
        List<String> lst = MongoGet.getFirstCollectionNDocuments(mongoDbConnection,
                                                                databaseName,
                                                                collectionName,
                                                                Integer.parseInt(limit));
        mongoDbConnection.disconnect();
        return new StringList(lst);
    }

    public static LargeMongoBinaryFileMetaList getFirstNBucketDocuments(final String clusterName,
                                                                          final String databaseName,
                                                                          final String bucketName,
                                                                          final String limit) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
        List<LargeMongoBinaryFileMeta> lst = MongoBucket.getFirstBucketDocs(mongoDbConnection,
                                                                            databaseName,
                                                                            bucketName,
                                                                            Integer.parseInt(limit));
        mongoDbConnection.disconnect();
        return new LargeMongoBinaryFileMetaList(lst);
    }

    public static long getCollectionDocsCount(final String clusterUniqueName,
                                              final String databaseName,
                                              final String collectionName,
                                              final String isEstimate) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
        long count = MongoGet.getCollectionDocumentsCount(mongoDbConnection, databaseName, collectionName, isEstimate);
        mongoDbConnection.disconnect();
        return count;
    }
    public static long deleteDocumentFromCollection(final String clusterUniqueName,
                                                    final String databaseName,
                                                    final String collectionName,
                                                    final String docId) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());

        long ret = MongoPut.deleteDocumentById(	mongoDbConnection, databaseName, collectionName, docId);
        mongoDbConnection.disconnect();
        return ret;
    }

    public static long deleteMultipleDocumentsFromCollection(final String clusterUniqueName,
                                                             final String databaseName,
                                                             final String collectionName,
                                                             final List<String> docIds) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());
        long ret = MongoPut.deleteDocumentByListId(mongoDbConnection, databaseName, collectionName, docIds);
        mongoDbConnection.disconnect();
        return ret;
    }



    public static String addLargeAttachmentToBucket(final MongoClusterDb mongoClusterDb,
                                                    final String clusterUniqueName,
                                                    final String databaseName,
                                                    final String bucketName,
                                                    final String fileName,
                                                    final String metadata,
                                                    final MultipartFile attachment,
                                                    final long userId) throws Exception {
        LargeObjectAssociatedMetadata metadata_ = new LargeObjectAssociatedMetadata();

        try {
            metadata_ = LargeObjectAssociatedMetadata.toLargeObjectAssociatedMetadata(metadata);
        } catch(Exception ignored) {	}

        assert metadata_ != null;
        metadata_.setOriginalUserId( userId );

        File attachmentFile = File.createTempFile(UUID.randomUUID().toString(), "temp");
        String absolutePath = attachmentFile.getAbsolutePath();
        FileOutputStream o = new FileOutputStream(attachmentFile);
        IOUtils.copy(attachment.getInputStream(), o);
        o.close();

        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                mongoClusterRecord.getClusterId(),
                mongoClusterRecord.getUniqueName());

        String fileId =
                MongoBucket.addLargeBinaryFile(	mongoClusterDb,
                                                mongoDbConnection,
                                                databaseName,
                                                bucketName,
                                                fileName,
                                                FileUtilWrapper.readFile(absolutePath),
                                                metadata_);

        FileUtilWrapper.deleteFile(absolutePath);

        mongoDbConnection.disconnect();
        return fileId;



    }


    public static String addLargeChatMessageToBucket(final String clusterUniqueName,
                                                    final String databaseName,
                                                    final String bucketName,
                                                    final String fileName,
                                                    final String metadata,
                                                    final byte[] attachment) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(), mongoClusterRecord.getClusterId(), mongoClusterRecord.getUniqueName());
        String fileId = MongoBucket.addLargeChatMessageFile(mongoDbConnection, databaseName, bucketName, fileName, attachment, metadata);
        mongoDbConnection.disconnect();
        return fileId;
    }



    public static LargeMongoBinaryFile getLargeAttachmentFromBucket(  final String clusterUniqueName,
                                                                      final String databaseName,
                                                                      final String bucketName,
                                                                      final String fileId) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());
        LargeMongoBinaryFile ret = MongoBucket.getLargeBinaryFile(mongoDbConnection, databaseName, bucketName, fileId);
        mongoDbConnection.disconnect();
        return ret;
    }

    public static boolean deleteLargeAttachmentFromBucket(final String clusterUniqueName,
                                                           final String databaseName,
                                                           final String bucketName,
                                                           final String fileId) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterUniqueName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord.getConnString(),
                                                                    mongoClusterRecord.getClusterId(),
                                                                    mongoClusterRecord.getUniqueName());
        boolean ret = MongoBucket.deleteLargeBinaryFile(mongoDbConnection, databaseName, bucketName, fileId);
        mongoDbConnection.disconnect();
        return ret;
    }



    public static MongoResultSet searchDocumentRange(final String clusterName,
                                                     final String dbName,
                                                     final String cName,
                                                     final String itemToSearch,
                                                     final String fromValue,
                                                     final String toValue,
                                                     final String valueSearchType
                                                     ) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
        MongoResultSet ret =  MongoGet.searchDocumentRange(mongoDbConnection, dbName, cName, itemToSearch, fromValue, toValue, valueSearchType,false) ;
        mongoDbConnection.disconnect();
        return ret;
    }


    public static MongoResultSet searchDocument (final String clusterName,
                                                 final String dbName,
                                                 final String cName,
                                                 final String itemToSearch,
                                                 final String valueToSearch,
                                                 final String operator,
                                                 final String valueToSearchType) throws Exception {
        MongoClusterRecord mongoClusterRecord = SqlRepoUtils.mongoDbMap.get(clusterName);
        MongoDbConnection mongoDbConnection = new MongoDbConnection(mongoClusterRecord);
        MongoResultSet ret = MongoGet.searchDocument(mongoDbConnection, dbName, cName, itemToSearch, valueToSearch, operator, valueToSearchType,false, false/*determine metadata is false*/);
        mongoDbConnection.disconnect();
        return ret;
    }


}
