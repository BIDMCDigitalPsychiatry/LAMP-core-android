// JNI bindings for digital.lamp.mindlamp.sensor.stream.OpenZlNative.
//
// Every method returns a negative LAMP_OZL_ERR_* code on failure. When the
// library is built without OpenZL (32-bit ABIs), isAvailable() returns false
// and the other methods return LAMP_OZL_ERR_UNAVAILABLE.

#include <jni.h>
#include <stdint.h>
#include <stdlib.h>

#define LAMP_OZL_ERR_UNAVAILABLE -5

#if LAMP_HAVE_OPENZL
#include "lamp_ozl.h"
#endif

JNIEXPORT jboolean JNICALL
Java_digital_lamp_mindlamp_sensor_stream_OpenZlNative_isAvailable(JNIEnv* env, jclass clazz) {
  (void)env;
  (void)clazz;
  return LAMP_HAVE_OPENZL ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_digital_lamp_mindlamp_sensor_stream_OpenZlNative_formatVersion(JNIEnv* env, jclass clazz) {
  (void)env;
  (void)clazz;
#if LAMP_HAVE_OPENZL
  return LAMP_OZL_FORMAT_VERSION;
#else
  return LAMP_OZL_ERR_UNAVAILABLE;
#endif
}

// Compresses packed 20-byte records. Returns the compressed bytes, or null
// with the error code stored in errorOut[0].
JNIEXPORT jbyteArray JNICALL
Java_digital_lamp_mindlamp_sensor_stream_OpenZlNative_compress(JNIEnv* env, jclass clazz,
                                                              jbyteArray input,
                                                              jintArray errorOut) {
  (void)clazz;
  jint err = LAMP_OZL_ERR_UNAVAILABLE;
  jbyteArray result = NULL;
#if LAMP_HAVE_OPENZL
  jsize n = (*env)->GetArrayLength(env, input);
  jbyte* src = (*env)->GetByteArrayElements(env, input, NULL);
  size_t cap = lamp_ozl_compress_bound((size_t)n);
  void* dst = malloc(cap);
  if (src == NULL || dst == NULL) {
    err = LAMP_OZL_ERR_ARGUMENT;
  } else {
    int64_t written = lamp_ozl_compress_accel(src, (size_t)n, dst, cap);
    if (written < 0) {
      err = (jint)written;
    } else {
      result = (*env)->NewByteArray(env, (jsize)written);
      if (result != NULL) {
        (*env)->SetByteArrayRegion(env, result, 0, (jsize)written, (const jbyte*)dst);
        err = 0;
      }
    }
  }
  if (src != NULL) (*env)->ReleaseByteArrayElements(env, input, src, JNI_ABORT);
  free(dst);
#else
  (void)input;
#endif
  if (errorOut != NULL && (*env)->GetArrayLength(env, errorOut) > 0) {
    (*env)->SetIntArrayRegion(env, errorOut, 0, 1, &err);
  }
  return result;
}

// Decompresses one frame. Returns the raw bytes, or null with the error code
// stored in errorOut[0].
JNIEXPORT jbyteArray JNICALL
Java_digital_lamp_mindlamp_sensor_stream_OpenZlNative_decompress(JNIEnv* env, jclass clazz,
                                                                jbyteArray input,
                                                                jintArray errorOut) {
  (void)clazz;
  jint err = LAMP_OZL_ERR_UNAVAILABLE;
  jbyteArray result = NULL;
#if LAMP_HAVE_OPENZL
  jsize n = (*env)->GetArrayLength(env, input);
  jbyte* src = (*env)->GetByteArrayElements(env, input, NULL);
  void* dst = NULL;
  if (src == NULL) {
    err = LAMP_OZL_ERR_ARGUMENT;
  } else {
    int64_t size = lamp_ozl_decompressed_size(src, (size_t)n);
    if (size < 0) {
      err = (jint)size;
    } else {
      dst = malloc(size > 0 ? (size_t)size : 1);
      int64_t got = dst == NULL ? LAMP_OZL_ERR_ARGUMENT
                                : lamp_ozl_decompress(src, (size_t)n, dst, (size_t)size);
      if (got < 0) {
        err = (jint)got;
      } else {
        result = (*env)->NewByteArray(env, (jsize)got);
        if (result != NULL) {
          (*env)->SetByteArrayRegion(env, result, 0, (jsize)got, (const jbyte*)dst);
          err = 0;
        }
      }
    }
  }
  if (src != NULL) (*env)->ReleaseByteArrayElements(env, input, src, JNI_ABORT);
  free(dst);
#else
  (void)input;
#endif
  if (errorOut != NULL && (*env)->GetArrayLength(env, errorOut) > 0) {
    (*env)->SetIntArrayRegion(env, errorOut, 0, 1, &err);
  }
  return result;
}
