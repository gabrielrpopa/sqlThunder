package com.widescope.rdbmsRepo.database.mongodb;

import com.mongodb.BasicDBObject;
import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.structuredFiles.csv.CsvWrapper;
import com.widescope.sqlThunder.utils.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MongoWrapperFunction {

    public static List<ObjectId> toObjectId(List<String> idList) {
        List<ObjectId> ret = new ArrayList<>();
        for(String x: idList) {
            ret.add( new ObjectId(x) );
        }
        return ret;
    }

    public static List<Document> toDocument(List<String> idList) {
        List<Document> ret = new ArrayList<>();
        for(String x: idList) {
            ret.add(Document.parse(x) );
        }
        return ret;
    }

    public static Document createTestObject(String item, float price, int quantity) {
        final Random rand = new Random();
        List<Document> sales = List.of(new Document("totalSales", "USA").append("sale", rand.nextDouble() * 100),
                                        new Document("totalSales", "Canada").append("sale", rand.nextDouble() * 100),
                                        new Document("totalSales", "Mexico").append("sale", rand.nextDouble() * 100));
        return new Document("_id", new ObjectId())  .append("item", item)
                                                    .append("price", price)
                                                    .append("quantity", quantity)
                                                    .append("scores", sales);
    }


    public static File copyFileToTempFolder(MultipartFile attachment) throws IOException {
        File attachmentFile = File.createTempFile(UUID.randomUUID().toString(), "temp");
        FileOutputStream o = new FileOutputStream(attachmentFile);
        IOUtils.copy(attachment.getInputStream(), o);
        o.close();
        return attachmentFile;
    }

    public static File copyFileToTempFolder(MultipartFile attachment, final String origFileName) throws IOException {
        File attachmentFile = File.createTempFile(UUID.randomUUID().toString(), origFileName);
        FileOutputStream o = new FileOutputStream(attachmentFile);
        IOUtils.copy(attachment.getInputStream(), o);
        o.close();
        return attachmentFile;
    }

    public static int addMongoRecordsFromZip(File attachmentFile,
                                              final int bulkCnt,
                                              MongoDbConnection mongoDbConnection,
                                              final String dbName,
                                              final String collectionName) throws Exception {
        List<Document> jsonDocument = new ArrayList<>();
        int count = 0;
        String line;
        ZipFile zipFile = new ZipFile(attachmentFile);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while(entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            if(!zipEntry.isDirectory()) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
                while((line = bufferedReader.readLine()) != null){
                    count++;
                    if(JsonUtils.isJsonValid(line)) {
                        if(bulkCnt == 0) {
                            MongoPut.addDocument(mongoDbConnection, dbName, collectionName, line);
                        } else {
                            if(count == bulkCnt) {
                                jsonDocument.add(Document.parse(line) );
                                MongoPut.bulkInsert(mongoDbConnection, dbName, collectionName, jsonDocument);
                                jsonDocument.clear();
                            } else {
                                jsonDocument.add(Document.parse(line) );
                            }
                        }
                    } else {
                        String jsonString = JsonUtils.commastringToJsonString(line);
                        if(bulkCnt == 0) {
                            MongoPut.addDocument(mongoDbConnection, dbName, collectionName, jsonString);
                        } else {
                            if(count == bulkCnt) {
                                jsonDocument.add(Document.parse(line) );
                                MongoPut.bulkInsert(mongoDbConnection, dbName, collectionName, jsonDocument);
                                jsonDocument.clear();
                            } else {
                                jsonDocument.add(Document.parse(jsonString) );
                            }
                        }
                    }
                }
                if(!jsonDocument.isEmpty())
                    MongoPut.bulkInsert(mongoDbConnection, dbName, collectionName, jsonDocument);

                bufferedReader.close();
            }
        }
        zipFile.close();
        return count;
    }





    public static int addMongoRecordsFromTextFiles(File attachmentFile,
                                                   final int bulkCnt,
                                                   MongoDbConnection mongoDbConnection,
                                                   final String dbName,
                                                   final String collectionName) throws Exception {
        List<Document> jsonDocument = new ArrayList<>();
        int count = 0;
        String line;

        BufferedReader objReader = new BufferedReader(new FileReader(attachmentFile.getAbsolutePath()));
        while ((line = objReader.readLine()) != null) {
            count++;
            if(JsonUtils.isJsonValid(line)) {
                if(bulkCnt == 0) {
                    MongoPut.addDocument(mongoDbConnection, dbName, collectionName, line);
                } else {
                    if(count == bulkCnt) {
                        MongoPut.bulkInsert(mongoDbConnection, dbName, collectionName, jsonDocument);
                        jsonDocument.clear();
                    } else {
                        jsonDocument.add(Document.parse(line) );
                    }
                }
            } else {
                String jsonString = JsonUtils.commastringToJsonString(line);
                if(bulkCnt == 0) {
                    MongoPut.addDocument(mongoDbConnection, dbName, collectionName, jsonString);
                } else {
                    if(count == bulkCnt) {
                        MongoPut.bulkInsert(mongoDbConnection, dbName, collectionName, jsonDocument);
                        jsonDocument.clear();
                    } else {
                        jsonDocument.add(Document.parse(jsonString) );
                    }
                }
            }
        }
        MongoPut.bulkInsert(mongoDbConnection, dbName, collectionName, jsonDocument);
        objReader.close();
        return count;
    }


    public static int
    addCsvRecords(final String filePath,
                  MongoDbConnection mongoDbConnection,
                  final String dbName,
                  final String cName,
                  final int bCount) throws Exception {
        String csvContent = CsvWrapper.readFile(filePath);
        List<String> jList = CsvWrapper.stringToJsonList(csvContent, "N");
        return MongoPut.addDocumentsToCollection(mongoDbConnection, dbName, cName, jList, bCount, 0);
    }

}
