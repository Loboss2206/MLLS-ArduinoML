package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import java.util.ArrayList;
import java.util.List;

public class Operation implements ICondition {

    private Operator operator;
    private List<ICondition> conditions = new ArrayList<>();

    public Operation(Operator operator, ICondition left, ICondition right) {
        this.operator = operator;
        this.conditions.add(left);
        this.conditions.add(right);
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public List<ICondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<ICondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}