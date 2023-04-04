package me.alaneuler.calcite.ng.demo.externalize;

import me.alaneuler.calcite.ng.demo.config.GlobalConfig;
import me.alaneuler.calcite.ng.demo.util.CommonTableMain;
import me.alaneuler.calcite.ng.demo.util.RelUtils;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.externalize.RelJsonReader;
import org.apache.calcite.rel.externalize.RelJsonWriter;

public class ExternalizationMain extends CommonTableMain {
  public static void main(String[] args) throws Exception {
    String sql = """
        SELECT pt_user.id, name, age, sum(price)
        FROM pt_user join pt_order ON pt_user.id = pt_order.user_id
        WHERE age >= 20 AND age <= 30
        GROUP BY pt_user.id, name, age
        ORDER BY pt_user.id
        """;

    RelNode relNode = RelUtils.sqlToRel(sql);
    RelJsonWriter writer = new RelJsonWriter();
    relNode.explain(writer);
    String jsonStr = writer.asString();
    // System.out.println(jsonStr);

    CalciteCatalogReader catalogReader = new CalciteCatalogReader(
        GlobalConfig.INSTANCE.getPx().getRootSchema(),
        GlobalConfig.INSTANCE.getPx().getDefaultSchemaPath(),
        GlobalConfig.INSTANCE.getPx().getTypeFactory(),
        GlobalConfig.INSTANCE.getPx().config());
    RelJsonReader reader = new RelJsonReader(relNode.getCluster(),
        catalogReader, GlobalConfig.INSTANCE.getPx().getRootSchema().plus());
    RelNode nNode = reader.read(jsonStr);
    RelJsonWriter writer2 = new RelJsonWriter();
    nNode.explain(writer2);
    String jsonStr2 = writer2.asString();

    RelUtils.dump(relNode);
    RelUtils.dump(nNode);
  }
}
