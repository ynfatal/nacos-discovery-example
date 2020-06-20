package com.example.controller;

import com.example.NacosDiscoveryConsumerExampleApplication.EchoService;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
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
    // `配置Bean`RibbonClientConfiguration 是在第一次使用 Feign 的时候初始化的。所以以下两个Bean在这里注入不了
    @Autowired(required = false)
    private ILoadBalancer loadBalancer;
    @Autowired(required = false)
    private IRule rule;

    @GetMapping("/echo-feign/{str}")
    public String feign(@PathVariable String str) {
        String echo = echoService.echo(str);
        return echo;
    }

}
