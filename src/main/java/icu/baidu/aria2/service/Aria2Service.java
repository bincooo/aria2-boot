package icu.baidu.aria2.service;

import icu.baidu.aria2.repo.BaiduCaller;
import icu.baidu.aria2.repo.KlwCaller;
import lombok.extern.java.Log;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Log
@Service
@CacheConfig(cacheNames = "baidu-link")
public class Aria2Service {

    private final BaiduCaller baiduCaller;
    private final KlwCaller klwCaller;

    public Aria2Service(BaiduCaller baiduCaller, KlwCaller klwCaller) {
        this.baiduCaller = baiduCaller;
        this.klwCaller = klwCaller;
    }

    @Cacheable(key = "#uri")
    public String get(String uri) {
        String link = baiduCaller.shareLink(uri);
        if (link == null) {
            log.info("百度网盘创建分享链接失败: " + uri);
            return null;
        }
        return klwCaller.parse(link);
    }
}
