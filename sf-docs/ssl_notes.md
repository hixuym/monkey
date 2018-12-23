keytool -keystore keystore -alias sf -genkey -keyalg RSA -sigalg SHA256withRSA

https://www.eclipse.org/jetty/documentation/current/configuring-ssl.html

keytool -genkeypair -alias sunflower -keyalg RSA -validity 36500 -keystore sunflower.jks

keytool -list -v -keystore sunflower.jks

keytool -export -alias sunflower -keystore sunflower.jks -rfc -file sunflower.cer

Keytool -import -alias sunflower -file sunflower.cer -keystore sunflower_truststore.jks

keytool -list -v -keystore sunflower_truststore.jks




