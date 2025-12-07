# GroovuinoML: Concrete Syntax (BNF)

**Implementation**: GroovuinoML - ArduinoML embedded DSL in Groovy
**Technology**: Groovy 4.0.21
**Kernel**: Java-based ArduinoML kernel with Visitor pattern

---

## BNF Grammar

```bnf
<program>           ::= <declaration>* <transition>* <export>

<declaration>       ::= <sensor-decl>
                      | <actuator-decl>
                      | <note-decl>
                      | <state-decl>
                      | <initial-decl>

<sensor-decl>       ::= "sensor" <identifier> "pin" <integer>

<actuator-decl>     ::= "actuator" <identifier> "pin" <integer>

<note-decl>         ::= "note" <identifier> "frequency" <integer> "duration" <integer>

<state-decl>        ::= <simple-state-decl> | <musical-state-decl>

<simple-state-decl> ::= "state" <identifier> "means" <action-chain>

<musical-state-decl> ::= "state" "(" <identifier> ")" "." "means" "(" <play-action> ")"

<action-chain>      ::= <actuator-action> ( "and" <actuator-action> )*

<actuator-action>   ::= <identifier> "becomes" <identifier>

<play-action>       ::= "play" "(" <identifier> ")" "." "on" "(" <identifier> ")"

<initial-decl>      ::= "initial" <identifier>

<transition>        ::= "from" <identifier> "to" <identifier> "when" <boolean-expr>

<boolean-expr>      ::= <predicate> ( <boolean-op> <predicate> )*

<predicate>         ::= <identifier> "becomes" <identifier>

<boolean-op>        ::= "and" | "or"

<export>            ::= "export" <identifier>

<identifier>        ::= '"' [a-zA-Z_][a-zA-Z0-9_]* '"'

<integer>           ::= [0-9]+
```

---

## Vocabulary Defined by Our Implementation

### Hardware Components

```groovy
sensor <name> pin <number>      // Define a digital input sensor
actuator <name> pin <number>    // Define a digital output actuator
```

**Examples from our scenarios:**
```groovy
sensor "B1" pin 8
sensor "button" pin 9
actuator "buzzer" pin 11
actuator "led" pin 12
```

### Musical Notes

```groovy
note <name> frequency <hz> duration <ms>
```

**Examples from Melania scenario:**
```groovy
note "C" frequency 262 duration 500
note "E" frequency 330 duration 500
note "G" frequency 392 duration 500
note "C_high" frequency 523 duration 500
```

### States

**Two syntaxes due to Groovy implementation constraints:**

**Simple actuator states:**
```groovy
state <name> means <actuator> becomes <signal>
state <name> means <actuator> becomes <signal> and <actuator> becomes <signal>
```

**Musical states (requires explicit syntax):**
```groovy
state(<name>).means(play(<note>).on(<actuator>))
```

**Examples:**
```groovy
state "idle" means "buzzer" becomes "low"
state "on" means "buzzer" becomes "high"
state("melody1").means(play("C").on("buzzer"))
```

### Initial State

```groovy
initial <state-name>
```

**Example:**
```groovy
initial "idle"
initial "off"
```

### Transitions

```groovy
from <state> to <state> when <condition>
```

**Conditions support AND/OR boolean composition:**
```groovy
from "off" to "on" when "button" becomes "high"
from "off" to "on" when "button" becomes "high" and "button2" becomes "high"
from "on" to "off" when "button" becomes "low" or "button2" becomes "low"
```

**Examples from our scenarios:**
```groovy
from "idle" to "melody1" when "B1" becomes "high"
from "melody1" to "idle" when "B1" becomes "low" and "B2" becomes "low"
```

### Export

```groovy
export <application-name>
```

**Examples:**
```groovy
export "Melania_MelodyPlayer"
export "Switch!"
```

---

## Complete Example: Melania Scenario

This demonstrates all vocabulary elements we implemented:

```groovy
// Hardware declarations
sensor "B1" pin 8
sensor "B2" pin 9
actuator "buzzer" pin 11

// Musical note definitions
note "C" frequency 262 duration 500
note "E" frequency 330 duration 500
note "G" frequency 392 duration 500
note "C_high" frequency 523 duration 500

// State machine
state "idle" means "buzzer" becomes "low"
state("melody1").means(play("C").on("buzzer"))
state("melody2").means(play("E").on("buzzer"))
state("melody3").means(play("G").on("buzzer"))
state("finale").means(play("C_high").on("buzzer"))

initial "idle"

// Transitions with boolean conditions
from "idle" to "melody1" when "B1" becomes "high"
from "idle" to "melody2" when "B2" becomes "high"
from "melody1" to "idle" when "B1" becomes "low" and "B2" becomes "low"
from "melody1" to "melody3" when "B2" becomes "high"
from "melody2" to "idle" when "B1" becomes "low" and "B2" becomes "low"
from "melody2" to "finale" when "B1" becomes "high"
from "melody3" to "idle" when "B1" becomes "low" and "B2" becomes "low"
from "finale" to "idle" when "B1" becomes "low" and "B2" becomes "low"

export "Melania_MelodyPlayer"
```

---

## Implementation Notes

### Why Two State Syntaxes?

Our implementation has a **syntax inconsistency** due to Groovy's method resolution:

**Works (command chain at top level):**
```groovy
state "idle" means "buzzer" becomes "low"
```

**Fails (command chain in closure parameter):**
```groovy
state "melody1" means play "C" on "buzzer"  // Error!
```

**Required workaround:**
```groovy
state("melody1").means(play("C").on("buzzer"))  // OK
```

**Root cause:** Groovy resolves `play` as a property lookup rather than a method call when inside the `means` closure parameter.

### Signal Values

Our implementation accepts signal identifiers:
- `"high"` → SIGNAL.HIGH
- `"low"` → SIGNAL.LOW

These are resolved at runtime through our GroovuinoMLBinding.

### Generated Arduino Code

Our implementation generates C++ code with:

**Debounce handling:**
```cpp
buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
if((digitalRead(8) == HIGH && buttonBounceGuard)) {
    buttonLastDebounceTime = millis();
    currentState = on;
}
```

**Musical tone generation:**
```cpp
tone(11, 262, 500);
delay(500);
noTone(11);
```

---

## Architecture

Our implementation uses:

**Kernel (Java):**
- `Sensor`, `Actuator` - Hardware components
- `State`, `Transition` - State machine
- `Action`, `NoteAction` - State behaviors
- `Note`, `Melody` - Musical elements
- `BooleanExpression`, `Predicate`, `BinaryExpression` - Composite pattern for conditions
- `ToWiring` visitor - C++ code generation

**DSL (Groovy):**
- `GroovuinoMLBasescript` - Base class providing vocabulary methods
- `GroovuinoMLModel` - Factory for kernel objects
- `GroovuinoMLBinding` - Runtime symbol resolution
- `GroovuinoMLDSL` - Script executor with security

**Mapping:**
```
sensor "B1" pin 8           → Sensor("B1", 8)
note "C" frequency 262...   → Note("C", 262, 500)
state "on" means ...        → State("on", [Action(...)])
from "x" to "y" when ...    → Transition(BooleanExpression)
```
