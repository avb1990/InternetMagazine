package ru.mail.fortune.internetmagazine.goods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

public class GroupGoodsDAOImpl extends IdentifableDAOImpl<GroupGoods> implements
		GroupGoodsDAO {

	public GroupGoodsDAOImpl(DataSource dataSource, String queriesXmlFileName) {
		super(DATABASE_NAME, dataSource, "insert into " + DATABASE_NAME
				+ "(id,name) values(?,?)", "update " + DATABASE_NAME
				+ " set name=? where id =?", new RowMapper<GroupGoods>() {
			public GroupGoods mapRow(java.sql.ResultSet rs, int rowNum)
					throws java.sql.SQLException {
				GroupGoods group = new GroupGoods();
				group.setId(rs.getInt("id"));
				group.setName(rs.getString("name"));
				return group;
			}
		}, queriesXmlFileName);

	}

	public static final String DATABASE_NAME = "goodsgroup";

	@Autowired
	private GoodsDAO goodsDAO;

	@Override
	public void insert(GroupGoods group) {
		super.insert(group);

		goodsDAO.deleteGoodsByGroup(group.getId());
		for (Goods goods : group.getGoods()) {
			goods.setId(Identifable.DEFAULT_ID);
			goodsDAO.insert(goods);
		}
	}

	@Override
	public GroupGoods get(int id) {

		GroupGoods group = super.get(id);
		group.getGoods().addAll(goodsDAO.getGoodsByGroup(group.getId()));
		return group;
	}

	@Override
	public void delete(int id) {
		super.delete(id);
		goodsDAO.deleteGoodsByGroup(id);
	}

	public GroupGoods getByGoods(Goods goods) {
		if (goods == null)
			return null;
		int id = getTemplate().queryForInt(
				"select gg.id from " + DATABASE_NAME + " gg, "
						+ GoodsDAOImpl.DATABASE_NAME
						+ " g where gg.id=g.groupid and g.id=?",
				new Object[] { goods.getId() });
		if (id == 0)
			return null;
		return get(id);
	}

	@Override
	protected List<Object> getSqlParametersFromObject(GroupGoods identifable) {
		ArrayList<Object> parameters = new ArrayList<Object>();
		Collections.addAll(parameters, identifable.getName());
		return parameters;
	}
}
