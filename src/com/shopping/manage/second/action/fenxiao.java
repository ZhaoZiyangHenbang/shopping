package com.shopping.manage.second.action;

import com.shopping.core.annotation.SecurityMapping;
import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.fenXiaoUtil;
import com.shopping.foundation.domain.*;
import com.shopping.foundation.domain.query.CombinLogQueryObject;
import com.shopping.foundation.domain.query.OrderFormQueryObject;
import com.shopping.foundation.service.*;
import com.shopping.view.web.tools.ImageViewTools;
import com.shopping.view.web.tools.NavViewTools;
import com.shopping.view.web.tools.OrderViewTools;
import com.shopping.view.web.tools.StoreViewTools;
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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/3/14.
 */
@Controller
public class fenxiao {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IOrderFormService orderFormService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IGoodsCartService goodsCartService;

    @Autowired
    private ICombinLogService combinLogService;

    @Autowired
    private IAddressService addressService;

    @Autowired
    private StoreViewTools storeViewTools;

    @Autowired
    private OrderViewTools orderViewTools;

    @Autowired
    private ImageViewTools imageViewTools;

    DecimalFormat df=new DecimalFormat("0.00");
      @RequestMapping({"/second/fenxiao.htm"})
    public ModelAndView fenxiao(HttpServletRequest request, HttpServletResponse response,String url){
        ModelAndView mv = new JModelAndView("user/second/61fenxiao.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        User user= SecurityUserHolder.getCurrentUser();
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
            if(null == user){
                mv = new JModelAndView("newwap/Login.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                request.getSession(false).removeAttribute("verify_code");
                boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
                if ((url != null) && (!url.equals(""))) {
                    request.getSession(false).setAttribute("refererUrl", url);
                }
                return mv;
            }else{
                mv = new JModelAndView("newwap/maijiazhongxin,woshifenxiaoshang.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            }
        }
        //销售提成（已完成的）
        BigDecimal finish = new BigDecimal("0");
        //下线提成（全部下级的）
        BigDecimal junior = new BigDecimal("0");
        BigDecimal junior2 = new BigDecimal("0");
        BigDecimal junior3 = new BigDecimal("0");
        BigDecimal junior4 = new BigDecimal("0");
        //预计提成（付完款的提成）
        BigDecimal predict = new BigDecimal("0");
        BigDecimal predict0 = new BigDecimal("0");
        BigDecimal predict2 = new BigDecimal("0");
        BigDecimal predict3 = new BigDecimal("0");
        BigDecimal predict4 = new BigDecimal("0");
        //分销商
        Integer fenXiaoShang = null;

        //普通会员不参与分销 zzy提成，但是消费后上三级有分销提成
       // if(0 != user.getUser_credit()){
            //一级分销的订单(已完成)zzy 相当于自己的订单
            List<OrderForm> orderList = this.orderList("0",user.getId());
            //一级分销的订单(预计)
            List<OrderForm> orderListYJ = this.orderList("1",user.getId());

            //添加到销售提成
            finish = this.addMoney(orderList,user,finish,"1");
            //添加到预计提成
            predict0 = this.addMoney(orderListYJ,user,predict,"1");



            if (user != null) {
                Map params = new HashMap();
                params.put("user_id", CommUtil.null2String(user.getId()));
                //二级的分销人
                List<User> user1 =this.userService.query("select obj from User obj where obj.parent_number =:user_id", params, -1, -1);
                if(user1.size()!=0){
                    for (User u1:user1) {
                        //二级分销的订单(已完成)
                        List<OrderForm> orderForm1 = this.orderList("0", u1.getId());
                        //二级分销的订单(预计)
                        List<OrderForm> orderFormYJ1 = this.orderList("1", u1.getId());

                        //二级分销下线提成
                        junior2 = this.addMoney(orderForm1, user, junior, "2");
                        //二级分销预计提成
                        predict2 = this.addMoney(orderFormYJ1, user, predict, "2");
                    }
                    //三级的分销人
                      Map params1 = new HashMap();
                        params1.put("user_id", CommUtil.null2String(user.getId()));
                        List<User> user2 = this.userService.query("select obj from User obj where obj.parent_p_number=:user_id", params1, -1, -1);
                    if(null !=user2) {
                        for (User u2 : user2) {
                            //三级分销的订单(已完成)
                            List<OrderForm> orderForm2 = this.orderList("0", u2.getId());
                            //三级分销的订单(预计)
                            List<OrderForm> orderFormYJ2 = this.orderList("1", u2.getId());
                            //添加到下线提成
                            junior3 = this.addMoney(orderForm2, user, junior, "3");
                            //添加到预计提成
                            predict3 = this.addMoney(orderFormYJ2, user, predict, "3");

                        }
                    }

                    //四级级分销（如果四级分销为普通会员，无法参加分销，获得的钱给一级分校的人）
                    Map params2 = new HashMap();
                    params2.put("user_id", CommUtil.null2String(user.getId()));
                    List<User> user3 = this.userService.query("select obj from User obj where obj.parent_pp_number=:user_id", params2, -1, -1);
                    if(null !=user3){
                        for (User u3:user3) {
                            if(0 == u3.getUser_credit()){
                                //四级分销的订单(已完成)
                                List<OrderForm> orderForm3 = this.orderList("0",u3.getId());
                                //四级分销的订单(预计)
                                List<OrderForm> orderFormYJ3 = this.orderList("1",u3.getId());
                                //添加到下线提成
                                junior4 = this.addMoney(orderForm3,user,junior,"1");
                                //添加到预计提成
                                predict4 = this.addMoney(orderFormYJ3,user,predict,"1");
                            }
                        }
                    }

                }
            }
          //分销商数量，+1就是数量加上自己
        List<Long> list1 = this.getUserListByStarLevel("2");
        List<Long> list2 = this.getUserListByStarLevel("3");
        fenXiaoShang = list1.size()+list2.size()+1;
        mv.addObject("fenXiaoShang",fenXiaoShang);
          //粉丝团数量
        mv.addObject("fans",this.getFans().size());
          //销售提成
          mv.addObject("finish",finish.doubleValue());
         // 下线提成
          junior=junior2.add(junior3).add(junior4);
          mv.addObject("junior",junior.doubleValue());
          //我的收益（销售提成+下级销售提成）
          mv.addObject("sum",finish.add(junior).doubleValue());
          //获取推荐码
          mv.addObject("user",user);
          //预计提成（应该是自己的预计提成加上三级下线的所有预计提成）
          predict=predict0.add(predict2).add(predict3).add(predict4);
          mv.addObject("predict",predict.doubleValue());
          //已发提成
          mv.addObject("getMoney",this.getMoney().doubleValue());
//暂时没用   是获取可提现金额
          //  mv.addObject("mayWithdraw",this.getTiChengSumMoney().doubleValue());
            mv.addObject("storeViewTools", this.storeViewTools);

        return mv;
    }
    /**
     * 销售提成
     * @param request
     * @param response
     * @param currentPage
     * @param type 0，本月 1，上月 2，全部
     * @param beginTime
     * @param endTime
     * @param userId
     * @return
     */
    //上月销售额度，就是上个月自己买商品的所有订单的一级提成
    @RequestMapping({"/second/xiaoshouticheng.htm"})
    public ModelAndView xiaoshouticheng(HttpServletRequest request, HttpServletResponse response,String currentPage,String type,String beginTime,String endTime,String userId){
        ModelAndView mv = new JModelAndView("user/second/62fen_xiaoshou_ben_shang.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
            mv = new JModelAndView("newwap/fen_xiaoshou_ben_shang.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        User user= SecurityUserHolder.getCurrentUser();
        OrderFormQueryObject ofq=new OrderFormQueryObject( currentPage,mv,"","");
        if(null != userId){
            ofq.addQuery("obj.user.id",new SysMap("user_id",Long.valueOf(userId)),"=");
        }else{
            ofq.addQuery("obj.user.id",new SysMap("user_id",user.getId()),"=");
        }
        ofq.addQueryIn("obj.order_status",new SysMap("order_status", this.getOrderType1()), "in");
        Map map = this.getLastDate(type);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date firstDay = null;
        Date lastDay = null;
        Date beginTimeDate = null;
        Date endTimeDate  = null;
        try {
            firstDay = format1.parse((String) map.get("firstDay"));
            lastDay = format1.parse((String)map.get("lastDay"));
            if(null != beginTime && null != endTime){
                beginTimeDate = format1.parse(beginTime+" 00:00:01");
                endTimeDate = format1.parse(endTime+" 23:59:59");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //当为上月和本月的时候
        if(null != type && !type.equals("2")){
            ofq.addQuery("obj.addTime",new SysMap("firstDay",firstDay), ">=");
            ofq.addQuery("obj.addTime",new SysMap("lastDay",lastDay), "<=");
        }
        //当为全部  并且时间查询的时候
        if(null != type && type.equals("2")){
            if(null != beginTime && null != endTime){
                ofq.addQuery("obj.addTime",new SysMap("firstDay",beginTimeDate), ">=");
                ofq.addQuery("obj.addTime",new SysMap("lastDay",endTimeDate), "<=");
            }
        }
        //分页后的订单
        IPageList pList = this.orderFormService.list(ofq);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = null;
        if(null != userId){
            if(null != type && type.equals("2") && null != beginTime && null != endTime){
                params = "&type="+type+"&beginTime="+beginTime+"&endTime="+endTime+"&userId="+userId;
            }else{
                params = "&type="+type+"&userId="+userId;
            }
        }else{
            if(null != type && type.equals("2") && null != beginTime && null != endTime){
                params = "&type="+type+"&beginTime="+beginTime+"&endTime="+endTime;
            }else{
                params = "&type="+type;
            }
        }
        CommUtil.saveIPageList2ModelAndView(url+"/second/xiaoshouticheng.htm", "", params, pList, mv);
        //全部的订单
       // IPageList pListAll = this.orderFormService.allList(ofq);
        IPageList pListAll = this.orderFormService.list(ofq);
        List<OrderForm> li = pListAll.getResult();
        //总价
        BigDecimal sumMoney = new BigDecimal("0");
        if(null != li){
            //销量  也就是订单总数
            mv.addObject("num",li.size());
            for (OrderForm order:li) {
                //总价
                sumMoney = sumMoney.add(order.getTotalPrice());
            }
        }else{
            mv.addObject("num",0);
        }
        BigDecimal sumTiCheng = new BigDecimal("0");
        mv.addObject("sumTiCheng",this.addMoney(li,user,sumTiCheng,"1").doubleValue());
        mv.addObject("sumMoney",sumMoney.doubleValue());
        if(null== userId){
            userId = user.getId()+"";
        }
        mv.addObject("userId",userId);
        mv.addObject("type",type);
        mv.addObject("beginTime",beginTime);
        mv.addObject("endTime",endTime);
        return mv;
    }





    /**
     * 下线提成
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"/second/xiaxianticheng.htm"})
    public ModelAndView xiaxianticheng(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("user/second/64fen_xiaxianticheng.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
            mv = new JModelAndView("newwap/fen_xiaxianticheng.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        //要返回页面的集合
        List<User> allUser = new ArrayList<>();
        User user= SecurityUserHolder.getCurrentUser();
        //二级普通会员
        if(null!=user) {
            Map params1 = new HashMap();
            params1.put("user_id", CommUtil.null2String(user.getId()));
          //  params1.put("deleteStatus", CommUtil.null2Boolean(false));
            params1.put("user_credit", CommUtil.null2Int(0));
            List<User> user5 = this.userService.query("select obj from User obj where obj.parent_number=:user_id and obj.user_credit=:user_credit", params1, -1, -1);
            BigDecimal a = null;
            if (user5.size() > 0) {
                for (User u : user5) {
                    params1.clear();
                    params1.put("id", u.getId());
                   // params1.put("deleteStatus", CommUtil.null2Boolean(false));
                    params1.put("order_status", this.getOrderType1());
                    List<OrderForm> list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:id and obj.order_status in (:order_status)", params1, -1, -1);
                    if (list.size() > 0) {
                        u.setOrder_list_num(CommUtil.null2String(list.size()));
                        //临时的存储ticheng
                        BigDecimal bi = addMoney(list, user, null, "1");
                        u.setTicheng(bi);

                        for (OrderForm of : list) {
                            BigDecimal b = of.getTotalPrice();
                            a = a.add(b);
                        }
                        u.setPay_money(a);
                        u.setRank("普通会员");
                        allUser.add(u);
                    }

                }
            }

            //二级的分销商
            params1.clear();
            params1.put("user_id",  CommUtil.null2String(user.getId()));
          //  params1.put("deleteStatus", CommUtil.null2Boolean(false));
           // params1.put("user_credit", CommUtil.null2Int(0));
            List<User> user2 = this.userService.query("select obj from User obj where obj.parent_number=:user_id and obj.user_credit !=0", params1, -1, -1);
            if (user2.size() > 0) {
                for (User u : user2) {
                    params1.clear();
                    Long user_id = u.getId();
                    params1.put("id", u.getId());
                  //  params1.put("deleteStatus", CommUtil.null2Boolean(false));
                    params1.put("order_status", this.getOrderType1());
                    List<OrderForm> list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:id and obj.order_status in (:order_status)", params1, -1, -1);
                    if (list.size() > 0) {
                        u.setOrder_list_num(CommUtil.null2String(list.size()));
                        //临时的存储ticheng
                        BigDecimal bi = addMoney(list, user, null, "2");
                        u.setTicheng(bi);

                        for (OrderForm of : list) {
                            BigDecimal b = of.getTotalPrice();
                            a = a.add(b);
                        }
                        u.setPay_money(a);
                        u.setRank("二级分销商");
                        allUser.add(u);
                    }

                }
            }
            //三级的普通会员
            params1.clear();
            params1.put("user_id",  CommUtil.null2String(user.getId()));
           // params1.put("deleteStatus", CommUtil.null2Boolean(false));
            params1.put("user_credit", 0);
            List<User> user6 = this.userService.query("select obj from User obj where obj.parent_p_number=:user_id and obj.user_credit=:user_credit", params1, -1, -1);
            if (user6.size() > 0) {
                for (User u : user6) {
                    params1.clear();
                    Long user_id = u.getId();
                    params1.put("id", u.getId());
                 //   params1.put("deleteStatus", CommUtil.null2Boolean(false));
                    params1.put("order_status", this.getOrderType1());
                    List<OrderForm> list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:id and order_status in (:order_status)", params1, -1, -1);
                    if (list.size() > 0) {
                        u.setOrder_list_num(CommUtil.null2String(list.size()));
                        //临时的存储ticheng
                        BigDecimal bi = addMoney(list, user, null, "2");
                        u.setTicheng(bi);

                        for (OrderForm of : list) {
                            BigDecimal b = of.getTotalPrice();
                            a = a.add(b);
                        }
                        u.setPay_money(a);
                        u.setRank("普通会员");
                        allUser.add(u);
                    }

                }
            }
            //三级分销商
            params1.clear();
            params1.put("user_id",  CommUtil.null2String(user.getId()));
         //   params1.put("deleteStatus", CommUtil.null2Boolean(false));
           // params1.put("user_credit", 0);
            List<User> user3 = this.userService.query("select obj from User obj where obj.parent_p_number=:user_id and obj.user_credit!=0", params1, -1, -1);

            if (user3.size() > 0) {
                for (User u : user3) {
                    params1.clear();
                    Long user_id = u.getId();
                    params1.put("id", u.getId());
                 //   params1.put("deleteStatus", CommUtil.null2Boolean(false));
                    params1.put("order_status", this.getOrderType1());
                    List<OrderForm> list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:id and obj.order_status in (:order_status)", params1, -1, -1);
                    if (list.size() > 0) {
                        u.setOrder_list_num(CommUtil.null2String(list.size()));
                        //临时的存储ticheng
                        BigDecimal bi = addMoney(list, user, null, "3");
                        u.setTicheng(bi);

                        for (OrderForm of : list) {
                            BigDecimal b = of.getTotalPrice();
                            a = a.add(b);
                        }
                        u.setPay_money(a);
                        u.setRank("三级分销商");
                        allUser.add(u);
                    }

                }
            }

            //四级普通会员
            params1.clear();
            params1.put("user_id", CommUtil.null2String(user.getId()));
            //params1.put("deleteStatus", CommUtil.null2Boolean(false));
            params1.put("user_credit", 0);
            List<User> user7 = this.userService.query("select obj from User obj where obj.parent_pp_number=:user_id and obj.user_credit=:user_credit", params1, -1, -1);
            if (user7.size() > 0) {
                for (User u : user7) {
                    params1.clear();
                    Long user_id = u.getId();
                    params1.put("id", u.getId());
                  //  params1.put("deleteStatus", CommUtil.null2Boolean(false));
                    params1.put("order_status", this.getOrderType1());
                    List<OrderForm> list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:id and obj.order_status in (:order_status)", params1, -1, -1);
                    if (list.size() > 0) {
                        u.setOrder_list_num(CommUtil.null2String(list.size()));
                        //临时的存储ticheng
                        BigDecimal bi = addMoney(list, user, null, "3");
                        u.setTicheng(bi);

                        for (OrderForm of : list) {
                            BigDecimal b = of.getTotalPrice();
                            a = a.add(b);
                        }
                        u.setPay_money(a);
                        u.setRank("普通会员");
                        allUser.add(u);
                    }

                }
            }

            //String number = CommUtil.null2String(user.getId());
            mv.addObject("user", user);
            mv.addObject("allUser", allUser);
        }
        return mv;
    }



    //会员预计提成查看
    @RequestMapping({"/second/huiyuanyujiticheng.htm"})
    public ModelAndView huiyuanyujiticheng(HttpServletRequest request, HttpServletResponse response,String currentPage,String type,String userId){
        ModelAndView mv = new JModelAndView("user/second/66fen_yujiticheng.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
            mv = new JModelAndView("newwap/fen_yujiticheng.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        User user= this.userService.getObjById(CommUtil.null2Long(userId));
        OrderFormQueryObject ofq=new OrderFormQueryObject( currentPage,mv,"","");
       /* if(null != userId){
            ofq.addQuery("obj.user.id",new SysMap("user_id",Long.valueOf(userId)),"=");
        }else{*/
        ofq.addQuery("obj.user.id",new SysMap("user_id",user.getId()),"=");
        //}
        ofq.addQueryIn("obj.order_status",new SysMap("order_status", this.getOrderType()), "in");

        //分页后的订单
        IPageList pList = this.orderFormService.list(ofq);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }

        CommUtil.saveIPageList2ModelAndView(url+"/second/yujiticheng.htm", "", null, pList, mv);
        //全部的订单
        // IPageList pListAll = this.orderFormService.allList(ofq);
        IPageList pListAll = this.orderFormService.list(ofq);
        mv.addObject("type",type);
        mv.addObject("user",user);
        mv.addObject("OrderViewTools", this.orderViewTools);
        return mv;
    }
    /**
     * 我的预计提成
     * @param request
     * @param response
     * @param currentPage
     * @param type type为分销商的等级
     * @return
     */

    @RequestMapping({"/second/yujiticheng.htm"})
    public ModelAndView yujiticheng(HttpServletRequest request, HttpServletResponse response,String currentPage,String type){
        ModelAndView mv = new JModelAndView("user/second/66fen_yujiticheng.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
            mv = new JModelAndView("newwap/fen_yujiticheng.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        User user= SecurityUserHolder.getCurrentUser();


            OrderFormQueryObject ofq=new OrderFormQueryObject( currentPage,mv,"","");
       /* if(null != userId){
            ofq.addQuery("obj.user.id",new SysMap("user_id",Long.valueOf(userId)),"=");
        }else{*/
            ofq.addQuery("obj.user.id",new SysMap("user_id",user.getId()),"=");
            //}
            ofq.addQueryIn("obj.order_status",new SysMap("order_status", this.getOrderType()), "in");

            //分页后的订单
            IPageList pList = this.orderFormService.list(ofq);
            String url = this.configService.getSysConfig().getAddress();
            if ((url == null) || (url.equals(""))) {
                url = CommUtil.getURL(request);
            }

            CommUtil.saveIPageList2ModelAndView(url+"/second/yujiticheng.htm", "", null, pList, mv);
            //全部的订单
            // IPageList pListAll = this.orderFormService.allList(ofq);
            IPageList pListAll = this.orderFormService.list(ofq);

        mv.addObject("type",type);
        mv.addObject("user",user);
        mv.addObject("OrderViewTools", this.orderViewTools);
        return mv;
    }


    //下线预计提成

    @RequestMapping({"/second/xiaxianyujiticheng.htm"})
    public ModelAndView xiaxianyujiticheng(HttpServletRequest request, HttpServletResponse response,String type){
        ModelAndView mv = new JModelAndView("user/second/64fen_xiaxianticheng.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
            mv = new JModelAndView("newwap/fen_xiaxianticheng.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }

        mv.addObject("type" ,type);
        mv.addObject("typ" ,"1");
        //要返回页面的集合
        List<User> allUser = new ArrayList<>();
        User user= SecurityUserHolder.getCurrentUser();
        //二级普通会员
        if(null!=user) {
            Map params1 = new HashMap();
            params1.put("user_id", CommUtil.null2String(user.getId()));
            //  params1.put("deleteStatus", CommUtil.null2Boolean(false));
            params1.put("user_credit", CommUtil.null2Int(0));
            List<User> user5 = this.userService.query("select obj from User obj where obj.parent_number=:user_id and obj.user_credit=:user_credit", params1, -1, -1);
            BigDecimal a = new BigDecimal("0");
            if (user5.size() > 0) {
                for (User u : user5) {

                   // System.out.println("shifou"+allUser.add(u));
                    params1.clear();
                    params1.put("id", u.getId());
                    // params1.put("deleteStatus", CommUtil.null2Boolean(false));
                    params1.put("order_status", this.getOrderType());
                    List<OrderForm> list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:id and obj.order_status in (:order_status)", params1, -1, -1);
                    if (list.size() > 0) {
                        allUser.add(u);
                        u.setOrder_list_num(CommUtil.null2String(list.size()));
                        //临时的存储ticheng
                        BigDecimal bi = addMoney(list, user, null, "1");
                        bi=new BigDecimal(df.format(bi));
                        u.setTicheng(bi);

                        for (OrderForm of : list) {
                            BigDecimal b = of.getTotalPrice();
                            a = a.add(b);
                        }
                        u.setPay_money(a);
                        u.setRank("普通会员");

                    }

                }
            }

            //二级的分销商
            params1.clear();
            params1.put("user_id", CommUtil.null2String(user.getId()));
            //  params1.put("deleteStatus", CommUtil.null2Boolean(false));
            // params1.put("user_credit", CommUtil.null2Int(0));
            List<User> user2 = this.userService.query("select obj from User obj where obj.parent_number=:user_id and obj.user_credit !=0", params1, -1, -1);
            if (user2.size() > 0) {
                for (User u : user2) {

                    params1.clear();
                    Long user_id = u.getId();
                    params1.put("id", u.getId());
                    //  params1.put("deleteStatus", CommUtil.null2Boolean(false));
                    params1.put("order_status", this.getOrderType());
                    List<OrderForm> list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:id and obj.order_status in (:order_status)", params1, -1, -1);
                    if (null !=list&&list.size() > 0) {
                        allUser.add(u);
                        u.setOrder_list_num(CommUtil.null2String(list.size()));
                        //临时的存储ticheng
                        BigDecimal bi = addMoney(list, user, null, "2");
                        bi=new BigDecimal(df.format(bi));
                        u.setTicheng(bi);

                        for (OrderForm of : list) {
                            BigDecimal b = of.getTotalPrice();
                            a = a.add(b);
                        }
                        u.setPay_money(a);
                        u.setRank("二级分销商");

                    }

                }
            }
            //三级的普通会员
            params1.clear();
            params1.put("user_id", CommUtil.null2String(user.getId()));
            // params1.put("deleteStatus", CommUtil.null2Boolean(false));
            params1.put("user_credit", 0);
            List<User> user6 = this.userService.query("select obj from User obj where obj.parent_p_number=:user_id and obj.user_credit=:user_credit", params1, -1, -1);
            if (user6.size() > 0) {
                for (User u : user6) {

                    params1.clear();
                    Long user_id = u.getId();
                    params1.put("id", u.getId());
                    //   params1.put("deleteStatus", CommUtil.null2Boolean(false));
                    params1.put("order_status", this.getOrderType());
                    List<OrderForm> list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:id and order_status in (:order_status)", params1, -1, -1);
                    if (list.size() > 0) {
                        allUser.add(u);
                        u.setOrder_list_num(CommUtil.null2String(list.size()));
                        //临时的存储ticheng
                        BigDecimal bi = addMoney(list, user, null, "2");
                        bi=new BigDecimal(df.format(bi));
                        u.setTicheng(bi);

                        for (OrderForm of : list) {
                            BigDecimal b = of.getTotalPrice();
                            a = a.add(b);
                        }
                        u.setPay_money(a);
                        u.setRank("普通会员");

                    }

                }
            }
            //三级分销商
            params1.clear();
            params1.put("user_id", CommUtil.null2String(user.getId()));
            //   params1.put("deleteStatus", CommUtil.null2Boolean(false));
            // params1.put("user_credit", 0);
            List<User> user3 = this.userService.query("select obj from User obj where obj.parent_p_number=:user_id and obj.user_credit!=0", params1, -1, -1);

            if (user3.size() > 0) {
                for (User u : user3) {

                    params1.clear();
                    Long user_id = u.getId();
                    params1.put("id", u.getId());
                    //   params1.put("deleteStatus", CommUtil.null2Boolean(false));
                    params1.put("order_status", this.getOrderType());
                    List<OrderForm> list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:id and obj.order_status in (:order_status)", params1, -1, -1);
                    if (list.size() > 0) {
                        allUser.add(u);
                        u.setOrder_list_num(CommUtil.null2String(list.size()));
                        //临时的存储ticheng
                        BigDecimal bi = addMoney(list, user, null, "3");
                        bi=new BigDecimal(df.format(bi));
                        u.setTicheng(bi);

                        for (OrderForm of : list) {
                            BigDecimal b = of.getTotalPrice();
                            a = a.add(b);
                        }
                        u.setPay_money(a);
                        u.setRank("三级分销商");

                    }

                }
            }

            //四级普通会员
            params1.clear();
            params1.put("user_id",CommUtil.null2String(user.getId()));
            //params1.put("deleteStatus", CommUtil.null2Boolean(false));
            params1.put("user_credit", 0);
            List<User> user7 = this.userService.query("select obj from User obj where obj.parent_pp_number=:user_id and obj.user_credit=:user_credit", params1, -1, -1);
            if (user7.size() > 0) {
                for (User u : user7) {

                    params1.clear();
                    Long user_id = u.getId();
                    params1.put("id", u.getId());
                    //  params1.put("deleteStatus", CommUtil.null2Boolean(false));
                    params1.put("order_status", this.getOrderType());
                    List<OrderForm> list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:id and obj.order_status in (:order_status)", params1, -1, -1);
                    if (list.size() > 0) {
                        allUser.add(u);
                        u.setOrder_list_num(CommUtil.null2String(list.size()));
                        //临时的存储ticheng
                        BigDecimal bi = addMoney(list, user, null, "3");
                        bi=new BigDecimal(df.format(bi));
                        u.setTicheng(bi);
                        for (OrderForm of : list) {
                            BigDecimal b = of.getTotalPrice();
                            a = a.add(b);
                        }
                        u.setPay_money(a);
                        u.setRank("普通会员");

                    }

                }
            }

            //String number = CommUtil.null2String(user.getId());
            mv.addObject("user", user);
            mv.addObject("allUser", allUser);
        }
        return mv;
    }



    /**
     * 分销商
     * @param request
     * @param response
     * @param type 分销等级
     * @return
     */
    @RequestMapping({"/second/fenxiaoshang.htm"})
    public ModelAndView fenxiaoshang(HttpServletRequest request, HttpServletResponse response,String type) throws ParseException {
        ModelAndView mv = new JModelAndView("user/second/69fen_fenxiaoshangs.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
            mv = new JModelAndView("newwap/fen_fenxiaoshangs.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        List<User> list = new ArrayList<>();
        User user= SecurityUserHolder.getCurrentUser();
        if(null != type && type.equals("1")){
            user.setRank("一级");
            list.add(user);
        }else if(null != type && type.equals("2")){
            Map map = this.getOrderListByTime("2");
            list = (List<User>)map.get("orderListAll");
            mv.addObject("toDay",map.get("toDay"));
            mv.addObject("yesterday",map.get("yesterday"));
            mv.addObject("thisWeek",map.get("thisWeek"));
            mv.addObject("month",map.get("month"));
            mv.addObject("all",map.get("all"));
        }else if(null != type && type.equals("3")){
            Map map = this.getOrderListByTime("3");
            list = (List<User>)map.get("orderListAll");
            mv.addObject("toDay",map.get("toDay"));
            mv.addObject("yesterday",map.get("yesterday"));
            mv.addObject("thisWeek",map.get("thisWeek"));
            mv.addObject("month",map.get("month"));
            mv.addObject("all",map.get("all"));
        }
        mv.addObject("user",list);
        mv.addObject("type",type);
        return mv;
    }

    /**
     * 粉丝团
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"/second/fensituan.htm"})
    public ModelAndView fensituan(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("user/second/70fensituan.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
            mv = new JModelAndView("newwap/fensituan.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        mv.addObject("fans",this.getFans());
        return mv;
    }


    /**
     * 提现记录
     * @param request
     * @param response
     * @return
     */
    @SecurityMapping(display = false, rsequence = 0, title="提现记录", value="/second/tixianjilu.htm*", rtype="second", rname="提现记录", rcode="bargain_seller", rgroup="提现记录")
    @RequestMapping({"/second/tixianjilu.htm"})
    public ModelAndView tixianjilu(HttpServletRequest request, HttpServletResponse response,String currentPage){
        ModelAndView mv = new JModelAndView("user/second/68fen_tixianjilu.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
            mv = new JModelAndView("newwap/fen_tixianjilu.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        User user= SecurityUserHolder.getCurrentUser();
        CombinLogQueryObject ofq=new CombinLogQueryObject(currentPage, mv, "addTime", "desc");
        ofq.addQuery("obj.user.id",new SysMap("user_id",user.getId()),"=");
        ofq.addQuery("obj.type",new SysMap("type",CommUtil.null2String(2)),"=");
        IPageList pList = combinLogService.list(ofq);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        CommUtil.saveIPageList2ModelAndView(url+"/second/tixianjilu.htm", "", "", pList, mv);
        return mv;
    }
    /*
    * 后台体现申请记录
    *
    * */
    @SecurityMapping(display = false, rsequence = 0, title="提现记录", value="/second/tixianjilu.htm*", rtype="second", rname="提现记录", rcode="bargain_seller", rgroup="提现记录")
    @RequestMapping({"/second/tixianjilu_shenh.htm"})
    public ModelAndView tixianjilu_shenh(HttpServletRequest request, HttpServletResponse response,String currentPage){
        ModelAndView mv = new JModelAndView("admin/blue/predeposit_list.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );

        CombinLogQueryObject ofq=new CombinLogQueryObject(currentPage, mv, "addTime", "desc");
        IPageList pList = combinLogService.list(ofq);
        //int a=pList.getResult().size();
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        CommUtil.saveIPageList2ModelAndView(url+"/second/tixianjilu.htm", "", "", pList, mv);
        return mv;
    }

    /*
    * 后台提现申请通过
    *
    * */
    @SecurityMapping(display = false, rsequence = 0, title="提现记录", value="/second/tixianjilu.htm*", rtype="second", rname="提现记录", rcode="bargain_seller", rgroup="提现记录")
    @RequestMapping({"/second/tixianjilu_tongguo.htm"})
    public String tixianjilu_tongguo(HttpServletRequest request, HttpServletResponse response,String currentPage,String id){

        CombinLog com= this.combinLogService.getObjById(CommUtil.null2Long(id));
        com.setStatus(CommUtil.null2String(1));
        this.combinLogService.update(com);
        //mv.addObject("currentPage",currentPage);
        return "redirect:/second/tixianjilu_shenh.htm?currentPage=" + currentPage;
    }





    /**
     * 申请提现
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"second/shenqingtixian"})
    public ModelAndView shenqingtixian(HttpServletRequest request, HttpServletResponse response,String num,String bank){
        ModelAndView mv = new JModelAndView("user/second/67fen_shenqingtixian.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
            mv = new JModelAndView("newwap/fen_shenqingtixian.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        mv.addObject("mayWithdraw",this.getTiChengSumMoney().doubleValue());
        Map params = new HashMap();
        params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
        List<Address> obj = this.addressService.query("select obj from Address obj where obj.user.id =:user_id and obj.type = '1'", params, -1, -1);
        mv.addObject("obj",obj);
        return mv;
    }

    /**
     * 申请提现
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"second/selectBank"})
    public void selectBank(HttpServletRequest request, HttpServletResponse response,String addressId){
        Address obj = this.addressService.getObjById(Long.parseLong(addressId));
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try
        {
            PrintWriter writer = response.getWriter();
            writer.print(obj.getBank());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }



    /**
     * 申请提现1
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"second/withdrawals.htm"})
    public ModelAndView withdrawals(HttpServletRequest request, HttpServletResponse response,String id){
        ModelAndView mv = new JModelAndView("user/second/67fen_shenqingtixian.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
            mv = new JModelAndView("newwap/fen_shenqingtixian.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        mv.addObject("mayWithdraw",this.getTiChengSumMoney().doubleValue());
        Address address = this.addressService.getObjById(Long.valueOf(Long.parseLong(id)));
        mv.addObject("address",address);
        Map params = new HashMap();
        params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
        List<Address> obj = this.addressService.query("select obj from Address obj where obj.user.id =:user_id and obj.type = '1'", params, -1, -1);
        mv.addObject("obj",obj);
        return mv;
    }

    /**
     * 添加申请提现
     * @param request
     * @param response
     * @return
     */
    @SecurityMapping(display = false, rsequence = 0, title="添加申请提现", value="/second/addtixian.htm*", rtype="second", rname="添加申请提现", rcode="bargain_seller", rgroup="添加申请提现")
    @RequestMapping({"second/addtixian.htm"})
    public void addtixian(HttpServletRequest request, HttpServletResponse response,String bankCard,String Money,String bankName){
        ModelAndView mv = new JModelAndView("user/second/67fen_shenqingtixian.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        User user= SecurityUserHolder.getCurrentUser();
        CombinLog com  = new CombinLog();
        com.setBank_name(bankName);
        com.setAddTime(new Date());
        com.setStatus("0");
        Address obj = this.addressService.getObjById(Long.parseLong(bankCard));
        com.setCard_number(obj.getBankCardnumber());
        com.setFactorage(new BigDecimal("2"));
        com.setMoney(new BigDecimal(Money).add(new BigDecimal("2")));
        com.setReality_money(new BigDecimal(Money));
        com.setUser(user);

        com.setType("2");
        com.setInfo("商家提现");

        boolean val = combinLogService.save(com);
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try
        {
            PrintWriter writer = response.getWriter();
            writer.print(val);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询订单
     * @param type 0销售提成（已完成） 1预计提成 2（全部的）zzy有什么用呢？
     * @param user_id
     * @return
     */
    public List<OrderForm> orderList(String type,Long user_id){
        List<OrderForm> list = new ArrayList<>();
        if (!CommUtil.null2String(type).equals("")) {
            if (type.equals("0")) {
                Map map = new HashMap();
                map.put("user_id",user_id);
                map.put("order_status",this.getOrderType1());
                list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:user_id and obj.order_status in(:order_status)",map,-1,-1);
            }
            if (type.equals("1")) {
                Map map = new HashMap();
                map.put("user_id",user_id);
                map.put("order_status",this.getOrderType());
                list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:user_id and obj.order_status in(:order_status)",map,-1,-1);
            }
           /* if (type.equals("2")) {
                Map map = new HashMap();
                map.put("user_id",user_id);
                list = this.orderFormService.query("select obj from OrderForm obj where obj.user.id=:user_id",map,-1,-1);
            }*/
        }
        return list;
    }

    /**
     * 各级订单的分销金额的添加   zzy暂时不用    用OrderViewTools.
     * @param orderList1
     * @param user     获取星级占比用
     * @param type 金额的类型  zzy没用上
     * @param rank 该分销是几级分销
     */
    public BigDecimal addMoney(List<OrderForm> orderList1,User user,BigDecimal type,String rank){
        //
        BigDecimal tiCheng = new BigDecimal("0");
        if(null!=orderList1&&orderList1.size()>0){
            //遍历订单列表
            BigDecimal tiCheng3 = new BigDecimal("0");
            for (OrderForm orderForm1:orderList1) {
                BigDecimal tiCheng1 = new BigDecimal("0");
                BigDecimal tiCheng2 = new BigDecimal("0");
                Map goodsCart = new HashMap();
                goodsCart.put("of_id", orderForm1.getId());
                //根据订单遍历购物车里面的商品
                List<GoodsCart> goodsList =  this.goodsCartService.query("select obj from GoodsCart obj where obj.of.id=:of_id", goodsCart, -1, -1);
                if(goodsList.size()!=0){
                    for (GoodsCart goodsCart1:goodsList) {
                        //当前该用户的星级提成比例    传一个星级获取到一个星级占比
                        BigDecimal xingji = fenXiaoUtil.getStarLevelRatio(user.getUser_credit()+"");
                        BigDecimal bi = new BigDecimal("0");

                        //体验店算分销利润规则：商品金额 * 数量 * 下线等级提成比例 * 星级提成比例
                        if(null != goodsCart1&&null != goodsCart1.getGoods()&&null != goodsCart1.getGoods().getGc()&&null != goodsCart1.getGoods().getGc().getBc_location() && goodsCart1.getGoods().getGc().getBc_location().equals("2")){
                            //体验店是按照总额分润（商品金额的分销）//商品金额*数量
                            bi = goodsCart1.getGoods().getStore_price().multiply(new BigDecimal(goodsCart1.getCount()));
                            //根据星级分润    * 星级提成比例
                            BigDecimal actual = bi.multiply(xingji);
                            //  * 下线等级提成比例
                            BigDecimal a = actual.multiply(fenXiaoUtil.getRatio(rank,"0"));
                           // type = type.add(a);
                            //体验店商品的提成
                            tiCheng1 = tiCheng1.add(a);
                        }else{//非体验店算分销利润规则：商品金额 * 数量 * 下线等级提成比例 * 星级提成比例  (不一样的在下线等级提成比例)
                            //非体验店是按照分润额度分润（分销金额的分销）   商品金额 * 数量
                            bi = goodsCart1.getGoods().getP_fenrun().multiply(new BigDecimal(goodsCart1.getCount()));
                            //根据星级分润   * 星级提成比例
                            BigDecimal actual = bi.multiply(xingji);
                            //  * 下线等级提成比例
                            BigDecimal a = actual.multiply(fenXiaoUtil.getRatio(rank,"1"));
                          //  type = type.add(a);
                            //非体验店商品提成
                            tiCheng2 = tiCheng2.add(a);
                        }
                        //一个订单的分润
                        tiCheng3=tiCheng3.add(tiCheng1).add(tiCheng2);
                    }
                }
                    //goods_coup_money当前此字段是提成的金额
                  //  orderForm1.setGoods_coup_money(tiCheng);
                //当前用户下所有订单的分润
                tiCheng=tiCheng.add(tiCheng3);
            }
        }
        return tiCheng;
    }


    /**
     * 获取上一个月的时间（例：2017-04）
     * @param type 0,本月 1，上月
     * @return
     */
    public Map getLastDate(String type){
        Map map = new HashMap();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //当前日期
        Calendar cale = Calendar.getInstance();
        if(type.equals("1")){
            //上个月
            cale.add(Calendar.MONTH, -1);
        }
        cale.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        String firstDay = format.format(cale.getTime())+" 00:00:01";
        cale.set(Calendar.DAY_OF_MONTH, cale.getActualMaximum(Calendar.DAY_OF_MONTH));//设置为最后一天
        String lastDay = format.format(cale.getTime())+" 23:59:59";
        map.put("firstDay",firstDay);
        map.put("lastDay",lastDay);
        return map;
    }


    /**
     *预计提成的订单类型
     * @return
     */
    public List getOrderType(){
        List<Integer> i = new ArrayList<>();
        i.add(15);
        i.add(20);
        i.add(30);
        i.add(40);
//        i.add(40);
        return i;
    }

    /**
     * 销售提成的订单类型
     * @return
     */
    public List getOrderType1(){
        List<Integer> i = new ArrayList<>();
        //i.add(40);
        i.add(50);
        i.add(51);
        i.add(60);
        i.add(65);
        return i;
    }

    /**
     * 获取当前用户的下级分销商
     * @param type 2 二级分销商 3 三级分销商
     * @return
     */
    public List<Long> getUserList(String type){
        List<Long> list = new ArrayList<>();
        User user = SecurityUserHolder.getCurrentUser();
        if(null != user){
            Map params = new HashMap();
            params.put("user_id", user.getId());
            //二级分销商的集合
            List<User> userList =this.userService.query("select obj from User obj where obj.parent.id =:user_id", params, -1, -1);
            if(null != type && type.equals("2")){
                if(null != userList){
                    for (User u:userList) {
                        list.add(u.getId());
                    }
                }
            }else if(null != type && type.equals("3")){
                if(null != userList){
                    for (User u:userList) {
                        Map params1 = new HashMap();
                        params1.put("user_id", u.getId());
                        //三级分销商的集合
                        List<User> userList1 =this.userService.query("select obj from User obj where obj.parent.id =:user_id", params1, -1, -1);
                        for (User u1:userList1) {
                            list.add(u1.getId());
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 查询今天，昨天，本周，本月的时间
     * @param type 1今天，2昨天，3本周，4本月
     * @return
     */
    public Map getTime(String type) throws ParseException {
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        if(null != type && type.equals("1")){
            String firstDate = si.format(new Date())+" 00:00:01";
            String lastDate = si.format(new Date())+" 23:59:59";
            map.put("firstDate",sim.parse(firstDate));
            map.put("lastDate",sim.parse(lastDate));
        }else if(null != type && type.equals("2")){
            Calendar cal=Calendar.getInstance();
            cal.add(Calendar.DATE,-1);
            Date time=cal.getTime();
            String firstDate = si.format(time)+" 00:00:01";
            String lastDate = si.format(time)+" 23:59:59";
            map.put("firstDate",sim.parse(firstDate));
            map.put("lastDate",sim.parse(lastDate));
        }else if(null != type && type.equals("3")){
            Calendar cal = Calendar.getInstance();
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Date a = cal.getTime();
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(a);
            cal1.add(Calendar.DAY_OF_WEEK, 6);
            Date a1 = cal1.getTime();
            String firstDate = si.format(a)+" 00:00:01";
            String lastDate = si.format(a1)+" 23:59:59";
            map.put("firstDate",sim.parse(firstDate));
            map.put("lastDate",sim.parse(lastDate));
        }else if(null != type && type.equals("4")){
            Calendar cale = Calendar.getInstance();
            cale.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
            String firstDay = si.format(cale.getTime())+" 00:00:01";
            cale.set(Calendar.DAY_OF_MONTH, cale.getActualMaximum(Calendar.DAY_OF_MONTH));//设置为1号,当前日期既为本月第一天
            String lastDay = si.format(cale.getTime())+" 23:59:59";
            map.put("firstDate",sim.parse(firstDay));
            map.put("lastDate",sim.parse(lastDay));
        }
        return map;
    }


    /**
     * 按照时间查询的订单集合
     * @param type
     * @return
     */
    public Map getOrderListByTime(String type) throws ParseException {
        Map map = new HashMap();
        User user = SecurityUserHolder.getCurrentUser();
        if(null != type && type.equals("2")){
            Map params = new HashMap();
            params.put("user_id", CommUtil.null2String(user.getId()));
            //二级分销商的集合
            List<User> list = this.userService.query("select obj from User obj where obj.parent_number =:user_id and obj.user_credit!=0", params, -1, -1);
            //zzy注释掉
            /*if(null != list){
                for (User u:list) {
                    u.setRank("二级");
                }
            }*/
            map.put("orderListAll",list);

            Map params1 = new HashMap();
            params1.put("user_id", CommUtil.null2String(user.getId()));
            params1.put("startTime", this.getTime("1").get("firstDate"));
            params1.put("endTime", this.getTime("1").get("lastDate"));
            //当天的集合
            List<User> list1 = this.userService.query("select obj from User obj where obj.parent_number =:user_id and obj.addTime>=:startTime and obj.addTime<=:endTime  and obj.user_credit!=0", params1, -1, -1);


            Map params2 = new HashMap();
            params2.put("user_id", CommUtil.null2String(user.getId()));
            params2.put("startTime", this.getTime("2").get("firstDate"));
            params2.put("endTime", this.getTime("2").get("lastDate"));
            //昨天的集合
            List<User> list2 = this.userService.query("select obj from User obj where obj.parent_number =:user_id and obj.addTime>=:startTime and obj.addTime<=:endTime  and obj.user_credit!=0", params2, -1, -1);


            Map params3 = new HashMap();
            params3.put("user_id", CommUtil.null2String(user.getId()));
            params3.put("startTime", this.getTime("3").get("firstDate"));
            params3.put("endTime", this.getTime("3").get("lastDate"));
            //本周的集合
            List<User> list3 = this.userService.query("select obj from User obj where obj.parent_number =:user_id and obj.addTime>=:startTime and obj.addTime<=:endTime  and obj.user_credit!=0", params3, -1, -1);


            Map params4 = new HashMap();
            params4.put("user_id", CommUtil.null2String(user.getId()));
            params4.put("startTime", this.getTime("4").get("firstDate"));
            params4.put("endTime", this.getTime("4").get("lastDate"));
            //本月的集合
            List<User> list4 = this.userService.query("select obj from User obj where obj.parent_number =:user_id and obj.addTime>=:startTime and obj.addTime<=:endTime  and obj.user_credit!=0", params4, -1, -1);

            map.put("toDay",list1.size());
            map.put("yesterday",list2.size());
            map.put("thisWeek",list3.size());
            map.put("month",list4.size());
            map.put("all",list.size());
        }else if(null != type && type.equals("3")){
           /* List<User> lists = new ArrayList<>();
            List<User> lists1 = new ArrayList<>();
            List<User> lists2 = new ArrayList<>();
            List<User> lists3 = new ArrayList<>();
            List<User> lists4 = new ArrayList<>();*/
            Map params = new HashMap();
            params.put("user_id",CommUtil.null2String(user.getId()));
            //san级分销商的集合
            List<User> list = this.userService.query("select obj from User obj where obj.parent_p_number =:user_id  and obj.user_credit!=0", params, -1, -1);
            if(null != list){
                /*for (User u:list) {
                    Map paramss = new HashMap();
                    paramss.put("user_id", u.getId());
                    List<User> listss = this.userService.query("select obj from User obj where obj.parent_p_number =:user_id  and obj.star_level!=0", paramss, -1, -1);
                    for (User u1:listss) {
                        lists.add(u1);
                    }
                }*/
                //三级分销全部的集合
                map.put("orderListAll",list);
                map.put("all",list.size());

                //for (User u:list) {
                    Map paramss = new HashMap();
                    paramss.put("user_id", CommUtil.null2String(user.getId()));
                    paramss.put("startTime", this.getTime("1").get("firstDate"));
                    paramss.put("endTime", this.getTime("1").get("lastDate"));
                    List<User> lists1 = this.userService.query("select obj from User obj where obj.parent_p_number =:user_id and obj.addTime>=:startTime and obj.addTime<=:endTime  and obj.user_credit!=0", paramss, -1, -1);
                   /* for (User u1:listss) {
                        lists1.add(u1);
                    }*/
               // }

                //for (User u:list) {
                    Map paramss2 = new HashMap();
                    paramss2.put("user_id", CommUtil.null2String(user.getId()));
                    paramss2.put("startTime", this.getTime("2").get("firstDate"));
                    paramss2.put("endTime", this.getTime("2").get("lastDate"));
                    List<User> lists2 = this.userService.query("select obj from User obj where obj.parent_p_number =:user_id and obj.addTime>=:startTime and obj.addTime<=:endTime  and obj.user_credit!=0", paramss2, -1, -1);
                    /*for (User u1:listss) {
                        lists2.add(u1);
                    }*/
               // }

                //for (User u:list) {
                    Map paramss3 = new HashMap();
                    paramss3.put("user_id", CommUtil.null2String(user.getId()));
                    paramss3.put("startTime", this.getTime("3").get("firstDate"));
                    paramss3.put("endTime", this.getTime("3").get("lastDate"));
                    List<User> lists3 = this.userService.query("select obj from User obj where obj.parent_p_number =:user_id and obj.addTime>=:startTime and obj.addTime<=:endTime  and obj.user_credit!=0", paramss3, -1, -1);
                   /* for (User u1:listss) {
                        lists3.add(u1);
                    }*/
                //}

                //for (User u:list) {
                    Map paramss4 = new HashMap();
                    paramss4.put("user_id", CommUtil.null2String(user.getId()));
                    paramss4.put("startTime", this.getTime("4").get("firstDate"));
                    paramss4.put("endTime", this.getTime("4").get("lastDate"));
                    List<User> lists4 = this.userService.query("select obj from User obj where obj.parent_p_number =:user_id and obj.addTime>=:startTime and obj.addTime<=:endTime and obj.user_credit!=0", paramss4, -1, -1);
                   /* for (User u1:listss) {
                        lists4.add(u1);
                    }*/
                //}
                map.put("toDay",lists1.size());
                map.put("yesterday",lists2.size());
                map.put("thisWeek",lists3.size());
                map.put("month",lists4.size());
                map.put("all",lists1.size());
            }
        }
        return map;
    }


    /**
     * 获取粉丝团的集合
     * @return
     */
        public List<User> getFans(){
            List<User> list = new ArrayList<>();
            User user = SecurityUserHolder.getCurrentUser();
            if(null != user){
                Map params = new HashMap();
                params.put("user_id", CommUtil.null2String(user.getId()));
                //二级分销商的集合
                List<User> userList =this.userService.query("select obj from User obj where obj.parent_number =:user_id", params, -1, -1);
                if(null != userList){
                    //获取三级、获取四级  加到集合list里面
                    List<User> userList3 =this.userService.query("select obj from User obj where obj.parent_p_number =:user_id", params, -1, -1);
                    List<User> userList4 =this.userService.query("select obj from User obj where obj.parent_pp_number =:user_id", params, -1, -1);
                    for(User u:userList){
                        list.add(u);
                    }
                    for(User u:userList3){
                        list.add(u);
                    }
                    for(User u:userList4){
                        list.add(u);
                    }


                   /* for (User u:userList) {
                        u.setRank("二级");
                        list.add(u);
                        Map params1 = new HashMap();
                        params1.put("user_id", u.getId());
                        //三级分销商的集合
                        List<User> userList1 =this.userService.query("select obj from User obj where obj.parent.id =:user_id", params1, -1, -1);
                        for (User u1:userList1) {
                            u1.setRank("三级");
                            list.add(u1);
                            Map params2 = new HashMap();
                            params2.put("user_id", u1.getId());
                            //普通会员的集合
                            List<User> userList2 =this.userService.query("select obj from User obj where obj.parent.id =:user_id and obj.star_level=0", params2, -1, -1);
                            for (User u2:userList2) {
                                u2.setRank("普通会员");
                                list.add(u2);
                            }
                        }
                    }*/
                }
            }
            return list;
    }

    /**
     * 获取当前用户的下级分销商数量（最少一星）
     * @param type 2 二级分销商 3 三级分销商
     * @return
     */
    public List<Long> getUserListByStarLevel(String type){
        List<Long> list = new ArrayList<>();
        User user = SecurityUserHolder.getCurrentUser();
        if(null != user){
            if(null != type && type.equals("2")) {
                Map params = new HashMap();
                params.put("user_id", CommUtil.null2String(user.getId()));
                //二级分销商的集合
                List<User> list1 = this.userService.query("select obj from User obj where obj.parent_number =:user_id and obj.user_credit!=0", params, -1, -1);
                if(null != list1){
                    for (User u:list1) {
                        list.add(u.getId());
                    }
                }
            } else if(null != type && type.equals("3")){
                //三级分销商的集合
                Map params1 = new HashMap();
                params1.put("user_id", CommUtil.null2String(user.getId()));
                List<User> List2 =this.userService.query("select obj from User obj where obj.parent_p_number =:user_id and obj.user_credit!=0", params1, -1, -1);
                if(null != List2){
                    for (User u:List2) {
                        list.add(u.getId());
                    }
                }

            }
        }
        return list;
    }


    /**
     * 获取提现的总金额
     * @return
     */
    public BigDecimal getMoney(){
        BigDecimal bi = new BigDecimal("0");
        User user = SecurityUserHolder.getCurrentUser();
        Map params = new HashMap();
        params.put("user_id", user.getId());
        List<CombinLog> list = combinLogService.query("select obj from CombinLog obj where user_id=:user_id",params,-1,-1);
        for (CombinLog co:list) {
            bi = bi.add(co.getMoney());
        }
        return bi;
    }


    /**
     *  获取可以提现的金额     zzy这个方法暂时没用
     * @return
     */
    public BigDecimal getTiChengSumMoney(){
        User user= SecurityUserHolder.getCurrentUser();
        //销售提成（已完成的）
        BigDecimal finish = new BigDecimal("0");
        //普通会员不参与分销
        if(user.getUser_credit()!=0){
            //一级分销的订单(已完成)
            List<OrderForm> orderList = this.orderList("0",user.getId());
            //添加到销售提成
            finish = this.addMoney(orderList,user,finish,"1");
            if (user != null) {
                Map params = new HashMap();
                params.put("user_id", user.getId());
                //二级的分销人
                List<User> user1 =this.userService.query("select obj from User obj where obj.parent.id =:user_id", params, -1, -1);
                if(user1.size()!=0){
                    for (User u1:user1) {
                        //二级分销的订单(已完成)
                        List<OrderForm> orderForm1 = this.orderList("0",u1.getId());
                        finish = this.addMoney(orderForm1,user,finish,"2");
                        Map params1 = new HashMap();
                        params1.put("user_id", u1.getId());
                        //三级的分销人
                        List<User> user2 = this.userService.query("select obj from User obj where obj.parent.id=:user_id", params1, -1, -1);
                        if(user2.size()!=0){
                            for (User u2:user2) {
                                //三级分销的订单(已完成)
                                List<OrderForm> orderForm2 = this.orderList("0",u2.getId());
                                //添加到下线提成
                                finish = this.addMoney(orderForm2,user,finish,"3");
                                Map params2 = new HashMap();
                                params2.put("user_id", u2.getId());
                                //四级级分销（如果四级分销为普通会员，无法参加分销，获得的钱给一级分校的人）
                                List<User> user3 = this.userService.query("select obj from User obj where obj.parent.id=:user_id", params2, -1, -1);
                                if(user3.size()!=0){
                                    for (User u3:user3) {
                                        if(0 != u3.getUser_credit()){
                                            //四级分销的订单(已完成)
                                            List<OrderForm> orderForm3 = this.orderList("0",u3.getId());
                                            //添加到下线提成
                                            finish = this.addMoney(orderForm3,user,finish,"1");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //已提现的金额
        BigDecimal tixian = this.getMoney();
        return finish.subtract(tixian);
    }
}
