package com.shopping.pay;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangqi on 17/5/11.
 */

public class PayEntity {
    String app_id= Global.get("app_id");
    String app_secret = Global.get("app_secret");
    String title;
    String amount;
    String out_trade_no;
    String sign;
    String return_url;
    Map<String,String> optional= new HashMap<>();
    public PayEntity(String title, String out_trade_no, String amount){
        this.title=title;
        this.out_trade_no=out_trade_no;
        this.amount=amount;
        this.sign=UtilsPay.getMessageDigest(this.app_id +title + amount + out_trade_no + this.app_secret);
    }

    public String getAmount() {
        return amount;
    }

    public PayEntity setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public String getApp_id() {
        return app_id;
    }

    public PayEntity setApp_id(String app_id) {
        this.app_id = app_id;
        return this;
    }

    public String getApp_secret() {
        return app_secret;
    }

    public PayEntity setApp_secret(String app_secret) {
        this.app_secret = app_secret;
        return this;
    }

    public Map<String, String> getOptional() {
        return optional;
    }

    public PayEntity setOptional(Map<String, String> optional) {
        this.optional = optional;
        return this;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public PayEntity setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
        return this;
    }

    public String getReturn_url() {
        return return_url;
    }

    public PayEntity setReturn_url(String return_url) {
        this.return_url = return_url;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public PayEntity setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PayEntity setTitle(String title) {
        this.title = title;
        return this;
    }
}
