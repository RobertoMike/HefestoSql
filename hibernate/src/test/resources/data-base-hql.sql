insert into users(`id`, `name`, `email`, `photo`, `status`, `active`, `verified`, `role`, `level`, `phone`, `deleted_at`, `created_at`)
values (1, 'test', 'test@mail.com', null, 'ACTIVE,BLOCKED', true, true, 'USER', 1, '111-1111', null, '2024-01-01 10:00:00'),
       (2, 'petto', 'petto@mail.com', null, 'BLOCKED', true, false, 'USER', 2, '222-2222', null, '2024-01-02 10:00:00'),
       (3, 'javi', 'javi@mail.com', 'javi.jpg', 'ACTIVE,PENDING', true, true, 'ADMIN', 5, '333-3333', null, '2024-01-03 10:00:00'),
       (4, 'mary', 'mary@mail.com', 'mary.jpg', 'SUSPENDED', false, true, 'USER', 3, '444-4444', null, '2024-01-04 10:00:00'),
       (5, 'leo', 'leo@mail.com', null, 'INACTIVE', false, false, 'USER', 1, null, null, '2024-01-05 10:00:00'),
       (6, 'lara', 'lara@mail.com', 'lara.jpg', 'DELETED,SUSPENDED', false, true, 'MODERATOR', 10, '666-6666', '2024-12-01 10:00:00', '2024-01-06 10:00:00'),
       (7, 'lau', 'lau@mail.com', null, 'PENDING,INACTIVE', true, false, 'USER', 2, null, null, '2024-01-07 10:00:00'),
       (8, 'gabi', 'gabi@mail.com', 'gabi.jpg', 'ACTIVE', true, true, 'ADMIN', 15, '888-8888', null, '2024-01-08 10:00:00') ON DUPLICATE KEY UPDATE id = id;

insert into pets(`id`, `name`)
values (1, 'scooby'),
       (2, 'lola'),
       (3, 'el tuerto'),
       (4, 'grillo'),
       (5, 'rex'),
       (6, 'luna') ON DUPLICATE KEY UPDATE id = id;


insert into user_pet(`id`, `user_id`, `pet_id`, `active`, `type`, `created_at`, `deleted_at`)
values (1, 1, 2, true, 'DOG', '2024-01-01 10:00:00', null),
       (2, 1, 3, true, 'CAT', '2024-01-02 10:00:00', null),
       (3, 2, 4, false, 'DOG', '2024-01-03 10:00:00', null),
       (4, 3, 5, true, 'DOG', '2024-01-04 10:00:00', null) ON DUPLICATE KEY UPDATE id = id;

insert into addresses(id, `address`, `city`, `country`, `user_id`)
values (1, 'calle del sol', 'madrid', 'spain', 1),
       (2, 'via del sol 3', 'la asuncion', 'venezuela', 1),
       (3, 'via del sol 3', 'la asuncion', 'venezuela', 7),
       (4, 'via del sol 3', 'la asuncion', 'venezuela', 5) ON DUPLICATE KEY UPDATE id = id;