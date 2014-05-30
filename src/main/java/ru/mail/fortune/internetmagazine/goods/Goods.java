package ru.mail.fortune.internetmagazine.goods;

import java.math.BigInteger;

import ru.mail.fortune.webservices.internetmagazine.goods.GoodsXml;

public class Goods extends Identifable {

	private String name;
	private double cost;
	private int groupId;

	public Goods(GoodsXml xml) {
		if (xml == null)
			throw new NullPointerException("xml can't be null");
		setId(Identifable.getIdFromBigInteger(xml.getId()));
		name = xml.getName();
		cost = xml.getCost();
		groupId = Identifable.getIdFromBigInteger(xml.getGroupId());
	}

	public Goods() {
		// TODO Auto-generated constructor stub
	}

	public Goods(int id, int groupId, String name, double cost) {
		this.setId(id);
		this.groupId = groupId;
		this.name = name;
		this.cost = cost;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public GoodsXml createXml() {
		GoodsXml goodsXml = new GoodsXml();
		goodsXml.setId(BigInteger.valueOf(getId()));
		goodsXml.setCost(cost);
		goodsXml.setName(name);
		goodsXml.setGroupId(BigInteger.valueOf(groupId));
		return goodsXml;
	}

}
