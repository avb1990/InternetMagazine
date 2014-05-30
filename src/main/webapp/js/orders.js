var ORDERS = {
	init : function() {
		sendRequest(
				GROUP_GOODS_REQUEST_CREATERS.GET_ALL_GROUP_GOODS_REQUEST_CREATER
						.createRequest(null), function(xml) {
					var goodsXml = xml.getElementsByTagName("goodsGroups");
					var goods = parseGetAllGroupGoodsResponse(goodsXml);
					ORDERS.controller.init({
						groups : goods,
						currentGroup : null
					});

				}, CONSTS.webServiceURL);
	},
	controller : {
		init : function(model) {
			this.model = model;
			this.refresh(this.model);
		},
		refresh : function(model) {
			var groupGoodsUl = $("#groupGoods");
			groupGoodsUl.empty();
			groupGoodsUl.append($("<p/>")).append("Группы товаров");
			var groupTable = $("<table/>");
			groupGoodsUl.append(groupTable);
			groupTable.append($("<tr/>").append($("<td/>").append("Название")));
			for (var i = 0; i < model.groups.length; i++) {
				var element = $("<tr/>");
				if (model.groups[i] === model.currentGroup)
					element.addClass("selected");
				groupTable.append(element);
				element
						.click((function() {
							var clickGroup = model.groups[i];
							return function() {
								ORDERS.controller.handle(createEvent(
										ORDERS.eventsNames.ON_GROUP_CLICK,
										clickGroup));
							};
						})());
				element.append($("<td/>").append(model.groups[i].name));
				groupGoodsUl.append(groupTable);
			}
			var goodsUL = $("#goods");
			goodsUL.empty();
			if (model.currentGroup !== null) {
				goodsUL.append($("<p/>").append("Товары"));
				var goodsTable = $("<table/>");
				var goodsName = $("<td/>").append("Название");
				var goodsCost = $("<td/>").append("Стоимость");
				var goodsAmount = $("<td/>").append("Количество");
				goodsUL.append(goodsTable.append($("<tr/>").append(goodsName)
						.append(goodsCost).append(goodsAmount).append(
								$("<td/>"))));
				for (var i = 0; i < model.currentGroup.goods.length; i++) {
					var tableRow = $("<tr/>");
					goodsTable.append(tableRow);
					var elementName = $("<td/>").append(
							model.currentGroup.goods[i].name);
					tableRow.append(elementName);

					var elementCost = $("<td/>").append(
							model.currentGroup.goods[i].cost);
					tableRow.append(elementCost);

					var amount = $("<td/>");
					var goodsInputAmount = $("<input/>");
					goodsInputAmount.on("input", (function() {
						var element = goodsInputAmount;
						var goods = model.currentGroup.goods[i];
						return function() {
							ORDERS.controller.handle(createEvent(
									ORDERS.eventsNames.GOODS_AMOUNT_CHANGE, {
										goods : goods,
										value : element.val().replace(
												new RegExp("[^0-9]", "g"), "")
									}));
						};
					})());
					amount.append(goodsInputAmount);
					var currentAmount = model.currentGroup.goods[i].currentAmount;
					if (currentAmount !== null && currentAmount != undefined) {
						goodsInputAmount.val(currentAmount);
					}
					tableRow.append(amount);
					tableRow
							.append($("<td/>")
									.append(
											$("<button/>")
													.append("Заказать")
													.click(
															(function() {
																var good = model.currentGroup.goods[i];
																return function() {
																	ORDERS.controller
																			.handle(createEvent(
																					ORDERS.eventsNames.GOODS_ORDER_CLICK,
																					good));
																};
															})())));

				}
				var basketUL = $("#basket");
				basketUL.empty();
				basketUL.append($("<p/>").append("Корзина"));
				if (model.basket !== null && model.basket !== undefined) {
					var basketTable = $("<table/>");
					basketUL.append(basketTable);
					var tableHead = $("<tr/>");
					basketTable.append(tableHead);
					tableHead.append($("<td/>").append("Название товара"));
					tableHead.append($("<td/>").append("Количество"));
					tableHead.append($("<tr/>"));

					var commonCost = 0;
					for (var i = 0; i < model.basket.goods.length; i++) {
						var tableRow = $("<tr/>");
						basketTable.append(tableRow);
						tableRow.append($("<td/>").append(
								model.basket.goods[i].goods.name));
						tableRow.append($("<td/>").append(
								model.basket.goods[i].amount));
						tableRow
								.append($("<td/>")
										.append(
												$("<button/>")
														.append(
																"удалить из корзины")
														.click(
																(function() {
																	var goods = model.basket.goods[i];
																	return function() {
																		ORDERS.controller
																				.handle(createEvent(
																						ORDERS.eventsNames.GOODS_IN_BASKET_DELETE,
																						goods));
																	};
																})())));
						commonCost += model.basket.goods[i].goods.cost
								* model.basket.goods[i].amount;
					}
					var tableBottom = $("<tr/>");
					basketTable.append(tableBottom);
					tableBottom
							.append($("<td/>").append("Итоговая стоимость:"));
					tableBottom.append($("<td/>").append(commonCost));
					basketUL
							.append($("<button/>")
									.append("Оформить заказ")
									.click(
											function() {
												ORDERS.controller
														.handle(createEvent(
																ORDERS.eventsNames.CREATE_ORDER_CLICK,
																null));
											}));
				} else {
					basketUL.append("На текущий момент в корзине нет товаров");
				}
			}

		},
		handle : function(eventInf) {
			if (eventInf.name === ORDERS.eventsNames.ON_GROUP_CLICK) {
				this.model.currentGroup = eventInf.data;
			} else if (eventInf.name === ORDERS.eventsNames.GOODS_AMOUNT_CHANGE) {
				eventInf.data.goods.currentAmount = eventInf.data.value;

			} else if (eventInf.name === ORDERS.eventsNames.GOODS_ORDER_CLICK) {
				var currentAmount = parseInt(eventInf.data.currentAmount, 10);
				if (isNaN(currentAmount)) {
					alert("Необходимо ввести количество товара");
					this.refresh(this.model);
					return;
				}
				if (this.model.basket === null
						|| this.model.basket === undefined)
					this.model.basket = new BasketGoods(null, [], null);
				for (var i = 0; i < this.model.basket.goods.length; i++) {
					if (this.model.basket.goods[i].goods.id === eventInf.data.id) {
						this.model.basket.goods[i].amount += currentAmount;
						this.refresh(this.model);
						return;
					}
				}
				this.model.basket.goods.push(new GoodsInBasket(null, null,
						parseInt(eventInf.data.currentAmount, 10),
						eventInf.data));

			} else if (eventInf.name === ORDERS.eventsNames.GOODS_IN_BASKET_DELETE) {
				var i;
				var len = this.model.basket.goods.length;
				for (i = 0; i < len; i++)
					if (this.model.basket.goods[i].goods.id === eventInf.data.goods.id) {
						break;
					}
				if (i !== len)
					this.model.basket.goods.splice(i);
				if (this.model.basket.goods.length === 0)
					this.model.basket = null;
			} else if (eventInf.name === ORDERS.eventsNames.CREATE_ORDER_CLICK) {
				sendRequest(
						BASKET_GOODS_REQUESTS_CREATERS.INSERT_BASKET_REQUEST_CREATER
								.createRequest(this.model.basket),
						function(xml) {
							if (checkIsResponseOk(xml)) {
								alert("Ваш заказ оформлен успешно");
								ORDERS.controller.model.basket = null;
								for (var i = 0; i < ORDERS.controller.model.currentGroup.goods.length; i++)
									ORDERS.controller.model.currentGroup.goods[i].currentAmount = undefined;
								ORDERS.controller
										.refresh(ORDERS.controller.model);
							}
						}, CONSTS.webServiceURL);
			}
			this.refresh(this.model);
		},
		model : null
	},
	eventsNames : {
		ON_GROUP_CLICK : "ON_GROUP_CLICK",
		GOODS_ORDER_CLICK : "GOODS_ORDER_CLICK",
		GOODS_AMOUNT_CHANGE : "GOODS_AMOUNT_CHANGE",
		GOODS_IN_BASKET_DELETE : "GOODS_IN_BASKET_DELETE",
		CREATE_ORDER_CLICK : "CREATE_ORDER_CLICK"
	}

};

ORDERS.init();