package io.github.mosser.arduinoml.dsl

import io.github.mosser.arduinoml.kernel.App
import io.github.mosser.arduinoml.kernel.generator.ToWiring
import io.github.mosser.arduinoml.kernel.structural._
import io.github.mosser.arduinoml.kernel.behavioral._
import scala.jdk.CollectionConverters._

trait ArduinoML {

  protected def hasForName(n: String) { app.setName(n) }

  protected def declare: StructureBuilder = {
    flush()
    currentBrick = Some(StructureBuilder())
    currentBrick.get
  }

  protected def state: StateBuilder = {
    flush()
    currentState = Some(StateBuilder())
    currentState.get
  }

  protected def transitions(transitionSystem: => Unit) {
    flush()
    transitionSystem
  }

  protected def exportToWiring = {
    flush()
    val codeGen = new ToWiring()
    app.accept(codeGen)
    println(codeGen.getResult)
  }

  private[this] val app: App = {
    val tmp = new App()
    tmp.setBricks(List[Brick]().asJava)
    tmp.setStates(List[State]().asJava)
    tmp
  }

  private def flush() {
    if (currentBrick.isDefined) {
      app.setBricks((app.getBricks.asScala.toList :+ currentBrick.get.toBrick).asJava)
      currentBrick = None
    }
    if (currentState.isDefined) {
      app.setStates((app.getStates.asScala.toList :+ currentState.get.toState).asJava)
      currentState = None
    }
  }

  private var currentBrick: Option[StructureBuilder] = None
  private var currentState: Option[StateBuilder] = None

  private def getBrickByName(n: String): Brick = {
    (app.getBricks.asScala find( _.getName == n )).get
  }

  private def getStateByName(n: String): State = {
    (app.getStates.asScala find( _.getName == n )).get
  }

  protected object Bricks extends Enumeration {
    val UNKNOWN, SENSOR, ACTUATOR = Value
  }

  protected case class StructureBuilder(kind: Bricks.Value = Bricks.UNKNOWN, pin: Int = -1, name: String = "") {

    def aSensor(): StructureBuilder = {
      currentBrick = Some(this.copy(kind = Bricks.SENSOR))
      currentBrick.get
    }

    def anActuator(): StructureBuilder = {
      currentBrick = Some(this.copy(kind = Bricks.ACTUATOR))
      currentBrick.get
    }

    def boundToPin(p: Int): StructureBuilder = {
      currentBrick = Some(this.copy(pin = p))
      currentBrick.get
    }

    def named(n: String): StructureBuilder = {
      currentBrick = Some(this.copy(name = n))
      currentBrick.get
    }

    def toBrick: Brick = {
      val brick = currentBrick.get.kind match {
        case Bricks.SENSOR   => new Sensor()
        case Bricks.ACTUATOR => new Actuator()
        case _ => throw new UnsupportedOperationException("Undefined brick type")
      }
      brick.setName(currentBrick.get.name)
      brick.setPin(currentBrick.get.pin)
      brick
    }

    def -->(signal: Signal): ActionBuilder = ActionBuilder(this,signal)

    def is(signal: Signal): ConditionBuilder = ConditionBuilder(this, signal)
  }

  protected case class StateBuilder(name: String = "", actions: Seq[Action] = Seq()) {

    def named(n: String): StateBuilder = {
      currentState = Some(this.copy(name = n))
      currentState.get
    }

    def executing(actions: ActionBuilder*): StateBuilder = {
      val acts = actions map { b =>
        val a = new Action()
        a.setActuator(getBrickByName(b.actuator.name).asInstanceOf[Actuator])
        a.setValue(b.signal.asSignal)
        a
      }
      currentState = Some(this.copy(actions = acts))
      currentState.get
    }

    def isInitial: Unit = {
      flush()
      app.setInitial(getStateByName(this.name))
    }

    def toState: State = {
      val s = new State()
      s.setName(this.name)
      s.setActions(this.actions.asJava)
      s
    }

    def ->(next: StateBuilder): TransitionBuilder = TransitionBuilder(this,next)
  }

  protected case class ActionBuilder(actuator: StructureBuilder, signal: Signal)

  protected case class ConditionBuilder(condition: BooleanExpression) {
    def and(other: ConditionBuilder): ConditionBuilder = {
      val operation = new BinaryExpression(Operator.AND, this.condition, other.condition)
      ConditionBuilder(operation)
    }

    def or(other: ConditionBuilder): ConditionBuilder = {
      val operation = new BinaryExpression(Operator.OR, this.condition, other.condition)
      ConditionBuilder(operation)
    }
  }

  protected object ConditionBuilder {
    def apply(sensor: StructureBuilder, signal: Signal): ConditionBuilder = {
      val predicate = new Predicate(
        getBrickByName(sensor.name).asInstanceOf[Sensor],
        signal.asSignal
      )
      ConditionBuilder(predicate)
    }
  }

  protected case class TransitionBuilder(from: StateBuilder, to: StateBuilder) {
    def when(cond: ConditionBuilder): Unit = {
      val trans = new Transition()
      trans.setNext(getStateByName(to.name))
      trans.setBooleanExpression(cond.condition)
      getStateByName(from.name).addTransition(trans)
    }
  }

  trait Signal { val asSignal: SIGNAL }
  object high extends Signal { override val asSignal = SIGNAL.HIGH }
  object low  extends Signal { override val asSignal = SIGNAL.LOW }
}
