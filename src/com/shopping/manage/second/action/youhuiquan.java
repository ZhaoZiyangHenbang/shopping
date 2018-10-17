package com.shopping.manage.second.action;

import com.shopping.core.annotation.SecurityMapping;
import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.WebForm;
import com.shopping.foundation.domain.*;
import com.shopping.foundation.domain.query.BargainGoodsQueryObject;
import com.shopping.foundation.domain.query.CouponQueryObject;
import com.shopping.foundation.service.*;
import com.shopping.view.web.tools.NavViewTools;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by acer on 2017/3/10.
 */


@Controller
public class youhuiquan {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private NavViewTools navTools;

    @Autowired
    private IBargainGoodsService bargainGoodsService;

    @Autowired
    private IGoodsClassService goodsClassService;

    @Autowired
    private ICouponService couponService;

    @Autowired
    private ICouponInfoService couponInfoService;

    @Autowired
    private IGoodsService goodsService;

     @RequestMapping({"/second/youhuiquan.htm"})
    public ModelAndView youHUiQuan(HttpServletRequest request, HttpServletResponse response,String currentPage,String coupon_uid , String orderBy, String coupon_type,String orderType, String coupon_name, String coupon_begin_time, String coupon_end_time) {
         ModelAndView mv = new JModelAndView("user/second/101youhuiquan.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
         String params = "";
         CouponQueryObject qo = new CouponQueryObject(currentPage, mv, orderBy, orderType);
         if (!CommUtil.null2String(coupon_name).equals("")) {
             qo.addQuery("obj.coupon_name", new SysMap("coupon_name", "%" + coupon_name + "%"), "like");
         }
         if (!CommUtil.null2String(coupon_begin_time).equals("")) {
             qo.addQuery("obj.coupon_begin_time", new SysMap("coupon_begin_time", CommUtil.formatDate(coupon_begin_time)), ">=");
         }
         if (!CommUtil.null2String(coupon_end_time).equals("")) {
             qo.addQuery("obj.coupon_end_time", new SysMap("coupon_end_time", CommUtil.formatDate(coupon_end_time)), "<=");
         }
         if (!CommUtil.null2String(coupon_type).equals("")) {
             qo.addQuery("obj.coupon_type", new SysMap("coupon_type", CommUtil.null2String(coupon_type)), "=");
         }
         if (!CommUtil.null2String(SecurityUserHolder.getCurrentUser().getId()).equals("")) {
             qo.addQuery("obj.ccuser.id", new SysMap("coupon_uid", CommUtil.null2Long(SecurityUserHolder.getCurrentUser().getId())), "=");
         }
         qo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
         IPageList pList = this.couponService.list(qo);
         CommUtil.saveIPageList2ModelAndView("", "", params, pList, mv);

         if (SecurityUserHolder.getCurrentUser()!=null) {
             User user=SecurityUserHolder.getCurrentUser();
             mv.addObject("user",user);
         }
         return mv;
        }
//领取优惠券

    @RequestMapping({"/second/youhuiquanlingqu.htm"})
    public void  youHUiQuanLing(HttpServletRequest request, HttpServletResponse response,String coupon_id ,String goods_id) {
        User user=SecurityUserHolder.getCurrentUser();
        Map params =new HashMap();
        params.put("coupon_id",CommUtil.null2Long(coupon_id));
        params.put("coupon_uid",CommUtil.null2Long(user.getId()));
        params.put("status",0);
        params.put("deleteStatus",CommUtil.null2Boolean(false));
        List coupon=this.couponInfoService.query("select obj from CouponInfo obj where obj.deleteStatus=:deleteStatus and obj.coupon.id = :coupon_id and obj.user.id= :coupon_uid and obj.status = :status",params,-1,-1);
        params.clear();
        if(coupon.size()>0){
            params.put("save","您已经领过该劵");
        }else {
        CouponInfo ci=new CouponInfo();
        ci.setAddTime(new Date());
        ci.setDeleteStatus(CommUtil.null2Boolean(false));
        ci.setStatus(CommUtil.null2Int(0));
        ci.setCoupon(this.couponService.getObjById(CommUtil.null2Long(coupon_id)));
        ci.setUser(user);
            if(null!=goods_id&&!"".equals(goods_id)){
                Goods goods=this.goodsService.getObjById(Long.valueOf(goods_id));
                ci.setGoods(goods);
            }

        this.couponInfoService.save(ci);
            params.put("save", "领券成功");
        }

        String ret = Json.toJson(params, JsonFormat.compact());
       response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }







    @RequestMapping({"/second/youhuiquanzengjia.htm"})
    public ModelAndView youHUiQuanZeng(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/second/102youhuiquan_zeng.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        if (SecurityUserHolder.getCurrentUser()!=null) {
            User user=SecurityUserHolder.getCurrentUser();
            mv.addObject("user",user);
        }
        return mv;
    }

    @RequestMapping({"/second/youhuiquanshanchu.htm"})
    public String youHUiQuanShan(HttpServletRequest request, HttpServletResponse response,String coupon_id) {
        Coupon cou=this.couponService.getObjById(Long.valueOf(coupon_id));
        cou.setDeleteStatus(CommUtil.null2Boolean(true));
        this.couponService.update(cou);
        return "redirect:/admin/coupon_list.htm";
    }
    @RequestMapping({"/second/youhuiquanbianji.htm"})
    public ModelAndView youHUiQuanbianji(HttpServletRequest request, HttpServletResponse response,String coupon_id) {
        ModelAndView mv = new JModelAndView("user/second/102youhuiquan_zeng.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        Coupon coupon=this.couponService.getObjById(Long.valueOf(coupon_id));
        mv.addObject("obj",coupon);
       return mv;
    }

    @RequestMapping({"/second/youhuiquanshanchu2.htm"})
    public String youHUiQuanShan2(HttpServletRequest request, HttpServletResponse response,String coupon_id) {
        Coupon co=this.couponService.getObjById(CommUtil.null2Long(coupon_id));
        co.setDeleteStatus(true);
        this.couponService.update(co);
        return "redirect:/second/youhuiquan.htm?coupon_type=1";
    }

    @RequestMapping({"/second/youhuiquansavee.htm"})
    public String youHUiQuanSave(HttpServletRequest request, HttpServletResponse response,String coupon_id) {
        WebForm wf = new WebForm();
        Coupon coupon =null;
        if(!"".equals(coupon_id)&&null != coupon_id){
            Coupon obj = this.couponService.getObjById(CommUtil.null2Long(coupon_id));
            coupon = (Coupon)wf.toPo(request, obj);
        }else {
            coupon = wf.toPo(request, Coupon.class);
            coupon.setAddTime(new Date());
        }

        if (SecurityUserHolder.getCurrentUser()!=null) {
            User user=SecurityUserHolder.getCurrentUser();
            coupon.setCcuser(user);
        }
        if(!"".equals(coupon_id)&&null != coupon_id){
            this.couponService.update(coupon);
        }else
        this.couponService.save(coupon);
        return "redirect:/second/youhuiquan.htm?coupon_type=1";
    }


}
