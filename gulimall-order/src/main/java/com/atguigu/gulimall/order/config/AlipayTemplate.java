package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016101900720182";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCzrPCgB9jlExvTy7ad687iJncsIvtkWlJy7sOxv5v6E5kzO2aeCW5t5j+MV/K9Qrdq0c6kStylC0r6VNRZxVth+09CBnAxBQjiyUTODGzXG2uphy1pqlwijRWdVWaZxzL7NWyIueqO/T3/4ooyOLqS8xMRXjtqsUmi9tX/s7DyIJVYawKRHH0Vjre6WT/KP4eLUCeAfkLN+Ie7HZEC0IaN7UaBMOx2R/uIzYly9bj2tCTkrRM5IJUd3lGfWI/dnaHL9IkgKKVgqk2KIGQ62Ka/ZWYppPWxxGpu45j4KLcPkHdtNJDKaymIfNiCuZIJrInJbsYASKV8grI4ObvFrrDRAgMBAAECggEAaLTvxVLNYzGvlc2xrnYtKj2gVWhNeD3qrm/WO0+eZi3lqaWEN7TnVcRIZFEcdI7ctLNv6O53hvb9ysSEu4v3pTH/DksSNn7onsEMB8fV5cVlCHZuUJWm+7ucTQPnttYPL7YCeeteddW+u9AZGQtWLyzYf9aaaaLHduDB9TGSfIblwJVgZflR5mxvHAriBEOEcC3QD9eJ3SrrZ0BgmLAiSRrLrnWfCkioD+1UBhXEsSzYMbT2jOUFVYZDeTeVWdXQSgmIFgQVpK01Imk3qHUboHVoGJcbE93XSg3VmLqpkgCJ6bRj+9kFwzVKy76NoV2KVGfZW+FGE4HTCE/n1qZ1AQKBgQDoJUeeZRMCsq6FRO9SYRJrovB2YdqS9f3dKwQulNbfQAtFFf+Prr2HvLBjLTGAhqG4Rl+Kfyn2U9ZNgHf6E7KqU/IIysrvcK5etKBVAY/jCC9mzJBgOFqjemU65M0tEqur2LPtyYf/TyXPn20FEHTnwRCPLxr9anNKLpDFeWw7hQKBgQDGI2fPBZPGUboxwiLV0nTMoIC49ugnLb7DwGOMFpBDSZzMe862Hgm9T8ccRo8Ph6f4zPmA/fzu1Rt5j2nDrmM7CSg8/ClnKLLwVoe2u7gPZSjbIJkeCL5SHAicqgnofs9fg1qvw6tHNMLwbbFoZxI7fa6qMMWc/fOxU7+XIBXD3QKBgBRPnrKHGWC1DXvvihjlEV2FhhB1g1A0YZ/GMbl5oEssKVBEGnYRzddgdPZYb+pU3TVoQte1RJCeyTNCuDyTGKC6c4S9aYAFldwHZbfKSTKN9sfBwigJBjUjclPyzNjSt15zuVpmtZVq8V33DuzQgcn9JFvCvVPKyoeFB3mfpA6ZAoGAK8urqHDdJxrtgw4cURMByjE4AqpgyjgsUGx4FIDwRk9BH1nGaS7ejD/UDQXznUUo9a+o/sAlZk0ok4gFosErIeN2R3SdWjX+x2z49oUrvAtqKnVcIu1f1BpqNObI5eERyVTpeKxRcZ3R8P7uFduihg/Tyb6aD4d6lBmk4ayv4ukCgYAMRzcWBNo3k/NvMu1YXZ5+2/M1WZownNLWk3Vma/vcSK4Bn7gsAC719u9omTqUc4e8caNpPorfxNkKhCUVdpZTJ6oKDq90fRsSv4GxXBJeC2+RNRds3m8FxcClmdvA/fXwe69BSiYhB6B8yx/Gon6E23KRwC69ceY+bNC/fuhQsg==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgD9oKFMvaPARUYFAxKtVAXQVDRIr6FlegxuyIY4Ne1vSJhVWPVS5onfTot2kL83NaGIXWshjFtWJD4RB+wrUUAAM1p66TWvD62Kw8huDZIEVyAk0dR2OSkmTBgQ3EBKXO5BFRlgV5TWXUgCC582CDNJFOB2lpePorN1WFxl5RExPLe6xZY9tXQs/P0GJEFKCFoyYvZ+lp6dccjoxrVtVkHewrZnSpFMIYW469TV4umdhDnCD1d/CVP/sI6khoxNGDTZteziCYWWZBZc9jeNQdPU9GHY+TkYydqLqHK7c49CbD7gFEI8Q/UZX12okYThcQjyqeDxAjM8KoAmc15hhkwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://www.kgc.cn/api/payNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://member.gulimall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";


    private String timeout = "10m";
    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();



        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
//                + "\"timeout_express\":\" +timeout +\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
