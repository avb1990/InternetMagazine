package ru.mail.fortune.internetmagazine.services;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.mail.fortune.internetmagazine.goods.BasketGoodsDAO;
import ru.mail.fortune.internetmagazine.goods.Goods;
import ru.mail.fortune.internetmagazine.goods.GoodsBasket;
import ru.mail.fortune.internetmagazine.goods.GoodsDAO;
import ru.mail.fortune.internetmagazine.goods.GroupGoods;
import ru.mail.fortune.internetmagazine.goods.GroupGoodsDAO;
import ru.mail.fortune.internetmagazine.goods.Utils;

@Service
public class InternetMagazineCommonServiceImpl implements
		InternetMagazineCommonService {

	@Autowired
	private GoodsDAO goodsDao;

	@Autowired
	private GroupGoodsDAO groupGoodsDao;

	@Autowired
	private BasketGoodsDAO basketGoodsDao;

	public List<GroupGoods> getAllGroups() {
		return groupGoodsDao.getAll();
	}

	public void insertBasket(GoodsBasket basket) {
		basketGoodsDao.insert(basket);
	}

	public void insertGoods(Goods goods) {
		goodsDao.insert(goods);
	}

	public void insertGroupGoods(GroupGoods GroupGoods) {
		groupGoodsDao.insert(GroupGoods);
	}

	public void insertGoodsBasket(GoodsBasket insertGoods) {
		basketGoodsDao.insert(insertGoods);
	}

	public List<GoodsBasket> getBasketsByPeriod(XMLGregorianCalendar startDate,
			XMLGregorianCalendar endDate, Integer groupId, Integer goodsId) {
		return basketGoodsDao.getByPeriod(
				Utils.xmlGregorianCalendar2date(startDate),
				Utils.xmlGregorianCalendar2date(endDate), groupId, goodsId);
	}

	public void deleteGoods(int id) {
		goodsDao.delete(id);

	}

	public void deleteGoodsBasket(int id) {
		basketGoodsDao.delete(id);

	}

	public void deleteGroupGoods(int id) {
		groupGoodsDao.delete(id);
	}
}
