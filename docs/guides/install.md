Prerequisites
========================================================

* **JDK >= 1.7**: http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html
* **Maven >=  3.0**: http://maven.apache.org/download.cgi
* **Tomcat >=  8.0**: http://tomcat.apache.org/download-80.cgi

Steps
==========

* **Download codes**: git clone https://github.com/oncecloud/dipper.git
* **Build Web**: 
    * cd ${dipper.home}/codes/dipper-web
    * mvn clean install -Dmaven.test.skip=true
* **Configure Web**:
    * mysql -u${username} -p${passowrd} < ${dipper.home}/target/crosscloud/WEB-INF/classes/webside.sql 
    * vi ${dipper.home}/target/crosscloud/WEB-INF/classes/jdbc.properties 
	* modify jdbc.url
        * modify jdbc.username
        * modify jdbc.password (java -cp target/crosscloud/WEB-INF/lib/druid-1.0.14.jar com.alibaba.druid.filter.config.ConfigTools ${original_password}, the output is real_password)
* **Start Tomcat**: 
    * cp -r ${dipper.home}/target/crosscloud ${tomcat.home}/webapps
    * ./${dipper.home}/bin/startup.sh

Verification
============
    * http://192.168.8.106:8080/crosscloud
    * username:admin@webside.com/password:admin123

