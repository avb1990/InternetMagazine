package ru.mail.fortune.internetmagazine.goods;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.Test;
import org.xml.sax.SAXException;

public class XmlQueryParserTest extends Assert {

	@Test
	public void checkQueryParser() throws IOException,
			ParserConfigurationException, SAXException {
		List<String> queries = IdentifableDAOImpl.getQueriesFromFile(getClass()
				.getResource("/test/testQueries.xml").getPath());
		assertEquals(queries.size(), 5);
		for (int i = 1; i <= 5; i++)
			assertEquals(queries.get(i - 1), "query" + i);
	}

}
