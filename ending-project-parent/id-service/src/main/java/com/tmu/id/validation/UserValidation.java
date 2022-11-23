package com.tmu.id.validation;




import com.tmu.id.model.User;
import com.tmu.utils.StringUtil;

import javax.validation.ValidationException;


public class UserValidation extends AbstractValidation {
    public String validateLogin(String userName, String password) throws ValidationException {

        if( StringUtil.isNullOrEmpty(userName) )
            getMessageDes().add("userName không được để trống");

        if( StringUtil.isNullOrEmpty(password) )
            getMessageDes().add("password không được để trống");

        /**/
        return !isValid() ? this.buildValidationMessage() : null ;
    }

    public String validateUpsertUser(User user) throws ValidationException {

        if (user == null) {
            getMessageDes().add("payload không hợp lệ");
            throw new ValidationException(this.buildValidationMessage());
        }

        if(  StringUtil.isNullOrEmpty(user.getUserName()) )
            getMessageDes().add("userName không được để trống");

        if( StringUtil.isNullOrEmpty(user.getPassword()) )
            getMessageDes().add("password không được để trống");

        if( StringUtil.isNullOrEmpty(user.getFullName()) )
            getMessageDes().add("fullName không được để trống");

        if( StringUtil.isNullOrEmpty(user.getRoleName()))
            getMessageDes().add("roleName không được để trống");

        /**/
        return (!isValid()) ? this.buildValidationMessage() : null;
    }

}
