package ru.mail.fortune.internetmagazine.goods;

import java.util.List;

public interface GoodsInBasketDAO {
	public void insert(GoodsInBasket goodsInBasket);

	public List<GoodsInBasket> getByBasket(int basketId);

	public void deleteByBasket(int basketId);
}
