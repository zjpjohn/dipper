/**
 * 判断输入内容是否为空
 */
function isNull(str) {
	return str.trim().length == 0;
}
/**
 * 判断日期类型是否为YYYY-MM-DD格式的类型
 */
function isDate(str) {
	if (str.length != 0) {
		var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/;
		var r = str.match(reg);
		return r != null;
	} else {
		return false;
	}
}
/**
 * 判断日期类型是否为YYYY-MM-DD hh:mm:ss格式的类型
 */
function isDateTime(str) {
	if (str.length != 0) {
		var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/;
		var r = str.match(reg);
		return r != null;
	} else {
		return false;
	}
}
/**
 * 判断日期类型是否为hh:mm:ss格式的类型
 */
function isTime(str) {
	if (str.length != 0) {
		reg = /^((20|21|22|23|[0-1]\d)\:[0-5][0-9])(\:[0-5][0-9])?$/;
		return reg.test(str);
	} else {
		return false;
	}
}
/**
 * 判断输入的字符是否为英文字母
 */
function isLetter(str) {
	if (str.length != 0) {
		reg = /^[a-zA-Z]+$/;
		return reg.test(str);
	} else {
		return false;
	}
}
/**
 * 判断输入的字符是否为整数
 */
function isInteger(str) {
	if (str.length != 0) {
		reg = /^[1-9]\d*$/;
		return reg.test(str);
	} else {
		return false;
	}
}
/**
 * 判断输入的字符是否为双精度
 */
function isDouble(str) {
	if (str.length != 0) {
		reg = /^[-\+]?\d+(\.\d+)?$/;
		return reg.test(str);
	} else {
		return false;
	}
}
/**
 * 判断输入的字符是否为:a-z,A-Z,0-9
 */
function isString(str) {
	if (str.length != 0) {
		reg = /^[a-zA-Z0-9_]+$/;
		return reg.test(str);
	} else {
		return false;
	}
}

/**
 * 判断输入字符是否为中文
 */
function isChinese(str) {
	if (str.length != 0) {
		reg = /^[\u0391-\uFFE5]+$/;
		return reg.test(str);
	} else {
		return false;
	}
}

/** 判断是否为合法的名称字符串 */
function isValidateValidName(str) {
	if (str.length != 0) {
		reg = /^[a-zA-Z0-9\u4e00-\u9fa5-_]+$/;
		return reg.test(str);
	} else {
		return false;
	}
}

/**
 * 判断输入的EMAIL格式是否正确
 */
function isEmail(str) {
	if (str.length != 0) {
		reg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
		return reg.test(str);
	} else {
		return false;
	}
}

/**
 * 判断输入的Url格式是否正确
 */
function isUrl(str) {
	if (str.length != 0) {
		reg = /^http:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"\"])*$/;
		return reg.test(str);
	} else {
		return false;
	}
}
/**
 * 判断输入的ip格式是否正确
 */
function isIp(str) {
	if (str.length != 0) {
		reg = /^([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-4])$/;
		return reg.test(str);
	} else {
		return false;
	}
}
/**
 * 输入是否为特殊字符
 */
function isSpecialChar(str) {
	if (str.length != 0) {
		reg = /^[\w\u4e00-\u9fa5]+$/gi;
		return reg.test(str);
	} else {
		return false;
	}
}

/**
 * 判断输入的是否为合法的URL路径
 */
function isHttpUrlStr(str) {
	if (str.length != 0) {
		reg = /^([fF][tT][pP]:\/\/|[hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/)([A-Za-z0-9-~])+([A-Za-z0-9-~\/])+$/;
		return reg.test(str);
	} else {
		return false;
	}
}

/*******************************************************************************
 * jQuery Validate扩展验证方法 (lining)
 ******************************************************************************/
$(function() {
	// 判断整数value是否等于0
	jQuery.validator.addMethod("isIntEqZero", function(value, element) {
		value = parseInt(value);
		return this.optional(element) || value == 0;
	}, "整数必须为0");

	// 判断整数value是否大于0
	jQuery.validator.addMethod("isIntGtZero", function(value, element) {
		value = parseInt(value);
		return this.optional(element) || value > 0;
	}, "整数必须大于0");

	// 判断整数value是否大于或等于0
	jQuery.validator.addMethod("isIntGteZero", function(value, element) {
		value = parseInt(value);
		return this.optional(element) || value >= 0;
	}, "整数必须大于或等于0");

	// 判断整数value是否不等于0
	jQuery.validator.addMethod("isIntNEqZero", function(value, element) {
		value = parseInt(value);
		return this.optional(element) || value != 0;
	}, "整数必须不等于0");

	// 判断整数value是否小于0
	jQuery.validator.addMethod("isIntLtZero", function(value, element) {
		value = parseInt(value);
		return this.optional(element) || value < 0;
	}, "整数必须小于0");

	// 判断整数value是否小于或等于0
	jQuery.validator.addMethod("isIntLteZero", function(value, element) {
		value = parseInt(value);
		return this.optional(element) || value <= 0;
	}, "整数必须小于或等于0");

	// 判断浮点数value是否等于0
	jQuery.validator.addMethod("isFloatEqZero", function(value, element) {
		value = parseFloat(value);
		return this.optional(element) || value == 0;
	}, "浮点数必须为0");

	// 判断浮点数value是否大于0
	jQuery.validator.addMethod("isFloatGtZero", function(value, element) {
		value = parseFloat(value);
		return this.optional(element) || value > 0;
	}, "浮点数必须大于0");

	// 判断浮点数value是否大于或等于0
	jQuery.validator.addMethod("isFloatGteZero", function(value, element) {
		value = parseFloat(value);
		return this.optional(element) || value >= 0;
	}, "浮点数必须大于或等于0");

	// 判断浮点数value是否不等于0
	jQuery.validator.addMethod("isFloatNEqZero", function(value, element) {
		value = parseFloat(value);
		return this.optional(element) || value != 0;
	}, "浮点数必须不等于0");

	// 判断浮点数value是否小于0
	jQuery.validator.addMethod("isFloatLtZero", function(value, element) {
		value = parseFloat(value);
		return this.optional(element) || value < 0;
	}, "浮点数必须小于0");

	// 判断浮点数value是否小于或等于0
	jQuery.validator.addMethod("isFloatLteZero", function(value, element) {
		value = parseFloat(value);
		return this.optional(element) || value <= 0;
	}, "浮点数必须小于或等于0");

	// 判断浮点型
	jQuery.validator.addMethod("isFloat", function(value, element) {
		return this.optional(element) || /^[-\+]?\d+(\.\d+)?$/.test(value);
	}, "只能包含数字、小数点等字符");

	// 匹配integer
	jQuery.validator.addMethod("isInteger", function(value, element) {
		return this.optional(element)
				|| (/^[-\+]?\d+$/.test(value) && parseInt(value) >= 0);
	}, "匹配integer");

	// 判断数值类型，包括整数和浮点数
	jQuery.validator.addMethod("isNumber", function(value, element) {
		return this.optional(element) || /^[-\+]?\d+$/.test(value)
				|| /^[-\+]?\d+(\.\d+)?$/.test(value);
	}, "匹配数值类型，包括整数和浮点数");

	// 只能输入[0-9]数字
	jQuery.validator.addMethod("isDigits", function(value, element) {
		return this.optional(element) || /^\d+$/.test(value);
	}, "只能输入0-9数字");

	// 判断中文字符
	jQuery.validator.addMethod("isChinese", function(value, element) {
		return this.optional(element) || /^[\u0391-\uFFE5]+$/.test(value);
	}, "只能包含中文字符。");

	// 判断英文字符
	jQuery.validator.addMethod("isEnglish", function(value, element) {
		return this.optional(element) || /^[A-Za-z]+$/.test(value);
	}, "只能包含英文字符。");

	// 手机号码验证
	jQuery.validator
			.addMethod(
					"isMobile",
					function(value, element) {
						var length = value.length;
						return this.optional(element)
								|| (length == 11 && /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/
										.test(value));
					}, "请正确填写您的手机号码。");

	// 电话号码验证
	jQuery.validator.addMethod("isPhone", function(value, element) {
		var tel = /^(\d{3,4}-?)?\d{7,9}$/g;
		return this.optional(element) || (tel.test(value));
	}, "请正确填写您的电话号码。");

	// 联系电话(手机/电话皆可)验证
	jQuery.validator.addMethod("isTel", function(value, element) {
		var length = value.length;
		var mobile = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
		var tel = /^(\d{3,4}-?)?\d{7,9}$/g;
		return this.optional(element) || tel.test(value)
				|| (length == 11 && mobile.test(value));
	}, "请正确填写您的联系方式");

	// 联系电话(只支持手机)验证
	jQuery.validator
			.addMethod(
					"isPhone",
					function(value, element) {
						var length = value.length;
						var mobile = /^(((13[0-9]{1})|(15[0-9]{1})|(17[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
						return this.optional(element)
								|| (length == 11 && mobile.test(value));
					}, "请正确填写您的联系方式");

	// 匹配qq
	jQuery.validator.addMethod("isQq", function(value, element) {
		return this.optional(element) || /^[1-9]\d{4,12}$/;
	}, "匹配QQ");

	// 邮政编码验证
	jQuery.validator.addMethod("isZipCode", function(value, element) {
		var zip = /^[0-9]{6}$/;
		return this.optional(element) || (zip.test(value));
	}, "请正确填写您的邮政编码。");

	// 匹配密码，以字母开头，长度在6-12之间，只能包含字符、数字和下划线。
	jQuery.validator.addMethod("isPwd", function(value, element) {
		return this.optional(element) || /^\w+$/.test(value);
	}, "只能包含字符、数字和下划线。");

	// 身份证号码验证
	jQuery.validator.addMethod("isIdCardNo", function(value, element) {
		// var idCard = /^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/;
		return this.optional(element) || isIdCardNo(value);
	}, "请输入正确的身份证号码。");

	// IP地址验证
	jQuery.validator
			.addMethod(
					"ip",
					function(value, element) {
						return this.optional(element)
								|| /^(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-4])))$/
										.test(value);
					}, "请填写正确的IP地址。");

	// 判断内容是否为空
	jQuery.validator.addMethod("isNotNull", function(value, element) {
		return this.optional(element) || /\S/.test(value);
	}, "内容不能为空。");

	// 判断是否为参数键字符串，以一个或者两个横线开始，
	jQuery.validator.addMethod("isParameterKey", function(value, element) {
		var length = value.length;
		var param_key = /^[\-]{1,2}[a-zA-Z]+$/;
		return this.optional(element) || (length > 1 && param_key.test(value));
	}, "请填写正确的参数键格式(以-或者--开始的字符串)");

	// 邮箱验证
	jQuery.validator
			.addMethod(
					"isMail",
					function(value, element) {
						return this.optional(element)
								|| /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/
										.test(value);
					}, "请填写正确的邮箱地址。");

	// 字符验证，只能包含中文、英文、数字、下划线等字符。
	jQuery.validator.addMethod("stringCheck", function(value, element) {
		return this.optional(element)
				|| /^[a-zA-Z0-9\u4e00-\u9fa5-_\s]+$/.test(value);
	}, "只能包含中文、英文、数字、下划线、空格等字符");

	// 字符验证，只能包含中文、英文、数字、下划线等字符。
	jQuery.validator.addMethod("stringNameCheck", function(value, element) {
		return this.optional(element)
				|| /^[a-zA-Z0-9\u4e00-\u9fa5-_]+$/.test(value);
	}, "只能包含中文、英文、数字、减号、下划线等字符");

	// 字符验证，只能包含中文、英文、数字、下划线、句点等字符。 @2016年5月9日，添加中文逗号，句号，顿号，分号，冒号，双引号，小括号，破折号
	jQuery.validator
			.addMethod(
					"descriptionCheck",
					function(value, element) {
						return this.optional(element)
								|| /^[a-zA-Z0-9\u4e00-\u9fa5-_\,\.\@\s\u3002\uff0c\u3001\uff1a\uff1b\u201c\u201d\u2018\u2019\u2014]+$/
										.test(value);
					}, "只能包含中文、英文、数字、下划线、空格、逗号、句号等字符");

	// 字符验证，只能包含中文、英文、数字、下划线、句点等字符。
	jQuery.validator.addMethod("commentCheck", function(value, element) {
		return this.optional(element)
				|| /^[a-zA-Z0-9\u4e00-\u9fa5-_\,\.\@\s]+$/.test(value);
	}, "只能包含中文、英文、数字、下划线、空格、逗号、句点等字符");

	// 匹配english
	jQuery.validator.addMethod("isEnglish", function(value, element) {
		return this.optional(element) || /^[A-Za-z]+$/.test(value);
	}, "匹配english");

	// 匹配汉字
	jQuery.validator.addMethod("isChinese", function(value, element) {
		return this.optional(element) || /^[\u4e00-\u9fa5]+$/.test(value);
	}, "匹配汉字");

	// 匹配中文(包括汉字和字符)
	jQuery.validator.addMethod("isChineseChar", function(value, element) {
		return this.optional(element) || /^[\u0391-\uFFE5]+$/.test(value);
	}, "匹配中文(包括汉字和字符) ");

	// 判断是否为合法字符(a-zA-Z0-9-_)
	jQuery.validator.addMethod("isRightfulString", function(value, element) {
		return this.optional(element) || /^[A-Za-z0-9_\-]+$/.test(value);
	}, "只能包含(字母、数字和下划线、破折号)合法字符");

	// 判断是否为镜像名称的合法字符(a-zA-Z0-9-_)
	jQuery.validator.addMethod("isImageName",
			function(value, element) {
				return this.optional(element)
						|| /^[a-z0-9][a-z0-9_\-\:]+$/.test(value);
			}, "只能以小写字母、数字起始，其后包含小写字母、数字、下划线的合法字符串");

	// 判断是否为合法字符(a-zA-Z0-9-_)
	jQuery.validator.addMethod("isURLString", function(value, element) {
		return this.optional(element) || /^[A-Za-z0-9_\-\.\/]+$/.test(value);
	}, "只能包含(字母、数字、下划线、破折号、斜线)合法字符");

	// 判断是否为合法字符(a-zA-Z0-9-_)
	jQuery.validator.addMethod("isFileName", function(value, element) {
		return this.optional(element) || /^[A-Za-z0-9_\-\.]+$/.test(value);
	}, "只能包含(字母、数字、下划线、破折号)合法字符");

	// 判断是否为合法字符(a-zA-Z0-9-_.)
	jQuery.validator.addMethod("isVersionString", function(value, element) {
		return this.optional(element) || /^[A-Za-z0-9_\-\.]+$/.test(value);
	}, "只能包含(字母、数字和下划线、破折号)合法字符");

	// 判断是否包含中英文特殊字符，除英文"-_"字符外
	jQuery.validator
			.addMethod(
					"isContainsSpecialChar",
					function(value, element) {
						var reg = RegExp(/[(\ )(\`)(\~)(\!)(\@)(\#)(\$)(\%)(\^)(\&)(\*)(\()(\))(\+)(\=)(\|)(\{)(\})(\')(\:)(\;)(\')(',)(\[)(\])(\.)(\<)(\>)(\/)(\?)(\~)(\！)(\@)(\#)(\￥)(\%)(\…)(\&)(\*)(\（)(\）)(\—)(\+)(\|)(\{)(\})(\【)(\】)(\‘)(\；)(\：)(\”)(\“)(\’)(\。)(\，)(\、)(\？)]+/);
						return this.optional(element) || !reg.test(value);
					}, "含有中英文特殊字符");

	// 判断是否是HttpURL地址
	jQuery.validator
			.addMethod(
					"isHttpUrlString",
					function(value, element) {
						var reg = /^([fF][tT][pP]:\/\/|[hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/|\/|\/\/)([A-Za-z0-9_\.-~])+$/;
						// var reg = RegExp(strRegex);
						return this.optional(element) || reg.test(value);
					}, "应用地址格式错误，请参考：http://tomcat/或者/tom_cat/");

	// 判断是否是合法端口
	jQuery.validator
			.addMethod(
					"isValidServerPort",
					function(value, element) {
						var reg = /^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-5][0-9][0-9][0-9][0-9]$)|(^[6][0-5][0-5][0-3][0-5]$)/;
						return this.optional(element) || reg.test(value);
					}, "端口输入错误，请参考1~65535之间的数字");

	// 判断是否是合法端口
	jQuery.validator
			.addMethod(
					"isValidDockerMem",
					function(value, element) {
						var reg = /^12[8-9]$|(^1[3-9][0-9]$)|(^[2-9][0-9][0-9]$|^[1-9]\d{3}$)|(^1[0-5]\d{3}$)|(^16[0-2]\d{2}$)|(^163[0-7]\d{1}$)|(^1638[0-4]$)/;
						return this.optional(element) || reg.test(value);
					}, "Docker内存量输入错误，请参考128~16384之间的数字");

	// 身份证号码的验证规则
	function isIdCardNo(num) {
		var len = num.length, re;
		if (len == 15)
			re = new RegExp(/^(\d{6})()?(\d{2})(\d{2})(\d{2})(\d{2})(\w)$/);
		else if (len == 18)
			re = new RegExp(/^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/);
		else {
			return false;
		}
		var a = num.match(re);
		if (a != null) {
			if (len == 15) {
				var D = new Date("19" + a[3] + "/" + a[4] + "/" + a[5]);
				var B = D.getYear() == a[3] && (D.getMonth() + 1) == a[4]
						&& D.getDate() == a[5];
			} else {
				var D = new Date(a[3] + "/" + a[4] + "/" + a[5]);
				var B = D.getFullYear() == a[3] && (D.getMonth() + 1) == a[4]
						&& D.getDate() == a[5];
			}
			if (!B) {
				return false;
			}
		}
		if (!re.test(num)) {
			return false;
		}
		return true;
	}

	// 主机账户名验证
	jQuery.validator.addMethod("hostName", function(value, element) {
		return this.optional(element)
				|| /^[a-zA-Z][a-zA-Z0-9_]{2,15}$/.test(value);
	}, "用户名不合法,正确格式:字母数字下划线的组合(字母开头),长度3-16");

	// 判断是否为linux命令合法字符(a-zA-Z0-9-_.:)
	jQuery.validator.addMethod("isCommand", function(value, element) {
		return this.optional(element) || /^[A-Za-z0-9_\-\.\/\:]+$/.test(value);
	}, "只能包含(字母、数字、下划线、破折号、斜线、冒号)合法字符 ！        ");

	// 判断是否为合法字符(a-zA-Z0-9-_)
	jQuery.validator.addMethod("isEnv", function(value, element) {
		return this.optional(element)
				|| /^[A-Za-z0-9_\-\/\=\:\.]+$/.test(value);
	}, "只能包含(字母、数字和下划线、破折号、斜线、等号、冒号)合法字符");

	// 判断是否为访问路径
	jQuery.validator.addMethod("isUrlString", function(value, element) {
		var reg = /^\/[A-Za-z0-9_\.-~]+$/;
		// var reg = RegExp(strRegex);
		return this.optional(element) || reg.test(value);
	}, "访问地址格式错误，请参考：/tom_cat");

	// 判断下拉框
	jQuery.validator.addMethod("isCheckedProxy", function(value, element) {
		value = parseInt(value);
		return this.optional(element) || value > 0;
	}, "请选择监测代理！");
});
