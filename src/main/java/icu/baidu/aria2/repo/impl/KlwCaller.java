package icu.baidu.aria2.repo.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import icu.baidu.aria2.config.BaiduProperties;
import icu.baidu.aria2.repo.Caller;
import lombok.extern.java.Log;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
@Repository
@CacheConfig(cacheNames = "cache-0")
public class KlwCaller implements Caller {
    private final BaiduProperties properties;
    private static final String REGEX = "<a [^>]+ href=\"(javascript:downfile[^\"]*?)\" title=\"([^\"]*?)\">";
    private static final String CHECK_REGEX = "var data = [{]([^}]*?)}";
    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    private String cookie = "";

    public KlwCaller(BaiduProperties properties) {
        this.properties = properties;
    }

    @Cacheable(key = "'KLW:' + #url")
    public String call(String url) {
        return parseLink(crawlHtml(url, 3));
    }

    private String crawlHtml(String path, int maxTry) {
        if (maxTry < 0) return null;

        String type = null;
        String url = "https://www.kelongwo.com/Resource_function/pan/baidu/?url=" + path + "&pwd=" + properties.getShareLinkPwd();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            get.setHeader("cookie", cookie);
            CloseableHttpResponse response = client.execute(get);
            String HTML = EntityUtils.toString(response.getEntity(), "utf-8");
            if (response.getStatusLine().getStatusCode() == 512) {
                cookie = checkCode(HTML);
                return crawlHtml(path, maxTry - 1);
            }


            Pattern pattern = Pattern.compile(REGEX);
            Matcher matcher = pattern.matcher(HTML);
            if (matcher.find()) {
                type = matcher.group(1);
                log.info("klw type: " + type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return type;
    }

    private String parseLink(String type) {
        if (type == null) return null;
        String url = "https://www.kelongwo.com/Resource_function/pan/baidu/api.php";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("cookie", cookie);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setEntity(new StringEntity("type_kelong=download_choice&type=" + type));
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String body = EntityUtils.toString(entity, "utf-8");
                log.info("klw body: " + body);
                @SuppressWarnings("unchecked")
                Map<String, Serializable> value = mapper.readValue(body, Map.class);
                if ((int) value.get("code") == 200) {
                    return (String) value.get("url");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String checkCode(String html) throws JsonProcessingException {
        log.info("克隆网开始人机检测 ...");
        Pattern compile = Pattern.compile(CHECK_REGEX);
        Matcher matcher = compile.matcher(html);
        Map<String, String> data = null;
        if (matcher.find()) {
            String jsonString = "{" + matcher.group(1).replaceAll("'", "\"") + "}";
            data = mapper.readValue(jsonString, Map.class);
        }
        if (data == null) return null;

        String url = "https://captcha.funcdn.com/jsCaptchaVerify";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");

            for (int code = 100000; code <= 999999; code++) {
                String hex = DigestUtils.md5DigestAsHex((data.get("challenge") + code).getBytes());
                if (hex.equals(data.get("answer"))) {
                    log.info("匹配成功，循环了"  + (code - 100000) + "次: " + hex);
                    data.put("code", code + "");
                    break;
                }
            }

            post.setEntity(new StringEntity(mapper.writeValueAsString(data)));
            CloseableHttpResponse response = client.execute(post);
            String body = EntityUtils.toString(response.getEntity(), "utf-8");
            log.info("结果: " + body);
            @SuppressWarnings("unchecked")
            Map<String, String> m = mapper.readValue(body, Map.class);
            return "_funcdn_token=" + m.get("fc_token");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @CacheEvict(key = "'KLW:' + #url")
    public void clear(String url) {}
}
