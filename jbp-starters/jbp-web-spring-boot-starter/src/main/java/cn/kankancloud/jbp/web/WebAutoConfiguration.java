package cn.kankancloud.jbp.web;

import cn.kankancloud.jbp.common.RejectExecutionFactory;
import cn.kankancloud.jbp.core.secure.HttpPrincipalHolder;
import cn.kankancloud.jbp.core.security.context.PrincipalHolder;
import cn.kankancloud.jbp.core.security.context.SupportedPrincipalHolders;
import cn.kankancloud.jbp.core.security.context.ThreadLocalPrincipalHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@ComponentScan(basePackages = "cn.kankancloud.jbp")
@EnableAsync
public class WebAutoConfiguration {

    @Bean
    public List<PrincipalHolder> principalFactories() {
        SupportedPrincipalHolders.register(new HttpPrincipalHolder());
        SupportedPrincipalHolders.register(new ThreadLocalPrincipalHolder());

        return SupportedPrincipalHolders.getPrincipalFactories();
    }

    /**
     * 服务内领域事件处理
     */
    @Bean
    @ConditionalOnMissingBean(name = "eventListenerPool")
    public Executor eventListenerPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 4);
        executor.setAllowCoreThreadTimeOut(true);

        // use SynchronousQueue
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("eventListenerPool-");
        executor.setRejectedExecutionHandler(RejectExecutionFactory.newThreadRun("EventListenerPool"));

        return executor;
    }
}
