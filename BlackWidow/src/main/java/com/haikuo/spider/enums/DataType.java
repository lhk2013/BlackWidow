package com.haikuo.spider.enums;

public enum DataType {
	
	/**
	 * 当前状态   1列表 2 详情 3具体
	 */
	ListDetailUrl(1,"1列表"),
	DetailUrl(2,"2 详情 "),
	DetailUrlOthers(3,"3详情带页码");
	
	private Integer type;
	private String remark;
	
	DataType(Integer type,String remark){
		this.type =type;
		this.remark = remark;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
