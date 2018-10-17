// JavaScript Document


var aa = 0;
$(document).ready(function() {
	$(".drop-down-bg").click(function() {
		var isshow = $('#drop-down').data("show");
		if(isshow == null || isshow == 0) {
			$(".drop-down-content").show();
			$(".swiper-slide").hide();
			$(".opacity").show();
		} else {
			$(".drop-down-content").hide();
			$(".swiper-slide").show();
			$(".opacity").hide();
		}
		if(aa % 2 == 0) {
			$('.drop-down-bg').html('<i class="icon iconfont">&#xe62a;</i>');
			$('#drop-down').data('show', "1");
		} else {
			$('.drop-down-bg').html('<i class="icon iconfont">&#xe62d;</i>');
			$('#drop-down').data('show', "0");
		}
		aa++;
		return false;
	});

	$("#stateBut").click(function() {
		var dis = $("#stateBut").data("show");
		if(dis == "hide") {
			$('#class1content').hide();
			$("#stateBut").data("show", "show")
		} else {
			$('#class1content').show();
			$("#stateBut").data("show", "hide")
		}
	});
	
	$("#stateBut1").click(function() {
		var dis = $("#stateBut1").data("show");
		if(dis == "hide1") {
			$('#class1content1').hide();
			$("#stateBut1").data("show", "show")
		} else {
			$('#class1content1').show();
			$("#stateBut1").data("show", "hide1")
		}
		if(aa % 2 == 0) {
			$('.liuhui').html('我的订单<i class="icon iconfont">&#xe609;</i>');
			$('#liuhui1').data('show', "1");
		} else {
			$('.liuhui').html('我的订单<i class="icon iconfont">&#xe613;</i>');
			$('#liuhui1').data('show', "0");
		}
		aa++;
		return false;
	});
	

});





