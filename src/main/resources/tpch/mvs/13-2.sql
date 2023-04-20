SELECT c_custkey,
       count(o_orderkey) AS c_count
FROM tpch_customer
LEFT OUTER JOIN tpch_orders ON c_custkey = o_custkey
AND o_comment NOT LIKE '%special%requests%'
GROUP BY c_custkey
