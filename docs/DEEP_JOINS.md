# Deep Joins

Deep joins allow you to navigate multi-level entity relationships using dot notation, simplifying complex relationship traversal.

## Overview

Instead of chaining multiple join calls, use dot notation to navigate through relationships in a single statement.

**Before:**
```java
Hefesto.make(User.class)
    .join("posts")
    .join("posts.comments")
    .join("posts.comments.author")
    .get();
```

**After:**
```java
Hefesto.make(User.class)
    .joinDeep("posts.comments.author")
    .get();
```

## Basic Usage

### Simple Deep Join

```java
// Navigate from User → Posts → Comments
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments")
    .get();
```

This creates the necessary join path:
```sql
FROM users u
LEFT JOIN posts p ON u.id = p.user_id
LEFT JOIN comments c ON p.id = c.post_id
```

### Deep Join with Alias

Assign an alias to the final entity in the path:

```java
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments", "postComments")
    .where("postComments.approved", true)
    .get();
```

### Deep Join with Join Type

Specify the join type for the entire path:

```java
// Use INNER join for all steps
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments", JoinOperator.INNER)
    .get();

// Use LEFT join (default)
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments", JoinOperator.LEFT)
    .get();
```

### Deep Join with Alias and Type

Combine alias and join type:

```java
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments.author", "commentAuthors", JoinOperator.LEFT)
    .where("commentAuthors.verified", true)
    .get();
```

## Multiple Deep Joins

Navigate multiple relationship paths from the same root entity:

```java
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments")        // Path 1: User → Posts → Comments
    .joinDeep("posts.tags")            // Path 2: User → Posts → Tags
    .joinDeep("profile.address")       // Path 3: User → Profile → Address
    .get();
```

## Combining with WHERE Conditions

### Filtering on Deep Relationships

```java
// Find users with comments containing specific text
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments", "postComments")
    .where("postComments.text", "%great%", Operator.LIKE)
    .where("postComments.approved", true)
    .get();
```

### Multiple Conditions on Different Levels

```java
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments", "postComments")
    .where("posts.status", "published")          // Filter on posts
    .where("postComments.approved", true)        // Filter on comments
    .where("name", "John%", Operator.LIKE)       // Filter on users
    .get();
```

## Inline Join Conditions

Apply conditions directly on the join clause for better performance:

```java
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments", "postComments", join -> {
        join.where("approved", true);
        join.where("spam", false);
    })
    .get();
```

**Generated SQL:**
```sql
FROM users u
LEFT JOIN posts p ON u.id = p.user_id
LEFT JOIN comments c ON p.id = c.post_id AND c.approved = true AND c.spam = false
```

## Ordering by Deep Properties

```java
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments", "postComments")
    .orderBy("postComments.createdAt", "DESC")
    .limit(10)
    .get();
```

## Selecting from Deep Relationships

```java
// Select fields from deeply nested entities
List<Object[]> results = Hefesto.make(User.class)
    .joinDeep("posts.comments", "postComments")
    .addSelect("id", "userId")
    .addSelect("posts.title", "postTitle")
    .addSelect("postComments.text", "commentText")
    .findFor(Object[].class);
```

## Aggregations with Deep Joins

```java
// Count comments per user
List<Object[]> stats = Hefesto.make(User.class)
    .joinDeep("posts.comments")
    .addSelect("id")
    .addSelect("name")
    .count("posts.comments.id", "commentCount")
    .groupBy("id", "name")
    .findFor(Object[].class);
```

## Common Patterns

### Blog Platform Example

```java
// Find authors with published posts that have approved comments
List<User> authors = Hefesto.make(User.class)
    .joinDeep("posts.comments", "postComments")
    .where("posts.status", "published")
    .where("postComments.approved", true)
    .orderBy("posts.publishedAt", "DESC")
    .get();
```

### E-commerce Example

```java
// Find customers with orders containing specific products
List<Customer> customers = Hefesto.make(Customer.class)
    .joinDeep("orders.items.product", "orderProducts")
    .where("orderProducts.category", "electronics")
    .where("orders.status", "completed")
    .get();
```

### Social Network Example

```java
// Find users with friends who liked specific posts
List<User> users = Hefesto.make(User.class)
    .joinDeep("friends.likedPosts", "friendLikes")
    .where("friendLikes.postId", specificPostId)
    .get();
```

## Benefits

### 1. Reduced Boilerplate

**Before:**
```java
Hefesto.make(User.class)
    .join("posts")
    .join("posts.comments")
    .join("posts.comments.author")
    .join("posts.comments.author.profile")
    .get();
```

**After:**
```java
Hefesto.make(User.class)
    .joinDeep("posts.comments.author.profile")
    .get();
```

### 2. Clearer Intent

The relationship path is immediately visible: `User → Posts → Comments → Author → Profile`

### 3. Easier Maintenance

Changes to intermediate relationships don't require updating multiple join statements.

### 4. Performance

Deep joins with inline conditions generate efficient SQL with conditions on the JOIN clause rather than WHERE clause.

## Comparison: Deep Join vs Multiple Joins

### Multiple Individual Joins

```java
// Explicit control over each join
Hefesto.make(User.class)
    .join("posts", "userPosts", JoinOperator.LEFT)
    .join("userPosts.comments", "postComments", JoinOperator.INNER)
    .join("postComments.author", "commentAuthor", JoinOperator.LEFT)
    .get();
```

**Pros:**
- Fine-grained control over each join type
- Can assign aliases at each level
- Can apply conditions at each step

### Deep Join

```java
// Concise navigation
Hefesto.make(User.class)
    .joinDeep("posts.comments.author", "commentAuthor")
    .get();
```

**Pros:**
- More concise
- Clear relationship path
- Less repetition

**When to use each:**
- **Deep Join**: For straightforward relationship navigation with consistent join types
- **Multiple Joins**: When you need different join types at each level or conditions at intermediate steps

## Limitations

### 1. Same Join Type for Entire Path

Deep joins apply the same join type to all steps. If you need mixed join types, use multiple explicit joins:

```java
// Can't do this with single deep join:
Hefesto.make(User.class)
    .join("posts", JoinOperator.INNER)          // INNER join
    .join("posts.comments", JoinOperator.LEFT)  // LEFT join
    .get();
```

### 2. Intermediate Aliases

You can only alias the final entity in the path. For intermediate aliases, use explicit joins.

### 3. Entity Relationship Requirements

The dot notation follows entity relationships defined in your JPA mappings. Ensure:
- `@OneToMany`, `@ManyToOne`, `@OneToOne`, or `@ManyToMany` annotations exist
- Relationship names match the property names in your entities
- Bidirectional relationships are properly mapped

## Troubleshooting

### "Illegal attempt to dereference" Error

**Problem:** The relationship path doesn't exist in your entity model.

**Solution:**
1. Verify the relationship exists: `User.posts`, `Post.comments`, etc.
2. Check property names match exactly (case-sensitive)
3. Ensure JPA relationships are properly annotated

### Unexpected Results

**Problem:** Getting more or fewer results than expected.

**Solution:**
- Check join type: LEFT vs INNER makes a big difference
- Verify WHERE conditions are on the right entity
- Consider using fetch joins if you need eager loading

### Performance Issues

**Problem:** Query is slow with deep joins.

**Solution:**
1. Use inline join conditions to filter earlier
2. Add indexes on foreign key columns
3. Consider using projections instead of full entity graphs
4. Use INNER joins when possible to reduce result set size

## Best Practices

1. **Use meaningful aliases** for complex queries:
   ```java
   .joinDeep("posts.comments.author", "commentAuthors")
   ```

2. **Apply conditions as early as possible** using inline join configuration:
   ```java
   .joinDeep("posts", join -> join.where("status", "published"))
   ```

3. **Limit depth** to avoid performance issues - rarely need more than 3-4 levels

4. **Use projections** for read-only queries to avoid loading full entity graphs:
   ```java
   .addSelect("posts.comments.text")
   ```

5. **Add database indexes** on foreign key columns used in join paths

## Next Steps

- Learn about [Conditional Groups](CONDITIONAL_GROUPS.md) for complex WHERE clauses
- Explore [Subqueries](SUBQUERIES.md) for advanced filtering
- Check out [Advanced Features](ADVANCED_FEATURES.md) for projections and DTOs

## API Reference

```java
// Deep join methods
joinDeep(String path)
joinDeep(String path, String alias)
joinDeep(String path, JoinOperator type)
joinDeep(String path, String alias, JoinOperator type)
joinDeep(String path, Consumer<JoinBuilder> config)
joinDeep(String path, JoinOperator type, Consumer<JoinBuilder> config)
```

**Parameters:**
- `path`: Dot-separated relationship path (e.g., "posts.comments.author")
- `alias`: Alias for the final entity in the path
- `type`: Join type (LEFT, INNER, RIGHT, FULL)
- `config`: Lambda for inline join conditions
