package ru.mail.fortune.internetmagazine.goods;

import java.util.List;

public interface GoodsDAO extends IdentifableDAO<Goods> {

	public List<Goods> getGoodsByGroup(int id);

	public void deleteGoodsByGroup(int id);
}
