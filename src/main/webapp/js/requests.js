function RequestCreater(requestName, xmlGenerator) {
	this.createHeader = function() {
		return LOGIN.createRequestHeader();
	};
	this.requestName = requestName;
	this.xmlGenerator = xmlGenerator;
	this.createRequest = function(object) {
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:good=\"http://ru/mail/fortune/webservices/internetmagazine/goods\">"
				+ this.createHeader()
				+ "<soapenv:Body>"
				+ "<"
				+ this.requestName
				+ ">"
				+ this.xmlGenerator(object)
				+ "</"
				+ this.requestName
				+ ">"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";
	};
}
var EMPTY_GENERATOR = function(object) {
	return "";
};

var GROUP_GOODS_REQUEST_CREATERS = {
	GET_ALL_GROUP_GOODS_REQUEST_CREATER : new RequestCreater(
			"good:GetAllGoodsGroupsRequest", EMPTY_GENERATOR),
	DELETE_GROUP_REQUEST_CREATER : new RequestCreater(
			"good:DeleteGoodsGroupRequest", function(group) {
				return "<good:deleteGoodsGroup>" + group.id
						+ "</good:deleteGoodsGroup>";
			}),
	SAVE_GROUP_REQUEST_CREATER : new RequestCreater(
			"good:InsertGoodsGroupRequest", function(group) {
				var request = "<good:insertGoodsGroup>";
				if (checkNotNullAndUndefined(group.id))
					request += "<good:id>" + group.id + "</good:id>";
				request += "<good:name>" + group.name + "</good:name>";

				for (var i = 0; i < group.goods.length; i++)
					request += "<good:goods>" + parseGoodsToXml(group.goods[i])
							+ "</good:goods>";
				request += "</good:insertGoodsGroup>";
				return request;
			})
};

var BASKET_GOODS_REQUESTS_CREATERS = {
	INSERT_BASKET_REQUEST_CREATER : new RequestCreater(
			"good:InsertGoodsBasketRequest", function(basket) {
				var request = "<good:insertGoods>";

				for (var i = 0; i < basket.goods.length; i++) {
					request += "<good:goods>" + "<good:goodsId>"
							+ basket.goods[i].goods.id + "</good:goodsId>";
					request += "<good:amount>" + basket.goods[i].amount
							+ "</good:amount>" + "</good:goods>";
				}
				request += "</good:insertGoods>";
				return request;
			}),
	GET_BASKET_BY_PERIOD_REQUEST_CREATER : new RequestCreater(
			"good:GetBasketsByPeriodRequest", function(constraints) {
				var request = "";
				if (checkNotNullAndUndefined(constraints.startDate))
					request += "<good:startDate>"
							+ convertDateToXmlString(constraints.startDate)
							+ "</good:startDate>";
				if (checkNotNullAndUndefined(constraints.endDate))
					request += "<good:endDate>"
							+ convertDateToXmlString(constraints.endDate)
							+ "</good:endDate>";
				if (checkNotNullAndUndefined(constraints.group))
					request += "<good:groupId>" + constraints.group.id
							+ "</good:groupId>";
				if (checkNotNullAndUndefined(constraints.goods))
					request += "<good:goodsId>" + constraints.goods.id
							+ "</good:goodsId>";
				return request;
			}),
};
var GOODS_REQUESTS_CREATORS = {
	DELETE_GOODS_REQUEST_CREATER : new RequestCreater(
			"good:DeleteGoodsRequest", function(goods) {
				return "<good:goodsId>" + goods.id + "</good:goodsId>";
			}),
	SAVE_GOODS_REQUEST : new RequestCreater("good:InsertGoodsRequest",
			parseGoodsToXml)

};