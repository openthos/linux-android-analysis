##AppStore服务端的实现

**一，服务端环境**

* 容器：Tomcat
* 语言：Servlet(Java)
* IDE : Eclipse
* 数据库：MySQL

**二，实现功能**

* [x] 排行功能(通过点击排行tab，按下载量进行排行)
* [x] 游戏分类功能(通过点击相应的分类实现游戏App的分类)
* [x] 所有App分类功能(通过点击相应的分类实现所有App的分类)
* [x] 断点续传功能(用户可以通过点击，下载，暂停，继续下载)
* [ ]  分页功能
* [ ]  搜索功能
* [ ]  举报功能
* [ ]  下拉刷新
* [x]  评论功能 
* [x]  响应ota请求下载
* [x]  响应ota版本检测

` 注：其他功能根据需要待添加 `

**三，数据库表的设计**

由于游戏App的分类较多，我们把所有App分为了两类，一类是游戏App，另一类是其他。对应的表：game和software

在客户端还有一个特别的Activity用于实现系统的ota，表名：updatesystem

**software表**

| Field               | Type         | Null | Key | Default | Extra | 含义    |
|---------------------|--------------|------|-----|---------|-------|---------|
| id                  | int(11)      | NO   | PRI | NULL    |       |软件编号|
| soft_name           | varchar(100) | YES  |     | NULL    |       |软件名称|
| dev_name            | varchar(100) | YES  |     | NULL    |       |开发者名称|
| dev_id              | varchar(100) | YES  |     | NULL    |       |开发者id|
| update_time         | datetime     | YES  |     | NULL    |       |更新时间|
| soft_language       | varchar(100) | YES  |     | NULL    |       |软件语言|
| soft_version        | varchar(100) | YES  |     | NULL    |       |软件版本|
| soft_download_count | int(11)      | YES  |     | NULL    |       |下载次数|
| introduce           | varchar(100) | YES  |     | NULL    |       |软件介绍|
| soft_size           | int(11)      | YES  |     | NULL    |       |软件大小|
| soft_classify       | int(11)      | YES  |     | NULL    |       |软件类别|
| allow               | int(11)      | YES  |     | NULL    |       |是否允许|

```
建表语句

 CREATE TABLE `software` (
  `id` int(11) NOT NULL,
  `soft_name` varchar(100) DEFAULT NULL,
  `dev_name` varchar(100) DEFAULT NULL,
  `dev_id` varchar(100) DEFAULT NULL,
  `update_time` datetime  DEFAULT NULL,
  `soft_language` varchar(100) DEFAULT NULL,
  `soft_version` varchar(100) DEFAULT NULL,
  `soft_download_count` int(11) DEFAULT NULL,
  `introduce` varchar(100) DEFAULT NULL,
  `soft_size` int(11) DEFAULT NULL,
  `soft_classify` int(11) DEFAULT NULL,
  `allow` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
```
**game表**

| Field               | Type         | Null | Key | Default | Extra |含义    |
|---------------------|--------------|------|-----|---------|-------|--------|
| id                  | int(11)      | NO   | PRI | NULL    |       |软件编号|
| game_name           | varchar(100) | YES  |     | NULL    |       |软件名称|
| dev_name            | varchar(100) | YES  |     | NULL    |       |开发者名称|
| dev_id              | varchar(100) | YES  |     | NULL    |       |开发者编号|
| update_time         | datetime     | YES  |     | NULL    |       |更新时间|
| soft_language       | varchar(100) | YES  |     | NULL    |       |软件语言|
| soft_version        | varchar(100) | YES  |     | NULL    |       |软件版本|
| soft_download_count | int(11)      | YES  |     | NULL    |       |下载次数|
| introduce           | varchar(100) | YES  |     | NULL    |       |软件介绍|
| size                | int(11)      | YES  |     | NULL    |       |软件大小|
| game_classify       | int(11)      | YES  |     | NULL    |       |软件分类|
| allow               | int(11)      | YES  |     | NULL    |       |是否允许|

```
建表语句

CREATE TABLE `game` (
  `id` int(11) NOT NULL,
  `game_name` varchar(100) DEFAULT NULL,
  `dev_name` varchar(100) DEFAULT NULL,
  `dev_id` varchar(100) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `soft_language` varchar(100) DEFAULT NULL,
  `soft_version` varchar(100) DEFAULT NULL,
  `soft_download_count` int(11) DEFAULT NULL,
  `introduce` varchar(100) DEFAULT NULL,
  `size` int(11) DEFAULT NULL,
  `game_classify` int(11) DEFAULT NULL,
  `allow` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

```
**updatesystem表**

| Field               | Type         | Null | Key | Default | Extra          |含义|
|---------------------|--------------|------|-----|---------|----------------|----|
| id                  | int(11)      | NO   | PRI | NULL    | auto_increment |版本编号|
| documentVersion     | varchar(200) | NO   |     | NULL    |                |版本号|
| documentPreVersion  | varchar(200) | YES  |     | NULL    |                |上一版本号|
| documentNextVersion | varchar(200) | YES  |     | NULL    |                |下一版本号|
| commitTime          | datetime     | NO   |     | NULL    |                |提交时间|
| submitter           | varchar(200) | NO   |     | NULL    |                |提交者|
| md5                 | varchar(255) | NO   |     | NULL    |                |版本MD5|

```
建表语句

CREATE TABLE `updatesystem` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `documentVersion` varchar(200) NOT NULL,
  `documentPreVersion` varchar(200) DEFAULT NULL,
  `documentNextVersion` varchar(200) DEFAULT NULL,
  `commitTime` datetime NOT NULL,
  `submitter` varchar(200) NOT NULL,
  `md5` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

```

`注：其他的表根据功能设计`

**四，代码结构**

![CodeStruct](https://github.com/openthos/customized-android-analysis/blob/master/AppStoreServer/pic/code1.png?raw=true)

![CodeStruct](https://github.com/openthos/customized-android-analysis/blob/master/AppStoreServer/pic/code2.png?raw=true)

```
代码分为四个部分：

servlet部分主要是controller用于响应请求，

service部分业务逻辑处理，

interface部分主要是对数据库功能的抽象，

utils部分是一些辅助类

```
**五，代码详解**

**1.数据库部分**

**DataBase.java**：数据库操作接口，包含对数据库CURD的基本操作
```
public interface DataBase {

	public Connection getConn();

	public boolean InsertDML(String sql);
	public boolean DeleDML(String sql);
	public boolean UpdateDML(String sql);
	public ResultSet QueryDML(String sql);
	public void CloseAll();
}
```
**DataBaseService.java：** 对DataBase接口的CURD进行实现

**AppStoreController.java:** 这个servlet类主要是响应appstore客户端的页面展示数据的请求。

**CheckUpdateController.java:** 响应版本检查的controller

**CommentController.java:** 响应评论功能的controller，包括查询，添加，删除评论等

**DownloadApkController.java:** 响应app下载的请求

**DownloadCountCul.java:** 响应app下载次数的请求

**UpdateSystemController.java:** 响应升级包的下载请求

**CheckUpdateService.java:** 响应版本检测controller的业务处理类

**CommentService.java:**  响应评论controller的业务处理类

**DownloadApkService.java:** 响应app下载的业务处理类

**DownloadUpdataSystemISOService.java:** 响应升级包下载的业务处理类

**OperateService.java:** 核心类，响应appstore客户端数据加载的业务处理类

**GetDBProperties.java:** 辅助性的类，用于获取数据库配置信息

**DownloadFileUtil.java:** 辅助性类，用于下载文件的类

**HttpsUtil.java:** https和http的请求的辅助类

**MD5.java:** 升级包md5校验类


**六，URL详情**

URL Base：https：//dev.openthos.org/appstore/AppStoreServer

|子URL|参数名称|参数值|返回数据|
|--------|--------|--------|--------------|
|App_store|operate,classify|paihang，software，game|JSON格式数据：[{operate:"paihang_software"},{id:4,soft_name:"app4",dev_name:"app6",dev_id:"app6",update_time:"app6",soft_language:"app6",soft_version:"app6",soft_download_count:81,introduce:"app6",soft_size:10},{id:8,soft_name:"app8",dev_name:"app6",dev_id:"app6",update_time:"app6",soft_language:"app6",soft_version:"app6",soft_download_count:48,introduce:"app6",soft_size:10}]|
|download|path|Software/10/10.apk|文件流|
|count|id&classify|id=10&classify=Software|无返回(内部处理即可)|
|iso|path|UpdateSystem|201607023.iso|文件流|
|check|无|无|最新系统版本号|
|comment|type,aid|add,query|JSON格式数据：[{id:2,aid:"10",content:"哈哈",username:"null",time:"2016-07-14"},{id:6,aid:"10",content:"骨头",username:"null",time:"2016-07-14"},{id:7,aid:"10",content:"",username:"null",time:"2016-07-14"}]|

**七，运行大致流程**

首先我们使用的是Tomcat容器，客户端发送请求到tomcat，tomcat通过配置文件找到相应的Servlet，

通过这个Controller我们对请求做处理，

调用相应的Service对请求做具体的处理。如果用到数据库则进行对数据库的操作。之后进行返回。

**八，注意事项**

* 没有完成的基本功能需要添加，其他功能根据需要进行添加
* 有些地方的设计是不合理的需要优化修改，比如表的设计，代码的编写
* 其实一个AppStore是一个很大的工程需要考虑的东西很多，目前只是初级阶段，以后的问题还会很多很多。
* 源码已上传



