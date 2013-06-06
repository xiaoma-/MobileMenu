package com.running.mobilemenu.model;




public class FoodOrderInfo {
	
	private int orderInfoSid;
	private int foodSid;//食品id
	private String foodName;//菜品名字
	private int foodNum;//份数
	private Float foodPrice;//单价
	private Float cardPrice;//会员价格
	private Float sumPrice;//总价
	private Float sumCardPrice;//总会员价
	private Float discountPrice;
	private String notice;//注意事项
    private String isSmall;//是否小份
	private String pictureURL;//图片
	private String state;
	private String stateStr;
	
	
	
	
	public String getStateStr() {
		return stateStr;
	}

	public void setStateStr(String stateStr) {
		this.stateStr = stateStr;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPictureURL() {
		return pictureURL;
	}

	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}

	public String getIsSmall() {
		return isSmall;
	}

	public void setIsSmall(String isSmall) {
		this.isSmall = isSmall;
	}

	public int getOrderInfoSid() {
		return orderInfoSid;
	}

	public void setOrderInfoSid(int orderInfoSid) {
		this.orderInfoSid = orderInfoSid;
	}

	public int getFoodSid() {
		return foodSid;
	}

	public void setFoodSid(int foodSid) {
		this.foodSid = foodSid;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}


	public int getFoodNum() {
		return foodNum;
	}

	public void setFoodNum(int foodNum) {
		this.foodNum = foodNum;
	}

	public Float getFoodPrice() {
		return foodPrice;
	}

	public void setFoodPrice(Float foodPrice) {
		this.foodPrice = foodPrice;
	}

	public Float getCardPrice() {
		return cardPrice;
	}

	public void setCardPrice(Float cardPrice) {
		this.cardPrice = cardPrice;
	}

	public Float getSumPrice() {
		return sumPrice;
	}

	public void setSumPrice(Float sumPrice) {
		this.sumPrice = sumPrice;
	}

	public Float getSumCardPrice() {
		return sumCardPrice;
	}

	public void setSumCardPrice(Float sumCardPrice) {
		this.sumCardPrice = sumCardPrice;
	}

	public Float getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(Float discountPrice) {
		this.discountPrice = discountPrice;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}


}
