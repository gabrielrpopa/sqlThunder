/*
 * Copyright 2022-present Infinite Loop Corporation Limited, Inc.
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

package com.widescope.scripting.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.widescope.logging.AppLogger;
import org.springframework.stereotype.Component;

import com.widescope.scripting.ScriptAccess;
import com.widescope.scripting.ScriptDetail;
import com.widescope.scripting.ScriptParamCompoundObject;
import com.widescope.scripting.ScriptParamDetail;


@Component
public class ScriptingInternalDb {

	// JDBC driver name and database URL 
	private final String JDBC_DRIVER = "org.h2.Driver";   
	private final String DB_URL_DISK = "jdbc:h2:file:./scriptRepo;MODE=PostgreSQL";
	
	   
	//  Database credentials 
	private final String USER = "sa"; 
	private final String PASS = "sa"; 
	
	
	
	public ScriptingInternalDb() { }
		   
	
	private void closeHandles(	Connection conn, 
								Statement statement, 
								ResultSet rs) {
		try	{ if(rs !=null && !rs.isClosed()) { rs.close();	} }	catch(Exception ignored)	{}
		try	{ if(statement !=null && !statement.isClosed()) { statement.close();	} }	catch(Exception ignored)	{}
		try	{ if(conn !=null && !conn.isClosed()) { conn.close();	} }	catch(Exception ignored)	{}
	}
	
	
	
	
	public void createSchema(final List<String> ddlList) throws Exception {
		Connection conn = null;
		Statement statement = null; 
		try { 
			Class.forName(JDBC_DRIVER); 
			conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);  
			statement = conn.createStatement(); 
			for (String ddl : ddlList) {
				statement.executeUpdate(ddl);
	        }
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		} finally	{
			closeHandles(conn, statement, null);
	    } 
	}

	
	
	
	
	

	
	
	public static ScriptingInternalDb 
	generateSchema() throws Exception	{
		List<String> ddlList = new java.util.ArrayList<String>();
		
		try {
			ddlList.add(ScriptingInternalDb.interpreterType);
			ddlList.add(ScriptingInternalDb.interpreterType_index1);
			ddlList.add(ScriptingInternalDb.interpreterType1);
			ddlList.add(ScriptingInternalDb.interpreterType2);
			ddlList.add(ScriptingInternalDb.interpreterType3);
			ddlList.add(ScriptingInternalDb.interpreterType4);
			ddlList.add(ScriptingInternalDb.interpreterType5);
			ddlList.add(ScriptingInternalDb.interpreterType6);
			ddlList.add(ScriptingInternalDb.interpreterType7);
			ddlList.add(ScriptingInternalDb.interpreterType8);
			ddlList.add(ScriptingInternalDb.interpreterType9);
			ddlList.add(ScriptingInternalDb.interpreterType10);
			
			
			ddlList.add(ScriptingInternalDb.script);
			ddlList.add(ScriptingInternalDb.script_index1);
			ddlList.add(ScriptingInternalDb.script1);
			
			ddlList.add(ScriptingInternalDb.scriptParamTable);
			ddlList.add(ScriptingInternalDb.scriptParamTableIndex1);
			ddlList.add(ScriptingInternalDb.scriptParamTableIndex2);
			ddlList.add(ScriptingInternalDb.scriptParamTableIndex3);
			ddlList.add(ScriptingInternalDb.scriptParamTableFk1);
			ddlList.add(ScriptingInternalDb.scriptParamTableConst1);
			ddlList.add(ScriptingInternalDb.scriptParamTableConst2);
			ddlList.add(ScriptingInternalDb.scriptParam1);
							
			
			ddlList.add(ScriptingInternalDb.scriptAccess);
			ddlList.add(ScriptingInternalDb.scriptAccessIndex1);
			ddlList.add(ScriptingInternalDb.scriptAccessInsert1);
			
			ddlList.add(ScriptingInternalDb.machineNodesBridgeToScript);
			ddlList.add(ScriptingInternalDb.machineNodesBridgeToScriptIndex1);
			
			
			/*Users access to nodes*/
			ddlList.add(ScriptingInternalDb.nodesUserAccess);
			ddlList.add(ScriptingInternalDb.nodesUserAccessIndex1);
			ddlList.add(ScriptingInternalDb.nodesUserAccessIndex2);
			ddlList.add(ScriptingInternalDb.nodesUserAccessConst1);
			
			
		
			ScriptingInternalDb h2OperationDisk = new ScriptingInternalDb();
			h2OperationDisk.createSchema(ddlList);
			return h2OperationDisk;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	



	
	
	public static String 
	interpreterType = "CREATE TABLE IF NOT EXISTS interpreterType ("
							+ "	interpreterId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
						    + "	interpreterName VARCHAR(MAX),"
						    + "	interpreterVersion VARCHAR(MAX),"
						    + "	interpreterPath VARCHAR(MAX),"
							+ "	command VARCHAR(MAX) ,"
							+ "	fileExtensions VARCHAR(MAX) "
						+ ")";
	
	
	public static String 
	interpreterType_const1 = "ALTER TABLE interpreterType ADD CONSTRAINT IF NOT EXISTS ck_interpreterType_1  "
							+ "CHECK (interpreterName IN ('PYTHON2', "
														+ "	'PYTHON3', "
														+ "	'GROOVY', "
														+ " 'PERL', "
														+ " 'R', "
														+ " 'RUBY', "
														+ " 'WINDOWS BATCH', "
														+ " 'BASH', "
														+ " 'POWERSHELL', "
														+ " 'JULIA') );";
	
	public static String 
	interpreterType_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS "
							+ "idx_interpreterType_1 ON interpreterType(interpreterName, interpreterVersion);";
	
	
	/*interpreterName must use com.widescope.scripting.interpreter.Interpreter*/
	public static String 
	interpreterType1 = "INSERT INTO interpreterType (interpreterName, interpreterVersion, interpreterPath, command, fileExtensions) "
						+ "VALUES 					('GROOVY', 			'3.0', 				'D:\\Interpreter\\Groovy\\Groovy30', 	'groovy', 'groovy') ";
	
	public static String 
	interpreterType2 = "INSERT INTO interpreterType (interpreterName, interpreterVersion, interpreterPath, command, fileExtensions) "
						+ "VALUES 					('PERL', 			'3.8.1', 			'D:\\Interpreter\\Perl\\Perl381', 	'perl', 'pl') ";
	
	public static String 
	interpreterType3 = "INSERT INTO interpreterType (interpreterName, interpreterVersion, interpreterPath, command, fileExtensions) "
						+ "VALUES 					('PYTHON2', 			'2.7.1', 			'D:\\Interpreter\\Python\\Python271', 	'python', 'py') ";
	
	public static String 
	interpreterType4 = "INSERT INTO interpreterType (interpreterName, interpreterVersion, interpreterPath, command, fileExtensions) "
						+ "VALUES 					('PYTHON3', 			'3.8.1', 			'D:\\Interpreter\\Python\\Python381', 	'python', 'py') ";
	
	public static String 
	interpreterType5 = "INSERT INTO interpreterType (interpreterName, interpreterVersion, interpreterPath, command, fileExtensions) "
						+ "VALUES 					('R', 				'4.1.0', 			'D:\\Interpreter\\R\\R410', 	'Rscript', 'r') ";
	
	public static String 
	interpreterType6 = "INSERT INTO interpreterType (interpreterName, interpreterVersion, interpreterPath, command, fileExtensions) "
						+ "VALUES 					('RUBY', 			'3.0.2', 			'D:\\Interpreter\\Ruby\\Ruby302', 	'ruby', 'rb') ";
	
	public static String 
	interpreterType7 = "INSERT INTO interpreterType (interpreterName, interpreterVersion, interpreterPath, command, fileExtensions) "
						+ "VALUES 					('WINDOWS BATCH', 			'4.1.0', 			'', 	'cmd.exe', 	'bat') ";
	
	public static String 
	interpreterType8 = "INSERT INTO interpreterType (interpreterName, interpreterVersion, interpreterPath, command, fileExtensions) "
						+ "VALUES 					('BASH', 			'5.1', 				'', 	'bash', 'sh') ";
	
		
	public static String 
	interpreterType9 = "INSERT INTO interpreterType (interpreterName, interpreterVersion, interpreterPath, command, fileExtensions) "
						+ "VALUES 					('POWERSHELL', 		'7.1', 				'', 	'', 	'ps') ";

	public static String 
	interpreterType10 = "INSERT INTO interpreterType (interpreterName, interpreterVersion, interpreterPath, command, fileExtensions) "
						+ "VALUES 					('JULIA', 		'1.6.2', 				'D:\\Interpreter\\Julia\\Julia162', 	'', 	'jl') ";
	
	
	public static String 
	interpreterType11 = "INSERT INTO interpreterType (interpreterName, interpreterVersion, interpreterPath, command, fileExtensions) "
						+ "VALUES 					('UNKNOWN', 		'0.0.0', 				'', 	'', 	'') ";

	
	
	
	
	
	/**
	 * Launching a script can be of this form: python scriptName.py paramString
	 * python scriptName.py -c @param1@ -d @param2@ -f @param2@
	 */
	
	public static String 
	script = "CREATE TABLE IF NOT EXISTS "
			+ "script (scriptId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
					+ "	creatorUserId BIGINT, "
					+ "	scriptName VARCHAR(MAX), "
					+ "	paramString VARCHAR(MAX), "    /*main's command line params. It can be a JSON string*/
					+ "	mainFile VARCHAR(MAX), "
					+ "	predictFile VARCHAR(MAX), "
					+ "	predictFunc VARCHAR(MAX), "
					+ "	interpreterId BIGINT, "
					+ "	scriptVersion BIGINT, "
					+ "	compliance VARCHAR(1) DEFAULT 'Y'"
					+ ")";


	public static String 
	script_index1 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_script_1 ON script(scriptName, scriptVersion);";

	
	public static String 
	scriptTableConst1 = "ALTER TABLE script ADD CONSTRAINT IF NOT EXISTS ck_script_1 CHECK (compliance IN ('Y', 'N') );";

	
	
	
	public static String 
	script1 = "INSERT INTO script (creatorUserId, scriptName, paramString, mainFile, predictFile, predictFunc, interpreterId, scriptVersion) "
						+ "VALUES (1, 'python-script', '', 'python-script.py', '', '' , 4, 1) ";	

	
	
	public static String 
	scriptParamTable = "CREATE TABLE IF NOT EXISTS "
			+ "scriptParam (scriptParamId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
					+ "	scriptId BIGINT, "
					+ "	paramName VARCHAR(MAX), "
					+ "	paramType VARCHAR(MAX), "
					+ "	paramDimension VARCHAR(MAX), "   /*Example [90][20][20] OR [?][?][?] when unknown*/
					+ "	paramDefaultValue VARCHAR(MAX), "
					+ "	paramPosition VARCHAR(MAX), "
					+ "	paramOrder BIGINT "
					+ ")";


	public static String 
	scriptParamTableIndex1 = "CREATE INDEX IF NOT EXISTS idxScriptParam1 ON scriptParam(scriptId);";
	public static String 
	scriptParamTableIndex2 = "CREATE UNIQUE INDEX IF NOT EXISTS idxScriptParam2 ON scriptParam(paramName, paramOrder);";
	public static String 
	scriptParamTableIndex3 = "CREATE UNIQUE INDEX IF NOT EXISTS idxScriptParam3 ON scriptParam(paramName, paramName);";
	public static String 
	scriptParamTableFk1 = "ALTER TABLE scriptParam ADD CONSTRAINT IF NOT EXISTS scriptParamFk1 FOREIGN KEY ( scriptId ) REFERENCES script( scriptId );";
	public static String 
	scriptParamTableConst1 = "ALTER TABLE scriptParam ADD CONSTRAINT IF NOT EXISTS ck_scriptParam_1 "
									+ "CHECK (paramType IN ('STRING', 'FLOAT', 'DECIMAL', 'LONG', 'DOUBLE', 'INTEGER', 'BOOL', 'DATE', 'TIMESTAMP') );";

	public static String 
	scriptParamTableConst2 = "ALTER TABLE scriptParam ADD CONSTRAINT IF NOT EXISTS ck_scriptParam_2 "
									+ "CHECK (paramPosition IN ('INPUT', 'OUTPUT') );";
	
	
	public static String 
	scriptParam1 = "INSERT INTO scriptParam (scriptId, paramName, paramType, paramDimension, paramDefaultValue, paramPosition, paramOrder) "
						+ "VALUES (1, '@counter@', 'INTEGER', 1, '1', 'INPUT', 1 ) ";	
	
	
	/////////////////////
	public static String 
	scriptAccess = "CREATE TABLE IF NOT EXISTS "
			+ "scriptAccess (id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
						+ "	scriptId BIGINT, "
						+ "	userId BIGINT"
						+ ")";


	public static String 
	scriptAccessIndex1 = "CREATE INDEX IF NOT EXISTS idxScriptAccess1 ON scriptAccess(scriptId);";
	

	public static String 
	scriptAccessInsert1 = "INSERT INTO scriptAccess (scriptId, userId) VALUES (1,  1 ) ";	
	
	/////////////////////
	
	
		

	
	
	public static String 
	machineNodesBridgeToScript = "CREATE TABLE IF NOT EXISTS "
								+ "machineNodesBridgeToScript (id INT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
													+ "	nodeId BIGINT, "
													+ "	scriptId BIGINT "
													+ ")";
	
	public static String 
	machineNodesBridgeToScriptIndex1 = "CREATE UNIQUE INDEX IF NOT EXISTS idxmachineNodesBridgeToScript1 ON machineNodesBridgeToScript(nodeId, scriptId);";
	
	
	
	
		
	public static String 
	nodesUserAccess = "CREATE TABLE IF NOT EXISTS "
				+ "nodesUserAccess (id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
									+ "	userId BIGINT,"
									+ "	accessType VARCHAR(1),"
									+ "	nodeId BIGINT )";

	
	public static String 
	nodesUserAccessConst1 = "ALTER TABLE nodesUserAccess ADD CONSTRAINT IF NOT EXISTS ck_nodesUserAccess_1 "
									+ "CHECK (accessType IN ('R', 'W', 'E') );";  // Read/Write/Execute
	

	public static String nodesUserAccessIndex1 = "CREATE INDEX IF NOT EXISTS idx_nodesUserAccess_1 ON nodesUserAccess(userId);";
	public static String nodesUserAccessIndex2 = "CREATE UNIQUE INDEX IF NOT EXISTS idx_nodesUserAccess_1 ON nodesUserAccess(userId, nodeId);";
	

	
	/*
	 
	 public static String 
	scriptType = "CREATE TABLE IF NOT EXISTS "
						+ "scriptType ("
						+ "	scriptTypeId BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,"
					    + "	scriptTypeName VARCHAR(999),"
					    + "	scriptSubType1Name VARCHAR(999),"
					    + "	scriptSubType2Name VARCHAR(999),"
					    + "	scriptSubType3Name VARCHAR(999),"
					    + "	scriptSubType4Name VARCHAR(999),"
					    + "	scriptSubType5Name VARCHAR(999),"
					    + "	scriptSubType6Name VARCHAR(999),"
					    + "	scriptTypeDescription VARCHAR(9999)"
						+ ")";
	
	public static String 
	scriptType1 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('Command', 		'', 				'', 				'', 				'', 				'',                '',                 '' ) ";	

	public static String 
	scriptType2 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 		'Statistics', 				'', 				'', 				'', 				'' , '' ,              '') ";	

	public static String 
	scriptType3 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'LogisticRegression', '' ,             '') ";	

	
	public static String 
	scriptType4 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'SupportVectorMachine', '' ,             '') ";	

	public static String 
	scriptType5 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'DecisionTree', 'ClasificationAndRegressionTree' ,             '') ";	


	public static String 
	scriptType6 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'DecisionTree', 'IterativeDichotomoser3' ,             '') ";	

	
	public static String 
	scriptType7 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'DecisionTree', 'C4.5' ,             '') ";	

	public static String 
	scriptType8 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'DecisionTree', 'C5.0' ,             '') ";	

	
	public static String 
	scriptType9 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'DecisionTree', 'Chi-SquareAutomaticInteractionDetection' , '') ";	

	public static String 
	scriptType10 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'DecisionTree', 'DecissionStump' , '') ";	

	
	public static String 
	scriptType11 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'DecisionTree', 'ConditionalDecissionTrees' , '') ";	

	public static String 
	scriptType12 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'DecisionTree', 'MS' , '') ";	

	public static String 
	scriptType20 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'RandomForest', '' , '') ";	
	
	public static String 
	scriptType30 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'Bayesian', '' , '') ";	
	
	public static String 
	scriptType31 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'Bayesian', 'NaiveBayes' , '') ";	

	public static String 
	scriptType32 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'Bayesian', 'AveragedOneDependenceEstimators' , '') ";	

	
	public static String 
	scriptType33 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'Bayesian', 'BayesianBeliefNetwork' , '') ";	

	
	public static String 
	scriptType34 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'SurfaceLearning', 	'Bayesian', 'MultinomialNaiveBayes' , '') ";	

	
	public static String 
	scriptType40 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'DeepLearning', 	'CNN', 'Dense' , '') ";	

	
	public static String 
	scriptType50 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'DeepLearning', 	'RNN', 'Dense' , '') ";	

	public static String 
	scriptType60 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Classification',	'DeepLearning', 	'ANN', 'Dense' , '') ";	

	
	public static String 
	scriptType70 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Regression',	'SurfaceLearning', 	'LinearRegression', '' , '') ";	

	public static String 
	scriptType71 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Regression',	'SurfaceLearning', 	'LassoRegression', '' , '') ";	

	
	public static String 
	scriptType72 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Regression',	'SurfaceLearning', 	'LogisticRegression', '' , '') ";	

	
	public static String 
	scriptType73 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Regression',	'SurfaceLearning', 	'SupportVectorMachines', '' , '') ";
	
	public static String 
	scriptType74 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Regression',	'SurfaceLearning', 	'MultivariateRegression', '' , '') ";	

	
	public static String 
	scriptType75 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Regression',	'SurfaceLearning', 	'MultipleRegressionAlgorithm', '' , '') ";	


	public static String 
	scriptType80 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Regression',	'DeepLearning', 	    'RNN',              'Dense' ,           '') ";	

	public static String 
	scriptType90 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Regression',	'DeepLearning', 	    'CNN',              'Dense' ,           '') ";	

	
	public static String 
	scriptType100 = "INSERT INTO scriptType (scriptTypeName, scriptSubType1Name, scriptSubType2Name, scriptSubType3Name, scriptSubType4Name, scriptSubType5Name, scriptSubType6Name, scriptTypeDescription) "
						+ "VALUES 		('StructuredDataAlgo', 'Learning',		'Supervised', 		'Regression',	'DeepLearning', 	    'ANN',              'Dense' ,           '') ";	
 
	  
	 */
	
	
	////////////////////////////////////////// Interpreters /////////////////////////////////////////////////////////////
	public int 
	interpreterAdd(	final String interpreterName, 
					final String interpreterVersion, 
					final String interpreterPath,
					final String command,
					final String fileExtensions ) throws Exception	{

		int ret = -1;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO interpreterType (interpreterName, "
													+ "	interpreterVersion, "
													+ " interpreterPath, "
													+ " command, "
													+ " fileExtensions"
													+ ") "
										+ "VALUES(?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, interpreterName);
			preparedStatement.setString(2, interpreterVersion);
			preparedStatement.setString(3, interpreterPath);
			preparedStatement.setString(4, command);
			preparedStatement.setString(5, fileExtensions);
            ret = preparedStatement.executeUpdate();

            
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public int 
	interpreterUpdate(	final int interpreterId, 
						final String interpreterName, 
						final String interpreterVersion, 
						final String interpreterPath,
						final String command,
						final String fileExtensions ) throws Exception	{
		int ret;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "UPDATE interpreterType SET interpreterName=?, "
													+ "	interpreterVersion=?, "
													+ " interpreterPath=?, "
													+ " command=?, "
													+ " fileExtensions=?"
													+ " WHERE  interpreterId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, interpreterName);
			preparedStatement.setString(2, interpreterVersion);
			preparedStatement.setString(3, interpreterPath);
			preparedStatement.setString(4, command);
			preparedStatement.setString(5, fileExtensions);
			preparedStatement.setInt(6, interpreterId);
            ret = preparedStatement.executeUpdate();

            
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	public int 
	interpreterDelete( final int interpreterId) throws Exception	{
		int ret;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "DELETE interpreterType WHERE interpreterId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setInt(1, interpreterId);
            ret = preparedStatement.executeUpdate();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public List<InterpreterType>
	interpreterByName(final String interpreterName) throws Exception {
		List<InterpreterType> ret = new ArrayList<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT interpreterId, "
								+ "interpreterName, "
								+ "interpreterVersion, "
								+ "interpreterPath, "
								+ "command, "
								+ "fileExtensions "
								+ "FROM interpreterType "
								+ "WHERE interpreterName =  ? ";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, "%" + interpreterName + "%");
			ResultSet rs = preparedStatement.executeQuery();

            while ( rs.next() ) {
            	InterpreterType b = new InterpreterType(rs.getInt("interpreterId"),
			            								rs.getString("interpreterName"),
			            								rs.getString("interpreterVersion"),
			            								rs.getString("interpreterPath"),
			            								rs.getString("command"),
			            								rs.getString("fileExtensions")
			            								);
            	ret.add(b);
            }
            
            
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public InterpreterType
	getInterpreterByName(final String interpreterName) throws Exception {

		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT interpreterId, "
							+ "interpreterName, "
							+ "interpreterVersion, "
							+ "interpreterPath, "
							+ "command, "
							+ "fileExtensions "
							+ "FROM interpreterType "
							+ "WHERE interpreterName =  ? ";

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, "%" + interpreterName + "%");
			ResultSet rs = preparedStatement.executeQuery();

			InterpreterType b = new InterpreterType(rs.getInt("interpreterId"),
					rs.getString("interpreterName"),
					rs.getString("interpreterVersion"),
					rs.getString("interpreterPath"),
					rs.getString("command"),
					rs.getString("fileExtensions")
			);


			rs.close();
			return b;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public List<InterpreterType>
	allInterpreters() throws Exception {
		List<InterpreterType> ret = new ArrayList<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT interpreterId, interpreterName,interpreterVersion, interpreterPath, command, fileExtensions FROM interpreterType GROUP BY interpreterName ";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			ResultSet rs = preparedStatement.executeQuery();
            
            while ( rs.next() ) {
            	InterpreterType b = new InterpreterType(rs.getInt("interpreterId"), 
								            			rs.getString("interpreterName"),
								            			rs.getString("interpreterVersion"),
								            			rs.getString("interpreterPath"),
								            			rs.getString("command"),
								            			rs.getString("fileExtensions")
            											);
            	ret.add(b);
            }
            
            
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public List<InterpreterType>
	interpreterByNameAndVersion(final String interpreterName,
								final String interpreterVersion) throws Exception {
		List<InterpreterType> ret = new ArrayList<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT interpreterId, "
								+ "interpreterName, "
								+ "interpreterVersion, "
								+ "interpreterPath, "
								+ "command, "
								+ "fileExtensions "
								+ "FROM interpreterType "
								+ "WHERE interpreterName = ? "
								+ "AND interpreterVersion = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, interpreterName);
			preparedStatement.setString(2, interpreterVersion);
			ResultSet rs = preparedStatement.executeQuery();
            preparedStatement.close();
            if ( rs.next() ) {
            	InterpreterType b = new InterpreterType(rs.getInt("interpreterId"),
															rs.getString("interpreterName"),
															rs.getString("interpreterVersion"),
															rs.getString("interpreterPath"),
															rs.getString("command"),           			                           
															rs.getString("fileExtensions")
															);
            	ret.add(b);
            }
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}


	public InterpreterType
	interpreter(final String interpreterName) throws Exception {
		InterpreterType ret = new InterpreterType();
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT interpreterId, "
				+ "interpreterName, "
				+ "interpreterVersion, "
				+ "interpreterPath, "
				+ "command, "
				+ "fileExtensions "
				+ "FROM interpreterType "
				+ "WHERE interpreterName = ? "
				;

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, interpreterName);
			ResultSet rs = preparedStatement.executeQuery();
			if ( rs.next() ) {
				ret = new InterpreterType(rs.getInt("interpreterId"),
						rs.getString("interpreterName"),
						rs.getString("interpreterVersion"),
						rs.getString("interpreterPath"),
						rs.getString("command"),
						rs.getString("fileExtensions")
				);
			}
			preparedStatement.close();
			rs.close();
			return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	
	public InterpreterType
	interpreterById(final int interpreterId) throws Exception {
		InterpreterType ret = new InterpreterType();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT interpreterId, "
								+ "interpreterName, "
								+ "interpreterVersion, "
								+ "interpreterPath, "
								+ "command, "
								+ "fileExtensions "
								+ "FROM interpreterType "
								+ "WHERE interpreterId = ? "
								;
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setInt(1, interpreterId);
			ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
            	ret = new InterpreterType(rs.getInt("interpreterId"),
					 					rs.getString("interpreterName"),
										rs.getString("interpreterVersion"),
										rs.getString("interpreterPath"),
										rs.getString("command"),           			                           
										rs.getString("fileExtensions")
										);
            }
            preparedStatement.close();
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	/******************************************* Scripts************************************************************************************** */

	
	public ScriptDetail 
	scriptAdd(	final long creatorUserId, 
				final String scriptName, 
				final String mainFile,
				final String paramString,
				final String predictFile,
				final String predictFunc, 
				int interpreterId,
				final int scriptVersion) throws Exception {

		ScriptDetail ret = null;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO script (creatorUserId, scriptName, mainFile, paramString, predictFile, predictFunc, interpreterId, scriptVersion) "
						+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, creatorUserId);
            preparedStatement.setString(2, scriptName);
            preparedStatement.setString(3, mainFile);
            preparedStatement.setString(4, paramString);
			preparedStatement.setString(5, predictFile);
			preparedStatement.setString(6, predictFunc);
            preparedStatement.setInt(7, interpreterId);
            preparedStatement.setInt(8, scriptVersion);
            preparedStatement.executeUpdate();
            String sqlStringGetId = "SELECT scriptId, "
								+ " scriptName, "
								+ " creatorUserId, "
								+ " mainFile, "
								+ " i.interpreterId, "
								+ " i.interpreterName, "
								+ " i.interpreterVersion, " 
								+ " i.interpreterPath, "
								+ " i.command, "
								+ " paramString , "
								+ " predictFile, "
								+ " predictFunc, "
								+ " scriptVersion, "
								+ " compliance"
								+ " FROM script s JOIN interpreterType i ON s.interpreterId = i.interpreterId WHERE scriptName = ? AND scriptVersion = ?";
          
    		try (PreparedStatement preparedStatement2 = conn.prepareStatement(sqlStringGetId)) {
    			preparedStatement2.setString(1, scriptName);
    			preparedStatement2.setInt(2, scriptVersion);
                ResultSet rs = preparedStatement2.executeQuery();
                if ( rs.next() ) {
                	ret = new ScriptDetail(	rs.getInt("interpreterId"),
							rs.getString("interpreterName"),
							rs.getString("interpreterVersion"),
							rs.getString("interpreterPath"),
							rs.getString("command"),
							rs.getInt("scriptId"),
							rs.getString("scriptName"),
							rs.getString("mainFile"),
							rs.getLong("creatorUserId"),
							"",
							rs.getString("paramString"),
							rs.getString("predictFile"),
							rs.getString("predictFunc"),
							rs.getInt("scriptVersion"),
							rs.getString("compliance")
			             );
                }
                rs.close();
			} catch (SQLException e)	{
				throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
			} catch (Exception e) {
				throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
			}
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	

	public void 
	scriptUpdate(final long creatorUserId, 
				 final String scriptName, 
				 final String mainFile,
				 final String paramString,
				 final String predictFile,
				 final String predictFunc, 
				 final int interpreterId,
				 final int scriptVersion
				 ) throws Exception {

		Class.forName(JDBC_DRIVER); 
		String sqlString = "MERGE INTO script (creatorUserId, scriptName,  mainFile, paramString, predictFile, predictFunc, interpreterId, scriptVersion) KEY(scriptName, scriptVersion) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, creatorUserId);
			preparedStatement.setString(2, scriptName);
			preparedStatement.setString(3, mainFile);
			preparedStatement.setString(4, paramString);
			preparedStatement.setString(5, predictFile);
			preparedStatement.setString(6, predictFunc);
			preparedStatement.setInt(7, interpreterId);
			preparedStatement.setInt(8, scriptVersion);
			preparedStatement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	scriptDelete(final String scriptName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE script WHERE scriptId = ? ";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			preparedStatement.setString(1, scriptName );
            preparedStatement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	
	public void scriptDelete(final int scriptId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE script WHERE scriptId = ? ";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			preparedStatement.setInt(1, scriptId );
            preparedStatement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public void scriptDeleteAll(final long scriptId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE script WHERE scriptId = ? ";
		String sqlString1 = "DELETE scriptAccess WHERE scriptId = ? ";
		String sqlString2 = "DELETE scriptParam WHERE scriptId = ?";
		
		try (	Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString);
				PreparedStatement preparedStatement1 = conn.prepareStatement(sqlString1);
				PreparedStatement preparedStatement2 = conn.prepareStatement(sqlString2);
			) {
           
            preparedStatement1.setLong(1, scriptId );
            preparedStatement1.executeUpdate();
            
            preparedStatement2.setLong(1, scriptId );
            preparedStatement2.executeUpdate();
            
            preparedStatement.setLong(1, scriptId );
            preparedStatement.executeUpdate();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public boolean 
	scriptVersionDelete(final String scriptName, 
						final long scriptVersionId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE script WHERE scriptName = ? AND scriptVersion = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			preparedStatement.setString(1, scriptName );
			preparedStatement.setLong(2, scriptVersionId );
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	scriptVersionDelete(final long scriptId,
						final long scriptVersionId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE script WHERE scriptId = ? AND scriptVersion = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			preparedStatement.setLong(1, scriptId );
			preparedStatement.setLong(2, scriptVersionId );
            preparedStatement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}

	
	public List<ScriptDetail> 
	getScriptById(final int scriptId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT scriptId, "
								+ " scriptName, "
								+ " creatorUserId, "
								+ " mainFile, "
								+ " i.interpreterId, "
								+ " i.interpreterName, "
								+ " i.interpreterVersion, " 
								+ " i.interpreterPath, "
								+ " i.command, "
								+ " paramString , "
								+ " predictFile, "
								+ " predictFunc, "
								+ " scriptVersion, "
								+ " compliance"
								+ " FROM script s JOIN interpreterType i ON s.interpreterId = i.interpreterId WHERE scriptId = ?";
		List<ScriptDetail> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setInt(1, scriptId);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	ScriptDetail b = new ScriptDetail(	rs.getInt("interpreterId"),
													rs.getString("interpreterName"),
													rs.getString("interpreterVersion"),
													rs.getString("interpreterPath"),
													rs.getString("command"),
													rs.getInt("scriptId"),
													rs.getString("scriptName"),
													rs.getString("mainFile"),
													rs.getLong("creatorUserId"),
													"",
													rs.getString("paramString"),
													rs.getString("predictFile"),
													rs.getString("predictFunc"),
													rs.getInt("scriptVersion"),
													rs.getString("compliance")
									             );
            	ret.add(b);
            }
            rs.close();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
		
	
	
	public ScriptDetail 
	getScript(final long scriptId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = " SELECT s.scriptId, "
								+ "	s.scriptName, "
								+ " s.creatorUserId, "
								+ " s.mainFile, "
								+ " i.interpreterId, "
								+ " i.interpreterName, "
								+ " i.interpreterVersion, " 
								+ " i.interpreterPath, "
								+ " i.command, "
								+ " s.paramString , "
								+ " s.predictFile, "
								+ " s.predictFunc , "
								+ " s.scriptVersion, "
								+ " s.compliance " 
								+ " FROM script s JOIN interpreterType i ON s.interpreterId = i.interpreterId WHERE scriptId = ?";
		ScriptDetail ret = new ScriptDetail();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setLong(1, scriptId);
            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
            	ret = new ScriptDetail( rs.getInt("interpreterId"),
            							rs.getString("interpreterName"),
            							rs.getString("interpreterVersion"),
            							rs.getString("interpreterPath"),
            							rs.getString("command"),
            			                rs.getLong("scriptId"),
									    rs.getString("scriptName"),
									    rs.getString("mainFile"),
									    rs.getLong("creatorUserId"),
									    "",
									    rs.getString("paramString"),
									    rs.getString("predictFile"),
									    rs.getString("predictFunc"),
									    rs.getInt("scriptVersion"),
									    rs.getString("compliance")
									   );

            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}



	public ScriptDetail
	getScript(final String scriptName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = " SELECT s.scriptId, "
							+ "	s.scriptName, "
							+ " s.creatorUserId, "
							+ " s.mainFile, "
							+ " i.interpreterId, "
							+ " i.interpreterName, "
							+ " i.interpreterVersion, "
							+ " i.interpreterPath, "
							+ " i.command, "
							+ " s.paramString , "
							+ " s.predictFile, "
							+ " s.predictFunc , "
							+ " s.scriptVersion, "
							+ " s.compliance "
							+ " FROM script s JOIN interpreterType i ON s.interpreterId = i.interpreterId WHERE s.scriptName = ?";
		ScriptDetail ret = new ScriptDetail();

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS);
			 PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, scriptName);
			ResultSet rs = preparedStatement.executeQuery();
			if ( rs.next() ) {
				ret = new ScriptDetail( rs.getInt("interpreterId"),
						rs.getString("interpreterName"),
						rs.getString("interpreterVersion"),
						rs.getString("interpreterPath"),
						rs.getString("command"),
						rs.getInt("scriptId"),
						rs.getString("scriptName"),
						rs.getString("mainFile"),
						rs.getLong("creatorUserId"),
						"",
						rs.getString("paramString"),
						rs.getString("predictFile"),
						rs.getString("predictFunc"),
						rs.getInt("scriptVersion"),
						rs.getString("compliance")
				);

			}
			rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}

		return ret;
	}
	
	
	public List<ScriptDetail> 
	getScriptByName(final String scriptName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT s.scriptId, "
								+ " s.scriptName, "
								+ " s.mainFile, "
								+ " s.creatorUserId, "
								+ " s.interpreterId, "
								+ " s.scriptVersion, "
								+ " s.paramString, "
								+ " s. predictFile, "
								+ " s.predictFunc, "
								+ " s.scriptVersion, "
								+ " s.compliance, "
								+ " i.interpreterName, "
								+ " i.interpreterVersion, "
								+ " i.interpreterPath, "
								+ " i.command "
								+ " FROM script s JOIN interpreterType i ON s.interpreterId = i.interpreterId "
								+ " WHERE s.scriptName LIKE ? "
								+ " ORDER BY s.scriptName, s.scriptVersion";
		if(scriptName == null || scriptName.isBlank() || scriptName.isEmpty())
			sqlString ="SELECT  s.scriptId, "
							+ " s.scriptName, "
							+ " s.mainFile, "
							+ " s.creatorUserId, "
							+ " s.interpreterId, "
							+ " s.scriptVersion, "
							+ " s.paramString, "
							+ " s. predictFile, "
							+ " s.predictFunc, "
							+ " s.scriptVersion, "
							+ " s.compliance, "
							+ " i.interpreterName, "
							+ " i.interpreterVersion, "
							+ " i.interpreterPath, "
							+ " i.command "
						+ " FROM script s JOIN interpreterType i ON s.interpreterId = i.interpreterId "
						+ " ORDER BY s.scriptName, s.scriptVersion";

		
		List<ScriptDetail> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			if(scriptName != null && !scriptName.isBlank() && !scriptName.isEmpty())
				preparedStatement.setString(1, "%" + scriptName + "%");
            
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	ScriptDetail b = new ScriptDetail( 	rs.getInt("interpreterId"),
            										rs.getString("interpreterName"),
            										rs.getString("interpreterVersion"),
            										rs.getString("interpreterPath"),
            										rs.getString("command"),
            										rs.getInt("scriptId"),
            										rs.getString("scriptName"),
            										rs.getString("mainFile"),
            										rs.getLong("creatorUserId"),
            										"",
 									               	rs.getString("paramString"),
 									               	rs.getString("predictFile"),
 									               	rs.getString("predictFunc"),
            										rs.getInt("scriptVersion"),
            										rs.getString("compliance")
									             );
            	ret.add(b);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	public List<ScriptDetail> 
	getScriptByUser(final long creatorUserId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString ="SELECT s.scriptId, "
								+ " s.creatorUserId, "
								+ " s.scriptName, "
								+ " s.mainFile, "
								+ " s.interpreterId, "
								+ " s.paramString, "
								+ " s.predictFile, "
								+ " s.predictFunc, "
								+ " s.scriptVersion, "
								+ " s.compliance, "
								+ " i.interpreterName, "
								+ " i.interpreterVersion, "
								+ " i.interpreterPath, "
								+ " i.command "
								+ "FROM script s JOIN interpreterType i ON s.interpreterId = i.interpreterId WHERE s.creatorUserId = ?";
		if(creatorUserId < 1 )
			throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.db, "User Id not set properly"));


		
		List<ScriptDetail> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			
			preparedStatement.setLong(1, creatorUserId);
            
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	ScriptDetail b = new ScriptDetail( 	rs.getInt("interpreterId"),
            										rs.getString("interpreterName"),
            										rs.getString("interpreterVersion"),
            										rs.getString("interpreterPath"),
            										rs.getString("command"),
            										rs.getInt("scriptId"),
            										rs.getString("scriptName"),
            										rs.getString("mainFile"),
            										rs.getLong("creatorUserId"),
            										"",
 									               	rs.getString("paramString"),
 									               	rs.getString("predictFile"),
 									               	rs.getString("predictFunc"),
            										rs.getInt("scriptVersion"),
            										rs.getString("compliance")
									             );
            	ret.add(b);
            }
            rs.close();

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		return ret;
	}
	
	public List<ScriptDetail> 
	getScriptByNameAndVersion(	final String scriptName, 
								final int scriptVersion) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT s.scriptId, "
								+ "s.scriptName, "
								+ "s.mainFile, "
								+ "s.creatorUserId, "
								+ "s.paramString, "
								+ "s.predictFile, "
								+ "s.predictFunc, "
								+ "s.interpreterId, "
								+ "s.scriptVersion, "
								+ "s.compliance, "
								+ "i.interpreterName, "
								+ "i.interpreterVersion,  "
								+ "i.interpreterPath, "
								+ "i.command, "
								+ "i.fileExtensions "
						+ " FROM script s JOIN interpreterType i ON i.interpreterId = s.interpreterId "
						+ " WHERE scriptName = ? AND scriptVersion = ?";
		List<ScriptDetail> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, scriptName);
            preparedStatement.setInt(2, scriptVersion);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	ScriptDetail b = new ScriptDetail(	rs.getInt("interpreterId"),
            										rs.getString("interpreterName"),
            										rs.getString("interpreterVersion"),
            										rs.getString("interpreterPath"),
            										rs.getString("command"),
            										rs.getInt("scriptId"),
            										rs.getString("scriptName"),
            										rs.getString("mainFile"),
            										rs.getLong("creatorUserId"),
            										"",
 									                rs.getString("paramString"),
 									                rs.getString("predictFile"),
 									                rs.getString("predictFunc"),
            										rs.getInt("scriptVersion"),	
            										rs.getString("compliance")
									             );
            	ret.add(b);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	
	
	//////////////////// Script Params

	public ScriptParamCompoundObject 
	getScriptParams(	final String scriptName,
						final long scriptVersion) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT scriptParamId,"
								+ "scriptId, "
								+ "paramName, "
								+ "paramType, "
								+ "paramDimension, "
								+ "paramDefaultValue, "
								+ "paramPosition, "
								+ "paramOrder "
						+ " FROM scriptParam "
						+ " WHERE scriptId = (SELECT scriptId FROM script WHERE scriptName = ? AND scriptVersion = ? )";
		ScriptParamCompoundObject ret = new ScriptParamCompoundObject();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setString(1, scriptName);
            preparedStatement.setLong(2, scriptVersion);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	ScriptParamDetail scriptParamDetail = new ScriptParamDetail(rs.getInt("scriptParamId"),
						            										rs.getInt("scriptId"),
						            										rs.getString("paramName"),
						            										rs.getString("paramType"),
						            										rs.getString("paramDimension"),
						            										rs.getString("paramDefaultValue"),
						            										rs.getString("paramPosition"),
						            										rs.getInt("paramOrder")
											             					);
            	
            	ret.addScriptParam(scriptParamDetail);
            }
            rs.close();
            preparedStatement.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	
	public List<ScriptParamDetail> 
	getScriptParams(final long scriptId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT scriptParamId,"
								+ "scriptId, "
								+ "paramName, "
								+ "paramType, "
								+ "paramDimension, "
								+ "paramDefaultValue, "
								+ "paramPosition, "
								+ "paramOrder "
						+ " FROM scriptParam "
						+ " WHERE scriptId = ?";
		List<ScriptParamDetail> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setLong(1, scriptId);
            ResultSet rs = preparedStatement.executeQuery();
            while ( rs.next() ) {
            	ScriptParamDetail scriptParamDetail = new ScriptParamDetail(rs.getInt("scriptParamId"),
						            										rs.getInt("scriptId"),
						            										rs.getString("paramName"),
						            										rs.getString("paramType"),
						            										rs.getString("paramDimension"),
						            										rs.getString("paramDefaultValue"),
						            										rs.getString("paramPosition"),
						            										rs.getInt("paramOrder")
											             					);
            	
            	ret.add(scriptParamDetail);
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}


	public ScriptParamDetail 
	getScriptParam(final int scriptId, final String paramName) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT scriptParamId,"
								+ "scriptId, "
								+ "paramName, "
								+ "paramType, "
								+ "paramDimension, "
								+ "paramDefaultValue, "
								+ "paramPosition, "
								+ "paramOrder "
						+ " FROM scriptParam "
						+ " WHERE scriptId = ? AND paramName = ?";
		ScriptParamDetail ret = null;
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
            preparedStatement.setInt(1, scriptId);
            preparedStatement.setString(2, paramName);
            ResultSet rs = preparedStatement.executeQuery();
            if ( rs.next() ) {
            	ret = new ScriptParamDetail(rs.getInt("scriptParamId"),
						            										rs.getInt("scriptId"),
						            										rs.getString("paramName"),
						            										rs.getString("paramType"),
						            										rs.getString("paramDimension"),
						            										rs.getString("paramDefaultValue"),
						            										rs.getString("paramPosition"),
						            										rs.getInt("paramOrder")
											             					);
            	
            }
            rs.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
		
		return ret;
	}
	
	
	public ScriptParamDetail 
	scriptParamAdd(	final long scriptId,
					final String paramName, 
					final String paramType, 
					final String paramDimension,
					final String paramDefaultValue,
					final String paramPosition,
					final int paramOrder
					) throws Exception {
		ScriptParamDetail script = null;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO scriptParam (scriptId, paramName, paramType, paramDimension, paramDefaultValue, paramPosition, paramOrder) "
						+ " VALUES(?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, scriptId);
			preparedStatement.setString(2, paramName);
            preparedStatement.setString(3, paramType);
            preparedStatement.setString(4, paramDimension);
            preparedStatement.setString(5, paramDefaultValue);
            preparedStatement.setString(6, paramPosition);
            preparedStatement.setInt(7, paramOrder);
            preparedStatement.executeUpdate();
            
            
            String sqlStringGetId = "SELECT scriptParamId, "
            							+ " scriptId, "
            							+ " paramName, "
            							+ " paramType, "
            							+ " paramDimension, "
            							+ " paramDefaultValue, "
            							+ " paramPosition, "
            							+ " paramOrder "
            							+ " FROM scriptParam WHERE scriptId = ? AND paramName = ?";
    		try (PreparedStatement preparedStatement2 = conn.prepareStatement(sqlStringGetId)) {
    			preparedStatement2.setLong(1, scriptId);
    			preparedStatement2.setString(2, paramName);
    			
    			
                ResultSet rs = preparedStatement2.executeQuery();
                if ( rs.next() ) {
                	script = new ScriptParamDetail(	rs.getInt("scriptParamId"),
													rs.getInt("scriptId"),
													rs.getString("paramName"),
													rs.getString("paramType"),
													rs.getString("paramDimension"),
													rs.getString("paramDefaultValue"),
													rs.getString("paramPosition"),
													rs.getInt("paramOrder")
						         					);


                }
                rs.close();
			} catch (SQLException e)	{
				throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
			} catch (Exception e) {
				throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
			}
            return script;

		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public void scriptParamDelete(	final long scriptParamId,
									final long scriptId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE scriptParam WHERE scriptParamId = ? AND scriptId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			preparedStatement.setLong(1, scriptParamId );
			preparedStatement.setLong(2, scriptId );
            preparedStatement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void scriptParamDelete(	final int scriptParamId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE scriptParam WHERE scriptId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(sqlString)) {
			preparedStatement.setInt(1, scriptParamId );
			preparedStatement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	scriptParamUpdate(	final int scriptParamId,
						final int scriptId, 
						final String paramName, 
						final String paramType,
						final String paramDimension,
						final String paramDefaultValue,
						final String paramPosition,
						final int paramOrder) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "MERGE INTO scriptParam (scriptParamId, scriptId, paramName, paramType, paramDimension, paramDefaultValue, paramPosition, paramOrder) "
						+ "KEY(scriptParamId, scriptId) "
						+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
			PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setInt(1, scriptParamId);
			preparedStatement.setInt(2, scriptId);
			preparedStatement.setString(3, paramName);
			preparedStatement.setString(4, paramType);
			preparedStatement.setString(5, paramDimension);
			preparedStatement.setString(6, paramDefaultValue);
			preparedStatement.setString(7, paramPosition);
			preparedStatement.setInt(8, paramOrder);
			preparedStatement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	/////////////////////////////////////////////////////// UserAccess ////////////////////////////////////
	public int userAccessAdd(	final long userId, 
								final String accessType, 
								final int scriptId) throws Exception {
		int ret;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO userAccess (userId, accessType, scriptId) VALUES(?, ?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, accessType);
            preparedStatement.setInt(3, scriptId);
            ret = preparedStatement.executeUpdate();
            preparedStatement.close();
            
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public void userAccessDelete(final int accessId) throws Exception	{
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE userAccess WHERE accessId = ? ";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, accessId );
            preparedStatement.executeUpdate();
            preparedStatement.close();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	userAccessDeleteToScript(final String scriptName) throws Exception	{
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE userAccess WHERE scriptId = (SELECT scriptId FROM script WHERE scriptName = ?) ";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setString(1, scriptName );
            preparedStatement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public void 
	userAccessDeleteToScript(final int scriptId) throws Exception	{
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE userAccess WHERE scriptId = ? ";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setInt(1, scriptId );
            preparedStatement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public void userAccessDelete(	final long userId, 
									final int scriptId) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "DELETE userAccess WHERE userId = ? AND scriptId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId );
			preparedStatement.setLong(2, scriptId );
            preparedStatement.executeUpdate();
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public List<ScriptAccess> 
	getUserAccessById(final long id) throws Exception {
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT id, userId, scriptId FROM scriptAccess WHERE id = ?" ;
		List<ScriptAccess> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, id);
			ResultSet rs = preparedStatement.executeQuery();
            preparedStatement.close();
            if ( rs.next() ) {
            	ScriptAccess b = new ScriptAccess(	rs.getLong("id"),
            										rs.getLong("userId"),
									                rs.getLong("scriptId")
            									);
            	ret.add(b);
            }
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public List<ScriptAccess> 
	getUserAccess(	final int userId, final int scriptId) throws Exception	{
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT id, userId, scriptId FROM scriptAccess WHERE userId = ? AND scriptId = ?";
		List<ScriptAccess> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			preparedStatement.setLong(2, scriptId);
			ResultSet rs = preparedStatement.executeQuery();
            preparedStatement.close();
            if ( rs.next() ) {
            	ScriptAccess b = new ScriptAccess(	rs.getLong("id"),
													rs.getLong("userId"),
									                rs.getLong("scriptId")
            										);
            	ret.add(b);
            }
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public List<ScriptAccess> 
	getUserAccessByUser(final long userId) throws Exception	{
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT accessId, userId, accessType, scriptId FROM scriptAccess  WHERE userId = ? ";
		List<ScriptAccess> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, userId);
			ResultSet rs = preparedStatement.executeQuery();
            preparedStatement.close();
            while ( rs.next() ) {
            	ScriptAccess b = new ScriptAccess(	rs.getLong("id"),
													rs.getLong("userId"),
									                rs.getLong("scriptId")
													);
            	ret.add(b);
            }
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public List<ScriptAccess> 
	getUserAccessByScript(final long scriptId) throws Exception	{
		Class.forName(JDBC_DRIVER);
		String sqlString = "SELECT accessId, userId, accessType, scriptId FROM scriptAccess  WHERE scriptId = ? ";
		List<ScriptAccess> ret = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, scriptId);
			ResultSet rs = preparedStatement.executeQuery();
            preparedStatement.close();
            while ( rs.next() ) {
            	ScriptAccess b = new ScriptAccess(	rs.getLong("id"),
													rs.getLong("userId"),
									                rs.getLong("scriptId")
													);
            	ret.add(b);
            }
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	//////////////////////////////////////////// NODE Bridges to Scripts
	
	public int
	machineNodeBridgeToScriptAdd(final long nodeId,
								 final long scriptId) throws Exception {
		int ret;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "INSERT INTO machineNodesBridgeToScript (nodeId , scriptId) VALUES(?, ?)";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, nodeId);
			preparedStatement.setLong(2, scriptId);
		    ret = preparedStatement.executeUpdate();
		    preparedStatement.close();
		    return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	public int
	machineNodeBridgeToScriptDelete(final long id) throws Exception {
		int ret;
		Class.forName(JDBC_DRIVER); 
		String sqlString = "DELETE machineNodesBridgeToScript WHERE id = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, id);
            ret = preparedStatement.executeUpdate();
            preparedStatement.close();
            
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public List<MachineNodeToScriptBridge> 
	machineNodesBridgeToScriptByNodeGet(final long nodeId) throws Exception {
	List<MachineNodeToScriptBridge> ret = new ArrayList<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT b.id, "
								+ " b.nodeId, "
								+ " s.scriptName , "
								+ " b.scriptId, "
								+ " '' AS nodeName   "
								+ " FROM machineNodesBridgeToScript b "
								+ " JOIN script s ON s.scriptId  = b.scriptId  "
								+ " WHERE b.nodeId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, nodeId);
			ResultSet rs = preparedStatement.executeQuery();
            
            while ( rs.next() ) {
            	MachineNodeToScriptBridge b = new MachineNodeToScriptBridge(rs.getInt("id"),
																			rs.getInt("nodeId"),
																			rs.getString("scriptName"),
																			rs.getInt("scriptId"),
																			rs.getString("nodeName")
								            								);
            	ret.add(b);
            }
            
            
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	public List<MachineNodeToScriptBridge> 
	machineNodesBridgeToScriptByScriptGet(final long scriptId) throws Exception {
		List<MachineNodeToScriptBridge> ret = new ArrayList<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT b.id, "
								+ " b.nodeId, "
								+ " s.scriptName , "
								+ " b.scriptId, "
								+ " '' as  nodeName   "
								+ " FROM machineNodesBridgeToScript b "
								+ " JOIN script s ON s.scriptId  = b.scriptId  "
								+ " WHERE b.scriptId = ?";
		
		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			preparedStatement.setLong(1, scriptId);
			ResultSet rs = preparedStatement.executeQuery();
            
            while ( rs.next() ) {
            	MachineNodeToScriptBridge b = new MachineNodeToScriptBridge(rs.getInt("id"),
																			rs.getInt("nodeId"),
																			rs.getString("scriptName"),
																			rs.getInt("scriptId"),
																			rs.getString("nodeName")
								            								);
            	ret.add(b);
            }
            
            
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
	
	
	
	public List<MachineNodeToScriptBridge> 
	machineNodesBridgeToScriptGet(final long nodeId, final long scriptId) throws Exception {
		List<MachineNodeToScriptBridge> ret = new ArrayList<>();
		Class.forName(JDBC_DRIVER); 
		String sqlString = "SELECT b.id, "
								+ " b.nodeId, "
								+ " s.scriptName , "
								+ " b.scriptId, "
								+ " '' as nodeName   "
								+ " FROM machineNodesBridgeToScript b "
								+ " JOIN script s ON s.scriptId  = b.scriptId  "
								+ " WHERE b.nodeId = ? AND b.scriptId = ?";

		try (Connection conn = DriverManager.getConnection(DB_URL_DISK, USER, PASS); 
				PreparedStatement preparedStatement = conn.prepareStatement(sqlString))	{
			
			preparedStatement.setLong(1, nodeId);
			preparedStatement.setLong(2, scriptId);
			ResultSet rs = preparedStatement.executeQuery();
            
            while ( rs.next() ) {
            	MachineNodeToScriptBridge b = new MachineNodeToScriptBridge(rs.getInt("id"),
								            								rs.getInt("nodeId"),
								            								rs.getString("scriptName"),
								            								rs.getInt("scriptId"),
								            								rs.getString("nodeName")
								            								);
            	ret.add(b);
            }
            
            
            rs.close();
            return ret;
		} catch (SQLException e)	{
			throw new Exception(AppLogger.logDb(e, Thread.currentThread().getStackTrace()[1]));
		} catch (Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.db));
		}
	}
}

