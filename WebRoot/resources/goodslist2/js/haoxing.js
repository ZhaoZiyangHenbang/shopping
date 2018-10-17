// JavaScript Document

! function(t, e) {
	"use strict";
	var i = {};
	! function() {
		var n = e.querySelector('meta[name="viewport"]'),
			a = e.querySelector('meta[name="hotcss"]'),
			r = t.devicePixelRatio || 1,
			d = 540,
			m = 0;
		if(r = r >= 3 ? 3 : r >= 2 ? 2 : 1, a) {
			var s = a.getAttribute("content");
			if(s) {
				var c = s.match(/initial\-dpr=([\d\.]+)/);
				c && (r = parseFloat(c[1]));
				var o = s.match(/max\-width=([\d\.]+)/);
				o && (d = parseFloat(o[1]));
				var u = s.match(/design\-width=([\d\.]+)/);
				u && (m = parseFloat(u[1]))
			}
		}
		e.documentElement.setAttribute("data-dpr", r), i.dpr = r, e.documentElement.setAttribute("max-width", d), i.maxWidth = d, m && (e.documentElement.setAttribute("design-width", m), i.designWidth = m);
		var l = 1 / r,
			h = "width=device-width, initial-scale=" + l + ", minimum-scale=" + l + ", maximum-scale=" + l + ", user-scalable=no";
		n ? n.setAttribute("content", h) : (n = e.createElement("meta"), n.setAttribute("name", "viewport"), n.setAttribute("content", h), e.head.appendChild(n))
	}(), i.px2rem = function(t, e) {
		return e || (e = parseInt(i.designWidth, 10)), 320 * parseInt(t, 10) / e / 20
	}, i.rem2px = function(t, e) {
		return e || (e = parseInt(i.designWidth, 10)), 20 * t * e / 320
	}, i.mresize = function() {
		var n = e.documentElement.getBoundingClientRect().width || t.innerWidth;
		return i.maxWidth && n / i.dpr > i.maxWidth && (n = i.maxWidth * i.dpr), n ? (e.documentElement.style.fontSize = 20 * n / 320 + "px", void(i.callback && i.callback())) : !1
	}, i.mresize(), t.addEventListener("resize", function() {
		clearTimeout(i.tid), i.tid = setTimeout(i.mresize, 33)
	}, !1), t.addEventListener("load", i.mresize, !1), setTimeout(function() {
		i.mresize()
	}, 333), t.hotcss = i
}(window, document);

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





