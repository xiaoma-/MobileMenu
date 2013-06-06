package com.running.mobilemenu.model;

import java.io.Serializable;
import java.util.List;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * @className Seats
 * @author xiaoma
 * @Description 桌位实体类
 * @date 2013-4-1 上午11:00:34
 */
@Table(name = "seats")
public class Seats implements Serializable {
	
	@Id
	@Column(name = "id")
	private String sid;
	private String seatName;// 座位名
	private int peopleNum;// 人数
	private String flagState;// 状态
	private int typeSid;
	private String typeName;
	private String resType2;
    private String status;// 状态
    
    
    
    
	public String getFlagState() {
		return flagState;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTypeSid() {
		return typeSid;
	}

	public void setTypeSid(int typeSid) {
		this.typeSid = typeSid;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getResType2() {
		return resType2;
	}

	public void setResType2(String resType2) {
		this.resType2 = resType2;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getSeatName() {
		return seatName;
	}

	public void setSeatName(String seatName) {
		this.seatName = seatName;
	}

	public int getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(int peopleNum) {
		this.peopleNum = peopleNum;
	}

	public String isFlagState() {
		return flagState;
	}

	public void setFlagState(String flagState) {
		this.flagState = flagState;
	}

}
