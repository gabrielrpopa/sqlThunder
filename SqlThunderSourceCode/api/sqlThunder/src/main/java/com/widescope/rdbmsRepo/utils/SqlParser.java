/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.widescope.rdbmsRepo.utils;

import com.widescope.logging.AppLogger;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import net.sf.jsqlparser.util.validation.Validation;
import net.sf.jsqlparser.util.validation.ValidationError;
import net.sf.jsqlparser.util.validation.feature.DatabaseType;
import net.sf.jsqlparser.util.validation.feature.FeaturesAllowed;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SqlParser {

    private static DatabaseType convert(final String databaseType) {
        if(databaseType.compareToIgnoreCase("H2") == 0) {
            return DatabaseType.H2;
        } else if(databaseType.compareToIgnoreCase("SQLSERVER") == 0){
            return DatabaseType.SQLSERVER;
        } else if(databaseType.compareToIgnoreCase("ORACLE") == 0){
            return DatabaseType.ORACLE;
        } else if(databaseType.compareToIgnoreCase("POSTGRESQL") == 0) {
            return DatabaseType.POSTGRESQL;
        } else {
            return DatabaseType.POSTGRESQL;
        }
    }


	public static boolean isSqlValidForAllDb(final String sql, final String databaseType) {
        boolean isValidDb = false;
        if( databaseType != null && !databaseType.trim().isEmpty() ) {
            isValidDb = Arrays.asList(DatabaseType.values()).contains(convert(databaseType));
        }

        if(!isValidDb) {
            Validation validation = new Validation(Arrays.asList(DatabaseType.SQLSERVER,
                                                    DatabaseType.MARIADB,
                                                    DatabaseType.POSTGRESQL,
                                                    DatabaseType.H2), sql);
            List<ValidationError> errors = validation.validate();
            return errors.isEmpty();
        } else {
            DatabaseType d = convert(databaseType);
            Validation validation = new Validation(Collections.singletonList(d), sql);
            List<ValidationError> errors = validation.validate();
            return errors.isEmpty();
        }

    }


    public static boolean isSqlDQL(final String sql) {
        Validation validation = new Validation(Collections.singletonList(FeaturesAllowed.SELECT), sql);
        List<ValidationError> errors = validation.validate();
        if(!errors.isEmpty()) {
        	String cap = errors.get(0).getCapability().getName();
        	if ( cap.equals("SELECT") ) {
        		return true;
        	}
        }
        return errors.isEmpty();
    }

    public static boolean isSqlDML(final String sql) {
        Validation validation = new Validation(Collections.singletonList(FeaturesAllowed.DML), sql);
        List<ValidationError> errors = validation.validate();
        
        if(!errors.isEmpty()) {
        	String cap = errors.get(0).getCapability().getName();
        	if ( cap.equals("DML") ) {
        		return true;
        	}
        }
        return errors.isEmpty();
    }

    public static boolean isSqlDDL(final String sql) {
        Validation validation = new Validation(Collections.singletonList(FeaturesAllowed.DDL), sql);
        List<ValidationError> errors = validation.validate();
        return errors.isEmpty();
    }

    public static boolean isDrop(final String sql) {
        Validation validation = new Validation(Collections.singletonList(FeaturesAllowed.DROP), sql);
        List<ValidationError> errors = validation.validate();
        return errors.isEmpty();
    }

    public static boolean isExecute(final String sql) {
        Validation validation = new Validation(Collections.singletonList(FeaturesAllowed.EXECUTE), sql);
        List<ValidationError> errors = validation.validate();
        return errors.isEmpty();
    }
    

    public static CreateTable isCreateTable(final String sql) throws JSQLParserException {

    	CreateTable createTable
        = (CreateTable) CCJSqlParserUtil.parseStatements(sql).getStatements().get(0);
        
        assertEquals("PRIMARY KEY", createTable.getIndexes().get(0).getType());
        assertEquals("UNIQUE", createTable.getIndexes().get(1).getType());
        assertEquals("FOREIGN KEY", createTable.getIndexes().get(2).getType());
        
        assertEquals(2, createTable.getColumnDefinitions().size());

        assertEquals("mycol", createTable.getColumnDefinitions().get(0).getColumnName());
        assertEquals("mycol2", createTable.getColumnDefinitions().get(1).getColumnName());
        assertEquals("PRIMARY KEY", createTable.getIndexes().get(0).getType());
        assertEquals("mycol", createTable.getIndexes().get(0).getColumnsNames().get(1));
        
        return createTable;

    }
    

  

    public static String getCreateTableStatementFromSql(final String sql) throws Exception {

        final String blank = "            ";
        String createStatement = "CREATE TABLE @tablename@ (@columns@);";
        if( !isSqlDQL(sql) ) {
            throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, "Not a SELECT statement")) ;
        }
        try {
        	StringBuilder columns = new StringBuilder();
        	Statement select1 = CCJSqlParserUtil.parse(sql);
        	List<SelectItem> selectCols = ((PlainSelect) ((Select) select1).getSelectBody()).getSelectItems();
        	
        	int cnt = selectCols.size();
        	for (SelectItem col : selectCols) {
        		columns.append(blank).append(col.toString()).append(" VARCHAR(100)");
        		cnt-=1;
        		if(cnt != 0) {
        			columns.append(", ");
        		}
        		columns.append("\n");
        	}
        	createStatement = createStatement.replaceFirst("@columns@", columns.toString());
        } catch (Exception e) {
            AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj);

		}
        return createStatement;
    }

    
    public static List<String> getTableNamesFromSql(final String query)  {
    	try {
	    	CCJSqlParserManager parseSql = new CCJSqlParserManager();
	        Statement stm = parseSql.parse(new StringReader(query));
	        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
	        return tablesNamesFinder.getTableList(stm);
    	} catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
    		return new ArrayList<>();
    	}
    }
}
