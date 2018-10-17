package com.shopping.manage.second.action;

import com.shopping.core.annotation.SecurityMapping;
import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.WebForm;
import com.shopping.foundation.domain.BargainGoods;
import com.shopping.foundation.domain.Goods;
import com.shopping.foundation.domain.GoodsClass;
import com.shopping.foundation.domain.query.BargainGoodsQueryObject;
import com.shopping.foundation.domain.query.GoodsClassQueryObject;
import com.shopping.foundation.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by acer on 2017/3/14.
 */
@Controller
public class ZeroShop {

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IGoodsClassService goodsClassService;
    @Autowired
    private IBargainService bargainService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IBargainGoodsService bargainGoodsService;

    @RequestMapping({"/second/zeroshop.htm"})
    public ModelAndView zeroshop(HttpServletRequest request, HttpServletResponse response,int bc_location, String currentPage, String orderBy, String orderType){
        ModelAndView mv = new JModelAndView("admin/blue/goods_class_list.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        request.getSession().setAttribute("shopping_view_type", "wap");
        //System.out.println("aaaaaabg_location"+bc_location);
        GoodsClassQueryObject qo = new GoodsClassQueryObject(currentPage, mv, "sequence", "asc");
        qo.addQuery("obj.bc_location", new SysMap("bc_location", Integer.valueOf(bc_location)),"=");
        qo.addQuery("obj.parent.id is null", null);
        //qo.addQuery("obj.bg_location is 1",null);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, GoodsClass.class, mv);
        IPageList pList = this.goodsClassService.list(qo);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_class_list.htm", "", "", pList, mv);

        return mv;
    }

    @RequestMapping({"/second/zeroShopList.htm"})
    public ModelAndView zeroShopList(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/second/zeroShopList.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

        BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy, orderType);
        //bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
        //bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(0)), "=");
        bqo.addQuery("obj.bg_nav", new SysMap("bg_nav", "lingrungou"), "=");
        bqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
        bqo.addQuery("obj.bg_goods.goods_store.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
       // bqo.addQuery("obj.bg_class", new SysMap("bg_class", bg_class), "=");
        bqo.setPageSize(Integer.valueOf(20));
        IPageList pList = this.bargainGoodsService.list(bqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

   return mv;
    }

    @RequestMapping({"/second/zeroshopAdd.htm"})
    public ModelAndView zeroShopAdd(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/second/zeroShopAdd.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 2 and obj.display=:display order by obj.sequence asc", params, 0, 15);
        //int max0 = gcs.size();
        mv.addObject("gcs", gcs);


        String bargain_time = CommUtil.formatLongDate(new Date());
        mv.addObject("bargain_time", bargain_time);
        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title="商品保存", value="/seller/bargain_apply_save.htm*", rtype="seller", rname="今日特价", rcode="bargain_seller", rgroup="促销管理")
    @RequestMapping({"/second/zeroShop_save.htm"})
    public String zeroShop_save(HttpServletRequest request, HttpServletResponse response, String goods_ids, String bg_class,double bg_price, String bargain_time, String bg_rebate)
    {    Goods goods=null;
        Date addTime=null;
        GoodsClass gcs=null;
        if ((goods_ids != null) && (!goods_ids.equals(""))) {
       String[] ids = goods_ids.split(",");
       for (String id : ids) {
            if (!goods_ids.equals("")) {
                BargainGoods bg = new BargainGoods();
             /*long a=bg.getBg_goods().getGoods_store().getId();*/
             //System.out.println("aaaaaid"+bg_class);
                addTime = new Date();
                bg.setAddTime(addTime);
                bg.setBg_class(bg_class);
                bg.setBg_nav("lingrungou");
                bg.setBg_status(0);
                bg.setBg_time(CommUtil.formatDate(bargain_time));
                goods = this.goodsService.getObjById(CommUtil.null2Long(goods_ids));
                gcs=this.goodsClassService.getObjById(CommUtil.null2Long(bg_class));
                goods.setBargain_status(1);
                goods.setGc(gcs);
                goods.setGoods_status(3);
                this.goodsService.update(goods);



                int ii= new Long(goods.getGoods_store().getId()).intValue();
                //bg.setBg_storeid(goods.getGoods_store());

                //double bg_price = CommUtil.mul(Double.valueOf(CommUtil.mul(Double.valueOf(0.1D), bg_rebate)), goods.getStore_price());
                //BigDecimal num = BigDecimal.valueOf(0.1D);
               // BigDecimal reb = BigDecimal.valueOf(CommUtil.null2Double(bg_rebate));
                bg.setBg_goods(goods);
                //BigDecimal now_price = BigDecimal.valueOf(CommUtil.mul(Double.valueOf(CommUtil.mul(num, reb)), goods.getGoods_current_price()));
                BigDecimal now_price = BigDecimal.valueOf(bg_price);
                bg.setBg_price(now_price);
                goods.setBargain_status(1);
                this.goodsService.update(goods);

                bg.setBg_goods(goods);
                //bg.setBg_rebate(BigDecimal.valueOf(CommUtil.null2Double(bg_rebate)));
                this.bargainGoodsService.save(bg);
            }
            }

            return "redirect:bargain_apply_successZ.htm?goods_ids=" + goods_ids+"&bargain_time="+bargain_time;
        }
        return "redirect:bargain_apply_errorZ.htm";
    }

    @SecurityMapping(display = false, rsequence = 0, title="商品保存成功", value="/seller/bargain_apply_success.htm*", rtype="seller", rname="今日特价", rcode="bargain_seller", rgroup="促销管理")
    @RequestMapping({"/second/bargain_apply_successZ.htm"})
    public ModelAndView bargain_apply_success(HttpServletRequest request, HttpServletResponse response, String goods_ids,String bargain_time)
    {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);


        mv.addObject("op_title", "申请零润购成功");
        mv.addObject("url", CommUtil.getURL(request) + "/second/zeroShopList.htm");
        return mv;
    }
    @SecurityMapping(display = false, rsequence = 0, title="商品保存失败", value="/seller/bargain_apply_error.htm*", rtype="seller", rname="今日特价", rcode="bargain_seller", rgroup="促销管理")
    @RequestMapping({"/second/bargain_apply_errorZ.htm"})
    public ModelAndView bargain_apply_error(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "至少选择一件商品");
        mv.addObject("url", CommUtil.getURL(request) + "/second/zeroshopAdd.htm");
        return mv;
         }
    @RequestMapping({"second/zerogoods_delete.htm"})
    public String havegoods_del(HttpServletRequest request, HttpServletResponse response,String id){
        String url="/second/zeroShopList.htm";
     /*   ModelAndView mv = new JModelAndView("user/second/seller_veryGoodGoods.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);*/
       BargainGoods bg=this.bargainGoodsService.getObjById(CommUtil.null2Long(id));
        bg.setDeleteStatus(true);
        this.bargainGoodsService.update(bg);
       /* Long id2=bg.getBg_goods().getId();
        Goods good=this.goodsService.getObjById(id2);
        good.setBargain_status(0);
        this.goodsService.update(good);
        this.bargainGoodsService.delete(CommUtil.null2Long(id));*/
        return "redirect:"+url;
    }
    }

