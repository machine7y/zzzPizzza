---
name: solid-principles
description: SOLID principles (SRP, OCP, LSP, ISP, DIP) for object-oriented code in any language — what counts as a violation and how to fix it, with signs at the level of classes, interfaces and modules. Load when designing new classes and interfaces, when reviewing for responsibility/coupling, and when editing an existing class that keeps "growing" new conditions or constructor parameters.
---

# SOLID

Five principles for designing classes and interfaces. These are heuristics for making a
decision, not laws the code must literally obey. Below are concrete signs of violation and
how to fix them, but apply them in moderation.

## How to apply

- The goal is code that's easier to change and understand. SOLID is one tool for that,
  not an end in itself. If formally following a principle makes the code more complex,
  the principle has lost.
- **Rigid, mechanical adherence to the principles is also bad**, not just ignoring them.
  An interface for every class "just in case," splitting one simple method into three
  for the sake of formal SRP, an abstraction for a hypothetical second implementation —
  this does the same harm as a god class with ten responsibilities, just with the
  opposite sign: more files, more indirection, the same (or worse) result for the reader.
- Look for the middle ground: apply a principle when it relieves real pain (the class is
  already hard to change, there are already two implementations, there's already
  confusion about responsibility), and don't apply it when the problem it solves doesn't
  physically exist yet.
- **When designing a new class** — keep the principles in mind as a default check, but
  don't unfold the structure for the sake of unfolding it; a simple, direct solution by
  default is preferable to a multi-layered "correct" one.
- **In review** — name the specific violated principle (not just "bad code") and give a
  fixed version right away, but just as explicitly point out the opposite case — when
  complexity was added under the SOLID banner but isn't actually needed.
- The principles aren't absolute. If splitting by the rule produces two meaningless
  wrappers instead of one clear class, common sense wins — but say so explicitly rather
  than silently breaking the rule.
- Don't apply the principles preemptively "for the future": an interface is introduced
  only for a reason from the list in "D — Dependency Inversion", not in advance.

---

## S — Single Responsibility

A class has one reason to change: it answers to one actor. An actor is one role or group
of people whose requests change the code. A class changed on requests of two actors holds
two responsibilities.

Signs of violation:
- changes requested by different actors land in the same class;
- the constructor keeps gaining dependencies, and different methods use different subsets
  of them;
- methods work on disjoint subsets of the class's fields;
- describing the class needs an "and" between unrelated duties, not "which does this
  through".

Fix: split the class along its actors, so each new class holds the code one actor changes.
Data access and its formatting for an output are split only when different actors change
them; when one actor owns both, they stay in one class.

In a layered architecture, SRP is often already built into the structure: one layer is
one use case, another is access to one kind of data, a third is one transformation. If a
single-use-case class starts branching across several different use cases, that's an
SRP violation, not "generality."

---

## O — Open/Closed

A class is open for extension, closed for modification: new behavior is added with new
code, not by editing conditional branching (`switch`/`if-else`) inside an already
working class.

Sign of violation: adding a new behavior variant requires adding a branch to existing
conditional logic keyed on a type/enum, in several different places in the code.
Fix: the varying behavior is extracted behind an interface, each variant is a separate
class implementing that contract; a new variant is added as a new class, the old code
isn't touched.

This doesn't mean "an interface for every branch." Branching over a finite, rarely
changing set of variants is normal, closed code, not an OCP violation. The principle
matters where the set of variants genuinely grows over time.

---

## L — Liskov Substitution

A subtype/implementation can be substituted for the base type without changing the
correctness of the calling code. If the calling code has to know the concrete subtype to
avoid breaking, the principle is violated.

Sign of violation: an interface implementation throws a "method not supported" error,
returns a knowingly wrong result for part of the input, or requires calling methods in
an order specific to that subtype.
Fix: the contract is split by actual capability rather than bent to fit one
implementation — a separate read-only interface and a read-write interface that extends
it, so a read-only implementation isn't forced to have a throwing method.

---

## I — Interface Segregation

A client shouldn't depend on interface methods it doesn't use. A broad "one interface
for everything" forces implementations to have empty/throwing stubs for methods they
don't need.

Sign of violation: an interface with a dozen methods, of which a particular client class
needs only one, and some implementations override the rest with an empty stub "just to
compile."
Fix: the interface is split along the boundary of actual usage (which classes call
what), not one interface per method — otherwise you get an ISP violation in the other
direction: pointless fragmentation.

---

## D — Dependency Inversion

Higher-level modules don't depend on lower-level modules directly — both depend on an
abstraction declared by the higher level. Higher level is code farther from I/O: business
rules. Lower level is code that talks to I/O, frameworks and external systems. Direction of
dependency: the implementation module depends on the interface declared by the consumer
module, not the other way around.

Abstractions don't depend on details. An interface's signatures use only the consumer's own
types and standard-library types, never the implementation's types: SDK, ORM, network DTO
and framework classes. An interface that repeats an SDK's API one to one is a wrapper, not
an inversion.

An interface is required when at least one of these holds:
- the dependency does I/O or reads nondeterministic state: network, database, file system,
  OS/platform APIs, clock, random numbers;
- the dependency is a third-party SDK or client;
- the dependency lives in a lower-level module than its consumer;
- there are already two implementations, or the task or the user states that a second one
  is coming;
- an interface for it already exists — then the consumer takes that interface, never its
  implementation class;
- a test has to replace the dependency, and the language or test tooling can't substitute a
  concrete class.

Otherwise — a class without I/O, in the consumer's own module, with no second
implementation existing or stated — the consumer takes the concrete class directly. That is
not a DIP violation, and an interface there is the "just in case" abstraction from "How to
apply".

Signs of violation:
- a consumer takes a concrete class although the list above requires an interface;
- a higher-level module imports a class from a lower-level module directly;
- an interface's signatures contain the implementation's types.

Fix: the interface is declared on the consumer module's side in the consumer's own types.
The implementation module implements it, maps its own types to the consumer's types, and is
wired in via DI/a factory. The consumer works only with the interface and never imports the
concrete implementation.

---

## Review checklist

1. **SRP** — the class answers to one actor: changes requested by different actors don't
   land in it, and its methods don't split its dependencies or fields into disjoint groups.
2. **OCP** — adding a new behavior variant doesn't require editing conditional logic in
   several existing files.
3. **LSP** — no interface implementation throws "not supported" or silently ignores part
   of the contract.
4. **ISP** — the interface has no methods that some implementations/clients don't
   genuinely use.
5. **DIP** — every dependency that matches the list in "D — Dependency Inversion" is taken
   as an interface declared by the consumer; interface signatures contain no
   implementation types; a higher-level module doesn't import a lower-level module directly.
6. Nothing is split "just in case": every interface has a reason from the list in
   "D — Dependency Inversion", every class split off for SRP has its own actor.
