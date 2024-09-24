package com.widescope.rdbmsRepo.database.mongodb;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import com.widescope.logging.AppLogger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeMongoBinaryFile;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeMongoBinaryFileMeta;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeMongoBinaryFileMetaRev;
import com.widescope.rdbmsRepo.database.mongodb.objects.LargeObjectAssociatedMetadata;
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterDb;


public class MongoBucket {

	public 
	static 
	LargeMongoBinaryFile 
	getLargeBinaryFile(	final MongoDbConnection mongoDbConnection, 
						final String dbName, 
						final String bucketName, 
						final String fileId) throws Exception	{
		try	{
			LargeMongoBinaryFile f = new LargeMongoBinaryFile();
			GridFSBucket bucket = GridFSBuckets.create(mongoDbConnection.getMongoClient().getDatabase(dbName), bucketName);
			ObjectId o = new ObjectId(fileId);
			GridFSDownloadStream downloadStream = bucket.openDownloadStream(o);
			GridFSFile gridFSFile = downloadStream.getGridFSFile();
            long fileLength = downloadStream.getGridFSFile().getLength();
            String fileName = downloadStream.getGridFSFile().getFilename();
            byte[] bytesToWriteTo = new byte[(int)fileLength];
            f.setFilename( fileName );
            if(null != gridFSFile.getMetadata()) {
                Document metaDoc = gridFSFile.getMetadata();
                LargeObjectAssociatedMetadata meta = new LargeObjectAssociatedMetadata();
                
                String originalFolder = "";
                try { originalFolder = metaDoc.get("originalFolder").toString(); } catch(Exception ignored) {}
                meta.setOriginalFolder( originalFolder );
                
                long originalUserId = -1;
                try { originalUserId = Long.parseLong(metaDoc.get("originalUserId").toString()); } catch(Exception ignored) {}
                meta.setOriginalUserId( originalUserId );
                
                String originalType = "";
                try { originalType = metaDoc.get("originalType").toString(); } catch(Exception ignored) {}
                meta.setOriginalType(originalType);
                
                long originalLastModified = -1;
                try { originalLastModified = Long.parseLong(metaDoc.get("originalLastModified").toString()); } catch(Exception ignored) {}
                meta.setOriginalLastModified(originalLastModified);
                
                f.setLargeObjectAssociatedMetadata( meta );
            }
            f.setFileSize( fileLength );
            f.setFile( bytesToWriteTo );
            downloadStream.close();
			return f;
		}
		catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}


	public 
	static 
	String 
	createBucket(	final MongoDbConnection mongoDbConnection, 
					final String dbName,
					final String bucketName,
					MongoClusterDb mongoClusterDb) throws Exception {
		byte[] data = new byte[1];
		data[0] = '\n';
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		GridFSUploadOptions  options  = new GridFSUploadOptions().chunkSizeBytes(data.length + 1);
		GridFSBucket b = GridFSBuckets.create(database, bucketName);
		GridFSUploadStream uploadStream = b.openUploadStream("toBeDeleted.null", options);
		uploadStream.write(data);
		uploadStream.close() ;
		mongoClusterDb.addBucket(mongoDbConnection.getClusterId(), dbName, bucketName);
		return bucketName;
	}

	public
	static
	String
	createBucket(	final MongoDbConnection mongoDbConnection,
					 final String dbName,
					 final String bucketName)  {

		byte[] data = new byte[1];
		data[0] = '\n';
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		GridFSUploadOptions  options  = new GridFSUploadOptions().chunkSizeBytes(data.length + 1);
		GridFSBucket b = GridFSBuckets.create(database, bucketName);
		GridFSUploadStream uploadStream = b.openUploadStream("toBeDeleted.null", options);
		uploadStream.write(data);
		uploadStream.close() ;
		return bucketName;
	}
	
	public 
	static 
	String 
	deleteBucket(	final MongoDbConnection mongoDbConnection, 
					final String dbName,
					final String bucketName,
					MongoClusterDb mongoClusterDb) throws Exception {
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		GridFSBucket b = GridFSBuckets.create(database, bucketName);
		b.drop();
		mongoClusterDb.deleteBucket(mongoDbConnection.getClusterId(), dbName, bucketName);
		return bucketName;
	}

	public
	static
	String
	deleteBucket(	final MongoDbConnection mongoDbConnection,
					 final String dbName,
					 final String bucketName)  {
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		GridFSBucket b = GridFSBuckets.create(database, bucketName);
		b.drop();
		return bucketName;
	}
	
	
	public 
	static 
	List<LargeMongoBinaryFileMeta> 
	getFirstBucketDocs(	final MongoDbConnection mongoDbConnection, 
						final String dbName,
						final String bucketName,
						final int firstN) {
		
		List<LargeMongoBinaryFileMeta> ret = new ArrayList<>();
		GridFSBucket bucket = GridFSBuckets.create(mongoDbConnection.getMongoClient().getDatabase(dbName), bucketName);
		Bson sort = Sorts.ascending("uploadDate");
        for (GridFSFile fsFile : bucket.find().sort(sort).limit(firstN)) {
            long timeStamp = fsFile.getUploadDate().getTime();
            String fileName = fsFile.getFilename();
            String id = fsFile.getObjectId().toString();
            long size = fsFile.getLength();

            ret.stream()
                    .filter(a -> a.getName().equals(fileName))
                    .findAny()
                    .ifPresentOrElse(a -> {
                                System.out.println("Present: " + fileName);
                                Optional<LargeMongoBinaryFileMeta> f = ret.stream().filter(b -> b.getName().equals(fileName)).findFirst();
                                int newRev = f.get().getRevList().stream().map(LargeMongoBinaryFileMetaRev::getRevision).toList().stream().max(Comparator.comparing(Integer::valueOf)).get() + 1;
                                LargeMongoBinaryFileMetaRev lRevNew = new LargeMongoBinaryFileMetaRev(id, newRev, timeStamp, size);
                                a.getRevList().add(lRevNew);
                            },
                            () -> {
                                System.out.println("Not Present: " + fileName);
                                LargeMongoBinaryFileMetaRev lRev = new LargeMongoBinaryFileMetaRev(id, 0, timeStamp, size);
                                List<LargeMongoBinaryFileMetaRev> lRevList = new ArrayList<LargeMongoBinaryFileMetaRev>();
                                lRevList.add(lRev);
                                LargeMongoBinaryFileMeta lMeta = new LargeMongoBinaryFileMeta(fileName, lRevList);
                                ret.add(lMeta);
                            }
                    );


        }
				
		return ret;
	}


	public 
	static 
	List<LargeMongoBinaryFileMeta> 
	getFilteredBucketDocs(	final MongoDbConnection mongoDbConnection, 
							final String dbName,
							final String bucketName,
							MongoClusterDb mongoClusterDb)  {

		List<LargeMongoBinaryFileMeta> ret = new ArrayList<>();
		
		GridFSBucket bucket = GridFSBuckets.create(mongoDbConnection.getMongoClient().getDatabase(dbName), bucketName);
		Bson query = Filters.eq("metadata.type", "zip archive");
		Bson sort = Sorts.ascending("uploadDate");

        for (GridFSFile fsFile : bucket.find(query).sort(sort)) {
            long timeStamp = fsFile.getUploadDate().getTime();
            String fileName = fsFile.getFilename();
            String id = fsFile.getObjectId().toString();
            long size = fsFile.getLength();
            ret.stream()
                    .filter(a -> a.getName().equals(fileName))
                    .findAny()
                    .ifPresentOrElse(a -> {
                                System.out.println("Present: " + fileName);
                                Optional<LargeMongoBinaryFileMeta> f = ret.stream().filter(b -> b.getName().equals(fileName)).findFirst();
                                int newRev = f.get().getRevList().stream().map(LargeMongoBinaryFileMetaRev::getRevision).toList().stream().max(Comparator.comparing(Integer::valueOf)).get() + 1;
                                LargeMongoBinaryFileMetaRev lRevNew = new LargeMongoBinaryFileMetaRev(id, newRev, timeStamp, size);
                                a.getRevList().add(lRevNew);
                            },
                            () -> {
                                System.out.println("Not Present: " + fileName);
                                LargeMongoBinaryFileMetaRev lRev = new LargeMongoBinaryFileMetaRev(id, 0, timeStamp, size);
                                List<LargeMongoBinaryFileMetaRev> lRevList = new ArrayList<LargeMongoBinaryFileMetaRev>();
                                lRevList.add(lRev);
                                LargeMongoBinaryFileMeta lMeta = new LargeMongoBinaryFileMeta(fileName, lRevList);
                                ret.add(lMeta);
                            }
                    );
        }
		return ret;
	}
	

	public 
	static 
	boolean 
	deleteLargeBinaryFile(	final MongoDbConnection mongoDbConnection, 
							final String dbName, 
							final String bucketName, 
							final String fileId) throws Exception {
		try	{
			GridFSBucket bucket = GridFSBuckets.create(mongoDbConnection.getMongoClient().getDatabase(dbName), bucketName);
			ObjectId oId = new ObjectId(fileId);
			bucket.delete(oId);
			return true;
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}

	public 
	static 
	String 
	addLargeBinaryFile(	final MongoClusterDb mongoClusterDb,
						final MongoDbConnection mongoDbConnection, 
						final String dbName, 
						final String bucketName, 
						final String fileName,
						final byte[] data,
						final LargeObjectAssociatedMetadata metadata_) throws Exception	{
		try	{
			final Document metadata = Document.parse(metadata_.toString());
			GridFSUploadOptions  options  = new GridFSUploadOptions().chunkSizeBytes(data.length + 1).metadata(metadata);
			MongoBucket.createBucket(mongoDbConnection, dbName, bucketName, mongoClusterDb);
			GridFSBucket bucket = GridFSBuckets.create(mongoDbConnection.getMongoClient().getDatabase(dbName), bucketName);
			GridFSUploadStream uploadStream = bucket.openUploadStream(fileName, options);
			uploadStream.write(data);
			ObjectId fileId = uploadStream.getObjectId() ;
			uploadStream.close() ;
			return fileId.toString();
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}


	public
	static
	String
	addLargeChatMessageFile(final MongoDbConnection mongoDbConnection,
						   	final String dbName,
						   	final String bucketName,
						   	final String fileName,
						   	final byte[] data,
						   	final String chatMessageWithoutAttachments) throws Exception {
		try	{
			final Document metadata = Document.parse(chatMessageWithoutAttachments);
			GridFSUploadOptions  options  = new GridFSUploadOptions().chunkSizeBytes(data.length + 1).metadata(metadata);
			MongoBucket.createBucket(mongoDbConnection, dbName, bucketName);
			GridFSBucket bucket = GridFSBuckets.create(mongoDbConnection.getMongoClient().getDatabase(dbName), bucketName);
			GridFSUploadStream uploadStream = bucket.openUploadStream(fileName, options);
			uploadStream.write(data);
			ObjectId fileId = uploadStream.getObjectId() ;
			uploadStream.close() ;
			return fileId.toString();
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}


}
