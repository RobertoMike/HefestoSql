# HefestoSQL Documentation

Welcome to the HefestoSQL documentation! This guide will help you master the fluent query API for Hibernate.

## 📚 Documentation Structure

### Getting Started
- **[Getting Started Guide](GETTING_STARTED.md)** - Installation, configuration, and your first queries

### Core Features
- **[Type-Safe Properties](TYPE_SAFE_PROPERTIES.md)** - Compile-time safe property references for Java and Kotlin
- **[Deep Joins](DEEP_JOINS.md)** - Navigate multi-level entity relationships with dot notation
- **[Conditional Groups](CONDITIONAL_GROUPS.md)** - Build complex WHERE clauses with AND/OR logic
- **[Subqueries](SUBQUERIES.md)** - Lambda-based subquery builder with full feature support

### Advanced Topics
- **[Advanced Features](ADVANCED_FEATURES.md)** - Custom DTOs, projections, pagination, dynamic queries, and performance optimization

### Reference & Guides
- **[Migration Guide](MIGRATION.md)** - Upgrading from older versions or migrating from other libraries
- **[Releasing Guide](RELEASING.md)** - For project maintainers (publishing releases)

## 🚀 Quick Navigation

### By Skill Level

**Beginner** → Start here!
1. [Getting Started](GETTING_STARTED.md)
2. [Type-Safe Properties](TYPE_SAFE_PROPERTIES.md)

**Intermediate** → Common use cases
3. [Deep Joins](DEEP_JOINS.md)
4. [Conditional Groups](CONDITIONAL_GROUPS.md)
5. [Subqueries](SUBQUERIES.md)

**Advanced** → Performance & optimization
6. [Advanced Features](ADVANCED_FEATURES.md)

### By Task

**Setting up a new project?**
→ [Getting Started Guide](GETTING_STARTED.md)

**Want compile-time safety?**
→ [Type-Safe Properties](TYPE_SAFE_PROPERTIES.md)

**Working with relationships?**
→ [Deep Joins](DEEP_JOINS.md)

**Building complex filters?**
→ [Conditional Groups](CONDITIONAL_GROUPS.md)

**Need advanced filtering?**
→ [Subqueries](SUBQUERIES.md)

**Optimizing performance?**
→ [Advanced Features](ADVANCED_FEATURES.md)

**Migrating or upgrading?**
→ [Migration Guide](MIGRATION.md)

## 📖 Reading Guide

### Complete Walkthrough (Recommended for new users)

Follow this order to learn HefestoSQL systematically:

1. **[Getting Started](GETTING_STARTED.md)** (30 min)
   - Installation and configuration
   - Entity setup
   - Basic queries (WHERE, ORDER BY, LIMIT)
   - Simple joins and aggregations

2. **[Type-Safe Properties](TYPE_SAFE_PROPERTIES.md)** (20 min)
   - JPA Metamodel setup (Java)
   - Kotlin property references
   - Benefits and usage patterns

3. **[Deep Joins](DEEP_JOINS.md)** (15 min)
   - Multi-level relationship navigation
   - Dot notation syntax
   - Performance considerations

4. **[Conditional Groups](CONDITIONAL_GROUPS.md)** (20 min)
   - whereAny and whereAll
   - Building complex AND/OR logic
   - Nested groups

5. **[Subqueries](SUBQUERIES.md)** (25 min)
   - WHERE IN/NOT IN with subqueries
   - EXISTS/NOT EXISTS patterns
   - Correlated subqueries

6. **[Advanced Features](ADVANCED_FEATURES.md)** (30 min)
   - Custom DTOs and projections
   - Pagination strategies
   - Dynamic queries
   - Performance optimization

**Total time:** ~2.5 hours for complete mastery

### Quick Reference (For experienced users)

If you're already familiar with query builders, jump to specific topics:

- **Type safety?** → [Type-Safe Properties](TYPE_SAFE_PROPERTIES.md#api-reference)
- **Complex joins?** → [Deep Joins](DEEP_JOINS.md#api-reference)
- **Complex WHERE?** → [Conditional Groups](CONDITIONAL_GROUPS.md#api-reference)
- **Subqueries?** → [Subqueries](SUBQUERIES.md#api-reference)
- **Performance?** → [Advanced Features](ADVANCED_FEATURES.md#performance-optimization)

## 🎯 Feature Matrix

Quick reference of what each guide covers:

| Feature | Getting Started | Type-Safe | Deep Joins | Conditional | Subqueries | Advanced |
|---------|----------------|-----------|------------|-------------|------------|----------|
| Basic WHERE | ✅ | ✅ | | | | |
| JOIN | ✅ | | ✅ | | | |
| ORDER BY | ✅ | ✅ | | | | |
| Aggregations | ✅ | | | | | ✅ |
| Pagination | ✅ | | | | | ✅ |
| JPA Metamodel | | ✅ | | | | |
| Kotlin KProperty1 | | ✅ | | | | |
| Dot notation joins | | | ✅ | | | |
| whereAny/whereAll | | | | ✅ | | |
| Nested groups | | | | ✅ | | |
| WHERE IN subquery | | | | | ✅ | |
| WHERE EXISTS | | | | | ✅ | |
| DTOs/Projections | | | | | | ✅ |
| Dynamic queries | | | | | | ✅ |
| Performance tuning | | | | | | ✅ |

## 💡 Tips for Learning

### For Java Developers
1. Start with [Getting Started](GETTING_STARTED.md)
2. Set up [JPA Metamodel](TYPE_SAFE_PROPERTIES.md#java-jpa-metamodel) for type safety
3. Practice with [real-world examples](ADVANCED_FEATURES.md#real-world-examples)

### For Kotlin Developers
1. Start with [Getting Started](GETTING_STARTED.md)
2. Use [Kotlin property references](TYPE_SAFE_PROPERTIES.md#kotlin-property-references)
3. Leverage lambda syntax for [conditional groups](CONDITIONAL_GROUPS.md) and [subqueries](SUBQUERIES.md)

### For Database-Heavy Applications
Focus on:
- [Pagination](ADVANCED_FEATURES.md#pagination)
- [Performance optimization](ADVANCED_FEATURES.md#performance-optimization)
- [DTOs over entities](ADVANCED_FEATURES.md#custom-dtos-and-projections)
- [N+1 prevention](ADVANCED_FEATURES.md#n1-query-prevention)

### For Microservices
Focus on:
- [Dynamic queries](ADVANCED_FEATURES.md#dynamic-queries)
- [Projections](ADVANCED_FEATURES.md#custom-dtos-and-projections)
- [Pagination](ADVANCED_FEATURES.md#pagination)
- [Query caching](ADVANCED_FEATURES.md#caching-strategies)

## 🔗 External Resources

- **[Main README](../README.md)** - Project overview and quick examples
- **[Complete Documentation](../DOCUMENTATION.md)** - Legacy comprehensive guide
- **[Benchmarks](../benchmarks/README.md)** - Performance comparisons
- **[GitHub Repository](https://github.com/RobertoMike/HefestoSql)** - Source code and issue tracker

## 🤝 Contributing to Documentation

Found an error or want to improve the docs? We welcome contributions!

1. Fork the repository
2. Make your changes to the markdown files in `docs/`
3. Submit a pull request

See [Contributing Guide](../CONTRIBUTING.md) for details.

## 📝 Documentation Conventions

Throughout these guides:
- `✅` marks best practices
- `❌` marks anti-patterns to avoid
- `⚠️` marks important warnings
- Code examples are runnable and tested
- Java examples use verbose syntax for clarity
- Kotlin examples demonstrate idiomatic syntax

## 🆘 Need Help?

- **Questions?** Open a [GitHub Discussion](https://github.com/RobertoMike/HefestoSql/discussions)
- **Bug reports?** Open a [GitHub Issue](https://github.com/RobertoMike/HefestoSql/issues)
- **Feature requests?** Open a [GitHub Issue](https://github.com/RobertoMike/HefestoSql/issues) with the "enhancement" label

---

**Happy querying! 🔥**
