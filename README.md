# MyStaticAgent
大量项目存在类似的功能：MQ监听、定时任务等。
当本地尝试连接时，若没有关闭这些通道，有可能会错误地消费、消耗掉测试资源。
每次都手动处理容易遗漏，提交代码页肯能带上脏数据。
使用Java Agent，每次本地启动都抹掉对应的功能，即不影响代码，又能避免影响测试环境。

# USE
```
-javaagent:path/to/your/javaagent.jar[=command]
```