SELECT c_custkey,
       COUNT(o_orderkey) AS c_count
FROM tpch_customer,
     tpch_orders
WHERE c_custkey = o_custkey
  AND o_comment NOT LIKE '%special%requests%'
GROUP BY c_custkey
