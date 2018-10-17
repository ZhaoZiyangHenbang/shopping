package com.shopping.manage.second.action;

import com.shopping.core.annotation.SecurityMapping;
import com.shopping.core.mv.JModelAndView;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.IUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/3/31.
 */
@Controller
public class goods_cartZ {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;





   /* @SecurityMapping(display = false, rsequence = 0, title = "查看购物车", value = "/second/goods_cartZ.htm*", rtype = "second", rname = "购物流程1", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/second/goods_cartZ.htm" })
    public ModelAndView goods_cartZ(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("newwap/goods_cartZ/goods_cart_manageZ.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);


        return mv;

    }*/

}


