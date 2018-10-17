$(document).ready(function() {
	
	$(window).bind('scroll resize', function() {
		var index = $(".pro-detail").offset().top-$(window).scrollTop();
		var index2 = $(".dianpu_meau").offset().top-$(window).scrollTop();
		if(index<5) {
			$("#nav2").addClass("item-active").siblings().removeClass("item-active")
		}else if(index>0){
			$("#nav1").addClass("item-active").siblings().removeClass("item-active")
		};
		if(index2<5) {
			$("#nav3").addClass("item-active").siblings().removeClass("item-active")
		}else if(index2>0&&index<0){
			$("#nav2").addClass("item-active").siblings().removeClass("item-active")
		};
	})
$(".proinfo-tab-tit").on("click", function() {
		$(this).addClass("item-active").siblings().removeClass("item-active")
	});

});