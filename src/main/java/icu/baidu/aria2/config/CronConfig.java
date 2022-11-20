package icu.baidu.aria2.config;

import icu.baidu.aria2.repo.BaiduCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class CronConfig {
    @Autowired
    private BaiduCaller caller;

    @Scheduled(cron = "0 0 * * * *")
    public void baiduFlush() {
        caller.flush();
    }
}
