# Datomic Architecture
> Interesting [pathway](https://datomic.learn-some.com) with datomic practical examples.


### Datomic's Data Model Summary:
- Entities: Represent unique "things" or objects in the database.
- Attributes: Describe properties of entities.
- Values: Hold the actual data for each attribute of an entity.

Each attribute in Datomic requires at least three properties:
- :db/ident - A unique identifier for the attribute (e.g., :person/first-name)
- :db/valueType - The type of data the attribute can hold (e.g., :db.type/string)
 :db/cardinality - Whether the attribute can have one or many values (:db.cardinality/one or :db.cardinality/many)



<img width="1152" alt="Screenshot 2024-10-02 at 10 20 47 a m" src="https://github.com/user-attachments/assets/95846aa3-ce7a-4500-ada7-8d42d13e1a53">



## Storage:

- Purpose: Durable data persistence
- Structure: Data organized in segments containing many datoms
- Options: Various backends (e.g., DynamoDB, with optional caching like Memcached)
- Key feature: Retrieves entire segments, not individual datoms

## Transactor:

- Purpose: Central coordinator of the database
- Main functions:
  - Processes transactions serially
  - Validates and commits changes
  - Maintains database consistency
  - Writes to the log

_Communicates with: Peers and Storage_

## Peers:

- Purpose: Database interface close to the application
- Main functions:
  - Sends transactions to Transactor
  - Executes queries locally
  - Caches data
  - Fetches data from Storage when needed

_Communicates with: Application, Transactor, and Storage_
