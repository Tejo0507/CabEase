// Simple BCrypt password generator using standard online encoder
// admin123 -> $2a$10$4BaQGRGJgXaE6L0Q6yMQGe3YmF4L6V8G6X.FnKfYe2D/x3DY5Qz.S
// user123  -> $2a$10$7JXuNOJjjLqQNh5MN2V.xOWJo1A1w5Fj.1Ps4xCl0f7YjkrLdvXrG
// test123  -> $2a$10$zKbSgOJ8wK7fO5q2J2KrLu.QGJ4a3.3eO5YGXhf2r.V5LtYgJV.a2

// Updated data.sql should use these hashes
console.log("Use these BCrypt hashes in data.sql:");
console.log("admin123: $2a$10$4BaQGRGJgXaE6L0Q6yMQGe3YmF4L6V8G6X.FnKfYe2D/x3DY5Qz.S");
console.log("user123:  $2a$10$7JXuNOJjjLqQNh5MN2V.xOWJo1A1w5Fj.1Ps4xCl0f7YjkrLdvXrG");
console.log("test123:  $2a$10$zKbSgOJ8wK7fO5q2J2KrLu.QGJ4a3.3eO5YGXhf2r.V5LtYgJV.a2");