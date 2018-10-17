 package com.shopping.manage.buyer.action;
 
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 import org.nutz.json.Json;
 import org.nutz.json.JsonFormat;
 import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.ResponseBody;
 import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.annotation.SecurityMapping;
import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.WebForm;
import com.shopping.core.tools.database.DatabaseTools;
import com.shopping.foundation.domain.Address;
import com.shopping.foundation.domain.Area;
import com.shopping.foundation.domain.query.AddressQueryObject;
import com.shopping.foundation.service.IAddressService;
import com.shopping.foundation.service.IAreaService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.IUserConfigService;
 
 @Controller
 public class AddressBuyerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IAddressService addressService;
 
   @Autowired
   private IAreaService areaService;
 
   @Autowired
   private DatabaseTools databaseTools;
 
   @SecurityMapping(display = false, rsequence = 0, title="收货地址列表", value="/buyer/address.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/address.htm"})
   public ModelAndView address(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType,String ids,String communt,String che,String type)
   {
     ModelAndView mv = new JModelAndView("user/second/51Buyer_Address.html", this.configService.getSysConfig(),
       this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession( false ).getAttribute( "shopping_view_type" ) );
	 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
		 mv = new JModelAndView("newwap/62dizhiguanli.html", this.configService.getSysConfig(),
			       this.userConfigService.getUserConfig(), 1, request, response);
	 }
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     AddressQueryObject qo = new AddressQueryObject(currentPage, mv, orderBy, orderType);
     qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
       qo.addQuery("obj.type",new SysMap("type",CommUtil.null2Int(0)),"=");
       qo.addQuery("obj.deleteStatus",new SysMap("deleteStatus",CommUtil.null2Boolean(false)),"=");
     IPageList pList = this.addressService.list(qo);
     CommUtil.saveIPageList2ModelAndView(url + "/buyer/address.htm", "", params, pList, mv);
     List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
     mv.addObject("areas", areas);
       mv.addObject("ids",ids);
       mv.addObject("communt",communt);
       mv.addObject("che",che);
       mv.addObject("type",type);
     return mv;
   }
    /**
     * 新增收货地址页面
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
   @SecurityMapping(display = false, rsequence = 0, title="新增收货地址", value="/buyer/address_add.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/address_add.htm"})
   public ModelAndView address_add(HttpServletRequest request, HttpServletResponse response, String currentPage,String ids,String communt,String che) {
     ModelAndView mv = new JModelAndView("user/default/usercenter/address_add.html", this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
	 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
		 mv = new JModelAndView("newwap/addressadd.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
	 }
     List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
     mv.addObject("areas", areas);
     mv.addObject("currentPage", currentPage);
       mv.addObject("ids",ids);
       mv.addObject("communt",communt);
       mv.addObject("che",che);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="编辑收货地址", value="/buyer/address_edit.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/address_edit.htm"})
   public ModelAndView address_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView("user/default/usercenter/address_add.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );

	 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
		 mv = new JModelAndView("newwap/addressadd.html", this.configService.getSysConfig(),
			       this.userConfigService.getUserConfig(), 1, request, response);
	 }
     List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
     Address obj = this.addressService.getObjById(CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     mv.addObject("areas", areas);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="收货地址保存", value="/buyer/address_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/address_save.htm"})
   public String address_save(HttpServletRequest request, HttpServletResponse response, String id, String area_id, String currentPage,String ids,String communt,String che)
   {
       Map map= new HashMap();
       map.put("user_id",SecurityUserHolder.getCurrentUser().getId());
       List<Address> list=this.addressService.query("select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc",map,-1,-1);
       for (Address addr:list) {
           addr.setIsdefaut(0);
           this.addressService.update(addr);
       }
     WebForm wf = new WebForm();
     Address address = null;
     if (id.equals("")) {
       address = wf.toPo(request, Address.class);
       address.setAddTime(new Date());
         address.setType(CommUtil.null2Int(0));

     } else {
       Address obj = this.addressService.getObjById(Long.valueOf(Long.parseLong(id)));
       address = (Address)wf.toPo(request, obj);
     }
       address.setIsdefaut(1);
     address.setUser(SecurityUserHolder.getCurrentUser());
     Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
     address.setArea(area);
     if (id.equals(""))
       this.addressService.save(address);
     else
       this.addressService.update(address);


     return "redirect:address.htm?currentPage=" + currentPage+"&&ids="+ids+"&&communt="+communt+"&&che="+che;
   }
   @SecurityMapping(display = false, rsequence = 0, title="收货地址删除", value="/buyer/address_del.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/address_del.htm"})
   public String address_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         Address address = this.addressService.getObjById(Long.valueOf(Long.parseLong(id)));
           address.setDeleteStatus(true);
         this.addressService.update(address);
       }
     }
     return "redirect:address.htm?currentPage=" + currentPage;
   }

   //默认收货地植状态改变
   @RequestMapping({"/buyer/address_change.htm"})
   public void address_change(HttpServletRequest request, HttpServletResponse response, String id) {
     Map map= new HashMap<>();

     map.put("user_id",SecurityUserHolder.getCurrentUser().getId());
     List<Address> list=this.addressService.query("select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc",map,-1,-1);
     for (Address addr:list) {
       addr.setIsdefaut(0);
       this.addressService.update(addr);
     }

    Address address=this.addressService.getObjById(CommUtil.null2Long(id));
    address.setIsdefaut(CommUtil.null2Int(1));
     this.addressService.update(address);
      map.clear();
      map.put("cheng","设置默认地址成功");
    String ret= Json.toJson(map, JsonFormat.compact());
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

     //默认卡号植状态改变
     @RequestMapping({"/buyer/bank_change.htm"})
     public void bank_change(HttpServletRequest request, HttpServletResponse response, String id) {
         Map map= new HashMap<>();

         map.put("user_id",SecurityUserHolder.getCurrentUser().getId());
         List<Address> list=this.addressService.query("select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc",map,-1,-1);
         for (Address addr:list) {
             addr.setIsdefaut_bank(1);
             this.addressService.update(addr);
         }

         Address address=this.addressService.getObjById(CommUtil.null2Long(id));
         address.setIsdefaut(CommUtil.null2Int(1));
         this.addressService.update(address);
         map.clear();
         map.put("cheng","设置默认卡号成功");
         String ret= Json.toJson(map, JsonFormat.compact());
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


 
 
 