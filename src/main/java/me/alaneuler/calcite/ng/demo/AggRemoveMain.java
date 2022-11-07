package me.alaneuler.calcite.ng.demo;

import me.alaneuler.calcite.ng.demo.config.PlannerPool;
import me.alaneuler.calcite.ng.demo.config.SchemaConfig;
import me.alaneuler.calcite.ng.demo.util.SqlUtils;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.Planner;

public class AggRemoveMain {
  public static void main(String[] args) throws Exception {
    String sql = """
        SELECT `alias_c1`, `alias_c2`, sum(`count_lsh_5h_1`) AS `alias_c3`
        FROM (
            (SELECT `vt_rt_lsb_khzh` AS `alias_c1`, `vt_rt_lsb_kmhao` AS `alias_c2`, COUNT(`vt_rt_lsb_lsh`) AS `count_lsh_5h_1`
            FROM (
                SELECT `pt_lsb`.`lsh` AS `vt_rt_lsb_lsh`, `pt_lsb`.`kmhao` AS `vt_rt_lsb_kmhao`, `pt_lsb`.`khzh` AS `vt_rt_lsb_khzh`
                FROM `pt_lsb`
                    LEFT JOIN `mv_m_dim_table`
                    ON `pt_lsb`.`lsh` = `mv_m_dim_table`.`lsh`
            ) `t1`
            GROUP BY `vt_rt_lsb_khzh`, `vt_rt_lsb_kmhao`
            ORDER BY COUNT(`vt_rt_lsb_lsh`) DESC
            LIMIT 100)
            UNION ALL
            (SELECT `vt_rt_lsb_khzh` AS `alias_c1`, `vt_rt_lsb_kmhao` AS `alias_c2`, COUNT(`vt_rt_lsb_lsh`) AS `count_lsh_5h_1`
            FROM (
                SELECT `pt_lsb`.`lsh` AS `vt_rt_lsb_lsh`, `pt_lsb`.`kmhao` AS `vt_rt_lsb_kmhao`, `pt_lsb`.`khzh` AS `vt_rt_lsb_khzh`
                FROM `pt_lsb`
                    LEFT JOIN `mv_m_dim_table`
                    ON `pt_lsb`.`lsh` = `mv_m_dim_table`.`lsh`
            ) `t1`
            GROUP BY `vt_rt_lsb_khzh`, `vt_rt_lsb_kmhao`
            ORDER BY COUNT(`vt_rt_lsb_lsh`) DESC
            LIMIT 100)
            UNION ALL
            (SELECT `vt_rt_lsb_khzh` AS `alias_c1`, `vt_rt_lsb_kmhao` AS `alias_c2`, COUNT(`vt_rt_lsb_lsh`) AS `count_lsh_5h_1`
            FROM (
                SELECT `pt_lsb`.`lsh` AS `vt_rt_lsb_lsh`, `pt_lsb`.`kmhao` AS `vt_rt_lsb_kmhao`, `pt_lsb`.`khzh` AS `vt_rt_lsb_khzh`
                FROM `pt_lsb`
                    LEFT JOIN `mv_m_dim_table`
                    ON `pt_lsb`.`lsh` = `mv_m_dim_table`.`lsh`
            ) `t1`
            GROUP BY `vt_rt_lsb_khzh`, `vt_rt_lsb_kmhao`
            ORDER BY COUNT(`vt_rt_lsb_lsh`) DESC
            LIMIT 100)
        )
        GROUP BY `alias_c1`, `alias_c2`
        ORDER BY `alias_c3` DESC
        LIMIT 100
        """;
    prepare();

    Planner planner = PlannerPool.getPlanner();
    SqlNode sqlNode = planner.parse(sql);
    sqlNode = planner.validate(sqlNode);
    RelNode relNode = planner.rel(sqlNode).project();

    RelOptPlanner hepPlanner = hepPlanner();
    hepPlanner.setRoot(relNode);
    relNode = hepPlanner.findBestExp();
    System.out.println(SqlUtils.toSqlString(relNode));
  }

  private static void prepare() {
    SchemaConfig.addTable("CREATE TABLE IF NOT EXISTS `pt_lsb` (lsh VARCHAR NOT NULL, zxh INTEGER NOT NULL, jyrq VARCHAR NOT NULL, jdgx VARCHAR NOT NULL, amt BIGINT NOT NULL, kmhao VARCHAR NOT NULL, zhao VARCHAR NOT NULL, khzh VARCHAR NOT NULL, jym VARCHAR NOT NULL, khlx VARCHAR NOT NULL, zhye BIGINT NOT NULL, event_date TIMESTAMP (3) NOT NULL)");
    SchemaConfig.addTable("CREATE TABLE IF NOT EXISTS `pt_rt_lsb` (lsh VARCHAR NOT NULL, zxh INTEGER NOT NULL, jyrq VARCHAR NOT NULL, jdgx VARCHAR NOT NULL, amt BIGINT NOT NULL, kmhao VARCHAR NOT NULL, zhao VARCHAR NOT NULL, khzh VARCHAR NOT NULL, jym VARCHAR NOT NULL, khlx VARCHAR NOT NULL, zhye BIGINT NOT NULL, event_date TIMESTAMP (3) NOT NULL)");
    SchemaConfig.addTable("CREATE TABLE IF NOT EXISTS `mv_m_dim_table` (u_pk VARCHAR NOT NULL, dfhm VARCHAR NOT NULL, dfzh VARCHAR NOT NULL, dfhum VARCHAR NOT NULL, dfzjh VARCHAR NOT NULL, lsh VARCHAR NOT NULL, zxh INTEGER NOT NULL, jyrq VARCHAR NOT NULL, event_date TIMESTAMP (3) NOT NULL)");
    SchemaConfig.addTable("CREATE TABLE IF NOT EXISTS `mv_m_rt_dim_table` (u_pk VARCHAR NOT NULL, dfhm VARCHAR NOT NULL, dfzh VARCHAR NOT NULL, dfhum VARCHAR NOT NULL, dfzjh VARCHAR NOT NULL, lsh VARCHAR NOT NULL, zxh INTEGER NOT NULL, jyrq VARCHAR NOT NULL, event_date TIMESTAMP (3) NOT NULL)");
    SchemaConfig.addTable("CREATE TABLE IF NOT EXISTS `pt_user_info` (khzh VARCHAR NOT NULL, customer_name VARCHAR NOT NULL, province VARCHAR NOT NULL, city VARCHAR NOT NULL, age INTEGER NOT NULL, job VARCHAR NOT NULL, xingb VARCHAR NOT NULL, xingz VARCHAR NOT NULL)");
    SchemaConfig.addTable("CREATE TABLE IF NOT EXISTS `pt_lsb` (lsh VARCHAR NOT NULL, zxh INTEGER NOT NULL, jyrq VARCHAR NOT NULL, jdgx VARCHAR NOT NULL, amt BIGINT NOT NULL, kmhao VARCHAR NOT NULL, zhao VARCHAR NOT NULL, khzh VARCHAR NOT NULL, jym VARCHAR NOT NULL, khlx VARCHAR NOT NULL, zhye BIGINT NOT NULL, event_date TIMESTAMP (3) NOT NULL)");
    SchemaConfig.addTable("CREATE TABLE IF NOT EXISTS `pt_rt_lsb` (lsh VARCHAR NOT NULL, zxh INTEGER NOT NULL, jyrq VARCHAR NOT NULL, jdgx VARCHAR NOT NULL, amt BIGINT NOT NULL, kmhao VARCHAR NOT NULL, zhao VARCHAR NOT NULL, khzh VARCHAR NOT NULL, jym VARCHAR NOT NULL, khlx VARCHAR NOT NULL, zhye BIGINT NOT NULL, event_date TIMESTAMP (3) NOT NULL)");
    SchemaConfig.addTable("CREATE TABLE IF NOT EXISTS `mv_m_dim_table` (u_pk VARCHAR NOT NULL, dfhm VARCHAR NOT NULL, dfzh VARCHAR NOT NULL, dfhum VARCHAR NOT NULL, dfzjh VARCHAR NOT NULL, lsh VARCHAR NOT NULL, zxh INTEGER NOT NULL, jyrq VARCHAR NOT NULL, event_date TIMESTAMP (3) NOT NULL)");
    SchemaConfig.addTable("CREATE TABLE IF NOT EXISTS `mv_m_rt_dim_table` (u_pk VARCHAR NOT NULL, dfhm VARCHAR NOT NULL, dfzh VARCHAR NOT NULL, dfhum VARCHAR NOT NULL, dfzjh VARCHAR NOT NULL, lsh VARCHAR NOT NULL, zxh INTEGER NOT NULL, jyrq VARCHAR NOT NULL, event_date TIMESTAMP (3) NOT NULL)");
    SchemaConfig.addTable("CREATE TABLE IF NOT EXISTS `pt_user_info` (khzh VARCHAR NOT NULL, customer_name VARCHAR NOT NULL, province VARCHAR NOT NULL, city VARCHAR NOT NULL, age INTEGER NOT NULL, job VARCHAR NOT NULL, xingb VARCHAR NOT NULL, xingz VARCHAR NOT NULL)");
  }

  private static RelOptPlanner hepPlanner() {
    HepProgramBuilder builder = new HepProgramBuilder();
    builder.addRuleInstance(CoreRules.AGGREGATE_UNION_TRANSPOSE);
    builder.addRuleInstance(CoreRules.AGGREGATE_REMOVE);
    return new HepPlanner(builder.build());
  }
}
