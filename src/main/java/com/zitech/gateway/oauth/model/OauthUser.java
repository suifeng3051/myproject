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
	private String scope;

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

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public String toString() {
		return "OauthUser{" +
				"id=" + id +
				", loginName='" + loginName + '\'' +
				", loginPhone='" + loginPhone + '\'' +
				", loginMail='" + loginMail + '\'' +
				", password='" + password + '\'' +
				", status=" + status +
				", insertTime=" + insertTime +
				", updateTime=" + updateTime +
				", scope=" + scope +
				'}';
	}
}
