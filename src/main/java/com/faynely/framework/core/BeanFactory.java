package com.faynely.framework.core;

/**
 * @author NickFayne 2018-05-20 10:19
 */
public interface BeanFactory {

    /**
     * 从 IOC 容器中获取一个实例 Bean
     * @param name
     * @return
     */
    Object getBean(String name);
}