# 快速开始

> SOFABoot 需要基于 3.2.x 版本以下，推荐使用 3.1.4。

1、clone 当前代码

> git clone https://github.com/sofastack-guides/sofa-ark-dynamic-guides.git

2、打包

> mvn clean package -DskipTests

3、启动宿主应用

> java -jar target/ark-dynamic-master-1.0.0-ark-executable.jar


4、验证宿主 rest 请求

> curl http://localhost:8088/master/api/master #期望返回：THIS IS DEFAULT PROVIDER

5、安装模块

> telnet localhost 1234

> biz -i file://biz -i file:///${your-path}/sofa-ark-dynamic-guides/target/ark-dynamic-module-1.0.0-ark-biz.jar

> biz -a #通过 biz -a 查看，如果两个模块都是 activited 状态，则继续

6、验证基座 rest 请求

> curl http://localhost:8088/module/api/module  #期望返回：SUCCESS FROM MODULE

