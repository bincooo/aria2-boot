package icu.baidu.aria2.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import icu.baidu.aria2.config.BaiduProperties;
import lombok.extern.java.Log;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
@Service
public class KlwCaller {
    private final BaiduProperties properties;
    private static final String REGEX = "<a [^>]+ href=\"(javascript:downfile[^\"]*?)\" title=\"([^\"]*?)\">";
    private static final ObjectMapper mapper = new ObjectMapper();

    public KlwCaller(BaiduProperties properties) {
        this.properties = properties;
    }

    public String parse(String link) {
        String url1 = "https://www.kelongwo.com/Resource_function/pan/baidu/?url=" + link + "&pwd=" + properties.getShareLinkPwd();
        String url2 = "https://www.kelongwo.com/Resource_function/pan/baidu/api.php";

        String type = null;
        String title = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(new HttpGet(url1));
            String HTML = EntityUtils.toString(response.getEntity(), "utf-8");

            Pattern pattern = Pattern.compile(REGEX);
            Matcher matcher = pattern.matcher(HTML);
            if (matcher.find()) {
                type = matcher.group(1);
                title = matcher.group(2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (type == null) return null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url2);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setEntity(new StringEntity("type_kelong=download_choice&type=" + type));
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                @SuppressWarnings("unchecked")
                Map<String, Serializable> value = mapper.readValue(EntityUtils.toString(response.getEntity(), "utf-8"), Map.class);
                if ((int) value.get("code") == 200) {
                    return (String) value.get("url");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
