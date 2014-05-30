package ru.mail.fortune.internetmagazine.goods;

import java.math.BigInteger;
import java.util.Comparator;

public abstract class Identifable {
	public static final int DEFAULT_ID = 0;
	private int id = DEFAULT_ID;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static void checkId(int basketId) {
		if (basketId == DEFAULT_ID)
			throw new IllegalArgumentException("object id cant be "
					+ DEFAULT_ID);
	}

	public static Comparator<Identifable> COMPARATOR = new Comparator<Identifable>() {

		public int compare(Identifable o1, Identifable o2) {
			return o1.getId() - o2.getId();
		}
	};

	public static int getIdFromBigInteger(BigInteger id2) {
		return id2 != null ? id2.intValue() : DEFAULT_ID;
	}
}
