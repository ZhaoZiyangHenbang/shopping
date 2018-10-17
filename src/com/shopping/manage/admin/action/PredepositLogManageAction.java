 package com.shopping.manage.admin.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.Visit;
 import com.shopping.foundation.domain.query.PredepositLogQueryObject;
 import com.shopping.foundation.domain.query.VisitQueryObject;
 import com.shopping.foundation.service.IPredepositLogService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;

 import com.shopping.foundation.service.IVisitService;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;

 import java.util.Date;

 @Controller
 public class PredepositLogManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;

     @Autowired
     private IVisitService visitService;
 
   @Autowired
   private IPredepositLogService predepositlogService;
 
   @SecurityMapping(display = false, rsequence = 0, title="预存款明细列表", value="/admin/predepositlog_list.htm*", rtype="admin", rname="预存款明细", rcode="predeposit", rgroup="会员")
   @RequestMapping({"/admin/predepositlog_list.htm"})
   public ModelAndView list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String userName)
   {
     ModelAndView mv = new JModelAndView("admin/blue/predepositlog_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
       VisitQueryObject vq= new VisitQueryObject(currentPage, mv, orderBy, orderType);
       if(null!=userName&&!"".equals(userName)){
           vq.addQuery("obj.vistName",new SysMap("vistName", CommUtil.null2String(userName)),"like");
       }
       vq.setPageSize(10);
        IPageList pList=this.visitService.list(vq);
       CommUtil.saveIPageList2ModelAndView("/admin/predepositlog_list.htm","","",pList,mv);
     return mv;
   }


     @SecurityMapping(display = false, rsequence = 0, title="预存款明细列表", value="/admin/predepositlog_list.htm*", rtype="admin", rname="预存款明细", rcode="predeposit", rgroup="会员")
     @RequestMapping({"/admin/predepositlog_vist.htm"})
     public String vist(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String id)
     {
        // ModelAndView mv = new JModelAndView("admin/blue/predepositlog_list.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
         Visit v=this.visitService.getObjById(CommUtil.null2Long(id));
            v.setV_type(CommUtil.null2String(2));
            this.visitService.save(v);

         return "redirect:/admin/predepositlog_list.htm";
     }
 }


 
 
 