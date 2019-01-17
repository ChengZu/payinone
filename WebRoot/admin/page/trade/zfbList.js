layui.config({
	base : "js/"
}).use(['form','layer','jquery','laypage'],function(){
	var form = layui.form(),
		layer = parent.layer === undefined ? layui.layer : parent.layer,
		laypage = layui.laypage,
		$ = layui.jquery;

	//加载页面数据
	//分页  
	var nums = 10; //每页出现的数据量
	var newsData = '';
	var tradeNum = 0;
	InitData(1,nums,function(){
		newsList(newsData);
	});



	//查询
	$(".search_btn").click(function(){
		var newArray = [];
		if($(".search_input").val() != ''){
			var index = layer.msg('查询中，请稍候',{icon: 16,time:false,shade:0.8});

			$.get("../../../GetZFBTrade.admin?outTradeNo="+$(".search_input").val(), function(data){
				newsData = eval('['+data+']');

				tradeNum=1;
				newsList(newsData);	
	            layer.close(index);

			})


		}else{
			layer.msg("请输入需要查询的内容");
		}
	})

	//添加文章
	$(".newsAdd_btn").click(function(){
		var index = layui.layer.open({
			title : "添加文章",
			type : 2,
			content : "newsAdd.html",
			success : function(layero, index){
				layui.layer.tips('点击此处返回文章列表', '.layui-layer-setwin .layui-layer-close', {
					tips: 3
				});
			}
		})
		//改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
		$(window).resize(function(){
			layui.layer.full(index);
		})
		layui.layer.full(index);
	})

	//推荐文章
	$(".recommend").click(function(){
		var $checkbox = $(".news_list").find('tbody input[type="checkbox"]:not([name="show"])');
		if($checkbox.is(":checked")){
			var index = layer.msg('推荐中，请稍候',{icon: 16,time:false,shade:0.8});
            setTimeout(function(){
                layer.close(index);
				layer.msg("推荐成功");
            },2000);
		}else{
			layer.msg("请选择需要推荐的文章");
		}
	})

	//审核文章
	$(".audit_btn").click(function(){
		var $checkbox = $('.news_list tbody input[type="checkbox"][name="checked"]');
		var $checked = $('.news_list tbody input[type="checkbox"][name="checked"]:checked');
		if($checkbox.is(":checked")){
			var index = layer.msg('审核中，请稍候',{icon: 16,time:false,shade:0.8});
            setTimeout(function(){
            	for(var j=0;j<$checked.length;j++){
            		for(var i=0;i<newsData.length;i++){
						if(newsData[i].newsId == $checked.eq(j).parents("tr").find(".news_del").attr("data-id")){
							//修改列表中的文字
							$checked.eq(j).parents("tr").find("td:eq(3)").text("审核通过").removeAttr("style");
							//将选中状态删除
							$checked.eq(j).parents("tr").find('input[type="checkbox"][name="checked"]').prop("checked",false);
							form.render();
						}
					}
            	}
                layer.close(index);
				layer.msg("审核成功");
            },2000);
		}else{
			layer.msg("请选择需要审核的文章");
		}
	})

	//批量删除
	$(".batchDel").click(function(){
		var index = layer.msg('刷新中，请稍候',{icon: 16,time:false,shade:0.8});
		InitData(1,nums,function(){
			layer.close(index);
			newsList(newsData);
		});
	})

	//全选
	form.on('checkbox(allChoose)', function(data){
		var child = $(data.elem).parents('table').find('tbody input[type="checkbox"]:not([name="show"])');
		child.each(function(index, item){
			item.checked = data.elem.checked;
		});
		form.render('checkbox');
	});

	//通过判断文章是否全部选中来确定全选按钮是否选中
	form.on("checkbox(choose)",function(data){
		var child = $(data.elem).parents('table').find('tbody input[type="checkbox"]:not([name="show"])');
		var childChecked = $(data.elem).parents('table').find('tbody input[type="checkbox"]:not([name="show"]):checked')
		if(childChecked.length == child.length){
			$(data.elem).parents('table').find('thead input#allChoose').get(0).checked = true;
		}else{
			$(data.elem).parents('table').find('thead input#allChoose').get(0).checked = false;
		}
		form.render('checkbox');
	})

	//是否展示
	form.on('switch(isShow)', function(data){
		var index = layer.msg('修改中，请稍候',{icon: 16,time:false,shade:0.8});
        setTimeout(function(){
            layer.close(index);
			layer.msg("展示状态修改成功！");
        },2000);
	})
 
	//操作
	$("body").on("click",".news_edit",function(){  //编辑
		layer.alert('您点击了文章编辑按钮，由于是纯静态页面，所以暂时不存在编辑内容，后期会添加，敬请谅解。。。',{icon:6, title:'文章编辑'});
	})

	$("body").on("click",".news_collect",function(){  //收藏.
		var str='';
		var _this = $(this);
		for(var i=0;i<newsData.length;i++){
			if(newsData[i].outTradeNo == _this.attr("data-id")){
				str += JSON.stringify(newsData[i]);
			}
		}
		layer.alert(str,{icon:6, title:'订单详情'});
	})

	$("body").on("click",".news_del",function(){  //删除
		var _this = $(this);
		layer.confirm('确定删除此信息？',{icon:3, title:'提示信息'},function(index){
			//_this.parents("tr").remove();
			for(var i=0;i<newsData.length;i++){
				if(newsData[i].newsId == _this.attr("data-id")){
					newsData.splice(i,1);
					newsList(newsData);
				}
			}
			layer.close(index);
		});
	})

	function InitData(curr,nums,fun){
		$.get("../../../GetZFBTradeList.admin?start="+(curr*nums-nums)+"&num="+nums, function(data){
			newsData = eval(data);
			$.get("../../../GetZFBTradeCount.admin", function(data){
				var trades = eval('('+data+')');
				tradeNum=trades.count;
				fun();
			})
			
		})
			
	}
	
	function searchTrade(id){
		$.get("../../../GetZFBTrade.admin?outTradeNo="+id, function(data){
			newsData = eval(data);

			tradeNum=1;

			fun();
		})
	}
	
	function newsList(that){
		//渲染数据
			
		function renderDate(data,curr){
			var dataHtml = '';

			currData = data;;
	
			if(currData.length != 0){
				for(var i=0;i<currData.length;i++){
					dataHtml += '<tr>'
			    	+'<td><input type="checkbox" name="checked" lay-skin="primary" lay-filter="choose"></td>'
			    	+'<td align="left">'+currData[i].outTradeNo+'</td>'
			    	+'<td>'+currData[i].body+'</td>'
			    	+'<td>'+currData[i].totalAmount+'</td>'
					+'<td>'+currData[i].createTime+'</td>'
			    	+'<td>'+currData[i].payState+'</td>'
			    	+'<td>'
					+  '<a class="layui-btn layui-btn-normal layui-btn-mini news_collect" data-id="'+currData[i].outTradeNo+'"><i class="layui-icon">&#xe600;</i> 详情</a>'
					+  '<a class="layui-btn layui-btn-danger layui-btn-mini news_del" data-id="'+currData[i].outTradeNo+'"><i class="layui-icon">&#xe640;</i> 退款</a>'
			        +'</td>'
			    	+'</tr>';
				}
			}else{
				dataHtml = '<tr><td colspan="8">暂无数据</td></tr>';
			}
			
		    return dataHtml;
		}

		
		if(that){
			newsData = that;
		}
		laypage({
			cont : "page",
			pages : Math.ceil(tradeNum/nums),
			jump : function(obj){
				if(that){
					that=null;
					$(".news_content").html(renderDate(newsData,obj.curr));
					$('.news_list thead input[type="checkbox"]').prop("checked",false);
					form.render();
				}else{
				var index = layer.msg('刷新中...',{icon: 16,time:false,shade:0.8});
				InitData(obj.curr,nums,function(){
					layer.close(index);
					$(".news_content").html(renderDate(newsData,obj.curr));
					$('.news_list thead input[type="checkbox"]').prop("checked",false);
			    	form.render();
					
				});
			}
		
			}
		})
		
		
	}
})
