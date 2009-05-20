keytool -genkeypair -dname "CN=DonsProxy Self-Signed Non-secure Certificate, O=moneybender.com" \
 -storepass password -keypass password -keystore ../config/keystore

keytool -list -v -storepass password -keystore ../config/keystore
