
package com.widescope.rdbmsRepo.database.rdbmsRepository;




import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.*;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbUtil;
import com.widescope.rdbmsRepo.database.tableFormat.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.ColMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.IndexMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.TableMetadata;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.types.ColumnTypeTable;
import com.widescope.rdbmsRepo.utils.SqlParser;

public class SqlMetadataWrapper {

	private static final Logger LOG = LogManager.getLogger(SqlMetadataWrapper.class);
	
	
	/**
	 * 
	 * @param metadata
	 * @param tableName
	 * @param toSystemType
	 * @return
	 * @throws Exception
	 */
	public static List<String> createIndexStm(	final TableMetadata metadata,
												final String tableName,
												final String toSystemType) throws Exception {
		List<String> stms = new ArrayList<>();
		List<IndexMetadata> pkList = new ArrayList<>();
		for (String indexName : metadata.getLstIndexMetadata().keySet()) {
			 Map<Integer, IndexMetadata> indexDef = metadata.getLstIndexMetadata().get(indexName);


            TreeMap<Integer, IndexMetadata> sortedIndexDef = new TreeMap<>(indexDef);
			 String stm = "";
			 StringBuilder colLst = new StringBuilder();
			 int counter = 0;
			 int maxCounter = sortedIndexDef.entrySet().size();
			 if(indexDef.values().stream().anyMatch(x -> x.getINDEXFORM().equalsIgnoreCase("PK"))) {
				 if(indexDef.size() == 1 && indexDef.get(0).getINDEX_NAME().contains("CONSTRAINT")) {
					 pkList.add( indexDef.get(0) );
				 }
				 continue;
			 }
			 switch(toSystemType.toUpperCase()) {
				case DbUtil.h2:
					stm = "CREATE INDEX IF NOT EXISTS " + indexName + " ON " + tableName.toUpperCase() + "(@colLst@);";
					break;
				 case DbUtil.postgresql, DbUtil.sqlserver:
					break;
                 default:
			}

			 
			 
			 for (Map.Entry<Integer, IndexMetadata> entry : sortedIndexDef.entrySet()) {
				 switch(toSystemType.toUpperCase()) {
					 case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
						if(counter == 0) {
							colLst.append(entry.getValue().getCOLUMN_NAME());
						} else { 
							if(maxCounter > 1) {
								colLst.append(",").append(entry.getValue().getCOLUMN_NAME());
							}
						}
						break;
                     default:
				}
				 counter++;
			 }
			 stm = stm.replace("@colLst@", colLst.toString());;
			 stms.add(stm);
        } 
		return stms;
	}


	/**
	 * Create table.Schema name is ignored in the call. To be extended beyond h2, postgresql, sqlserver.
	 */
	public static RdbmsTableSetup createTableStm(	final TableMetadata metadata,
													final String schemaName,
													final String tableName,
													final String toSystemType) {
		
		RdbmsTableSetup r = new RdbmsTableSetup();
		//CREATE SCHEMA IF NOT EXISTS schemaName;
		//SET SCHEMA schemaName;
		String createTableStm = "CREATE TABLE IF NOT EXISTS " + tableName + " ( @columns@ );";
		String insertTableStm;
		if(schemaName.isBlank() || schemaName.isEmpty()) {
			insertTableStm = "INSERT INTO " + tableName + "(@columns@) VALUES( @values@ );";
		} else {
			insertTableStm = "INSERT INTO " + schemaName + "." + tableName + "(@columns@) VALUES( @values@ );";
		}
		
		//StringBuilder columnsInsert = new StringBuilder();
		StringBuilder valuesInsert= new StringBuilder();
		
		
		StringBuilder columns = new StringBuilder();
		int maxCnt = metadata.getLstColMetadata().entrySet().size();
		for (var entry : metadata.getLstColMetadata().entrySet()) {
			System.out.println(entry.getKey() + "/" + entry.getValue());
			
			int COLUMN_SIZE = entry.getValue().COLUMN_SIZE; 		//	The precision of the column.
			int DECIMAL_DIGITS = entry.getValue().DECIMAL_DIGITS; 	//	The scale of the column.
			//int NUM_PREC_RADIX = entry.getValue().NUM_PREC_RADIX; 	//	The radix of the column.
			
			String COLUMN_NAME = entry.getValue().COLUMN_NAME;		//	The column name.
			//columnsInsert.append(COLUMN_NAME);
			
			switch(entry.getValue().TYPE_NAME.toUpperCase()) {
				case "IDENTITY":  /*H2*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" CHARACTER ");
							break;
						default:
							columns.append(entry.getKey()).append(" CHARACTER ");
					}
					break;
				
				case "CHARACTER":  /*H2*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" CHARACTER ");
							break;
						default:
							columns.append(entry.getKey()).append(" CHARACTER ");
					}
					break;
				case "CHAR": 		/*H2*/
				case "\"CHAR\"":   	/*POSTGRES*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" CHAR(").append(COLUMN_SIZE).append(") ");
							break;
						default:
							columns.append(entry.getKey()).append(" CHAR(").append(COLUMN_SIZE).append(") ");
					}
					break;
				case "CHARACTER VARYING": /*H2   Mapped to java.lang.String*/
				case "VARCHAR":/*H2*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" VARCHAR(").append(COLUMN_SIZE).append(") ");
							break;
						default:
							columns.append(entry.getKey()).append(" VARCHAR(").append(COLUMN_SIZE).append(") ");
					}
					break;
				
				case "VARCHAR_IGNORECASE": /*H2   Mapped to java.lang.String*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" VARCHAR_IGNORECASE(").append(COLUMN_SIZE).append(") ");
							break;
						default:
							columns.append(entry.getKey()).append(" VARCHAR_IGNORECASE(").append(COLUMN_SIZE).append(") ");
					}
					break;
				
				case "BINARY": /*H2 Mapped to byte[]*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" BINARY(").append(COLUMN_SIZE).append(") ");
							break;
						default:
							columns.append(entry.getKey()).append(" BINARY(").append(COLUMN_SIZE).append(") ");
					}
					break;
				
				case "BINARY VARYING": /*H2 Mapped to java.lang.String*/
				case "VARBINARY": /*H2 Mapped to java.lang.String*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" VARBINARY(").append(COLUMN_SIZE).append(") ");
							break;
						default:
							columns.append(entry.getKey()).append(" VARBINARY(").append(COLUMN_SIZE).append(") ");
					}
					break;
				
				case "BINARY LARGE OBJECT": /*H2 Mapped to java.sql.Blob*/
				case "BLOB": /*H2 Mapped to java.sql.Blob*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" BLOB(").append(COLUMN_SIZE).append(") ");
							break;
						default:
							columns.append(entry.getKey()).append(" BLOB(").append(COLUMN_SIZE).append(") ");
					}
					break;
				
				case "BOOLEAN": /*H2 Mapped to java.lang.Boolean*/
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" BOOLEAN ");
							break;
						default:
							columns.append(entry.getKey()).append(" BOOLEAN ");
					}
					break;
				
				case "TINYINT": /*H2 mapped to java.lang.Integer or java.lang.Byte*/ 
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" TINYINT ");
							break;
						default:
							columns.append(entry.getKey()).append(" TINYINT ");
					}
					break;
				
				case "SMALLINT": /*H2 mapped to java.lang.Integer or java.lang.Short*/
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" SMALLINT ");
							break;
						default:
							columns.append(entry.getKey()).append(" SMALLINT ");
					}
					break;
				
				case "INTEGER": /*H2 Mapped to java.lang.Integer*/
				case "INT": /*H2 Mapped to java.lang.Integer*/
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" INTEGER ");
							break;
						default:
							columns.append(entry.getKey()).append(" INTEGER ");
					}
					break;
				
				case "BIGINT": /*H2 Mapped to java.lang.Long*/
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" BIGINT ");
							break;
						default:
							columns.append(entry.getKey()).append(" BIGINT ");
						}
					break;
				
				case "NUMERIC": /*H2 Mapped to java.math.BigDecimal*/
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" NUMERIC(").append(COLUMN_SIZE).append(", ").append(DECIMAL_DIGITS).append(") ");
							break;
						default:
							columns.append(entry.getKey()).append(" NUMERIC(").append(COLUMN_SIZE).append(", ").append(DECIMAL_DIGITS).append(") ");
					}
					break;
				
				case "DECIMAL": /*H2 Mapped to java.math.BigDecimal*/
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" DECIMAL(").append(COLUMN_SIZE).append(", ").append(DECIMAL_DIGITS).append(") ");
							break;
						default:
							columns.append(entry.getKey()).append(" DECIMAL(").append(COLUMN_SIZE).append(", ").append(DECIMAL_DIGITS).append(") ");
					}
					break;
				case "CLOB": /*H2 Mapped to java.math.BigDecimal*/
				case "CHARACTER LARGE OBJECT":	
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" CLOB ");
							break;
						default:
							columns.append(entry.getKey()).append(" CLOB ");
					}
					break;
				case "REAL": /*H2 Mapped to java.lang.Double*/
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" REAL ");
							break;
						default:
							columns.append(entry.getKey()).append(" REAL ");
					}
					break;
				
				case "DOUBLE PRECISION": /*H2 Mapped to java.lang.Double*/
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" DOUBLE PRECISION ");
							break;
						default:
							columns.append(entry.getKey()).append(" DOUBLE PRECISION ");
					}
					break; 
				
				case "DECFLOAT": /*H2 Mapped to java.lang.Double*/
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" DECFLOAT(").append(COLUMN_SIZE).append(") ");
							break;
						default:
							columns.append(entry.getKey()).append(" DECFLOAT(").append(COLUMN_SIZE).append(") ");
					}
					break; 
				
				case "DATE": /*H2 mapped to java.time.LocalDate*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" DATE ");
							break;
						default:
							columns.append(entry.getKey()).append(" DATE ");
					}
					break; 
				case "DATE[]": /*POSTGRES mapped to java.time.LocalDate[]*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" DATE ");
							break;
						default:
							columns.append(entry.getKey()).append(" DATE ");
					}
					break; 
					
				case "DATERANGE": /*POSTGRES mapped to java.time.LocalDate[]*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" INTEGER ");
							break;
						default:
							columns.append(entry.getKey()).append(" INT ");
					}
					break;
					
				case "DATERANGE[]": /*POSTGRES mapped to java.time.LocalDate[]*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" INTEGER ");
							break;
						default:
							columns.append(entry.getKey()).append(" INT ");
					}
					break; 
				
				case "TIME": /*H2 mapped to java.sql.Time. java.time.LocalTime*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" TIME ");
							break;
						default:
							columns.append(entry.getKey()).append(" TIME ");
					}
					break;
				
				case "TIMESTAMP": /*H2 mapped to java.sql.Timestamp*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" TIMESTAMP ");
							break;
						default:
							columns.append(entry.getKey()).append(" TIMESTAMP ");
					}
					break; 
				
				case "TIME WITH TIME ZONE": /*H2*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" TIME ");
							break;
						default:
							columns.append(entry.getKey()).append(" TIME ");
					}
					break; 
				
				case "TIME($) WITH TIME ZONE": /*H2*/
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;
				
				case "INTERVAL YEAR": /*H2 Mapped to org.h2.api.Interval. java.time.Period*/
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" INTERVAL YEAR ");
							break;
						default:
							columns.append(entry.getKey()).append(" INTERVAL YEAR ");
					}
					break; 
				case "INTERVAL MONTH": /*H2 Mapped to org.h2.api.Interval. java.time.Period*/
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" INTERVAL YEAR ");
							break;
						default:
							columns.append(entry.getKey()).append(" INTERVAL YEAR ");
					}
					break; 
					
				case "INTERVAL DAY": /*H2 Mapped to org.h2.api.Interval. java.time.Period*/
					valuesInsert.append("@").append(COLUMN_NAME).append("@");
					switch(toSystemType.toUpperCase()) {
						case DbUtil.h2, DbUtil.postgresql, DbUtil.sqlserver:
							columns.append(entry.getKey()).append(" INTERVAL DAY ");
							break;
						default:
							columns.append(entry.getKey()).append(" INTERVAL DAY ");
					}
					break; 
				case "INTERVAL HOUR": /*H2*/
				case "INTERVAL MINUTE": /*H2*/
				case "INTERVAL SECOND": /*H2*/
				case "INTERVAL YEAR TO MONTH": /*H2*/
				case "INTERVAL DAY TO HOUR": /*H2*/
				case "INTERVAL DAY TO SECOND": /*H2*/
				case "INTERVAL HOUR TO SECOND": /*H2*/
				case "INTERVAL MINUTE TO SECOND": /*H2*/
					System.out.println("Default catch INTERVAL: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	 
				
				case "JAVA_OBJECT": /*H2*/
					System.out.println("Default catch JAVA_OBJECT: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	 
				case "ENUM": /*H2*/
					System.out.println("Default catch ENUM: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	 
				case "GEOMETRY": /*H2*/
					System.out.println("Default catch GEOMETRY: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	 
				case "JSON": /*H2*/
					System.out.println("Default catch JSON: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	 
				case "ARRAY": /*H2*/
					System.out.println("Default catch ARRAY: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	 
				case "ROW": /*H2*/
					System.out.println("Default catch ROW: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;
				case "BOX": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	 
				case "BOX[]": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	 
				case "BYTEA": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	  	 
				case "BYTEA[]": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	  
				case "CID": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	  	 
				case "CID[]": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	  
				case "CIRCLE": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	  
				case "CIRCLE[]": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	  	 
				
				default: /*if we got here most likely we have to find a different way*/
				
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert.append("'@").append(COLUMN_NAME).append("@'");
					break;	 
			
			}
			
			
			
			maxCnt --;
			if(maxCnt > 0) {
					columns.append(",");
			}
		}
		createTableStm = createTableStm.replace("@columns@", columns.toString());
		//insertTableStm = valuesInsert.toString().replace("@columns@", columnsInsert.toString());
		insertTableStm = valuesInsert.toString().replace("@values@", valuesInsert.toString());
		
		r.setCreateTableStm(createTableStm);
		r.setInsertTableStm(insertTableStm);
		return r;
	}
	
	public static boolean 
	isTable(String tableName, Connection conn) {
		DatabaseMetaData md;
		Set<String> tables = new HashSet<>();
		ResultSet tableRs = null;
		try {
			md = conn.getMetaData();
			tableRs = md.getTables(null, null, tableName, null);
			while (tableRs.next()) {
				String tName = tableRs.getString("TABLE_NAME");
				String tType = tableRs.getString("TABLE_TYPE");
				if(tType.equalsIgnoreCase("TABLE")) {
					tables.add(tName);
				}
			}
		} catch (SQLException e) {
			return false;
		} finally {
			DbUtil.closeDbHandles(conn, null, tableRs);
		}

        return tables.contains(tableName);

    }
	
	
	public static 
	TableMetadata getTableColumns(String tableName, Connection conn) throws Exception {
		TableMetadata ret = new TableMetadata();
		try	{
			DatabaseMetaData md = conn.getMetaData();
			ResultSet columns = md.getColumns(null, null, tableName, null);
			while (columns.next()) {
				ColMetadata cm = new ColMetadata();
				cm.TABLE_CAT= columns.getString("TABLE_CAT");
				cm.TABLE_SCHEM = columns.getString("TABLE_SCHEM");
				cm.TABLE_NAME = columns.getString("TABLE_NAME");
				cm.COLUMN_NAME = columns.getString("COLUMN_NAME");
				cm.DATA_TYPE =  columns.getInt("DATA_TYPE");
				cm.TYPE_NAME = columns.getString("TYPE_NAME");
				cm.COLUMN_SIZE = columns.getInt("COLUMN_SIZE");
				cm.BUFFER_LENGTH = columns.getInt("BUFFER_LENGTH");
				cm.DECIMAL_DIGITS = columns.getInt("DECIMAL_DIGITS");
				cm.NUM_PREC_RADIX = columns.getInt("NUM_PREC_RADIX");
				cm.NULLABLE = columns.getInt("NULLABLE");

				cm.REMARKS = columns.getString("REMARKS");
				cm.COLUMN_DEF = columns.getString("COLUMN_DEF");
				cm.SQL_DATA_TYPE = columns.getInt("SQL_DATA_TYPE");
				cm.SQL_DATETIME_SUB = columns.getInt("SQL_DATETIME_SUB");
				cm.CHAR_OCTET_LENGTH = columns.getInt("CHAR_OCTET_LENGTH");
				cm.ORDINAL_POSITION = columns.getInt("ORDINAL_POSITION");
				cm.IS_NULLABLE = columns.getString("IS_NULLABLE");
				//cm.SS_IS_SPARSE = columns.getString("SS_IS_SPARSE");
				//cm.SS_IS_COLUMN_SET = columns.getInt("SS_IS_COLUMN_SET");
				//cm.SS_IS_COMPUTED = columns.getInt("SS_IS_COMPUTED");
				cm.IS_AUTOINCREMENT = columns.getString("IS_AUTOINCREMENT");
				//cm.SS_UDT_CATALOG_NAME = columns.getString("SS_UDT_CATALOG_NAME");
				//cm.SS_UDT_SCHEMA_NAME = columns.getString("SS_UDT_SCHEMA_NAME");
				//cm.SS_UDT_ASSEMBLY_TYPE_NAME = columns.getString("SS_UDT_ASSEMBLY_TYPE_NAME");
				//cm.SS_XML_SCHEMACOLLECTION_CATALOG_NAME = columns.getString("SS_XML_SCHEMACOLLECTION_CATALOG_NAME");
				//cm.SS_XML_SCHEMACOLLECTION_SCHEMA_NAME = columns.getString("SS_XML_SCHEMACOLLECTION_SCHEMA_NAME");
				//cm.SS_XML_SCHEMACOLLECTION_NAME = columns.getString("SS_XML_SCHEMACOLLECTION_NAME");
				//cm.SS_DATA_TYPE = columns.getInt("SS_DATA_TYPE");
				ret.addColMetadata(cm);
			}
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, null, null);
		}
		return ret;
	}
	

	public static List<String> 
	getAllTableList(final DbConnectionInfo connectionDetailInfo,
					final String schemaName) throws Exception {
		Connection conn = null;
		List<String> ret = new ArrayList<String>();
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", new String[] { "TABLE" }); // SYSTEM TABLE
			if(schemaName!=null && !schemaName.isBlank() && !schemaName.isEmpty()) {
				while (rs.next()) {
					String schema = rs.getString("TABLE_SCHEM");
					String tableName = rs.getString("TABLE_NAME");
					/*
					String tableCat = rs.getString("TABLE_CAT");
					String tableType = rs.getString("TABLE_TYPE");
					String remarks = rs.getString("REMARKS");
					String typeSchema = rs.getString("TYPE_SCHEM");
					String typeName = rs.getString("TYPE_NAME");
					*/
					if(schema.toUpperCase().compareTo(schemaName.toUpperCase()) == 0) {
						ret.add(tableName);
					}
				}
			} else {
				while (rs.next()) {
					
					
					String tableName = rs.getString("TABLE_NAME");
					/*
					String tableCat = rs.getString("TABLE_CAT");
					String tableType = rs.getString("TABLE_TYPE");
					String remarks = rs.getString("REMARKS");
					String typeSchema = rs.getString("TYPE_SCHEM");
					String typeName = rs.getString("TYPE_NAME");
					String refGen = rs.getString("REF_GENERATION");
					*/
					if(tableName.compareToIgnoreCase("TUTORIALS_TBL") == 0) {
						//String schema = rs.getString("TABLE_SCHEM");
						
					}
					
					
					ret.add(rs.getString(3));
				}
			}
			
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, null, null);
		}
		return ret;
	}
	
	
	public static List<String> 
	getSystemTableList(	final DbConnectionInfo connectionDetailInfo,
						final String schemaName) throws Exception 
	{
		Connection conn = null;
		List<String> ret = new ArrayList<String>();
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", new String[] { "SYSTEM TABLE" }); // SYSTEM TABLE
			if(schemaName!=null && !schemaName.isBlank() && !schemaName.isEmpty()) {
				while (rs.next()) {
					String schema = rs.getString("TABLE_SCHEM");
					String tableName = rs.getString("TABLE_NAME");
					/*
					String tableCat = rs.getString("TABLE_CAT");
					String tableType = rs.getString("TABLE_TYPE");
					String remarks = rs.getString("REMARKS");
					String typeSchema = rs.getString("TYPE_SCHEM");
					String typeName = rs.getString("TYPE_NAME");
					*/
					if(schema.toUpperCase().compareTo(schemaName.toUpperCase()) == 0) {
						ret.add(tableName);
					}
				}
			} else {
				while (rs.next()) {
					
					
					String tableName = rs.getString("TABLE_NAME");
					/*
					String tableCat = rs.getString("TABLE_CAT");
					String tableType = rs.getString("TABLE_TYPE");
					String remarks = rs.getString("REMARKS");
					String typeSchema = rs.getString("TYPE_SCHEM");
					String typeName = rs.getString("TYPE_NAME");
					String refGen = rs.getString("REF_GENERATION");
					*/
					if(tableName.compareToIgnoreCase("TUTORIALS_TBL") == 0) {
						//String schema = rs.getString("TABLE_SCHEM");
					}
					ret.add(rs.getString(3));
				}
			}
			
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, null, null);
		}
		return ret;
	}
	
	
	
	public static TableMetadata
	getIndexesList(	final DbConnectionInfo connectionDetailInfo,
					final String catalog,
					final String schema,
					final String tableName,
					TableMetadata ret) throws Exception 
	{
		Connection conn = null;
		
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs1 = md.getIndexInfo(catalog, schema, tableName, true, false); // regular Index
			while (rs1.next()) {
				IndexMetadata i = new IndexMetadata();
				i.setINDEXFORM("INDEX");
				i.setCOLUMN_NAME(rs1.getString("COLUMN_NAME"));
				i.setINDEX_NAME(rs1.getString("INDEX_NAME"));
				i.setNON_UNIQUE(rs1.getBoolean("NON_UNIQUE"));
				i.setTYPE(rs1.getShort("TYPE"));
				i.setASC_OR_DESC(rs1.getString("ASC_OR_DESC "));
				i.setORDINAL_POSITION(rs1.getInt("ORDINAL_POSITION"));
				i.setCARDINALITY(rs1.getInt("CARDINALITY"));
				
				ret.addIndexMetadata(i);
			}
			
			ResultSet rs2 = md.getPrimaryKeys(catalog, schema, tableName); // Primary Keys
			while (rs2.next()) {
				IndexMetadata i = new IndexMetadata();
				i.setINDEXFORM("PK");
				i.setCOLUMN_NAME(rs2.getString("COLUMN_NAME"));
				i.setINDEX_NAME(rs2.getString("PK_NAME"));
				i.setKEY_SEQ(rs2.getShort("KEY_SEQ"));
				ret.addIndexMetadata(i);
			}
			
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, null, null);
		}
		return ret;
	}
	
	
	public static TableMetadata
	getIndexesList(	final Connection conn,
					final String catalog,
					final String schema,
					final String tableName,
					TableMetadata ret) throws Exception 
	{
		try	{
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs1 = md.getIndexInfo(catalog, schema, tableName, false, false); // regular Index
			while (rs1.next()) {
				IndexMetadata i = new IndexMetadata();
				i.setINDEXFORM("INDEX");
				i.setCOLUMN_NAME(rs1.getString("COLUMN_NAME"));
				i.setINDEX_NAME(rs1.getString("INDEX_NAME"));
				i.setNON_UNIQUE(rs1.getBoolean("NON_UNIQUE"));
				i.setTYPE(rs1.getShort("TYPE"));
				i.setASC_OR_DESC(rs1.getString("ASC_OR_DESC"));
				i.setORDINAL_POSITION(rs1.getInt("ORDINAL_POSITION"));
				i.setCARDINALITY(rs1.getInt("CARDINALITY"));
				
				ret.addIndexMetadata(i);
			}
			
			ResultSet rs2 = md.getPrimaryKeys(catalog, schema, tableName); // Primary Keys
			while (rs2.next()) {
				IndexMetadata i = new IndexMetadata();
				i.setINDEXFORM("PK");
				i.setCOLUMN_NAME(rs2.getString("COLUMN_NAME"));
				i.setINDEX_NAME(rs2.getString("PK_NAME"));
				i.setKEY_SEQ(rs2.getShort("KEY_SEQ"));
				ret.addIndexMetadata(i);
			}
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, null, null);
		}
		return ret;
	}
	
	public static List<String> 
	getAllTableList(final Connection conn,
				final String schemaName) throws Exception 
	{
		List<String> ret = new ArrayList<String>();
		try	{
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", new String[] { "TABLE" });
			if(schemaName!=null && !schemaName.isBlank() && !schemaName.isEmpty()) {
				while (rs.next()) {
					String schema = rs.getString("TABLE_SCHEM");
					String tableName = rs.getString("TABLE_NAME");
					/*
					String tableCat = rs.getString("TABLE_CAT");
					String tableType = rs.getString("TABLE_TYPE");
					String remarks = rs.getString("REMARKS");
					String typeSchema = rs.getString("TYPE_SCHEM");
					String typeName = rs.getString("TYPE_NAME");
					*/
					if(schema.toUpperCase().compareTo(schemaName.toUpperCase()) == 0) {
						ret.add(tableName);
					}
				}
			} else {
				while (rs.next()) {
					String tableName = rs.getString("TABLE_NAME");
					/*
					String tableCat = rs.getString("TABLE_CAT");
					String tableType = rs.getString("TABLE_TYPE");
					String remarks = rs.getString("REMARKS");
					String typeSchema = rs.getString("TYPE_SCHEM");
					String typeName = rs.getString("TYPE_NAME");
					String refGen = rs.getString("REF_GENERATION");
					*/
					if(tableName.compareToIgnoreCase("CHANGE_ME_TBL") == 0) {
						//String schema = rs.getString("TABLE_SCHEM");
					}
					ret.add(rs.getString(3));
				}
			}
			
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally {
			DbUtil.closeDbHandles(conn, null, null);
		}
		return ret;
	}
	
	
	public static List<String> 
	getSystemTableList(	final Connection conn,
						final String schemaName) throws Exception 
	{
		List<String> ret = new ArrayList<String>();
		try	{
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", new String[] { "SYSTEM TABLE" });
			if(schemaName!=null && !schemaName.isBlank() && !schemaName.isEmpty()) {
				while (rs.next()) {
					String schema = rs.getString("TABLE_SCHEM");
					String tableName = rs.getString("TABLE_NAME");

					String tableCat = rs.getString("TABLE_CAT");
					String tableType = rs.getString("TABLE_TYPE");
					String remarks = rs.getString("REMARKS");
					String typeSchema = rs.getString("TYPE_SCHEM");
					String typeName = rs.getString("TYPE_NAME");

					if(schema.toUpperCase().compareTo(schemaName.toUpperCase()) == 0) {
						ret.add(tableName);
					}
				}
			} else {
				while (rs.next()) {
					String tableName = rs.getString("TABLE_NAME");

					String tableCat = rs.getString("TABLE_CAT");
					String tableType = rs.getString("TABLE_TYPE");
					String remarks = rs.getString("REMARKS");
					String typeSchema = rs.getString("TYPE_SCHEM");
					String typeName = rs.getString("TYPE_NAME");
					String refGen = rs.getString("REF_GENERATION");

					ret.add(rs.getString(3));
				}
			}
			
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
		}
		return ret;
	}
	

	public static List<String> 
	getSchemas(	final DbConnectionInfo connectionDetailInfo) throws Exception {
		if(connectionDetailInfo == null ) {
			AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, " exception: connectionDetailInfo is null");
			return new ArrayList<> ();
		}
		
		Connection conn = null;
		List<String> ret = new ArrayList<>();
		ResultSet rs = null;
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			DatabaseMetaData md = conn.getMetaData();
			rs = md.getSchemas();
			while (rs.next()) {
				ret.add(rs.getString(1));
			}
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, null, rs);
		}
		return ret;
	}

	public static TableMetadata 
	getTableColumns(final DbConnectionInfo connectionDetailInfo,
					final String tableName) throws Exception
	{
		Connection conn = null;
		TableMetadata ret = new TableMetadata();
		try	{
			Class.forName(connectionDetailInfo.getJdbcDriver());
			conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
			DatabaseMetaData md = conn.getMetaData();
			ResultSet columns = md.getFunctionColumns(null, null, tableName, null);
			while (columns.next()) {
				ColMetadata cm = new ColMetadata();
				cm.TABLE_CAT= columns.getString("TABLE_CAT");
				cm.TABLE_SCHEM = columns.getString("TABLE_SCHEM");
				cm.TABLE_NAME = columns.getString("TABLE_NAME");
				cm.COLUMN_NAME = columns.getString("COLUMN_NAME");
				cm.DATA_TYPE =  columns.getInt("DATA_TYPE");
				cm.TYPE_NAME = columns.getString("TYPE_NAME");
				cm.COLUMN_SIZE = columns.getInt("COLUMN_SIZE");
				cm.BUFFER_LENGTH = columns.getInt("BUFFER_LENGTH");
				cm.DECIMAL_DIGITS = columns.getInt("DECIMAL_DIGITS");
				cm.NUM_PREC_RADIX = columns.getInt("NUM_PREC_RADIX");
				cm.NULLABLE = columns.getInt("NULLABLE");

				cm.REMARKS = columns.getString("REMARKS");
				cm.COLUMN_DEF = columns.getString("COLUMN_DEF");
				cm.SQL_DATA_TYPE = columns.getInt("SQL_DATA_TYPE");
				cm.SQL_DATETIME_SUB = columns.getInt("SQL_DATETIME_SUB");
				cm.CHAR_OCTET_LENGTH = columns.getInt("CHAR_OCTET_LENGTH");
				cm.ORDINAL_POSITION = columns.getInt("ORDINAL_POSITION");
				cm.IS_NULLABLE = columns.getString("IS_NULLABLE");
				cm.SS_IS_SPARSE = columns.getString("SS_IS_SPARSE");
				cm.SS_IS_COLUMN_SET = columns.getInt("SS_IS_COLUMN_SET");
				cm.SS_IS_COMPUTED = columns.getInt("SS_IS_COMPUTED");
				cm.IS_AUTOINCREMENT = columns.getString("IS_AUTOINCREMENT");
				cm.SS_UDT_CATALOG_NAME = columns.getString("SS_UDT_CATALOG_NAME");
				cm.SS_UDT_SCHEMA_NAME = columns.getString("SS_UDT_SCHEMA_NAME");
				cm.SS_UDT_ASSEMBLY_TYPE_NAME = columns.getString("SS_UDT_ASSEMBLY_TYPE_NAME");
				cm.SS_XML_SCHEMACOLLECTION_CATALOG_NAME = columns.getString("SS_XML_SCHEMACOLLECTION_CATALOG_NAME");
				cm.SS_XML_SCHEMACOLLECTION_SCHEMA_NAME = columns.getString("SS_XML_SCHEMACOLLECTION_SCHEMA_NAME");
				cm.SS_XML_SCHEMACOLLECTION_NAME = columns.getString("SS_XML_SCHEMACOLLECTION_NAME");
				cm.SS_DATA_TYPE = columns.getInt("SS_DATA_TYPE");
				ret.addColMetadata(cm);
			}
		} catch (Exception e)	{
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			DbUtil.closeDbHandles(conn, null, null);
		}
		return ret;
	}
	
	
	public static boolean 
	isTableCompat(final TableMetadata m, final Map<String, String> metadata) {
		boolean ret = true;
		for (var entry : metadata.entrySet()) {
		    System.out.println(entry.getKey() + "/" + entry.getValue());
		    if(m.getLstColMetadata().containsKey(entry.getKey())) {
		    	if(!Objects.equals(m.getLstColMetadata().get(entry.getKey()).TYPE_NAME, "")) {
		    		ret = false;
		    		break;
		    	}
		    } else {
		    	ret = false;
	    		break;
		    }
		}
		
		return ret;
		
	}
	

	public static String createRdbmsTableStm(	final Map<String, String> metadata, 
												final String tableName) {
		String ret = "CREATE TABLE IF NOT EXISTS " + tableName + " ( @columns@ );";
		StringBuilder columns = new StringBuilder();
		int maxCnt = metadata.entrySet().size();
		for (var entry : metadata.entrySet()) {
		    String val = entry.getValue();
		    System.out.println(entry.getKey() + "/" + val);
		    if(val.compareToIgnoreCase("JAVA.LANG.STRING") == 0 || val.compareToIgnoreCase("VARCHAR") == 0) {
		    	columns.append(entry.getKey()).append(" VARCHAR(MAX) ");
		    } else if(val.compareToIgnoreCase("JAVA.MATH.BIGDECIMAL") == 0 || val.compareToIgnoreCase("DECIMAL") == 0) {
		    	columns.append(entry.getKey()).append(" int ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.DOUBLE") == 0 || val.compareToIgnoreCase("DOUBLE") == 0 || val.compareToIgnoreCase("MONEY") == 0) {
		    	columns.append(entry.getKey()).append(" DOUBLE ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.FLOAT") == 0 || val.compareToIgnoreCase("FLOAT") == 0) {
		    	columns.append(entry.getKey()).append(" FLOAT ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.BIGINTEGER") == 0 || val.compareToIgnoreCase("BIGINT") == 0) {
		    	columns.append(entry.getKey()).append(" BIGINT ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.INTEGER") == 0 || val.compareToIgnoreCase("INTEGER") == 0) {
		    	columns.append(entry.getKey()).append(" INTEGER ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.LONG") == 0 || val.compareToIgnoreCase("LONG") == 0) {
		    	columns.append(entry.getKey()).append(" LONG ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.SHORT") == 0 || val.compareToIgnoreCase("SHORT") == 0) {
				columns.append(entry.getKey()).append(" SHORT ");
			} else {
		    	columns.append(entry.getKey()).append(" VARCHAR(MAX) ");
		    }
		    
		    maxCnt --;
		    if(maxCnt > 0) {
		    	columns.append(",");
		    }
		}
		ret = ret.replace("@columns@", columns.toString());
		return ret;
	}
	

	public static String createRdbmsTableStm(final TableDefinition tableDefinition) {
		String ret = "CREATE TABLE IF NOT EXISTS " + tableDefinition.getTableName() + " ( @columns@ );";
		StringBuilder columns = new StringBuilder();
		int maxCnt = tableDefinition.getMetadata().size();
		
		for (ColumnDefinition entry : tableDefinition.getMetadata()) {
		    String val = entry.getColumnType();
		    System.out.println(entry.getColumnName() + "/" + entry.getColumnType());
		    if(val.compareToIgnoreCase("JAVA.LANG.STRING") == 0 || val.compareToIgnoreCase("VARCHAR") == 0) {
		    	columns.append(entry.getColumnName()).append(" VARCHAR(").append(entry.getColumnPrecision()).append(") ");
		    } else if(val.compareToIgnoreCase("JAVA.MATH.BIGDECIMAL") == 0 || val.compareToIgnoreCase("DECIMAL") == 0) {
		    	columns.append(entry.getColumnName()).append(" int ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.DOUBLE") == 0 || val.compareToIgnoreCase("DOUBLE") == 0 ||	val.compareToIgnoreCase("MONEY") == 0) {
		    	columns.append(entry.getColumnName()).append(" DOUBLE(").append(entry.getColumnPrecision()).append(", ").append(entry.getColumnScale()).append(") ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.FLOAT") == 0 || val.compareToIgnoreCase("FLOAT") == 0) {
		    	columns.append(entry.getColumnName()).append(" FLOAT(").append(entry.getColumnPrecision()).append(", ").append(entry.getColumnScale()).append(") ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.BIGINTEGER") == 0 || val.compareToIgnoreCase("BIGINT") == 0) {
		    	columns.append(entry.getColumnName()).append(" BIGINT ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.INTEGER") == 0 || val.compareToIgnoreCase("INTEGER") == 0) {
		    	columns.append(entry.getColumnName()).append(" INTEGER ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.LONG") == 0 || val.compareToIgnoreCase("LONG") == 0) {
		    	columns.append(entry.getColumnName()).append(" LONG ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.SHORT") == 0 || val.compareToIgnoreCase("SHORT") == 0) {
		    	columns.append(entry.getColumnName()).append(" SHORT ");
		    } else {
		    	columns.append(entry.getColumnName()).append(" VARCHAR(MAX) ");
		    }
		    
		    maxCnt --;
		    if(maxCnt > 0) {
		    	columns.append(",");
		    }
		}
		ret = ret.replace("@columns@", columns.toString());
		return ret;
	}
	
	
	
	/**
	 * Creates an insert statement based on metadata provided
	 * @param metadata
	 * @param tableName
	 * @return
	 */
	public static String generateInsertTableStm(final Map<String, String> metadata, 
												final String schemaName,
												final String tableName) {

		String ret;
		if(schemaName.isBlank() || schemaName.isEmpty()) {
			ret = "INSERT INTO " + tableName + "(@columns@) VALUES( @values@ );";
		} else {
			ret = "INSERT INTO " + schemaName+ "." + tableName + "(@columns@) VALUES( @values@ );";
		}
			
		
		StringBuilder columns = new StringBuilder();
		StringBuilder values= new StringBuilder();
		
		int maxCnt = metadata.entrySet().size();
		for (var entry : metadata.entrySet()) {
			columns.append(entry.getKey());
			if(entry.getValue().toUpperCase().compareTo("JAVA.LANG.STRING") == 0 ||
					entry.getValue().compareTo("TEXT") == 0 ||
					entry.getValue().compareTo("VARCHAR") == 0 ||
					entry.getValue().compareTo("DATE") == 0	) {
				values.append("'@").append(entry.getKey()).append("@'");
			} else if(entry.getValue().toUpperCase().compareTo("JAVA.MATH.BIGDECIMAL") == 0 ||
					entry.getValue().compareTo("JAVA.LANG.DOUBLE") == 0 ||
					entry.getValue().compareTo("JAVA.LANG.FLOAT") == 0 ||
					entry.getValue().compareTo("JAVA.LANG.BIGINTEGER") == 0 ||
					entry.getValue().compareTo("BIGINT") == 0 ||
					entry.getValue().compareTo("JAVA.LANG.INTEGER") == 0 ||
					entry.getValue().compareTo("JAVA.LANG.Long") == 0 ||
					entry.getValue().compareTo("LONG") == 0 ||
					entry.getValue().compareTo("INTEGER") == 0 ||
					entry.getValue().compareTo("BYTE") == 0 ||
					entry.getValue().compareTo("BOOLEAN") == 0 ||
					entry.getValue().compareTo("SHORT") == 0 ||
					entry.getValue().compareTo("UNSIGNED_LONG") == 0 ||
					entry.getValue().compareTo("DOUBLE") == 0 ||
					entry.getValue().compareTo("FLOAT") == 0 ||
					entry.getValue().compareTo("HALF_FLOAT") == 0 ||
					entry.getValue().compareTo("SCALED_FLOAT") == 0 ||
					entry.getValue().compareTo("BINARY") == 0 ||
					entry.getValue().compareTo("JAVA.LANG.SHORT") == 0 ||
					entry.getValue().compareTo("JAVA.MATH.BIGDECIMAL") == 0 ||
					entry.getValue().compareTo("JAVA.LANG.NUMBER") == 0 ||
					entry.getValue().compareTo("JAVA.LANG.BOOLEAN") == 0
					) {
				values.append("@").append(entry.getKey()).append("@");
			} else {
				values.append("@").append(entry.getKey()).append("@");
			}
			
			
			maxCnt --;
			if(maxCnt > 0) {
				columns.append(", ");
				values.append(", ");
			}
		}
		
		ret = ret.replace("@columns@", columns.toString());
		ret = ret.replace("@values@", values.toString());
		return ret;
	}

	public static Map<String, String>  generateMetadata(final List<RowValue> tableData) {
		Map<String, String> metadata = new HashMap<>();
		for (CellValue row : tableData.get(0).getRow()) {
			if (row.getColumnValue() instanceof String) {
				metadata.put(row.getColumnName(), "VARCHAR2");
			} else if (row.getColumnValue() instanceof Date) {
				metadata.put(row.getColumnName(), "DATE");
			} else if (row.getColumnValue() instanceof Integer) {
				metadata.put(row.getColumnName(), "INTEGER");
			} else if (row.getColumnValue() instanceof Double) {
				metadata.put(row.getColumnName(), "DOUBLE PRECISION");
			} else if (row.getColumnValue() instanceof Float ) {
				metadata.put(row.getColumnName(), "DECFLOAT");
			} else if (row.getColumnValue() instanceof BigInteger ) {
				metadata.put(row.getColumnName(), "BIGINT");
			} else if (row.getColumnValue() instanceof Long ) {
				metadata.put(row.getColumnName(), "BIGINT");
			} else if (row.getColumnValue() instanceof Short) {
					metadata.put(row.getColumnName(), "TINYINT");
			} else if(row.getColumnValue() instanceof Boolean ) {
				metadata.put(row.getColumnName(), "BOOLEAN");
			} else {
				metadata.put(row.getColumnName(), "VARCHAR2");
			}
		}

		return metadata;
	}

	public static Map<String, Object>  generateMRows(RowValue rValue) {
		Map<String, Object> rows = new HashMap<>();
		for(CellValue row: rValue.getRow()) {
			rows.put(row.getColumnName(), row.getColumnValue());
		}
		return rows;
	}
	

	public static String generateInsertTableStm(final TableDefinition tableDefinition, 
												final String schemaName) {

		String ret = "";
		if(schemaName.isBlank() || schemaName.isEmpty()) {
			ret = "INSERT INTO " + tableDefinition.getTableName() + "(@columns@) VALUES( @values@ );";
		} else {
			ret = "INSERT INTO " + schemaName+ "." + tableDefinition.getTableName() + "(@columns@) VALUES( @values@ );";
		}
			
		
		StringBuilder columns = new StringBuilder();
		StringBuilder values= new StringBuilder();
		
		int maxCnt = tableDefinition.getMetadata().size();
		for (ColumnDefinition entry : tableDefinition.getMetadata()) {
			columns.append(entry.getColumnName());
			if(entry.getColumnName().toUpperCase().compareTo("JAVA.LANG.STRING") == 0 ||
					entry.getColumnType().compareTo("TEXT") == 0 ||
					entry.getColumnType().compareTo("VARCHAR") == 0 ||
					entry.getColumnType().compareTo("DATE") == 0	) {
				values.append("'@").append(entry.getColumnName()).append("@'");
			} else if(entry.getColumnType().toUpperCase().compareTo("JAVA.MATH.BIGDECIMAL") == 0 ||
					entry.getColumnType().compareTo("JAVA.LANG.DOUBLE") == 0 ||
					entry.getColumnType().compareTo("JAVA.LANG.FLOAT") == 0 ||
					entry.getColumnType().compareTo("JAVA.LANG.BIGINTEGER") == 0 ||
					entry.getColumnType().compareTo("BIGINT") == 0 ||
					entry.getColumnType().compareTo("JAVA.LANG.INTEGER") == 0 ||
					entry.getColumnType().compareTo("JAVA.LANG.Long") == 0 ||
					entry.getColumnType().compareTo("LONG") == 0 ||
					entry.getColumnType().compareTo("INTEGER") == 0 ||
					entry.getColumnType().compareTo("BYTE") == 0 ||
					entry.getColumnType().compareTo("BOOLEAN") == 0 ||
					entry.getColumnType().compareTo("SHORT") == 0 ||
					entry.getColumnType().compareTo("UNSIGNED_LONG") == 0 ||
					entry.getColumnType().compareTo("DOUBLE") == 0 ||
					entry.getColumnType().compareTo("FLOAT") == 0 ||
					entry.getColumnType().compareTo("HALF_FLOAT") == 0 ||
					entry.getColumnType().compareTo("SCALED_FLOAT") == 0 ||
					entry.getColumnType().compareTo("BINARY") == 0 ||
					entry.getColumnType().compareTo("JAVA.LANG.SHORT") == 0 ||
					entry.getColumnType().compareTo("JAVA.MATH.BIGDECIMAL") == 0 ||
					entry.getColumnType().compareTo("JAVA.LANG.NUMBER") == 0 ||
					entry.getColumnType().compareTo("JAVA.LANG.BOOLEAN") == 0
					) {
				values.append("@").append(entry.getColumnName()).append("@");
			} else {
				values.append("@").append(entry.getColumnName()).append("@");
			}
			
			
			maxCnt --;
			if(maxCnt > 0) {
				columns.append(", ");
				values.append(", ");
			}
		}
		
		ret = ret.replace("@columns@", columns.toString());
		ret = ret.replace("@values@", values.toString());
		return ret;
	}
	
	public static String generateInsertTableStm(final List<TableFormatExtMetadataOutput> extendedMetadata, 
												final String schemaName,
												final String tableName) {

		String ret ;
		if(schemaName.isBlank() || schemaName.isEmpty()) {
			ret = "INSERT INTO " + tableName + "(@columns@) VALUES( @values@ );";
		} else {
			ret = "INSERT INTO " + schemaName+ "." + tableName + "(@columns@) VALUES( @values@ );";
		}
		
		
		StringBuilder columns = new StringBuilder();
		StringBuilder values= new StringBuilder();
		
		int maxCnt = extendedMetadata.size();
		for (var entry : extendedMetadata) {
			columns.append(entry.getColName());
			if(entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("JAVA.LANG.STRING") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("TEXT") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("VARCHAR") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("DATE") == 0	) {
				values.append("'@").append(entry.getColName()).append("@'");
			} else if(entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("JAVA.MATH.BIGDECIMAL") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("JAVA.LANG.DOUBLE") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("JAVA.LANG.FLOAT") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("JAVA.LANG.BIGINTEGER") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("BIGINT") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("JAVA.LANG.INTEGER") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("JAVA.LANG.Long") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("LONG") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("INTEGER") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("BYTE") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("BOOLEAN") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("SHORT") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("UNSIGNED_LONG") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("DOUBLE") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("FLOAT") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("HALF_FLOAT") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("SCALED_FLOAT") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("BINARY") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("JAVA.LANG.SHORT") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("JAVA.LANG.NUMBER") == 0 ||
				entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("JAVA.LANG.BOOLEAN") == 0
			) {
				values.append("'@").append(entry.getColName()).append("@'");
			} else {
				values.append("@").append(entry.getColName()).append("@");
			}

			maxCnt --;
			if(maxCnt > 0) {
				columns.append(", ");
				values.append(", ");
			}
		}
		
		ret = ret.replace("@columns@", columns.toString());
		ret = ret.replace("@values@", values.toString());
		return ret;
	}

	public static String 
	generateExecutableInsertTableStm(	String insertStm,
										final Map<String, String> metadata, /* colName/col java type*/
										final Map<String, Object> row /*colName/value*/) {
		for (var entry : metadata.entrySet()) {
			String str = row.get(entry.getKey()).toString().replaceAll("'", "''");
			insertStm = insertStm.replace("@" + entry.getKey()+ "@", str);
		}
		return insertStm;
	}
	

	
	
	public static String 
	generateExecutableInsertTableStm(	String insertStm,
										final TableFormatRowOutput row ) {
		for (TableFormatCellOutput entry : row.getRow()) {
			/*replace single quote with two single quotes*/
			String str = entry.getValue().toString().replaceAll("'", "''");  
			insertStm = insertStm.replace("@" + entry.getColName() + "@", str);
		}
		return insertStm;
	}
	
	
	
	public static 
	TableFormatMap 
	execAdhocForMigration(	final String schemaUniqueName, 
							final String sqlStatement
							) throws Exception {
		TableFormatMap migrationReturn = new TableFormatMap();
		
		Map<String, String> m = new HashMap<String, String>();
		Map<String, ResultMetadata> mExt = new HashMap<String, ResultMetadata>();
		List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
		if(SqlParser.isSqlDQL(sqlStatement)) {
			SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(schemaUniqueName);
			DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(db);
			Connection conn = null;
			Statement statement = null;
			ResultSet rs ;
			try	{
				Class.forName(connectionDetailInfo.getJdbcDriver());
				conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				rs = statement.executeQuery(sqlStatement);
				
				
				ColumnTypeTable columnTypeTable = new ColumnTypeTable();
				ResultSetMetaData metaData = rs.getMetaData();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String colTypeName = columnTypeTable.columnIdToName.get(metaData.getColumnType(i)) ;
					
					
					if(colTypeName == null) {	colTypeName = metaData.getColumnTypeName(i);	}
					
					m.put(metaData.getColumnName(i), colTypeName);
		    		ResultMetadata rm = new ResultMetadata(	metaData.getColumnName(i), 
		    												metaData.getColumnType(i), 
		    												colTypeName, 
		    												metaData.getPrecision(i), 
		    												metaData.getScale(i));
		    		
		    		mExt.put(metaData.getColumnName(i), rm);
				}

				
	        	
				while (rs.next()) {
					Map<String,Object> row = new HashMap<String,Object>();
		        	for (int i = 1; i <= metaData.getColumnCount(); i++) {
		        		String columnName = metaData.getColumnName(i);
		        		int columnType = metaData.getColumnType(i);
	        		
		        		if(columnType == Types.NVARCHAR || columnType == Types.VARCHAR ) {
		        			String val = rs.getString(columnName);
		        			row.put(columnName, val);
		        		}
		        		else if(columnType == Types.INTEGER || columnType == Types.BIGINT || columnType == Types.SMALLINT) {
		        			Integer val = rs.getInt(columnName);
		        			row.put(columnName, val);
		        		}
		        		else if(columnType == Types.FLOAT || columnType == Types.DOUBLE || columnType == Types.DECIMAL || columnType == Types.REAL) {
		        			Float val = rs.getFloat(columnName);
		        			row.put(columnName, val);
		        		}
		        		else if(columnType == Types.TIMESTAMP)	{
		        			Timestamp val = rs.getTimestamp(columnName);
		        			row.put(columnName, val);
		        		}
		        		else if(columnType == Types.DATE) {
		        			Date val = rs.getDate(columnName);
		        			row.put(columnName, val);
		        		}
		        		else if(columnType == Types.CLOB) {
		        			row.put(columnName, rs.getClob(columnName));
		        		}
		        		else if(columnType == Types.BINARY || columnType == Types.LONGVARBINARY || columnType == Types.VARBINARY) {
		        			row.put(columnName, rs.getBytes(columnName));
		        		}
		        		
		        		else if(columnType == Types.BOOLEAN) {
		        			row.put(columnName, rs.getBoolean(columnName));
		        		}
		        		else {
		        			row.put(columnName, rs.getString(columnName));
		        		}
		            }
		        	rows.add(row);

		        }
				
				
			}
			catch (Exception e)	{
				throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
			}
			finally	{
				DbUtil.closeDbHandles(conn, statement, null);
			}
			
			
			
		} 
		
		migrationReturn.setExtendedMetadata(mExt);
		migrationReturn.setMetadata(m);
		migrationReturn.setRows(rows);
		migrationReturn.setRowCount(rows.size());
		migrationReturn.setColCount(m.size());
		
		return migrationReturn;
	}

	public static String createRdbmsTableStm(	final String uniqueSchemaName,
												final String sql, 
												final String tableName) throws Exception {
	
		SqlRepoDatabase db = SqlRepoUtils.sqlRepoDatabaseMap.get(uniqueSchemaName);
		DbConnectionInfo conn = DbConnectionInfo.makeDbConnectionInfo(db);
		TableFormatMap f=	SqlQueryExecUtils.execStaticQueryWithTableNoRs(conn, sql);
		return SqlMetadataWrapper.createRdbmsTableStm(f.getMetadata(), tableName);
	}
	
	// parse  java.sql.Types
	public static String 
	getType(final int type) {
        return switch (type) {
            case Types.BIT -> "BIT";
            case Types.TINYINT -> "TINYINT";
            case Types.SMALLINT -> "SMALLINT";
            case Types.INTEGER -> "INTEGER";
            case Types.BIGINT -> "BIGINT";
            case Types.FLOAT -> "FLOAT";
            case Types.REAL -> "REAL";
            case Types.DOUBLE -> "DOUBLE";
            case Types.NUMERIC -> "NUMERIC";
            case Types.DECIMAL -> "DECIMAL";
            case Types.CHAR -> "CHAR";
            case Types.VARCHAR -> "VARCHAR";
            case Types.LONGVARCHAR -> "LONGVARCHAR";
            case Types.DATE -> "DATE";
            case Types.TIME -> "TIME";
            case Types.TIMESTAMP -> "TIMESTAMP";
            case Types.BINARY -> "BINARY";
            case Types.VARBINARY -> "VARBINARY";
            case Types.LONGVARBINARY -> "LONGVARBINARY";
            case Types.NULL -> "NULL";
            case Types.OTHER -> "OTHER";
            case Types.JAVA_OBJECT -> "JAVA_OBJECT";
            case Types.DISTINCT -> "DISTINCT";
            case Types.STRUCT -> "STRUCT";
            case Types.ARRAY -> "ARRAY";
            case Types.BLOB -> "BLOB";
            case Types.CLOB -> "CLOB";
            case Types.REF -> "REF";
            case Types.DATALINK -> "DATALINK";
            case Types.BOOLEAN -> "BOOLEAN";
            case Types.ROWID -> "ROWID";
            case Types.NCHAR -> "NCHAR";
            case Types.NVARCHAR -> "NVARCHAR";
            case Types.LONGNVARCHAR -> "LONGNVARCHAR";
            case Types.NCLOB -> "NCLOB";
            case Types.SQLXML -> "SQLXML";
            case Types.REF_CURSOR -> "REF_CURSOR";
            case Types.TIME_WITH_TIMEZONE -> "TIME_WITH_TIMEZONE";
            case Types.TIMESTAMP_WITH_TIMEZONE -> "TIMESTAMP_WITH_TIMEZONE";
            default -> "UNKNOWN";
        };
		
	}
	
	public static boolean 
	isNumberLikeFormat(final int type) {
        return switch (type) {
            case Types.TINYINT, 
				 Types.SMALLINT, 
				 Types.INTEGER, 
				 Types.BIGINT, 
				 Types.FLOAT, 
				 Types.REAL, 
				 Types.DOUBLE, 
				 Types.NUMERIC, 
				 Types.DECIMAL -> true;
			
			case Types.BIT, 
				 Types.CHAR, 
				 Types.DATE, 
				 Types.TIME, 
				 Types.LONGVARCHAR, 
				 Types.VARCHAR, 
				 Types.TIMESTAMP, 
				 Types.BINARY,
				 Types.LONGVARBINARY,
				 Types.NULL,
				 Types.OTHER,
				 Types.JAVA_OBJECT,
				 Types.DISTINCT,
				 Types.STRUCT,
				 Types.ARRAY ,
				 Types.BLOB,
				 Types.CLOB,
				 Types.REF,
				 Types.DATALINK,
				 Types.BOOLEAN,
				 Types.ROWID,
				 Types.NCHAR,
				 Types.NVARCHAR,
				 Types.LONGNVARCHAR,
				 Types.NCLOB,
				 Types.SQLXML, Types.REF_CURSOR,
				 Types.TIME_WITH_TIMEZONE, 
				 Types.TIMESTAMP_WITH_TIMEZONE,
				 Types.VARBINARY-> false;

            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
		
	}
	
	
	
	public static boolean 
	isNumberLikeFormat(final String type) {
        return switch (type) {

            case "TINYINT",
				 "SMALLINT",
				 "INTEGER",
				 "BIGINT",
				 "FLOAT",
				 "REAL",
				 "DOUBLE",
				 "NUMERIC",
				 "DECIMAL" -> true;

			case "BIT",
				 "CHAR",
				 "VARCHAR",
				 "LONGVARCHAR",
				 "DATE",
				 "TIME",
				 "TIMESTAMP",
				 "BINARY",
				 "VARBINARY",
				 "LONGVARBINARY",
				 "NULL",
				 "OTHER",
				 "JAVA_OBJECT",
				 "DISTINCT",
				 "STRUCT",
				 "ARRAY",
				 "BLOB",
				 "CLOB",
				 "REF",
				 "DATALINK",
				 "BOOLEAN",
				 "ROWID",
				 "NCHAR",
				 "NVARCHAR",
				 "LONGNVARCHAR",
				 "NCLOB",
				 "SQLXML",
				 "REF_CURSOR",
				 "TIME_WITH_TIMEZONE",
				 "TIMESTAMP_WITH_TIMEZONE" -> false;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
		
	}
	
	
	public static Object 
	getTypeValue(final int type, final String value) {
        return switch (type) {
            case Types.BIT -> value.getBytes()[0];
            case Types.TINYINT -> Short.parseShort(value);
            case Types.SMALLINT, Types.INTEGER -> Integer.parseInt(value);
            case Types.BIGINT -> Long.parseLong(value);
            case Types.FLOAT -> Float.parseFloat(value);
            case Types.DOUBLE, Types.DECIMAL, Types.NUMERIC -> Double.parseDouble(value);
            case Types.DATE, Types.TIMESTAMP, Types.TIME, Types.TIMESTAMP_WITH_TIMEZONE, Types.TIME_WITH_TIMEZONE -> LocalDateTime.parse(value);
			case Types.BOOLEAN -> Boolean.parseBoolean(value);
			/*
			case Types.CHAR -> value;
			case Types.VARCHAR -> value;
			case Types.LONGVARCHAR -> value;
			case Types.REAL -> value;
            case Types.BINARY -> value;
            case Types.VARBINARY -> value;
            case Types.LONGVARBINARY -> value;
            case Types.NULL -> null;
            case Types.OTHER -> value;
            case Types.JAVA_OBJECT -> value;
            case Types.DISTINCT -> value;
            case Types.STRUCT -> value;
            case Types.ARRAY -> value;
            case Types.BLOB -> value;
            case Types.CLOB -> value;
            case Types.REF -> value;
            case Types.DATALINK -> value;
            case Types.ROWID -> value;
            case Types.NCHAR -> value;
            case Types.NVARCHAR -> value;
            case Types.LONGNVARCHAR -> value;
            case Types.NCLOB -> value;
            case Types.SQLXML -> value;
            case Types.REF_CURSOR -> value;
			*/
            default -> value;
        };
		
	}
	
	
	public static String 
	generateInsertTableStatement(	final Map<String, String> metadata, 
									final String schemaName,
									final String tableName) {

		String ret = "";
		if(schemaName.isBlank() || schemaName.isEmpty()) {
			ret = "INSERT INTO " + tableName + "(@columns@) VALUES( @values@ );";
		} else {
			ret = "INSERT INTO " + schemaName+ "." + tableName + "(@columns@) VALUES( @values@ );";
		}
		
		
		String columns = "";
		String values= "";
		
		int maxCnt = metadata.entrySet().size();
		for (var entry : metadata.entrySet()) {
			//System.out.println(entry.getKey() + "/" + entry.getValue());
			columns += entry.getKey();
			if(!isNumberLikeFormat(entry.getValue())) {
				values += "'@" + entry.getKey() + "@'";
			} else {
				values += "@" + entry.getKey() + "@";
			}
			maxCnt --;
			if(maxCnt > 0) {
				columns += ", ";
				values += ", ";
			}
		}
		
		ret = ret.replace("@columns@", columns);
		ret = ret.replace("@values@", values);
		return ret;
	}



	
}
