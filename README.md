
# SQL Thunder :zap:

![SQL Thunder](https://user-images.githubusercontent.com/80181538/227396305-76d3d5ff-febe-4ebc-a040-d59c729c9e54.png)

[![GitHub stars](https://img.shields.io/github/stars/gabrielrpopa/sqlThunder.svg?style=social&label=Stars)](https://github.com/gabrielrpopa/sqlThunder) [![GitHub forks](https://img.shields.io/github/forks/gabrielrpopa/sqlThunder.svg?style=social&label=Forks)](https://github.com/gabrielrpopa/sqlThunder) [![GitHub issues](https://img.shields.io/github/issues/gabrielrpopa/sqlThunder.svg?style=flat&label=Issues)](https://github.com/gabrielrpopa/sqlThunder/issues) [![Version](https://img.shields.io/badge/Version-0.0.1-blue.svg)](https://github.com/gabrielrpopa/sqlThunder/releases/tag/v1.0.0) [![GitHub Follow](https://img.shields.io/github/followers/gabrielrpopa?style=social)](https://github.com/username) [![Twitter Follow](https://img.shields.io/twitter/follow/username.svg?style=social)](https://twitter.com/frankcberardi) [![LinkedIn Connect](https://img.shields.io/badge/LinkedIn-Connect-blue.svg?logo=linkedin)](https://ca.linkedin.com/in/gabriel-r-popa-4744451?trk=profile-badge) [![Website](https://img.shields.io/badge/Visit-Website-green)](https://sqlthunder.ca/)

[SQL Thunder](https://sqlthunder.ca/) is a web-based interactive SQL and scripting platform used to build, scale and manage distributed data applications.

Integrate SQL queries, embedded databases, MongoDB collections, Elasticsearch indexes and Python scripts for distributed data ingestion, storage, processing, warehousing and governance. 

- Migrate data to/from MongoDB collections, Elasticsearch indexes and RDBMS tables (Oracle, Microsoft SQL Server, PostgreSQL, Embedded databases).
- Ingest CSV files into RDBMS tables, Elasticsearch indexes, MongoDB collections, in-memory SQL databases or cache.
- Distributed parameterized query and scripting execution using multiple data sources and languages.
- Generate reports through push notifications to web interface or client applications from running scripts (Python, PowerShell, etc).
- Send and receive files between SQL Thunder instances.
- Easy to use web interface. 

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Installation

1. Download [Documentation](http://209.15.130.226/assets/downloads/QuickStart.docx) 
2. Download [Java binary middle tier](http://209.15.130.226/assets/downloads/sqlThunder.war) 
3. Download [Web Application](http://209.15.130.226/assets/downloads/sql-thunder.zip)
4. Download [Python API](http://209.15.130.226/assets/downloads/sqlThunderRestClientLib-1.0.0-py3-none-any.whl)

#### How to install and run SQL Thunders Java API (requires Java 11+)
=======
Experiment by logging in using your Google account at https://sqlthunder.ca

Instructions on how to install and run.

- War file contains a built-in Apache Tomcat server (9.0.64), run from command line using: 

    - `java -jar sqlThunder.war com.widescope.sqlThunder`.

- Deploy to Apache Tomcat: https://tomcat.apache.org/tomcat-9.0-doc/deployer-howto.html.

- API contains locally installed preset configurations (localhost where API is running) Postgres database, MongoDB and Elasticsearch.
=======
Also, watch training videos here:
![image](https://user-images.githubusercontent.com/80181538/227396305-76d3d5ff-febe-4ebc-a040-d59c729c9e54.png)

- Postgres database instance assumes installation credentials are: 
    - username=`postgres`
    - password=`postgres`

- The installed MongoDB and Elasticsearch instances are standard installation without security.

- The presets can be changed or deleted from their corresponding management screens in the SQL Thunder web application.

- Change `hostMachine` with machine name or IP address of host environment.

- Using swagger: https://hostMachine:9094/sqlThunder/swagger-ui.html.

- Using API documentation: https://hostMachine:9094/v2/api-docs.

- `hostMachine` can be replaced with host name, IP address when using it in a network of multiple users or simply use localhost for local use only.

- The API pushes notifications via websocks on https://hostMachine:9094:7071.

- The API can be also used with a custom UI through Swagger API.

#### How to install SQL Thunder's web application
- Unzip web application.

- Download and install http Apache Tomcat server (XAMPP).

- Copy `sql-thunder` folder into `htdocs` folder of the XAMPP install folder.

- In folder assets open `app-config.json` where you have to update baseUrl and webSocketsUrl with your machine name.

- Now you are ready to go. Go to your browser and type the url of your installation. If it is just on your local network https://hostMachine:80 will do it or local access only https://localhost:80.

- the build in initial user is super@yourdomain.com and the password is changeme. Pls create your own account and delete this one.

- User passwords are stored as hash strings for minimal security purposes.

- Passwords are also encrypted when sent from the front end to the back end just in case your site is not secured by a certificate.

- DB passwords and other connecting server passwords are also encrypted in the database for your protection.

- Anything else, such as payloads and headers are not encrypted, so you better use a certicate if you are concerned with the security of your data.

- This is not an open source application in order to provide bare minimal security to users.

#### How to install Sql Thunder Python API
- Use the following command once you have Python 3 installed :
`python -m pip install --force-reinstall sqlThunderRestClientLib-1.0.0-py3-none-any.whl`.

- An example of python script generating a report streamed in realtime to the UI. Note: Sql Thunder will capture stdin/stderr and streams to the api.

- Please look at the screenshots in QuickStart for details of simply just fiddle with it.

```python
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
```

## Usage

Elasticsearch to In-memory Database
<video controls width="500">
    <source src="https://sqlthunder.ca/assets/video/SqlThunderElasticsearchToInMemSqlDb.mp4?raw=true" type="video/mp4">
</video>

MongoDB To In-memory Database
<video controls width="500">
    <source src="https://sqlthunder.ca/assets/video/SqlThunderMongoToInMemSqlDb.mp4?raw=true" 
    type="video/mp4">
</video>

Relational Database Management System To In-memory Database
<video controls width="500">
    <source src="https://sqlthunder.ca/assets/video/SqlThunderRdbmsToInMemSqlDb.mp4?raw=true" 
    type="video/mp4">
</video>

Export Elasticsearch to MongoDB
<video controls width="500">
    <source src="https://sqlthunder.ca/assets/video/SqlThunderElasticsearchExportToMongoDb.mp4?raw=true" type="video/mp4">
</video>

Export Relational Database Management System to MongoDB
<video controls width="500">
    <source src="https://sqlthunder.ca/assets/video/SqlThunderPostgresExportToMongoDb.mp4?raw=true
" type="video/mp4">
</video>

## Contribution

Welcome to the SQL Thunder's community, below are the guidelines to help you get started:

Please review our [Code of Conduct](https://github.com/gabrielrpopa/sqlThunder/blob/main/CODE_OF_CONDUCT.md).

#### Reporting Issues

If you encounter bugs, have feature requests, or want to suggest improvements, please open an [issue](https://github.com/gabrielrpopa/sqlThunder/issues).

#### Pull Requests

Contributions through pull requests are highly encouraged. Here's how you can contribute code:

1. **Fork the Repository**: Click the "Fork" button at the top right of the [SQL Thunder's repository](https://github.com/gabrielrpopa/sqlThunder) to create your copy.

2. **Clone Your Fork**: Clone your forked repository to your local machine using `git clone`.

3. **Create a Branch**: Create a new branch for your changes, such as `feature/my-feature` or `fix/issue-123`.

4. **Make Changes**: Implement your desired changes and improvements. Ensure your code adheres to the project's coding standards.

5. **Test Your Changes**: If applicable, write tests and make sure existing tests pass.

6. **Commit Your Changes**: Commit your changes with clear and concise messages.

7. **Push Your Changes**: Push your branch to your forked repository on GitHub.

8. **Open a Pull Request**: Go to the original [SQL Thunder's repository](https://github.com/gabrielrpopa/sqlThunder) and open a pull request from your branch. Provide a descriptive title and details about your changes.

9. **Review and Discussion**: Engage in the discussion on your pull request. Address any feedback or questions from maintainers and other contributors.

10. **Merge**: Once your pull request is approved and any required checks pass, it will be merged into the main project. Congratulations on your contribution!

### Contributor License Agreement

By contributing to SQL Thunder, you agree to abide by the project's [Contributor License Agreement](CONTRIBUTOR_LICENSE_AGREEMENT.md).

## License

MIT License

Copyright (c) 2023 Gabriel R. Popa

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.