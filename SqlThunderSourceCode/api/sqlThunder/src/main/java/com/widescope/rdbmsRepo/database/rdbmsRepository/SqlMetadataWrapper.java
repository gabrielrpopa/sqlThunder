
package com.widescope.rdbmsRepo.database.rdbmsRepository;




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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.ColMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.IndexMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.TableMetadata;
import com.widescope.rdbmsRepo.database.embeddedDb.rdbms.RdbmsTableSetup;
import com.widescope.rdbmsRepo.database.tableFormat.ColumnDefinition;
import com.widescope.rdbmsRepo.database.tableFormat.TableDefinition;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatCellOutput;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatExtMetadataOutput;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatRowOutput;
import com.widescope.rdbmsRepo.database.types.ColumnTypeTable;
import com.widescope.rdbmsRepo.utils.SqlParser;

public class SqlMetadataWrapper {

	private static final Logger LOG = LogManager.getLogger(SqlMetadataWrapper.class);
	
	
	/**
	 * 
	 * @param metadata
	 * @param schema
	 * @param tableName
	 * @param toSystemType
	 * @return
	 * @throws Exception
	 */
	public static List<String> createIndexStm(	final TableMetadata metadata,
												final String schema,
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
				case "H2":
					stm = "CREATE INDEX IF NOT EXISTS " + indexName + " ON " + tableName.toUpperCase() + "(@colLst@);";
					break;
				case "POSTGRES", "SQLSERVER":
					break;
                 default:
			}

			 
			 
			 for (Map.Entry<Integer, IndexMetadata> entry : sortedIndexDef.entrySet()) {
				 switch(toSystemType.toUpperCase()) {
					case "H2", "POSTGRES", "SQLSERVER":
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
	
	public static RdbmsTableSetup createTableStm(	final TableMetadata metadata,
													final String schemaName,
													final String tableName,
													final String toSystemType) {
		
		RdbmsTableSetup r = new RdbmsTableSetup();
		
		String createTableStm = "CREATE TABLE IF NOT EXISTS " + tableName + " ( @columns@ );";
		String insertTableStm = "INSERT INTO " + schemaName + "." + tableName + "(@columns@) VALUES( @values@ );";
		if(schemaName.isBlank() || schemaName.isEmpty()) {
			insertTableStm = "INSERT INTO " + tableName + "(@columns@) VALUES( @values@ );";
		}
		
		String columnsInsert = "";
		String valuesInsert= "";
		
		
		String columns = "";
		int maxCnt = metadata.getLstColMetadata().entrySet().size();
		for (var entry : metadata.getLstColMetadata().entrySet()) {
			System.out.println(entry.getKey() + "/" + entry.getValue());
			
			int COLUMN_SIZE = entry.getValue().COLUMN_SIZE; 		//	The precision of the column.
			int DECIMAL_DIGITS = entry.getValue().DECIMAL_DIGITS; 	//	The scale of the column.
			//int NUM_PREC_RADIX = entry.getValue().NUM_PREC_RADIX; 	//	The radix of the column.
			
			String COLUMN_NAME = entry.getValue().COLUMN_NAME;		//	The column name.
			columnsInsert += COLUMN_NAME;
			
			switch(entry.getValue().TYPE_NAME.toUpperCase()) {
				case "IDENTITY":  /*H2*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " CHARACTER ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " CHARACTER ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " CHARACTER ";
							break;
						default:
							columns += entry.getKey() + " CHARACTER ";
					}
					break;
				
				case "CHARACTER":  /*H2*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " CHARACTER ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " CHARACTER ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " CHARACTER ";
							break;
						default:
							columns += entry.getKey() + " CHARACTER ";
					}
					break;
				case "CHAR": 		/*H2*/
				case "\"CHAR\"":   	/*POSTGRES*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " CHAR(" + COLUMN_SIZE + ") ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " CHAR(" + COLUMN_SIZE + ") ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " CHAR(" + COLUMN_SIZE + ") ";
							break;
						default:
							columns += entry.getKey() + " CHAR(" + COLUMN_SIZE + ") ";
					}
					break;
				case "CHARACTER VARYING": /*H2   Mapped to java.lang.String*/
				case "VARCHAR":/*H2*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " VARCHAR(" + COLUMN_SIZE + ") ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " VARCHAR(" + COLUMN_SIZE + ") ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " VARCHAR(" + COLUMN_SIZE + ") ";
							break;
						default:
							columns += entry.getKey() + " VARCHAR(" + COLUMN_SIZE + ") ";
					}
					break;
				
				case "VARCHAR_IGNORECASE": /*H2   Mapped to java.lang.String*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " VARCHAR_IGNORECASE(" + COLUMN_SIZE + ") ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " VARCHAR_IGNORECASE(" + COLUMN_SIZE + ") ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " VARCHAR_IGNORECASE(" + COLUMN_SIZE + ") ";
							break;
						default:
							columns += entry.getKey() + " VARCHAR_IGNORECASE(" + COLUMN_SIZE + ") ";
					}
					break;
				
				case "BINARY": /*H2 Mapped to byte[]*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " BINARY(" + COLUMN_SIZE + ") ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " BINARY(" + COLUMN_SIZE + ") ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " BINARY(" + COLUMN_SIZE + ") ";
							break;
						default:
							columns += entry.getKey() + " BINARY(" + COLUMN_SIZE + ") ";
					}
					break;
				
				case "BINARY VARYING": /*H2 Mapped to java.lang.String*/
				case "VARBINARY": /*H2 Mapped to java.lang.String*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " VARBINARY(" + COLUMN_SIZE + ") ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " VARBINARY(" + COLUMN_SIZE + ") ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " VARBINARY(" + COLUMN_SIZE + ") ";
							break;
						default:
							columns += entry.getKey() + " VARBINARY(" + COLUMN_SIZE + ") ";
					}
					break;
				
				case "BINARY LARGE OBJECT": /*H2 Mapped to java.sql.Blob*/
				case "BLOB": /*H2 Mapped to java.sql.Blob*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " BLOB(" + COLUMN_SIZE + ") ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " BLOB(" + COLUMN_SIZE + ") ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " BLOB(" + COLUMN_SIZE + ") ";
							break;
						default:
							columns += entry.getKey() + " BLOB(" + COLUMN_SIZE + ") ";
					}
					break;
				
				case "BOOLEAN": /*H2 Mapped to java.lang.Boolean*/
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " BOOLEAN ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " BOOLEAN ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " BOOLEAN ";
							break;
						default:
							columns += entry.getKey() + " BOOLEAN ";
					}
					break;
				
				case "TINYINT": /*H2 mapped to java.lang.Integer or java.lang.Byte*/ 
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " TINYINT ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " TINYINT ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " TINYINT ";
							break;
						default:
							columns += entry.getKey() + " TINYINT ";
					}
					break;
				
				case "SMALLINT": /*H2 mapped to java.lang.Integer or java.lang.Short*/
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " SMALLINT ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " SMALLINT ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " SMALLINT ";
							break;
						default:
							columns += entry.getKey() + " SMALLINT ";
					}
					break;
				
				case "INTEGER": /*H2 Mapped to java.lang.Integer*/
				case "INT": /*H2 Mapped to java.lang.Integer*/
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " INTEGER ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " INTEGER ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " INTEGER ";
							break;
						default:
							columns += entry.getKey() + " INTEGER ";
					}
					break;
				
				case "BIGINT": /*H2 Mapped to java.lang.Long*/
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " BIGINT ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " BIGINT ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " BIGINT ";
							break;
						default:
							columns += entry.getKey() + " BIGINT ";
						}
					break;
				
				case "NUMERIC": /*H2 Mapped to java.math.BigDecimal*/
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " NUMERIC(" + COLUMN_SIZE + ", " + DECIMAL_DIGITS + ") ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " NUMERIC(" + COLUMN_SIZE + ", " + DECIMAL_DIGITS + ") ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " NUMERIC(" + COLUMN_SIZE + ", " + DECIMAL_DIGITS + ") ";
							break;
						default:
							columns += entry.getKey() + " NUMERIC(" + COLUMN_SIZE + ", " + DECIMAL_DIGITS + ") ";
					}
					break;
				
				case "DECIMAL": /*H2 Mapped to java.math.BigDecimal*/
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " DECIMAL(" + COLUMN_SIZE + ", " + DECIMAL_DIGITS + ") ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " DECIMAL(" + COLUMN_SIZE + ", " + DECIMAL_DIGITS + ") ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " DECIMAL(" + COLUMN_SIZE + ", " + DECIMAL_DIGITS + ") ";
							break;
						default:
							columns += entry.getKey() + " DECIMAL(" + COLUMN_SIZE + ", " + DECIMAL_DIGITS + ") ";
					}
					break;
				case "CLOB": /*H2 Mapped to java.math.BigDecimal*/
				case "CHARACTER LARGE OBJECT":	
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " CLOB ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " CLOB ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " CLOB ";
							break;
						default:
							columns += entry.getKey() + " CLOB ";
					}
					break;
				case "REAL": /*H2 Mapped to java.lang.Double*/
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " REAL ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " REAL ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " REAL ";
							break;
						default:
							columns += entry.getKey() + " REAL ";
					}
					break;
				
				case "DOUBLE PRECISION": /*H2 Mapped to java.lang.Double*/
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " DOUBLE PRECISION ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " DOUBLE PRECISION ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " DOUBLE PRECISION ";
							break;
						default:
							columns += entry.getKey() + " DOUBLE PRECISION ";
					}
					break; 
				
				case "DECFLOAT": /*H2 Mapped to java.lang.Double*/
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " DECFLOAT(" + COLUMN_SIZE + ") ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " DECFLOAT(" + COLUMN_SIZE + ") ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " DECFLOAT(" + COLUMN_SIZE + ") ";
							break;
						default:
							columns += entry.getKey() + " DECFLOAT(" + COLUMN_SIZE + ") ";
					}
					break; 
				
				case "DATE": /*H2 mapped to java.time.LocalDate*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " DATE ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " DATE ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " DATE ";
							break;
						default:
							columns += entry.getKey() + " DATE ";
					}
					break; 
				case "DATE[]": /*POSTGRES mapped to java.time.LocalDate[]*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " DATE ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " DATE ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " DATE ";
							break;
						default:
							columns += entry.getKey() + " DATE ";
					}
					break; 
					
				case "DATERANGE": /*POSTGRES mapped to java.time.LocalDate[]*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " INTEGER ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " DATERANGE ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " INT ";
							break;
						default:
							columns += entry.getKey() + " INT ";
					}
					break;
					
				case "DATERANGE[]": /*POSTGRES mapped to java.time.LocalDate[]*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " INTEGER ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " DATERANGE[] ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " INT ";
							break;
						default:
							columns += entry.getKey() + " INT ";
					}
					break; 
				
				case "TIME": /*H2 mapped to java.sql.Time. java.time.LocalTime*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " TIME ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " TIME ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " TIME ";
							break;
						default:
							columns += entry.getKey() + " TIME ";
					}
					break;
				
				case "TIMESTAMP": /*H2 mapped to java.sql.Timestamp*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " TIMESTAMP ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " TIMESTAMP ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " TIMESTAMP ";
							break;
						default:
							columns += entry.getKey() + " TIMESTAMP ";
					}
					break; 
				
				case "TIME WITH TIME ZONE": /*H2*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " TIME ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " TIME ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " TIME ";
							break;
						default:
							columns += entry.getKey() + " TIME ";
					}
					break; 
				
				case "TIME($) WITH TIME ZONE": /*H2*/
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;
				
				case "INTERVAL YEAR": /*H2 Mapped to org.h2.api.Interval. java.time.Period*/
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " INTERVAL YEAR ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " INTERVAL YEAR ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " INTERVAL YEAR ";
							break;
						default:
							columns += entry.getKey() + " INTERVAL YEAR ";
					}
					break; 
				case "INTERVAL MONTH": /*H2 Mapped to org.h2.api.Interval. java.time.Period*/
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " INTERVAL YEAR ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " INTERVAL YEAR ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " INTERVAL YEAR ";
							break;
						default:
							columns += entry.getKey() + " INTERVAL YEAR ";
					}
					break; 
					
				case "INTERVAL DAY": /*H2 Mapped to org.h2.api.Interval. java.time.Period*/
					valuesInsert += "@" + COLUMN_NAME + "@";
					switch(toSystemType.toUpperCase()) {
						case "H2":
							columns += entry.getKey() + " INTERVAL DAY ";
							break;
						case "POSTGRES":
							columns += entry.getKey() + " INTERVAL DAY ";
							break;
						case "SQLSERVER":
							columns += entry.getKey() + " INTERVAL DAY ";
							break;
						default:
							columns += entry.getKey() + " INTERVAL DAY ";
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
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	 
				
				case "JAVA_OBJECT": /*H2*/
					System.out.println("Default catch JAVA_OBJECT: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	 
				case "ENUM": /*H2*/
					System.out.println("Default catch ENUM: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	 
				case "GEOMETRY": /*H2*/
					System.out.println("Default catch GEOMETRY: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	 
				case "JSON": /*H2*/
					System.out.println("Default catch JSON: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	 
				case "ARRAY": /*H2*/
					System.out.println("Default catch ARRAY: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	 
				case "ROW": /*H2*/
					System.out.println("Default catch ROW: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;
				case "BOX": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	 
				case "BOX[]": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	 
				case "BYTEA": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	  	 
				case "BYTEA[]": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	  
				case "CID": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	  	 
				case "CID[]": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	  
				case "CIRCLE": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	  
				case "CIRCLE[]": /*POSTGRES*/
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	  	 
				
				default: /*if we got here most likely we have to find a different way*/
				
					System.out.println("Default catch: " + entry.getKey());
					valuesInsert += "'@" + COLUMN_NAME + "@'";
					break;	 
			
			}
			
			
			
			maxCnt --;
			if(maxCnt > 0) {
					columns += ",";
			}
		}
		createTableStm = createTableStm.replace("@columns@", columns);
		insertTableStm = valuesInsert.replace("@columns@", columnsInsert);
		insertTableStm = valuesInsert.replace("@values@", valuesInsert);
		
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
			closeDbHandles(conn, null, tableRs);
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
		} finally	{
			/*Make sure you maintain or close connection in the calling method depending of static or in-mem DB*/
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
			LOG.error("MetadataWrapper::getTableList Exception: " + connectionDetailInfo.getDbName() + ": " + e.getMessage());
			throw e;
		} finally	{
			try { if(conn!=null && !conn.isClosed()) conn.close(); }  catch(SQLException se3)  { System.out.print(se3.getMessage()); }
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
			LOG.error("MetadataWrapper::getTableList Exception: " + connectionDetailInfo.getDbName() + ": " + e.getMessage());
			throw e;
		} finally	{

			try { if(conn!=null && !conn.isClosed()) conn.close(); }  catch(SQLException se3)  { System.out.print(se3.getMessage()); }
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
		PreparedStatement statement = null;
		
		
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
			LOG.error("MetadataWrapper::getIndexesList Exception: " + connectionDetailInfo.getDbName() + ": " + e.getMessage());
			throw e;
		} finally	{
			try { if(statement!=null && !statement.isClosed())	statement.close(); } catch(SQLException se2)	{ System.out.print(se2.getMessage());}
			try { if(conn!=null && !conn.isClosed()) conn.close(); }  catch(SQLException se3)  { System.out.print(se3.getMessage()); }
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
		PreparedStatement statement = null;
	
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
			LOG.error("MetadataWrapper::getIndexesList Exception: " + tableName + ": " + e.getMessage());
			throw e;
		} finally	{
			try { if(statement!=null && !statement.isClosed())	statement.close(); } catch(SQLException se2)	{ System.out.print(se2.getMessage());}
			try { if(conn!=null && !conn.isClosed()) conn.close(); }  catch(SQLException se3)  { System.out.print(se3.getMessage()); }
		}
		return ret;
	}
	
	public static List<String> 
	getAllTableList(final Connection conn,
				final String schemaName) throws Exception 
	{
		PreparedStatement statement = null;
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
					if(tableName.compareToIgnoreCase("TUTORIALS_TBL") == 0) {
						//String schema = rs.getString("TABLE_SCHEM");
					}
					ret.add(rs.getString(3));
				}
			}
			
		} catch (Exception e)	{
			LOG.error("MetadataWrapper::getTableList Exception: " + e.getMessage());
			throw e;
		} finally	{
			try { if(statement!=null && !statement.isClosed())	statement.close(); } catch(SQLException se2)	{ System.out.print(se2.getMessage());}
		}
		return ret;
	}
	
	
	public static List<String> 
	getSystemTableList(	final Connection conn,
						final String schemaName) throws Exception 
	{
		PreparedStatement statement = null;
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
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		} finally	{
			closeDbHandles(conn, null, rs);
		}
		return ret;
	}

	public static TableMetadata 
	getTableColumns(final DbConnectionInfo connectionDetailInfo,
					final String tableName) throws Exception
	{
		Connection conn = null;
		PreparedStatement statement = null;
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
			AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
		} finally	{
			closeDbHandles(conn,null, null);
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
		    } else if(val.compareToIgnoreCase("JAVA.LANG.BIGDECIMAL") == 0 || val.compareToIgnoreCase("DECIMAL") == 0) {
		    	columns.append(entry.getKey()).append(" int ");
		    } else if(val.compareToIgnoreCase("JAVA.LANG.DOUBLE") == 0 || val.compareToIgnoreCase("DOUBLE") == 0 ||	val.compareToIgnoreCase("MONEY") == 0) {
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
		    } else if(val.compareToIgnoreCase("JAVA.LANG.BIGDECIMAL") == 0 || val.compareToIgnoreCase("DECIMAL") == 0) {
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
			} else if(entry.getValue().toUpperCase().compareTo("JAVA.LANG.BIGDECIMAL") == 0 ||
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
					entry.getValue().compareTo("JAVA.LANG.BIGDECIMAL") == 0 ||
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
			} else if(entry.getColumnType().toUpperCase().compareTo("JAVA.LANG.BIGDECIMAL") == 0 ||
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
					entry.getColumnType().compareTo("JAVA.LANG.BIGDECIMAL") == 0 ||
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
			} else if(entry.getResultMetadata().getColumnTypeName().compareToIgnoreCase("JAVA.LANG.BIGDECIMAL") == 0 ||
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
			
			System.out.println(entry.getKey() + "/" + entry.getValue() + "/" + row.get(entry.getKey()));
			System.out.println("value" + "/" + row.get(entry.getKey()));
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
			ResultSet rs = null;
			try	{
				Class.forName(connectionDetailInfo.getJdbcDriver());
				conn = DriverManager.getConnection(connectionDetailInfo.getDbUrl(),connectionDetailInfo.getUserName(), connectionDetailInfo.getPassword());
				statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				rs = statement.executeQuery(sqlStatement);
				
				
				ColumnTypeTable columnTypeTable = new ColumnTypeTable();
				ResultSetMetaData metaData = rs.getMetaData();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String colTypeName = columnTypeTable.columnIdToName.get(Integer.valueOf(metaData.getColumnType(i))  ) ;
					
					
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
	        		
		        		if(columnType == java.sql.Types.NVARCHAR || columnType == java.sql.Types.VARCHAR ) {
		        			String val = rs.getString(columnName);
		        			row.put(columnName, val);
		        		}
		        		else if(columnType == java.sql.Types.INTEGER 	|| columnType == java.sql.Types.BIGINT 
		        														|| columnType == java.sql.Types.SMALLINT) {
		        			Integer val = rs.getInt(columnName);
		        			row.put(columnName, val);
		        		}
		        		else if(columnType == java.sql.Types.FLOAT 	|| columnType == java.sql.Types.DOUBLE 
		        													|| columnType == java.sql.Types.DECIMAL
		        													|| columnType == java.sql.Types.REAL) {
		        			Float val = rs.getFloat(columnName);
		        			row.put(columnName, val);
		        		}
		        		else if(columnType == java.sql.Types.TIMESTAMP)	{
		        			Timestamp val = rs.getTimestamp(columnName);
		        			row.put(columnName, val);
		        		}
		        		else if(columnType == java.sql.Types.DATE) {
		        			Date val = rs.getDate(columnName);
		        			row.put(columnName, val);
		        		}
		        		else if(columnType == java.sql.Types.CLOB) {
		        			row.put(columnName, rs.getClob(columnName));
		        		}
		        		else if(columnType == java.sql.Types.BINARY || columnType == java.sql.Types.LONGVARBINARY 
		        													|| columnType == java.sql.Types.VARBINARY) {
		        			row.put(columnName, rs.getBytes(columnName));
		        		}
		        		
		        		else if(columnType == java.sql.Types.BOOLEAN) {
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
				LOG.error("MetadataWrapper::execStaticQuery Exception: " + connectionDetailInfo.getDbName() + ": " + e.getMessage());
				throw e;
			}
			finally	{
				try { if(statement!=null && !statement.isClosed())	statement.close(); } catch(SQLException se2)	{ System.out.print(se2.getMessage());}
			    try { if(conn!=null && !conn.isClosed()) conn.close(); }  catch(SQLException se3)  { System.out.print(se3.getMessage()); }
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
		switch(type) {
			case Types.BIT:
				return "BIT";
			case Types.TINYINT:
				return "TINYINT";
			case Types.SMALLINT:
				return "SMALLINT";
			case Types.INTEGER:
				return "INTEGER";
			case Types.BIGINT:
				return "BIGINT";
			case Types.FLOAT:
				return "FLOAT";
			case Types.REAL:
				return "REAL";
			case Types.DOUBLE:
				return "DOUBLE";
			case Types.NUMERIC:
				return "NUMERIC";
			case Types.DECIMAL:
				return "DECIMAL";
			case Types.CHAR:
				return "CHAR";
			case Types.VARCHAR:
				return "VARCHAR";
			case Types.LONGVARCHAR:
				return "LONGVARCHAR";
			case Types.DATE:
				return "DATE";
			case Types.TIME:
				return "TIME";
			case Types.TIMESTAMP:
				return "TIMESTAMP";
			case Types.BINARY:
				return "BINARY";
			case Types.VARBINARY:
				return "VARBINARY";
			case Types.LONGVARBINARY:
				return "LONGVARBINARY";
			case Types.NULL:
				return "NULL";
			case Types.OTHER:
				return "OTHER";
			case Types.JAVA_OBJECT:
				return "JAVA_OBJECT";
			case Types.DISTINCT:
				return "DISTINCT";
			case Types.STRUCT:
				return "STRUCT";
			case Types.ARRAY:
				return "ARRAY";
			case Types.BLOB:
				return "BLOB";
			case Types.CLOB:
				return "CLOB";
			case Types.REF:
				return "REF";
			case Types.DATALINK:
				return "DATALINK";
			case Types.BOOLEAN:
				return "BOOLEAN";
			case Types.ROWID:
				return "ROWID";
			case Types.NCHAR:
				return "NCHAR";
			case Types.NVARCHAR:
				return "NVARCHAR";
			case Types.LONGNVARCHAR:
				return "LONGNVARCHAR";
			case Types.NCLOB:
				return "NCLOB";
			case Types.SQLXML:
				return "SQLXML";
			case Types.REF_CURSOR:
				return "REF_CURSOR";
			case Types.TIME_WITH_TIMEZONE:
				return "TIME_WITH_TIMEZONE";
			case Types.TIMESTAMP_WITH_TIMEZONE:
				return "TIMESTAMP_WITH_TIMEZONE";
			default:
				return "UNKNOWN";
		}
		
	}
	
	public static boolean 
	isNumberLikeFormat(final int type) {
		switch(type) {
			case Types.BIT:
				return false;
			case Types.TINYINT:
				return true;
			case Types.SMALLINT:
				return true;
			case Types.INTEGER:
				return true;
			case Types.BIGINT:
				return true;
			case Types.FLOAT:
				return true;
			case Types.REAL:
				return true;
			case Types.DOUBLE:
				return true;
			case Types.NUMERIC:
				return true;
			case Types.DECIMAL:
				return true;
			case Types.CHAR:
				return false;
			case Types.VARCHAR:
				return false;
			case Types.LONGVARCHAR:
				return false;
			case Types.DATE:
				return false;
			case Types.TIME:
				return false;
			case Types.TIMESTAMP:
				return false;
			case Types.BINARY:
				return false;
			case Types.VARBINARY:
				return false;
			case Types.LONGVARBINARY:
				return false;
			case Types.NULL:
				return false;
			case Types.OTHER:
				return false;
			case Types.JAVA_OBJECT:
				return false;
			case Types.DISTINCT:
				return false;
			case Types.STRUCT:
				return false;
			case Types.ARRAY:
				return false;
			case Types.BLOB:
				return false;
			case Types.CLOB:
				return false;
			case Types.REF:
				return false;
			case Types.DATALINK:
				return false;
			case Types.BOOLEAN:
				return true;
			case Types.ROWID:
				return false;
			case Types.NCHAR:
				return false;
			case Types.NVARCHAR:
				return false;
			case Types.LONGNVARCHAR:
				return false;
			case Types.NCLOB:
				return false;
			case Types.SQLXML:
				return false;
			case Types.REF_CURSOR:
				return false;
			case Types.TIME_WITH_TIMEZONE:
				return false;
			case Types.TIMESTAMP_WITH_TIMEZONE:
				return false;
			default:
				return false;
		}
		
	}
	
	
	
	public static boolean 
	isNumberLikeFormat(final String type) {
		switch(type) {
			case "BIT":
				return false;
			case "TINYINT":
				return true;
			case "SMALLINT":
				return true;
			case "INTEGER":
				return true;
			case "BIGINT":
				return true;
			case "FLOAT":
				return true;
			case "REAL":
				return true;
			case "DOUBLE":
				return true;
			case "NUMERIC":
				return true;
			case "DECIMAL":
				return true;
			case "CHAR":
				return false;
			case "VARCHAR":
				return false;
			case "LONGVARCHAR":
				return false;
			case "DATE":
				return false;
			case "TIME":
				return false;
			case "TIMESTAMP":
				return false;
			case "BINARY":
				return false;
			case "VARBINARY":
				return false;
			case "LONGVARBINARY":
				return false;
			case "NULL":
				return false;
			case "OTHER":
				return false;
			case "JAVA_OBJECT":
				return false;
			case "DISTINCT":
				return false;
			case "STRUCT":
				return false;
			case "ARRAY":
				return false;
			case "BLOB":
				return false;
			case "CLOB":
				return false;
			case "REF":
				return false;
			case "DATALINK":
				return false;
			case "BOOLEAN":
				return true;
			case "ROWID":
				return false;
			case "NCHAR":
				return false;
			case "NVARCHAR":
				return false;
			case "LONGNVARCHAR":
				return false;
			case "NCLOB":
				return false;
			case "SQLXML":
				return false;
			case "REF_CURSOR":
				return false;
			case "TIME_WITH_TIMEZONE":
				return false;
			case "TIMESTAMP_WITH_TIMEZONE":
				return false;
			default:
				return false;
		}
		
	}
	
	
	public static Object 
	getTypeValue(final int type, final String value) {
		switch(type) {
			case Types.BIT:
				return value.getBytes()[0];
			case Types.TINYINT:
				return Short.parseShort(value);
			case Types.SMALLINT:
				return Integer.parseInt(value);
			case Types.INTEGER:
				return Integer.parseInt(value);
			case Types.BIGINT:
				return Long.parseLong(value);
			case Types.FLOAT:
				return Float.parseFloat(value);
			case Types.REAL:
				return value;
			case Types.DOUBLE:
				return Double.parseDouble(value) ;
			case Types.NUMERIC:
				return Double.parseDouble(value) ;
			case Types.DECIMAL:
				return Double.parseDouble(value) ;
			case Types.CHAR:
				return value;
			case Types.VARCHAR:
				return value;
			case Types.LONGVARCHAR:
				return value;
			case Types.DATE:
				return LocalDateTime.parse(value);
			case Types.TIME:
				return LocalDateTime.parse(value);
			case Types.TIMESTAMP:
				return LocalDateTime.parse(value);
			case Types.BINARY:
				return value;
			case Types.VARBINARY:
				return value;
			case Types.LONGVARBINARY:
				return value;
			case Types.NULL:
				return null;
			case Types.OTHER:
				return value;
			case Types.JAVA_OBJECT:
				return value;
			case Types.DISTINCT:
				return value;
			case Types.STRUCT:
				return value;
			case Types.ARRAY:
				return value;
			case Types.BLOB:
				return value;
			case Types.CLOB:
				return value;
			case Types.REF:
				return value;
			case Types.DATALINK:
				return value;
			case Types.BOOLEAN:
				return Boolean.parseBoolean(value);
			case Types.ROWID:
				return value;
			case Types.NCHAR:
				return value;
			case Types.NVARCHAR:
				return value;
			case Types.LONGNVARCHAR:
				return value;
			case Types.NCLOB:
				return value;
			case Types.SQLXML:
				return value;
			case Types.REF_CURSOR:
				return value;
			case Types.TIME_WITH_TIMEZONE:
				return LocalDateTime.parse(value);
			case Types.TIMESTAMP_WITH_TIMEZONE:
				return LocalDateTime.parse(value);
			default:
				return value;
		}
		
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


	public static void closeDbHandles(Connection conn,	Statement statement, ResultSet rs)
	{
		try { if(rs!=null && !rs.isClosed())	rs.close(); } catch(SQLException se1)	{ System.out.print(se1.getMessage());}
		try { if(statement!=null && !statement.isClosed())	statement.close(); } catch(SQLException se2)	{ System.out.print(se2.getMessage());}
		try { if(conn!=null && !conn.isClosed()) conn.close(); }  catch(SQLException se3)  { System.out.print(se3.getMessage()); }
	}
	
}
