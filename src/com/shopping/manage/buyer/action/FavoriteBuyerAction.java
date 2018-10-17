 package com.shopping.manage.buyer.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Favorite;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.FavoriteQueryObject;
 import com.shopping.foundation.service.IFavoriteService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;

 import org.springframework.asm.Type;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;

 import java.util.List;

 @Controller
 public class FavoriteBuyerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IFavoriteService favoriteService;
 
   @SecurityMapping(display = false, rsequence = 0, title="用户商品收藏", value="/buyer/favorite_goods.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/favorite_goods.htm"})
   public ModelAndView favorite_goods(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType,String type)
   {
     ModelAndView mv = new JModelAndView("user/second/58Goods_Collect.html", this.configService.getSysConfig(),
       this.userConfigService.getUserConfig(), 0, request, response);
       String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
       if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
           mv = new JModelAndView("newwap/Goods_Collect.html",
                   this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       }
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "&type="+type;
     FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(CommUtil.null2Int(type))), "=");
     qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
     qo.addQuery("obj.deleteStatus", new SysMap("deleteStatus",CommUtil.null2Boolean(false)), "=");
     IPageList pList = this.favoriteService.list(qo);
      List<Favorite> list = pList.getResult();
       mv.addObject("type",type);
     CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_goods.htm", "", params, pList, mv);
     mv.addObject("type",type);
     return mv;
   }

  /* @SecurityMapping(display = false, rsequence = 0, title="用户店铺收藏", value="/buyer/favorite_store.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/favorite_store.htm"})
   public ModelAndView favorite_store(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView(
       "user/second/59Store_Collect.html", this.configService
       .getSysConfig(),
       this.userConfigService.getUserConfig(), 0, request, response);
       String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
       if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
           mv = new JModelAndView("newwap/我的店铺.html",
                   this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       }
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv,
       orderBy, orderType);
     qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(1)), "=");
     qo.addQuery("obj.user.id",
       new SysMap("user_id",
       SecurityUserHolder.getCurrentUser().getId()), "=");
     IPageList pList = this.favoriteService.list(qo);
     CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_store.htm",
       "", params, pList, mv);
     return mv;
   }*/

   @SecurityMapping(display = false, rsequence = 0, title="用户收藏删除", value="/buyer/favorite_del.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/favorite_delZ.htm"})
   public String favorite_del(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, int type) {
    /* String[] ids = mulitId.split(",");
     for (String id : ids) {*/
       if (!id.equals("")) {
         Favorite favorite = this.favoriteService.getObjById(Long.valueOf(Long.parseLong(id)));
           favorite.setDeleteStatus(true);
         this.favoriteService.update(favorite);
       }
       return "redirect:favorite_goods.htm?currentPage=" + currentPage+"&type="+type;

    // return "redirect:favorite_store.htm?currentPage=" + currentPage;
   }

 }


 
 
 