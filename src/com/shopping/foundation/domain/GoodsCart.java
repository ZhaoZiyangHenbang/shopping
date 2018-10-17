package com.shopping.foundation.domain;

import com.shopping.core.domain.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_goodscart")
public class GoodsCart extends IdEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //商品
    @OneToOne
    private Goods goods;
    //数量
    private int count;
    //购物车商品提交状态
    private int gc_status;

    public int getGc_status() {
        return gc_status;
    }

    public void setGc_status(int gc_status) {
        this.gc_status = gc_status;
    }

    private String of_type;
    //价格
    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    //商品规格属性
    @ManyToMany
    @JoinTable(name = "shopping_cart_gsp", joinColumns = {
            @javax.persistence.JoinColumn(name = "cart_id")}, inverseJoinColumns = {
            @javax.persistence.JoinColumn(name = "gsp_id")})
    private List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();

    //商品规格
    @Lob
    @Column(columnDefinition = "LongText")
    private String spec_info;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrderForm of;
    private String cart_type;

    //商店运输
    @ManyToOne(fetch = FetchType.LAZY)
    private StoreCart sc;

    private String evaluate_status;

    public String getEvaluate_status() {
        return evaluate_status;
    }

    public void setEvaluate_status(String evaluate_status) {
        this.evaluate_status = evaluate_status;
    }

    public StoreCart getSc() {
        return this.sc;
    }

    public void setSc(StoreCart sc) {
        this.sc = sc;
    }

    public Goods getGoods() {
        return this.goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<GoodsSpecProperty> getGsps() {
        return this.gsps;
    }

    public void setGsps(List<GoodsSpecProperty> gsps) {
        this.gsps = gsps;
    }

    public String getSpec_info() {
        return this.spec_info;
    }

    public void setSpec_info(String spec_info) {
        this.spec_info = spec_info;
    }

    public OrderForm getOf() {
        return this.of;
    }

    public void setOf(OrderForm of) {
        this.of = of;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCart_type() {
        return this.cart_type;
    }

    public void setCart_type(String cart_type) {
        this.cart_type = cart_type;
    }


    public String getOf_type() {
        return of_type;
    }

    public void setOf_type(String of_type) {
        this.of_type = of_type;
    }
}
