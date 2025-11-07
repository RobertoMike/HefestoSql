# 🔥 HefestoSQL

[![Maven Central](https://img.shields.io/maven-central/v/io.github.robertomike/hefesto-hibernate.svg)](https://central.sonatype.com/artifact/io.github.robertomike/hefesto-hibernate)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE.txt)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Hibernate](https://img.shields.io/badge/Hibernate-6.0+-green.svg)](https://hibernate.org/)

**HefestoSQL** is a powerful, fluent API wrapper for Hibernate Criteria Builder and HQL that dramatically simplifies database queries. Write cleaner, more maintainable code with 70% less boilerplate.

## ✨ Key Features

- 🎯 **Type-Safe Properties**: Compile-time safety with property references
- 🔗 **Deep Joins**: Navigate complex relationships with dot notation
- 🔍 **Lambda Conditional Groups**: Build complex WHERE clauses elegantly
- 🚀 **Subquery Builder**: First-class subquery support with lambda syntax
- 📊 **Aggregate Shortcuts**: Simplified COUNT, SUM, AVG, MIN, MAX operations
- 🎨 **Inline Join Conditions**: Apply WHERE conditions directly on joins
- 🔄 **Dual Implementation**: Choose between Criteria Builder or HQL

## 📦 Installation

### Hibernate 6.x (Latest - Recommended)

**Maven:**
```xml
<dependency>
    <groupId>io.github.robertomike</groupId>
    <artifactId>hefesto-hibernate</artifactId>
    <version>2.1.1</version>
</dependency>
```

**Gradle (Kotlin DSL):**
```kotlin
dependencies {
    implementation("io.github.robertomike:hefesto-hibernate:2.1.1")
}
```

**Gradle (Groovy):**
```gradle
dependencies {
    implementation 'io.github.robertomike:hefesto-hibernate:2.1.1'
}
```

### Hibernate 5.x (Legacy)

```xml
<dependency>
    <groupId>io.github.robertomike</groupId>
    <artifactId>hefesto-hibernate</artifactId>
    <version>1.1.1</version>
</dependency>
```

## 🚀 Quick Start

### 1. Entity Setup

Your entities must extend `BaseModel`:

```java
@Entity
@Table(name = "users")
public class User extends BaseModel {
    private String name;
    private String email;
    private Integer age;
    
    @OneToMany(mappedBy = "user")
    private List<Post> posts;
    
    // Getters and setters...
}
```

### 2. Configuration (Non-Spring Boot)

```java
import org.hibernate.Session;
import io.github.robertomike.hefesto.builders.Hefesto;

public class Configuration {
    public void configure(Session session) {
        Hefesto.setSession(session);
    }
}
```

**Note:** If using Spring Boot 2 or 3, configuration is automatic! ✨

### 3. Your First Query

```java
// Get all users
List<User> users = Hefesto.make(User.class).get();

// Find user by ID
Optional<User> user = Hefesto.make(User.class).findFirstById(1L);

// Filter users
List<User> adults = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER_OR_EQUAL)
    .get();
```

## 📚 Complete Feature Guide

### 🎯 Feature #1: Type-Safe Properties

Access entity properties with compile-time safety:

```java
// Traditional approach (error-prone)
.where("name", "John")  // Typo risk!

// Type-safe approach
.where(User_.name(), "John")  // Compile-time checked!

// Works with joins too
.join("posts")
.where(Post_.title(), "My Post")
```

**Benefits:**
- ✅ Compile-time error detection
- ✅ IDE autocomplete support
- ✅ Refactoring-friendly
- ✅ No runtime string errors

### 🔗 Feature #2: Deep Joins

Navigate complex relationships effortlessly:

```java
// Dot notation for deep joins
Hefesto.make(User.class)
    .joinDeep("posts.comments.author")
    .get();

// Equivalent to manually creating:
// root.join("posts").join("comments").join("author")

// With custom join types and aliases
Hefesto.make(User.class)
    .joinDeep("posts.comments", "postComments", JoinOperator.LEFT)
    .where("postComments.text", "Great!", Operator.LIKE)
    .get();
```

### 🔍 Feature #3: Lambda Conditional Groups

Build complex WHERE clauses with clean syntax:

```java
// Complex AND/OR logic
Hefesto.make(User.class)
    .whereAny(group -> {
        // This creates: (age > 25 OR age < 18)
        group.where("age", 25, Operator.GREATER);
        group.where("age", 18, Operator.LESS);
    })
    .whereAll(group -> {
        // AND (email LIKE '%@gmail.com' AND name LIKE 'John%')
        group.where("email", "%@gmail.com", Operator.LIKE);
        group.where("name", "John%", Operator.LIKE);
    })
    .get();

// Nested groups
Hefesto.make(User.class)
    .whereAny(outer -> {
        outer.where("status", "active");
        outer.whereAll(inner -> {
            inner.where("age", 18, Operator.GREATER);
            inner.where("verified", true);
        });
    })
    .get();
```

### 🚀 Feature #4: Subquery Builder

First-class subquery support with lambda syntax:

```java
// EXISTS subquery
Hefesto.make(User.class)
    .whereExists(sub -> sub
        .from(Post.class)
        .where("user.id", "${parent.id}")  // Reference parent query
        .where("published", true)
    )
    .get();

// IN subquery
Hefesto.make(User.class)
    .whereIn("id", sub -> sub
        .from(Post.class)
        .select("user.id")
        .where("views", 1000, Operator.GREATER)
    )
    .get();

// Complex subquery with joins
Hefesto.make(User.class)
    .whereNotIn("id", sub -> sub
        .from(Comment.class)
        .join("post")
        .select("post.user.id")
        .where("text", "spam", Operator.LIKE)
    )
    .get();
```

### 📊 Feature #5: Aggregate Shortcuts

Simplified aggregate operations:

```java
// Count
Long userCount = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER)
    .countResults();

// Using shortcuts
Long maxId = Hefesto.make(User.class)
    .max("id")
    .findFirstFor(Long.class);

Double avgAge = Hefesto.make(User.class)
    .avg("age")
    .findFirstFor(Double.class);

// Multiple aggregates
List<Object[]> stats = Hefesto.make(User.class)
    .count("id", "totalUsers")
    .avg("age", "averageAge")
    .max("createdAt", "newestUser")
    .findFor(Object[].class);

// Group by with aggregates
List<AgeStats> ageStats = Hefesto.make(User.class)
    .select("country")
    .count("id", "total")
    .avg("age", "avgAge")
    .groupBy("country")
    .findFor(AgeStats.class);
```

### 🎨 Feature #6: Inline Join Conditions

Apply WHERE conditions directly on joins:

```java
// Traditional approach (filter after join)
Hefesto.make(User.class)
    .join("posts")
    .where("posts.status", "published")
    .get();

// Inline join conditions (cleaner, more efficient)
Hefesto.make(User.class)
    .join("posts", join -> {
        join.alias("Post");
        join.where("status", "published");
        join.where("views", 100, Operator.GREATER);
    })
    .get();

// With different join types
Hefesto.make(User.class)
    .join("posts", JoinOperator.LEFT, join -> {
        join.alias("PublishedPosts");
        join.where("status", "published");
    })
    .where("age", 25, Operator.GREATER)
    .get();

// Multiple joins with conditions
Hefesto.make(User.class)
    .join("posts", join -> {
        join.alias("Post");
        join.where("status", "published");
    })
    .join("comments", join -> {
        join.alias("Comment");
        join.where("approved", true);
    })
    .get();
```

## 🔧 Advanced Usage

### Custom DTOs / Projections

Return custom result classes instead of entities:

```java
// Define a DTO
public class UserSummary {
    private String name;
    private String email;
    
    public UserSummary(String name, String email) {
        this.name = name;
        this.email = email;
    }
    // Getters...
}

// Project to DTO
List<UserSummary> summaries = Hefesto.make(User.class, UserSummary.class)
    .select("name", "email")
    .get();

// With joins
List<UserWithPostCount> results = Hefesto.make(User.class, UserWithPostCount.class)
    .select("name", "email")
    .join("posts")
    .count("posts.id", "postCount")
    .groupBy("id", "name", "email")
    .findFor(UserWithPostCount.class);
```

### Pagination

```java
// Simple pagination
Page<User> page = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER)
    .orderBy("name")
    .paginate(1, 20); // page 1, 20 items per page

// Access page data
List<User> users = page.getData();
long total = page.getTotal();
int currentPage = page.getPage();
```

### Dynamic Queries

```java
public List<User> searchUsers(String name, Integer minAge, String email) {
    var query = Hefesto.make(User.class);
    
    // Conditionally add filters
    if (name != null) {
        query.where("name", name + "%", Operator.LIKE);
    }
    
    if (minAge != null) {
        query.where("age", minAge, Operator.GREATER_OR_EQUAL);
    }
    
    if (email != null) {
        query.where("email", email);
    }
    
    return query.orderBy("name").get();
}
```

### Custom Predicates

For complex scenarios not covered by the API:

```java
Hefesto.make(User.class)
    .whereCustom((cb, cr, root, joins, parentRoot) -> {
        // Full access to Criteria API
        return cb.and(
            cb.like(root.get("email"), "%@company.com"),
            cb.or(
                cb.equal(root.get("department"), "IT"),
                cb.equal(root.get("department"), "Engineering")
            )
        );
    })
    .get();
```

## 📖 Query Methods Reference

### Retrieval Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `get()` | `List<T>` | Get all matching records |
| `findFirst()` | `Optional<T>` | Get first matching record |
| `findFirstById(id)` | `Optional<T>` | Get by ID |
| `findFirstBy(field, value)` | `Optional<T>` | Get first by field |
| `findFirstBy(field, op, value)` | `Optional<T>` | Get first with operator |
| `countResults()` | `Long` | Count matching records |
| `paginate(page, size)` | `Page<T>` | Get paginated results |
| `findFor(Class)` | `List<R>` | Get as different type |
| `findFirstFor(Class)` | `R` | Get first as different type |

### WHERE Operators

| Operator | SQL Equivalent | Example |
|----------|----------------|---------|
| `EQUAL` | `=` | `.where("age", 25)` |
| `DIFF` | `<>` | `.where("status", "deleted", DIFF)` |
| `GREATER` | `>` | `.where("age", 18, GREATER)` |
| `GREATER_OR_EQUAL` | `>=` | `.where("age", 18, GREATER_OR_EQUAL)` |
| `LESS` | `<` | `.where("price", 100, LESS)` |
| `LESS_OR_EQUAL` | `<=` | `.where("price", 100, LESS_OR_EQUAL)` |
| `LIKE` | `LIKE` | `.where("name", "John%", LIKE)` |
| `NOT_LIKE` | `NOT LIKE` | `.where("email", "%spam%", NOT_LIKE)` |
| `IN` | `IN` | `.whereIn("id", Arrays.asList(1,2,3))` |
| `NOT_IN` | `NOT IN` | `.whereNotIn("status", blocked)` |
| `IS_NULL` | `IS NULL` | `.where("deletedAt", null)` |
| `IS_NOT_NULL` | `IS NOT NULL` | `.where("email", null, DIFF)` |
| `BETWEEN` | `BETWEEN` | `.whereBetween("age", 18, 65)` |

### Join Types

| Type | Description |
|------|-------------|
| `INNER` | Inner join (default) |
| `LEFT` | Left outer join |
| `RIGHT` | Right outer join |

## 🔄 HQL vs Criteria Builder

HefestoSQL supports both implementations:

```java
// Criteria Builder (Type-safe, refactoring-friendly)
import io.github.robertomike.hefesto.builders.Hefesto;

List<User> users = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER)
    .get();

// HQL (String-based, more flexible for complex queries)
import io.github.robertomike.hefesto.builders.Hefesto as HqlHefesto;

List<User> users = HqlHefesto.make(User.class)
    .where("age", 18, Operator.GREATER)
    .get();
```

**When to use each:**
- **Criteria Builder**: Type-safety, compile-time checks, IDE support
- **HQL**: Complex native queries, string-based flexibility

## ⚡ Performance

Check our [benchmarks](benchmarks/README.md) for detailed performance comparisons.

**TLDR:** HefestoSQL adds minimal overhead (~2-5%) while providing massive developer productivity gains.

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) for details.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.txt) file for details.

## ☕ Support

If HefestoSQL has helped you, consider [buying me a coffee](https://www.buymeacoffee.com/robertomike)!

[![coffee](./buy-me-coffee.png)](https://www.buymeacoffee.com/robertomike)

## 📚 Additional Resources

- [API Documentation](docs/API.md)
- [Migration Guide](docs/MIGRATION.md)
- [Examples Repository](examples/)
- [Benchmark Results](benchmarks/README.md)
- [Changelog](CHANGELOG.md)

## 🎯 Roadmap

- [ ] **Better fetch/eager loading API**
- [ ] **Custom WHERE clause simplification**
- [ ] **Typed select builder**
- [ ] **Spring Data integration**
- [ ] **QueryDSL compatibility layer**

---

**Made with ❤️ by [RobertoMike](https://github.com/RobertoMike)**
