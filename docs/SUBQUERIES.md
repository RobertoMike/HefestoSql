# Subqueries

Subqueries allow you to use the result of one query as part of another query's conditions, enabling powerful filtering and correlation logic.

## Overview

HefestoSQL provides lambda-based subquery methods for clean, inline subquery construction.

**Before (Pre-built subquery):**
```java
var subQuery = Hefesto.make(Post.class)
    .addSelect("userId")
    .where("published", true)
    .setCustomResultForSubQuery(Long.class);

List<User> users = Hefesto.make(User.class)
    .whereIn("id", subQuery)
    .get();
```

**After (Lambda subquery):**
```java
List<User> users = Hefesto.make(User.class)
    .whereIn("id", Post.class, sub -> {
        sub.addSelect("userId");
        sub.where("published", true);
    })
    .get();
```

## WHERE IN Subqueries

Find records where a field matches any value from a subquery.

### Basic WHERE IN

```java
// Find users who have published posts
List<User> users = Hefesto.make(User.class)
    .whereIn("id", Post.class, sub -> {
        sub.addSelect("userId");
        sub.where("published", true);
    })
    .get();
```

**Generated SQL:**
```sql
SELECT * FROM users
WHERE id IN (
    SELECT user_id FROM posts WHERE published = true
)
```

### WHERE IN with Multiple Conditions

```java
// Find users with popular posts (>1000 views)
List<User> users = Hefesto.make(User.class)
    .whereIn("id", Post.class, sub -> {
        sub.addSelect("userId");
        sub.where("views", 1000, Operator.GREATER);
        sub.where("status", "approved");
    })
    .get();
```

### WHERE IN with Joins

```java
// Find users with comments on published posts
List<User> users = Hefesto.make(User.class)
    .whereIn("id", Comment.class, sub -> {
        sub.addSelect("userId");
        sub.join("post");
        sub.where("post.published", true);
    })
    .get();
```

## WHERE NOT IN Subqueries

Find records that don't match any value from a subquery.

### Basic WHERE NOT IN

```java
// Find users without any posts
List<User> users = Hefesto.make(User.class)
    .whereNotIn("id", Post.class, sub -> {
        sub.addSelect("userId");
    })
    .get();
```

**Generated SQL:**
```sql
SELECT * FROM users
WHERE id NOT IN (
    SELECT user_id FROM posts
)
```

### Complex NOT IN

```java
// Find users who haven't made spam comments
List<User> users = Hefesto.make(User.class)
    .whereNotIn("id", Comment.class, sub -> {
        sub.addSelect("userId");
        sub.where("flaggedAsSpam", true);
        sub.where("deletedAt", null, Operator.IS_NULL);
    })
    .get();
```

## WHERE EXISTS Subqueries

Check if a subquery returns any results. Often used for correlated subqueries.

### Basic EXISTS

```java
// Find users who have at least one post
List<User> users = Hefesto.make(User.class)
    .whereExists(Post.class, sub -> {
        sub.getBuilder().whereField("userId", "id");
    })
    .get();
```

**Generated SQL:**
```sql
SELECT * FROM users u
WHERE EXISTS (
    SELECT 1 FROM posts p WHERE p.user_id = u.id
)
```

### EXISTS with Conditions

```java
// Find users with published posts
List<User> users = Hefesto.make(User.class)
    .whereExists(Post.class, sub -> {
        sub.getBuilder().whereField("userId", "id");
        sub.where("published", true);
        sub.where("deletedAt", null, Operator.IS_NULL);
    })
    .get();
```

### EXISTS with Complex Logic

```java
// Find users with recent activity (posts or comments in last 30 days)
LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

List<User> activeUsers = Hefesto.make(User.class)
    .whereAny(group -> {
        // Has recent posts
        group.whereExists(Post.class, sub -> {
            sub.getBuilder().whereField("userId", "id");
            sub.where("createdAt", thirtyDaysAgo, Operator.GREATER);
        });
        
        // Has recent comments
        group.whereExists(Comment.class, sub -> {
            sub.getBuilder().whereField("userId", "id");
            sub.where("createdAt", thirtyDaysAgo, Operator.GREATER);
        });
    })
    .get();
```

## WHERE NOT EXISTS Subqueries

Check that a subquery returns no results.

### Basic NOT EXISTS

```java
// Find users without any posts
List<User> users = Hefesto.make(User.class)
    .whereNotExists(Post.class, sub -> {
        sub.getBuilder().whereField("userId", "id");
    })
    .get();
```

**Generated SQL:**
```sql
SELECT * FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM posts p WHERE p.user_id = u.id
)
```

### NOT EXISTS with Conditions

```java
// Find users without pending moderation items
List<User> users = Hefesto.make(User.class)
    .whereNotExists(Post.class, sub -> {
        sub.getBuilder().whereField("userId", "id");
        sub.where("moderationStatus", "pending");
    })
    .get();
```

## OR Connection Methods

Connect subqueries with OR instead of AND.

### orWhereIn

```java
List<User> users = Hefesto.make(User.class)
    .where("role", "admin")
    .orWhereIn("id", Post.class, sub -> {
        sub.addSelect("userId");
        sub.where("featured", true);
    })
    .get();
```

**Generated SQL:**
```sql
WHERE role = 'admin' OR id IN (SELECT user_id FROM posts WHERE featured = true)
```

### orWhereNotIn

```java
List<User> users = Hefesto.make(User.class)
    .where("verified", true)
    .orWhereNotIn("id", Suspension.class, sub -> {
        sub.addSelect("userId");
        sub.where("active", true);
    })
    .get();
```

### orWhereExists / orWhereNotExists

```java
List<User> users = Hefesto.make(User.class)
    .where("role", "moderator")
    .orWhereExists(Post.class, sub -> {
        sub.getBuilder().whereField("userId", "id");
        sub.where("views", 10000, Operator.GREATER);
    })
    .get();
```

## Correlated Subqueries

Correlated subqueries reference columns from the outer query.

### Using whereField

```java
// Find users whose latest post is recent
LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);

List<User> users = Hefesto.make(User.class)
    .whereExists(Post.class, sub -> {
        // Correlate: posts.user_id = users.id
        sub.getBuilder().whereField("userId", "id");
        sub.where("createdAt", lastWeek, Operator.GREATER);
    })
    .get();
```

### Multiple Correlations

```java
// Find users with posts that have comments
List<User> users = Hefesto.make(User.class)
    .whereExists(Post.class, sub -> {
        sub.getBuilder().whereField("userId", "id");  // posts.user_id = users.id
        sub.whereExists(Comment.class, commentSub -> {
            commentSub.getBuilder().whereField("postId", "posts.id");  // comments.post_id = posts.id
        });
    })
    .get();
```

## Real-World Examples

### E-commerce: Users with Recent Orders

```java
// Find customers who placed orders in last 30 days
LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

List<Customer> recentCustomers = Hefesto.make(Customer.class)
    .whereExists(Order.class, sub -> {
        sub.getBuilder().whereField("customerId", "id");
        sub.where("createdAt", thirtyDaysAgo, Operator.GREATER);
        sub.where("status", "completed");
    })
    .orderBy("name")
    .get();
```

### Blog: Authors with Popular Content

```java
// Find authors with at least one post with 1000+ views
List<User> popularAuthors = Hefesto.make(User.class)
    .whereExists(Post.class, sub -> {
        sub.getBuilder().whereField("authorId", "id");
        sub.where("views", 1000, Operator.GREATER_OR_EQUAL);
        sub.where("published", true);
    })
    .orderBy("name")
    .get();
```

### Social Media: Inactive Users

```java
// Find users with no activity in last 90 days
LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);

List<User> inactiveUsers = Hefesto.make(User.class)
    .whereNotExists(Post.class, sub -> {
        sub.getBuilder().whereField("userId", "id");
        sub.where("createdAt", ninetyDaysAgo, Operator.GREATER);
    })
    .whereNotExists(Comment.class, sub -> {
        sub.getBuilder().whereField("userId", "id");
        sub.where("createdAt", ninetyDaysAgo, Operator.GREATER);
    })
    .get();
```

### E-commerce: Products Without Sales

```java
// Find products that have never been ordered
List<Product> unsoldProducts = Hefesto.make(Product.class)
    .whereNotExists(OrderItem.class, sub -> {
        sub.getBuilder().whereField("productId", "id");
    })
    .where("active", true)
    .orderBy("createdAt", "DESC")
    .get();
```

### Advanced: Users with High Engagement

```java
// Find users who have both:
// - Written posts with avg views > 500
// - Received comments from verified users
List<User> engagedUsers = Hefesto.make(User.class)
    .whereExists(Post.class, postSub -> {
        postSub.getBuilder().whereField("authorId", "id");
        postSub.addSelect("AVG(views)");
        postSub.groupBy("authorId");
        postSub.where("AVG(views)", 500, Operator.GREATER);
    })
    .whereExists(Comment.class, commentSub -> {
        commentSub.join("author");
        commentSub.whereExists(Post.class, userPostSub -> {
            userPostSub.getBuilder().whereField("id", "comment.postId");
            userPostSub.getBuilder().whereField("authorId", "users.id");
        });
        commentSub.where("author.verified", true);
    })
    .get();
```

## Subquery Context Methods

Inside the subquery lambda, you have access to:

### Selection

```java
sub.addSelect("field")                     // Select single field
sub.addSelect("field1", "field2")          // Select multiple fields
sub.addSelect("field", CustomType.class)   // Select with type
```

### Filtering

```java
sub.where("field", value)
sub.where("field", value, Operator.GREATER)
sub.whereIn("field", value1, value2)
sub.whereNotIn("field", values)
sub.whereIsNull("field")
sub.whereIsNotNull("field")
```

### Conditional Groups

```java
sub.whereAny(group -> {
    group.where("field1", value1);
    group.where("field2", value2);
})
sub.whereAll(group -> {
    group.where("field1", value1);
    group.where("field2", value2);
})
```

### Joins

```java
sub.join("relationship")
sub.join("relationship", JoinOperator.LEFT)
```

### Sorting and Limiting

```java
sub.orderBy("field")
sub.orderBy("field", "DESC")
sub.limit(10)
sub.offset(5)
```

### Grouping

```java
sub.groupBy("field1", "field2")
```

### Advanced Operations

```java
sub.getBuilder()  // Access full builder for advanced methods like whereField()
```

## Type Inference

HefestoSQL automatically infers the result type from the field:

```java
// Type inferred as Long from User.id
.whereIn("id", Post.class, sub -> {
    sub.addSelect("userId");  // Automatically typed as Long
})
```

### Manual Type Override

If automatic inference doesn't work, specify the type:

```java
.whereIn("customField", Post.class, sub -> {
    sub.addSelect("specialField", String.class);
})
```

## Performance Considerations

### 1. Use EXISTS Instead of IN for Large Result Sets

**EXISTS** stops after finding first match:
```java
// Good for large subqueries
.whereExists(Post.class, sub -> {
    sub.getBuilder().whereField("userId", "id");
})
```

**IN** returns all matching values:
```java
// Good for small subqueries
.whereIn("id", Post.class, sub -> {
    sub.addSelect("userId");
})
```

### 2. Add Indexes

Ensure foreign key columns used in subqueries are indexed:
```sql
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_comments_post_id ON comments(post_id);
```

### 3. Use Joins When Appropriate

Sometimes joins are more efficient than subqueries:

**Subquery:**
```java
.whereIn("id", Post.class, sub -> {
    sub.addSelect("userId");
    sub.where("published", true);
})
```

**Join (may be faster):**
```java
.join("posts", join -> {
    join.where("published", true);
})
```

### 4. Limit Subquery Results

Add limits to prevent large result sets:
```java
.whereIn("id", Post.class, sub -> {
    sub.addSelect("userId");
    sub.where("featured", true);
    sub.limit(100);  // Limit subquery results
})
```

## Common Patterns

### Find Records with Related Data

```java
// Pattern: Users with X
.whereExists(RelatedEntity.class, sub -> {
    sub.getBuilder().whereField("foreignKey", "id");
    sub.where("condition", value);
})
```

### Find Records without Related Data

```java
// Pattern: Users without X
.whereNotExists(RelatedEntity.class, sub -> {
    sub.getBuilder().whereField("foreignKey", "id");
})
```

### Find Records with Specific Counts

```java
// Users with more than 10 posts
.whereCustom((cb, cq, root, joins, parentRoot) -> {
    Subquery<Long> subquery = cq.subquery(Long.class);
    Root<Post> postRoot = subquery.from(Post.class);
    subquery.select(cb.count(postRoot.get("id")));
    subquery.where(cb.equal(postRoot.get("userId"), root.get("id")));
    return cb.greaterThan(subquery, 10L);
})
```

### Anti-Join Pattern

```java
// Find A without matching B (anti-join)
.whereNotIn("id", B.class, sub -> {
    sub.addSelect("aId");
    sub.where("condition", value);
})
```

## Troubleshooting

### Subquery Returns No Results

1. Test the subquery independently:
   ```java
   List<Long> ids = Hefesto.make(Post.class)
       .addSelect("userId")
       .where("published", true)
       .findFor(Long.class);
   ```

2. Check correlation in EXISTS queries:
   ```java
   sub.getBuilder().whereField("foreignKey", "primaryKey");
   ```

### Type Mismatch Errors

Ensure selected field type matches the where field type:
```java
// user.id is Long, so subquery must return Long
.whereIn("id", Post.class, sub -> {
    sub.addSelect("userId");  // Must be Long
})
```

### Performance Issues

1. Enable SQL logging to see generated queries
2. Check for missing indexes
3. Consider using joins instead of subqueries
4. Add LIMIT to subqueries
5. Use EXISTS instead of IN for large result sets

## Best Practices

1. **Use EXISTS for existence checks** - More efficient than counting
2. **Limit subquery fields** - Select only what you need
3. **Add appropriate indexes** - On foreign keys and filtered columns
4. **Test subqueries independently** - Easier debugging
5. **Use type-safe properties** - When available for compile-time safety
6. **Document complex subqueries** - Explain the business logic

## Next Steps

- Explore [Conditional Groups](CONDITIONAL_GROUPS.md) for complex WHERE logic
- Learn about [Deep Joins](DEEP_JOINS.md) as an alternative to subqueries
- Check out [Advanced Features](ADVANCED_FEATURES.md) for more query techniques

## API Reference

```java
// WHERE IN
whereIn(String field, Class<S> subQueryModel, Consumer<SubQueryContext<Hefesto<S>>> block)
orWhereIn(String field, Class<S> subQueryModel, Consumer<SubQueryContext<Hefesto<S>>> block)

// WHERE NOT IN
whereNotIn(String field, Class<S> subQueryModel, Consumer<SubQueryContext<Hefesto<S>>> block)
orWhereNotIn(String field, Class<S> subQueryModel, Consumer<SubQueryContext<Hefesto<S>>> block)

// WHERE EXISTS
whereExists(Class<S> subQueryModel, Consumer<SubQueryContext<Hefesto<S>>> block)
orWhereExists(Class<S> subQueryModel, Consumer<SubQueryContext<Hefesto<S>>> block)

// WHERE NOT EXISTS
whereNotExists(Class<S> subQueryModel, Consumer<SubQueryContext<Hefesto<S>>> block)
orWhereNotExists(Class<S> subQueryModel, Consumer<SubQueryContext<Hefesto<S>>> block)
```

### SubQueryContext Methods

```java
// Selection
addSelect(String... fields)
addSelect(String field, Class<R> resultClass)

// Filtering
where(String field, Object value)
where(String field, Object value, Operator operator)
whereIn(String field, Object... values)
whereNotIn(String field, Object... values)
whereIsNull(String field)
whereIsNotNull(String field)

// Groups
whereAny(Consumer<WhereGroupContext> block)
whereAll(Consumer<WhereGroupContext> block)

// Joins
join(String relationship)
join(String relationship, JoinOperator type)

// Sorting
orderBy(String field)
orderBy(String field, String direction)

// Pagination
limit(int limit)
offset(int offset)

// Grouping
groupBy(String... fields)

// Advanced
getBuilder()  // Access to full builder (for whereField, etc.)
```
