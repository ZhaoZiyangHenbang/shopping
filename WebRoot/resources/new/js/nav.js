$(function(){

$(".cbtn-search").click(function(){$(".srch").slideToggle(300)});
$(window).scroll(function(){
    if($(window).scrollTop()>40){
     $('.cbtn-top').fadeIn();
     }else{
		 $('.cbtn-top').fadeOut();  
	 }
});
$('.cbtn-top').click(function(){
    $('body,html').animate({scrollTop:0},500)
});
	
if($("#slide").length>0){
		slideShow("slide","slide-pos");
	}
});






