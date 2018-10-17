package com.shopping.manage.second.action;

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
 * Created by acer on 2017/3/14.
 */
@Controller
public class Service {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;


    @RequestMapping({"/second/service.htm"})
    public ModelAndView everyDayGoods(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("../shop/newwap/Customerservice .html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
        request.getSession().setAttribute("shopping_view_type", "wap");




        return mv;
    }
}
