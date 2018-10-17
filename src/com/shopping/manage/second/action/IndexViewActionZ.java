package com.shopping.manage.second.action;

import com.shopping.core.mv.JModelAndView;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.*;
import com.shopping.foundation.service.*;
import com.shopping.view.web.tools.NavViewTools;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by acer on 2017/3/27.
 */
@Controller
public class IndexViewActionZ {
    @Autowired
    private IGoodsCartService goodsCartService;
    @Autowired
    private IUserService userService;
    @Autowired
    private NavViewTools navTools;
    @Autowired
    private IStoreService storeService;
    @Autowired
    private IMessageService messageService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IStoreCartService storeCartService;
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IIntegralLogService integralLogService;

    @Autowired
    private IGoodsClassService goodsClassService;

    @RequestMapping( { "second/topZ.htm" } )
    public ModelAndView topZ(HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView mv = new JModelAndView( "user/second/topAndFooter/top.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response );
        List msgs = new ArrayList();
        if( SecurityUserHolder.getCurrentUser() != null ) {
            Map params = new HashMap();
            params.put( "status", Integer.valueOf( 0 ) );
            params.put( "reply_status", Integer.valueOf( 1 ) );
            params.put( "from_user_id", SecurityUserHolder.getCurrentUser().getId() );
            params.put( "to_user_id", SecurityUserHolder.getCurrentUser().getId() );
            msgs = this.messageService.query( "select obj from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:to_user_id) or (obj.reply_status=:reply_status and obj.fromUser.id=:from_user_id) ", params, -1, -1 );
        }
        Store store = null;
        if( SecurityUserHolder.getCurrentUser() != null )
            store = this.storeService.getObjByProperty( "user.id", SecurityUserHolder.getCurrentUser().getId() );
        mv.addObject( "store", store );
        mv.addObject( "navTools", this.navTools );
        mv.addObject( "msgs", msgs );
        List<GoodsCart> list = new ArrayList<GoodsCart>();
        List<StoreCart> cart = new ArrayList<StoreCart>();
        List<StoreCart> user_cart = new ArrayList<StoreCart>();
        List<StoreCart> cookie_cart = new ArrayList<StoreCart>();
        User user = null;
        if( SecurityUserHolder.getCurrentUser() != null ) {
            user = this.userService.getObjById( SecurityUserHolder.getCurrentUser().getId() );
        }
        String cart_session_id = "";
        Map params = new HashMap();
        Cookie[] cookies = request.getCookies();
        if( cookies != null ) {
            for( Cookie cookie : cookies ) {
                if( cookie.getName().equals( "cart_session_id" ) ) {
                    cart_session_id = CommUtil.null2String( cookie.getValue() );
                }
            }
        }
        if( user != null ) {
            if( !cart_session_id.equals( "" ) ) {
                if( user.getStore() != null ) {
                    params.clear();
                    params.put( "cart_session_id", cart_session_id );
                    params.put( "user_id", user.getId() );
                    params.put( "sc_status", Integer.valueOf( 0 ) );
                    params.put( "store_id", user.getStore().getId() );
                    List<StoreCart> store_cookie_cart = this.storeCartService.query( "select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
                            params, -1, -1 );
                    for( StoreCart sc : store_cookie_cart ) {
                        for( GoodsCart gc : sc.getGcs() ) {
                            gc.getGsps().clear();
                            this.goodsCartService.delete( gc.getId() );
                        }
                        this.storeCartService.delete( sc.getId() );
                    }
                }

                params.clear();
                params.put( "cart_session_id", cart_session_id );
                params.put( "sc_status", Integer.valueOf( 0 ) );
                cookie_cart = this.storeCartService.query( "select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status", params, -1, -1 );

                params.clear();
                params.put( "user_id", user.getId() );
                params.put( "sc_status", Integer.valueOf( 0 ) );
                user_cart = this.storeCartService.query( "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params, -1, -1 );
            }
            else {
                params.clear();
                params.put( "user_id", user.getId() );
                params.put( "sc_status", Integer.valueOf( 0 ) );
                user_cart = this.storeCartService.query( "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params, -1, -1 );
            }

        }
        else if( !cart_session_id.equals( "" ) ) {
            params.clear();
            params.put( "cart_session_id", cart_session_id );
            params.put( "sc_status", Integer.valueOf( 0 ) );
            cookie_cart = this.storeCartService.query( "select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status", params, -1, -1 );
        }

        for( StoreCart sc : user_cart ) {
            boolean sc_add = true;
            for( StoreCart sc1 : cart ) {
                if( sc1.getStore().getId().equals( sc.getStore().getId() ) ) {
                    sc_add = false;
                }
            }
            if( sc_add )
                cart.add( sc );
        }
        boolean sc_add;
        for( StoreCart sc : cookie_cart ) {
            sc_add = true;
            for( StoreCart sc1 : cart ) {
                if( sc1.getStore().getId().equals( sc.getStore().getId() ) ) {
                    sc_add = false;
                    for( GoodsCart gc : sc.getGcs() ) {
                        gc.setSc( sc1 );
                        this.goodsCartService.update( gc );
                    }
                    this.storeCartService.delete( sc.getId() );
                }
            }
            if( sc_add ) {
                cart.add( sc );
            }
        }
        if( cart != null ) {
            for( StoreCart sc : cart ) {
                if( sc != null ) {
                    list.addAll( sc.getGcs() );
                }
            }
        }
        float total_price = 0.0F;
        for( GoodsCart gc : list ) {
            Goods goods = this.goodsService.getObjById( gc.getGoods().getId() );
            if( CommUtil.null2String( gc.getCart_type() ).equals( "combin" ) )
                total_price = CommUtil.null2Float( goods.getCombin_price() );
            else {
                total_price = CommUtil.null2Float( Double.valueOf( CommUtil.mul( Integer.valueOf( gc.getCount() ), goods.getGoods_current_price() ) ) ) + total_price;
            }
        }
        mv.addObject( "total_price", Float.valueOf( total_price ) );
        mv.addObject( "cart", list );

        return mv;
    }


   /* @RequestMapping( { "second/recommend_goods.htm" } )
    public ModelAndView recommend_goods( HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mv = new JModelAndView("user/second/05goods_list_recommend.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

        Map params = new HashMap();
        params.put( "store_recommend", Boolean.valueOf( true ) );
        params.put( "goods_status", Integer.valueOf( 0 ) );
        List store_reommend_goods_list = this.goodsService.query( "select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.store_recommend_time desc", params, -1, -1 );

        mv.addObject( "objs", store_reommend_goods_list );
        return mv;
    }*/
   @RequestMapping( { "second/recommend.htm" } )
   public ModelAndView recommend(HttpServletRequest request, HttpServletResponse response ) {
       ModelAndView mv = new JModelAndView( "user/second/05goods_list_recommend.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response );
       Map params = new HashMap();
       params.clear();
       params.put( "store_recommend", Boolean.valueOf( true ) );
       params.put( "goods_status", Integer.valueOf( 0 ) );
       //params.put( "begin", Integer.valueOf(begin) );
       List store_reommend_goods_list = this.goodsService.query( "select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.store_recommend_time desc", params, -1, -1 );

       mv.addObject( "objs", store_reommend_goods_list );

       return mv;
   }

    @RequestMapping( { "second/headZ.htm" } )
    public ModelAndView headZ(HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView mv = new JModelAndView( "user/second/topAndFooter/head.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response );
        String type = CommUtil.null2String( request.getAttribute( "type" ) );
        mv.addObject( "type", type.equals( "" ) ? "goods" : type );

        return mv;
    }
    @RequestMapping( { "second/left_buy.htm" } )
    public ModelAndView left_buyZ(HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView mv = new JModelAndView( "user/second/topAndFooter/left_buy.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response );
        String type = CommUtil.null2String( request.getAttribute( "type" ) );
        mv.addObject( "type", type.equals( "" ) ? "goods" : type );

        return mv;
    }


    @RequestMapping( { "second/leftZ.htm" } )
    public ModelAndView leftZ(HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView mv = new JModelAndView( "user/second/topAndFooter/left.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response );

        if (SecurityUserHolder.getCurrentUser()!=null) {
            User user=SecurityUserHolder.getCurrentUser();
            mv.addObject("user",user);
        }
        return mv;
    }

    @RequestMapping( { "second/rightZ.htm" } )
    public ModelAndView rightZ(HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView mv = new JModelAndView( "user/second/topAndFooter/right.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response );
        String str=this.configService.getSysConfig().getService_qq_list();
        String[] stri=CommUtil.splitByChar(str,"\r\n");
        String str2=this.configService.getSysConfig().getService_telphone_list();
        String[] stri2=CommUtil.splitByChar(str2,"\r\n");

        mv.addObject("stri",stri);
        mv.addObject("stri2",stri2);
        return mv;
    }

    @RequestMapping( { "second/footZ.htm" } )
    public ModelAndView footZ(HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView mv = new JModelAndView( "user/second/topAndFooter/foot.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response );


        return mv;
    }

    @RequestMapping( { "second/head_index.htm" } )
    public ModelAndView head_index(HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView mv = new JModelAndView( "user/second/topAndFooter/head_index.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response );
      User user=SecurityUserHolder.getCurrentUser();
      if(null!=user){
          mv.addObject("user",user);
          String p_number=user.getParent_number();
          User user2=this.userService.getObjById(CommUtil.null2Long(p_number));
          if(null!=user2){
              mv.addObject("user2",user2);
          }
      }


        return mv;
    }
    @RequestMapping( { "second/nav_everyday.htm" } )
    public ModelAndView nav_everyday( HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView mv = new JModelAndView("user/second/topAndFooter/nav_everyday.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

        mv.addObject( "navTools", this.navTools );
        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "display", Boolean.valueOf( true ) );
        params.put( "bc_location", "3" );
        List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display and obj.bc_location=:bc_location order by obj.sequence asc", params, 0, 14 );
        List childs = ((GoodsClass)gcs.get(0)).getChilds();
        mv.addObject( "gcs", gcs );

            User user2=(User) request.getSession().getAttribute("P-user");
            if(null!=user2){
                mv.addObject("user2",user2);
            }
        return mv;
    }

    @RequestMapping({"/fenxiaoguize.htm"})
    public ModelAndView fenxiaoguize(HttpServletRequest request, HttpServletResponse response)
    {
        ModelAndView mv = new JModelAndView("fenxiaoguize.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
        if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
             mv = new JModelAndView("newwap/fenxiaoguize.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        Article art=this.articleService.getObjById(CommUtil.null2Long(196627));

            mv.addObject("article",art);
        return mv;
    }



    //签到积分
    @RequestMapping( { "second/qiandao.htm" } )
    public void qiandao( HttpServletRequest request, HttpServletResponse response) {
        User user=SecurityUserHolder.getCurrentUser();
        Map map = new HashMap();
        //Calendar cal = Calendar.getInstance();
        map.put("qiandaoTime",CommUtil.formatDate(CommUtil.formatShortDate(new Date())));
        System.out.println("aaaa"+CommUtil.formatShortDate(new Date()));
        map.put("integral_user_id",CommUtil.null2Long(user.getId()));
        map.put("type",CommUtil.null2String("qiandao"));
        List is=this.integralLogService.query("select obj from IntegralLog obj where obj.qiandaoTime =:qiandaoTime and obj.integral_user.id=:integral_user_id and obj.type=:type ",map,-1,-1);
        if(is.size()>0){
            map.put("qian","今天已经签过到");
        }else{
            map.clear();
            if (this.configService.getSysConfig().isIntegral()) {
                int fen=user.getIntegral();
                int qian=this.configService.getSysConfig().getQiandao();
                int sum=fen + qian;
                user.setIntegral(sum);
                this.userService.update(user);

                IntegralLog log = new IntegralLog();
                log.setAddTime(new Date());
                log.setContent("用户签到添加" + this.configService.getSysConfig().getQiandao() + "分");
                log.setIntegral(this.configService.getSysConfig().getQiandao());
                log.setIntegral_user(user);
                log.setType("qiandao");
                log.setQiandaoTime(CommUtil.formatDate(CommUtil.formatShortDate(new Date())));
                log.setIsshengxiao("0");
                this.integralLogService.save(log);
                map.put("qian","签到成功");
            }else
                map.put("qian","暂时没有开启签到功能");
        }
        String qian= Json.toJson(map, JsonFormat.compact());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");

        try {
            PrintWriter writer=response.getWriter();
            writer.print(qian);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
