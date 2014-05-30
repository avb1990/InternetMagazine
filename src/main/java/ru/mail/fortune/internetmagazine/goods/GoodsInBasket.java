package ru.mail.fortune.internetmagazine.goods;

import java.math.BigInteger;

import ru.mail.fortune.webservices.internetmagazine.goods.Basket2GoodsXml;

public class GoodsInBasket {
	private int basketId;
	private int goodsId;
	private int amount;

	public GoodsInBasket(int basketId, int goodsId, int amount) {
		super();
		this.basketId = basketId;
		this.goodsId = goodsId;
		this.amount = amount;
	}

	public GoodsInBasket(Basket2GoodsXml xml) {
		if (xml == null)
			throw new NullPointerException("xml can't be null");
		basketId = Identifable.getIdFromBigInteger(xml.getBasketId());
		goodsId = Identifable.getIdFromBigInteger(xml.getGoodsId());
		amount = Identifable.getIdFromBigInteger(xml.getAmount());
	}

	public GoodsInBasket() {
	}

	public int getBasketId() {
		return basketId;
	}

	public void setBasketId(int basketId) {
		this.basketId = basketId;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Basket2GoodsXml createXml() {
		Basket2GoodsXml xml = new Basket2GoodsXml();
		xml.setAmount(BigInteger.valueOf(amount));
		xml.setBasketId(BigInteger.valueOf(basketId));
		xml.setGoodsId(BigInteger.valueOf(goodsId));
		return xml;
	}
}
