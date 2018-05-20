package com.faynely.framework.beans;

/**
 * @author NickFayne 2018-05-20 10:36
 */
public class BeanWrapper {

    //还会用到一个 观察者 模式
    //1. 支持时间响应，会有一个事件监听。
    private BeanPostProcessor beanPostProcessor;

    private Object wrappedInstance;

    /**
     * 最原始的对象，直接反射 new 出来的
     */
    private Object originalInstance;

    public BeanWrapper(Object instance){
        this.wrappedInstance = instance;
        this.originalInstance = instance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    /**
     * 返回代理之后的 Class
     * @return
     */
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }

    public BeanPostProcessor getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public void setBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessor = beanPostProcessor;
    }
}
