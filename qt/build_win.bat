echo "Start Windows build"

mkdir win
cd win

cmake -G "MinGW Makefiles" -DCMAKE_BUILD_TYPE=Release -DQT_QMAKE_EXECUTABLE=C:\Qt\4.8.5\bin\qmake.exe ..

mingw32-make VERBOSE=1

cd ..