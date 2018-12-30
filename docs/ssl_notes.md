keytool -keystore keystore -alias mk -genkey -keyalg RSA -sigalg SHA256withRSA

https://www.eclipse.org/jetty/documentation/current/configuring-ssl.html

keytool -genkeypair -alias monkey -keyalg RSA -validity 36500 -keystore monkey.jks

keytool -list -v -keystore monkey.jks

keytool -export -alias monkey -keystore monkey.jks -rfc -file monkey.cer

Keytool -import -alias monkey -file monkey.cer -keystore monkey_truststore.jks

keytool -list -v -keystore monkey_truststore.jks




