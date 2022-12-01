package icu.baidu.aria2.service;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.dnspod.v20210323.DnspodClient;
import com.tencentcloudapi.dnspod.v20210323.models.*;
import lombok.extern.java.Log;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Log
@Service
public class DnsService {

    @Value("${dnspod.secret-id}")
    private String secretId;
    @Value("${dnspod.secret-key}")
    private String secretKey;

    @Value("${dnspod.domain}")
    private String domain;
    @Value("${dnspod.record-id}")
    private Long rid;

    public void pullIp() {
        log.info("开始同步公网IP至腾讯云dnspod");
//        String url = "https://ident.me/";
        String url = "https://ifconfig.me/ip";
        String ip = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(new HttpGet(url));
            HttpEntity entity = response.getEntity();
            if (entity != null) ip = EntityUtils.toString(entity, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ip == null) {
            log.info("同步IP失败，查询本机公网IP失败 ~");
            return;
        }

        try {
            Credential cred = new Credential(secretId, secretKey);
            DnspodClient client = new DnspodClient(cred, "ap-shanghai");

            DescribeRecordRequest rec = new DescribeRecordRequest();
            rec.setDomain(domain);
            rec.setRecordId(rid);
            DescribeRecordResponse res = client.DescribeRecord(rec);
            RecordInfo info = res.getRecordInfo();
            if (info == null) {
                log.info("同步IP失败，查询腾讯云端dns失败 ~");
                return;
            }
            if (!info.getValue().equals(ip)) {
                ModifyRecordRequest request = new ModifyRecordRequest();
                request.setRecordId(rid);
                request.setDomain(domain);
                request.setSubDomain(info.getSubDomain());
                request.setRecordType(info.getRecordType());
                request.setRecordLine(info.getRecordLine());
                request.setValue(ip);
                ModifyRecordResponse response = client.ModifyRecord(request);
                String jsonString = ModifyRecordResponse.toJsonString(response);
                log.info("同步IP解析结果: " + jsonString);
            } else log.info("ip一致无需同步 ~");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
