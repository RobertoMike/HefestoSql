# Hefesto Benchmarks

This module contains performance benchmarks for HefestoSQL using JMH (Java Microbenchmark Harness).

## Overview

The benchmarks compare:
- **Hefesto Criteria Builder** vs **Raw Hibernate Criteria API**
- Query building and execution performance
- Different query complexities (simple, joins, complex conditions)

## Running Benchmarks

### Run all benchmarks:
```bash
./gradlew :hefesto-benchmarks:jmh
```

### Run specific benchmark:
```bash
./gradlew :hefesto-benchmarks:jmh -Pjmh.includes=".*CriteriaBuilderBenchmark.*"
```

### Run with custom parameters:
```bash
./gradlew :hefesto-benchmarks:jmh \
  -Pjmh.iterations=5 \
  -Pjmh.warmupIterations=3 \
  -Pjmh.fork=2
```

## Benchmark Categories

### 1. Simple Queries
- Basic SELECT * operations
- No WHERE conditions
- Measures baseline query building overhead

### 2. WHERE Queries
- Queries with multiple WHERE conditions
- Tests operator handling and predicate building

### 3. JOIN Queries
- Queries with relationship joins
- Tests join construction performance

### 4. Complex Queries
- Multiple joins + WHERE conditions + ORDER BY + LIMIT
- Tests overall system performance under realistic load

### 5. COUNT Queries
- Aggregate function queries
- Tests query transformation and execution

## Results Interpretation

Results are in **milliseconds** (average time per operation):
- **Lower is better**
- Focus on relative performance (Hefesto vs Raw Hibernate)
- Absolute times depend on hardware

## Test Data

Each benchmark uses:
- 1,000 Users
- 5,000 Posts (5 per user)
- 50,000 Comments (10 per post)
- H2 in-memory database

## Extending Benchmarks

To add new benchmarks:

1. Create a new class extending `BaseBenchmark`
2. Annotate methods with `@Benchmark`
3. Use `@State(Scope.Benchmark)` for state management
4. Follow JMH best practices

Example:
```java
@State(Scope.Benchmark)
public class MyBenchmark extends BaseBenchmark {
    
    @Override
    protected void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(MyEntity.class);
    }
    
    @Override
    protected void populateTestData(Session session) {
        // Populate test data
    }
    
    @Benchmark
    public List<MyEntity> myBenchmark() {
        // Benchmark code
    }
}
```

## Performance Tips

1. **Warmup is important**: JVM needs time to optimize
2. **Run multiple iterations**: Reduces variance
3. **Fork multiple JVMs**: Isolates benchmark runs
4. **Use realistic data**: Matches production scenarios

## CI/CD Integration

Add to CI pipeline:
```yaml
- name: Run Benchmarks
  run: ./gradlew :hefesto-benchmarks:jmh
  
- name: Upload Results
  uses: actions/upload-artifact@v2
  with:
    name: benchmark-results
    path: benchmarks/build/results/jmh/results.json
```

## Troubleshooting

### Out of Memory
Increase JVM heap:
```bash
export GRADLE_OPTS="-Xmx4g"
./gradlew :hefesto-benchmarks:jmh
```

### Slow Execution
Reduce iterations:
```bash
./gradlew :hefesto-benchmarks:jmh \
  -Pjmh.iterations=1 \
  -Pjmh.warmupIterations=1
```
