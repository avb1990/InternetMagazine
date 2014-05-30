function GroupGoods(id, name, goods) {
	this.id = id;
	this.name = name;
	this.goods = goods;
	for (var i = 0; i < this.goods.length; i++)
		this.goods[i].groupId = this.id;
}

function parseGroupGoodsFromXml($xml) {
	var id = parseInt($xml.getElementsByTagName("id")[0].innerHTML, 10);
	var name = $xml.getElementsByTagName("name")[0].innerHTML;
	var goodsXml = $xml.getElementsByTagName("goods");
	var goods = [];
	for (var i = 0; i < goodsXml.length; i++) {
		goods[i] = parseGoodsFromXml(new DOMParser().parseFromString(
				"<element>" + goodsXml[i].innerHTML + "</element>", "text/xml"));
	}
	return new GroupGoods(id, name, goods);
}

// function createGetAllGroupGoodsRequest() {
// return "<soapenv:Envelope
// xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"
// xmlns:good=\"http://ru/mail/fortune/webservices/internetmagazine/goods\">"
// + "<soapenv:Header/>"
// + "<soapenv:Body>"
// + "<good:GetAllGoodsGroupsRequest/>"
// + "</soapenv:Body>"
// + "</soapenv:Envelope>";
// }
function parseGetAllGroupGoodsResponse(goodsXml) {
	var goods = new Array(goodsXml.length);
	for (var i = 0; i < goodsXml.length; i++) {
		goods[i] = parseGroupGoodsFromXml(new DOMParser().parseFromString(
				"<element>" + goodsXml[i].innerHTML + "</element>", "text/xml"));

	}
	return goods;
}
// function createDeleteGroupRequest(group) {
// return "<soapenv:Envelope
// xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"
// xmlns:good=\"http://ru/mail/fortune/webservices/internetmagazine/goods\">"
// + "<soapenv:Header/>"
// + "<soapenv:Body>"
// + "<good:DeleteGoodsGroupRequest>"
// + "<good:deleteGoodsGroup>"
// + group.id
// + "</good:deleteGoodsGroup>"
// + "</good:DeleteGoodsGroupRequest>"
// + "</soapenv:Body>"
// + "</soapenv:Envelope>";
// }
//
// function createSaveGroupRequest(group) {
// var request = "<soapenv:Envelope
// xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"
// xmlns:good=\"http://ru/mail/fortune/webservices/internetmagazine/goods\">"
// + "<soapenv:Header/>"
// + "<soapenv:Body>"
// + "<good:InsertGoodsGroupRequest>" + "<good:insertGoodsGroup>";
// if (checkNotNullAndUndefined(group.id))
// request += "<good:id>" + group.id + "</good:id>";
// request += "<good:name>" + group.name + "</good:name>";
//
// for (var i = 0; i < group.goods.length; i++)
// request += "<good:goods>" + parseGoodsToXml(group.goods[i])
// + "</good:goods>";
// request += "</good:insertGoodsGroup>" + "</good:InsertGoodsGroupRequest>"
// + "</soapenv:Body>" + "</soapenv:Envelope>";
// return request;
// }
