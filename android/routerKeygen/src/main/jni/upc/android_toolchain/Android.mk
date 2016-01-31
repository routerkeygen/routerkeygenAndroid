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

ifeq ($(MY_USE_STATIC_SSL),1)
# This is to do builds with full openssl built in.
# Not use unless you know what you do and create a ssl_static target in openssl lib
	LOCAL_STATIC_LIBRARIES += ssl_static crypto_static
	LOCAL_LDLIBS += -lz
else
# Normal mainstream users mode
	LOCAL_STATIC_LIBRARIES += crypto_ec_static
	LOCAL_SHARED_LIBRARIES += libssl libcrypto
endif


LOCAL_STATIC_LIBRARIES += libgcc
include $(BUILD_SHARED_LIBRARY)

