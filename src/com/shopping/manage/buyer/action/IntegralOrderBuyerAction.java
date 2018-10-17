 package com.shopping.manage.buyer.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.*;
 import com.shopping.foundation.domain.query.IntegralGoodsOrderQueryObject;
 import com.shopping.foundation.domain.query.IntegralLogQueryObject;
 import com.shopping.foundation.service.*;

 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.core.annotation.Order;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class IntegralOrderBuyerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IIntegralGoodsService integralGoodsService;
 
   @Autowired
   private IIntegralGoodsOrderService integralGoodsOrderService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IIntegralLogService integralLogService;

    @Autowired
    private IIntegralLogService integrallogService;
     @Autowired
     private IOrderFormService orderFormService;
 
   @SecurityMapping(display = false, rsequence = 0, title="买家订单列表", value="/buyer/integral_order_list.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/integral_order_list.htm"})
   public ModelAndView integral_order_list(HttpServletRequest request, HttpServletResponse response,String shengxiao, String currentPage, String orderBy, String orderType)
   {
       ModelAndView mv = mv = new JModelAndView("user/second/56Buyer_Integra.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

//       if("0".equals(shengxiao)) {
//            mv = new JModelAndView("user/second/56Buyer_Integra.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
//       }
       if("1".equals(shengxiao)) {
           mv = new JModelAndView("user/second/56Buyer_Integra_2.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
       }

     /*if (this.configService.getSysConfig().isIntegralStore()) {
       IntegralGoodsOrderQueryObject qo = new IntegralGoodsOrderQueryObject(currentPage, mv, "addTime", "desc");
       qo.addQuery("obj.igo_user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
       IPageList pList = this.integralGoodsOrderService.list(qo);
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "系统未开启积分商城");
       mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
     }*/
       String url = this.configService.getSysConfig().getAddress();
       if ((url == null) || (url.equals(""))) {
           url = CommUtil.getURL(request);
       }
       String userName=SecurityUserHolder.getCurrentUser().getUserName();
       String params = "";
       IntegralLogQueryObject qo = new IntegralLogQueryObject(currentPage, mv, orderBy, orderType);
       if ((userName != null) && (!userName.equals("")))
           qo.addQuery("obj.integral_user.userName", new SysMap("userName", userName), "=");

       qo.addQuery("obj.isshengxiao", new SysMap("isshengxiao",shengxiao), "=");
       IPageList pList = this.integrallogService.list(qo);
     /*  OrderForm order= this.orderFormService.getObjById(SecurityUserHolder.getCurrentUser().getId());*/
       Map map=new HashMap();
       map.put("id",SecurityUserHolder.getCurrentUser().getId());
           List order= this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:id",map,-1,-1);
       int jifen=SecurityUserHolder.getCurrentUser().getIntegral();
       mv.addObject("jifen",jifen);
       mv.addObject("order",order);
       CommUtil.saveIPageList2ModelAndView(
               url + "/admin/integrallog_list.htm", "", "&userName=" +
                       CommUtil.null2String(userName), pList, mv);
     return mv;
   }



   @SecurityMapping(display = false, rsequence = 0, title="取消订单", value="/buyer/integral_order_cancel.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/integral_order_cancel.htm"})
   public ModelAndView integral_order_cancel(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView("success.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     IntegralGoodsOrder obj = this.integralGoodsOrderService
       .getObjById(CommUtil.null2Long(id));
     if (obj != null)
     {
       if (obj.getIgo_user().getId().equals(
         SecurityUserHolder.getCurrentUser().getId())) {
         obj.setIgo_status(-1);
         this.integralGoodsOrderService.update(obj);
         for (IntegralGoodsCart igc : obj.getIgo_gcs()) {
           IntegralGoods goods = igc.getGoods();
           goods.setIg_goods_count(goods.getIg_goods_count() + 
             igc.getCount());
           this.integralGoodsService.update(goods);
         }
         User user = obj.getIgo_user();
         user.setIntegral(user.getIntegral() + obj.getIgo_total_integral());
         this.userService.update(user);
         IntegralLog log = new IntegralLog();
         log.setAddTime(new Date());
         log.setContent("取消" + obj.getIgo_order_sn() + "积分兑换，返还积分");
         log.setIntegral(obj.getIgo_total_integral());
         log.setIntegral_user(obj.getIgo_user());
         log.setOperate_user(SecurityUserHolder.getCurrentUser());
         log.setType("integral_order");
         this.integralLogService.save(log);
         mv.addObject("op_title", "积分兑换取消成功");
         mv.addObject("url", CommUtil.getURL(request) + 
           "/buyer/integral_order_list.htm");
       }
     }mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, 
       response);
     mv.addObject("op_title", "参数错误，无该订单");
     mv.addObject("url", CommUtil.getURL(request) + 
       "/buyer/integral_order_list.htm");
 
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="积分订单详情", value="/buyer/integral_order_view.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/integral_order_view.htm"})
   public ModelAndView integral_order_view(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/integral_order_view.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     IntegralGoodsOrder obj = this.integralGoodsOrderService
       .getObjById(CommUtil.null2Long(id));
     if (obj != null)
     {
       if (obj.getIgo_user().getId().equals(
         SecurityUserHolder.getCurrentUser().getId())) {
         mv.addObject("obj", obj);
         mv.addObject("currentPage", currentPage);
       }
     }mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, 
       response);
     mv.addObject("op_title", "参数错误，无该订单");
     mv.addObject("url", CommUtil.getURL(request) + 
       "/buyer/integral_order_list.htm");
 
     label172: return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="确认收货", value="/buyer/integral_order_cofirm.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/integral_order_cofirm.htm"})
   public ModelAndView integral_order_cofirm(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/integral_order_cofirm.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     IntegralGoodsOrder obj = this.integralGoodsOrderService
       .getObjById(CommUtil.null2Long(id));
     if (obj != null)
     {
       if (obj.getIgo_user().getId().equals(
         SecurityUserHolder.getCurrentUser().getId())) {
         mv.addObject("obj", obj);
       }
     }mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, 
       response);
     mv.addObject("op_title", "参数错误，无该订单");
     mv.addObject("url", CommUtil.getURL(request) + 
       "/buyer/integral_order_list.htm");
 
     label161: return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="确认收货保存", value="/buyer/integral_order_cofirm_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/integral_order_cofirm_save.htm"})
   public ModelAndView integral_order_cofirm_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView("success.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     IntegralGoodsOrder obj = this.integralGoodsOrderService
       .getObjById(CommUtil.null2Long(id));
     if (obj != null)
     {
       if (obj.getIgo_user().getId().equals(
         SecurityUserHolder.getCurrentUser().getId())) {
         obj.setIgo_status(40);
         this.integralGoodsOrderService.update(obj);
         mv.addObject("op_title", "确认收货成功");
         mv.addObject("url", CommUtil.getURL(request) + 
           "/buyer/integral_order_list.htm");
       }
     }mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, 
       response);
     mv.addObject("op_title", "参数错误，无该订单");
     mv.addObject("url", CommUtil.getURL(request) + 
       "/buyer/integral_order_list.htm?currentPage=" + 
       currentPage);
 
     label215: return mv;
   }
     @SecurityMapping(display = false, rsequence = 0, title="确认收货", value="/buyer/integral_order_cofirm.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
     @RequestMapping({"/buyer/integral_regul.htm"})
     public ModelAndView integral_regu(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
         ModelAndView mv = new JModelAndView("user/second/57Buyer_Integra_Regu.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
         //IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
         int jifen=SecurityUserHolder.getCurrentUser().getIntegral();
         mv.addObject("jifen",jifen);

         mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm" + currentPage);
     return mv;
     }
 }


 
 
 