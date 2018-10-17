 package com.shopping.view.web.tools;
 
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.fenXiaoUtil;
 import com.shopping.foundation.domain.*;
 import com.shopping.foundation.service.IGoodsCartService;
 import com.shopping.foundation.service.IOrderFormService;

 import java.math.BigDecimal;
 import java.text.DecimalFormat;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 
 @Component
 public class OrderViewTools
 {
     @Autowired
     private IGoodsCartService goodsCartService;
 
   @Autowired
   private IOrderFormService orderFormService;
 
   public int query_user_order(String order_status)
   {
     Map params = new HashMap();
     int status = -1;
     if (order_status.equals("order_submit")) {
       status = 10;
     }
     if (order_status.equals("order_pay")) {
       status = 20;
     }
     if (order_status.equals("order_shipping")) {
       status = 30;
     }
     if (order_status.equals("order_receive")) {
       status = 40;
     }
     if (order_status.equals("order_finish")) {
       status = 60;
     }
     if (order_status.equals("order_cancel")) {
       status = 0;
     }
     params.put("status", Integer.valueOf(status));
     params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
     List ofs = this.orderFormService
       .query(
       "select obj from OrderForm obj where obj.order_status=:status and obj.user.id=:user_id", 
       params, -1, -1);
     return ofs.size();
   }
 
   public int query_store_order(String order_status) {
     if (SecurityUserHolder.getCurrentUser().getStore() != null) {
       Map params = new HashMap();
       int status = -1;
       if (order_status.equals("order_submit")) {
         status = 10;
       }
       if (order_status.equals("order_pay")) {
         status = 20;
       }
       if (order_status.equals("order_shipping")) {
         status = 30;
       }
       if (order_status.equals("order_receive")) {
         status = 40;
       }
       if (order_status.equals("order_finish")) {
         status = 60;
       }
       if (order_status.equals("order_cancel")) {
         status = 0;
       }
       params.put("status", Integer.valueOf(status));
       params.put("store_id", SecurityUserHolder.getCurrentUser()
         .getStore().getId());
       List ofs = this.orderFormService
         .query(
         "select obj from OrderForm obj where obj.order_status=:status and obj.store.id=:store_id", 
         params, -1, -1);
       return ofs.size();
     }
     return 0;
   }

   //计算一个订单可以拿到䣤提成
    public BigDecimal query_order_ticheng(OrderForm orderForm,User user,String rank) {//rank是几级分销商
        DecimalFormat df=new DecimalFormat("0.00");
      Map goodsCart = new HashMap();
      goodsCart.put("of_id", orderForm.getId());
      List<GoodsCart> goodsList =  this.goodsCartService.query("select obj from GoodsCart obj where obj.of.id=:of_id", goodsCart, -1, -1);
        BigDecimal tiCheng1 = new BigDecimal("0");
        BigDecimal tiCheng2 = new BigDecimal("0");
        BigDecimal tiCheng3 = new BigDecimal("0");
        if(null!=goodsList&&goodsList.size()!=0){
            for (GoodsCart goodsCart1:goodsList) {
                //当前该用户的星级提成比例    传一个星级获取到一个星级占比
                BigDecimal xingji = fenXiaoUtil.getStarLevelRatio(user.getUser_credit()+"");
                BigDecimal bi = new BigDecimal("0");
                //体验店算分销利润规则：商品金额 * 数量 * 下线等级提成比例 * 星级提成比例
         /*       Goods bbb=goodsCart1.getGoods();
                GoodsClass ccc= goodsCart1.getGoods().getGc();
                String aaa=goodsCart1.getGoods().getGc().getBc_location();*/
                if(null != goodsCart1 && null != goodsCart1.getGoods() && null != goodsCart1.getGoods().getGc() && null != goodsCart1.getGoods().getGc().getBc_location() && goodsCart1.getGoods().getGc().getBc_location().equals("2")){
                    //体验店是按照总额分润（商品金额的分销）//商品金额*数量
                    bi = goodsCart1.getGoods().getStore_price().multiply(new BigDecimal(goodsCart1.getCount()));
                    //根据星级分润    * 星级提成比例
                    BigDecimal actual = bi.multiply(xingji);
                    //  * 下线等级提成比例
                    BigDecimal a = actual.multiply(fenXiaoUtil.getRatio(rank,"0"));
                    // type = type.add(a);
                    //体验店商品的提成
                    tiCheng1 = tiCheng1.add(a);
                }else{//非体验店算分销利润规则：商品金额 * 数量 * 下线等级提成比例 * 星级提成比例  (不一样的在下线等级提成比例)
                    //非体验店是按照分润额度分润（分销金额的分销）   商品金额 * 数量
                    bi = goodsCart1.getGoods().getP_fenrun().multiply(new BigDecimal(goodsCart1.getCount()));
                    //根据星级分润   * 星级提成比例
                    BigDecimal actual = bi.multiply(xingji);
                    //  * 下线等级提成比例
                    BigDecimal a = actual.multiply(fenXiaoUtil.getRatio(rank,"1"));
                    //  type = type.add(a);
                    //非体验店商品提成
                    tiCheng2 = tiCheng2.add(a);
                }
                //一个订单的分润
                tiCheng3=tiCheng3.add(tiCheng1).add(tiCheng2);

            }
        }
        tiCheng3=new BigDecimal(df.format(tiCheng3));
      return tiCheng3;
    }
 }


 
 
 