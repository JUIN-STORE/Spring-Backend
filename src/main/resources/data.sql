-- 데이터베이스 만들기
CREATE DATABASE IF NOT EXISTS jz;

-- category 데이터 넣기
INSERT IGNORE INTO jz.category (category_id, category_name, depth, parent_id) VALUES (1, 'BOOK', 1, null);
INSERT IGNORE INTO jz.category (category_id, category_name, depth, parent_id) VALUES (2, 'CLOTHES', 1, null);
INSERT IGNORE INTO jz.category (category_id, category_name, depth, parent_id) VALUES (3, 'FOOD', 1, null);
INSERT IGNORE INTO jz.category (category_id, category_name, depth, parent_id) VALUES (4, 'IT-BOOK', 2, 1);
INSERT IGNORE INTO jz.category (category_id, category_name, depth, parent_id) VALUES (5, 'IT-CLOTHES', 2, 2);
INSERT IGNORE INTO jz.category (category_id, category_name, depth, parent_id) VALUES (6, 'IT-FOOD', 2, 3);
INSERT IGNORE INTO jz.category (category_id, category_name, depth, parent_id) VALUES (7, 'JPA-BOOK', 3, 4);
INSERT IGNORE INTO jz.category (category_id, category_name, depth, parent_id) VALUES (8, 'SPRING-BOOK', 3, 4);
INSERT IGNORE INTO jz.category (category_id, category_name, depth, parent_id) VALUES (9, 'FOOD-BOOK', 3, 4);
