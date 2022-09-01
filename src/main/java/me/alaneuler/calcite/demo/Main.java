package me.alaneuler.calcite.demo;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableInterpretable;
import org.apache.calcite.adapter.enumerable.EnumerableRel;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.plan.Contexts;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCostImpl;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.prepare.Prepare;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.runtime.Bindable;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;
import org.apache.calcite.tools.Program;
import org.apache.calcite.tools.Programs;
import org.apache.calcite.tools.RuleSet;
import org.apache.calcite.tools.RuleSets;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        String sql = "SELECT u.id, name, age, sum(price) " +
                "FROM users AS u join orders AS o ON u.id = o.user_id " +
                "WHERE age >= 20 AND age <= 30 " +
                "GROUP BY u.id, name, age ORDER BY u.id";
        // 创建 SqlParser，用于解析 SQL 字符串
        SqlParser.Config parserConfig = SqlParser.config().withQuotedCasing(Casing.UNCHANGED)
                .withUnquotedCasing(Casing.UNCHANGED).withCaseSensitive(true);
        SqlParser parser = SqlParser.create(sql, parserConfig);
        // 解析 SQL 字符串，生成 SqlNode 树
        SqlNode sqlNode = parser.parseStmt();

        // 创建 Schema，一个 Schema 中包含多个表
        // Calcite 中的 Schema 类似 RDBMS 中的 Database
        SimpleTable userTable = SimpleTable.newBuilder("users")
                .addField("id", SqlTypeName.VARCHAR).addField("name", SqlTypeName.VARCHAR)
                .addField("age", SqlTypeName.INTEGER).withFilePath("table/users.csv")
                .withRowCount(10).build();
        SimpleTable orderTable = SimpleTable.newBuilder("orders")
                .addField("id", SqlTypeName.VARCHAR).addField("user_id", SqlTypeName.VARCHAR)
                .addField("goods", SqlTypeName.VARCHAR).addField("price", SqlTypeName.DECIMAL)
                .withFilePath("table/orders.csv").withRowCount(10).build();
        SimpleSchema schema = SimpleSchema.newBuilder("s")
                .addTable(userTable).addTable(orderTable).build();
        CalciteSchema rootSchema = CalciteSchema.createRootSchema(false, false);
        rootSchema.add(schema.getSchemaName(), schema);

        RelDataTypeFactory typeFactory = new JavaTypeFactoryImpl();
        CalciteConnectionConfig connectionConfig = CalciteConnectionConfig.DEFAULT
                .set(CalciteConnectionProperty.CASE_SENSITIVE, Boolean.TRUE.toString())
                .set(CalciteConnectionProperty.UNQUOTED_CASING, Casing.UNCHANGED.toString())
                .set(CalciteConnectionProperty.QUOTED_CASING, Casing.UNCHANGED.toString());

        // 创建 CatalogReader，用于指示如何读取 Schema 信息
        Prepare.CatalogReader catalogReader = new CalciteCatalogReader(rootSchema,
                Collections.singletonList(schema.getSchemaName()), typeFactory, connectionConfig);
        // 创建 SqlValidator，用于执行 SQL 验证
        SqlValidator.Config validatorConfig = SqlValidator.Config.DEFAULT
                .withLenientOperatorLookup(connectionConfig.lenientOperatorLookup())
                .withSqlConformance(connectionConfig.conformance())
                .withDefaultNullCollation(connectionConfig.defaultNullCollation())
                .withIdentifierExpansion(true);
        SqlValidator validator = SqlValidatorUtil.newValidator(
                SqlStdOperatorTable.instance(), catalogReader, typeFactory, validatorConfig);
        // 执行 SQL 验证
        SqlNode validateSqlNode = validator.validate(sqlNode);

        // 创建 VolcanoPlanner，VolcanoPlanner 在后面的优化中还需要用到
        VolcanoPlanner planner = new VolcanoPlanner(RelOptCostImpl.FACTORY, Contexts.of(connectionConfig));
        planner.addRelTraitDef(ConventionTraitDef.INSTANCE);
        // 创建 SqlToRelConverter
        RelOptCluster cluster = RelOptCluster.create(planner, new RexBuilder(typeFactory));
        SqlToRelConverter.Config converterConfig = SqlToRelConverter.config()
                .withTrimUnusedFields(true).withExpand(false);
        SqlToRelConverter converter = new SqlToRelConverter(null, validator, catalogReader,
                cluster, StandardConvertletTable.INSTANCE, converterConfig);
        // 将 SqlNode 树转化为 RelNode 树
        RelNode relNode =  converter.convertQuery(validateSqlNode, false, true).rel;

        // 优化规则
        RuleSet rules = RuleSets.ofList(CoreRules.FILTER_TO_CALC, CoreRules.PROJECT_TO_CALC,
                CoreRules.FILTER_CALC_MERGE, CoreRules.PROJECT_CALC_MERGE,
                CoreRules.FILTER_INTO_JOIN,// 过滤谓词下推到Join之前
                EnumerableRules.ENUMERABLE_TABLE_SCAN_RULE,
                EnumerableRules.ENUMERABLE_PROJECT_TO_CALC_RULE, EnumerableRules.ENUMERABLE_FILTER_TO_CALC_RULE,
                EnumerableRules.ENUMERABLE_JOIN_RULE, EnumerableRules.ENUMERABLE_SORT_RULE,
                EnumerableRules.ENUMERABLE_CALC_RULE, EnumerableRules.ENUMERABLE_AGGREGATE_RULE);
        Program program = Programs.of(RuleSets.ofList(rules));
        RelNode optimizerRelTree = program.run(planner, relNode,
                relNode.getTraitSet().plus(EnumerableConvention.INSTANCE),
                Collections.emptyList(), Collections.emptyList());

        EnumerableRel enumerable = (EnumerableRel) optimizerRelTree;
        EnumerableRel.Prefer prefer = EnumerableRel.Prefer.ARRAY;
        Bindable bindable = EnumerableInterpretable.toBindable(new LinkedHashMap<>(), null, enumerable, prefer);
        Enumerable bind = bindable.bind(new SimpleDataContext(rootSchema.plus()));
        Enumerator enumerator = bind.enumerator();
        while (enumerator.moveNext()) {
            Object current = enumerator.current();
            Object[] values = (Object[]) current;
            System.out.println(String.format("%s,%s,%s,%s", values[0], values[1], values[2],
                    ((BigDecimal) values[3]).setScale(2, RoundingMode.HALF_EVEN)));
        }
    }
}
