#include "demo.h"
#include "jniUtils.h"
#include "javaObject.h"

static JavaVM* gJavaVm;
static jobject gJavaObj;

void callJavaMethod(JNIEnv *env,jobject jobj){
    printCurrentTime();

    //1.找到要回调的类
    jclass clazz = env->FindClass(JAVA_QiNative_CLASS);
    if(NULL == clazz){
        LOGD("find class QiNative is null");
        return;
    }

    //2.找到类中的方法
    jmethodID javaCallback = env->GetMethodID(clazz,"methodCalledByJni","(Ljava/lang/String;)V");
    if(NULL == javaCallback){
        LOGD("find methodCalledByJni method is null");
        return;
    }

    //3.调用对象的实例方法
    jstring msg = env->NewStringUTF("callJavaMethod in JinMain.cpp");
    env->CallVoidMethod(jobj,javaCallback,msg);

    //4. 删除局部引用
    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(jobj);
    env->DeleteLocalRef(msg);

    printCurrentTime();
}

void callJavaStaticMethod(JNIEnv *env){

    //1.找到要回调的类
    jclass clazz = env->FindClass(JAVA_QiNative_CLASS);
    if(NULL == clazz){
        LOGD("find class QiNative is null");
        return;
    }

    //2.找到类中的方法
    jmethodID javaCallback = env->GetStaticMethodID(clazz,"staticMethodCalledByJni","(Ljava/lang/String;)V");
    if(NULL == javaCallback){
        LOGD("find methodCalledByJni method is null");
        return;
    }

    //3.调用对象的实例方法
    jstring msg = env->NewStringUTF("callJavaStaticMethod in JinMain.cpp");
    env->CallStaticVoidMethod(clazz,javaCallback,msg);

    //4. 删除局部引用
    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(msg);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_tencent_cmake_jni_QiNative_getIntFromJni(JNIEnv *env, jobject instance) {
    callJavaMethod(env,instance);
    callJavaStaticMethod(env);
    return num(23,100);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_tencent_cmake_jni_QiNative_getStringFromJni(JNIEnv *env, jobject instance) {
    return env->NewStringUTF("来自 jni msg!");
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tencent_cmake_jni_QiNative_init(JNIEnv *env, jclass type) {
    env->GetJavaVM(&gJavaVm);
    gJavaObj = env->NewGlobalRef(type);
    env->DeleteLocalRef(type);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tencent_cmake_jni_QiNative_setPerson(JNIEnv *env, jobject instance, jobject personObj) {
    jclass clazz;
    jfieldID nameField,ageField,heightField;
    jstring name;
    jint age;
    jfloat height;
    const char *c_str = NULL;

    // 1.获取personObj类的Class引用
    clazz = env->GetObjectClass(personObj);
    if (clazz == NULL) {
        return;
    }

    // 2. 获取personObj类实例变量personObj的属性
    nameField = env->GetFieldID(clazz,"name", "Ljava/lang/String;");
    ageField = env->GetFieldID(clazz,"age", "I");
    heightField = env->GetFieldID(clazz,"height", "F");
    if (NULL == nameField || NULL == ageField || NULL == heightField) {
        return;
    }
    //3.获取字段的值
    name = (jstring)env->GetObjectField(personObj,nameField);
    age = (jint)env->GetIntField(personObj,ageField);
    height = (jfloat)env->GetFloatField(personObj,heightField);

    //4.将unicode编码的java字符串转换成C风格字符串
    c_str = env->GetStringUTFChars(name,NULL);

    LOGD("name=%s,age=%d,height=%.2f",c_str,age,height);

    env->DeleteLocalRef(clazz);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_tencent_cmake_jni_QiNative_getPersonFromJni(JNIEnv *env, jobject instance) {
    jclass clazz;
    jfieldID nameField,ageField,heightField;
    jobject personObj;

    clazz = env->FindClass(JAVA_PERSON_CLASS);
    nameField = env->GetFieldID(clazz,"name", "Ljava/lang/String;");
    ageField = env->GetFieldID(clazz,"age", "I");
    heightField = env->GetFieldID(clazz,"height", "F");

    personObj = env->AllocObject(clazz);

    env->SetObjectField(personObj, nameField, env->NewStringUTF("高圆圆"));
    env->SetIntField(personObj, ageField, 35);
    env->SetFloatField(personObj, heightField, 165.664f);

    env->DeleteLocalRef(clazz);
    return personObj;
}
