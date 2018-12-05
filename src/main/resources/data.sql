-- in this demo username and passwords are identical

insert into users (username, password, enabled) 
values ('user', '$2a$10$opaTlVRWtftWBrIKFtjq2OWm58NyTfNnhtXTzLGqj5FNT9Drl2TTq', true);
insert into authorities (username, authority) 
values ('user', 'ROLE_USER');

insert into users (username, password, enabled) 
values ('user2', '$2a$10$qFOWTDBUgqBpLQ37H7YX6.JiDCrUihY3RmNdzd//pCyPk9Q1RJboi', true);
insert into authorities (username, authority) 
values ('user2', 'ROLE_USER');

insert into users (username, password, enabled) 
values ('admin', '$2a$10$ih2w5IGJQ0PrNJTQuSiuDuHIHaIGNmhTZV42ugf8ehP5y1WauXIcC', true);
insert into authorities (username, authority) 
values ('admin', 'ROLE_ADMIN');
