SELECT r_name,
       p_type,
       SUM(l_extendedprice * (1 - l_discount)) AS revenue
FROM tpch_region
LEFT JOIN tpch_nation ON r_regionkey = n_regionkey
LEFT JOIN tpch_supplier ON n_nationkey = s_nationkey
LEFT JOIN tpch_partsupp ON s_suppkey = ps_suppkey
LEFT JOIN tpch_part ON ps_partkey = p_partkey
LEFT JOIN tpch_lineitem ON ps_suppkey = l_suppkey AND ps_partkey = l_partkey
WHERE l_shipdate >= '1995-01-01'
  AND l_shipdate < '1996-01-01'
GROUP BY tpch_region.r_name,
         tpch_part.p_type HAVING SUM(l_extendedprice * (1 - l_discount)) > 10000
