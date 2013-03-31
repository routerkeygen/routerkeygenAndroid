#
# RouterKeygen build system
# Author: Rui Araújo <ruka.araujo AT gmail.com>
#

#Put here CPPs
SET(ROUTERKEYGEN_SRCS
    main.cpp
    RouterKeygen.cpp
    algorithms/AxtelKeygen.cpp
    algorithms/ZyxelKeygen.cpp
    algorithms/Wlan6Keygen.cpp
    algorithms/Wlan2Keygen.cpp
    algorithms/VerizonKeygen.cpp
    algorithms/ThomsonKeygen.cpp
    algorithms/TelseyKeygen.cpp
    algorithms/TecomKeygen.cpp
    algorithms/TeletuKeygen.cpp
    algorithms/PirelliKeygen.cpp
    algorithms/Skyv1Keygen.cpp
    algorithms/OnoKeygen.cpp
    algorithms/InfostradaKeygen.cpp
    algorithms/InterCableKeygen.cpp
    algorithms/HuaweiKeygen.cpp
    algorithms/EircomKeygen.cpp
    algorithms/DlinkKeygen.cpp
    algorithms/DiscusKeygen.cpp
    algorithms/ComtrendKeygen.cpp
    algorithms/AliceKeygen.cpp
    algorithms/Keygen.cpp
    wifi/QWifiManagerPrivate.cpp
    algorithms/EasyBoxKeygen.cpp
    algorithms/PBSKeygen.cpp
    algorithms/PtvKeygen.cpp
    algorithms/OteKeygen.cpp
    algorithms/OteBAUDKeygen.cpp
    algorithms/MaxcomKeygen.cpp
    algorithms/MegaredKeygen.cpp
    algorithms/AndaredKeygen.cpp
    algorithms/ConnKeygen.cpp
    wifi/QWifiManager.cpp
    KeygenThread.cpp
    WirelessMatcher.cpp
    main.cpp
    sha256.cpp
    algorithms/CabovisaoSagemKeygen.cpp
    config/AliceConfigParser.cpp
    config/AliceMagicInfo.cpp
    config/TeleTuConfigParser.cpp
    config/TeleTuMagicInfo.cpp
    algorithms/WifimediaRKeygen.cpp
    dialog/welcomedialog.cpp
    dialog/AboutDialog.cpp
    config/OTEHuaweiConfigParser.cpp
    algorithms/OteHuaweiKeygen.cpp
    dialog/UpdateDialog.cpp
)
SET(ROUTERKEYGEN_HDRS
    include/RouterKeygen.h
    wifi/QWifiManagerPrivate.h
    include/QWifiManager.h
    include/welcomedialog.h
    include/AboutDialog.h
    include/UpdateDialog.h
    include/ZyxelKeygen.h
    include/Wlan6Keygen.h
    include/Wlan2Keygen.h
    include/VerizonKeygen.h
    include/ThomsonKeygen.h
    include/TelseyKeygen.h
    include/TecomKeygen.h
    include/TeleTuKeygen.h
    include/Skyv1Keygen.h
    include/PirelliKeygen.h
    include/OnoKeygen.h
    include/InfostradaKeygen.h
    include/HuaweiKeygen.h
    include/EircomKeygen.h
    include/DlinkKeygen.h
    include/DiscusKeygen.h
    include/ComtrendKeygen.h
    include/AliceMagicInfo.h
    include/AliceConfigParser.h
    include/TeleTuConfigParser.h
    include/TeleTuMagicInfo.h
    include/AliceKeygen.h
    include/AxtelKeygen.h
    include/EasyBoxKeygen.h
    include/OteKeygen.h
    include/OteBAUDKeygen.h
    include/PBSKeygen.h
    include/PtvKeygen.h
    include/MegaredKeygen.h
    include/MaxcomKeygen.h
    include/AndaredKeygen.h
    include/ConnKeygen.h
    include/QScanResult.h
    include/sha256.h
    include/Keygen.h
    include/KeygenThread.h
    include/WirelessMatcher.h
    include/unknown.h
    include/CabovisaoSagemKeygen.h
    include/WifimediaRKeygen.h
    include/OTEHuaweiConfigParser.h
    include/OteHuaweiKeygen.h
)

set(HEADERS_HAVING_Q_OBJECT)
foreach(header ${MYPROJECT_CURRENT_HEADERS})
    file(STRINGS "${header}" lines REGEX "Q_OBJECT")
    if(lines)
        list(APPEND HEADERS_HAVING_Q_OBJECT "${header}")
    endif()
endforeach()

#Put here QRC files to be Rcc-ed
SET(ROUTERKEYGEN_RCC
    ../resources/resources.qrc
    ../lang/resources-lang.qrc
)

#include paths for routerkeygen
INCLUDE_DIRECTORIES(
  ${CMAKE_CURRENT_BINARY_DIR}
  include 
  .
)

#Put here UI files
SET(ROUTERKEYGEN_UIS
    forms/routerkeygen.ui
    forms/welcome_dialog.ui
    forms/aboutdialog.ui
    forms/UpdateDialog.ui
)



IF (UNIX AND NOT APPLE )
	LIST( APPEND ROUTERKEYGEN_SRCS
		         wifi/QWifiManagerPrivateUnix.cpp )
	LIST( APPEND ROUTERKEYGEN_HDRS
		         wifi/QWifiManagerPrivateUnix.h )
    SET(ROUTERKEYGEN_LIBS ${ROUTERKEYGEN_LIBS} ${QT_QTDBUS_LIBRARY} -lcrypto)
    INCLUDE_DIRECTORIES(/usr/include/NetworkManager)
ENDIF(UNIX AND NOT APPLE )


IF( MINGW )
    # resource compilation for MinGW
    ADD_CUSTOM_COMMAND( OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/icon.o
        COMMAND ${CMAKE_WINDRES} -I${CMAKE_SOURCE_DIR} -i${CMAKE_SOURCE_DIR}/cmake/icon.rc
        -o ${CMAKE_CURRENT_BINARY_DIR}/icon.o )
    SET(ROUTERKEYGEN_SRCS ${ROUTERKEYGEN_SRCS} ${CMAKE_CURRENT_BINARY_DIR}/icon.o)
    SET(LINK_FLAGS -Wl,-subsystem,windows)
ENDIF( MINGW )

IF(WIN32)
    ENABLE_LANGUAGE(ASM)
	LIST( APPEND ROUTERKEYGEN_SRCS
		         wifi/QWifiManagerPrivateWin.cpp 
		         sha1/sha1dgst.c 
		         sha1/sha1-586.win32.S )
	LIST( APPEND ROUTERKEYGEN_HDRS
		         wifi/QWifiManagerPrivateWin.h
				 include/sha_locl.h
				 include/sha.h
				 include/opensslconf.h
				 include/md32_common.h )
    SET(GUI_TYPE WIN32)
ENDIF(WIN32)

IF(APPLE)
	LIST( APPEND ROUTERKEYGEN_SRCS
		         wifi/QWifiManagerPrivateMac.cpp 
		         mac/macloginitemsmanager.mm)
	LIST( APPEND ROUTERKEYGEN_HDRS
		         wifi/QWifiManagerPrivateMac.h
       			 include/macloginitemsmanager.h )
    SET(GUI_TYPE MACOSX_BUNDLE)
    SET(ROUTERKEYGEN_SRCS ${ROUTERKEYGEN_SRCS} ../share/routerkeygen.icns)
    SET_SOURCE_FILES_PROPERTIES(../share/routerkeygen.icns PROPERTIES
        MACOSX_PACKAGE_LOCATION Resources)
    SET(MACOSX_BUNDLE_ICON_FILE routerkeygen.icns)
    FIND_LIBRARY(COCOA_LIBRARY Cocoa)
    FIND_PATH(COCOA_INCLUDE_DIR Cocoa/Cocoa.h)
    SET(ROUTERKEYGEN_LIBS ${ROUTERKEYGEN_LIBS} ${COCOA_LIBRARY} -lcrypto)
ENDIF(APPLE)

QT4_WRAP_UI(ROUTERKEYGEN_UIS_H ${ROUTERKEYGEN_UIS})
QT4_ADD_RESOURCES(ROUTERKEYGEN_RCC_SRCS ${ROUTERKEYGEN_RCC})
QT4_WRAP_CPP(ROUTERKEYGEN_MOC_SRCS ${ROUTERKEYGEN_HDRS})


ADD_EXECUTABLE(routerkeygen ${ROUTERKEYGEN_SRCS} ${ROUTERKEYGEN_MOC_SRCS} ${ROUTERKEYGEN_UIS_H} ${ROUTERKEYGEN_RCC_SRCS} )

# Add translations
ADD_DEPENDENCIES(routerkeygen translations)

# Link the target against the Qt libraries
TARGET_LINK_LIBRARIES(routerkeygen ${ROUTERKEYGEN_LIBS})

# Now we are done.

INSTALL(TARGETS routerkeygen
        BUNDLE  DESTINATION ${ROUTERKEYGEN_BIN_DIR}
        RUNTIME DESTINATION ${ROUTERKEYGEN_BIN_DIR})

ADD_CUSTOM_COMMAND(
    OUTPUT ${CMAKE_SOURCE_DIR}/bin/routerkeygen
    COMMAND ${CMAKE_COMMAND} copy ${CMAKE_CURRENT_SOURCE_DIR}/routerkeygen ${CMAKE_SOURCE_DIR}/bin/routerkeygen
    DEPENDS ${CMAKE_BINARY_DIR}/src/routerkeygen
)

ADD_DEFINITIONS(-W -Wall)

# Creates app bundle in /bin, uncomment #-dmg to create a dmg output 
IF( APPLE )
    ADD_CUSTOM_COMMAND(
        TARGET routerkeygen
        POST_BUILD
        COMMAND macdeployqt ${CMAKE_CURRENT_BINARY_DIR}/../bin/routerkeygen.app -no-plugins #-dmg
        WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
        COMMENT "Deploying the Qt Framework onto the bundle"
    )
ENDIF( APPLE )

