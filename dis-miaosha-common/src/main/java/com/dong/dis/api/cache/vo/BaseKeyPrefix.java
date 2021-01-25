package com.dong.dis.api.cache.vo;

/**
 * 模板方法的基本类
 * @author dong
 */
public abstract class BaseKeyPrefix implements KeyPrefix  {
    /**
     * 过期时间
     */
    int expireSeconds;
    /**
     * 前缀
     */
    String prefix;

    /**
     * 默认过期时间为0，即不过期。过期时间收到redis缓存策略的影响
     * @param prefix
     */
    public BaseKeyPrefix(String prefix){
        this(0,prefix);
    }
    public BaseKeyPrefix(int expireSeconds,String prefix){
        this.prefix = prefix;
        this.expireSeconds = expireSeconds;
    }
    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    /**
     * 前缀为模板类的实现类的类名
     * @return
     */
    @Override
    public String getPrefix() {
        String simpleName = getClass().getSimpleName();
        return simpleName+":"+prefix;
    }
}
