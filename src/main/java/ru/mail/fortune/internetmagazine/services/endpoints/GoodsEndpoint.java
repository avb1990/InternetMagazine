package ru.mail.fortune.internetmagazine.services.endpoints;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import ru.mail.fortune.internetmagazine.goods.Goods;
import ru.mail.fortune.internetmagazine.goods.GoodsBasket;
import ru.mail.fortune.internetmagazine.goods.GroupGoods;
import ru.mail.fortune.internetmagazine.goods.Identifable;
import ru.mail.fortune.internetmagazine.services.InternetMagazineCommonService;
import ru.mail.fortune.webservices.internetmagazine.goods.ActionStatus;
import ru.mail.fortune.webservices.internetmagazine.goods.DeleteGoodsBasketRequest;
import ru.mail.fortune.webservices.internetmagazine.goods.DeleteGoodsBasketResponse;
import ru.mail.fortune.webservices.internetmagazine.goods.DeleteGoodsGroupRequest;
import ru.mail.fortune.webservices.internetmagazine.goods.DeleteGoodsGroupResponse;
import ru.mail.fortune.webservices.internetmagazine.goods.DeleteGoodsRequest;
import ru.mail.fortune.webservices.internetmagazine.goods.DeleteGoodsResponse;
import ru.mail.fortune.webservices.internetmagazine.goods.GetAllGoodsGroupsRequest;
import ru.mail.fortune.webservices.internetmagazine.goods.GetAllGoodsGroupsResponse;
import ru.mail.fortune.webservices.internetmagazine.goods.GetBasketsByPeriodRequest;
import ru.mail.fortune.webservices.internetmagazine.goods.GetBasketsByPeriodResponse;
import ru.mail.fortune.webservices.internetmagazine.goods.InsertGoodsBasketRequest;
import ru.mail.fortune.webservices.internetmagazine.goods.InsertGoodsBasketResponse;
import ru.mail.fortune.webservices.internetmagazine.goods.InsertGoodsGroupRequest;
import ru.mail.fortune.webservices.internetmagazine.goods.InsertGoodsGroupResponse;
import ru.mail.fortune.webservices.internetmagazine.goods.InsertGoodsRequest;
import ru.mail.fortune.webservices.internetmagazine.goods.InsertGoodsResponse;
import ru.mail.fortune.webservices.internetmagazine.goods.TestIsOkRequest;
import ru.mail.fortune.webservices.internetmagazine.goods.TestIsOkResponse;

@Endpoint
public class GoodsEndpoint {
	public static final String TARGET_NAMESPACE = "http://ru/mail/fortune/webservices/internetmagazine/goods";

	@Autowired
	InternetMagazineCommonService service;

	@PayloadRoot(localPart = "GetAllGoodsGroupsRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload
	GetAllGoodsGroupsResponse getGoodsGroups(
			@RequestPayload GetAllGoodsGroupsRequest request) {
		GetAllGoodsGroupsResponse goodsGroupsResponse = new GetAllGoodsGroupsResponse();
		List<GroupGoods> allGroups = service.getAllGroups();
		for (GroupGoods group : allGroups)
			goodsGroupsResponse.getGoodsGroups().add(group.createXml());
		return goodsGroupsResponse;
	}

	@PayloadRoot(localPart = "DeleteGoodsRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload
	DeleteGoodsResponse deleteGoods(@RequestPayload DeleteGoodsRequest request) {
		DeleteGoodsResponse response = new DeleteGoodsResponse();
		checkId(request.getGoodsId());
		service.deleteGoods(request.getGoodsId().intValue());
		response.setActionStatus(getOkActionStatus());
		return response;
	}

	private ActionStatus getOkActionStatus() {
		ActionStatus actionStatus = new ActionStatus();
		actionStatus.setIsOk(true);
		return actionStatus;
	}

	@PayloadRoot(localPart = "DeleteGoodsBasketRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload
	DeleteGoodsBasketResponse deleteGoodsBasket(
			@RequestPayload DeleteGoodsBasketRequest request) {
		DeleteGoodsBasketResponse response = new DeleteGoodsBasketResponse();

		checkId(request.getDeleteGoods());
		service.deleteGoodsBasket(request.getDeleteGoods().intValue());
		response.setActionStatus(getOkActionStatus());
		return response;
	}

	protected void checkId(BigInteger id) {
		if (id == null)
			throw new NullPointerException("id can't be null");
	}

	@PayloadRoot(localPart = "DeleteGoodsGroupRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload
	DeleteGoodsGroupResponse deleteGoodsGroup(
			@RequestPayload DeleteGoodsGroupRequest request) {
		DeleteGoodsGroupResponse response = new DeleteGoodsGroupResponse();
		checkId(request.getDeleteGoodsGroup());
		service.deleteGroupGoods(request.getDeleteGoodsGroup().intValue());
		response.setActionStatus(getOkActionStatus());
		return response;
	}

	@PayloadRoot(localPart = "InsertGoodsRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload
	InsertGoodsResponse insertGoods(@RequestPayload InsertGoodsRequest request) {
		InsertGoodsResponse response = new InsertGoodsResponse();
		service.insertGoods(new Goods(Identifable.getIdFromBigInteger(request
				.getId()),
				Identifable.getIdFromBigInteger(request.getGroupId()), request
						.getName(), request.getCost()));
		response.setActionStatus(getOkActionStatus());
		return response;
	}

	@PayloadRoot(localPart = "InsertGoodsGroupRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload
	InsertGoodsGroupResponse insertGoodsGroup(
			@RequestPayload InsertGoodsGroupRequest request) {
		InsertGoodsGroupResponse response = new InsertGoodsGroupResponse();
		service.insertGroupGoods(new GroupGoods(request.getInsertGoodsGroup()));
		response.setActionStatus(getOkActionStatus());
		return response;
	}

	@PayloadRoot(localPart = "InsertGoodsBasketRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload
	InsertGoodsBasketResponse insertGoodsBasket(
			@RequestPayload InsertGoodsBasketRequest request) {
		InsertGoodsBasketResponse response = new InsertGoodsBasketResponse();
		service.insertGoodsBasket(new GoodsBasket(request.getInsertGoods()));
		response.setActionStatus(getOkActionStatus());
		return response;
	}

	@PayloadRoot(localPart = "GetBasketsByPeriodRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload
	GetBasketsByPeriodResponse getBAsketsByPeriod(
			@RequestPayload GetBasketsByPeriodRequest request) {
		GetBasketsByPeriodResponse response = new GetBasketsByPeriodResponse();
		List<GoodsBasket> baskets = service.getBasketsByPeriod(request
				.getStartDate(), request.getEndDate(),
				request.getGroupId() != null ? request.getGroupId().intValue()
						: null, request.getGoodsId() != null ? request
						.getGoodsId().intValue() : null);
		for (GoodsBasket basket : baskets)
			response.getBaskets().add(basket.createXml());
		response.setActionStatus(getOkActionStatus());
		return response;
	}

	@PayloadRoot(localPart = "TestIsOkRequest", namespace = TARGET_NAMESPACE)
	public @ResponsePayload
	TestIsOkResponse testIsOk(@RequestPayload TestIsOkRequest request) {
		TestIsOkResponse response = new TestIsOkResponse();
		response.setActionStatus(getOkActionStatus());
		return response;
	}
}
