# HefestoSQL Features Documentation

This document provides comprehensive details on all major features implemented in HefestoSQL.

---

## Table of Contents

1. [Type-Safe Properties](#1-type-safe-properties)
2. [Lambda Conditional Groups](#2-lambda-conditional-groups)
3. [Lambda Subquery Builder](#3-lambda-subquery-builder)
4. [Aggregate Shortcuts](#4-aggregate-shortcuts)
5. [Deep Join Navigation](#5-deep-join-navigation)
6. [Inline Join Conditions](#6-inline-join-conditions)

---

## 1. Type-Safe Properties

**Status:** ✅ Implemented  
**Version:** 2.0+  
**Tests:** 18 test cases

### Overview

Type-safe properties provide compile-time safety when referencing entity fields. Instead of using string literals that can contain typos, you use generated property references.

### Problem Solved

**Before:**
```java
// String literals - prone to typos, no IDE support
.where("usrname", "John")  // Typo! Runtime error
.where("email", "john@example.com")
```

**After:**
```java
// Type-safe references - compile-time checked
.where(User_.username(), "John")  // Typo caught at compile time!
.where(User_.email(), "john@example.com")
```

### Usage Examples

#### Basic Property Reference
```java
List<User> users = Hefesto.make(User.class)
    .where(User_.name(), "John")
    .where(User_.age(), 25, Operator.GREATER)
    .get();
```

#### With Joins
```java
List<User> users = Hefesto.make(User.class)
    .join("posts")
    .where(Post_.title(), "My Post")
    .where(Post_.published(), true)
    .get();
```

#### In Select Statements
```java
List<UserDTO> dtos = Hefesto.make(User.class, UserDTO.class)
    .select(User_.name(), User_.email(), User_.age())
    .get();
```

#### With Ordering
```java
List<User> users = Hefesto.make(User.class)
    .orderBy(User_.createdAt(), "DESC")
    .orderBy(User_.name(), "ASC")
    .get();
```

### Benefits

- ✅ **Compile-time safety**: Typos caught during compilation
- ✅ **IDE autocomplete**: Full IntelliJ/Eclipse support
- ✅ **Refactoring-friendly**: Rename properties across your codebase
- ✅ **No runtime surprises**: Errors caught before deployment

### Implementation Details

Property references use Hibernate's JPA Metamodel generation. Each entity class gets a corresponding `Entity_` class with static methods returning property names.

---

## 2. Lambda Conditional Groups

**Status:** ✅ Implemented  
**Version:** 2.0+  
**Tests:** 22 test cases

### Overview

Lambda conditional groups allow you to build complex AND/OR WHERE clauses using clean, readable lambda syntax instead of nested predicate builders.

### Problem Solved

**Before:**
```java
// Complex nested predicates
CriteriaBuilder cb = session.getCriteriaBuilder();
Predicate age1 = cb.gt(root.get("age"), 25);
Predicate age2 = cb.lt(root.get("age"), 18);
Predicate emailLike = cb.like(root.get("email"), "%@gmail.com");
Predicate nameLike = cb.like(root.get("name"), "John%");
Predicate orPredicate = cb.or(age1, age2);
Predicate andPredicate = cb.and(emailLike, nameLike);
cq.where(cb.and(orPredicate, andPredicate));
```

**After:**
```java
// Clean lambda syntax
Hefesto.make(User.class)
    .whereAny(group -> {
        group.where("age", 25, Operator.GREATER);
        group.where("age", 18, Operator.LESS);
    })
    .whereAll(group -> {
        group.where("email", "%@gmail.com", Operator.LIKE);
        group.where("name", "John%", Operator.LIKE);
    })
    .get();
```

### Usage Examples

#### Simple OR Group
```java
// Find users who are either admins OR moderators
List<User> users = Hefesto.make(User.class)
    .whereAny(group -> {
        group.where("role", "admin");
        group.where("role", "moderator");
    })
    .get();
// SQL: WHERE (role = 'admin' OR role = 'moderator')
```

#### Simple AND Group
```java
// Find active users with verified emails
List<User> users = Hefesto.make(User.class)
    .whereAll(group -> {
        group.where("active", true);
        group.where("emailVerified", true);
    })
    .get();
// SQL: WHERE (active = true AND emailVerified = true)
```

#### Nested Groups
```java
// Complex logic: (age > 18 AND verified) OR (age > 25)
List<User> users = Hefesto.make(User.class)
    .whereAny(outer -> {
        outer.whereAll(inner -> {
            inner.where("age", 18, Operator.GREATER);
            inner.where("verified", true);
        });
        outer.where("age", 25, Operator.GREATER);
    })
    .get();
// SQL: WHERE ((age > 18 AND verified = true) OR age > 25)
```

#### Multiple Conditional Groups
```java
// Combine multiple groups
List<User> users = Hefesto.make(User.class)
    .whereAny(group -> {
        group.where("status", "active");
        group.where("status", "pending");
    })
    .whereAll(group -> {
        group.where("age", 18, Operator.GREATER);
        group.where("country", "US");
    })
    .get();
// SQL: WHERE (status = 'active' OR status = 'pending') 
//       AND (age > 18 AND country = 'US')
```

#### With Operators
```java
// Using different operators in groups
List<User> users = Hefesto.make(User.class)
    .whereAny(group -> {
        group.where("age", 65, Operator.GREATER);
        group.where("age", 18, Operator.LESS);
    })
    .whereAll(group -> {
        group.where("email", "%@company.com", Operator.LIKE);
        group.where("salary", 50000, Operator.GREATER_OR_EQUAL);
    })
    .get();
```

### Benefits

- ✅ **Readable**: Natural AND/OR logic flow
- ✅ **Composable**: Nest groups as deeply as needed
- ✅ **Type-safe**: Full compile-time checking
- ✅ **Maintainable**: Easy to understand and modify

### API Reference

| Method | Description |
|--------|-------------|
| `whereAny(Consumer<ConditionalGroup>)` | Creates an OR group |
| `whereAll(Consumer<ConditionalGroup>)` | Creates an AND group |

Inside the lambda, use the `group` parameter to add conditions:
- `group.where(field, value)`
- `group.where(field, value, operator)`
- `group.whereAny(...)` - nested OR
- `group.whereAll(...)` - nested AND

---

## 3. Lambda Subquery Builder

**Status:** ✅ Implemented  
**Version:** 2.0+  
**Tests:** 20 test cases

### Overview

First-class support for SQL subqueries using lambda syntax. Build complex subqueries with the same fluent API as main queries.

### Problem Solved

**Before:**
```java
// Complex subquery creation
CriteriaBuilder cb = session.getCriteriaBuilder();
CriteriaQuery<User> mainQuery = cb.createQuery(User.class);
Root<User> mainRoot = mainQuery.from(User.class);

Subquery<Long> subquery = mainQuery.subquery(Long.class);
Root<Post> subRoot = subquery.from(Post.class);
subquery.select(subRoot.get("user").get("id"));
subquery.where(cb.equal(subRoot.get("published"), true));

mainQuery.where(mainRoot.get("id").in(subquery));
```

**After:**
```java
// Clean lambda syntax
Hefesto.make(User.class)
    .whereIn("id", sub -> sub
        .from(Post.class)
        .select("user.id")
        .where("published", true)
    )
    .get();
```

### Usage Examples

#### EXISTS Subquery
```java
// Find users who have published posts
List<User> users = Hefesto.make(User.class)
    .whereExists(sub -> sub
        .from(Post.class)
        .where("user.id", "${parent.id}")  // Reference parent query
        .where("published", true)
    )
    .get();
// SQL: WHERE EXISTS (SELECT 1 FROM Post WHERE user_id = User.id AND published = true)
```

#### NOT EXISTS Subquery
```java
// Find users without any posts
List<User> users = Hefesto.make(User.class)
    .whereNotExists(sub -> sub
        .from(Post.class)
        .where("user.id", "${parent.id}")
    )
    .get();
// SQL: WHERE NOT EXISTS (SELECT 1 FROM Post WHERE user_id = User.id)
```

#### IN Subquery
```java
// Find users who have popular posts (> 1000 views)
List<User> users = Hefesto.make(User.class)
    .whereIn("id", sub -> sub
        .from(Post.class)
        .select("user.id")
        .where("views", 1000, Operator.GREATER)
    )
    .get();
// SQL: WHERE id IN (SELECT user_id FROM Post WHERE views > 1000)
```

#### NOT IN Subquery
```java
// Find users who haven't made spam comments
List<User> users = Hefesto.make(User.class)
    .whereNotIn("id", sub -> sub
        .from(Comment.class)
        .select("user.id")
        .where("text", "%spam%", Operator.LIKE)
    )
    .get();
// SQL: WHERE id NOT IN (SELECT user_id FROM Comment WHERE text LIKE '%spam%')
```

#### Subquery with Joins
```java
// Find users with comments on published posts
List<User> users = Hefesto.make(User.class)
    .whereIn("id", sub -> sub
        .from(Comment.class)
        .join("post")
        .select("user.id")
        .where("post.published", true)
    )
    .get();
```

#### Complex Subquery
```java
// Find users whose average post views exceed 500
List<User> users = Hefesto.make(User.class)
    .whereCustom((cb, cq, root, joins, parentRoot) -> {
        Subquery<Double> subquery = cq.subquery(Double.class);
        // ... complex subquery logic
        return cb.gt(
            subquery,
            500.0
        );
    })
    .get();
```

#### Multiple Subqueries
```java
// Combine multiple subquery conditions
List<User> users = Hefesto.make(User.class)
    .whereExists(sub -> sub
        .from(Post.class)
        .where("user.id", "${parent.id}")
        .where("published", true)
    )
    .whereNotIn("id", sub -> sub
        .from(Comment.class)
        .select("user.id")
        .where("flagged", true)
    )
    .get();
```

### Parent Query References

Use `${parent.field}` syntax to reference fields from the parent query:

```java
.whereExists(sub -> sub
    .from(Post.class)
    .where("userId", "${parent.id}")  // Links to parent User.id
    .where("status", "published")
)
```

### Benefits

- ✅ **Consistent API**: Same fluent syntax as main queries
- ✅ **Type-safe**: Compile-time checking for entity types
- ✅ **Readable**: Self-documenting subquery logic
- ✅ **Powerful**: Supports joins, conditions, and selections

### API Reference

| Method | Description |
|--------|-------------|
| `whereExists(Consumer<SubqueryBuilder>)` | EXISTS subquery |
| `whereNotExists(Consumer<SubqueryBuilder>)` | NOT EXISTS subquery |
| `whereIn(field, Consumer<SubqueryBuilder>)` | IN subquery |
| `whereNotIn(field, Consumer<SubqueryBuilder>)` | NOT IN subquery |

SubqueryBuilder methods:
- `.from(Class)` - Set subquery entity
- `.select(field)` - Select field for IN/NOT IN
- `.where(...)` - Add conditions
- `.join(...)` - Join relationships
- All standard query builder methods

---

## 4. Aggregate Shortcuts

**Status:** ✅ Implemented  
**Version:** 2.0+  
**Tests:** 21 test cases

### Overview

Simplified methods for common aggregate functions (COUNT, SUM, AVG, MIN, MAX) without needing to manually construct selections.

### Problem Solved

**Before:**
```java
// Verbose aggregate construction
CriteriaBuilder cb = session.getCriteriaBuilder();
CriteriaQuery<Long> cq = cb.createQuery(Long.class);
Root<User> root = cq.from(User.class);
cq.select(cb.count(root.get("id")));
cq.where(cb.gt(root.get("age"), 18));
Long count = session.createQuery(cq).getSingleResult();
```

**After:**
```java
// One-liner
Long count = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER)
    .countResults();
```

### Usage Examples

#### COUNT
```java
// Count all users
Long total = Hefesto.make(User.class).countResults();

// Count with conditions
Long adults = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER_OR_EQUAL)
    .countResults();

// Count with alias (for DTO projection)
List<Object[]> results = Hefesto.make(User.class)
    .count("id", "totalUsers")
    .findFor(Object[].class);
```

#### SUM
```java
// Sum all salaries
Long totalSalary = Hefesto.make(User.class)
    .sum("salary")
    .findFirstFor(Long.class);

// Sum with conditions
Long departmentSalary = Hefesto.make(User.class)
    .where("department", "Engineering")
    .sum("salary")
    .findFirstFor(Long.class);
```

#### AVG (Average)
```java
// Average age
Double avgAge = Hefesto.make(User.class)
    .avg("age")
    .findFirstFor(Double.class);

// Average with grouping
List<DepartmentAvg> results = Hefesto.make(User.class, DepartmentAvg.class)
    .select("department")
    .avg("salary", "averageSalary")
    .groupBy("department")
    .findFor(DepartmentAvg.class);
```

#### MIN
```java
// Minimum age
Integer minAge = Hefesto.make(User.class)
    .min("age")
    .findFirstFor(Integer.class);

// Earliest registration date
LocalDateTime earliest = Hefesto.make(User.class)
    .min("createdAt")
    .findFirstFor(LocalDateTime.class);
```

#### MAX
```java
// Maximum age
Integer maxAge = Hefesto.make(User.class)
    .max("age")
    .findFirstFor(Integer.class);

// Latest login
LocalDateTime lastLogin = Hefesto.make(User.class)
    .max("lastLoginAt")
    .findFirstFor(LocalDateTime.class);
```

#### Multiple Aggregates
```java
// Combine multiple aggregates
List<Object[]> stats = Hefesto.make(User.class)
    .count("id", "total")
    .sum("salary", "totalSalary")
    .avg("age", "averageAge")
    .min("createdAt", "firstUser")
    .max("createdAt", "lastUser")
    .findFor(Object[].class);

Object[] result = stats.get(0);
Long total = (Long) result[0];
Long totalSalary = (Long) result[1];
Double avgAge = (Double) result[2];
// etc...
```

#### GROUP BY with Aggregates
```java
// Group by department with statistics
public class DepartmentStats {
    private String department;
    private Long count;
    private Double avgSalary;
    private Long maxSalary;
    
    // Constructor matching select order
    public DepartmentStats(String dept, Long cnt, Double avg, Long max) {
        this.department = dept;
        this.count = cnt;
        this.avgSalary = avg;
        this.maxSalary = max;
    }
}

List<DepartmentStats> stats = Hefesto.make(User.class, DepartmentStats.class)
    .select("department")
    .count("id", "count")
    .avg("salary", "avgSalary")
    .max("salary", "maxSalary")
    .groupBy("department")
    .findFor(DepartmentStats.class);
```

#### Aggregates with Joins
```java
// Count posts per user
List<Object[]> results = Hefesto.make(User.class)
    .select("id", "name")
    .join("posts")
    .count("posts.id", "postCount")
    .groupBy("id", "name")
    .findFor(Object[].class);
```

### Benefits

- ✅ **Concise**: Single method calls instead of complex selection construction
- ✅ **Readable**: Clear intent with method names matching SQL functions
- ✅ **Composable**: Combine multiple aggregates easily
- ✅ **GROUP BY support**: Works seamlessly with grouping

### API Reference

| Method | SQL Function | Return Type Hint |
|--------|--------------|------------------|
| `countResults()` | COUNT(*) | Long |
| `count(field)` | COUNT(field) | For selection |
| `count(field, alias)` | COUNT(field) AS alias | For DTO projection |
| `sum(field)` | SUM(field) | For selection |
| `sum(field, alias)` | SUM(field) AS alias | For DTO projection |
| `avg(field)` | AVG(field) | For selection |
| `avg(field, alias)` | AVG(field) AS alias | For DTO projection |
| `min(field)` | MIN(field) | For selection |
| `min(field, alias)` | MIN(field) AS alias | For DTO projection |
| `max(field)` | MAX(field) | For selection |
| `max(field, alias)` | MAX(field) AS alias | For DTO projection |

---

## 5. Deep Join Navigation

**Status:** ✅ Implemented  
**Version:** 2.1+  
**Tests:** Covered in join tests

### Overview

Navigate complex multi-level relationships using dot notation instead of chaining multiple join calls.

### Problem Solved

**Before:**
```java
// Multiple chained joins
Hefesto.make(User.class)
    .join("posts")
    .join("posts.comments")
    .join("posts.comments.author")
    .get();
```

**After:**
```java
// Single deep join
Hefesto.make(User.class)
    .joinDeep("posts.comments.author")
    .get();
```

### Usage Examples

#### Simple Deep Join
```java
// Navigate through posts -> comments -> author
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments.author")
    .get();
```

#### Deep Join with Alias
```java
// Assign alias to final entity
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments", "userComments")
    .where("userComments.approved", true)
    .get();
```

#### Deep Join with Join Type
```java
// Use LEFT join for entire path
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments.author", "commentAuthors", JoinOperator.LEFT)
    .get();
```

#### Multiple Deep Joins
```java
// Navigate multiple relationship paths
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments")
    .joinDeep("posts.tags")
    .joinDeep("profile.address")
    .get();
```

#### Deep Join with Conditions
```java
// Filter on deeply nested entities
List<User> users = Hefesto.make(User.class)
    .joinDeep("posts.comments", "postComments")
    .where("postComments.text", "%great%", Operator.LIKE)
    .where("postComments.approved", true)
    .get();
```

### Benefits

- ✅ **Concise**: Single method for multi-level joins
- ✅ **Readable**: Clear relationship path visualization
- ✅ **Flexible**: Supports aliases and join types
- ✅ **Maintainable**: Less code to understand and modify

---

## 6. Inline Join Conditions

**Status:** ✅ Implemented  
**Version:** 2.1+  
**Tests:** 6 test cases

### Overview

Apply WHERE conditions directly on JOIN clauses using lambda configuration, resulting in cleaner and more efficient SQL queries.

### Problem Solved

**Before:**
```java
// Conditions applied AFTER join (less efficient)
Hefesto.make(User.class)
    .join("posts")
    .where("posts.status", "published")
    .where("posts.views", 100, Operator.GREATER)
    .get();

// SQL: FROM User u LEFT JOIN Post p ON u.id = p.user_id 
//      WHERE p.status = 'published' AND p.views > 100
```

**After:**
```java
// Conditions on JOIN clause (more efficient, cleaner)
Hefesto.make(User.class)
    .join("posts", join -> {
        join.where("status", "published");
        join.where("views", 100, Operator.GREATER);
    })
    .get();

// SQL: FROM User u LEFT JOIN Post p ON u.id = p.user_id 
//      AND p.status = 'published' AND p.views > 100
```

### Why It Matters

**Performance Benefits:**
- ✅ Conditions evaluated during JOIN (fewer rows processed)
- ✅ Better query optimization by the database
- ✅ Reduced intermediate result sets

**Readability Benefits:**
- ✅ Conditions clearly associated with their joins
- ✅ Logical grouping of related filters
- ✅ Self-documenting query structure

### Usage Examples

#### Basic Inline Condition
```java
// Join only published posts
List<User> users = Hefesto.make(User.class)
    .join("posts", join -> {
        join.where("status", "published");
    })
    .get();
```

#### Multiple Inline Conditions
```java
// Join popular, published posts
List<User> users = Hefesto.make(User.class)
    .join("posts", join -> {
        join.where("status", "published");
        join.where("views", 1000, Operator.GREATER);
        join.where("featured", true);
    })
    .get();
```

#### Inline Conditions with Alias
```java
// Set alias and add conditions
List<User> users = Hefesto.make(User.class)
    .join("posts", join -> {
        join.alias("PublishedPosts");
        join.where("status", "published");
    })
    .orderBy("PublishedPosts.views", "DESC")
    .get();
```

#### Different Join Types with Conditions
```java
// LEFT join with conditions
List<User> users = Hefesto.make(User.class)
    .join("posts", JoinOperator.LEFT, join -> {
        join.where("status", "published");
        join.where("deletedAt", null);  // Only non-deleted posts
    })
    .get();

// INNER join with conditions
List<User> users = Hefesto.make(User.class)
    .join("posts", JoinOperator.INNER, join -> {
        join.where("status", "published");
    })
    .get();
```

#### Multiple Joins with Conditions
```java
// Apply conditions to multiple joins
List<User> users = Hefesto.make(User.class)
    .join("posts", join -> {
        join.alias("Post");
        join.where("status", "published");
    })
    .join("comments", join -> {
        join.alias("Comment");
        join.where("approved", true);
        join.where("spam", false);
    })
    .get();
```

#### Combining with Main WHERE
```java
// Inline join conditions + main WHERE clause
List<User> users = Hefesto.make(User.class)
    .join("posts", join -> {
        join.where("status", "published");  // On JOIN
    })
    .where("age", 18, Operator.GREATER)  // On main query
    .where("active", true)
    .get();

// SQL: FROM User u 
//      LEFT JOIN Post p ON u.id = p.user_id AND p.status = 'published'
//      WHERE u.age > 18 AND u.active = true
```

#### Complex Example
```java
// Real-world scenario: Find active users with recent published posts
List<User> users = Hefesto.make(User.class)
    .where("active", true)
    .where("emailVerified", true)
    .join("posts", JoinOperator.LEFT, join -> {
        join.alias("RecentPost");
        join.where("status", "published");
        join.where("publishedAt", sevenDaysAgo, Operator.GREATER);
        join.where("featured", true);
    })
    .orderBy("RecentPost.publishedAt", "DESC")
    .limit(10)
    .get();
```

### Benefits

- ✅ **Better Performance**: Conditions evaluated during join
- ✅ **Cleaner SQL**: Logical condition placement
- ✅ **More Readable**: Clear intent and association
- ✅ **Flexible**: Works with all join types and operators

### API Reference

```java
// Basic join with conditions
.join(String relationship, Consumer<JoinBuilder> config)

// Join with type and conditions
.join(String relationship, JoinOperator type, Consumer<JoinBuilder> config)

// Inside the lambda:
JoinBuilder methods:
- .alias(String name) - Set alias for joined entity
- .where(String field, Object value) - Add condition
- .where(String field, Object value, Operator op) - Add condition with operator
```

---

## Testing

All features are comprehensively tested:

- **Type-Safe Properties**: 18 tests
- **Lambda Conditional Groups**: 22 tests
- **Lambda Subquery Builder**: 20 tests
- **Aggregate Shortcuts**: 21 tests
- **Inline Join Conditions**: 6 tests

**Total**: 87 dedicated feature tests + existing core functionality tests

Run tests:
```bash
./gradlew test
```

---

## Performance

All features are designed with performance in mind:

- **Minimal overhead**: ~2-5% compared to raw Hibernate
- **Efficient SQL generation**: Optimized query construction
- **Lazy evaluation**: Queries built but not executed until `.get()` or similar

Run benchmarks:
```bash
./gradlew :hefesto-benchmarks:jmh
```

---

## Future Enhancements

Planned improvements for these features:

- [ ] Property reference generation via annotation processor
- [ ] Conditional group builders for more complex scenarios
- [ ] Subquery result caching
- [ ] Aggregate function pipeline
- [ ] Deep join condition support
- [ ] Join condition type safety

---

**Last Updated:** 2024  
**Version:** 2.1+
