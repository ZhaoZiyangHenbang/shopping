package com.shopping.pay;

/**
 * Created by Administrator on 2017/5/11.
 */
public class AlipayConfig {
    // 商户appid
    public static String APPID = "2088621809215011";
    //"2016080700185913";
    // 私钥 pkcs8格式的
    public static String RSA_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMIt/SSHlflcCkgr" +
            "xaXHTZ9/8ChkTOeR2m0jeoAT4YLzyJSvldpmTCXRUMhJI4WiB/657ZD3PYWF//kp" +
            "0bUdHG982fLNp7ziCVYPcJ0afZhkmX4GXs8ZPLEwjs1qHmS2OwC55+iUAmhodVBs" +
            "+SpcJLod+Kt3xvs8DpOhEbiHGCRFAgMBAAECgYAfjIlMnhrn4ORqxXHfdXpPJ+JF" +
            "Lcaw+SmcAd+tX42MUT+NqNmYGnbUziVp5Tg4RhM2d875drIwN41XVkwUOR6dViAY" +
            "yJOBRUO1/CbgXWfHk8OenYohOvk5Ksp7cJqDcJyzESjk6ppdS3FI3Yq9gvMW3kr4" +
            "Ft2e7rtTeBxX57erIQJBAOBOHvEywJ0qTCoQke/QN1IW/5a0ffw8kbzWmuS2r5tp" +
            "nd3FChlXhD7Ate4dYssiRaGorPMXv7umI7zF0/lmPRsCQQDdniAz3fTxFcbGGYoy" +
            "wH445nOjMY8dhLxNW1dgJBmfJGMajOSMw7SEI0kqVgg/QIk5hwqXZW2Yx8h58FcW" +
            "phofAkBDVC/Fhs56Xq/uEHs92OpCIdUmj5chl6zHWkO0U4HyzIkugG6/nJ19mKsD" +
            "hp6ZCviLxe3kKkTLNtuTVm6CdkXxAkAjU8SQYA5eq/j+tS2Jf5PfYCUI26qTqwgV" +
            "UL0x7y9CZhuCLPl10vCgq7tLC2HPK2t0hylSDvIUi6xSe9R2IM5DAkA/vgbmd5AE" +
            "8QMe+JHCPv6kw8VMNIz9TLdU7mevUh8CBB4zhetehN+wMUHjzQjGpI2N9IEFMhDp" +
            "Ag+bMYIXNouJ";
    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/wappay/notify";
    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    public static String return_url = "http://localhost:8080/wappay/return";
    // 请求网关地址
    public static String URL = "https://openapi.alipay.com/gateway.do";
    // 编码
    public static String CHARSET = "UTF-8";
    // 返回格式
    public static String FORMAT = "json";
    // 支付宝公钥
    public static String ALIPAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDCLf0kh5X5XApIK8Wlx02ff/AoZEznkdptI3qAE+GC88iUr5XaZkwl0VDISSOFogf+ue2Q9z2Fhf/5KdG1HRxvfNnyzae84glWD3CdGn2YZJl+Bl7PGTyxMI7Nah5ktjsAuefolAJoaHVQbPkqXCS6Hfird8b7PA6ToRG4hxgkRQIDAQAB";
    // 日志记录目录
    public static String log_path = "/log";
    // RSA2
    public static String SIGNTYPE = "RSA2";
    // 超时时间 可空
    public static String timeout_express="2m";
    // 销售产品码 必填
    public static String product_code="QUICK_WAP_PAY";
}
