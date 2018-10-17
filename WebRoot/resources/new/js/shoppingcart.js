//购物车
//单品页面
var isWeixin = navigator.userAgent.toLowerCase().indexOf("micromessenger");
//购物车去结算页面---canGiftFlag:是否验证有赠品未领取或已选赠品领光，false：验证；true：直接结算(默认)
	function toCheckout(canGiftFlag){			
	var canGiftFlag = canGiftFlag?canGiftFlag:false;	
	var display=$(".shopCartSubmitone").css("display");	
	var tradecurrency="";
	var usableIntegral=""
	if(display=="block"){		
		tradecurrency = $('.shopCartSubmitone').attr("tradecurrency");
		usableIntegral = $(".shopCartSubmitone").attr("dataCarOne");
	}else{		
		var tradecurrencys = $("#shopCartSub").val();
		
		if(tradecurrencys=="0"){			
			tradecurrency="cny";
		}else{
			tradecurrency=tradecurrencys;
		}
		usableIntegral = $("#shopCartSubO").val();
	}			
	var checkIndex=true;
	// $('.shopCountN').each(function(index){
	// 	if(!$(this).hasClass('shopCountNo')){
	// 		var thisVal=$(this).val();
	// 		if(thisVal>9){
	// 			checkIndex=false;
	// 		}
	// 	}
	// });
	$('.shopcart').find('.shopCartC').each(function(index){
		if($(this).attr('disabled')!=='disabled'){
			if($(this).attr('checked')=='checked'){
				var thisVal=$(this).parents('.pro_item_change').find('.shopCountN').val();
				if(thisVal>10){
					checkIndex=false;
				}
			}
			
		}
	})
	

	if(checkIndex){		
		window.location.href= context + "/pay/checkout.sc?tradecurrency="+tradecurrency+"&first=1&canGiftFlag="+canGiftFlag+"&node=E_GWC_QJS&usableIntegral="+usableIntegral+"";
	}else{
		YouGouWap.base.showWapTip({
			type:'tip',
			msg:'同一件商品限购10件',
			okButtonTxt:'知道了',
			isOkButton:true,
			autoHide:false
		});
	}
	
}

$(function(){
	//加入收藏
	$('.fav').click(function(){
		alert("df");
		/*$.ajax('',function(){
			showWapTip({
				msg:'收藏成功',
				autoHide:true
			})
		});*/
		var flag = $(this).attr('flag')*1;
		var _f = new Favorites();
		var no = $(this).attr('no');
		if(flag >0){
			_f.delCallback = function(){
				window.location.reload();
			};
			_f.doDelete(no);
		}else{
			_f.addCallback = function(){
				window.location.reload();
			};
			_f.add(no);
		}
	});
	
	//删除购物车商品
	$('.delpro').click(function(){
		var me = $(this);
		YouGouWap.base.showWapTip({
			title:'温馨提示',
			type:'confirm',
			msg:'您确认要删除该商品么?',
			confirmCallback:confirmWF
		});
		function confirmWF(){
			//清除cookie
			var productNo = me.attr("data");
			var tradeCurrency = me.attr("tradeCurrency");
			if(typeof productNo != 'undefined'){
				var url = (context + "/delshoppingcart.sc");
				var param={"productId" : productNo, "tradeCurrency" : tradeCurrency};
				$.post(url, param, function(data){
					var flag = data.flag*1;
					if(flag > 0){
						YouGouWap.base.showWapTip({
							msg:'删除成功',
							autoHide:true
						});
						//console.info(data);
						if(tradeCurrency=='HKD' || tradeCurrency=='hkd'){
							window.location.href= (context + "/shoppingcart.sc?shoppingType=HKD");
						}else if(tradeCurrency=='KRW_ZF' || tradeCurrency=='krw_zf'){
                            window.location.href= (context + "/shoppingcart.sc?shoppingType=KRW_ZF");
                        }else if(tradeCurrency=='KRW' || tradeCurrency=='krw'){
                            window.location.href= (context + "/shoppingcart.sc?shoppingType=KRW");
                        }else{
							window.location.href= (context + "/shoppingcart.sc");
						}
						
					}else{
						YouGouWap.base.showWapTip({
							msg:'删除购物车失败,请重新删除',
							autoHide:true
						});
					}
				}, "json")
			}
		}
	});
	
	
	//修改购物车货品数量
	$('.proNO').blur(function(){
		var productNo = $(this).attr('data');
		var tradeCurrency = $(this).attr("tradeCurrency");
		if(!tradeCurrency){
			tradeCurrency = "";
		}
		var num = $(this).val()*1;
		if(num > 9){
			$(this).val(9);
			YouGouWap.base.showWapTip({
				msg:'同一款商品限购10件!',
				autoHide:true
			});
		}
		if(num < 1){
			$(this).val(1);
		}
		var url = (context + "/setshoppingcart.sc");
		var param={"productId" : productNo, "productNum" : $(this).val()*1, "tradeCurrency" : tradeCurrency, "r" : new Date().getTime()};
		$.post(url, param, function(data){
			var flag = data.flag*1;
			if(flag < 0){
				alert(data.msg);
			}else if(flag > 0){
				$("#amount"+tradeCurrency).text(data.amount);//修改金额
				$("#downnum"+tradeCurrency).text("数量：" + data.num + "件");//修改数量
				if(tradeCurrency == "hkd"){
					$("#tit").text("购物车(" + (parseInt(data.num) + parseInt(cnynum)) + ")件");
				}else{
					$("#tit").text("购物车(" + (parseInt(data.num) + parseInt(hkdnum)) + ")件");
				}
			}
		}, "json");
	});
	
	
	//购物车去结算页面
	$('input[name="checkout"]').click(function(){
		toCheckout(false);
	});
	function tiaoCM(){
		//var num=Math.random();
		location.hash="#chiMa";
		location.replace(location.href);		
	}
	/*if (window.locaiton.hash !=="") {
		 window.location.hash ="";
	}*/
	//加入购物车
	$("#addShop").click(function (){
		var productNo = $("#sizeNo").val();
		var productId = $("#productid").val();
		var activeId = $("#activeId").val();
		var _this=$(this);
		var tradecurrency = $("#tradecurrency").val();
		if("undefined" == typeof tradecurrency){
			tradecurrency='cny';
		}
		if(productId==undefined || productId=='undefined'){
			productId="";
		}
		if(activeId==undefined || activeId=='undefined'){
			activeId="";
		}
		if($('.proDetailSelect').css('display')=='block'){
			if(productNo*1 <= 0){
				YouGouWap.base.showWapTip({
					msg:'请选择尺码!',
					autoHide:true
				});
				return;
			}
			if($.trim(productNo).length < 11){
				YouGouWap.base.showWapTip({
					msg:'商品不合法，请重新选择商品!',
					autoHide:true
				});
				return;
			}
			addShopF('D_JRGWC_1');
		}else{			
			if(productNo*1 <= 0 || $.trim(productNo).length < 11){
				btnShow('addShop',_this);						
				ygDetailPage.layoutShow();
			}else{
				addShopF('D_JRGWC_1');
			}
			
		}
	});
	$("#confirmeAddShop").click(function (){
		addShopF('D_JRGWC_2');
	});
	function addShopF(value){
		var productNo = $("#sizeNo").val();
		var productId = $("#productid").val();
		var activeId = $("#activeId").val();
		var tradecurrency = $("#tradecurrency").val();		
		if("undefined" == typeof tradecurrency){
			tradecurrency='cny';
		}
		if(productId==undefined || productId=='undefined'){
			productId="";
		}
		if(activeId==undefined || activeId=='undefined'){
			activeId="";
		}
		if(productNo*1 <= 0){
			YouGouWap.base.showWapTip({
				msg:'请选择尺码!',
				autoHide:true
			});
			// setTimeout(function(){
	  //         tiaoCM();

	  //       },1000)
			return;
		}
		if($.trim(productNo).length < 11){
			YouGouWap.base.showWapTip({
				msg:'商品不合法，请重新选择商品!',
				autoHide:true
			});
			return;
		}
		if(typeof productNo != 'undefined' && typeof _gaq != 'undefined'){
			var url = (context + "/addshoppingcart.sc");
			var urlAddShop = rootPath+"/updateShoppingCartStatus.sc";
			var num =1;
			if($("#saledata").length > 0){
				var param={"productNo" : productNo, "productNum" : num,"productid":productId,"activeId":activeId,node:value,"activeType":$("#saledata").val()};
			}else{
				var param={"productNo" : productNo, "productNum" : num,"productid":productId,"activeId":activeId,node:value,"activeType":''};
			}
			$.post(url, param, function(data){
				var flag = data.flag*1;
				if(flag > 0 || flag == -1){
					//ygDetailPage.layoutHide();
					YouGouWap.base.showWapTip({
						msg:'加入购物车成功!',
						autoHide:true,
						time:2000
					});
					_gaq.push(['_trackPageview','/PageAction/Buy/add_to_cart?page=' + document.location.pathname + document.location.search + '&from=' + document.referrer]);
					var getProId=$('#productid').attr('oldProId');
					var currentLink=window.location.href;
					setTimeout(function(){
						window.location.href=currentLink.replace(getProId,productId);
					}, 2000);
				}else if(flag == 0){
					YouGouWap.base.showWapTip({
						msg:'仅限预约用户购买',
						autoHide:true
					});
				}
				else{
					alert(data.msg);
				}
			}, "json");

			return false;
		}
		_hmt.push(['_trackEvent', '商品页面加入购物车', 'click', 'gotoBuyCar']);
	}
	
	//立即购买
	var displayproSelect=$(".proDetailSelect").css("display");	
	if($("#buynow").length>0){
		$("#buynow").click(function (){			
			var thiss=$(this);	
			btnShow('buynow',thiss);
			ygDetailPage.layoutShow();
		});
	}
	if($("#buy").length>0){
		$("#buy").click(function (){
			var thiss=$(this);			
			btnShow('buy',thiss);
			ygDetailPage.layoutShow();
		});
	}
	if($("#buy1").length>0){
		$("#buy1").click(function (){
			var thiss=$(this);			
			btnShow('buy1',thiss);
			ygDetailPage.layoutShow();
		});
	}
	if($("#buy2").length>0){
		$("#buy2").click(function (){
			var thiss=$(this);			
			btnShow('buy2',thiss);
			ygDetailPage.layoutShow();
		});
	}	
	$('#confirmeBuy').on('click',function(){
		if($("#buynow").length>0){
			buynow();
		}else{
			buyF('D_QJS_2');
		}
	});		
	function btnShow(btnName,thiss){			
		var productNo = $("#sizeNo").val();
		var productId = $("#productid").val();
		var activeId = $("#activeId").val();
		var tradecurrency = $("#tradecurrency").val();		
		$('#waitLoadingBg').show();		
        if($(".proNumberNoSale").css("display")=="block"){
        	$('.proNoBuy').show();
        }
		$('.proDetailSelect').show();			
		if(productNo*1 <= 0){	
			 $("#buy").addClass("buyNow");				
			 if(btnName=='addShop'){
				$('.prodtl_buyNew .btn_brow_add').hide();
				$('.prodtl_buyNew .btn_org_buy').hide();	
		    	$('#confirmeAddShop').show();	
			 }else{
			 	if(thiss.hasClass('buytc')){
					YouGouWap.base.showWapTip({
						msg:'请选择尺码!',
						autoHide:true
					});				
					return;
				}else{						
					$('.prodtl_buyNew .btn_brow_add').hide();
					$('.prodtl_buyNew .btn_org_buy').hide();	
					$('#confirmeBuy').show();	
				}
			 }
			 
		}else{
			if(btnName=='addShop'){
				addShopF()
			}else if(btnName=='buynow'){
		    	buynow();
		    }else if(btnName=='buy'){
		    	buyF('D_QJS_1');
		    }			
		}
		
		
		
		/*if(btnName=='addShop'){
			$('.prodtl_buyNew .btn_brow_add').hide();
			$('.prodtl_buyNew .btn_org_buy').hide();	
	    	$('#confirmeAddShop').show();	
		}else{			
			if(productNo*1 <= 0){	
				$("#buy").addClass("buyNow");					
				if(thiss.hasClass('buytc')){
					YouGouWap.base.showWapTip({
						msg:'请选择尺码!',
						autoHide:true
					});				
					return;
				}else{						
					$('.prodtl_buyNew .btn_brow_add').hide();
					$('.prodtl_buyNew .btn_org_buy').hide();	
					$('#confirmeBuy').show();	
				}
			}else{
				if(btnName=='addShop'){
					addShopF()
				}else if(btnName=='buynow'){
			    	buynow();
			    }else if(btnName=='buy'){
			    	buyF('D_QJS_1');
			    }			
			}
		}
		
	  
	   * if(productNo*1 <= 0){
			$("#buy").addClass("buyNow");		
			if(btnName=='addShop'){
				$('.prodtl_buyNew .btn_brow_add').hide();
				$('.prodtl_buyNew .btn_org_buy').hide();	
		    	$('#confirmeAddShop').show();	
			}
			if(thiss.hasClass('buytc')){
				YouGouWap.base.showWapTip({
					msg:'请选择尺码!',
					autoHide:true
				});				
				return;
			}else{						
				$('.prodtl_buyNew .btn_brow_add').hide();
				$('.prodtl_buyNew .btn_org_buy').hide();	
				$('#confirmeBuy').show();	
			}
		}else{
			if(btnName=='addShop'){
				addShopF()
			}else if(btnName=='buynow'){
		    	buynow();
		    }else if(btnName=='buy'){
		    	buyF('D_QJS_1');
		    }		
		}	
		
		
		/*alert(btnName);
	   *  if(thiss.hasClass('buytc')){			   
			if(productNo*1 <= 0){	
				$("#buy").addClass("buyNow");	
				YouGouWap.base.showWapTip({
					msg:'请选择尺码!',
					autoHide:true
				});				
				return;
			    if(btnName=='addShop'){
			    	$('.prodtl_buyNew .btn_brow_add').hide();
					$('.prodtl_buyNew .btn_org_buy').hide();	
			    	$('#confirmeAddShop').show();		    	
			    }else{		   
			    	$('#confirmeBuy').show();
			   }
			}else{
				if(btnName=='addShop'){
					addShopF()
				}else if(btnName=='buynow'){
			    	buynow();
			    }else if(btnName=='buy'){
			    	buyF('D_QJS_1');
			    }			
			}
		}else{			
			if(productNo*1 <= 0){	
				 $("#buy").addClass("buyNow");	
				 $('.prodtl_buyNew .btn_brow_add').hide();
				 $('.prodtl_buyNew .btn_org_buy').hide();	
			    if(btnName=='addShop'){
			    	$('#confirmeAddShop').show();		    	
			    }else{		   
			    	$('#confirmeBuy').show();
			   }
			}else{
				if(btnName=='addShop'){
					addShopF()
				}else if(btnName=='buynow'){
			    	buynow();
			    }else if(btnName=='buy'){
			    	buyF('D_QJS_1');
			    }			
			}
		}*/
		
		
	}
	function buyF(value){		
		//location.replace(location.href);
		var productNo = $("#sizeNo").val();
		var productId = $("#productid").val();
		var activeId = $("#activeId").val();
		var tradecurrency = $("#tradecurrency").val();
		var urlAddShop = rootPath+"/updateShoppingCartStatus.sc";
		if("undefined" == typeof tradecurrency){
			tradecurrency='cny';
		}
		if(productId==undefined || productId=='undefined'){
			productId="";
		}
		if(activeId==undefined || activeId=='undefined'){
			activeId="";
		}		
		if(productNo*1 <= 0){
			YouGouWap.base.showWapTip({
				msg:'请选择尺码!',
				autoHide:true
			});
			// setTimeout(function(){
	  //         tiaoCM();  
	  //       },1000)
			return;
		}
		if($.trim(productNo).length < 11){
			YouGouWap.base.showWapTip({
				msg:'商品不合法，请重新选择商品!',
				autoHide:true
			});
			return;
		}
		if(typeof productNo != 'undefined' && typeof _gaq != 'undefined'){			
			var url = (context + "/pay/checkout.sc");
			var num =1;
			var buyNow = "now";
			_gaq.push(['_trackPageview','/PageAction/Buy/add_to_cartdown']);
			if($("#saledata").length > 0){
				var activeType=$("#saledata").val();
				window.location.href= url+"?productNo="+productNo+"&productId="+productId+"&first=1"+"&productNum="
                 + num + "&buyNow=rightnow" + "&commodityid=" + productId + "&activeId=" + activeId + "&tradecurrency=" + tradecurrency+"&node="+value+"&activeType="+activeType;
			}else{
				window.location.href= url+"?productNo="+productNo+"&productId="+productId+"&first=1"+"&productNum="
                 + num + "&buyNow=rightnow" + "&commodityid=" + productId + "&activeId=" + activeId + "&tradecurrency=" + tradecurrency+"&node="+value;
			}		
			
			//return false;
		}

		/*
		$.ajax({
			type:"POST",
			url:urlAddShop,
			data: "selectType=all&status=0&shoppingCartType="+tradecurrency+"&productId="+productId+"&productNo="+productNo+"&buyNow=rightnow" +"&id=" + (new Date()).valueOf(),
			success:function(data){
				$.ajax({
					type:"POST",
					url:urlAddShop,
					data: "selectType=1&status=1&shoppingCartType="+tradecurrency+"&productId="+productId+"&productNo="+productNo+"&buyNow=rightnow"+"&id=" + (new Date()).valueOf(),
					success:function(data){
						//return false;
						if(typeof productNo != 'undefined'){
							var url = (context + "/pay/checkout.sc");
							var num =1;
							var buyNow = "now"
							window.location.href= url+"?productNo="+productNo+"&productId="+productId+"&productNum="+num+"&buyNow=rightnow"+"&commodityid="+productId+"&activeId="+activeId;
							return false;
						}
					}
				});
			}
		})
		*/
		_hmt.push(['_trackEvent', '商品页面立即购买', 'click', 'singleBuy']);
	}

	

	function buynow(){
		var productNo = $("#sizeNo").val();//商品尺码
		var productId = $("#productid").val();//商品编号
		var activeId = $("#activeId").val();
		var tradecurrency = $("#tradecurrency").val();
		var urlAddShop = rootPath+"/updateShoppingCartStatus.sc";
		if("undefined" == typeof tradecurrency){
			tradecurrency='cny';
		}
		if(productId==undefined || productId=='undefined'){
			productId="";
		}
		if(activeId==undefined || activeId=='undefined'){
			activeId="";
		}
		if(productNo*1 <= 0){
			YouGouWap.base.showWapTip({
				msg:'请选择尺码!',
				autoHide:true
			});
			return;
		}

		if($.trim(productNo).length < 11){
			YouGouWap.base.showWapTip({
				msg:'商品不合法，请重新选择商品!',
				autoHide:true
			});
			return;
		}
		
		if(typeof productNo != 'undefined' && typeof _gaq != 'undefined'){
			var url = (context + "/pay/checkout.sc");
			var num =1;
			var buyNow = "now";
			_gaq.push(['_trackPageview','/PageAction/Buy/add_to_cartdown']);
			window.location.href= url+"?productNo="+productNo+"&productId="+productId+"&productNum="+num+"&first=1"
                +"&buyNow=rightnow"+"&commodityid="+productId+"&activeId="+activeId + "&tradecurrency=" + tradecurrency+"&node=D_LJGM_NULL";
			//return false;
		}
		/*
		$.ajax({
			type:"POST",
			url:urlAddShop,
			data: "selectType=all&status=0&shoppingCartType="+tradecurrency+"&productId="+productId+"&productNo="+productNo+"&buyNow=rightnow"+"&id=" + (new Date()).valueOf(),
			success:function(data){
				$.ajax({
					type:"POST",
					url:urlAddShop,
					data: "selectType=1&status=1&shoppingCartType="+tradecurrency+"&productId="+productId+"&productNo="+productNo+"&buyNow=rightnow"+"&id=" + (new Date()).valueOf(),
					success:function(data){
						if(typeof productNo != 'undefined'){
							var url = (context + "/pay/checkout.sc");
							var num =1;
							var buyNow = "now"
							window.location.href= url+"?productNo="+productNo+"&productId="+productId+"&productNum="+num+"&buyNow=rightnow"+"&commodityid="+productId+"&activeId="+activeId;
							return false;
						}
					}
				});
			}
		})
		*/
	}	
	
	function sumitNum(name, number){
		if(typeof isCleanCookie == "boolean")isCleanCookie=false;
		var couponNum = $('#couponcard').val();
		var giftNum = $('#cardid').val();
		var address = $('#address').val();
		var payway = $(".payway[checked='checked']").val();
		
		var tradecurrency = $('#tradecurrency').val();
		var productNo = $('#productNo').val();
		var productNum = $('#productNum').val();
		var url;
		if(tradecurrency == 'hkd'){
			url = context + '/pay/checkout.sc?address=' + address +'&payway=' + payway + '&tradecurrency=' + tradecurrency + '&productNo=' + productNo + '&productNum=' + productNum;
		}else{
			url = context + '/pay/checkout.sc?address=' + address +'&payway=' + payway;
		}
		//var url = context + '/pay/checkout.sc?address=' + address +'&payway=' + payway;
		if(typeof name !=  'undefined'){
			if('coupon' == name){
				url = url + '&couponcard=' + number + '&cardid=' + giftNum;
				
			}else if('giftcard' == name){
				url = url  + '&couponcard=' + couponNum + '&cardid=' + number;
			}
			window.location.href = url+"&node=E_GWC_QJS";
		}
	};
	
	$('.gftcpn_lst .areacheck').click(function(){
		var _this=$(this);
		var number = _this.attr('data');
		var name = _this.attr('name');
		sumitNum(name, number);
	});

	//选择支付方式
	$('.payway').click(function(){
		var couponNum = $('#couponcard').val();
		var giftNum = $('#cardid').val();
		var address = $('#address').val();
		var payway = $(this).attr('data');
		
		var tradecurrency = $('#tradecurrency').val();
		var productNo = $('#productNo').val();
		var productNum = $('#productNum').val();
		
		//如果是货到付款,判断手机验证
/*		if('1' == payway){
			var mobilecheck = $("#mobilecheck").val();
			if(mobilecheck == 'false'){
				window.location.href = context + "/my/gotoVerificationPhoneNumber.sc";
				return false;
			}
		}*/
		var url = context+'/pay/checkout.sc?address=' + address +'&payway=' + payway;
		url  +=   '&couponcard=' + couponNum + '&cardid=' + giftNum;
		if(tradecurrency == 'hkd'){
			url = url + '&tradecurrency=' + tradecurrency + '&productNo=' + productNo + '&productNum=' + productNum;
		}
		window.location.href = url+"&node=E_GWC_QJS";
	});
	
	$('.inpsmt').click(function(){
		var _this=$(this);
		var number = $.trim(_this.siblings('.inptxt').val());
		var name = _this.attr('name');
		sumitNum(name, number);
		var couponNum = $('#couponcard').val();
		var giftNum = $('#cardid').val();
		var address = $('#address').val();
		var payway = $(".payway[checked='checked']").val();
		if(typeof isCleanCookie == "boolean")isCleanCookie=false;
		var url = context + '/pay/checkout.sc?address=' + address +'&payway=' + payway;
		
		var tradecurrency = $('#tradecurrency').val();
		var productNo = $('#productNo').val();
		var productNum = $('#productNum').val();
		
		if(typeof name !=  'undefined'){
			if('coupon' == name){
				url = url + '&couponNumber=' + number + '&cardid=' + giftNum;
				
			}else if('giftcard' == name){
				url = url + '&couponcard=' + couponNum + '&cardNumber=' + number;
			}
			if(tradecurrency == 'hkd'){
				url = url + '&tradecurrency=' + tradecurrency + '&productNo=' + productNo + '&productNum=' + productNum;
			}
			window.location.href = url;
		}
		return false;
	});
	
	//立即预约
	$("#appointment").click(function(){
		var productNo = $("#sizeNo").val();
		var productId = $("#productid").val();
		var activeId = $("#activeId").val();
		var tradecurrency = $("#tradecurrency").val();
		var url = (context + "/appointmentImmediately.sc");
		if(productId==undefined || productId=='undefined'){
			productId="";
		}
		if(activeId==undefined || activeId=='undefined'){
			activeId="";
		}
		/*if($.trim(productNo).length < 11){
			YouGouWap.base.showWapTip({
				msg:'商品不合法，请重新选择商品!',
				autoHide:true
			});
			return;
		}*/
		if(typeof productId != 'undefined'){
			$.ajax({
				type:"get",
				url:url,
				data:"productid="+productId+"&activeId="+activeId,
				success:function(data){
					var obj=$.parseJSON(data);
					console.info(data);
					if(obj.status=="logout"){
						var redirect = (context + "/toLogin.sc");
						window.location.href=redirect;
					}else if(obj.status=="success"){
						showVerificationDialog();
					}else if(obj.status=="fail"){
						YouGouWap.base.showWapTip({
							msg:obj.errorDesc,
							autoHide:true
						});
						return false;
					}else{
						YouGouWap.base.showWapTip({
							msg:"预约发生异常，请重试",
							autoHide:true
						});
						return false;
					}
				}
			});
			return false;
		}
	});
	
});
/*goods.js*/
/*弹窗*/
var mDialog=function(options){
	var opts = { 
		title:'',
		content:'',
		mask:true,
		jt:false,
		jtposition:'bottom',
		jtright:'9%',
		jtleft:null,
		closable:true,
		fix:true,
		left:'50%',
		right:null,
		top:'50%',
		close:null,
		success:null,
		width:'auto'
	}; 
	opts = $.extend(opts, options || {});
	var popHeight,popWidth;
	var mask=opts.mask;
	var title=opts.title;
	var jt=opts.jt;
	var content=opts.content;
	var closable=opts.closable;
	var fix=opts.fix;
	var _left=opts.left;
	var _right=opts.right;
	var _top=opts.top;
	var _jtposition=opts.jtposition;
	var _jtright=opts.jtright;
	var _jtleft=opts.jtleft;
	var mpop=$('<section class="mpop">');
	var m=$('<div class="mpop_mask">');
	var c=$('<div class="mpop_c">');
	var hd=$('<div class="mpop_c_hd">');
	var clos=$('<span class="mpop_close">');
	var bd=$('<div class="mpop_c_bd">');
	var jtHtml='<i class="jt"></i>';
	
	if(_right==null){c.css({left:_left,top:_top});}
	
	if(mask){
		mpop.append(m);
	}
	if(title){
		hd.append('<h2>'+title+'</h2>');
	}
	if(closable){
		hd.append(clos);
	}
	if(content){
		bd.html(content);	
	}
	c.append(hd).append(bd);
	mpop.append(c);
	$('.screen_wrap').append(mpop);	
	m.width($('.screen_wrap').width());
	if(fix){
		if(_left=='50%'){
			setTimeout(function(){
				popWidth=c.width()/2;
				c.css({'margin-left':-popWidth});
			},0);
		}
		if(_top=='50%'){
			popHeight=c.height()/2;
			c.css({'margin-top':-popHeight});
		}
		c.css('position','fixed');
	}else{
		c.css('position','absolute');
	}
	c.css('width',opts.width);
	if(jt){
		c.append(jtHtml);
		var _jiantou=c.find('.jt');
		_jtleft==null?_jiantou.css('right',_jtright):_jiantou.css('left',_jtleft);
		popHeight=c.height();
		c.css({right:_right,top:_top});
		switch(_jtposition){
			case 'bottom':
			_jiantou.css({'bottom':-11});
			c.css('margin-top',-popHeight-15);
			break;
			case 'top':
			_jiantou.addClass('jt_up').css({'top':-11});
			c.css('margin-top',30);
			break;
		}
	}
	if(!title&&!closable){
		hd.remove();
	}
	
	if(typeof (opts.success)=="function"){
		opts.success();
	}
	
	clos.click(function(){
		if(typeof (opts.close)=="function"){
			opts.close();
		}
		mpop.remove();
	});
}
/*tab切换*/
var winHe = $(window).height();
$('.tab_bd .tab_item').css('min-height',winHe+30+'px');
$.fn.clickTab=function(){
	var _this;
	return this.each(function(){
		_this=$(this);
		var _tabItem=_this.find('.tab_bd .tab_item');
		_this.find('.tab_hd li').click(function(){
			detailTabClick($(this),_tabItem)
		});
	});
}
function detailTabClick(wrap,_tabItem){
	var _this=wrap;
	var _index=_this.index();
	var winH = $(window).height();
	var winW = $(window).width();
	var boxH = $('.prodtl_bd').height();
	_this.addClass('curr').siblings().removeClass('curr');
	var _thisTabCnt = _tabItem.eq(_index);
	_thisTabCnt.removeClass('none').siblings().addClass('none');
	if(_this.attr('ajaxurl')&&_this.attr('ajaxurl')!=''){
		$.get(_this.attr('ajaxurl'),function(d){
			_thisTabCnt.html(d);
			_this.attr('ajaxurl','');
			if(_this.attr('callback')){
				window[_this.attr('callback')]();
			}
			if(wrap.hasClass('proRate')){
                getClick();
            }
		})
	}
	var ofV=$('.prodtl_bd').offset().top;
	window.scrollTo(0,ofV);
}

$('.jsTab').clickTab();
//品牌馆
$('.brand_pvln_item .hd').bind('click',function(){
	var _this=$(this);
	var ico=_this.find('.ico_updown');
	var parent=_this.parent('.brand_pvln_item');
	var siblings=parent.siblings('.brand_pvln_item');
	if(parent.hasClass('curr')){
		ico.removeClass('up').addClass('down');
		parent.removeClass('curr');
	}else{
		ico.removeClass('down').addClass('up');
		parent.addClass('curr');
		siblings.removeClass('curr');
		siblings.find('.hd .ico_updown').removeClass('up').addClass('down');
	}
});
//分类查找
$('.subcatsrch_item .item_hd').click(function(){
	$(this).parent().toggleClass('subcatsrch_item_hover');
	$(this).parent().siblings().removeClass('subcatsrch_item_hover');
})
//通过修改样式来显示不同的星级
$(".big_encmmt a").click(function(){
	var _this=$(this);
	var index=_this.index()+1;
	_this.parent().removeClass().addClass("big_encmmt "+"big_encmmt"+index);
	return false;
});


//订单详情页
$(function(){
	//选择尺码\颜色

	$('.jsCheckColor a').on('click',function(){
		alert("s");
		var _this=$(this);
		var getProNumber=_this.find('img').attr('proNumber');
		var getProColor=_this.find('img').attr('alt');
		colorSelect($(this),getProNumber,getProColor);
		
	});
	sizeSelect();	
	//尺码加减
	$('.add').click(function(){
		var val=parseInt($('#inpProNum').val());
		$('#inpProNum').val(val+1)
	});
	$('.plut').click(function(){
		var val=parseInt($('#inpProNum').val());
		if(val<1){val=1}
		$('#inpProNum').val(val-1)		
	});
	//分享弹窗
	var sharePop=$('#sharepop');
	$('.jsIshare').click(function(){
		var jsOptTx_top=$('.jsOptTx').offset().top;
		$('.jsIshare').addClass('curr_ishare');
		mDialog({
			title:'分享',
			content:sharePop.html(),
			jt:true,
			fix:false,
			left:9,
			top:jsOptTx_top,
			success:function(){
				sharePop.remove();
				$('header').css('z-index',101);
			},
			close:function(){
				$('.jsIshare').removeClass('curr_ishare');
				$('header').css('z-index','');
			}
		});
		return false;
	});
	
	//大图弹窗
	var big_pro_img=$('#big_pro_img');
	$('.jsShowBigProImg a').click(function(){
		mDialog({
			content:big_pro_img.html(),
			top:45,
			fix:false,
			mask:false,
			left:0,
			width:'100%',
			success:function(){
				big_pro_img.remove();
				$('header').css('z-index',101);
				$('.jsSmallProImg a').click(function(){
					var _this=$(this);
					var bigImg=_this.find('img').attr('bigImg');
					$('.jsBigProImg img').attr('src',bigImg);
					return false;
				});
				$('.prodtl').hide();
			},
			close:function(){
				$('header').css('z-index','');
				$('.prodtl').show();
			}
		});
		return false;
	});
	//验证
	function checkForm(){
		var arrErr=[];
		if($('#sizeNo').val()==''){
			arrErr.push('请选择尺码!');
		}
		return arrErr.join('');
	}
	//加入购物车
	$('#addShopcart').click(function(){
		var err=checkForm();
		if(err==''){
			YouGouWap.base.showWapTip({
				msg:'已成功加入购物车',
				type:'confirm',
				confirmButton:'查看购物车',
				cancleButton:'继续购物',
				confirmCallback:lookShopCart
			});	
			function lookShopCart(){
				window.location.href="shopcart.shtml";
			}
		}else{
			YouGouWap.base.showWapTip({
				title:'温馨提示',
				msg:err,
				isOkButton:true
			});
		}
		return false;
	});
	//收藏
	$('.jsAddFav').click(function(){
		var favurl=$(this).attr('no');
		var thisObj=$(this);
		$.ajax({
			  type: 'POST',
			  dataType: 'html',
			  url: favurl,
			  // data to be added to query string:
			 // data: { name: 'Zepto.js' },
			  // type of data we are expecting in return:
			  //dataType: 'json',
			  timeout: 50000,
			  //context: $('body'),
			  success: function(data){
			    // Supposing this JSON payload was received:
			    //   {"project": {"id": 42, "html": "<div>..." }}
			    // append the HTML to context object.
			    //this.append(data.project.html)
				if (data == 'success') {
					YouGouWap.base.showWapTip({
						msg:'收藏成功',
						autoHide:true	
				
					});
					thisObj.addClass('collect_hover');
				}
			  },
			  error: function(xhr, type){
			    alert('Ajax error!');
			  }
			});
	});
})();

	//加载评论
	function bindComment(){
		$('#morecomment span').bind('click',function(){
			var morecomment=$('#morecomment'),curPage = morecomment.attr('curpage');
			
			$.get(morecomment.attr('ajaxUrl')+'&page='+curPage,function(d){
				if(d!=''){
					$(d).insertBefore(morecomment);
					morecomment.attr('curpage',parseInt(curPage)+1);
				}
				if(curPage == morecomment.attr('totalpage')){
					morecomment.html('已全部显示');
				}
			});
		});			
	}
(function(){
	//订单详情-取消订单
	var _cancelOrderPop=$('#cancelOrderPop');
	function resetOrderUi(tart){
		$('header').css('z-index','');
		tart.css({'color':'','position':'','z-index':''});
	}
	$('.jsCancelOrder').click(function(){
		var _this=$(this);
		var _top=_this.offset().top;
		var _width=_this.width()/2;
		mDialog({
			title:'取消订单原因',
			content:_cancelOrderPop.html(),
			jt:true,
			jtposition:'top',
			fix:false,
			jtright:_width-12,
			right:20,
			top:_top,
			success:function(){
				_cancelOrderPop.remove();
				$('header').css('z-index',101);
				_this.css({'color':'#fff','position':'relative','z-index':103});
				$('.jsCancleBtn').click(function(){
					$('.mpop').remove();
					resetOrderUi(_this);
					return false;
				});
			},
			close:function(){
				resetOrderUi(_this);
			}
		});
		return false;
	});
	
	//订单详情-选择支付方式
	var _sltPayTypePop=$('#sltPayTypePop');
	$('.jsSltPayType').click(function(){
		var _this=$(this); 
		var _top=_this.offset().top;
		var _width=_this.width()/2
		mDialog({
			title:'选择支付方式',
			content:_sltPayTypePop.html(),
			jt:true,
			jtposition:'top',
			fix:false,
			jtright:_width-10,
			right:20,
			top:_top+15,
			success:function(){
				_sltPayTypePop.remove();
				$('header').css('z-index',101);
				_this.css({'position':'relative','z-index':103});
			},
			close:function(){
				$('header').css('z-index','');
				_this.css({'position':'','z-index':''});
			}
		});
		return false;
	});

})();


(function(){
	var _modifyAddrPop=$('#modifyAddrPop');
	$('.jsModifAddr').click(function(){
		var _this=$(this);
		var _top=_this.offset().top;
		var _width=_this.width()/2;
		mDialog({
			title:'修改收货地址',
			content:_modifyAddrPop.html(),
			jt:true,
			jtposition:'top',
			fix:false,
			jtleft:_width,
			left:12,
			top:_top+15,
			success:function(){
				_modifyAddrPop.remove();
				$('header').css('z-index',101);
				_this.css({'position':'relative','z-index':103});
			},
			close:function(){
				$('header').css('z-index','');
				_this.css({'position':'','z-index':''});
			}
		});
		return false;
	});
	
	$(function(){
		$('.jsModifyAddrHd').click(function(){
			var _this=$(this);
			var _parent=_this.parent();
			var _bd=_this.siblings();
			_parent.addClass('gftcpn_curr');
			return false;
		});
		$('.jsModifyAddrLst .areacheck').click(function(){
			var _this=$(this);
			var _parent=_this.parent();
			var _name=_parent.find('.jsName').html();
			var _phone=_parent.find('.jsPhone').html();
			var _addr=_parent.find('.jsAddr span').html();
			$('.jsModifyAddrHd .tt').html(_name+'&nbsp;'+_phone+'<br />'+_addr);
			_parent.parents('.modify_addr').removeClass('gftcpn_curr');
		});
	});
})();

//倒计时
function countDown(time,type,targ){
	var i=time;
	setInterval(function(){
		i-=1;
		targ.html(i);
		if(i<=1){
			switch (type){
				case 0:
					window.history.go(-1);
				case 1:
					window.location.href= (context + '/my/myIndex.sc');
				break;
			}
		}
	},1000);
}


//10秒倒计时刷新页面
if($('.jsTimeCountDown')[0]){
	var _targ=$('.jsTimeCountDown');
	countDown(10,0,_targ);
}

//5秒后进入个人中心
if($('#regSuccess')[0]){
	var _targ=$('#regSuccess');
	countDown(5,1,_targ);
}

//商品详情页倒计时
function timeDown(times,targ,value){
	var i=times;
	var looper = setInterval(function(){
		i-=1000;
		if(i < 0){
			clearInterval(looper);
			return;
		}
	   var  leftDay = Math.floor(i/(60*60*24*1000));
	   var  remain = i%(60*60*24*1000);
	   var leftHour = Math.floor(remain/(60*60*1000));
	   if(leftHour<10){
	   	leftHour='0'+leftHour;
	   }	   
	   remain = remain%(60*60*1000);
	   var  leftMinute = Math.floor(remain/(60*1000));
	   if(leftMinute<10){
	   	leftMinute='0'+leftMinute;
	   }
	   remain = remain%(60*1000);
	   var  leftSecond = Math.round(remain/1000);
	   if(leftSecond<10){
	   	leftSecond='0'+leftSecond;
	   }
	   var getArr=[];
	   if(targ.length==0){
	   	return false;
	   }
	   if(value===1){
	   		targ.text(leftDay +"天"+ leftHour +"小时"+ leftMinute +"分" + leftSecond +"秒");
	   }
	   if(value===2){
	   		getArr.push('距结束仅剩 ');
	   		if(leftDay>0){
	   			getArr.push('<em class="proCountDownDay">'+leftDay+'</em>天 ');
	   		}
			getArr.push('<span class="proCountDownHour">'+leftHour+'</span> : ');
   			getArr.push('<span class="proCountDownMinu">'+leftMinute+'</span> :  ');
   			getArr.push('<span class="proCountDownSecond">'+leftSecond+'</span>');
	   		if(leftDay<=0 && leftHour<=0 && leftMinute<=0 && leftSecond<=0){
	   			targ.html('活动时间已经结束');
	   			setTimeout(function(){
	   				$('.proCountDown').remove();
	   			},3000)
	   			return false;
	   		}else{
	   			targ.html(getArr.join(''));
	   		}
	   		
	   }
	   //console.log(leftSecond);
		
	},1000);
}

//商品详情页倒计时
if($('.commodityinfoTime').length>0){
	$('.commodityinfoTime').each(function(){
		var times=$(this).attr('data')*1;
		timeDown(times,$(this),1);
		console.log(1)
	})
	
}
if($('#proCountDownTime')[0]){
	var times=$('#proCountDownTime').attr('data')*1;
	timeDown(times,$('.proCountDownTime'),2);
}
function colorSelect(wrap,wrapNumber,wrapColor){
	var _this=wrap;
	var proNumber=wrapNumber;
	var proColor=wrapColor;
	// $('#sizeNo').val(_this.attr('nos'));
	// $('#sizeNoArea').html(_this.text());
	//e.preventDefault();
	//return false;
	$.ajax({
        'type':'get',
        'url': context+'/chSize.sc?cmdNo='+proNumber,
        'dataType':'json',
        success:function(data){
        	//console.log(data)
        	//cos = 1;//已下架 2;//已售罄 0;//正常
            if(data.status){
            	var getSize=data.sizeList;
            	var getSizeArr=[];
            	for(var i=0;i<getSize.length;i++){
            		if(getSize[i].invertNum>0){
            			getSizeArr.push('<a nos="'+getSize[i].productNo+'"><span class="have">'+getSize[i].sizeName+'</span></a> ');
            		}else{
            			getSizeArr.push('<a><span class="no">'+getSize[i].sizeName+'</span></a> ');
            		}
            	}
                $('.proDetailSelect .proDetailSelectImgDiv').find('img').attr('src',data.picUrl_160);
                if(typeof data.cos !=undefined && data.cos != ''){
                	if(data.cos==1){//
                		$('.proDetailSelectImgW .proNumberNothing').show();
                		$('.prodtl_buyNew .proNoBuy').html('已下架').show();
                		buyNo=true;
                	}else if(data.cos==2){
                		$('.proDetailSelectImgW .proNumberNoSale').show();
                		$('.prodtl_buyNew .proNoBuy').html('已售罄').show();
                		buyNo=true;
                	}else{
	                	$('.proMarkNothing').hide();
	                	$('.prodtl_buyNew .proNoBuy').hide();
	                	buyNo=false;
	                }
                }else{
                	buyNo=false;
	                if(typeof buyNew != 'undefined' && buyNew){
	                	$('.proMarkNothing').hide();
	                	if(buyNo){
	                		$('.prodtl_buyNew_no .btn_org').hide();
		                	$('.prodtl_buyNew_no .proBuyNoo').show();
		                	//console.log(buyNo)
	                	}else{
	                		$('.prodtl_buyNew_no .btn_org').hide();
		                	$('.prodtl_buyNew_no .btn_brow_add').show();
		                	$('.prodtl_buyNew_no .btn_org_buy_now').show();
	                	}
	                	
	                }else{
	                	$('.prodtl_buyNew .proNoBuy').hide();
	                	$('.proMarkNothing').hide();
	                }
	            }

	            //console.log(data.price)
	            data.price=parseInt(data.price);
				$('.proDetailSelect .proDetailPrice').html('￥'+data.price);
				$('.proDetailSelect .proDetailSS').html('尺码');
				$('.proDetailSelect .proDetailSC').html(proColor);
				$('.proDetailSelect .jsCheckCm').html(getSizeArr.join(''));
                $('#productid').val(proNumber);
                //console.log(getCheckedSize)
                if(getCheckedSize!=''){
					var getSizeCurrent='';
					$('.proDetailSelect .jsCheckCm a').each(function(){
						if(typeof $(this).attr('nos') != 'undefined'){
							var getNosNumber=$(this).attr('nos');
							getNosNumber=getNosNumber.substring(getNosNumber.length-3);
							var getCheckedSizeLast=getCheckedSize.substring(getCheckedSize.length-3);
							if(getNosNumber == getCheckedSizeLast){
								getSizeCurrent=$(this).attr('nos');
								$(this).addClass('checked');
								return false;
							}
						}	
					})
					if(getSizeCurrent!=''){
						$('#sizeNo').val(getCheckedSize);
					}else{
						$('#sizeNo').val("");
					}
				}else{
					$('#sizeNo').val("");
				}
                sizeSelect();
                _this.addClass('checked').siblings().removeClass('checked');
                
            }else{
 //            	YouGouWap.base.showWapTip({
				// 	msg:'颜色商品下所有尺码已售罄',
				// 	autoHide:true	
			
				// });
            }
        }
    });
}
function sizeSelect(){
	$('.jsCheckCm a .have').off('click');
	$('.jsCheckCm a .have').on('click',function(){
		var _this=$(this);		
		_this.parent('a').addClass('checked').siblings().removeClass('checked');
		var getNumber=_this.parent('.checked').attr('nos');		
		$('#sizeNo').val(getNumber);
		getCheckedSize=getNumber;
		$('#sizeNoArea').html(_this.parent('a').text());
		$('.proDetailSelect .proDetailSS').html(_this.parent('a').text());
	});
}

$(function(){
	//领卷
	var displayproSelect=$(".proDetailSelect1").css("display");	
	if($("#lingjuan").length>0){
		$("#lingjuan").click(function (){
			var thiss=$(this);			
			btnShow('lingjuan',thiss);
			ygDetailPage.layoutShow();
		});
	}	
	function btnShow(btnName,thiss){			
		$('#waitLoadingBg').show();		
		$('.proDetailSelect1').show();			
	}
})
	
	
	




