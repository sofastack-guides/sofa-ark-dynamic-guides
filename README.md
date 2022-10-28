## 实验内容
- 动态部署
  
  通过 [SOFAArk](https://github.com/sofastack/sofa-ark) 提供的动态模块能力，实现商品列表排序策略的动态变更。通过在不重启宿主机，不更改应用配置的情况下实现应用行为的改变。

- 静态合并部署

  通过 [SOFAArk](https://github.com/sofastack/sofa-ark) 提供的静态合并部署能力（2.0.7版本及以上），宿主应用启动时，pom依赖里的 ark biz 模块会安装并启动，无需通过动态部署手动安装 ark biz 模块。
## 前期准备

从 github 上将 demo 工程克隆到本地

```bash
git clone git@github.com:sofastack-guides/sofa-ark-dynamic-guides.git
```

然后将工程导入到 IDEA 或者 eclipse，打开工程后，工程目录结构如下：

```bash
├── dynamic-facade
├── dynamic-provider
├── dynamic-stock-mng
└── pom.xml
```

* dynamic-facade 定义了一个 Java 接口 io.sofastack.dynamic.facade.StrategyService，该接口用于对传入的商品列表进排序并返回
* dynamic-provider 实现了 dynamic-facade 定义的接口，并将实现类发布成一个服务
* dynamic-stock-mng 宿主应用，提供一个 web 页面，用于展示实验效果

## 动态部署
### 1、将 dynamic-provider 打包成 ark biz
在 dynamic-provider/pom.xml 中，增加 ark 打包插件，该模块实现了宿主应用的一个接口，同时暴露一个rest服务，进行配置：

![image.png](https://gw.alipayobjects.com/mdn/rms_ff360b/afts/img/A*y2BvRKG14JUAAAAAAAAAAABkARQnAQ)


```xml
<plugins>
  <!--这里添加ark 打包插件-->
  <plugin>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofa-ark-maven-plugin</artifactId>
    <executions>
      <execution>
        <id>default-cli</id>
        <goals>
          <goal>repackage</goal>
        </goals>
      </execution>
    </executions>
    <configuration>
      <skipArkExecutable>true</skipArkExecutable>
      <outputDirectory>./target</outputDirectory>
      <bizName>dynamic-provider</bizName>
    </configuration>
  </plugin>
</plugins>
```

### 2、构建宿主应用

在已下载下来的工程中，dynamic-stock-mng 作为实验的宿主应用工程模型。通过此步骤，将 dynamic-stock-mng  构建成为动态模块的宿主应用。在SOFAArk2.0之后，宿主应用已经与普通应用并无差别，主要体现在下面会介绍到的宿主应用的打包方式、构建产物和启动方式。

#### step1 : 引入动态模块依赖

> 动态模块是通过 SOFAArk 组件来实现的，因此需要引入 SOFAArk 相关的依赖即可。关于 SOFAArk 可以参考[SOFABoot 类隔离](https://www.sofastack.tech/projects/sofa-boot/sofa-ark-readme/)
一节进行了解。

![image.png](https://gw.alipayobjects.com/mdn/rms_565baf/afts/img/A*lM_1SoNIXIYAAAAAAAAAAABkARQnAQ)

* 宿主应用打包插件

    ```xml
        <plugins>
            <!-- 这里可直接使用springboot的打包插件 -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <outputDirectory>target</outputDirectory>
                    <classifier>ark-biz</classifier>
                </configuration>
                <executions>
                    <execution>
                        <id>package</id>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    ```

### 3、打包 & 启动宿主应用

#### 启动宿主应用
SOFAArk 2.0之后宿主应用可以直接启动，可以在IDE里增加`-Dsofa.ark.embed.enable=true` 启动参数，直接启动 StockMngApplication 类。

启动成功之后日志信息如下：

![image.png](https://gw.alipayobjects.com/mdn/rms_565baf/afts/img/A*3N_nS6P223IAAAAAAAAAAABkARQnAQ)

### 4、 引入默认的排序策略模块
dynamic-provider 提供的 io.sofastack.dynamic.facade.StrategyService 实现类返回了默认排序。

执行 mvn clean package 进行打包，此时可以打包出新版本 dynamic-provider ark biz包，如下图所示：
![image.png](https://gw.alipayobjects.com/mdn/rms_c69e1f/afts/img/A*fbgOSpPdAIkAAAAAAAAAAABkARQnAQ)

telnet 连接 SOFAArk，安装默认版本 dynamic-provider:
```bash
## 连接 SOFAArk telnet
> telnet localhost 1234

## 安装新版本 dynamic-provider
sofa-ark>biz -i file:///Users/sample/sofachannel-demo/dynamic-provider/target/dynamic-provider-1.0.0-ark-biz.jar
Start to process install command now, pls wait and check.

## 查看安装的模块信息
sofa-ark>biz -a
stock-mng:1.0.0:activated
dynamic-provider:1.0.0:activated
biz count = 2
```

访问 http://localhost:8080 ，现在展示的是默认的排列顺序，如下所示：

![image.png](https://gw.alipayobjects.com/mdn/rms_c69e1f/afts/img/A*HpKuR7Wn44UAAAAAAAAAAABkARQnAQ)

### 5、新建按照销量排序策略模块
dynamic-provider 提供的 io.sofastack.dynamic.facade.StrategyService 实现类返回了默认排序，现在我们要开发一个新版本模块，这个新版本模块会按照销量高低返回商品列表。

首先，修改 io.sofastack.dynamic.provider.impl.StrategyServiceImpl 实现类如下：

```java
@Service
@SofaService
public class StrategyServiceImpl implements StrategyService {
    @Override
    public List<ProductInfo> strategy(List<ProductInfo> products) {
        Collections.sort(products, (m, n) -> n.getOrderCount() - m.getOrderCount());
        products.stream().forEach(p -> p.setName(p.getName()+"("+p.getOrderCount()+")"));
        return products;
    }
}
```

然后，修改 dynamic-provider 版本号 2.0.0: 

```xml
<version>2.0.0</version>
```

最后，由于本Demo引入web-ark-plugin，所以每个模块会复用同一个tomcat实例，所以需要更改server的webContextPath，搜索并修改dynamic-provider的pom由原来的

```xml
<webContextPath>provider</webContextPath>
```
换成一个不同的webContextPath
```xml
<webContextPath>provider-1</webContextPath>
```

配置完成之后，执行 mvn clean package 进行打包，此时可以打包出新版本 dynamic-provider ark biz包，如下图所示：
![pic](https://gw.alipayobjects.com/mdn/rms_c69e1f/afts/img/A*lWUSQb95azoAAAAAAAAAAABkARQnAQ)


telnet 连接 SOFAArk，安装新版本 dynamic-provider:
```bash
## 连接 SOFAArk telnet
> telnet localhost 1234

## 安装新版本 dynamic-provider
sofa-ark>biz -i file:///Users/sample/sofachannel-demo/dynamic-provider/target/dynamic-provider-2.0.0-ark-biz.jar
Start to process install command now, pls wait and check.

## 查看安装的模块信息
sofa-ark>biz -a
stock-mng:1.0.0:activated
dynamic-provider:2.0.0:resolved
dynamic-provider:1.0.0:activated
biz count = 3

## 切换 activated 模块
sofa-ark>biz -o dynamic-provider:2.0.0
Start to process switch command now, pls wait and check.
```

切换完模块后，访问 http://localhost:8080 ，现在展示的是列表编程按照销量进行排序，如下所示：
如果要访问模块中的rest请求，请带上模块 sofa-ark-maven-plugin 里定义的webContextPath

![image.png](https://gw.alipayobjects.com/mdn/rms_c69e1f/afts/img/A*vqEJQ4775u4AAAAAAAAAAABkARQnAQ)

## 静态合并部署

静态合并部署，即：宿主应用启动时，pom 依赖中的 ark biz 模块会安装并启动，无需手动进行动态安装部署。静态合并部署和动态部署不是互斥的，在静态部署之后，也可以通过动态部署来升级 ark biz。在此次实验中，首先我们安装 ark biz 模块到本地 maven 仓库，接着把 ark biz 模块作为依赖引入到宿主应用中，最后启动宿主应用验证静态合并部署能力。

### 1、安装 ark biz 到本地 maven 仓库
我们安装 dynamic-provider 1.0.0 版本的 ark biz 到本地 maven 仓库。

首先，为了统一实验流程，我们确认 dynamic-provider 版本为 1.0.0，webContextPath 为 provider:
```bash
<version>1.0.0</version>
// ...
<webContextPath>provider</webContextPath>
```
接着，修改 dynamic-provider 的打包配置，如下：
```bash
<plugin>
                <groupId>com.alipay.sofa</groupId>
                <artifactId>sofa-ark-maven-plugin</artifactId>
                <!-- 版本需要 2.0.7 及以上-->
                <version>2.0.7</version> 
                <executions>
                    <execution>
                        <id>default-cli</id>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <skipArkExecutable>true</skipArkExecutable>
                    <!-- 将 ark biz 安装到仓库-->
                    <attach>true</attach>
                    <outputDirectory>./target</outputDirectory>
                    <bizName>dynamic-provider</bizName>
                    <webContextPath>provider</webContextPath>
                    <declaredMode>true</declaredMode>
                </configuration>
            </plugin>
```
然后，在项目根目录下执行以下命令安装 ark biz 到本地 maven 仓库：
```bash
mvn clean install -pl dynamic-provider -am
```
最后，在本地 maven 仓库检查存在 ark biz：
dynamic-provider-1.0.0-ark-biz.jar
> 其路径大致如下 ..../io/sofastack/dynamic-provider/1.0.0/dynamic-provider-1.0.0-ark-biz.jar
### 2、宿主应用引入 ark biz 依赖
在 dynamic-stock-mng 中引入 dynamic-provider-1.0.0-ark-biz 依赖：
```bash
<dependency>
    <groupId>io.sofastack</groupId>
    <artifactId>dynamic-provider</artifactId>
    <classifier>ark-biz</classifier>
    <version>1.0.0</version>
</dependency>
```
### 3、启动宿主应用并验证
#### 3.1 本地打包启动验证
首先，打包项目：
```bash
mvn clean package -DskipTests
```
然后，启动宿主应用 dynamic-stock-mng：
```bash
cd dynamic-stock-mng/target

java -jar -Dsofa.ark.embed.enable=true -Dcom.alipay.sofa.ark.master.biz=dynamic-stock-mng dynamic-stock-mng-1.0.0-ark-biz.jar
```
启动成功后日志信息如下：

最后，访问 http://localhost:8080 ，现在展示的是默认的排列顺序，如下所示：
![image.png](https://gw.alipayobjects.com/mdn/rms_c69e1f/afts/img/A*HpKuR7Wn44UAAAAAAAAAAABkARQnAQ)

#### 3.2 IDE 本地启动验证
为了避免 IDE 读取本地编译的 dynamic-provider 和 dynamic-facade，需要将本项目中的 dynamic-provider 和 dynamic-facade 设置成其它版本，如 2.0.0。
- dynamic-facade/pom.xml
```bash
<artifactId>dynamic-facade</artifactId>
<version>2.0.0</version>
```
- dynamic-provider/pom.xml
```bash
<artifactId>dynamic-provider</artifactId>
<version>2.0.0</version>
```
IDE**清除缓存**后，重启IDE。在IDE里增加-Dsofa.ark.embed.enable=true 启动参数，直接启动 StockMngApplication 类。
启动成功后日志信息如下：

访问 http://localhost:8080 ，现在展示的是默认的排列顺序，如下所示：
![image.png](https://gw.alipayobjects.com/mdn/rms_c69e1f/afts/img/A*HpKuR7Wn44UAAAAAAAAAAABkARQnAQ)