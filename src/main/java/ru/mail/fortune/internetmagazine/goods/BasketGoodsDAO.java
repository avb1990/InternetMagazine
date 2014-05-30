package ru.mail.fortune.internetmagazine.goods;

import java.util.Date;
import java.util.List;

public interface BasketGoodsDAO extends IdentifableDAO<GoodsBasket> {

	public List<GoodsBasket> getByPeriod(Date startDate, Date endDate,
			Integer groupId, Integer goodsId);
}
