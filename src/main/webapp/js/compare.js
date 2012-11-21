$(document).ready(function() {
	$('#comparison_submit').attr("disabled", "disabled" )
	insertServicesIntoInput();
	$('#comparison_submit').click(function() {
		transformComparisonData();
	});
});

function insertServicesIntoInput() {
	$.blockUI();
	$.ajax({
		type : "GET",
		url : "../v1/serviceManifestationIndex/all",
		dataType : ($.browser.msie) ? "text" : "xml",
		success : function(servicesXML) {
			createSourceRadiobuttons(servicesXML);
			createTargetCheckboxes(servicesXML);
			$.unblockUI();
		},
		error : function(xhr, textStatus, errorThrown) {
			alert('Error! Status ' + xhr.status +" with url: " + url + ".");
			$.unblockUI();
		}
	});
}

function createSourceRadiobuttons(servicesXML) {
	var radioContainer = $("<div />", {
		id: "comparison_sourceContainer"
	});
	$(servicesXML).children(0).children("serviceManifestation").each(function() {
		var radioButton = $("<input />", {
			type: "radio",
			class: "comparison_sourceIdRadio",
			value: $(this).attr("id"),
			name: "comparison_sourceRadioGroup",
		});
		radioContainer.append(radioButton)
		radioContainer.append($(this).attr("name"));
		radioContainer.append($("</br>"));
	});
	$('#comparison_sourceId').after(radioContainer);
	$("input[name=comparison_sourceRadioGroup]").change(function () {
		$('#comparison_submit').removeAttr('disabled');
	});
}

function createTargetCheckboxes(servicesXML) {
	var checkBoxContainer = $("<div />", {
		id: "comparison_targetContainer"
	});
	$(servicesXML).children(0).children("serviceManifestation").each(function() {
		var checkbox = $("<input />", {
			type: "checkbox",
			class: "comparison_targetIdCheckbox",
			value: $(this).attr("id"),
			name: "comparison_targetCheckboxGroup",
		});
		checkBoxContainer.append(checkbox)
		checkBoxContainer.append($(this).attr("name"));
		checkBoxContainer.append($("</br>"));
	});
	$('#comparison_targetId').after(checkBoxContainer);
}

function transformComparisonData() {
	var src = $("input[name=comparison_sourceRadioGroup]:checked").val();
	var target = "";
	$("input[name=comparison_targetCheckboxGroup]:checked").each(function() {
		target += (target.length <= 0 ? "" : ",") + $(this).val();
	});
	var url = "../v1/compare/" + src + (target.length <= 0 ? "" : "/" + target);

	$.blockUI();
	$.ajax({
		type : "GET",
		url : url,
		dataType : ($.browser.msie) ? "text" : "xml",
		success : function(xml) {
			$('#comparison_result').empty();
			$(xml).find('comparisonResults').each(function() {
				appendComparisonResult($(this));
			});
			$('#comparison_result').css('visibility', 'visible');
			$.unblockUI();
		},
		error : function(xhr, textStatus, errorThrown) {
			alert('Error! Status ' + xhr.status +" with url: " + url + ".");
			$.unblockUI();
		}
	});
}

function appendComparisonResult(result) {
	var rows = [];
	var source = result.find('source');

	initializeRows(source, rows);

	appendServiceData(source, rows);
	appendAttributeData(source, rows);

	result.find('target').each(function() {
		appendServiceData($(this), rows);
		appendAttributeData($(this), rows);
	});

	$('#comparison_result').append(createTable(rows));
}

function initializeRows(source, rows) {
	rows[0] = "<thead class='comparisonHead'><tr class='comparisonRow'>";
	var index = 1;
	source.find('attribute').each(function() {
		rows[index] = "<tr class='comparisonRow'>";
		index++;
	});
}

function createTable(rows) {
	var table = "<table class='comparisonData'>";
	$.each(rows, function(index, value) {
		table += value + (index == 0 ? "</tr></thead>" : "</tr>");
	});
	table += "</table>";
	return table;
}

function appendServiceData(service, rows) {
	rows[0] += "<th class='comparisonHeadCell'";

	var totalScore = $(service).attr("totalScore");
	if (typeof totalScore != 'undefined')
		rows[0] += " comparisonScore='" + totalScore + "'";
	
	rows[0] += ">" + service.attr('name') + "</th>";
}

function appendAttributeData(service, rows) {
	$.each(rows, function(index) {
		if (index > 0) {
			rows[index] += "<td class='comparisonCell'>";

			var attribute = service.find("attribute[index="
					+ (parseInt(index) - 1) + "]");
			if (attribute.length > 0)
				rows[index] += getAttributeData(attribute);

			rows[index] += "</td>";
		}
	});
}

function getAttributeData(attribute) {
	var data = "<div class='attr'";
	
	var type = attribute.attr("type");
	if (typeof type != 'undefined')
		data += " attrType='" + type + "'";

	var score = attribute.attr("score");
	if (typeof score != 'undefined')
		data += " attrScore='" + score + "'";

	data += "><div class='attrTypeLabel'>" + attribute.attr("typeLabel")
			+ "</div>";

	var label = attribute.attr("label");
	if (typeof label != 'undefined')
		data += "<div class='attrLabel'>" + label + "</div>";

	if (attribute.attr("type") == "ratio") {
		data += "<div class='attrUnit'>" + attribute.attr("unit") + "</div>";
		
		var value = convertValueToNiceString(attribute.attr("value"));
		if (value != null)
			data += "<div class='attrVal'>" + value + "</div>";
		
		var minValue = convertValueToNiceString(attribute.attr("minValue"));
		if (minValue != null)
			data += "<div class='attrMinVal'>" + minValue + "</div>";
		
		var maxValue = convertValueToNiceString(attribute.attr("maxValue"));
		if (maxValue != null)
			data += "<div class='attrMaxVal'>" + maxValue + "</div>";
	}
	
	$(attribute).children("valueReference").each(function() {
		data += "<div class='attrValueRef'>";
		data += getAttributeData($(this));
		data += "</div>";
	});
	
	data += "</div>";
	return data;
}

function convertValueToNiceString (value) {
	if (typeof value == 'undefined')
		return null;

	if(value == "1.7976931348623157E308")
		return "unlimited";
	
	var numberOtherThanZero = /^.*\.[123456789]+$/;
	if(numberOtherThanZero.test(value))
		return value;
	
	return value.substr(0, value.lastIndexOf("."));
}

//jQuery.fn.ToggleCompareButton = function() {
//	this.keyup(function() {
//		var srcLength = jQuery.trim($("#comparison_sourceId").val()).length;
//		if (srcLength > 0)
//			$('#comparison_submit').removeAttr('disabled');
//		else
//			$('#comparison_submit').attr('disabled', 'disabled');
//	});
//};
//
//var lastKeyCode = 0;
//jQuery.fn.ForceNumericOnly = function() {
//	// does not capture "^"
//	return this.each(function() {
//		$(this).keydown(function(e) {
//			var key = e.charCode || e.keyCode || 0;
//			
//			if($(this).is("#comparison_targetId")) {
//				//chaining with comma is allowed in targetId box..
//				if(lastKeyCode == 188 && key == 188)
//					return false;
//				lastKeyCode = key;
//				if(key == 188)
//					return true;
//			}
//			
//			return (key == 8 || 
//					key == 9 || 
//					key == 46 || 
//					(key >= 37 && key <= 40) || 
//					(key >= 48 && key <= 57) || 
//					(key >= 96 && key <= 105));
//		});
//	});
//};
