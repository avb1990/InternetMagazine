package ru.mail.fortune.internetmagazine.goods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GoodsInBasketDAOImpl implements GoodsInBasketDAO {

	@Autowired
	DataSource dataSource;

	private JdbcTemplate template;

	public static final String DATABASE_NAME = "goodsInBasket";

	private JdbcTemplate getTemplate() {
		if (template == null)
			template = new JdbcTemplate(dataSource);
		return template;
	}

	public void insert(GoodsInBasket goodsInBasket) {

		if (goodsInBasket == null)
			throw new NullPointerException("object can't be null");

		Identifable.checkId(goodsInBasket.getGoodsId());
		Identifable.checkId(goodsInBasket.getBasketId());

		int count = getTemplate().queryForInt(
				"select count(*) from " + DATABASE_NAME
						+ " where basketid=? and goodsid=?",
				new Object[] { goodsInBasket.getBasketId(),
						goodsInBasket.getGoodsId() });

		if (count != 0) {
			getTemplate().update(
					"update " + DATABASE_NAME
							+ " set amount=? where basketid=?,goodsid=?",
					new Object[] { goodsInBasket.getAmount(),
							goodsInBasket.getBasketId(),
							goodsInBasket.getGoodsId() });
		} else {
			getTemplate().update(
					"insert into " + DATABASE_NAME
							+ "(goodsid,basketid,amount) values(?,?,?)",
					new Object[] { goodsInBasket.getGoodsId(),
							goodsInBasket.getBasketId(),
							goodsInBasket.getAmount() });
		}
	}

	public List<GoodsInBasket> getByBasket(int basketId) {
		Identifable.checkId(basketId);
		List<Map<String, Object>> rows = getTemplate().queryForList(
				"select goodsid,amount from " + DATABASE_NAME
						+ " where basketid=?", new Object[] { basketId });

		List<GoodsInBasket> goodsInBaskets = new ArrayList<GoodsInBasket>();
		for (Map<String, Object> row : rows) {
			GoodsInBasket goodsInBasket = new GoodsInBasket();
			goodsInBasket.setBasketId(basketId);
			goodsInBasket.setGoodsId((Integer) row.get("goodsid"));
			goodsInBasket.setAmount((Integer) row.get("amount"));
			goodsInBaskets.add(goodsInBasket);
		}
		return goodsInBaskets;
	}

	public void deleteByBasket(int basketId) {
		Identifable.checkId(basketId);

		getTemplate().update(
				"delete from " + DATABASE_NAME + " where basketid=?",
				new Object[] { basketId });
	}

}
