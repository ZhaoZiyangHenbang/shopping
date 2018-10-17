 package com.shopping.view.web.action;
 
 import java.io.File;
 import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.*;
 import com.shopping.foundation.domain.query.*;
 import com.shopping.foundation.service.*;
 import com.shopping.view.web.tools.*;
 import org.apache.commons.httpclient.util.DateParseException;
 import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
 import com.shopping.manage.admin.tools.UserTools;
import com.shopping.manage.seller.Tools.TransportTools;

 @Controller
 public class GoodsViewAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 @Autowired
 private IExpressCompanyService expressCompanyService;

   @Autowired
   private IGoodsService goodsService;

     @Autowired
     private IGoodsTypeService goodsTypeService;
 
   @Autowired
   private IGoodsClassService goodsClassService;

     @Autowired
     private IUserService userService;
 
   @Autowired
   private IUserGoodsClassService userGoodsClassService;
 
   @Autowired
   private IStoreService storeService;
 
   @Autowired
   private IEvaluateService evaluateService;
 
   @Autowired
   private IOrderFormService orderFormService;
 
   @Autowired
   private IGoodsCartService goodsCartService;
 
   @Autowired
   private IConsultService consultService;
  @Autowired
  private IAddressService addressService;
   @Autowired
   private IGoodsBrandService brandService;
 
   @Autowired
   private IGoodsSpecPropertyService goodsSpecPropertyService;
 
   @Autowired
   private IGoodsTypePropertyService goodsTypePropertyService;
 
   @Autowired
   private IAreaService areaService;
 
   @Autowired
   private IStoreClassService storeClassService;
 
   @Autowired
   private AreaViewTools areaViewTools;
 
   @Autowired
   private GoodsViewTools goodsViewTools;
 
   @Autowired
   private StoreViewTools storeViewTools;
 
   @Autowired
   private UserTools userTools;
 
   @Autowired
   private TransportTools transportTools;

   @Autowired
   private NavViewTools navTools;
     @Autowired
     private IUserGoodsClassService usergoodsclassService;

     @Autowired
     private IGroupGoodsService groupGoodsService;
     @Autowired
     private IGroupService groupService;

     @Autowired
     private ICouponService couponService;

     @Autowired
     private ICouponInfoService couponInfoService;
     @Autowired
     private IStorePartnerService storepartnerService;
 
   @RequestMapping({"/goods_list.htm"})
   public ModelAndView goods_list(HttpServletRequest request, HttpServletResponse response, String gc_id, String store_id, String recommend, String currentPage, String orderBy, String orderType, String begin_price, String end_price)
   {
     String template = "default";
     Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));
     if (store != null) {
      /* if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
         template = store.getTemplate();
       }*/
       ModelAndView mv = new JModelAndView(template + "/30seller_index.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
         if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
             mv =  new JModelAndView( "newwap/44allgoods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         }
       GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
       gqo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", store.getId()), "=");
       gqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
       gqo.addQuery("obj.goods_status", new SysMap("goods_status", CommUtil.null2Int(0)), "=");
         StringBuffer param = new StringBuffer();
         UserGoodsClass ugc = null;
         if(null !=gc_id&&!"".equals(gc_id)){
          ugc = this.userGoodsClassService.getObjById(CommUtil.null2Long(gc_id));
             param.append("&gc_id="+gc_id);
         }
       if (ugc != null) {
         Set<Long> ids = genericUserGcIds(ugc);
         List ugc_list = new ArrayList();
         for (Long g_id : ids) {
           UserGoodsClass temp_ugc = this.userGoodsClassService.getObjById(g_id);
           ugc_list.add(temp_ugc);
         }
         gqo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
         for (int i = 0; i < ugc_list.size(); i++)
           gqo.addQuery("ugc" + i, ugc_list.get(i), "obj.goods_ugcs", 
             "member of", "or");
       }
       else {
         ugc = new UserGoodsClass();
         ugc.setClassName("全部商品");
         mv.addObject("ugc", ugc);
       }

       param.append("&store_id="+store_id);
       if ((recommend != null) && (!recommend.equals(""))) {
         gqo.addQuery("obj.goods_recommend", 
           new SysMap("goods_recommend", Boolean.valueOf(CommUtil.null2Boolean(recommend))), 
           "=");
           param.append("&recommend="+recommend);
       }

       if ((begin_price != null) && (!begin_price.equals(""))) {
         gqo.addQuery("obj.store_price", 
           new SysMap("begin_price", 
           BigDecimal.valueOf(CommUtil.null2Double(begin_price))), 
           ">=");
           param.append("&begin_price="+begin_price);
       }
       if ((end_price != null) && (!end_price.equals(""))) {
         gqo.addQuery("obj.store_price", 
           new SysMap("end_price", 
           BigDecimal.valueOf(CommUtil.null2Double(end_price))), 
           "<=");
           param.append("&end_price="+end_price);
       }
       if(orderBy!=null&& !"".equals(orderBy)){
           param.append("&orderBy="+orderBy);
       }
         gqo.setPageSize(6);
       IPageList pList = this.goodsService.list(gqo);
       String url = this.configService.getSysConfig().getAddress();
       if ((url == null) || (url.equals(""))) {
         url = CommUtil.getURL(request);
       }

       CommUtil.saveIPageList2ModelAndView(url+"/goods_list.htm", "", param.toString(), pList, mv);
       mv.addObject("GoodsViewTools",this.goodsViewTools);
       mv.addObject("ugc", ugc);
       mv.addObject("store", store);
       mv.addObject("store_id",store_id);
       mv.addObject("recommend", recommend);
       mv.addObject("goodsStoreAll", this.getMap(store_id).get("goodsStoreAll"));
       mv.addObject("goodsStoreSX", this.getMap(store_id).get("goodsStoreSX"));
       mv.addObject("goodsStoreCX", this.getMap(store_id).get("goodsStoreCX"));
       mv.addObject("goodsStoreDPDT", this.getMap(store_id).get("goodsStoreDPDT"));
       mv.addObject("begin_price", begin_price);
       mv.addObject("end_price", end_price);
       mv.addObject("goodsViewTools", this.goodsViewTools);
       mv.addObject("storeViewTools", this.storeViewTools);
       mv.addObject("areaViewTools", this.areaViewTools);
       mv.addObject("orderBy",orderBy);
       return mv;
     }
     ModelAndView mv = new JModelAndView("error.html",
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, 
       response);
     mv.addObject("op_title", "请求参数错误");
     mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
       mv.addObject("GoodsViewTools", goodsViewTools);
     return mv;
   }

     @RequestMapping({"/goods_list_ajax.htm"})
     public ModelAndView goods_list_ajax(HttpServletRequest request, HttpServletResponse response, String gc_id, String store_id, String recommend, String currentPage, String orderBy, String orderType, String begin_price, String end_price) {
       ModelAndView mv=null;
       Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));

         if (store != null) {
                 mv =  new JModelAndView( "newwap/0_tongyong.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
             }
       /*     int a=CommUtil.null2Int(currentPage);
            int b=a+1;
            currentPage = b+"";*/
             GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
             gqo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", store.getId()), "=");
             gqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
             gqo.addQuery("obj.goods_status", new SysMap("goods_status", CommUtil.null2Int(0)), "=");
             gqo.setPageSize(6);
             UserGoodsClass ugc = null;
             if(null !=gc_id&&!"".equals(gc_id)){
                 ugc = this.userGoodsClassService.getObjById(CommUtil.null2Long(gc_id));
                 mv.addObject("gc_id",gc_id);
             }
             if (ugc != null) {
                 Set<Long> ids = genericUserGcIds(ugc);
                 List ugc_list = new ArrayList();
                 for (Long g_id : ids) {
                     UserGoodsClass temp_ugc = this.userGoodsClassService.getObjById(g_id);
                     ugc_list.add(temp_ugc);
                 }
                 gqo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
                 for (int i = 0; i < ugc_list.size(); i++)
                     gqo.addQuery("ugc" + i, ugc_list.get(i), "obj.goods_ugcs",
                             "member of", "or");
             } else {
                 ugc = new UserGoodsClass();
                 ugc.setClassName("全部商品");
                 mv.addObject("ugc", ugc);
             }
             if ((recommend != null) && (!recommend.equals(""))) {
                 gqo.addQuery("obj.goods_recommend", new SysMap("goods_recommend", Boolean.valueOf(CommUtil.null2Boolean(recommend))), "=");
                mv.addObject("recommend",recommend);
             }

             if ((begin_price != null) && (!begin_price.equals(""))) {
                 gqo.addQuery("obj.store_price", new SysMap("begin_price", BigDecimal.valueOf(CommUtil.null2Double(begin_price))), ">=");
                 mv.addObject("begin_price",begin_price);
             }
             if ((end_price != null) && (!end_price.equals(""))) {
                 gqo.addQuery("obj.store_price", new SysMap("end_price", BigDecimal.valueOf(CommUtil.null2Double(end_price))), "<=");
                 mv.addObject("end_price",end_price);

             }
             if(orderBy!=null&& !"".equals(orderBy)){
                 mv.addObject("orderType",orderType);
             }
             IPageList pList = this.goodsService.list(gqo);
             CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
         mv.addObject("GoodsViewTools",this.goodsViewTools);
         mv.addObject("store_id",store_id);
             mv.addObject("ugc", ugc);
             mv.addObject("store", store);
             mv.addObject("recommend", recommend);
             mv.addObject("goodsStoreAll", this.getMap(store_id).get("goodsStoreAll"));
             mv.addObject("goodsStoreSX", this.getMap(store_id).get("goodsStoreSX"));
             mv.addObject("goodsStoreCX", this.getMap(store_id).get("goodsStoreCX"));
             mv.addObject("goodsStoreDPDT", this.getMap(store_id).get("goodsStoreDPDT"));
             mv.addObject("begin_price", begin_price);
             mv.addObject("end_price", end_price);
             mv.addObject("goodsViewTools", this.goodsViewTools);
             mv.addObject("storeViewTools", this.storeViewTools);
             mv.addObject("areaViewTools", this.areaViewTools);
             mv.addObject("orderBy",orderBy);

             return mv;
         }

     @RequestMapping({"/goods_new.htm"})
     public ModelAndView goods_new(HttpServletRequest request, HttpServletResponse response,String goods_type, String gc_id, String store_id, String currentPage, String orderBy, String orderType)
     {          goods_type=CommUtil.decode(goods_type);
         UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil.null2Long(gc_id));
         String template = "default";
         Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));
         if (store != null) {
             ModelAndView mv = new JModelAndView(template + "/31store_new.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
             String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
             if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
                 mv =  new JModelAndView( "newwap/45goods_new.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
             }
             GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
             gqo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", store.getId()), "=");
             gqo.addQuery("obj.goods_type",new SysMap("goods_type",goods_type),"=");
             gqo.setPageSize(Integer.valueOf(10));
             IPageList pList = this.goodsService.list(gqo);
             mv.addObject("goods_type",goods_type);
            String url = this.configService.getSysConfig().getAddress();
             if ((url == null) || (url.equals(""))) {
                 url = CommUtil.getURL(request);
             }
             String param = "&store_id="+store_id+"&goods_type="+goods_type;
             CommUtil.saveIPageList2ModelAndView(url+"/goods_new.htm", "", param, pList, mv);
             mv.addObject("store", store);
             mv.addObject("goodsStoreAll", this.getMap(store_id).get("goodsStoreAll"));
             mv.addObject("goodsStoreSX", this.getMap(store_id).get("goodsStoreSX"));
             mv.addObject("goodsStoreCX", this.getMap(store_id).get("goodsStoreCX"));
             mv.addObject("goodsStoreDPDT", this.getMap(store_id).get("goodsStoreDPDT"));
             mv.addObject("store_id",store_id);
             mv.addObject("goods_type",goods_type);
             mv.addObject("GoodsViewTools",this.goodsViewTools);
             return mv;
         }
         ModelAndView mv = new JModelAndView("error.html",
                 this.configService.getSysConfig(),
                 this.userConfigService.getUserConfig(), 1, request,
                 response);
         mv.addObject("op_title", "请求参数错误");
         mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         mv.addObject("GoodsViewTools", goodsViewTools);
         return mv;
     }

     @RequestMapping({"/goods_new_ajax.htm"})
     public ModelAndView goods_new_ajax(HttpServletRequest request, HttpServletResponse response,String goods_type, String gc_id, String store_id, String currentPage, String orderBy, String orderType)
     {goods_type=CommUtil.decode(goods_type);
         UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil.null2Long(gc_id));
         String template = "default";
         Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));
         if (store != null) {
             ModelAndView mv =  new JModelAndView( "newwap/0_tongyong.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

             GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
             gqo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", store.getId()), "=");
             gqo.addQuery("obj.goods_type",new SysMap("goods_type",goods_type),"=");
             gqo.setPageSize(Integer.valueOf(10));
             IPageList pList = this.goodsService.list(gqo);
             mv.addObject("goods_type",goods_type);
             String url = this.configService.getSysConfig().getAddress();
             if ((url == null) || (url.equals(""))) {
                 url = CommUtil.getURL(request);
             }
             String param = "&store_id="+store_id+"&goods_type="+goods_type;
             CommUtil.saveIPageList2ModelAndView(url+"/goods_new.htm", "", param, pList, mv);
             mv.addObject("store", store);
             mv.addObject("goodsStoreAll", this.getMap(store_id).get("goodsStoreAll"));
             mv.addObject("goodsStoreSX", this.getMap(store_id).get("goodsStoreSX"));
             mv.addObject("goodsStoreCX", this.getMap(store_id).get("goodsStoreCX"));
             mv.addObject("goodsStoreDPDT", this.getMap(store_id).get("goodsStoreDPDT"));

             return mv;
         }
         ModelAndView mv = new JModelAndView("error.html",
                 this.configService.getSysConfig(),
                 this.userConfigService.getUserConfig(), 1, request,
                 response);
         mv.addObject("op_title", "请求参数错误");
         mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         mv.addObject("GoodsViewTools", goodsViewTools);
         return mv;
     }

     private Set<Long> genericUserGcIds(UserGoodsClass ugc)
   {
     Set ids = new HashSet();
     ids.add(ugc.getId());
     for (UserGoodsClass child : ugc.getChilds()) {
       Set<Long> cids = genericUserGcIds(child);
       for (Long cid : cids) {
         ids.add(cid);
       }
       ids.add(child.getId());
     }
     return ids;
   }

     @RequestMapping({"/goodsDiv.htm"})
     public ModelAndView goodsDiv(HttpServletRequest request, HttpServletResponse response, String id,String currentPage) {
         String template = "default";
         ModelAndView mv =new JModelAndView(template + "/25goods_detail_div.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         //全部商品评价
         EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
         qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
         qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
         qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
         qo.setPageSize(Integer.valueOf(8));
         IPageList pList = this.evaluateService.list(qo);
         CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/goods_"+id+".htm", "", "", pList, mv);
         //好评
         EvaluateQueryObject qo1 = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
         qo1.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
         qo1.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
         qo1.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
         qo1.addQuery("obj.description_evaluate", new SysMap("description_evaluate", BigDecimal.valueOf(4)), ">=");
         qo1.addQuery("obj.description_evaluate", new SysMap("description_evaluate1", BigDecimal.valueOf(5)), "<=");
         qo1.setPageSize(Integer.valueOf(8));
         IPageList pList1 = this.evaluateService.list(qo1);
         CommUtil.saveIPageList2ModelAndView1(CommUtil.getURL(request) + "/goods_"+id+".htm", "", "", pList1, mv);
         //中评
         EvaluateQueryObject qo2 = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
         qo2.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
         qo2.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
         qo2.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
         qo2.addQuery("obj.description_evaluate", new SysMap("description_evaluate", BigDecimal.valueOf(2)), ">=");
         qo2.addQuery("obj.description_evaluate", new SysMap("description_evaluate1", BigDecimal.valueOf(3)), "<=");
         qo2.setPageSize(Integer.valueOf(8));
         IPageList pList2 = this.evaluateService.list(qo2);
         CommUtil.saveIPageList2ModelAndView2(CommUtil.getURL(request) + "/goods_"+id+".htm", "", "", pList2, mv);
         //差评
         EvaluateQueryObject qo3 = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
         qo3.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
         qo3.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
         qo3.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
         qo3.addQuery("obj.description_evaluate", new SysMap("description_evaluate", BigDecimal.valueOf(1)), "=");
         qo3.setPageSize(Integer.valueOf(8));
         IPageList pList3 = this.evaluateService.list(qo3);
         CommUtil.saveIPageList2ModelAndView3(CommUtil.getURL(request) + "/goods_"+id+".htm", "", "", pList3, mv);
         //有图
         EvaluateQueryObject qo4 = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
         qo4.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
         qo4.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
         qo4.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
         qo4.addQuery("obj.pic_status", new SysMap("pic_status", "0"), "=");
         qo4.setPageSize(Integer.valueOf(8));
         IPageList pList4 = this.evaluateService.list(qo4);
         CommUtil.saveIPageList2ModelAndView4(CommUtil.getURL(request) + "/goods_"+id+".htm", "", "", pList4, mv);
         Map params = new HashMap();
         //评价数量
         params.clear();
         params.put("goods_id",CommUtil.null2Long(id));
         params.put("evaluate_type", "goods");
         List evas = this.evaluateService.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type", params, -1, -1);
         mv.addObject("eva_count", Integer.valueOf(evas.size()));
         //好评
         params.clear();
         params.put("goods_id",CommUtil.null2Long(id));
         params.put("evaluate_type", "goods");
         params.put("description_evaluate", BigDecimal.valueOf(4));
         params.put("description_evaluate1", BigDecimal.valueOf(5));
         List praise = this.evaluateService.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type and obj.description_evaluate>=:description_evaluate and obj.description_evaluate<=:description_evaluate1", params, -1, -1);
         mv.addObject("praise", Integer.valueOf(praise.size()));
         //中评
         params.clear();
         params.put("goods_id",CommUtil.null2Long(id));
         params.put("evaluate_type", "goods");
         params.put("description_evaluate", BigDecimal.valueOf(2));
         params.put("description_evaluate1", BigDecimal.valueOf(3));
         List middle = this.evaluateService.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type and obj.description_evaluate>=:description_evaluate and obj.description_evaluate<=:description_evaluate1", params, -1, -1);
         mv.addObject("middle", Integer.valueOf(middle.size()));
         //差评
         params.clear();
         params.put("goods_id",CommUtil.null2Long(id));
         params.put("evaluate_type", "goods");
         params.put("description_evaluate", BigDecimal.valueOf(1));
         List bad = this.evaluateService.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type and obj.description_evaluate=:description_evaluate", params, -1, -1);
         mv.addObject("bad", Integer.valueOf(bad.size()));
         //有图
         params.clear();
         params.put("goods_id",CommUtil.null2Long(id));
         params.put("evaluate_type", "goods");
         params.put("pic_status", "0");
         List pic = this.evaluateService.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type and obj.pic_status=:pic_status", params, -1, -1);
         mv.addObject("pic", Integer.valueOf(pic.size()));
         return mv;
     }

/*
     @RequestMapping({"/goods_wap_xiang.htm"})
     public ModelAndView goods_wap_xiang(HttpServletRequest request, HttpServletResponse response, String id,String currentPage,String gg_id) {
         ModelAndView  mv = new JModelAndView("wap/store_goods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);



         return mv;
     }*/


     @RequestMapping({"/goods.htm"})
     public ModelAndView goods(HttpServletRequest request, HttpServletResponse response, String id,String currentPage,String gg_id,String userid) {
         ModelAndView mv = null;
         //抢购时间
         List<Group> groups = this.getGroups();
         String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
         Goods obj = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
         if (obj.getGoods_status() == 0||obj.getGoods_status() == 3) {
             String template = "default";
       /*if ((obj.getGoods_store().getTemplate() != null) && (!obj.getGoods_store().getTemplate().equals(""))) {
           template = "default";
         template = obj.getGoods_store().getTemplate();
       }*/
             // mv = new JModelAndView(template + "/store_goods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

             if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
                 mv = new JModelAndView("wap/store_goods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                 // mv = new JModelAndView("newwap/27store_goods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
             }
             if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "pc" )) ) {
                 mv = new JModelAndView(template + "/25goods_detail.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
                 String view=request.getParameter("shopping_view_type");
                 if( (view != null) && (!view.equals( "" )) && (view.equals( "wap" )) ){
                 mv = new JModelAndView("wap/store_goods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);}
                 // mv = new JModelAndView("newwap/27store_goods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
             }
             if(null!=groups){
                 mv.addObject("endTime", groups.get(0).getEndTime());
                 mv.addObject("endTimeLong", groups.get(0).getEndTime().getTime());
             }

             mv.addObject("id",id);
             mv.addObject("gg_id",gg_id);
             Map params = new HashMap();
             //是否为限时抢购商品
             if(gg_id!=null&&!"".equals(gg_id)){
                 GroupGoods ggs=this.groupGoodsService.getObjById(CommUtil.null2Long(gg_id));
                 mv.addObject("ggs",ggs);
             }
             //wap的商品  是否可用优惠券
             params.clear();
             params.put("coupon_begin_time",new Date());
             params.put("coupon_end_time",new Date());
             params.put("coupon_count",CommUtil.null2Int(0));
             params.put("coupon_gid",CommUtil.null2Long(id));
             params.put("deleteStatus",CommUtil.null2Boolean(false));
             List couponZ=this.couponService.query("select obj from Coupon obj where obj.deleteStatus =:deleteStatus and obj.coupon_begin_time <= :coupon_begin_time" +
                     " and obj.coupon_end_time >=:coupon_end_time and obj.coupon_count > :coupon_count and obj.goods.id=:coupon_gid",params,-1,-1);
             mv.addObject("couponZ", couponZ);

             obj.setGoods_click(obj.getGoods_click() + 1);
             if ((this.configService.getSysConfig().isZtc_status()) && (obj.getZtc_status() == 2)) {
                 obj.setZtc_click_num(obj.getZtc_click_num() + 1);
             }
             if ((obj.getGroup() != null) && (obj.getGroup_buy() == 2)) {
                 Group group = obj.getGroup();
                 if (group.getEndTime().before(new Date())) {
                     obj.setGroup(null);
                     obj.setGroup_buy(0);
                     obj.setGoods_current_price(obj.getStore_price());
                 }
             }
             mv.addObject("navTools", this.navTools);
             params.clear();
             params.put("display", Boolean.valueOf(true));
             List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 1 and obj.display=:display order by obj.sequence asc", params, 0, 15);
             mv.addObject("gcs", gcs);
             //商品详情 推荐
             params.clear();
             params.put("goods_store_id",CommUtil.null2Long(obj.getGoods_store().getId()));
             params.put("goods_recommend",Boolean.valueOf( true ));
             List recommend=this.goodsService.query("select obj from Goods obj where obj.goods_recommend =:goods_recommend and obj.goods_store.id=:goods_store_id ",params,0,3);
             mv.addObject("recom",recommend);
             if (obj.getGoods_store().getStore_status() == 2) {
                 mv.addObject("obj", obj);
                 mv.addObject("store", obj.getGoods_store());
                 params.clear();
                 ////店铺可用优惠券
                 params.put("coupon_begin_time",new Date());
                 params.put("coupon_end_time",new Date());
                 params.put("coupon_uid",CommUtil.null2Long(obj.getGoods_store().getUser().getId()));
                 params.put("coupon_count",CommUtil.null2Int(0));
                 params.put("deleteStatus",CommUtil.null2Boolean(false));
                 List coupon=this.couponService.query("select obj from Coupon obj where obj.deleteStatus =:deleteStatus and obj.goods.id is null and obj.ccuser.id=:coupon_uid and obj.coupon_begin_time <= :coupon_begin_time" +
                         " and obj.coupon_end_time >=:coupon_end_time and obj.coupon_count > :coupon_count ",params,-1,-1);
                 //用户已领优惠券
                 User user=SecurityUserHolder.getCurrentUser();
                if(null!=userid&&!"".equals(userid)){
                    User user2=this.userService.getObjById(CommUtil.null2Long(userid));
                    request.getSession().setAttribute("P_user",user2);
                }

                 if(user !=null){
                    // mv.addObject("user",user);

                     params.clear();
                     params.put("coupon_uid",CommUtil.null2Long(user.getId()));
                     params.put("status",CommUtil.null2Int("0"));
                     params.put("id",CommUtil.null2Long(obj.getGoods_store().getUser().getId()));//
                     params.put("deleteStatus",CommUtil.null2Boolean(false));
                     List coupon2=this.couponInfoService.query("select obj from CouponInfo obj where obj.deleteStatus =:deleteStatus and obj.user.id= :coupon_uid and obj.status = :status and obj.coupon.ccuser.id = :id",params,-1,-1);
                     mv.addObject("coupon2",coupon2);
                     //查询当前用户已经领取的优惠券
                     List<Coupon> yiJIng = new ArrayList<>();
                     params.clear();
                     params.put("user_id",SecurityUserHolder.getCurrentUser().getId());
                     params.put("status",CommUtil.null2Int("0"));
                     params.put("deleteStatus",CommUtil.null2Boolean(false));
                     List<CouponInfo> couponInfoList = couponInfoService.query("select obj from CouponInfo obj where obj.deleteStatus =:deleteStatus and obj.user.id =:user_id and obj.status =:status ",params,-1,-1);
                     for (CouponInfo couponInfo:couponInfoList) {
                         params.clear();
                         params.put("deleteStatus",CommUtil.null2Boolean(false));
                         params.put("couponId",couponInfo.getCoupon().getId());
                         params.put("date",new Date());
                         List<Coupon> co = this.couponService.query("select obj from Coupon obj where obj.deleteStatus =:deleteStatus and obj.id =:couponId and obj.coupon_begin_time <:date and obj.coupon_end_time >:date",params,-1,-1);
                         if(co.size()>0){
                             yiJIng.add(co.get(0));
                         }
                     }
                     //用户可以领的优惠券
                     coupon.removeAll(yiJIng);
                     //购物车中商品的数量
                     params.clear();
                     params.put("user_id",CommUtil.null2Long(user.getId()));
                     params.put("deleteStatus",CommUtil.null2Boolean(false));
                     List<GoodsCart> list = this.goodsCartService.query("select obj from GoodsCart obj where obj.deleteStatus =:deleteStatus and obj.sc.user.id= :user_id and obj.deleteStatus = 0",params,-1,-1);
                     mv.addObject("goodsCartCount",list.size());
                 }

                 //可以零的优惠券  展示在详情页上方和下方
                 mv.addObject("coupon",coupon);
                 //积分
                 int l=this.configService.getSysConfig().getConsumptionRatio();
                 int m=0;
                 if(obj.getStore_price()!=null){
                     m=obj.getStore_price().intValue();
                 }
               /*int m=obj.getStore_price().intValue();*/
                 int Money=CommUtil.null2Int(m);
                 int fen=Money/l;
                 int n=this.configService.getSysConfig().getEveryIndentLimit();
                 if(0!=n){
                 if(fen>n)fen=100;
                 }
                 mv.addObject("fen",fen);
                 //店铺内商品分类
                 params.clear();
                 params.put("user_id", obj.getGoods_store().getUser().getId());
                 params.put("display", Boolean.valueOf(true));
                 params.put("deleteStatus",CommUtil.null2Boolean(false));
                 List ugcs = this.userGoodsClassService.query("select obj from UserGoodsClass obj where obj.deleteStatus =:deleteStatus and obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc", params, -1, -1);
                 mv.addObject("ugcs", ugcs);
                 //商品表里面的商品
                 GoodsQueryObject gqo = new GoodsQueryObject();
               //  gqo.setPageSize(Integer.valueOf(4));
                 gqo.addQuery("obj.goods_store.id", new SysMap("store_id", obj.getGoods_store().getId()), "=");
                 gqo.addQuery("obj.goods_recommend", new SysMap("goods_recommend", Boolean.valueOf(true)), "=");
                 gqo.addQuery("obj.id", new SysMap("id", obj.getId()), "!=");
                 gqo.setOrderBy("addTime");
                 gqo.setOrderType("desc");
                 gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
                 mv.addObject("goods_recommend_list", this.goodsService.list(gqo).getResult());
                 //全部商品评价
                 EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
                 qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
                 qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
                 qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
                 if( (shopping_view_type == null) || (shopping_view_type.equals( "" )) || (!shopping_view_type.equals( "wap" )) ) {
                     qo.setPageSize(Integer.valueOf(8));
                 }
                 IPageList pList = this.evaluateService.list(qo);
                 CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/goods_"+id+".htm", "", "", pList, mv);
                 //好评
                 EvaluateQueryObject qo1 = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
                 qo1.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
                 qo1.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
                 qo1.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
                 qo1.addQuery("obj.description_evaluate", new SysMap("description_evaluate", BigDecimal.valueOf(4)), ">=");
                 qo1.addQuery("obj.description_evaluate", new SysMap("description_evaluate1", BigDecimal.valueOf(5)), "<=");
                 if( (shopping_view_type == null) || (shopping_view_type.equals( "" )) || (!shopping_view_type.equals( "wap" )) ) {
                     qo1.setPageSize(Integer.valueOf(8));
                 }
                 IPageList pList1 = this.evaluateService.list(qo1);
                 CommUtil.saveIPageList2ModelAndView1(CommUtil.getURL(request) + "/goods_"+id+".htm", "", "", pList1, mv);
                 //中评
                 EvaluateQueryObject qo2 = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
                 qo2.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
                 qo2.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
                 qo2.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
                 qo2.addQuery("obj.description_evaluate", new SysMap("description_evaluate", BigDecimal.valueOf(2)), ">=");
                 qo2.addQuery("obj.description_evaluate", new SysMap("description_evaluate1", BigDecimal.valueOf(3)), "<=");
                 if( (shopping_view_type == null) || (shopping_view_type.equals( "" )) || (!shopping_view_type.equals( "wap" )) ) {
                     qo2.setPageSize(Integer.valueOf(8));
                 }
                 IPageList pList2 = this.evaluateService.list(qo2);
                 CommUtil.saveIPageList2ModelAndView2(CommUtil.getURL(request) + "/goods_"+id+".htm", "", "", pList2, mv);
                 //差评
                 EvaluateQueryObject qo3 = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
                 qo3.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
                 qo3.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
                 qo3.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
                 qo3.addQuery("obj.description_evaluate", new SysMap("description_evaluate", BigDecimal.valueOf(1)), "=");
                 if( (shopping_view_type == null) || (shopping_view_type.equals( "" )) || (!shopping_view_type.equals( "wap" )) ) {
                     qo3.setPageSize(Integer.valueOf(8));
                 }
                 IPageList pList3 = this.evaluateService.list(qo3);
                 CommUtil.saveIPageList2ModelAndView3(CommUtil.getURL(request) + "/goods_"+id+".htm", "", "", pList3, mv);
                 //有图
                 EvaluateQueryObject qo4 = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
                 qo4.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(id)), "=");
                 qo4.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
                 qo4.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
                 qo4.addQuery("obj.pic_status", new SysMap("pic_status", "0"), "=");
                 if( (shopping_view_type == null) || (shopping_view_type.equals( "" )) || (!shopping_view_type.equals( "wap" )) ) {
                     qo4.setPageSize(Integer.valueOf(8));
                 }
                 IPageList pList4 = this.evaluateService.list(qo4);
                 CommUtil.saveIPageList2ModelAndView4(CommUtil.getURL(request) + "/goods_"+id+".htm", "", "", pList4, mv);
                 //评价数量
                 params.clear();
                 params.put("goods_id",CommUtil.null2Long(id));
                 params.put("evaluate_type", "goods");
                 List evas = this.evaluateService.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type", params, -1, -1);
                 mv.addObject("eva_count", Integer.valueOf(evas.size()));
                 //好评
                 params.clear();
                 params.put("goods_id",CommUtil.null2Long(id));
                 params.put("evaluate_type", "goods");
                 params.put("description_evaluate", BigDecimal.valueOf(4));
                 params.put("description_evaluate1", BigDecimal.valueOf(5));
                 List praise = this.evaluateService.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type and obj.description_evaluate>=:description_evaluate and obj.description_evaluate<=:description_evaluate1", params, -1, -1);
                 mv.addObject("praise", Integer.valueOf(praise.size()));
                 //中评
                 params.clear();
                 params.put("goods_id",CommUtil.null2Long(id));
                 params.put("evaluate_type", "goods");
                 params.put("description_evaluate", BigDecimal.valueOf(2));
                 params.put("description_evaluate1", BigDecimal.valueOf(3));
                 List middle = this.evaluateService.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type and obj.description_evaluate>=:description_evaluate and obj.description_evaluate<=:description_evaluate1", params, -1, -1);
                 mv.addObject("middle", Integer.valueOf(middle.size()));
                 //差评
                 params.clear();
                 params.put("goods_id",CommUtil.null2Long(id));
                 params.put("evaluate_type", "goods");
                 params.put("description_evaluate", BigDecimal.valueOf(1));
                 List bad = this.evaluateService.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type and obj.description_evaluate=:description_evaluate", params, -1, -1);
                 mv.addObject("bad", Integer.valueOf(bad.size()));
                 //有图
                 params.clear();
                 params.put("goods_id",CommUtil.null2Long(id));
                 params.put("evaluate_type", "goods");
                 params.put("pic_status", "0");
                 List pic = this.evaluateService.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type and obj.pic_status=:pic_status", params, -1, -1);
                 mv.addObject("pic", Integer.valueOf(pic.size()));
                 mv.addObject("goodsViewTools", this.goodsViewTools);
                 mv.addObject("storeViewTools", this.storeViewTools);
                 mv.addObject("areaViewTools", this.areaViewTools);
                 mv.addObject("transportTools", this.transportTools);

                 String str=this.configService.getSysConfig().getService_qq_list();
                 String[] stri=CommUtil.splitByChar(str,"\r\n");
                 String strr=stri[0];
                 mv.addObject("strr",strr);

                 List<Goods> user_viewed_goods = (List)request.getSession(false).getAttribute("user_viewed_goods");
                 if (user_viewed_goods == null) {
                     user_viewed_goods = new ArrayList();
                 }
                 boolean add = true;
                 for (Goods goods : user_viewed_goods) {
                     if (goods.getId().equals(obj.getId())) {
                         add = false;
                         break;
                     }
                 }
                 if (add) {
                     if (user_viewed_goods.size() >= 4)
                         user_viewed_goods.set(1, obj);
                     else
                         user_viewed_goods.add(obj);
                 }
                 request.getSession(false).setAttribute("user_viewed_goods", user_viewed_goods);

                 IpAddress ipAddr = IpAddress.getInstance();
                 String current_ip = CommUtil.getIpAddr(request);
                 String current_city = ipAddr.IpStringToAddress(current_ip);
                 if ((current_city == null) || (current_city.equals(""))) {
                     current_city = "全国";
                 }

                 mv.addObject("current_city", current_city);

                 List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
                 mv.addObject("areas", areas);
                 generic_evaluate(obj.getGoods_store(), mv);
             } else {
                 mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                         this.userConfigService.getUserConfig(), 1, request, response);
                 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
                     mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
                             this.userConfigService.getUserConfig(), 1, request, response);
                 }
                 mv.addObject("op_title", "店铺够开通，拒绝访问");
                 mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
             }
         } else {
             mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
             if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
                 mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
             }
             mv.addObject("op_title", "该商品未上架，不允许查看");
             mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         }
         return mv;
     }

     @RequestMapping({"/store_goods_list.htm"})
   public ModelAndView store_goods_list(HttpServletRequest request, HttpServletResponse response,String area2_id,String bc_location, String salenum,String price,String gc_id, String currentPage,String style, String orderBy, String orderType, String store_price_begin, String store_price_end, String brand_ids, String gs_ids, String properties, String op, String goods_name, String area_name, String area_id, String goods_view, String all_property_status, String detail_property_status,
                                        String a_id,String b_id,String p_low,String p_hig,String prope_id)
   {
    // ModelAndView mv = new JModelAndView("store_goods_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     ModelAndView mv = new JModelAndView("user/second/05_01_goods_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
	 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
		/* mv = new JModelAndView("wap/store_goods_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);*/
		 mv = new JModelAndView("newwap/06new_file.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
	 }
        if(null==style){
       style="1";
   }
     GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
        if(null!=gc&&null!=gc.getGoodsType()&&null!=gc.getGoodsType().getProperties()){
            List<GoodsTypeProperty> typenames=gc.getGoodsType().getProperties();
            //List ty=new ArrayList();
            Map<String,List> mapz=new HashMap<String,List>();
            for (int i=0;i<typenames.size();i++){
                String name=typenames.get(i).getName();
                String[] typevalues=typenames.get(i).getValue().split(",");
                List listtypevals= new ArrayList();
                for(int j=0;j<typevalues.length;j++){
                    listtypevals.add(typevalues[j]);
                }

                mapz.put(name,listtypevals);
                //   ty.add(map);
            }
            mv.addObject("ty",mapz);
        }

       //mv.addObject("ty",ty);

     mv.addObject("gc", gc);
     mv.addObject("bc_location", bc_location);
     mv.addObject("GoodsViewTools", goodsViewTools);

       //
       /*Map map3=new HashMap();
       List   goodsBrand  = this.brandService.query("select obj from GoodsBrand obj where obj.types",null,-1,-1);
       mv.addObject("goodsBrand",  goodsBrand);*/


       if ((orderBy == null) || (orderBy.equals(""))) {
       orderBy = "addTime";
     }
     if ((op != null) && (!op.equals(""))) {
       mv.addObject("op", op);
     }
     String orderBy1 = orderBy;
     if (this.configService.getSysConfig().isZtc_status()) {
       orderBy = "ztc_dredge_price desc,obj." + orderBy;
     }
     GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
     Set ids = genericIds(gc);
     Map paras = new HashMap();
     paras.put("ids", ids);
     gqo.addQuery("obj.gc.id in (:ids)", paras);

       if(null!=gc_id&&!"".equals(gc_id)){
           gqo.addQuery("obj.gc.id", new SysMap("gc_id", CommUtil.null2Long(gc_id)), ">=");
           mv.addObject("gc_id", gc_id);

       }
       if (style!=null&&style.equals("2")) {
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
       if (style!=null&&style.equals("3")) {
           if ((price != null) && (!price.equals(""))) {
               gqo.setOrderBy(price);
               if (price.equals("3")) {

                   orderType = "desc";
               } else {
                   orderType = "asc";
               }
               gqo.setOrderType(orderType);
           }
       }

       if (style!=null&&style.equals("4")) {
           gqo.addQuery("obj.gc.bc_location", new SysMap("bc_location", CommUtil.null2String(1)), "=");
       }

     if ((store_price_begin != null) && (!store_price_begin.equals(""))&&(store_price_end==null||"".equals(store_price_end))) {
       gqo.addQuery("obj.store_price", new SysMap("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin))), ">=");
       mv.addObject("store_price_begin", store_price_begin);
     }
     if ((store_price_end != null) && (!store_price_end.equals(""))&&(store_price_begin==null||"".equals(store_price_begin))) {
       gqo.addQuery("obj.store_price", new SysMap("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end))), "<=");
       mv.addObject("store_price_end", store_price_end);
     }
       if(store_price_begin!=null&& !"".equals(store_price_begin)&&store_price_end!=null&& !"".equals(store_price_end)){
           Map map=new HashMap();
           map.put("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin)));
           map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
           gqo.addQuery("obj.store_price>=:store_price_begin and obj.store_price<=:store_price_end",map);
           mv.addObject("store_price_end", store_price_end);
           mv.addObject("store_price_begin", store_price_begin);
       }
       //类型属性筛选
     /*  if (null!=prope_id&&!"".equals(prope_id)){
        String[] ids2=CommUtil.splitByChar(prope_id,",");
        List list=new ArrayList();
            for (int i=0;i<ids2.length;i++){
                list.add(CommUtil.null2Long(ids2[i]));
           }
           Map map=new HashMap();
           map.put("prope_id", list);
           gqo.addQuery("obj.gc.goodsType.properties.id in (:prope_id)",map);
       }*/

        //筛选价格
       if(p_low!=null&& !"".equals(p_low)&&p_hig!=null&& !"".equals(p_hig)){
           Map map=new HashMap();
           map.put("p_low", BigDecimal.valueOf(CommUtil.null2Double(p_low)));
           map.put("p_hig", BigDecimal.valueOf(CommUtil.null2Double(p_hig)));
           gqo.addQuery("obj.store_price>=:p_low and obj.store_price<=:p_hig",map);
           mv.addObject("p_low",p_low);
           mv.addObject("p_hig",p_hig);
           mv.addObject("store_price_end", store_price_end);
           mv.addObject("store_price_begin", store_price_begin);
       }
       if (a_id!=null&&!"".equals(a_id)){
           Map map=new HashMap();
           map.put("area_id", CommUtil.null2Long(a_id));
           gqo.addQuery("obj.goods_store.area.parent.id=:area_id", map);
           mv.addObject("a_id",a_id);
       }
       if (b_id!=null&&!"".equals(b_id)){
           Map map=new HashMap();
           map.put("brand_id", CommUtil.null2Long(b_id));
           gqo.addQuery("obj.goods_brand.id=:brand_id", map);
       }
    /* if ((keyword != null) && (!keyword.equals(""))) {
       gqo.addQuery("obj.gc.goods_name", new SysMap("name", "%" + keyword + "%"), "like");
       mv.addObject("keyword", keyword);
     }
*/
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

     if ((area_id != null) && (!area_id.equals(""))) {
       Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
       mv.addObject("area", area);
       Set area_ids = getAreaChildIds(area);
       Map p_area = new HashMap();
       p_area.put("area_ids", area_ids);
       gqo.addQuery("obj.goods_store.area.id in (:area_ids)", p_area);
     }
     if ((area_name != null) && (!area_name.equals(""))) {
       mv.addObject("area_name", area_name);
       Map like_area = new HashMap();
       like_area.put("area_name", area_name + "%");
       List likes_areas = this.areaService.query("select obj from Area obj where obj.areaName like:area_name", like_area, -1, -1);
       Set like_area_ids = getArrayAreaChildIds(likes_areas);
       like_area.clear();
       like_area.put("like_area_ids", like_area_ids);
       gqo.addQuery("obj.goods_store.area.id in (:like_area_ids)", like_area);
     }
 
     gqo.addQuery("obj.goods_store.store_status", new SysMap("store_status", Integer.valueOf(2)), "=");
//     gqo.setPageSize(Integer.valueOf(12));
     gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
     List goods_property = new ArrayList();
     if (!CommUtil.null2String(brand_ids).equals("")) {
       String[] brand_id_list = brand_ids.substring(1).split("\\|");
       if (brand_id_list.length == 1) {
         String brand_id = brand_id_list[0];
         String[] brand_info_list = brand_id.split(",");
         gqo.addQuery("obj.goods_brand.id", new SysMap("brand_id", CommUtil.null2Long(brand_info_list[0])), "=", "and");
         Map map = new HashMap();
         GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_info_list[0]));
         map.put("name", "品牌");
         map.put("value", brand.getName());
         map.put("type", "brand");
         map.put("id", brand.getId());
         goods_property.add(map);
       } else {
         for (int i = 0; i < brand_id_list.length; i++) {
           String brand_id = brand_id_list[i];
           if (i == 0) {
             String[] brand_info_list = brand_id.split(",");
             gqo.addQuery("and (obj.goods_brand.id=" + CommUtil.null2Long(brand_info_list[0]), null);
             Map map = new HashMap();
             GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_info_list[0]));
             map.put("name", "品牌");
             map.put("value", brand.getName());
             map.put("type", "brand");
             map.put("id", brand.getId());
             goods_property.add(map);
           } else if (i == brand_id_list.length - 1) {
             String[] brand_info_list = brand_id.split(",");
             gqo.addQuery("or obj.goods_brand.id=" + CommUtil.null2Long(brand_info_list[0]) + ")", null);
             Map map = new HashMap();
             GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_info_list[0]));
             map.put("name", "品牌");
             map.put("value", brand.getName());
             map.put("type", "brand");
             map.put("id", brand.getId());
             goods_property.add(map);
           } else {
             String[] brand_info_list = brand_id.split(",");
             gqo.addQuery("or obj.goods_brand.id=" + CommUtil.null2Long(brand_info_list[0]), null);
             Map map = new HashMap();
             GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_info_list[0]));
             map.put("name", "品牌");
             map.put("value", brand.getName());
             map.put("type", "brand");
             map.put("id", brand.getId());
             goods_property.add(map);
           }
         }
       }
       mv.addObject("brand_ids", brand_ids);
     }
     if (!CommUtil.null2String(gs_ids).equals("")) {
       List gsp_lists = generic_gsp(gs_ids);
 
       for (int j = 0; j < gsp_lists.size(); j++) {
         List gsp_list = (List)gsp_lists.get(j);
         if (gsp_list.size() == 1) {
           GoodsSpecProperty gsp = (GoodsSpecProperty)gsp_list.get(0);
           gqo.addQuery("gsp" + j, gsp, "obj.goods_specs", "member of", "and");
           Map map = new HashMap();
           map.put("name", gsp.getSpec().getName());
           map.put("value", gsp.getValue());
           map.put("type", "gs");
           map.put("id", gsp.getId());
           goods_property.add(map);
         } else {
           for (int i = 0; i < gsp_list.size(); i++) {
             if (i == 0) {
               GoodsSpecProperty gsp = (GoodsSpecProperty)gsp_list.get(i);
               gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs", "member of", "and(");
               Map map = new HashMap();
               map.put("name", gsp.getSpec().getName());
               map.put("value", gsp.getValue());
               map.put("type", "gs");
               map.put("id", gsp.getId());
               goods_property.add(map);
             } else if (i == gsp_list.size() - 1) {
               GoodsSpecProperty gsp = (GoodsSpecProperty)gsp_list.get(i);
               gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs)", "member of", "or");
               Map map = new HashMap();
               map.put("name", gsp.getSpec().getName());
               map.put("value", gsp.getValue());
               map.put("type", "gs");
               map.put("id", gsp.getId());
               goods_property.add(map);
             } else {
               GoodsSpecProperty gsp = (GoodsSpecProperty)gsp_list.get(i);
               gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs", "member of", "or");
               Map map = new HashMap();
               map.put("name", gsp.getSpec().getName());
               map.put("value", gsp.getValue());
               map.put("type", "gs");
               map.put("id", gsp.getId());
               goods_property.add(map);
             }
           }
         }
       }
       mv.addObject("gs_ids", gs_ids);
     }
     if (!CommUtil.null2String(properties).equals("")) {
       String[] properties_list = properties.substring(1).split("\\|");
       for (int i = 0; i < properties_list.length; i++) {
         String property_info = properties_list[i];
         String[] property_info_list = property_info.split(",");
         GoodsTypeProperty gtp = this.goodsTypePropertyService.getObjById(CommUtil.null2Long(property_info_list[0]));
 
         Map p_map = new HashMap();
         p_map.put("gtp_name" + i, "%" + gtp.getName().trim() + "%");
         p_map.put("gtp_value" + i, "%" + property_info_list[1].trim() + "%");
         gqo.addQuery("and (obj.goods_property like :gtp_name" + i + " and obj.goods_property like :gtp_value" + i + ")", p_map);
         Map map = new HashMap();
         map.put("name", gtp.getName());
         map.put("value", property_info_list[1]);
         map.put("type", "properties");
         map.put("id", gtp.getId());
         goods_property.add(map);
       }
       mv.addObject("properties", properties);
     }
     Map params = new HashMap();
     params.put("common", Boolean.valueOf(true));
     List areas = this.areaService.query("select obj from Area obj where obj.common=:common order by sequence asc", params, -1, -1);
     mv.addObject("areas", areas);
 
     IPageList pList = this.goodsService.list(gqo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("gc", gc);
     mv.addObject("orderBy", orderBy1);
     mv.addObject("user_viewed_goods", request.getSession(false).getAttribute("user_viewed_goods"));
     mv.addObject("goods_property", goods_property);
     if (CommUtil.null2String(goods_view).equals("list"))
       goods_view = "list";
     else {
       goods_view = "thumb";
     }
 
     if (this.configService.getSysConfig().isZtc_status()) {
       List ztc_goods = null;
       Map ztc_map = new HashMap();
       ztc_map.put("ztc_status", Integer.valueOf(3));
       ztc_map.put("now_date", new Date());
       ztc_map.put("ztc_gold", Integer.valueOf(0));
       if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
         ztc_goods = this.goodsService.query("select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc", ztc_map, 0, 5);
       }
       if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
         ztc_map.put("gc_ids", ids);
         ztc_goods = this.goodsService.query("select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) order by obj.ztc_dredge_price desc", ztc_map, 0, 5);
       }
       mv.addObject("ztc_goods", ztc_goods);
     }
     if ((detail_property_status != null) && 
       (!detail_property_status.equals(""))) {
       mv.addObject("detail_property_status", detail_property_status);
       String[] temp_str = detail_property_status.split(",");
       Map pro_map = new HashMap();
       List pro_list = new ArrayList();
       for (String property_status : temp_str) {
         if ((property_status != null) && (!property_status.equals(""))) {
           String[] mark = property_status.split("_");
           pro_map.put(mark[0], mark[1]);
           pro_list.add(mark[0]);
         }
       }
       mv.addObject("pro_list", pro_list);
       mv.addObject("pro_map", pro_map);
     }
       mv.addObject("style",style);
       mv.addObject("bc_location",bc_location);
       mv.addObject("gc_id",gc_id);
     mv.addObject("goods_view", goods_view);
     mv.addObject("all_property_status", all_property_status);
     //所有区域
       List area = this.areaService.query("select obj from Area obj where obj.parent is null order by sequence asc", null, -1, -1);
       mv.addObject("area", area);
       /*List goodsType=this.goodsTypeService.query("select obj from GoodsType obj where obj.",null,-1,-1);
       mv.addObject("goodsType",goodsType);*/

     return mv;
   }


     @RequestMapping({"/store_goods_list_ajax.htm"})
     public ModelAndView store_goods_list_ajax(HttpServletRequest request, HttpServletResponse response,String area2_id,String bc_location, String salenum,String price,String gc_id, String currentPage,String style, String orderBy, String orderType, String store_price_begin, String store_price_end, String brand_ids, String gs_ids, String properties, String op, String goods_name, String area_name, String area_id, String goods_view, String all_property_status, String detail_property_status,
                                          String a_id,String b_id,String p_low,String p_hig,String prope_id)
     {
         ModelAndView  mv = new JModelAndView("newwap/0_gc商品列表.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         if(null==style){
             style="1";
         }
         int a=CommUtil.null2Int(currentPage);
         int b=a+1;
         currentPage = b+"";

         GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
         if(null!=gc&&null!=gc.getGoodsType()&&null!=gc.getGoodsType().getProperties()){
             List<GoodsTypeProperty> typenames=gc.getGoodsType().getProperties();
             //List ty=new ArrayList();
             Map<String,List> mapz=new HashMap<String,List>();
             for (int i=0;i<typenames.size();i++){
                 String name=typenames.get(i).getName();
                 String[] typevalues=typenames.get(i).getValue().split(",");
                 List listtypevals= new ArrayList();
                 for(int j=0;j<typevalues.length;j++){
                     listtypevals.add(typevalues[j]);
                 }

                 mapz.put(name,listtypevals);
                 //   ty.add(map);
             }
             mv.addObject("ty",mapz);
         }

         //mv.addObject("ty",ty);

         mv.addObject("gc", gc);
         mv.addObject("bc_location", bc_location);
         mv.addObject("GoodsViewTools", goodsViewTools);

         //
       /*Map map3=new HashMap();
       List   goodsBrand  = this.brandService.query("select obj from GoodsBrand obj where obj.types",null,-1,-1);
       mv.addObject("goodsBrand",  goodsBrand);*/


         if ((orderBy == null) || (orderBy.equals(""))) {
             orderBy = "addTime";
         }
         if ((op != null) && (!op.equals(""))) {
             mv.addObject("op", op);
         }
         String orderBy1 = orderBy;
         if (this.configService.getSysConfig().isZtc_status()) {
             orderBy = "ztc_dredge_price desc,obj." + orderBy;
         }
         GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
         Set ids = genericIds(gc);
         Map paras = new HashMap();
         paras.put("ids", ids);
         gqo.addQuery("obj.gc.id in (:ids)", paras);

         if(null!=gc_id&&!"".equals(gc_id)){
             gqo.addQuery("obj.gc.id", new SysMap("gc_id", CommUtil.null2Long(gc_id)), ">=");
             mv.addObject("gc_id", gc_id);

         }
         if (style!=null&&style.equals("2")) {
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
         if (style!=null&&style.equals("3")) {
             if ((price != null) && (!price.equals(""))) {
                 gqo.setOrderBy(price);
                 if (price.equals("3")) {

                     orderType = "desc";
                 } else {
                     orderType = "asc";
                 }
                 gqo.setOrderType(orderType);
             }
         }

         if (style!=null&&style.equals("4")) {
             gqo.addQuery("obj.gc.bc_location", new SysMap("bc_location", CommUtil.null2String(1)), "=");
         }

         if ((store_price_begin != null) && (!store_price_begin.equals(""))&&(store_price_end==null||"".equals(store_price_end))) {
             gqo.addQuery("obj.store_price", new SysMap("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin))), ">=");
             mv.addObject("store_price_begin", store_price_begin);
         }
         if ((store_price_end != null) && (!store_price_end.equals(""))&&(store_price_begin==null||"".equals(store_price_begin))) {
             gqo.addQuery("obj.store_price", new SysMap("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end))), "<=");
             mv.addObject("store_price_end", store_price_end);
         }
         if(store_price_begin!=null&& !"".equals(store_price_begin)&&store_price_end!=null&& !"".equals(store_price_end)){
             Map map=new HashMap();
             map.put("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin)));
             map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
             gqo.addQuery("obj.store_price>=:store_price_begin and obj.store_price<=:store_price_end",map);
             mv.addObject("store_price_end", store_price_end);
             mv.addObject("store_price_begin", store_price_begin);
         }
         //类型属性筛选
     /*  if (null!=prope_id&&!"".equals(prope_id)){
        String[] ids2=CommUtil.splitByChar(prope_id,",");
        List list=new ArrayList();
            for (int i=0;i<ids2.length;i++){
                list.add(CommUtil.null2Long(ids2[i]));
           }
           Map map=new HashMap();
           map.put("prope_id", list);
           gqo.addQuery("obj.gc.goodsType.properties.id in (:prope_id)",map);
       }*/

         //筛选价格
         if(p_low!=null&& !"".equals(p_low)&&p_hig!=null&& !"".equals(p_hig)){
             Map map=new HashMap();
             map.put("p_low", BigDecimal.valueOf(CommUtil.null2Double(p_low)));
             map.put("p_hig", BigDecimal.valueOf(CommUtil.null2Double(p_hig)));
             gqo.addQuery("obj.store_price>=:p_low and obj.store_price<=:p_hig",map);
             mv.addObject("store_price_end", store_price_end);
             mv.addObject("store_price_begin", store_price_begin);
         }
         if (a_id!=null&&!"".equals(a_id)){
             Map map=new HashMap();
             map.put("area_id", CommUtil.null2Long(a_id));
             gqo.addQuery("obj.goods_store.area.parent.id=:area_id", map);
         }
         if (b_id!=null&&!"".equals(b_id)){
             Map map=new HashMap();
             map.put("brand_id", CommUtil.null2Long(b_id));
             gqo.addQuery("obj.goods_brand.id=:brand_id", map);
         }
    /* if ((keyword != null) && (!keyword.equals(""))) {
       gqo.addQuery("obj.gc.goods_name", new SysMap("name", "%" + keyword + "%"), "like");
       mv.addObject("keyword", keyword);
     }
*/
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

         if ((area_id != null) && (!area_id.equals(""))) {
             Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
             mv.addObject("area", area);
             Set area_ids = getAreaChildIds(area);
             Map p_area = new HashMap();
             p_area.put("area_ids", area_ids);
             gqo.addQuery("obj.goods_store.area.id in (:area_ids)", p_area);
         }
         if ((area_name != null) && (!area_name.equals(""))) {
             mv.addObject("area_name", area_name);
             Map like_area = new HashMap();
             like_area.put("area_name", area_name + "%");
             List likes_areas = this.areaService.query("select obj from Area obj where obj.areaName like:area_name", like_area, -1, -1);
             Set like_area_ids = getArrayAreaChildIds(likes_areas);
             like_area.clear();
             like_area.put("like_area_ids", like_area_ids);
             gqo.addQuery("obj.goods_store.area.id in (:like_area_ids)", like_area);
         }

         gqo.addQuery("obj.goods_store.store_status", new SysMap("store_status", Integer.valueOf(2)), "=");
//     gqo.setPageSize(Integer.valueOf(12));
         gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
         List goods_property = new ArrayList();
         if (!CommUtil.null2String(brand_ids).equals("")) {
             String[] brand_id_list = brand_ids.substring(1).split("\\|");
             if (brand_id_list.length == 1) {
                 String brand_id = brand_id_list[0];
                 String[] brand_info_list = brand_id.split(",");
                 gqo.addQuery("obj.goods_brand.id", new SysMap("brand_id", CommUtil.null2Long(brand_info_list[0])), "=", "and");
                 Map map = new HashMap();
                 GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_info_list[0]));
                 map.put("name", "品牌");
                 map.put("value", brand.getName());
                 map.put("type", "brand");
                 map.put("id", brand.getId());
                 goods_property.add(map);
             } else {
                 for (int i = 0; i < brand_id_list.length; i++) {
                     String brand_id = brand_id_list[i];
                     if (i == 0) {
                         String[] brand_info_list = brand_id.split(",");
                         gqo.addQuery("and (obj.goods_brand.id=" + CommUtil.null2Long(brand_info_list[0]), null);
                         Map map = new HashMap();
                         GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_info_list[0]));
                         map.put("name", "品牌");
                         map.put("value", brand.getName());
                         map.put("type", "brand");
                         map.put("id", brand.getId());
                         goods_property.add(map);
                     } else if (i == brand_id_list.length - 1) {
                         String[] brand_info_list = brand_id.split(",");
                         gqo.addQuery("or obj.goods_brand.id=" + CommUtil.null2Long(brand_info_list[0]) + ")", null);
                         Map map = new HashMap();
                         GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_info_list[0]));
                         map.put("name", "品牌");
                         map.put("value", brand.getName());
                         map.put("type", "brand");
                         map.put("id", brand.getId());
                         goods_property.add(map);
                     } else {
                         String[] brand_info_list = brand_id.split(",");
                         gqo.addQuery("or obj.goods_brand.id=" + CommUtil.null2Long(brand_info_list[0]), null);
                         Map map = new HashMap();
                         GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_info_list[0]));
                         map.put("name", "品牌");
                         map.put("value", brand.getName());
                         map.put("type", "brand");
                         map.put("id", brand.getId());
                         goods_property.add(map);
                     }
                 }
             }
             mv.addObject("brand_ids", brand_ids);
         }
         if (!CommUtil.null2String(gs_ids).equals("")) {
             List gsp_lists = generic_gsp(gs_ids);

             for (int j = 0; j < gsp_lists.size(); j++) {
                 List gsp_list = (List)gsp_lists.get(j);
                 if (gsp_list.size() == 1) {
                     GoodsSpecProperty gsp = (GoodsSpecProperty)gsp_list.get(0);
                     gqo.addQuery("gsp" + j, gsp, "obj.goods_specs", "member of", "and");
                     Map map = new HashMap();
                     map.put("name", gsp.getSpec().getName());
                     map.put("value", gsp.getValue());
                     map.put("type", "gs");
                     map.put("id", gsp.getId());
                     goods_property.add(map);
                 } else {
                     for (int i = 0; i < gsp_list.size(); i++) {
                         if (i == 0) {
                             GoodsSpecProperty gsp = (GoodsSpecProperty)gsp_list.get(i);
                             gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs", "member of", "and(");
                             Map map = new HashMap();
                             map.put("name", gsp.getSpec().getName());
                             map.put("value", gsp.getValue());
                             map.put("type", "gs");
                             map.put("id", gsp.getId());
                             goods_property.add(map);
                         } else if (i == gsp_list.size() - 1) {
                             GoodsSpecProperty gsp = (GoodsSpecProperty)gsp_list.get(i);
                             gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs)", "member of", "or");
                             Map map = new HashMap();
                             map.put("name", gsp.getSpec().getName());
                             map.put("value", gsp.getValue());
                             map.put("type", "gs");
                             map.put("id", gsp.getId());
                             goods_property.add(map);
                         } else {
                             GoodsSpecProperty gsp = (GoodsSpecProperty)gsp_list.get(i);
                             gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs", "member of", "or");
                             Map map = new HashMap();
                             map.put("name", gsp.getSpec().getName());
                             map.put("value", gsp.getValue());
                             map.put("type", "gs");
                             map.put("id", gsp.getId());
                             goods_property.add(map);
                         }
                     }
                 }
             }
             mv.addObject("gs_ids", gs_ids);
         }
         if (!CommUtil.null2String(properties).equals("")) {
             String[] properties_list = properties.substring(1).split("\\|");
             for (int i = 0; i < properties_list.length; i++) {
                 String property_info = properties_list[i];
                 String[] property_info_list = property_info.split(",");
                 GoodsTypeProperty gtp = this.goodsTypePropertyService.getObjById(CommUtil.null2Long(property_info_list[0]));

                 Map p_map = new HashMap();
                 p_map.put("gtp_name" + i, "%" + gtp.getName().trim() + "%");
                 p_map.put("gtp_value" + i, "%" + property_info_list[1].trim() + "%");
                 gqo.addQuery("and (obj.goods_property like :gtp_name" + i + " and obj.goods_property like :gtp_value" + i + ")", p_map);
                 Map map = new HashMap();
                 map.put("name", gtp.getName());
                 map.put("value", property_info_list[1]);
                 map.put("type", "properties");
                 map.put("id", gtp.getId());
                 goods_property.add(map);
             }
             mv.addObject("properties", properties);
         }
         Map params = new HashMap();
         params.put("common", Boolean.valueOf(true));
         List areas = this.areaService.query("select obj from Area obj where obj.common=:common order by sequence asc", params, -1, -1);
         mv.addObject("areas", areas);

         IPageList pList = this.goodsService.list(gqo);
         CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
         mv.addObject("gc", gc);
         mv.addObject("orderBy", orderBy1);
         mv.addObject("user_viewed_goods", request.getSession(false).getAttribute("user_viewed_goods"));
         mv.addObject("goods_property", goods_property);
         if (CommUtil.null2String(goods_view).equals("list"))
             goods_view = "list";
         else {
             goods_view = "thumb";
         }

         if (this.configService.getSysConfig().isZtc_status()) {
             List ztc_goods = null;
             Map ztc_map = new HashMap();
             ztc_map.put("ztc_status", Integer.valueOf(3));
             ztc_map.put("now_date", new Date());
             ztc_map.put("ztc_gold", Integer.valueOf(0));
             if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
                 ztc_goods = this.goodsService.query("select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc", ztc_map, 0, 5);
             }
             if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
                 ztc_map.put("gc_ids", ids);
                 ztc_goods = this.goodsService.query("select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) order by obj.ztc_dredge_price desc", ztc_map, 0, 5);
             }
             mv.addObject("ztc_goods", ztc_goods);
         }
         if ((detail_property_status != null) &&
                 (!detail_property_status.equals(""))) {
             mv.addObject("detail_property_status", detail_property_status);
             String[] temp_str = detail_property_status.split(",");
             Map pro_map = new HashMap();
             List pro_list = new ArrayList();
             for (String property_status : temp_str) {
                 if ((property_status != null) && (!property_status.equals(""))) {
                     String[] mark = property_status.split("_");
                     pro_map.put(mark[0], mark[1]);
                     pro_list.add(mark[0]);
                 }
             }
             mv.addObject("pro_list", pro_list);
             mv.addObject("pro_map", pro_map);
         }
         mv.addObject("style",style);
        // mv.addObject("currentPage",currentPage);
         mv.addObject("bc_location",bc_location);
         mv.addObject("gc_id",gc_id);
         mv.addObject("goods_view", goods_view);
         mv.addObject("all_property_status", all_property_status);
         //所有区域
         List area = this.areaService.query("select obj from Area obj where obj.parent is null order by sequence asc", null, -1, -1);
         mv.addObject("area", area);
       /*List goodsType=this.goodsTypeService.query("select obj from GoodsType obj where obj.",null,-1,-1);
       mv.addObject("goodsType",goodsType);*/

         return mv;
     }

   private Set<Long> getArrayAreaChildIds(List<Area> areas) {
     Set ids = new HashSet();
     for (Area area : areas) {
       ids.add(area.getId());
       for (Area are : area.getChilds()) {
         Set<Long> cids = getAreaChildIds(are);
         for (Long cid : cids) {
           ids.add(cid);
         }
       }
     }
     return ids;
   }
 
   @RequestMapping({"/ztc_goods_list.htm"})
   public ModelAndView ztc_goods_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String goods_view)
   {
     ModelAndView mv = new JModelAndView("ztc_goods_list.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, 
       orderType);
     gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
     gqo.addQuery("obj.ztc_status", new SysMap("ztc_status", Integer.valueOf(3)), "=");
     gqo.addQuery("obj.ztc_begin_time", 
       new SysMap("ztc_begin_time", 
       new Date()), "<=");
     gqo.addQuery("obj.ztc_gold", new SysMap("ztc_gold", Integer.valueOf(0)), ">");
     gqo.setOrderBy("ztc_dredge_price");
     gqo.setOrderType("desc");
     gqo.setPageSize(Integer.valueOf(20));
     IPageList pList = this.goodsService.list(gqo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("goods_view", goods_view);
     mv.addObject("user_viewed_goods", request.getSession(false)
       .getAttribute("user_viewed_goods"));
     return mv;
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
 
   private List<List<GoodsSpecProperty>> generic_gsp(String gs_ids) {
     List<List<GoodsSpecProperty>> list = new ArrayList<List<GoodsSpecProperty>>();
     String[] gs_id_list = gs_ids.substring(1).split("\\|");
     for (String gd_id_info : gs_id_list) {
       String[] gs_info_list = gd_id_info.split(",");
       GoodsSpecProperty gsp = this.goodsSpecPropertyService
         .getObjById(CommUtil.null2Long(gs_info_list[0]));
       boolean create = true;
       for (List<GoodsSpecProperty> gsp_list : list) {
         for (GoodsSpecProperty gsp_temp : gsp_list)
         {
           if (gsp_temp.getSpec().getId()
             .equals(gsp.getSpec().getId())) {
             gsp_list.add(gsp);
             create = false;
             break;
           }
         }
       }
       if (create) {
         List gsps = new ArrayList();
         gsps.add(gsp);
         list.add(gsps);
       }
     }
     return list;
   }
 
   @RequestMapping({"/goods_evaluation.htm"})
   public ModelAndView goods_evaluation(HttpServletRequest request, HttpServletResponse response,String id,  String goods_id, String currentPage) {
       String template = "default";
     ModelAndView mv = new JModelAndView(template+"goods_evaluation.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       String shopping_view_type = CommUtil.null2String(request.getSession().getAttribute("shopping_view_type"));
     if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("wap"))) {
       mv = new JModelAndView("newwap/wodedingdandaipingjia.html",
               this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     }
    OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
     mv.addObject("of", of);
//     Long idd =  of.getAddr().getId();
//     Address ad = addressService.getObjById(idd);
//     mv.addObject("ad",ad);
  /*  String ecname = of.getEc().getCompany_name();*/

     EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");

     qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
     qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
     qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
     qo.setPageSize(Integer.valueOf(8));
     IPageList pList = this.evaluateService.list(qo);
     CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/goods_evaluation.htm", "", "", pList, mv);
     mv.addObject("storeViewTools", this.storeViewTools);

     Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
     mv.addObject("goods", goods);

     return mv;
   }
     @RequestMapping({"/order_evaluation.htm"})
     public ModelAndView order_evaluation(HttpServletRequest request, HttpServletResponse response,String id) {
         ModelAndView mv = new JModelAndView("newwap/wodedingdandaipingjia.html",
                     this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
         String b = String.valueOf(of.getAddTime());
         String d = b.substring(0, b.lastIndexOf("."));
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         try {
             of.setAddTime(sdf.parse(d));
         }catch  (ParseException e){
             e.printStackTrace();
         }

         mv.addObject("of", of);
         return mv;
     }
     //wap订单评价
     @RequestMapping({"/order_evaluation_edit.htm"})
     public ModelAndView order_evaluation_edit(HttpServletRequest request, HttpServletResponse response,String id) {
         ModelAndView mv =  new JModelAndView("newwap/order_evaluation.html",
                 this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
         mv.addObject("of", of);
        return mv;
     }
     //wap订单评价保存
     @RequestMapping({"/order_evaluation_save.htm"})
     public String order_evaluation_save(HttpServletRequest request, HttpServletResponse response,String id) throws IOException {
         OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
         of.setOrder_status(50);
         this.orderFormService.update(of);
         Evaluate eva = new Evaluate();
         eva.setAddTime(new Date());
         eva.setEvaluate_order_info(request.getParameter("evaluate_order_info"));
         eva.setService_evaluate(BigDecimal.valueOf(
                 CommUtil.null2Double(request.getParameter("service_evaluate"))));
         eva.setEvaluate_type("order");
         eva.setEvaluate_user(SecurityUserHolder.getCurrentUser());
         eva.setOf(of);
         this.evaluateService.save(eva);
         return "redirect:/buyer/order.htm?currentPage=1";
     }
     //wap订单评价详情
     @RequestMapping({"/order_evaluation_info.htm"})
     public ModelAndView order_evaluation_info(HttpServletRequest request, HttpServletResponse response,String id){
         ModelAndView  mv = new JModelAndView("newwap/order_evaluation_info.html",
                 this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
         List evaluate=this.evaluateService.query("select obj from Evaluate obj where evaluate_type = 'order' and of_id ='"+id+"'",
                 null, -1, -1);
         mv.addObject("evaluate", evaluate.get(0));
         mv.addObject("of", of);
         return mv;
     }
     //wap商品评价
     @RequestMapping({"/goods_evaluation1.htm"})
     public ModelAndView goods_evaluation1(HttpServletRequest request, HttpServletResponse response,String id,  String goods_id,String goodsCart_id,String status) {
         ModelAndView mv =  new JModelAndView("newwap/goods_evaluation.html",
                     this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
         mv.addObject("of", of);
         Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
         GoodsCart goodsCart=this.goodsCartService.getObjById(CommUtil.null2Long(goodsCart_id));
         mv.addObject("goods", goods);
         mv.addObject("goodsCart", goodsCart);
         mv.addObject("status", status);
         return mv;
     }
     //wap商品评价详情
     @RequestMapping({"/goods_evaluation_info.htm"})
     public ModelAndView goods_evaluation_info(HttpServletRequest request, HttpServletResponse response,String id,  String goods_id,String goodsCart_id,String status) {
         ModelAndView mv =  new JModelAndView("newwap/goods_evaluation_info.html",
                 this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
         mv.addObject("of", of);
         Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
         GoodsCart goodsCart=this.goodsCartService.getObjById(CommUtil.null2Long(goodsCart_id));
         List evaluate=this.evaluateService.query("select obj from Evaluate obj where evaluate_goods_id ='"+goods_id+"' and of_id ='"+id+"'",
                 null, -1, -1);

         mv.addObject("goods", goods);
         mv.addObject("goodsCart", goodsCart);
         mv.addObject("evaluate", evaluate.get(0));
         mv.addObject("user",SecurityUserHolder.getCurrentUser());
         mv.addObject("status", status);
         return mv;
     }
     //wap商品评价保存
     @RequestMapping({"/goods/goods_evaluation_save.htm"})
     public ModelAndView goods_evaluation_save(HttpServletRequest request, HttpServletResponse response,String id,  String goods_id,String goodsCart_id,String status) throws IOException {
         ModelAndView  mv ;
         if ("0".equals(status)) {
              mv = new JModelAndView("newwap/shangpinpingjia.html",
                     this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
             List of = this.orderFormService
                     .query(
                             "select obj from OrderForm obj where user_id ='"+SecurityUserHolder.getCurrentUser().getId()+"' ",
                             null, -1, -1);
             mv.addObject("of", of);
         }else{
               mv = new JModelAndView("newwap/wodedingdandaipingjia.html",
                     this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
             OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
             mv.addObject("of", of);
         }
         String uploadFilePath = this.configService.getSysConfig()
                 .getUploadFilePath();
         String saveFilePathName = request.getSession().getServletContext()
                 .getRealPath("/") +
                 uploadFilePath + File.separator + "evaluate";
         OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
         Goods goods=this.goodsService.getObjById(CommUtil.null2Long(goods_id));
         GoodsCart goodsCart=this.goodsCartService.getObjById(CommUtil.null2Long(goodsCart_id));
         goodsCart.setEvaluate_status("1");
         this.goodsCartService.update(goodsCart);
         Evaluate eva = new Evaluate();
         eva.setAddTime(new Date());
         eva.setEvaluate_goods(goods);
         eva.setEvaluate_info(request.getParameter("evaluate_info"));
         eva.setDescription_evaluate(BigDecimal.valueOf(
                 CommUtil.null2Double(request.getParameter("description_evaluate"))));
         eva.setEvaluate_type("goods");
         eva.setEvaluate_user(SecurityUserHolder.getCurrentUser());
         eva.setOf(obj);
         eva.setGoods_spec(goodsCart.getSpec_info());
         Map map = new HashMap();
         map = CommUtil.saveFileToServer(request, "brandLogo",
                 saveFilePathName, null, null);
         if (map.get("fileName") != "") {
             eva.setPic_status("0");
             eva.setPic_url(uploadFilePath+"/evaluate/"+map.get("fileName"));
         }else {
             eva.setPic_status("1");
         }
         this.evaluateService.save(eva);
         return mv;
     }
   @RequestMapping({"/goods_detail.htm"})
   public ModelAndView goods_detail(HttpServletRequest request, HttpServletResponse response, String id, String goods_id) {
    String template = "default";
     Store store = this.storeService.getObjById(CommUtil.null2Long(id));
    if (store != null) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template+ "/goods_detail.html",
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     Goods goods = this.goodsService
       .getObjById(CommUtil.null2Long(goods_id));
     mv.addObject("obj", goods);
   /*  generic_evaluate(goods.getGoods_store(), mv);
     this.userTools.query_user();*/
     return mv;
   }
 
   @RequestMapping({"/goods_order.htm"})
   public ModelAndView goods_order(HttpServletRequest request, HttpServletResponse response, String id, String goods_id, String currentPage)
   {
     String template = "default";
     Store store = this.storeService.getObjById(CommUtil.null2Long(id));
     if (store != null) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/goods_order.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     GoodsCartQueryObject qo = new GoodsCartQueryObject(currentPage, mv, 
       "addTime", "desc");
     qo.addQuery("obj.goods.id", 
       new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
     qo.addQuery("obj.of.order_status", new SysMap("order_status", Integer.valueOf(20)), ">=");
     qo.setPageSize(Integer.valueOf(8));
     IPageList pList = this.goodsCartService.list(qo);
     CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
       "/goods_order.htm", "", "", pList, mv);
     mv.addObject("storeViewTools", this.storeViewTools);
     return mv;
   }
 
   @RequestMapping({"/goods_consult.htm"})
   public ModelAndView goods_consult(HttpServletRequest request, HttpServletResponse response, String id, String goods_id, String currentPage)
   {
     String template = "default";
     Store store = this.storeService.getObjById(CommUtil.null2Long(id));
     if (store != null) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/goods_consult.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv, 
       "addTime", "desc");
     qo.addQuery("obj.goods.id", 
       new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
     IPageList pList = this.consultService.list(qo);
     CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
       "/goods_consult.htm", "", "", pList, mv);
     mv.addObject("storeViewTools", this.storeViewTools);
     mv.addObject("goods_id", goods_id);
     return mv;
   }
 
   @RequestMapping({"/goods_consult_save.htm"})
   public ModelAndView goods_consult_save(HttpServletRequest request, HttpServletResponse response, String goods_id, String consult_content, String consult_email, String Anonymous, String consult_code)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/success.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String verify_code = CommUtil.null2String(request.getSession(false)
       .getAttribute("consult_code"));
     boolean visit_consult = true;
     if (!this.configService.getSysConfig().isVisitorConsult()) {
       if (SecurityUserHolder.getCurrentUser() == null) {
         visit_consult = false;
       }
       if (CommUtil.null2Boolean(Anonymous)) {
         visit_consult = false;
       }
     }
     if (visit_consult) {
       if (CommUtil.null2String(consult_code).equals(verify_code)) {
         Consult obj = new Consult();
         obj.setAddTime(new Date());
         obj.setConsult_content(consult_content);
         obj.setConsult_email(consult_email);
         if (!CommUtil.null2Boolean(Anonymous)) {
           obj.setConsult_user(SecurityUserHolder.getCurrentUser());
           mv.addObject("op_title", "咨询发布成功");
         }
         obj.setGoods(this.goodsService.getObjById(
           CommUtil.null2Long(goods_id)));
         this.consultService.save(obj);
         request.getSession(false).removeAttribute("consult_code");
       } else {
         mv = new JModelAndView("error.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "验证码错误，咨询发布失败");
       }
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "不允许游客咨询");
     }
     mv.addObject("url", CommUtil.getURL(request) + "/goods_" + goods_id + 
       ".htm");
     return mv;
   }
 
   /**
    * 根据商品规格查询商品规格价格
	 * @param request
	 * @param response
	 * @param gsp 规格
	 * @param id
	 */
	@RequestMapping({"/load_goods_gsp.htm"})
   public void load_goods_gsp(HttpServletRequest request, HttpServletResponse response, String gsp, String id) {
     Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
     Map map = new HashMap();
     int count = 0;
     double price = 0.0D;
     if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
       for (GroupGoods gg : goods.getGroup_goods_list())
         if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
           count = gg.getGg_group_count() - gg.getGg_def_count();
           price = CommUtil.null2Double(gg.getGg_price());
         }
     }
     else {
       count = goods.getGoods_inventory();
       price = CommUtil.null2Double(goods.getStore_price());
       if (goods.getInventory_type().equals("spec")) {
         List<Map> list = (List)Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
         String[] gsp_ids = gsp.split(",");
         for (Map temp : list) {
           String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
           Arrays.sort(gsp_ids);
           Arrays.sort(temp_ids);
           if (Arrays.equals(gsp_ids, temp_ids)) {
             count = CommUtil.null2Int(temp.get("count"));
             price = CommUtil.null2Double(temp.get("price"));
           }
         }
       }
     }
     map.put("count", Integer.valueOf(count));
     map.put("price", Double.valueOf(price));
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(Json.toJson(map, JsonFormat.compact()));
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @RequestMapping({"/trans_fee.htm"})
   public void trans_fee(HttpServletRequest request, HttpServletResponse response, String city_name, String goods_id) {
	   Map map = new HashMap();
       Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
       float mail_fee = 0.0F;
       float express_fee = 0.0F;
       float ems_fee = 0.0F;
       if (goods.getTransport() != null) {
           if(goods.getTransport().getTrans_mail_info()!=null) {
               mail_fee = this.transportTools.cal_goods_trans_fee(
                       CommUtil.null2String(goods.getTransport().getId()), "mail",
                       CommUtil.null2String(goods.getGoods_weight()),
                       CommUtil.null2String(goods.getGoods_volume()), city_name);
           }
           if(goods.getTransport().getTrans_express_info()!=null) {
               express_fee = this.transportTools.cal_goods_trans_fee(
                       CommUtil.null2String(goods.getTransport().getId()), "express",
                       CommUtil.null2String(goods.getGoods_weight()),
                       CommUtil.null2String(goods.getGoods_volume()), city_name);
           }
           if(goods.getTransport().getTrans_ems_info()!=null) {
               ems_fee = this.transportTools.cal_goods_trans_fee(
                       CommUtil.null2String(goods.getTransport().getId()), "ems",
                       CommUtil.null2String(goods.getGoods_weight()),
                       CommUtil.null2String(goods.getGoods_volume()), city_name);
           }
       }
       map.put("mail_fee", Float.valueOf(mail_fee));
       map.put("express_fee", Float.valueOf(express_fee));
       map.put("ems_fee", Float.valueOf(ems_fee));
       map.put("current_city_info", CommUtil.substring(city_name, 5));
       response.setContentType("text/plain");
       response.setHeader("Cache-Control", "no-cache");
       response.setCharacterEncoding("UTF-8");
       try
       {
           PrintWriter writer = response.getWriter();
           writer.print(Json.toJson(map, JsonFormat.compact()));
       }
       catch (IOException e) {
           e.printStackTrace();
       }
   }
 
   @RequestMapping({"/goods_share.htm"})
   public ModelAndView goods_share(HttpServletRequest request, HttpServletResponse response, String goods_id) {
     ModelAndView mv = new JModelAndView("goods_share.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
     mv.addObject("obj", goods);
     return mv;
   }
 
   private Set<Long> genericIds(GoodsClass gc) {
     Set ids = new HashSet();
     ids.add(gc.getId());
     for (GoodsClass child : gc.getChilds()) {
       Set<Long> cids = genericIds(child);
       for (Long cid : cids) {
         ids.add(cid);
       }
       ids.add(child.getId());
     }
     return ids;
   }
 
   private void generic_evaluate(Store store, ModelAndView mv) {
     double description_result = 0.0D;
     double service_result = 0.0D;
     double ship_result = 0.0D;
     if (store.getSc() != null) {
       StoreClass sc = this.storeClassService.getObjById(store.getSc()
         .getId());
       float description_evaluate = CommUtil.null2Float(sc
         .getDescription_evaluate());
       float service_evaluate = CommUtil.null2Float(sc
         .getService_evaluate());
       float ship_evaluate = CommUtil.null2Float(sc.getShip_evaluate());
       if (store.getPoint() != null) {
         float store_description_evaluate = CommUtil.null2Float(store
           .getPoint().getDescription_evaluate());
         float store_service_evaluate = CommUtil.null2Float(store
           .getPoint().getService_evaluate());
         float store_ship_evaluate = CommUtil.null2Float(store
           .getPoint().getShip_evaluate());
 
         description_result = CommUtil.div(Float.valueOf(store_description_evaluate - 
           description_evaluate), Float.valueOf(description_evaluate));
         service_result = CommUtil.div(Float.valueOf(store_service_evaluate - 
           service_evaluate), Float.valueOf(service_evaluate));
         ship_result = CommUtil.div(Float.valueOf(store_ship_evaluate - ship_evaluate), 
           Float.valueOf(ship_evaluate));
       }
     }
     if (description_result > 0.0D) {
       mv.addObject("description_css", "better");
       mv.addObject("description_type", "高于");
       mv.addObject("description_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(description_result), Integer.valueOf(100)))) + 
         "%");
     }
     if (description_result == 0.0D) {
       mv.addObject("description_css", "better");
       mv.addObject("description_type", "持平");
       mv.addObject("description_result", "-----");
     }
     if (description_result < 0.0D) {
       mv.addObject("description_css", "lower");
       mv.addObject("description_type", "低于");
       mv.addObject(
         "description_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(-description_result), Integer.valueOf(100)))) + 
         "%");
     }
     if (service_result > 0.0D) {
       mv.addObject("service_css", "better");
       mv.addObject("service_type", "高于");
       mv.addObject("service_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(service_result), Integer.valueOf(100)))) + 
         "%");
     }
     if (service_result == 0.0D) {
       mv.addObject("service_css", "better");
       mv.addObject("service_type", "持平");
       mv.addObject("service_result", "-----");
     }
     if (service_result < 0.0D) {
       mv.addObject("service_css", "lower");
       mv.addObject("service_type", "低于");
       mv.addObject("service_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(-service_result), Integer.valueOf(100)))) + 
         "%");
     }
     if (ship_result > 0.0D) {
       mv.addObject("ship_css", "better");
       mv.addObject("ship_type", "高于");
       mv.addObject("ship_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(ship_result), Integer.valueOf(100)))) + "%");
     }
     if (ship_result == 0.0D) {
       mv.addObject("ship_css", "better");
       mv.addObject("ship_type", "持平");
       mv.addObject("ship_result", "-----");
     }
     if (ship_result < 0.0D) {
       mv.addObject("ship_css", "lower");
       mv.addObject("ship_type", "低于");
       mv.addObject("ship_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(-ship_result), Integer.valueOf(100)))) + "%");
     }
   }

     private void add_store_common_info(ModelAndView mv, Store store) {
         Map params = new HashMap();
         params.put("user_id", store.getUser().getId());
         params.put("display", Boolean.valueOf(true));
         List ugcs = this.userGoodsClassService.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc", params, -1, -1);
         mv.addObject("ugcs", ugcs);
         params.clear();
         params.put("recommend", Boolean.valueOf(true));
         params.put("goods_store_id", store.getId());
         params.put("goods_status", Integer.valueOf(0));
         List goods_recommend = this.goodsService
                 .query("select obj from Goods obj where obj.goods_recommend=:recommend and obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.addTime desc",
                         params, 0, 8);
         params.clear();
         params.put("goods_store_id", store.getId());
         params.put("goods_status", Integer.valueOf(0));
         List goods_new = this.goodsService
                 .query("select obj from Goods obj where obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.addTime desc ",
                         params, 0, 0);
         mv.addObject("goods_recommend", goods_recommend);
         mv.addObject("goods_new", goods_new);
         mv.addObject("goodsViewTools", this.goodsViewTools);
         mv.addObject("storeViewTools", this.storeViewTools);
         mv.addObject("areaViewTools", this.areaViewTools);
     }

     @RequestMapping({"/store_classify.htm"})
     public ModelAndView store_classify(HttpServletRequest request, HttpServletResponse response,String id)
     {
         ModelAndView mv = new JModelAndView("newwap/43store_classify.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         Store store = this.storeService.getObjById(CommUtil.null2Long(id));

         mv.addObject("store", store);
         add_store_common_info(mv, store);
         //generic_evaluate(store, mv);
         Map params = new HashMap();
         params.put("store_id", store.getId());
         List partners = this.storepartnerService.query("select obj from StorePartner obj where obj.store.id=:store_id order by obj.sequence asc", params, -1, -1);
         mv.addObject("partners", partners);
         mv.addObject("goodsViewTools", this.goodsViewTools);
         return mv;
     }
     public List getGroups(){
         Map params = new HashMap();
         params.put("beginTime", new Date());
         params.put("endTime", new Date());
         List groups = this.groupService.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.status=0", params, -1, -1);
         return groups;
     }


     @RequestMapping({"/toLogin.htm"})
     public ModelAndView toLogin(HttpServletRequest request, HttpServletResponse response) {
         ModelAndView mv = new JModelAndView("newwap/Login.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         return mv;
     }


     @RequestMapping({"/getDiscountCoupon.htm"})
     public void getDiscountCoupon(HttpServletRequest request, HttpServletResponse response,String id) {
         User user=SecurityUserHolder.getCurrentUser();
         Goods obj = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
         if(user !=null){
             Map params = new HashMap();
             params.put("coupon_uid",CommUtil.null2Long(user.getId()));
             params.put("status",CommUtil.null2Int("0"));
             params.put("id",CommUtil.null2Long(obj.getGoods_store().getUser().getId()));//
             List coupon2=this.couponInfoService.query("select obj from CouponInfo obj where obj.user.id= :coupon_uid and obj.status = :status and obj.coupon.ccuser.id = :id order by obj.addTime desc",params,-1,-1);
             String ret = Json.toJson(coupon2, JsonFormat.compact());
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
    }


     /**
      * 页面初始化的参数
      * @param id
      * @return
      */
     public Map getMap(String id){
         Map result = new HashMap();
         Map params1 = new HashMap();
         params1.put("goods_store_id",CommUtil.null2Long(id));
         params1.put("deleteStatus",CommUtil.null2Boolean(false));
         List goodsStoreAll = goodsService.query("select obj from Goods obj where obj.deleteStatus=:deleteStatus and obj.goods_store.id =:goods_store_id",params1,-1,-1);

         Map params2 = new HashMap();
         params2.put("goods_store_id",CommUtil.null2Long(id));
         params2.put("goods_type","shangxin");
         params2.put("deleteStatus",CommUtil.null2Boolean(false));
         List goodsStoreSX = goodsService.query("select obj from Goods obj where obj.deleteStatus=:deleteStatus and obj.goods_store.id =:goods_store_id and obj.goods_type =:goods_type",params2,-1,-1);

         Map params3 = new HashMap();
         params3.put("goods_store_id",CommUtil.null2Long(id));
         params3.put("goods_type","cuxiao");
         params3.put("deleteStatus",CommUtil.null2Boolean(false));
         List goodsStoreCX = goodsService.query("select obj from Goods obj where obj.deleteStatus=:deleteStatus and obj.goods_store.id =:goods_store_id and obj.goods_type =:goods_type",params3,-1,-1);

         Map params4 = new HashMap();
         params4.put("goods_store_id",CommUtil.null2Long(id));
         params4.put("goods_selfSay","zishu");
         params4.put("selfSay_status",Integer.valueOf("1"));
         List goodsStoreDPDT = goodsService.query("select obj from Goods obj where obj.goods_store.id =:goods_store_id and obj.goods_selfSay =:goods_selfSay and obj.selfSay_status =:selfSay_status",params4,-1,-1);

         result.put("goodsStoreAll",goodsStoreAll.size());
         result.put("goodsStoreSX",goodsStoreSX.size());
         result.put("goodsStoreCX",goodsStoreCX.size());
         result.put("goodsStoreDPDT",goodsStoreDPDT.size());
         return result;
     }
     //wap退货
     @RequestMapping({"/seller/order_returnxiangqing.htm"})
     public ModelAndView order_returnxiangqing(HttpServletRequest request, HttpServletResponse response, String id) {
         ModelAndView mv = new JModelAndView(
                 "newwap/wodedingdan_yiwanc_tuihuo.html",
                 this.configService.getSysConfig(),
                 this.userConfigService.getUserConfig(),1, request, response);
         OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
         mv.addObject("obj", of);

         return mv;
     }
     @RequestMapping({"/ordertuihuo.htm"})
     public ModelAndView ordertuihuo(HttpServletRequest request, HttpServletResponse response,String id) {
         ModelAndView mv =  new JModelAndView("newwap/wodedingdan_yiwancheng_shengqingtuihuo.html",
                 this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
         mv.addObject("obj", obj);
         return mv;
     }
 }


 
 
 