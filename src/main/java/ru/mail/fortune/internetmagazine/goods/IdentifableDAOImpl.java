package ru.mail.fortune.internetmagazine.goods;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class IdentifableDAOImpl<T extends Identifable> implements
		IdentifableDAO<T> {

	private String dbName;
	private DataSource dataSource;
	private JdbcTemplate template;
	private String sqlInsert;
	private String sqlUpdate;
	private RowMapper<T> rowMapper;
	private String queriesXmlFileName;

	public IdentifableDAOImpl(String dbName, DataSource dataSource,
			String sqlInsert, String sqlUpdate, RowMapper<T> rowMapper,
			String queriesXmlFileName) {
		super();
		this.dbName = dbName;
		this.dataSource = dataSource;
		this.sqlInsert = sqlInsert;
		this.sqlUpdate = sqlUpdate;
		this.rowMapper = rowMapper;
		this.queriesXmlFileName = queriesXmlFileName;
	}

	abstract protected List<Object> getSqlParametersFromObject(T identifable);

	public void insert(T identifable) {
		if (identifable == null)
			throw new NullPointerException();

		List<Object> parameters = getSqlParametersFromObject(identifable);
		if (identifable.getId() == Identifable.DEFAULT_ID) {
			identifable.setId(getMaxId() + 1);
			parameters.add(0, identifable.getId());
			getTemplate().update(sqlInsert,
					parameters.toArray(new Object[parameters.size()]));
		} else {
			if (get(identifable.getId()) == null)
				throw new IllegalArgumentException(
						"Невозможно найти объект с таким id");
			parameters.add(parameters.size(), identifable.getId());
			getTemplate().update(sqlUpdate,
					parameters.toArray(new Object[parameters.size()]));
		}
	}

	public JdbcTemplate getTemplate() {

		if (template == null)
			template = new JdbcTemplate(dataSource);
		return template;
	}

	private int getMaxId() {
		return getTemplate().queryForInt("select max(id) from " + dbName);
	}

	public T get(int id) {

		Identifable.checkId(id);
		int count = checkExist(id);
		if (count == 0)
			return null;
		return getTemplate().queryForObject(
				"select * from " + dbName + " where id =?",
				new Object[] { id }, rowMapper);
	}

	protected int checkExist(int id) {
		return getTemplate().queryForInt(
				"select count(*) from " + dbName + " where id=?",
				new Object[] { id });
	}

	public void delete(int id) {
		Identifable.checkId(id);

		getTemplate().update("delete from " + dbName + " where id=?",
				new Object[] { id });
	}

	public List<T> getAll() {
		List<Integer> ids = getTemplate().queryForList(
				"select id from " + dbName, Integer.class);
		List<T> groups = new ArrayList<T>();
		for (Integer id : ids)
			groups.add(get(id));
		return groups;
	}

	public static void initialize(JdbcTemplate template, String updateName,
			List<String> sqlQueryes) {
		String sql = "create table if not exists updates(name text not null, count int not null)";
		template.execute(sql);

		sql = "select max(count) from updates where name = ?";
		int count = template.queryForInt(sql, new Object[] { updateName });

		if (count == 0) {
			sql = "insert into updates(name,count) values (?,?)";
			count = 0;
			template.update(sql, new Object[] { updateName, count });
		}

		for (int i = count; i < sqlQueryes.size(); i++) {
			template.update(sqlQueryes.get(i));
			sql = "update updates set count =? where name = ?";
			template.update(sql, new Object[] { i + 1, updateName });
		}

	}

	public static List<String> getQueriesFromFile(String fileName)
			throws IOException, ParserConfigurationException, SAXException {
		List<String> queries = new ArrayList<String>();

		File file = new File(fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("query");
		for (int i = 0; i < nList.getLength(); i++) {

			Node nNode = nList.item(i);
			queries.add(nNode.getChildNodes().item(0).getNodeValue());
		}
		return queries;
	}

	public void init() throws IOException, ParserConfigurationException,
			SAXException {
		IdentifableDAOImpl.initialize(
				getTemplate(),
				this.getClass().getName(),
				IdentifableDAOImpl.getQueriesFromFile(getClass().getResource(
						queriesXmlFileName).getPath()));
	}

	protected String getDbName() {
		return dbName;
	}

	protected RowMapper<T> getRowMapper() {
		return rowMapper;
	}
}
