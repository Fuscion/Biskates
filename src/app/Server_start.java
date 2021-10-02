package app;
/*
 * Simple Spark web application
 *
 */

import static spark.Spark.port;
import static spark.Spark.staticFiles;

import mySqlUtils.MySQLConn;

public class Server_start {

	public static void main(String[] args) {

		// Configure Spark
		port(8000);
		staticFiles.externalLocation("src/resources");

		MySQLConn conn = MySQLConn.getSharedInstance();
		conn.init("localhost", "3306", "root", "12345678", "biskates");

		@SuppressWarnings("unused")
		SparkRest server = new SparkRest(conn);

	}
}