# HarmlessAgent
大量项目存在类似的功能：MQ监听、定时任务等。
当本地尝试连接时，若没有关闭这些通道，有可能会错误地消费、消耗掉测试资源。
每次都手动处理容易遗漏，提交代码页肯能带上脏数据。
使用Java Agent，每次本地启动都抹掉对应的功能，即不影响代码，又能避免影响测试环境。

# USE
```
-javaagent:path/to/your/javaagent.jar[=command]
```

针对FeignClient, 测试环境Zookeeper主动屏蔽了开发区地址。但是我们可以通过注册中心网址查看到注册到的ip端口。
所以我们通过修改FeignClient，直接将请求地址写入class类中，使得不需要访问Zookeeper就可以正常调试测试环境。

# USE the FeignClient function
```
# 默认是test1
-Dagent.discovery.env=test1
```