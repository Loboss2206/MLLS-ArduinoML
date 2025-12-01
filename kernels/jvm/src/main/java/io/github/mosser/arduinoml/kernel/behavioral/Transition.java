package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class Transition implements Visitable {

    private State next;
    private BooleanExpression booleanExpression;

	public State getNext() {
		return next;
	}

	public void setNext(State next) {
		this.next = next;
	}

    public BooleanExpression getBooleanExpression() {
        return booleanExpression;
    }

    public void setBooleanExpression(BooleanExpression booleanExpression) {
        this.booleanExpression = booleanExpression;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}