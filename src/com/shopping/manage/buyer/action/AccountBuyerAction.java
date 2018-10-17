 package com.shopping.manage.buyer.action;
 
 import java.io.*;
 import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



//import sun.misc.BASE64Decoder;
 import com.shopping.foundation.domain.*;
 import com.shopping.foundation.domain.query.AddressQueryObject;
 import com.shopping.foundation.domain.query.StoreQueryObject;
 import com.shopping.foundation.service.*;
 import com.shopping.foundation.test.TestEmail;
 import com.shopping.send.HttpRequest;
 import com.shopping.send.Str2MD5;
 import org.apache.commons.codec.binary.Base64;
 import org.apache.commons.lang.ObjectUtils;
 import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
 import org.nutz.json.Json;
 import org.nutz.json.JsonFormat;
 import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.multipart.MultipartFile;
 import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.annotation.SecurityMapping;
import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.Md5Encrypt;
import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.query.SnsFriendQueryObject;
import com.shopping.foundation.domain.query.UserQueryObject;
 import com.shopping.manage.admin.tools.MsgTools;
import com.shopping.uc.api.UCClient;
 
 @Controller
 public class AccountBuyerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
   @Autowired
   private IAddressService addressService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IMobileVerifyCodeService mobileverifycodeService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @Autowired
   private ISnsFriendService sndFriendService;
 
   @Autowired
   private ITemplateService templateService;
 
   @Autowired
   private IAreaService areaService;
     @Autowired
     private IStoreService storeService;
   @Autowired
   private MsgTools msgTools;
   private static final String DEFAULT_AVATAR_FILE_EXT = ".jpg";
   //private static BASE64Decoder _decoder = new BASE64Decoder();
   public static final String OPERATE_RESULT_CODE_SUCCESS = "200";
   public static final String OPERATE_RESULT_CODE_FAIL = "400";

   @SecurityMapping(display = false, rsequence = 0, title="意见反馈", value="/buyer/feedback.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/feedback.htm"})
   public ModelAndView feedback(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
             "user/second/60FeedBack.html", this.configService
             .getSysConfig(),
             this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
     if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
       mv = new JModelAndView("newwap/意见反馈.html",
               this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     }
     mv.addObject("user", this.userService.getObjById(
             SecurityUserHolder.getCurrentUser().getId()));
     List areas = this.areaService.query(
             "select obj from Area obj where obj.parent.id is null", null,
             -1, -1);
     mv.addObject("areas", areas);
     return mv;
   }

   @SecurityMapping(display = false, rsequence = 0, title="附近门店", value="/buyer/nearby_stores.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/nearby_stores.htm"})
   public ModelAndView nearby_stores(HttpServletRequest request, HttpServletResponse response,String currentPage) {
     ModelAndView mv = new JModelAndView(
             "user/default/usercenter/nearby_stores.html", this.configService
             .getSysConfig(),
             this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
     if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
       mv = new JModelAndView("newwap/附件门店.html",
               this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     }
     User user=this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
       mv.addObject("user",user);
if (user.getArea()!=null){
       StoreQueryObject sqo = new StoreQueryObject(currentPage, mv, "addTime", "desc");
       Map map=new HashMap();
       map.put("area_id",CommUtil.null2Long( user.getArea().getId()));
        sqo.addQuery("obj.area.id=:area_id",map);

       IPageList pList = this.storeService.list(sqo);
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
}else{
    mv.addObject("msg","请前去完善你的个人信息,方可查看附近门店");
}
 /*    List areas = this.areaService.query(
             "select obj from Area obj where obj.parent.id is null", null,
             -1, -1);
     mv.addObject("areas", areas);*/
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="个人信息导航", value="/buyer/account_nav.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_nav.htm"})
   public ModelAndView account_nav(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/account_nav.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String op = CommUtil.null2String(request.getAttribute("op"));
     mv.addObject("op", op);
     mv.addObject("user", this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId()));
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="个人信息", value="/buyer/account.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account.htm"})
   public ModelAndView account(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
       "user/second/45Buyer_P_Account.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
     if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
       mv = new JModelAndView("newwap/60zhanghuguanli.html",
               this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     }
     mv.addObject("user", this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId()));
     List areas = this.areaService.query(
       "select obj from Area obj where obj.parent.id is null", null, 
       -1, -1);
     mv.addObject("areas", areas);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="个人资料", value="/buyer/account_data.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_data.htm"})
   public ModelAndView account_data(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
             "user/default/usercenter/account_data.html", this.configService
             .getSysConfig(),
             this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
     if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
       mv = new JModelAndView("newwap/61gerenziliao.html",
               this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     }
     mv.addObject("user", this.userService.getObjById(
             SecurityUserHolder.getCurrentUser().getId()));
  User user=   this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
       Area area =null;
       if(null !=user.getArea()) {
           area = this.areaService.getObjById(CommUtil.null2Long(user.getArea().getId()));
       }
    /* List areas = this.areaService.query(
             "select obj from Area obj where obj.parent.id is null", null,
             -1, -1);*/
     mv.addObject("areas", area);
     return mv;
   }

   @SecurityMapping(display = false, rsequence = 0, title="个人资料编辑", value="/buyer/account_edit.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_edit.htm"})
   public ModelAndView account_edit(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView("user/default/usercenter/account_edit.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
     if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
       mv = new JModelAndView("newwap/61gerenziliao_bianji.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     }
     mv.addObject("user", this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
     List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
     mv.addObject("areas", areas);
     return mv;
   }


   @SecurityMapping(display = false, rsequence = 0, title="个人信息获取下级地区ajax", value="/buyer/account_getAreaChilds.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_getAreaChilds.htm"})
   public ModelAndView account_getAreaChilds(HttpServletRequest request, HttpServletResponse response, String parent_id) {
     ModelAndView mv = new JModelAndView("user/default/usercenter/account_area_chlids.html", 
       this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
     Map map = new HashMap();
     map.put("parent_id", CommUtil.null2Long(parent_id));
     List childs = this.areaService.query("select obj from Area obj where obj.parent.id=:parent_id", map, -1, -1);
     if (childs.size() > 0) {
       mv.addObject("childs", childs);
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="个人信息保存", value="/buyer/account_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_save.htm"})
   public ModelAndView account_save(HttpServletRequest request, HttpServletResponse response, String area_id, String birthday,String trueName,String radio,String WW,String WX,String MSN,String qq,String card_no){
     ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     WebForm wf = new WebForm();
     User u = SecurityUserHolder.getCurrentUser();
     User user = (User)wf.toPo(request, u);
     if ((area_id != null) && (!area_id.equals(""))) {
       Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
       user.setArea(area);
     }
     if ((birthday != null) && (!birthday.equals(""))) {
       String[] y = birthday.split("-");
       Calendar calendar = new GregorianCalendar();
       int years = calendar.get(  1) - CommUtil.null2Int(y[0]);
      user.setYears(years);
     }
     if (radio!=null&&!radio.equals("")){
       user.setSex(CommUtil.null2Int(radio));
     }
       user.setId(SecurityUserHolder.getCurrentUser().getId());
       user.setWW(WW);
       user.setWX(WX);
       user.setQQ(qq);
       user.setMSN(MSN);
       user.setSfz(card_no);
       user.setTrueName(trueName);

     user.setBirthday(CommUtil.formatDate(birthday));



     this.userService.update(user);
     mv.addObject("op_title", "个人信息修改成功");
     mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
     return mv;
   }

     @SecurityMapping(display = false, rsequence = 0, title="修改用户名", value="/buyer/userName_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
     @RequestMapping({"/buyer/userName_save.htm"})
     public ModelAndView userName_save(HttpServletRequest request, HttpServletResponse response, String userName){
         ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         User u = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
         Map params = new HashMap();
         params.put("userName", userName);
         List users = this.userService.query("select obj from User obj where obj.userName=:userName", params, -1, -1);
         if (!u.getUserName().equals(userName)&&(users != null) && (users.size() > 0)) {
             mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
             mv.addObject("op_title", "用户名已存在");
             mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_user.htm");
         }else {
             u.setUserName(userName);
             this.userService.update(u);
             mv.addObject("op_title", "个人信息修改成功");
             mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_user.htm");
         }

         return mv;
     }

   @SecurityMapping(display = false, rsequence = 0, title="个人信息保存", value="/buyer/account_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_saveweb.htm"})
   public ModelAndView account_saveweb(HttpServletRequest request, HttpServletResponse response, String area_id, String birthday,String trueName,String radio,String WW,String WX,String MSN,String qq) {
     ModelAndView mv = new JModelAndView("newwap/60zhanghuguanli.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     WebForm wf = new WebForm();
     User u = SecurityUserHolder.getCurrentUser();
     User user = (User)wf.toPo(request, u);
     if ((area_id != null) && (!area_id.equals(""))) {
       Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
       user.setArea(area);
     }
     if ((birthday != null) && (!birthday.equals(""))) {
       String[] y = birthday.split("-");
       Calendar calendar = new GregorianCalendar();
       int years = calendar.get(1) - CommUtil.null2Int(y[0]);
       user.setYears(years);
     }
     if (radio!=null&&!radio.equals("")){
       user.setSex(CommUtil.null2Int(radio));
     }
     user.setId(SecurityUserHolder.getCurrentUser().getId());
     user.setWW(WW);
     user.setWX(WX);
     user.setQQ(qq);
     user.setMSN(MSN);
     user.setTrueName(trueName);
     user.setBirthday(CommUtil.formatDate(birthday));
     this.userService.update(user);
   /*  mv.addObject("op_title", "个人信息修改成功");*/
//     mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
     return mv;
   }




   @SecurityMapping(display = false, rsequence = 0, title="密码修改", value="/buyer/account_password.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_password.htm"})
   public ModelAndView account_password(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView("user/second/50Buyer_P_Psw.html", this.configService.getSysConfig(),
       this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
	 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
		 mv = new JModelAndView("newwap/55wangjimima.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
	 }
     return mv;
   }

     /*wap修改密码*/
     @SecurityMapping(display = false, rsequence = 0, title="密码修改保存", value="/buyer/account_password_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
     @RequestMapping({"/buyer/account_password_save_wap.htm"})
     public ModelAndView  account_password_save_wap(HttpServletRequest request, HttpServletResponse response, String sendYzm, String yzm, String password,String telephone) throws Exception {
         ModelAndView mv = new JModelAndView("wap/pswSuccess.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

      User user=this.userService.getObjByProperty("telephone",telephone);
        /* WebForm wf = new WebForm();
         User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        *//* System.out.print("你输入的手机号:"+telephone);
         System.out.print("新密码是:"+password);
         System.out.print("原来的手机号:"+user.getTelephone());*//*
         if (user.getTelephone().equals(telephone)) {*/
             if (yzm!=null&&!"".equals(yzm)&&sendYzm!=null&&!"".equals(sendYzm)&&yzm.equals(sendYzm)) {
                user.setPassword(Md5Encrypt.md5(password).toLowerCase());
                 this.userService.update(user);
                 mv.addObject("op_title", "密码修改成功");
                 mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
            } else {
                 mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
                         this.userConfigService.getUserConfig(), 1, request,
                         response);
                 mv.addObject("op_title", "验证码输入错误，密码修改失败!");
                 mv.addObject("url", CommUtil.getURL(request) +
                         "/buyer/account_password.htm");
            }
        /* }else {
             mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request,
                     response);
             mv.addObject("op_title", "该手机号未绑定该账号，密码修改失败!");
             mv.addObject("url", CommUtil.getURL(request) +
                     "/buyer/account_password.htm");
         }*/
         return mv;
     }
   @SecurityMapping(display = false, rsequence = 0, title="密码修改保存", value="/buyer/account_password_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_password_save.htm"})
   public ModelAndView account_password_save(HttpServletRequest request, HttpServletResponse response, String sendYzm, String yzm,String password, String new_password) throws Exception {
     ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
	 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
		 mv = new JModelAndView("newwap/success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
	 }
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     if (yzm!=null&&!"".equals(yzm)&&sendYzm!=null&&!"".equals(sendYzm)&&yzm.equals(sendYzm)) {
       user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
       user.setQq_psw(new_password);
       boolean ret = this.userService.update(user);
       /*if ((ret) && (this.configService.getSysConfig().isUc_bbs())) {
         UCClient uc = new UCClient();
         String str = uc.uc_user_edit(user.getUsername(),
           CommUtil.null2String(old_password),
           CommUtil.null2String(new_password),
           CommUtil.null2String(user.getEmail()), 1, 0, 0);
       }
 */
       mv.addObject("op_title", "密码修改成功");
       send_sms(request, "sms_tobuyer_pws_modify_notify");
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
  		 mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
  	   }
       mv.addObject("op_title", "验证码输入错误，修改失败");
     }
     mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="邮箱修改", value="/buyer/account_email.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_email.htm"})
   public ModelAndView account_email(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
       "user/second/48Buyer_P_Email.html", this.configService
       .getSysConfig(),
       this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
     if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
       mv = new JModelAndView("newwap/69youxiangbangding.html",
               this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     }
     User user = this.userService.getObjById(
             SecurityUserHolder.getCurrentUser().getId());
     mv.addObject("user",user);
     return mv;
   }
     @SecurityMapping(display = false, rsequence = 0, title="邮箱添加", value="/buyer/account_email_add.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
     @RequestMapping({"/buyer/account_email_add.htm"})
     public ModelAndView account_email_add(HttpServletRequest request, HttpServletResponse response) {
         ModelAndView mv = new JModelAndView(
                 "user/second/48Buyer_P_Email_add.html", this.configService
                 .getSysConfig(),
                 this.userConfigService.getUserConfig(), 0, request, response);
         User user = this.userService.getObjById(
                 SecurityUserHolder.getCurrentUser().getId());
         mv.addObject("user",user);
         return mv;
     }
   @RequestMapping({"/buyer/account_email_sms.htm"})
   public void account_email_sms(HttpServletRequest request, HttpServletResponse response, String email) {
     String yzm=(int)((Math.random()*9+1)*100000)+"";
     String username = "chaichao@strongbm.com";
     String password = "strongbm888";
     String smtp_server = "smtp.yijia1234.com";
     String from_mail_address = "chaichao@strongbm.com";
     String subject = "颐佳商城验证码";
     String content = "颐佳商城验证码："+yzm;
     System.out.println( "颐佳商城验证码："+yzm);
     boolean ret = TestEmail.sendEmail(username, password, smtp_server,
             from_mail_address, email, subject, content);
     if (ret)
       System.out.println("发送邮件成功！");
     else
       System.out.println("发送邮件失败!");
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
   @SecurityMapping(display = false, rsequence = 0, title="邮箱修改保存", value="/buyer/account_email_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_email_save.htm"})
   public ModelAndView account_email_save(HttpServletRequest request, HttpServletResponse response, String password, String email,String yzm,String sendYzm) {
     ModelAndView mv = new JModelAndView("success.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1,
       request, response);
     WebForm wf = new WebForm();
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     if (user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) {
       if (yzm!=null&&!"".equals(yzm)&&sendYzm!=null&&!"".equals(sendYzm)&&yzm.equals(sendYzm)) {
         user.setEmail(email);
         this.userService.update(user);
         mv.addObject("op_title", "邮箱保存成功");
         mv.addObject("url", CommUtil.getURL(request) +
                 "/buyer/account.htm");
       }else{
         mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                 this.userConfigService.getUserConfig(), 1, request,
                 response);
         mv.addObject("op_title", "验证码输入错误，邮箱保存失败");
         mv.addObject("url", CommUtil.getURL(request) +
                 "/buyer/account_email.htm");
       }
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(),
         this.userConfigService.getUserConfig(), 1, request,
         response);
       mv.addObject("op_title", "密码输入错误，邮箱修改失败");
       mv.addObject("url", CommUtil.getURL(request) +
               "/buyer/account_email.htm");
     }
     return mv;
   }
     @SecurityMapping(display = false, rsequence = 0, title="邮箱修改保存", value="/buyer/account_email_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
     @RequestMapping({"/buyer/account_email_save_wap.htm"})
     public ModelAndView account_email_save_wap(HttpServletRequest request, HttpServletResponse response, String password, String email,String yzm,String sendYzm) {
         ModelAndView mv = new JModelAndView("wap/emailSuccess.html", this.configService
                 .getSysConfig(), this.userConfigService.getUserConfig(), 1,
                 request, response);
         WebForm wf = new WebForm();
         User user = this.userService.getObjById(
                 SecurityUserHolder.getCurrentUser().getId());
         if (user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) {
             if (yzm!=null&&!"".equals(yzm)&&sendYzm!=null&&!"".equals(sendYzm)&&yzm.equals(sendYzm)) {
                 user.setEmail(email);
                 this.userService.update(user);
                 mv.addObject("op_title", "邮箱修改成功");
                 mv.addObject("url", CommUtil.getURL(request) +
                         "/buyer/account.htm");
             }else{
                 mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
                         this.userConfigService.getUserConfig(), 1, request,
                         response);
                 mv.addObject("op_title", "验证码输入错误，邮箱修改失败");
                 mv.addObject("url", CommUtil.getURL(request) +
                         "/buyer/account_email.htm");
             }
         } else {
             mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request,
                     response);
             mv.addObject("op_title", "密码输入错误，邮箱修改失败");
             mv.addObject("url", CommUtil.getURL(request) +
                     "/buyer/account_email.htm");
         }
         return mv;
     }






   @SecurityMapping(display = false, rsequence = 0, title="银行卡管理", value="/buyer/account_bank.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_bank.htm"})
   public ModelAndView account_bank(HttpServletRequest request, HttpServletResponse response ,String currentPage, String orderBy, String orderType) {
     ModelAndView mv = new JModelAndView("user/second/52Buyer_Back.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
     if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
       mv = new JModelAndView("newwap/65yinhangka.html",
               this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     }
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     AddressQueryObject qo = new AddressQueryObject(currentPage, mv, orderBy, orderType);
     qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
       qo.addQuery("obj.type",new SysMap("type",CommUtil.null2Int(1)),"=");
     IPageList pList = this.addressService.list(qo);
     CommUtil.saveIPageList2ModelAndView(url + "/buyer/address.htm", "", params, pList, mv);
     List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
     mv.addObject("areas", areas);
     mv.addObject("user", this.userService.getObjById(
             SecurityUserHolder.getCurrentUser().getId()));
     return mv;
   }
     @SecurityMapping(display = false, rsequence = 0, title="银行卡添加", value="/buyer/account_bank.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
     @RequestMapping({"/buyer/account_bank_add_a.htm"})
     public ModelAndView account_bank_add_(HttpServletRequest request, HttpServletResponse response ,String currentPage,String backname, String trueName, String backnum, String bank_addr,String type,String id,String area_id) {
         ModelAndView mv = new JModelAndView("newwap/65yinhangka_add.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

            return mv;
     }
   @SecurityMapping(display = false, rsequence = 0, title="银行卡保存", value="/buyer/account_bank.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_bank_add.htm"})
   public String account_bank_add(HttpServletRequest request, HttpServletResponse response ,String currentPage,String backname, String trueName, String backnum, String bank_addr,String type,String id,String area_id) {
     WebForm wf = new WebForm();
     Address address = null;
     if (id.equals("")) {
       address = wf.toPo(request, Address.class);
       address.setAddTime(new Date());
     } else {
       address = this.addressService.getObjById(Long.valueOf(Long.parseLong(id)));
     }
       address.setTrueName(trueName);
       address.setBank(backname);
       address.setBankCardnumber(backnum);
       address.setBank_addr(bank_addr);
       address.setType(CommUtil.null2Int(1));
     address.setUser(SecurityUserHolder.getCurrentUser());
     Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
     address.setArea(area);
     if (id.equals("")||id==null) {
       this.addressService.save(address);
     } else {
       this.addressService.update(address);
     }
     return "redirect:account_bank.htm";
   }
   @SecurityMapping(display = false, rsequence = 0, title="银行卡删除", value="/buyer/account_bank.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_bank_del.htm"})
   public String account_bank_del(HttpServletRequest request, HttpServletResponse response ,String id) {

     this.addressService.delete(CommUtil.null2Long(id));

     return  "redirect:account_bank.htm";
   }


   @SecurityMapping(display = false, rsequence = 0, title="图像修改", value="/buyer/account_avatar.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_avatar.htm"})
   public ModelAndView account_avatar(HttpServletRequest request, HttpServletResponse response) {
     //ModelAndView mv = new JModelAndView("user/default/usercenter/account_avatar.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
      ModelAndView mv = new JModelAndView("user/second/47Buyer_P_Photo.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
    mv.addObject("user", this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
     mv.addObject("url", CommUtil.getURL(request));
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="图像上传", value="/buyer/upload_avatar.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/upload_avatar.htm"})
   public String upload_avatar(HttpServletRequest request, @RequestParam("file")MultipartFile file, HttpServletResponse response) throws IOException {

     //System.out.println(file.getSize()+"1111111111111");
     response.setContentType("text/html;charset=UTF-8");
     response.setHeader("Pragma", "No-cache");
     response.setHeader("Cache-Control", "no-cache");
     response.setDateHeader("Expires", 0L);

       String filePath = request.getSession().getServletContext().getRealPath("") + "/upload/avatar";
       File uploadDir = new File(filePath);
       if (!uploadDir.exists()) {
         uploadDir.mkdirs();
       }
       String customParams = CommUtil.null2String(request.getParameter("custom_params"));
       String imageType = CommUtil.null2String(request.getParameter("image_type"));
       if ("".equals(imageType)) {
         imageType = ".jpg";
       }
       User user = SecurityUserHolder.getCurrentUser();
       String bigAvatarName = SecurityUserHolder.getCurrentUser().getId() + "_big";

       saveImage(filePath, imageType, file, bigAvatarName);
       Accessory photo = new Accessory();
       if (user.getPhoto() != null) {
         photo = user.getPhoto();
       } else {
         photo.setAddTime(new Date());
         photo.setWidth(132);
         photo.setHeight(132);
       }
       photo.setName(bigAvatarName + imageType);
       photo.setExt(imageType);
       photo.setPath(this.configService.getSysConfig().getUploadFilePath() + "/avatar");
       if (user.getPhoto() == null)
         this.accessoryService.save(photo);
       else {
         this.accessoryService.update(photo);
       }
       user.setPhoto(photo);
       this.userService.update(user);
       return "redirect:/buyer/account_avatar.htm";
   }
//wap 图像上传

     @SecurityMapping(display = false, rsequence = 0, title="图像上传", value="/buyer/upload_avatar.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
     @RequestMapping({"/buyer/upload_avatar_wap.htm"})
     public String upload_avatar_wap(HttpServletRequest request, @RequestParam("file")MultipartFile file, HttpServletResponse response) throws IOException {

         //System.out.println(file.getSize()+"1111111111111");
         response.setContentType("text/html;charset=UTF-8");
         response.setHeader("Pragma", "No-cache");
         response.setHeader("Cache-Control", "no-cache");
         response.setDateHeader("Expires", 0L);

         String filePath = request.getSession().getServletContext().getRealPath("") + "/upload/avatar";
         File uploadDir = new File(filePath);
         if (!uploadDir.exists()) {
             uploadDir.mkdirs();
         }
         String customParams = CommUtil.null2String(request.getParameter("custom_params"));
         String imageType = CommUtil.null2String(request.getParameter("image_type"));
         if ("".equals(imageType)) {
             imageType = ".jpg";
         }
         User user = SecurityUserHolder.getCurrentUser();
         String bigAvatarName = SecurityUserHolder.getCurrentUser().getId() + "_big";

         saveImage(filePath, imageType, file, bigAvatarName);
         Accessory photo = new Accessory();
         if (user.getPhoto() != null) {
             photo = user.getPhoto();
         } else {
             photo.setAddTime(new Date());
             photo.setWidth(132);
             photo.setHeight(132);
         }
         photo.setName(bigAvatarName + imageType);
         photo.setExt(imageType);
         photo.setPath(this.configService.getSysConfig().getUploadFilePath() + "/avatar");
         if (user.getPhoto() == null)
             this.accessoryService.save(photo);
         else {
             this.accessoryService.update(photo);
         }
         user.setPhoto(photo);
         this.userService.update(user);
         return "redirect:/buyer/account_edit.htm";
     }

   private String saveImage(String filePath, String imageType, MultipartFile file, String avatarName) throws IOException {
     if (!file.isEmpty()) {
       if ("".equals(avatarName))
         avatarName = UUID.randomUUID().toString() + ".jpg";
       else {
         avatarName = avatarName + imageType;
       }
       try {
         File f = new File(filePath + File.separator + avatarName);
         BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
         out.write(file.getBytes());
         out.flush();
         out.close();
       } catch (FileNotFoundException e) {
         e.printStackTrace();
         return"上传失败,"+e.getMessage();

       }catch (IOException e) {
         e.printStackTrace();
         return"上传失败,"+e.getMessage();
       }
       return"上传成功";
     }else {
       return"上传失败，因为文件是空的.";
     }

   }
  /* private void saveImage(String filePath, String imageType, String avatarContent, String avatarName)
     throws IOException
   {
     avatarContent = CommUtil.null2String(avatarContent);
     if (!"".equals(avatarContent)) {
       if ("".equals(avatarName))
         avatarName = UUID.randomUUID().toString() +
           ".jpg";
       else {
         avatarName = avatarName + imageType;
       }
       //byte[] data = _decoder.decodeBuffer(avatarContent);
       byte[] data = Base64.decodeBase64(avatarContent);
       File f = new File(filePath + File.separator + avatarName);
       DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
       dos.write(data);
       dos.flush();
       dos.close();
     }
   }*/

   @SecurityMapping(display = false, rsequence = 0, title="手机号码修改", value="/buyer/account_mobile.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_mobile.htm"})
   public ModelAndView account_mobile(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView("user/second/49Buyer_P_Phone.html", this.configService.getSysConfig(),
       this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
     if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
       mv = new JModelAndView("newwap/68shoujibangding.html",
               this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     }

     User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
     mv.addObject("user",user);
     mv.addObject("url", CommUtil.getURL(request));
     return mv;
   }
//wap 手机绑定
     @RequestMapping({"/buyer/account_mobile_savewap.htm"})
     public void account_mobile_savewap(HttpServletRequest request, HttpServletResponse response,String sendYzm, String telephone,String yzm,String password){
         User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
         Map params= new HashMap();
         if (user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) {
             if (yzm != null && !"".equals(yzm) && sendYzm != null && !"".equals(sendYzm) && yzm.equals(sendYzm)) {
                 user.setTelephone(telephone);
                 this.userService.update(user);
                 params.put("jieguo", "手机绑定成功");

             } else {
                 params.put("jieguo", "验证码错误，手机绑定失败");
             }
         }else{
             params.put("jieguo", "密码输入错误，手机绑定失败");
         }
         response.setContentType("text/plain");
         response.setHeader("Cache-Control", "no-cache");
         response.setCharacterEncoding("UTF-8");
         try {
             PrintWriter writer = response.getWriter();
             writer.print(Json.toJson(params, JsonFormat.compact()));
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

   @SecurityMapping(display = false, rsequence = 0, title="手机号码保存", value="/buyer/account_mobile_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_mobile_save_wap.htm"})
   public ModelAndView account_mobile_save_wap(HttpServletRequest request, HttpServletResponse response, String sendYzm, String telephone,String yzm,String password) throws Exception {
     ModelAndView mv = new JModelAndView("wap/phoneSuccess.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     WebForm wf = new WebForm();
     User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
     if (user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) {
       if (yzm!=null&&!"".equals(yzm)&&sendYzm!=null&&!"".equals(sendYzm)&&yzm.equals(sendYzm)) {
         user.setTelephone(telephone);
         this.userService.update(user);
           mv.addObject("op_title", "手机绑定成功");
           mv.addObject("url", CommUtil.getURL(request) +
                   "/buyer/account.htm");
       } else {
         mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         mv.addObject("op_title", "验证码错误，手机绑定失败");
         mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_mobile.htm");
       }
     } else {
       mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       mv.addObject("op_title", "密码输入错误，手机绑定失败");
       mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_mobile.htm");
     }
     return mv;
   }
     @SecurityMapping(display = false, rsequence = 0, title="手机号码保存", value="/buyer/account_mobile_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
     @RequestMapping({"/buyer/account_mobile_save.htm"})
     public ModelAndView account_mobile_save(HttpServletRequest request, HttpServletResponse response, String sendYzm, String telephone,String yzm,String password) throws Exception {
         ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         WebForm wf = new WebForm();
         User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
         if (user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) {
             if (yzm!=null&&!"".equals(yzm)&&sendYzm!=null&&!"".equals(sendYzm)&&yzm.equals(sendYzm)) {
                 user.setTelephone(telephone);
                 this.userService.update(user);
                 mv.addObject("op_title", "手机绑定成功");
                 mv.addObject("url", CommUtil.getURL(request) +
                         "/buyer/account.htm");
             } else {
                 mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                 mv.addObject("op_title", "验证码错误，手机绑定失败");
                 mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_mobile.htm");
             }
         } else {
             mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
             mv.addObject("op_title", "密码输入错误，手机绑定失败");
             mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_mobile.htm");
         }
         return mv;
     }
   @SecurityMapping(display = false, rsequence = 0, title="手机短信发送", value="/buyer/account_mobile_sms.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_mobile_sms.htm"})
   public void account_mobile_sms(HttpServletRequest request, HttpServletResponse response, String telephone) {
     String yzm=(int)((Math.random()*9+1)*100000)+"";
     System.out.print("YI");
     String sign="%e9%a2%90%e4%bd%b3%e6%98%93%e8%b4%ad";
     String Msg="%e5%b0%8a%e6%95%ac%e7%9a%84%e7%94%a8%e6%88%b7%3a%e6%82%a8%e6%ad%a3%e5%9c%a8%e8%af%95%e5%9b%be%e4%bf%ae%e6%94%b9%e4%b8%aa%e4%ba%ba%e4%bf%a1%e6%81%af%2c%e6%82%a8%e7%9a%84%e6%89%8b%e6%9c%ba%e9%aa%8c%e8%af%81%e7%a0%81%e4%b8%ba"+yzm;
     String sr= HttpRequest.sendPost("http://manager.wxtxsms.cn/smsport/sendPost.aspx", "uid=haoxing&upsd="+ Str2MD5.MD532("00000000")+"&sendtele="+telephone+"&Msg="+Msg+"&sign="+sign);
     Map<String,String> map = new HashMap<>();
     map.put("yzm",yzm);
     System.out.print("验证码是:"+yzm);
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
 
   @SecurityMapping(display = false, rsequence = 0, title="好友管理", value="/buyer/friend.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/friend.htm"})
   public ModelAndView account_friend(HttpServletRequest request, HttpServletResponse response, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/account_friend.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     SnsFriendQueryObject qo = new SnsFriendQueryObject(currentPage, mv, 
       "addTime", "desc");
     qo.addQuery("obj.fromUser.id", 
       new SysMap("user_id", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     IPageList pList = this.sndFriendService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="好友添加", value="/buyer/friend_add.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/friend_add.htm"})
   public ModelAndView friend_add(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/account_friend_search.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     List areas = this.areaService.query(
       "select obj from Area obj where obj.parent.id is null", null, 
       -1, -1);
     mv.addObject("areas", areas);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="用户名", value="/buyer/account_user.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_user.htm"})
   public ModelAndView Account_User(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
             "user/second/46Buyer_P_User.html",
             this.configService.getSysConfig(), this.userConfigService
             .getUserConfig(), 0, request, response);
     List areas = this.areaService.query(
             "select obj from Area obj where obj.parent.id is null", null,
             -1, -1);
       mv.addObject("user", this.userService.getObjById(
               SecurityUserHolder.getCurrentUser().getId()));
     mv.addObject("areas", areas);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="搜索用户", value="/buyer/account_friend_search.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_friend_search.htm"})
   public ModelAndView friend_search(HttpServletRequest request, HttpServletResponse response, String userName, String area_id, String sex, String years, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/account_friend_search.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     UserQueryObject qo = new UserQueryObject(currentPage, mv, "addTime", 
       "desc");
     qo.addQuery("obj.userRole", new SysMap("userRole", "ADMIN"), "!=");
     if ((userName != null) && (!userName.equals(""))) {
       mv.addObject("userName", userName);
       qo.addQuery("obj.userName", 
         new SysMap("userName", "%" + userName + 
         "%"), "like");
     }
     if ((years != null) && (!years.equals(""))) {
       mv.addObject("years", years);
       if (years.equals("18")) {
         qo.addQuery("obj.years", 
           new SysMap("years", 
           Integer.valueOf(CommUtil.null2Int(years))), "<=");
       }
       if (years.equals("50")) {
         qo.addQuery("obj.years", 
           new SysMap("years", 
           Integer.valueOf(CommUtil.null2Int(years))), ">=");
       }
       if ((!years.equals("18")) && (!years.equals("50"))) {
         String[] y = years.split("~");
         qo.addQuery("obj.years", 
           new SysMap("years", 
           Integer.valueOf(CommUtil.null2Int(y[0]))), ">=");
         qo.addQuery("obj.years", 
           new SysMap("years2", 
           Integer.valueOf(CommUtil.null2Int(y[1]))), "<=");
       }
     }
     if ((sex != null) && (!sex.equals(""))) {
       mv.addObject("sex", sex);
       qo.addQuery("obj.sex", new SysMap("sex", Integer.valueOf(CommUtil.null2Int(sex))), 
         "=");
     }
     if ((area_id != null) && (!area_id.equals(""))) {
       Area area = this.areaService
         .getObjById(CommUtil.null2Long(area_id));
       mv.addObject("area", area);
       qo.addQuery("obj.area.id", 
         new SysMap("area_id", 
         CommUtil.null2Long(area_id)), "=");
     }
     qo.setPageSize(Integer.valueOf(18));
     qo.addQuery("obj.id", 
       new SysMap("user_id", 
       SecurityUserHolder.getCurrentUser().getId()), "!=");
     IPageList pList = this.userService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     List areas = this.areaService.query(
       "select obj from Area obj where obj.parent.id is null", null, 
       -1, -1);
     mv.addObject("areas", areas);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="好友添加", value="/buyer/friend_add_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/friend_add_save.htm"})
   public void friend_add_save(HttpServletRequest request, HttpServletResponse response, String user_id) {
     boolean flag = false;
     Map params = new HashMap();
     params.put("user_id", CommUtil.null2Long(user_id));
     params.put("uid", SecurityUserHolder.getCurrentUser().getId());
     List sfs = this.sndFriendService
       .query(
       "select obj from SnsFriend obj where obj.fromUser.id=:uid and obj.toUser.id=:user_id", 
       params, -1, -1);
     if (sfs.size() == 0) {
       SnsFriend friend = new SnsFriend();
       friend.setAddTime(new Date());
       friend.setFromUser(SecurityUserHolder.getCurrentUser());
       friend.setToUser(this.userService.getObjById(
         CommUtil.null2Long(user_id)));
       flag = this.sndFriendService.save(friend);
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(flag);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="好友删除", value="/buyer/friend_del.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/friend_del.htm"})
   public void friend_del(HttpServletRequest request, HttpServletResponse response, String id) {
     this.sndFriendService.delete(CommUtil.null2Long(id));
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(true);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="账号绑定", value="/buyer/account_bind.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   /*@RequestMapping({"/buyer/account_bind.htm"})*/
   public ModelAndView account_bind(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/account_bind.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     mv.addObject("user", user);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="账号解除绑定", value="/buyer/account_bind_cancel.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/account_bind_cancel.htm"})
   public String account_bind_cancel(HttpServletRequest request, HttpServletResponse response, String account) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/account_bind.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     if (CommUtil.null2String(account).equals("qq")) {
       user.setQq_openid(null);
     }
     if (CommUtil.null2String(account).equals("sina")) {
       user.setSina_openid(null);
     }
     this.userService.update(user);
     return "redirect:account_bind.htm";
   }
     @SecurityMapping(display = false, rsequence = 0, title="编辑银行卡地址", value="/buyer/address_edit.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
     @RequestMapping({"/buyer/bank_edit.htm"})
     public ModelAndView address_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {



         ModelAndView    mv = new JModelAndView("newwap/67bianjiyinhangka.html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request, response);


         Address obj = this.addressService.getObjById(CommUtil.null2Long(id));
         mv.addObject("obj", obj);

         return mv;
     }





   private void send_sms(HttpServletRequest request, String mark) throws Exception
   {
     com.shopping.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
     if ((template != null) && (template.isOpen())) {
       User user = this.userService.getObjById(
         SecurityUserHolder.getCurrentUser().getId());
       String mobile = user.getMobile();
       if ((mobile != null) && (!mobile.equals(""))) {
         String path = request.getSession().getServletContext()
           .getRealPath("/") + 
           "/vm/";
         PrintWriter pwrite = new PrintWriter(
           new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
         pwrite.print(template.getContent());
         pwrite.flush();
         pwrite.close();
 
         Properties p = new Properties();
         p.setProperty("file.resource.loader.path", request
           .getRealPath("/") + 
           "vm" + File.separator);
         p.setProperty("input.encoding", "UTF-8");
         p.setProperty("output.encoding", "UTF-8");
         Velocity.init(p);
         org.apache.velocity.Template blank = Velocity.getTemplate(
           "msg.vm", "UTF-8");
         VelocityContext context = new VelocityContext();
         context.put("user", user);
         context.put("config", this.configService.getSysConfig());
         context.put("send_time", CommUtil.formatLongDate(new Date()));
         context.put("webPath", CommUtil.getURL(request));
         StringWriter writer = new StringWriter();
         blank.merge(context, writer);
 
         String content = writer.toString();
         this.msgTools.sendSMS(mobile, content);
       }
     }
   }
 }

