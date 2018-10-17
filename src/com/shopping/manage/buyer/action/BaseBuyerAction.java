 package com.shopping.manage.buyer.action;

 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.*;
 import com.shopping.foundation.domain.query.BargainGoodsQueryObject;
 import com.shopping.foundation.domain.query.DynamicQueryObject;
 import com.shopping.foundation.domain.query.FavoriteQueryObject;
 import com.shopping.foundation.domain.query.OrderFormQueryObject;
 import com.shopping.foundation.service.*;
 import com.shopping.view.web.tools.OrderViewTools;
 import com.shopping.view.web.tools.StoreViewTools;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.core.annotation.Order;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;

 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.*;
 
 @Controller
 public class BaseBuyerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IMessageService messageService;
 
   @Autowired
   private StoreViewTools storeViewTools;
 
   @Autowired
   private OrderViewTools orderViewTools;
 
   @Autowired
   private IDynamicService dynamicService;
 
   @Autowired
   private ISnsFriendService snsFriendService;
 
   @Autowired
   private IFavoriteService favService;
 
   @Autowired
   private IStoreService storeService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private ISnsAttentionService SnsAttentionService;
 
   @Autowired
   private IHomePageGoodsClassService HomeGoodsClassService;
   @Autowired
   private IOrderFormService orderFormService;
   @Autowired
   private IFavoriteService favoriteService;

   @Autowired
   private IIntegralLogService integralLogService;

   @Autowired
   private IGoodsCartService goodsCartService;

   @Autowired
   private IBargainGoodsService bargainGoodsService;

 
   @SecurityMapping(display = false, rsequence = 0, title="买家中心", value="/buyer/index.htm*", rtype="buyer", rname="买家中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/index.htm"})
   public ModelAndView  index (HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String type,String order_status) throws ParseException {
     ModelAndView mv = new JModelAndView("user/second/41Buyer_N.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
	 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
		 mv = new JModelAndView("newwap/maijiazhongxin,woshifenxiaoshang.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
	 }

     List msgs = new ArrayList();
     if (SecurityUserHolder.getCurrentUser() != null) {
       Map params = new HashMap();
       params.put("status", Integer.valueOf(0));
       params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
       msgs = this.messageService.query("select obj from Message obj where obj.status=:status and obj.toUser.id=:user_id and obj.parent.id is null", params, -1, -1);
     }
     mv.addObject("msgs", msgs);
     mv.addObject("storeViewTools", this.storeViewTools);
     mv.addObject("orderViewTools", this.orderViewTools);

     DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv, orderBy, orderType);
     qo.addQuery("obj.display", new SysMap("display", Boolean.valueOf(true)), "=");
     if ((type == null) || (type.equals(""))) {
       type = "2";
     }
     if (type.equals("1")) {
       qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder.getCurrentUser().getId()), "=");
     }
     if (type.equals("2")) {
       Map map = new HashMap();
       map.put("f_uid", SecurityUserHolder.getCurrentUser().getId());
       List myFriends = this.snsFriendService.query("select obj from SnsFriend obj where obj.fromUser.id=:f_uid", map, -1, -1);
       Set ids = getSnsFriendToUserIds(myFriends);

       Map paras = new HashMap();
       paras.put("ids", null);
       if (myFriends.size() > 0) {
         paras.put("ids", ids);
       }
       qo.addQuery("obj.user.id in (:ids)", paras);
     }
     if (type.equals("3")) {
       Map params = new HashMap();
       params.put("uid", SecurityUserHolder.getCurrentUser().getId());
       List SnsAttentions = this.SnsAttentionService.query("select obj from SnsAttention obj where obj.fromUser.id=:uid ", params, -1, -1);
       Set ids = getSnsAttentionToUserIds(SnsAttentions);
       params.clear();
       params.put("ids", ids);
       if ((ids != null) && (ids.size() > 0)) {
         qo.addQuery("obj.user.id in (:ids)", params);
       }
     }
     if (type.equals("4")) {
       qo.addQuery("obj.user.id",
         new SysMap("uid",
         SecurityUserHolder.getCurrentUser().getId()), "=");
       qo.addQuery("obj.store.id is not null", null);
     }
     qo.addQuery("obj.locked", new SysMap("locked", Boolean.valueOf(false)), "=");
     qo.addQuery("obj.dissParent.id is null", null);
     qo.setOrderBy("addTime");
     qo.setOrderType("desc");
     qo.setPageSize(Integer.valueOf(10));
     IPageList  pList = this.dynamicService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     List list = new ArrayList();
     for (int i = 1; i <= 120; i++) {
       list.add(Integer.valueOf(i));
     }
     mv.addObject("order_status", order_status);
     mv.addObject("type", type);
     mv.addObject("emoticons", list);


     OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
     ofqo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
     ofqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus",CommUtil.null2Boolean(false)), "=");
     if (!CommUtil.null2String(order_status).equals("")) {
       if (order_status.equals("order_submit")) {
         ofqo.addQuery("obj.order_status",
                 new SysMap("order_status", Integer.valueOf(10)), "=");
       }
       if (order_status.equals("order_pay")) {
         ofqo.addQuery("obj.order_status",
                 new SysMap("order_status", Integer.valueOf(20)), "=");
       }
       if (order_status.equals("order_shipping")) {
         ofqo.addQuery("obj.order_status", new SysMap("order_status", Integer.valueOf(30)), "=");
       }
       if (order_status.equals("order_receive")) {
         ofqo.addQuery("obj.order_status",
                 new SysMap("order_status", Integer.valueOf(40)), "=");
       }
       if (order_status.equals("order_finish")) {
         ofqo.addQuery("obj.order_status",
                 new SysMap("order_status", Integer.valueOf(60)), "=");
       }
       if (order_status.equals("order_wancheng")) {
         ofqo.addQuery("obj.order_status",
                 new SysMap("order_status", Integer.valueOf(45)), "=");
       }
       if (order_status.equals("order_cancel")) {
         ofqo.addQuery("obj.order_status",
                 new SysMap("order_status", Integer.valueOf(0)), "=");
       }
       if (order_status.equals("order_wancheng")) {
         ofqo.addQuery("obj.order_status",
                 new SysMap("order_status", Integer.valueOf(70)), "=");
       }
       if (order_status.equals("order_weipingjia")) {
         ofqo.addQuery("obj.order_status",
                 new SysMap("order_status", Integer.valueOf(51)), "=");
       }
       if (order_status.equals("order_tuihuo")) {
         ofqo.addQuery("obj.order_status",
                 new SysMap("order_status", Integer.valueOf(46)), "=");
       }
     }
     mv.addObject("order_status", order_status);
     pList = this.orderFormService.list(ofqo);

     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     CommUtil.saveIPageList2ModelAndView(url+"/buyer/index.htm", "", "", pList, mv);

     Map params=new HashMap();
     params.put("type",Integer.valueOf(0));
     params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
     List fav = this.favoriteService.query("select obj from Favorite obj where obj.user.id = :user_id and obj.type = :type ",params,0,4);

     mv.addObject("fav",fav);
     //是否签到
     SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd");
     Date date = si.parse(si.format(new Date()));
     Map map = new HashMap();
     map.put("f_uid", SecurityUserHolder.getCurrentUser().getId());
     map.put("qiandaoTime", date);
     List<IntegralLog> integralLogList = integralLogService.query("select obj from IntegralLog obj where obj.integral_user.id=:f_uid and obj.qiandaoTime =:qiandaoTime", map, -1, -1);
     String integralLog = "0";//0，是未签到，1,已签到
     if(integralLogList.size()>0){
       integralLog = "1";
     }
     mv.addObject("integralLog",integralLog);
     return mv;
   }
 
   private Set<Long> getSnsAttentionToUserIds(List<SnsAttention> SnsAttentions) {
     Set ids = new HashSet();
     for (SnsAttention attention : SnsAttentions) {
       ids.add(attention.getToUser().getId());
     }
     return ids;

   }
   @SecurityMapping(display = false, rsequence = 0, title="买家中心导航", value="/buyer/nav.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/recommend.htm"})
   public ModelAndView recommend(HttpServletRequest request, HttpServletResponse response,String currentPage) {
     ModelAndView mv = new JModelAndView("user/second/44Buyer_Recommend.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
     String op = CommUtil.null2String(request.getAttribute("op"));
     BargainGoodsQueryObject bgqo=new BargainGoodsQueryObject(currentPage, mv, "addTime", "desc");
     User user=SecurityUserHolder.getCurrentUser();
     if(null!=user){
       bgqo.addQuery("obj.tuijian_userid.id", new SysMap("tuijian_userid", CommUtil.null2Long(user.getId())), "=");
     }
     bgqo.setPageSize(Integer.valueOf(3));
     IPageList pList = this.bargainGoodsService.list(bgqo);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     CommUtil.saveIPageList2ModelAndView(url+"/buyer/recommend.htm", "", "", pList, mv);


     mv.addObject("op", op);
     return mv;
   }
   @RequestMapping({"/buyer/recommend_chakan.htm"})
   public ModelAndView recommend_chakan(HttpServletRequest request, HttpServletResponse response,String bgId,String gc_id,String id) {
     ModelAndView mv = new JModelAndView("user/second/44shangpintuijian.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
     Goods goods=this.goodsService.getObjById(CommUtil.null2Long(id));
     BargainGoods bg =this.bargainGoodsService.getObjById(CommUtil.null2Long(bgId));
     if (null!=bg) {
       mv.addObject("str",bg.getBg_recommend_reason());
     }else {
       mv.addObject("str",null);
     }
     mv.addObject("name",goods.getGoods_name());
     mv.addObject("gc_id",gc_id);
     mv.addObject("id",id);
      return mv;
   }
   @RequestMapping({"/buyer/recommend_del.htm"})
   public String recommend_del(HttpServletRequest request, HttpServletResponse response,String id) {
     String url="/buyer/recommend.htm";
         this.bargainGoodsService.delete(CommUtil.null2Long(id));

     return "redirect:"+url;

   }


     @SecurityMapping(display = false, rsequence = 0, title="买家中心导航", value="/buyer/nav.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/nav.htm"})
   public ModelAndView nav(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/buyer_nav.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String op = CommUtil.null2String(request.getAttribute("op"));
     mv.addObject("op", op);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="买家中心导航", value="/buyer/head.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/head.htm"})
   public ModelAndView head(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/buyer_head.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     return mv;
   }

   @RequestMapping({"/buyer/recommend_savezzy.htm"})
   public String  save(HttpServletRequest request, HttpServletResponse response,String name,String id,String gc_id,String str) {
        Goods goods=this.goodsService.getObjById(CommUtil.null2Long(id));
        GoodsCart goodsCart=this.goodsCartService.getObjById(CommUtil.null2Long(gc_id));
        BargainGoods bg = new BargainGoods();
        bg.setBg_price(goodsCart.getPrice());
        bg.setBg_nav(CommUtil.null2String("youhaohuo"));
        bg.setBg_class(CommUtil.null2String("dongtai"));
        bg.setAddTime(new Date());
        bg.setBg_goods(goods);
        bg.setBg_status(0);
        bg.setBg_recommend_reason(str);
        bg.setTuijian_userid(SecurityUserHolder.getCurrentUser());
     this.bargainGoodsService.save(bg);

    return "redirect:/buyer/recommend.htm";
   }

   @RequestMapping({"/buyer/authority.htm"})
   public ModelAndView authority(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView("error.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     mv.addObject("op_title", "您没有该项操作权限");
     mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     return mv;
   }
 
   private Set<Long> getSnsFriendToUserIds(List<SnsFriend> myfriends) {
     Set ids = new HashSet();
     if (myfriends.size() > 0) {
       for (SnsFriend friend : myfriends) {
         ids.add(friend.getToUser().getId());
       }
     }
     return ids;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="动态发布保存", value="/buyer/dynamic_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/dynamic_save.htm"})
   public ModelAndView dynamic_save(HttpServletRequest request, HttpServletResponse response, String content, String currentPage, String orderBy, String orderType, String store_id, String goods_id) {
     ModelAndView mv = new JModelAndView("user/default/usercenter/dynamic_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
     WebForm wf = new WebForm();
     Dynamic dynamic = wf.toPo(request, Dynamic.class);
     dynamic.setAddTime(new Date());
     dynamic.setUser(SecurityUserHolder.getCurrentUser());
     dynamic.setContent(content);
     dynamic.setDisplay(true);
     if ((store_id != null) && (!store_id.equals(""))) {
       Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));
       dynamic.setStore(store);
     }
     if ((goods_id != null) && (!goods_id.equals(""))) {
       Goods goods = this.goodsService.getObjById(
         CommUtil.null2Long(goods_id));
       dynamic.setGoods(goods);
 
       Map params = new HashMap();
       params.put("uid", SecurityUserHolder.getCurrentUser().getId());
       params.put("gc_id", goods.getGc().getId());
       List hgcs = this.HomeGoodsClassService.query("select obj from HomePageGoodsClass obj where obj.user.id=:uid and obj.gc.id=:gc_id", params, -1, -1);
       if (hgcs.size() == 0) {
         Map map = new HashMap();
         map.put("uid", SecurityUserHolder.getCurrentUser().getId());
         HomePageGoodsClass hpgc = new HomePageGoodsClass();
         hpgc.setAddTime(new Date());
         hpgc.setUser(SecurityUserHolder.getCurrentUser());
         hpgc.setGc(goods.getGc());
         this.HomeGoodsClassService.save(hpgc);
       }
     }
     this.dynamicService.save(dynamic);
 
     DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv, orderBy, orderType);
     qo.addQuery("obj.dissParent.id is null", null);
     qo.setOrderBy("addTime");
     qo.setOrderType("desc");
     qo.setPageSize(Integer.valueOf(10));
     IPageList pList = this.dynamicService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="删除动态", value="/buyer/dynamic_del.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/dynamic_del.htm"})
   public ModelAndView dynamic_ajax_del(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String orderBy, String orderType)
   {
     if (!id.equals("")) {
       Dynamic dynamic = this.dynamicService
         .getObjById(Long.valueOf(Long.parseLong(id)));
       this.dynamicService.delete(Long.valueOf(Long.parseLong(id)));
     }
 
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/dynamic_list.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.dissParent.id is null", null);
     qo.setOrderBy("addTime");
     qo.setOrderType("desc");
     qo.setPageSize(Integer.valueOf(10));
     IPageList pList = this.dynamicService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="ajax回复保存方法", value="/buyer/dynamic_ajax_reply.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/dynamic_ajax_reply.htm"})
   public ModelAndView dynamic_ajax_reply(HttpServletRequest request, HttpServletResponse response, String parent_id, String fieldName, String reply_content)
     throws ClassNotFoundException
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/dynamic_childs_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     WebForm wf = new WebForm();
     Dynamic dynamic = wf.toPo(request, Dynamic.class);
     Dynamic parent = null;
     if ((parent_id != null) && (!parent_id.equals(""))) {
       parent = this.dynamicService.getObjById(Long.valueOf(Long.parseLong(parent_id)));
       dynamic.setDissParent(parent);
       this.dynamicService.update(parent);
       dynamic.setDissParent(parent);
     }
     dynamic.setAddTime(new Date());
     dynamic.setUser(SecurityUserHolder.getCurrentUser());
     dynamic.setContent(reply_content);
     this.dynamicService.save(dynamic);
     mv.addObject("obj", parent);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="ajax赞动态方法", value="/buyer/dynamic_ajax_praise.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/dynamic_ajax_praise.htm"})
   public void dynamic_ajax_praise(HttpServletRequest request, HttpServletResponse response, String dynamic_id)
     throws ClassNotFoundException
   {
     Dynamic dynamic = this.dynamicService.getObjById(
       Long.valueOf(Long.parseLong(dynamic_id)));
     dynamic.setPraiseNum(dynamic.getPraiseNum() + 1);
     this.dynamicService.update(dynamic);
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(dynamic.getPraiseNum());
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="ajax转发动态保存方法", value="/buyer/dynamic_ajax_turn.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/dynamic_ajax_turn.htm"})
   public ModelAndView dynamic_ajax_turn(HttpServletRequest request, HttpServletResponse response, String dynamic_id, String content, String currentPage, String orderType, String orderBy)
     throws ClassNotFoundException
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/dynamic_list.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Dynamic dynamic = this.dynamicService.getObjById(
       Long.valueOf(Long.parseLong(dynamic_id)));
     dynamic.setTurnNum(dynamic.getTurnNum() + 1);
     this.dynamicService.update(dynamic);
     Dynamic turn = new Dynamic();
     turn.setAddTime(new Date());
     turn.setContent(content + "//转自" + dynamic.getUser().getUserName() + 
       ":" + dynamic.getContent());
     turn.setUser(SecurityUserHolder.getCurrentUser());
     this.dynamicService.save(turn);
 
     DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.dissParent.id is null", null);
     qo.setOrderBy("addTime");
     qo.setOrderType("desc");
     qo.setPageSize(Integer.valueOf(10));
     IPageList pList = this.dynamicService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="删除动态下方自己发布的评论", value="/buyer/dynamic_reply_del.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/dynamic_reply_del.htm"})
   public ModelAndView dynamic_reply_del(HttpServletRequest request, HttpServletResponse response, String id, String parent_id)
   {
     if (!id.equals("")) {
       Dynamic dynamic = this.dynamicService
         .getObjById(Long.valueOf(Long.parseLong(id)));
       this.dynamicService.delete(Long.valueOf(Long.parseLong(id)));
     }
 
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/dynamic_childs_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if ((parent_id != null) && (!parent_id.equals(""))) {
       Dynamic obj = this.dynamicService.getObjById(
         CommUtil.null2Long(parent_id));
       mv.addObject("obj", obj);
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="用户分享收藏店铺列表", value="/buyer/fav_store_list.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/fav_store_list.htm"})
   public ModelAndView fav_store_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/fav_store_list.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.user.id", 
       new SysMap("uid", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(1)), "=");
     qo.setPageSize(Integer.valueOf(4));
     IPageList pList = this.favService.list(qo);
     mv.addObject("objs", pList.getResult());
     String Ajax_url = CommUtil.getURL(request) + 
       "/buyer/fav_store_list_ajax.htm";
     mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(Ajax_url, 
       "", pList.getCurrentPage(), pList.getPages()));
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="用户分享收藏店铺ajax列表", value="/buyer/fav_store_list_ajax.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/fav_store_list_ajax.htm"})
   public ModelAndView fav_store_list_ajax(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/fav_store_list_ajax.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.user.id", 
       new SysMap("uid", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(1)), "=");
     qo.setPageSize(Integer.valueOf(4));
     IPageList pList = this.favService.list(qo);
     mv.addObject("objs", pList.getResult());
     String Ajax_url = CommUtil.getURL(request) + 
       "/buyer/fav_store_list_ajax.htm";
     mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(Ajax_url, 
       "", pList.getCurrentPage(), pList.getPages()));
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="用户分享收藏商品列表", value="/buyer/fav_goods_list.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/fav_goods_list.htm"})
   public ModelAndView fav_goods_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView("user/default/usercenter/fav_goods_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
     FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv, orderBy, orderType);
     qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder.getCurrentUser().getId()), "=");
     qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(0)), "=");
     qo.setPageSize(Integer.valueOf(4));
     IPageList pList = this.favService.list(qo);
     mv.addObject("objs", pList.getResult());
     String Ajax_url = CommUtil.getURL(request) + "/buyer/fav_goods_list_ajax.htm";mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(Ajax_url, "", pList.getCurrentPage(), pList.getPages()));
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="用户分享收藏商品ajax列表", value="/buyer/fav_goods_list_ajax.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/fav_goods_list_ajax.htm"})
   public ModelAndView fav_goods_list_ajax(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/fav_goods_list_ajax.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.user.id", 
       new SysMap("uid", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(0)), "=");
     qo.setPageSize(Integer.valueOf(4));
     IPageList pList = this.favService.list(qo);
     mv.addObject("objs", pList.getResult());
     String Ajax_url = CommUtil.getURL(request) + 
       "/buyer/fav_goods_list_ajax.htm";
     mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(Ajax_url, 
       "", pList.getCurrentPage(), pList.getPages()));
     return mv;
   }




 }


 
 
 