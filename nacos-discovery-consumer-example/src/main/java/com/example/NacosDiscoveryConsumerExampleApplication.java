package com.example;

import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Nacos 底层已经集成了 Ribbon
 * RibbonNacosAutoConfiguration 配置正常生效，但是该配置使用 @AutoConfigurationAfter 要求在 RibbonAutoConfiguration 之后自动配置；
 * 而 RibbonAutoConfiguration 也使用了 @AutoConfigurationAfter 要求在 EurekaClientAutoConfiguration 之后自动配置，但是新方案是没有
 * Eureka 的，所以得出的结论是 @AutoConfigurationAfter 指定的类如果不存在，那么它可以跳过这个要求进行下一个判断。（不过，源码还没找到
 * 具体实现）
 *
 * Ribbon 默认规则为区域回避，实现类为 ZoneAvoidanceRule，这个类继承了 PredicateBasedRule， PredicateBasedRule 又继承了 ClientConfigEnabledRoundRobinRule， ClientConfigEnabledRoundRobinRule 底层默认实现的规则又是 RoundRobinRule（轮询规则），不过具体轮询方法是使用 PredicateBasedRule 的 com.netflix.loadbalancer.PredicateBasedRule#choose(java.lang.Object)
 * 初始化负载均衡默认策略的具体实现在 org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration#ribbonRule(com.netflix.client.config.IClientConfig)，在第一次调用 FeignClient 时触发初始化，作为 Bean 被初始化到 Spring 容器中
 * RibbonClientConfiguration 在第一次调用 FeignClient 时触发初始化，作为 配置Bean 被初始化到 Spring 容器中
 * org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration#ribbonLoadBalancer(com.netflix.client.config.IClientConfig, com.netflix.loadbalancer.ServerList, com.netflix.loadbalancer.ServerListFilter, com.netflix.loadbalancer.IRule, com.netflix.loadbalancer.IPing, com.netflix.loadbalancer.ServerListUpdater)
 * com.netflix.loadbalancer.ZoneAwareLoadBalancer#ZoneAwareLoadBalancer(com.netflix.client.config.IClientConfig, com.netflix.loadbalancer.IRule, com.netflix.loadbalancer.IPing, com.netflix.loadbalancer.ServerList, com.netflix.loadbalancer.ServerListFilter, com.netflix.loadbalancer.ServerListUpdater)
 * com.netflix.loadbalancer.DynamicServerListLoadBalancer#DynamicServerListLoadBalancer(com.netflix.client.config.IClientConfig, com.netflix.loadbalancer.IRule, com.netflix.loadbalancer.IPing, com.netflix.loadbalancer.ServerList, com.netflix.loadbalancer.ServerListFilter, com.netflix.loadbalancer.ServerListUpdater)
 * com.netflix.loadbalancer.BaseLoadBalancer#BaseLoadBalancer(com.netflix.client.config.IClientConfig, com.netflix.loadbalancer.IRule, com.netflix.loadbalancer.IPing)
 * com.netflix.loadbalancer.BaseLoadBalancer#initWithConfig(com.netflix.client.config.IClientConfig, com.netflix.loadbalancer.IRule, com.netflix.loadbalancer.IPing, com.netflix.loadbalancer.LoadBalancerStats)
 * com.netflix.loadbalancer.ZoneAwareLoadBalancer#setRule(com.netflix.loadbalancer.IRule)
 * com.netflix.loadbalancer.BaseLoadBalancer#setRule(com.netflix.loadbalancer.IRule)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class NacosDiscoveryConsumerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosDiscoveryConsumerExampleApplication.class, args);
    }

    @FeignClient(name = "service-provider", fallback = EchoServiceFallbackImpl.class)
    public interface EchoService {

        @GetMapping("/echo/{str}")
        String echo(@PathVariable("str") String str);

        @GetMapping("divide")
        String divide(@RequestParam("a") Integer a, @RequestParam("b") Integer b);

        default String divide(Integer a) {
            return divide(a, 0);
        }

        @GetMapping("/notFound")
        String notFound();
    }

    @Component
    public
    class EchoServiceFallbackImpl implements EchoService {

        @Override
        public String echo(String str) {
            return "echo fallback";
        }

        @Override
        public String divide(Integer a, Integer b) {
            return "divide fallback";
        }

        @Override
        public String notFound() {
            return "notFound fallback";
        }
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        // 目的是
        return args -> {
            System.out.println("来看看 ApplicationContext 里边的 Bean：");
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            Arrays.stream(beanNames).forEach(System.out::println);
        };
    }

}
