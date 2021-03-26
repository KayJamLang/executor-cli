;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------
;General

  ;Name and file
  Name "KayJamExecutor CLI"
  OutFile "windows.exe"
  Unicode True

  ;Default installation folder
  InstallDir "$PROGRAMFILES\kj-cli"

  ;Request application privileges for Windows Vista
  RequestExecutionLevel admin

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_LICENSE "LICENSE"
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section
  CreateDirectory "$INSTDIR"
  CreateDirectory "$INSTDIR\bin"
  CreateDirectory "$INSTDIR\libs"

  SetOutPath "$INSTDIR\bin"
  File bin\kj-cli.jar

  SetOutPath "$INSTDIR\libs"
  File libs\request-library-0.1.jar
  File libs\json-library-0.1.jar


  SetOutPath "$WINDIR"
  File bin\kj-cli.bat

  nsExec::Exec 'setx KJ_HOME "$INSTDIR" /m'
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;Bin
  Delete "$INSTDIR\bin\kj-cli.jar"
  RMDir "$INSTDIR\bin"

  ;Libs
  Delete "$INSTDIR\libs\request-library-0.1.jar"
  Delete "$INSTDIR\libs\json-library-0.1.jar"
  RMDir "$INSTDIR\libs"

  Delete "$WINDIR\kj-cli.bat"

  ;Other
  Delete "$INSTDIR\Uninstall.exe"
  RMDir "$INSTDIR"
SectionEnd