package ru.mail.fortune.internetmagazine.goods;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.mail.fortune.webservices.internetmagazine.goods.Basket2GoodsXml;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test/repository-test-context.xml" })
public class GoodsInBasketTest extends Assert {

	@Autowired
	GoodsInBasketDAO goodsInBasketDao;

	@Test
	public void insertUpdateTest() {

		List<GoodsInBasket> goodsInBaskets = new ArrayList<GoodsInBasket>();
		goodsInBaskets.add(new GoodsInBasket(1, 1, 5));
		goodsInBaskets.add(new GoodsInBasket(1, 2, 6));
		goodsInBaskets.add(new GoodsInBasket(1, 3, 8));
		goodsInBaskets.add(new GoodsInBasket(1, 4, 3));
		goodsInBaskets.add(new GoodsInBasket(2, 2, 9));
		goodsInBaskets.add(new GoodsInBasket(2, 4, 5));
		goodsInBaskets.add(new GoodsInBasket(2, 5, 5));
		goodsInBaskets.add(new GoodsInBasket(2, 6, 5));

		for (GoodsInBasket goodInBasket : goodsInBaskets)
			goodsInBasketDao.insert(goodInBasket);

		List<GoodsInBasket> goodsInBasketsFromDb = goodsInBasketDao
				.getByBasket(1);

		List<GoodsInBasket> compareBaskets = new ArrayList<GoodsInBasket>(
				goodsInBaskets.subList(0, 4));

		compareTwoList(goodsInBasketsFromDb, compareBaskets);

		goodsInBasketsFromDb = goodsInBasketDao.getByBasket(2);

		compareBaskets = new ArrayList<GoodsInBasket>(goodsInBaskets.subList(5,
				goodsInBaskets.size()));

		goodsInBasketDao.deleteByBasket(1);

		assertEquals(0, goodsInBasketDao.getByBasket(1).size());

		goodsInBasketDao.deleteByBasket(2);

		assertEquals(0, goodsInBasketDao.getByBasket(2).size());
	}

	@Test
	public void compareXmlConvert() {
		GoodsInBasket goodsInBasket = new GoodsInBasket(2, 4, 48);
		Basket2GoodsXml xml = goodsInBasket.createXml();
		compareGoodsInBasketAndXml(goodsInBasket, xml);

		xml.setAmount(BigInteger.valueOf(9348574));
		xml.setBasketId(BigInteger.valueOf(654658));
		xml.setGoodsId(BigInteger.valueOf(9485));

		goodsInBasket = new GoodsInBasket(xml);
		compareGoodsInBasketAndXml(goodsInBasket, xml);
	}

	public static void compareGoodsInBasketAndXml(GoodsInBasket goodsInBasket,
			Basket2GoodsXml xml) {
		assertEquals(xml.getBasketId().intValue(), goodsInBasket.getBasketId());
		assertEquals(xml.getGoodsId().intValue(), goodsInBasket.getGoodsId());
		assertEquals(xml.getAmount().intValue(), goodsInBasket.getAmount());
	}

	public static void compareTwoList(List<GoodsInBasket> goodsInBasketsFromDb,
			List<GoodsInBasket> compareBaskets) {
		Comparator<GoodsInBasket> goodsInBasketComparator = new Comparator<GoodsInBasket>() {

			public int compare(GoodsInBasket o1, GoodsInBasket o2) {
				return (o1.getBasketId() - o2.getBasketId())
						* (o1.getGoodsId() - o2.getGoodsId());
			}
		};
		assertEquals(goodsInBasketsFromDb.size(), compareBaskets.size());
		Collections.sort(goodsInBasketsFromDb, goodsInBasketComparator);
		Collections.sort(compareBaskets, goodsInBasketComparator);
		for (int i = 0; i < goodsInBasketsFromDb.size(); i++)
			compareTwoObject(goodsInBasketsFromDb.get(i), compareBaskets.get(i));

	}

	public static void compareTwoObject(GoodsInBasket goodsInBasket,
			GoodsInBasket goodsInBasket2) {
		assertEquals(goodsInBasket.getBasketId(), goodsInBasket2.getBasketId());
		assertEquals(goodsInBasket.getGoodsId(), goodsInBasket2.getGoodsId());
		assertEquals(goodsInBasket.getAmount(), goodsInBasket2.getAmount());
	}
}
