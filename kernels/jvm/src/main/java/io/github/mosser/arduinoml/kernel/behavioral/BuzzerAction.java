package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;

public class BuzzerAction extends Action {

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
