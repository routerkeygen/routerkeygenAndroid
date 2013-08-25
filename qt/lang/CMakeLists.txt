#
# RouterKeygen build system
# Authors: Rui Araújo <ruka.araujo@gmail.com>
#

## RCC file
SET(TS_QRC ${CMAKE_SOURCE_DIR}/lang/resources-lang.qrc)

IF(NOT QT_LUPDATE_EXECUTABLE)
    MESSAGE(WARNING "Could not find lupdate. You won't be able to update translations.")
ENDIF(NOT QT_LUPDATE_EXECUTABLE)

IF(NOT QT_LRELEASE_EXECUTABLE)
    MESSAGE(WARNING "Could not find lrelease. Your build won't contain translations.")
    ## Create an empty RCC file
    FILE(WRITE ${TS_QRC} "<RCC></RCC>")
ENDIF(NOT QT_LRELEASE_EXECUTABLE)

## Wraps the supplied .ts files in lrelease commands
MACRO(QT4_ADD_TRANSLATIONS outfiles)
    FILE(WRITE ${TS_QRC} "<RCC><qresource prefix=\"lang\">\n")
    FOREACH (it ${ARGN})
        GET_FILENAME_COMPONENT(it ${it} ABSOLUTE)
        GET_FILENAME_COMPONENT(outfile ${it} NAME_WE)

        FILE(APPEND ${TS_QRC} "<file>${outfile}.qm</file>\n")

        SET(outfile ${CMAKE_SOURCE_DIR}/lang/${outfile}.qm)

        ADD_CUSTOM_COMMAND(
            OUTPUT ${outfile}
            COMMAND ${QT_LRELEASE_EXECUTABLE}
            ARGS -compress -silent -nounfinished ${it} -qm ${outfile}
            MAIN_DEPENDENCY ${it}
        )

        SET(${outfiles} ${${outfiles}} ${outfile})
    ENDFOREACH(it)
    FILE(APPEND ${TS_QRC} "</qresource></RCC>")
ENDMACRO(QT4_ADD_TRANSLATIONS)

## Set available translations file ids here
OPTION(LANGUAGES "Using translations")
SET(ALL_LANGUAGES
        en
        pt
	nl
	es # add here more lang tokens like jp, ro etc. as per ts files
   )

## Check if user has provided specific LANGS option
IF(NOT DEFINED LANGS)
    SET(LANGUAGES ${ALL_LANGUAGES} CACHE STRING "Using translations" FORCE)
ELSE(NOT DEFINED LANGS)
    IF(NOT LANGS)
        SET(LANGUAGES "" CACHE STRING "Using translations" FORCE)
    ELSEIF(LANGS STREQUAL *)
        SET(LANGUAGES ${ALL_LANGUAGES} CACHE STRING "Using translations" FORCE)
    ELSE(NOT LANGS)
        STRING(REGEX MATCHALL [a-zA-Z_]+
               langs1 ${LANGS})
        SET(LANGUAGES ${langs1} CACHE STRING "Using translations" FORCE)
    ENDIF(NOT LANGS)
ENDIF(NOT DEFINED LANGS)

## Display what translations files will be processed
MESSAGE(STATUS "Translations: ${LANGUAGES}")

## Collect all translations files that are to be processed
FOREACH(LANGUAGE ${LANGUAGES})
    FILE(GLOB temp_TS ${CMAKE_SOURCE_DIR}/lang/routerkeygen_*${LANGUAGE}*.ts)
    LIST(APPEND routerkeygen_TS ${temp_TS})
ENDFOREACH()

IF(QT_LRELEASE_EXECUTABLE)
    ## Wraps the collected translations
    QT4_ADD_TRANSLATIONS(routerkeygen_QMS ${routerkeygen_TS})

    ## Create a target that runs lrelease for all the .ts files
    ## "ALL" means that it will be run by default.
    ADD_CUSTOM_TARGET(translations ALL DEPENDS ${routerkeygen_QMS})
ENDIF(QT_LRELEASE_EXECUTABLE)

IF(QT_LUPDATE_EXECUTABLE)
    ## Search for all translatable strings in the sources directory
    FILE(GLOB_RECURSE translate_SRCS ${CMAKE_SOURCE_DIR}/src/*.cpp
                                 ${CMAKE_SOURCE_DIR}/src/*.hpp)
    FILE(GLOB_RECURSE translate_HDRS ${CMAKE_SOURCE_DIR}/src/*.h)
    FILE(GLOB_RECURSE translate_UIS ${CMAKE_SOURCE_DIR}/src/*.ui)

    SET(translate_SRCS ${translate_SRCS} ${translate_HDRS} ${translate_UIS})

    ## Create a target that runs lupdate for all the sources and UI files
    ADD_CUSTOM_TARGET(lupdate
        COMMAND ${QT_LUPDATE_EXECUTABLE} -noobsolete ${translate_SRCS} -ts ${routerkeygen_TS}
    )
ENDIF(QT_LUPDATE_EXECUTABLE)

