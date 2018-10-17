 package com.shopping.view.web.action;
 
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.*;
 import com.shopping.foundation.domain.query.EvaluateQueryObject;
 import com.shopping.foundation.domain.query.GoodsBrandQueryObject;
 import com.shopping.foundation.domain.query.GoodsQueryObject;
 import com.shopping.foundation.domain.query.StoreQueryObject;
 import com.shopping.foundation.service.*;
 import com.shopping.view.web.tools.AreaViewTools;
 import com.shopping.view.web.tools.GoodsViewTools;
 import com.shopping.view.web.tools.StoreViewTools;

 import java.math.BigDecimal;
 import java.util.*;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 import org.springframework.web.servlet.View;

 @Controller
 public class StoreViewAction
 {
 
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
   private IUserGoodsClassService userGoodsClassService;
 
   @Autowired
   private IStoreNavigationService storenavigationService;
 
   @Autowired
   private IStorePartnerService storepartnerService;
 
   @Autowired
   private IEvaluateService evaluateService;
 
   @Autowired
   private AreaViewTools areaViewTools;
 
   @Autowired
   private GoodsViewTools goodsViewTools;

   @Autowired
   private ICouponService couponService;
 
   @Autowired
   private StoreViewTools storeViewTools;

   @Autowired
   private IOrderFormService orderFormService;

   @Autowired
   private IGoodsBrandService goodsBrandService;

   @Autowired
   private IUserService userService;

   @Autowired
   private IAccessoryService accessoryService;

   @Autowired
   private ICouponInfoService couponInfoService;

   @RequestMapping({"/store.htm"})
   public ModelAndView store(HttpServletRequest request, HttpServletResponse response, String id,String view,String userid){
    // String serverName = request.getServerName().toLowerCase();
     User user = SecurityUserHolder.getCurrentUser();

     Store store = null;
   /*
     if ((id == null) && (serverName.indexOf(".") >= 0) && (serverName.indexOf(".") != serverName.lastIndexOf(".")) && (this.configService.getSysConfig().isSecond_domain_open())) {
       String secondDomain = serverName.substring(0, serverName.indexOf("."));
       store = this.storeService.getObjByProperty("store_second_domain", secondDomain);
     } else {*/
       store = this.storeService.getObjById(CommUtil.null2Long(id));
   //  }

     if (store == null) {
       ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       mv.addObject("op_title", "不存在该店铺信息");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
       return mv;
     }

     String template = "default";
     //选择模板 暂时不用
    /* if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }*/
     ModelAndView mv =null;
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
     if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "pc" ))){
       mv = new JModelAndView( template+"/seller_store.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       if((null != view) && (!"".equals(view)) && ("wap".equals(view))){
         shopping_view_type="wap";
       }
     }
      if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {

       request.getSession().setAttribute("shopping_view_type","wap");
       if (null==store.getMould()) {
         mv =  new JModelAndView( "newwap/42buyer_store.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       }else if (store.getMould().equals("red")) {
         mv =  new JModelAndView( "newwap/42buyer_store1.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       }else if (store.getMould().equals("orange")) {
         mv =  new JModelAndView( "newwap/42buyer_store2.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       }else {
         mv =  new JModelAndView( "newwap/42buyer_store.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

       }
       /*if(user == null){
         mv = new JModelAndView("newwap/Login.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
         return mv;
       }*/
     }
    /* if(user == null){
       mv = new JModelAndView("Login_N.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       return mv;
     }*/
     Map params = new HashMap();
     //查询当前用户已经领取的优惠券
    /* List<Coupon> yiLing = new ArrayList<>();
     params.put("user_id",SecurityUserHolder.getCurrentUser().getId());
     params.put("status",CommUtil.null2Int("0"));
     List<CouponInfo> couponInfoList = couponInfoService.query("select obj from CouponInfo obj where obj.user.id =:user_id and obj.status =:status ",params,-1,-1);
     for (CouponInfo couponInfo:couponInfoList) {
       params.clear();
       params.put("couponId",couponInfo.getCoupon().getId());
       params.put("date",new Date());
       List<Coupon> coupon = this.couponService.query("select obj from Coupon obj where obj.id =:couponId and obj.coupon_begin_time <:date and obj.coupon_end_time >:date",params,-1,-1);
       if(coupon.size()>0){
         yiLing.add(coupon.get(0));
       }
     }*/


     if(null!=userid&&!"".equals(userid)){
       User user2=this.userService.getObjById(CommUtil.null2Long(userid));
       request.getSession().setAttribute("P_user",user2);
     }
     //查询店铺的优惠券
     params.clear();
     params.put("coupon_type",CommUtil.null2String(1));
     Long uid=store.getUser().getId();
     params.put("coupon_uid",CommUtil.null2Long(uid));
     List<Coupon> cou=this.couponService.query("select obj from Coupon obj where obj.ccuser.id =:coupon_uid and obj.coupon_type =:coupon_type  order by obj.addTime desc",params,-1,-1);
     //cou.removeAll(yiLing);
     if(null!=cou){
       mv.addObject("cou",cou);
     }

     if (store.getStore_status() == 2) {
       add_store_common_info(mv, store);
       mv.addObject("store", store);
       mv.addObject("nav_id", "store_index");
     } else {
       mv = new JModelAndView("error.html", 
         this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request,
         response);
       mv.addObject("op_title", "店铺已经关闭或者未开通店铺");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     generic_evaluate(store, mv);


     //初始化的参数
     Map params1 = new HashMap();
     params1.put("goods_store_id",CommUtil.null2Long(id));
    // params1.put("deleteStatus",CommUtil.null2Boolean(false)); /*/
    // List goodsStoreAll = goodsService.query("select obj from Goods obj where obj.deleteStatus:=deleteStatus and obj.goods_store.id =:goods_store_id",params1,-1,-1);
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

     mv.addObject("goodsStoreAll",goodsStoreAll.size());
     mv.addObject("goodsStoreSX",goodsStoreSX.size());
     mv.addObject("goodsStoreCX",goodsStoreCX.size());
     mv.addObject("goodsStoreDPDT",goodsStoreDPDT.size());
     mv.addObject("store",store);


     return mv;
   }

   @RequestMapping({"/seller_store_top.htm"})
   public ModelAndView seller_store_top(HttpServletRequest request, HttpServletResponse response,String id){
     String serverName = request.getServerName().toLowerCase();
     User user = SecurityUserHolder.getCurrentUser();
     System.out.println(id);
     String store_id=request.getParameter("store_id");
     if(null==store_id)store_id=id;
     Store store = null;
       store = this.storeService.getObjById(CommUtil.null2Long(store_id));
     String template = "default";
     //选择模板 暂时不用
    /* if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }*/
     ModelAndView mv = new JModelAndView( template+"/seller_store_top.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);


     /*if(user == null){
       mv = new JModelAndView("Login_N.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       return mv;
     }*/

       Map params = new HashMap();
       //查询当前用户已经领取的优惠券
      /* List<Coupon> yiLing = new ArrayList<>();
       params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
       params.put("status", CommUtil.null2Int("0"));
       List<CouponInfo> couponInfoList = couponInfoService.query("select obj from CouponInfo obj where obj.user.id =:user_id and obj.status =:status ", params, -1, -1);
       for (CouponInfo couponInfo : couponInfoList) {
         params.clear();
         params.put("couponId", couponInfo.getCoupon().getId());
         params.put("date", new Date());
         List<Coupon> coupon = this.couponService.query("select obj from Coupon obj where obj.id =:couponId and obj.coupon_begin_time <:date and obj.coupon_end_time >:date", params, -1, -1);
         if (coupon.size() > 0) {
           yiLing.add(coupon.get(0));
         }
       }*/
       //查询店铺的优惠券
       params.clear();
       params.put("coupon_type", CommUtil.null2String(1));
       Long uid = store.getUser().getId();
       params.put("coupon_uid", CommUtil.null2Long(uid));
       List<Coupon> cou = this.couponService.query("select obj from Coupon obj where obj.ccuser.id =:coupon_uid and obj.coupon_type =:coupon_type  order by obj.addTime desc", params, -1, -1);
       //cou.removeAll(yiLing);
       mv.addObject("cou", cou);

       if (store.getStore_status() == 2) {
         add_store_common_info(mv, store);
         mv.addObject("store", store);
         mv.addObject("nav_id", "store_index");
       } else {
         mv = new JModelAndView("error.html",
                 this.configService.getSysConfig(),
                 this.userConfigService.getUserConfig(), 1, request,
                 response);
         mv.addObject("op_title", "店铺已经关闭或者未开通店铺");
         mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
       }
       generic_evaluate(store, mv);


       //初始化的参数
       Map params1 = new HashMap();
       params1.put("goods_store_id", CommUtil.null2Long(id));
       List goodsStoreAll = goodsService.query("select obj from Goods obj where obj.goods_store.id =:goods_store_id", params1, -1, -1);

       Map params2 = new HashMap();
       params2.put("goods_store_id", CommUtil.null2Long(id));
       params2.put("goods_type", "shangxin");
       List goodsStoreSX = goodsService.query("select obj from Goods obj where obj.goods_store.id =:goods_store_id and obj.goods_type =:goods_type", params2, -1, -1);

       Map params3 = new HashMap();
       params3.put("goods_store_id", CommUtil.null2Long(id));
       params3.put("goods_type", "cuxiao");
       List goodsStoreCX = goodsService.query("select obj from Goods obj where obj.goods_store.id =:goods_store_id and obj.goods_type =:goods_type", params3, -1, -1);

       Map params4 = new HashMap();
       params4.put("goods_store_id", CommUtil.null2Long(id));
       params4.put("goods_selfSay", "zishu");
       params4.put("selfSay_status", Integer.valueOf("1"));
       List goodsStoreDPDT = goodsService.query("select obj from Goods obj where obj.goods_store.id =:goods_store_id and obj.goods_selfSay =:goods_selfSay and obj.selfSay_status =:selfSay_status", params4, -1, -1);

       mv.addObject("goodsStoreAll", goodsStoreAll.size());
       mv.addObject("goodsStoreSX", goodsStoreSX.size());
       mv.addObject("goodsStoreCX", goodsStoreCX.size());
       mv.addObject("goodsStoreDPDT", goodsStoreDPDT.size());

     return mv;
   }




   @RequestMapping({"/store_wap.htm"})
   public ModelAndView store_wap(HttpServletRequest request, HttpServletResponse response, String id)

   {

     String serverName = request.getServerName().toLowerCase();
     Store store = null;

     if ((id == null) && (serverName.indexOf(".") >= 0) &&
             (serverName.indexOf(".") != serverName.lastIndexOf(".")) &&
             (this.configService.getSysConfig().isSecond_domain_open())) {
       String secondDomain = serverName.substring(0,
               serverName.indexOf("."));
       store = this.storeService.getObjByProperty("store_second_domain",
               secondDomain);
     } else {
       store = this.storeService.getObjById(CommUtil.null2Long(id));
     }
     ModelAndView   mv = new JModelAndView("newwap/42buyer_store.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     generic_evaluate(store, mv);
     return mv;
   }
   @RequestMapping({"/store_info2.htm"})
   public ModelAndView store_info2(HttpServletRequest request, HttpServletResponse response, String id) {

     Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
     ModelAndView mv = new JModelAndView("newwap/42buyer_store.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     Map params=new HashMap();
     params.put("goods_store_id",store.getId());
     List goods=this.goodsService.query("select obj from Goods obj where obj.goods_store.id=:goods_store_id ",params,0,4);
     List goods2=this.goodsService.query("select obj from Goods obj where obj.goods_store.id=:goods_store_id ",params,9,3);
     mv.addObject("good",goods);
     mv.addObject("goods2",goods2);
     if (store.getStore_status() == 2) {
       //初始化的参数
       Map params1 = new HashMap();
       params1.put("goods_store_id",CommUtil.null2Long(id));
       params1.put("deleteStatus",CommUtil.null2Boolean(false));
       List goodsStoreAll = goodsService.query("select obj from Goods obj where obj.deleteStatus =:deleteStatus and obj.goods_store.id =:goods_store_id",params1,-1,-1);

       Map params2 = new HashMap();
       params2.put("goods_store_id",CommUtil.null2Long(id));
       params2.put("goods_type","shangxin");
       List goodsStoreSX = goodsService.query("select obj from Goods obj where obj.goods_store.id =:goods_store_id and obj.goods_type =:goods_type",params2,-1,-1);

       Map params3 = new HashMap();
       params3.put("goods_store_id",CommUtil.null2Long(id));
       params3.put("goods_type","cuxiao");
       List goodsStoreCX = goodsService.query("select obj from Goods obj where obj.goods_store.id =:goods_store_id and obj.goods_type =:goods_type",params3,-1,-1);

       Map params4 = new HashMap();
       params4.put("goods_store_id",CommUtil.null2Long(id));
       params4.put("goods_selfSay","zishu");
       params4.put("selfSay_status",Integer.valueOf("1"));
       List goodsStoreDPDT = goodsService.query("select obj from Goods obj where obj.goods_store.id =:goods_store_id and obj.goods_selfSay =:goods_selfSay and obj.selfSay_status =:selfSay_status",params4,-1,-1);

       mv.addObject("goodsStoreAll",goodsStoreAll.size());
       mv.addObject("goodsStoreSX",goodsStoreSX.size());
       mv.addObject("goodsStoreCX",goodsStoreCX.size());
       mv.addObject("goodsStoreDPDT",goodsStoreDPDT.size());

       mv.addObject("store", store);
       mv.addObject("nav_id", "store_info");
       mv.addObject("areaViewTools", this.areaViewTools);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(),
               this.userConfigService.getUserConfig(), 1, request,
               response);
       mv.addObject("op_title", "店铺信息错误");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }

     return mv;
   }





   /*@RequestMapping({"/store_goods.htm"})
   public ModelAndView store_index(HttpServletRequest request, HttpServletResponse response, String id)
   {

     String template = "default";

     ModelAndView mv = new JModelAndView(template + "/30seller_index.html",
             this.configService.getSysConfig(),
             this.userConfigService.getUserConfig(), 1, request,
             response);

     return mv;
   }*/


   @RequestMapping({"/store_left.htm"})
   public ModelAndView store_left(HttpServletRequest request, HttpServletResponse response)
   {
     Store store = this.storeService.getObjById(CommUtil.null2Long(request
       .getAttribute("id")));
     String template = "default";
     /*if ((store != null) && (store.getTemplate() != null) &&
       (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }*/
     ModelAndView mv = new JModelAndView(template + "/store_left.html",
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     mv.addObject("store", store);
     add_store_common_info(mv, store);
     generic_evaluate(store, mv);
     Map params = new HashMap();
     params.put("store_id", store.getId());
     List partners = this.storepartnerService
       .query("select obj from StorePartner obj where obj.store.id=:store_id order by obj.sequence asc", 
       params, -1, -1);
     mv.addObject("partners", partners);
     mv.addObject("goodsViewTools", this.goodsViewTools);
     return mv;
   }

   @RequestMapping({"/store_left1.htm"})
   public ModelAndView store_left1(HttpServletRequest request, HttpServletResponse response) {
     Store store = this.storeService.getObjById(CommUtil.null2Long(request.getAttribute("id")));
     String template = "default";
    /* if ((store != null) && (store.getTemplate() != null) &&
       (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }*/
     ModelAndView mv = new JModelAndView(template + "/store_left1.html", this.configService.getSysConfig(),
       this.userConfigService.getUserConfig(), 1, request, response);
     mv.addObject("store", store);
     add_store_common_info(mv, store);
     Map params = new HashMap();
     params.put("store_id", store.getId());
     List partners = this.storepartnerService
       .query("select obj from StorePartner obj where obj.store.id=:store_id order by obj.sequence asc", 
       params, -1, -1);
     mv.addObject("partners", partners);
     return mv;
   }
 
   @RequestMapping({"/store_left2.htm"})
   public ModelAndView store_left2(HttpServletRequest request, HttpServletResponse response) {
     Store store = this.storeService.getObjById(CommUtil.null2Long(request
       .getAttribute("id")));
     String template = "default";
     if ((store != null) && (store.getTemplate() != null) && 
       (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_left2.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     mv.addObject("store", store);
     add_store_common_info(mv, store);
     return mv;
   }
 
   @RequestMapping({"/store_nav.htm"})
   public ModelAndView store_nav(HttpServletRequest request, HttpServletResponse response) {
     Long id = CommUtil.null2Long(request.getAttribute("id"));
     Store store = this.storeService.getObjById(id);
     String template = "default";
     if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_nav.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     if (store.getStore_status() == 2) {
       Map params = new HashMap();
       params.put("store_id", store.getId());
       params.put("display", Boolean.valueOf(true));
       List navs = this.storenavigationService
         .query("select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display=:display order by obj.sequence asc", 
         params, -1, -1);
       mv.addObject("navs", navs);
       mv.addObject("store", store);
       String goods_view = CommUtil.null2String(request
         .getAttribute("goods_view"));
       mv.addObject("goods_view", Boolean.valueOf(CommUtil.null2Boolean(goods_view)));
       mv.addObject("goods_id", 
         CommUtil.null2String(request.getAttribute("goods_id")));
       mv.addObject("goods_list", 
         Boolean.valueOf(CommUtil.null2Boolean(request.getAttribute("goods_list"))));
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "店铺信息错误");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
 
   @RequestMapping({"/store_credit.htm"})
   public ModelAndView store_credit(HttpServletRequest request, HttpServletResponse response, String id) {
     Store store = this.storeService.getObjById(CommUtil.null2Long(id));
     String template = "default";
     if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_credit.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     if (store.getStore_status() == 2) {
       EvaluateQueryObject qo = new EvaluateQueryObject("1", mv, 
         "addTime", "desc");
       qo.addQuery("obj.of.store.id", 
         new SysMap("store_id", store.getId()), "=");
       IPageList pList = this.evaluateService.list(qo);
       CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
         "/store_eva.htm", "", "", pList, mv);
       mv.addObject("store", store);
       mv.addObject("nav_id", "store_credit");
       mv.addObject("storeViewTools", this.storeViewTools);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "店铺信息错误");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
 
   @RequestMapping({"/store_eva.htm"})
   public ModelAndView store_eva(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String eva_val)
   {
     Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
     String template = "default";
     if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_eva.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     if (store.getStore_status() == 2) {
       EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, 
         "addTime", "desc");
       qo.addQuery("obj.evaluate_goods.goods_store.id", 
         new SysMap("store_id", store.getId()), "=");
       if (!CommUtil.null2String(eva_val).equals("")) {
         qo.addQuery("obj.evaluate_buyer_val", 
           new SysMap("evaluate_buyer_val", Integer.valueOf(CommUtil.null2Int(eva_val))), "=");
       }
       IPageList pList = this.evaluateService.list(qo);
       CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
         "/store_eva.htm", "", 
         "&eva_val=" + CommUtil.null2String(eva_val), pList, mv);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "店铺信息错误");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
 
   @RequestMapping({"/store_info.htm"})
   public ModelAndView store_info(HttpServletRequest request, HttpServletResponse response, String id,String currentPage,String orderBy,String orderType) {

     Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
     String template = "default";
     /*if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }*/
     ModelAndView mv = new JModelAndView(template + "/32store_introduce.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     GoodsBrandQueryObject qo = new GoodsBrandQueryObject(currentPage, mv, orderBy, orderType);
     qo.addQuery("obj.user.store.id", new SysMap("id", CommUtil.null2Long(id)), "=");
     qo.setOrderBy("addTime");
     qo.setOrderType("desc");
     IPageList pList = this.goodsBrandService.list(qo);

     CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) +
             "/store_info.htm", "", "", pList, mv);
     Map param =new HashMap();
     param.put("id",CommUtil.null2Long(id));
     param.put("recommend",Boolean.valueOf(true));

   List goods=  this.goodsService.query("select obj  from Goods obj where obj.goods_store.id=:id and goods_recommend=:recommend",param,0,3);
     if (store.getStore_status() == 2) {
       mv.addObject("store", store);
       mv.addObject("nav_id", "store_info");
       mv.addObject("goods",goods);
       mv.addObject("areaViewTools", this.areaViewTools);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "店铺信息错误");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
   @RequestMapping({"/store_info3.htm"})
   public ModelAndView store_info3(HttpServletRequest request, HttpServletResponse response, String id) {

     Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
    /*  String template = "default";
    if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }*/
     ModelAndView mv = new JModelAndView( "newwap/47store_introduce.html",
             this.configService.getSysConfig(),
             this.userConfigService.getUserConfig(), 1, request, response);

     Map params4 = new HashMap();
     params4.put("store_id",CommUtil.null2Long(id));
     List<User> user = this.userService.query("select obj from User obj where obj.store.id =:store_id",params4,-1,-1);

     if(null != user){
       List<Accessory> li = new ArrayList<>();
       Map params5 = new HashMap();
       params5.put("user_id",CommUtil.null2Long(user.get(0).getId()));
       List<GoodsBrand> goodsBrand = goodsBrandService.query("select obj from GoodsBrand obj where obj.user.id =:user_id and deleteStatus = 0 and audit = 1",params5,-1,-1);
       for (GoodsBrand brand:goodsBrand) {
         Accessory accessory = accessoryService.getObjById(brand.getId());
         li.add(accessory);
       }
       mv.addObject("goodsBrand", li);
     }
     Map params1 = new HashMap();
     params1.put("goods_store_id",CommUtil.null2Long(id));
     List goodsStoreAll = goodsService.query("select obj from Goods obj where obj.goods_store.id =:goods_store_id",params1,-1,-1);
     Map params2 = new HashMap();
     params2.put("store_id",CommUtil.null2Long(id));
     List<OrderForm> order = orderFormService.query("select obj from OrderForm obj where obj.store.id =:store_id",params2,-1,-1);
     Integer bi = 0;
     Integer sum = 0;
     for (OrderForm or:order) {
       Map params3 = new HashMap();
       params3.put("of_id",CommUtil.null2Long(or.getId()));
       List<Evaluate> evaluate = evaluateService.query("select obj from Evaluate obj where obj.of.id =:of_id",params3,-1,-1);
        if (evaluate!=null&&evaluate.size()>0) {
          for (int i = 0; i <evaluate.size() ; i++) {
            if (evaluate.get(i).getDescription_evaluate()!=null){
              bi = bi+evaluate.get(i).getDescription_evaluate().intValue();
              sum = sum+1;
            }
          }
        }
     }
     Double grade = 0.00;
     if (sum>0) {
      grade=(double)(bi*100/sum);
      grade=grade/100;
     }
     if (store.getStore_status() == 2) {
       mv.addObject("goodsStoreAll", goodsStoreAll.size());
       mv.addObject("grade",grade.doubleValue());
       mv.addObject("store", store);
       mv.addObject("nav_id", "store_info");
       mv.addObject("areaViewTools", this.areaViewTools);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(),
               this.userConfigService.getUserConfig(), 1, request,
               response);
       mv.addObject("op_title", "店铺信息错误");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }


   @RequestMapping({"/store_dynamic.htm"})
   public ModelAndView store_dynamic(HttpServletRequest request, HttpServletResponse response, String id,String currentPage) {
     Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
     /*Map map=new HashMap();
     map.put("id",CommUtil.null2Long(id));
     List goods= this.goodsService.query("select obj from Goods obj where obj.goods_store.id=:id",map,-1,-1);*/
     String template = "default";
     /*if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }*/
     ModelAndView mv = new JModelAndView(template + "/31store_dynamic.html",
             this.configService.getSysConfig(),
             this.userConfigService.getUserConfig(), 1, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
     if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
       mv =  new JModelAndView( template + "/46store_dynamic.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     }
     EvaluateQueryObject qo4 = new EvaluateQueryObject(currentPage, mv, "addTime", "desc");
     qo4.addQuery("obj.goods_store.id", new SysMap("goods_store_id", CommUtil.null2Long(id)), "=");
     qo4.addQuery("obj.goods_selfSay", new SysMap("goods_selfSay", "zishu"), "=");
     qo4.addQuery("obj.selfSay_status", new SysMap("selfSay_status", Integer.valueOf("1")), "=");
     qo4.addQuery("obj.deleteStatus", new SysMap("deleteStatus",CommUtil.null2Boolean(false)), "=");
     IPageList pList = this.goodsService.list(qo4);
     String params = "&id="+id;
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     CommUtil.saveIPageList2ModelAndView(url + "/store_dynamic.htm", "", params, pList, mv);
     if (store.getStore_status() == 2) {
       /*mv.addObject("goods",goods);*/
       mv.addObject("store", store);
       mv.addObject("nav_id", "store_info");
       mv.addObject("user",SecurityUserHolder.getCurrentUser());
       mv.addObject("areaViewTools", this.areaViewTools);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(),
               this.userConfigService.getUserConfig(), 1, request,
               response);
       mv.addObject("op_title", "店铺信息错误");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }

   @RequestMapping({"/store_url.htm"})
   public ModelAndView store_url(HttpServletRequest request, HttpServletResponse response, String id) {
     StoreNavigation nav = this.storenavigationService.getObjById(
       CommUtil.null2Long(id));
     String template = "default";
     if ((nav.getStore().getTemplate() != null) && 
       (!nav.getStore().getTemplate().equals(""))) {
       template = nav.getStore().getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_url.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     mv.addObject("store", nav.getStore());
     mv.addObject("nav", nav);
     mv.addObject("nav_id", nav.getId());
     return mv;
   }
 
   private void add_store_common_info(ModelAndView mv, Store store) {
     Map params = new HashMap();
     params.put("user_id", store.getUser().getId());
     params.put("display", Boolean.valueOf(true));
     List ugcs = this.userGoodsClassService
       .query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc", 
       params, -1, -1);
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
       params, 0, 12);
     mv.addObject("goods_recommend", goods_recommend);
     mv.addObject("goods_new", goods_new);
     mv.addObject("goodsViewTools", this.goodsViewTools);
     mv.addObject("storeViewTools", this.storeViewTools);
     mv.addObject("areaViewTools", this.areaViewTools);
   }
 
   @RequestMapping({"/store_list.htm"})
   public ModelAndView store_list(HttpServletRequest request, HttpServletResponse response, String id, String sc_id, String currentPage, String orderType, String store_name, String store_ower, String type)
   {
     ModelAndView mv = new JModelAndView("store_list.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     List scs = this.storeClassService
       .query("select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc", 
       null, -1, -1);
     mv.addObject("scs", scs);
     StoreQueryObject sqo = new StoreQueryObject(currentPage, mv, 
       "store_credit", orderType);
     if ((sc_id != null) && (!sc_id.equals(""))) {
       sqo.addQuery("obj.sc.id", 
         new SysMap("sc_id", CommUtil.null2Long(sc_id)), "=");
     }
     if ((store_name != null) && (!store_name.equals(""))) {
       sqo.addQuery("obj.store_name", 
         new SysMap("store_name", "%" + 
         store_name + "%"), "like");
       mv.addObject("store_name", store_name);
     }
     if ((store_ower != null) && (!store_ower.equals(""))) {
       sqo.addQuery("obj.store_ower", 
         new SysMap("store_ower", "%" + 
         store_ower + "%"), "like");
       mv.addObject("store_ower", store_ower);
     }
     sqo.addQuery("obj.store_status", new SysMap("store_status", Integer.valueOf(2)), "=");
     IPageList pList = this.storeService.list(sqo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("storeViewTools", this.storeViewTools);
     mv.addObject("type", type);
     return mv;
   }
 
   @RequestMapping({"/store_goods_search.htm"})
   public ModelAndView store_goods_search(HttpServletRequest request, HttpServletResponse response, String keyword, String store_id,String store_price_begin,String store_price_end, String currentPage)
   {
     Store store = this.storeService.getObjById(Long.valueOf(CommUtil.null2Long(store_id)));
     String template = "default";
    /* if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }*/
     ModelAndView mv = new JModelAndView(template + "/30seller_index.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
     if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
       mv =  new JModelAndView( "newwap/44allgoods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     }
     GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, null, null);
     gqo.addQuery("obj.goods_store.id", new SysMap("store_id", CommUtil.null2Long(store_id)), "=");
     if (keyword!=null&&!"".equals(keyword)) {
       gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + keyword + "%"), "like");
     }
     gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
     if (store_price_begin!=null&&!"".equals(store_price_begin)&&store_price_end==null&&"".equals(store_price_end)){
      gqo.addQuery("obj.store_price",new SysMap("store_price",BigDecimal.valueOf(CommUtil.null2Double(store_price_begin))),">=");
     }
     if (store_price_end!=null&&!"".equals(store_price_end)&&store_price_begin==null&&"".equals(store_price_begin)){
       gqo.addQuery("obj.store_price",new SysMap("store_price", BigDecimal.valueOf(CommUtil.null2Double(store_price_end))),"<=");
     }
     if(store_price_begin!=null&& !"".equals(store_price_begin)&&store_price_end!=null&& !"".equals(store_price_end)){
       Map map=new HashMap();
       map.put("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin)));
       map.put("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end)));
       gqo.addQuery("obj.store_price>=:store_price_begin and obj.store_price<=:store_price_end",map);


     }
     gqo.setPageSize(Integer.valueOf(20));
     IPageList pList = this.goodsService.list(gqo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("keyword", keyword);
     mv.addObject("store", store);
     mv.addObject("store_price_begin",store_price_begin);
     mv.addObject("store_price_end",store_price_end);
     mv.addObject("goodsStoreAll", this.getMap(store_id).get("goodsStoreAll"));
     mv.addObject("goodsStoreSX", this.getMap(store_id).get("goodsStoreSX"));
     mv.addObject("goodsStoreCX", this.getMap(store_id).get("goodsStoreCX"));
     mv.addObject("goodsStoreDPDT", this.getMap(store_id).get("goodsStoreDPDT"));


     return mv;
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
     List goodsStoreAll = goodsService.query("select obj from Goods obj where obj.goods_store.id =:goods_store_id",params1,-1,-1);

     Map params2 = new HashMap();
     params2.put("goods_store_id",CommUtil.null2Long(id));
     params2.put("goods_type","shangxin");
     List goodsStoreSX = goodsService.query("select obj from Goods obj where obj.goods_store.id =:goods_store_id and obj.goods_type =:goods_type",params2,-1,-1);

     Map params3 = new HashMap();
     params3.put("goods_store_id",CommUtil.null2Long(id));
     params3.put("goods_type","cuxiao");
     List goodsStoreCX = goodsService.query("select obj from Goods obj where obj.goods_store.id =:goods_store_id and obj.goods_type =:goods_type",params3,-1,-1);

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
 
   @RequestMapping({"/store_head.htm"})
   public ModelAndView store_head(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView("store_head.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     Store store = this.storeService.getObjById(CommUtil.null2Long(request
       .getAttribute("store_id")));
     generic_evaluate(store, mv);
     mv.addObject("store", store);
     mv.addObject("storeViewTools", this.storeViewTools);
     return mv;
   }
 
   private void generic_evaluate(Store store, ModelAndView mv) {
     double description_result = 0.0D;
     double service_result = 0.0D;
     double ship_result = 0.0D;
     if ((store != null) && (store.getSc() != null) && (store.getPoint() != null)) {
       StoreClass sc = this.storeClassService.getObjById(store.getSc()
         .getId());
       float description_evaluate = CommUtil.null2Float(sc
         .getDescription_evaluate());
       float service_evaluate = CommUtil.null2Float(sc
         .getService_evaluate());
       float ship_evaluate = CommUtil.null2Float(sc.getShip_evaluate());
       float store_description_evaluate = CommUtil.null2Float(store
         .getPoint().getDescription_evaluate());
       float store_service_evaluate = CommUtil.null2Float(store.getPoint()
         .getService_evaluate());
       float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
         .getShip_evaluate());
 
       description_result = CommUtil.div(Float.valueOf(store_description_evaluate - 
         description_evaluate), Float.valueOf(description_evaluate));
       service_result = CommUtil.div(Float.valueOf(store_service_evaluate - 
         service_evaluate), Float.valueOf(service_evaluate));
       ship_result = CommUtil.div(Float.valueOf(store_ship_evaluate - ship_evaluate), 
         Float.valueOf(ship_evaluate));
     }
     if (description_result > 0.0D) {
       mv.addObject("description_css", "better");
       mv.addObject("description_type", "高于");
       mv.addObject(
         "description_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(description_result), Integer.valueOf(100)) > 100.0D ? 100.0D : 
         CommUtil.mul(Double.valueOf(description_result), Integer.valueOf(100)))) + 
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
 }


 
 
 