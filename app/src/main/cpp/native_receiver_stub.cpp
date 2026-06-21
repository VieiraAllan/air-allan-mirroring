#include <jni.h>
#include <string>
#include <atomic>
#include <thread>
#include <chrono>

static std::atomic<bool> g_running(false);
static std::atomic<int> g_state(0);

// 0 = parado
// 1 = inicializando
// 2 = aguardando iPhone
// 3 = espelhando (reservado para próxima fase)
// 4 = parando

extern "C"
JNIEXPORT jstring JNICALL
Java_com_allan_airmirroring_NativeReceiverBridge_receiverVersion(
        JNIEnv* env,
        jobject /* this */) {
    std::string text = "Core nativo pronto (JNI)";
    return env->NewStringUTF(text.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_allan_airmirroring_NativeReceiverBridge_startReceiver(
        JNIEnv* env,
        jobject /* this */) {
    if (g_running.load()) return;

    g_state.store(1);
    std::this_thread::sleep_for(std::chrono::milliseconds(250));

    g_running.store(true);
    g_state.store(2);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_allan_airmirroring_NativeReceiverBridge_stopReceiver(
        JNIEnv* env,
        jobject /* this */) {
    if (!g_running.load()) {
        g_state.store(0);
        return;
    }

    g_state.store(4);
    std::this_thread::sleep_for(std::chrono::milliseconds(250));

    g_running.store(false);
    g_state.store(0);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_allan_airmirroring_NativeReceiverBridge_isRunning(
        JNIEnv* env,
        jobject /* this */) {
    return g_running.load() ? JNI_TRUE : JNI_FALSE;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_allan_airmirroring_NativeReceiverBridge_statusText(
        JNIEnv* env,
        jobject /* this */) {
    std::string text;

    switch (g_state.load()) {
        case 0:
            text = "Status nativo: parado";
            break;
        case 1:
            text = "Status nativo: inicializando";
            break;
        case 2:
            text = "Status nativo: aguardando conexão do iPhone";
            break;
        case 3:
            text = "Status nativo: espelhando iPhone";
            break;
        case 4:
            text = "Status nativo: parando";
            break;
        default:
            text = "Status nativo: desconhecido";
            break;
    }

    return env->NewStringUTF(text.c_str());
}