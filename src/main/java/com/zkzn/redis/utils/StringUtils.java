package com.zkzn.redis.utils;

public class StringUtils {

	public static boolean isBlank(String message) {
		boolean isTrue = false;
		if(message.equals("")||message.equals(" ")||message.equals("null")||null==message){
			isTrue = true;
		}else{
			isTrue=false;
		}
		return isTrue;
	}

}
