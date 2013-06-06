package com.running.mobilemenu.model;





import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

@Table(name="food_type")
public class FoodType {
	@Id
	@Column(name="id",type="Integer")
	private int typeSid;
	@Column(name="typeName")
	private String typeName;
	private int orderBy;
	@Column(name="createString")
	private String createString;
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
	
	public int getTypeSid() {
		return this.typeSid;
	}

	public void setTypeSid(int typeSid) {
		this.typeSid = typeSid;
	}

	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getOrderBy() {
		return this.orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}

	

	public String getCreateString() {
		return this.createString;
	}

	public void setCreateString(String createString) {
		this.createString = createString;
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