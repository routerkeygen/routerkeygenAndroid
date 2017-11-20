LOCAL_PATH := $(call my-dir)
JNI_PATH := $(LOCAL_PATH)

include $(CLEAR_VARS)
include $(LOCAL_PATH)/android-config.mk

LOCAL_CFLAGS :=  -DNO_WINDOWS_BRAINDEATH -DOPENSSL_BN_ASM_MONT -DSHA1_AS

ifeq ($(TARGET_ARCH_ABI),armeabi)
    LOCAL_SRC_FILES := sha/sha1-armv4-large.S
endif
ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
    LOCAL_SRC_FILES := sha/sha1-armv4-large.S
    LOCAL_LDFLAGS := -Wl,-Bsymbolic
endif
ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
    LOCAL_SRC_FILES := sha/sha1-armv8.S
endif
ifeq ($(TARGET_ARCH_ABI),x86)
    LOCAL_CFLAGS :=$(LOCAL_CFLAGS) -march=i686 -mtune=intel -mssse3 -mfpmath=sse -m32
    LOCAL_SRC_FILES := sha/sha1-586.S
endif
ifeq ($(TARGET_ARCH_ABI),x86_64)
    LOCAL_CFLAGS :=$(LOCAL_CFLAGS) -fno-integrated-as -march=x86-64 -msse4.2 -mpopcnt -m64 -mtune=intel
    LOCAL_SRC_FILES := sha/sha1-x86_64.S
endif

LOCAL_SRC_FILES := $(LOCAL_SRC_FILES) \
	sha/sha1dgst.c \
	thomson.c \
	thomsonDic.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDLIBS := $(LOCAL_LDLIBS) -llog

LOCAL_MODULE:= thomson

include $(BUILD_SHARED_LIBRARY)

include $(JNI_PATH)/upc/android_toolchain/Android.mk
