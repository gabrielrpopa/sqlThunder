package com.widescope.sqlThunder.config.configRepo;

public class Constants {

    public static String noneString = "";

    /*Execution Type: Repo or Adhoc*/
    public static String adhoc = "ADHOC";
    public static String repo = "REPO";

    /*Execution Type Initial: Repo = R or Adhoc = A*/
    public static String adhocShort = "A";
    public static String repoShort = "R";

    /*Database Type*/
    public static String mongoSource = "MONGO";
    public static String elasticSource = "ELASTIC";
    public static String rdbmsSource = "RDBMS";

    /*Storage folders*/
    public static String mongoFolder = "MONGODB";
    public static String elasticFolder = "ELASTICSEARCH";
    public static String rdbmsFolder = "RDBMS";
    public static String scriptFolder = "SCRIPT";
    public static String fileStorageFolder = "STORAGE";
    public static String exchangeFolder = "EXCHANGE";
    public static String chatFolder = "CHAT";

    /*Output Type*/
    public static String outputTypeResultQuery = "ResultQuery";
    public static String outputTypeCSV = "CSV";
    public static String outputTypeJSON = "JSON";


    /*Output Packaging Type*/
    public static String packagePlain = "plain";
    public static String packageZip = "zip";


    /*Language Paradigm*/
    public static String langMQL = "MQL";
    public static String langSql = "SQL";
    public static String langDsl = "DSL";
    public static String langEql = "EQL";
    public static String langEsql = "ESQL";
    public static String langKql = "KQL";
    public static String langMixed = "MIX";

    /*SQL Sub-paradigm*/
    public static final String RDBMS_DDL = "DDL";
    public static final String RDBMS_DML = "DML";
    public static final String RDBMS_DQL = "DQL";

}
