# 博客项目

## 关于项目
```text
项目名称：个人博客系统（2020.7.20-8.10）

项目简介：个人博客项目，比较完整的博客系统，具备了常见的博客相关功能，初衷是为了记录学习中的所得，方便与他人交流。

技术架构：Spring+SpringMVC+SpringBoot+Hibernate+MySQL

开发环境及工具：JDK14、IDEA2020、Edge、PowerDeginer、Navicat、Maven、Git等。

模块实现：
    管理员登录：采用拦截器，在未登录时拦截请求到登录页面，采用MD5加密，验证管理员登录。
    博客管理模块：针对已发布和未发布的博客，进行编辑、删除、查询等操作，同时可以指定博客的分类，是否开启评论、转载声明等。
    评论模块：实现了博客详情下的评论功能，支持关键字屏蔽和博主评论加强显示。
    搜索模块：针对博客的标题和内容实现了搜索功能。
    分类模块：针对博客进行了分类处理，支持按分类查找博客，显示该分类博客总数。

技术要点：项目采用SpringBoot框架，使用Spring支持的themaleaf模板引擎，利用Themaleaf模板引擎、Semantic-ui、jQuery、CSS、JavaScript实现项目页面的构建，
         使用Hibernate框架搭配JPA规范实现了数据库的构建和连接，项目容器采用Tomcat。
```
