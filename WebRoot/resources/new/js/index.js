$(document).ready(function(){
	$(".address-list").hide();
 $(".slide-title").on("click",function(){
 	if($(this).hasClass("active")){
 		$(this).removeClass("active");
 		$(this).addClass("close");
 		$(this).siblings().show()
 	}else{
 		$(this).removeClass("close")
 		$(this).addClass("active")
 		$(this).siblings().hide()
 	}
 })
 $(".menu").on("click",function(){
 	if($(".menu-list").hasClass("hide")){
 		$(".menu-list").removeClass("hide");
 		$(".menu-list").addClass("show");
 	}else{
 		$(".menu-list").removeClass("show");
 		$(".menu-list").addClass("hide");
 	}
 })
  /* $(document).ready(function() {
            $(window).scroll(function() {
                if ($(document).scrollTop() <= 0) {
                    alert("我已经到顶了，我可以刷新");
                }
                if ($(document).scrollTop() >= $(document).height() - $(window).height()) {
                   alert("我已经到底了，我需要加载更多" );
                }
            });
        });*/
       $(".gray").on("click",function(){
       	$(this).removeClass("gray");
       	$(this).addClass("red");
       	$(this).siblings().removeClass("red");
       	$(this).siblings().addclass("gray");
       });
        $(".red").on("click",function(){
       	$(this).removeClass("red");
       	$(this).addClass("gray");
       	$(this).siblings().removeClass("gray");
       	$(this).siblings().addclass("red");
       })
    
});