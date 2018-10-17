package com.shopping.send;

import com.alibaba.fastjson.JSONObject;
import com.shopping.foundation.service.IUserService;
import com.shopping.plug.login.action.HttpRequestProxy;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangqi on 2017/5/16.
 */
@Controller
@RequestMapping("sendYzm")
public class SendYzm {

    @Autowired
    private IUserService userService;
    @RequestMapping("/send.htm")
    public void send(HttpServletRequest request, HttpServletResponse response,String telephone){
        String yzm=(int)((Math.random()*9+1)*100000)+"";
        //String sign="颐佳易购";
        String sign="%e9%a2%90%e4%bd%b3%e6%98%93%e8%b4%ad";
        //String Msg="尊敬的用户:您申请的手机注册验证码为";
        String Msg="%e5%b0%8a%e6%95%ac%e7%9a%84%e7%94%a8%e6%88%b7%3a%e6%82%a8%e7%94%b3%e8%af%b7%e7%9a%84%e6%89%8b%e6%9c%ba%e6%b3%a8%e5%86%8c%e9%aa%8c%e8%af%81%e7%a0%81%e4%b8%ba"+yzm;
        String sr=HttpRequest.sendPost("http://manager.wxtxsms.cn/smsport/sendPost.aspx", "uid=haoxing&upsd="+Str2MD5.MD532("00000000")+"&sendtele="+telephone+"&Msg="+Msg+"&sign="+sign);
     /*   System.out.println("uid=haoxing");
        System.out.println("upsd="+Str2MD5.MD532("00000000"));
        System.out.println("sendtele="+telephone);
        System.out.println("Msg="+Msg);
        System.out.println("sign="+sign);
        System.out.println("sr="+sr);*/
        Map<String,String> map = new HashMap<>();
        map.put("yzm",yzm);
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(map, JsonFormat.compact()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



/*@RequestMapping("/weixin_login.htm")
 public void wx(HttpServletResponse response) {
       *//* try {
            response.sendRedirect("https://open.weixin.qq.com/connect/qrconnect?appid="
                    + ShareLoginDict.WEIXINKEY.getState()
                    + "&redirect_uri="
                    + URLEncoder.encode(ShareLoginDict.WEIXINURL.getState())
                    + "&response_type=code&scope=snsapi_login&state=66666#wechat_redirect");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*//*

    }*/
@RequestMapping("/weixin_login.htm")
public void wx(HttpServletResponse response,HttpServletRequest request, String code,String state) {

    if(null!=code&&!"".equals(code)) {
      /*  try {*/
            Map map=new HashMap();
           /* map.put("code",code);
            map.put("state",state);*/
            JSONObject json=JSONObject.parseObject(HttpRequestProxy.doPost("https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code", map,"utf-8"));


            map.clear();
            JSONObject json2=JSONObject.parseObject(HttpRequestProxy.doPost("https://api.weixin.qq.com//sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID", map,"utf-8"));

       /* } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }
}

   /* public Result userwx(String return_code) {
        Result result = new Result();
        Map<String, Object> token = (Map<String, Object>) WeiXinAPI.getToken(return_code);
        if (token != null && token.get("access_token") != null) {
            Map<String, Object> user = (Map<String, Object>) WeiXinAPI.getWxUser(token.get("access_token").toString(), token.get("openid").toString());
            if (user != null) {
                result.addModel("openid", user.get("openid"));
                result.addModel("nickname", user.get("nickname"));
                result.addModel("headimgurl", user.get("headimgurl"));
                result.addModel("data", "data_success");
            }else{
                result.addModel("data", "data_null");
            }
        }else{
            result.addModel("data", "data_nu    ll");
        }
        return result;
    }*/
   @RequestMapping("/telephone.htm")
   public void telephone(HttpServletRequest request, HttpServletResponse response,String telephone){
       Map params = new HashMap();
       params.put("telephone", telephone);
       List users = this.userService.query("select obj from User obj where obj.telephone=:telephone", params, -1, -1);
       Map<String,String> map = new HashMap<>();
       if (null!=users&&users.size()>0) {
           map.put("info","0");
       }else{
           map.put("info","1");
       }
       response.setContentType("text/plain");
       response.setHeader("Cache-Control", "no-cache");
       response.setCharacterEncoding("UTF-8");
       try {
           PrintWriter writer = response.getWriter();
           writer.print(Json.toJson(map, JsonFormat.compact()));
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
    @RequestMapping("/email.htm")
    public void email(HttpServletRequest request, HttpServletResponse response,String email){
        Map params = new HashMap();
        params.put("email", email);
        List users = this.userService.query("select obj from User obj where obj.email=:email", params, -1, -1);
        Map<String,String> map = new HashMap<>();
        if (null!=users&&users.size()>0) {
            map.put("info","0");
        }else{
            map.put("info","1");
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(map, JsonFormat.compact()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
