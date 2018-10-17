package com.shopping.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.*;
import com.shopping.foundation.service.*;
import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2017/5/9.
 */
@RequestMapping("/wappay")
@Controller
public class WapPayController{

    @Autowired
    private ISysConfigService configService;
    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IOrderFormService orderFormService;
    @Autowired
    private IGroupGoodsService groupGoodsService;

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IOrderFormLogService orderFormLogService;
    @Autowired
    private IPaymentService paymentService;
    private static Logger log = Logger.getLogger(WapPayController.class);

    
    @RequestMapping(value = "/pay.htm")
    public ModelAndView pay(String order_id, Model model,HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("newwap/goods_cartZ/pay.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
        List payments = new ArrayList();
        Map params = new HashMap();
        //判断是否平台支付
            params.put("mark", "alipay_wap");
            params.put("type", "admin");
            payments = this.paymentService.query(
                    "select obj from Payment obj where obj.mark=:mark and obj.type=:type", params, -1, -1);
        Payment payment = (Payment) payments.get(0);
        order.setPayment(payment);
        this.orderFormService.update(order);
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        //调用RSA签名方式
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
        AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();
        String siteURL = CommUtil.getURL(request);
        // 封装请求支付信息
        AlipayTradeWapPayModel paymodel=new AlipayTradeWapPayModel();
        paymodel.setOutTradeNo(order_id);
        paymodel.setSubject(order_id);
        paymodel.setTotalAmount(String.valueOf(order.getTotalPrice()));
        paymodel.setBody("微商城支付");
        paymodel.setTimeoutExpress(AlipayConfig.timeout_express);
        paymodel.setProductCode(AlipayConfig.product_code);
        alipay_request.setBizModel(paymodel);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(siteURL + "/wappay/notify.htm");
        // 设置同步地址
        alipay_request.setReturnUrl(siteURL + "/wappay/return.htm");
        // form表单生成
        String form = "";
        try {
            // 调用SDK生成表单
             form = client.pageExecute(alipay_request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        model.addAttribute("form",form);
        return mv;
    }


    /***
     * 支付同步方法
     * @return
     */
    @RequestMapping(value = "/return.htm")
    public void returnUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
//获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        //商户订单号

        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

        //支付宝交易号

        String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

        OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(out_trade_no));

        com.shopping.pay.alipay.config.AlipayConfig config = new com.shopping.pay.alipay.config.AlipayConfig();
        config.setNotify_url(CommUtil.getURL(request, this.configService.getSysConfig()) + "/alipay/alipay_notify.htm");
        config.setReturn_url(CommUtil.getURL(request, this.configService.getSysConfig()) + "/alipay/aplipay_return.htm");
        System.out.println(trade_no);
        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        boolean verify_result = false;
        try {
            verify_result = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        if(verify_result){//验证成功
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码
            //该页面可做页面美工编辑
            response.getWriter().write("success");
            System.out.println("验证成功<br />");
            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
            order.setOrder_status(20);
            order.setOut_order_id(trade_no);
            order.setPayTime(new Date());
            this.orderFormService.update(order);

            update_goods_inventory(order);
            OrderFormLog ofl = new OrderFormLog();
            ofl.setAddTime(new Date());
            ofl.setLog_info("支付宝wap在线支付");
            ofl.setLog_user(order.getUser());
            ofl.setOf(order);
            this.orderFormLogService.save(ofl);
            response.sendRedirect(CommUtil.getURL(request, this.configService.getSysConfig()) + "/alipay/paysuccess.htm");
            System.out.println(CommUtil.getURL(request, this.configService.getSysConfig()) + "/alipay/paysuccess.htm");
            //////////////////////////////////////////////////////////////////////////////////////////
        }else{
            //该页面可做页面美工编辑
            response.getWriter().write("fail");
            System.out.println("验证失败");
        }
    }

    /***
     * 支付异步方法
     * @return
     */
    @RequestMapping(value = "/notify.htm")
    public void  notifyUrl(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
//获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        //商户订单号

        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
        OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(out_trade_no));


        //支付宝交易号

        String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

        //交易状态
        String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        boolean verify_result = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");

        if(verify_result){//验证成功
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码

            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

           /* if(trade_status.equals("TRADE_FINISHED")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            } else if (trade_status.equals("TRADE_SUCCESS")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            }
*/
            if (((trade_status.equals("WAIT_SELLER_SEND_GOODS")) ||
                    (trade_status.equals("TRADE_FINISHED")) ||
                    (trade_status.equals("TRADE_SUCCESS"))) && (order.getOrder_status() < 20)) {
                order.setOrder_status(20);
                order.setOut_order_id(trade_no);
                order.setPayTime(new Date());
                this.orderFormService.update(order);

                update_goods_inventory(order);
                OrderFormLog ofl = new OrderFormLog();
                ofl.setAddTime(new Date());
                ofl.setLog_info("支付宝wap在线支付");
                ofl.setLog_user(order.getUser());
                ofl.setOf(order);
                this.orderFormLogService.save(ofl);

            }
            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
//            out.println("success");	//请不要修改或删除
            response.getWriter().write("success");
            System.out.println("验证成功<br />");
            //////////////////////////////////////////////////////////////////////////////////////////
        }else{//验证失败
            response.getWriter().write("fail");
            System.out.println("验证失败<br />");
        }
    }
    /**
     * 更新库存
     * @param order
     */
    private void update_goods_inventory(OrderForm order)
    {
        for (GoodsCart gc : order.getGcs()) {
            Goods goods = gc.getGoods();
            if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
                for (GroupGoods gg : goods.getGroup_goods_list()) {
                    if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
                        gg.setGg_def_count(gg.getGg_def_count() + gc.getCount());
                        gg.setGg_count(gg.getGg_count() - gc.getCount());
                        this.groupGoodsService.update(gg);
                    }
                }
            }
            List gsps = new ArrayList();
            for (GoodsSpecProperty gsp : gc.getGsps()) {
                gsps.add(gsp.getId().toString());
            }
            String[] gsp_list = new String[gsps.size()];
            gsps.toArray(gsp_list);
            goods.setGoods_salenum(goods.getGoods_salenum() + gc.getCount());
            String inventory_type = goods.getInventory_type() == null ? "all" : goods.getInventory_type();
            Map temp;
            if (inventory_type.equals("all")) {
                goods.setGoods_inventory(goods.getGoods_inventory() - gc.getCount());
            } else {
                List list = Json.fromJson(ArrayList.class, CommUtil.null2String(goods.getGoods_inventory_detail()));
                for (Iterator localIterator4 = list.iterator(); localIterator4.hasNext(); ) { temp = (Map)localIterator4.next();
                    String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
                    Arrays.sort(temp_ids);
                    Arrays.sort(gsp_list);
                    if (Arrays.equals(temp_ids, gsp_list)) {
                        temp.put("count", Integer.valueOf(CommUtil.null2Int(temp.get("count")) - gc.getCount()));
                    }
                }
                goods.setGoods_inventory_detail(Json.toJson(list, JsonFormat.compact()));
            }
            for (GroupGoods gg : goods.getGroup_goods_list()) {
                if ((!gg.getGroup().getId().equals(goods.getGroup().getId())) || (gg.getGg_count() != 0))
                    continue;
                goods.setGroup_buy(3);
            }

            this.goodsService.update(goods);
        }
    }
}
