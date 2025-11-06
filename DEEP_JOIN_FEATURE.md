# Deep Join Feature

## Overview

The Deep Join feature allows you to create nested join relationships in a simple and intuitive way. This feature is available in HefestoSql and provides two distinct approaches for creating deep joins:

1. **Dot Notation** - Simple string-based path
2. **Fluent API** - Method chaining for programmatic construction

Both approaches create identical internal structures and produce the same SQL joins.

## Use Cases

Deep joins are useful when you need to:
- Navigate through multiple relationship levels (e.g., `User -> Pet -> Owner -> Address`)
- Build complex queries with nested joins
- Avoid repetitive join declarations
- Create readable, maintainable query code

## API Options

### 1. Dot Notation (Simple)

The simplest way to create deep joins is using a dot-separated path string:

```java
// Basic usage - all INNER joins
Hefesto<User> query = Hefesto.table(User.class, session)
    .joinDeep("pets.owner.address");

// With JOIN operator
Hefesto<User> query = Hefesto.table(User.class, session)
    .joinDeep("pets.owner.address", JoinOperator.LEFT);

// With alias for the final join
Hefesto<User> query = Hefesto.table(User.class, session)
    .joinDeep("pets.owner.address", "addr", JoinOperator.LEFT);
```

**Dot Notation creates:**
- User (root)
  - → pets (INNER join)
    - → owner (INNER join)
      - → address (LEFT join, aliased as "addr")

### 2. Fluent API (Programmatic)

For more control and programmatic construction, use the fluent API:

```java
// Create initial join
Join join = Join.make("pets");

// Add deep nested joins
join.withDeep("owner")      // Returns the new "owner" join
    .withDeep("address");   // Returns the new "address" join

// Add to query
Hefesto<User> query = Hefesto.table(User.class, session)
    .join(join);
```

**With operators and aliases:**

```java
Join join = Join.make("pets", JoinOperator.LEFT);

// withDeep returns the newly created join, allowing chaining
join.withDeep("owner", JoinOperator.LEFT)
    .withDeep("address", "addr", JoinOperator.LEFT);

Hefesto<User> query = Hefesto.table(User.class, session)
    .join(join);
```

**Multiple branches from same parent:**

```java
Join storeJoin = Join.make("store");

// Branch 1: store -> users -> brands
storeJoin.withDeep("users")
         .withDeep("brands");

// Branch 2: store -> products
storeJoin.withDeep("products");

// Results in:
// store
//   ├── users -> brands
//   └── products
```

## Equivalence

These two approaches are equivalent:

```java
// Dot notation
hefesto.joinDeep("pets.owner.address");

// Fluent API
Join join = Join.make("pets");
join.withDeep("owner")
    .withDeep("address");
hefesto.join(join);
```

## Static Factory Methods

You can also use static factory methods for immediate deep join creation:

```java
// Create deep join from path
Join deepJoin = Join.makeDeep("pets.owner.address");

// With alias
Join deepJoin = Join.makeDeep("pets.owner.address", "addr");

// With operator
Join deepJoin = Join.makeDeep("pets.owner.address", JoinOperator.LEFT);

// With both alias and operator
Join deepJoin = Join.makeDeep("pets.owner.address", "addr", JoinOperator.LEFT);
```

## Combined Usage

You can combine both approaches in the same query:

```java
Hefesto<User> query = Hefesto.table(User.class, session)
    // Dot notation for simple path
    .joinDeep("pets.owner")
    
    // Fluent API for complex branching
    .join(Join.make("addresses")
        .withDeep("city")
        .withDeep("country"))
    
    // Regular join
    .join("orders")
    
    // Add conditions
    .where("name", Operation.LIKE, "%John%");
```

## Implementation Details

### Join Structure

The `Join` class contains:
- `table`: The table/entity to join
- `fieldJoin`: The relationship field (for relationship joins)
- `alias`: Optional alias for the join
- `operator`: Join type (INNER, LEFT, RIGHT)
- `deepJoins`: Mutable list of nested joins

### Processing

The `ConstructJoinImplementation` class recursively processes joins:

1. Iterates through top-level joins
2. For each join, creates JPA Criteria API join
3. If the join has `deepJoins`, recursively processes each nested join
4. Stores all joins by alias in a map for WHERE clause references

### Helper Methods

The `Join` class provides helper methods:
- `hasDeepJoins()`: Returns true if join has nested joins
- `isCustomJoin()`: Returns true if join uses ON clause (fieldJoin is null)

## Testing

The feature includes comprehensive test coverage:

### Unit Tests (DeepJoinTest.java)
- 13 tests covering API usage, structure creation, and equivalence
- All tests passing ✅

### Integration Tests (DeepJoinIntegrationTest.java)
- 20 tests with real Hibernate queries
- Tests cover: simple joins, WHERE clauses, operators, count, pagination
- Both structure validation and database execution tests
- All tests passing ✅

## Examples

### Example 1: Simple Deep Join with WHERE

```java
List<User> users = Hefesto.table(User.class, session)
    .joinDeep("pets.owner.address", JoinOperator.LEFT)
    .where("pets.name", Operation.EQUAL, "Max")
    .where("address.city", Operation.EQUAL, "New York")
    .get();
```

### Example 2: Fluent API with Multiple Branches

```java
Join storeJoin = Join.make("store");

// First branch: store -> users -> brands
storeJoin.withDeep("users")
         .withDeep("brands");

// Second branch: store -> products -> categories
storeJoin.withDeep("products")
         .withDeep("categories");

List<Order> orders = Hefesto.table(Order.class, session)
    .join(storeJoin)
    .where("brands.name", Operation.LIKE, "%Premium%")
    .get();
```

### Example 3: Count with Deep Join

```java
Long count = Hefesto.table(User.class, session)
    .joinDeep("pets.owner", JoinOperator.LEFT)
    .where("owner.name", Operation.EQUAL, "John")
    .countResults();
```

### Example 4: Pagination with Deep Join

```java
List<User> users = Hefesto.table(User.class, session)
    .joinDeep("addresses.city.country")
    .where("country.name", Operation.EQUAL, "USA")
    .limit(10)
    .offset(20)
    .get();
```

## Benefits

1. **Readability**: Clear, concise syntax for complex joins
2. **Maintainability**: Easy to modify join paths
3. **Flexibility**: Choose between dot notation and fluent API
4. **Type Safety**: Compile-time checking (when using constants)
5. **Consistency**: Same result regardless of approach used

## Migration Guide

If you have existing code with manual nested joins:

**Before:**
```java
Join pets = Join.make("pets");
Join owner = Join.make("owner");
Join address = Join.make("address");

pets.getDeepJoins().add(owner);
owner.getDeepJoins().add(address);

hefesto.join(pets);
```

**After (Dot Notation):**
```java
hefesto.joinDeep("pets.owner.address");
```

**After (Fluent API):**
```java
hefesto.join(Join.make("pets")
    .withDeep("owner")
    .withDeep("address"));
```

## Notes

- Deep joins work with both relationship joins and custom joins
- The operator applies to each level unless specified differently in fluent API
- Aliases can only be set on the final join when using dot notation
- For branching joins, use the fluent API
- The fluent API's `withDeep()` returns the newly created join, not the parent

## Conclusion

The Deep Join feature significantly simplifies the creation of complex nested joins in HefestoSql. Choose the approach that best fits your use case:
- **Dot notation** for simple, linear join paths
- **Fluent API** for programmatic construction and branching structures
