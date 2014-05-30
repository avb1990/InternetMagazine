package ru.mail.fortune.internetmagazine.goods;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import ru.mail.fortune.webservices.internetmagazine.goods.GoodsGroupXml;
import ru.mail.fortune.webservices.internetmagazine.goods.GoodsXml;

public class GroupGoods extends Identifable {

	private String name;
	private List<Goods> goods = new ArrayList<Goods>();

	public GroupGoods(GoodsGroupXml xml) {
		if (xml == null)
			throw new NullPointerException("xml can't be null");
		setId(Identifable.getIdFromBigInteger(xml.getId()));
		name = xml.getName();
		for (GoodsXml goodsXml : xml.getGoods())
			addGoods(new Goods(goodsXml));
	}

	public GroupGoods() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		for (Goods goods : this.goods)
			goods.setGroupId(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Goods> getGoods() {
		return goods;
	}

	public void setGoods(List<Goods> goods) {
		this.goods = goods;
	}

	public void addGoods(Goods goods) {
		goods.setGroupId(getId());
		this.goods.add(goods);
	}

	public GoodsGroupXml createXml() {
		GoodsGroupXml xml = new GoodsGroupXml();
		xml.setId(BigInteger.valueOf(getId()));
		xml.setName(name);
		for (Goods anotherGoods : goods)
			xml.getGoods().add(anotherGoods.createXml());
		return xml;
	}
}
