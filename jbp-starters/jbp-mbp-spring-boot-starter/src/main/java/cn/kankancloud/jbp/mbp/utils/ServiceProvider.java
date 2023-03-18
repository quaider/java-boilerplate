package cn.kankancloud.jbp.mbp.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ServiceProvider implements ApplicationContextAware, ApplicationEventPublisherAware {

    private static ApplicationContext applicationContext;
    private static ApplicationEventPublisher applicationEventPublisher;

    @SuppressWarnings("unchecked")
    public static <T> T getService(String serviceId) {
        if (serviceId == null) {
            return null;
        }

        return (T) applicationContext.getBean(serviceId);
    }

    public static <T> T getService(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * get services of some type
     *
     * @param tClass type of service
     * @param <T>    type of service
     * @return a Map with the matching beans, containing the bean names as keys and the corresponding bean instances as values
     */
    public static <T> Map<String, T> getServicesOfType(Class<T> tClass) {
        return applicationContext.getBeansOfType(tClass);
    }

    public static ApplicationEventPublisher getEventBus() {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ServiceProvider.applicationContext = applicationContext; // NOSONAR
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        ServiceProvider.applicationEventPublisher = applicationEventPublisher; // NOSONAR
    }
}
