package main.groovy.groovuinoml.dsl

import io.github.mosser.arduinoml.kernel.behavioral.Action
import io.github.mosser.arduinoml.kernel.behavioral.ICondition
import io.github.mosser.arduinoml.kernel.behavioral.State
import io.github.mosser.arduinoml.kernel.behavioral.Transition
import io.github.mosser.arduinoml.kernel.structural.Actuator
import io.github.mosser.arduinoml.kernel.structural.SIGNAL
import io.github.mosser.arduinoml.kernel.structural.Sensor

abstract class GroovuinoMLBasescript extends Script {

	// sensor "name" pin n
	def sensor(String name) {
		[pin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createSensor(name, n) },
		onPin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createSensor(name, n)}]
	}
	
	// actuator "name" pin n
	def actuator(String name) {
		[pin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createActuator(name, n) }]
	}
	
	// state "name" means actuator becomes signal [and actuator becomes signal]*n
	def state(String name) {
		List<Action> actions = new ArrayList<Action>()
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createState(name, actions)
		// recursive closure to allow multiple and statements
		def closure
		closure = { actuator -> 
			[becomes: { signal ->
				Action action = new Action()
				action.setActuator(actuator instanceof String ? (Actuator)((GroovuinoMLBinding)this.getBinding()).getVariable(actuator) : (Actuator)actuator)
				action.setValue(signal instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal) : (SIGNAL)signal)
				actions.add(action)
				[and: closure]
			}]
		}
		[means: closure]
	}
	
	// initial state
	def initial(state) {
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().setInitialState(state instanceof String ? (State)((GroovuinoMLBinding)this.getBinding()).getVariable(state) : (State)state)
	}
	
	// from state1 to state2 when sensor becomes signal
	def from(String state1Name) {
        [to: { String state2Name ->
            [when: { String sensorName ->
                [becomes: { String signalName ->
                    State s1 = (State)((GroovuinoMLBinding)this.getBinding()).getVariable(state1Name)
                    State s2 = (State)((GroovuinoMLBinding)this.getBinding()).getVariable(state2Name)
                    Sensor sensor = (Sensor)((GroovuinoMLBinding)this.getBinding()).getVariable(sensorName)
                    SIGNAL signal = (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signalName)

                    ICondition condition = ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createComparison(sensor, signal)
                    ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createTransition(s1, s2, condition)
                    
                    Transition t = s1.getTransitions().get(s1.getTransitions().size()-1)

                    def conditionBuilder
                    conditionBuilder = {
                        [
                            and: { String nextSensor ->
                                [becomes: { String nextSignal ->
                                    Sensor ns = (Sensor)((GroovuinoMLBinding)this.getBinding()).getVariable(nextSensor)
                                    SIGNAL nsig = (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(nextSignal)
                                    ICondition right = ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createComparison(ns, nsig)
                                    
                                    ICondition current = t.getCondition()
                                    ICondition newCond = ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createOperation("AND", current, right)
                                    t.setCondition(newCond)
                                    
                                    return conditionBuilder()
                                }]
                            },
                            or: { String nextSensor ->
                                [becomes: { String nextSignal ->
                                    Sensor ns = (Sensor)((GroovuinoMLBinding)this.getBinding()).getVariable(nextSensor)
                                    SIGNAL nsig = (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(nextSignal)
                                    ICondition right = ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createComparison(ns, nsig)

                                    ICondition current = t.getCondition()
                                    ICondition newCond = ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createOperation("OR", current, right)
                                    t.setCondition(newCond)

                                    return conditionBuilder()
                                }]
                            }
                        ]
                    }
                    return conditionBuilder()
                }]
            },
            after: { delay ->
                State s1 = (State)((GroovuinoMLBinding)this.getBinding()).getVariable(state1Name)
                State s2 = (State)((GroovuinoMLBinding)this.getBinding()).getVariable(state2Name)
                ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createTransition(s1, s2, delay)
            }]
        }]
    }
	
	// export name
	def export(String name) {
		println(((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().generateCode(name).toString())
	}
	
	// disable run method while running
	int count = 0
	abstract void scriptBody()
	def run() {
		if(count == 0) {
			count++
			scriptBody()
		} else {
			println "Run method is disabled"
		}
	}
}