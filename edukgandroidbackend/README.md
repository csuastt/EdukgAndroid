<center><font size=5>EduKG Android App 后端项目</font></center>

 - 启动请运行 startup.bat 或 startup.sh ，**务必等待服务器启动完成再发送请求**，否则有可能出 bug 。
 - 关闭请运行 shutdown.bat 或 shutdown.sh ，请勿使用其他方式关闭服务器，否则有可能出 bug 。
 - 运行前请将数据（9 个 csv 文件）放在 tomcat/bin 目录下。

可用如下请求测试网站是否正常运行：

http://localhost:8080/api/login?id=&name=lfw&password=123&nickname=roufaen&old_password=123&new_password=456

http://localhost:8080/api/history?id=&type=his_type&course=math&context=his_context

http://localhost:8080/api/favorites?id=&type=fav_type&course=math&context=fav_context

http://localhost:8080/api/add_history?id=&type=entity&course=math&context=cont

http://localhost:8080/api/add_time?id=&time=1234&date=2021-08-07

http://localhost:8080/api/add_question?id=&course=math&entity=cont&correctness=no

http://localhost:8080/api/recommendation?id=
