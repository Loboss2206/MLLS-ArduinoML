package io.github.mosser.arduinoml.kernel.generator;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.structural.*;

import java.util.List;

/**
 * Quick and dirty visitor to support the generation of Wiring code
 */
public class ToWiring extends Visitor<StringBuffer> {
	enum PASS {ONE, TWO}


	public ToWiring() {
		this.result = new StringBuffer();
	}

	private void w(String s) {
		result.append(String.format("%s",s));
	}

	@Override
	public void visit(App app) {
		//first pass, create global vars
		context.put("pass", PASS.ONE);
		w("// Wiring code generated from an ArduinoML model\n");
		w(String.format("// Application name: %s\n", app.getName())+"\n");

		w("long debounce = 200;\n");
		w("\nenum STATE {");
		String sep ="";
		for(State state: app.getStates()){
			w(sep);
			state.accept(this);
			sep=", ";
		}
		w("};\n");
		if (app.getInitial() != null) {
			w("STATE currentState = " + app.getInitial().getName()+";\n");
		}

		for(Brick brick: app.getBricks()){
			brick.accept(this);
		}

		//second pass, setup and loop
		context.put("pass",PASS.TWO);
		w("\nvoid setup(){\n");
		for(Brick brick: app.getBricks()){
			brick.accept(this);
		}
		w("}\n");

		w("\nvoid loop() {\n" +
			"\tswitch(currentState){\n");
		for(State state: app.getStates()){
			state.accept(this);
		}
		w("\t}\n" +
			"}");
	}

	@Override
	public void visit(Actuator actuator) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format("  pinMode(%d, OUTPUT); // %s [Actuator]\n", actuator.getPin(), actuator.getName()));
			return;
		}
	}


	@Override
	public void visit(Sensor sensor) {
		if(context.get("pass") == PASS.ONE) {
			w(String.format("\nboolean %sBounceGuard = false;\n", sensor.getName()));
			w(String.format("long %sLastDebounceTime = 0;\n", sensor.getName()));
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format("  pinMode(%d, INPUT);  // %s [Sensor]\n", sensor.getPin(), sensor.getName()));
			return;
		}
	}

	@Override
	public void visit(State state) {
		if(context.get("pass") == PASS.ONE){
			w(state.getName());
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w("\t\tcase " + state.getName() + ":\n");
			for (Action action : state.getActions()) {
				action.accept(this);
			}

            if (state.getTransitions() != null) {
				// Update all bounce guards before checking transitions
				java.util.Set<Sensor> sensorsInTransitions = new java.util.HashSet<>();
				for(Transition t : state.getTransitions()) {
					collectSensors(t.getBooleanExpression(), sensorsInTransitions);
				}
				for(Sensor s : sensorsInTransitions) {
					w(String.format("\t\t\t%sBounceGuard = millis() - %sLastDebounceTime > debounce;\n",
						s.getName(), s.getName()));
				}

                for(Transition t : state.getTransitions()) {
                    t.accept(this);
                }
            }
            w("\t\tbreak;\n");
            return;
        }
    }

	private void collectSensors(BooleanExpression expr, java.util.Set<Sensor> sensors) {
		if (expr instanceof Predicate) {
			sensors.add(((Predicate) expr).getSensor());
		} else if (expr instanceof BinaryExpression) {
			collectSensors(((BinaryExpression) expr).getLeft(), sensors);
			collectSensors(((BinaryExpression) expr).getRight(), sensors);
		}
	}

    @Override
    public void visit(Transition transition) {
        if(context.get("pass") == PASS.ONE) {
            return;
        }
        if(context.get("pass") == PASS.TWO) {
            w("\t\t\tif( ");
            transition.getBooleanExpression().accept(this);
            w(" ) {\n");

			// Update timestamps for all sensors in this transition
			java.util.Set<Sensor> sensors = new java.util.HashSet<>();
			collectSensors(transition.getBooleanExpression(), sensors);
			for(Sensor s : sensors) {
				w(String.format("\t\t\t\t%sLastDebounceTime = millis();\n", s.getName()));
			}

            w("\t\t\t\tcurrentState = " + transition.getNext().getName() + ";\n");
            w("\t\t\t}\n");
            return;
        }
    }

	@Override
	public void visit(TimeTransition transition) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			int delayInMS = transition.getDelay();
			w(String.format("\t\t\tdelay(%d);\n", delayInMS));
			w("\t\t\t\tcurrentState = " + transition.getNext().getName() + ";\n");
			w("\t\t\t}\n");
			return;
		}
	}

	@Override
	public void visit(Action action) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format("\t\t\tdigitalWrite(%d,%s);\n",action.getActuator().getPin(),action.getValue()));
			return;
		}
	}

	@Override
	public void visit(NoteAction noteAction) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			Note note = noteAction.getNote();
			int buzzerPin = noteAction.getActuator().getPin();
			w(String.format("\t\t\ttone(%d, %d, %d);\n", buzzerPin, note.getFrequency(), note.getDuration()));
			w(String.format("\t\t\tdelay(%d);\n", note.getDuration()));
			w(String.format("\t\t\tnoTone(%d);\n", buzzerPin));
			return;
		}
	}

    @Override
    public void visit(Predicate predicate) {
        if(context.get("pass") == PASS.ONE) {
            return;
        }
        if(context.get("pass") == PASS.TWO) {
            Sensor sensor = predicate.getSensor();
            w(String.format("(digitalRead(%d) == %s && %sBounceGuard)", 
                sensor.getPin(), predicate.getValue(), sensor.getName()));
            return;
        }
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        if(context.get("pass") == PASS.ONE) {
            return;
        }
        if(context.get("pass") == PASS.TWO) {
            w("(");
            binaryExpression.getLeft().accept(this);
            w(binaryExpression.getOperator() == Operator.AND ? " && " : " || ");
            binaryExpression.getRight().accept(this);
            w(")");
            return;
        }
    }

}