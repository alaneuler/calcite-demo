package me.alaneuler.calcite.ng.demo.entry;

import me.alaneuler.calcite.ng.demo.config.GlobalConfig;
import me.alaneuler.calcite.ng.demo.config.PlannerPool;
import me.alaneuler.calcite.ng.demo.util.RelNodeUtils;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.externalize.RelJsonReader;
import org.apache.calcite.rel.externalize.RelJsonWriter;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.Planner;

import static me.alaneuler.calcite.ng.demo.entry.Main.prepare;

public class ExternalizationMain {
    public static void main(String[] args) throws Exception {
        String sql = """
        SELECT pt_user.id, name, age, sum(price)
        FROM pt_user join pt_order ON pt_user.id = pt_order.user_id
        WHERE age >= 20 AND age <= 30
        GROUP BY pt_user.id, name, age
        ORDER BY pt_user.id
        """;
        prepare();

        Planner planner = PlannerPool.getPlanner();
        SqlNode sqlNode = planner.parse(sql);
        sqlNode = planner.validate(sqlNode);
        RelNode relNode = planner.rel(sqlNode).project();

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
                catalogReader,
                GlobalConfig.INSTANCE.getPx().getRootSchema().plus());
        RelNode nNode = reader.read(jsonStr);
        RelJsonWriter writer2 = new RelJsonWriter();
        nNode.explain(writer2);
        String jsonStr2 = writer2.asString();

        RelNodeUtils.dump(relNode);
        RelNodeUtils.dump(nNode);
    }
}
