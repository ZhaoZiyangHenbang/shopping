package com.shopping.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 特价商品
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_bargain_goods")
public class BargainGoods extends IdEntity {

	/**
	 * UID
	 */
	//推荐理由
	private String bg_recommend_reason;

	public String getBg_recommend_reason() {
		return bg_recommend_reason;
	}

	public void setBg_recommend_reason(String bg_recommend_reason) {
		this.bg_recommend_reason = bg_recommend_reason;
	}

	private static final long serialVersionUID = 7152250727882399474L;
	//商品
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods bg_goods;
//推荐用户
	@JoinColumn(name="tuijian_userid")
	@ManyToOne
	private User tuijian_userid;

	public User getTuijian_userid() {
		return tuijian_userid;
	}

	public void setTuijian_userid(User tuijian_userid) {
		this.tuijian_userid = tuijian_userid;
	}

	//状态
	private int bg_status;

	//
	private Date end_time;

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	//折扣
	@Column(precision = 3, scale = 1)
	private BigDecimal bg_rebate;

	//所属栏目
	private String bg_nav;

	public String getBg_nav() {
		return bg_nav;
	}

	public void setBg_nav(String bg_nav) {
		this.bg_nav = bg_nav;
	}

	//所属类别
	private String bg_class;

	public String getBg_class() {
		return bg_class;
	}

	public void setBg_class(String bg_class) {
		this.bg_class = bg_class;
	}
	//数量
	@Column(columnDefinition = "int default 1")
	private int bg_count;
	@ManyToOne(fetch = FetchType.LAZY)
	private User bg_admin_user;
	//特价价格
	@Column(precision = 12, scale = 2)
	private BigDecimal bg_price;
	//特价时间
	@Temporal(TemporalType.DATE)
	private Date bg_time;
	//开始时间&zzy审核时间
	private Date audit_time;
	//限时抢购开始时间
	/*private Date cts_startTime;
//限时抢购结束时间

	public Date getCts_startTime() {
		return cts_startTime;
	}

	public void setCts_startTime(Date cts_startTime) {
		this.cts_startTime = cts_startTime;
	}

	public Date getCts_endTime() {
		return cts_endTime;
	}

	public void setCts_endTime(Date cts_endTime) {
		this.cts_endTime = cts_endTime;
	}

	private Date cts_endTime;*/

	public int getBg_storeid() {
		return bg_storeid;
	}
	public void setBg_storeid(int bg_storeid) {
		this.bg_storeid = bg_storeid;
	}
	//店铺id
	private int bg_storeid;

	public Date getAudit_time() {
		return this.audit_time;
	}

	public void setAudit_time(Date audit_time) {
		this.audit_time = audit_time;
	}

	public Date getBg_time() {
		return this.bg_time;
	}

	public void setBg_time(Date bg_time) {
		this.bg_time = bg_time;
	}

	public Goods getBg_goods() {
		return this.bg_goods;
	}

	public void setBg_goods(Goods bg_goods) {
		this.bg_goods = bg_goods;
	}

	public int getBg_status() {
		return this.bg_status;
	}

	public void setBg_status(int bg_status) {
		this.bg_status = bg_status;
	}

	public User getBg_admin_user() {
		return this.bg_admin_user;
	}

	public void setBg_admin_user(User bg_admin_user) {
		this.bg_admin_user = bg_admin_user;
	}

	public BigDecimal getBg_price() {
		return this.bg_price;
	}

	public void setBg_price(BigDecimal bg_price) {
		this.bg_price = bg_price;
	}

	public BigDecimal getBg_rebate() {
		return this.bg_rebate;
	}

	public void setBg_rebate(BigDecimal bg_rebate) {
		this.bg_rebate = bg_rebate;
	}

	public int getBg_count() {
		return this.bg_count;
	}

	public void setBg_count(int bg_count) {
		this.bg_count = bg_count;
	}
}
