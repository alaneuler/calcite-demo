SELECT c_count, COUNT(*) AS custdist
FROM mv1
GROUP BY c_count
ORDER BY COUNT(*) IS NULL DESC, 2 DESC, c_count IS NULL DESC, c_count DESC
