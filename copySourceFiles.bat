C:\PROGRA~1\PuTTY\putty.exe -load hortonworks -pw hadoop -m deleteRemoteSourceFiles.txt

pscp.exe -pw hadoop src/cloud/* root@127.0.0.1:/root/distributedKnapsack/src/cloud