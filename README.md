### 安装方法
1. 安装jdk11+

window: 下载安装包无脑安装即可

linux:
```shell
apt-get install openjdk11
// or
yum install openjdk11
```
2. 修改配置文件 application.yaml
```yaml
baidu:
  # 如果你的请求地址需要删除前缀可以配置remove-prefix
  remove-prefix: 百度网盘
  # 分享链接的密码，默认9527
  share-link-pwd: 9527
  # 网页打开你的百度网盘，进入开发者工具点击【Fetch/XHR】
  # 刷新页面后点击开发者工具里的链接可以找到以下参数 app-id、cookie、dp-log-id
  app-id: 2505xx
  cookie: BAIDUID=84E2BFB882EAD2xxxx05771053BBA588:FG=1; BAIDUID_BFESS=84E2BFB88xxx2598C05771053BBA588:FG=1; csrfToken=9woVLtZeE9IF4nA_rqgocl3P; newlogin=1; BDUSS=XN3aTRBTDJwdn5IWWJGN2l6LWRBdmZhMkRDaHY0MXFTMGhxSXptd0xRTlVGYUZqSVFBQUFBJCQAAAAAAAAAAAEAAAA9cMOPub3BubrDu6y~7LzQvfQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFSIeWNUiHlje; BDUSS_BFESS=XN3aTRBTDJwdn5IWWJGN2l6LWRBdmZhMkRDaHY0MXFTMGhxSXptd0xRTlVGYUZqSVFBQUFBJCQAAAAAAAAAAAEAAAA9cMOPub3BubrDu6y~7LzQvfQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFSIeWNUiHlje; STOKEN=01c3b5d1bacaf726938aa26eb8afd0ca7908313078ad65ebf72da756129b0aeb; PANPSC=12465308967212712687%3AKkwrx6t0uHB2xxxD8%2BbSuXlibiKzYuqjC14bcoQ3Rb16E6HpLI5T5C%2FyiR6%2FrXp563bR7esl%2BvbUEC3RmUNHf6sRWrHWTitSOA6%2BHHtciFfRxz%2FZnirvP3pcJ689LEAIReFB47CbU9vHkRFzk38adFcn1Zflfd6NEFB3kkm1IYWWwAi260CAi3Ua%2FmHyIq%2FPkL2qAeJq7%2BV1Lxzud0jMsTEZmK5RR4JqhfSnrP2UPxXuD7k8za9a6w%3D%3D; ndut_fmt=141E6CFB27FC83A2CE67753E37341AF4E0E4F45F5AB4E6421448BCD37BA10783; ab_sr=1.0.1_NThkZDIxOTMzOWUwNmUyYmIyNDVkMjZiNzRjZGI0NzZlMWUyZjkwMmQ0NTMwOTBlMTU3NjczZDJmNTI0NDQ4MzA4ZmEyYjdlOTk1OGQ4Yzc4ZmYyM2IwYzM3MjVlYzg4MjZiYWViNzI0MDYwZjljNTBiMWY1NWEwMDg3NzRhMWEyNjA3OTA4MGQwNWViODM4ZWJlMDk0NmMwYWMwNDQwY2RmZGM0MmQ0MWY1MDZkNTNlZTZjZjkwZTVjODU5NDFk
  # alist 提供的 access_token
  access-token: 122.4e4c13cbc98661d546fa8b17c9f43113.YsTDXeKUe22KZT9LxfKE16JWDIWWkWH9MOuzRsO.WXWkxx
  dp-log-id: 119080001725427500xx
```
![图片1](images/截屏2022-11-20%2017.31.43.png)
![图片1](images/截屏2022-11-20%2017.38.46.png)

域名dns解析自动上传。
用于解决家庭公网IP不固定，每天凌晨2点会检测公网ip信息，一旦变化便同步到腾讯云dnspod。
腾讯云dnspod使用方法请自行百度
```yaml
dnspod:
  # 你的顶级域名
  domain: icu-xxx.xx
  # 域名解析的行id
  record-id: 12546xxxx
  # 腾讯云pod提供的开放secretId和secretKey
  secret-id: AKIDHf3AEHJJG9xdaLvMcJm0yf9Oxxxxxx
  secret-key: RdaKDqJYXHo7B3h6HDvqdLzxumXxxxxx
```
![图片1](images/截屏2022-12-01%2020.13.33.png)

3. 运行 aria2-boot.jar
进入 aria2-boot.jar 所在目录
打开cmd控制台执行:

window: 
```shell
java -jar aria2-boot.jar -Dspring.config.location=/[你的路径]/application.yaml
```
linux:
```shell
nohup java -jar aria2-boot.jar -Dspring.config.location=/[你的路径]/application.yaml > /dev/null 2>&1 &
```

4. 页面下载配置

需要把user-agent设置为 netdisk;87875

也许有些人需要推送到下载器的（Motrix）
为此我写了一些js，有这些需求的可以配置一下
源码在 `alist-skin`目录下
也可以直接在alist的 全局｜自定义头内添加
```html
<script>
    window.config = {
        // 座右铭
        motto: "书山有路勤为径，学海无崖苦作舟。",
        // 菜单
        menus: [
            {
                name: "博客",
                link: "https://cnblogs.com/bingco",
                method: 1 // 0当前页跳转， 1新标签页跳转
            },
            {
                name: "网盘文档",
                link: "https://alist.nn.ci",
                method: 1
            },
            {
                name: "管理",
                link: "/@manage",
                method: 0
            }
        ],
        // 下载代理，用于推送到下载器
        aria2: {
            jsonrpc: "http://localhost:16800/jsonrpc",
            proxy: location.origin,
            ua: "netdisk;87875",
            max: 32
        }
    }
</script>
<script src="https://cdn.jsdelivr.net/gh/medlar01/cdn@0.0.21/alist-skin/skin.js"></script>
```
![图片1](images/截屏2022-11-20%2017.40.48.png)
![图片1](images/截屏2022-11-20%2017.43.23.png)
### 说明

白嫖第三方分享解析，原理是个人百度网盘创建分享链接，通过白嫖网站的api获取直链
个人分享的链接会被缓存，也就是重复下载不需要再次分享；有效期为12小时
半内存半磁盘存储的缓存，系统中断下线会有几率来不及存到本地

当然了，这个白嫖网站解析很慢，但聊胜于无后续我看看有没有其它更好的
