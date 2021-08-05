package com.wj.tfht.rpc.server.register;

import com.wj.tfht.rpc.annotation.InjectService;
import com.wj.tfht.rpc.client.cache.ServerDiscoveryCache;
import com.wj.tfht.rpc.client.net.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

/**
 * @ClassName DefaultRpcProcessor
 * @Description:
 * @Author wanGJ1E
 * @Date 2021/8/5
 * @Version V1.0
 **/
@Slf4j
@Configuration
public class DefaultRpcProcessor implements ApplicationListener<ContextRefreshedEvent> {

    private ProxyFactory proxyFactory;

    /**
     * 通过上下文刷新完成事件。 再找出需要的对象，通过反射属性注入
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Objects.requireNonNull(event.getApplicationContext(), "applicationContext is null...");
        ApplicationContext applicationContext = event.getApplicationContext();

        // 注入Service
        injectService(applicationContext);


    }

    private void injectService(ApplicationContext applicationContext) {
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Class<?> type = applicationContext.getType(name);
            if (Objects.isNull(type)) {
                log.warn(String.format("name：%s , class is null", name));
            }
            Optional.ofNullable(type.getDeclaredFields())
                    .ifPresent(fields -> {
                        for (Field field : fields) {
                            InjectService injectService = field.getAnnotation(InjectService.class);
                            //存在注解，创建服务引用的代理对象
                            Optional.ofNullable(injectService)
                                    .ifPresent(service -> {
                                        Class<?> klass = field.getType();
                                        Object bean = applicationContext.getBean(name);
                                        field.setAccessible(Boolean.TRUE);

                                        try {
                                            field.set(bean, proxyFactory.getProxy(klass));
                                        } catch (IllegalAccessException e) {
                                            log.warn("BeanName: {} ,代理对象属性注入失败", name);
                                            e.printStackTrace();
                                        }
                                        ServerDiscoveryCache.SERVICE_CLASS_NAMES.add(klass.getName());
                                    });

                        }
                    });
        }
    }
}
