package com.widescope.rdbmsRepo.database.mongodb;


import java.nio.charset.StandardCharsets;
import java.util.*;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.mongodb.objects.*;
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
import com.widescope.rdbmsRepo.database.mongodb.repo.MongoClusterDb;


public class MongoBucket {


	public static LargeObjectAssociatedMetadata getMetadata(GridFSFile gridFSFile) {
		LargeObjectAssociatedMetadata meta = new LargeObjectAssociatedMetadata();
		Document metadata;
		try { metadata = gridFSFile.getMetadata(); } catch (Exception ignored) { metadata = new Document();  }
		if(metadata != null) {
			try { meta.setFolder(Objects.requireNonNull(metadata).getString("originalFolder")); } catch (Exception ignored) {}
			try { meta.setUserId(Objects.requireNonNull(metadata).getInteger("originalUserId")); } catch (Exception ignored) {}
			try { meta.setType(Objects.requireNonNull(metadata).getString("originalType")); } catch (Exception ignored) {}
			try { meta.setLastModified(Objects.requireNonNull(metadata).getInteger("originalLastModified")); } catch (Exception ignored) {}
		}
		return meta;
	}

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
            if(null != gridFSFile.getMetadata()) {
                f.setLargeObjectAssociatedMetadata( getMetadata(gridFSFile) );
            }
			byte[] bytesToWriteTo = new byte[(int)fileLength];
			if(fileLength == downloadStream.read(bytesToWriteTo)) {
				f.setFileStr(new String(bytesToWriteTo, StandardCharsets.UTF_8));
			}
			f.setFilename( fileName );
            f.setFileSize( fileLength );
            downloadStream.close();
			return f;
		}
		catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}


	public 
	static 
	void
	createBucket(	final MongoDbConnection mongoDbConnection, 
					final String dbName,
					final String bucketName,
					MongoClusterDb mongoClusterDb) throws Exception {
		byte[] data = new byte[1];
		data[0] = '\n';
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		LargeObjectAssociatedMetadata metadata_ = new LargeObjectAssociatedMetadata();
		final Document metadata = Document.parse(metadata_.toString());
		GridFSUploadOptions  options  = new GridFSUploadOptions().chunkSizeBytes(data.length + 1).metadata(metadata);
		GridFSBucket b = GridFSBuckets.create(database, bucketName);
		GridFSUploadStream uploadStream = b.openUploadStream("toBeDeleted.null", options);
		uploadStream.write(data);
		uploadStream.close() ;
		mongoClusterDb.addBucket(mongoDbConnection.getClusterId(), dbName, bucketName);
	}

	public
	static
	void
	createBucket(	final MongoDbConnection mongoDbConnection,
					 final String dbName,
					 final String bucketName)  {
		byte[] data = new byte[1];
		data[0] = '\n';
		LargeObjectAssociatedMetadata metadata_ = new LargeObjectAssociatedMetadata();
		final Document metadata = Document.parse(metadata_.toString());
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		GridFSUploadOptions  options  = new GridFSUploadOptions().chunkSizeBytes(data.length + 1).metadata(metadata);
		GridFSBucket b = GridFSBuckets.create(database, bucketName);
		GridFSUploadStream uploadStream = b.openUploadStream("toBeDeleted.null", options);
		uploadStream.write(data);
		uploadStream.close() ;
	}
	
	public 
	static 
	void
	deleteBucket(	final MongoDbConnection mongoDbConnection, 
					final String dbName,
					final String bucketName,
					MongoClusterDb mongoClusterDb) throws Exception {
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		GridFSBucket b = GridFSBuckets.create(database, bucketName);
		b.drop();
		mongoClusterDb.deleteBucket(mongoDbConnection.getClusterId(), dbName, bucketName);
	}

	public
	static
	void
	deleteBucket(	final MongoDbConnection mongoDbConnection,
					 final String dbName,
					 final String bucketName)  {
		MongoDatabase database = mongoDbConnection.getMongoClient().getDatabase(dbName);
		GridFSBucket b = GridFSBuckets.create(database, bucketName);
		b.drop();
	}

	public
	static
	List<LargeMongoBinaryFileSummary>
	getAllBucketDocsMetadata(final MongoDbConnection mongoDbConnection,
							 final String dbName,
							 final String bucketName) {
		List<LargeMongoBinaryFileSummary> ret = new ArrayList<>();
		GridFSBucket bucket = GridFSBuckets.create(mongoDbConnection.getMongoClient().getDatabase(dbName), bucketName);
		Bson sort = Sorts.ascending("uploadDate");
		for (GridFSFile fsFile : bucket.find().sort(sort)) {
			long uploadDate = fsFile.getUploadDate().getTime();
			String fileName = fsFile.getFilename();
			String id = fsFile.getObjectId().toString();
			long fileSize = fsFile.getLength();
			LargeObjectAssociatedMetadata meta = getMetadata(fsFile);
			ret.add(new LargeMongoBinaryFileSummary(fileName, fileSize, uploadDate, id, meta.getFolder(), meta.getUserId(), meta.getType(), meta.getLastModified()));
		}
		return ret;
	}


	// filter:str = "{\"filename\": /.*csv*/}"
	/**
	 * * Filter Example:
	 * 	 {"filename": "my_file.txt"}
	 * 	 {"filename": {"$int": ["test.csv", "test_10.csv"]}}
	 * 	 {"filename": .*csv*}   see above for python test
	 * 	 {"length": {"$gt": 10000}}
	 * 	 {"metadata.type": "image"}
	 * @param mongoDbConnection
	 * @param dbName
	 * @param bucketName
	 * @param filter
	 * @return
	 */
	public
	static
	List<LargeMongoBinaryFileSummary>
	getFilteredBucketDocsMetadata(final MongoDbConnection mongoDbConnection,
								  final String dbName,
								  final String bucketName,
								  final String filter) {
		final Document docFilter = Document.parse(filter);
		List<LargeMongoBinaryFileSummary> ret = new ArrayList<>();
		GridFSBucket bucket = GridFSBuckets.create(mongoDbConnection.getMongoClient().getDatabase(dbName), bucketName);
		Bson sort = Sorts.ascending("uploadDate");
		for (GridFSFile fsFile : bucket.find(docFilter).sort(sort)) {
			long uploadDate = fsFile.getUploadDate().getTime();
			String fileName = fsFile.getFilename();
			String id = fsFile.getObjectId().toString();
			long fileSize = fsFile.getLength();
			LargeObjectAssociatedMetadata meta = getMetadata(fsFile);
			ret.add(new LargeMongoBinaryFileSummary(fileName, fileSize, uploadDate, id, meta.getFolder(), meta.getUserId(), meta.getType(), meta.getLastModified()));
		}
		return ret;
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
	deleteManyFilesFromBucket(final MongoDbConnection mongoDbConnection,
							  final String dbName,
							  final String bucketName,
							  final String filter) throws Exception {
		try	{
			List<LargeMongoBinaryFileSummary> filesToDelete =
			getFilteredBucketDocsMetadata(mongoDbConnection, dbName, bucketName, filter) ;
			GridFSBucket bucket = GridFSBuckets.create(mongoDbConnection.getMongoClient().getDatabase(dbName), bucketName);
			for(LargeMongoBinaryFileSummary f: filesToDelete) {
				ObjectId oId = new ObjectId(f.getId());
				bucket.delete(oId);
			}
			return true;
		} catch(Exception ex)	{
			throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
		}
	}


	public 
	static 
	boolean
	deleteFileFromBucket(final MongoDbConnection mongoDbConnection,
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
	addFileToBucket(final MongoClusterDb mongoClusterDb,
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
