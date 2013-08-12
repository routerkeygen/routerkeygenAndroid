#
# RouterKeygen build system
# Author: Rui Araújo <ruka.araujo AT gmail.com>
#

#Put here CPPs

file(GLOB ROUTERKEYGEN_SRCS
    "algorithms/*.cpp"
    "config/*.cpp"
    "qcmdlineparser/*.cpp"
    "dialog/*.cpp"
)

LIST( APPEND ROUTERKEYGEN_SRCS
    main.cpp
    RouterKeygen.cpp
    wifi/QWifiManagerPrivate.cpp
    wifi/QWifiManager.cpp
    KeygenThread.cpp
    WirelessMatcher.cpp
    sha/sha256.cpp
)


file(GLOB ROUTERKEYGEN_HDRS
    "algorithms/*.h"
    "config/*.h"
    "dialog/*.h"
    "qcmdlineparser/*.h"
)
LIST( APPEND ROUTERKEYGEN_HDRS
    RouterKeygen.h
    wifi/QWifiManagerPrivate.h
    wifi/QWifiManager.h
    wifi/QScanResult.h
    sha/sha256.h
    KeygenThread.h
    WirelessMatcher.h
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
  .
)

CONFIGURE_FILE(${CMAKE_SOURCE_DIR}/cmake/version.h.cmake ${CMAKE_CURRENT_BINARY_DIR}/version.h)


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


ADD_EXECUTABLE(routerkeygen ${GUI_TYPE} ${ROUTERKEYGEN_SRCS} ${ROUTERKEYGEN_HDRS} ${ROUTERKEYGEN_MOC_SRCS} ${ROUTERKEYGEN_UIS_H} ${ROUTERKEYGEN_RCC_SRCS} )

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

