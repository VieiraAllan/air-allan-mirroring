#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_allan_airmirroring_MainActivity_nativeReceiverVersion(
        JNIEnv* env,
        jobject /* this */) {
    std::string text = "Core nativo pronto (stub JNI)";
    return env->NewStringUTF(text.c_str());
}