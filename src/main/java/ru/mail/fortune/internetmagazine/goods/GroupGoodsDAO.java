package ru.mail.fortune.internetmagazine.goods;


public interface GroupGoodsDAO extends IdentifableDAO<GroupGoods> {

	public GroupGoods getByGoods(Goods goods);
}
