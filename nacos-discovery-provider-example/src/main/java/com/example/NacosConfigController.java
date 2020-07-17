package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 偷懒不想多写一个demo来测试，就把测试写这里了，测试 nacos config
 * @author Fatal
 * @date 2020/6/19
 */
//@RestController
//@RefreshScope
public class NacosConfigController {

    @Value("${user.birthday:1997-10-21}")
    private String birthday;

    @GetMapping("/config/extension")
    public String shareConfig() {
        return "The birthday of user in the extension.yml configuration is " + birthday;
    }

}
