#ifndef _JNI_UTILS_H_
#define _JNI_UTILS_H_

#include <stdlib.h>
#include <jni.h>
#include <time.h>
#include <android/log.h>

#ifdef __cplusplus

extern "C"
{
#endif
#define  LOG_TAG    "QiNative"
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


void printCurrentTime(){
    struct timeval time;
    gettimeofday(&time, NULL); //精度us
    LOGD("took %lld ms", time.tv_sec * 1000 + time.tv_usec /1000);
}

#ifdef __cplusplus
}
#endif

#endif
