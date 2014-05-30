package ru.mail.fortune.internetmagazine.services;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import ru.mail.fortune.internetmagazine.goods.Goods;
import ru.mail.fortune.internetmagazine.goods.GoodsBasket;
import ru.mail.fortune.internetmagazine.goods.GroupGoods;

public interface InternetMagazineCommonService {

	public List<GroupGoods> getAllGroups();

	public void insertBasket(GoodsBasket basket);

	public void deleteGoods(int id);

	public void deleteGoodsBasket(int id);

	public void deleteGroupGoods(int id);

	public void insertGoods(Goods goods);

	public void insertGroupGoods(GroupGoods GroupGoods);

	public void insertGoodsBasket(GoodsBasket goodsBasket);

	public List<GoodsBasket> getBasketsByPeriod(XMLGregorianCalendar startDate,
			XMLGregorianCalendar endDate, Integer groupId, Integer goodsId);
}
