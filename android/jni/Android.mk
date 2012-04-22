LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
include $(LOCAL_PATH)/android-config.mk


LOCAL_SRC_FILES := \
        sha/sha1-armv4-large.S \
	sha/sha1dgst.c \
	thomson.c \
	thomsonDic.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_CFLAGS :=  -DNO_WINDOWS_BRAINDEATH -DOPENSSL_BN_ASM_MONT -DSHA1_AS
LOCAL_LDLIBS := -llog

LOCAL_MODULE:= thomson

include $(BUILD_SHARED_LIBRARY)
