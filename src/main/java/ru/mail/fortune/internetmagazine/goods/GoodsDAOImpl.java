package ru.mail.fortune.internetmagazine.goods;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;

public class GoodsDAOImpl extends IdentifableDAOImpl<Goods> implements GoodsDAO {

	public static final String DATABASE_NAME = "goods";

	public GoodsDAOImpl(DataSource dataSource, String queriesXmlFileName) {

		super(DATABASE_NAME, dataSource, "insert into " + DATABASE_NAME
				+ "(id,name,cost,groupid) values(?,?,?,?)", "update "
				+ DATABASE_NAME + " set name=?,cost= ?,groupid=? where id=?",
				new RowMapper<Goods>() {

					public Goods mapRow(ResultSet rs, int rowNum)
							throws SQLException {

						Goods goods = new Goods();
						goods.setId(rs.getInt("id"));
						goods.setName(rs.getString("name"));
						goods.setCost(rs.getDouble("cost"));
						goods.setGroupId(rs.getInt("groupid"));
						return goods;
					}
				}, queriesXmlFileName);
	}

	private static final String SQL_SELECT_BY_GROUP = "select id from "
			+ DATABASE_NAME + " where groupid=?";

	public List<Goods> getGoodsByGroup(int id) {

		if (id == Identifable.DEFAULT_ID)
			throw new NullPointerException();

		List<Integer> ids = getTemplate().queryForList(SQL_SELECT_BY_GROUP,
				new Object[] { id }, Integer.class);

		List<Goods> goods = new ArrayList<Goods>();
		for (Integer goodsId : ids) {
			goods.add(get(goodsId));
		}
		return goods;
	}

	private static String SQL_DELETE_BY_GROUP = "delete from " + DATABASE_NAME
			+ " where groupid=?";

	public void deleteGoodsByGroup(int id) {
		if (id == Identifable.DEFAULT_ID)
			throw new NullPointerException();
		
		getTemplate().update(SQL_DELETE_BY_GROUP, new Object[] { id });
	}

	@Override
	protected List<Object> getSqlParametersFromObject(Goods identifable) {
		ArrayList<Object> parameters = new ArrayList<Object>();
		Collections.addAll(parameters, identifable.getName(),
				identifable.getCost(), identifable.getGroupId());
		return parameters;

	}
}
