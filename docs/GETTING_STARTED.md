# Getting Started with HefestoSQL

This guide will help you set up and start using HefestoSQL in your project.

## Prerequisites

- Java 17 or higher
- Hibernate 6.x (or Hibernate 5.x with version 1.x.x)
- Maven or Gradle

## Installation

### Hibernate 6.x (Recommended)

#### Maven
```xml
<dependency>
    <groupId>io.github.robertomike</groupId>
    <artifactId>hefesto-hibernate</artifactId>
    <version>2.1.1</version>
</dependency>
```

#### Gradle (Kotlin DSL)
```kotlin
dependencies {
    implementation("io.github.robertomike:hefesto-hibernate:2.1.1")
}
```

#### Gradle (Groovy)
```gradle
dependencies {
    implementation 'io.github.robertomike:hefesto-hibernate:2.1.1'
}
```

### Hibernate 5.x (Legacy)

Use version `1.1.1` instead of `2.1.1` in the dependency declarations above.

## Entity Setup

All your entities must extend `BaseModel`:

```java
import io.github.robertomike.hefesto.models.BaseModel;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends BaseModel {
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "age")
    private Integer age;
    
    @OneToMany(mappedBy = "user")
    private List<Post> posts;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    // Getters and setters...
}
```

**Why extend BaseModel?**
- Provides standard `id` field
- Includes common utility methods
- Enables HefestoSQL's advanced features

## Configuration

### Spring Boot (Auto-configuration)

If you're using Spring Boot 2 or 3, **no configuration is needed!** HefestoSQL automatically detects and configures the Hibernate Session.

### Non-Spring Boot Applications

You need to manually set the Hibernate Session:

```java
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import io.github.robertomike.hefesto.builders.Hefesto;

public class Application {
    
    private SessionFactory sessionFactory;
    
    public void init() {
        // Create your SessionFactory (standard Hibernate setup)
        Configuration configuration = new Configuration();
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();
        
        // Set the session for HefestoSQL
        Session session = sessionFactory.openSession();
        Hefesto.setSession(session);
    }
    
    public void performQuery() {
        // Now you can use Hefesto
        List<User> users = Hefesto.make(User.class).get();
    }
}
```

**Important**: The session should be set per request/transaction in web applications. Use filters or interceptors to manage this.

## Your First Query

### Simple Retrieval

```java
import io.github.robertomike.hefesto.builders.Hefesto;
import java.util.List;

// Get all users
List<User> allUsers = Hefesto.make(User.class).get();

// Get first 10 users
List<User> firstTen = Hefesto.make(User.class)
    .limit(10)
    .get();
```

### Finding Records

```java
import java.util.Optional;

// Find by ID
Optional<User> user = Hefesto.make(User.class)
    .findFirstById(1L);

// Find first matching a condition
Optional<User> johnDoe = Hefesto.make(User.class)
    .where("name", "John Doe")
    .findFirst();
```

### Filtering with WHERE

```java
import io.github.robertomike.hefesto.enums.Operator;

// Simple equality
List<User> adults = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER_OR_EQUAL)
    .get();

// Multiple conditions (AND)
List<User> activeAdults = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER_OR_EQUAL)
    .where("status", "active")
    .get();

// LIKE operator
List<User> gmailUsers = Hefesto.make(User.class)
    .where("email", "%@gmail.com", Operator.LIKE)
    .get();
```

### Sorting

```java
// Ascending order
List<User> sortedByName = Hefesto.make(User.class)
    .orderBy("name")
    .get();

// Descending order
List<User> sortedByAge = Hefesto.make(User.class)
    .orderBy("age", "DESC")
    .get();

// Multiple sort fields
List<User> sorted = Hefesto.make(User.class)
    .orderBy("department")
    .orderBy("name")
    .get();
```

### Joins

```java
// Simple join
List<User> usersWithPosts = Hefesto.make(User.class)
    .join("posts")
    .get();

// Left join
List<User> allUsersAndTheirPosts = Hefesto.make(User.class)
    .join("posts", JoinOperator.LEFT)
    .get();

// Join with conditions
List<User> usersWithPublishedPosts = Hefesto.make(User.class)
    .join("posts", join -> {
        join.where("status", "published");
    })
    .get();
```

### Aggregations

```java
// Count records
Long userCount = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER)
    .countResults();

// Get maximum value
Long maxId = Hefesto.make(User.class)
    .max("id")
    .findFirstFor(Long.class);

// Calculate average
Double avgAge = Hefesto.make(User.class)
    .avg("age")
    .findFirstFor(Double.class);
```

### Pagination

```java
import io.github.robertomike.hefesto.utils.Page;

// Get page 1 with 20 items per page
Page<User> page = Hefesto.make(User.class)
    .orderBy("name")
    .paginate(1, 20);

// Access page data
List<User> users = page.getData();
long totalRecords = page.getTotal();
int currentPage = page.getPage();
int totalPages = page.getTotalPages();
```

## Common Patterns

### Dynamic Queries

Build queries conditionally based on input:

```java
public List<User> searchUsers(String name, Integer minAge, String email) {
    var query = Hefesto.make(User.class);
    
    if (name != null && !name.isEmpty()) {
        query.where("name", name + "%", Operator.LIKE);
    }
    
    if (minAge != null) {
        query.where("age", minAge, Operator.GREATER_OR_EQUAL);
    }
    
    if (email != null && !email.isEmpty()) {
        query.where("email", email);
    }
    
    return query.orderBy("name").get();
}
```

### Counting with Filters

```java
// Count active users
Long activeCount = Hefesto.make(User.class)
    .where("status", "active")
    .countResults();

// Count users in age range
Long count = Hefesto.make(User.class)
    .where("age", 18, Operator.GREATER_OR_EQUAL)
    .where("age", 65, Operator.LESS)
    .countResults();
```

### Checking Existence

```java
// Check if any record exists
boolean hasActiveUsers = Hefesto.make(User.class)
    .where("status", "active")
    .countResults() > 0;

// Check if specific record exists
boolean exists = Hefesto.make(User.class)
    .where("email", "user@example.com")
    .findFirst()
    .isPresent();
```

## Next Steps

Now that you've learned the basics, explore advanced features:

- **[Type-Safe Properties](TYPE_SAFE_PROPERTIES.md)** - Compile-time safety for field references
- **[Deep Joins](DEEP_JOINS.md)** - Navigate complex relationships
- **[Conditional Groups](CONDITIONAL_GROUPS.md)** - Build complex WHERE clauses with AND/OR logic
- **[Subqueries](SUBQUERIES.md)** - Use subqueries in your queries
- **[Advanced Features](ADVANCED_FEATURES.md)** - Custom DTOs, projections, and more

## Troubleshooting

### "Session is not set" Error

**Problem**: You forgot to configure the Hibernate Session.

**Solution**: 
- **Spring Boot**: Ensure Spring Boot autoconfiguration is enabled
- **Non-Spring**: Call `Hefesto.setSession(session)` before making queries

### "Table doesn't exist" Error

**Problem**: Hibernate can't find your entity mapping.

**Solution**:
- Verify your entity has `@Entity` and `@Table` annotations
- Check Hibernate configuration includes your entity package
- Ensure database schema is up to date

### ClassCastException with Projections

**Problem**: Query results don't match the expected type.

**Solution**:
- Ensure your DTO constructor matches the selected fields
- Use `findFor(YourClass.class)` instead of `get()` for custom types
- Verify field types match between entity and DTO

## Support

- **GitHub Issues**: [Report bugs or request features](https://github.com/RobertoMike/HefestoSql/issues)
- **Documentation**: [Browse all guides](README.md)
- **Examples**: See the `examples/` directory in the repository
