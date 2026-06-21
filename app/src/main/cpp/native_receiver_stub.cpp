#include <jni.h>
#include <string>
#include <atomic>
#include <thread>
#include <chrono>

static std::atomic<bool> g_running(false);
static std::atomic<int> g_state(0);
// 0 = parado
// 1 = iniciando
// 2 = rodando
// 3 = parando

extern "C"
JNIEXPORT jstring JNICALL
Java_com_allan_airmirroring_MainActivity_nativeReceiverVersion(
        JNIEnv* env,
        jobject /* this */) {
    std::string text = "Core nativo pronto (stub JNI)";
    return env->NewStringUTF(text.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_allan_airmirroring_MainActivity_nativeStartReceiver(
        JNIEnv* env,
        jobject /* this */) {
    if (g_running.load()) return;

    g_state.store(1);
    std::this_thread::sleep_for(std::chrono::milliseconds(300));

    g_running.store(true);
    g_state.store(2);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_allan_airmirroring_MainActivity_nativeStopReceiver(
        JNIEnv* env,
        jobject /* this */) {
    if (!g_running.load()) {
        g_state.store(0);
        return;
    }

    g_state.store(3);
    std::this_thread::sleep_for(std::chrono::milliseconds(300));

    g_running.store(false);
    g_state.store(0);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_allan_airmirroring_MainActivity_nativeIsRunning(
        JNIEnv* env,
        jobject /* this */) {
    return g_running.load() ? JNI_TRUE : JNI_FALSE;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_allan_airmirroring_MainActivity_nativeStatusText(
        JNIEnv* env,
        jobject /* this */) {
    std::string text;

    switch (g_state.load()) {
        case 0:
            text = "Status nativo: parado";
            break;
        case 1:
            text = "Status nativo: iniciando";
            break;
        case 2:
            text = "Status nativo: rodando";
            break;
        case 3:
            text = "Status nativo: parando";
            break;
        default:
            text = "Status nativo: desconhecido";
            break;
    }

    return env->NewStringUTF(text.c_str());
}