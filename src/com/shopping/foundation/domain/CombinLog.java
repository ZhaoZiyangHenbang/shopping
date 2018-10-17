package com.shopping.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 提现记录
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_combin_log")
public class CombinLog extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6386087245825092486L;
	//开始时间
	private Date begin_time;
	//结束时间
	private Date end_time;
	//金币
	private int gold;

	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;

	private String status;

	private String bank_name;

	private String card_number;

	private BigDecimal factorage;

	private BigDecimal money;

	private BigDecimal reality_money;

	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	private String type;

	private String info;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public BigDecimal getReality_money() {
		return reality_money;
	}

	public void setReality_money(BigDecimal reality_money) {
		this.reality_money = reality_money;
	}

	public Date getBegin_time() {
		return this.begin_time;
	}

	public void setBegin_time(Date begin_time) {
		this.begin_time = begin_time;
	}

	public Date getEnd_time() {
		return this.end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public int getGold() {
		return this.gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public Store getStore() {
		return this.store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public String getCard_number() {
		return card_number;
	}

	public void setCard_number(String card_number) {
		this.card_number = card_number;
	}

	public BigDecimal getFactorage() {
		return factorage;
	}

	public void setFactorage(BigDecimal factorage) {
		this.factorage = factorage;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
