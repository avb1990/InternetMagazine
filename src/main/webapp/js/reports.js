var REPORTS = {
	init : function() {
		$(document).ready(function() {
			REPORTS.controller.init();
		});
	},
	controller : {
		init : function() {
			$("#startDate").datepicker(
					{
						onSelect : function(text) {
							REPORTS.controller
									.handle(createEvent(
											REPORTS.eventsNames.ON_START_DATE,
											$("#startDate").datepicker(
													"getDate")));
						}
					});
			$("#endDate")
					.datepicker(
							{
								onSelect : function(text) {
									REPORTS.controller.handle(createEvent(
											REPORTS.eventsNames.ON_END_DATE, $(
													"#endDate").datepicker(
													"getDate")));
								}
							});
			$("#selectReportType").change(
					function() {
						REPORTS.controller.handle(createEvent(
								REPORTS.eventsNames.ON_SELECT_REPORT, $(
										"#selectReportType").val()));
					});
			$("#selectGroup").change(
					function() {
						REPORTS.controller.handle(createEvent(
								REPORTS.eventsNames.ON_SELECT_GROUP, $(
										"#selectGroup").val()));
					});
			$("#selectGoods").change(
					function() {
						REPORTS.controller.handle(createEvent(
								REPORTS.eventsNames.ON_SELECT_GOODS, $(
										"#selectGoods").val()));
					});
			$("#btnCreateReport").click(
					function() {
						REPORTS.controller.handle(createEvent(
								REPORTS.eventsNames.ON_CREATE_REPORT, null));
					});
			LOGIN.openLogInDialog(function() {
			});
			sendRequest(
					GROUP_GOODS_REQUEST_CREATERS.GET_ALL_GROUP_GOODS_REQUEST_CREATER
							.createRequest(null),
					function(xml) {

						REPORTS.controller.model = new REPORTS.controller.reportsModel(
								parseGetAllGroupGoodsResponse(xml
										.getElementsByTagName("goodsGroups")));
						REPORTS.controller.refresh(REPORTS.controller.model);
					}, CONSTS.webServiceURL);
		},
		reportsModel : function(groups) {
			this.groups = groups;
			this.reportsTypes = [
					REPORTS.controller.reportsTypes.CUSTOMERS_REPORT,
					REPORTS.controller.reportsTypes.SALES_VOLUME_REPORT ];
			// if (groups.length > 0) {
			// this.currentGroup = groups[0];
			// if (groups[0].goods.length > 0)
			// this.currentGoods = groups[0].goods[0];
			// else
			// this.currentGoods = null;
			// }
		},
		model : null,
		handle : function(eventInf) {
			if (eventInf.name === REPORTS.eventsNames.ON_START_DATE) {
				this.model.startDate = eventInf.data;
				this.model.baskets = null;
			} else if (eventInf.name === REPORTS.eventsNames.ON_END_DATE) {
				this.model.endDate = eventInf.data;
				this.model.baskets = null;
			} else if (eventInf.name === REPORTS.eventsNames.ON_SELECT_REPORT) {
				this.model.currentReportType = eventInf.data;
				this.model.baskets = null;
			} else if (eventInf.name === REPORTS.eventsNames.ON_SELECT_GROUP) {
				for (var i = 0; i < this.model.groups.length; i++)
					if (this.model.groups[i].name.replace(/([\s])\s*/g, " ") === eventInf.data) {
						if (eventInf.data !== this.model.currentGroup)
							this.model.currentGoods = null;
						this.model.currentGroup = this.model.groups[i];
						this.model.baskets = null;
						this.refresh(this.model);
						return;
					}
				this.model.currentGroup = null;
				this.model.currentGoods = null;
				this.model.baskets = null;

			} else if (eventInf.name === REPORTS.eventsNames.ON_SELECT_GOODS) {
				for (var i = 0; i < this.model.currentGroup.goods.length; i++)
					if (this.model.currentGroup.goods[i].name.replace(
							/([\s])\s*/g, " ") === eventInf.data) {
						this.model.currentGoods = this.model.currentGroup.goods[i];
						this.model.baskets = null;
						this.refresh(this.model);
						return;
					}
				this.model.currentGoods = null;
				this.model.baskets = null;
			} else if (eventInf.name === REPORTS.eventsNames.ON_CREATE_REPORT) {
				if (!checkNotNullAndUndefined(this.model.currentReportType)) {
					alert("Необходимо выбрать тип отчета");
					return;
				}
				if (this.model.currentReportType === this.reportsTypes.SALES_VOLUME_REPORT
						&& (!checkNotNullAndUndefined(this.model.currentGroup) && !checkNotNullAndUndefined(this.model.currentGoods))) {
					alert("Необходимо выбрать группу товаротов или товар");
					return;
				}
				sendRequest(
						BASKET_GOODS_REQUESTS_CREATERS.GET_BASKET_BY_PERIOD_REQUEST_CREATER
								.createRequest(new GetBasketsRequestConstraints(
										this.model.startDate,
										this.model.endDate,
										this.model.currentGroup,
										this.model.currentGoods)),
						function(xml) {
							REPORTS.controller.model.baskets = parseBasketsFromGetBasketsByPeriodResponse(xml);
							REPORTS.controller
									.refresh(REPORTS.controller.model);
						}, CONSTS.webServiceURL);
			}
			this.refresh(this.model);
		},
		refresh : function(model) {
			var selectReportType = $("#selectReportType");
			selectReportType.empty();
			var reportEmptyOption = $("<option/>").append("");
			selectReportType.append(reportEmptyOption);
			if (!checkNotNullAndUndefined(model.currentReportType))
				reportEmptyOption.attr("selected", "selected");
			for (var i = 0; i < model.reportsTypes.length; i++) {
				var option = $("<option/>").append(model.reportsTypes[i]);
				if (model.currentReportType === model.reportsTypes[i])
					option.attr("selected", "selected");
				selectReportType.append(option);
			}
			var selectGroup = $("#selectGroup");
			selectGroup.empty();
			var emptyGroupOption = $("<option/>").append("");
			selectGroup.append(emptyGroupOption);
			if (!checkNotNullAndUndefined(model.currentGroup))
				emptyGroupOption.attr("selected", "selected");
			for (var i = 0; i < model.groups.length; i++) {
				var option = $("<option/>").append(model.groups[i].name);
				if (model.currentGroup === model.groups[i])
					option.attr("selected", "selected");
				selectGroup.append(option);
			}
			var selectGood = $("#selectGoods");
			selectGood.empty();
			if (checkNotNullAndUndefined(model.currentGroup)) {
				selectGood.prop('disabled', false);
				var emptyGoodsOption = $("<option/>").append("");
				selectGood.append(emptyGoodsOption);
				if (!checkNotNullAndUndefined(model.currentGoods))
					emptyGoodsOption.attr("selected", "selected");
				for (var i = 0; i < model.currentGroup.goods.length; i++) {
					var option = $("<option/>").append(
							model.currentGroup.goods[i].name);
					if (model.currentGoods === model.currentGroup.goods[i])
						option.attr("selected", "selected");
					selectGood.append(option);
				}
			} else {
				selectGood.prop('disabled', true);
			}
			var reportsArea = $("#reportsArea");
			if (checkNotNullAndUndefined(model.baskets)) {
				var saleDate = $("<th/>").append("Дата покупки");
				var groupName = $("<th/>").append("Название группы товара");
				var goodsName = $("<th/>").append("Название товара");
				var amount = $("<th/>").append("Количество");
				var cost = $("<th/>").append("Общая стоимость");
				reportsArea.empty();
				var isCurrentReportCustomers = model.currentReportType === REPORTS.controller.reportsTypes.CUSTOMERS_REPORT;
				if (isCurrentReportCustomers)
					reportsArea.append($("<thead/>").append(
							$("<tr/>").append(saleDate).append(groupName)
									.append(goodsName).append(amount).append(
											cost)));
				else {
					// if (checkNotNullAndUndefined(model.currentGoods))
					// reportsArea.append($("<thead/>").append(
					// $("<tr/>").append(goodsName).append(cost)));
					// else
					// reportsArea.append($("<thead/>").append(
					// $("<tr/>").append(groupName).append(cost)));
				}
				var cachedGoods = [];
				var cachedGroups = [];
				var commonCost = 0;
				for (var i = 0; i < model.baskets.length; i++) {
					for (var j = 0; j < model.baskets[i].goods.length; j++) {
						var row = $("<tr/>");
						var goodsid = model.baskets[i].goods[j].goodsId;
						var goods = cachedGoods[goodsid];
						if (!checkNotNullAndUndefined(goods)) {
							goods = REPORTS.controller.findGoods(goodsid);
							cachedGoods[goodsid] = goods;
						}
						if (goods === null)
							break;
						if (!isCurrentReportCustomers
								&& (checkNotNullAndUndefined(model.currentGoods)
										&& goodsid !== model.currentGoods.id || model.currentGroup.id !== goods.groupId))
							break;
						var group = cachedGroups[goods.groupId];
						if (!checkNotNullAndUndefined(group)) {
							group = REPORTS.controller.findGroup(goods.groupId);
							cachedGroups[goods.groupId];
						}
						var amount = model.baskets[i].goods[j].amount;
						var allCost = goods.cost * amount;
						commonCost += allCost;
						if (isCurrentReportCustomers) {
							row.append($("<td/>").append(
									formateDate(model.baskets[i].date)));
							row.append($("<td/>").append(group.name));
							row.append($("<td/>").append(goods.name));
							row.append($("<td/>").append(amount));
							row.append($("<td/>").append(allCost));
							reportsArea.append(row);
						}
					}

				}
				var commonText = isCurrentReportCustomers ? "Итого:"
						: (checkNotNullAndUndefined(model.currentGoods) ? "Итого по товару "
								+ model.currentGoods.name + ":"
								: "Итого по группе товаров "
										+ model.currentGroup.name + ":");
				reportsArea.append($("<tr/>").append(
						$("<td/>").attr("colspan", 4).append(commonText))
						.append($("<td/>").append(commonCost)));
			} else {
				reportsArea.empty();
			}

		},
		reportsTypes : {
			CUSTOMERS_REPORT : "Отчет по покупателям",
			SALES_VOLUME_REPORT : "Отчет по продажам"
		},
		findGoods : function(goodsId) {

			for (var i = 0; i < this.model.groups.length; i++)
				for (var j = 0; j < this.model.groups[i].goods.length; j++)
					if (this.model.groups[i].goods[j].id === goodsId)
						return this.model.groups[i].goods[j];
			return null;
		},
		findGroup : function(groupId) {
			for (var i = 0; i < this.model.groups.length; i++) {
				if (this.model.groups[i].id === groupId)
					return this.model.groups[i];
			}
			return null;
		}
	},

	eventsNames : {
		ON_START_DATE : "ON_START_DATE",
		ON_END_DATE : "ON_END_DATE",
		ON_SELECT_GROUP : "ON_SELECT_GROUP",
		ON_SELECT_GOODS : "ON_SELECT_GOODS",
		ON_SELECT_REPORT : "ON_SELECT_REPORT",
		ON_CREATE_REPORT : "ON_CREATE_REPORT"
	},
};
function checkNotNullAndUndefined(value) {
	return value !== null && value !== undefined;
}
REPORTS.init();