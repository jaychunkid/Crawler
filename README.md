## Crawler
网络信息检索课程实验，实现一个能够从根url开始，从网页中抓取url并记录在文件中的爬虫程序。
程序基于广度优先原则进行抓取，支持检测robot协议，支持设定抓取的最大深度，抓取文件类型，以及本地域名。
仅本地域名下的url会被抓取，不属于本地域名的url仅记录。
* FetcherImpl：利用net包下的HttpURLConnection类，连接服务器，获取返回的内容，支持服务器重定向响应，以及从响应报文中获取字符集。
* ParserImpl：利用swing包下的ParserDelegator类对网页内容进行解析，支持获取meta标签中的重定向信息，字符集信息以及robot协议。
* RobotstxtManagerImpl：管理每个url对应的robot协议内容。
* URLPoolImpl：管理爬虫抓取到的url，支持重复url检测，获取待处理url顺序遵循FIFO原则。
### 测试程序
* 运行startCrawler类中的main方法
* 输入根url
* 输入本地域名
* 输入抓取的最大深度
* 等待程序完成，输出抓取到的url数目，程序运行时间和抓取速度
* 抓取到的url记录在doc目录下的urls.txt文件中，程序运行日志记录在doc目录下的log.txt文件中


