#
# DPKG Finder
# Authors: Rohit Yadav <rohityadav89@gmail.com>
#

FIND_PROGRAM(DPKG
    NAMES dpkg-deb
    PATHS "/usr/bin") #Add paths here

IF ( DPKG )
    GET_FILENAME_COMPONENT(DPKG_PATH ${DPKG} ABSOLUTE)
    MESSAGE(STATUS "Found dpkg-deb : ${DPKG_PATH}")
    SET(DPKG_FOUND "YES")
ELSE ( DPKG ) 
    MESSAGE(STATUS "dpkg-deb NOT found. deb generation will not be available")
    SET(DPKG_FOUND "NO")
ENDIF ( DPKG )
