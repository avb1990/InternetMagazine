package ru.mail.fortune.internetmagazine.goods;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

public class BasketGoodsDAOImpl extends IdentifableDAOImpl<GoodsBasket>
		implements BasketGoodsDAO {

	public BasketGoodsDAOImpl(DataSource dataSource, String queriesXmlFileName) {
		super(DATABASE_NAME, dataSource, "insert into " + DATABASE_NAME
				+ "(id,date) values(?,?)", "update " + DATABASE_NAME
				+ " set date=? where id = ?", new RowMapper<GoodsBasket>() {

			public GoodsBasket mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				GoodsBasket basket = new GoodsBasket();
				basket.setId(rs.getInt("id"));
				basket.setDate(new Date(rs.getLong("date")));
				return basket;
			}
		}, queriesXmlFileName);
	}

	@Autowired
	private GoodsDAO goodsDAO;

	@Autowired
	private GroupGoodsDAO groupDao;

	@Autowired
	GoodsInBasketDAO goodsInBasketDao;

	public static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
	public static final String DATABASE_NAME = "goodsbasket";

	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			DATE_FORMAT_STRING);

	@Override
	public void insert(GoodsBasket identifable) {
		if (identifable.getDate() == null)
			identifable.setDate(new Date());
		super.insert(identifable);
		goodsInBasketDao.deleteByBasket(identifable.getId());
		for (GoodsInBasket goodsInBasket : identifable.getGoods())
			goodsInBasketDao.insert(goodsInBasket);
	}

	@Override
	public GoodsBasket get(int id) {
		GoodsBasket basket = super.get(id);
		if (basket != null)
			basket.setGoods(goodsInBasketDao.getByBasket(id));
		return basket;
	}

	public static final String DATABASE_MANY_TO_MANY_MAPPING_NAME = GoodsInBasketDAOImpl.DATABASE_NAME;

	@Override
	public void delete(int id) {
		super.delete(id);
		goodsInBasketDao.deleteByBasket(id);
	}

	public List<GoodsBasket> getByPeriod(Date startDate, Date endDate,
			Integer groupId, Integer goodsId) {
		if (startDate != null && endDate != null
				&& startDate.compareTo(endDate) > 0)
			throw new IllegalArgumentException(
					"дата начала не может быть раньше даты конца!");
		List<GoodsBasket> baskets = new ArrayList<GoodsBasket>();
		String sqlStartDate = startDate != null ? String.valueOf(
				startDate.getTime()).toString() : null;
		String sqlEndDate = endDate != null ? String.valueOf(endDate.getTime())
				: null;
		String sqlWhere = null;
		if (sqlStartDate != null || sqlEndDate != null) {
			String sqlStartDateWhere = sqlStartDate != null ? ("basket.date>='"
					+ sqlStartDate + "'") : null;
			String sqlEndDateWhere = sqlEndDate != null ? ("basket.date<='"
					+ sqlEndDate + "'") : null;
			if (sqlStartDateWhere != null && sqlEndDateWhere == null)
				sqlWhere = sqlStartDateWhere;
			else if (sqlStartDateWhere == null && sqlEndDateWhere != null)
				sqlWhere = sqlEndDateWhere;
			else
				sqlWhere = sqlStartDateWhere + " and " + sqlEndDateWhere;
		}
		String sqlFrom = DATABASE_NAME + " basket";
		if (goodsId != null) {
			String sqlGoodsIdWhere = "basket.id=gib.basketid and gib.goodsid="
					+ goodsId;
			sqlWhere = sqlWhere == null ? sqlGoodsIdWhere
					: (sqlWhere + " and " + sqlGoodsIdWhere);
			sqlFrom += ", " + DATABASE_MANY_TO_MANY_MAPPING_NAME + " gib";
		} else if (groupId != null) {
			String sqlGoodsIdWhere = "basket.id=gib.basketid and gib.goodsid in(select id from "
					+ GoodsDAOImpl.DATABASE_NAME
					+ " where groupid="
					+ groupId
					+ ")";
			sqlWhere = sqlWhere == null ? sqlGoodsIdWhere
					: (sqlWhere + " and " + sqlGoodsIdWhere);
			sqlFrom += ", " + DATABASE_MANY_TO_MANY_MAPPING_NAME + " gib";
		}
		List<Integer> ids = getTemplate().queryForList(
				"select id from " + sqlFrom
						+ (sqlWhere != null ? (" where " + sqlWhere) : ""),
				Integer.class);
		for (Integer id : ids)
			baskets.add(get(id));
		return baskets;
	}

	@Override
	protected List<Object> getSqlParametersFromObject(GoodsBasket identifable) {
		return new ArrayList<Object>(Arrays.asList(identifable.getDate()));
	}

}
