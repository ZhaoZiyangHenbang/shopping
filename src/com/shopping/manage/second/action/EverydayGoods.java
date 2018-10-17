package com.shopping.manage.second.action;

import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.BargainGoods;
import com.shopping.foundation.domain.query.BargainGoodsQueryObject;
import com.shopping.foundation.service.IBargainGoodsService;
import com.shopping.foundation.service.IGoodsClassService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.IUserConfigService;
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
 * Created by acer on 2017/3/10.
 */


@Controller
public class EverydayGoods {
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
    private GoodsViewTools goodsViewTools;

    @RequestMapping({"/second/everydaygoods.htm"})
    public ModelAndView everyDayGoods(HttpServletRequest request, HttpServletResponse response,String bg_time,String bg_class,String bg_nav, String currentPage, String orderBy, String orderType){
        if (bg_nav !=null&&"youhaohuo".equals(bg_nav)) {
            if (bg_class != null && "jingpin".equals(bg_class)) {
                ModelAndView mv = new JModelAndView("user/second/11haveGoodGoods_veryGood.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
                String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
                if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
                    mv = new JModelAndView("newwap/haveGoodGoods/haveGoodGoods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

                }

                Map params = new HashMap();
                params.put("display", Boolean.valueOf(true));
                List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 2 and obj.display=:display order by obj.sequence asc", params, 0, 15);
                mv.addObject( "navTools", this.navTools );
                mv.addObject("gcs", gcs);
                BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy, orderType);
                bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
                bqo.addQuery("obj.bg_nav", new SysMap("bg_nav", bg_nav), "=");
                bqo.addQuery("obj.bg_class", new SysMap("bg_class", bg_class), "=");
                bqo.setPageSize(Integer.valueOf(6));
                IPageList pList = this.bargainGoodsService.list(bqo);
                String page = "&bg_nav="+bg_nav+"&bg_class="+bg_class;
                CommUtil.saveIPageList2ModelAndView("/second/everydaygoods.htm", "", page, pList, mv);
                mv.addObject("GoodsViewTools", goodsViewTools);
                return mv;
            }
            if (bg_class != null && "zishu".equals(bg_class)) {
              /*  ModelAndView mv = new JModelAndView("newwap/haveGoodGoods/myself.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                request.getSession().setAttribute("shopping_view_type", "wap");*/

                ModelAndView mv = new JModelAndView("user/second/13myselfSay.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
                String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
                if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
                    mv = new JModelAndView("newwap/haveGoodGoods/myself.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                }

                Map params = new HashMap();
                params.put("display", Boolean.valueOf(true));
                List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 2 and obj.display=:display order by obj.sequence asc", params, 0, 15);
                mv.addObject( "navTools", this.navTools );
                mv.addObject("gcs", gcs);
                BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy, orderType);
                bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
                bqo.addQuery("obj.bg_nav", new SysMap("bg_nav", bg_nav), "=");
                bqo.addQuery("obj.bg_class", new SysMap("bg_class", bg_class), "=");
                bqo.setPageSize(Integer.valueOf(20));
                IPageList pList = this.bargainGoodsService.list(bqo);
                CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
                mv.addObject("GoodsViewTools", goodsViewTools);
                return mv;
            }
            if (bg_class != null && "dongtai".equals(bg_class)) {
                ModelAndView mv = new JModelAndView("user/second/15dynamic.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
                String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
                if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
                    mv = new JModelAndView("newwap/haveGoodGoods/dynamic.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                }

                Map params = new HashMap();
                params.put("display", Boolean.valueOf(true));
                params.put("deleteStatus", Boolean.valueOf(false));
                List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null and obj.bc_location = 2 and obj.display=:display order by obj.sequence asc", params, 0, 15);
                mv.addObject( "navTools", this.navTools );
                mv.addObject("gcs", gcs);
                BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy, orderType);
                bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
                bqo.addQuery("obj.bg_nav", new SysMap("bg_nav", bg_nav), "=");
                bqo.addQuery("obj.bg_class", new SysMap("bg_class", bg_class), "=");
                bqo.setPageSize(Integer.valueOf(6));
                IPageList pList = this.bargainGoodsService.list(bqo);
                CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
                mv.addObject("GoodsViewTools", goodsViewTools);
                return mv;
            }

        }
        if (bg_nav !=null&&"lingrungou".equals(bg_nav)) {
            if (bg_class != null && !"".equals(bg_class)) {
              /*  ModelAndView mv = new JModelAndView("wap/store_goods_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                request.getSession().setAttribute("shopping_view_type", "wap");*/
                ModelAndView mv = new JModelAndView("user/second/10zeroShopping_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
                String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
                if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
                    mv = new JModelAndView("wap/store_goods_list2.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                }

                Map params = new HashMap();
                params.put("display", Boolean.valueOf(true));
                List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 2 and obj.display=:display order by obj.sequence asc", params, 0, 15);
                mv.addObject( "navTools", this.navTools );
                mv.addObject("gcs", gcs);
                BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy, orderType);
                bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
                bqo.addQuery("obj.bg_nav", new SysMap("bg_nav", bg_nav), "=");

                bqo.addQuery("obj.bg_class", new SysMap("bg_class", bg_class), "=");

                String page = "&bg_nav="+bg_nav+"&bg_class="+bg_class;
                bqo.setPageSize(Integer.valueOf(8));
                IPageList pList = this.bargainGoodsService.list(bqo);
                CommUtil.saveIPageList2ModelAndView("/second/everydaygoods.htm", "", page, pList, mv);
                mv.addObject("bg_class",bg_class);
                mv.addObject("GoodsViewTools", goodsViewTools);
                return mv;
            }else {
            /*    ModelAndView mv = new JModelAndView("../shop/newwap/zeroshopping/zeroshop.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
                request.getSession().setAttribute("shopping_view_type", "wap");*/
                ModelAndView mv = new JModelAndView("user/second/09zeroShopping.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
                String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
                if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
                    // mv = new JModelAndView("wap/store_goods_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                    mv = new JModelAndView("newwap/zeroshopping/zeroshop.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                }
                Map params = new HashMap();
                params.put("display", Boolean.valueOf(true));
                List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 2 and obj.display=:display order by obj.sequence asc", params, 0, 15);
                mv.addObject("gcs", gcs);
                mv.addObject( "navTools", this.navTools );
                BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy, orderType);
                bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
                bqo.addQuery("obj.bg_nav", new SysMap("bg_nav", bg_nav), "=");
                // bqo.addQuery("obj.bg_class", new SysMap("bg_class", bg_class), "=");
                bqo.setPageSize(Integer.valueOf(8));
                IPageList pList = this.bargainGoodsService.list(bqo);
                String param="&bg_nav="+bg_nav;
                CommUtil.saveIPageList2ModelAndView("/second/everydaygoods.htm", "", param, pList, mv);
                mv.addObject("bg_nav",bg_nav);
                mv.addObject("GoodsViewTools", goodsViewTools);
                return mv;
            }
        }

        if (bg_nav !=null&&"tiantianshangxin".equals(bg_nav)) {
            if (bg_class != null && !"".equals(bg_class)) {
                ModelAndView mv = new JModelAndView("user/second/07everyDayGoods_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
                String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
                if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
                    mv = new JModelAndView("../shop/newwap/tiantian/GoodsList.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                }
                Map params = new HashMap();
                params.put("display", Boolean.valueOf(true));
                params.put("deleteStatus", Boolean.valueOf(false));
                List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null and obj.bc_location = 3 and obj.display=:display order by obj.sequence asc", params, 0, 15);
                mv.addObject("gcs", gcs);
                mv.addObject( "navTools", this.navTools );

                BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy, orderType);
                //bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
                bqo.addQuery("obj.bg_class", new SysMap("bg_class", bg_class), "=");
                bqo.addQuery("obj.bg_nav", new SysMap("bg_nav", bg_nav), "=");
                bqo.addQuery("obj.bg_time", new SysMap("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(new Date()))), "=");
                bqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
                bqo.setPageSize(Integer.valueOf(8));
                IPageList pList = this.bargainGoodsService.list(bqo);
                String page = "&bg_nav="+bg_nav+"&bg_class="+bg_class;
                CommUtil.saveIPageList2ModelAndView("/second/everydaygoods.htm", "", page, pList, mv);
                mv.addObject("bg_class",bg_class);
                mv.addObject("GoodsViewTools", goodsViewTools);
                return mv;
            } else {
                ModelAndView mv = new JModelAndView("user/second/06everyDayGoods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
                String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
                if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
                   /* mv = new JModelAndView("../shop/newwap/tiantian/GoodsList.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
               */
                    mv = new JModelAndView("../shop/newwap/tiantian/everydayhavenew.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

                }
                Map params = new HashMap();
                params.put("display", Boolean.valueOf(true));
                params.put("deleteStatus", Boolean.valueOf(false));
                List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 3 and obj.deleteStatus=:deleteStatus and obj.display=:display order by obj.sequence asc", params, 0, 10);
                mv.addObject( "navTools", this.navTools );
                mv.addObject("gcs", gcs);

                BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy, "asc");
                //bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
                bqo.addQuery("obj.bg_nav", new SysMap("bg_nav", bg_nav), "=");
                bqo.addQuery("obj.bg_time", new SysMap("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(new Date()))), "=");
                bqo.setPageSize(100);
                IPageList pList = this.bargainGoodsService.list(bqo);
                mv.addObject("GoodsViewTools", goodsViewTools);
                //CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
                if( pList.getResult()!=null&&pList.getResult().size()>100){
                    List todayList=pList.getResult();
                    // int todayCount=todayList.size();
                    int todayCount=100;
                    List goodstoday=new ArrayList();
                    for (int i = 0; i < todayCount; i++) {
                        goodstoday.add(todayList.get(i));
                    }
                    mv.addObject("goodstoday",goodstoday);
                    return mv;
                }
                if (pList.getResult()==null){
                    Calendar cal = Calendar.getInstance();
                    cal.add(6, -1);
                    String yesteday=CommUtil.formatShortDate(cal.getTime());
                    BargainGoodsQueryObject bqoy = new BargainGoodsQueryObject(currentPage, mv, orderBy, "asc");
                    //bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
                    bqoy.addQuery("obj.bg_nav", new SysMap("bg_nav", bg_nav), "=");
                    bqoy.addQuery("obj.bg_time", new SysMap("bg_time", CommUtil.formatDate(yesteday)), "=");
                    bqoy.setPageSize(100);
                    IPageList pListy = this.bargainGoodsService.list(bqoy);
                    if( pListy.getResult()!=null&&pListy.getResult().size()>100){
                        List todayList=pListy.getResult();
                        // int todayCount=todayList.size();
                        int todayCount=100;
                        List goodstoday=new ArrayList();
                        for (int i = 0; i < todayCount; i++) {
                            goodstoday.add(todayList.get(i));
                        }
                        mv.addObject("goodstoday",goodstoday);
                        return mv;
                    }else if( pListy.getResult()!=null&&pListy.getResult().size()<=100){
                        List todayList=pListy.getResult();
                        int todayCount=todayList.size();
                        //int todayCount=100;
                        List goodstoday=new ArrayList();
                        for (int i = 0; i < todayCount; i++) {
                            goodstoday.add(todayList.get(i));
                        }
                        mv.addObject("goodstoday",goodstoday);
                        return mv;
                    }else {
                        return mv;
                    }
                }
                if(pList.getResult()!=null&&pList.getResult().size()<100){
                    List todayList=pList.getResult();
                    int todayCount=todayList.size();
                    List goodstoday=new ArrayList();
                    for (int i = 0; i < todayCount; i++) {
                        goodstoday.add(todayList.get(i));
                    }
                    mv.addObject("goodstoday",goodstoday);
                    Calendar cal = Calendar.getInstance();
                    cal.add(6, -1);
                    String yesteday=CommUtil.formatShortDate(cal.getTime());
                    BargainGoodsQueryObject bqoy = new BargainGoodsQueryObject(currentPage, mv, orderBy, "asc");
                    //bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
                    bqoy.addQuery("obj.bg_nav", new SysMap("bg_nav", bg_nav), "=");
                    bqoy.addQuery("obj.bg_time", new SysMap("bg_time", CommUtil.formatDate(yesteday)), "=");
                    bqoy.setPageSize(100);
                    IPageList pListy = this.bargainGoodsService.list(bqoy);
                    if(pListy.getResult()!=null){
                        List yesterdayList=pListy.getResult();
                        int yesterdayCount=yesterdayList.size();
                        int lastCount=100-todayList.size();
                        if (lastCount> yesterdayCount){
                            lastCount=yesterdayCount;
                            for (int i = 0; i < lastCount; i++) {
                                goodstoday.add(yesterdayList.get(i));
                            }
                            mv.addObject("goodstoday",goodstoday);
                            return mv;
                        }
                        if (lastCount< yesterdayCount){
                            for (int i = 0; i < lastCount; i++) {
                                goodstoday.add(yesterdayList.get(i));
                            }
                            mv.addObject("goodstoday",goodstoday);
                            return mv;
                        }
                    }
                }
                return mv;
            }
        }
        return null;
    }



    @RequestMapping({"/second/ajaxeverydaygoods.htm"})
    public ModelAndView ajaxeverydaygoods(HttpServletRequest request, HttpServletResponse response,String bg_class,String bg_nav, String currentPage, String orderBy, String orderType){
        if (bg_nav !=null&&"tiantianshangxin".equals(bg_nav)) {
            if (bg_class != null && !"".equals(bg_class)) {
                ModelAndView
                    mv = new JModelAndView("newwap/0_tongyongtiantian.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

                Map params = new HashMap();
                params.put("display", Boolean.valueOf(true));
                params.put("deleteStatus", Boolean.valueOf(false));
                List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null and obj.bc_location = 3 and obj.display=:display order by obj.sequence asc", params, 0, 15);
                mv.addObject("gcs", gcs);
                mv.addObject("navTools", this.navTools);

                BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy, orderType);
                //bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
                bqo.addQuery("obj.bg_class", new SysMap("bg_class", bg_class), "=");
                bqo.addQuery("obj.bg_nav", new SysMap("bg_nav", bg_nav), "=");
                bqo.addQuery("obj.bg_time", new SysMap("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(new Date()))), "=");
                bqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
                bqo.setPageSize(Integer.valueOf(7));
                IPageList pList = this.bargainGoodsService.list(bqo);
                String page = "&bg_nav=" + bg_nav + "&bg_class=" + bg_class;
                CommUtil.saveIPageList2ModelAndView("/second/everydaygoods.htm", "", page, pList, mv);
                mv.addObject("bg_class", bg_class);
                mv.addObject("GoodsViewTools", goodsViewTools);
                return mv;
            }
        }
        if (bg_nav !=null&&"youhaohuo".equals(bg_nav)) {
            if (bg_class != null && "jingpin".equals(bg_class)) {
                ModelAndView
                    mv = new JModelAndView("newwap/0_youhaohuo_j.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                Map params = new HashMap();
                params.put("display", Boolean.valueOf(true));
                params.put("deleteStatus", Boolean.valueOf(false));
                List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null and obj.bc_location = 2 and obj.display=:display order by obj.sequence asc", params, 0, 15);
                mv.addObject("navTools", this.navTools);
                mv.addObject("gcs", gcs);
                BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy, orderType);
                bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
                bqo.addQuery("obj.bg_nav", new SysMap("bg_nav", bg_nav), "=");
                bqo.addQuery("obj.bg_class", new SysMap("bg_class", bg_class), "=");
                bqo.setPageSize(Integer.valueOf(6));
                IPageList pList = this.bargainGoodsService.list(bqo);
                String page = "&bg_nav=" + bg_nav + "&bg_class=" + bg_class;
                CommUtil.saveIPageList2ModelAndView("/second/everydaygoods.htm", "", page, pList, mv);
                mv.addObject("GoodsViewTools", goodsViewTools);
                return mv;
            }

            if (bg_class != null && "dongtai".equals(bg_class)) {
                ModelAndView mv = new JModelAndView("user/second/15dynamic.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
                String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
                if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
                    mv = new JModelAndView("newwap/0_youhaohuo_j.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                }

                Map params = new HashMap();
                params.put("display", Boolean.valueOf(true));
                params.put("deleteStatus", Boolean.valueOf(false));
                List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null and obj.bc_location = 2 and obj.display=:display order by obj.sequence asc", params, 0, 15);
                mv.addObject( "navTools", this.navTools );
                mv.addObject("gcs", gcs);
                BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy, orderType);
                bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
                bqo.addQuery("obj.bg_nav", new SysMap("bg_nav", bg_nav), "=");
                bqo.addQuery("obj.bg_class", new SysMap("bg_class", bg_class), "=");
                bqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
                bqo.setPageSize(Integer.valueOf(6));
                IPageList pList = this.bargainGoodsService.list(bqo);
                CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
                mv.addObject("GoodsViewTools", goodsViewTools);
                return mv;
            }
        }
        return null;
    }


}
