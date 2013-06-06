package com.running.mobilemenu.model;

import java.io.Serializable;
import java.util.List;

public class MyOrder implements Serializable {

	public static final String _ID = "_id";

	public static final String STATE = "orderState";

	private int orderSid;
	private String orderNumber;// 订单编号
	private String resSid;// 台位ID
	private String resName;// 台位
	private int personNum;// 人数
	private int foodNum;// 菜品总数
	private String status;// 状态
	private String serviceSid;// 设备id
	private int serverSid;// 服务员的ID
	private String serverName; // 服务员的名字
	private String orderState;// 当前系统中提交状态

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

	public int getFoodNum() {
		return foodNum;
	}

	public void setFoodNum(int foodNum) {
		this.foodNum = foodNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getServiceSid() {
		return serviceSid;
	}

	public void setServiceSid(String serviceSid) {
		this.serviceSid = serviceSid;
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

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public static String getId() {
		return _ID;
	}

	public static String getState() {
		return STATE;
	}

}
