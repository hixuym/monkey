keytool -keystore keystore -alias Monkey -genkey -keyalg RSA -sigalg SHA256withRSA

1、keytool -genkeypair -alias Monkey -keyalg RSA -validity 36500 -keystore monkey.keystore

2、keytool -list -v -keystore monkey.keystore

3、keytool -export -alias Monkey -keystore monkey.keystore -rfc -file monkey.cer

4、Keytool -import -alias Monkey -file monkey.cer -keystore monkey.truststore

5、keytool -list -v -keystore monkey.truststore

https://www.eclipse.org/jetty/documentation/current/configuring-ssl.html





