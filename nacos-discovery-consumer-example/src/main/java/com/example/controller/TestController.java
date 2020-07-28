package com.example.controller;

import com.example.NacosDiscoveryConsumerExampleApplication.EchoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fatal
 * @date 2020/6/11
 */
@RestController
public class TestController {

    @Autowired
    private EchoService echoService;

    @GetMapping("/echo-feign/{str}")
    public String feign(@PathVariable String str) {
        String echo = echoService.echo(str);
        return echo;
    }

}
