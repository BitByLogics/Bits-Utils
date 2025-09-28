package net.bitbylogic.utils.condition.type;

import lombok.RequiredArgsConstructor;
import net.bitbylogic.utils.condition.Condition;

import java.util.Objects;

@RequiredArgsConstructor
public abstract class SimpleCondition implements Condition {

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        SimpleCondition that = (SimpleCondition) object;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
