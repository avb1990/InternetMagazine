package ru.mail.fortune.internetmagazine.goods;

import java.util.List;

public interface IdentifableDAO<T extends Identifable> {
	public void insert(T group);

	public T get(int id);

	public void delete(int id);

	public List<T> getAll();
}
