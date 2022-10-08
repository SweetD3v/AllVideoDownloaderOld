#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring  extern "C" jstring
Java_com_demo_bitmapops_CartoonUtils_stringFromJNI(
        JNIEnv *env,
        jobject) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
