package com.shopping.manage.second.action;

import com.shopping.core.mv.JModelAndView;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.Coupon;
import com.shopping.foundation.domain.CouponInfo;
import com.shopping.foundation.domain.GoodsClass;
import com.shopping.foundation.service.*;
import com.shopping.view.web.tools.GoodsViewTools;
import com.shopping.view.web.tools.NavViewTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by Administrator on 2017/3/14.
 */
@Controller
public class TestStore {


    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IGoodsClassService goodsClassService;

    @Autowired
    private NavViewTools navTools;

    @Autowired
    private ICouponInfoService couponInfoService;

    @Autowired
    private ICouponService couponService;

    @Autowired
    private GoodsViewTools goodsViewTools;

    @Autowired
    private IGoodsService goodsService;
    @RequestMapping({"brans_in.htm"})
    public ModelAndView brans_in(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("user/second/18testShop.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
            mv = new JModelAndView("user/second/35brans_in.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

        return mv;
    }

    @RequestMapping({"entrepreneurship.htm"})
    public ModelAndView entrepreneurship(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("user/second/18testShop.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        mv = new JModelAndView("user/second/24entrepreneurship.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        return mv;
    }
    @RequestMapping({"member_college.htm"})
    public ModelAndView member_college(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("user/second/18testShop.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        mv = new JModelAndView("user/second/23member_college.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        return mv;
    }

    @RequestMapping({"/second/teststore.htm"})
    public ModelAndView everyDayGoods(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("user/second/18testShop.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
            mv = new JModelAndView("../shop/newwap/testshop/testshop.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        }
        Map params = new HashMap();
        params.put( "display", Boolean.valueOf( true ) );
        params.put( "bc_location", "4" );
        List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display and obj.bc_location=:bc_location order by obj.sequence asc", params, 0, 14 );
        List gc1=new ArrayList();
        List gc3=new ArrayList();
        List gc5=new ArrayList();
        List gc6=new ArrayList();
        for (Object gc:gcs) {
            //System.out.println(((GoodsClass)gc).getClassName());
            gc1=((GoodsClass)gc).getChilds();
            for (Object gc2:gc1) {
                gc6.add(gc2);
                /*gc3=((GoodsClass)gc2).getChilds();
                for (Object gc4:gc3) {
                    gc5.add(gc4);
                }*/
            }
        }
        mv.addObject( "gc", gc6 );
        mv.addObject( "gcs", gcs );
        mv.addObject("navTools", this.navTools);


        params.clear();
       // params.put( "store_recommend", Boolean.valueOf( true ) );obj.store_recommend=:store_recommend and
        params.put( "goods_status", Integer.valueOf( 0 ) );
        params.put( "bc_location", "4" );
        List store_reommend_goods_list = this.goodsService.query( "select obj from Goods obj where obj.goods_status=:goods_status and obj.gc.bc_location = :bc_location order by obj.addTime desc", params, 0, 8 );
        List store_reommend_goods = new ArrayList();
        int max = store_reommend_goods_list.size() - 1;
        for( int i = 0; i <= max; i++ ) {
            store_reommend_goods.add(store_reommend_goods_list.get( i ));
        }
        mv.addObject( "store_reommend_goods_list", store_reommend_goods_list );
        mv.addObject( "store_reommend_goods", store_reommend_goods );
        mv.addObject("GoodsViewTools", goodsViewTools);
        return mv;
    }


    @RequestMapping({"/second/testshopsecond.htm"})
    public ModelAndView testShopSecond(HttpServletRequest request, HttpServletResponse response,int id){
        ModelAndView mv = new JModelAndView("../shop/newwap/testshop/testshopsecond.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
       // request.getSession().setAttribute("shopping_view_type", "wap");
        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 4 and obj.display=:display order by obj.sequence asc", params, -1, -1);
        //int max0 = gcs.size();
        mv.addObject("gcs", gcs);

        params.clear();
        params.put("display", Boolean.valueOf(true));
        params.put("id", Long.valueOf(id));
        List gg = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id =:id and obj.bc_location = 4 and obj.display=:display order by obj.sequence asc", params, -1, -1);
        mv.addObject("gg", gg);

        params.clear();
        params.put( "store_recommend", Boolean.valueOf( true ) );
        params.put( "goods_status", Integer.valueOf( 0 ) );
        params.put("id", Long.valueOf(id));
        params.put( "bc_location", "4" );
        List store_reommend_goods_list = this.goodsService.query( "select obj from Goods obj where obj.store_recommend = :store_recommend and obj.goods_status = :goods_status and obj.gc.parent.id=:id and obj.gc.bc_location = :bc_location order by obj.addTime desc", params, 0, 6 );
       // List store_reommend_goods_list = this.goodsService.query( "select obj from Goods obj where obj.goods_status=:goods_status  and obj.gc.parent.id=:id and obj.gc.bc_location = :bc_location order by obj.addTime desc", params, 0, 6 );
        List store_reommend_goods = new ArrayList();
        int max = store_reommend_goods_list.size() - 1;
        for( int i = 0; i <= max; i++ ) {
            store_reommend_goods.add(store_reommend_goods_list.get( i ));
        }
        mv.addObject( "store_reommend_goods", store_reommend_goods );

        params.clear();
        //params.put( "store_recommend", Boolean.valueOf( true ) );
        params.put( "goods_status", Integer.valueOf( 0 ) );
        params.put("id", Long.valueOf(id));
        params.put( "bc_location", "4" );
        List list = this.goodsService.query( "select obj from Goods obj where obj.goods_status = :goods_status and obj.gc.parent.id=:id and obj.gc.bc_location = :bc_location order by obj.addTime desc", params, 0, 6 );
        mv.addObject( "list", list );
        mv.addObject("id",id);
        mv.addObject("GoodsViewTools", goodsViewTools);
        return mv;
    }

//二级分类跳列表页
    @RequestMapping({"/second/testshopsecond_list.htm"})
    public ModelAndView testShopSecondList(HttpServletRequest request, HttpServletResponse response,int id) {

        ModelAndView mv = new JModelAndView("user/second/19testShop_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
            mv = new JModelAndView("newwap/testshop/GoodsListS.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        Map params = new HashMap();
        params.put( "goods_status", Integer.valueOf( 0 ) );
        params.put("id", Long.valueOf(id));
        params.put( "bc_location", "4" );
        List list = this.goodsService.query( "select obj from Goods obj where obj.goods_status = :goods_status and obj.gc.id=:id and obj.gc.bc_location = :bc_location order by obj.addTime desc", params, 0, 6 );
        mv.addObject( "list", list );

        params.clear();
        params.put( "display", Boolean.valueOf( true ) );
        params.put( "bc_location", "4" );
        List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display and obj.bc_location=:bc_location order by obj.sequence asc", params, 0, 14 );
        List gc1=new ArrayList();
        List gc3=new ArrayList();
        List gc5=new ArrayList();
        List gc6=new ArrayList();
        for (Object gc:gcs) {
            //System.out.println(((GoodsClass)gc).getClassName());
            gc1=((GoodsClass)gc).getChilds();
            for (Object gc2:gc1) {
                gc6.add(gc2);
                /*gc3=((GoodsClass)gc2).getChilds();
                for (Object gc4:gc3) {
                    gc5.add(gc4);
                }*/
            }
        }
        mv.addObject( "gcs", gcs );
        mv.addObject("navTools", this.navTools);
        mv.addObject("GoodsViewTools", goodsViewTools);
        return mv;
    }

        @RequestMapping({"/second/testshopclass.htm"})
    public ModelAndView testShopClass(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("../shop/newwap/testshop/testshopclass.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        request.getSession().setAttribute("shopping_view_type", "wap");


            Map params = new HashMap();
            params.put( "display", Boolean.valueOf( true ) );
            params.put( "bc_location", "4" );
            List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display and obj.bc_location=:bc_location order by obj.sequence asc", params, 0, 14 );
           /* List gc1=new ArrayList();
            List gc3=new ArrayList();
            List gc5=new ArrayList();
            List gc6=new ArrayList();
            for (Object gc:gcs) {
                //System.out.println(((GoodsClass)gc).getClassName());
                gc1=((GoodsClass)gc).getChilds();
                for (Object gc2:gc1) {
                    gc6.add(gc2);
                *//*gc3=((GoodsClass)gc2).getChilds();
                for (Object gc4:gc3) {
                    gc5.add(gc4);
                }*//*
                }
            }*/
            mv.addObject( "gcs", gcs );
            mv.addObject("navTools", this.navTools);

          /*  params.clear();
            // params.put( "store_recommend", Boolean.valueOf( true ) );obj.store_recommend=:store_recommend and
            params.put( "goods_status", Integer.valueOf( 0 ) );
            params.put( "bc_location", "4" );
            List store_reommend_goods_list = this.goodsService.query( "select obj from Goods obj where obj.goods_status=:goods_status and obj.gc.bc_location = :bc_location order by obj.addTime desc", params, 0, 8 );
            List store_reommend_goods = new ArrayList();
            int max = store_reommend_goods_list.size() - 1;
            for( int i = 0; i <= max; i++ ) {
                store_reommend_goods.add(store_reommend_goods_list.get( i ));
            }
            mv.addObject( "store_reommend_goods_list", store_reommend_goods_list );
            mv.addObject( "store_reommend_goods", store_reommend_goods );*/


            return mv;
    }





    @RequestMapping({"/second/testshopclasssecond.htm"})
    public ModelAndView testShopClass2(HttpServletRequest request, HttpServletResponse response,int id){

        ModelAndView mv = new JModelAndView("../shop/newwap/testshop/testshopclasssecond.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);

        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        params.put("id", Long.valueOf(id));
        List gg = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id =:id and obj.bc_location = 4 and obj.display=:display order by obj.sequence asc", params, -1, -1);
        mv.addObject("gg", gg);
        return mv;
    }
    @RequestMapping({"/second/testshopvouchers.htm"})
    public ModelAndView testshopvouchers(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("user/second/20testShop_vouchers.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);

        Map params = new HashMap();
        params.put( "display", Boolean.valueOf( true ) );
        params.put( "bc_location", "4" );
        List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display and obj.bc_location=:bc_location order by obj.sequence asc", params, 0, 14 );
        List gc1=new ArrayList();
        List gc3=new ArrayList();
        List gc5=new ArrayList();
        List gc6=new ArrayList();
        for (Object gc:gcs) {
            //System.out.println(((GoodsClass)gc).getClassName());
            gc1=((GoodsClass)gc).getChilds();
            for (Object gc2:gc1) {
                gc6.add(gc2);
                /*gc3=((GoodsClass)gc2).getChilds();
                for (Object gc4:gc3) {
                    gc5.add(gc4);
                }*/
            }
        }
        mv.addObject( "gc", gc6 );
        mv.addObject( "gcs", gcs );
        mv.addObject("navTools", this.navTools);

        params.clear();
        params.put("coupon_begin_time", new Date());
        params.put("coupon_end_time", new Date());
        params.put("coupon_type", String.valueOf("0"));
        List<Coupon> list=this.couponService.query("select obj from Coupon obj where obj.coupon_begin_time<=:coupon_begin_time and obj.coupon_end_time>=:coupon_end_time and obj.coupon_type=:coupon_type",params,-1,-1);
            mv.addObject("objs",list);

        if(SecurityUserHolder.getCurrentUser()!=null) {
            params.clear();
            params.put("coupon_begin_time", new Date());
            params.put("coupon_end_time", new Date());
            params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
            List<CouponInfo> list2 = this.couponInfoService.query("select obj from CouponInfo obj where obj.goods.id is not null and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time and obj.user.id=:user_id", params, -1, -1);
            mv.addObject("obj", list2);
        }
        return mv;
    }




}
