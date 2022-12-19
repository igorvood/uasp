# Сборка проектов



Для сборки проектов необходимо подключить возможность загрузки зависимостей из Nexus. Ниже представлен пример подключения на примере репозитория https://nexus-ci.corp.dev.vtb/ и зависимости uasp-streaming-common

1. Создаем мастер-пароль

   ```
   mvn --encrypt-master-password <любой пароль>
   {jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}
   ```

   

2. Создаём файл и копируем мастер пароль в файл ${user.home}/.m2/settings-security.xml

   ```
   <settingsSecurity>
       <master>{jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}</master>
   </settingsSecurity> 
   ```

3. Шифруем пароль от УЗ vtbXXXXXXX 

   ```
   mvn --encrypt-password <пароль_от_vtbXXXXXXX>
   {COQLCE6DU6GtcS5P=}
   ```

   и добавляем его в файл ${user.home}/.m2/settings.xml

   ```
   <settings xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/SETTINGS/1.0.0"
         xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                             http://maven.apache.org/xsd/settings-1.0.0.xsd">
     <servers>
       <server>
           <id>drpim-maven</id>
           <username>vtbXXXXXXX</username>
           <password>{COQLCE6DU6GtcS5P=}</password>
       </server>
     </servers>
   </settings>
   ```

   

4. Добавляем репозиторий в pom.xml

   ```
   <repositories>
     <repository>
       <id>drpim-maven</id>
       <name>drpim-maven</name>
       <url>https://nexus-ci.corp.dev.vtb/repository/drpim-maven-lib/</url>
     </repository>
   </repositories>
   ```

   

5. Корректируем и/или добавляем dependency

   ```
   <dependency>
     <groupId>ru.vtb.uasp.common</groupId>
     <artifactId>uasp-streaming-common</artifactId>
     <version>1.0.c369f6a</version>
   </dependency>
   ```

   

6. Если необходимо, отключаем maven-install-plugin

7. Пробуем собирать, указав ключ maven.wagon.http.ssl.insecure

   ```
   mvn -Dmaven.wagon.http.ssl.insecure=true clean package
   ```

   

8. Добавляем ключи для работы maven в IntelliJ IDEA (Build->Build Tools->Maven->Runner→VM Options): 

   -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true

   MAVEN_OPTS=-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true

   ![](D:\projects\innotech\uasp\streaming-mvp\uasp-streaming-common\docs\images\build\idea-build-maven-runner.png)

   

9. Ставим галочку Tools - Server Certificaties

   ![](D:\projects\innotech\uasp\streaming-mvp\uasp-streaming-common\docs\images\build\idea-tools-server-cerificates.png)

   





