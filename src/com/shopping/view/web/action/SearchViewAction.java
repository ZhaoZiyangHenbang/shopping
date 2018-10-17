package com.shopping.view.web.action;

import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.*;
import com.shopping.foundation.domain.query.GoodsQueryObject;
import com.shopping.foundation.domain.query.StoreQueryObject;
import com.shopping.foundation.service.*;
import com.shopping.lucene.LuceneResult;
import com.shopping.lucene.LuceneUtil;
import com.shopping.lucene.LuceneVo;
import com.shopping.view.web.tools.GoodsViewTools;
import com.shopping.view.web.tools.StoreViewTools;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.service.NameEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

@Controller
public class SearchViewAction {
    @Autowired
    private ISysConfigService configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IStoreService storeService;
    @Autowired
    private IStoreClassService storeClassService;

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private GoodsViewTools goodsViewTools;

    @Autowired
    private IConsultService consultService;

    @Autowired
    private StoreViewTools storeViewTools;

    @Autowired
    private IStoreGradeService storeGradeService;

    @Autowired
    private IAreaService areaService;

    @Autowired
    private IGoodsClassService goodsClassService;

    @Autowired
    private IGoodsBrandService goodsBrandService;

    @Autowired
    private IGoodsTypeService goodsTypeService;

    @RequestMapping({"/sousuo.htm"})
    public ModelAndView sousuo (HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("newwap/search/search.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        Map param = new HashMap();
        User user=SecurityUserHolder.getCurrentUser();
        List list2=new ArrayList();
       /* if(null!=user) {
            param.put("reply_user_id", user.getId());
            List list = this.consultService.query("select obj from Consult obj where obj.reply_user.id=:reply_user_id order by obj.addTime desc", param, 0, 10);
            mv.addObject("obj", list);
        }else {*/
            Cookie[] cookies= request.getCookies();
            for(Cookie cookie:cookies){
                if("keyword".equals(cookie.getName())){
                    //System.out.println("aaa="+cookie.getValue());
                    String cookiesString=cookie.getValue();
                    String[] cookiess=cookiesString.split("/");
                    for (Object coo:cookiess) {
                        list2.add(coo);
                    }
                }
            }
            mv.addObject("list",list2);
       // }
        return mv;
    }
    @RequestMapping({"/sousuo_delet.htm"})
    public ModelAndView sousuo_delet (HttpServletRequest request, HttpServletResponse response,String valu){
        ModelAndView mv = new JModelAndView("newwap/search/search.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        /*if ("".equals(id)||null==id){
            List list=this.consultService.query("select obj from Consult obj ", null,-1,-1);
            for (int i = 0; i < list.size(); i++) {
                Long Eid=((Consult)list.get(i)).getId();
                this.consultService.delete(Eid);
            }
            return mv;
        }
        Long idL=Long.valueOf(id);
        this.consultService.delete(idL);
        List list=this.consultService.query("select obj from Consult obj ", null,-1,-1);
        mv.addObject("obj",list);
        return mv;*/
        Cookie[] cookies= request.getCookies();
        for(Cookie cookie:cookies){
            if("keyword".equals(cookie.getName())){
                String cookieValue =cookie.getValue();
                String replaceStr=cookieValue;
                if(!"null".equals(valu)){
                    if(cookieValue.contains(valu)){
                        replaceStr= cookieValue.replaceAll(valu+"/","");
                    }
                }else{
                    replaceStr= cookieValue.replaceAll(cookieValue,"");
                }

                Cookie c1=new Cookie("keyword",replaceStr);
                c1.setMaxAge(600000);
                response.addCookie(c1);
            }
        }


        return mv;
    }
    @RequestMapping({"/search_wap_ajax.htm"})
    public ModelAndView search_wap_ajax(HttpServletRequest request, HttpServletResponse response,Boolean ziying,String brand_id,String guigeming_id,String guigezhi_id, String keyword,String area2_id, String currentPage,String salenum,String recommend,String price, String orderType, String store_price_begin, String store_price_end, String area_id, String style,String gc_id) {
        // ModelAndView mv = new JModelAndView("search_goods_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        ModelAndView mv = new JModelAndView("newwap/0_search商品列表.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       /* int a=CommUtil.null2Int(currentPage);
        int b=a+1;
        currentPage = b+"";
        keyword = CommUtil.decode(keyword);
        if (!"".equals(keyword)&&null!=keyword){
            User user=SecurityUserHolder.getCurrentUser();
            Consult consult= new Consult();
            consult.setAddTime(new Date());
            consult.setReply_user(user);
            consult.setConsult_content(keyword);
            this.consultService.save(consult);
        }*/
        String path = System.getProperty("user.dir") + File.separator + "luence" + File.separator + "goods";
        LuceneUtil lucene = LuceneUtil.instance();
        LuceneUtil.setIndex_path(path);

        GoodsQueryObject gqo=new GoodsQueryObject(currentPage, mv, "addTime", "desc");
        //查价格,查地
        if (style!=null&&style.equals("2")) {
            if ((salenum!=null)&&(!salenum.equals(""))){
                gqo.setOrderBy(salenum);
                if (salenum.equals("2")) {
                    orderType = "desc";
                } else {
                    orderType = "asc";
                }
                gqo.setOrderType(orderType);
            }
        }
        if (style!=null&&style.equals("3")) {
            if((price!=null)&&(!price.equals(""))){
                gqo.setOrderBy(price);
                if (price.equals("2")) {

                    orderType = "desc";
                } else {
                    orderType = "asc";
                }
                gqo.setOrderType(orderType);
            }
        }


        if(keyword!=null&&!"".equals(keyword)){
            gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + keyword + "%"), "like");
        }
            if (!"".equals(area2_id)&&null!=area2_id) {
                Map map = new HashMap();
                map.put("area_id", CommUtil.null2Long(area2_id));
                gqo.addQuery("obj.goods_store.area.parent.id=:area_id", map);

            }
        if (store_price_begin != null && !"".equals(store_price_begin)&&(store_price_end==null||"".equals(store_price_end))) {
            Map map = new HashMap();
            map.put("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin)));
            //    map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
            gqo.addQuery("obj.store_price>=:store_price_begin", map);
        }
        if ((store_price_end != null && !"".equals(store_price_end))&&(store_price_begin==null||"".equals(store_price_begin))) {
            Map map = new HashMap();
            map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
            gqo.addQuery("obj.store_price<=:store_price_end", map);
        }
        if(store_price_begin!=null&& !"".equals(store_price_begin)&&store_price_end!=null&& !"".equals(store_price_end)){
            Map map=new HashMap();
            map.put("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin)));
            map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
            gqo.addQuery("obj.store_price>=:store_price_begin and obj.store_price<=:store_price_end",map);
        }
        if(null!=brand_id&&!"".equals(brand_id)){
            gqo.addQuery("obj.goods_brand.id",new SysMap("goods_brand_id",CommUtil.null2Long(brand_id)),"=");
        }

        if(null!=guigeming_id&&!"".equals(guigeming_id)&&null!=guigezhi_id&&!"".equals(guigezhi_id)){
            gqo.addQuery("obj.goods_specs.id",new SysMap("id",CommUtil.null2Long(guigeming_id)),"=");
          //  gqo.addQuery("obj.goods_specs.id",new SysMap("id",CommUtil.null2Long(guigeming_id)),"=");
        }
        if (gc_id != null && !"".equals(gc_id)) {
            Map map = new HashMap();
            map.put("gc_id",CommUtil.null2Long(gc_id));
            gqo.addQuery("obj.gc.id=:gc_id", map);
        }
        gqo.addQuery("obj.goods_status",new SysMap("goods_status",CommUtil.null2Int(0)),"=");
        gqo.setPageSize(Integer.valueOf(12));
        String params=null;
        if(null!=keyword)
            params="&keyword="+keyword;
        if(null!=area2_id)
            params="&area2_id="+area2_id;
        if(null!=area2_id&&null!=area2_id)
            params="&keyword="+keyword+"&area2_id="+area2_id;
        if(gqo!=null) {
            IPageList pList = this.goodsService.list(gqo);
            if(pList!=null)
                CommUtil.saveIPageList2ModelAndView("/search_wap.htm", "", params, pList, mv);
        }
        if (null==style||"".equals(style)) {
            style="1";
        }
        mv.addObject("style",style);
        if(null!=keyword) mv.addObject("keyword",keyword);

        //所有区域
        List area = this.areaService.query("select obj from Area obj where obj.parent is null order by sequence asc", null, -1, -1);
        mv.addObject("area", area);
        //根据关键字查到䣤品牌
        Map param = new HashMap();
        param.put("name", keyword);
        List brand = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.name like :name",param,-1 ,-1);
        mv.addObject("brand", brand);
        mv.addObject("GoodsViewTools", goodsViewTools);
        return mv;
    }
    @RequestMapping({"/search_wap.htm"})
    public ModelAndView search_wap(HttpServletRequest request, HttpServletResponse response,String area_name,String orderBy,String sc_id, String storeGrade_id,String checkbox_id, String storepoint,Boolean ziying, String type,String brand_id,String guigeming_id,String guigezhi_id, String keyword,String area2_id, String currentPage,String salenum,String recommend,String price, String orderType, String store_price_begin, String store_price_end, String area_id, String style,String gc_id) {
        // ModelAndView mv = new JModelAndView("search_goods_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        ModelAndView mv = new JModelAndView("newwap/05new_file.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        keyword = CommUtil.decode(keyword);
        //User user=SecurityUserHolder.getCurrentUser();
        try{
            if (!"".equals(keyword)&&null!=keyword){
           /* if(null!=user) {
                Consult consult = new Consult();
                consult.setAddTime(new Date());
                consult.setReply_user(user);
                consult.setConsult_content(keyword);
                this.consultService.save(consult);
            }else{*/
                Cookie[] cookies= request.getCookies();
                StringBuffer cookieList=new StringBuffer();
                for(Cookie cookie:cookies){
                    //System.out.println("aaa"+cookie.getName());
                    if("keyword".equals(cookie.getName())){
                        //System.out.println("bbbbb"+cookie.getValue());
                        String cookieValue =cookie.getValue();
                        if(cookieValue.contains(keyword)){
                            cookieList=new StringBuffer(cookieValue);;
                            continue;
                        }else{
                            cookieList.append(keyword+"/");
                            cookieList.append(cookie.getValue());
                            //cookieList.toString().replaceAll("sfdasdfasd",cookieList.toString());
                        }
                    }
                }
                Cookie c1=new Cookie("keyword",cookieList.toString());
                c1.setMaxAge(600000);
                response.addCookie(c1);
                //}
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String path = System.getProperty("user.dir") + File.separator + "luence" + File.separator + "goods";
        LuceneUtil lucene = LuceneUtil.instance();
        LuceneUtil.setIndex_path(path);
        if (type.equals("store")) {
                mv = new JModelAndView("newwap/search/sousuoshangpu.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

            StoreQueryObject sqo = new StoreQueryObject(currentPage, mv, "addTime", "desc");
            if ((keyword != null) && (!keyword.equals(""))) {
                sqo.addQuery("obj.store_name", new SysMap("store_name", "%" + keyword + "%"), "like");
                mv.addObject("store_name", keyword);
            }
            if ((sc_id != null) && (!sc_id.equals(""))) {
                StoreClass storeclass = this.storeClassService.getObjById(CommUtil.null2Long(sc_id));
                Set ids = getStoreClassChildIds(storeclass);
                Map map = new HashMap();
                map.put("ids", ids);
                sqo.addQuery("obj.sc.id in (:ids)", map);
                mv.addObject("sc_id", sc_id);
            }
            if ((storeGrade_id != null) && (!storeGrade_id.equals(""))) {
                sqo.addQuery("obj.grade.id", new SysMap("grade_id", CommUtil.null2Long(storeGrade_id)), "=");
                mv.addObject("storeGrade_id", storeGrade_id);
            }
            if ((orderBy != null) && (!orderBy.equals(""))) {
                sqo.setOrderBy(orderBy);
                if (orderBy.equals("addTime"))
                    orderType = "asc";
                else {
                    orderType = "desc";
                }
                sqo.setOrderType(orderType);
                mv.addObject("orderBy", orderBy);
                mv.addObject("orderType", orderType);
            }
            if ((checkbox_id != null) && (!checkbox_id.equals(""))) {
                sqo.addQuery("obj." + checkbox_id, new SysMap("obj_checkbox_id", Boolean.valueOf(true)), "=");
                mv.addObject("checkbox_id", checkbox_id);
            }
            if ((storepoint != null) && (!storepoint.equals(""))) {
                sqo.addQuery("obj.sp.store_evaluate1", new SysMap("sp_store_evaluate1", new BigDecimal(storepoint)), ">=");
                mv.addObject("storepoint", storepoint);
            }
            if ((area_id != null) && (!area_id.equals(""))) {
                mv.addObject("area_id", area_id);
                Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
                Set area_ids = getAreaChildIds(area);
                Map params = new HashMap();
                params.put("ids", area_ids);
                sqo.addQuery("obj.area.id in (:ids)", params);
            }
            if ((area_name != null) && (!area_name.equals(""))) {
                mv.addObject("area_name", area_name);
                sqo.addQuery("obj.area.areaName", new SysMap("areaName", "%" + area_name.trim() + "%"), "like");
                sqo.addQuery("obj.area.parent.areaName", new SysMap("areaName", "%" + area_name.trim() + "%"), "like", "or");
                sqo.addQuery("obj.area.parent.parent.areaName", new SysMap("areaName", "%" + area_name.trim() + "%"), "like", "or");
            }

            sqo.addQuery("obj.store_status", new SysMap("store_status", Integer.valueOf(2)), "=");
            sqo.setPageSize(Integer.valueOf(12));
            IPageList pList = this.storeService.list(sqo);
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
            List scs = this.storeClassService.query("select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);

            Map map = new HashMap();
            // map.put("common", Boolean.valueOf(true));
            // List areas = this.areaService.query("select obj from Area obj where obj.common =:common order by sequence asc", map, -1, -1);
            List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null order by sequence asc", map, -1, -1);
            List child=((Area)areas.get(0)).getChilds();
            mv.addObject("areas", areas);
            mv.addObject("storeViewTools", this.storeViewTools);
            mv.addObject("scs", scs);
            List storeGrades = this.storeGradeService.query("select obj from StoreGrade obj order by sequence asc", null, -1, -1);
            mv.addObject("storeGrades", storeGrades);
        }
        if (type.equals("goods")) {
            GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, "addTime", "desc");
            //查价格,查地
            if (style != null && style.equals("2")) {
                if ((salenum != null) && (!salenum.equals(""))) {
                    gqo.setOrderBy(salenum);
                    if (salenum.equals("2")) {
                        orderType = "desc";
                    } else {
                        orderType = "asc";
                    }
                    gqo.setOrderType(orderType);
                }
            }
            if (style != null && style.equals("3")) {
                if ((price != null) && (!price.equals(""))) {
                    gqo.setOrderBy(price);
                    if (price.equals("2")) {

                        orderType = "desc";
                    } else {
                        orderType = "asc";
                    }
                    gqo.setOrderType(orderType);
                }
            }


            if (keyword != null && !"".equals(keyword)) {
                gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + keyword + "%"), "like");
                mv.addObject("keyword",keyword);
            }
            if (!"".equals(area2_id) && null != area2_id) {
                Map map = new HashMap();
                map.put("area_id", CommUtil.null2Long(area2_id));
                gqo.addQuery("obj.goods_store.area.parent.id=:area_id", map);

            }
            if (store_price_begin != null && !"".equals(store_price_begin) && (store_price_end == null || "".equals(store_price_end))) {
                Map map = new HashMap();
                map.put("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin)));
                //    map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
                gqo.addQuery("obj.store_price>=:store_price_begin", map);
            }
            if ((store_price_end != null && !"".equals(store_price_end)) && (store_price_begin == null || "".equals(store_price_begin))) {
                Map map = new HashMap();
                map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
                gqo.addQuery("obj.store_price<=:store_price_end", map);
            }
            if (store_price_begin != null && !"".equals(store_price_begin) && store_price_end != null && !"".equals(store_price_end)) {
                Map map = new HashMap();
                map.put("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin)));
                map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
                gqo.addQuery("obj.store_price>=:store_price_begin and obj.store_price<=:store_price_end", map);
            }
            if (null != brand_id && !"".equals(brand_id)) {
                gqo.addQuery("obj.goods_brand.id", new SysMap("goods_brand_id", CommUtil.null2Long(brand_id)), "=");
            }

            if (null != guigeming_id && !"".equals(guigeming_id) && null != guigezhi_id && !"".equals(guigezhi_id)) {
                gqo.addQuery("obj.goods_specs.id", new SysMap("id", CommUtil.null2Long(guigeming_id)), "=");
                //  gqo.addQuery("obj.goods_specs.id",new SysMap("id",CommUtil.null2Long(guigeming_id)),"=");
            }
            if (gc_id != null && !"".equals(gc_id)) {
                Map map = new HashMap();
                map.put("gc_id", CommUtil.null2Long(gc_id));
                gqo.addQuery("obj.gc.id=:gc_id", map);
            }
            gqo.addQuery("obj.goods_status", new SysMap("goods_status", CommUtil.null2Int(0)), "=");
            gqo.setPageSize(Integer.valueOf(12));
            String params = null;
            if (null != keyword)
                params = "&keyword=" + keyword;
            if (null != area2_id)
                params = "&area2_id=" + area2_id;
            if (null != area2_id && null != area2_id)
                params = "&keyword=" + keyword + "&area2_id=" + area2_id;
            if (gqo != null) {
                IPageList pList = this.goodsService.list(gqo);
                if (pList != null)
                    CommUtil.saveIPageList2ModelAndView("/search_wap.htm", "", params, pList, mv);
            }
            if (null == style || "".equals(style)) {
                style = "1";
            }
            mv.addObject("style", style);
            if (null != keyword) mv.addObject("keyword", keyword);

            //所有区域
            List area = this.areaService.query("select obj from Area obj where obj.parent is null order by sequence asc", null, -1, -1);
            mv.addObject("area", area);
            //根据关键字查到䣤品牌
            Map param = new HashMap();
            param.put("name", keyword);
            List brand = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.name like :name", param, -1, -1);
            mv.addObject("brand", brand);
            mv.addObject("GoodsViewTools", goodsViewTools);
        }

        return mv;
    }

    @RequestMapping({"/search.htm"})
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response, String type,String keyword,String area2_id, String currentPage,String salenum,String recommend,String price, String orderBy, String orderType, String store_price_begin, String store_price_end, String view_type, String sc_id, String storeGrade_id, String checkbox_id, String storepoint, String area_id, String area_name, String goods_view,String style,String gc_id,String bc_location) {
         ModelAndView mv = new JModelAndView("user/second/05goods_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        String shopping_view_type = CommUtil.null2String(request.getSession(false).getAttribute("shopping_view_type"));
        if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("wap"))) {
           // mv = new JModelAndView("newwap/search/sousuoshangpin.html",
           mv = new JModelAndView("newwap/8shangpinshaixuan.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        if ((type == null) || (type.equals(""))) {
            type = "goods";
        }
        //搜索记录存储
            keyword = CommUtil.decode(keyword);
        if (!"".equals(keyword)&&null!=keyword){
            Map map=new HashMap();
            map.put("keyword",keyword);
             List <Consult> con= this.consultService.query("select obj from Consult obj where obj.consult_content=:keyword",map,-1,-1);
            if (con.size()!=0) {
                for (Consult c:con) {
                    this.consultService.delete(c.getId());
                }
            }
            User user=SecurityUserHolder.getCurrentUser();
            Consult consult= new Consult();
            consult.setAddTime(new Date());
            consult.setReply_user(user);
            consult.setConsult_content(keyword);
            this.consultService.save(consult);}

        if (type.equals("store")) {
           // mv = new JModelAndView("store_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv = new JModelAndView("user/second/3searchShop.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
            if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("wap"))) {
                mv = new JModelAndView("newwap/search/sousuoshangpu.html",
                        this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            }

            StoreQueryObject sqo = new StoreQueryObject(currentPage, mv, "addTime", "desc");
            if ((keyword != null) && (!keyword.equals(""))) {
                sqo.addQuery("obj.store_name", new SysMap("store_name", "%" + keyword + "%"), "like");
                mv.addObject("store_name", keyword);
            }
            if ((sc_id != null) && (!sc_id.equals(""))) {
                StoreClass storeclass = this.storeClassService.getObjById(CommUtil.null2Long(sc_id));
                Set ids = getStoreClassChildIds(storeclass);
                Map map = new HashMap();
                map.put("ids", ids);
                sqo.addQuery("obj.sc.id in (:ids)", map);
                mv.addObject("sc_id", sc_id);
            }
            if ((storeGrade_id != null) && (!storeGrade_id.equals(""))) {
                sqo.addQuery("obj.grade.id", new SysMap("grade_id", CommUtil.null2Long(storeGrade_id)), "=");
                mv.addObject("storeGrade_id", storeGrade_id);
            }
            if ((orderBy != null) && (!orderBy.equals(""))) {
                sqo.setOrderBy(orderBy);
                if (orderBy.equals("addTime"))
                    orderType = "asc";
                else {
                    orderType = "desc";
                }
                sqo.setOrderType(orderType);
                mv.addObject("orderBy", orderBy);
                mv.addObject("orderType", orderType);
            }
            if ((checkbox_id != null) && (!checkbox_id.equals(""))) {
                sqo.addQuery("obj." + checkbox_id, new SysMap("obj_checkbox_id", Boolean.valueOf(true)), "=");
                mv.addObject("checkbox_id", checkbox_id);
            }
            if ((storepoint != null) && (!storepoint.equals(""))) {
                sqo.addQuery("obj.sp.store_evaluate1", new SysMap("sp_store_evaluate1", new BigDecimal(storepoint)), ">=");
                mv.addObject("storepoint", storepoint);
            }
            if ((area_id != null) && (!area_id.equals(""))) {
                mv.addObject("area_id", area_id);
                Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
                Set area_ids = getAreaChildIds(area);
                Map params = new HashMap();
                params.put("ids", area_ids);
                sqo.addQuery("obj.area.id in (:ids)", params);
            }
            if ((area_name != null) && (!area_name.equals(""))) {
                mv.addObject("area_name", area_name);
                sqo.addQuery("obj.area.areaName", new SysMap("areaName", "%" + area_name.trim() + "%"), "like");
                sqo.addQuery("obj.area.parent.areaName", new SysMap("areaName", "%" + area_name.trim() + "%"), "like", "or");
                sqo.addQuery("obj.area.parent.parent.areaName", new SysMap("areaName", "%" + area_name.trim() + "%"), "like", "or");
            }

            sqo.addQuery("obj.store_status", new SysMap("store_status", Integer.valueOf(2)), "=");
            sqo.setPageSize(Integer.valueOf(12));
            IPageList pList = this.storeService.list(sqo);
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
            List scs = this.storeClassService.query("select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);

            Map map = new HashMap();
           // map.put("common", Boolean.valueOf(true));
           // List areas = this.areaService.query("select obj from Area obj where obj.common =:common order by sequence asc", map, -1, -1);
            List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null order by sequence asc", map, -1, -1);
            List child=((Area)areas.get(0)).getChilds();
            mv.addObject("areas", areas);
            mv.addObject("storeViewTools", this.storeViewTools);
            mv.addObject("scs", scs);
            List storeGrades = this.storeGradeService.query("select obj from StoreGrade obj order by sequence asc", null, -1, -1);
            mv.addObject("storeGrades", storeGrades);
        }
//               商品
      /*  String area=null;
        String area2=null;*/
            if(area_id!=null&&!"".equals(area_id)){
                area_id=area_id.substring(0,7);
                if(area_id.equals(",,,,,,,")){
                    area_id=null;
                }

                if(area2_id!=null&&!"".equals(area2_id)){
                    area2_id=area2_id.substring(0,7);
                    if(area2_id.equals(",,,,,,,")){
                        area2_id=null;
                    }
                }

            }

        if (type.equals("goods")) {
            String path = System.getProperty("user.dir") + File.separator + "luence" + File.separator + "goods";
            LuceneUtil lucene = LuceneUtil.instance();
            LuceneUtil.setIndex_path(path);

            GoodsQueryObject gqo=new GoodsQueryObject(currentPage, mv, "addTime", "desc");

            //查价格,查地
            if (style!=null&&style.equals("2")) {
                if ((salenum!=null)&&(!salenum.equals(""))){
                    gqo.setOrderBy(salenum);
                    if (salenum.equals("2")) {
                        orderType = "desc";
                    } else {
                        orderType = "asc";
                    }
                    gqo.setOrderType(orderType);
                }
            }
            if (style!=null&&style.equals("3")) {
                if((price!=null)&&(!price.equals(""))){
                    gqo.setOrderBy(price);
                    if (price.equals("2")) {

                        orderType = "desc";
                    } else {
                        orderType = "asc";
                    }
                    gqo.setOrderType(orderType);
                }
            }

            if(keyword!=null&&!"".equals(keyword)){
                gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + keyword + "%"), "like","or" );
                gqo.addQuery("obj.goods_brand.name ", new SysMap("name", "%" + keyword + "%"), "like" );

            }

            if (recommend!=null&&!"".equals(recommend)){
                gqo.addQuery("obj.store_recommend",new SysMap("store_recommend",Boolean.valueOf(recommend)),"=");
            }

           /* Map mapv = new HashMap();
            mapv.put("goods_status", 0);
            gqo.addQuery("obj.goods_status=:goods_status", mapv);*/

            if( area_id!=null&&!"".equals(area_id)) {
                if (!"".equals(area2_id)&&null!=area2_id) {
                    Map map = new HashMap();
                    map.put("area_id", CommUtil.null2Long(area2_id));
                    gqo.addQuery("obj.goods_store.area.parent.id=:area_id", map);

                } else {

                    Map map = new HashMap();
                    map.put("area_id", CommUtil.null2Long(area_id));
                    gqo.addQuery("obj.goods_store.area.parent.parent.id=:area_id", map);  /*  gqo.addQuery("obj.store_price",new SysMap("store_price",new BigDecimal(store_price_begin)),">=","or");
                     gqo.addQuery("obj.store_price",new SysMap("store_price",new BigDecimal(store_price_end)),"<=","or");*/
                }
            }
               if (store_price_begin != null && !"".equals(store_price_begin)&&(store_price_end==null||"".equals(store_price_end))) {
                   Map map = new HashMap();
                   map.put("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin)));
               //    map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
                   gqo.addQuery("obj.store_price>=:store_price_begin", map);
               }
               if ((store_price_end != null && !"".equals(store_price_end))&&(store_price_begin==null||"".equals(store_price_begin))) {
                   Map map = new HashMap();
                   map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
                   gqo.addQuery("obj.store_price<=:store_price_end", map);
               }
             if(store_price_begin!=null&& !"".equals(store_price_begin)&&store_price_end!=null&& !"".equals(store_price_end)){
                 Map map=new HashMap();
                 map.put("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin)));
                 map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
                 gqo.addQuery("obj.store_price>=:store_price_begin and obj.store_price<=:store_price_end",map);


             }
            if (gc_id != null && !"".equals(gc_id)) {
                Map map = new HashMap();
                map.put("gc_id",CommUtil.null2Long(gc_id));
                gqo.addQuery("obj.gc.id=:gc_id or obj.gc.parent.id=:gc_id or obj.gc.parent.parent.id :=gc_id", map);
            }


            gqo.setPageSize(Integer.valueOf(12));
            if ((keyword != null) && (!keyword.equals(""))) {
                if(gqo!=null) {
                    IPageList pList = this.goodsService.list(gqo);
                    if(pList!=null)
                        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
                }
            }else {

            }
            boolean order_type = true;
            String order_by = "";
            if (CommUtil.null2String(orderType).equals("asc")) {
                order_type = false;
            }
            if (CommUtil.null2String(orderType).equals("")) {
                orderType = "desc";
            }
            if (CommUtil.null2String(orderBy).equals("store_price")) {
                order_by = "store_price";
            }
            if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
                order_by = "goods_salenum";
            }
            if (CommUtil.null2String(orderBy).equals("goods_collect")) {
                order_by = "goods_collect";
            }
            if (CommUtil.null2String(orderBy).equals("goods_addTime")) {
                order_by = "addTime";
            }
            if (CommUtil.null2String(orderBy).equals("gbname")) {
                order_by = "gbname";
            }
            Sort sort = null;
            if (!CommUtil.null2String(order_by).equals("")) {
                sort = new Sort(new SortField(order_by, 7, order_type));
            }
         /*   LuceneResult pList = lucene.search(keyword,
                    CommUtil.null2Int(currentPage),
                    CommUtil.null2Int(store_price_begin),
                    CommUtil.null2Int(store_price_end), null, sort);
            List<GoodsBrand> gb= new ArrayList<>();
            List<GoodsType> gt = new ArrayList<>();*/
           /* for (LuceneVo vo : pList.getVo_list()) {
                Goods goods = this.goodsService.getObjById(vo.getVo_id());
                GoodsType  goodstype = goods.getGc().getGoodsType();
                if(!gt.contains(goodstype))gt.add(goodstype);

                pList.getGoods_list().add(goods);
            }
            CommUtil.saveLucene2ModelAndView("goods", pList, mv);*/
            Map params=new HashMap();
            /*params.put("name",keyword);
            List<GoodsBrand> gb=this.goodsBrandService.query("select distinct  obj from GoodsBrand obj where obj.types.name like :name",params,-1,-1);
            params.clear();
            params.put("name",keyword);
            List<GoodsType> gt=this.goodsTypeService.query("select obj from GoodsType obj where obj.name like :name ",params,-1,-1);*/

            GoodsClass gc = new GoodsClass();
            gc.setClassName("商品搜索结果");
            List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null order by sequence asc", null, -1, -1);
            List child=((Area)areas.get(0)).getChilds();
            mv.addObject("areas", areas);
            /*mv.addObject("gb",gb);
            mv.addObject("gt",gt);*/
            mv.addObject("gc", gc);
            mv.addObject("store_price_end", store_price_end);
            mv.addObject("store_price_begin", store_price_begin);
            mv.addObject("recommend", recommend);
            mv.addObject("keyword", keyword);
            mv.addObject("area_id", area_id);
           mv.addObject("area2_id", area2_id);
            mv.addObject("orderBy", orderBy);
            mv.addObject("orderType", orderType);
            if (CommUtil.null2String(goods_view).equals("list"))
                goods_view = "list";
            else {
                goods_view = "thumb";
            }

           /* if (this.configService.getSysConfig().isZtc_status()) {
                Object ztc_map = new HashMap();
                ((Map)ztc_map).put("ztc_status", Integer.valueOf(3));
                ((Map)ztc_map).put("now_date", new Date());
                ((Map)ztc_map).put("ztc_gold", Integer.valueOf(0));
                List ztc_goods = this.goodsService
                        .query("select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc", (Map)ztc_map, 0, 5);
                mv.addObject("ztc_goods", ztc_goods);
            }*/
            mv.addObject("goods_view", goods_view);
        }
        if (CommUtil.null2String(view_type).equals("")) {
            view_type = "list";
        }
        mv.addObject("view_type", view_type);
        mv.addObject("type", type);
        if (null==style||"".equals(style)) {
            style="1";
        }
        mv.addObject("style",style);
        mv.addObject("bc_location",bc_location);
        mv.addObject("gc_id",gc_id);
        return mv;
    }
    @RequestMapping({"/searchDiv.htm"})
    public ModelAndView searchDiv(HttpServletRequest request, HttpServletResponse response,Boolean ziying,String brand_id,String guigeming_id,String guigezhi_id, String keyword,String area2_id, String currentPage,String salenum,String recommend,String price, String orderType, String store_price_begin, String store_price_end, String area_id, String style,String gc_id) {
        // ModelAndView mv = new JModelAndView("search_goods_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        ModelAndView mv = new JModelAndView("user/second/05goods_list_div.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        String shopping_view_type = CommUtil.null2String(request.getSession(false).getAttribute("shopping_view_type"));
          if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("wap"))) {
            // mv = new JModelAndView("newwap/search/sousuoshangpin.html",
              mv = new JModelAndView("newwap/8shangpinshaixuan.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

          }
        keyword = CommUtil.decode(keyword);
        if (!"".equals(keyword)&&null!=keyword){
            User user=SecurityUserHolder.getCurrentUser();
            Consult consult= new Consult();
            consult.setAddTime(new Date());
            consult.setReply_user(user);
            consult.setConsult_content(keyword);
            this.consultService.save(consult);
        }
            String path = System.getProperty("user.dir") + File.separator + "luence" + File.separator + "goods";
            LuceneUtil lucene = LuceneUtil.instance();
            LuceneUtil.setIndex_path(path);

            GoodsQueryObject gqo=new GoodsQueryObject(currentPage, mv, "addTime", "desc");
            //查价格,查地
            if (style!=null&&style.equals("2")) {
                if ((salenum!=null)&&(!salenum.equals(""))){
                    gqo.setOrderBy(salenum);
                    if (salenum.equals("2")) {
                        orderType = "desc";
                    } else {
                        orderType = "asc";
                    }
                    gqo.setOrderType(orderType);
                }
            }
            if (style!=null&&style.equals("3")) {
                if((price!=null)&&(!price.equals(""))){
                    gqo.setOrderBy(price);
                    if (price.equals("2")) {

                        orderType = "desc";
                    } else {
                        orderType = "asc";
                    }
                    gqo.setOrderType(orderType);
                }
            }

            if(keyword!=null&&!"".equals(keyword)){
                gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + keyword + "%"), "like");
            }

            if (recommend!=null&&!"".equals(recommend)){
                gqo.addQuery("obj.store_recommend",new SysMap("store_recommend",Boolean.valueOf(recommend)),"=");
            }
            if( area_id!=null&&!"".equals(area_id)) {
                if (!"".equals(area2_id)&&null!=area2_id) {
                    Map map = new HashMap();
                    map.put("area_id", CommUtil.null2Long(area2_id));
                    gqo.addQuery("obj.goods_store.area.parent.id=:area_id", map);

                } else {

                    Map map = new HashMap();
                    map.put("area_id", CommUtil.null2Long(area_id));
                    gqo.addQuery("obj.goods_store.area.parent.parent.id=:area_id", map);  /*  gqo.addQuery("obj.store_price",new SysMap("store_price",new BigDecimal(store_price_begin)),">=","or");
                     gqo.addQuery("obj.store_price",new SysMap("store_price",new BigDecimal(store_price_end)),"<=","or");*/
                }
            }
            if (store_price_begin != null && !"".equals(store_price_begin)&&(store_price_end==null||"".equals(store_price_end))) {
                Map map = new HashMap();
                map.put("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin)));
                //    map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
                gqo.addQuery("obj.store_price>=:store_price_begin", map);
            }
            if ((store_price_end != null && !"".equals(store_price_end))&&(store_price_begin==null||"".equals(store_price_begin))) {
                Map map = new HashMap();
                map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
                gqo.addQuery("obj.store_price<=:store_price_end", map);
            }
            if(store_price_begin!=null&& !"".equals(store_price_begin)&&store_price_end!=null&& !"".equals(store_price_end)){
                Map map=new HashMap();
                map.put("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin)));
                map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
                gqo.addQuery("obj.store_price>=:store_price_begin and obj.store_price<=:store_price_end",map);
            }
            if(null!=brand_id&&!"".equals(brand_id)){
                gqo.addQuery("obj.goods_brand.id",new SysMap("goods_brand_id",CommUtil.null2Long(brand_id)),"=");
            }
        if(null!=ziying){
        if(ziying==true){
            gqo.addQuery("obj.gc.bc_location",new SysMap("bc_location",CommUtil.null2String(1)),"=");
            mv.addObject("ziying",ziying);
        }
        }
        if(null!=guigeming_id&&!"".equals(guigeming_id)&&null!=guigezhi_id&&!"".equals(guigezhi_id)){
            gqo.addQuery("obj.goods_specs.id",new SysMap("id",CommUtil.null2Long(guigeming_id)),"=");
            gqo.addQuery("obj.goods_specs.id",new SysMap("id",CommUtil.null2Long(guigeming_id)),"=");
        }
        if (gc_id != null && !"".equals(gc_id)) {
            Map map = new HashMap();
            map.put("gc_id",CommUtil.null2Long(gc_id));
           // gqo.addQuery("obj.gc.id=:gc_id", map);
            // gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + keyword + "%"), "like","or" );
            //gqo.addQuery("obj.gc.id=:gc_id or obj.gc.parent.id=:gc_id or obj.gc.parent.parent.id :=gc_id", map);
            gqo.addQuery("obj.gc.id",new SysMap("gc_id",CommUtil.null2Long(gc_id)),"=");
            gqo.addQuery("obj.gc.parent.id",new SysMap("gc_id",CommUtil.null2Long(gc_id)),"=","or");
            gqo.addQuery("obj.gc.parent.parent.id",new SysMap("gc_id",CommUtil.null2Long(gc_id)),"=","or");

        }
        gqo.addQuery("obj.goods_status",new SysMap("goods_status",CommUtil.null2Int(0)),"=");
            gqo.setPageSize(Integer.valueOf(12));
            if(gqo!=null) {
                IPageList pList = this.goodsService.list(gqo);
                if(pList!=null)
                    CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
            }
        if (null==style||"".equals(style)) {
            style="1";
        }
        mv.addObject("style",style);
        return mv;
    }

    @RequestMapping({"/searchStore.htm"})
    public ModelAndView searchStore(HttpServletRequest request, HttpServletResponse response,String keyword,String area2_id, String currentPage, String area_id) {
        ModelAndView mv = new JModelAndView("user/second/3searchShop_div.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        StoreQueryObject sqo = new StoreQueryObject(currentPage, mv, "addTime", "desc");
        if ((keyword != null) && (!keyword.equals(""))) {
            sqo.addQuery("obj.store_name", new SysMap("store_name", "%" + keyword + "%"), "like");
            mv.addObject("store_name", keyword);
        }
        if ((area_id != null) && (!area_id.equals(""))) {
            //mv.addObject("area_id", area_id);
            Area area;
            if ((area2_id != null) && (!area2_id.equals(""))) {
                 area = this.areaService.getObjById(CommUtil.null2Long(area2_id));
            }else {
                 area = this.areaService.getObjById(CommUtil.null2Long(area_id));
            }
            Set area_ids = getAreaChildIds(area);
            Map params = new HashMap();
            params.put("ids", area_ids);
            sqo.addQuery("obj.area.id in (:ids)", params);
        }
        sqo.addQuery("obj.store_status", new SysMap("store_status", Integer.valueOf(2)), "=");
        sqo.setPageSize(Integer.valueOf(20));
        IPageList pList = this.storeService.list(sqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("storeViewTools", this.storeViewTools);
        return mv;
    }
  /* @RequestMapping({"/searchA.htm"})
    public ModelAndView searchA(String area_id ,HttpServletResponse response,HttpServletRequest request){
       ModelAndView mv = new JModelAndView("search_goods_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
        List area2=area.getChilds();
       List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null order by sequence asc", null, -1, -1);
       mv.addObject("areas","areas");
       mv.addObject("area2","area2");

      *//*  for (Object a:area2) {
            ((Area)a).getAreaName();
        }*//*
       return mv;
    }*/

    @RequestMapping({"/search_ajax.htm"})
    public void searchAjax(HttpServletRequest request, HttpServletResponse response, String keyword, String currentPage, String orderBy, String orderType, String store_price_begin, String store_price_end, String view_type, String sc_id, String storeGrade_id, String checkbox_id, String storepoint) {
        Map<String, Object> map = new HashMap<String, Object>();
        keyword = CommUtil.decode(keyword);
        String path = System.getProperty("user.dir") + File.separator + "luence" + File.separator + "goods";
        LuceneUtil lucene = LuceneUtil.instance();
        LuceneUtil.setIndex_path(path);
        boolean order_type = true;
        String order_by = "";
        if (CommUtil.null2String(orderType).equals("asc")) {

            order_type = false;
        }
        if (CommUtil.null2String(orderType).equals("")) {
            orderType = "desc";
        }
        if (CommUtil.null2String(orderBy).equals("store_price")) {
            order_by = "store_price";
        }
        if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
            order_by = "goods_salenum";
        }
        if (CommUtil.null2String(orderBy).equals("goods_collect")) {
            order_by = "goods_collect";
        }
        if (CommUtil.null2String(orderBy).equals("goods_addTime")) {
            order_by = "addTime";
        }

        Sort sort = null;

        if (!CommUtil.null2String(order_by).equals("")) {
            sort = new Sort(new SortField(order_by, 7, order_type));
        }

        LuceneResult pList = lucene.search(keyword, CommUtil.null2Int(currentPage),
                CommUtil.null2Int(store_price_begin), CommUtil.null2Int(store_price_end), null, sort);

        for (LuceneVo vo : pList.getVo_list()) {
            Goods goods = this.goodsService.getObjById(vo.getVo_id());
            pList.getGoods_list().add(goods);
        }
        map.put("store_price_end", store_price_end);
        map.put("store_price_begin", store_price_begin);
        map.put("keyword", keyword);
        map.put("orderBy", orderBy);
        map.put("orderType", orderType);

        CommUtil.saveWebPaths(map, this.configService.getSysConfig(), request);

        CommUtil.saveLucene2Map("goods", pList, map);

       /*if (this.configService.getSysConfig().isZtc_status()) {
         Object ztc_map = new HashMap();
         ((Map)ztc_map).put("ztc_status", Integer.valueOf(3));
         ((Map)ztc_map).put("now_date", new Date());
         ((Map)ztc_map).put("ztc_gold", Integer.valueOf(0));
         List ztc_goods = this.goodsService.query("select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc", (Map)ztc_map, 0, 5);
         mv.addObject("ztc_goods", ztc_goods);
       }*/

        if (CommUtil.null2String(view_type).equals("")) {
            view_type = "list";
        }
        map.put("view_type", view_type);

        String ret = Json.toJson(map, JsonFormat.compact());
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


    private Set<Long> getStoreClassChildIds(StoreClass sc) {
        Set ids = new HashSet();
        ids.add(sc.getId());
        for (StoreClass storeclass : sc.getChilds()) {
            Set<Long> cids = getStoreClassChildIds(storeclass);
            for (Long cid : cids) {
                ids.add(cid);
            }
        }
        return ids;
    }

    private Set<Long> getAreaChildIds(Area area) {
        Set ids = new HashSet();
        ids.add(area.getId());
        for (Area are : area.getChilds()) {
            Set<Long> cids = getAreaChildIds(are);
            for (Long cid : cids) {
                ids.add(cid);
            }
        }
        return ids;
    }
}




