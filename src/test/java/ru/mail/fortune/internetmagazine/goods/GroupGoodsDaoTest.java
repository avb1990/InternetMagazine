package ru.mail.fortune.internetmagazine.goods;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.mail.fortune.webservices.internetmagazine.goods.GoodsGroupXml;
import ru.mail.fortune.webservices.internetmagazine.goods.GoodsXml;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test/repository-test-context.xml" })
public class GroupGoodsDaoTest extends Assert {

	@Autowired
	GoodsDAO goodsDao;

	@Autowired
	GroupGoodsDAO groupGoodsDao;

	DataSource dataSource;
	JdbcTemplate template;

	private Goods goods1;

	private Goods goods2;

	private Goods goods3;

	private Goods goods4;

	private GroupGoods group1;

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
	public void testInsertAndQuery() {
		doBefore();

		GroupGoods group2 = new GroupGoods();
		group2.setName("group2");
		group2.addGoods(goods3);
		group2.addGoods(goods4);
		groupGoodsDao.insert(group2);

		GroupGoods group1FromDb = groupGoodsDao.get(group1.getId());
		compareTwoGroups(group1, group1FromDb);

		List<GroupGoods> groups = groupGoodsDao.getAll();
		assertEquals(groups.size(), 2);
		Collections.sort(groups, Identifable.COMPARATOR);
		compareTwoGroups(groups.get(0), group1);
		compareTwoGroups(groups.get(1), group2);

		group1.setName("new name of group");
		group1.getGoods().remove(1);
		group1.addGoods(goods3);

		groupGoodsDao.insert(group1);
		group1FromDb = groupGoodsDao.get(group1.getId());
		compareTwoGroups(group1, group1FromDb);

		group1FromDb = groupGoodsDao.getByGoods(group1.getGoods().get(0));
		compareTwoGroups(group1, group1FromDb);

	}

	private void doBefore() {
		goods1 = new Goods();
		goods1.setName("good1");
		goods1.setCost(10054.34);

		goods2 = new Goods();
		goods2.setName("good2");
		goods2.setCost(159.58);

		goods3 = new Goods();
		goods3.setName("good3");
		goods3.setCost(2005.45);

		goods4 = new Goods();
		goods4.setName("good4");
		goods4.setCost(99999.54);

		group1 = new GroupGoods();
		group1.setName("group1");
		group1.addGoods(goods1);
		group1.addGoods(goods2);
		groupGoodsDao.insert(group1);
	}

	@Test
	public void checkXmlImport() {
		doBefore();

		GoodsGroupXml xml = group1.createXml();

		compareGroupAndXml(group1, xml);

		xml.setId(BigInteger.valueOf(13));
		xml.setName("the name");
		xml.getGoods().add(goods1.createXml());
		xml.getGoods().add(goods2.createXml());
		xml.getGoods().add(goods3.createXml());
		xml.getGoods().add(goods4.createXml());
		for (GoodsXml goodsXml : xml.getGoods())
			goodsXml.setGroupId(BigInteger.valueOf(13));

		group1 = new GroupGoods(xml);
		compareGroupAndXml(group1, xml);
	}

	private void compareGroupAndXml(GroupGoods group, GoodsGroupXml xml) {
		assertEquals(group.getId(), xml.getId().intValue());
		assertEquals(group.getName(), xml.getName());
		assertEquals(group.getGoods().size(), xml.getGoods().size());
		for (int i = 0; i < group.getGoods().size(); i++)
			GoodsDAOTest.compareGoodsAndXml(group.getGoods().get(i), xml
					.getGoods().get(i));

	}

	public void compareTwoGroups(GroupGoods group1, GroupGoods group1FromDb) {
		assertEquals(group1.getId(), group1FromDb.getId());
		assertEquals(group1.getName(), group1FromDb.getName());
		Collections.sort(group1.getGoods(), Identifable.COMPARATOR);
		Collections.sort(group1FromDb.getGoods(), Identifable.COMPARATOR);
		assertEquals(group1.getGoods().size(), group1FromDb.getGoods().size());
		for (int i = 0; i < group1.getGoods().size(); i++)
			GoodsDAOTest.compareTwoGoods(group1.getGoods().get(i), group1FromDb
					.getGoods().get(i));
	}
}
