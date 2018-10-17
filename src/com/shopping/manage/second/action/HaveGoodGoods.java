package com.shopping.manage.second.action;

import com.shopping.core.annotation.SecurityMapping;
import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.WebForm;
import com.shopping.core.tools.database.DatabaseTools;
import com.shopping.foundation.domain.*;
import com.shopping.foundation.domain.query.BargainGoodsQueryObject;
import com.shopping.foundation.domain.query.FavoriteQueryObject;
import com.shopping.foundation.domain.query.GoodsQueryObject;
import com.shopping.foundation.service.*;
import com.shopping.lucene.LuceneUtil;
import com.shopping.lucene.LuceneVo;
import com.shopping.manage.admin.tools.StoreTools;
import com.shopping.manage.seller.Tools.TransportTools;
import com.shopping.view.web.tools.GoodsViewTools;
import com.shopping.view.web.tools.NavViewTools;
import com.shopping.view.web.tools.StoreViewTools;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/3/14.
 */
@Controller
public class HaveGoodGoods {


    @Autowired
    private IBargainGoodsService bargainGoodsService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IGoodsClassService goodsClassService;

    @Autowired
    private IGoodsClassStapleService goodsclassstapleService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IAccessoryService accessoryService;

    @Autowired
    private IUserGoodsClassService userGoodsClassService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IStoreService storeService;

    @Autowired
    private IGoodsBrandService goodsBrandService;

    @Autowired
    private IGoodsSpecPropertyService specPropertyService;

    @Autowired
    private IGoodsTypePropertyService goodsTypePropertyService;

    @Autowired
    private IWaterMarkService waterMarkService;

    @Autowired
    private IGoodsCartService goodsCartService;

    @Autowired
    private IAlbumService albumService;

    @Autowired
    private IReportService reportService;

    @Autowired
    private IOrderFormService orderFormService;

    @Autowired
    private IOrderFormLogService orderFormLogService;

    @Autowired
    private IEvaluateService evaluateService;

    @Autowired
    private ITransportService transportService;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private TransportTools transportTools;

    @Autowired
    private StoreTools storeTools;

    @Autowired
    private StoreViewTools storeViewTools;

    @Autowired
    private GoodsViewTools goodsViewTools;

    @Autowired
    private DatabaseTools databaseTools;

    @Autowired
    private NavViewTools navTools;

    @Autowired
    private IFavoriteService favoriteService;
   /* @RequestMapping({"/second/havegoodgoods.htm"})
    public ModelAndView everyDayGoods(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("../shop/newwap/haveGoodGoods/haveGoodGoods.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);


        return mv;
    }*/

    @RequestMapping({"/second/haveGoodGoods.htm"})
    public ModelAndView haveGoodGoods(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
        ModelAndView mv = new JModelAndView("user/second/seller_veryGoodGoods.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);

        BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy, orderType);
        //bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
        //bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(0)), "=");
        bqo.addQuery("obj.bg_nav", new SysMap("bg_nav", "youhaohuo"), "=");
        bqo.addQuery("obj.bg_goods.goods_store.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
         bqo.addQuery("obj.bg_class", new SysMap("bg_class", "jingpin"), "=");
        bqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
        bqo.setPageSize(Integer.valueOf(20));
        IPageList pList = this.bargainGoodsService.list(bqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title="商品删除", value="seller/bargain_del.htm*", rtype="seller", rname="商品管理", rcode="bargain_seller", rgroup="删除商品")
    @RequestMapping({"second/havegoods_delete.htm"})
    public String havegoods_del(HttpServletRequest request, HttpServletResponse response,String id){
         String url="/second/haveGoodGoods.htm";
        BargainGoods bg=this.bargainGoodsService.getObjById(CommUtil.null2Long(id));
        bg.setDeleteStatus(true);
     /*   ModelAndView mv = new JModelAndView("user/second/seller_veryGoodGoods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);*/
        this.bargainGoodsService.update(bg);
        return "redirect:"+url;
    }


    @RequestMapping({"/second/haveGoodGoods_add.htm"})
    public ModelAndView haveGoodGoods_add(HttpServletRequest request, HttpServletResponse response){
    ModelAndView mv = new JModelAndView("user/second/seller_veryGoodGoods_add.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);

        Date date = new Date();
        mv.addObject("date",date);
        return mv;
    }

    @RequestMapping({"second/veryGoodGoods_detail.htm"})
    public ModelAndView veryGoodGoods_detail(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType,int id){
        ModelAndView mv = new JModelAndView("user/second/12haveGoodGoods_detariy.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
            mv = new JModelAndView("newwap/haveGoodGoods/12veryGoodGoods_detariy.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        mv.addObject("navTools", this.navTools);
        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 1 and obj.display=:display order by obj.sequence asc", params, 0, 15);
        mv.addObject("gcs", gcs);

       BargainGoods bg=this.bargainGoodsService.getObjById(Long.valueOf(id));
        mv.addObject("bg",bg);
        params.clear();
        params.put("goods_recommend", Boolean.valueOf(true));
        List goods=   this.goodsService.query("select obj from Goods obj where goods_recommend=:goods_recommend",params,0,10);
        mv.addObject("good",goods);

        return mv;
    }

    @RequestMapping({"second/seller_dynamic.htm"})
    public ModelAndView seller_dynamic(HttpServletRequest request, HttpServletResponse response,String currentPage){
        ModelAndView mv = new JModelAndView("user/second/99seller_dynamic.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);

        BargainGoodsQueryObject bgqo=new BargainGoodsQueryObject(currentPage, mv, "addTime", "desc");
        User user=SecurityUserHolder.getCurrentUser();
        if(null!=user){
            bgqo.addQuery("obj.bg_goods.goods_store.user.id", new SysMap("id", CommUtil.null2Long(user.getId())), "=");
        }
        bgqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
        bgqo.addQuery("obj.bg_nav", new SysMap("bg_nav", "youhaohuo"), "=");
        bgqo.addQuery("obj.bg_class", new SysMap("bg_class", CommUtil.null2String("dongtai")), "=");
        bgqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
        bgqo.setPageSize(Integer.valueOf(8));
        IPageList pList = this.bargainGoodsService.list(bgqo);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }

        CommUtil.saveIPageList2ModelAndView(url+"second/seller_dynamic.htm", "", "", pList, mv);

        return mv;
    }

    @RequestMapping({"second/seller_dynamic_deta.htm"})
    public ModelAndView seller_dynamic_deta(HttpServletRequest request, HttpServletResponse response,String goodsname,String addtime,String username ,String liyou){
        ModelAndView mv = new JModelAndView("user/second/100seller_dongtai_xiangqing.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
            mv.addObject("goodsname",goodsname);

            mv.addObject("addtime",addtime);
            mv.addObject("username",username);
            mv.addObject("liyou",liyou);
        return mv;
    }

    @RequestMapping({"second/dynamic_detail.htm"})
    public ModelAndView dynamic_detail(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType,int id){
        ModelAndView mv = new JModelAndView("user/second/16dynamic.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
            mv = new JModelAndView("newwap/16有好货_动态详情.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        mv.addObject("navTools", this.navTools);
        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 1 and obj.display=:display order by obj.sequence asc", params, 0, 15);
        mv.addObject("gcs", gcs);

        BargainGoods bg=this.bargainGoodsService.getObjById(Long.valueOf(id));
        mv.addObject("bg",bg);
        params.clear();
        params.put("goods_recommend", Boolean.valueOf(true));
        List goods=   this.goodsService.query("select obj from Goods obj where goods_recommend=:goods_recommend",params,0,10);
        mv.addObject("good",goods);
        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title="商品保存", value="/seller/bargain_apply_save.htm*", rtype="seller", rname="今日特价", rcode="bargain_seller", rgroup="促销管理")
    @RequestMapping({"/second/veryGoodGoods_save.htm"})
    public String zeroShop_save(HttpServletRequest request, HttpServletResponse response, String goods_ids, String bg_class, String bargain_time, String bg_recommend_reason,String bg_nav,String bg_rebate)
    {    Goods goods=null;
        Date addTime=null;
        if ((goods_ids != null) && (!goods_ids.equals(""))) {
            String[] ids = goods_ids.split(",");
            for (String id : ids) {
                if (!goods_ids.equals("")) {
                    BargainGoods bg = new BargainGoods();
                    addTime = new Date();
                    bg.setAddTime(addTime);
                    bg.setBg_class("jingpin");
                    bg.setBg_nav("youhaohuo");
                    bg.setBg_status(0);
                    bg.setBg_recommend_reason(bg_recommend_reason);
                    bg.setBg_time(CommUtil.formatDate(bargain_time));
                    goods = this.goodsService.getObjById(CommUtil.null2Long(goods_ids));
                    goods.setBargain_status(1);
                    this.goodsService.update(goods);
                    //int ii= new Long(goods.getGoods_store().getId()).intValue();
                    //bg.setBg_storeid(goods.getGoods_store());
                    //double bg_price = CommUtil.mul(Double.valueOf(CommUtil.mul(Double.valueOf(0.1D), bg_rebate)), goods.getStore_price());
                    //BigDecimal num = BigDecimal.valueOf(0.1D);
                    // BigDecimal reb = BigDecimal.valueOf(CommUtil.null2Double(bg_rebate));
                    bg.setBg_goods(goods);
                    //BigDecimal now_price = BigDecimal.valueOf(CommUtil.mul(Double.valueOf(CommUtil.mul(num, reb)), goods.getGoods_current_price()));
                    //BigDecimal now_price = BigDecimal.valueOf(bg_price);
                    //bg.setBg_price(now_price);
                    goods.setBargain_status(1);
                    this.goodsService.update(goods);
                    bg.setBg_goods(goods);
                    //bg.setBg_rebate(BigDecimal.valueOf(CommUtil.null2Double(bg_rebate)));
                    this.bargainGoodsService.save(bg);
                }
            }
            return "redirect:bargain_apply_successZZ.htm";
        }
        return "redirect:bargain_apply_errorZZ.htm";
    }

    @SecurityMapping(display = false, rsequence = 0, title="商品保存成功", value="/seller/bargain_apply_success.htm*", rtype="seller", rname="今日特价", rcode="bargain_seller", rgroup="促销管理")
    @RequestMapping({"/second/bargain_apply_successZZ.htm"})
    public ModelAndView bargain_apply_success(HttpServletRequest request, HttpServletResponse response, String goods_ids,String bargain_time)
    {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "申请精品商品成功");
        mv.addObject("url", CommUtil.getURL(request) + "/second/haveGoodGoods.htm");
        return mv;
    }
    @SecurityMapping(display = false, rsequence = 0, title="商品保存失败", value="/seller/bargain_apply_error.htm*", rtype="seller", rname="今日特价", rcode="bargain_seller", rgroup="促销管理")
    @RequestMapping({"/second/bargain_apply_errorZZ.htm"})
    public ModelAndView bargain_apply_error(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "至少选择一件商品");
        mv.addObject("url", CommUtil.getURL(request) + "/second/haveGoodGoods_add.htm");
        return mv;
    }

    @RequestMapping({"/second/selfSay_wap.htm"})
    public ModelAndView selfSay_wap(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
        ModelAndView mv = new JModelAndView("user/second/13myselfSay.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
            mv = new JModelAndView("newwap/haveGoodGoods/myself.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        Map params = new HashMap();
        GoodsQueryObject goods = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        goods.addQuery("obj.goods_selfSay" ,new SysMap("goods_selfSay", "zishu"), "=");
        goods.addQuery("obj.selfSay_status" ,new SysMap("selfSay_status", Integer.valueOf( 1 )), "=");
       // goods.addQuery("obj.goods_status" ,new SysMap("goods_status",Integer.valueOf( 3 )), "=");
        goods.addQuery("obj.deleteStatus" ,new SysMap("deleteStatus",CommUtil.null2Boolean(false)), "=");


        goods.setPageSize(Integer.valueOf(6));
        IPageList pList = this.goodsService.list(goods);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
       /* params.put("goods_selfSay", "zishu");
        List list = this.goodsService.query( "select obj from Goods obj where obj.goods_selfSay = :goods_selfSay and obj.goods_status=:goods_status and obj.selfSay_status=:selfSay_status order by obj.goods_selfSay_newTime desc", params, 0, 6 );
     */

        mv.addObject("navTools", this.navTools);
        params.clear();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 1 and obj.display=:display order by obj.sequence asc", params, 0, 15);
        mv.addObject("gcs", gcs);
        return mv;
    }
    //我赞商品
    @RequestMapping({"/second/wozan.htm"})
    public ModelAndView wozan(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/second/17有好货_我赞.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
            mv = new JModelAndView("newwap/17有好货_我赞.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        User user=SecurityUserHolder.getCurrentUser();

        FavoriteQueryObject fqo=new FavoriteQueryObject(currentPage, mv, orderBy, orderType);
        if(null!=user) {
            fqo.addQuery("obj.user.id", new SysMap("id", CommUtil.null2Long(user.getId())), "=");
            fqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
            fqo.setPageSize(6);
            IPageList pList = this.favoriteService.list(fqo);
            CommUtil.saveIPageList2ModelAndView("/second/wozan.htm", "", "", pList, mv);
        }
        return mv;

    }

    @RequestMapping({"/second/wozan_ajax.htm"})
    public ModelAndView wozan_ajax(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/second/17有好货_我赞.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
            mv = new JModelAndView("newwap/0_youhaohuo_j.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        User user=SecurityUserHolder.getCurrentUser();

        FavoriteQueryObject fqo=new FavoriteQueryObject(currentPage, mv, orderBy, orderType);
        if(null!=user)fqo.addQuery("obj.user.id",new SysMap("id", CommUtil.null2Long(user.getId())), "=");
        fqo.addQuery("obj.deleteStatus",new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
        fqo.setPageSize(6);
        IPageList pList = this.favoriteService.list(fqo);
        CommUtil.saveIPageList2ModelAndView("/second/wozan.htm", "", "", pList, mv);
        return mv;

    }
    //刷新自述
    @RequestMapping({"/second/selfSay_shua.htm"})
    public String selfSay_shua(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType,String id){
        //ModelAndView mv = new JModelAndView("user/second/seller_selfsay.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        Goods good=this.goodsService.getObjById(CommUtil.null2Long(id));
        good.setGoods_selfSay_newTime(new Date());
        this.goodsService.update(good);

        return "redirect:/second/selfSay.htm?selfSay_status=1";
    }


//自述列标
    @RequestMapping({"/second/selfSay.htm"})
    public ModelAndView selfSay(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
        ModelAndView mv = new JModelAndView("user/second/seller_selfsay.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);

        Map params = new HashMap();
        //params.put( "store_recommend", Boolean.valueOf( true ) );
        params.put( "goods_status", Integer.valueOf( 3) );
        //params.put( "selfSay_status", Integer.valueOf( 1 ) );
        //System.out.println("aaaaaaaaaaadddd="+id);
        Store store = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()).getStore();
        params.put("store_id", store.getId());
        params.put("goods_selfSay", "zishu");
        params.put("deleteStatus", false);
        List list = this.goodsService.query( "select obj from Goods obj where obj.deleteStatus =:deleteStatus and obj.goods_selfSay = :goods_selfSay and obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_selfSay_newTime desc", params, 0, 6 );
        mv.addObject("objs",list);
        return mv;
    }
    //删除自述
    @SecurityMapping(display = false, rsequence = 0, title="商品删除", value="seller/bargain_del.htm*", rtype="seller", rname="商品管理", rcode="bargain_seller", rgroup="删除商品")
    @RequestMapping({"/second/selfSay_delete.htm"})
    public String bargain_del(HttpServletRequest request, HttpServletResponse response,String id){
        Goods g=this.goodsService.getObjById(CommUtil.null2Long(id));
        g.setDeleteStatus(true);

       // ModelAndView mv = new JModelAndView("user/second/seller_selfsay.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        this.goodsService.update(g);
        return "redirect:/second/selfSay.htm?selfSay_status=1";
    }

    @RequestMapping({"/second/selfSay_admin.htm"})
    public ModelAndView selfSay_admin(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType,String selfSay_status){

        ModelAndView mv = new JModelAndView("admin/blue/bargain_goods_list5.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);

        Map params = new HashMap();
        //params.put( "store_recommend", Boolean.valueOf( true ) );
        params.put( "goods_status", Integer.valueOf(3) );

            params.put("selfSay_status", Integer.valueOf(selfSay_status));
            params.put("deleteStatus", CommUtil.null2Boolean(false));
            params.put("goods_selfSay", "zishu");
            List list = this.goodsService.query("select obj from Goods obj where obj.goods_selfSay = :goods_selfSay and obj.deleteStatus=:deleteStatus and obj.goods_status=:goods_status and obj.selfSay_status=:selfSay_status order by obj.goods_selfSay_newTime desc", params, 0, 6);
            mv.addObject("objs", list);
            mv.addObject("selfSay_status",selfSay_status);
        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title="特价商品通过", value="/admin/bargain_goods_audit.htm*", rtype="admin", rname="天天特价", rcode="bargain_admin", rgroup="运营")
    @RequestMapping({"/second/bargain_goods_audit_zishu.htm"})
    public String bargain_goods_audit_zishu(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
        String uri = "";
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {

                    Goods bg = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
                    bg.setSelfSay_status(1);
                    bg.setGoods_selfSay_newTime(new Date());
                    this.goodsService.update(bg);


                }
            uri = "redirect:/second/selfSay_admin.htm?selfSay_status=0";
            }

        return uri;
    }

    @SecurityMapping(display = false, rsequence = 0, title="特价拒绝", value="/admin/bargain_goods_refuse.htm*", rtype="admin", rname="天天特价", rcode="bargain_admin", rgroup="运营")
    @RequestMapping({"/second/goods_refuse_zishu.htm"})
    public String goods_refuse__zishu(HttpServletRequest request, HttpServletResponse response, String bargain_time, String mulitId, String currentPage) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                Goods bg = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
                bg.setSelfSay_status(-1);
                bg.setGoods_selfSay_newTime(new Date());
                this.goodsService.update(bg);
            }
        }

        return "redirect:/second/selfSay_admin.htm?selfSay_status=0";
    }


    @RequestMapping({"/second/selfSay_detail.htm"})
    public ModelAndView selfSay_detail(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType,int id){
        ModelAndView mv = new JModelAndView("user/second/14myselfSay_detail.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);

        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if ((shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" ))) {
             mv = new JModelAndView("newwap/haveGoodGoods/myself_detail.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        /*Map params = new HashMap();
        //params.put( "store_recommend", Boolean.valueOf( true ) );
        params.put( "goods_status", Integer.valueOf( 0 ) );
        params.put( "selfSay_status", Integer.valueOf( 1 ) );
        //System.out.println("aaaaaaaaaaadddd="+id);
        Store store = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()).getStore();
        params.put("store_id", store.getId());
        params.put("goods_selfSay", "zishu");
        List list = this.goodsService.query( "select obj from Goods obj where obj.goods_selfSay = :goods_selfSay and obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.selfSay_status=:selfSay_status order by obj.goods_selfSay_newTime desc", params, 0, 6 );
       */
        Goods good = this.goodsService.getObjById(Long.valueOf(id));
        Store store =this.goodsService.getObjById( Long.valueOf(id)).getGoods_store();

        mv.addObject("store",store);
        mv.addObject("obj",good);
        return mv;
    }

    @RequestMapping({"/second/selfSay_add.htm"})
    public ModelAndView selfSay_add(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("user/second/seller_selfSay_add2.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);


        Date date = new Date();
        mv.addObject("date",date);
        return mv;
    }


    private String clearContent(String inputString) {
        String htmlStr = inputString;
        String textStr = "";
        try {
            String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>";
            String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>";
            String regEx_html = "<[^>]+>";
            String regEx_html1 = "<[^>]+";
            Pattern p_script = Pattern.compile(regEx_script, 2);
            Matcher m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll("");

            Pattern p_style = Pattern.compile(regEx_style, 2);
            Matcher m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll("");

            Pattern p_html = Pattern.compile(regEx_html, 2);
            Matcher m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll("");

            Pattern p_html1 = Pattern.compile(regEx_html1, 2);
            Matcher m_html1 = p_html1.matcher(htmlStr);
            htmlStr = m_html1.replaceAll("");

            textStr = htmlStr;
        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }
        return textStr;
    }
    @SecurityMapping(display = false, rsequence = 0, title = "有好货-自述保存", value = "/seller/add_goods_finish.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/second/selfSay_save.htm" })
    public ModelAndView selfSay_save(HttpServletRequest request, HttpServletResponse response, String image_ids, String goods_main_img_id) {
        ModelAndView mv = null;
mv = new JModelAndView("user/default/usercenter/add_goods_finish.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

            WebForm wf = new WebForm();
            Goods goods = null;
                goods = wf.toPo(request, Goods.class);
                goods.setAddTime(new Date());
                User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
                goods.setGoods_store(user.getStore());

            goods.setGoods_name(clearContent(goods.getGoods_name()));

            Accessory main_img = null;
            if ((goods_main_img_id != null) && (!goods_main_img_id.equals(""))) {
                main_img = this.accessoryService.getObjById(Long.valueOf(Long.parseLong(goods_main_img_id)));
            }
            goods.setGoods_main_photo(main_img);
            goods.setGoods_selfSay("zishu");
            goods.setGoods_selfSay_newTime(new Date());
            goods.setGoods_status(CommUtil.null2Int(3));
            String[] img_ids = image_ids.split(",");
            goods.getGoods_photos().clear();
            for (int i = 0; i < img_ids.length; i++) {
                String img_id = img_ids[i];
                if (!img_id.equals("")) {
                    Accessory img = this.accessoryService.getObjById(Long.valueOf(Long.parseLong(img_id)));
                    goods.getGoods_photos().add(img);
                }
            }
        goods.setP_fenrun(BigDecimal.valueOf(0));
            this.goodsService.save(goods);
                String goods_lucene_path = System.getProperty("user.dir") + File.separator + "luence" + File.separator + "goods";
                File file = new File(goods_lucene_path);
                if (!file.exists()) {
                    CommUtil.createFolder(goods_lucene_path);
                }
                LuceneVo vo = new LuceneVo();
                vo.setVo_id(goods.getId());
                vo.setVo_title(goods.getGoods_name());
                vo.setVo_content(goods.getGoods_details());
                vo.setVo_type("goods");
                vo.setVo_store_price(CommUtil.null2Double(goods.getStore_price()));
                vo.setVo_add_time(goods.getAddTime().getTime());
                vo.setVo_goods_salenum(goods.getGoods_salenum());
                LuceneUtil lucene = LuceneUtil.instance();
                LuceneUtil.setIndex_path(goods_lucene_path);
                lucene.writeIndex(vo);
            mv.addObject("obj", goods);
        return mv;
    }



}
