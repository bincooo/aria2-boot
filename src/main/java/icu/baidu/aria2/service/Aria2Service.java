package icu.baidu.aria2.service;

import icu.baidu.aria2.repo.Caller;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Log
@Service
public class Aria2Service {

    private final Caller baiduCaller;
    private final Caller klwCaller;

    public Aria2Service(@Qualifier("baiduCaller") Caller baiduCaller, @Qualifier("klwCaller") Caller klwCaller) {
        this.baiduCaller = baiduCaller;
        this.klwCaller = klwCaller;
    }

    public String get(String uri) {
        String url = baiduCaller.call(uri);
        if (url == null) {
            baiduCaller.clear(uri);
            log.info("百度网盘创建分享链接失败: " + uri);
            return null;
        }
        String link = klwCaller.call(url);
        if (link == null) {
            klwCaller.clear(url);
        }
        return link;
    }
}
