keytool -genkey -alias sf -keypass 123456 -keyalg RSA -keysize 1024 -validity 365 -keystore D:/sf.keystore -storepass 123456

keytool -genkey -alias sf-client -keypass 123456 -keyalg RSA -keysize 1024 -validity 365 -storetype PKCS12 -keystore D:/sf-client.p12 -storepass 123456

keytool -export -alias sf-client -keystore D:/sf-client.p12 -storetype PKCS12 -keypass 123456 -file D:/sf-client.cer

keytool -import -v -file D:/sf-client.cer -keystore D:/sf.keystore

keytool -list -v -keystore D:/sf.keystore

keytool -keystore D:/sf.keystore -export -alias sf -file D:/sf-server.cer


https://www.cnblogs.com/zhangzb/p/5200418.html