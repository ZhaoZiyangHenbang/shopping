package com.shopping.manage.second.action;

import com.shopping.core.annotation.SecurityMapping;
import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.User;
import com.shopping.foundation.domain.query.OrderFormQueryObject;
import com.shopping.foundation.service.IOrderFormService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.IUserConfigService;
import com.shopping.view.web.tools.StoreViewTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by acer on 2017/3/14.
 */
@Controller
public class LogisticsQuery {
    @Autowired
    private IOrderFormService orderFormService;

    @Autowired
    private StoreViewTools storeViewTools;
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @SecurityMapping(display = false, rsequence = 0, title = "查看物流", value = "/second/logisticsquery.htm*", rtype = "buyer", rname = "物流信息", rcode = "logisticsquery", rgroup = "在线购物")
    @RequestMapping({"/second/logisticsquery.htm"})
    public ModelAndView everyDayGoods(HttpServletRequest request, HttpServletResponse response, String currentPage, String order_status, String order_id,String buyer_userName){
        ModelAndView mv=null;
        request.getSession().setAttribute("shopping_view_type", "wap");
       User user= SecurityUserHolder.getCurrentUser();
        if(null==user||"".equals(user)){
            mv = new JModelAndView("newwap/Login.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),1,request, response);
        }else{
            mv = new JModelAndView("../shop/newwap/21物流.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
            OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
            ofqo.addQuery("obj.user.id", new SysMap("user_id", user.getId()), "=");
            ofqo.addQuery("obj.order_status", new SysMap("order_status", 30), ">=");

            //状态
            if (!CommUtil.null2String(order_status).equals("")) {
               /* if (order_status.equals("order_submit")) {
                    ofqo.addQuery("obj.order_status",
                            new SysMap("order_status", Integer.valueOf(10)), "=");
                }
                if (order_status.equals("order_pay")) {
                    ofqo.addQuery("obj.order_status",
                            new SysMap("order_status", Integer.valueOf(20)), "=");
                }*/
                if (order_status.equals("order_shipping")) {
                    ofqo.addQuery("obj.order_status",
                            new SysMap("order_status", Integer.valueOf(30)), "=");
                }
                if (order_status.equals("order_receive")) {
                    ofqo.addQuery("obj.order_status",
                            new SysMap("order_status", Integer.valueOf(40)), "=");
                }
                if (order_status.equals("order_evaluate")) {
                    ofqo.addQuery("obj.order_status",
                            new SysMap("order_status", Integer.valueOf(50)), "=");
                }
                if (order_status.equals("order_finish")) {
                    ofqo.addQuery("obj.order_status",
                            new SysMap("order_status", Integer.valueOf(60)), "=");
                }
               /* if (order_status.equals("order_cancel")) {
                    ofqo.addQuery("obj.order_status",
                            new SysMap("order_status", Integer.valueOf(0)), "=");
                }*/
            }
            if (!CommUtil.null2String(order_id).equals("")) {
                ofqo.addQuery("obj.order_id",
                        new SysMap("order_id", "%" + order_id +
                                "%"), "like");
            }
            if (!CommUtil.null2String(buyer_userName).equals("")) {
                ofqo.addQuery("obj.user.userName",
                        new SysMap("userName",
                                buyer_userName), "=");
            }
            IPageList pList = this.orderFormService.list(ofqo);
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
            mv.addObject("storeViewTools", this.storeViewTools);
            mv.addObject("order_id", order_id);
            mv.addObject("order_status", order_status == null ? "all" :
                    order_status);

            mv.addObject("buyer_userName", buyer_userName);
            return mv;

        }







      return mv;




    }


}



