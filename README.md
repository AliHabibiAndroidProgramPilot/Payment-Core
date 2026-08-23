# Android Payment System Assessment

## Overview

This project is my implementation of a senior-level Android technical assessment focused on IPC, TCP networking, local persistence, dependency injection, foreground execution, concurrency control, and transaction lifecycle management.

The original assessment specification requires the solution to be built in **Java**, using **RxJava** and **Dagger 2**. I intentionally chose to **fully replace that technology stack with a modern Android stack** — Kotlin, Coroutines/Flow, Hilt, and Jetpack Compose with ViewModel — while preserving every original business requirement, architectural boundary, and engineering challenge described in the assessment. Nothing about the *problem* was simplified; only the *tools* used to solve it were modernized.

The system consists of two independent Android applications communicating over AIDL:

### Merchant App
Acts as the client application. It builds and sends payment-like transaction requests, observes progress through AIDL callbacks, renders state with Jetpack Compose, and exposes its logic through a `ViewModel` backed by Kotlin `Flow`.

### Payment Core App
Acts as the payment engine. It is responsible for:

* Receiving requests through AIDL
* Validating transaction data
* Persisting transaction information in Room
* Managing transaction state transitions (including protecting terminal states)
* Simulating TCP-style processing steps as part of the transaction pipeline
* Running transaction processing inside a Foreground Service with live notifications
* Delivering progress updates and final results through AIDL callbacks
* Enforcing single active transaction execution and thread safety
* Rejecting duplicate transaction requests at both the domain and persistence layers

---

## Original Assessment Requirements

The original assessment specification requires:

| Area | Original Requirement |
|---|---|
| Language | Java only |
| Dependency Injection | Dagger 2 only |
| Async / Reactive | RxJava 2 or RxJava 3 |
| Persistence | Room (DAO + Entity, Java-facing) |
| IPC | AIDL (request + callback interfaces) |
| Networking | Raw TCP socket client with timeout handling |
| Background Execution | Foreground Service with Notification |
| Architecture | Clean Architecture (presentation / domain / data) |
| Threading | Single active transaction guard (`AtomicBoolean`, lock, or serialized scheduler) |

Additionally, the assessment calls for production-oriented considerations: thread safety, concurrency control, transaction state management, resource cleanup, error handling, service lifecycle management, and performance awareness.

**This implementation deliberately diverges from the language/framework column above** — the reasoning is explained in [Modernization Decisions](#modernization-decisions) below — while every functional and architectural requirement in the spec is preserved or exceeded.

---

## Technology Stack

| Concern | Assessment Requirement | This Implementation |
|---|---|---|
| Language | Java | **Kotlin** |
| Async / Reactive | RxJava 2/3 | **Kotlin Coroutines + Flow / StateFlow** |
| Dependency Injection | Dagger 2 | **Hilt** |
| UI | Not specified (implied Views) | **Jetpack Compose + ViewModel** |
| Persistence | Room | Room (unchanged) |
| IPC | AIDL | AIDL (unchanged) |
| Background Execution | Foreground Service + Notification | Foreground Service + Notification (unchanged) |
| Architecture | Clean Architecture | Clean Architecture (unchanged) |

### Full Stack Used
* Kotlin
* Kotlin Coroutines & Flow / StateFlow
* Jetpack Compose
* ViewModel (Jetpack lifecycle)
* Hilt (dependency injection)
* Room (persistence)
* AIDL (cross-process IPC)
* Foreground Service
* Jetpack Architecture Components

---

## Architecture

The project follows Clean Architecture with clear separation between presentation, domain, and data layers, split across three Gradle modules:

```text
Merchant App (Compose UI + ViewModel)
    │
    ▼
AIDL Interface  ◄────────────  aidl_contract module
    │                          (PaymentRequest, PaymentResult,
    ▼                           IPaymentService, IPaymentCallback)
Payment Core App
    │
    ├── Presentation Layer   (Compose UI, MainActivity)
    ├── Binder Layer         (PaymentServiceBinder, NotificationHelper,
    │                         TransactionNotificationServiceLifecycle)
    ├── Domain Layer         (UseCases, TransactionRepository interface,
    │                         TransactionStatus, TransactionStateEvent)
    └── Data Layer           (Room: TransactionDao, TransactionsEntity,
                               TransactionRepositoryImpl, TypeConverters)
```

**Modules:**
* `aidl_contract` — shared AIDL interfaces and Parcelable models (`PaymentRequest`, `PaymentResult`, `IPaymentService`, `IPaymentCallback`), consumed by both apps.
* `app` (Payment Core) — the payment engine described above.
* `merchant` — the client app that initiates transactions.

---

## Core Transaction Flow

1. Merchant App sends a transaction request via `PaymentCoreConnector`, bound over AIDL.
2. Payment Core receives the request in `PaymentServiceBinder.startTransaction`.
3. A single-transaction guard (`AtomicBoolean.compareAndSet`) rejects the call immediately if another transaction is already in progress.
4. `StartTransactionUseCase` validates the request (`requestId`, `terminalId`, `amount`, `traceNumber`) and rejects duplicate `requestId`s before any processing begins.
5. The transaction is persisted in Room in state `RECEIVED`, then progressed through `STORED`.
6. The Foreground Service enters the foreground with a "processing" notification.
7. The use case emits a sequence of state transitions representing connection and network stages (`PROCESSING` → `CONNECTING` → `SENDING` → `WAITING_RESPONSE`), each mirrored to Room and to the notification.
8. The transaction reaches a terminal state (`SUCCESS` or `FAILED`), which Room enforces cannot be reached from, or overwritten once already in, another terminal state.
9. The AIDL callback (`onTransactionComplete` / `onTransactionFailed`) delivers the final result to the Merchant App.
10. The Foreground Service stops (or remains alive, per `keepServiceAlive`), and the transaction lock is always released in a `finally` block regardless of success, failure, or exception.

---

## Key Technical Challenges

### AIDL-Based IPC
Demonstrates Android IPC between two independent apps using a dedicated `aidl_contract` module, with:
* Request delivery (`startTransaction`)
* Progress callbacks (`onTransactionProgress`)
* Completion / failure callbacks (`onTransactionComplete` / `onTransactionFailed`)
* Status querying (`getTransactionStatus`)
* Signature-level permission (`BIND_PAYMENT_PERMISSION`) protecting the bound service

### Room Persistence & Business Rule Enforcement
Transaction data is stored and updated throughout the lifecycle. Two business rules are enforced with **defense-in-depth**, at both the domain layer and the persistence layer:

**1. Duplicate `requestId` rejection**
* Domain: `StartTransactionUseCase` pre-checks via `transactionRepository.getTransactionByRequestId()` and a `require(existing == null)` guard before inserting.
* Persistence: `TransactionDao.insertTransaction` uses `OnConflictStrategy.ABORT` (not `REPLACE`), so a duplicate `requestId` can never silently clobber an existing row even if the domain check is bypassed.

**2. Terminal state protection**
* Domain: `TransactionStatus` owns the semantic definition of finality via an `isFinal` property, so "what counts as final" lives in one place instead of scattered string checks.
* Persistence: every `UPDATE` query in `TransactionDao` (`updateTransactionStatus`, `markTransactionSuccess`, `markTransactionFailed`) appends `AND status NOT IN ('SUCCESS', 'FAILED')`, making the guard atomic at the point of mutation.
* Observability: all three DAO update methods return `Int` (affected row count) instead of `Unit`, propagated through `TransactionRepositoryImpl` as a `Boolean`, so a rejected/no-op write is detectable by the caller instead of silently swallowed.

This layered ownership pattern — domain models define *what* a rule means, persistence enforces it *atomically* — is used deliberately so that neither layer has to fully trust the other.

### Foreground Service
Transaction processing runs inside a Foreground Service to guarantee reliable execution during longer operations. The notification is updated live as the transaction progresses through: processing → connecting → waiting for response → completed/failed.

---

## Concurrency & Thread Safety

Only one active transaction is allowed at a time. The implementation guards against:

* Concurrent `startTransaction` calls (via `AtomicBoolean.compareAndSet`)
* Duplicate `requestId` processing (via use-case pre-check + DB `ABORT` conflict strategy)
* Invalid state transitions (via DB-level `AND status NOT IN (...)` guards on every terminal-adjacent update)
* Resource / lock leaks — the transaction lock is released in a `finally` block covering success, failure, and cancellation paths alike

---

## Transaction States

The system models transaction state explicitly via the `TransactionStatus` enum, rather than relying on scattered string literals:

* `RECEIVED`
* `STORED`
* `PROCESSING`
* `CONNECTING`
* `SENDING`
* `WAITING_RESPONSE`
* `SUCCESS`
* `FAILED`
* `CANCELLED`

`SUCCESS` and `FAILED` are treated as terminal. Once a transaction reaches either state, it is protected against further modification at the database layer (see [Room Persistence & Business Rule Enforcement](#room-persistence--business-rule-enforcement)).

> **Note:** The SQL literal state list used in the DAO guards (`'SUCCESS', 'FAILED'`) cannot reference the Kotlin `TransactionStatus` enum directly and must be kept manually in sync with it — this is called out with comments in `TransactionDao.kt` pointing back to the domain model.

---

## Modernization Decisions

Although the assessment specifically requires Java, RxJava, and Dagger 2, this implementation intentionally adopts a fully modern Android stack instead.

### Java → Kotlin
* Null-safety
* Reduced boilerplate (data classes, sealed interfaces for state modeling)
* More expressive domain modeling (e.g. `TransactionStateEvent`, `PaymentEvent` as sealed interfaces)

### RxJava → Kotlin Coroutines & Flow
* Simpler asynchronous/reactive programming model
* Structured concurrency and easier cancellation handling
* `Flow` naturally expresses the transaction's sequence of state events; `callbackFlow` bridges the AIDL callback interface back into a cold `Flow` on the Merchant side
* `StateFlow` drives Compose UI state in both apps

### Dagger 2 → Hilt
* Significantly reduced DI boilerplate
* First-class scoping for `ServiceComponent` (used to scope the use cases to the Payment Core's foreground service) and `SingletonComponent`
* Faster iteration and easier testing setup

### Views (implied) → Jetpack Compose + ViewModel
* Declarative UI driven directly by `StateFlow`/`MutableStateFlow` exposed from `ViewModel`s (`MainScreenViewModel`)
* No manual view-state synchronization; UI recomposes automatically from a single source of truth
* Consistent with current Android development practice for new applications

The objective of these substitutions is **not** to avoid the original requirements but to demonstrate how the same architecture, IPC design, persistence guarantees, and concurrency requirements can be implemented — and, in the case of business-rule enforcement, hardened further — using the current Android development ecosystem.

---

## Disclaimer

This project is an educational and assessment-focused implementation and does not represent a complete production payment solution. Security, encryption, compliance requirements, payment certification processes, real TCP server integration, and sensitive payment data handling are intentionally outside the scope of this assessment.
