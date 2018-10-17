 package com.shopping.core.tools;

 import com.alibaba.fastjson.JSONObject;

 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.List;

 /**
  * 分销的占比
  */
 public class fenXiaoUtil{
   //非体验店(分销利润的百分比)
   public static final BigDecimal ONEFENXIAO = new BigDecimal("0.45");
   public static final BigDecimal TWOFENXIAO = new BigDecimal("0.25");
   public static final BigDecimal THREEFENXIAO = new BigDecimal("0.1");

   //体验店（商品的百分比）
   public static final BigDecimal TONEFENXIAO = new BigDecimal("0.66");
   public static final BigDecimal TTWOFENXIAO = new BigDecimal("0.27");
   public static final BigDecimal TTHREEFENXIAO = new BigDecimal("0.07");

   //会员等级的划分(获取分销金额的占比)
   public static final BigDecimal ONEXING = new BigDecimal("0.2");
   public static final BigDecimal TWOXING = new BigDecimal("0.4");
   public static final BigDecimal THREEXING = new BigDecimal("0.6");
   public static final BigDecimal FOREXING = new BigDecimal("0.8");
   public static final BigDecimal FIVEXING = new BigDecimal("1");

   /**
    * 根据会员的等级获取分销金额的占比
    * @param starLevel
    * @return
      */
    public static BigDecimal getStarLevelRatio(String starLevel){
        if(starLevel.equals("1")){
            return ONEXING;
        }else if(starLevel.equals("2")){
            return TWOXING;
        }else if(starLevel.equals("3")){
          return THREEXING;
        }else if(starLevel.equals("4")){
          return FOREXING;
        }
          return FIVEXING;
    }

   /**
    * 根据分销等级和是否为体验店返回占比
    * @param rank
    * @param type(0为体验店 1不为体验店)
    * @return
      */
    public static BigDecimal getRatio(String rank,String type){
      if(rank.equals("1")){
        if(type.equals("0")){
            return TONEFENXIAO;
        }else if(type.equals("1")){
          return ONEFENXIAO;
        }
      }else if(rank.equals("2")){
        if(type.equals("0")){
          return TTWOFENXIAO;
        }else if(type.equals("1")){
          return TWOFENXIAO;
        }
      }else if(rank.equals("3")){
        if(type.equals("0")){
          return TTHREEFENXIAO;
        }else if(type.equals("1")){
          return THREEFENXIAO;
        }
      }
      return null;
    }


     /**
      * 获取用户的等级
      * @param type（1，数量,0，金额，）
      * @param num
      * @param json(信用规则的字符串)
      * @return
      */
     public static String getCredit(String type, long num, String json){
         JSONObject jsStr = JSONObject.parseObject(json);
         //信用的集合
         List<String> creditMoneyList = new ArrayList<>();
         List<String> creditpersonList = new ArrayList<>();
         int credit = 1;
         for (int a = 0;a<20;a++){
             StringBuffer st = new StringBuffer();
             st.append(credit+":");
             for (int b = 0;b<2;b++){
                 int jsID = Integer.parseInt(jsStr.getString("creditrule"+(a+b)));
                 st.append(jsID+",");
             }
             a++;
             if(a<=10){
                 creditMoneyList.add(st.substring(0,st.length()-1));
             }else{
                 creditpersonList.add(st.substring(0,st.length()-1));
             }
             credit++;
             if(credit==6){
                 credit=1;
             }
         }

         if(type.equals("1")) {
             for (String s:creditpersonList) {
                 String credits = s.split(",")[0].split(":")[0];
                 String min = s.split(",")[0].split(":")[1];
                 String max = s.split(",")[1];
                 if(num>=Integer.valueOf(min)&&num<Integer.valueOf(max)){
                    return credits;
                 }
             }
         }else{
             for (String s:creditMoneyList) {
                 String credits = s.split(",")[0].split(":")[0];
                 String min = s.split(",")[0].split(":")[1];
                 String max = s.split(",")[1];
                 if(num>=Integer.valueOf(min)&&num<Integer.valueOf(max)){
                     return credits;
                 }else if(num<Integer.valueOf(min)){
                     return "0";
                 }
             }
         }
        return null;
     }
 }

