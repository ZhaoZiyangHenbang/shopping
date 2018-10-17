 package com.shopping.view.web.action;
 
 import java.io.IOException;
 import java.math.BigDecimal;
 import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

 import com.shopping.foundation.service.*;
 import com.shopping.view.web.tools.NavViewTools;
 import org.apache.commons.httpclient.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
 import org.springframework.ui.Model;
 import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.mv.JModelAndView;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.Md5Encrypt;
import com.shopping.foundation.domain.Album;
import com.shopping.foundation.domain.IntegralLog;
import com.shopping.foundation.domain.User;
 import com.shopping.uc.api.UCClient;
import com.shopping.uc.api.UCTools;
import com.shopping.view.web.tools.ImageViewTools;
 
 @Controller
 public class LoginViewAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;

     @Autowired
     private IGoodsClassService goodsClassService;
 
   @Autowired
   private IRoleService roleService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IIntegralLogService integralLogService;
 
   @Autowired
   private IAlbumService albumService;

     @Autowired
     private NavViewTools navTools;
 
   @Autowired
   private ImageViewTools imageViewTools;
 
   @Autowired
   private UCTools ucTools;

     @Autowired
     private IArticleService articleService;
 
   /**
	 * 用户登录跳转页面
	 * @param request
	 * @param response
	 * @param url
	 * @return
	 */
   @RequestMapping({"/user/login.htm"})
   public ModelAndView login(HttpServletRequest request, HttpServletResponse response, String url) {
     ModelAndView mv = new JModelAndView("Login_N.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     String shopping_view_type = CommUtil.null2String(request.getSession(false).getAttribute("shopping_view_type"));
     if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("wap"))) {
    	 mv = new JModelAndView("newwap/Login.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     }
     request.getSession(false).removeAttribute("verify_code");
     boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
     if ((url != null) && (!url.equals(""))) {
       request.getSession(false).setAttribute("refererUrl", url);
     }
     if (domain_error) {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("wap"))) {
    	   mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       }
     }
     else {
       mv.addObject("imageViewTools", this.imageViewTools);
     }
     mv.addObject("uc_logout_js", request.getSession(false).getAttribute("uc_logout_js"));
     return mv;
   }

   /**
	 * 注册跳转页面
	 * @param request
	 * @param response
	 * @return
	 */
   @RequestMapping({"/register.htm"})
   public ModelAndView register(HttpServletRequest request, HttpServletResponse response,String info,String yqm) {
       ModelAndView mv =null;
        String shopping_view_type = CommUtil.null2String(request.getSession(false).getAttribute("shopping_view_type"));
       if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("pc"))) {
            mv = new JModelAndView("Register_N.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
           String view=request.getParameter("shopping_view_type");
           if((view != null) && (!view.equals("")) && (view.equals("wap"))) {
               shopping_view_type="wap";
           }
       }
	 if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("wap"))) {
		 mv = new JModelAndView("newwap/register.html", this.configService.getSysConfig(),
			       this.userConfigService.getUserConfig(), 1, request, response);
	 }

     request.getSession(false).removeAttribute("verify_code");
       if (info!=null) {
           if ("1".equals(info)) {
               mv.addObject("info","该手机号已经注册!");
           }else{
               mv.addObject("info","验证码输入错误!");
           }
       }
       if(null!=yqm&&!"".equals(yqm)){
           mv.addObject("yqm",yqm);
       }else{
           User user=(User)request.getSession().getAttribute("P_user");
           if(null!=user){
               yqm=user.getId().toString();
               mv.addObject("yqm",yqm);
           }

       }



     return mv;
   }
     /*@RequestMapping({"/xieyi.htm"})
     public ModelAndView xieyi(HttpServletRequest request, HttpServletResponse response){
         ModelAndView mv = new JModelAndView("", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         String shopping_view_type = CommUtil.null2String(request.getSession(false).getAttribute("shopping_view_type"));
         if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("wap"))) {
              mv = new JModelAndView("newwap/RegistrationProtocol .html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request, response);
         }


         return mv;
     }*/
     @RequestMapping({"/wangji.htm"})
     public ModelAndView wangji(HttpServletRequest request, HttpServletResponse response){
         ModelAndView  mv = new JModelAndView("newwap/55wangjimima.html", this.configService.getSysConfig(),
                 this.userConfigService.getUserConfig(), 1, request, response);

         return mv;
     }
   /**
	 * 注册保存
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param email
	 * @param code
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
       @RequestMapping({"/register_finish.htm"})
   public String register_finish(HttpServletRequest request, HttpServletResponse response,String telephone,String yqm, String userName, String password, String email, String code,String number
       ,String yzm,String sendYzm) throws IOException {
    boolean reg = true;
     if (yzm!=null&&!"".equals(yzm)&&sendYzm!=null&&!"".equals(sendYzm)&&yzm.equals(sendYzm)) {
         Map params = new HashMap();
         params.put("telephone", telephone);
         List users = this.userService.query("select obj from User obj where obj.telephone=:telephone", params, -1, -1);
         if ((users != null) && (users.size() > 0)) {
             reg = false;
         }
         params.clear();
         params.put("userName", telephone);
         List users1 = this.userService.query("select obj from User obj where obj.userName=:userName", params, -1, -1);
         if ((users1 != null) && (users1.size() > 0)) {
             reg = false;
         }
         if (reg) {
             User user = new User();
             user.setUserName(telephone);
             user.setTelephone(telephone);
             user.setPay_money(new BigDecimal(0));
             if(null!=yqm&&!"".equals(yqm)){
                 params.clear();
                 params.put("user_id",CommUtil.null2Long(yqm));
                 //us是上级用户
                 List<User> us = this.userService.query("select obj from User obj where obj.id=:user_id", params, -1, -1);
                 if (null!=us&&us.size()>0) {
                     //  user.setParent(us.get(0));
                     //获取上级用户的pid和ppid
                     user.setParent_number(CommUtil.null2String(us.get(0).getId()));
                     if(null!=us.get(0).getParent_number()&&!"".equals(us.get(0).getParent_number())){
                         user.setParent_p_number(CommUtil.null2String(us.get(0).getParent_number()));
                     }
                     if(null!=us.get(0).getParent_pp_number()&&!"".equals(us.get(0).getParent_p_number())){
                         user.setParent_pp_number(CommUtil.null2String(us.get(0).getParent_p_number()));
                     }

                 }
             }

             //根据推荐码查出所有子用户，算出推荐人等级存到user表里,
         /*    List<User> us2 = this.userService.query("select obj from User obj where obj.parent.id=:user_id", params, -1, -1);
             int num=us2.size()+1;
            //获取等级规则进行比较得出加上新注册的人是否改变等级*/

             user.setUserRole("BUYER");
             user.setAddTime(new Date());
             user.setPassword(Md5Encrypt.md5(password).toLowerCase());
             params.clear();
             params.put("type", "BUYER");
             List roles = this.roleService.query("select obj from Role obj where obj.type=:type", params, -1, -1);
             user.getRoles().addAll(roles);

             this.userService.save(user);

     /*  return "redirect:shopping_login.htm?telephone=" + CommUtil.encode(telephone) + "&password=" + password + "&encode=true";*/
             return "redirect:user/login.htm";
         }
         return "redirect:register.htm?info=1";
     }else {
         return "redirect:register.htm?info=2";
     }
   }
   
   /**
	 * 登录操作成功之后跳转判断
	 * @param request
	 * @param response
	 * @return
	 */
   @RequestMapping({"/user_login_success.htm"})
   public ModelAndView user_login_success(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     String url = CommUtil.getURL(request) + "/index.htm";
     String shopping_view_type = CommUtil.null2String(request.getSession(false).getAttribute("shopping_view_type"));
     //跳转到微信端
     if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("wap"))) {
       String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
       mv = new JModelAndView("wap/success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       url = CommUtil.getURL(request) + "/wap/index.htm?store_id=" + store_id;
     }
     HttpSession session = request.getSession(false);
     if ((session.getAttribute("refererUrl") != null) && (!session.getAttribute("refererUrl").equals(""))) {
       url = (String)session.getAttribute("refererUrl");
       session.removeAttribute("refererUrl");
     }
     if (this.configService.getSysConfig().isUc_bbs()) {
       String uc_login_js = CommUtil.null2String(request.getSession(false).getAttribute("uc_login_js"));
       mv.addObject("uc_login_js", uc_login_js);
     }
     //第三方登录：QQ、新浪等
     /*String bind = CommUtil.null2String(request.getSession(false).getAttribute("bind"));
     if (!bind.equals("")) {
    	 mv = new JModelAndView(bind + "_login_bind.html", 
         this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, response);
       User user = SecurityUserHolder.getCurrentUser();
       mv.addObject("user", user);
       request.getSession(false).removeAttribute("bind");
     }*/


       mv.addObject( "navTools", this.navTools );
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "display", Boolean.valueOf( true ) );
		params.put( "bc_location", "1" );
		params.put( "deleteStatus", CommUtil.null2Boolean(false) );
		List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null and obj.display=:display and obj.bc_location=:bc_location order by obj.sequence asc", params, 0, 14 );
		/*List childs = ((GoodsClass)gcs.get(0)).getChilds();*/
		mv.addObject( "gcs", gcs );
     mv.addObject("op_title", "登录成功");
     mv.addObject("url", url);
     return mv;
   }
   /**
	 * 登录模态窗口
	 * @param request
	 * @param response
	 * @return
	 */
   @RequestMapping({"/user_dialog_login.htm"})
   public ModelAndView user_dialog_login(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView("user_dialog_login.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     return mv;
   }
   
   
   /** wap登录业务逻辑begin */
   
    @RequestMapping({ "/user/wap/login.htm" })
	public ModelAndView waplogin(HttpServletRequest request, HttpServletResponse response, String url) {
		ModelAndView mv = new JModelAndView("wap/login.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
			if ((url != null) && (!url.equals(""))) {
			request.getSession(false).setAttribute("refererUrl", url);
		}
		if (domain_error)
			mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
		else {
			mv.addObject("imageViewTools", this.imageViewTools);
		}
		mv.addObject("uc_logout_js", request.getSession(false).getAttribute("uc_logout_js"));

		/*St ring shopping_view_type = CommUtil.null2String(request.getSession(false).getAttribute("shopping_view_type"));
 		}

		if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("wap"))) {
			//String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
			mv = new JModelAndView("wap/success.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "成功！");
			mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
		}*/

		return mv;
	}
}


 
 
 