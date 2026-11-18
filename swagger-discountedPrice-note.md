# Why `GET /products` Shows `"discountedPrice"` in Swagger UI

When you call `GET /products` in Swagger UI, Spring Boot converts your Java objects into **JSON** using a built-in library called **Jackson**.

## 1. Jackson turns every `public get...()` method into a JSON field
Jackson does **not** just read your `private` fields—it looks at **every `public` getter method** on the object, removes `"get"`, and lowercases the first letter:

- `getId()` → `"id"`
- `getName()` → `"name"`
- `getPrice()` → `"price"`
- `getSummary()` → `"summary"`
- `getDiscountedPrice()` → `"discountedPrice"`

## 2. Why it appears even though `GET /products` returns `List<Product>`
In `ProductController.java`, `getAllProducts()` returns `List<Product>`, **but** the actual objects stored inside that list (in `ProductService.java`) are `DigitalProduct` and `PhysicalProduct`.

At runtime, Jackson inspects the **real object** (`DigitalProduct` or `PhysicalProduct`), sees `getDiscountedPrice()` (implemented from `Discountable`), and automatically includes `"discountedPrice"` in the JSON response.

*(If it shows `0.0`, that is because `applyDiscount(...)` hasn't been called on those products yet, and a Java `double` field defaults to `0.0`.)*

## Tip
If you ever write a `get...()` method in Spring Boot that you do **not** want included in the JSON output, put `@JsonIgnore` above that method. In this tutorial, seeing `"discountedPrice"` (and `"summary"`) in Swagger UI is expected.
