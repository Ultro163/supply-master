INSERT INTO prices (id, supplier_id, product_id, price_per_kg, start_date, end_date)
SELECT gen_random_uuid(),
       s.id,
       p.id,
       ROUND((50 + RANDOM() * 50)::numeric, 2),
       '2024-01-01',
       '2024-03-31'
FROM suppliers s
         CROSS JOIN products p;