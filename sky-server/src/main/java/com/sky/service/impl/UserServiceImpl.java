package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    //微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    UserMapper userMapper;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    WeChatProperties weChatProperties;

    /**
     *  * 根据微信授权码实现微信登录
     *  * @param userLoginDTO
     *  * @return
     *  */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //授权码
        String code = userLoginDTO.getCode();
        String openId = getOpenid(code);

        //如果没有得到openid抛出异常
        if(openId==null){
            System.out.println("openid的值 " + null);
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //根据openid查询到登录的用户
        User user = userMapper.getByOpenId(openId);

        //如果登陆的新的用户那么顺便完成注册
        if(user == null){
            user = User.builder().openid(openId).createTime(LocalDateTime.now()).build();

            userMapper.insert(user);
        }
        return user;
    }


    /**
     *  * 获取微信用户的openid
     *  * @param code
     *  * @return
     *  */
    public String getOpenid(String code){
        //请求参数封装
        Map<String,String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");

        //调用工具类，向微信接口服务发送请求
        String json = HttpClientUtil.doGet(WX_LOGIN,map);
        log.info("微信登录返回结果：{}", json);

        //解析json字符串
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        log.info("微信用户的openid为：{}", openid);
        return openid;
    }
}
