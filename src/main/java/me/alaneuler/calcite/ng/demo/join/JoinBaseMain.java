package me.alaneuler.calcite.ng.demo.join;

import me.alaneuler.calcite.ng.demo.util.TableUtils;

public abstract class JoinBaseMain {
  static {
    createTables();
  }

  private static void createTables() {
    TableUtils.createTable(
        """
        CREATE TABLE customers (
          id INTEGER,
          name VARCHAR,
          age INTEGER,
          PRIMARY KEY (id)
        )
        """);

    TableUtils.createTable(
        """
        CREATE TABLE products (
          id INTEGER,
          name VARCHAR,
          price DOUBLE,
          weight DOUBLE,
          PRIMARY KEY (id)
        )
        """);

    TableUtils.createTable(
        """
       CREATE TABLE orders (
         id INTEGER,
         product_id INTEGER,
         customer_id INTEGER,
         dt DATE,
         PRIMARY KEY (id),
         FOREIGN KEY (product_id) REFERENCES products(id),
         FOREIGN KEY (customer_id) REFERENCES customers(id)
       )
       """);
  }
}
