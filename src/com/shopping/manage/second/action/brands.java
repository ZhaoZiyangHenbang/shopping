package com.shopping.manage.second.action;

import com.shopping.brands.bean.Brandsbean;
import com.shopping.brands.service.BrandsService;
import com.shopping.brands.service.BrandsServiceImpl;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.Visit;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.IUserConfigService;
import com.shopping.foundation.service.IVisitService;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/28.
 */
@Controller
public class brands {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IVisitService vistService;

    @RequestMapping({"/second/brandsServlet.htm"})
    public String brands(HttpServletRequest request, HttpServletResponse response) {
       // ModelAndView mv = new JModelAndView("wap/index.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

        String name=request.getParameter("name");
        String tel=request.getParameter("tel");
        String descripe=request.getParameter("descripe");
      /*  System.out.println("name="+name);
        System.out.println("tel="+tel);
        System.out.println("descripe="+descripe);*/

        Visit bb=new Visit();
        bb.setVistName(name);
        bb.setVistTel(tel);
        bb.setVistDescript(descripe);
        bb.setAddTime(new Date());
        bb.setV_type("1");
        // this.brandsService.save(bb);
        this.vistService.save(bb);

        return "redirect:" + CommUtil.getURL(request) + "/wap/index.htm";
    }
    @RequestMapping({"brandsServlet.htm"})
    public String brandspc(HttpServletRequest request, HttpServletResponse response,String type) {
       // ModelAndView mv = new JModelAndView("index.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

        String name=request.getParameter("name");
        String tel=request.getParameter("tel");
        String descripe=request.getParameter("descripe");
       /* System.out.println("name="+name);
        System.out.println("tel="+tel);
        System.out.println("descripe="+descripe);*/

        Visit bb=new Visit();
        bb.setVistName(name);
        bb.setVistTel(tel);
        bb.setVistDescript(descripe);
        bb.setAddTime(new Date());
        bb.setV_type("1");

        // this.brandsService.save(bb);
        this.vistService.save(bb);
        if(null!=type&&"nihao".equals(type)){
            return "redirect:/buyer/index.htm";
        }else {
            return "redirect:index.htm";
        }
     }

    @RequestMapping({"brandsServletAjax.htm"})
    public void brandsServletAjax(HttpServletResponse response,String name,String tel,String descripe) {
        Map map = new HashMap();
        Visit bb = new Visit();
        bb.setVistName(name);
        bb.setVistTel(tel);
        bb.setVistDescript(descripe);
        bb.setAddTime(new Date());
        bb.setV_type("1");
        boolean a = this.vistService.save(bb);
        if(a){
            map.put("msg","提交成功，请等待客服人员和你联系！");
        }else{
            map.put("msg","提交失败，请稍后再试！！");
        }
        String msg= Json.toJson(map, JsonFormat.compact());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer=response.getWriter();
            writer.print(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
