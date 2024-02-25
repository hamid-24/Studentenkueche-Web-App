### Website is running on https://uncertain.fb3pool.hs-flensburg.de/
### For using the API endpoints, we recommend [Postman](https://www.postman.com/downloads/)

### Endpoints
- `https://uncertain.fb3pool.hs-flensburg.de`
  - `/apilogin (Method: POST; Body: username, password)`
  - `/api/recipes/{userId} (Method: GET)`
  - `/api/recipes/{id} (Method: DELETE)`
  - `/api/recipes/{id} (Method: PUT)`

# To Run this Spring Boot App locally:
1. ### Preferably use Intellij IDEA since it supports all Spring Boot dependencies
2. ### Install JDK 21
    ```
    sudo apt update
    sudo apt install openjdk-21
    ```
3. ### Configure MariaDB:
   - Install MariaDB
     ```
     sudo apt update
     sudo apt install mariadb-server
     ```
   - Set up a root user with the following command. This will take you through a series of prompts.\
     Please read them carefully. If you already have a root user, please change the root password to none (no password). \
     You can safely answer 'n' to all other questions.
     ```
     sudo mysql_secure_installation
     ```
   - Login with root user and access the MySQL Shell
     ```
     sudo mysql -u root
     ```
   - In the MySQL Shell, create the database cuse2ha
     ```
     create database cuse2ha;
     exit;
     ```
   - Make sure the MariaDB service is running:
     ```
     sudo systemctl start mariadb
     ```
   - If there is a conflict concerning the port number, please stop the current service running on port 3306, \
     which MariaDB uses by default
     ```
     sudo kill $(sudo lsof -t -i:3306)
     ```
     this command kills the process currently running on port 3306. \
     After that, use the last command again to start the MariaDB service.
4. ### You are done, now start the app from your IDE