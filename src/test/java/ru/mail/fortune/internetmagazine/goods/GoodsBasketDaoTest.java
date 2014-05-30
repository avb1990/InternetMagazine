package ru.mail.fortune.internetmagazine.goods;

import java.beans.XMLDecoder;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.mail.fortune.webservices.internetmagazine.goods.Basket2GoodsXml;
import ru.mail.fortune.webservices.internetmagazine.goods.GoodsBasketXml;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test/repository-test-context.xml" })
public class GoodsBasketDaoTest extends Assert {

	@Autowired
	GoodsDAO goodsDao;

	@Autowired
	BasketGoodsDAO basketGoodsDao;

	@Autowired
	GroupGoodsDAO groupGoodsDao;

	DataSource dataSource;
	JdbcTemplate template;

	private Goods goods1;

	private Goods goods2;

	private GroupGoods group;

	private Goods goods3;

	private Goods goods4;

	private Goods goods5;

	private Goods goods6;

	private GroupGoods group1;

	private Date curDate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Before
	public void setUp() {
		template = new JdbcTemplate(dataSource);
	}

	@After
	public void tearDown() {
		template.update("delete from " + GoodsDAOImpl.DATABASE_NAME);
		template.update("delete from " + BasketGoodsDAOImpl.DATABASE_NAME);
		template.update("delete from "
				+ BasketGoodsDAOImpl.DATABASE_MANY_TO_MANY_MAPPING_NAME);
		template.update("delete from " + GroupGoodsDAOImpl.DATABASE_NAME);
	}

	@Test
	public void insertDeleteAndQueryTest() {

		fillBefore();

		GoodsBasket basket = new GoodsBasket();
		basket.setDate(getCurrentDate());
		basket.addGoods(goods1);
		basket.addGoods(goods2);
		basket.addGoods(goods3);
		basketGoodsDao.insert(basket);

		GoodsBasket basketFromDb = basketGoodsDao.get(basket.getId());

		compareTwoBaskets(basket, basketFromDb);

		Date someAnotherDate = getCurrentDate();
		someAnotherDate.setYear(38);
		basket.setDate(someAnotherDate);
		basket.getGoods().remove(2);

		Goods anotherGoods = new Goods();
		anotherGoods.setCost(928347273764.29384);
		anotherGoods.setName("another name");
		anotherGoods.setGroupId(group.getId());
		goodsDao.insert(anotherGoods);

		basket.addGoods(anotherGoods);
		basketGoodsDao.insert(basket);

		basketFromDb = basketGoodsDao.get(basket.getId());
		compareTwoBaskets(basket, basketFromDb);

		basketGoodsDao.delete(basket.getId());

		assertNull(basketGoodsDao.get(basket.getId()));
	}

	public Date getCurrentDate() {
		if (curDate == null) {
			curDate = new Date();
			curDate.setTime(curDate.getTime() - curDate.getTime() % 1000); // очищаем
																			// миллисекунды
		}
		return (Date) curDate.clone();
	}

	private void fillBefore() {
		goods1 = new Goods();
		goods1.setCost(123.56);
		goods1.setName("the name1");
		goodsDao.insert(goods1);

		goods2 = new Goods();
		goods2.setCost(182837.123);
		goods2.setName("the name2");
		goodsDao.insert(goods2);

		goods3 = new Goods();
		goods3.setCost(98.345345);
		goods3.setName("the name3");
		goodsDao.insert(goods3);

		group = new GroupGoods();
		group.setName("group1");
		group.getGoods().add(goods1);
		group.getGoods().add(goods2);
		group.getGoods().add(goods3);
		groupGoodsDao.insert(group);

		goods4 = new Goods();
		goods4.setCost(64545);
		goods4.setName("the name4");
		goodsDao.insert(goods4);

		goods5 = new Goods();
		goods5.setCost(65489221.34);
		goods5.setName("the name5");
		goodsDao.insert(goods5);

		goods6 = new Goods();
		goods6.setCost(98.345345);
		goods6.setName("the name6");
		goodsDao.insert(goods6);

		group1 = new GroupGoods();
		group1.setName("group2");
		group1.getGoods().add(goods4);
		group1.getGoods().add(goods5);
		group1.getGoods().add(goods6);
		groupGoodsDao.insert(group1);

	}

	@Test
	public void getByDateTest() {
		fillBefore();
		Date beforeStart = getCurrentDate();
		beforeStart.setYear(29);

		Date startDate = getCurrentDate();
		startDate.setYear(30);

		Date middleDate = getCurrentDate();
		middleDate.setYear(31);

		Date endDate = getCurrentDate();
		endDate.setYear(32);

		Date afterEnd = getCurrentDate();
		afterEnd.setYear(33);

		GoodsBasket basketBeforeStart = new GoodsBasket();
		basketBeforeStart.setDate(beforeStart);
		basketBeforeStart.addGoods(goods1);
		basketBeforeStart.addGoods(goods2);
		basketGoodsDao.insert(basketBeforeStart);

		GoodsBasket basketInTheMiddle1 = new GoodsBasket();
		basketInTheMiddle1.setDate((middleDate));
		basketInTheMiddle1.addGoods(goods3);
		basketGoodsDao.insert(basketInTheMiddle1);

		GoodsBasket basketInTheMiddle2 = new GoodsBasket();
		basketInTheMiddle2.setDate((middleDate));
		basketInTheMiddle2.addGoods(goods4);
		basketInTheMiddle2.addGoods(goods5);
		basketGoodsDao.insert(basketInTheMiddle2);

		GoodsBasket basketAfterEnd = new GoodsBasket();
		basketAfterEnd.setDate((afterEnd));
		basketAfterEnd.addGoods(goods6);
		basketAfterEnd.addGoods(goods1);
		basketGoodsDao.insert(basketAfterEnd);

		List<GoodsBasket> goodsBasketsFromDb = basketGoodsDao.getByPeriod(
				startDate, endDate, null, null);
		List<GoodsBasket> goodsBaskets = new ArrayList<GoodsBasket>(
				Arrays.asList(basketInTheMiddle1, basketInTheMiddle2));
		compareTwoBasketsList(goodsBasketsFromDb, goodsBaskets);

		goodsBasketsFromDb = basketGoodsDao.getByPeriod(afterEnd, null, null, null);
		goodsBaskets = Arrays.asList(basketAfterEnd);
		compareTwoBasketsList(goodsBasketsFromDb, goodsBaskets);

		goodsBasketsFromDb = basketGoodsDao.getByPeriod(null, startDate, null, null);
		goodsBaskets = Arrays.asList(basketBeforeStart);
		compareTwoBasketsList(goodsBasketsFromDb, goodsBaskets);

		goodsBasketsFromDb = basketGoodsDao.getByPeriod(null, null, null, null);
		goodsBaskets = Arrays.asList(basketBeforeStart, basketInTheMiddle1,
				basketInTheMiddle2, basketAfterEnd);
		compareTwoBasketsList(goodsBasketsFromDb, goodsBaskets);

		try {
			goodsBasketsFromDb = basketGoodsDao.getByPeriod(endDate, startDate, null, null);
			fail("тут должно упасть!");
		} catch (IllegalArgumentException t) {

		}

	}

	@Test
	public void checkXmlInput() {
		fillBefore();
		GoodsBasket basket = new GoodsBasket();
		basket.setId(28);
		basket.setDate(getCurrentDate());
		basket.addGoods(goods1);
		basket.addGoods(goods2);
		basket.addGoods(goods3);
		basket.addGoods(goods4);
		GoodsBasketXml xml = basket.createXml();

		compareBasketAndXml(basket, xml);

		int id = 1498989;
		xml.setId(BigInteger.valueOf(id));
		xml.setDate(Utils.date2XMLGregorianCalendar(getCurrentDate()));
		xml.getGoods().add(
				new GoodsInBasket(id, goods1.getId(), 234).createXml());
		xml.getGoods().add(
				new GoodsInBasket(id, goods2.getId(), 12).createXml());
		xml.getGoods()
				.add(new GoodsInBasket(id, goods3.getId(), 1).createXml());
		xml.getGoods().add(
				new GoodsInBasket(id, goods4.getId(), 131313).createXml());

		basket = new GoodsBasket(xml);

		compareBasketAndXml(basket, xml);
	}

	private void compareBasketAndXml(GoodsBasket basket, GoodsBasketXml xml) {
		assertEquals(basket.getId(), xml.getId().intValue());
		compareXmlAndDate(basket.getDate(), xml.getDate());
		assertEquals(basket.getGoods().size(), xml.getGoods().size());
		for (int i = 0; i < basket.getGoods().size(); i++)
			GoodsInBasketTest.compareGoodsInBasketAndXml(
					basket.getGoods().get(i), xml.getGoods().get(i));

	}

	public void compareTwoBasketsList(List<GoodsBasket> goodsBasketsFromDb,
			List<GoodsBasket> goodsBaskets) {
		assertEquals(goodsBasketsFromDb.size(), goodsBaskets.size());
		Collections.sort(goodsBasketsFromDb, Identifable.COMPARATOR);
		Collections.sort(goodsBaskets, Identifable.COMPARATOR);
		for (int i = 0; i < goodsBasketsFromDb.size(); i++)
			compareTwoBaskets(goodsBasketsFromDb.get(i), goodsBaskets.get(i));
	}

	public void compareTwoBaskets(GoodsBasket basket, GoodsBasket basketFromDb) {
		assertEquals(basketFromDb.getDate(), basket.getDate());
		Comparator<GoodsInBasket> goodsInBasketComparator = new Comparator<GoodsInBasket>() {

			public int compare(GoodsInBasket o1, GoodsInBasket o2) {
				return o1.getBasketId() - o2.getBasketId();
			}
		};
		Collections.sort(basketFromDb.getGoods(), goodsInBasketComparator);
		Collections.sort(basket.getGoods(), goodsInBasketComparator);
		for (int i = 0; i < basketFromDb.getGoods().size(); i++) {
			compareTwoGoodsInBasket(basketFromDb.getGoods().get(i), basket
					.getGoods().get(i));
		}
	}

	private void compareTwoGoodsInBasket(GoodsInBasket goodsInBasket,
			GoodsInBasket goodsInBasket2) {
		assertEquals(goodsInBasket.getBasketId(), goodsInBasket2.getBasketId());
		assertEquals(goodsInBasket.getAmount(), goodsInBasket2.getAmount());
		assertEquals(goodsInBasket.getGoodsId(), goodsInBasket2.getGoodsId());
	}

	@Test
	public void testDateUtils() {
		Date date = getCurrentDate();
		date.setYear(2014);
		date.setMonth(2);
		date.setDate(6);
		date.setHours(18);
		date.setMinutes(47);
		date.setSeconds(45);

		BasketGoodsDAOImpl.DATE_FORMAT.format(date).equals(
				"2014-02-06 18:47:45");
	}

	@Test
	public void testXmlToDate() {
		Date date = getCurrentDate();
		date.setYear(2014);
		date.setMonth(2);
		date.setDate(6);
		date.setHours(18);
		date.setMinutes(47);
		date.setSeconds(45);

		XMLGregorianCalendar xmlDate = Utils.date2XMLGregorianCalendar(date);
		compareXmlAndDate(date, xmlDate);

		date = Utils.xmlGregorianCalendar2date(xmlDate);

		compareXmlAndDate(date, xmlDate);

	}

	protected void compareXmlAndDate(Date date, XMLGregorianCalendar xmlDate) {
		assertEquals(date.getYear(), xmlDate.getYear() - 1900);
		assertEquals(date.getMonth(), xmlDate.getMonth() - 1);
		assertEquals(date.getDate(), xmlDate.getDay());
		assertEquals(date.getHours(), xmlDate.getHour());
		assertEquals(date.getMinutes(), xmlDate.getMinute());
		assertEquals(date.getSeconds(), xmlDate.getSecond());
	}
}
