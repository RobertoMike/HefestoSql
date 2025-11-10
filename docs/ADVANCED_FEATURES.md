# Advanced Features

This guide covers advanced querying techniques including custom DTOs, projections, pagination, dynamic queries, and performance optimization.

## Custom DTOs and Projections

Instead of loading full entities, project data into custom Data Transfer Objects (DTOs) for better performance and cleaner APIs.

### Basic DTO Projection

**Define your DTO:**
```java
public class UserSummary {
    private Long id;
    private String name;
    private String email;
    
    // Constructor matching query field order
    public UserSummary(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // Getters...
}
```

**Query with projection:**
```java
List<UserSummary> summaries = Hefesto.make(User.class, UserSummary.class)
    .addSelect("id", "name", "email")
    .where("active", true)
    .findFor(UserSummary.class);
```

### DTO with Aggregations

```java
public class DepartmentStats {
    private String department;
    private Long employeeCount;
    private Double avgSalary;
    private Long maxSalary;
    
    public DepartmentStats(String dept, Long count, Double avg, Long max) {
        this.department = dept;
        this.employeeCount = count;
        this.avgSalary = avg;
        this.maxSalary = max;
    }
}

List<DepartmentStats> stats = Hefesto.make(Employee.class, DepartmentStats.class)
    .addSelect("department")
    .count("id", "employeeCount")
    .avg("salary", "avgSalary")
    .max("salary", "maxSalary")
    .groupBy("department")
    .findFor(DepartmentStats.class);
```

### Nested DTO with Joins

```java
public class PostWithAuthor {
    private Long postId;
    private String title;
    private String authorName;
    private String authorEmail;
    
    public PostWithAuthor(Long postId, String title, String authorName, String authorEmail) {
        this.postId = postId;
        this.title = title;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
    }
}

List<PostWithAuthor> posts = Hefesto.make(Post.class, PostWithAuthor.class)
    .join("author")
    .addSelect("id", "title", "author.name", "author.email")
    .where("published", true)
    .findFor(PostWithAuthor.class);
```

### Object Array Projection

For quick ad-hoc queries without creating DTOs:

```java
List<Object[]> results = Hefesto.make(User.class)
    .addSelect("id", "name", "email")
    .where("active", true)
    .findFor(Object[].class);

for (Object[] row : results) {
    Long id = (Long) row[0];
    String name = (String) row[1];
    String email = (String) row[2];
}
```

### Single Field Projection

```java
// Get just the IDs
List<Long> ids = Hefesto.make(User.class)
    .addSelect("id")
    .where("active", true)
    .findFor(Long.class);

// Get just the names
List<String> names = Hefesto.make(User.class)
    .addSelect("name")
    .where("verified", true)
    .findFor(String.class);
```

## Pagination

### Basic Pagination

```java
import io.github.robertomike.hefesto.utils.Page;

// Get page 1 with 20 items per page
Page<User> page1 = Hefesto.make(User.class)
    .where("active", true)
    .orderBy("name")
    .paginate(1, 20);

// Access page data
List<User> users = page1.getData();
long totalRecords = page1.getTotal();
int currentPage = page1.getPage();
int pageSize = page1.getPageSize();
int totalPages = page1.getTotalPages();
boolean hasNext = page1.hasNextPage();
boolean hasPrevious = page1.hasPreviousPage();
```

### Pagination with Filters

```java
public Page<User> searchUsers(String search, int page, int size) {
    var query = Hefesto.make(User.class);
    
    if (search != null && !search.isEmpty()) {
        query.whereAny(group -> {
            group.where("name", "%" + search + "%", Operator.LIKE);
            group.where("email", "%" + search + "%", Operator.LIKE);
        });
    }
    
    return query
        .where("active", true)
        .orderBy("name")
        .paginate(page, size);
}
```

### Pagination with DTOs

```java
Page<UserSummary> page = Hefesto.make(User.class, UserSummary.class)
    .addSelect("id", "name", "email")
    .where("role", "user")
    .orderBy("createdAt", "DESC")
    .paginate(1, 50);
```

### Manual Limit and Offset

For custom pagination logic:

```java
int page = 2;
int size = 20;
int offset = (page - 1) * size;

List<User> users = Hefesto.make(User.class)
    .orderBy("name")
    .limit(size)
    .offset(offset)
    .get();

// Get total count separately
Long total = Hefesto.make(User.class).countResults();
```

## Dynamic Queries

Build queries conditionally based on runtime parameters.

### Simple Dynamic Query

```java
public List<User> searchUsers(String name, String email, String role, Boolean active) {
    var query = Hefesto.make(User.class);
    
    if (name != null && !name.isEmpty()) {
        query.where("name", "%" + name + "%", Operator.LIKE);
    }
    
    if (email != null && !email.isEmpty()) {
        query.where("email", email);
    }
    
    if (role != null) {
        query.where("role", role);
    }
    
    if (active != null) {
        query.where("active", active);
    }
    
    return query.orderBy("name").get();
}
```

### Dynamic Sorting

```java
public List<User> getUsers(String sortBy, String sortDir) {
    var query = Hefesto.make(User.class).where("active", true);
    
    if (sortBy != null) {
        String direction = "DESC".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
        query.orderBy(sortBy, direction);
    } else {
        query.orderBy("createdAt", "DESC");  // Default sort
    }
    
    return query.get();
}
```

### Dynamic Filters with Builder Pattern

```java
public class UserQueryBuilder {
    private final Hefesto<User> query;
    
    public UserQueryBuilder() {
        this.query = Hefesto.make(User.class);
    }
    
    public UserQueryBuilder withName(String name) {
        if (name != null && !name.isEmpty()) {
            query.where("name", "%" + name + "%", Operator.LIKE);
        }
        return this;
    }
    
    public UserQueryBuilder withRole(String role) {
        if (role != null) {
            query.where("role", role);
        }
        return this;
    }
    
    public UserQueryBuilder withAgeRange(Integer minAge, Integer maxAge) {
        if (minAge != null) {
            query.where("age", minAge, Operator.GREATER_OR_EQUAL);
        }
        if (maxAge != null) {
            query.where("age", maxAge, Operator.LESS_OR_EQUAL);
        }
        return this;
    }
    
    public UserQueryBuilder active() {
        query.where("active", true);
        return this;
    }
    
    public List<User> execute() {
        return query.orderBy("name").get();
    }
}

// Usage
List<User> users = new UserQueryBuilder()
    .withName("John")
    .withRole("admin")
    .withAgeRange(18, 65)
    .active()
    .execute();
```

### Specification Pattern

```java
public interface UserSpecification {
    void apply(Hefesto<User> query);
}

public class ActiveUserSpec implements UserSpecification {
    public void apply(Hefesto<User> query) {
        query.where("active", true);
    }
}

public class RoleSpec implements UserSpecification {
    private final String role;
    
    public RoleSpec(String role) {
        this.role = role;
    }
    
    public void apply(Hefesto<User> query) {
        query.where("role", role);
    }
}

// Usage
var query = Hefesto.make(User.class);
new ActiveUserSpec().apply(query);
new RoleSpec("admin").apply(query);
List<User> users = query.get();
```

## Aggregate Functions

### COUNT

```java
// Count all
Long total = Hefesto.make(User.class).countResults();

// Count with conditions
Long activeCount = Hefesto.make(User.class)
    .where("active", true)
    .countResults();

// Count with grouping
List<Object[]> countsByDept = Hefesto.make(User.class)
    .addSelect("department")
    .count("id", "userCount")
    .groupBy("department")
    .findFor(Object[].class);
```

### SUM

```java
// Total sales
Long totalSales = Hefesto.make(Order.class)
    .sum("total")
    .findFirstFor(Long.class);

// Sales by category
List<Object[]> salesByCat = Hefesto.make(Product.class)
    .addSelect("category")
    .sum("sales", "totalSales")
    .groupBy("category")
    .findFor(Object[].class);
```

### AVG (Average)

```java
// Average age
Double avgAge = Hefesto.make(User.class)
    .avg("age")
    .findFirstFor(Double.class);

// Average salary by department
List<Object[]> avgSalaries = Hefesto.make(Employee.class)
    .addSelect("department")
    .avg("salary", "avgSalary")
    .groupBy("department")
    .findFor(Object[].class);
```

### MIN and MAX

```java
// Youngest and oldest user
Integer minAge = Hefesto.make(User.class).min("age").findFirstFor(Integer.class);
Integer maxAge = Hefesto.make(User.class).max("age").findFirstFor(Integer.class);

// Min/max by group
List<Object[]> ageRanges = Hefesto.make(User.class)
    .addSelect("department")
    .min("age", "youngest")
    .max("age", "oldest")
    .groupBy("department")
    .findFor(Object[].class);
```

### Multiple Aggregates

```java
public class UserStatistics {
    private Long totalUsers;
    private Double avgAge;
    private Integer minAge;
    private Integer maxAge;
    private Long activeCount;
    
    public UserStatistics(Long total, Double avg, Integer min, Integer max, Long active) {
        this.totalUsers = total;
        this.avgAge = avg;
        this.minAge = min;
        this.maxAge = max;
        this.activeCount = active;
    }
}

UserStatistics stats = Hefesto.make(User.class, UserStatistics.class)
    .count("id", "totalUsers")
    .avg("age", "avgAge")
    .min("age", "minAge")
    .max("age", "maxAge")
    .sum("CASE WHEN active = true THEN 1 ELSE 0 END", "activeCount")
    .findFirstFor(UserStatistics.class);
```

## Distinct Queries

### Distinct Selection

```java
// Get unique values
List<String> uniqueDepartments = Hefesto.make(User.class)
    .addSelect("DISTINCT department")
    .findFor(String.class);
```

### Distinct with Multiple Fields

```java
List<Object[]> uniquePairs = Hefesto.make(User.class)
    .addSelect("DISTINCT department, role")
    .findFor(Object[].class);
```

## Raw SQL Expressions

For cases where you need raw SQL expressions:

```java
// Using string expressions in WHERE
List<User> users = Hefesto.make(User.class)
    .whereRaw("YEAR(createdAt) = ?", 2024)
    .get();

// Using expressions in SELECT
List<Object[]> results = Hefesto.make(User.class)
    .addSelect("name")
    .addSelect("CONCAT(firstName, ' ', lastName) as fullName")
    .findFor(Object[].class);
```

## Fetch Joins vs Regular Joins

### Regular Join (No Eager Loading)

```java
// Posts are not loaded into User entities
List<User> users = Hefesto.make(User.class)
    .join("posts")
    .where("posts.published", true)
    .get();
```

### Fetch Join (Eager Loading)

```java
// Posts are loaded into User entities
List<User> users = Hefesto.make(User.class)
    .joinFetch("posts")
    .where("posts.published", true)
    .get();
```

**Use fetch joins when:**
- You need the related data in memory
- Avoiding N+1 query problems
- Working with entity graphs

**Use regular joins when:**
- Only filtering, not accessing related data
- Using projections/DTOs
- Performance is critical

## N+1 Query Prevention

### Problem: N+1 Queries

```java
// BAD: Triggers N+1 queries
List<User> users = Hefesto.make(User.class).get();
for (User user : users) {
    // Each access triggers a separate query
    System.out.println(user.getPosts().size());
}
```

### Solution 1: Fetch Join

```java
// GOOD: Single query with join
List<User> users = Hefesto.make(User.class)
    .joinFetch("posts")
    .get();

for (User user : users) {
    System.out.println(user.getPosts().size());  // No extra query
}
```

### Solution 2: DTO Projection

```java
// GOOD: Only load what you need
List<Object[]> results = Hefesto.make(User.class)
    .join("posts")
    .addSelect("id", "name")
    .count("posts.id", "postCount")
    .groupBy("id", "name")
    .findFor(Object[].class);
```

## Performance Optimization

### 1. Use Projections for Read-Only Queries

```java
// Instead of loading full entities
List<User> users = Hefesto.make(User.class).get();

// Load only needed fields
List<UserSummary> summaries = Hefesto.make(User.class, UserSummary.class)
    .addSelect("id", "name", "email")
    .findFor(UserSummary.class);
```

### 2. Add Indexes

Ensure database indexes on:
- Foreign keys used in joins
- Fields used in WHERE clauses
- Fields used in ORDER BY

```sql
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_users_created_at ON users(created_at);
```

### 3. Use Pagination

Always paginate large result sets:

```java
// Bad: Loading all records
List<User> users = Hefesto.make(User.class).get();

// Good: Paginated
Page<User> page = Hefesto.make(User.class).paginate(1, 50);
```

### 4. Limit Joins

Avoid unnecessary joins:

```java
// Only join what you need
List<User> users = Hefesto.make(User.class)
    .join("posts")  // Only if filtering on posts or need post data
    .get();
```

### 5. Use EXISTS Instead of IN for Large Subqueries

```java
// More efficient for large subqueries
.whereExists(Post.class, sub -> {
    sub.getBuilder().whereField("userId", "id");
    sub.where("published", true);
})
```

## Caching Strategies

### Query Result Caching

```java
// Enable second-level cache in Hibernate
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends BaseModel {
    // ...
}

// Query with cache
List<User> users = Hefesto.make(User.class)
    .where("active", true)
    .get();  // Results cached
```

### Custom Caching

```java
public class UserRepository {
    private final Cache<String, List<User>> cache = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();
    
    public List<User> getActiveUsers() {
        return cache.get("active_users", () -> 
            Hefesto.make(User.class)
                .where("active", true)
                .orderBy("name")
                .get()
        );
    }
}
```

## Batch Operations

### Batch Updates

```java
// Load users
List<User> users = Hefesto.make(User.class)
    .where("status", "pending")
    .get();

// Update in batch
Session session = Hefesto.getSession();
Transaction tx = session.beginTransaction();
for (int i = 0; i < users.size(); i++) {
    users.get(i).setStatus("processed");
    if (i % 50 == 0) {
        session.flush();
        session.clear();
    }
}
tx.commit();
```

## Best Practices

1. **Use DTOs for read-only queries** - Better performance, cleaner APIs
2. **Always paginate** - Don't load thousands of records at once
3. **Use fetch joins wisely** - Only when you need the related data
4. **Add database indexes** - On frequently queried fields
5. **Use projections** - Select only needed columns
6. **Enable query logging during development** - Identify N+1 issues
7. **Use EXISTS for existence checks** - More efficient than COUNT
8. **Avoid SELECT \*** - Specify fields explicitly
9. **Use connection pooling** - Configure HikariCP or similar
10. **Monitor query performance** - Use tools like Hibernate Statistics

## Common Anti-Patterns to Avoid

### 1. Loading Full Entities for Simple Data

```java
// BAD
List<User> users = Hefesto.make(User.class).get();
List<String> names = users.stream().map(User::getName).collect(Collectors.toList());

// GOOD
List<String> names = Hefesto.make(User.class)
    .addSelect("name")
    .findFor(String.class);
```

### 2. N+1 Queries

```java
// BAD
List<User> users = Hefesto.make(User.class).get();
for (User user : users) {
    user.getPosts().size();  // N+1 queries!
}

// GOOD
List<User> users = Hefesto.make(User.class)
    .joinFetch("posts")
    .get();
```

### 3. Unnecessary Joins

```java
// BAD
List<User> users = Hefesto.make(User.class)
    .join("posts")
    .join("comments")
    .join("profile")
    .get();  // Joins not used!

// GOOD
List<User> users = Hefesto.make(User.class).get();
```

### 4. Missing Pagination

```java
// BAD
List<User> users = Hefesto.make(User.class).get();  // Could be millions!

// GOOD
Page<User> page = Hefesto.make(User.class).paginate(1, 50);
```

## Troubleshooting

### MultipleBagFetchException

**Problem:** Cannot fetch multiple collections simultaneously

**Solution:** Use separate queries or DTO projections
```java
// Instead of
.joinFetch("posts")
.joinFetch("comments")  // Error!

// Use
.joinFetch("posts")
// Separate query for comments
```

### LazyInitializationException

**Problem:** Accessing lazy-loaded data outside session

**Solution:** Use fetch joins or DTOs
```java
List<User> users = Hefesto.make(User.class)
    .joinFetch("posts")
    .get();
```

### Slow Queries

**Problem:** Query takes too long

**Solutions:**
1. Add database indexes
2. Use projections instead of full entities
3. Add pagination
4. Reduce number of joins
5. Use EXISTS instead of IN
6. Check execution plan (EXPLAIN)

## Next Steps

- Review [Getting Started](GETTING_STARTED.md) for basics
- Learn about [Type-Safe Properties](TYPE_SAFE_PROPERTIES.md) for compile-time safety
- Explore [Conditional Groups](CONDITIONAL_GROUPS.md) for complex logic
- Check out [Subqueries](SUBQUERIES.md) for advanced filtering
