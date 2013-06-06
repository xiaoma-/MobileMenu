package com.running.mobilemenu.model;

import java.io.Serializable;
import java.util.Date;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

/**
 * @className Traffics
 * @author xiaoma
 * @Description 流量统计
 * @date 2013-5-20 下午3:19:59
 */
@Table(name = "traffics")
public class Traffics implements Serializable  {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	private int sid;
	

	@Column(name = "send", type = "long")
	private long send;
	
	@Column(name = "receive", type = "long")
	private long receive;
	
	@Column(name = "sendDay", type = "long")
	private long sendDay;
	
	@Column(name = "receiveDay", type = "long")
	private long receiveDay;
	
	@Column(name = "createDate")
	private String createDate;
    
	@Column(name = "flag")
	private String flag;//确定每天的流量
	
	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public long getSend() {
		return send;
	}

	public void setSend(long send) {
		this.send = send;
	}

	public long getReceive() {
		return receive;
	}

	public void setReceive(long receive) {
		this.receive = receive;
	}

	public long getSendDay() {
		return sendDay;
	}

	public void setSendDay(long sendDay) {
		this.sendDay = sendDay;
	}

	public long getReceiveDay() {
		return receiveDay;
	}

	public void setReceiveDay(long receiveDay) {
		this.receiveDay = receiveDay;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	
	
}
