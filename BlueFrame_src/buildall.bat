set path=c:\windows\system32;c:\windows;c:\jdk1.6\bin;c:\wtk22\bin
call cmp.bat
call cmpClient.bat
cd bin
call pre.bat
call jars.bat
cd output
call jarc.bat
cd ..
cd ..
@echo "Build Completed!!!"
