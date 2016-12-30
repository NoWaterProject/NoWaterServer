# ParknShop
## Server

### Dependences

If you want to build the project from source code, you should have **apache-maven-3.3.9**. Then run in the Terminal: **mvn clean package** 

* System
	* CentOS 7.2.1511
* Database
	* mysql-5.7.16
	* redis
* Environment
	* jdk1.8
	* python 2.7.5
	* pip 9.0.1

### Steps
#### mysql-5.7.16

* Download the rpm, such as mysql57-community-release-el7-9.noarch.rpm, then
```
	sudo rpm -ivh mysql57-community-release-el7-9.noarch.rpm
	sudo yum install mysql-server
```
* we can see the temporary password by following command
```
	grep "password" /var/log/mysqld.log
	sudo chown -R root:root /var/lib/mysql
```
* we can login
```
	mysql -u root -p
```
* enter the temporary password, then change the password
```
	set global validate_password_policy=0;
	ALTER USER USER() IDENTIFIED BY 'YourPassword';
```
* After exit, we can init our database


#### redis

```
	wget http://download.redis.io/releases/redis-3.2.5.tar.gz
	tar zxvf redis-3.2.5.tar.gz
	mv redis-3.2.5 redis
	cd redis
	make
```

#### JDK1.8

You should put the run **tar zxvf **, then put the **bin** to $PATH.

#### pip

You should run the command **yum install python-pip**.
Then get the SDK of TencentCloud's Object Store Service.

* pip install qcloud_cos
* pip install python-crontab
* pip install requests

#### Start

Before you start your project, you should edit the **conf/Online.properties**, change the password of database. Then

* bin/start.sh
