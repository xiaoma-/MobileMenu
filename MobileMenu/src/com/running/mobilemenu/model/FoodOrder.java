package com.running.mobilemenu.model;


import java.util.Date;
import java.util.Set;

public class FoodOrder {
	private int orderSid;
	private String orderNumber;
	private String resSid;
	private String resName;
	private int personNum;
	private Date advanceDate;
	private Date useDate;
	private Date billDate;
	private int foodNum;
	private Float sumPrice;
	private Float factPrice;
	private Float discountPrice;
	private String status;
	private int serverSid;
	private String serverName;
	private String isSynchro;
	private Date createDate;

	private Set<FoodOrderInfo> orderInfos;
	
	public int getOrderSid() {
		return orderSid;
	}
	public void setOrderSid(int orderSid) {
		this.orderSid = orderSid;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getResSid() {
		return resSid;
	}
	public void setResSid(String resSid) {
		this.resSid = resSid;
	}
	public String getResName() {
		return resName;
	}
	public void setResName(String resName) {
		this.resName = resName;
	}
	public int getPersonNum() {
		return personNum;
	}
	public void setPersonNum(int personNum) {
		this.personNum = personNum;
	}
	public Date getAdvanceDate() {
		return advanceDate;
	}
	public void setAdvanceDate(Date advanceDate) {
		this.advanceDate = advanceDate;
	}
	public Date getUseDate() {
		return useDate;
	}
	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}
	public Date getBillDate() {
		return billDate;
	}
	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}
	public int getFoodNum() {
		return foodNum;
	}
	public void setFoodNum(int foodNum) {
		this.foodNum = foodNum;
	}
	public Float getSumPrice() {
		return sumPrice;
	}
	public void setSumPrice(Float sumPrice) {
		this.sumPrice = sumPrice;
	}
	public Float getFactPrice() {
		return factPrice;
	}
	public void setFactPrice(Float factPrice) {
		this.factPrice = factPrice;
	}
	public Float getDiscountPrice() {
		return discountPrice;
	}
	public void setDiscountPrice(Float discountPrice) {
		this.discountPrice = discountPrice;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getServerSid() {
		return serverSid;
	}
	public void setServerSid(int serverSid) {
		this.serverSid = serverSid;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getIsSynchro() {
		return isSynchro;
	}
	public void setIsSynchro(String isSynchro) {
		this.isSynchro = isSynchro;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public Set<FoodOrderInfo> getOrderInfos() {
		return orderInfos;
	}
	public void setOrderInfos(Set<FoodOrderInfo> orderInfos) {
		this.orderInfos = orderInfos;
	}
	
}
