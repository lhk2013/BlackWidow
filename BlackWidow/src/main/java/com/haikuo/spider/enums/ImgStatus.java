package com.haikuo.spider.enums;

/**
 * 当前状态   1未完成（初始） 2 完成 3 执行失败
 */
public enum ImgStatus {
	/**
	 * 1未完成（初始） 
	 */
	Init(1,"1未完成（初始）"),
	/**
	 *   2 完成 
	 */
	Success(2,"2 完成"),
	/**
	 * 3 执行失败
	 */
	Failure(3,"3执行失败");
	private int type;
	private String remark;
	ImgStatus(int type,String remark){
		this.type =type;
		this.remark = remark;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
