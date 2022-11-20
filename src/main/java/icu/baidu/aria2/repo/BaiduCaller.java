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
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Log
@Repository
public class BaiduCaller {

    private final BaiduProperties properties;
    private static final ObjectMapper mapper = new ObjectMapper();

    public BaiduCaller(BaiduProperties properties) {
        this.properties = properties;
    }

    public String shareLink(String uri) {
        int index = uri.lastIndexOf("/");
        String dir = uri.substring(0, index);
        String file = uri.substring(index + 1);

        String fsId = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            if (dir.equals("")) dir = "/";
            String url = "https://pan.baidu.com/rest/2.0/xpan/file?method=list&dir=" + dir + "&web=0&access_token="
                    + properties.getAccessToken();
            log.info("开始获取网盘目录: " + dir);
            HttpGet get = new HttpGet(url);
            get.setHeader("Cookie", properties.getCookie());
            CloseableHttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            if (entity != null) {

                String body = EntityUtils.toString(entity, "utf-8");
                log.info(body);
                @SuppressWarnings("unchecked")
                List<Map<String, Serializable>> list = (List<Map<String, Serializable>>) mapper
                        .readValue(body, Map.class)
                        .get("list");
                if (list != null) {
                    fsId = list.stream()
                            .filter(it -> file.equals(it.get("server_filename")))
                            .map(it -> it.get("fs_id").toString())
                            .findAny()
                            .orElse(null);
                }
            }
        } catch (IOException e) {
            log.info("获取网盘目录失败: " + dir);
            e.printStackTrace();
        }

        if (fsId == null) {
            log.info("无法获取网盘文件【" + file + "】的 fs_id");
            return null;
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            UUID uuid = UUID.randomUUID();
            String url = "https://pan.baidu.com/share/set?channel=chunlei&bdstoken="
                    + uuid + "&clienttype=0&app_id="
                    + properties.getAppId() + "&web=1&dp-logid="
                    + properties.getDpLogId();
            log.info("开始创建网盘分享链接: " + uri);
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setHeader("Cookie", properties.getCookie());
            post.setEntity(new StringEntity("period=1&pwd=" + properties.getShareLinkPwd() +
                    "&eflag_disable=true&schannel=4&fid_list=[" + fsId + "]"));
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if(entity != null) {
                String body = EntityUtils.toString(entity, "utf-8");
                log.info(body);
                @SuppressWarnings("unchecked")
                Map<String, Serializable> value = mapper.readValue(body, Map.class);
                return (String) value.get("link");
            }

        } catch (IOException e) {
            log.info("无法创建网盘分享链接: " + uri);
            e.printStackTrace();
        }
        return null;
    }

    public void flush() {
        String url = "https://pan.baidu.com/disk/main";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            get.setHeader("Cookie", properties.getCookie());
            client.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
