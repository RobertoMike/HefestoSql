# 🔥 HefestoSQL

[![Maven Central](https://img.shields.io/maven-central/v/io.github.robertomike/hefesto-hibernate.svg)](https://central.sonatype.com/artifact/io.github.robertomike/hefesto-hibernate)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE.txt)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Hibernate](https://img.shields.io/badge/Hibernate-6.0+-green.svg)](https://hibernate.org/)

**HefestoSQL** is a powerful, fluent API wrapper for Hibernate that dramatically simplifies database queries. Write cleaner, more maintainable code with 70% less boilerplate.

📖 **[Read the Complete Documentation](DOCUMENTATION.md)** for detailed features, examples, and guides!

---

## ✨ Quick Example

```java
// Before: Traditional Hibernate
CriteriaBuilder cb = session.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);
Join<User, Post> posts = root.join("posts", JoinType.LEFT);
Predicate ageCondition = cb.gt(root.get("age"), 18);
Predicate statusCondition = cb.equal(posts.get("status"), "published");
cq.where(cb.and(ageCondition, statusCondition));
cq.orderBy(cb.asc(root.get("name")));
List<User> users = session.createQuery(cq).getResultList();

// After: HefestoSQL
List<User> users = Hefesto.make(User.class)
    .join("posts", join -> join.where("status", "published"))
    .where("age", 18, Operator.GREATER)
    .orderBy("name")
    .get();
```

**That's 70% less code!** 🎉

---

## 📦 Installation

### Hibernate 6.x (Latest)

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
implementation("io.github.robertomike:hefesto-hibernate:2.1.1")
```

**Gradle (Groovy):**
```gradle
implementation 'io.github.robertomike:hefesto-hibernate:2.1.1'
```

### Hibernate 5.x (Legacy)
Replace version with `1.1.1`

---

## 🚀 Getting Started

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

### 2. Configuration (Non-Spring Boot only)

```java
import io.github.robertomike.hefesto.builders.Hefesto;

public class Configuration {
    public void configure(Session session) {
        Hefesto.setSession(session);
    }
}
```

**Spring Boot users**: Configuration is automatic! ✨

### 3. Write Your First Query

```java
// Get all users
List<User> users = Hefesto.make(User.class).get();

// Find user by ID
Optional<User> user = Hefesto.make(User.class).findFirstById(1L);

// Filter with conditions
List<User> adults = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER_OR_EQUAL)
    .get();
```

---

## 🎯 Key Features

### 🔗 Deep Joins
Navigate complex relationships with ease:
```java
Hefesto.make(User.class)
    .joinDeep("posts.comments.author")
    .get();
```

### 🔍 Lambda Conditional Groups
Build complex WHERE clauses elegantly:
```java
Hefesto.make(User.class)
    .whereAny(group -> {
        group.where("age", 25, Operator.GREATER);
        group.where("age", 18, Operator.LESS);
    })
    .get();
```

### 🚀 Subquery Builder
First-class subquery support:
```java
Hefesto.make(User.class)
    .whereExists(sub -> sub
        .from(Post.class)
        .where("user.id", "${parent.id}")
        .where("published", true)
    )
    .get();
```

### 📊 Aggregate Shortcuts
Simplified aggregations:
```java
Long count = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER)
    .countResults();

Double avgAge = Hefesto.make(User.class)
    .avg("age")
    .findFirstFor(Double.class);
```

### 🎨 Inline Join Conditions
Apply WHERE conditions directly on joins:
```java
Hefesto.make(User.class)
    .join("posts", join -> {
        join.where("status", "published");
        join.where("views", 100, Operator.GREATER);
    })
    .get();
```

### 🎯 Type-Safe Properties
Compile-time safety with property references:
```java
Hefesto.make(User.class)
    .where(User_.name(), "John")  // Compile-time checked!
    .get();
```

---

## 📚 More Examples

### Pagination
```java
Page<User> page = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER)
    .orderBy("name")
    .paginate(1, 20); // page 1, 20 items per page
```

### Custom DTOs / Projections
```java
List<UserSummary> summaries = Hefesto.make(User.class, UserSummary.class)
    .select("name", "email")
    .get();
```

### Complex Queries
```java
Hefesto.make(User.class)
    .join("posts", JoinOperator.LEFT, join -> {
        join.where("status", "published");
    })
    .whereAll(group -> {
        group.where("email", "%@company.com", Operator.LIKE);
        group.where("active", true);
    })
    .orderBy("createdAt", "DESC")
    .limit(10)
    .get();
```

---

## 📖 Documentation

### Getting Started
- **[Getting Started Guide](docs/GETTING_STARTED.md)** - Installation, setup, and first queries
- **[Migration Guide](docs/MIGRATION.md)** - Upgrade guide for existing projects

### Core Features
- **[Type-Safe Properties](docs/TYPE_SAFE_PROPERTIES.md)** - Compile-time safe property references (Java & Kotlin)
- **[Deep Joins](docs/DEEP_JOINS.md)** - Navigate multi-level relationships with dot notation
- **[Conditional Groups](docs/CONDITIONAL_GROUPS.md)** - Build complex WHERE clauses with whereAny/whereAll
- **[Subqueries](docs/SUBQUERIES.md)** - Lambda-based subquery builder with EXISTS, IN, NOT IN

### Advanced Topics
- **[Advanced Features](docs/ADVANCED_FEATURES.md)** - DTOs, projections, pagination, performance optimization
- **[Performance Benchmarks](benchmarks/README.md)** - Performance comparison with raw Hibernate

### Reference
- **[Complete Documentation](DOCUMENTATION.md)** - Legacy comprehensive documentation
- **[Releasing Guide](docs/RELEASING.md)** - For maintainers

---

## ⚡ Performance

HefestoSQL adds minimal overhead (~2-5%) while providing massive developer productivity gains. Check our [benchmarks](benchmarks/README.md) for detailed comparisons.

Run benchmarks yourself:
```bash
./gradlew :hefesto-benchmarks:jmh
```

---

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) for details.

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.txt) file for details.

---

## ☕ Support

If HefestoSQL has helped you, consider [buying me a coffee](https://www.buymeacoffee.com/robertomike)!

[![coffee](./buy-me-coffee.png)](https://www.buymeacoffee.com/robertomike)

---

**Made with ❤️ by [RobertoMike](https://github.com/RobertoMike)**
