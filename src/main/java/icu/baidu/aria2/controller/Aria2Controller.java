package icu.baidu.aria2.controller;

import icu.baidu.aria2.config.BaiduProperties;
import icu.baidu.aria2.service.Aria2Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.HandlerMapping;

@Log
@Controller
public class Aria2Controller {
    private final BaiduProperties properties;
    private final Aria2Service service;

    public Aria2Controller(BaiduProperties properties, Aria2Service service) {
        this.properties = properties;
        this.service = service;
    }

    // 百度直链跳转
    // 白嫖第三方分享解析，原理是个人百度网盘创建分享链接，通过白嫖网站的api获取直链
    // 个人分享的链接会被缓存，也就是重复下载不需要再次分享；有效期为1天
    // 半内存半磁盘存储的缓存，系统中断下线会有几率来不及存到本地
    @GetMapping("/baidu/**")
    public String baidu(HttpServletRequest request) {
        String uri = handleUri(request);
        String link = service.get(uri);
        log.info("解析结果: " + link);
        if (link == null) {
            throw new RuntimeException("解析失败~");
        }

        return "redirect:" + link;
    }

    private String handleUri(HttpServletRequest request) {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String uri = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        if (StringUtils.hasLength(properties.getRemovePrefix()) && uri.startsWith(properties.getRemovePrefix())) {
            uri = uri.substring(properties.getRemovePrefix().length());
        }
        return uri;
    }

//    @Autowired
//    private DnsService dnsService;
//
//    @GetMapping("/test")
//    @ResponseBody
//    public String test() {
//        dnsService.pullIp();
//        return "OK";
//    }
}
