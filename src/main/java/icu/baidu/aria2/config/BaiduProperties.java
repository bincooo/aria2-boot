package icu.baidu.aria2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "baidu")
public class BaiduProperties {
    private String removePrefix;
    private String shareLinkPwd = "9527";
    private String dpLogId;
    private String cookie;
    private String appId;

}
