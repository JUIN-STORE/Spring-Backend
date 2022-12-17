-- 데이터베이스 만들기
create database jz;

-- category 데이터 넣기
INSERT INTO jz.category (category_id, category_name, depth, parent_id) VALUES (1, 'book', 1, null);
INSERT INTO jz.category (category_id, category_name, depth, parent_id) VALUES (2, 'CLOTHES', 1, null);
INSERT INTO jz.category (category_id, category_name, depth, parent_id) VALUES (3, 'FOOD', 1, null);
