insert into users(`id`, `name`, `email`, `photo`, `status`)
values (1, 'test', 'test@mail.com', null, 'ACTIVE,BLOCKED'),
       (2, 'petto', 'petto@mail.com', null, 'BLOCKED'),
       (3, 'javi', 'javi@mail.com', 'javi.jpg', 'ACTIVE,PENDING'),
       (4, 'mary', 'mary@mail.com', 'mary.jpg', 'SUSPENDED'),
       (5, 'leo', 'leo@mail.com', null, 'INACTIVE'),
       (6, 'lara', 'lara@mail.com', 'lara.jpg', 'DELETED,SUSPENDED'),
       (7, 'lau', 'lau@mail.com', null, 'PENDING,INACTIVE'),
       (8, 'gabi', 'gabi@mail.com', 'gabi.jpg', 'ACTIVE') ON DUPLICATE KEY UPDATE id = id;

insert into pets(`id`, `name`)
values (1, 'scooby'),
       (2, 'lola'),
       (3, 'el tuerto'),
       (4, 'grillo'),
       (5, 'rex'),
       (6, 'luna') ON DUPLICATE KEY UPDATE id = id;


insert into user_pet(`id`, `user_id`, `pet_id`)
values (1, 1, 2),
       (2, 1, 3),
       (3, 2, 4),
       (4, 3, 5) ON DUPLICATE KEY UPDATE id = id;

insert into addresses(id, `address`, `city`, `country`, `user_id`)
values (1, 'calle del sol', 'madrid', 'spain', 1),
       (2, 'via del sol 3', 'la asuncion', 'venezuela', 1),
       (3, 'via del sol 3', 'la asuncion', 'venezuela', 7),
       (4, 'via del sol 3', 'la asuncion', 'venezuela', 5) ON DUPLICATE KEY UPDATE id = id;