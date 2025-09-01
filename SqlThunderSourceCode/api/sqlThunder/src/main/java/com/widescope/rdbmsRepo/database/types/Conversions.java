package com.widescope.rdbmsRepo.database.types;

import com.widescope.rdbmsRepo.database.DbUtil;

import java.util.Objects;

public class Conversions {

	/*
	 * type can be anything that will be comprehended by this mapping
	 * from and to are the database type H2, POSTGRES, ORACLE, SQLSERVER
	 * */
	public static 
	String getRdbmsToRdbmsMapping(	String type,
									String from,
									String to) {
		if(from.compareTo(DbUtil.h2) == 0) {
			if(Objects.equals(to, DbUtil.h2)) {
				return type;
			} else if(Objects.equals(to, DbUtil.postgresql)) {
				return type;
			} else if(Objects.equals(to, DbUtil.oracle)) {
				return type;
			} else if(Objects.equals(to, DbUtil.sqlserver)) {
				return type;
			} else if(Objects.equals(to, DbUtil.sybase)) {
				return type;
			} else {
				return "";
			}
		} else if(from.compareTo(DbUtil.postgresql) == 0) {
			if(Objects.equals(to, DbUtil.h2)) {
				return type;
			} else if(Objects.equals(to, DbUtil.postgresql)) {
				return type;
			} else if(Objects.equals(to, DbUtil.oracle)) {
				return type;
			} else if(Objects.equals(to, DbUtil.sqlserver)) {
				return type;
			} else if(Objects.equals(to, DbUtil.sybase)) {
				return type;
			} else {
				return "";
			}
		} else if(from.compareTo(DbUtil.oracle) == 0) {
			if(Objects.equals(to, DbUtil.h2)) {
				return type;
			} else if(Objects.equals(to, DbUtil.postgresql)) {
				return type;
			} else if(Objects.equals(to, DbUtil.oracle)) {
				return type;
			} else if(Objects.equals(to, DbUtil.sqlserver)) {
				return type;
			} else if(Objects.equals(to, DbUtil.sybase)) {
				return type;
			} else {
				return "";
			}
		} else if(from.compareTo(DbUtil.sqlserver) == 0) {
			if(Objects.equals(to, DbUtil.h2)) {
				return type;
			} else if(Objects.equals(to, DbUtil.postgresql)) {
				return type;
			} else if(Objects.equals(to, DbUtil.oracle)) {
				return type;
			} else if(Objects.equals(to, DbUtil.sqlserver)) {
				return type;
			} else if(Objects.equals(to, DbUtil.sybase)) {
				return type;
			} else {
				return "";
			}
		} else if(from.compareTo(DbUtil.sybase) == 0) {
			if(Objects.equals(to, DbUtil.h2)) {
				return type;
			} else if(Objects.equals(to, DbUtil.postgresql)) {
				return type;
			} else if(Objects.equals(to, DbUtil.oracle)) {
				return type;
			} else if(Objects.equals(to, DbUtil.sqlserver)) {
				return type;
			} else if(Objects.equals(to, DbUtil.sybase)) {
				return type;
			} else {
				return "";
			}
		} else {
			return "";
		}
		
	}
	
	public static String getColumnJavaType(int type) {
		if( type == java.sql.Types.BIT ){
			return "java.lang.Short";
		} else if( type == java.sql.Types.TINYINT ){
			return "java.lang.Short";
		} else if( type == java.sql.Types.SMALLINT ){
			return "java.lang.Short";
		} else if( type == java.sql.Types.INTEGER ){
			return "java.lang.Integer";
		} else if( type == java.sql.Types.BIGINT ){
			return "java.math.BigInteger";
		} else if( type == java.sql.Types.FLOAT ){
			return "java.lang.Float";
		} else if( type == java.sql.Types.REAL){
			return "java.lang.Double";
		} else if( type == java.sql.Types.DOUBLE){
			return "java.lang.Double";
		} else if( type == java.sql.Types.NUMERIC){
			return "java.lang.Number";	
		} else if( type == java.sql.Types.DECIMAL){
			return "java.math.BigDecimal";	
		} else if( type == java.sql.Types.CHAR){
			return "java.lang.String";
		} else if( type == java.sql.Types.VARCHAR) {
			return "java.lang.String";
		} else if( type == java.sql.Types.LONGVARCHAR){
			return "java.lang.String";
		} else if( type == java.sql.Types.DATE) {
			return "java.util.Date";
		} else if( type == java.sql.Types.TIME){
			return "java.sql.Timestamp";
		} else if( type == java.sql.Types.TIMESTAMP){
			return "java.sql.Timestamp";
		} else if( type == java.sql.Types.BINARY){
			return "";
		} else if( type == java.sql.Types.VARBINARY){
			return "";
		} else if( type == java.sql.Types.LONGVARBINARY){
			return "";
		} else if( type == java.sql.Types.NULL){
			return "java.lang.Null";
		} else if( type == java.sql.Types.OTHER){
			return "";
		} else if( type == java.sql.Types.JAVA_OBJECT){
			return "";
		} else if( type == java.sql.Types.DISTINCT){
			return "";
		} else if( type == java.sql.Types.STRUCT){
			return "";
		} else if( type == java.sql.Types.ARRAY){
			return "";
		} else if( type == java.sql.Types.BLOB){
			return "";
		} else if( type == java.sql.Types.CLOB){
			return "";
		} else if( type == java.sql.Types.REF) {
			return "";
		} else if( type == java.sql.Types.DATALINK){
			return "";
		} else if( type == java.sql.Types.BOOLEAN){
			return "";
		} else if( type == java.sql.Types.ROWID){
			return "java.lang.String";
		} else if( type == java.sql.Types.NCHAR){
			return "java.lang.String";
		} else if( type == java.sql.Types.NVARCHAR){
			return "java.lang.String";
		} else if( type == java.sql.Types.LONGNVARCHAR){
			return "java.lang.String";
		} else if( type == java.sql.Types.NCLOB){
			return "java.lang.String";
		} else if( type == java.sql.Types.SQLXML){
			return "java.lang.String";
		} else if( type == java.sql.Types.REF_CURSOR){
			return "java.lang.String";
		} else if( type == java.sql.Types.TIME_WITH_TIMEZONE){
			return "java.util.Date";
		} else if( type == java.sql.Types.TIMESTAMP_WITH_TIMEZONE){
			return "java.util.Date";
		} else{
			return "";
		
		}
	}
	
	
	
	public static String getColumnSqlType(int type) {
		if( type == java.sql.Types.BIT ){
			return "java.sql.Types.BIT";
		} else if( type == java.sql.Types.TINYINT ){
			return "java.sql.Types.TINYINT";
		} else if( type == java.sql.Types.SMALLINT ){
			return "java.sql.Types.SMALLINT";
		} else if( type == java.sql.Types.INTEGER ){
			return "java.sql.Types.INTEGER";
		} else if( type == java.sql.Types.BIGINT ){
			return "java.sql.Types.BIGINT";
		} else if( type == java.sql.Types.FLOAT ){
			return "java.sql.Types.FLOAT";
		} else if( type == java.sql.Types.REAL){
			return "java.sql.Types.REAL";
		} else if( type == java.sql.Types.DOUBLE){
			return "java.sql.Types.DOUBLE";
		} else if( type == java.sql.Types.NUMERIC){
			return "java.sql.Types.NUMERIC";	
		} else if( type == java.sql.Types.DECIMAL){
			return "java.sql.Types.DECIMAL";	
		} else if( type == java.sql.Types.CHAR){
			return "java.sql.Types.CHAR";
		} else if( type == java.sql.Types.VARCHAR) {
			return "java.sql.Types.VARCHAR";
		} else if( type == java.sql.Types.LONGVARCHAR){
			return "java.sql.Types.LONGVARCHAR";
		} else if( type == java.sql.Types.DATE) {
			return "java.sql.Types.DATE";
		} else if( type == java.sql.Types.TIME){
			return "java.sql.Types.TIME";
		} else if( type == java.sql.Types.TIMESTAMP){
			return "java.sql.Types.TIMESTAMP";
		} else if( type == java.sql.Types.BINARY){
			return "java.sql.Types.BINARY";
		} else if( type == java.sql.Types.VARBINARY){
			return "java.sql.Types.VARBINARY";
		} else if( type == java.sql.Types.LONGVARBINARY){
			return "java.sql.Types.LONGVARBINARY";
		} else if( type == java.sql.Types.NULL){
			return "java.sql.Types.NULL";
		} else if( type == java.sql.Types.OTHER){
			return "java.sql.Types.OTHER";
		} else if( type == java.sql.Types.JAVA_OBJECT){
			return "java.sql.Types.JAVA_OBJECT";
		} else if( type == java.sql.Types.DISTINCT){
			return "java.sql.Types.DISTINCT";
		} else if( type == java.sql.Types.STRUCT){
			return "java.sql.Types.STRUCT";
		} else if( type == java.sql.Types.ARRAY){
			return "java.sql.Types.ARRAY";
		} else if( type == java.sql.Types.BLOB){
			return "ava.sql.Types.BLOB";
		} else if( type == java.sql.Types.CLOB){
			return "java.sql.Types.CLO";
		} else if( type == java.sql.Types.REF) {
			return "java.sql.Types.REF";
		} else if( type == java.sql.Types.DATALINK){
			return "java.sql.Types.DATALINK";
		} else if( type == java.sql.Types.BOOLEAN){
			return "java.sql.Types.BOOLEAN";
		} else if( type == java.sql.Types.ROWID){
			return "java.sql.Types.ROWID";
		} else if( type == java.sql.Types.NCHAR){
			return "java.sql.Types.NCHAR";
		} else if( type == java.sql.Types.NVARCHAR){
			return "ava.sql.Types.NVARCHAR";
		} else if( type == java.sql.Types.LONGNVARCHAR){
			return "java.sql.Types.LONGNVARCHAR";
		} else if( type == java.sql.Types.NCLOB){
			return "java.sql.Types.NCLOB";
		} else if( type == java.sql.Types.SQLXML){
			return "ava.sql.Types.SQLXML";
		} else if( type == java.sql.Types.REF_CURSOR){
			return "java.sql.Types.REF_CURSOR";
		} else if( type == java.sql.Types.TIME_WITH_TIMEZONE){
			return "java.sql.Types.TIME_WITH_TIMEZONE";
		} else if( type == java.sql.Types.TIMESTAMP_WITH_TIMEZONE){
			return "java.sql.Types.TIMESTAMP_WITH_TIMEZONE";
		} else{
			return "NULL";
		
		}
	}
	
}
