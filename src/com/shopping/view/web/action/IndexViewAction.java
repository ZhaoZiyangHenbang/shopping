package com.shopping.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.query.support.IPageList;
import com.shopping.foundation.domain.*;
import com.shopping.foundation.domain.query.GoodsQueryObject;
import com.shopping.foundation.domain.query.GroupGoodsQueryObject;
import com.shopping.foundation.service.*;
import com.shopping.plug.login.action.QQLoginPlug;
import com.shopping.send.HttpRequest;
import com.shopping.send.Str2MD5;
import org.nutz.dao.util.cri.Static;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.mv.JModelAndView;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.Md5Encrypt;
import com.shopping.manage.admin.tools.MsgTools;
import com.shopping.view.web.tools.GoodsFloorViewTools;
import com.shopping.view.web.tools.GoodsViewTools;
import com.shopping.view.web.tools.NavViewTools;
import com.shopping.view.web.tools.StoreViewTools;

@Controller
public class IndexViewAction {
	@Autowired
	private IGroupPriceRangeService groupPriceRangeService;
	@Autowired
	private IGroupClassService groupClassService;

	@Autowired
	private ISysConfigService configService;

	@Autowired
	private IUserConfigService userConfigService;

	@Autowired
	private IGoodsClassService goodsClassService;

	@Autowired
	private IGoodsBrandService goodsBrandService;

	@Autowired
	private IPartnerService partnerService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IArticleClassService articleClassService;

	@Autowired
	private IArticleService articleService;

	@Autowired
	private IAccessoryService accessoryService;

	@Autowired
	private IMessageService messageService;

	@Autowired
	private IStoreService storeService;

	@Autowired
	private IGoodsService goodsService;

	@Autowired
	private INavigationService navigationService;

	@Autowired
	private IGroupGoodsService groupGoodsService;

	@Autowired
	private IGroupService groupService;

	@Autowired
	private IGoodsFloorService goodsFloorService;

	@Autowired
	private IBargainGoodsService bargainGoodsService;

	@Autowired
	private IDeliveryGoodsService deliveryGoodsService;

	@Autowired
	private IStoreCartService storeCartService;

	@Autowired
	private IGoodsCartService goodsCartService;

	@Autowired
	private NavViewTools navTools;

	@Autowired
	private GoodsViewTools goodsViewTools;

	@Autowired
	private StoreViewTools storeViewTools;

	@Autowired
	private MsgTools msgTools;

	@Autowired
	private GoodsFloorViewTools gf_tools;

	@Autowired
	private IAlbumService albumService;

	@Autowired
	private IIntegralLogService integralLogService;
	/**
	 * 页面最上面部分
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping( { "/top.htm" } )
	public ModelAndView top( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "top.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
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
	/**
	 * 横着的导航栏
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping( { "/nav.htm" } )
	public ModelAndView nav( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView("nav.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

		mv.addObject( "navTools", this.navTools );
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "display", Boolean.valueOf( true ) );
		params.put( "bc_location", "1" );
		params.put( "deleteStatus", CommUtil.null2Boolean(false) );
		List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null and obj.display=:display and obj.bc_location=:bc_location order by obj.sequence asc", params, 0, 14 );
		/*List childs = ((GoodsClass)gcs.get(0)).getChilds();*/
		mv.addObject( "gcs", gcs );
		return mv;
	}

	@RequestMapping( { "/nav1.htm" } )
	public ModelAndView nav1( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "nav1.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		Map params = new HashMap();
		params.put( "display", Boolean.valueOf( true ) );
		params.put( "bc_location", "1" );
		params.put( "deleteStatus", CommUtil.null2Boolean(false) );
		List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null and obj.bc_location=:bc_location and obj.display=:display  order by obj.sequence asc", params, 0, 14 );
		mv.addObject( "gcs", gcs );
		mv.addObject( "navTools", this.navTools );
		return mv;
	}

	@RequestMapping( { "/nav_tt.htm" } )
	public ModelAndView nav_tt( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "nav_tt.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		Map params = new HashMap();
		params.put( "display", Boolean.valueOf( true ) );
		params.put( "bc_location", "3" );
		params.put( "deleteStatus", CommUtil.null2Boolean(false) );
		List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null and obj.bc_location=:bc_location and obj.display=:display  order by obj.sequence asc", params, 0, 14 );
		mv.addObject( "gcs", gcs );
		mv.addObject( "navTools", this.navTools );
		return mv;
	}
	@RequestMapping( { "/nav_lr.htm" } )
	public ModelAndView nav_lr( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "nav_lr.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		Map params = new HashMap();
		params.put( "display", Boolean.valueOf( true ) );
		params.put( "bc_location", "2" );
		params.put( "deleteStatus", CommUtil.null2Boolean(false) );
		List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null and obj.bc_location=:bc_location and obj.display=:display  order by obj.sequence asc", params, 0, 14 );
		mv.addObject( "gcs", gcs );
		mv.addObject( "navTools", this.navTools );
		return mv;
	}
	@RequestMapping( { "/nav_xs.htm" } )
	public ModelAndView nav_xs( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "nav_xs.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		Map params = new HashMap();
		params.put( "deleteStatus", CommUtil.null2Boolean(false) );
		List gcs = this.groupClassService.query("select obj from GroupClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null order by obj.gc_sequence asc", params, -1, -1);
		mv.addObject( "gcs", gcs );
		mv.addObject( "navTools", this.navTools );
		return mv;
	}
	@RequestMapping( { "/nav_sxy.htm" } )
	public ModelAndView nav_sxy( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "nav_sxy.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		Map params = new HashMap();
		params.put( "deleteStatus", CommUtil.null2Boolean(false) );
		params.put( "display", Boolean.valueOf( true ) );
		params.put( "bc_location", "5" );
		List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null and obj.bc_location=:bc_location and obj.display=:display  order by obj.sequence asc", params, 0, 14 );
		mv.addObject( "gcs", gcs );
		mv.addObject( "navTools", this.navTools );
		return mv;
	}
	@RequestMapping( { "/nav_ty.htm" } )
	public ModelAndView nav_ty( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "nav_ty.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		Map params = new HashMap();
		params.put( "display", Boolean.valueOf( true ) );
		params.put( "bc_location", "4" );
		params.put( "deleteStatus", CommUtil.null2Boolean(false) );
		List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.deleteStatus=:deleteStatus and obj.parent.id is null and obj.bc_location=:bc_location and obj.display=:display  order by obj.sequence asc", params, 0, 14 );
		mv.addObject( "gcs", gcs );
		mv.addObject( "navTools", this.navTools );
		return mv;
	}

	@RequestMapping( { "/head.htm" } )
	public ModelAndView head( HttpServletRequest request, HttpServletResponse response ,String id) {
		ModelAndView mv = new JModelAndView( "head.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		String type = CommUtil.null2String( request.getAttribute( "type" ) );
		mv.addObject( "type", type.equals( "" ) ? "goods" : type );
		if (!"".equals(id)&&null!=id) {
			User user = this.userService.getObjById(Long.valueOf(id));
			mv.addObject("user", user);
		}
		return mv;
	}

	@RequestMapping( { "/login_head.htm" } )
	public ModelAndView login_head( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "login_head.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		return mv;
	}

	@RequestMapping( { "/floor.htm" } )
	public ModelAndView floor( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "floor.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		Map params = new HashMap();
		params.put( "gf_display", Boolean.valueOf( true ) );
		List floors = this.goodsFloorService.query( "select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc", params, -1, -1 );
		mv.addObject( "floors", floors );
		mv.addObject( "gf_tools", this.gf_tools );
		mv.addObject( "url", CommUtil.getURL( request ) );
		return mv;
	}
	
	@RequestMapping({"/floor_ajax.htm"})
    public void floorAjax(HttpServletRequest request, HttpServletResponse response, Long id, Integer count) {
		Map params = new HashMap();
		params.put("gf_display", Boolean.valueOf(true));
		params.put("id", id);
		List floors = this.goodsFloorService.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null and obj.id=:id order by obj.gf_sequence asc", params, -1, -1);
		Map<String, Object> map = new HashMap<String, Object>();
		CommUtil.saveWebPaths(map,this.configService.getSysConfig(),request);
		String ret = showLoadFloorAjaxHtml(floors, count, CommUtil.getURL(request), map);
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

	@RequestMapping( { "/footer.htm" } )
	public ModelAndView footer( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "footer.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			mv = new JModelAndView( "wap/footer.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		}
		mv.addObject( "navTools", this.navTools );
		return mv;
	}
	private String generic_username(String userName)
	{
		String name = userName;
		User user = this.userService.getObjByProperty("userName", name);
		if (user != null) {
			for (int i = 1; i < 1000000; i++) {
				name = name + i;
				user = this.userService.getObjByProperty("userName", name);
				if (user == null) {
					break;
				}
			}
		}
		return name;
	}
	@RequestMapping( { "/index.htm" } )
	public ModelAndView index( HttpServletRequest request, HttpServletResponse response,String currentPage, String orderBy, String orderType, String gc_id, String gpr_id, String ga_id,String yqm) {

		ModelAndView mv = new JModelAndView( "index.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		//设置为PC访问
		request.getSession().setAttribute("shopping_view_type", "pc");

		User user2=this.userService.getObjById(CommUtil.null2Long(yqm));
		request.getSession().setAttribute("P_user",user2);
		Map params = new HashMap();
		/*params.put( "display", Boolean.valueOf( true ) );
		List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc", params, 0, 15 );
		mv.addObject( "gcs", gcs );
		params.clear();*/
		//限时秒杀

		// 推荐品牌
		params.put( "audit", Integer.valueOf( 1 ) );
		params.put( "recommend", Boolean.valueOf( true ) );
		params.put( "deleteStatus", Boolean.valueOf( false ) );
		List gbs = this.goodsBrandService.query( "select obj from GoodsBrand obj where obj.deleteStatus=:deleteStatus and obj.audit=:audit and obj.recommend=:recommend order by obj.sequence", params, 0, 16 );
		mv.addObject( "gbs", gbs );
		// 底部显示的合作伙伴
		params.clear();
		List img_partners = this.partnerService.query( "select obj from Partner obj where obj.image.id is not null order by obj.sequence asc", params, -1, -1 );
		mv.addObject( "img_partners", img_partners );
		List text_partners = this.partnerService.query( "select obj from Partner obj where obj.image.id is null order by obj.sequence asc", params, -1, -1 );
		mv.addObject( "text_partners", text_partners );

		//推荐分类
		params.clear();
		params.put( "display", Boolean.valueOf( true ) );
		params.put( "bc_location", "1" );
		//params.put( "recommend",Boolean.valueOf( true ));
		List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display and obj.bc_location=:bc_location order by obj.sequence asc", params, 0,11);
		List gc1=new ArrayList();
		List gc3=new ArrayList();
		List gc5=new ArrayList();
		List gc6=new ArrayList();
		for (Object gc:gcs) {
			//System.out.println(((GoodsClass)gc).getClassName());
			gc1=((GoodsClass)gc).getChilds();
			for (Object gc2:gc1) {
				gc6.add(gc2);
				gc3=((GoodsClass)gc2).getChilds();
				for (Object gc4:gc3) {
					if(((GoodsClass)gc4).isRecommend())
					gc5.add(gc4);
				}
			}
		}
		mv.addObject( "gc5", gc5);
		mv.addObject( "gc6", gc6);


		// 底部新闻分类显示
		params.clear();
		params.put( "mark", "news" );
		List acs = this.articleClassService.query( "select obj from ArticleClass obj where obj.parent.id is null and obj.mark!=:mark order by obj.sequence asc", params, 0, 9 );
		mv.addObject( "acs", acs );
		// 商城新闻
		params.clear();
		params.put( "class_mark", "news" );
		params.put( "display", Boolean.valueOf( true ) );
		List articles = this.articleService.query( "select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc", params, 0, 5 );
		mv.addObject( "articles", articles );
		// 查询推荐店铺商品
		params.clear();
		params.put( "store_recommend", Boolean.valueOf( true ) );
		params.put( "goods_status", Integer.valueOf( 0 ) );
		params.put( "deleteStatus", CommUtil.null2Boolean(false) );
		//params.put( "begin", Integer.valueOf(begin) );
		List store_reommend_goods_list = this.goodsService.query( "select obj from Goods obj where obj.deleteStatus=:deleteStatus and obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.store_recommend_time desc", params, 0, 8 );

		List store_reommend_goods = new ArrayList();
		int max = store_reommend_goods_list.size() >= 8 ? 7 : store_reommend_goods_list.size() - 1;
		for( int i = 0; i <= max; i++ ) {
			store_reommend_goods.add(store_reommend_goods_list.get( i ));
		}
		// 1、推荐商品可在后台编辑
		mv.addObject( "store_reommend_goods", store_reommend_goods );
		mv.addObject( "store_reommend_goods_count", Double.valueOf( Math.ceil( CommUtil.div( Integer.valueOf( store_reommend_goods_list.size() ), Integer.valueOf( 5 ) ) ) ) );
		mv.addObject( "goodsViewTools", this.goodsViewTools );
		mv.addObject( "storeViewTools", this.storeViewTools );
		if( SecurityUserHolder.getCurrentUser() != null ) {
			mv.addObject( "user", this.userService.getObjById( SecurityUserHolder.getCurrentUser().getId() ) );
		}
		// 团购查询
		/*params.clear();
		params.put( "beginTime", new Date() );
		params.put( "endTime", new Date() );
		List groups = this.groupService.query( "select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime", params, -1, -1 );
		if( groups.size() > 0 ) {
			// 2、团购商品
			params.clear();
			params.put( "gg_status", Integer.valueOf(1));
			//params.put( "gg_recommend", Integer.valueOf(1));
			params.put( "group_id", ((Group)groups.get(0)).getId());
			List ggs = this.groupGoodsService.query( "select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.group_id = :group_id order by obj.gg_recommend_time desc", params, 0, 5 );
			//if( ggs.size() > 0 )
			mv.addObject("ggs", ggs);
		}*/
		params.clear();
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("deleteStatus", CommUtil.null2Boolean(false));
		List groups = this.groupService.query("select obj from Group obj where obj.deleteStatus=:deleteStatus and obj.beginTime<=:beginTime and obj.endTime>=:endTime", params, -1, -1);
		if (groups.size() > 0) {
			GroupGoodsQueryObject ggqo = new GroupGoodsQueryObject(currentPage, mv, orderBy, orderType);
			ggqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
			ggqo.addQuery("obj.group.id", new SysMap("group_id", ((Group)groups.get(0)).getId()), "=");

			ggqo.addQuery("obj.gg_status", new SysMap("gg_status", Integer.valueOf(1)), "=");
			IPageList pList = this.groupGoodsService.list(ggqo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}
		// 3、天天特价
		/*params.clear();
		params.put( "bg_time", CommUtil.formatDate( CommUtil.formatShortDate( new Date() ) ) );
		params.put( "bg_status", Integer.valueOf( 1 ) );
		List bgs = this.bargainGoodsService.query( "select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status", params, 0, 5 );
		mv.addObject( "bgs", bgs );*/
		// 4、热销商品倒序-疯狂抢购

	/*	params.clear();
		params.put("goods_status", Integer.valueOf(0));
		List list = this.goodsService.query("select obj from Goods obj where obj.goods_status=:goods_status order by obj.goods_salenum desc", params, 0, 5);
		mv.addObject( "fengKuangs", list );*/
		params.clear();
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("deleteStatus", CommUtil.null2Boolean(false));
		List g = this.groupService.query("select obj from Group obj where obj.deleteStatus =:deleteStatus and obj.beginTime<=:beginTime and obj.endTime>=:endTime", params, -1, -1);
		if (g.size() > 0) {
			GroupGoodsQueryObject ggqo = new GroupGoodsQueryObject(currentPage, mv, orderBy, orderType);
			ggqo.addQuery("obj.group.id", new SysMap("group_id", ((Group)groups.get(0)).getId()), "=");
			ggqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
			if ((gc_id != null) && (!gc_id.equals(""))) {
				ggqo.addQuery("obj.gg_gc.id", new SysMap("gc_id", CommUtil.null2Long(gc_id)), "=");
			}
			if ((ga_id != null) && (!ga_id.equals(""))) {
				ggqo.addQuery("obj.gg_ga.id", new SysMap("ga_id", CommUtil.null2Long(ga_id)), "=");
				mv.addObject("ga_id", ga_id);
			}
			GroupPriceRange gpr = this.groupPriceRangeService.getObjById(CommUtil.null2Long(gpr_id));
			if (gpr != null) {
				ggqo.addQuery("obj.gg_price", new SysMap("begin_price", BigDecimal.valueOf(gpr.getGpr_begin())), ">=");
				ggqo.addQuery("obj.gg_price", new SysMap("end_price", BigDecimal.valueOf(gpr.getGpr_end())), "<=");
			}
			ggqo.addQuery("obj.gg_status", new SysMap("gg_status", Integer.valueOf(1)), "=");
			IPageList pList = this.groupGoodsService.list(ggqo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			//List gcs = this.groupClassService.query("select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc", null, -1, -1);
			List gprs = this.groupPriceRangeService.query("select obj from GroupPriceRange obj order by obj.gpr_begin asc", null, -1, -1);
			mv.addObject("gprs", gprs);
			//mv.addObject("gcs", gcs);
			mv.addObject("group", groups.get(0));
			if ((orderBy == null) || (orderBy.equals(""))) {
				orderBy = "addTime";
			}
			if ((orderType == null) || (orderType.equals(""))) {
				orderType = "desc";
			}
			mv.addObject("order_type", CommUtil.null2String(orderBy) + "_" + CommUtil.null2String(orderType));
			mv.addObject("gc_id", gc_id);
			mv.addObject("gpr_id", gpr_id);
		}

		// 5、随机生成推荐商品 猜您喜欢
	/*	List store_guess_goods = new ArrayList();
		Random rand = new Random();
		int recommend_goods_random = rand.nextInt(5);
		int begin = recommend_goods_random * 5;
		if( begin > store_reommend_goods_list.size() - 1 ) {
			begin = 0;
		}
		int maxsize = begin + 5;
		if( maxsize > store_reommend_goods_list.size() ) {
			begin -= maxsize - store_reommend_goods_list.size();
			maxsize--;
		}
		for( int i = 0; i < store_reommend_goods_list.size(); i++ ) {
			if( (i >= begin) && (i < maxsize) ) {
				store_guess_goods.add(store_reommend_goods_list.get( i ));
			}
		}
		mv.addObject( "cais", store_guess_goods );*/
		// 6、满送商品
		/*params.clear();
		params.put( "d_status", Integer.valueOf( 1 ) );
		params.put( "d_begin_time", new Date() );
		params.put( "d_end_time", new Date() );
		List dgs = this.deliveryGoodsService.query( "select obj from DeliveryGoods obj where obj.d_status=:d_status and obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time order by obj.d_audit_time desc", params, 0, 5 );
		mv.addObject( "dgs", dgs );*/

		// 7、新品上架
		/*params.clear();
		params.put( "goods_status", Integer.valueOf( 0 ) );
		List new_goods_list = this.goodsService.query( "select obj from Goods obj where obj.goods_status=:goods_status order by obj.addTime desc", params, 0, 5 );
		mv.addObject( "xinShangs", new_goods_list );*/

		// 8、点击数最多:人气商品
		/*params.clear();
		params.put( "goods_status", Integer.valueOf( 0 ) );
		List hot_goods_list = this.goodsService.query( "select obj from Goods obj where obj.goods_status=:goods_status order by obj.goods_click desc", params, 0, 5 );
		mv.addObject( "hots", hot_goods_list );*/

		return mv;
	}

	@RequestMapping( { "/close.htm" } )
	public ModelAndView wapclose( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "close.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			mv = new JModelAndView( "wap/close.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		}
		return mv;
	}

	@RequestMapping( { "/404.htm" } )
	public ModelAndView error404( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "404.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			//String store_id = CommUtil.null2String( request.getSession( false ).getAttribute( "store_id" ) );
			mv = new JModelAndView( "wap/404.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
			mv.addObject( "url", CommUtil.getURL( request ) + "/wap/index.htm");
		}

		return mv;
	}

	@RequestMapping( { "/500.htm" } )
	public ModelAndView error500( HttpServletRequest request, HttpServletResponse response ) {

		ModelAndView mv = new JModelAndView( "500.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		String shopping_view_type = CommUtil.null2String( request.getSession( false ).getAttribute( "shopping_view_type" ) );

			if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			String store_id = CommUtil.null2String( request.getSession( false ).getAttribute( "store_id" ) );
			mv = new JModelAndView( "wap/500.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
			mv.addObject( "url", CommUtil.getURL( request ) + "/wap/index.htm?store_id=" + store_id );
		}

		return mv;
	}

	@RequestMapping( { "/goods_class.htm" } )
	public ModelAndView goods_class( HttpServletRequest request, HttpServletResponse response ,String bc_location) {
		ModelAndView mv = new JModelAndView( "goods_class.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );

		String shopping_view_type = CommUtil.null2String( request.getSession( false ).getAttribute( "shopping_view_type" ) );
		if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			mv = new JModelAndView( "wap/goods_class.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		}
		Map params = new HashMap();
		params.put( "display", Boolean.valueOf( true ) );
		params.put("bc_location",bc_location);
		params.put("deleteStatus",CommUtil.null2Boolean(false));
		List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.deleteStatus =:deleteStatus and obj.parent.id is null and obj.display=:display and obj.bc_location=:bc_location order by obj.sequence asc", params, -1, -1 );
		mv.addObject( "gcs", gcs );
		mv.addObject("bc_location",bc_location);
		return mv;
	}

	@RequestMapping( { "/goodsclass.htm" } )
	public ModelAndView goodsclass( HttpServletRequest request, HttpServletResponse response  ,int bc_location) {

		ModelAndView mv = new JModelAndView( "goodsclass.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
		String shopping_view_type = CommUtil.null2String( request.getSession( false ).getAttribute( "shopping_view_type" ) );

		if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			mv = new JModelAndView( "newwap/Commodityclass.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		}
		Map params = new HashMap();
		
		params.put( "bc_location", bc_location );
		List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location =:bc_location order by obj.sequence asc", params,-1, -1 );
		mv.addObject( "gcs", gcs );

        return mv;
	}
	
	@RequestMapping( { "/forget.htm" } )
	public ModelAndView forget( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "Forget_N.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		/*SysConfig config = this.configService.getSysConfig();
		if( !config.isEmailEnable() ) {
			mv = new JModelAndView( "error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
			mv.addObject( "op_title", "系统关闭邮件功能，不能找回密码" );
			mv.addObject( "url", CommUtil.getURL( request ) + "/index.htm" );
		}*/
		return mv;
	}
	@RequestMapping({ "/resetPassword.htm" })
	public ModelAndView resetPassword(HttpServletRequest request, HttpServletResponse response, String userName,String password,String yzm,String sendYzm){
		String a[]=userName.split("@");
		List users;
		ModelAndView mv;
		String op_title;
		if (a.length==2) {
			Map params = new HashMap();
			params.put("email", userName);
			users = this.userService.query("select obj from User obj where obj.email=:email", params, -1, -1);
		}else{
			Map params = new HashMap();
			params.put("telephone", userName);
			users = this.userService.query("select obj from User obj where obj.telephone=:telephone", params, -1, -1);
		}
		if (users!=null&&users.size()>0) {
			User user=(User)users.get(0);
			if (yzm.equals(sendYzm)) {
				user.setPassword( Md5Encrypt.md5(password));
				this.userService.update(user);
				 mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
				op_title="重置密码成功";
				mv.addObject( "url", CommUtil.getURL( request ) + "/user/login.htm" );
			}else{
				mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
				op_title="验证码不对";
				mv.addObject( "url", CommUtil.getURL( request ) + "/forget.htm" );
			}
		}else {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
			op_title="该手机号/邮箱没有对应用户";
			mv.addObject( "url", CommUtil.getURL( request ) + "/forget.htm" );
		}
		mv.addObject( "op_title", op_title);
		return mv;
	}

	@RequestMapping({ "/sms.htm" })
	public void sms(HttpServletRequest request, HttpServletResponse response, String userName) {
		String yzm=(int)((Math.random()*9+1)*100000)+"";
		String a[]=userName.split("@");
		if (a.length==2) {
				String email=userName;
				String subject="颐佳商城重置密码";
				String content="颐佳商城重置密码验证码为:"+yzm;
			this.msgTools.sendEmail(email, subject, content);
		}else{
			String sign="%e9%a2%90%e4%bd%b3%e6%98%93%e8%b4%ad";
			String Msg="%e6%82%a8%e7%9a%84%e9%87%8d%e7%bd%ae%e5%af%86%e7%a0%81%e9%aa%8c%e8%af%81%e7%a0%81%e4%b8%ba%3a"+yzm;
			String sr= HttpRequest.sendPost("http://manager.wxtxsms.cn/smsport/sendPost.aspx", "uid=haoxing&upsd="+ Str2MD5.MD532("00000000")+"&sendtele="+userName+"&Msg="+Msg+"&sign="+sign);
		}
		Map<String,String> map = new HashMap<>();
		map.put("yzm",yzm);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping( { "/find_pws.htm" } )
	public ModelAndView find_pws( HttpServletRequest request, HttpServletResponse response, String userName, String email, String code ) {
		ModelAndView mv = new JModelAndView( "success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		HttpSession session = request.getSession( false );
		String verify_code = (String)session.getAttribute( "verify_code" );
		if( code.toUpperCase().equals( verify_code ) ) {
			User user = this.userService.getObjByProperty( "userName", userName );
			if( user.getEmail().equals( email.trim() ) ) {
				String pws = CommUtil.randomString( 6 ).toLowerCase();
				String subject = this.configService.getSysConfig().getTitle() + "密码找回邮件";
				String content = user.getUsername() + ",您好！您通过密码找回功能重置密码，新密码为：" + pws;
				boolean ret = this.msgTools.sendEmail( email, subject, content );
				if( ret ) {
					user.setPassword( Md5Encrypt.md5( pws ) );
					this.userService.update( user );
					mv.addObject( "op_title", "新密码已经发送到邮箱:<font color=red>" + email + "</font>，请查收后重新登录" );
					mv.addObject( "url", CommUtil.getURL( request ) + "/user/login.htm" );
				}
				else {
					mv = new JModelAndView( "error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
					mv.addObject( "op_title", "邮件发送失败，密码暂未执行重置" );
					mv.addObject( "url", CommUtil.getURL( request ) + "/forget.htm" );
				}
			}
			else {
				mv = new JModelAndView( "error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
				mv.addObject( "op_title", "用户名、邮箱不匹配" );
				mv.addObject( "url", CommUtil.getURL( request ) + "/forget.htm" );
			}
		}
		else {
			mv = new JModelAndView( "error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
			mv.addObject( "op_title", "验证码不正确" );
			mv.addObject( "url", CommUtil.getURL( request ) + "/forget.htm" );
		}
		return mv;
	}


	@RequestMapping( { "/outline.htm" } )
	public ModelAndView outline( HttpServletRequest request, HttpServletResponse response ) {
		ModelAndView mv = new JModelAndView( "error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		mv.addObject( "op_title", "该用户在其他地点登录，您被迫下线！" );
		mv.addObject( "url", CommUtil.getURL( request ) + "/index.htm" );
		String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			mv = new JModelAndView( "wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
			mv.addObject( "url", CommUtil.getURL( request ) + "/wap/index.htm" );
		}
		return mv;
	}
	/*
	* 首页ajax加载10次
	* */
	@RequestMapping( { "/wap/index_ajax.htm" } )
	public ModelAndView wapindex_ajax( HttpServletRequest request, HttpServletResponse response,String currentPage, String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("newwap/0_index_ajax.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
//平台推荐的商品取一个开始
		GoodsQueryObject gqo=new GoodsQueryObject(currentPage, mv, "addTime", "desc");
		gqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", CommUtil.null2Int(0)), "=");
		gqo.addQuery("obj.store_recommend", new SysMap("store_recommend", CommUtil.null2Boolean(true)), "=");
		gqo.setPageSize(1);
		IPageList pList = this.goodsService.list(gqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("GoodsViewTools",goodsViewTools);
//平台推荐的商品取一个结束
//根据最近搜索商品查出九个开始
		Map params =new HashMap();
		List listgc=new ArrayList();
		Cookie[] cookiesss= request.getCookies();
		if(null!=cookiesss){
			for(Cookie cookie:cookiesss){
				if("keyword".equals(cookie.getName())){
					//System.out.println("aaa="+cookie.getValue());
					String cookiesString=cookie.getValue();
					String[] cookiess=cookiesString.split("/");
					int chang=cookiess.length;
					if(chang>=5)chang=5;else chang=cookiess.length;
					for (int i=0;i<chang;i++) {
						listgc.add(cookiess[i]);//获取5个最近搜索的关键词
					}
					//获取一个0——5的随机数字
					int aa=listgc.size();
					int max=4;
					if(aa>5)max=4;else max=listgc.size();
					int min=0;
					Random random = new Random();
					int s = random.nextInt(max)%(max-min+1) + min;
					String  keywo="";
					if(null!=listgc){
						keywo=(String) listgc.get(s);
					}
					params.clear();
					params.put( "goods_name", "%"+CommUtil.null2String(keywo)+"%" );
					params.put( "className", "%"+CommUtil.null2String(keywo)+"%" );
					params.put( "goods_status", Integer.valueOf( 0 ) );
					List goodskeyw = this.goodsService.query( "select obj from Goods obj where obj.goods_name like :goods_name or obj.gc.className like :className and obj.goods_status=:goods_status order by obj.addTime desc", params, 0, 9 );
					mv.addObject("goodskeyw",goodskeyw);

				}
			}
		}
//根据最近搜索商品查出九个结束


	return mv;
	}

	@RequestMapping( { "/wap/wap_navv.htm" } )
	public ModelAndView wap_nav( HttpServletRequest request, HttpServletResponse response,String currentPage, String orderBy, String orderType, String gc_id, String gpr_id, String ga_id ,String yqm) {
		ModelAndView mv = new JModelAndView("newwap/0wap_nav.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

		return mv;
	}


	
	/** wap首页业务逻辑begin */
	
	/**
	 * wap首页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping( { "/wap/index.htm" } )
	public ModelAndView wapindex( HttpServletRequest request, HttpServletResponse response,String currentPage, String orderBy, String orderType, String gc_id, String gpr_id, String ga_id ,String yqm) {
		ModelAndView mv = new JModelAndView( "wap/index.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response );
		//设置为wap访问
		request.getSession().setAttribute("shopping_view_type", "wap");
		User user2=this.userService.getObjById(CommUtil.null2Long(yqm));
		request.getSession().setAttribute("P_user",user2);
		mv.addObject("GoodsViewTools",goodsViewTools);
		Map params = new HashMap();
		params.put( "display", Boolean.valueOf( true ) );
		List gcs = this.goodsClassService.query( "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc", params, 0, 15 );
		mv.addObject( "gcs", gcs );
		params.clear();
		params.put( "audit", Integer.valueOf( 1 ) );
		params.put( "recommend", Boolean.valueOf( true ) );
		params.put( "deleteStatus", Boolean.valueOf( false ) );
		List gbs = this.goodsBrandService.query( "select obj from GoodsBrand obj where obj.deleteStatus=:deleteStatus and obj.audit=:audit and obj.recommend=:recommend order by obj.sequence", params, -1, -1 );
		mv.addObject( "gbs", gbs );
		// z商城新闻开始
		params.clear();
		params.put( "class_mark", "news" );
		params.put( "display", Boolean.valueOf( true ) );
		List articles = this.articleService.query( "select obj from Article obj where obj.articleClass.mark=:class_mark and obj.display=:display order by obj.addTime desc", params, 0, 5 );
		mv.addObject( "articles", articles );
		// z商城新闻结束
		//wap首页导航开始
		mv.addObject( "navTools", this.navTools );
		//wap首页导航结束

		params.clear();
		List img_partners = this.partnerService.query( "select obj from Partner obj where obj.image.id is not null order by obj.sequence asc", params, -1, -1 );
		mv.addObject( "img_partners", img_partners );
		List text_partners = this.partnerService.query( "select obj from Partner obj where obj.image.id is null order by obj.sequence asc", params, -1, -1 );
		mv.addObject( "text_partners", text_partners );
		params.clear();
		params.put( "mark", "news" );
		List acs = this.articleClassService.query( "select obj from ArticleClass obj where obj.parent.id is null and obj.mark!=:mark order by obj.sequence asc", params, 0, 9 );
		mv.addObject( "acs", acs );
//平台推荐的商品取一个开始
		params.clear();
		params.put( "store_recommend", Boolean.valueOf( true ) );
		params.put( "goods_status", Integer.valueOf( 0 ) );
		List store_reommend_goods_list = this.goodsService.query( "select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.store_recommend_time desc", params, 0, 1 );
		List store_reommend_goods = new ArrayList();
		//int max = store_reommend_goods_list.size() >= 21 ? 20 : store_reommend_goods_list.size() - 1;
		int max = store_reommend_goods_list.size() - 1;
		for( int i = 0; i <= max; i++ ) {
			store_reommend_goods.add(store_reommend_goods_list.get( i ));
		}
		mv.addObject( "store_reommend_goods", store_reommend_goods );

		mv.addObject( "store_reommend_goods_count", Double.valueOf( Math.ceil( CommUtil.div( Integer.valueOf( store_reommend_goods_list.size() ), Integer.valueOf( 5 ) ) ) ) );
		mv.addObject( "goodsViewTools", this.goodsViewTools );
		mv.addObject( "storeViewTools", this.storeViewTools );
		if( SecurityUserHolder.getCurrentUser() != null ) {
			mv.addObject( "user", this.userService.getObjById( SecurityUserHolder.getCurrentUser().getId() ) );
		}
		params.clear();
//平台推荐的商品取一个结束
//根据最近搜索商品查出九个开始
		List listgc=new ArrayList();
		Cookie[] cookiesss= request.getCookies();
		if(null!=cookiesss){
		for(Cookie cookie:cookiesss){
			if("keyword".equals(cookie.getName())){
				//System.out.println("aaa="+cookie.getValue());
				String cookiesString=cookie.getValue();
				String[] cookiess=cookiesString.split("/");
				int chang=cookiess.length;
				if(chang>=5)chang=5;else chang=cookiess.length;
				for (int i=0;i<chang;i++) {
					listgc.add(cookiess[i]);//获取5个最近搜索的关键词
				}
				String  keywo=(String) listgc.get(0);
				params.clear();
				params.put( "goods_name", "%"+CommUtil.null2String(keywo)+"%" );
				params.put( "className", "%"+CommUtil.null2String(keywo)+"%" );
				params.put( "goods_status", Integer.valueOf( 0 ) );
				List goodskeyw = this.goodsService.query( "select obj from Goods obj where obj.goods_name like :goods_name or obj.gc.className like :className and obj.goods_status=:goods_status order by obj.addTime desc", params, 0, 9 );
				mv.addObject("goodskeyw",goodskeyw);
			}
		}
		}
//根据最近搜索商品查出九个结束
		//开始限时秒杀

		params.clear();
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("deleteStatus", CommUtil.null2Boolean(false));
		List groups = this.groupService.query("select obj from Group obj where obj.deleteStatus=:deleteStatus and obj.beginTime<=:beginTime and obj.endTime>=:endTime", params, -1, -1);
		if (groups.size() > 0) {
			GroupGoodsQueryObject ggqo = new GroupGoodsQueryObject(currentPage, mv, orderBy, orderType);

			ggqo.addQuery("obj.group.id", new SysMap("group_id", ((Group)groups.get(0)).getId()), "=");
			ggqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", CommUtil.null2Boolean(false)), "=");
			if ((gc_id != null) && (!gc_id.equals(""))) {
				ggqo.addQuery("obj.gg_gc.id", new SysMap("gc_id", CommUtil.null2Long(gc_id)), "=");
			}
			if ((ga_id != null) && (!ga_id.equals(""))) {
				ggqo.addQuery("obj.gg_ga.id", new SysMap("ga_id", CommUtil.null2Long(ga_id)), "=");
				mv.addObject("ga_id", ga_id);
			}
			GroupPriceRange gpr = this.groupPriceRangeService.getObjById(CommUtil.null2Long(gpr_id));
			if (gpr != null) {
				ggqo.addQuery("obj.gg_price", new SysMap("begin_price", BigDecimal.valueOf(gpr.getGpr_begin())), ">=");
				ggqo.addQuery("obj.gg_price", new SysMap("end_price", BigDecimal.valueOf(gpr.getGpr_end())), "<=");
			}
			ggqo.addQuery("obj.gg_status", new SysMap("gg_status", Integer.valueOf(1)), "=");
			IPageList pList = this.groupGoodsService.list(ggqo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			//List gcs = this.groupClassService.query("select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc", null, -1, -1);
			List gprs = this.groupPriceRangeService.query("select obj from GroupPriceRange obj order by obj.gpr_begin asc", null, -1, -1);
			mv.addObject("gprs", gprs);
			//mv.addObject("gcs", gcs);
			mv.addObject("group", groups.get(0));
			if ((orderBy == null) || (orderBy.equals(""))) {
				orderBy = "addTime";
			}
			if ((orderType == null) || (orderType.equals(""))) {
				orderType = "desc";
			}
			mv.addObject("order_type", CommUtil.null2String(orderBy) + "_" + CommUtil.null2String(orderType));
			mv.addObject("gc_id", gc_id);
			mv.addObject("gpr_id", gpr_id);
		}


		// zzy新增 随机生成推荐商品 猜您喜欢
		/*List store_guess_goods = new ArrayList();
		Random rand = new Random();
		int recommend_goods_random = rand.nextInt(5);
		int begin = recommend_goods_random * 5;
		if( begin > store_reommend_goods_list.size() - 1 ) {
			begin = 0;
		}
		int maxsize = begin + 5;
		if( maxsize > store_reommend_goods_list.size() ) {
			begin -= maxsize - store_reommend_goods_list.size();
			maxsize--;
		}
		for( int i = 0; i < store_reommend_goods_list.size(); i++ ) {
			if( (i >= begin) && (i < maxsize) ) {
				store_guess_goods.add(store_reommend_goods_list.get( i ));
			}
		}
		mv.addObject( "cais", store_guess_goods );*/


		params.clear();
		params.put( "bg_time", CommUtil.formatDate( CommUtil.formatShortDate( new Date() ) ) );
		params.put( "bg_status", Integer.valueOf( 1 ) );
		List bgs = this.bargainGoodsService.query( "select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status", params, 0, 5 );
		mv.addObject( "bgs", bgs );
		params.clear();
		params.put( "d_status", Integer.valueOf( 1 ) );
		params.put( "d_begin_time", new Date() );
		params.put( "d_end_time", new Date() );
		List dgs = this.deliveryGoodsService.query( "select obj from DeliveryGoods obj where obj.d_status=:d_status and obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time order by obj.d_audit_time desc", params, 0, 3 );
		mv.addObject( "dgs", dgs );
		
		List msgs = new ArrayList();
		if( SecurityUserHolder.getCurrentUser() != null ) {
			params.clear();
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
		params.clear();
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
						//gc.setSc( sc );
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


	 public String showLoadFloorAjaxHtml(List lists, int i, String url, Map<String, Object> map) {

		   String img = null;
		   String loadimg = map.get("imageWebServer")+"/resources/style/common/images/loader.gif";
		   String errorimg = map.get("webPath")+"/"+map.get("goodsImagePath")+"/"+map.get("goodsImageName");
		   String goods_url = null;
		   String goods_class_url = null;
		   String child_goods_class_url = null;
				   
		   GoodsFloor floor = (GoodsFloor)lists.get(0);
				   
		   img = null;
		   
		   StringBuffer sb = new StringBuffer(1000);
		   sb.append("<div class='floor "+floor.getGf_css()+"'>").append("<div class='floor_box' id='floor_"+i+"'>");
		   sb.append("<div class='floor_menu'>").append("<div class='title'>").append("<div class='txt-type'>").append("<span>").append(i).append("</span>");
		   sb.append("<h2 title='").append(floor.getGf_name()).append("'>").append(floor.getGf_name()).append("</h2></div></div><div class='flr_m_details'><ul class='flr_m_du'>");
		   List<GoodsClass> gcs = this.gf_tools.generic_gf_gc(floor.getGf_gc_list());
		   for(GoodsClass gc : gcs){
			   goods_class_url = map.get("webPath")+"/store_goods_list_"+gc.getId()+".htm";
			   sb.append("<li><h4><a href='").append(goods_class_url).append("'>").append(gc.getClassName()).append("</a></h4><p>");
			   for(GoodsClass c_gc : gc.getChilds()){
				   child_goods_class_url = map.get("webPath")+"/store_goods_list_"+c_gc.getId()+".htm";
				   sb.append("<span><a href='").append(child_goods_class_url).append("' target='_blank'>").append(c_gc.getClassName()).append("</a></span>");
			   }
			   sb.append("</p></li>");
		   }
		   sb.append("</ul><div class='flr_advertisment'>");
		   //拼接左侧广告
		   sb.append(gf_tools.generic_adv(url,floor.getGf_left_adv()));
		   
		   sb.append("</div></div></div><div class='floorclass'><ul class='floorul'>");
		   
		   int num = 0;
		   for(GoodsFloor info : floor.getChilds()){
			   num++;
			   sb.append("<li ");
			   if(num==1){
				   sb.append("class='this'");
			   }
			   sb.append("style='cursor:pointer;' id='").append(info.getId()).append("' store_gc='").append(floor.getId()).append("' >");
			   sb.append(info.getGf_name()).append("<s></s></li>");
		   }
		   sb.append("</ul>");
		   
		   int count = 0;
		   
		   for(GoodsFloor info : floor.getChilds()){
			   
			   count++;
			   sb.append("<div id='").append(info.getId()).append("' store_gc='").append(floor.getId()).append("' class='ftab'");
			   if(count>1){
				   sb.append("style='display:none;'");
			   }
			   sb.append("><div class='ftabone'><div class='classpro'>");
			   for(Goods goods : this.gf_tools.generic_goods(info.getGf_gc_goods())){
				   if(goods!=null){
					   if(goods.getGoods_main_photo()!=null)
				           img=map.get("imageWebServer")+"/"+goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName()+"_small."+goods.getGoods_main_photo().getExt();
				       else
				    	   img=map.get("imageWebServer")+"/"+map.get("goodsImagePath")+"/"+map.get("goodsImageName");
				       
				       goods_url=map.get("webPath")+"/goods_"+goods.getId()+".htm";
				       
				       if((Boolean)map.get("IsSecondDomainOpen")) {
				    	   goods_url="http://"+goods.getGoods_store().getStore_second_domain()+"."+map.get("domainPath")+"/goods_"+goods.getId()+".htm";
				       }
				       sb.append("<div class='productone'><ul class='this'><li><span class='center_span'>");
				       //sb.append("<p><a href='").append(goods_url).append("' target='_blank' ><img src='").append(loadimg).append("' original='");
				       //sb.append(img).append("' onerror=\"this.src=").append(errorimg).append(";\" width='28' height='28'/></a></p></span></li>");
				       sb.append("<p><a href='").append(goods_url).append("' target='_blank' ><img src='").append(img).append("' original='");
				       sb.append(img).append("' onerror=\"this.src=").append(errorimg).append(";\" /></a></p></span></li>");
				       sb.append("<li class='pronames'><a href='").append(goods_url).append("' target='_blank'>").append(goods.getGoods_name()).append("</a></li>");
				       sb.append("<li><span class=\"hui2\">市场价：</span><span class=\"through hui\">¥").append(goods.getGoods_price());
				       sb.append("</span></li><li><span class=\"hui2\">商城价：</span><strong class=\"red\">¥").append(goods.getGoods_current_price());
				       sb.append("</strong></li></ul></div>");
				   }
			   }
			   sb.append("</div></div></div>");
		   }
		   sb.append("</div><div class='ranking'>");
		   Map<String, Object> mmap = gf_tools.generic_goods_list(floor.getGf_list_goods());
		   sb.append("<h1>").append(mmap.get("list_title")).append("</h1>");
		   
		   if(mmap.get("goods1")!=null){
			   Goods goods=(Goods)mmap.get("goods1");
			   if(goods.getGoods_main_photo()!=null)
		           img=map.get("imageWebServer")+"/"+goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName()+"_small."+goods.getGoods_main_photo().getExt();
		       else
		    	   img=map.get("imageWebServer")+"/"+map.get("goodsImagePath")+"/"+map.get("goodsImageName");
			   
			   goods_url=map.get("webPath")+"/goods_"+goods.getId()+".htm";
			   
			   sb.append("<ul class=\"rankul\"><li class=\"rankimg\"><a href='").append(goods_url).append("' target=\"_blank\">");
			   //sb.append("<img src='").append(loadimg).append("' original='").append(img).append("' onerror=\"this.src='").append(errorimg).append("';\" width='28' height='28'/>");
			   sb.append("<img src='").append(img).append("' original='").append(img).append("' onerror=\"this.src='").append(errorimg).append("';\"  width='73' height='73'/>");
			   sb.append("</a><span class=\"rankno1\"></span></li><li class=\"rankhui\"><strong><a href='").append(goods_url).append("' target=\"_blank\">");
			   sb.append(CommUtil.substring(goods.getGoods_name(),12)).append("</a></strong></li><li class=\"rankmoney\">¥").append(goods.getGoods_current_price());
			   sb.append("</li></ul>");
		   }
		   
		   if(mmap.get("goods2")!=null){
			   Goods goods=(Goods)mmap.get("goods2");
			   if(goods.getGoods_main_photo()!=null)
		           img=map.get("imageWebServer")+"/"+goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName()+"_small."+goods.getGoods_main_photo().getExt();
		       else
		    	   img=map.get("imageWebServer")+"/"+map.get("goodsImagePath")+"/"+map.get("goodsImageName");
			   
			   goods_url=map.get("webPath")+"/goods_"+goods.getId()+".htm";
			   
			   sb.append("<ul class=\"rankul\"><li class=\"rankimg\"><a href='").append(goods_url).append("' target=\"_blank\">");
			 //sb.append("<img src='").append(loadimg).append("' original='").append(img).append("' onerror=\"this.src='").append(errorimg).append("';\" width='28' height='28'/>");
			   sb.append("<img src='").append(img).append("' original='").append(img).append("' onerror=\"this.src='").append(errorimg).append("';\"  width='73' height='73'/>");
			   sb.append("</a><span class=\"rankno1\"></span></li><li class=\"rankhui\"><strong><a href='").append(goods_url).append("' target=\"_blank\">");
			   sb.append(CommUtil.substring(goods.getGoods_name(),12)).append("</a></strong></li><li class=\"rankmoney\">¥").append(goods.getGoods_current_price());
			   sb.append("</li></ul>");
		   }
		   
		   if(mmap.get("goods3")!=null){
			   Goods goods=(Goods)mmap.get("goods3");
			   if(goods.getGoods_main_photo()!=null)
		           img=map.get("imageWebServer")+"/"+goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName()+"_small."+goods.getGoods_main_photo().getExt();
		       else
		    	   img=map.get("imageWebServer")+"/"+map.get("goodsImagePath")+"/"+map.get("goodsImageName");
			   
			   goods_url=map.get("webPath")+"/goods_"+goods.getId()+".htm";
			   
			   sb.append("<ul class=\"rankul\"><li class=\"rankimg\"><a href='").append(goods_url).append("' target=\"_blank\">");
			 //sb.append("<img src='").append(loadimg).append("' original='").append(img).append("' onerror=\"this.src='").append(errorimg).append("';\" width='28' height='28'/>");
			   sb.append("<img src='").append(img).append("' original='").append(img).append("' onerror=\"this.src='").append(errorimg).append("';\"  width='73' height='73'/>");
			   sb.append("</a><span class=\"rankno1\"></span></li><li class=\"rankhui\"><strong><a href='").append(goods_url).append("' target=\"_blank\">");
			   sb.append(CommUtil.substring(goods.getGoods_name(),12)).append("</a></strong></li><li class=\"rankmoney\">¥").append(goods.getGoods_current_price());
			   sb.append("</li></ul>");
		   }
		   
		   sb.append("<ul class=\"rankul2\">");
		   if(mmap.get("goods4")!=null){
			   Goods goods=(Goods)mmap.get("goods4");
			   goods_url=map.get("webPath")+"/goods_"+goods.getId()+".htm";
			   sb.append("<li><a href='").append(goods_url).append("' target='_blank'>").append(CommUtil.substring(goods.getGoods_name(),14)).append("</a></li>");
		   }
		   if(mmap.get("goods5")!=null){
			   Goods goods=(Goods)mmap.get("goods5");
			   goods_url=map.get("webPath")+"/goods_"+goods.getId()+".htm";
			   sb.append("<li><a href='").append(goods_url).append("' target='_blank'>").append(CommUtil.substring(goods.getGoods_name(),14)).append("</a></li>");
		   }
		   if(mmap.get("goods6")!=null){
			   Goods goods=(Goods)mmap.get("goods6");
			   goods_url=map.get("webPath")+"/goods_"+goods.getId()+".htm";
			   sb.append("<li><a href='").append(goods_url).append("' target='_blank'>").append(CommUtil.substring(goods.getGoods_name(),14)).append("</a></li>");
		   }
		   sb.append("</ul><div class=\"rank_advertisment\">");
		   //拼接右侧广告
		   sb.append(this.gf_tools.generic_adv(url, floor.getGf_right_adv()));
		   sb.append("</div></div></div><div class=\"floor_brand\"><span class=\"fl_brand_sp\"></span><span class=\"flr_sp_brand\">");
		   
		   for(GoodsBrand brand : this.gf_tools.generic_brand(floor.getGf_brand_list())){
			   String brand_url = map.get("webPath")+"/brand_goods_"+brand.getId()+".htm";
			   String brand_img = map.get("imageWebServer") + "/" + brand.getBrandLogo().getPath() + "/" + brand.getBrandLogo().getName();
			   sb.append("<a href='").append(brand_url).append("' target='_blank'><img src='").append(brand_img).append("' width='98' height='35' /></a>");
		   }
		   sb.append("</span></div></div>");
	 
	     return sb.toString();
	   }
	   
	
	
	
}
