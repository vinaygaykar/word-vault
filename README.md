# Word Vault
This project is a Java implementaion of the Trie data structure.

## What is a Trie?

A Trie (pronounced "try") is a tree-based data structure used for efficiently storing and searching for strings. It is also known as a Prefix Tree, because each node in the tree represents a prefix of one or more strings.

The key advantage of a Trie is that it allows for very fast lookups of strings. It achieves this by storing the strings in a tree structure, where each node in the tree represents a single character in the string. Each node may have zero or more child nodes, each representing the next character in the string. By traversing the tree from the root node to the leaf nodes, we can find the string we are searching for.

## Usages

Create a new instance of the Trie class:

```java
final Dictionary<Integer> trie = new CompressedTrie<>(); 
// OR
final Dictionary<Integer> trie = new SimpleTrie<>();
```

Add strings to the Trie:

```java
trie.add("hello", 1);
trie.add("world", 2);
```

Search for strings in the Trie:

```java
final Optional<Integer> found = trie.getValue("hello");
```

## Features

This Trie implementation has the following features:

- [x] Adding strings to the Trie
- [x] Checking if a string exists in the Trie
- [ ] Removing strings from the Trie
- [x] Finding all strings in the Trie that start with a given prefix

## API

This project exposes an interface called `Dictionary<V>` which acts as an API guideline for the complete project. 

| **Interface Description**                                     | **Dictionary**
|---------------------------------------------------------------|------------------------------------------
| Add a single word                                             | `add("hello", 1)`                     |
| Get value associated with a word                                             | `getValue("hello")`                     |
| Search for prefix matches with count of words to return                                     | `hasPrefix("he", 3)` |
| Get the number of words stored 	                    | `size()` 	|

`Dictionary<V>` interface is implemented by two classes:
1. `SimpleTrie` - Simplest implementation of Trie where every character of a word to be added is a node itself.
2. `CompressedTrie` - This is a memory optimised version where every nodes from `SimpleTrie` which have single child are combined together to reduce count of nodes

---

## Nice to have

- [ ] A new interface (child perhaps, of Dictionary) which would only store words and not values, e.g. what HashSet is to HashMap
- [ ] Benchmarking data between `SimpleTrie` & `ComplexTrie`
- [ ] An API which exposes count of nodes created
- [ ] Removing keys from the vault
- [ ] DAWG implementation also?
