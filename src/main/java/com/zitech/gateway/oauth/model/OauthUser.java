package com.zitech.gateway.oauth.model;


import java.util.Date;

public class OauthUser {
    private int id;
    private String loginName;
	private String loginPhone;
	private String loginMail;
	private String password;
	private int status;
	private Date insertTime;
	private Date updateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPhone() {
		return loginPhone;
	}

	public void setLoginPhone(String loginPhone) {
		this.loginPhone = loginPhone;
	}

	public String getLoginMail() {
		return loginMail;
	}

	public void setLoginMail(String loginMail) {
		this.loginMail = loginMail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
