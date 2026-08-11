# Android Payment System Assessment

## Overview

This project is my implementation of a senior-level Android technical assessment focused on IPC, TCP networking, local persistence, dependency injection, foreground execution, concurrency control, and transaction lifecycle management. The original assessment requires the solution to be implemented using Java, RxJava, and Dagger 2. However, I intentionally chose to implement the project using modern Android development practices with Kotlin, Coroutines, Flow, and Hilt while preserving the original business requirements, architecture, and engineering challenges.

The system consists of two Android applications:

### Merchant App

Acts as the client application that initiates payment-like transactions and receives transaction updates and final results through AIDL callbacks.

### Payment Core App

Acts as the payment engine responsible for:

* Receiving requests through AIDL
* Validating transaction data
* Persisting transaction information in Room
* Managing transaction state transitions
* Executing TCP communication with a mock payment server
* Running transaction processing inside a Foreground Service
* Delivering progress updates and final results through AIDL callbacks
* Enforcing single active transaction execution and thread safety

---

## Original Assessment Requirements

The original assessment specification requires:

* Java
* RxJava
* Dagger 2
* Room
* AIDL
* Raw TCP Socket Communication
* Foreground Service
* Clean Architecture

Additionally, the assessment requires production-oriented considerations such as:

* Thread safety
* Concurrency control
* Transaction state management
* Resource cleanup
* Error handling
* Service lifecycle management
* Performance awareness

The complete transaction flow and technical requirements are preserved in this implementation despite the technology substitutions.

---

## Technology Stack

### Implementation Stack

* Kotlin
* Coroutines
* Flow / StateFlow
* Hilt
* Room
* AIDL
* Foreground Service
* TCP Socket Client
* Jetpack Architecture Components

### Assessment Stack (Original Requirement)

* Java
* RxJava
* Dagger 2
* Room
* AIDL
* Foreground Service
* TCP Socket Client

---

## Architecture

The project follows Clean Architecture principles with clear separation between presentation, domain, and data layers as requested by the assessment.

```text
Merchant App
    │
    ▼
AIDL Interface
    │
    ▼
Payment Core App
    │
    ├── Presentation Layer
    ├── Domain Layer
    └── Data Layer
            ├── Room
            ├── TCP Client
            └── AIDL Service
```

---

## Core Transaction Flow

1. Merchant App sends a transaction request.
2. Payment Core receives the request through AIDL.
3. Request validation is performed.
4. Single transaction guard verifies no transaction is currently active.
5. Transaction is persisted in Room.
6. Foreground Service starts.
7. TCP connection is established.
8. Request is sent to the mock server.
9. Response is received and validated.
10. Room database is updated.
11. AIDL callback returns the final result.
12. Foreground Service is stopped or kept alive depending on the request configuration.

---

## Key Technical Challenges

### AIDL-Based IPC

The project demonstrates Android Inter-Process Communication (IPC) using AIDL between two independent Android applications.

Features include:

* Request delivery
* Progress callbacks
* Completion callbacks
* Error callbacks
* Status querying

### TCP Networking

A raw TCP socket client is used to communicate with a mock payment server.

Implemented concerns include:

* Connection timeout
* Read timeout
* Response parsing
* Invalid response handling
* Safe socket cleanup
* Failure recovery

### Room Persistence

Transaction information is stored locally and updated throughout the transaction lifecycle.

Data includes:

* Request information
* Transaction state
* Response data
* Processing timestamps
* Service lifecycle configuration

### Foreground Service

Transaction processing is executed within a Foreground Service to ensure reliable execution during long-running operations.

The notification reflects the current transaction state throughout processing.

---

## Concurrency & Thread Safety

Only one active transaction is allowed at a time.

The implementation includes mechanisms to prevent:

* Concurrent transaction execution
* Race conditions
* Duplicate transaction processing
* Invalid state transitions
* Resource leaks

Special attention is given to releasing transaction locks and cleaning resources in success, failure, and cancellation scenarios.

---

## Transaction States

The system models transaction state explicitly rather than relying on scattered string values.

Supported states include:

* RECEIVED
* STORED
* PROCESSING
* CONNECTING
* SENDING
* WAITING_RESPONSE
* SUCCESS
* FAILED
* CANCELLED

Final transaction states are protected against accidental modification.

---

## Modernization Decisions

Although the assessment specifically requires Java, RxJava, and Dagger 2, this implementation intentionally adopts modern Android development practices.

### Java → Kotlin

Benefits:

* Improved readability
* Null-safety
* Reduced boilerplate
* Better language expressiveness

### RxJava → Coroutines & Flow

Benefits:

* Simpler asynchronous programming model
* Structured concurrency
* Easier cancellation handling
* Improved readability and maintainability

### Dagger 2 → Hilt

Benefits:

* Reduced dependency injection boilerplate
* Better Android integration
* Faster development
* Easier testing and maintenance

The objective of these substitutions is not to avoid the original requirements but to demonstrate how the same architecture and business requirements can be implemented using the current Android development ecosystem.

---

## Disclaimer

This project is an educational and assessment-focused implementation and does not represent a complete production payment solution. Security, encryption, compliance requirements, payment certification processes, and sensitive payment data handling are intentionally outside the scope of this assessment.
