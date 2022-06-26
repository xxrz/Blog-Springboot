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
> @Comonent：在实体类。这个类被Spring接管了，注册到了容器中。【标注的是类】
>
> @Configuration：在配置类。代表这是一个配置类。【标注的是类】
>
> ​	@ComponentScan("com.ys.pojo")
>
> ​	@Bean：注册一个bean，相当于之前的bean标签。【标注的是方法】
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



## Mybatis注意点

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



### 在Mybatis-plus中如何使用分类插件

内置分页插件：基于 Mybatis 物理分页，开发者无需关心具体操作，配置好插件之后，写分页等同于普通List查询。



步骤：

**创建配置类MyBatisPlusConfig**

1.添加配置类注解@Configuration。

2.需要扫描**mapper接口**所在的包（主类中的注解移过来）

3.配置分页插件（需要注解@Bean）

```java
//1.添加配置类注解@Configuration
@Configuration
//2.需要扫描**mapper接口**所在的包（主类中的注解移过来）
@MapperScan("nuc.guigu.zwj.mybatisplus.mapper")
public class MyBatisPlusConfig {
    //3.配置分页插件
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
   //数据库类型是MySql，因此参数填写DbType.MYSQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```



**创建一个测试类MyBatisPlusTest**

1.测试类记得添加注解@SpringBootTest

2.对使用的mapper组件进行自动装配，添加注解@Autowired

```java
@Autowired
UserMapper userMapper;
```

3.测试方法里

userMapper方法里有一个selectPage（），这个方法参数有两个，第一个是Page类型的（分页对象），第二个是Wapper类型的，因此我们创建这两个对象。

Page类的泛型为我们操作的实体类对象，参数为当前页的页码（current），个每页显示的条数（size），语句为

```java
Page<User> page = new Page<>(2,3);
```

第二个参数Wapper类型为条件构造器的条件，因为这里查询的是所有数据（即没有条件），所以Wapper类型的数据填null。

```java
//1.测试类记得添加注解@SpringBootTest
@SpringBootTest
public class MyBatisPlusPluginsTest {
 //2.对使用的mapper组件进行自动装配，添加注解@Autowired
    @Autowired
    UserMapper userMapper;
 
    //3.测试方法
    @Test
    public void test01(){
        Page<User> page = new Page<>(2,3);
        userMapper.selectPage(page,null);
        System.out.println(page);
    }
}
```



### Mybatis如何使用分类插件

https://www.jianshu.com/p/8c78d05e4506



## restful

### 概念

Restful就是一个资源定位及资源操作的风格。不是标准也不是协议，只是一种风格。基于这个风格设计的软件可以更简洁，更有层次，更易于实现缓存等机制。



### 使用

非REST的url：http://../query.action?id=3&type=t01（传递的数据大小有限制）

REST的url风格：http://../query/3/t01[需要服务端指定是什么参数，如示例三]



@RequestMapping：标注请求的地址路径。

可使用RestFul风格，隐藏参数名传入参数值。如示例三，@PathVariable标注需要传入的参数，并以{}的形式在@RequestMapping说明。

```java
@RequestMapping("/HelloController")
public class HelloController {

    // 示例一：
    //真实访问地址 : 项目名/HelloController/hello
    @RequestMapping("/hello")
    public String sayHello(Model model){
        //向模型model中添加属性msg与值，可以在JSP页面中取出并渲染
        model.addAttribute("msg","hello,SpringMVC");
        //web-inf/jsp/hello.jsp 返回视图名称
        return "hello";
    }
    // 示例三：
        @RequestMapping("/test/{p1}/{p2}")
//    @GetMapping("/test/{p1}/{p2}")
    public String test(@PathVariable int p1, @PathVariable int p2, Model model){
        int result = p1 + p2;
        model.addAttribute("msg",result);

        return "test"; // 默认页面为转发的方式
        // return "redirect:/hello"; // 重定向到/hello路径
    }
}
```



### 相关注解

### 注解

1. `@RequestMapping`一般用来指定controller控制器访问路径吗，例：`@RequestMapping("/restful")`

2. 当请求地址中包含变量时，可以与`@PathVariable`注解一起使用，用来获取参数

3. 可以指定访问的方式（POST, GET, DELETE, PUT ...）,例：`@RequestMapping(value = "/t1/{p1}/{p2}", method = {RequestMethod.POST})`

4. @RequestMapping有一些衍生注解, 如下：

    ```java
    @GetMapping
    @PostMapping
    @PutMapping
    @DeleteMapping
    @PatchMapping
    ```



## 重要的点

- invoke被认为是一种拦截器
- service可以调用dao，也可以调用service（事务传播）
- 声明bean：将对象放入ioc容器（常常要配合包扫描）
  注入bean（也叫自动装配）：为对象注入属性
- AOP原理是基于动态代理，请求拦截交给动态代理
- 事务的原理是AOP
- handler就是拦截器



mybatis的xml文件中，#{}中的变量时从方法中传过来的

```xml
 <!-- List<Tag> findTagsByArticleId(Long articleId);-->
    <select id="findTagsByArticleId" parameterType="long" resultType="com.mszlu.blog.dao.pojo.Tag">
        select id,avatar,tag_name as tagName from ms_tag
        where id in
        (select id from ms_article_tag where article_id=#{articleId})
    </select>
```





## 注解

### 修饰在类上

```java
@Comonent
@Configuration
@Repository(“名称”)：dao层
@Service(“名称”)：service层
@Controller(“名称”)：web层
@ComponentScan("com.ys.pojo") //包扫描
@ControllerAdvice //拦截所有标注@controller的注解
```



### 修饰在方法上

```java
@Bean //注册一个bean
@ExceptionHandler(Exception.class) //拦截的异常的类型
@ResponseBody //返回json数据,不加的话是页面
```

>   **为什么有了@comonent还需要@Bean**
>
>   -   如果你想要将第三方库中的组件装配到你的应用中，在这种情况下，是没有办法在它的类上添加@Component注解的，因此就不能使用自动化装配的方案了，但是我们可以使用@Bean,当然也可以使用XML配置。
>   -    @Bean 需要在配置类中使用，即类上需要加上@Configuration注解
>   -   @Compent 作用就相当于 XML配置

### 类的属性（变量）

```java
@Autowired //注入
```



### 类和方法

```java
@RequestMapping("articles") //路径映射
@PostMapping("search")
```



### 参数（变量）

```java
@PathVariable("id") //里面的值mybatis中是传入的参数名
@RequestBody //从请求的body中获取值,一般和PostMapping一起用
```

