$(document).ready(function() {
	$(document).ready(function() {
		$(window).scroll(function() {
			if($(document).scrollTop() <= 0) {
				alert("我已经到顶了，我可以刷新");
			}
			if($(document).scrollTop() >= $(document).height() - $(window).height()) {
				alert("我已经到底了，我需要加载更多");
			}
		});
	});
});
