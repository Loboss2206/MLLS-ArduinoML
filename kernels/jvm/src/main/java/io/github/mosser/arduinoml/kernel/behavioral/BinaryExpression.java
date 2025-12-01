package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import java.util.ArrayList;
import java.util.List;

public class BinaryExpression implements BooleanExpression {

    private Operator operator;
    private BooleanExpression left;
    private BooleanExpression right;

    public BinaryExpression(Operator operator, BooleanExpression left, BooleanExpression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public BooleanExpression getLeft() {
        return left;
    }

    public void setLeft(BooleanExpression left) {
        this.left = left;
    }

    public BooleanExpression getRight() {
        return right;
    }

    public void setRight(BooleanExpression right) {
        this.right = right;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}