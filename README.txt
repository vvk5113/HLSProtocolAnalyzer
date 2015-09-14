HPA Project - using Rest (Jersey), Maven

I n t r o d u c t i o n
=============================================================================================
pending...

O b j e c t i v e
=============================================================================================
pending...

P r e - r e q u i s i t e s - JDK1.6, Maven 3.x
============================================================================================
	1. Install a Java 6.0 compliant JDK.  Create an environment variable called JAVA_HOME
	pointing to the directory where the JDK was installed (for example,
	C:\Program Files\Java\jdk1.6.28).
	
	2. Install Maven 3.0.3.  Create an environment variable called M2_HOME, pointing to the
	directory where Maven was installed (for example, C:\apache-maven-3.0.3).  Also
	add M2_HOME to the PATH environment variable (e.g. export PATH=$M2_HOME:$PATH on UNIX
	and set PATH=%M2_HOME%;%PATH% on Windows.
	3. Install parent-pom  (http://pucm01/repos/pml/lnb/foundation/maven)
	

Maven Build
============================================================================================	
	1. mvn clean package -Denv=local/dev/prod/mo
	   Note: If env is not passed, 'local' will be used as default value. env is used for generating war artifact based on 
	   environment specific properties (@see src/main/filters for environment specific properties)	
	2. Deploy hpa.war into web container (example: tomcat7)
	3. Ping http://localhost:<port>/hpa
	

Project Structure	
============================================================================================	
hpa
|------src/main/java (java code)
		|--com/psu/hpa/Sample.java (sample class)
		|--com/psu/hpa/MyResource.java (Restful sample resource class)
|------src/main/resources (all environment specific configurations)
		|---log4j.xml (sample file and write log into /eip/hpa/logs/hpa.log, please use filters files to change this path)
|------src/main/filters (prop filters which are used to filter resources at build time)
		|---local.properties (all local environment properties)
		|---dev.properties (all dev environment properties)
		|---mo.properties (all model-office environment properties)
		|---prod.properties (all production environment properties)
|------src/test/java (unit test code)
		|--com/psu/hpa/SampleTest.java (sample unit test class)
|------src/test/resources (unit test configurations)
|------src/main/webapp/css (css files, please feel free to store other script files  if required)
		|-------jquery.mobile-1.0.1.css
		|-------jquery.mobile-1.0.1.min.css
|------src/main/webapp/scripts (jquery and jquery-mobile javascript files, please feel free to store other script files if required)
		|-------jquery.mobile-1.0.1.js
		|-------jquery.mobile-1.0.1.min.js
		|-------jquery-1.7.1.js
|------src/main/webapp/WEB-INF/web.xml (deployment descriptor)
|------src/main/webapp/index.jsp (sample index.jsp)
|------pom.xml (maven POM)
