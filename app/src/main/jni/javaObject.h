//
// Created by rubinqiu on 2017/11/22.
//

#ifndef CMAKE_OBJECT_H_H
#define CMAKE_OBJECT_H_H

#define JAVA_QiNative_CLASS "com/tencent/cmake/jni/QiNative"
#define JAVA_PERSON_CLASS "com/tencent/cmake/jni/Person"

struct PersonOffsets
{
    jfieldID  name;
    jfieldID  age;
    jfieldID  height;
} gPersonOffsets;

#endif //CMAKE_OBJECT_H_H

