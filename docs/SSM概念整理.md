# SSM概念整理

## Spring是用于管理项目中的对象的

通过applicationcontext获取所有的applicationContext.xml的中的所有注册到bean中的对象，也就是需要Spring去统一管理



## 所谓注入、装配就是给对象赋值

### 装配有三种方式：

#### xml

(xml又有三种方式）

##### 构造器注入：

```xml
<constructor-arg name="name" value="1"/>
```

##### set注入：

注意：也就是说Student中需要有对Student对象属性的set方法

```xml
<bean id="student" class="cn.di.spring.Student">
        <!--第1种：普通值注入：value-->
        <property name="name" value="张三"/>
</bean>
```

##### 扩展方式注入：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:c="http://www.springframework.org/schema/c"
       
       <!--    p(property)可以直接注入属性的值-->
		<bean id="user" class="com.edu.zut.pojo.UserDi" p:name="Weining" p:age="18"/>
		<!-- c (construct)通过构造器注入-->
		<bean id="userc" class="com.edu.zut.pojo.UserDi" c:name="Weining" c:age="12"/>
</beans>
```



#### 使用注解开发

在实体类上

##### @component：

可标注任意类为 `Spring` 组件，实现bean的注入

> @Repository(“名称”)：dao层
>  @Service(“名称”)：service层
>  @Controller(“名称”)：web层

##### @Value("gongyi"):

实现属性注入

```xml
//等价于<bean id="user" class="com.gongyi.pojo.User"/>
//@Component组件
@Component
@Scope("singleton")
public class User {
    // 相当于<property name="name" value="gongyi"/>
    @Value("gongyi")
    public String name;
    //@Value("muzi")
    public void setName(String name) {
        this.name = name;
    }
}
```



前提：

扫描包和自动装配的关系

```xml
<!--指定要扫描的包，这个包下的注解就会生效-->
	<context:component-scan base-package="com.gongyi"/>
    <!--开启注解的支持-->
    <context:annotation-config/>
```



#### java方式配置spring

其实还是注解

> 主要注解：
>
> @Comonent：在实体类。这个类被Spring接管了，注册到了容器中
>
> @Configuration：在配置类。代表这是一个配置类
>
> ​	@ComponentScan("com.ys.pojo")
>
> ​	@Bean：注册一个bean，相当于之前的bean标签
> ​    //方法名字相当于之前bean内的id属性
> ​    //返回值相当于class属性
> ​    //<bean id="cat1" class="com.kuang.pojo.Cat"/>

- 实体类

  ```java
  @Component//这个注解的意思是这个类被Spring接管了，注册到了容器中
  public class User {
      private String name;
   
      public String getName() {
          return name;
      }
      @Value("小红")//属性注入值
      public void setName(String name) {
          this.name = name;
      }
   
      @Override
      public String toString() {
          return "User{" +
                  "name='" + name + '\'' +
                  '}';
      }
  }
  ```

- 配置类

  ```java
  /*完全不使用spring的xml配置，全权交给Java来做*/
  @Configuration//代表这是一个配置类
  @ComponentScan("com.ys.pojo")
  public class ysconfig {
      @Bean//注册一个bean，相当于之前的bean标签
      //方法名字相当于之前bean内的id属性
      //返回值相当于class属性
      //<bean id="cat1" class="com.kuang.pojo.Cat"/>
      public User user() {
          return new User();//返回要注册到bean的对象
      }
  }
  ```

- 测试

  ```java
  @Test
      public void Test1() {
          //如果完全使用了配置类方式去做，我们就只能通过Annotationconfig上下文来获取容器，通过配置类的class对象加载
          AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ysconfig.class);
          User user = context.getBean("user", User.class);
          System.out.println(user);
      }
  ```

  

### 隐式自动装配

基于bean，也就是一个对象的所有属性，而不是某一个属性

##### xml

byname：保证id唯一

```xml
<!--
1.byName
  - 将查找其类中所有的set方法名，例如setCat，获得将set去掉并且首字母小写的字符串，即cat。
  - 去spring容器中寻找是否有此字符串名称id的对象。
  - 如果有，就取出注入；如果没有，就报空指针异常。
  - 保证id唯一-->
<bean id="person" class="com.pojo.Person" autowire="byName">
```

bytype：保证class唯一

```xml
<!--
2.byType
  - 如果您有相同类型的多个 Bean，则注入失败，并且引发异常
  - 保证class唯一-->
<bean id="person" class="com.pojo.Person" autowire="byType"/>
```

constructor：

```xml
<!--
3.constructor
  - 如果您有相同类型的多个 Bean，则注入失败，并且引发异常-->
<bean id="person" class="com.pojoPerson" autowire="constructor"/>
```



##### 注解

@Autowired：可以不写set方法，需要符合bytype

@Autowired默认通过bytype的方式实现，加上@Qualifier则可以根据byName的方式自动装配

@Resource默认通过byname的方式实现，如果找不到名字，则通过byType实现!



## mybatis注意点

mybatis中连接完数据库、定义mapper接口并实现mapper接口中的sql语句、绑定mybatis的配置文件sqlSessionFactory实例化、

然后需要通过**SqlSession**才能进行对数据库的数据进行增删改查的功能。

方法一：在每个接口的实现类中获取sqlSession

```java
public class UserMapperImple implements UserMapper {
    private SqlSessionTemplate sqlSessionTemplate;

    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    public List<User> getUserList(){
        UserMapper mapper = sqlSessionTemplate.getMapper(UserMapper.class);
        return mapper.getUserList();
    }
}
```

方法二：使用`MapperScannerConfigure`

```xml
    <!--本来应该还有第4步，注册sqlSessionTemple，对应mybatis里的获取sqlsession对象（用来处理sql语句）-->
    <!--4. 注册sqlSessionTemple-->
    <!--原本的做法是：dao/BookMapperImpl.java中实现 UserMapper的实现类:用sqlSessionTemplate执行sql语句-->
    <!--但我们配置dao的接口扫描包，省略了这一步，动态的实现了Dao接口可以注入的Spring容器中-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!--注入 sqlFactory-->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
        <!--扫描要扫描的包，dao接口-->
        <property name="basePackage" value="com.test.dao"/>
    </bean>
```

![image-20220603142621579](appendix\SSM概念整理\image-20220603142621579.png)



## Mybatis和Mybatis-plus

### Mybatis的使用流程

#### Dao层

dao层调数据库

- 首先建立相应的数据库表

- 再通过建立对应的实体类

- 写对应的实体类的接口操作

- 设置相应的相应的xml文件

  > 针对这个接口设置相应的相应的xml文件
  >
  > 编写sql语句

#### Service层

service调dao层

- 需要哪些业务的时候，设置相应的接口。(接口和实现类）

  > 通过Autowired进行对应Mapper实例的注入
  >
  > 对应的Service方法里面进行mapper的引用即可(也就是实现属性的setter方法)

#### Controller层

controller调service层

设置对应的访问方法



### 为什么要使用Mybatis-plus

- 依赖少：仅仅依赖 Mybatis 以及 Mybatis-Spring 。

- 损耗小：启动即会自动注入基本 CURD，性能基本无损耗，直接面向对象操作 。

- 预防Sql注入：内置 Sql 注入剥离器，有效预防Sql注入攻击 。

- 通用CRUD操作：内置通用 Mapper、通用 Service，仅仅通过少量配置即可实现单表大部分 CRUD 操作，更有强大的条件构造器，满足各类使用需求 
- 多种主键策略：支持多达4种主键策略（内含分布式唯一ID生成器），可自由配置，完美解决主键问题 。
- 支持热加载：Mapper 对应的 XML 支持热加载，对于简单的 CRUD 操作，甚至可以无 XML 启动
- 支持ActiveRecord：支持 ActiveRecord 形式调用，实体类只需继承 Model 类即可实现基本 CRUD 操作
- 支持代码生成：采用代码或者 Maven 插件可快速生成 Mapper 、 Model 、 Service 、 Controller 层代码（生成自定义文件，避免开发重复代码），支持模板引擎、有超多自定义配置等。
- 支持自定义全局通用操作：支持全局通用方法注入（ Write once, use anywhere ）。
- 支持关键词自动转义：支持数据库关键词（order、key…）自动转义，还可自定义关键词 。
- 内置分页插件：基于 Mybatis 物理分页，开发者无需关心具体操作，配置好插件之后，写分页等同于普通List查询。
- 内置性能分析插件：可输出 Sql 语句以及其执行时间，建议开发测试时启用该功能，能有效解决慢查询 。
- 内置全局拦截插件：提供全表 delete 、 update 操作智能分析阻断，预防误操作。
- 默认将实体类的类名查找数据库中的表，使用@TableName(value=“table1”)注解指定表名，@TableId指定表主键，若字段与表中字段名保持一致可不加注解。
  

### Mybatis-plus

#### Dao层

- 首先建立相应的数据库表（一样）

- 再通过建立对应的实体类（一样）

- 编写mapper接口

  Mapper接口要继承 BaseMapper 类并指定类型

  ```java
  package com.wang.mybatis_plus.mapper;
  
  import com.baomidou.mybatisplus.core.mapper.BaseMapper;
  import com.wang.mybatis_plus.pojo.User;
  import org.springframework.stereotype.Repository;
  
  //代表持久层
  @Repository
  //在对应的Mapper上继承基本的类 BaseMapper
  public interface UserMapper extends BaseMapper<User> {
      //所有的CRUD功能已经编写完成了
  }
  ```

- 主启动类扫描mapper，并自动装配mapper接口

  ```java
  package com.wang.mybatis_plus;
  
  import org.mybatis.spring.annotation.MapperScan;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  
  //扫描我们的Mapper文件夹
  @MapperScan("com.wang.mybatis_plus.mapper")
  //是SpringBoot项目的中的核心注解，目的是开启自动装配
  @SpringBootApplication
  public class MybatisPlusApplication {
  
      public static void main(String[] args) {
          SpringApplication.run(MybatisPlusApplication.class, args);
      }
  
  }
  ```



## 重要的点

- invoke被认为是一种拦截器

- service可以调用dao，也可以调用service（事务传播）

- 声明bean：将对象放入ioc容器（常常要配合包扫描）
  注入bean（也叫自动装配）：为对象注入属性
- AOP原理是基于动态代理，请求拦截交给动态代理