SELECT c_count,
       count(*) AS custdist
FROM (
  SELECT c_custkey,
         count(o_orderkey) AS c_count
  FROM tpch_customer
  LEFT OUTER JOIN tpch_orders ON c_custkey = o_custkey
  AND o_comment NOT LIKE '%special%requests%'
  GROUP BY c_custkey) AS c_orders
GROUP BY c_count