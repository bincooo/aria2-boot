package icu.baidu.aria2.config;

import icu.baidu.aria2.repo.impl.BaiduCaller;
import icu.baidu.aria2.service.DnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class CronConfig {
    @Autowired
    private BaiduCaller caller;
    @Autowired
    private DnsService dnsService;

    // 每小时执行一次
    @Scheduled(cron = "0 0 * * * *")
    public void baiduFlush() {
        caller.flush();
    }

    // 每天2点执行一次
//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 2 * * ?")
    public void dnsFlush() {
        dnsService.pullIp();
    }
}
