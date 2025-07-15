# Yet Another Binary Serializer (YABS)

[![Build Status](https://img.shields.io/github/actions/workflow/status/rjhdby/yabs/build.yml?branch=main)](https://github.com/rjhdby/yabs/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/rjhdby/yabs/branch/main/graph/badge.svg)](https://codecov.io/gh/rjhdby/yabs)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A lightweight, efficient binary encoding library for Kotlin objects with zero dependencies.

## Overview

Yet Another Binary Serializer (YABS) is a type-safe binary serialization/deserialization library for Kotlin that provides fast, efficient encoding of objects into byte arrays and vice versa. It's designed to be simple to use while offering powerful capabilities for all Kotlin types.

## Features

- **Efficient**: Optimized binary encoding with minimal overhead
- **Zero dependencies**: Completely standalone library
- **Flexible**: Works with standard Kotlin classes, data classes, and collections
- **Extensible**: Easy to add custom encoders for specific types
- **Automatic registration**: Automatically creates and registers encoders for data classes

## Quick Start

```kotlin
// Define your data class
data class Person(
    val name: String,
    val age: Int,
    val emails: List<String>
)

// Create an encoder for your class
val encoder = TypeEncoder.create<Person>()

// Encode an object to a byte array
val person = Person("John Doe", 30, listOf("john@example.com", "doe@example.com"))
val bytes = encoder.encode(person)

// Decode a byte array back to an object
val decoded = encoder.decode(bytes)
```

## Supported Types

- Primitive types (Int, Long, Float, Double, Boolean, etc.)
- String
- Enum classes
- Collections (List, Set, Queue)
- Nested data classes
- Nullable types
- UUID (Kotlin and Java implementation)
- Custom not data classes with custom encoder implementation

## Not implemented yet
- Map
- Collections with nullable elements

## Custom Encoders

You can create and register custom encoders for specific types:

```kotlin
class CustomTypeEncoder : TypeEncoder<MyCustomType> {
    override fun classifier(): String = MyCustomType::class.qualifiedName!!

    override fun encode(value: MyCustomType): ByteArray {
        // Custom encoding logic
    }

    override fun decodeEntry(bytes: ByteArray, startOffset: Int): TypeEncoder.Entry<MyCustomType> {
        // Custom decoding logic
    }
}

// Register your custom encoder
TypeEncoder.register(CustomTypeEncoder())
```

## Advanced Usage

### Variable Length Integer Encoding

The library uses variable-length encoding for integers to minimize space:

```kotlin
val encoded = VariableLengthIntEncoder.encode(1234)
val decoded = VariableLengthIntEncoder.decode(encoded)
```

### Working with Collections

The library has built-in support for various collection types:

```kotlin
// Lists
val listEncoder = TypeEncoder.create<List<String>>()
val encodedList = listEncoder.encode(listOf("a", "b", "c"))

// Sets
val setEncoder = TypeEncoder.create<Set<Int>>()
val encodedSet = setEncoder.encode(setOf(1, 2, 3))
```

## Performance

Yet Another Binary Serializer is designed for optimal performance with minimal overhead. The binary format is compact and efficient, making it suitable for applications where performance is critical.

For each type, the serialization/deserialization pipeline is created only once (lazily) and then reused without additional overhead in subsequent operations

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
