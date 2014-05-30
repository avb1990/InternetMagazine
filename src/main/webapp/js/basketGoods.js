function BasketGoods(id, goods, date) {
	this.id = id;
	this.goods = goods;
	this.date = date;
}
function parseBasketGoodsFromXml($xml) {
	var id = parseInt($xml.getElementsByTagName("id")[0].innerHTML, 10);
	var date = new Date(Date
			.parse($xml.getElementsByTagName("date")[0].innerHTML));

	var goodsXml = $xml.getElementsByTagName("goods");
	var goods = [];
	for (var i = 0; i < goodsXml.length; i++) {
		goods[i] = parseGoodsInBasketFromXml(new DOMParser().parseFromString(
				"<element>" + goodsXml[i].innerHTML + "</element>", "text/xml"));
	}
	return new BasketGoods(id, goods, date);
}

function GoodsInBasket(goodsId, basketId, amount, goods) {
	this.goodsId = goodsId;
	this.basketId = basketId;
	this.amount = amount;
	this.goods = goods;

}
function parseGoodsInBasketFromXml($xml) {
	var basketId = parseInt($xml.getElementsByTagName("basketId")[0].innerHTML,
			10);
	var goodsId = parseInt($xml.getElementsByTagName("goodsId")[0].innerHTML,
			10);
	var amount = parseInt($xml.getElementsByTagName("amount")[0].innerHTML, 10);
	return new GoodsInBasket(goodsId, basketId, amount);
}
function GetBasketsRequestConstraints(startDate, endDate, group, goods) {
	this.startDate = startDate;
	this.endDate = endDate;
	this.group = group;
	this.goods = goods;
}

// function createInsertBasketRequest(basket) {
// request = "<soapenv:Envelope
// xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"
// xmlns:good=\"http://ru/mail/fortune/webservices/internetmagazine/goods\">"
// + "<soapenv:Header/>"
// + "<soapenv:Body>"
// + "<good:InsertGoodsBasketRequest>" + "<good:insertGoods>";
//
// for (var i = 0; i < basket.goods.length; i++) {
// request += "<good:goods>" + "<good:goodsId>" + basket.goods[i].goods.id
// + "</good:goodsId>";
// request += "<good:amount>" + basket.goods[i].amount + "</good:amount>"
// + "</good:goods>";
// }
// request += "</good:insertGoods>" + "</good:InsertGoodsBasketRequest>"
// + "</soapenv:Body>" + "</soapenv:Envelope>";
// return request;
//
// }
// function createGetBasketsByPeriodRequest(startDate, endDate, group, goods) {
// var request = "<soapenv:Envelope
// xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"
// xmlns:good=\"http://ru/mail/fortune/webservices/internetmagazine/goods\">"
// + +"<soapenv:Header/>"
// + "<soapenv:Body>"
// + "<good:GetBasketsByPeriodRequest>";
//
// if (checkNotNullAndUndefined(startDate))
// request += "<good:startDate>" + convertDateToXmlString(startDate)
// + "</good:startDate>";
// if (checkNotNullAndUndefined(endDate))
// request += "<good:endDate>" + convertDateToXmlString(endDate)
// + "</good:endDate>";
// if (checkNotNullAndUndefined(group))
// request += "<good:groupId>" + group.id + "</good:groupId>";
// if (checkNotNullAndUndefined(goods))
// request += "<good:goodsId>" + goods.id + "</good:goodsId>";
//
// request += "</good:GetBasketsByPeriodRequest>" + "</soapenv:Body>"
// + "</soapenv:Envelope>";
//
// return request;
// }

function parseBasketsFromGetBasketsByPeriodResponse(xml) {
	var basketsXml = xml.getElementsByTagName("baskets");
	var baskets = [];
	for (var i = 0; i < basketsXml.length; i++)
		baskets[i] = parseBasketGoodsFromXml(new DOMParser().parseFromString(
				"<element>" + basketsXml[i].innerHTML + "</element>",
				"text/xml"));

	return baskets;

}
