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
@Table(name = "shopping_storecart")
public class StoreCart extends IdEntity {

    //商店
    @ManyToOne
    private Store store;
//
    private BigDecimal yunshufeiyong;

    public BigDecimal getYunshufeiyong() {
        return yunshufeiyong;
    }

    public void setYunshufeiyong(BigDecimal yunshufeiyong) {
        this.yunshufeiyong = yunshufeiyong;
    }

    //商品运送
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sc")
    private List<GoodsCart> gcs = new ArrayList();
    //总价
    private BigDecimal total_price;

    @ManyToOne
    @JoinColumn(name="sh_coupon_id")
    private CouponInfo couponInfo;
private String yunshu_id;

    public String getYunshu_id() {
        return yunshu_id;
    }

    public void setYunshu_id(String yunshu_id) {
        this.yunshu_id = yunshu_id;
    }

    public CouponInfo getCouponInfo() {
        return couponInfo;
    }

    public void setCouponInfo(CouponInfo couponInfo) {
        this.couponInfo = couponInfo;
    }

    //用户
    @ManyToOne
    private User user;
    //运送会话ID
    private String cart_session_id;

    //运送状态
    @Column(columnDefinition = "int default 0")
    private int sc_status;

    public int getSc_status() {
        return this.sc_status;
    }

    public void setSc_status(int sc_status) {
        this.sc_status = sc_status;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCart_session_id() {
        return this.cart_session_id;
    }

    public void setCart_session_id(String cart_session_id) {
        this.cart_session_id = cart_session_id;
    }

    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<GoodsCart> getGcs() {
        return this.gcs;
    }

    public void setGcs(List<GoodsCart> gcs) {
        this.gcs = gcs;
    }

    public BigDecimal getTotal_price() {
        return this.total_price;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }
}



 
 