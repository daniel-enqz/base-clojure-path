# Clojure, Datomic and Software
> Based on [Brave Clojure Book](https://www.braveclojure.com/do-things/) and some other exercises.

> Datomic Content can be found [here](https://github.com/daniel-enqz/base-clojure-path/tree/master/src/datomic)

## Functional Programming
> Some important insights of Clojure Functional Programming Principles can be found [here](https://github.com/daniel-enqz/base-clojure-path/blob/master/src/clojure_noob/f_functional_programming.clj)
- Side Effect: A function has a side effect when it has any observable effect other than its primary effect of returning a value to the invoker of the function. Updating an atom, Producing a log, Create / Update a value in the database, Performing I/O, or calling other function that has side effects are examples of actions that cause side effects on a function.
       
## Domain Driven Design
"What problem am I trying to solve?". That problem exists in a broader context. That context may be employee relations, cold chain shipping, medical records, banking--whatever the context, that cognitive space is our problem domain. That domain is comprised of the entities embodying the concepts and data, as well as the operations that create, transform, and update those entities into solutions to our problems. We can think of those operations as domain operations.
                                              
## Product Rules
- A customer can be taken into account into multiple segments.
- Always save stuff in your domain, avoid getting data outside domain.

## Questions to ask to a code
- Have the right domain operations been exposed?
- Do any of these functions have side effects?
- Are all of these functions deterministic?
- Is there a more or less efficient way to do the same things?

### When doing great code
> _Entities are simplest when distinct and composable.
Domain functions avoid complexity by avoiding side effects and concerning themselves only with entities in their domain._
- Be reasonable: Write code with clear intent, limited side effects, and unambiguous naming for better comprehension.
- Build just enough: Avoid over-engineering by creating a minimal, extensible domain language for your application.
- Compose: Design independent, cleanly composable components to avoid messy integration code.
- Be precise: Use precise language and create unambiguous entities, functions, and queries for clear communication.
- Use what works: Leverage existing solutions and ideas rather than reinventing the wheel for efficiency and stability.

### When doing combinations
- Figure out what question you’re trying to ask. This step is often the most difficult.
- Filter the data to remove unneeded elements. 
- Transform the elements into the desired form.
- Reduce the transformed elements to the answer.

```clojure
(defn pending-credits
"given a sequence of transactions, returns a money entity
representing all unsettled (pending) credit transactions"
[txs]

(->> txs
(filter-by-type :credit)
(filter-by-status :pending)
(map :amount)
(reduce +$)))
```

