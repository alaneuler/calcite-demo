package me.alaneuler.calcite.ng.demo.materialize.simple;

import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.MaterializeUtils;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import me.alaneuler.calcite.ng.demo.util.TableUtils;
import me.alaneuler.calcite.ng.demo.util.VolcanoUtils;
import org.apache.calcite.plan.RelOptMaterialization;
import org.apache.calcite.plan.RelOptRules;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;

public class SPJGMain extends CommonTableMain {
  public static void main(String[] args) {
    createTables();
    String sql = """
        select name, sum(price)
        from (
          select name, price
          from orders, customers
          where orders.customer_id = customers.id
          and price > 10
        )
        group by name
        """;
    String mvSql = """
        select name, price
        from orders, customers
        where orders.customer_id = customers.id
        and price > 10
        """;

    RelNode rel = RelUtils.sqlToRel(sql);
    RelUtils.dump(rel);

    RelOptMaterialization materialization = MaterializeUtils
        .createMaterialization("mv", mvSql, rel.getCluster(), false);
    VolcanoPlanner planner = VolcanoUtils.extractVolcanoPlanner(rel);
    planner.setTopDownOpt(true);
    planner.setNoneConventionHasInfiniteCost(false);
    RelOptRules.MATERIALIZATION_RULES.forEach(planner::addRule);
    planner.addMaterialization(materialization);

    planner.setRoot(rel);
    RelNode after = planner.findBestExp();
    RelUtils.dump(after);
  }

  private static void createTables() {
    TableUtils.createTable("""
        CREATE TABLE orders (
          id INTEGER,
          product VARCHAR,
          price DOUBLE,
          customer_id INTEGER
        )
        """);
    TableUtils.createTable("""
        CREATE TABLE customers (
          id INTEGER,
          name VARCHAR
        )
        """);

    TableUtils.createTable("""
        CREATE TABLE mv (
          name VARCHAR,
          price DOUBLE
        )
        """);
  }
}
