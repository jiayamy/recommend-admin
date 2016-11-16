1）
redis服务器：
SHYQ-PS-MV-SV03-hcfw-01	缓存服务器-01	172.16.70.117	　	缓存服务器-01	version/v#wdtke!33	Wd3Wd@2010

内网VIP:172.16.20.140 80 					对应内网80
SHYQ-PS-MV-SV03-jkfw-01	接口服务器-01	172.16.70.116	　	接口服务器-01	version/v#wdtke!33	Wd3Wd@2010
SHYQ-PS-MV-SV03-jkfw-02	接口服务器-01	172.16.70.119	　	接口服务器-01	version/v#wdtke!33	Wd3Wd@2010
SHYQ-PS-MV-SV03-jkfw-03	接口服务器-01	172.16.70.120	　	接口服务器-01	version/v#wdtke!33	Wd3Wd@2010
SHYQ-PS-MV-SV03-jkfw-04	接口服务器-01	172.16.70.121	　	接口服务器-01	version/v#wdtke!33	Wd3Wd@2010

内网VIP:172.16.20.142 8080 	外网IP:221.181.100.26 	对应内网8080 
SHYQ-PS-MV-SV03-webgl-01	WEB管理服务器-01		172.16.70.143	version/v#wdtke!33	Wd3Wd@2010
SHYQ-PS-MV-SV03-webgl-02	WEB管理服务器-02		172.16.70.144

version/!QASW@#ED        root/3edc#EDC或root/6yhn^YHN

生产环境MongoDB
	地址：10.200.21.129:27017, 10.200.21.57:27017, 10.200.21.66:27017
	数据库名称：user_label
	用户名：label_read
	密码：label_read[migu]
生成环境暂时使用mongodb：（其他同上）
	10.200.15.52:27017
 
 2）上线操作步骤
 cd /nas/nas_log/app_bak/rcmd/
 rz -be
 cd /usr/local/tomcat/webapps/
 cp /nas/nas_log/app_bak/rcmd/recommend_zl201611071700* ./
 tar -zcvf recommend_bak20161026.tar.gz ./recommend
 
 unzip recommend_zl201611071700.zip
 cd ..
 ps axu | grep httpd
 /apache2/bin/apachectl stop
 
 ps axu | grep java
 pkill java
 
 bin/startup.sh
 tail -f /logs/recommend-admin.log
 ctrl+c
 /apache2/bin/apachectl start
 3)上线失败后操作
 进入webapps目录
 cd /usr/local/tomcat/webapps/
 tar -zxvf recommend_bak20161013.tar.gz
 cd ..
 /apache2/bin/apachectl stop
 pkill java
 bin/startup.sh
 tail -f /logs/recommend-admin.log
 ctrl+c
 /apache2/bin/apachectl start
 
 --20161108
 推荐系统按照标签的权重来确定 每个标签都要设置一个权重，搜索出来以后按照权重*标签的分数
 
 