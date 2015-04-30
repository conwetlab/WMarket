var tt;

$(document).ready(function() {
	tt = jQuery('<div/>', { id: 'valueRefTT' });
	$(tt).css("position","absolute");
	$(tt).css("z-index","99");
	$(tt).css("display","none");
	$(tt).css("width", "auto");
	$(tt).css("height", "auto");
	$("body").append(tt);
});

$('#comparison_submit').ajaxComplete(function() {
	//tooltip show
	$(".attr").mouseover(function() {
		if ($(this).children("div[class='attrValueRef']").length != 0) {
			addValueRefTooltip($(this));
	    }
	});
	//tooltip hide
	$(".attr").mouseout(function() {
		tt.css("display", "none");
	});
	//add class for attr with value refs
	$(".attr").each(function() {
		if ($(this).children("div[class='attrValueRef']").length != 0) {
			$(this).addClass("attrHasRefs");
		}
	});
	$(".comparisonHeadCell").each(function() {
		var name = $(this).html();
		var name = name.substring(0, name.indexOf(",") + 1) + "</br>" + name.substring(name.indexOf(",") + 1);
		$(this).html(name);
	});
	addRowAnchors();
	addScoreToTableHeads();
});

function addValueRefTooltip(attribute) {
	$(tt).empty();
	addValueRefs($(attribute).children("div[contains(@class,'attrValueRef')]"));
	
	var windowWidth = $(window).width();
	var windowHeight = $(window).height();  
	var parentLeft = $(attribute).offset().left;
	var parentTop = $(attribute).offset().top;
	var parentWidth = $(attribute).outerWidth();
	var parentHeight = $(attribute).outerHeight();
	var width = $(tt).outerWidth();
	var height = $(tt).outerHeight();

	var left = parentLeft + parentWidth;
	var top = parentTop - height;
	
	var distToRight = windowWidth - left - width;
	if(distToRight <= 5)
		left = parentLeft - width;
	
	top += top <= 0 ? Math.abs(top) : 0;
	top += top < 5 ? 5 : 0;
			
	$(tt).css("left", left);
	$(tt).css("top", top);
	$(tt).css("display", "block");
}

function addValueRefs(valueRef) {
	$(valueRef).children("div[contains(@class,'attr')]").each(function() {
		var typeLabel = $(this).children("div[class='attrTypeLabel']").eq(0).html();
		var label = $(this).children("div[class='attrLabel']").eq(0).html();
		var unit = $(this).children("div[class='attrUnit']").eq(0).html();
		var value = $(this).children("div[class='attrVal']").eq(0).html();
		var minValue = $(this).children("div[class='attrMinVal']").eq(0).html();
		var maxValue = $(this).children("div[class='attrMaxVal']").eq(0).html();
		
		var indent = "";
		for ( var i = 0; i < ($(this).parents("div[contains(@class,'attr')]").size() - 5); i++) {
			indent += "&nbsp;&nbsp;";
		}
		
		var element = $("<div />", { class: "valueRefTTElement" });
		element.html(
			indent +
			typeLabel +
			(label ? ", " + label : "") +
			(value ? ", " + value : "") +
			(minValue ? ", min " + minValue : "") +
			(maxValue ? ", max " + maxValue : "") +
			(unit ? ", [" + unit +"]" : "")
		);
		
		if(!indent && $(tt).html()) {
			element.css("border-top", "1px dotted gray");
			element.css("padding-top", "5px");
			element.css("margin-top", "3px");
		} 

		if(indent) {
			element.css("padding-top", "2px");
			element.css("padding-bottom", "2px");
		} 
		
		
		$(tt).append(element);
		
		$(this).children("div[contains(@class,'attrValueRef')]").each(function() {
			addValueRefs($(this), indent + 1);
		})
	});
}

function addRowAnchors()  {
	$("tbody").children("tr").each(function() {
		var td = $("<td />");
		
		var upAnchor = $("<div />", {
			class: "rowAnchor moveUpAnchor",
			text: "",
			click: function(e){
				e.preventDefault();
				moveRowUp($(this).parent().parent());
			}
		});
		
		var downAnchor = $("<div />", {
		    class: "rowAnchor moveDownAnchor",
		    text: "",
		    click: function(e){
			    e.preventDefault();
			    moveRowDown($(this).parent().parent());
		    }
		});
		
		$(td).prepend(downAnchor);
		$(td).prepend(upAnchor);
		$(this).prepend(td);
	});
	$("thead").children("tr").each(function() {
		var anchor = $("<th />");
		$(this).prepend(anchor);
	});
}

function moveRowUp(row) {
	var index = row.index();
	if(index == 0)
		return;
	$(row).after($(row).parent().children().eq(index - 1));		
}

function moveRowDown(row) {
	var index = row.index();
	var maxIndex = row.parent().children().size() - 1;
	if(index == maxIndex)
		return;
	$(row).before($(row).parent().children().eq(index + 1));
}

function addScoreToTableHeads() {
	$(".comparisonHeadCell").each(function() {
		var score = parseFloat(($(this).attr("comparisonscore") * 100).toString().substring(0,3));
		$(this).append($("<div />", {
		    class: "comparison_HeadCellScore",
		    text: (score >= 0 ? "[" + score + "%]" : "") }));
	});
}
