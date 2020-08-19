package com.atguigu.gulimall.gulimallmember;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

//@SpringBootTest
//@RunWith(SpringRunner.class)
public class GulimallMemberApplicationTests {

    @Test
    public void contextLoads() {
//        String s = DigestUtils.md5Hex("123456");
//        System.out.println(s);


        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //$2a$10$IxhnJo6iOmmSupKlzdn24OiZQrZj6aHEKK3qI0xHmL0CySGokWySu
        //$2a$10$7gUquDZcF/pOmQs9pDoYa.gTe1ALBwW1w5onjK.kBCC7PptkTdDLO
        String encode = passwordEncoder.encode("123456");
        boolean matches = passwordEncoder.matches("123456", "$2a$10$IxhnJo6iOmmSupKlzdn24OiZQrZj6aHEKK3qI0xHmL0CySGokWySu");
        System.out.println(encode + "->" +matches );
    }

}
