package com.zitech.gateway.oauth.dao.user;


import com.zitech.gateway.oauth.model.UserThirdInfo;
import com.zitech.platform.dao.base.func.IEntityDAO;

public interface UserThirdInfoMapper extends IEntityDAO<UserThirdInfo, UserThirdInfo> {
	int deleteByPrimaryKey(Long id);

	int insert(UserThirdInfo record);

	int insertSelective(UserThirdInfo record);

	UserThirdInfo selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(UserThirdInfo record);

	int updateByPrimaryKey(UserThirdInfo record);

	UserThirdInfo selectByUserId(Long userId);

	void updateByuserid(UserThirdInfo userThirdInfo);

}