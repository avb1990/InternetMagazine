function Goods(id, name, cost, groupid) {
	this.id = id;
	this.name = name;
	this.cost = cost;
	this.groupId = groupid;
}
function parseGoodsFromXml($xml) {
	var id = parseInt($xml.getElementsByTagName("id")[0].innerHTML, 10);
	var name = $xml.getElementsByTagName("name")[0].innerHTML;
	var cost = parseFloat($xml.getElementsByTagName("cost")[0].innerHTML);
	var groupId = parseInt($xml.getElementsByTagName("groupId")[0].innerHTML,
			10);
	return new Goods(id, name, cost, groupId);
}


// function createDeleteGoodsRequest(goods) {
// return "<soapenv:Envelope
// xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"
// xmlns:good=\"http://ru/mail/fortune/webservices/internetmagazine/goods\">"
// + LOGIN.createRequestHeader()
// + "<soapenv:Body>"
// + "<good:DeleteGoodsRequest>"
// + "<good:goodsId>"
// + goods.id
// + "</good:goodsId>"
// + "</good:DeleteGoodsRequest>"
// + "</soapenv:Body>" + "</soapenv:Envelope>";
// }

// function createSaveGoodsRequest(goods) {
// var request = "<soapenv:Envelope
// xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"
// xmlns:good=\"http://ru/mail/fortune/webservices/internetmagazine/goods\">"
// + LOGIN.createRequestHeader()
// + "<soapenv:Body>"
// + "<good:InsertGoodsRequest>";
//
// request += parseGoodsToXml(goods) + "</good:InsertGoodsRequest>"
// + "</soapenv:Body>" + "</soapenv:Envelope>";
//
// return request;
// }

function parseGoodsToXml(goods) {
	var xml = "";
	if (checkNotNullAndUndefined(goods.id))
		xml += "<good:id>" + goods.id + "</good:id>";

	xml += "<good:name>" + goods.name + "</good:name>" + "<good:cost>"
			+ goods.cost + "</good:cost>" + "<good:groupId>" + goods.groupId
			+ "</good:groupId>";
	return xml;
}
