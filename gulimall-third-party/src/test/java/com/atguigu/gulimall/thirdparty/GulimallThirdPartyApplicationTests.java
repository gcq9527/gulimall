package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.gulimall.thirdparty.commponent.SmsComponent;
import com.atguigu.gulimall.thirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    OSSClient ossClient;

    @Autowired
    SmsComponent smsComponent;

    @Test
    public void testSendSms() throws Exception {
//        smsComponent.sendsmsCode("13112716808","65aa6k56");

        Map<String,String> map = new HashMap<>();
        map.put("client_id","1133714539");
        map.put("client_secret","f22eb330342e7f8797a7dbe173bd9424");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri ","http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code","e7d8231ada0cf805cbb44f8a2d8e9177");

        HttpResponse response = com.atguigu.common.utils.HttpUtils.doPost("https://api.weibo.com",
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
//        Random random = new Random();
//        int i = random.nextInt(100000);
//        System.out.println(i);

    }

    public void sendSms() {
        String host = "http://dingxin.market.alicloudapi.com";
        String path = "/dx/sendSms";
        String method = "POST";
        String appcode = "c6ebcb445e0241f5b699640b2f7e2e5c";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", "13112716808");
        querys.put("param", "code:6789");
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();


        try {

            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testUpload() throws FileNotFoundException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = "LTAI4G9Z9J5nDBngEXEqnquj";
        String accessKeySecret = "CG6Dy17G3OdAOowi556d7IiEclMMLi";

// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\white\\Pictures\\Saved Pictures\\IMG_0815.JPG");

        ossClient.putObject("gulimall-oss01", "IMG_0815.JPG", inputStream);

// 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传成功");
    }

    @Test
    public void testUpload2() throws FileNotFoundException {

        InputStream inputStream = new FileInputStream("C:\\Users\\white\\Pictures\\1646268-20190705165110280-31541911.png");

            ossClient.putObject("gulimall-oss01", "123123123.png", inputStream);
        ossClient.shutdown();
        System.out.println("上传成功");
    }

}
