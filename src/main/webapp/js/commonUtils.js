var CONSTS = {
	webServiceURL : "./endpoints"
};

function sendRequest(request, hanlerResponse, soapHostUrl, errorHandler) {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open('POST', soapHostUrl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4) {
			if (xmlhttp.status == 200) {
				try {
					hanlerResponse(this.responseXML);
				} catch (e) {
					alert("Внимание! При обработке сообщения с сервера произошла ошибка!");
				}
			} else if (checkNotNullAndUndefined(errorHandler))
				errorHandler(this.responseXML);
		}
	};
	xmlhttp.setRequestHeader('Content-Type', 'text/xml');
	xmlhttp.setRequestHeader("Access-Control-Allow-Origin", "*");
	xmlhttp.send(request);
}
function checkIsResponseOk(xml) {
	return xml.getElementsByTagName("isOk")[0].innerHTML === "true";
}

function checkNotNullAndUndefined(value) {
	return value !== null && value !== undefined;
}

function createEvent(name, data) {
	return {
		name : name,
		data : data
	};
}

function convertDateToXmlString(date) {
	if (date === null || date === undefined)
		return "";

	return "" + date.getUTCFullYear().toString() + "-"
			+ PadDigits((date.getUTCMonth() + 1).toString(), 2) + "-"
			+ PadDigits(date.getUTCDate().toString(), 2) + "T"
			+ PadDigits(date.getUTCHours().toString(), 2) + ":"
			+ PadDigits(date.getUTCMinutes().toString(), 2) + ":"
			+ PadDigits(date.getUTCSeconds().toString(), 2);

}
function formateDate(date) {
	if (!checkNotNullAndUndefined(date))
		return "";
	return "" + date.getUTCDate().toString() + ":"
			+ PadDigits((date.getUTCMonth() + 1).toString(), 2) + ":"
			+ date.getUTCFullYear().toString() + " "
			+ PadDigits(date.getUTCHours().toString(), 2) + ":"
			+ PadDigits(date.getUTCMinutes().toString(), 2);
}

function PadDigits(input, totalDigits) {
	var result = input;
	if (totalDigits > input.length) {
		for (var i = 0; i < (totalDigits - input.length); i++) {
			result = '0' + result;
		}
	}
	return result;
}