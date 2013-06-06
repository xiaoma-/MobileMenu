package com.running.mobilemenu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

@Table(name = "foods")
public class Food implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	private int sid;

	@Column(name = "name")
	private String name;

	@Column(name = "bigPrice", type = "float")
	private Float bigPrice;

	@Column(name = "bigCardPrice", type = "float")
	private Float bigCardPrice;

	@Column(name = "smallPrice", type = "float")
	private Float smallPrice;

	@Column(name = "smallCardPrice", type = "float")
	private Float smallCardPrice;

	@Column(name = "history", length = 1000)
	private String history;// 历史背景

	@Column(name = "materials", length = 1000)
	private String materials; // 原材料

	@Column(name = "makingProcess", length = 1000)
	private String makingProcess;// 制作流程

	@Column(name = "notice", length = 500)
	private String notice;// 提示

	@Column(name = "peopleNum", type = "Integer")
	private int peopleNum;

	@Column(name = "demo")
	private String demo;

	@Column(name = "typeName")
	private String typeName;

	@Column(name = "typeSid", type = "Integer")
	private int typeSid;

	@Column(name = "isRecommend")
	private String isRecommend;

	@Column(name = "pictureURL")
	private String pictureURL;

	@Column(name = "createDate")
	private Date createDate;

	@Column(name = "encode")
	private String code;

	@Column(name = "spell")
	private String spell;

	@Column(name = "field1")
	private String field1;

	@Column(name = "field2")
	private String field2;

	@Column(name = "field3")
	private String field3;

	@Column(name = "field4")
	private String field4;

	@Column(name = "field5")
	private String field5;

	@Column(name = "field6")
	private String field6;

	@Column(name = "field7")
	private String field7;

	@Column(name = "field8")
	private String field8;

	@Column(name = "field9")
	private String field9;

	private int orderCount = 1;

	private int bigPicture;
	private int sPicture;
	private List<FoodImage> images;

	public List<FoodImage> getImages() {
		return images;
	}

	public void setImages(List<FoodImage> images) {
		this.images = images;
	}

	public int getTypeSid() {
		return typeSid;
	}

	public void setTypeSid(int typeSid) {
		this.typeSid = typeSid;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getBigPrice() {
		return bigPrice;
	}

	public void setBigPrice(Float bigPrice) {
		this.bigPrice = bigPrice;
	}

	public Float getBigCardPrice() {
		return bigCardPrice;
	}

	public void setBigCardPrice(Float bigCardPrice) {
		this.bigCardPrice = bigCardPrice;
	}

	public Float getSmallPrice() {
		return smallPrice;
	}

	public void setSmallPrice(Float smallPrice) {
		this.smallPrice = smallPrice;
	}

	public Float getSmallCardPrice() {
		return smallCardPrice;
	}

	public void setSmallCardPrice(Float smallCardPrice) {
		this.smallCardPrice = smallCardPrice;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public String getMaterials() {
		return materials;
	}

	public void setMaterials(String materials) {
		this.materials = materials;
	}

	public String getMakingProcess() {
		return makingProcess;
	}

	public void setMakingProcess(String makingProcess) {
		this.makingProcess = makingProcess;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public int getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(int peopleNum) {
		this.peopleNum = peopleNum;
	}

	public String getDemo() {
		return demo;
	}

	public void setDemo(String demo) {
		this.demo = demo;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(String isRecommend) {
		this.isRecommend = isRecommend;
	}

	public String getPictureURL() {
		return pictureURL;
	}

	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public int getBigPicture() {
		return bigPicture;
	}

	public void setBigPicture(int bigPicture) {
		this.bigPicture = bigPicture;
	}

	public int getsPicture() {
		return sPicture;
	}

	public void setsPicture(int sPicture) {
		this.sPicture = sPicture;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSpell() {
		return spell;
	}

	public void setSpell(String spell) {
		this.spell = spell;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public String getField4() {
		return field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	public String getField6() {
		return field6;
	}

	public void setField6(String field6) {
		this.field6 = field6;
	}

	public String getField7() {
		return field7;
	}

	public void setField7(String field7) {
		this.field7 = field7;
	}

	public String getField8() {
		return field8;
	}

	public void setField8(String field8) {
		this.field8 = field8;
	}

	public String getField9() {
		return field9;
	}

	public void setField9(String field9) {
		this.field9 = field9;
	}

}
