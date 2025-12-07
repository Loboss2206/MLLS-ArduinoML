package main.groovy.groovuinoml.dsl;

import java.util.*;

import groovy.lang.Binding;
import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.Brick;
import io.github.mosser.arduinoml.kernel.structural.Buzzer;
import io.github.mosser.arduinoml.kernel.structural.LED;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

public class GroovuinoMLModel {
	private List<Brick> bricks;
	private List<State> states;
	private State initialState;
	private List<Note> notes;
	private List<Melody> melodies;

	private Binding binding;

	public GroovuinoMLModel(Binding binding) {
		this.bricks = new ArrayList<Brick>();
		this.states = new ArrayList<State>();
		this.notes = new ArrayList<Note>();
		this.melodies = new ArrayList<Melody>();
		this.binding = binding;
	}
	
	public void createSensor(String name, Integer pinNumber) {
		Sensor sensor = new Sensor();
		sensor.setName(name);
		sensor.setPin(pinNumber);
		this.bricks.add(sensor);
		this.binding.setVariable(name, sensor);
	}
	
	public void createActuator(String name, Integer pinNumber) {
		Actuator actuator = new Actuator();
		actuator.setName(name);
		actuator.setPin(pinNumber);
		this.bricks.add(actuator);
		this.binding.setVariable(name, actuator);
	}

	public void createBuzzer(String name, Integer pinNumber) {
		Buzzer buzzer = new Buzzer();
		buzzer.setName(name);
		buzzer.setPin(pinNumber);
		this.bricks.add(buzzer);
		this.binding.setVariable(name, buzzer);
	}

	public void createLED(String name, Integer pinNumber) {
		LED led = new LED();
		led.setName(name);
		led.setPin(pinNumber);
		this.bricks.add(led);
		this.binding.setVariable(name, led);
	}
	
	public void createState(String name, List<Action> actions) {
		State state = new State();
		state.setName(name);
		state.setActions(actions);
		this.states.add(state);
		this.binding.setVariable(name, state);
	}
	
	public BooleanExpression createPredicate(Sensor sensor, SIGNAL value) {
		return new Predicate(sensor, value);
	}
	
	public BooleanExpression createBinaryExpression(String operator, BooleanExpression left, BooleanExpression right) {
		return new BinaryExpression(Operator.valueOf(operator.toUpperCase()), left, right);
	}

	public void createTransition(State from, State to, BooleanExpression condition) {
		Transition transition = new Transition();
		transition.setNext(to);
		transition.setBooleanExpression(condition);
		from.addTransition(transition);
	}

	public void createTransition(State from, State to, int delay) {
		TimeTransition transition = new TimeTransition();
		transition.setNext(to);
		transition.setDelay(delay);
		from.addTransition(transition);
	}
	
	public void setInitialState(State state) {
		this.initialState = state;
	}

	public void createNote(String name, int frequency, int duration) {
		Note note = new Note();
		note.setName(name);
		note.setFrequency(frequency);
		note.setDuration(duration);
		this.notes.add(note);
		this.binding.setVariable(name, note);
	}

	public void createMelody(String name, List<Note> noteList) {
		Melody melody = new Melody();
		melody.setName(name);
		melody.setNotes(noteList);
		this.melodies.add(melody);
		this.binding.setVariable(name, melody);
	}

	public NoteAction createNoteAction(Actuator buzzer, Note note) {
		NoteAction action = new NoteAction();
		action.setActuator(buzzer);
		action.setNote(note);
		return action;
	}

	@SuppressWarnings("rawtypes")
	public Object generateCode(String appName) {
		App app = new App();
		app.setName(appName);
		app.setBricks(this.bricks);
		app.setStates(this.states);
		app.setInitial(this.initialState);
		Visitor codeGenerator = new ToWiring();
		app.accept(codeGenerator);
		
		return codeGenerator.getResult();
	}
}