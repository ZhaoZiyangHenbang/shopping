package com.shopping.manage.second.action;

import com.shopping.core.mv.JModelAndView;
import com.shopping.foundation.service.IGoodsClassService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.IUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/21.
 */
@Controller
public class GoodClassList {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;


    @Autowired
    private IGoodsClassService goodsClassService;

    @RequestMapping({"/second/goodclasslist.htm"})
    public ModelAndView everyDayGoods(HttpServletRequest request, HttpServletResponse response ){
        ModelAndView mv = new JModelAndView("../shop/newwap/Commodityclass.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request, response);
//        request.getSession().setAttribute("shopping_view_type", "wap");
//        Map params = new HashMap();
//        params.put("display", Boolean.valueOf(true));
//        List gcs = this.goodsClassService.query("select obj from GoodsClass obj where obj.parent.id is null and obj.bc_location = 1 and obj.display=:display order by obj.sequence asc", params, 0, 15);
//        mv.addObject("gcs", gcs);
        return mv;
    }
}
