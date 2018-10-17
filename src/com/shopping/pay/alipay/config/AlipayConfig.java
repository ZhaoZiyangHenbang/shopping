 package com.shopping.pay.alipay.config;
 
 public class AlipayConfig
 {
   private String partner = "";
 
   private String key = "";
 
   private String seller_email = "";
 
   private String notify_url = "";
 
   private String return_url = "";
 
   private String log_path = "D:\\alipay_log_" + System.currentTimeMillis() + 
     ".txt";
 
   private String input_charset = "utf-8";
 
   private String sign_type = "MD5";//
 
   private String transport = "http";
 
	// 商户的私钥,需要PKCS8格式，RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
	public static String private_key = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMIt/SSHlflcCkgr" +
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
  //public static String private_key ="MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAK0LNe0/SbNtYlCnhQtzPqjIrVd8Wp1R+AKs4x77kRs8k575N4lc9OZF6QFZZyVg8e5asGJ04WZ5OrzECp+C0kLytrxWXbYfTwFB23kJ/LsNrr1PbQzsYCWqYXl1fG4/P2zlYNhZgRWL3btxbFbxIJ7EYjMxjm5K2/8pFge4mi2zAgMBAAECgYB8lPijMRBPpK3GoYSwxKzU+X2K4dWfM9dZnbz6nmVO9aLWLlikhY8vhh+FwCb5iz1CvHoGuF1a2GbIZl+7jYTwTB5A1zv/NuXmgKZisUSgJ7r4equVf/P2HkwuOdxXICl+r+p44Fu813YssnUfAQ/riAkjUxei8rSZ+5vcRkyrEQJBAOQwABUrz3/EJLYTy7hnVO6dnLsGQIDQhQG68TEgO8FFc8JtNPwqwBzrxmCcpRPGstN4Ms1+jqsi5A+p8N3hZBkCQQDCIpgAxtP4ZI3TxMAvh8BDACIE+oy/Mgi+YlmukJfXVCNUcEGglG9clEE/6wbg8Yc9mjsurGpFHHZwhYUkMPmrAkEAqe/Rb/LjokxJ5suRUTCrQNViTqpWSViBZYt6alKODd6SWj6IUi/oSOGSKIsgKT02GmUOuJlC2NKwM7Yk6qkbqQJBAIQS2OqeWhNBBaQu1LkBV/G9I2fTtPFWbtBnRLAYOtUJrseBX4SJt4F6czGzCYj7iJCGXwEP13SROpqum7fVgeUCQQC7+ZGamS0wpa+g+tDVdBFvQ6f/81J2sgzPBqP6m4wYrW5JGdK1h13pPx4H9R49g8m0gGvrxi+Oo0fbog1n5GmY";
	// 支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
	public static String ali_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDCLf0kh5X5XApIK8Wlx02ff/AoZEznkdptI3qAE+GC88iUr5XaZkwl0VDISSOFogf+ue2Q9z2Fhf/5KdG1HRxvfNnyzae84glWD3CdGn2YZJl+Bl7PGTyxMI7Nah5ktjsAuefolAJoaHVQbPkqXCS6Hfird8b7PA6ToRG4hxgkRQIDAQAB";
  // public static String ali_public_key ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCtCzXtP0mzbWJQp4ULcz6oyK1XfFqdUfgCrOMe+5EbPJOe+TeJXPTmRekBWWclYPHuWrBidOFmeTq8xAqfgtJC8ra8Vl22H08BQdt5Cfy7Da69T20M7GAlqmF5dXxuPz9s5WDYWYEVi927cWxW8SCexGIzMY5uStv/KRYHuJotswIDAQAB";


   public static String getPrivate_key() {
     return private_key;
   }
 
   public static void setPrivate_key(String private_key) {
     private_key = private_key;
   }
 
   public static String getAli_public_key() {
     return ali_public_key;
   }
 
   public static void setAli_public_key(String ali_public_key) {
     ali_public_key = ali_public_key;
   }
 
   public String getPartner() {
     return this.partner;
   }
 
   public void setPartner(String partner) {
     this.partner = partner;
   }
 
   public String getKey() {
     return this.key;
   }
 
   public void setKey(String key) {
     this.key = key;
   }
 
   public String getSeller_email() {
     return this.seller_email;
   }
 
   public void setSeller_email(String seller_email) {
     this.seller_email = seller_email;
   }
 
   public String getNotify_url() {
     return this.notify_url;
   }
 
   public void setNotify_url(String notify_url) {
     this.notify_url = notify_url;
   }
 
   public String getReturn_url() {
     return this.return_url;
   }
 
   public void setReturn_url(String return_url) {
     this.return_url = return_url;
   }
 
   public String getLog_path() {
     return this.log_path;
   }
 
   public void setLog_path(String log_path) {
     this.log_path = log_path;
   }
 
   public String getInput_charset() {
     return this.input_charset;
   }
 
   public void setInput_charset(String input_charset) {
     this.input_charset = input_charset;
   }
 
   public String getSign_type() {
     return this.sign_type;
   }
 
   public void setSign_type(String sign_type) {
     this.sign_type = sign_type;
   }
 
   public String getTransport() {
     return this.transport;
   }
 
   public void setTransport(String transport) {
     this.transport = transport;
   }
 }


 
 
 