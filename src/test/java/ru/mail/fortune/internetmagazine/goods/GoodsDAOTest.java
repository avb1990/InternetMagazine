package ru.mail.fortune.internetmagazine.goods;

import java.math.BigInteger;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.mail.fortune.webservices.internetmagazine.goods.GoodsXml;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test/repository-test-context.xml" })
public class GoodsDAOTest extends TestCase {
	@Autowired
	GoodsDAO goodsDao;

	@Autowired
	GroupGoodsDAO groupGoodsDao;

	DataSource dataSource;
	JdbcTemplate template;

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
		template.update("delete from " + GroupGoodsDAOImpl.DATABASE_NAME);
	}

	@Test
	public void insertAndGetGoods() {
		Goods goods = new Goods();
		goods.setCost(25.45);
		goods.setGroupId(Identifable.DEFAULT_ID);
		goods.setName("name");

		goodsDao.insert(goods);

		assertEquals(goods.getId(), 1);

		Goods goodsFromDb = goodsDao.get(goods.getId());
		compareTwoGoods(goods, goodsFromDb);

		try {
			goods.setId(2);
			goodsDao.insert(goods);
			fail("тут должно упасть с ошибкой!");
		} catch (Exception e) {

		}

		goods.setId(1);
		goods.setName("new name");
		goods.setCost(105.98);

		goodsDao.insert(goods);

		goodsFromDb = goodsDao.get(goods.getId());
		compareTwoGoods(goods, goodsFromDb);

		goodsDao.delete(goods.getId());
		assertNull(goodsDao.get(goods.getId()));
	}

	@Test
	public void checkXmlImport() {
		Goods goods = new Goods();
		goods.setId(1);
		goods.setGroupId(2);
		goods.setCost(45.45);
		goods.setName("name name");

		GoodsXml xml = goods.createXml();
		compareGoodsAndXml(goods, xml);

		xml.setId(BigInteger.valueOf(1));
		xml.setCost(345.455);
		xml.setGroupId(BigInteger.valueOf(48));

		goods = new Goods(xml);
		compareGoodsAndXml(goods, xml);

	}

	public static void compareGoodsAndXml(Goods goods, GoodsXml xml) {
		assertEquals(xml.getId().intValue(), goods.getId());
		assertEquals(xml.getGroupId().intValue(), goods.getGroupId());
		assertEquals(xml.getCost(), goods.getCost());
		assertEquals(xml.getName(), goods.getName());
	}

	public static void compareTwoGoods(Goods goods, Goods goodsFromDb) {
		assertEquals(goodsFromDb.getCost(), goods.getCost());
		assertEquals(goods.getName(), goodsFromDb.getName());
		assertEquals(goods.getId(), goodsFromDb.getId());
		assertEquals(goods.getGroupId(), goodsFromDb.getGroupId());
	}
}
