# Native library for the sensor-stream codec (libsensorstream.so).
#
# 64-bit ABIs (arm64-v8a, x86_64) link the vendored OpenZL v0.3.0 sources and
# the lamp_ozl shim. 32-bit ABIs build a stub that reports OpenZL as
# unavailable, so the app falls back to zstd (OpenZL does not support 32-bit
# targets). See third_party/openzl/VERSION and scripts/vendor-openzl.sh.

cmake_minimum_required(VERSION 3.22.1)
project(sensorstream C)

set(CMAKE_C_STANDARD 11)
set(CMAKE_C_EXTENSIONS ON)
set(CMAKE_C_VISIBILITY_PRESET hidden)

set(VENDOR ${CMAKE_CURRENT_SOURCE_DIR}/third_party/openzl)

if(ANDROID_ABI MATCHES "^(arm64-v8a|x86_64)$")
  file(GLOB_RECURSE OPENZL_SOURCES CONFIGURE_DEPENDS ${VENDOR}/openzl/src/*.c)
  file(GLOB_RECURSE ZSTD_SOURCES CONFIGURE_DEPENDS ${VENDOR}/zstd/*.c)
  set(LZ4_SOURCES ${VENDOR}/lz4/lz4.c ${VENDOR}/lz4/lz4hc.c)

  add_library(openzl_vendored STATIC ${OPENZL_SOURCES} ${ZSTD_SOURCES} ${LZ4_SOURCES})
  target_include_directories(openzl_vendored PUBLIC
    ${VENDOR}/config ${VENDOR}/openzl ${VENDOR}/openzl/include ${VENDOR}/openzl/src
    ${VENDOR}/zstd ${VENDOR}/lz4)
  target_compile_definitions(openzl_vendored PRIVATE
    _GNU_SOURCE _REENTRANT ZSTD_DISABLE_ASM=1 ZSTD_LEGACY_SUPPORT=0 XXH_NAMESPACE=ZSTD_)
  target_compile_options(openzl_vendored PRIVATE -O2 -w -ffunction-sections -fdata-sections)

  add_library(sensorstream SHARED lamp_ozl.c sensorstream_jni.c)
  target_compile_definitions(sensorstream PRIVATE LAMP_HAVE_OPENZL=1)
  target_link_libraries(sensorstream PRIVATE openzl_vendored)
else()
  add_library(sensorstream SHARED sensorstream_jni.c)
  target_compile_definitions(sensorstream PRIVATE LAMP_HAVE_OPENZL=0)
endif()

target_include_directories(sensorstream PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})
# 16 KB page alignment, required by Google Play for targetSdk 35+.
target_link_options(sensorstream PRIVATE "-Wl,-z,max-page-size=16384" "-Wl,--exclude-libs,ALL" "-Wl,--gc-sections")
