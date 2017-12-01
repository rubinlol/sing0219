package com.tencent.appframework.strategy;


/**
 * 下载代理策略类
 */
public enum ProxyStrategy {

    DEFAULT,//默认策略，自动判断是否有代理，有代理则用代理
    FORCE_PROXY,//强制用代理
    NO_PROXY //强制不用代理


}