SELECT n_name,
       SUM(l_extendedprice * (1 - l_discount)) AS revenue
FROM tpch_customer,
     tpch_orders,
     tpch_lineitem,
     tpch_supplier,
     tpch_nation,
     tpch_region
WHERE c_custkey = o_custkey
  AND l_orderkey = o_orderkey
  AND l_suppkey = s_suppkey
  AND c_nationkey = s_nationkey
  AND s_nationkey = n_nationkey
  AND n_regionkey = r_regionkey
  AND r_name = 'ASIA'
  AND o_orderdate >= '1994-01-01'
  AND o_orderdate < '1995-01-01'
GROUP BY n_name
ORDER BY revenue DESC
