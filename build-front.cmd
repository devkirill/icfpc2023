pushd front
call npm ci
call npm run build
rd /s /q ..\src\main\resources\static
xcopy build ..\src\main\resources\static /e /i /h
popd
