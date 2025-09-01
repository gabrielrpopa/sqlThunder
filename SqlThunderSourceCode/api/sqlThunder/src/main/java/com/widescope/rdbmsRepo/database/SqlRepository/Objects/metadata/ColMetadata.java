package com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata;

public class ColMetadata {
	public String TABLE_CAT;
	public String TABLE_SCHEM; 	//	The table schema name.
	public String TABLE_NAME;	//	The table name.
	public String COLUMN_NAME;	//	The column name.
	public int DATA_TYPE; 		//	The SQL data type from java.sql.Types.
	public String TYPE_NAME; 	//	The name of the data type.
	public int COLUMN_SIZE; 	//	The precision of the column.
	public int BUFFER_LENGTH; 	//	Transfer size of the data.
	public int DECIMAL_DIGITS; 	//	The scale of the column.
	public int NUM_PREC_RADIX; 	//	The radix of the column.
	public int NULLABLE; 		//	Indicates if the column is nullable. It can be one of the following values:

	public String REMARKS; 		//	The comments associated with the column. Note: SQL Server always returns null for this column.
	public String COLUMN_DEF; 	//	The default value of the column.
	public int SQL_DATA_TYPE; 	//	Value of the SQL data type as it appears in the TYPE field of the descriptor. This column is the same as the DATA_TYPE column, except for the datetime and SQL-92 interval data types. This column always returns a value.
	public int SQL_DATETIME_SUB; //	Subtype code for datetime and SQL-92 interval data types. For other data types, this column returns NULL.
	public int CHAR_OCTET_LENGTH; //	The maximum number of bytes in the column.
	public int ORDINAL_POSITION; //	The index of the column within the table.
	public String IS_NULLABLE; 	//	Indicates if the column allows null values.
	public String SS_IS_SPARSE	; //	If the column is a sparse column, this has the value 1; otherwise, 0.1
	public int SS_IS_COLUMN_SET; //	If the column is the sparse column_set column, this has the value 1; otherwise, 0. 1
	public int SS_IS_COMPUTED; 	//	Indicates if a column in a TABLE_TYPE is a computed column. 1
	public String IS_AUTOINCREMENT; //	"YES" if the column is auto incremented. "NO" if the column is not auto incremented. "" (empty string) if the driver cannot determine if the column is auto incremented. 1
	public String SS_UDT_CATALOG_NAME; //	The name of the catalog that contains the user-defined type (UDT). 1
	public String SS_UDT_SCHEMA_NAME; //	The name of the schema that contains the user-defined type (UDT). 1
	public String SS_UDT_ASSEMBLY_TYPE_NAME; //	The fully-qualified name user-defined type (UDT). 1
	public String SS_XML_SCHEMACOLLECTION_CATALOG_NAME	; //	The name of the catalog where an XML schema collection name is defined. If the catalog name cannot be found, this variable contains an empty string. 1
	public String SS_XML_SCHEMACOLLECTION_SCHEMA_NAME; //	The name of the schema where an XML schema collection name is defined. If the schema name cannot be found, this is an empty string. 1
	public String SS_XML_SCHEMACOLLECTION_NAME; //	The name of an XML schema collection. If the name cannot be found, this is an empty string. 1
	public int SS_DATA_TYPE; //The SQL Server data type that is used by extended stored procedures.
								//Note For more information about the data types returned by SQL Server, see "Data Types (Transact-SQL)" in SQL Server Books Online.
	
	
	public ColMetadata() {
		
	}
	
	public ColMetadata(final int DATA_TYPE) {
		this.DATA_TYPE = DATA_TYPE;
	}
		
	
}
