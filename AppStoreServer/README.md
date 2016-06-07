##AppStore服务端的实现

**一，服务端环境**

* 容器：Tomcat
* 语言：Servlet(Java)
* IDE : JavaEE
* 数据库：MySQL

**二，实现功能**

* [x] 排行功能(通过点击排行tab，按下载量进行排行)
* [x] 游戏分类功能(通过点击相应的分类实现游戏App的分类)
* [x] 所有App分类功能(通过点击相应的分类实现所有App的分类)
* [x] 断点续传功能(用户可以通过点击，下载，暂停，继续下载)
* [ ]  分页功能
* [ ]  搜索功能
* [ ]  举报功能
* [ ]  评论功能 

` 注：其他功能根据需要待添加 `

**三，数据库表的设计**

由于游戏App的分类较多，我们把所有App分为了两类，一类是游戏App，另一类是其他。对应的表：game和software

**software表**

| Field               | Type         | Null | Key | Default | Extra |
|---------------------|--------------|------|-----|---------|-------|
| id                  | int(11)      | NO   | PRI | NULL    |       |
| soft_name           | varchar(100) | YES  |     | NULL    |       |
| dev_name            | varchar(100) | YES  |     | NULL    |       |
| dev_id              | varchar(100) | YES  |     | NULL    |       |
| update_time         | varchar(100) | YES  |     | NULL    |       |
| soft_language       | varchar(100) | YES  |     | NULL    |       |
| soft_version        | varchar(100) | YES  |     | NULL    |       |
| soft_download_count | int(11)      | YES  |     | NULL    |       |
| introduce           | varchar(100) | YES  |     | NULL    |       |
| soft_size           | int(11)      | YES  |     | NULL    |       |
| soft_classify       | int(11)      | YES  |     | NULL    |       |
| allow               | int(11)      | YES  |     | NULL    |       |


**game表**

| Field               | Type         | Null | Key | Default | Extra |
|---------------------|--------------|------|-----|---------|-------|
| id                  | int(11)      | NO   | PRI | NULL    |       |
| game_name           | varchar(100) | YES  |     | NULL    |       |
| dev_name            | varchar(100) | YES  |     | NULL    |       |
| dev_id              | varchar(100) | YES  |     | NULL    |       |
| update_time         | varchar(100) | YES  |     | NULL    |       |
| soft_language       | varchar(100) | YES  |     | NULL    |       |
| soft_version        | varchar(100) | YES  |     | NULL    |       |
| soft_download_count | int(11)      | YES  |     | NULL    |       |
| introduce           | varchar(100) | YES  |     | NULL    |       |
| size                | int(11)      | YES  |     | NULL    |       |
| game_classify       | int(11)      | YES  |     | NULL    |       |
| allow               | int(11)      | YES  |     | NULL    |       |


`注：其他的表根据功能设计`

**四，代码结构**

![CodeStruct](https://github.com/Entiy/AppStore/blob/master/pic/CodeStruct.png)

**五，运行大致流程**

首先我们使用的是Tomcat容器，客户端发送请求到tomcat，tomcat通过配置文件找到相应的Servlet，

通过这个Controller我们对请求做处理，

调用相应的Service对请求做具体的处理。如果用到数据库则进行对数据库的操作。之后进行返回。

**六，注意事项**

* 没有完成的基本功能需要添加，其他功能根据需要进行添加
* 有些地方的设计是不合理的需要优化修改，比如表的设计，代码的编写
* 其实一个AppStore是一个很大的工程需要考虑的东西很多，目前只是初级阶段，以后的问题还会很多很多。
* 源码已上传



