package com.shopping.manage.second.action;

import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.Goods;
import com.shopping.foundation.domain.GoodsClass;
import com.shopping.foundation.domain.query.GoodsQueryObject;
import com.shopping.foundation.domain.query.TemplateQueryObject;
import com.shopping.foundation.service.*;
import com.shopping.view.web.tools.GoodsViewTools;
import com.shopping.view.web.tools.NavViewTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/14.
 */
@Controller
public class Busniessshool {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IGoodsClassService goodsClassService;

    @Autowired
    private GoodsViewTools goodsViewTools;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private NavViewTools navTools;


    @RequestMapping({"/second/businessshool.htm"})
    public ModelAndView businessShool(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("user/second/21businessSchool.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);

        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {

            mv = new JModelAndView("../shop/newwap/busniessShool/busniessShool.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        }

        Map params = new HashMap();
        params.put( "class_mark", "shang" );
        params.put( "display", Boolean.valueOf( true ) );
        List articles = this.articleService.query( "select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc", params, 0, 5 );
        mv.addObject( "articles", articles );

        params.clear();
        // params.put( "store_recommend", Boolean.valueOf( true ) );obj.store_recommend=:store_recommend and
        params.put( "goods_status", Integer.valueOf( 0 ) );
        params.put( "bc_location", "5" );
        List store_reommend_goods_list = this.goodsService.query( "select obj from Goods obj where obj.goods_status=:goods_status and obj.gc.bc_location = :bc_location order by obj.addTime desc", params, 0, 8 );
        List store_reommend_goods = new ArrayList();
        int max = store_reommend_goods_list.size() - 1;
        for( int i = 0; i <= max; i++ ) {
            store_reommend_goods.add(store_reommend_goods_list.get( i ));
        }
        mv.addObject( "store_reommend_goods_list", store_reommend_goods_list );
        mv.addObject( "store_reommend_goods", store_reommend_goods );

        params.clear();
        params.put( "display", Boolean.valueOf( true ) );
        params.put( "bc_location", "5" );
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
        mv.addObject("GoodsViewTools", goodsViewTools);
        return mv;
    }

    @RequestMapping({"/second/check.htm"})
    public ModelAndView check(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("../shop/newwap/search.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        request.getSession().setAttribute("shopping_view_type", "wap");
        return mv;
    }
    @RequestMapping({"/second/schoolgoodsclass.htm"})
    public ModelAndView schoolGoodsClass(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("../shop/newwap/busniessShool/busniessSchoolClass.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        request.getSession().setAttribute("shopping_view_type", "wap");
        Map params = new HashMap();
        params.put( "class_mark", "news" );
        params.put( "display", Boolean.valueOf( true ) );
        List articles = this.articleService.query( "select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc", params, 0, 5 );
        mv.addObject( "articles", articles );

        params.clear();
        // params.put( "store_recommend", Boolean.valueOf( true ) );obj.store_recommend=:store_recommend and
        params.put( "goods_status", Integer.valueOf( 0 ) );
        params.put( "bc_location", "5" );
        List store_reommend_goods_list = this.goodsService.query( "select obj from Goods obj where obj.goods_status=:goods_status and obj.gc.bc_location = :bc_location order by obj.addTime desc", params, 0, 8 );
        List store_reommend_goods = new ArrayList();
        int max = store_reommend_goods_list.size() - 1;
        for( int i = 0; i <= max; i++ ) {
            store_reommend_goods.add(store_reommend_goods_list.get( i ));
        }
        mv.addObject( "store_reommend_goods_list", store_reommend_goods_list );
        mv.addObject( "store_reommend_goods", store_reommend_goods );

        params.clear();
        params.put( "display", Boolean.valueOf( true ) );
        params.put( "bc_location", "5" );
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
        return mv;
    }


    @RequestMapping({"/second/bussinesschoolclasssecond.htm"})
    public ModelAndView bussinesschoolclasssecond(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("../shop/newwap/busniessShool/busniessSchoolClassSecond.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        request.getSession().setAttribute("shopping_view_type", "wap");

        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 5 and obj.display=:display order by obj.sequence asc", params, 0, 15);
        //int max0 = gcs.size();
        mv.addObject("gcs", gcs);
        return mv;
    }


    @RequestMapping({"/second/schoolgoodssecond.htm"})
    public ModelAndView schoolGoodsSecond(HttpServletRequest request, HttpServletResponse response,int id){
        ModelAndView mv = new JModelAndView("../shop/newwap/busniessShool/busniessSchoolSecond.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        request.getSession().setAttribute("shopping_view_type", "wap");
        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 5 and obj.display=:display order by obj.sequence asc", params, 0, 15);
        //int max0 = gcs.size();
        mv.addObject("gcs", gcs);
        params.clear();
        params.put("display", Boolean.valueOf(true));
        params.put("id", Long.valueOf(id));
        List gg = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id =:id and obj.bc_location = 5 and obj.display=:display order by obj.sequence asc", params, 0, 15);
        mv.addObject("gg", gg);

        params.clear();
        params.put( "store_recommend", Boolean.valueOf( true ) );
        params.put( "goods_status", Integer.valueOf( 0 ) );
        params.put("id", Long.valueOf(id));
        params.put( "bc_location", "5" );
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
        params.put( "bc_location", "5" );
        List list = this.goodsService.query( "select obj from Goods obj where obj.goods_status = :goods_status and obj.gc.parent.id=:id and obj.gc.bc_location = :bc_location order by obj.addTime desc", params, 0, 6 );
        mv.addObject( "list", list );
        mv.addObject("GoodsViewTools", goodsViewTools);
        return mv;
    }

    /**
     * 二级分类跳列表页
     * @param request
     * @param response
     * @param id 二级分类的ID
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param second 二级分类的ID
     * @param first 一级分类的ID
     * @param type type=1 为一级的不限
     * @return
     */
    @RequestMapping({"/second/busniessSchoolsecond_list.htm"})
    public ModelAndView busniessShoolSecondList(HttpServletRequest request, HttpServletResponse response,int id,String currentPage, String orderBy, String orderType,String second,String first,String type) {
        ModelAndView mv = new JModelAndView("user/second/22businessShool_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
            mv = new JModelAndView("newwap/busniessShool/GoodsListB.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        GoodsClass goodsClass = goodsClassService.getObjById(Long.valueOf(id));
        Map params = new HashMap();
        String bc_location=goodsClass.getBc_location();
        mv.addObject("bc_location",bc_location);
        //一级分类
        params.put( "bc_location", goodsClass.getBc_location());
        List<GoodsClass> firstList = goodsClassService.query("select obj from GoodsClass obj where obj.parent.id = null and bc_location = :bc_location", params, -1,-1);
        params.clear();
        params.put( "parent_id", goodsClass.getParent().getId());
        //全部的二级分类
        List<GoodsClass> secondList = goodsClassService.query("select obj from GoodsClass obj where obj.parent.id = :parent_id and deleteStatus = 0 order by obj.addTime desc", params, -1,-1);
        mv.addObject("firstList", firstList);
        mv.addObject("secondList", secondList);
        mv.addObject("goodsClassId", id);
        /*mv.addObject("second",id);
        mv.addObject("first",goodsClass.getParent().getId());*/
        if(first==null && second == null && type==null){
            second = id+"";
            first = goodsClass.getParent().getId()+"";
        }
        String parameter = "&id="+id;
        TemplateQueryObject qo = new TemplateQueryObject(currentPage, mv,orderBy, orderType);
        if(first!=null && second != null){
            mv.addObject("second",second);
            mv.addObject("first",first);
            qo.addQuery("obj.gc.id", new SysMap("gc_id", Long.valueOf(second)), "=");
            parameter = "&id="+id+"&second="+second+"&first="+first;
        }
        if(first!=null && second == null){
            mv.addObject("first",first);
            params.clear();
            params.put( "parent_id", Long.valueOf(first));
            //二级分类的集合
            List<GoodsClass> thirdList = goodsClassService.query("select obj from GoodsClass obj where obj.parent.id = :parent_id and deleteStatus = 0 order by obj.addTime desc", params, -1,-1);
            List<Long> list = new ArrayList<>();
            for (GoodsClass goodsClasss:thirdList) {
                list.add(goodsClasss.getId());
            }
            qo.addQueryIn("obj.gc.id",new SysMap("gc_id", list), "in");
            //qo.addQuery("obj.gc.id", new SysMap("gc_id", Long.valueOf(second)), "=");
            parameter = "&id="+id+"&first="+first;
        }
        qo.addQuery("obj.gc.bc_location", new SysMap("bc_location", goodsClass.getBc_location()), "=");
        //qo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", Boolean.valueOf( true )), "=");
        IPageList pList = goodsService.list(qo);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        //左侧的下拉框
        params.clear();
        params.put( "display", Boolean.valueOf( true ) );
        params.put( "bc_location", goodsClass.getBc_location() );
        List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display and obj.bc_location=:bc_location order by obj.sequence asc", params, 0, 14 );
        List gc1=new ArrayList();
        /*List gc3=new ArrayList();
        List gc5=new ArrayList();*/
        List gc6=new ArrayList();
        for (Object gc:gcs) {
            gc1=((GoodsClass)gc).getChilds();
            for (Object gc2:gc1) {
                gc6.add(gc2);
            }
        }
       params.clear();
        params.put( "class_mark", "shang" );
        params.put( "display", Boolean.valueOf( true ) );
        List articles = this.articleService.query( "select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc", params, 0, 5 );
        mv.addObject( "articles", articles );
        mv.addObject( "gc", gc6 );
        mv.addObject( "gcs", gcs );
        mv.addObject("velocityCount", pList.getRowCount());
        mv.addObject( "navTools", this.navTools );

        CommUtil.saveIPageList2ModelAndView(url + "/second/busniessSchoolsecond_list.htm","", parameter, pList, mv);
        return mv;
    }

    @RequestMapping({"/second/brandcomee.htm"})
    public ModelAndView brandCome(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("newwap/brandcome.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),1,request, response);
        request.getSession().setAttribute("shopping_view_type", "wap");
        return mv;
    }


}
