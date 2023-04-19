SELECT *
FROM tpch_region
LEFT JOIN tpch_nation ON r_regionkey = n_regionkey
LEFT JOIN tpch_supplier ON n_nationkey = s_nationkey
LEFT JOIN tpch_partsupp ON s_suppkey = ps_suppkey
LEFT JOIN tpch_part ON ps_partkey = p_partkey
LEFT JOIN tpch_lineitem ON ps_suppkey = l_suppkey
AND ps_partkey = l_partkey
