package ru.mail.fortune.internetmagazine.goods;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.mail.fortune.webservices.internetmagazine.goods.Basket2GoodsXml;
import ru.mail.fortune.webservices.internetmagazine.goods.GoodsBasketXml;

public class GoodsBasket extends Identifable {

	private Date date;
	private List<GoodsInBasket> goods = new ArrayList<GoodsInBasket>();

	public GoodsBasket(GoodsBasketXml xml) {
		setId(Identifable.getIdFromBigInteger(xml.getId()));
		date = Utils.xmlGregorianCalendar2date(xml.getDate());
		for (Basket2GoodsXml goodsInBasketXml : xml.getGoods())
			goods.add(new GoodsInBasket(goodsInBasketXml));
	}

	public GoodsBasket() {
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<GoodsInBasket> getGoods() {
		return goods;
	}

	public void setGoods(List<GoodsInBasket> goods) {
		this.goods = goods;
	}

	public void addGoods(Goods goods) {
		if (goods == null || goods.getId() == Identifable.DEFAULT_ID)
			throw new NullPointerException("goods can't be null");

		for (GoodsInBasket goodsInBasket : this.goods)
			if (goodsInBasket.getGoodsId() == goods.getId())
				return;

		GoodsInBasket goodsInBasket = new GoodsInBasket();
		goodsInBasket.setGoodsId(goods.getId());
		goodsInBasket.setBasketId(getId());
		this.goods.add(goodsInBasket);
	}

	public void addGoods(GoodsInBasket goodsInBasket) {
		goodsInBasket.setBasketId(getId());
		this.goods.add(goodsInBasket);
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		for (GoodsInBasket goodsInBasket : goods)
			goodsInBasket.setBasketId(id);
	}

	public GoodsBasketXml createXml() {
		GoodsBasketXml xml = new GoodsBasketXml();
		xml.setId(BigInteger.valueOf(getId()));
		xml.setDate(Utils.date2XMLGregorianCalendar(date));
		for (GoodsInBasket g2b : goods)
			xml.getGoods().add(g2b.createXml());
		return xml;
	}
}
