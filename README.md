# Welcome to HefestoSql

This library is for simplify the hibernate Criteria Builder, made it more simple and easy.

## Links
- [Example of use](#example-of-use)
- [Configuration](#first-configuration)
- [How to install it](#how-to-install)
- [Difference between Hibernate and Hefesto](#some-differences)
- [Operations for where](#what-are-the-supported-operators)
- [Operations for select](#can-i-select-only-some-fields)
- [Alias class for custom result](#can-i-use-other-class-that-are-not-the-hibernate-model-for-receiving-the-data)
- [Methods to get data](#what-methods-can-i-use-to-get-data)

## Example of use

It needs to be used with a model of Hibernate and this model need to implement the interface HibernateModel or BaseModel

```java
public class example {
    public List<Model> method() {
        return Hefesto.make(Model.class).get();
    }
}
```

## How to install
Actually there are 2 versions:
1) For Hibernate 6 (version: 2.1.1)
2) For Hibernate 5 (version: 1.1.1)

### Maven installation
```xml
<dependency>
    <groupId>io.github.robertomike</groupId>
    <artifactId>hefesto-hibernate</artifactId>
    <version>1.1.1</version>
</dependency>
```

### Gradle installation
```gradle
dependencies {
    implementation 'io.github.robertomike:hefesto-hibernate:1.1.1'
}
```

## First configuration

For HefestoSql work, you need to set the session of Hibernate inside HefestoSql.

If you are using spring boot 2 or 3 you can skip this step.

Example:

```java

import org.hibernate.Session;

public class ConfigClass {
    public void config(Session session) {
        return Hefesto.setSession(session);
    }
}
```

## Some differences

Difference write code with HefestoSql and Hibernate Criteria Builder

<table>
<tr>
    <th>Hibernate Criteria Builder</th>
    <th>HefestoSql</th>
</tr>
<tr>
<td>

```java
public class Example {
    public void example(Session session) {
        var cb = session.getCriteriaBuilder();
        var cr = cb.createQuery(Model.class);
        var root = cr.from(Model.class);
        
        var result = session.createQuery(cr).getResultList();
    }
}
```
</td>
<td>

```java
public class Example {
    public void example() {
        var result = Hefesto.make(Model.class).get();
    }
}
```
</td>
</tr>

<tr>
<td>

```java
import jakarta.persistence.criteria.JoinType;

public class Example {
    public void example(Session session) {
        var cb = session.getCriteriaBuilder();
        var cr = cb.createQuery(Model.class);
        var root = cr.from(Model.class);
        
        var join = root.join("field", JoinType.INNER);
        
        cr.where(cb.or(cb.equal(join.get("id"), 1L), cb.like(root.get("name"), "%name%")));
        cr.orderBy(cb.asc(root.get("id")), cb.desc(join.get("name")));
        
        var result = session.createQuery(cr).getResultList();
    }
}
```
</td>
<td>

```java
package io.github.robertomike.enums.Operator;

public class Example {
    public void example() {
        var result = Hefesto.make(Model.class)
                .join("field")
                .where("field.id", 1)
                .orWhere("name", Operator.LIKE, "%name%")
                .orderBy("id", "field.name")
                .get();
    }
}
```
</td>
</tr>
</table>

## What are the supported operators?

- EQUAL("=")
- DIFF("<>")
- LESS_OR_EQUAL("<=")
- LESS("<")
- GREATER_OR_EQUAL(">=")
- GREATER(">")
- IN("in")
- LIKE("like")
- NOT_LIKE("not_like")
- NOT_IN("not in")
- IS_NULL("is null")
- IS_NOT_NULL("is not null")
- FIND_IN_SET("find_in_set")
- NOT_FIND_IN_SET("find_in_set")

### Can I do a sub-query?

Yes you can use a sub-query, the methods that support sub-query are:

- whereIn
- orWhereIn
- whereNotIn
- orWhereNotIn
- whereExists
- whereNotExists
- orWhereExists
- orWhereNotExists

When use sub-query with an in operation you need to specify the return value for the query, 
for that you need to call the method 'setCustomResultForSubQuery'.
If you don't use this method Hefesto will throw and exception.

### What can I do if I need something more specific?

You can use the method whereCustom, this method receive a lambda method, 
in this lambda method you receive:

- CriteriaBuilder cb
- CriteriaQuery<?> cr
- Root<?> root
- Map<String, Join<?, ?>> joins (these are all the joins, if you are inside sub-query you receive parents joins and sub-query joins )
- Root<?> parentRoot (if you are inside a sub-query you can use this variable for access to parent root)

## Can I select only some fields?

Yes, you can, there is the method select for that, you can select also join fields.
Here some examples:

```java
package io.github.robertomike.builders.Hefesto;

public class Example {
    public void example() {
        var result = Hefesto.make(Model.class)
                .join("field")
                // This method set all this selects (erase the last selects)
                .select("id", "name", "field.name")
                // The second parameter is an alias
                .addSelect("email", "emailCompany")
                .get();
    }
}
```

### Can I use functions on select? How I count the results?

Here is an example of how can you do both

```java
package io.github.robertomike.builders.Hefesto;

public class Example {
    public void example() {
        Long total = Hefesto.make(Model.class)
                // The second parameter is an alias
                .addSelect("id", SelectOperator.COUNT)
                .findFirstFor(Long.class);
    }
}
```

### What functions are available for select?

For now these are the supported operators 

- COUNT
- AVG
- MIN
- MAX
- SUM

## Can I use other class that are not the Hibernate Model for receiving the data?

Yes, you can, is very easy.

```java
package io.github.robertomike.builders.Hefesto;

public class Example {
    public void example() {
        // The first is the hibernate model and the second is the alias class 
        Optional<UserNameWithPetName> result = new Hefesto<>(User.class, UserNameWithPetName.class)
                // You need to select what do you want for the alias class
                .addSelect("name", "userName")
                .join("pets")
                // This select is not necessary to be after the join, because the query is created when call one of the methods to get data
                .addSelect("pets.name", "petName")
                // This add a where(id, 2) and return the result
                .findFirstById(2);
    }
}
```

## What methods can I use to get data?

There is many methods:
- findFirstById (only one parameter): Return an optional object
- findFirstBy (field, operator, value): Return an optional object
- findFirst (): Return an optional object
- get(): Return a list of objects without limits
- page (limit, offset): Return a page object with inside total, data and page
- countResults(): Return a long object
- findFor(Class<?>): Return a list object of the passed class
- findFirstFor(Class<?>): Return an object of the passed class


[![coffee](./buy-me-coffee.png)](https://www.buymeacoffee.com/robertomike)
