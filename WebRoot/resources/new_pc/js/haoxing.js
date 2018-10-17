jQuery('document').ready(function() {
	jQuery('.top_mid_slide')
		.slide({
			mainCell: '.top_mid_slider_ul',
			titCell: '.banner_top li',
			titOnClassName: 'this',
			trigger: 'mouseover',
			autoPlay: true
		});
});
jQuery(document).ready(function() {
	jQuery(".toph_bgsear li").mouseover(function() {
		jQuery(".toph_bgsear>li").show();
	}).mouseleave(function() {
		jQuery(".toph_bgsear>li").last().hide();
	}).click(function() {
		var index = jQuery(this).index();
		if(index == 1) {
			var f_text = jQuery(".toph_bgsear li").find("a").first().text();
			jQuery(".toph_bgsear li").find("a").first().html(jQuery(this).find("a").text() + "<s></s>");
			jQuery(".toph_bgsear li").find("a").last().text(f_text);
			jQuery("#type").val(jQuery(this).attr("type"));
			jQuery(".toph_bgsear>li").last().hide();
		}
	});

	jQuery(".two_code_a").click(function() {
		jQuery(this).parent().remove();
	});

});

function search_form() {
	var keyword = arguments[0];
	var type = arguments[1];
	if(keyword != "" && keyword != undefined) {
		jQuery("#keyword").val(keyword);
	}
	if(type != "" && type != undefined) {
		jQuery("#type").val(type);
	}
	jQuery("#searchForm").submit();
	jQuery("#keyword").val("");
}

jQuery(document).ready(function() {
	jQuery("#navul li").each(function() {
		var original_url = jQuery(this).attr("original_url");
		if("/index.htm".indexOf(original_url) >= 0) {
			jQuery(this).addClass("this");
		}
	});
	//
	jQuery(".left_menu_dl").live("mouseover", function() {
		var sc_id = jQuery(this).attr("id");
		var sc_color = jQuery(this).attr("sc_color");
		var child_count = jQuery(this).attr("child_count");
		var eq = jQuery(this).index();

		if(child_count > 0) {
			jQuery("#dts_" + sc_id).addClass("left_menu_this").removeClass("left_menu_dt");
			jQuery("#child_" + sc_id).show();
		}
		jQuery("#left_menu_con_" + sc_id).attr("style", "border:1px solid " + sc_color + "; border-left:1px solid " + sc_color + ";").find(".menu_con_right_top").css("background-color", sc_color);
		var z = -35;
		var x = eq * z;
		jQuery("#left_menu_con_" + sc_id).css('margin-top', x + 'px');
		jQuery(".left_menu_dd[id=child_" + sc_id + "]").show();

	}).live("mouseleave", function() {
		jQuery("dt[id^=dts_]").removeAttr("style");
		jQuery("div[id^=left_menu_con_]").removeAttr("style");
		var child_count = jQuery(this).attr("child_count");
		jQuery("dt[id^=dts_]").removeClass("left_menu_this").addClass("left_menu_dt");
		jQuery(".left_menu_dd[id^=child_]").hide();
	});
	jQuery(".nav_left").mouseover(function() {
		jQuery("#other_menu").show();

	});
	jQuery(".nav_left").mouseleave(function() {
		jQuery("#other_menu").hide();
	});
	
	//
});

jQuery(document).ready(function() {
	jQuery(":radio[id=transport_type]").click(function() {
		var val = jQuery(this).val();
		if(val == 0) {
			jQuery("#transport_template_select").show();
			jQuery("#mail_trans_fee").attr("readonly", "readonly");
			jQuery("#express_trans_fee").attr("readonly", "readonly");
			jQuery("#ems_trans_fee").attr("readonly", "readonly");
		} else {
			jQuery("#transport_template_select").hide();
			jQuery("#mail_trans_fee").removeAttr("readonly");
			jQuery("#express_trans_fee").removeAttr("readonly");
			jQuery("#ems_trans_fee").removeAttr("readonly");
		}
	});
	jQuery(":radio[id=transport_type][value=1]").attr("checked", "checked");
	jQuery("#transport_template_select").hide();;
});;;