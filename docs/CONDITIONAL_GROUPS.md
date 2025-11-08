# Conditional Groups

Conditional groups allow you to build complex WHERE clauses with AND/OR logic using clean, readable lambda syntax.

## Overview

Instead of manually managing predicates, use `whereAny` and `whereAll` to create logical groups.

**Before:**
```java
CriteriaBuilder cb = session.getCriteriaBuilder();
Predicate age1 = cb.gt(root.get("age"), 25);
Predicate age2 = cb.lt(root.get("age"), 18);
Predicate orPredicate = cb.or(age1, age2);
cq.where(orPredicate);
```

**After:**
```java
Hefesto.make(User.class)
    .whereAny(group -> {
        group.where("age", 25, Operator.GREATER);
        group.where("age", 18, Operator.LESS);
    })
    .get();
```

## Basic Usage

### whereAny - OR Logic

Creates a group where conditions are combined with OR:

```java
// Find users who are admins OR moderators
List<User> users = Hefesto.make(User.class)
    .whereAny(group -> {
        group.where("role", "admin");
        group.where("role", "moderator");
    })
    .get();
```

**Generated SQL:**
```sql
WHERE (role = 'admin' OR role = 'moderator')
```

### whereAll - AND Logic

Creates a group where conditions are combined with AND:

```java
// Find verified users over 18
List<User> users = Hefesto.make(User.class)
    .whereAll(group -> {
        group.where("age", 18, Operator.GREATER);
        group.where("verified", true);
    })
    .get();
```

**Generated SQL:**
```sql
WHERE (age > 18 AND verified = true)
```

## Combining with Regular WHERE

Groups connect to previous conditions with AND by default:

```java
List<User> users = Hefesto.make(User.class)
    .where("id", 0, Operator.GREATER)
    .whereAny(group -> {
        group.where("status", "active");
        group.where("status", "pending");
    })
    .whereIsNotNull("email")
    .get();
```

**Generated SQL:**
```sql
WHERE id > 0 
  AND (status = 'active' OR status = 'pending')
  AND email IS NOT NULL
```

## Nested Groups

Create complex logic by nesting groups within groups:

### AND Group Inside OR Group

```java
List<User> users = Hefesto.make(User.class)
    .whereAny(outer -> {
        outer.where("role", "admin");
        outer.whereAll(inner -> {
            inner.where("role", "user");
            inner.where("verified", true);
        });
    })
    .get();
```

**Generated SQL:**
```sql
WHERE (role = 'admin' OR (role = 'user' AND verified = true))
```

### OR Group Inside AND Group

```java
List<User> users = Hefesto.make(User.class)
    .whereAll(outer -> {
        outer.whereIsNotNull("email");
        outer.whereAny(inner -> {
            inner.where("name", "John");
            inner.where("name", "Jane");
        });
    })
    .get();
```

**Generated SQL:**
```sql
WHERE (email IS NOT NULL AND (name = 'John' OR name = 'Jane'))
```

### Deep Nesting

```java
List<User> users = Hefesto.make(User.class)
    .whereAny(level1 -> {
        level1.where("role", "admin");
        level1.whereAll(level2 -> {
            level2.where("age", 18, Operator.GREATER);
            level2.whereAny(level3 -> {
                level3.where("status", "active");
                level3.where("status", "verified");
            });
        });
    })
    .get();
```

**Generated SQL:**
```sql
WHERE (
  role = 'admin' 
  OR (
    age > 18 
    AND (status = 'active' OR status = 'verified')
  )
)
```

## OR Connection Methods

Use `orWhereAny` and `orWhereAll` to connect groups with OR instead of AND:

### orWhereAny

```java
List<User> users = Hefesto.make(User.class)
    .where("id", 100, Operator.GREATER)
    .orWhereAny(group -> {
        group.where("name", "John");
        group.where("name", "Jane");
    })
    .get();
```

**Generated SQL:**
```sql
WHERE id > 100 OR (name = 'John' OR name = 'Jane')
```

### orWhereAll

```java
List<User> users = Hefesto.make(User.class)
    .where("country", "US")
    .orWhereAll(group -> {
        group.where("age", 18, Operator.GREATER);
        group.where("verified", true);
    })
    .get();
```

**Generated SQL:**
```sql
WHERE country = 'US' OR (age > 18 AND verified = true)
```

## Available Methods Inside Groups

### Basic WHERE

```java
.whereAny(group -> {
    group.where("field", value);                          // field = value
    group.where("field", value, Operator.GREATER);         // field > value
    group.where("field", value, Operator.LIKE);            // field LIKE value
})
```

### Null Checks

```java
.whereAny(group -> {
    group.whereIsNull("field");
    group.whereIsNotNull("field");
})
```

### IN / NOT IN

```java
.whereAny(group -> {
    // Varargs
    group.whereIn("name", "John", "Jane", "Bob");
    group.whereNotIn("status", "deleted", "banned");
    
    // Iterable
    List<String> names = Arrays.asList("John", "Jane");
    group.whereIn("name", names);
    group.whereNotIn("status", excludedStatuses);
})
```

### Type-Safe References

```java
// Java with JPA Metamodel
.whereAny(group -> {
    group.where(User_.name, "John");
    group.whereIsNotNull(User_.email);
    group.whereIn(User_.status, Status.ACTIVE, Status.PENDING);
})

// Kotlin with property references
.whereAny { group ->
    group.where(User::name, "John")
    group.whereIsNotNull(User::email)
    group.whereIn(User::status, Status.ACTIVE, Status.PENDING)
}
```

### Nested Groups

```java
.whereAny(outer -> {
    outer.where("field1", value1);
    outer.whereAll(inner -> {
        inner.where("field2", value2);
        inner.where("field3", value3);
    });
})
```

## Real-World Examples

### User Search with Multiple Criteria

```java
public List<User> searchUsers(String searchTerm, List<String> roles, Integer minAge) {
    var query = Hefesto.make(User.class);
    
    // Base conditions
    query.whereIsNotNull("email");
    
    // Search in name OR email
    if (searchTerm != null) {
        query.whereAny(group -> {
            group.where("name", "%" + searchTerm + "%", Operator.LIKE);
            group.where("email", "%" + searchTerm + "%", Operator.LIKE);
        });
    }
    
    // Role filter
    if (roles != null && !roles.isEmpty()) {
        query.whereIn("role", roles);
    }
    
    // Age filter
    if (minAge != null) {
        query.where("age", minAge, Operator.GREATER_OR_EQUAL);
    }
    
    return query.orderBy("name").get();
}
```

### E-commerce Product Filter

```java
List<Product> products = Hefesto.make(Product.class)
    .whereAll(group -> {
        // Must be in stock
        group.where("stockQuantity", 0, Operator.GREATER);
        
        // Must be in these categories OR have these tags
        group.whereAny(categoryOrTag -> {
            categoryOrTag.whereIn("category", "electronics", "computers");
            categoryOrTag.whereIn("tags", "featured", "bestseller");
        });
    })
    .where("price", 1000, Operator.LESS_OR_EQUAL)
    .orderBy("price")
    .get();
```

### Social Media Content Moderation

```java
// Find posts that need manual review:
// - Flagged by users OR contains suspicious keywords
// - AND not yet reviewed
// - AND not deleted
List<Post> postsToReview = Hefesto.make(Post.class)
    .whereAll(needsReview -> {
        // Flagged or suspicious
        needsReview.whereAny(flaggedOrSuspicious -> {
            flaggedOrSuspicious.where("flagCount", 3, Operator.GREATER_OR_EQUAL);
            flaggedOrSuspicious.where("text", "%spam%", Operator.LIKE);
            flaggedOrSuspicious.where("text", "%scam%", Operator.LIKE);
        });
        
        // Not yet processed
        needsReview.whereIsNull("reviewedAt");
        needsReview.whereIsNull("deletedAt");
    })
    .orderBy("flagCount", "DESC")
    .get();
```

### Banking Transaction Filter

```java
// Find suspicious transactions:
// (Large amount OR foreign country) AND (unusual time OR first transaction)
List<Transaction> suspicious = Hefesto.make(Transaction.class)
    .whereAll(outer -> {
        // Large or foreign
        outer.whereAny(largeOrForeign -> {
            largeOrForeign.where("amount", 10000, Operator.GREATER);
            largeOrForeign.whereNotIn("country", "US", "CA", "UK");
        });
        
        // Unusual timing or new customer
        outer.whereAny(unusualOrNew -> {
            unusualOrNew.where("hour", 2, Operator.LESS);
            unusualOrNew.where("hour", 22, Operator.GREATER);
            unusualOrNew.where("customer.transactionCount", 1, Operator.EQUALS);
        });
    })
    .where("status", "pending")
    .orderBy("amount", "DESC")
    .get();
```

## Benefits

### 1. Readability

**Complex predicate logic:**
```java
Predicate p1 = cb.equal(root.get("status"), "active");
Predicate p2 = cb.equal(root.get("status"), "pending");
Predicate p3 = cb.gt(root.get("age"), 18);
Predicate p4 = cb.isTrue(root.get("verified"));
Predicate or = cb.or(p1, p2);
Predicate and = cb.and(p3, p4);
cq.where(cb.and(or, and));
```

**Readable groups:**
```java
.whereAny(group -> {
    group.where("status", "active");
    group.where("status", "pending");
})
.whereAll(group -> {
    group.where("age", 18, Operator.GREATER);
    group.where("verified", true);
})
```

### 2. Maintainability

Adding or removing conditions from a group is straightforward:

```java
.whereAny(group -> {
    group.where("role", "admin");
    group.where("role", "moderator");
    group.where("role", "supervisor");  // Easy to add
})
```

### 3. Composability

Groups can be nested indefinitely to match any logical structure.

### 4. Type Safety

When combined with type-safe properties, you get compile-time checking:

```java
.whereAny(group -> {
    group.where(User_.status, Status.ACTIVE);      // Compile-time checked
    group.where(User_.status, Status.PENDING);
})
```

## Common Patterns

### Age Range (Under 18 OR Over 65)

```java
.whereAny(group -> {
    group.where("age", 18, Operator.LESS);
    group.where("age", 65, Operator.GREATER);
})
```

### Multiple Status Values

```java
// Option 1: whereAny
.whereAny(group -> {
    group.where("status", "active");
    group.where("status", "pending");
    group.where("status", "verified");
})

// Option 2: whereIn (more concise for same field)
.whereIn("status", "active", "pending", "verified")
```

**Use whereAny when:**
- Different operators for same field
- Different fields

**Use whereIn when:**
- Same field, equality checks
- Simple value list

### Search Across Multiple Fields

```java
String searchTerm = "%john%";
.whereAny(group -> {
    group.where("firstName", searchTerm, Operator.LIKE);
    group.where("lastName", searchTerm, Operator.LIKE);
    group.where("email", searchTerm, Operator.LIKE);
    group.where("phone", searchTerm, Operator.LIKE);
})
```

### Date Range with Edge Cases

```java
// Find records created this month OR updated today
LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
LocalDate today = LocalDate.now();

.whereAny(group -> {
    group.where("createdAt", startOfMonth, Operator.GREATER_OR_EQUAL);
    group.whereAll(updated -> {
        updated.where("updatedAt", today, Operator.GREATER_OR_EQUAL);
        updated.whereIsNotNull("updatedBy");
    });
})
```

## Best Practices

1. **Keep groups focused** - Each group should represent one logical concept
   ```java
   // Good: Clear intent
   .whereAny(group -> {
       group.where("role", "admin");
       group.where("role", "moderator");
   })
   
   // Avoid: Unrelated conditions in same group
   .whereAny(group -> {
       group.where("role", "admin");
       group.where("age", 18, Operator.GREATER);  // Unrelated
   })
   ```

2. **Name your groups descriptively** in complex scenarios:
   ```java
   .whereAny(adminOrModerator -> {
       adminOrModerator.where("role", "admin");
       adminOrModerator.where("role", "moderator");
   })
   .whereAll(activeAndVerified -> {
       activeAndVerified.where("status", "active");
       activeAndVerified.where("verified", true);
   })
   ```

3. **Prefer whereIn for multiple values on same field**:
   ```java
   // Prefer this
   .whereIn("status", "active", "pending", "verified")
   
   // Over this
   .whereAny(group -> {
       group.where("status", "active");
       group.where("status", "pending");
       group.where("status", "verified");
   })
   ```

4. **Document complex logic** with comments:
   ```java
   // User must be (verified OR premium) AND (active OR recently active)
   .whereAll(outer -> {
       outer.whereAny(group -> {
           group.where("verified", true);
           group.where("subscriptionType", "premium");
       });
       outer.whereAny(group -> {
           group.where("status", "active");
           group.where("lastActiveAt", weekAgo, Operator.GREATER);
       });
   })
   ```

## Troubleshooting

### Empty Groups

Empty groups are ignored:

```java
.whereAny(group -> {
    // Nothing here
})
// This group is skipped, no WHERE clause added
```

### Null Values

Null values are automatically ignored:

```java
.whereAny(group -> {
    group.where("name", null);  // Ignored
    group.where("email", "test@mail.com");  // Applied
})
```

### Unexpected Results

If query returns unexpected results:
1. Add `.countResults()` to check result count
2. Enable SQL logging to see generated query
3. Verify logical operators match intent (AND vs OR)
4. Check nested group structure

## Next Steps

- Explore [Subqueries](SUBQUERIES.md) for advanced filtering
- Learn about [Type-Safe Properties](TYPE_SAFE_PROPERTIES.md) for compile-time safety
- Check out [Deep Joins](DEEP_JOINS.md) for relationship navigation

## API Reference

```java
// AND connection (default)
whereAny(Consumer<WhereGroupContext> block)    // Conditions inside combined with OR
whereAll(Consumer<WhereGroupContext> block)    // Conditions inside combined with AND

// OR connection
orWhereAny(Consumer<WhereGroupContext> block)  // Connects to previous with OR
orWhereAll(Consumer<WhereGroupContext> block)  // Connects to previous with OR
```

### WhereGroupContext Methods

```java
// Basic WHERE
where(String field, Object value)
where(String field, Operator operator, Object value)

// Type-safe WHERE
where(SingularAttribute<T, V> attribute, V value)
where(SingularAttribute<T, V> attribute, Operator operator, V value)
where(KProperty1<T, V> property, V value)
where(KProperty1<T, V> property, Operator operator, V value)

// Null checks
whereIsNull(String field)
whereIsNotNull(String field)

// IN / NOT IN
whereIn(String field, Object... values)
whereIn(String field, Iterable<?> values)
whereNotIn(String field, Object... values)
whereNotIn(String field, Iterable<?> values)

// Nested groups
whereAny(Consumer<WhereGroupContext> block)
whereAll(Consumer<WhereGroupContext> block)
```
