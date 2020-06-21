Keytool生成jks证书



keytool工具说明

```bash
keytool -genkey -alias oauth2_key_gen（别名） 
-keypass levia0738（私钥密码） 
-keyalg RSA（算法） 
-sigalg sha256withrsa（算法小类） 
-keysize 1024（密钥长度） 
-validity 3650（有效期）
-keystore d:/test.jks（生成路径） 
-storepass levia6246（主密码）

```

命令

```bash
keytool -genkey -alias oauth2_key_gen -keypass levia0738 -keyalg RSA -sigalg sha256withrsa -keysize 1024 -validity 3650 -keystore d:/test.jks -storepass levia6246
```

