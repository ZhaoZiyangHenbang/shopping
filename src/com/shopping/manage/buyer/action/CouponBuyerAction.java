 package com.shopping.manage.buyer.action;
 
 import com.alipay.api.domain.Data;
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.CouponInfoQueryObject;
 import com.shopping.foundation.service.ICouponInfoService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;

 import java.util.Date;
 import java.util.HashMap;
 import java.util.Map;

 @Controller
 public class CouponBuyerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private ICouponInfoService couponInfoService;
 
   @SecurityMapping(display = false, rsequence = 0, title="买家优惠券列表", value="/buyer/coupon.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/coupon.htm"})
   public ModelAndView coupon(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage,String status)
   {
     ModelAndView mv = new JModelAndView("user/second/53Buyer_Coupon.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
     CouponInfoQueryObject qo = new CouponInfoQueryObject(currentPage, mv, "addTime", "desc");
     qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
     qo.addQuery("obj.status",new SysMap("status",  CommUtil.null2Int(0)),"=");
     qo.addQuery("obj.deleteStatus",new SysMap("deleteStatus",  CommUtil.null2Boolean(false)),"=");

       qo.addQuery("obj.coupon.coupon_begin_time",new SysMap("coupon_begin_time",new Date()),"<=");
       qo.addQuery("obj.coupon.coupon_end_time",new SysMap("coupon_end_time",new Date()),">=");
       IPageList pList = this.couponInfoService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

     return mv;
   }
     @SecurityMapping(display = false, rsequence = 0, title="买家优惠券已使用", value="/buyer/coupon.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
     @RequestMapping({"/buyer/coupon_used.htm"})
     public ModelAndView coupon_used(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage)
     {
         ModelAndView mv = new JModelAndView("user/second/54Buyer_Coupon_Used.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
         CouponInfoQueryObject qo = new CouponInfoQueryObject(currentPage, mv, "addTime", "desc");
         qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
         qo.addQuery("obj.status",new SysMap("status",  CommUtil.null2Int(1)),"=");
        /* qo.addQuery("obj.deleteStatus",new SysMap("deleteStatus",  CommUtil.null2Boolean(false)),"=");*/
         IPageList pList = this.couponInfoService.list(qo);
         CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
         return mv;
     }
     @SecurityMapping(display = false, rsequence = 0, title="买家优惠券已过期", value="/buyer/coupon.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
     @RequestMapping({"/buyer/coupon_out.htm"})
     public ModelAndView coupon_out(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage)
     {
         ModelAndView mv = new JModelAndView("user/second/55Buyer_Coupon_Out.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
         CouponInfoQueryObject qo = new CouponInfoQueryObject(currentPage, mv, "addTime", "desc");
         qo.addQuery("obj.user.id",new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
         qo.addQuery("obj.deleteStatus",new SysMap("deleteStatus",  CommUtil.null2Boolean(false)),"=");
        /* qo.addQuery("obj.coupon.coupon_type",new SysMap("type",String.valueOf(0)),"=");*/
         Map map=new HashMap();
         map.put("coupon_end_time",new Date());
         qo.addQuery("obj.coupon.coupon_end_time<=:coupon_end_time ",map);
         IPageList pList = this.couponInfoService.list(qo);
         CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
         return mv;
     }
 }


 
 
 