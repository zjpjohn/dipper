// zero
//
//	描述：配置属性action已经子属性,可支持ajax直接从后台取数据！
//      静态数据需要配置data属性
//		
//		动态取数据或静态数据格式统一，如下
//		移动一条数据时，支持回调函数，可在验证时使用
//		backFun函数必须返回布尔类型，true:允许移动,false:不允许移动
//
//	方法：
//		作用：
//			获取选择完成后的值，
//		方法名次：
//			$.fn.selectionBc.getValue(selector,separator);
//		参数：
//			selector 选择器,如:'#cy',
//			separator 为值连接符，如'::'
//		结果：
//			1::2::3
//
//		demo
//		$('#test').selectionBc({
//			size : 10,
//			type : 'multiple',//single
//				
//			action : {
//				type : 'get',
//				dataType : 'json',
//				url : "test",
//				params : undefined
//			},
//			
//			data : {
//				rightData : [{"label" : "test1","value" : "1"},{"label" : "test2","value" : "2"}],
//				leftData : [{"label" : "test3","value" : "3"},{"label" : "test4","value" : "4"}]
//			},
//			backFun : function(i,v,t,o){
//				console.info(i);
//				console.info(v);
//				console.info(t);
//				console.info(o);
//				//return v == 1 ? true : 0;
//				return true;
//			}
//		});

(function($){
	$.fn.selectionBc = function(options){
		options = jQuery.extend({
					data : {
						rightData : [],
						leftData : []
					}
				},$.fn.selectionBc.defaults,options);

		this.each(function(){
			var $this = $(this),
				html = '<div style="' + options.parentCss + '">'
				+ '<div style="' + options.titleCss + 'height:' + options.height + 'px;line-height:' + options.height + 'px;"><span style="' + options.titleSpanFirstCss + 'font-size:' + options.fontSize + 'px">' + options.leftTitle + '</span><span style="' + options.titleSpanCss + 'font-size:' + options.fontSize + 'px">' + options.rightTitle + '</span></div>'
				+ '<select size=' + options.size + ' style="' + options.leftOrRightCss + 'height:' + options.selectHeight + '"></select>'
				+ '<div style="' + options.btnCss + '""><a href="javascript:void(0);" style="' + options.btnACss + '"><img src="' + options.leftImage + '" style="width:30px;height:30px;"></a><a href="javascript:void(0);" style="' + options.btnACss + '"><img src="' + options.rightImage + '" style="width:30px;height:30px;"></a></div>'
				+ '<select size=' + options.size + ' style="' + options.leftOrRightCss + 'height:' + options.selectHeight + '"></select>'
				+ '</div>';
				parent = jQuery(html);
			if(0 === options.width)parent.width('100%');
			else
				parent.width(options.width);
			$this.append(html);
			createOptions($this,options,options.data.rightData,options.data.leftData);
			return this;
		});
	};

	var createOptions = function($this,options,rightData,leftData){
		if(options.action.url){
			getDataByAjax($this,options);
		}else{
			createOptionsHtmlByStatic($this,rightData,leftData);
		}
		setEvents($this,options);
	},
	getDataByAjax = function($this,options){
		$.ajax({
		   type: options.action.type,
		   dataType : options.action.dataType,
		   url: options.action.url,
		   data : options.action.params,
		   success: function(result){
			   if(result.rightData && result.leftData){
			   		createOptionsHtmlByStatic($this,result.rightData,result.leftData);
			   		setEvents($this,options);
			   }
		   },
		   error : function(e){
		   		console.info(e);
			   console.info('加载' + action + '数据异常!');
		   }
		});
	},
	createOptionsByJson = function(array){
		var liHtml = '';
		jQuery.each(array,function(i,temp){
			if (temp.selected) {
				liHtml += '<option value="' + temp.value + '">' + temp.label + '</option>';
			} else {
				liHtml += '<option value="' + temp.value + '">' + temp.label + '</option>';
			}
		});
		return liHtml;
	},
	createOptionsHtmlByStatic = function($this,rightData,leftData){
		createRightHtml($this,rightData);
		createLeftHtml($this,leftData);
	},
	createRightHtml = function($this,data){
		if(data && data instanceof Array){
			var html = createOptionsByJson(data);
			if(html)
				$this.find('select:eq(0)').append(html);
		}
	},
	createLeftHtml = function($this,data){
		if(data && data instanceof Array){
			var html = createOptionsByJson(data);
			if(html)
				$this.find('select:eq(1)').append(html);
		}
	},
	eventFun = function(src,target,options){
		var leftDom = src.get(0),
			rightDom = target.get(0),
			index = leftDom.selectedIndex,
			o = leftDom.options[index];
			canMove = index >= 0 ? true : false;
		if(canMove && 'single' === options.type){
			if(rightDom.options.length > 0){
				canMove = false;
			}
		}
		if(canMove && options.backFun && typeof(options.backFun) === 'function'){
			canMove = options.backFun(index,o.value,o.text,o);
		}

		if (index >= 0 && canMove) {
			var op = new Option(o.text);
			op.value = o.value;
			op.selected = true;
			leftDom.remove(index);
			rightDom.options.add(op);
			setEventByUnit(op, target, src,options);
			if(options.backFun && typeof(options.backFun) === 'function'){
				options.backFun(op);
			}
		}
	},
	setEventByUnit = function(thiz,src,target,options){
		$(thiz).off('dblclick').dblclick(function(){
			eventFun(src, target, options);
		});
	},
	dblClickFun = function($this,options){
		var left = $this.find('select:eq(0)'),
			right = $this.find('select:eq(1)');

		$.each(left.children(),function(){
			setEventByUnit(this, left, right,options);
		});
		$.each(right.children(),function(){
			setEventByUnit(this, right, left,options);
		});
	},
	setEvents = function($this,options){
		var left = $this.find('select:eq(0)'),
			right = $this.find('select:eq(1)');

		$this.find('a:eq(0)').off('click').on('click',function(e){
			eventFun(left,right,options);
			e.stopPropagation();
		});
		$this.find('a:eq(1)').off('click').on('click',function(e){
			eventFun(right,left,options);
			e.stopPropagation();
		});
		dblClickFun($this,options);
	},
	getValue = function(separator){
		var v = '';
		if(undefined === separator){
			separator = ';';
		}
		$.each(this.privates.rightSelf.children(),function(){
			v += this.value + separator;
		});
		if('' !== v && v.length > 0){
			v = v.substring(0,v.length - separator.length);
		}else{
			v = undefined;
		}
		return v;
	}
	$.fn.selectionBc.getText = function(selector,separator){
		separator = separator || ',';
		var $select = $(selector).find('select:eq(1)'),
			value = '';
		$.each($select.get(0).options,function(){
			value += this.text + separator;
		});
		value = value ? value.substring(0,value.length - separator.length) :null;
		return value;
	};
	$.fn.selectionBc.getValue = function(selector,separator){
		separator = separator || ',';
		var $select = $(selector).find('select:eq(1)'),
			value = '';
		$.each($select.get(0).options,function(){
			value += this.value + separator;
		});
		value = value ? value.substring(0,value.length - separator.length) : null;
		return value;
	};
	$.fn.selectionBc.defaults = {
			size : 5,
			action : {
				type : 'post',
				dataType : 'json',
				url : undefined,
				params : undefined
			},
			width : 0,
			type : 'multiple',//single
			backFun : undefined,

			leftTitle : '未选择',
			rightTitle : '已选择',
			fontSize : 12,
			height : '22',
			selectHeight : '100px',

			parentClassName : 'user_sela_body',
			parentCss : 'font-size:0; ',

			//titleClassName : 'user_title',
			titleCss : 'font-weight: bold;color: #fff;font-size: 0;',

			titleSpanCss : 'background: none repeat scroll 0 0 rgba(0, 0, 0, 0.1);border-radius: 4px 4px 0 0;display: inline-block;text-align: center;width: 44%;',
			titleSpanFirstCss : 'background: none repeat scroll 0 0 rgba(0, 0, 0, 0.1);border-radius: 4px 4px 0 0;display: inline-block;margin-right: 12%;text-align: center;width: 44%;',

			//leftClassName : 'user_sela dmb_bg',
			leftOrRightCss : 'display:inline-block; font-size:12px; width:44%;vertical-align:middle;',

			//rightClassName : 'user_sela dmb_bg',

			//btnClassName : 'user_as',
			btnCss : 'display:inline-block; font-size:12px; vertical-align:middle; width:12%; text-align:center;',

			btnACss : 'display:block; border:none; background:none; font-size:24px; color:rgba(0,0,0,0.4); width:100%;cursor:pointer;',

			rightImage : 'data:image/.png;base64,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',
			leftImage : 'data:image/.png;base64,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'
	}
})(jQuery);