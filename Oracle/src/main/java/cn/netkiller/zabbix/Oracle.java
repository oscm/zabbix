package cn.netkiller.zabbix;

import java.sql.DriverManager;
import java.net.ProxySelector;

import java.sql.ResultSet;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.*;

/**
 * Hello world!
 *
 */
public class Oracle {

	private String url = null; // 数据库链接地址
	private String username = null;// 用户名,系统默认的账户名
	private String password = null;// 你安装时选设置的密码

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private Connection connection = null;// 创建一个数据库连接

	public Oracle() {
		ProxySelector.setDefault(null);
		try {
			this.logger.setLevel(Level.INFO);

			Formatter formatter = new Formatter() {

				@Override
				public String format(LogRecord logRecord) {
					StringBuilder b = new StringBuilder();
					b.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
					b.append(" ");
					b.append(logRecord.getSourceClassName());
					b.append(" ");
					b.append(logRecord.getSourceMethodName());
					b.append(" ");
					b.append(logRecord.getLevel());
					b.append(" ");
					b.append(logRecord.getMessage());
					b.append(System.getProperty("line.separator"));
					return b.toString();
				}
			};

			FileHandler fileHandler = new FileHandler("monitor.%g.log", true);

			logger.setUseParentHandlers(false);

			fileHandler.setLevel(Level.INFO);
			logger.addHandler(fileHandler);

			fileHandler.setFormatter(formatter);
			logger.setLevel(Level.INFO);

			LogManager lm = LogManager.getLogManager();
			lm.addLogger(logger);

			Class.forName("oracle.jdbc.OracleDriver");// 加载Oracle驱动程序

		} catch (ClassNotFoundException e) {
			logger.info(e.getMessage());
			System.exit(1);
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public void openConfig(String config) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(config));
			this.url = properties.getProperty("jdbc.url");
			this.username = properties.getProperty("jdbc.username");
			this.password = properties.getProperty("jdbc.password");
		} catch (FileNotFoundException e) {
			logger.info(e.getMessage() + " Working Directory = " + config);
			System.exit(1);
		} catch (IOException e) {
			logger.info(e.getMessage());
			System.exit(1);
		}
		if (this.url == null || this.username == null || this.password == null) {
			logger.info("This Propertie file is invalid");
			System.exit(1);
			// throw new Exception("");
		}

	}

	public boolean connect() {
		try {
			this.connection = DriverManager.getConnection(this.url, this.username, this.password);
		} catch (SQLException e) {
			logger.info(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
			return false;
		}
		return true;
	}

	public int test() {
		String sql = "select current_date from dual";
		ResultSet result = this.query(sql);
		int status = 0;
		try {
			if (result.next()) {
				logger.info(String.format("%s %s", result.getDate(1), result.getTime(1)));
				status = 1;
			}
			result.close();
		} catch (SQLException e) {
			logger.info(e.getMessage());
		}
		return status;
	}

	public ResultSet query(String sql) {
		if (!this.connect()) {
			return null;
		}
		ResultSet result = null;
		try {
			Statement statement = connection.createStatement();
			result = statement.executeQuery(sql);
		} catch (SQLException e) {
			logger.info(e.getMessage());
			return null;
		}
		return result;
	}

	public List<String> user() {
		List<String> users = new ArrayList<String>();
		String sql = "select username from all_users";
		try {
			ResultSet result = this.query(sql);
			while (result.next()) {
				users.add(result.getString("username"));
				// System.out.println(result.getString(1));
			}
			result.close();
		} catch (SQLException e) {
			logger.info(e.getMessage());
		}
		// System.out.println(users.toString());
		return users;
	}

	public Map<String, Integer> getSession(String username) {
		String sql = "select username,count(username) as count from v$session where username is not null group by username";
		if (username != null) {
			sql = "select username,count(username) as count from v$session where username = '" + username
					+ "' group by username";
		}
		Map<String, Integer> session = new HashMap<String, Integer>();
		try {
			ResultSet result = this.query(sql);
			while (result.next()) {
				session.put(result.getString("username"), result.getInt("count"));
			}
			result.close();
		} catch (SQLException e) {
			logger.info(e.getMessage());
		}
		return session;
	}

	public void help(String prog) {
		System.out.println(String.format("%s -Dconfig=/path/to/jdbc.properties", prog));
	}

	public static void main(String[] args) {
		try {
			Oracle oracle = new Oracle();

			if (System.getProperty("config") == null) {
				oracle.help(oracle.getClass().getName());
				oracle.openConfig("jdbc.properties");
			} else {
				oracle.openConfig(System.getProperty("config"));
			}

			if (args[0].equals("--query")) {
				System.out.println(oracle.test());
			}
			if (args[0].equals("--user")) {
				for (String username : oracle.user()) {
					System.out.println(username);
				}
			}
			if (args[0].equals("--session")) {
				Map<String, Integer> session = null;
				if (args.length == 2) {
					session = oracle.getSession(args[1]);
				} else {
					session = oracle.getSession(null);
				}

				for (Map.Entry<String, Integer> entry : session.entrySet()) {
					System.out.println(String.format("%s:%d", entry.getKey(), entry.getValue()));
				}

			}
			oracle.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}
