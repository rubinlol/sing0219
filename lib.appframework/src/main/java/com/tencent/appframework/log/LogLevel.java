package com.tencent.appframework.log;

public interface LogLevel {
    /**
     * 日志级别：详尽——俗称“啰嗦”
     */
    public static final int VERBOSE = 1;
    /**
     * 日志级别：调试
     */
    public static final int DEBUG = 2;
    /**
     * 日志级别：信息
     */
    public static final int INFO = 4;
    /**
     * 日志级别：警告
     */
    public static final int WARN = 8;
    /**
     * 日志级别：错误
     */
    public static final int ERROR = 16;
    /**
     * 日志级别：断言
     */
    public static final int ASSERT = 32;

    /**
     * 日志级别：详尽以上的所有级别（不含详尽）
     */
    public static final int ABOVE_VERBOSE = DEBUG | INFO | WARN | ERROR | ASSERT;
    /**
     * 日志级别：调试及调试以上的级别
     */
    public static final int DEBUG_AND_ABOVE = ABOVE_VERBOSE;
    /**
     * 日志级别：调试以上的所有级别（不含调试）
     */
    public static final int ABOVE_DEBUG = INFO | WARN | ERROR | ASSERT;

    /**
     * 日志级别：信息及信息以上的级别
     */
    public static final int INFO_AND_ABOVE = ABOVE_VERBOSE;
    /**
     * 日志级别：信息以上的所有级别（不含信息）
     */
    public static final int ABOVE_INFO = WARN | ERROR | ASSERT;

    /**
     * 日志级别：警告及警告以上的级别
     */
    public static final int WARN_AND_ABOVE = ABOVE_INFO;
    /**
     * 日志级别：警告以上的所有级别（不含警告）
     */
    public static final int ABOVE_WARN = ERROR | ASSERT;

    /**
     * 日志级别：所有级别（详尽、调试、信息、警告、错误和断言）
     */
    public static final int ALL = VERBOSE | DEBUG | INFO | WARN | ERROR | ASSERT;

}
