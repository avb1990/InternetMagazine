var ADMINISTRATION = {
	init : function() {
		$(document).ready(this.controller.init);
	},
	controller : {
		init : function() {

			$("#groupNameEdit").on(
					"input",
					function() {
						ADMINISTRATION.controller.handle(createEvent(
								ADMINISTRATION.eventNames.GROUP_NAME_EDIT,
								this.value));
					});
			$("#goodsNameEdit").on(
					"input",
					function() {
						ADMINISTRATION.controller.handle(createEvent(
								ADMINISTRATION.eventNames.GOODS_NAME_EDIT,
								this.value));
					});
			$("#goodsCostEdit").on(
					"input",
					function() {
						ADMINISTRATION.controller.handle(createEvent(
								ADMINISTRATION.eventNames.GOODS_COST_EDIT,
								this.value.replace(new RegExp("[^0-9.]", "g"),
										"")));
					});
			$("#saveGroupChanges").click(
					function() {
						ADMINISTRATION.controller.handle(createEvent(
								ADMINISTRATION.eventNames.SAVE_GROUP_CHANGE,
								null));
					});
			$("#saveGoodsChanges").click(
					function() {
						ADMINISTRATION.controller.handle(createEvent(
								ADMINISTRATION.eventNames.SAVE_GOODS_CHANGE,
								null));
					});
			ADMINISTRATION.controller.getAllGroup();

			LOGIN.openLogInDialog(function() {
				ADMINISTRATION.controller.handle(createEvent(
						ADMINISTRATION.eventNames.LOG_IN_SUCCESS, null));
			});
		},
		model : null,
		refresh : function(model) {
			var groupUL = $("#groupGoods");

			groupUL.empty();
			groupUL.append($("<p/>").append("Группы товаров"));
			var groupTable = $("<table/>");
			groupUL.append(groupTable);
			groupTable.append($("<tr/>").append(
					$("<td/>").attr("colspan", "2").append("Название")));

			for (var i = 0; i < model.groups.length; i++)
				groupTable.append(this.createGroupView(model.groups[i],
						model.currentGroup));
			groupUL.append($("<button>").append("Добавить группу").click(
					function() {
						ADMINISTRATION.controller.handle(createEvent(
								ADMINISTRATION.eventNames.ADD_GROUP, null));
					}));

			var currentGroup = model.currentGroup;
			var goodsUL = $("#goods");
			goodsUL.empty();
			var $groupNameEdit = $("#groupNameEdit");
			var $groupSaveButton = $("#saveGroupChanges");
			if (checkNotNullAndUndefined(currentGroup)) {
				$groupNameEdit.prop('disabled', false);
				$groupSaveButton.prop("disabled", false);
				$groupNameEdit.val(model.editGroupName);
				goodsUL.append($("<p/>").append("Товары"));
				var goodsTable = $("<table/>");
				goodsTable.append($("<tr/>").append(
						$("<td/>").attr("colspan", "2").append("Название")));
				goodsUL.append(goodsTable);
				for (var i = 0; i < currentGroup.goods.length; i++) {
					goodsTable.append(this.createGoodsView(
							currentGroup.goods[i], model.currentGoods));
				}
				goodsUL
						.append($("<button/>")
								.append("добавить товар")
								.click(
										function() {
											ADMINISTRATION.controller
													.handle(createEvent(
															ADMINISTRATION.eventNames.ADD_GOODS,
															null));
										}));
			} else {
				$groupNameEdit.val("");
				$groupNameEdit.prop('disabled', true);
				$groupSaveButton.prop("disabled", true);
			}
			var currentGoods = model.currentGoods;
			var goodsNameEdit = $("#goodsNameEdit");
			var goodsCostEdit = $("#goodsCostEdit");
			var goodsSaveButton = $("#saveGoodsChanges");
			if (checkNotNullAndUndefined(currentGoods)) {
				goodsNameEdit.val(model.editGoodsName);
				goodsNameEdit.prop("disabled", false);
				goodsCostEdit.val(model.editGoodsCost);
				goodsCostEdit.prop("disabled", false);

				goodsSaveButton.prop("disabled", false);
			} else {
				goodsNameEdit.val("");
				goodsNameEdit.prop("disabled", true);
				goodsCostEdit.val("");
				goodsCostEdit.prop("disabled", true);

				goodsSaveButton.prop("disabled", true);
			}

		},
		handle : function(eventInf) {
			if (eventInf.name === ADMINISTRATION.eventNames.ON_GROUP_CLICK) {
				this.model.currentGroup = eventInf.data;
				this.model.currentGoods = null;
				this.resetEditGoodsInModel(this.model);
				this.resetEditGroupInModel(this.model);

			} else if (eventInf.name === ADMINISTRATION.eventNames.ON_GOODS_CLICK) {
				this.model.currentGoods = eventInf.data;
				this.resetEditGoodsInModel(this.model);
			} else if (eventInf.name === ADMINISTRATION.eventNames.DELETE_GOODS) {
				var i = 0;
				var len = this.model.currentGroup.goods.length;
				for (i = 0; i < len; i++) {
					if (this.model.currentGroup.goods[i] === eventInf.data) {
						break;
					}
				}
				if (i < len)
					this.model.currentGroup.goods.splice(i, 1);
				if (this.model.currentGroup.goods[i] === this.model.currentGoods) {
					this.model.currentGoods = null;
					this.resetEditGoodsInModel(this.model);
				}

				if (checkNotNullAndUndefined(eventInf.data.id))
					sendRequest(
							GOODS_REQUESTS_CREATORS.DELETE_GOODS_REQUEST_CREATER
									.createRequest(eventInf.data),
							this.checkIsOkAndGetAllGroups, CONSTS.webServiceURL);
			} else if (eventInf.name === ADMINISTRATION.eventNames.DELETE_GROUP) {
				var i = 0;
				var len = this.model.groups.length;
				for (i = 0; i < len; i++) {
					if (this.model.groups[i] === eventInf.data) {
						break;
					}
				}
				if (i < len) {
					this.model.groups.splice(i, 1);
				}
				if (this.model.currentGroup === eventInf.data) {
					this.model.currentGroup = null;
					this.resetEditGroupInModel(this.model);
					this.resetEditGoodsInModel(this.model);
				}
				if (checkNotNullAndUndefined(eventInf.data.id))
					sendRequest(
							GROUP_GOODS_REQUEST_CREATERS.DELETE_GROUP_REQUEST_CREATER
									.createRequest(eventInf.data),
							this.checkIsOkAndGetAllGroups, CONSTS.webServiceURL);

			} else if (eventInf.name === ADMINISTRATION.eventNames.GROUP_NAME_EDIT) {
				if (checkNotNullAndUndefined(this.model.currentGroup))
					this.model.editGroupName = eventInf.data;
			} else if (eventInf.name === ADMINISTRATION.eventNames.GOODS_NAME_EDIT) {
				if (checkNotNullAndUndefined(this.model.currentGoods))
					this.model.editGoodsName = eventInf.data;
			} else if (eventInf.name === ADMINISTRATION.eventNames.GOODS_COST_EDIT) {
				if (checkNotNullAndUndefined(this.model.currentGoods)) {
					this.model.editGoodsCost = eventInf.data;
				}
			} else if (eventInf.name === ADMINISTRATION.eventNames.SAVE_GROUP_CHANGE) {
				if (ADMINISTRATION.controller.checkGroup(this.model)) {
					this.model.currentGroup.name = this.model.editGroupName;
					var i;
					var len = this.model.groups.length;
					for (i = 0; i < len; i++)
						if (this.model.groups[i] === this.model.currentGroup)
							break;
					if (i === len)
						this.model.groups.push(this.model.currentGroup);

					sendRequest(
							GROUP_GOODS_REQUEST_CREATERS.SAVE_GROUP_REQUEST_CREATER
									.createRequest(this.model.currentGroup),
							this.checkIsOkAndGetAllGroups, CONSTS.webServiceURL);
				}

			} else if (eventInf.name === ADMINISTRATION.eventNames.SAVE_GOODS_CHANGE) {
				if (this.checkCurrentGoods(this.model)) {
					this.model.currentGoods.name = this.model.editGoodsName;
					this.model.currentGoods.cost = parseFloat(this.model.editGoodsCost);
					var i, len = this.model.currentGroup.goods.length;
					for (i = 0; i < len; i++)
						if (this.model.currentGroup.goods[i] === this.model.currentGoods)
							break;
					if (i === len)
						this.model.currentGroup.goods
								.push(this.model.currentGoods);

					sendRequest(GOODS_REQUESTS_CREATORS.SAVE_GOODS_REQUEST
							.createRequest(this.model.currentGoods),
							this.checkIsOkAndGetAllGroups, CONSTS.webServiceURL);
				}
			} else if (eventInf.name === ADMINISTRATION.eventNames.ADD_GOODS) {
				var goods = new Goods(null, null, null,
						this.model.currentGroup.id);
				this.model.currentGoods = goods;
				this.resetEditGoodsInModel(this.model);

			} else if (eventInf.name === ADMINISTRATION.eventNames.ADD_GROUP) {
				group = new GroupGoods(null, null, []);
				this.model.groups.push(group);
				this.model.currentGroup = group;
				this.model.currentGoods = null;
				this.resetEditGoodsInModel(this.model);
				this.resetEditGroupInModel(this.model);
			}

			this.refresh(this.model);
		},
		createGroupView : function(group, currentGoods) {
			var tableRow = $("<tr/>");
			if (group === currentGoods)
				tableRow.addClass("selected");
			var element = $("<td/>").append(group.name).click(
					(function() {
						var curGroup = group;
						return function() {
							ADMINISTRATION.controller.handle(createEvent(
									ADMINISTRATION.eventNames.ON_GROUP_CLICK,
									curGroup));
						};
					})());
			var buttonDelete = $("<td/>")
					.append(
							$("<button/>")
									.append("Удалить")
									.click(
											(function() {
												var curGroup = group;
												return function() {
													ADMINISTRATION.controller
															.handle(createEvent(
																	ADMINISTRATION.eventNames.DELETE_GROUP,
																	curGroup));
												};
											})()));

			return tableRow.append(element).append(buttonDelete);
		},
		createGoodsView : function(goods, currentGoods) {
			var tableRow = $("<tr/>");
			if (currentGoods === goods)
				tableRow.addClass("selected");
			var element = $("<td/>").append(goods.name).click(
					(function() {
						var curGoods = goods;
						return function() {
							ADMINISTRATION.controller.handle(createEvent(
									ADMINISTRATION.eventNames.ON_GOODS_CLICK,
									curGoods));
						};
					})());
			var buttonDelete = $("<td/>")
					.append(
							$("<button/>")
									.append("Удалить")
									.click(
											(function() {
												var curGoods = goods;
												return function() {
													ADMINISTRATION.controller
															.handle(createEvent(
																	ADMINISTRATION.eventNames.DELETE_GOODS,
																	curGoods));
												};
											})()));

			return tableRow.append(element).append(buttonDelete);
		},
		checkGroup : function(model) {
			var group = model.currentGroup;
			if (!checkNotNullAndUndefined(group))
				return true;
			if (model.editGroupName === ""
					|| !checkNotNullAndUndefined(model.editGroupName)) {
				alert("Необходимо указать название группы товаров");
				return false;
			}
			return true;
		},
		checkCurrentGoods : function(model) {
			var goods = model.currentGoods;
			if (!checkNotNullAndUndefined(goods))
				return true;
			if (model.editGoodsName === ""
					|| !checkNotNullAndUndefined(model.editGoodsName)) {
				alert("Необходимо указать название товара");
				return false;
			}
			if (model.editGoodsCost === ""
					|| !checkNotNullAndUndefined(model.editGoodsCost)
					|| isNaN(parseFloat(model.editGoodsCost))) {

				alert("Необходимо указать стоимость товара");
				return false;

			}
			return true;
		},
		resetEditGroupInModel : function(model) {
			if (checkNotNullAndUndefined(model.currentGroup)) {
				model.editGroupName = model.currentGroup.name;
			} else
				model.editGroupName = "";

		},
		resetEditGoodsInModel : function(model) {
			if (checkNotNullAndUndefined(model.currentGoods)) {
				model.editGoodsName = model.currentGoods.name;
				var cost = model.currentGoods.cost;
				if (checkNotNullAndUndefined(cost))
					model.editGoodsCost = cost.toString();
			} else {
				model.editGoodsName = "";
				model.editGoodsCost = "";
			}
		},
		getAllGroup : function() {
			sendRequest(
					GROUP_GOODS_REQUEST_CREATERS.GET_ALL_GROUP_GOODS_REQUEST_CREATER
							.createRequest(null),
					function(xml) {
						var goodsXml = xml.getElementsByTagName("goodsGroups");
						var groups = parseGetAllGroupGoodsResponse(goodsXml);
						if (!checkNotNullAndUndefined(ADMINISTRATION.controller.model)) {
							ADMINISTRATION.controller.model = {
								groups : groups
							};
						} else {
							ADMINISTRATION.controller.model.groups = groups;
						}
						ADMINISTRATION.controller
								.refresh(ADMINISTRATION.controller.model);

					}, CONSTS.webServiceURL);
		},
		checkIsOkAndGetAllGroups : function(xml) {
			if (checkIsResponseOk(xml)) {
				ADMINISTRATION.controller.getAllGroup();
			} else
				alert("Внимание! Произошла ошибка при сохранении изменений");
		},
		logInController : null

	},
	createGroupEditView : function(group) {
		$
				.createElement("div")
				.append("название")
				.append(
						$
								.createElement("input")
								.on(
										"input",
										(function() {
											var curGroup = group;
											return function() {
												ADMINISTRATION.controller
														.handle(createEvent(
																ADMINISTRATION.eventNames.GROUP_NAME_EDIT,
																curGroup));
											};
										})()));
	},
	createEvent : function(name, data) {
		return {
			name : name,
			data : data
		};
	},
	eventNames : {
		DELETE_GROUP : "DELETE_GROUP",
		ON_GROUP_CLICK : "ON_GROUP_CLICK",
		EDIT_GROUP_CLICK : "EDIT_GROUP_CLICK",
		DELETE_GOODS : "DELETE_GOODS",
		EDIT_GOODS : "EDIT_GOODS",
		GROUP_NAME_EDIT : "GROUP_NAME_EDIT",
		GOODS_NAME_EDIT : "GOODS_NAME_EDIT",
		GOODS_COST_EDIT : "GOODS_COST_EDIT",
		ON_GOODS_CLICK : "ON_GOODS_CLICK",
		SAVE_GROUP_CHANGE : "SAVE_GROUP_CHANGE",
		SAVE_GOODS_CHANGE : "SAVE_GOODS_CHANGE",
		ADD_GOODS : "ADD_GOODS",
		ADD_GROUP : "ADD_GROUP",
		LOG_IN_SUCCESS : "LOG_IN_SUCCESS"
	},
};

ADMINISTRATION.init();