# NoWaterServer
## Usage
下载好IDEA（最好是专业版，社区版没测试过）之后，选择File->Open->NoWaterServer目录下的pom.xml文件。然后就慢慢等它构建完，最下面状态栏没有Process在执行之后，Build->Make Project。
装好最新版maven，配好环境变量。
* mvn clean package
* java -jar target/NoWaterServer-1.0-SNAPSHOT.jar

## MySQL
* 执行脚本命令：mysql -u root -p -D NoWater<nowater.sql

