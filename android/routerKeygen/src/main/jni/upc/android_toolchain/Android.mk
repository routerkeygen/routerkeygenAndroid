MODULE_PATH := $(call my-dir)/..
###################
# UPC key gen    #
###################
include $(CLEAR_VARS)
LOCAL_PATH := $(MODULE_PATH)

# Shared library name
LOCAL_MODULE := upc

# Include self headers
LOCAL_C_INCLUDES += $(LOCAL_PATH)/include

# Source files
JNI_SRC_DIR := src
LOCAL_SRC_FILES := $(JNI_SRC_DIR)/md5.c $(JNI_SRC_DIR)/upc_keys.c $(JNI_SRC_DIR)/upc_ubee.c $(JNI_SRC_DIR)/upc_keys_wrapper.c

# Android logging + dynamic linking
LOCAL_LDLIBS += -llog -ldl

include $(BUILD_SHARED_LIBRARY)

