# Tutorial 5 Notes — Polymorphism & Abstraction (`5COSC019W`)

> **Source Guide:** `notes1/Tutorial5_OOP_Lab_Guide_Student.pdf`
> **Workspace:** `tutorial/springboot` (`uk.ac.westminster.productsapi`)
> **Status:** Updated with current progress through **Activity 1** (and notes/checklists ready for **Activities 2–7**).

---

## Core Mental Model

- **Reference type (compile time):** The declared type on the **left** of `=`. Decides which methods you are **allowed** to call.
- **Object type (runtime):** What was actually created with `new` on the **right** of `=`. Decides which overridden implementation **actually runs** (runtime dispatch / polymorphism).
- **`@Override`:** Compiler check that a method truly matches a parent or interface signature. Without it, a typo (e.g. `getsummary()`) silently creates a brand-new method instead of overriding.
- **Why JSON gets new fields automatically:** Spring Boot uses **Jackson**, which serialises objects using **public `get...()` methods**, not private fields. Adding `public String getSummary()` automatically produces a `"summary"` field in JSON, and `getDiscountedPrice()` produces `"discountedPrice"`.
- **`abstract class` vs `interface`:**
  - `extends` (**IS-A**): A class can extend only **one** parent class (`Product`). Making `Product` `abstract` stops `new Product(...)` and lets `abstract` methods force subclasses to provide implementations.
  - `implements` (**CAN-DO**): A class can implement **many** interfaces (`Discountable`). An interface defines a contract (method signatures, no instance fields, no constructors).

---

## Activity 0 — Predict the Output (Answer Key & Notes)

Given:
```java
Product        a = new Product(1L, "Mug", 8.50);
Product        b = new DigitalProduct(2L, "E-book", 9.99, "/dl/2", 4.2);
DigitalProduct c = new DigitalProduct(3L, "Audiobook", 14.99, "/dl/3", 92.0);
```

| # | Statement | Compiles? | Which version runs — or why not? |
| --- | --- | --- | --- |
| 1 | `a.getSummary()` | Yes | `Product`'s `getSummary()` runs (`a` is declared `Product` and created as `new Product(...)`). |
| 2 | `b.getSummary()` | Yes | `DigitalProduct`'s `getSummary()` runs. `Product` has `getSummary()` (so it compiles), and the actual object is `DigitalProduct` (runtime dispatch). |
| 3 | `c.getSummary()` | Yes | `DigitalProduct`'s `getSummary()` runs (`c` is both declared and created as `DigitalProduct`). |
| 4 | `b.getDownloadUrl()` | No | `Product` (the reference type on the left) does not declare `getDownloadUrl()`. |
| 5 | `c.getDownloadUrl()` | Yes | `DigitalProduct`'s `getDownloadUrl()` runs (`c` is declared as `DigitalProduct`). |
| 6 | `a.getDownloadUrl()` | No | `Product` does not declare `getDownloadUrl()`. |
| 7 | `((DigitalProduct) b).getDownloadUrl()` | Yes | Compiles because of the cast, and succeeds at runtime because `b` actually holds a `DigitalProduct` object. `DigitalProduct.getDownloadUrl()` runs. |
| 8 | `((DigitalProduct) a).getDownloadUrl()` | Yes | Compiles because the compiler trusts the explicit cast, **but crashes at runtime with `ClassCastException`** because `a` actually holds a `Product`, not a `DigitalProduct`. |

---

## Progress Tracker (Current Repo State)

### Activity 1 — Give Every Product a Voice (`getSummary()`)
- [x] **Task 3 (`Product.java`):** Added `public String getSummary() { return name + "- $" + price; }`
- [x] **Task 4 (`DigitalProduct.java`):** Overrode `getSummary()` with `@Override`:
  ```java
  @Override
  public String getSummary(){
      return "Digital" + getName() + "|Download"+ downloadUrl;
  }
  ```
  *(Note: you may want spaces around `"Digital: " + getName() + " | Download: " + downloadUrl` for cleaner output, and note `getName()` is required instead of `name` because `name` is `private` in `Product`.)*
- [ ] **Task 5 (Test `GET /products`):** Restart app and verify the `"summary"` field appears in JSON.
  - **Note for Task 5:** Jackson saw `getSummary()` and generated a `"summary"` JSON property automatically; `DigitalProduct` returns the overridden summary while `PhysicalProduct` currently falls back to `Product`'s generic summary.
- [ ] **Task 6 (Break it on purpose):** Rename `getSummary` to `getsummary` with `@Override` (compiler catches it) vs without `@Override` (compiles silently, bug causes fallback to `Product.getSummary()`), then undo.

---

### Activity 2 — Second Override on Your Own (`PhysicalProduct.java`)
- [ ] **Task 7:** Add `@Override public String getSummary()` in `PhysicalProduct.java` including `getName()` and `weightKg` (or `getWeightKg()`).
  - **Heads-up in `PhysicalProduct.java`:** Line 11 currently has `this.weightKg = weightKg;` instead of `this.weightKg = weight;` (the parameter is named `weight`), so `weightKg` stays `0.0` unless fixed to `this.weightKg = weight;`.
- [ ] **Task 8:** Restart and call `GET /products` to verify `DigitalProduct` and `PhysicalProduct` produce visibly different `"summary"` strings.
- [ ] **Task 9 (Annotation experiment):** Remove `@Override` temporarily — it still runs, because `@Override` is a compile-time safety check that catches typos or signature mismatches (which would otherwise become overloads or unrelated methods). Put `@Override` back.

---

### Activity 3 — Make `Product` Abstract
- [ ] **Task 10:** Change `public class Product` to `public abstract class Product`.
  - Keep `Product`'s constructors — subclasses still call `super(...)`.
  - Ensure no `new Product(...)` remains anywhere in the codebase (`ProductService` seed data already uses `DigitalProduct` and `PhysicalProduct`).
- [ ] **Task 11:** Fix `POST /products` in `ProductController.java`.
  - Because Jackson cannot instantiate an `abstract` `Product` from `@RequestBody Product`, temporarily change the parameter to `@RequestBody DigitalProduct` and add a one-line comment explaining why.
- [ ] **Task 12:** Change `getSummary()` in `Product.java` to:
  ```java
  public abstract String getSummary();
  ```
  - Every concrete subclass is now forced by the compiler to implement `getSummary()`.

---

### Activity 4 — Build the `Discountable` Interface
- [ ] **Task 13:** Create `Discountable.java` in `uk.ac.westminster.productsapi`:
  ```java
  public interface Discountable {
      void applyDiscount(double rate);
      double getDiscountedPrice();
  }
  ```
- [ ] **Task 14 (`DigitalProduct.java`):** `implements Discountable`, add `private double discountedPrice;`, implement `applyDiscount(double rate)` using full rate (`getPrice() * (1 - rate)`), and return `discountedPrice` in `getDiscountedPrice()`.
- [ ] **Task 15 (`PhysicalProduct.java`):** `implements Discountable`, add `private double discountedPrice;`, cap discount at 20%:
  ```java
  double capped = Math.min(rate, 0.20);
  this.discountedPrice = getPrice() * (1 - capped);
  ```
- [ ] **Task 16:** Restart and call `GET /products`.
  - **Why `"discountedPrice"` appears in JSON:** Same Jackson getter rule as `getSummary()` — `getDiscountedPrice()` exposes `"discountedPrice"` (initially `0.0` until `applyDiscount(...)` is called, unless initialised to `price` in the constructor).
  - **Why both classes compile with different bodies:** An interface fixes the **method signatures** (the contract), not the implementation body.

---

### Activity 5 — `GET /products/summaries` & Casting Experiment
- [ ] **Task 17 (`ProductService.java`):** Add `public List<String> getAllSummaries()` looping `for (Product p : products)` and adding `p.getSummary()` (no `instanceof`, no casts).
- [ ] **Task 18 (`ProductController.java`):** Add `@GetMapping("/products/summaries")` calling `productService.getAllSummaries()`.
- [ ] **Task 19 (Casting experiment):**
  - Temporarily try `p.applyDiscount(0.10);` inside the `Product p` loop -> fails to compile because `Product` (the reference type) does not implement `Discountable`.
  - Pattern-matching `if (p instanceof Discountable d) { d.applyDiscount(0.10); }` compiles because it checks capability at runtime and binds `d` as `Discountable`.
  - Remove both test snippets afterwards.

---

### Activity 6 — Update UML (`docs/uml/product-hierarchy.puml`)
- [ ] **Task 20:** Change `class Product` to `abstract class Product` and mark `+ {abstract} getSummary() : String`.
- [ ] **Task 21:** Add `interface Discountable` with `applyDiscount` and `getDiscountedPrice`.
- [ ] **Task 22:** Check arrow types and inheritance rules:
  - `Product <|-- DigitalProduct` and `Product <|-- PhysicalProduct` (**solid** line `<|--` for `extends`).
  - `Discountable <|.. DigitalProduct` and `Discountable <|.. PhysicalProduct` (**dashed** line `<|..` for `implements`).
  - Do not repeat `id`, `name`, or `price` inside subclass boxes.

---

### Activity 7 — Git Commit & Optional Stretch
- [ ] **Task 23:** Verify compilation/run, then stage, commit, and push (`Discountable.java`, `Product.java`, `DigitalProduct.java`, `PhysicalProduct.java`, `ProductService.java`, `ProductController.java`, `docs/uml/product-hierarchy.puml`).
- [ ] **Optional Stretch:** Add `BundleProduct extends Product` holding `List<Product>` — notice `ProductService.getAllSummaries()` needs **0 lines changed** because it relies purely on polymorphism.
