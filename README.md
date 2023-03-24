# sqlThunder

An Integrated Data Warehouse System with SQL and Scripting Repository

Data migration from/to Mongodb collections, Elasticsearch indexes, rdbms tables (Oracle, Sqlserver, Postgres, Embedded DBs), via SQL statements
CSV ingestion (no schema required), into any rdbms tables, Elasticsearch index, Mongodb collection, or in-mem Sql DBs
Exporting any rdbms, Elasticsearch, Mongodb dataset or CSV to in-mem SQL DBs for instant query in SQL language.
Inter-team, inter-company collaboration by sending/receiving files (embedded dbs or CSV) to/from SqlThunder instances
Distributed scripting execution
Distributed SQL execution by mixing and matching various data sources
Easy to use web user interface

Download from http://infinite-loop.ca/

![image](https://user-images.githubusercontent.com/80181538/227396130-3b945f80-5f61-4af2-9baa-590bf37e5ad1.png)


Quick notes to install Sql Thunder. First download the api and web application from the above
How to install and run the java API. It requires Java 11+
As the war file has a built-in Apache Tomcat/9.0.64 server, it can run from command line with: java -jar sqlThunder.war com.widescope.sqlThunder
Deploy it to Tomcat, for that, pls follow https://tomcat.apache.org/tomcat-9.0-doc/deployer-howto.html#Deploying_on_a_running_Tomcat_server
You can also easily use XAMPP for this purpose.
Note that the api, comes with presets for locally installed (localhost, where API is running) Postgres db, Mongo db and Elasticsearch
Postgresdb instance assumes your installation credentials are username='postgres' and password='postgres'
The installed Mongodb and Elasticsearch instances are standard installation without security
The presets can be changed or deleted all together from their coresponding management screens in the Sql Thunder UI
How to use the API (change accordingly hostMachine with either machine name or ip address of the host to reflect your environment )
using swagger: http(s)://hostMachine:9094/sqlThunder/swagger-ui.html
using api doc: http(s)://hostMachine:9094/v2/api-docs
hostMachine, can be replaced with host name, ip address when using it in a network of multiple users or simply use localhost for local use only
Please also be aware that the API pushes notifications via websocks on http(s)://hostMachine:9094:7071/
The API can be also used with a custom UI, before that knowledge about all end points signatures (headers and body payloads) is required
This can be aquired by looking at Swagger API
How to use the API (change accordingly hostMachine with either machine name or ip address of the host to reflect your environment )
using swagger: http(s)://hostMachine:9094/sqlThunder/swagger-ui.html
using api doc: http(s)://hostMachine:9094/v2/api-docs
Please also be aware that the API pushes notifications via websocks on http(s)://hostMachine:9094:7071/
How to install the front end application
unzip the file
download and install Http Apache server via your prefered distribution. You can easily use XAMPP for this purpose. You can also use any other HTTP server available
copy the content found in the sql-thunder folder into htdocs folder of the XAMPP install folder
Go into folder assets and open app-config.json where you have to update baseUrl and webSocketsUrl with your machine name
Now you are ready to go. Go to your browser and type the url of your installation. If it is just on your local network http(s)://hostMachine:80 will do it or local access only http(s)://localhost:80
the build in initial user is super@yourdomain.com and the password is changeme. Pls create your own account and delete this one
User passwords are stored as hash strings for minimal security purposes
Passwords are also encrypted when sent from the front end to the back end just in case your site is not secured by a certificate
DB passwords and other connecting server passwords are also encrypted in the database for your protection
Anything else, such as payloads and headers are not encrypted, so you better use a certicate if you are concerned with the security of your data
This is not an open source application in order to provide bare minimal security to users
How to install Sql Thunder Python API
Use the following command once you have Python 3 installed :
python -m pip install --force-reinstall sqlThunderRestClientLib-1.0.0-py3-none-any.whl
An example of python script generating a report streamed in realtime to the UI. Note: Sql Thunder will capture stdin/stderr and streams to the api
Please look at the screenshots in QuickStart for details of simply just fiddle with it
user = '@user@'
password = '@session@'
session = '@session@'
request='@request@'
from sqlThunderRestClientLib import sqlThunderRestClient as c
metadata = [c.ColumnDefinition("Name", "STRING", 100, 0), c.ColumnDefinition("Age", "INTEGER", 0, 0)]
tableDefinition = c.TableDefinition( "", "", "", metadata )
response = c.loopbackScriptDataHeader(user, session, request, tableDefinition)
print("loopbackScriptDataHeader response: ", response )
rowValue = c.RowValue( [c.CellValue("Name", "John Doe"), c.CellValue("Age", 44)])
response = c.loopbackScriptDataDetail(user, session, request, rowValue)
print("loopbackScriptDataDetail response: ", response )
rowValue = c.RowValue( [c.CellValue("Name", "Total"), c.CellValue("Age", 44)])
response = c.loopbackScriptDataFooter(user, session, request, rowValue)
print("loopbackScriptDataFooter response: ", response )



Also watch traing videos here:
![image](https://user-images.githubusercontent.com/80181538/227396305-76d3d5ff-febe-4ebc-a040-d59c729c9e54.png)


