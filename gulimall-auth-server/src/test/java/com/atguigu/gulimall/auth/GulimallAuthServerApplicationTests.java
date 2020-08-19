package com.atguigu.gulimall.auth;

import com.atguigu.common.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class GulimallAuthServerApplicationTests {

    @Test
    public void contextLoads() throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("client_id","1133714539");
        map.put("client_secret","f22eb330342e7f8797a7dbe173bd9424");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri ","http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code","e7d8231ada0cf805cbb44f8a2d8e9177");

        HttpResponse response = HttpUtils.doPost("https://api.weibo.com",
                "/oauth2/access_token",
                "post",
                null,
                null,
                map);
        if (response.getStatusLine().getStatusCode() ==200){
            System.out.println(response);
        } else {
            System.out.println(response);
        }
    }

}
