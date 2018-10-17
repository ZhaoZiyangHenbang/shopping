// JavaScript Document

//购物车
//单品页面


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

	





	
	
	




