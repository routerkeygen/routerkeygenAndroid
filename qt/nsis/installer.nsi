; CMakeQt NSIS Config script
;
; NSIS Script by:
; Ludovic Fauvet <etix@l0cal.com>
; Rohit Yadav <rohityadav89@gmail.com>

!include "FileAssociation.nsh"

;--------------------------------
; General

; Name
Name "RouterKeygen"

; Output file
OutFile "@NSIS_OUTPUT_FILE@"

; Get installation folder from registry if available
InstallDirRegKey HKLM "Software\@PROJECT_NAME_SHORT@" "Install_Dir"

; Install directory
InstallDir "$PROGRAMFILES\@PROJECT_NAME_SHORT@"

; Request admin permissions for Vista and higher
RequestExecutionLevel admin

; Compression method
SetCompressor /SOLID lzma

;--------------------------------
; Interface

; Warn the user if he try to close the installer
!define MUI_ABORTWARNING

LicenseText "License"
LicenseData "@CMAKE_SOURCE_DIR@/COPYING"

;--------------------------------
; Pages

; Install
Page license
Page components
Page directory
Page instfiles

; Uninstall
UninstPage uninstConfirm
UninstPage instfiles

;--------------------------------
; Installer sections

Section "@PROJECT_NAME_SHORT@ (required)"

  SectionIn RO

  ; Set output path to the installation directory.
  SetOutPath $INSTDIR

  ; Put file there
  File "routerkeygen.exe"
  File "@CMAKE_SOURCE_DIR@/share/routerkeygen.ico"
  File "@CMAKE_SOURCE_DIR@/COPYING"
  File "@CMAKE_SOURCE_DIR@/AUTHORS"
  File "@CMAKE_SOURCE_DIR@/NEWS"

  ; Write the installation path into the registry
  WriteRegStr HKLM "Software\@PROJECT_NAME_SHORT@" "Install_Dir" "$INSTDIR"

  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\@PROJECT_NAME_SHORT@" "DisplayName" "@PROJECT_NAME_LONG@"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\@PROJECT_NAME_SHORT@" "DisplayIcon" '"$INSTDIR\routerkeygen.exe"'
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\@PROJECT_NAME_SHORT@" "DisplayVersion" "@PROJECT_VERSION@"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\@PROJECT_NAME_SHORT@" "Publisher" "Rui Araújo"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\@PROJECT_NAME_SHORT@" "URLInfoAbout" "http://code.google.com/p/android-thomson-key-solver/"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\@PROJECT_NAME_SHORT@" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\@PROJECT_NAME_SHORT@" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\@PROJECT_NAME_SHORT@" "NoRepair" 1
  WriteUninstaller "uninstall.exe"

  ; File association, put here:
  ;${registerExtension} "$INSTDIR\routerkeygen.exe" ".bbf" "Bla bla file format"

SectionEnd

Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\@PROJECT_NAME_LONG@"
  CreateShortCut "$SMPROGRAMS\@PROJECT_NAME_LONG@\@PROJECT_NAME_SHORT@.lnk" "$INSTDIR\routerkeygen.exe" "" "$INSTDIR\routerkeygen.ico" 0
  CreateShortCut "$SMPROGRAMS\@PROJECT_NAME_LONG@\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0

SectionEnd

Section "Desktop Shortcut"

  CreateShortCut "$DESKTOP\@PROJECT_NAME_LONG@.lnk" "$INSTDIR\routerkeygen.exe" "" "$INSTDIR\routerkeygen.ico" 0

SectionEnd

;--------------------------------
; Uninstaller sections

Section "Uninstall"

  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\@PROJECT_NAME_SHORT@"
  DeleteRegKey HKLM "Software\@PROJECT_NAME_SHORT@"

  ; Remove files and uninstaller
  Delete "$INSTDIR\routerkeygen.exe"
  Delete "$INSTDIR\routerkeygen.ico"
  Delete "$INSTDIR\uninstall.exe"
  Delete "$INSTDIR\COPYING"
  Delete "$INSTDIR\AUTHORS"
  Delete "$INSTDIR\NEWS"

  ; Remove shortcuts, if any
  Delete "$SMPROGRAMS\@PROJECT_NAME_LONG@\*.*"
  Delete "$DESKTOP\@PROJECT_NAME_LONG@.lnk"

  ; Remove directories used
  RMDir "$SMPROGRAMS\@PROJECT_NAME_LONG@"
  RMDir "$INSTDIR"

  ; Remove file association
  ;${unregisterExtension} ".bbf" "Bla bla file format"

SectionEnd
