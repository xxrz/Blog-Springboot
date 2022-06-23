# Blog

## 前端

[参考链接](http://www.1024sky.cn/blog/article/62348)

1. 安装nmp

2. 打开blog-ui的文件夹，运行cmd，输入以下命令

   ```cmd
   npm install --registry=https://registry.npm.taobao.org
   npm run build
   npm run dev
   ```

3. 浏览器输入并访问

   http://localhost:8080



## 后端

### blog-api

redis：启动，打开redis-server.exe

mysql：修改账号密码

运行BlogApp.java



### blog-admin

运行AdminApp.java

访问：http://localhost:8889/pages/main.html

如不想登录需要在pom.xml中，注释

```xml
<!--        <dependency>-->
<!--            <groupId>org.springframework.boot</groupId>-->
<!--            <artifactId>spring-boot-starter-security</artifactId>-->
<!--        </dependency>-->
```

并更新maven

然后注释相关的java代码

即可访问