# Scala DSL: Concrete Syntax (BNF)

**Implementation**: ArduinoML embedded DSL in Scala
**Technology**: Scala 2.13.12
**Kernel**: Java-based ArduinoML kernel with Visitor pattern

---

## BNF Grammar

```bnf
<program>           ::= <name-decl> <declaration>* <initial-decl> <transition-block> <export>

<name-decl>         ::= "this" "hasForName" <string>

<declaration>       ::= <sensor-decl> | <actuator-decl> | <state-decl>

<sensor-decl>       ::= "val" <identifier> "=" "declare" "." "aSensor" "()" "named" <string> "boundToPin" <integer>

<actuator-decl>     ::= "val" <identifier> "=" "declare" "." "anActuator" "()" "named" <string> "boundToPin" <integer>

<state-decl>        ::= "val" <identifier> "=" "state" "named" <string> "executing" "(" <action-list> ")"

<action-list>       ::= <action> ( "," <action> )*

<action>            ::= <identifier> "-->" <signal>

<signal>            ::= "high" | "low"

<initial-decl>      ::= <identifier> "." "isInitial"

<transition-block>  ::= "transitions" "{" <transition>* "}"

<transition>        ::= <identifier> "->" <identifier> "when" "(" <condition> ")"

<condition>         ::= <predicate> ( <boolean-op> <predicate> )*

<predicate>         ::= <identifier> "is" <signal>

<boolean-op>        ::= "and" | "or"

<export>            ::= "exportToWiring"

<identifier>        ::= [a-z][a-zA-Z0-9_]*

<string>            ::= '"' [a-zA-Z_][a-zA-Z0-9_]* '"'

<integer>           ::= [0-9]+
```

---

## Vocabulary Defined by Our Implementation

### Application Name

```scala
this hasForName <name>
```

**Example:**
```scala
this hasForName "MultiStateAlarm!"
```

### Hardware Components

**Sensors:**
```scala
val <variable> = declare.aSensor() named <name> boundToPin <pin>
```

**Actuators:**
```scala
val <variable> = declare.anActuator() named <name> boundToPin <pin>
```

**Examples from our scenarios:**
```scala
val button = declare.aSensor() named "button" boundToPin 9
val led    = declare.anActuator() named "led" boundToPin 12
val buzzer = declare.anActuator() named "buzzer" boundToPin 11
```

### States

```scala
val <variable> = state named <name> executing (
  <actuator> --> <signal>,
  <actuator> --> <signal>
)
```

**Examples:**
```scala
val off = state named "off" executing (
  led --> low,
  buzzer --> low
)

val buzzing = state named "buzzing" executing (
  buzzer --> high,
  led --> low
)
```

### Initial State

```scala
<state-variable>.isInitial
```

**Example:**
```scala
off.isInitial
```

### Transitions

```scala
transitions {
  <from-state> -> <to-state> when (<condition>)
}
```

**Conditions support AND/OR boolean composition:**
```scala
<sensor> is <signal>
<sensor> is <signal> and <sensor> is <signal>
<sensor> is <signal> or <sensor> is <signal>
```

**Examples from our scenarios:**
```scala
transitions {
  off     -> buzzing when (button is high)
  buzzing -> ledOn   when (button is high)
  ledOn   -> off     when (button is high)
}
```

### Export

```scala
exportToWiring
```

Generates Arduino C++ code to standard output.

---

## Complete Example: MultiStateAlarm

This demonstrates all vocabulary elements we implemented:

```scala
package io.github.mosser.arduinoml.samples

import io.github.mosser.arduinoml.dsl.ArduinoML

object MultiStateAlarm extends App with ArduinoML {

  // Application name
  this hasForName "MultiStateAlarm!"

  // Hardware declarations
  val button = declare.aSensor()    named "button" boundToPin 8
  val led    = declare.anActuator() named "led"    boundToPin 12
  val buzzer = declare.anActuator() named "buzzer" boundToPin 11

  // State definitions
  val off = state named "off" executing (
    led --> low,
    buzzer --> low
  )

  val buzzing = state named "buzzing" executing (
    buzzer --> high,
    led --> low
  )

  val ledOn = state named "ledOn" executing (
    led --> high,
    buzzer --> low
  )

  // Initial state
  off.isInitial

  // Transitions
  transitions {
    off     -> buzzing when (button is high)
    buzzing -> ledOn   when (button is high)
    ledOn   -> off     when (button is high)
  }

  // Code generation
  exportToWiring
}
```

---

## Implementation Notes

### Scala-Specific Syntax

Our implementation uses **Scala's object-oriented and functional features**:

**Trait Mixin:**
```scala
object MultiStateAlarm extends App with ArduinoML
```
Mixes in the ArduinoML DSL vocabulary.

**Value Declarations:**
```scala
val button = declare.aSensor() named "button" boundToPin 9
```
Immutable values bind hardware components to Scala variables.

**Infix Operators:**
```scala
led --> low              // Method call syntax
button is high           // Infix notation
off -> buzzing           // Arrow syntax for transitions
```

### Signal Constants

Our implementation defines signals as Scala objects:
- `high` → SIGNAL.HIGH
- `low` → SIGNAL.LOW

No quotes required - they are identifiers in Scala scope.

### Type Safety

Unlike Groovy, our Scala implementation enforces types at compile time:

```scala
val button = declare.aSensor() named "button" boundToPin 9     // OK
val button = declare.aSensor() named "button" boundToPin "9"   // Compile error!
```

The type system ensures:
- Pin numbers are integers
- States reference declared actuators
- Transitions reference declared sensors
- Boolean expressions use proper operators

### Generated Arduino Code

Our implementation generates identical C++ code to Groovy:

**Debounce handling:**
```cpp
buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
if((digitalRead(9) == HIGH && buttonBounceGuard)) {
    buttonLastDebounceTime = millis();
    currentState = buzzing;
}
```

**State machine structure:**
```cpp
enum STATE {off, buzzing, ledOn};
STATE currentState = off;

void loop() {
    switch(currentState) {
        case off:
            digitalWrite(12, LOW);
            digitalWrite(11, LOW);
            // transitions...
        break;
    }
}
```

---

## Architecture

Our implementation uses:

**Kernel (Java):**
- Same kernel as Groovy implementation
- `Sensor`, `Actuator`, `State`, `Transition`
- `BooleanExpression`, `Predicate`, `BinaryExpression`
- `ToWiring` visitor for C++ generation

**DSL (Scala):**
- `ArduinoMLDSL` trait - Provides vocabulary methods
- Case classes for builders:
  - `StructureBuilder` - Sensors/actuators
  - `StateBuilder` - States
  - `ConditionBuilder` - Boolean expressions
  - `TransitionBuilder` - State transitions
- Type-safe method chaining

**Mapping:**
```
declare.aSensor() named "x" boundToPin 9  → Sensor("x", 9)
state named "off" executing (...)         → State("off", [Action(...)])
button is high                            → Predicate(button, SIGNAL.HIGH)
s1 -> s2 when (...)                      → Transition(s1, s2, BooleanExpression)
```

---

## Differences from Groovy Implementation

| Feature | Scala | Groovy |
|---------|-------|--------|
| **Syntax** | Object-oriented with infix | Command chain strings |
| **Variables** | `val` bindings required | Direct string references |
| **Signals** | Object constants (`high`) | String identifiers (`"high"`) |
| **Type Safety** | Compile-time | Runtime |
| **Music Support** | Not implemented | Implemented with `note`, `play` |
| **Consistency** | Uniform syntax | Dual syntax for states |

**Key advantage:** Scala's type system catches errors at compile time that Groovy only catches at runtime.

**Missing feature:** Music/melody support exists in Groovy but not in our Scala implementation.
